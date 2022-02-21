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
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
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
		
        progressBar = new JProgressBar();
        progressBar.setValue(0);
        progressBar.setStringPainted(true);
        JPanel progressPanel = new JPanel();
        progressPanel.add(progressBar);
        contentPanel.add(progressPanel);
        
		statusLabel = new JLabel("Welcome to Mageek");
		contentPanel.add(statusLabel);
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

}