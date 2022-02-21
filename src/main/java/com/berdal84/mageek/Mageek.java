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
import java.io.File;

import javax.swing.WindowConstants;


/**
 * Mageek2 is the Java version of Mageek.ijm macro
 * 
 * This plugin allows to process files by selecting a source directory.
 * The process will:
 * - scan recursively the folder
 * - display a scan result to let the user to choose which file extension to process, and which colors to use,
 * - importing each file's series,
 * - splitting each series's channel,
 * - applying a Z projection (to combine all slices),
 * - colorize each channel,
 * - save result to a "ANALYZED" folder.
 */
@Plugin(type = Command.class, menuPath = "Plugins>Mageek")
public class Mageek<T extends RealType<T>> implements Command {

	
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
    
    /* Scanned files */
    private File scannedFiles[] = {};
    
    /* Ignored files */
    private File ignoredFiles[] = {};
    
    /* Processed files */
    private File processedFiles[] = {};
    
    private MageekDialog dialog;
    
    @Override
    public void run()
    {    
    	log.log( LogLevel.INFO, "Running Mageek ...");
    	
		dialog = new MageekDialog(ui.context());
		dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		
		dialog.addBrowseListener(new ActionListener()
		{
	         public void actionPerformed(ActionEvent arg0)
	         {
	        	dialog.setStatus("Browsing folder ...");
	            askSourceDirectoryToUser();
	            
	            if ( sourceFolder != null )
	            {
	            	dialog.setStatus("Source folder " + sourceFolder.toString() + " picked. Click on process now.");
	            }
	            else
	            {
	            	dialog.setStatus("Browsing aborted.");
	            }
	         }
	     });
	      
		dialog.addLaunchProcessListener(new ActionListener()
		{
	         public void actionPerformed(ActionEvent arg0)
	         {
	        	dialog.setStatus("Processing ...");
	            process();
	            dialog.setStatus("Processing DONE");
	         }
	     });
		
		dialog.addQuitListener(new ActionListener()
		{
	         public void actionPerformed(ActionEvent arg0)
	         {
	            showStats();
	            stop();
	         }
	     });
		
		dialog.setVisible(true);
		dialog.setAlwaysOnTop(false);
    }

	private void process()
	{
		if ( sourceFolder != null )
		{
			log.log( LogLevel.INFO, String.format("Scanning folder %s ...", sourceFolder.getAbsolutePath()) );
    		
        	// TODO: display scan result (extension list) and color presets.
    		// TODO: process files
		}
		else
		{
			log.log( LogLevel.ERROR, "No source folder set !");
		}
	}

	private void askSourceDirectoryToUser()
	{
		// ask user to pick a source folder
    	File pickedFolder = ui.chooseFile(HOME_FOLDER, FileWidget.DIRECTORY_STYLE);
     	if ( pickedFolder == null )
    	{
     		sourceFolder      = null;
     		destinationFolder = null;
     		log.log( LogLevel.TRACE, "User did not select any folder !");
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

    		destinationFolder = new File( destFolderPath );
    		
    		if ( destinationFolder.exists() )
    		{
    			String message = String.format(
    					"Output directory %s already exists.\nDo you want to erase its content before to launch the process ?",
    					destinationFolder.toString()
    					);
		
    			DialogPrompt.Result result = ui.showDialog( message, MessageType.QUESTION_MESSAGE, OptionType.YES_NO_CANCEL_OPTION);
    			
    			switch( result )
    			{
    			case YES_OPTION:
    				Mageek.deleteDirectory(destinationFolder, false);
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
     * Show the statistics after images have been processed
     * - scanned
     * - ignored
     * - processed
     * With a good bye message.
     */
    void showStats()
    {
    	String innerMessage;
    	
    	MessageType messageType;    	
    	
    	if ( this.sourceFolder == null)
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
    				scannedFiles.length,
    				ignoredFiles.length,
    				processedFiles.length
    				);
        	
        	messageType = MessageType.INFORMATION_MESSAGE;
    	}
    	
    	
    	String message = String.format(
    			"%s\n\n\t\tHasta la vista, baby. ^^",
    			innerMessage
    			);

    	ui.showDialog( message, "Processing result window", messageType );
    }
    
    private void stop()
    {
    	dialog.setVisible(false);
    	dialog.dispose();
    }

    
    /**
     * This main function serves for development purposes.
     * It allows you to run the plugin immediately out of
     * your integrated development environment (IDE).
     *
     * @param args whatever, it's ignored
     * @throws Exception
     */
    public static void main(final String... args) throws Exception {
        // create the ImageJ application context with all available services
        final ImageJ ij = new ImageJ();
        ij.ui().showUI();
        ij.command().run(Mageek.class, true);
    }
    
    private static void deleteDirectory(File directoryToBeDeleted, boolean self) {
        File[] allContents = directoryToBeDeleted.listFiles();
        if (allContents != null) {
            for (File file : allContents) {
                Mageek.deleteDirectory(file, true);
            }
        }
        if ( self)
        	directoryToBeDeleted.delete();
    }

}
