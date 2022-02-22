/*
 * To the extent possible under law, the ImageJ developers have waived
 * all copyright and related or neighboring rights to this tutorial code.
 *
 * See the CC0 1.0 Universal license for details:
 *     http://creativecommons.org/publicdomain/zero/1.0/
 */
package com.berdal84.mageek;

import net.imagej.ImageJ;
import net.imagej.ops.OpService;
import net.imglib2.type.numeric.RealType;
import org.scijava.command.Command;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;
import org.scijava.ui.DialogPrompt.MessageType;
import org.scijava.ui.DialogPrompt.OptionType;
import org.scijava.ui.DialogPrompt;

import org.scijava.ui.UIService;
import org.scijava.widget.FileWidget;
import org.scijava.log.LogLevel;
import org.scijava.log.LogService;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JTree;

import javax.swing.WindowConstants;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

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
public class Mageek<T extends RealType<T>> implements Command
{

    @Parameter
    private UIService ui;

    @Parameter
    private OpService op;

    @Parameter
    private LogService log;

    /* The current source folder */
    private File sourceFolder;

    /* The current destination folder */
    private File destinationFolder;

    /* User home folder */
    private final File HOME_FOLDER = new File(System.getProperty("user.home"));

    /* Subfolder name to put all analysed files */
    private final String ANALYSED_SUBFOLDER_PATH = "ANALYSED";

    /* The script title */
    private final String SCRIPT_TITLE = "Mageek";

    /* The script title */
    private final String SCRIPT_VERSION = "1.0.0";

    /* Scanned extensions */
    private ArrayList<String> scannedFileExtensions = new ArrayList<String>();
    
    /* Scanned files */
    private ArrayList<File> scannedFiles = new ArrayList<File>();

    /* Ignored files */
    private ArrayList<File> ignoredFiles = new ArrayList<File>();

    /* Processed files */
    private ArrayList<File> processedFiles = new ArrayList<File>();

    /* The main UI */
    private MageekFrame dialog;
    
    private String[] SELECTED_EXTENSIONS_DEFAULT = {"*.czi", "*.lif", "*.nd2"};

    @Override
    public void run()
    {
        log.log(LogLevel.INFO, String.format("Running %s ...", SCRIPT_TITLE));

        dialog = new MageekFrame(ui.context());
        dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        dialog.setStatus(String.format("Welcome to %s v%s", SCRIPT_TITLE, SCRIPT_VERSION));
        dialog.setSourceDirectory("Select a source directory ...");

        dialog.addBrowseListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent arg0)
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
                    
                    scannedFileExtensions = FileHelper.getFileExtensions( scannedFiles );
                    dialog.setExtensions(scannedFileExtensions);
                    dialog.setSelectedExtensions(SELECTED_EXTENSIONS_DEFAULT);
                }
                else
                {
                    dialog.setStatus("Browsing aborted.");
                    dialog.setProgress(0);
                    dialog.clearFileList();
                }
            }
        });

        dialog.addLaunchProcessListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent evt)
            {
                dialog.setStatus("Processing ...");
                process();
                dialog.setStatus("Processing DONE");
                dialog.setProgress(100);
                showStats();
            }
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

        dialog.addExtensionCheckedListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent evt)
            {
                dialog.setStatus(
                    String.format(
                        "Extension %s checked/unchecked. Updating file list ...",
                        evt.getActionCommand()
                    )
                );
                filterFiles(dialog.getCheckedExtensions());
            }
        });

        dialog.setVisible(true);
        dialog.setAlwaysOnTop(false);
    }

    protected void filterFiles(List<String> checkedExtensions)
    {
        dialog.setStatus(
            String.format(
                "Filtering files with the following extensions: %s",
                checkedExtensions.toString()
            )
        );
    }

    private void process()
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
            log.log(LogLevel.INFO, "Processing DONE");

            // TODO: display scan result (extension list) and color presets.
            // TODO: process files
        }
        else
        {
            log.log(LogLevel.ERROR, "No source folder set !");
        }
    }

    private void askSourceDirectoryToUser()
    {
        // ask user to pick a source folder
        File pickedFolder = ui.chooseFile(HOME_FOLDER, FileWidget.DIRECTORY_STYLE);
        
        if (pickedFolder == null)
        {
            sourceFolder = null;
            destinationFolder = null;
            log.log(LogLevel.TRACE, "User did not select any folder !");
        }
        else
        {
            sourceFolder = pickedFolder;

            String destFolderPath = String.format(
                    "%s%s%s",
                    sourceFolder.getAbsolutePath(),
                    File.separator,
                    ANALYSED_SUBFOLDER_PATH
            );

            destinationFolder = new File(destFolderPath);

            if (destinationFolder.exists())
            {
                String message = String.format(
                        "Output directory %s already exists.\nDo you want to erase its content before to launch the process ?",
                        destinationFolder.toString()
                );

                DialogPrompt.Result result =
                    ui.showDialog(
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
     * Show the statistics after images have been processed - scanned - ignored
     * - processed With a good bye message.
     */
    void showStats()
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
                    "%s processed the folder %s.\nOutput file(s) were generated into %s.\n\nResume:\n - scanned: %d file(s)\n - ignored: %d file(s)\n - processed: %d file(s)",
                    SCRIPT_TITLE,
                    sourceFolder.toString(),
                    destinationFolder.toString(),
                    scannedFiles.size(),
                    ignoredFiles.size(),
                    processedFiles.size()
            );

            messageType = MessageType.INFORMATION_MESSAGE;
        }

        String message = String.format(
                "%s\n\n\t\tHasta la vista, baby. ^^",
                innerMessage
        );

        dialog.setStats(message);
    }

    private void stop()
    {
        dialog.setVisible(false);
        dialog.dispose();
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
        ij.command().run(Mageek.class, true);
    }

}
