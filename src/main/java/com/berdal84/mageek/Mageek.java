/*
 * To the extent possible under law, the ImageJ developers have waived
 * all copyright and related or neighboring rights to this tutorial code.
 *
 * See the CC0 1.0 Universal license for details:
 *     http://creativecommons.org/publicdomain/zero/1.0/
 */

package com.berdal84.mageek;

import net.imagej.Dataset;
import net.imagej.ImageJ;
import net.imagej.ops.OpService;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.img.Img;
import net.imglib2.type.numeric.RealType;
import org.scijava.command.Command;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;
import org.scijava.ui.DialogPrompt.MessageType;
import org.scijava.ui.UIService;
import org.scijava.widget.FileWidget;
import org.scijava.log.LogLevel;
import org.scijava.log.LogService;

import java.io.File;
import java.lang.StringBuilder;

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
    private UIService uiService;

    @Parameter
    private OpService opService;
    
    @Parameter
    private LogService logService;
    
    /* The current source folder */
    File sourceFolder;
    
    /* The current destination folder */
    File destinationFolder;

    /* User home folder */
    private final File HOME_FOLDER = new File(System.getProperty("user.home"));

    /* Scanned files */
    File scannedFiles[] = {};
    
    /* Ignored files */
    File ignoredFiles[] = {};
    
    /* Processed files */
    File processedFiles[] = {};
    
    @Override
    public void run()
    {    	
    	logService.log( LogLevel.INFO, "Running Mageek ...");

    	// TODO: Display Mageek main window
    	
    	File pickedFolder = this.pickSourceFolder();
     	if ( pickedFolder == null )
    	{
     		this.sourceFolder = null;
     		this.destinationFolder = null;
     		logService.log( LogLevel.WARN, "User cancelled and did not select any folder !");
    	}
    	else
    	{
    		this.sourceFolder = pickedFolder;
    		this.destinationFolder = pickedFolder;
    		logService.log( LogLevel.INFO, "Scanning folder " + this.sourceFolder.toString() + " ...");
    		logService.log( LogLevel.INFO, "Processing files ...");
        	// TODO: display scan result (extension list) and color presets.
    		// TODO: process files
    	}
    	this.showStatistics();
    	logService.log( LogLevel.INFO, "Mageek Stopped");
    }
    
    /**
     * Show the statistics after images have been processed
     * - scanned
     * - ignored
     * - processed
     * With a good bye message.
     */
    private void showStatistics()
    {
    	StringBuilder sb = new StringBuilder(); 
    	MessageType messageType;    	
    	
    	if ( this.sourceFolder == null)
    	{
    		sb.append("\nNo source folder was selected.");
    		messageType = MessageType.WARNING_MESSAGE;
    	}
    	else
    	{
    		sb.append("\nProcessing done !");
    		sb.append("\n\nFolders: ");
        	sb.append("\n- source: ");
        	sb.append(this.sourceFolder.toString());    	
        	sb.append("\n- dest: ");
        	sb.append(this.destinationFolder.toString());
        	
        	sb.append("\n\n - scanned: ");
        	sb.append(this.scannedFiles.length);
        	
        	sb.append("\n - ignored: ");
        	sb.append(this.ignoredFiles.length);
        	
        	sb.append("\n - processed: ");
        	sb.append(this.processedFiles.length);
        	
        	messageType = MessageType.INFORMATION_MESSAGE;
    	}
    	
    	sb.append("\n\nHasta la vista, baby. ^^");

    	uiService.showDialog(sb.toString(), "Processing result window", messageType );
    }
    
    /**
     * Open a window to pick a folder
     * @return
     */
    private File pickSourceFolder()
    {    	
    	return uiService.chooseFile(HOME_FOLDER, FileWidget.DIRECTORY_STYLE);
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

}
