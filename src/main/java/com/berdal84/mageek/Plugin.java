/*
 * To the extent possible under law, the ImageJ developers have waived
 * all copyright and related or neighboring rights to this tutorial code.
 *
 * See the CC0 1.0 Universal license for details:
 *     http://creativecommons.org/publicdomain/zero/1.0/
 */
package com.berdal84.mageek;


import ij.ImagePlus;
import ij.plugin.ZProjector;

import io.scif.services.DatasetIOService;

import net.imagej.DatasetService;
import net.imagej.ImageJ;

import net.imglib2.type.numeric.RealType;

import org.scijava.command.Command;
import org.scijava.plugin.Parameter;
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
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.AbstractButton;
import javax.swing.WindowConstants;
import javax.swing.event.ListSelectionEvent;

import loci.formats.FormatException;
import loci.plugins.in.DisplayHandler;
import loci.plugins.in.ImagePlusReader;
import loci.plugins.in.ImportProcess;
import loci.plugins.in.ImporterOptions;
import loci.plugins.in.ImporterPrompter;

/**
 * Mageek Plugin is the Java version of Mageek.ijm macro
 */
@org.scijava.plugin.Plugin(type = Command.class, menuPath = "Plugins>Mageek")
public class Plugin<T extends RealType<T>>  implements Command
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
    private GUI gui;
    
    /* The current color preset */
    private ColorPreset selectedColors;

    private final String[] SELECTED_EXTENSIONS_DEFAULT =  // TODO: convert to enum
    {
        "*.czi",
        "*.lif",
        "*.nd2"
    };
                         
    /** a value from ZProjector.METHODS array */
    private String projectorMethod;

    private final Map<String, ColorPreset> colorPresets;
    
    private Thread currentProcessThread;
    
    public Plugin()
    {
        currentProcessThread = null;
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
            ColorPreset preset = new ColorPreset("Confocal", MetaColor.Blue , MetaColor.Red, MetaColor.Green, MetaColor.Magenta );
            colorPresets.put(preset.getName(), preset);
        }

        {
            ColorPreset preset = new ColorPreset("Legacy", MetaColor.Blue, MetaColor.Green, MetaColor.Red, MetaColor.Magenta );
            colorPresets.put(preset.getName(), preset);
        }
        
        {
            ColorPreset preset = new ColorPreset("Custom", MetaColor.Null , MetaColor.Null, MetaColor.Null, MetaColor.Null );
            colorPresets.put(preset.getName(), preset);
        }        
               
        selectedColors = new ColorPreset( colorPresets.get("Confocal") );
    }
    
    @Override
    public void run()
    {
        log.log(LogLevel.INFO, String.format("Running %s ...", title));

        gui = new GUI(ui.context());
        
        gui.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

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
                ColorPreset preset = colorPresets.get(presetName);
               
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
                ColorPreset presetSelected = colorPresets.get(presetName);
                gui.setColorPreset( presetSelected, true);
                
                // We choose a unique event for color item selection changed
                // We need to update the selectedColorPreset entirely
                for( int i=0; i < 4; i++ )
                {
                    selectedColors.setMetaColorAt(i, presetSelected.getMetaColorAt(i) );
                }
            }
        });
        
        gui.addBatchModeListener((ActionEvent evt) ->
        {            
            AbstractButton abstractButton = (AbstractButton)evt.getSource();
            batchMode = abstractButton.getModel().isSelected();
        });

        gui.addCancelBtnListener((ActionEvent e) ->
        {                    
            if( currentProcessThread != null )
            {        
                DialogPrompt.Result response = ui.showDialog(
                    "Do you really want to cancel the current process?",
                    MessageType.QUESTION_MESSAGE,
                    OptionType.YES_NO_OPTION);
                
                if ( response.equals(DialogPrompt.Result.YES_OPTION) )
                {
                    try
                    {
                        currentProcessThread.interrupt();
                        currentProcessThread.join();
                        gui.setStatus("Process canceled");
                        gui.setProgress(0);
                    }
                    catch (InterruptedException ex)
                    {
                        Logger.getLogger(Plugin.class.getName()).log(Level.SEVERE, null, ex);
                    }  
                }
            }
        });
        
        gui.setStatus(String.format("Welcome to %s v%s", title, version));
        gui.setSourceDirectory("Select a source directory ...");

        gui.setAvailableColors(MetaColor.All);      

        ArrayList presets = new ArrayList(colorPresets.values());
        gui.setAvailableColorPresets(presets);
        gui.setColorPreset(selectedColors, true);
        
        gui.setAvailableZProjection(ZProjector.METHODS);
        gui.setZProjection(ZProjector.METHODS[ZProjector.AVG_METHOD]);
        gui.setVisible(true);
        gui.setAlwaysOnTop(false);
        gui.setBatchMode(batchMode);
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
        gui.setProgress(0);
        
        if (sourceFolder != null)
        {
            // TODO: use ImageJ2 proper mechanism
            Process p = new Process();
            p.setup(
                filteredFiles,
                destinationFolder,
                batchMode,
                projectorMethod,
                selectedColors,
                log
            );
            
            currentProcessThread  = new Thread(p);
            
            p.setListener( new Process.Listener()
            {
                @Override
                public void onProgressChange(int _progress)
                {
                    gui.setProgress( _progress );
                }

                @Override
                public void onStatusChange(String _status)
                {
                    gui.setStatus(_status);
                }
            });            
            
            currentProcessThread.start();
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
                case OK_OPTION:
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
