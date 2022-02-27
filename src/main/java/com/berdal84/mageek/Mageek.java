/*
 * To the extent possible under law, the ImageJ developers have waived
 * all copyright and related or neighboring rights to this tutorial code.
 *
 * See the CC0 1.0 Universal license for details:
 *     http://creativecommons.org/publicdomain/zero/1.0/
 */
package com.berdal84.mageek;


import ij.ImagePlus;
import ij.io.FileSaver;
import ij.plugin.ChannelSplitter;
import ij.plugin.ZProjector;
import ij.process.ImageProcessor;
import ij.process.LUT;
import io.scif.services.DatasetIOService;
import net.imagej.ImageJ;
import net.imglib2.type.numeric.RealType;
import org.scijava.command.Command;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;
import org.scijava.run.RunService;
import org.scijava.ui.DialogPrompt.MessageType;
import org.scijava.ui.DialogPrompt.OptionType;
import org.scijava.ui.DialogPrompt;
import org.scijava.ui.UIService;
import org.scijava.widget.FileWidget;
import org.scijava.log.LogLevel;
import org.scijava.log.LogService;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.beans.PropertyChangeEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.WindowConstants;
import javax.swing.event.ListSelectionEvent;
import loci.formats.FormatException;
import loci.plugins.in.DisplayHandler;
import loci.plugins.in.ImagePlusReader;
import loci.plugins.in.ImportProcess;
import loci.plugins.in.ImporterOptions;
import loci.plugins.in.ImporterPrompter;
import net.imagej.DatasetService;

/**
 * Mageek2 is the Java version of Mageek.ijm macro
 *
 * This plugin allows to process files by selecting a source directory.The
 process will: - scan recursively the folder - display a scan result to let
 the user to choose which file extension to process, and which colors to use,
 - importing each file's series, - splitting each series's channel, - applying
 a Z projection (to combine all slices), - colorize each channel, - save
 result to a "ANALYZED" folder.
 * @param <T>
 */
@Plugin(type = Command.class, menuPath = "Plugins>Mageek")
public class Mageek<T extends RealType<T>>  implements Command
{
   
    @Parameter
    private UIService ui;

    @Parameter
    private LogService log;

    @Parameter
    DatasetService ds;

    @Parameter
    DatasetIOService io;
        
    /* in batch mode, files loaded are not displayed. All the process is done in background */
    private boolean batchMode;
    
    /* The current source folder */
    private File sourceFolder;

    /* The current destination folder */
    private File destinationFolder;

    /* Subfolder name to put all analysed files */
    private final String analysedFolderName ;

    /* The script title */
    private final String title;

    /* The script title */
    private final String version;

    /* Scanned extensions */
    private ArrayList<String> scannedFileExtensions;

    /* Scanned files */
    private ArrayList<File> scannedFiles;

    /* Filtered files */
    private ArrayList<File> filteredFiles;

    /* Ignored files */
    private final ArrayList<File> ignoredFiles;

    /* Processed files */
    private final ArrayList<File> processedFiles;

    /* The main UI */
    private MageekFrame gui;
    
    /* The current color preset */
    private MColorPreset selectedColors;

    private final String[] SELECTED_EXTENSIONS_DEFAULT =  // TODO: convert to enum
    {
        "*.czi",
        "*.lif",
        "*.nd2"
    };
                         
    /** a value from ZProjector.METHODS array */
    private String projectorMethod;

    private final Map<String, MColorPreset> colorPresets;
    
     /**
     * This main function serves for development purposes. It allows you to run
     * the plugin immediately out of your integrated development environment
     * (IDE).
     *
     * @param args whatever, it's ignored
     * @throws Exception
     */
    public static void main(final String... args) throws Exception
    {
        // create the ImageJ application context with all available services
        final ImageJ ij = new ImageJ();
        ij.ui().showUI();
        ij.command().run(Mageek.class, false);
    }
    
    public Mageek()
    {
        title   = "Mageek";
        version = "1.0.0";
        batchMode = true;
        analysedFolderName = "ANALYSED";
        scannedFileExtensions = new ArrayList<>();
        scannedFiles   = new ArrayList<>();
        filteredFiles  = new ArrayList<>();
        ignoredFiles   = new ArrayList<>();
        processedFiles = new ArrayList<>();
        projectorMethod = ZProjector.METHODS[ZProjector.AVG_METHOD];
        
        colorPresets = new HashMap();
        {
            MColorPreset preset = new MColorPreset("Confocal", MColor.Blue , MColor.Red, MColor.Green, MColor.Magenta );
            colorPresets.put(preset.getName(), preset);
        }

        {
            MColorPreset preset = new MColorPreset("Legacy", MColor.Blue, MColor.Green, MColor.Red, MColor.Magenta );
            colorPresets.put(preset.getName(), preset);
        }
        
        {
            MColorPreset preset = new MColorPreset("Custom", MColor.Null , MColor.Null, MColor.Null, MColor.Null );
            colorPresets.put(preset.getName(), preset);
        }        
               
        selectedColors = new MColorPreset( colorPresets.get("Confocal") );
    }
    
    @Override
    public void run()
    {
        log.log(LogLevel.INFO, String.format("Running %s ...", title));

        gui = new MageekFrame(ui.context());
        
        gui.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        gui.setStatus(String.format("Welcome to %s v%s", title, version));
        gui.setSourceDirectory("Select a source directory ...");

        gui.setAvailableColors(MColor.All);      

        ArrayList presets = new ArrayList(colorPresets.values());
        gui.setAvailableColorPresets(presets);
        gui.setColorPreset(selectedColors, true);
        
        gui.setAvailableZProjection(ZProjector.METHODS);
        gui.setZProjection(ZProjector.METHODS[ZProjector.AVG_METHOD]);
        gui.setVisible(true);
        gui.setAlwaysOnTop(false);
        gui.setBatchMode(batchMode);
        
        gui.addBrowseBtnListener((ActionEvent evt)->
        {
                gui.setStatus("Browsing folder ...");
                gui.setProgress(0);
                askSourceDirectoryToUser();

                if (sourceFolder != null)
                {
                    gui.setStatus(
                            String.format(
                                    "Source folder %s picked. Click on process now.",
                                    sourceFolder.toString()
                            )
                    );
                    gui.setSourceDirectory(sourceFolder.toString());

                    scannedFiles = FileHelper.getFiles(sourceFolder, true);
                    gui.setFileList(scannedFiles);
                    scannedFileExtensions = FileHelper.getFileExtensions(scannedFiles);
                    gui.setFileExtensionList(scannedFileExtensions);
                    gui.setSelectedFileExtensions(SELECTED_EXTENSIONS_DEFAULT);
                }
                else
                {
                    gui.setStatus("Browsing aborted.");
                    gui.clearFileList();
                }
        });

        gui.addLaunchProcessListener((ActionEvent evt) ->
        {
            gui.setStatus("Processing ...");

            if ( createOutputDirectory() )
            {
                processFiles();
                gui.setStatus("Processing DONE");
                gui.setProgress(100);
                displayStatisticsInStatusBar();
            }
            else
            {
                gui.setStatus("Process aborted.");
            }
                
        });

        gui.addWindowListener(new WindowListener()
        {
            @Override
            public void windowOpened(WindowEvent e)
            {
            }

            @Override
            public void windowClosing(WindowEvent e)
            {
            }

            @Override
            public void windowClosed(WindowEvent e)
            {
                log.info("Mageek is stopped.");
            }

            @Override
            public void windowIconified(WindowEvent e)
            {
            }

            @Override
            public void windowDeiconified(WindowEvent e)
            {
            }

            @Override
            public void windowActivated(WindowEvent e)
            {
            }

            @Override
            public void windowDeactivated(WindowEvent e)
            {
            }

        });

        gui.addFileExtSelectionListener((ListSelectionEvent e) ->
        {
            filterFiles(gui.getSelectedFileExtensions() );
                gui.setFileList(filteredFiles);
        });

        gui.addSelectZProjectionListener((ItemEvent e) ->
        {
            projectorMethod = (String)e.getItem();
            log.info(String.format("ZProjection changed to %s", projectorMethod));
        });
        
        gui.addSelectColorListener((ItemEvent evt) ->
        {
            log.info("Color changed !");
            String presetName = gui.getSelectedPresetName();
            
            // We choose a unique event for color item selection changed
            // We need to update the selectedColorPreset entirely
            for( int i=0; i < 4; i++ )
            {
                selectedColors.setIJColorStringAt(i, gui.getSelectedIJColorStringAt(i) );
            }
            
            //colorPreset.setIJColorStringAt(0)
            if( colorPresets.containsKey(presetName))
            {
                MColorPreset preset = colorPresets.get(presetName);
               
                if (!preset.getIJColorStringAt(0).equals(gui.getSelectedIJColorStringAt(1)) ||
                    !preset.getIJColorStringAt(1).equals(gui.getSelectedIJColorStringAt(2)) ||
                    !preset.getIJColorStringAt(2).equals(gui.getSelectedIJColorStringAt(3))||
                    !preset.getIJColorStringAt(3).equals(gui.getSelectedIJColorStringAt(4)))
                {
                    gui.setColorPreset(colorPresets.get("Custom"), false);
                }
            }
        });
        
        gui.addSelectColorPresetListener((ItemEvent e) ->
        {            
            String presetName = (String)e.getItem();
            log.info( String.format("ColorPreset changed to %s", presetName ));
            
            if ( !presetName.equals("Custom") )
            {
                MColorPreset presetSelected = colorPresets.get(presetName);
                gui.setColorPreset( presetSelected, true);
                
                // We choose a unique event for color item selection changed
                // We need to update the selectedColorPreset entirely
                for( int i=0; i < 4; i++ )
                {
                    selectedColors.setMetaColorAt(i, presetSelected.getMetaColorAt(i) );
                }
            }
        });
        
        gui.addBatchModeListener((PropertyChangeEvent e) ->
        {            
            batchMode = (boolean)e.getNewValue();
        });
    }

    /**
     * Filter scanned files with the checked extensions.
     *
     * @param selectedExtensions
     * @return
     */
    protected ArrayList<File> filterFiles(List<String> selectedExtensions)
    {
        filteredFiles = new ArrayList<>(scannedFiles);
        filteredFiles.removeIf((_eachFile) ->
        {
            return !selectedExtensions.contains( FileHelper.getFormattedExtension(_eachFile) );
        });

        gui.setStatus(
                String.format(
                        "Filtering files with the following extensions: %s",
                        selectedExtensions.toString()
                )
        );

        return filteredFiles;
    }

    /**
     * Launch the process over filtered files.
     *
     * TODO: create a separate Plugin able to run in parallel. UI is stuck while processing currently.
     * 
     * Each file will be processed one by one, result will be saved to
     * destinationDirectory.
     */
    private void processFiles()
    {
        
        final ArrayList<ImagePlus[]> allImages = new ArrayList<>();
        gui.setProgress(0);
        
        if (sourceFolder != null)
        {
            log.log(
                    LogLevel.INFO,
                    String.format(
                            "Processing folder %s ...",
                            sourceFolder.getAbsolutePath()
                    )
            );
            
            processedFiles.clear();
            ignoredFiles.clear();
           
            for (File file : filteredFiles)
            {
                try
                {
                    gui.setStatus(String.format("Processing file %s ...", file.toPath()));
                    ImagePlus[] allSeries = open(file);

                    // In case we have zero images, we skip.
                    if  ( allSeries.length > 0 )
                    {
                       allImages.add(allSeries); 
                       
                       // At this step, each image is a serie (@see open(File) method)
                       int serie = 0;
                       for(ImagePlus serieImg : allSeries )
                        {
                            ImagePlus[] allChannels = ChannelSplitter.split(serieImg);
                            
                            // At this step, each image is a channel.
                            int channel = 0;
                            for (ImagePlus channelImg : allChannels)
                            {
                                if( !batchMode )
                                {
                                    channelImg.show();
                                }
                                
                                if ( channelImg.getNSlices() > 1 )
                                {
                                    channelImg = ij.plugin.ZProjector.run(channelImg, projectorMethod );
                                }                                
                                
                                ImageProcessor p     = channelImg.getProcessor();                                
                                java.awt.Color color = selectedColors.getColorAt(channel);
                                
                                LUT lut = LUT.createLutFromColor(color);
                                p.setLut(lut);

                                String outputPath = String.format(
                                        "%s%s%s_serie_%d_channel_%d.tiff",
                                        destinationFolder.getAbsolutePath(),
                                        File.separator,
                                        file.getName(),
                                        serie,
                                        channel
                                );

                                ImagePlus out = new ImagePlus("out", p.createImage());
                                
                                if( !batchMode )
                                {
                                    channelImg.close();
                                    out.show();
                                }
                                
                                FileSaver saver = new FileSaver(out);
                                saver.saveAsTiff(outputPath);
                                channel++;
                                
                                                                
                                if( !batchMode )
                                {
                                    out.close();
                                    
                                }
                            }
                            serie++;
                        }

                    }                    
                    gui.setStatus( String.format("File %s processed.", file.toPath()));
                }
                catch (Exception ex)
                {
                    ignoredFiles.add(file);
                    Logger.getLogger(Mageek.class.getName()).log(Level.SEVERE, null, ex);
                }
                processedFiles.add(file);

                gui.setProgress( processedFiles.size() / filteredFiles.size() * 100);
                gui.repaint();
            }

            gui.setStatus("Processing DONE");
        }
        else
        {
            ui.showDialog("Please set a source folder first.");
        }
    }

    /**
     * Open a file dialog to pick a source directory.
     *
     * I case the folder has already been analyzed (presence of ANALYSED
     * sub-folder) a prompt will ask user to overwrite/do not overwrite/cancel.
     */
    private void askSourceDirectoryToUser()
    {
        // ask user to pick a source folder
        File pickedFolder = ui.chooseFile(
                new File(System.getProperty("user.home")),
                FileWidget.DIRECTORY_STYLE);

        if (pickedFolder == null)
        {
            sourceFolder = null;
            destinationFolder = null;
            log.log(LogLevel.TRACE, "User did not select any folder !");
        }
        else
        {
            sourceFolder = pickedFolder;            
        }
    }
    
    /**
     * Create the output directory, or if exists ask user to empty it or to abort.
     * @return 
     */
    private boolean createOutputDirectory()
    {
        boolean success;
        
        String destFolderPath = String.format("%s%s%s",
                    sourceFolder.getAbsolutePath(),
                    File.separator,
                    analysedFolderName
            );

        destinationFolder = new File(destFolderPath);

        if (destinationFolder.exists() && destinationFolder.list().length != 0)
        {
            String message = String.format(
                "Output directory %s already exists and is not empty.\n" +
                "Do you really want to continue? (all files and folders inside will be erased)",
                destinationFolder.toString()
            );

            DialogPrompt.Result result
                    = ui.showDialog(
                            message,
                            MessageType.QUESTION_MESSAGE,
                            OptionType.OK_CANCEL_OPTION
                    );

            switch (result)
            {
                case YES_OPTION:
                    FileHelper.deleteDirectoryContent(destinationFolder, false);
                    success = true;
                    break;

                default:
                    destinationFolder = null;
                    success = false;
                    break;
            }
        }
        else
        {
            destinationFolder.mkdir();
            success = true;
        }
        return success;
    }

    /**
     * Update the statistics in status bar
     */
    void displayStatisticsInStatusBar()
    {
        String innerMessage;

        MessageType messageType;

        if (this.sourceFolder == null)
        {
            innerMessage = "Nothing to process ...";
            messageType = MessageType.INFORMATION_MESSAGE;
        }
        else
        {
            innerMessage = String.format(
                    "Process done, %d file(s) processed (%d ignored)",
                    processedFiles.size(),
                    ignoredFiles.size()
            );

            messageType = MessageType.INFORMATION_MESSAGE;
        }

        String message = String.format(
                "%s --- Hasta la vista, baby. ^^",
                innerMessage
        );

        gui.setStatus(message);
    }
    
    private ImagePlus[] open(File file) throws IOException, FormatException   
    {
  
        ImporterOptions options = new ImporterOptions();
        options.setId(file.getPath());  
        options.setOpenAllSeries(true);
        options.setSplitChannels(false);
        options.setWindowless(true);
        
        ImportProcess process = new ImportProcess(options);
        /*
         * @link {loci.plugins.in.Importer.showDialogs}
         * Goto to the source coe linked above to understand why we need this Prompter.
         */
        ImporterPrompter prompter = new ImporterPrompter(process);
                
        process.execute();

        log.debug("display metadata");
        DisplayHandler displayHandler = new DisplayHandler(process);
        displayHandler.displayOriginalMetadata();
        displayHandler.displayOMEXML();

        log.debug("read pixel data");
        ImagePlusReader reader = new ImagePlusReader(process);
        ImagePlus[] imps = reader.openImagePlus();

//        log.debug("display pixels");
//        displayHandler.displayImages(imps);
//
//        log.debug("display ROIs");
//        displayHandler.displayROIs(imps);   
        
        if (!process.getOptions().isVirtual())
        {
            process.getReader().close();
        }
        
        return imps;
    }
}
