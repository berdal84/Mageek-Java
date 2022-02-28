/*
 * The MIT License
 *
 * Copyright 2022 Berdal84.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.berdal84.mageek;

import java.awt.event.ActionListener;
import java.awt.event.ItemListener;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javax.swing.DefaultListModel;
import javax.swing.JComboBox;
import javax.swing.event.ListSelectionListener;
import net.imagej.ops.OpService;
import org.scijava.Context;
import org.scijava.app.StatusService;
import org.scijava.command.CommandService;
import org.scijava.log.LogService;
import org.scijava.plugin.Parameter;
import org.scijava.thread.ThreadService;
import org.scijava.ui.UIService;

/**
 * Main GUI for Mageek
 */
public class MageekFrame extends javax.swing.JFrame
{

    /**
     * Creates new form MageekFrame
     * @param ctx
     */
    public MageekFrame(final Context ctx)
    {
        ctx.inject(this);
        initComponents();
        colorComboBoxes = new ArrayList<>();
        colorComboBoxes.add(color1ComboBox);
        colorComboBoxes.add(color2ComboBox);
        colorComboBoxes.add(color3ComboBox);
        colorComboBoxes.add(color4ComboBox);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents()
    {

        jTabbedPane1 = new javax.swing.JTabbedPane();
        filler1 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(32767, 32767));
        processPanel = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        cancelBtn = new javax.swing.JButton();
        processBtn = new javax.swing.JButton();
        progressBar = new javax.swing.JProgressBar();
        statusLabel = new javax.swing.JLabel();
        srcDirPanel = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        filler2 = new javax.swing.Box.Filler(new java.awt.Dimension(10, 30), new java.awt.Dimension(10, 30), new java.awt.Dimension(32767, 30));
        sourceDirectoryTextEdit = new javax.swing.JTextField();
        browseBtn = new javax.swing.JButton();
        fileListPanel = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        fileTextArea = new javax.swing.JTextPane();
        extensionsPanel = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jScrollPane4 = new javax.swing.JScrollPane();
        extensionList = new javax.swing.JList<>();
        jLabel1 = new javax.swing.JLabel();
        colorPanel = new javax.swing.JPanel();
        zProjectionComboBox = new javax.swing.JComboBox<>();
        zProjectionLabel = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        colorPreset = new javax.swing.JComboBox<>();
        color1ComboBox = new javax.swing.JComboBox<>();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        color2ComboBox = new javax.swing.JComboBox<>();
        color3ComboBox = new javax.swing.JComboBox<>();
        jLabel9 = new javax.swing.JLabel();
        color4ComboBox = new javax.swing.JComboBox<>();
        batchCheckBox = new javax.swing.JCheckBox();
        jLabel10 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        processPanel.setLayout(new java.awt.GridLayout(2, 0));

        jPanel2.setLayout(new java.awt.BorderLayout());

        cancelBtn.setText("Cancel");
        jPanel2.add(cancelBtn, java.awt.BorderLayout.LINE_START);

        processBtn.setText("Process");
        processBtn.setMaximumSize(new java.awt.Dimension(200, 29));
        processBtn.setMinimumSize(new java.awt.Dimension(100, 29));
        processBtn.setPreferredSize(new java.awt.Dimension(200, 29));
        jPanel2.add(processBtn, java.awt.BorderLayout.CENTER);

        processPanel.add(jPanel2);

        progressBar.setStringPainted(true);
        processPanel.add(progressBar);

        statusLabel.setBackground(new java.awt.Color(153, 153, 153));
        statusLabel.setFont(new java.awt.Font("Lucida Grande", 2, 13)); // NOI18N
        statusLabel.setText("Status: Please select a source folder");

        srcDirPanel.setLayout(new javax.swing.BoxLayout(srcDirPanel, javax.swing.BoxLayout.LINE_AXIS));

        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel3.setText("Source directory:");
        jLabel3.setAlignmentX(1.0F);
        srcDirPanel.add(jLabel3);
        srcDirPanel.add(filler2);

        sourceDirectoryTextEdit.setEditable(false);
        sourceDirectoryTextEdit.setText("/Users/berenger/");
        srcDirPanel.add(sourceDirectoryTextEdit);

        browseBtn.setText("Browse");
        browseBtn.setMaximumSize(new java.awt.Dimension(40, 29));
        browseBtn.setMinimumSize(new java.awt.Dimension(20, 29));
        srcDirPanel.add(browseBtn);

        jLabel5.setText("File list:");

        fileTextArea.setEditable(false);
        fileTextArea.setBorder(null);
        jScrollPane2.setViewportView(fileTextArea);

        javax.swing.GroupLayout fileListPanelLayout = new javax.swing.GroupLayout(fileListPanel);
        fileListPanel.setLayout(fileListPanelLayout);
        fileListPanelLayout.setHorizontalGroup(
            fileListPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel5)
            .addComponent(jScrollPane2)
        );
        fileListPanelLayout.setVerticalGroup(
            fileListPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(fileListPanelLayout.createSequentialGroup()
                .addComponent(jLabel5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2))
        );

        jLabel2.setText("Extensions:");

        extensionList.setModel(listModel);
        extensionList.setAlignmentX(0.0F);
        extensionList.addListSelectionListener(new javax.swing.event.ListSelectionListener()
        {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt)
            {
                extensionListValueChanged(evt);
            }
        });
        jScrollPane4.setViewportView(extensionList);

        javax.swing.GroupLayout extensionsPanelLayout = new javax.swing.GroupLayout(extensionsPanel);
        extensionsPanel.setLayout(extensionsPanelLayout);
        extensionsPanelLayout.setHorizontalGroup(
            extensionsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
            .addGroup(extensionsPanelLayout.createSequentialGroup()
                .addComponent(jLabel2)
                .addGap(0, 38, Short.MAX_VALUE))
        );
        extensionsPanelLayout.setVerticalGroup(
            extensionsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(extensionsPanelLayout.createSequentialGroup()
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 304, Short.MAX_VALUE))
        );

        jLabel1.setFont(new java.awt.Font("Lucida Grande", 0, 24)); // NOI18N
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("Mageek");

        colorPanel.setAutoscrolls(true);

        zProjectionLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        zProjectionLabel.setText("Z projection");

        jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel4.setText("Color Preset");

        jLabel6.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel6.setText("Channel 1");

        jLabel7.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel7.setText("Channel 3");

        jLabel8.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel8.setText("Channel 2");

        jLabel9.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel9.setText("Channel 4");

        batchCheckBox.setText("Batch Mode");
        batchCheckBox.setToolTipText("Enable by default, will process the images in background (faster).");

        javax.swing.GroupLayout colorPanelLayout = new javax.swing.GroupLayout(colorPanel);
        colorPanel.setLayout(colorPanelLayout);
        colorPanelLayout.setHorizontalGroup(
            colorPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(colorPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(colorPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(zProjectionLabel, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel4, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel8, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel9, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(colorPanelLayout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jLabel7)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(colorPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(colorPreset, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(zProjectionComboBox, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(colorPanelLayout.createSequentialGroup()
                        .addGroup(colorPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(color1ComboBox, javax.swing.GroupLayout.Alignment.LEADING, 0, 165, Short.MAX_VALUE)
                            .addComponent(color4ComboBox, javax.swing.GroupLayout.Alignment.LEADING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(color2ComboBox, javax.swing.GroupLayout.Alignment.LEADING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(color3ComboBox, javax.swing.GroupLayout.Alignment.LEADING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, colorPanelLayout.createSequentialGroup()
                        .addComponent(batchCheckBox, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addContainerGap())))
        );
        colorPanelLayout.setVerticalGroup(
            colorPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(colorPanelLayout.createSequentialGroup()
                .addGroup(colorPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(zProjectionComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(zProjectionLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(colorPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(colorPreset, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(colorPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(color1ComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(colorPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(color2ComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel8))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(colorPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(color3ComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel7))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(colorPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(color4ComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel9))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(batchCheckBox, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );

        jLabel10.setText("Options:");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                        .addComponent(filler1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(srcDirPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 843, Short.MAX_VALUE)
                            .addComponent(statusLabel, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                .addComponent(extensionsPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(fileListPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(colorPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jLabel10)
                                    .addComponent(processPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))))))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(filler1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 49, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(srcDirPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(extensionsPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(fileListPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(7, 7, 7))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel10)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(colorPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(processPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)))
                .addComponent(statusLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void extensionListValueChanged(javax.swing.event.ListSelectionEvent evt)//GEN-FIRST:event_extensionListValueChanged
    {//GEN-HEADEREND:event_extensionListValueChanged
 
    }//GEN-LAST:event_extensionListValueChanged


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox batchCheckBox;
    private javax.swing.JButton browseBtn;
    private javax.swing.JButton cancelBtn;
    private javax.swing.JComboBox<String> color1ComboBox;
    private javax.swing.JComboBox<String> color2ComboBox;
    private javax.swing.JComboBox<String> color3ComboBox;
    private javax.swing.JComboBox<String> color4ComboBox;
    private javax.swing.JPanel colorPanel;
    private javax.swing.JComboBox<String> colorPreset;
    private javax.swing.JList<String> extensionList;
    private javax.swing.JPanel extensionsPanel;
    private javax.swing.JPanel fileListPanel;
    private javax.swing.JTextPane fileTextArea;
    private javax.swing.Box.Filler filler1;
    private javax.swing.Box.Filler filler2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JButton processBtn;
    private javax.swing.JPanel processPanel;
    private javax.swing.JProgressBar progressBar;
    private javax.swing.JTextField sourceDirectoryTextEdit;
    private javax.swing.JPanel srcDirPanel;
    private javax.swing.JLabel statusLabel;
    private javax.swing.JComboBox<String> zProjectionComboBox;
    private javax.swing.JLabel zProjectionLabel;
    // End of variables declaration//GEN-END:variables

    private final ArrayList<JComboBox> colorComboBoxes;
    
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

    private DefaultListModel listModel = new DefaultListModel();
    
    public void addBatchModeListener(PropertyChangeListener listener)
    { 
        batchCheckBox.addPropertyChangeListener(listener);
    }
   
    public void addCancelBtnListener(ActionListener listener)
    { 
        cancelBtn.addActionListener(listener);
    }
            
    public void addFileExtSelectionListener(ListSelectionListener listener)
    { 
        extensionList.addListSelectionListener(listener);
    }

    public void addSelectColorPresetListener(ItemListener listener)
    {
        colorPreset.addItemListener(listener);
    }
    
    public void addSelectZProjectionListener(ItemListener listener)
    {
        zProjectionComboBox.addItemListener(listener);
    }
    
    public void addSelectColorListener(ItemListener listener)
    {
        for( JComboBox eachCombo : colorComboBoxes )
        {
            eachCombo.addItemListener(listener);  
        }        
    }
    
    public void removeSelectColorListener(ItemListener listener)
    {
        for( JComboBox eachCombo : colorComboBoxes )
        {
            eachCombo.removeItemListener(listener);  
        }        
    }
           
    public void addBrowseBtnListener(ActionListener listener)
    {
        browseBtn.addActionListener(listener);
    }

    public void addLaunchProcessListener(ActionListener listener)
    {
        processBtn.addActionListener(listener);
    }

    public void setStatus(String str)
    {
        statusLabel.setText(String.format("Status: %s", str));
    }

    public void setProgress(int n)
    {
        progressBar.setValue(n);
    }

    public void setSourceDirectory(String path)
    {
        sourceDirectoryTextEdit.setText(path);
    }

    public void setFileExtensionList(ArrayList<String> extensions)
    {
        extensionList.removeAll();
        
        for (String eachExtension : extensions)
        {
            listModel.add( 0, eachExtension );
        }
    }

    public List<String> getSelectedFileExtensions()
    {
        List<String> result = extensionList.getSelectedValuesList();       
        return result;
    }

    void clearFileList()
    {
        fileTextArea.removeAll();
    }
    
    void setFileList(ArrayList<File> scannedFiles)
    {
        fileTextArea.removeAll();
        StringBuilder sb = new StringBuilder();
        
        scannedFiles.forEach((each) ->
        {
            sb.append(each.toString());
            sb.append('\n');
        });
        
        fileTextArea.setText(sb.toString());
    };

    void setSelectedFileExtensions(String[] _extensions)
    {      
        
        for (String eachExtension : _extensions)
        {
            final int index = listModel.lastIndexOf(eachExtension);
            if ( index != -1 )
            {
                extensionList.addSelectionInterval(index, index);
            }            
        }
    }
    
    void setAvailableColors(MColor[] _colors)
    {
        for( JComboBox eachCB : colorComboBoxes )
        {
            eachCB.removeAllItems();
        }
        
        for( MColor eachColor : _colors)
        {
            for( JComboBox eachCB : colorComboBoxes )
            {
                eachCB.addItem(eachColor.toString());
            }
        }
    }
    
    void setColor(MColor _color, int _channel)
    {
        colorComboBoxes
                .get(_channel)
                .setSelectedItem(_color.toString());
    }
        
    void setAvailableZProjection(String[] _projections)
    {
        zProjectionComboBox.removeAllItems();
        for( String each : _projections)
        {
            zProjectionComboBox.addItem(each);
        }
    }
    
    void setZProjection(String _projection)
    {
        zProjectionComboBox.setSelectedItem(_projection);
    }

    void setColorPreset(MColorPreset _preset, boolean _setColors)
    {
        colorPreset.setSelectedItem(_preset.getName());
        if ( _setColors )
        {
            int i = 0;
            for( JComboBox eachCB : colorComboBoxes)
            {
                ItemListener[] listeners = eachCB.getItemListeners();
                for(ItemListener listener : listeners)
                {
                    removeSelectColorListener(listener);
                }
                
                eachCB.setSelectedItem(_preset.getIJColorStringAt(i));
                
                for(ItemListener listener : listeners)
                {
                    addSelectColorListener(listener);
                }
                
                i++;
            } 
        }
    }

    void setAvailableColorPresets(ArrayList<MColorPreset> _presets)
    {
        colorPreset.removeAllItems();
        for( MColorPreset eachPreset : _presets)
        {
            colorPreset.addItem(eachPreset.getName());
        }
    }

    String getSelectedPresetName()
    {
        return (String) colorPreset.getSelectedItem();
    }

    String getSelectedIJColorStringAt(int i)
    {
        return (String) colorComboBoxes.get(i).getSelectedItem();
    }

    void setBatchMode(boolean batchMode)
    {
        batchCheckBox.setSelected(batchMode);
    }
    
};
