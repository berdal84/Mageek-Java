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
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
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
import loci.plugins.BF;
import net.imagej.Dataset;
import net.imagej.DefaultImgPlusService;
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
    private DefaultImgPlusService imgPlusService;
    
    @Parameter
    private RunService run;
    
    @Parameter
    private UIService ui;

    @Parameter
    private LogService log;

    @Parameter
    private DefaultDatasetIOService dataSetService;
        
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
    private MageekFrame dialog;

    private final String[] SELECTED_EXTENSIONS_DEFAULT =  // TODO: convert to enum
    {
        "*.czi",
        "*.lif",
        "*.nd2"
    };
    
    private static final String Z_PROJECT_NONE = "None";

    private final String[] AVAILABLE_ZPROJECTION = { // TODO: convert to enum
        "Max Intensity",
        "Average Intensity",
        "Sum Slices",
        "Min Intensity",
        "Standard Deviation",
        "Median",
        Z_PROJECT_NONE
    };
    
    private String zProjectionMode;
    
    private final String defaultColorPresetName;

    private final Map<String, ColorPreset> colorPresets;
    
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
        zProjectionMode = "Max Intensity";
        defaultColorPresetName = "Confocal";
        
        colorPresets = new HashMap();
        {
            Color[] colors = { Color.BLUE , Color.RED, Color.GREEN, Color.MAGENTA};
            ColorPreset preset = new ColorPreset("Confocal", colors );
            colorPresets.put(preset.getName(), preset);
        }

        {
            Color[] colors = { Color.BLUE, Color.GREEN, Color.RED, Color.MAGENTA};
            ColorPreset preset = new ColorPreset("Legacy", colors );
            colorPresets.put(preset.getName(), preset);
        }
        
        {
            Color[] colors = { Color.NULL , Color.NULL, Color.NULL, Color.NULL};
            ColorPreset preset = new ColorPreset("Custom", colors );
            colorPresets.put(preset.getName(), preset);
        }
    }
    
    @Override
    public void run()
    {
        log.log(LogLevel.INFO, String.format("Running %s ...", title));

        dialog = new MageekFrame(ui.context());
        
        dialog.addBrowseBtnListener( (ActionEvent evt)->
        {
                dialog.setStatus("Browsing folder ...");

                askSourceDirectoryToUser();

                if (sourceFolder != null)
                {
                    dialog.setStatus(
                            String.format(
                                    "Source folder %s picked. Click on process now.",
                                    sourceFolder.toString()
                            )
                    );
                    dialog.setProgress(10);

                    dialog.setSourceDirectory(sourceFolder.toString());

                    scannedFiles = FileHelper.getFiles(sourceFolder, true);
                    dialog.setFileList(scannedFiles);
                    scannedFileExtensions = FileHelper.getFileExtensions(scannedFiles);
                    dialog.setFileExtensionList(scannedFileExtensions);
                    dialog.setSelectedFileExtensions(SELECTED_EXTENSIONS_DEFAULT);
                }
                else
                {
                    dialog.setStatus("Browsing aborted.");
                    dialog.setProgress(0);
                    dialog.clearFileList();
                }
        });

        dialog.addLaunchProcessListener((ActionEvent evt) ->
        {
                dialog.setStatus("Processing ...");
                processFiles();
                dialog.setStatus("Processing DONE");
                dialog.setProgress(100);
                displayStatisticsInStatusBar();
        });

        dialog.addWindowListener(new WindowListener()
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

        dialog.addFileExtSelectionListener((ListSelectionEvent e) ->
        {
            filterFiles( dialog.getSelectedFileExtensions() );
                dialog.setFileList(filteredFiles);
        });

        dialog.addSelectZProjectionListener( (ItemEvent e) ->
        {
            zProjectionMode = (String)e.getItem();
            log.info( String.format("ZProjection changed to %s", zProjectionMode));
        });
        
        dialog.addSelectColorListener((ItemEvent e) ->
        {
            log.info("Color changed !");
            String presetName = dialog.getSelectedPresetName();
            if( colorPresets.containsKey(presetName))
            {
                ColorPreset preset = colorPresets.get(presetName);
               
                if (!preset.getColorString(0).equals( dialog.getSelectedColorAt(1)) ||
                    !preset.getColorString(1).equals( dialog.getSelectedColorAt(2)) ||
                    !preset.getColorString(2).equals( dialog.getSelectedColorAt(3))||
                    !preset.getColorString(3).equals( dialog.getSelectedColorAt(4)))
                {
                    dialog.setColorPreset(colorPresets.get("Custom"), false);
                }
            }
        });
        
        dialog.addSelectColorPresetListener((ItemEvent e) ->
        {            
            String presetName = (String)e.getItem();
            log.info( String.format("ColorPreset changed to %s", presetName ));
            
            if ( !presetName.equals("Custom") )
            {
                dialog.setColorPreset( colorPresets.get(presetName), true);  
            }
        });
        
        dialog.addBatchModeListener((PropertyChangeEvent e) ->
        {            
            batchMode = (boolean)e.getNewValue();
        });
                
        {
            Color[] colors = { Color.NULL , Color.NULL, Color.NULL, Color.NULL};
            ColorPreset preset = new ColorPreset("Custom", colors );
            colorPresets.put(preset.getName(), preset);
        }
        
        {
            Color[] colors = { Color.BLUE , Color.RED, Color.GREEN, Color.MAGENTA};
            ColorPreset preset = new ColorPreset("Confocal", colors );
            colorPresets.put(preset.getName(), preset);
        }

        {
            Color[] colors = { Color.BLUE, Color.GREEN, Color.RED, Color.MAGENTA};
            ColorPreset preset = new ColorPreset("Legacy", colors );
            colorPresets.put(preset.getName(), preset);
        }

        dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        dialog.setStatus(String.format("Welcome to %s v%s", title, version));
        dialog.setSourceDirectory("Select a source directory ...");

        dialog.setAvailableColors(Color.ALL);      

        ArrayList presets = new ArrayList(colorPresets.values());
        dialog.setAvailableColorPresets(presets);
        dialog.setColorPreset(colorPresets.get(defaultColorPresetName), true);
        
        dialog.setAvailableZProjection(AVAILABLE_ZPROJECTION);
        dialog.setZProjection(Z_PROJECT_NONE);
        dialog.setVisible(true);
        dialog.setAlwaysOnTop(false);
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

        dialog.setStatus(
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
                    // The followind block only open "simple" formats (jpeg, png, etc...) but not czi, lif nor nd2.
                    if ( dataSetService.canOpen(file.toString()) )
                    {                     
                        Dataset img = dataSetService.open(file.toString());
                        processedFiles.add(file);                
                        dataSetService.save(img, destinationFolder.getAbsolutePath() + File.pathSeparator + file.getName());   
                        log.info(String.format("Processing %s DONE", file.toString()));	
                    }
                    else
                    {
                        ImagePlus[] imps = BF.openImagePlus(file.toString());                        

                        for (ImagePlus imp : imps)
                        {
                            if (!batchMode)
                            {
                                imp.show();
                            }
                            imp.close();
                        }  
                    }
                }
                catch( IOException | FormatException e)
                {
                    log.warn(String.format("Unable to open file %s. Reason: %s", file.toString(), e.getMessage()) );
                    ignoredFiles.add(file);
                }
            }

            log.info("Processing DONE");
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

            String destFolderPath = String.format("%s%s%s",
                    sourceFolder.getAbsolutePath(),
                    File.separator,
                    analysedFolderName
            );

            destinationFolder = new File(destFolderPath);

            if (destinationFolder.exists())
            {
                String message = String.format(
                        "Output directory %s already exists.\nDo you want to erase its content before to launch the process ?",
                        destinationFolder.toString()
                );

                DialogPrompt.Result result
                        = ui.showDialog(
                                message,
                                MessageType.QUESTION_MESSAGE,
                                OptionType.YES_NO_CANCEL_OPTION
                        );

                switch (result)
                {
                    case YES_OPTION:
                        FileHelper.deleteDirectoryContent(destinationFolder, false);
                        break;

                    case NO_OPTION:
                        break;

                    case CANCEL_OPTION:
                        destinationFolder = null;
                        sourceFolder = null;
                        break;
                }
            }
            else
            {
                destinationFolder.mkdir();
            }
        }
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

        dialog.setStatus(message);
    }

    /**
     * Stop Mageek.
     *
     * Main dialog will be closed.
     */
    private void stop()
    {
        dialog.setVisible(false);
        dialog.dispose();
    }
    
    private void colorizeFile(File _output, Color[] _colorForChannel, int channels, int slices, int frames )
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
}
