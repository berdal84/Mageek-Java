/*
 * To the extent possible under law, the ImageJ developers have waived
 * all copyright and related or neighboring rights to this tutorial code.
 *
 * See the CC0 1.0 Universal license for details:
 *     http://creativecommons.org/publicdomain/zero/1.0/
 */
package com.berdal84.mageek;


import ij.IJ;
import ij.ImagePlus;
import ij.ImageStack;
import ij.Macro;
import ij.io.FileSaver;
import ij.plugin.ChannelSplitter;
import ij.plugin.ZProjector;
import ij.process.ByteProcessor;
import ij.process.ColorProcessor;
import ij.process.FloatProcessor;
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
import io.scif.services.DefaultDatasetIOService;
import java.awt.ItemSelectable;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.beans.PropertyChangeEvent;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.WindowConstants;
import javax.swing.event.ListSelectionEvent;
import loci.formats.FormatException;
import net.imagej.DefaultImgPlusService;

import loci.plugins.BF;
import loci.plugins.LociImporter;
import loci.plugins.in.DisplayHandler;
import loci.plugins.in.ImagePlusReader;
import loci.plugins.in.ImportProcess;
import loci.plugins.in.ImporterOptions;
import static loci.plugins.in.ImporterOptions.VIEW_HYPERSTACK;
import loci.plugins.in.ImporterPrompter;
import loci.plugins.util.LibraryChecker;
import net.imagej.DatasetService;
import net.imagej.ImgPlus;
import net.imglib2.img.Img;
import net.imglib2.img.display.imagej.ImageJFunctions;
import net.imglib2.test.ImgLib2Assert;
import org.scijava.plugin.PluginInfo;
import org.scijava.plugin.SciJavaPlugin;

/**
 * Mageek2 is the Java version of Mageek.ijm macro
 *
 * This plugin allows to process files by selecting a source directory. The
 * process will: - scan recursively the folder - display a scan result to let
 * the user to choose which file extension to process, and which colors to use,
 * - importing each file's series, - splitting each series's channel, - applying
 * a Z projection (to combine all slices), - colorize each channel, - save
 * result to a "ANALYZED" folder.
 */
@Plugin(type = Command.class, menuPath = "Plugins>Mageek")
public class Mageek<T extends RealType<T>>  implements Command
{
    @Parameter
    private RunService run;
    
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
        
        gui.addBrowseBtnListener((ActionEvent evt)->
        {
                gui.setStatus("Browsing folder ...");

                askSourceDirectoryToUser();

                if (sourceFolder != null)
                {
                    gui.setStatus(
                            String.format(
                                    "Source folder %s picked. Click on process now.",
                                    sourceFolder.toString()
                            )
                    );
                    gui.setProgress(10);

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
                    gui.setProgress(0);
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
                gui.setColorPreset( colorPresets.get(presetName), true);  
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
                                FileSaver saver = new FileSaver(out);
                                saver.saveAsTiff(outputPath);
                                channel++;
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
            
           if ( !batchMode && !allImages.isEmpty() )
           {
               DialogPrompt.Result response =
                    ui.showDialog(
                        "Would you like to open the images?",
                        MessageType.QUESTION_MESSAGE,
                        OptionType.YES_NO_OPTION
                   );
               
               if( response.equals( DialogPrompt.Result.YES_OPTION ) )
               {
                   allImages.forEach( (ImagePlus[] imgs )->
                   {
                       for(ImagePlus img : imgs )
                       {
                           img.show();
                       }
                   });
               }
           }
           else
           {
               gui.setStatus("No images were loaded :(");
           }
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

    /**
     * Stop Mageek.
     *
     * Main dialog will be closed.
     */
    private void stop()
    {
        gui.setVisible(false);
        gui.dispose();
    }
    
    private void colorizeFile(File _output, MColor[] _colorForChannel, int channels, int slices, int frames )
    {	
//	log.info("Colorize... ", _outputFileName , "[ ", channels, " channel(s), ", slices, " slice(s), ", frames, " frame(s) ]");	
//	existingFileCount = nImages();
//	channelToProcessCount = channels;	
//	// in some cases we could have more channels than colors, so we skip the channels without color
//	if ( channelToProcessCount  > _colorForChannel.length ) {
//		channelToProcessCount = _colorForChannel.length;
//	}
//
//	// Stack.setDisplayMode("color");
//
//	// Colorize each channels using specified color (_colorForChannel is an Array of strings)
//
//	for( i=0; i < channelToProcessCount; i++)
//        {
//            if( channelToProcessCount > 1 ) { 
//                    Stack.setChannel(i+1);
//            }
//            colorScriptName = _colorForChannel[i];
//            print("Colorizing channel ", i+1, " as ", colorScriptName, "...");
//            run(colorScriptName);
//	}
//
//	if( frames > 1 || slices > 1)
//        { 
//            if (zProjUserChoice != Z_PROJECT_NONE ){
//                    run("Z Project...", "projection=["+ zProjUserChoice +"]");	
//            }
//            selectImage(existingFileCount);
//            close();
//	}
//	
//	// Split channels if needed
//	if ( channelToProcessCount > 1 )
//        {
//            run("Split Channels");
//	}	
//	
//	// Rename the channel(s) image(s)
//	for( i=0; i < channelToProcessCount; i++)
//        {
//            selectImage(existingFileCount+i);
//            rename(_colorForChannel[i]);
//	}
//
//	/** Merge channel is disabled for now...
//	options = "";
//	for( i=0; i < channelToProcessCount; i++) {
//		options = options + "c"+(i+1)+"=" + _colorForChannel[i] + " ";
//	}
//	run("Merge Channels...", options + "keep");
//	*/
//	
//	// Save each image
//	for( i=0; i < channelToProcessCount; i++)
//        {
//            selectWindow(_colorForChannel[i]);
//            run("RGB Color");
//            saveAs("Tiff", _outputFileName + "_color_" + _colorForChannel[i] + ".tif");
//	}
//
//	// Create a Montage with colored channels (only if we have more than one channel)
//	if ( channelToProcessCount > 1 )
//        {
//            run("Images to Stack", "name=name title=[] use keep");
//            run("Make Montage...", "columns=" + channelToProcessCount + " rows=1 scale=0.5 first=1 last="+ channelToProcessCount +" increment=1 border=1 font=12");
//            saveAs("Tiff", _outputFileName + "_Montage.tif");
//	}
//
//	// Close opened images
//	while (nImages() > existingFileCount )
//        { 		
//            selectImage(nImages); 
//            close(); 
//	}

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
