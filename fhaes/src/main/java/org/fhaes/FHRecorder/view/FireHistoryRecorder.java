/**************************************************************************************************
 * Fire History Analysis and Exploration System (FHAES), Copyright (C) 2015
 * 
 * Contributors: Alex Beatty, Clayton Bodendein, Kyle Hartmann, Scott Goble, and Peter Brewer
 * 
 * 		This program is free software: you can redistribute it and/or modify it under the terms of
 * 		the GNU General Public License as published by the Free Software Foundation, either version
 * 		3 of the License, or (at your option) any later version.
 * 
 * 		This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * 		without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * 		See the GNU General Public License for more details.
 * 
 * 		You should have received a copy of the GNU General Public License along with this program.
 * 		If not, see <http://www.gnu.org/licenses/>.
 * 
 *************************************************************************************************/
package org.fhaes.FHRecorder.view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.border.BevelBorder;
import javax.swing.border.SoftBevelBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.fhaes.FHRecorder.controller.FileController;
import org.fhaes.FHRecorder.controller.IOController;
import org.fhaes.FHRecorder.controller.SampleController;
import org.fhaes.FHRecorder.model.FHX2_File;
import org.fhaes.enums.FeedbackDisplayProtocol;
import org.fhaes.enums.FeedbackMessageType;
import org.fhaes.exceptions.CompositeFileException;
import org.fhaes.feedback.FeedbackMessagePanel;
import org.fhaes.feedback.FeedbackPreferenceManager.FeedbackDictionary;

import net.miginfocom.swing.MigLayout;

/**
 * FireHistoryRecorder Class.
 * 
 * @author Alex Beatty, Clayton Bodendein, Kyle Hartmann, Scott Goble
 */
public class FireHistoryRecorder extends JDialog {
	
	private static final long serialVersionUID = 1L;
	
	// Declare local constants
	private final int DATA_TAB_INDEX = 0;
	private final int METADATA_TAB_INDEX = 1;
	private final int SUMMARY_TAB_INDEX = 2;
	private final int GRAPH_TAB_INDEX = 3;
	
	// Declare primary GUI panels
	private SampleInputPanel sampleInput;
	private MetaDataPanel metaDataPanel;
	private CommentPanel commentPanel;
	private SummaryPanel summaryPanel;
	private GraphPanel graphPanel;
	private ErrorDisplayPanel errorPanel;
	
	// Declare other GUI objects
	private JTabbedPane tabbedPane;
	private JPanel dataTab;
	private JPanel metadataTab;
	private JPanel summaryTab;
	private JPanel graphsTab;
	private JButton saveButton;
	private JButton discardChangesButton;
	private JButton closeButton;
	
	// Declare feedback message panel
	private static FeedbackMessagePanel feedbackMessagePanel;
	
	// Declare local variables
	private boolean leftTabsSinceRedraw = true;
	
	/**
	 * Creates new form PrimaryWindow.
	 */
	public FireHistoryRecorder() {
		
		initGUI();
	}
	
	/**
	 * Closes the dialog but only after running necessary checks to ensure user doesn't inadvertantly lose data.
	 */
	private void closeAfterRunningChecks() {
		
		if (FileController.isChangedSinceLastSave())
		{
			Object[] options = { "Save", "Close without saving", "Cancel" };
			
			int n = JOptionPane.showOptionDialog(FileController.thePrimaryWindow, "Save changes to file before closing?", "Confirm",
					JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[2]);
					
			if (n == JOptionPane.YES_OPTION)
			{
				FileController.save();
				setVisible(false);
			}
			else if (n == JOptionPane.NO_OPTION)
			{
				FileController.setIsChangedSinceLastSave(false);
				FileController.setIsChangedSinceOpened(false);
				setVisible(false);
			}
			else if (n == JOptionPane.CANCEL_OPTION)
			{
				return;
			}
		}
		
		setVisible(false);
	}
	
	/**
	 * Selects the first sample of the currently loaded file.
	 */
	public void selectFirstSample() {
		
		try
		{
			this.sampleInput.sampleListBox.setSelectedIndex(0);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	/**
	 * Handles when the "Save" menu button is clicked.
	 * 
	 * @param evt
	 * @throws CompositeFileException
	 */
	private void doSave(java.awt.event.ActionEvent evt) throws CompositeFileException {
		
		updateOptionalData();
		
		if (FileController.isFileCorrupted())
		{
			BufferedReader br = new BufferedReader(new StringReader(errorPanel.getFixedFile()));
			tabbedPane.setSelectedIndex(0);
			tabbedPane.remove(errorPanel);
			try
			{
				IOController.readFileFromBufferedReader(br);
				FileController.displayUpdatedFile();
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
		
		FileController.save();
		
		feedbackMessagePanel.updateFeedbackMessage(FeedbackMessageType.INFO, FeedbackDisplayProtocol.AUTO_HIDE,
				FeedbackDictionary.FHRECORDER_FILE_SAVED_MESSAGE.toString());
	}
	
	/**
	 * Displays all valid screens.
	 * 
	 * @param inFHX2File
	 */
	public void generateScreens(FHX2_File inFHX2File) {
		
		if (sampleInput != null)
			dataTab.remove(sampleInput);
		if (metaDataPanel != null)
			metadataTab.remove(metaDataPanel);
		if (commentPanel != null)
			metadataTab.remove(commentPanel);
		if (summaryPanel != null)
			summaryTab.remove(summaryPanel);
		if (graphPanel != null)
			graphsTab.remove(graphPanel);
			
		sampleInput = new SampleInputPanel(inFHX2File.getRequiredPart());
		metaDataPanel = new MetaDataPanel(inFHX2File.getOptionalPart());
		commentPanel = new CommentPanel(inFHX2File.getOptionalPart());
		summaryPanel = new SummaryPanel();
		graphPanel = new GraphPanel();
		
		dataTab.add(sampleInput, BorderLayout.CENTER);
		metadataTab.add(metaDataPanel, BorderLayout.CENTER);
		metadataTab.add(commentPanel, BorderLayout.SOUTH);
		summaryTab.add(summaryPanel, BorderLayout.CENTER);
		graphsTab.add(graphPanel, BorderLayout.CENTER);
	}
	
	/**
	 * Refreshes all tab-panels with the most recent information.
	 */
	public void showInput() {
		
		this.tabbedPane.setEnabled(true);
		this.tabbedPane.setSelectedIndex(0);
		
		if (FileController.isFileCorrupted())
		{
			errorPanel = new ErrorDisplayPanel();
			errorPanel.setBorder(new SoftBevelBorder(BevelBorder.LOWERED, null, null, null, null));
			errorPanel.displayFileErrors(IOController.getFile().getErrors());
			tabbedPane.addTab("Errors", null, errorPanel, null);
			tabbedPane.setSelectedComponent(errorPanel);
			tabbedPane.setEnabled(false);
			errorPanel.setEnabled(false);
			errorPanel.setScrollBarsToTop();
			
			FileController.setIsChangedSinceLastSave(false);
			FileController.setIsChangedSinceOpened(true);
		}
		else
		{
			tabbedPane.setSelectedIndex(0);
			tabbedPane.setEnabled(true);
			tabbedPane.remove(errorPanel);
		}
		
		// file contains a data set that does not have a defined end year
		if (FileController.wasLastYearDefinedInFile() == false)
		{
			feedbackMessagePanel.updateFeedbackMessage(FeedbackMessageType.INFO, FeedbackDisplayProtocol.MANUAL_HIDE,
					"File contains valid data with an unformatted sample. A temporary sample has been generated using the boundaries of the data set.");
		}
		
		// file either has bad data or is not an FHX file
		else if (IOController.getFile().fileHasNoValidData())
		{
			if (!FileController.isFileNew())
			{
				feedbackMessagePanel.updateFeedbackMessage(FeedbackMessageType.WARNING, FeedbackDisplayProtocol.MANUAL_HIDE,
						"File contains invalid data or is not an FHX file. Saving will overwrite previous file contents.");
			}
		}
		
		// file is properly formatted
		else
		{
			feedbackMessagePanel.clearFeedbackMessage();
		}
	}
	
	/**
	 * Displays the data tab.
	 */
	public void showInfo() {
		
		this.tabbedPane.setSelectedIndex(DATA_TAB_INDEX);
	}
	
	/**
	 * Displays the metadata tab.
	 */
	public void showComments() {
		
		this.tabbedPane.setSelectedIndex(METADATA_TAB_INDEX);
	}
	
	/**
	 * TODO
	 */
	public void redrawSampleInputPanel() {
		
		sampleInput.redrawSampleListPanel();
	}
	
	/**
	 * TODO
	 */
	public void redrawEventPanel() {
		
		sampleInput.redrawSampleDataPanel(SampleController.getSelectedSampleIndex());
	}
	
	/**
	 * TODO
	 */
	public void enableDependentMenuItems() {
		
		this.dataTab.setEnabled(true);
		this.metadataTab.setEnabled(true);
	}
	
	/**
	 * TODO
	 */
	public void disableDependentMenuItems() {
		
		this.dataTab.setEnabled(false);
		this.metadataTab.setEnabled(false);
	}
	
	/**
	 * Saves the metadata and comments to the FHX file.
	 */
	private void updateOptionalData() {
		
		metaDataPanel.saveInfoToData();
		commentPanel.saveComments();
	}
	
	/**
	 * Gets the feedbackMessagePanel instance.
	 */
	public static FeedbackMessagePanel getFeedbackMessagePanel() {
		
		return feedbackMessagePanel;
	}
	
	/**
	 * Initializes the GUI.
	 */
	private void initGUI() {
		
		this.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		this.setModalityType(ModalityType.APPLICATION_MODAL);
		this.setMinimumSize(new Dimension(1000, 700));
		this.setResizable(true);
		
		this.addWindowListener(new WindowAdapter() {
			
			@Override
			public void windowClosing(WindowEvent e) {
				
				closeAfterRunningChecks();
			}
		});
		
		this.getContentPane().setLayout(new MigLayout("hidemode 2,insets 0", "[grow][][][][0:0:0]", "[][500:500,grow,fill][35:35:35]"));
		
		feedbackMessagePanel = new FeedbackMessagePanel();
		this.getContentPane().add(feedbackMessagePanel, "cell 0 0 5 1,grow");
		
		tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		this.getContentPane().add(tabbedPane, "cell 0 1 5 1,grow");
		
		dataTab = new JPanel();
		dataTab.setBorder(new SoftBevelBorder(BevelBorder.LOWERED, null, null, null, null));
		metadataTab = new JPanel();
		metadataTab.setBorder(new SoftBevelBorder(BevelBorder.LOWERED, null, null, null, null));
		summaryTab = new JPanel();
		summaryTab.setBorder(new SoftBevelBorder(BevelBorder.LOWERED, null, null, null, null));
		graphsTab = new JPanel();
		graphsTab.setBorder(new SoftBevelBorder(BevelBorder.LOWERED, null, null, null, null));
		
		dataTab.setLayout(new BorderLayout());
		metadataTab.setLayout(new BorderLayout());
		summaryTab.setLayout(new BorderLayout());
		graphsTab.setLayout(new BorderLayout());
		
		tabbedPane.addTab("Data", null, dataTab, null);
		tabbedPane.addTab("Metadata", null, metadataTab, null);
		tabbedPane.addTab("Summary", null, summaryTab, null);
		tabbedPane.addTab("Graphs", null, graphsTab, null);
		
		tabbedPane.addChangeListener(new ChangeListener() {
			
			// this refreshes the summary and graph tabs whenever they are selected in the window
			@Override
			public void stateChanged(ChangeEvent e) {
				
				if (tabbedPane.getSelectedIndex() == SUMMARY_TAB_INDEX || tabbedPane.getSelectedIndex() == GRAPH_TAB_INDEX)
				{
					if (leftTabsSinceRedraw)
					{
						IOController.getFile().getRequiredPart().calculateFirstYear();
						IOController.getFile().getRequiredPart().calculateLastYear();
						
						generateScreens(IOController.getFile());
						sampleInput.redrawSampleDataPanel(SampleController.getSelectedSampleIndex());
						summaryPanel.refreshTable();
						graphPanel.refreshCharts(false);
						
						leftTabsSinceRedraw = false;
					}
				}
				else
				{
					leftTabsSinceRedraw = true;
				}
			}
		});
		
		saveButton = new JButton("Save");
		saveButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				
				try
				{
					doSave(e);
				}
				catch (CompositeFileException ex)
				{
					ex.printStackTrace();
				}
			}
			
		});
		this.getContentPane().add(saveButton, "cell 1 2,growx,aligny center");
		
		closeButton = new JButton("Close");
		closeButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				
				closeAfterRunningChecks();
			}
			
		});
		this.getContentPane().add(closeButton, "cell 2 2,growx,aligny center");
		
		discardChangesButton = new JButton("Discard changes");
		discardChangesButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				
				FileController.filePath = null;
				setVisible(false);
			}
			
		});
		this.getContentPane().add(discardChangesButton, "cell 3 2,growx,aligny center");
		
		this.setTitle(FileController.progName);
		this.pack();
	}
}
