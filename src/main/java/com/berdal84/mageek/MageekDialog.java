package com.berdal84.mageek;
/*
 * To the extent possible under law, the ImageJ developers have waived
 * all copyright and related or neighboring rights to this tutorial code.
 *
 * See the Unlicense for details:
 *     https://unlicense.org/
 */

import java.awt.AWTEvent;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTextArea;
import javax.swing.border.EmptyBorder;

import net.imagej.ops.OpService;

import org.scijava.Context;
import org.scijava.app.StatusService;
import org.scijava.command.CommandService;
import org.scijava.log.LogService;
import org.scijava.plugin.Parameter;
import org.scijava.thread.ThreadService;
import org.scijava.ui.UIService;

public class MageekDialog extends JDialog {
	public AWTEvent browserButtonClicked;

	@Parameter
	private OpService ops;

	@Parameter
	private LogService log;

	@Parameter
	private StatusService status;

	@Parameter
	private CommandService cmd;

	@Parameter
	private ThreadService thread;

	@Parameter
	private UIService ui;

	private final JPanel contentPanel = new JPanel();

	private final JButton browseBtn;
	private final JButton quitBtn;
	private final JButton processBtn;
	private final JLabel statusLabel;
	private final JProgressBar progressBar;
	private final JPanel extensionPanel;
	private ActionListener extensionCheckedListener;
	private final JTextArea statusTexArea;
	/**
	 * Create the dialog.
	 */
	public MageekDialog(final Context ctx)
	{
		ctx.inject(this);
		setBounds(100, 100, 700, 400);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);

		browseBtn = new JButton("Pick source folder");
		contentPanel.add(browseBtn);

		processBtn = new JButton("Launch processr");
		contentPanel.add(processBtn);

		quitBtn = new JButton("Quit");
		contentPanel.add(quitBtn);
		
		extensionPanel = new JPanel();
		contentPanel.add(extensionPanel);
		
        progressBar = new JProgressBar();
        progressBar.setValue(0);
        progressBar.setStringPainted(true);
        progressBar.setSize(500, 12);
        JPanel progressPanel = new JPanel();
        progressPanel.add(progressBar);
        contentPanel.add(progressPanel);
        
		statusLabel = new JLabel("Welcome to Mageek");
		contentPanel.add(statusLabel);
		
		statusTexArea = new JTextArea(200, 20);
		contentPanel.add(statusTexArea);
	}

	public void addExtensionCheckedListener(ActionListener listener) {
		extensionCheckedListener = listener;
	}
	
	public void addBrowseListener(ActionListener listener) {
		browseBtn.addActionListener(listener);
	}

	public void addLaunchProcessListener(ActionListener listener) {
		processBtn.addActionListener(listener);
	}

	public void addQuitListener(ActionListener listener) {
		quitBtn.addActionListener(listener);
	}

	public void setStatus(String str) {
		statusLabel.setText(str);
	}
	
	public void setProgress(int n) {
		progressBar.setValue(n);
	}
	
	public void setExtensions(String[] extensions) {
		
		extensionPanel.removeAll();
		for( String eachExtension : extensions) {
			JCheckBox checkbox = new JCheckBox( String.format("*.%s", eachExtension) );
	        checkbox.setSelected(true);
	        checkbox.setActionCommand(eachExtension);
	        checkbox.addActionListener(extensionCheckedListener);
	        extensionPanel.add(checkbox);
		}
	}
	
	public void setExtensionVisible(boolean visible) {
		extensionPanel.setVisible(visible);
	}

	public List<String> getCheckedExtensions() {
		List<String> result = new ArrayList<String>();
		for( Component c : extensionPanel.getComponents() ) {
			JCheckBox cb = (JCheckBox)c;
			if( cb.isSelected() ) {
				result.add(cb.getActionCommand());
			}
		}
		return result;
	}

	public void setStats(String message) {
		statusTexArea.removeAll();
		statusTexArea.append(message);
		
	}


}