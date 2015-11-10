/**************************************************************************************************
 * Fire History Analysis and Exploration System (FHAES), Copyright (C) 2015
 * 
 * Contributors: Elena Velasquez and Peter Brewer
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
package org.fhaes.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComponent;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.DefaultHighlighter.DefaultHighlightPainter;

import org.fhaes.analysis.FHDescriptiveStats;
import org.fhaes.enums.EventTypeToProcess;
import org.fhaes.fhfilereader.FHFile;
import org.fhaes.help.RemoteHelp;
import org.fhaes.neofhchart.ChartActions;
import org.fhaes.neofhchart.NeoFHChart;
import org.fhaes.preferences.App;
import org.fhaes.preferences.FHAESPreferences.PrefKey;
import org.fhaes.preferences.PrefsEvent;
import org.fhaes.preferences.PrefsListener;
import org.fhaes.util.Builder;
import org.fhaes.util.FHAESAction;
import org.fhaes.util.Platform;
import org.fhaes.util.TextLineNumber;
import org.jdesktop.swingx.JXTable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * ReportPanel Class. This panel contains tabs with the reports produced by the various FHAES modules.
 * 
 * @author Peter Brewer
 */
public class ReportPanel extends JPanel implements PrefsListener {
	
	private static final long serialVersionUID = 1L;
	
	// Declare logger
	private static final Logger log = LoggerFactory.getLogger(ReportPanel.class);
	
	// Declare local constants
	private static final int FILE_VIEWER_INDEX = 0;
	private static final int FILE_SUMMARY_INDEX = 1;
	private static final int ANALYSIS_INDEX = 2;
	private static final int MAP_INDEX = 3;
	private static final int CHART_INDEX = 4;
	private static final int FIVE_MEGABYTE_LENGTH = 5242880;
	
	// Declare GUI components
	private JTabbedPane tabbedPane;
	protected JTextArea txtSummary;
	protected JTextArea txtFHX;
	private JTextArea errorMessage;
	private JButton btnEditFile;
	protected FHAESAction actionSelectAll;
	protected FHAESAction actionCopy;
	protected FHAESAction actionResultsHelp;
	protected FHAESAction actionParamConfig;
	protected AnalysisResultsPanel panelResults;
	protected MapPanel panelMap;
	protected NeoFHChart panelChart;
	
	// Declare local variables
	private ArrayList<FHFile> fhxFiles;
	private FHFile fhxFile;
	
	/**
	 * Create the panel.
	 */
	public ReportPanel() {
	
		initActions();
		initGUI();
	}
	
	/**
	 * Returns a value indicating whether the file has been populated.
	 * 
	 * @return
	 */
	public Boolean isFilePopulated() {
	
		return fhxFile != null;
	}
	
	/**
	 * Populate the reports for a specific file.
	 * 
	 * @param inFile
	 */
	public void setFile(FHFile inFile) {
	
		log.debug("setFile called with file: \"" + inFile + "\"");
		
		if (inFile != null && !inFile.exists())
		{
			JOptionPane.showMessageDialog(App.mainFrame, "The file '" + inFile.getName() + "' does not exist.", "File not found",
					JOptionPane.ERROR_MESSAGE);
			
			fhxFile = null;
		}
		else
		{
			fhxFile = inFile;
		}
		
		populateViewerSummaryAndMapTabs();
		populateSingleFileReports();
		
	}
	
	/**
	 * Populate reports that work on all files in the workspace.
	 * 
	 * @param files
	 */
	public void setFiles(ArrayList<FHFile> files) {
	
		fhxFiles = new ArrayList<FHFile>();
		
		// Check the files passed still exist
		for (FHFile file : files)
		{
			if (file.exists())
			{
				fhxFiles.add(file);
			}
			else
			{
				JOptionPane.showMessageDialog(App.mainFrame, "The file '" + file.getName() + "' does not exist.", "File not found",
						JOptionPane.ERROR_MESSAGE);
			}
		}
		
		populateAnalysisReports();
	}
	
	/**
	 * Populate the file viewer, file sumamry and map tabs
	 */
	private void populateViewerSummaryAndMapTabs() {
	
		// **********
		// First Handle the FHX Viewer panel
		// **********
		
		log.debug("populateViewerSummaryAndMapTabs called");
		
		// Scrub viewer
		txtFHX.setText("");
		txtSummary.setText("");
		errorMessage.setText("");
		errorMessage.setVisible(false);
		
		populateFileReaderTab();
		populateSummaryTab();
		txtFHX.repaint();
		
		// Hide error panel by default
		errorMessage.setText("");
		errorMessage.setVisible(false);
		btnEditFile.setVisible(false);
		
		if (fhxFile != null)
		{
			// Set the error message if necessary
			if (fhxFile.getErrorMessage() != null)
			{
				errorMessage.setText(fhxFile.getErrorMessage());
				errorMessage.setVisible(true);
				
				if (!fhxFile.isValidFHXFile())
				{
					// Show edit file button
					btnEditFile.setVisible(true);
				}
				
				// Change focus to 'File Viewer' to highlight problem with file
				tabbedPane.setSelectedIndex(FILE_VIEWER_INDEX);
			}
			
			if (fhxFile.isValidFHXFile())
			{
				log.debug("Populating chart tab");
				panelChart.loadFile(fhxFile.getFireHistoryReader());
				
			}
			else
			{
				panelChart.clearChart();
			}
		}
		else
		{
			panelChart.clearChart();
		}
	}
	
	/**
	 * Some analyses are specific to single files. In this case they must be run when the user selected a different file. The analyses
	 * should only be run though if analyses have been run to ensure the user has specified what EventTypeToProcess
	 */
	private void populateSingleFileReports() {
	
		if (fhxFile != null && fhxFile.isValidFHXFile())
		{
			// Only run the descriptive stats if the other analyses are available
			// This is necessary because we need to make sure the user has specified what EventTypeToProcess
			if (panelResults.areAnalysesRunAndCurrent())
			{
				log.debug("Populating descriptive stats");
				panelResults.setSingleFileSummaryModel(FHDescriptiveStats.getSingleFileSummaryTableModel(fhxFile));
				panelResults.singleFileSummaryFile = FHDescriptiveStats.getSingleFileSummaryAsFile(fhxFile, null);
				panelResults.setSingleEventSummaryModel(FHDescriptiveStats.getEventSummaryTableModel(fhxFile,
						App.prefs.getEventTypePref(PrefKey.EVENT_TYPE_TO_PROCESS, EventTypeToProcess.FIRE_AND_INJURY_EVENT)));
				panelResults.singleEventSummaryFile = FHDescriptiveStats.getEventSummaryAsFile(fhxFile, null,
						App.prefs.getEventTypePref(PrefKey.EVENT_TYPE_TO_PROCESS, EventTypeToProcess.FIRE_AND_INJURY_EVENT));
			}
			
		}
	}
	
	/**
	 * TODO
	 */
	private void populateAnalysisReports() {
	
		if (fhxFiles == null || fhxFiles.size() == 0)
		{
			fhxFile = null;
			panelMap.setFHFiles(null);
			panelResults.clearResults();
			populateFileReaderTab();
			populateSummaryTab();
			return;
		}
		
		panelMap.setFHFiles(fhxFiles);
		
	}
	
	/**
	 * Populates the summary tab only if the file and report exist.
	 */
	private void populateSummaryTab() {
	
		log.debug("populateSummaryTab called");
		
		if (fhxFile == null || fhxFile.getReport() == null)
		{
			txtSummary.setText("");
			return;
		}
		
		txtSummary.setText(fhxFile.getReport());
		txtSummary.setCaretPosition(0);
	}
	
	/**
	 * Populates the file reader tab only if the file exists.
	 */
	private void populateFileReaderTab() {
	
		log.debug("populateFileReaderTab called");
		
		if (fhxFile == null)
		{
			txtFHX.setText("");
			txtFHX.getHighlighter().removeAllHighlights();
			return;
		}
		
		try
		{
			// Try to read file into FHX viewer tab
			FileReader reader = new FileReader(fhxFile);
			BufferedReader br = new BufferedReader(reader);
			if (fhxFile.length() < FIVE_MEGABYTE_LENGTH) // 5mb
			{
				txtFHX.read(br, txtFHX);
				br.close();
			}
			else
			{
				txtFHX.setText("File too large to display");
			}
			
			// If there are errors in the file try to highlight the offensive line
			txtFHX.getHighlighter().removeAllHighlights();
			if (fhxFile.getErrorLine() != null && fhxFile.getErrorLine() <= txtFHX.getLineCount())
			{
				DefaultHighlightPainter painter = new DefaultHighlighter.DefaultHighlightPainter(Color.YELLOW);
				try
				{
					int start = txtFHX.getLineStartOffset(fhxFile.getErrorLine() - 1);
					int end = txtFHX.getLineStartOffset(fhxFile.getErrorLine());
					log.debug("Line number offset to highlight: " + start);
					txtFHX.getHighlighter().addHighlight(start, end, painter);
					txtFHX.setCaretPosition(start);
				}
				catch (BadLocationException e)
				{
					log.error("Unable to move caret to position in file. BadLocationException");
					e.printStackTrace();
				}
			}
		}
		catch (Exception e2)
		{
			log.error("Problems setting up file viewer");
			e2.printStackTrace();
		}
		
		txtFHX.setCaretPosition(0);
	}
	
	/**
	 * Select all text within the currently focused text area.
	 */
	public void selectAll() {
	
		JComponent focusedComponent = getFocusedReportTab();
		
		if (focusedComponent != null)
		{
			log.debug("Focused component is: " + focusedComponent.getName());
			focusedComponent.requestFocusInWindow();
			
			if (focusedComponent instanceof JTextArea)
			{
				((JTextArea) focusedComponent).selectAll();
			}
			else if (focusedComponent instanceof JXTable)
			{
				((JXTable) focusedComponent).selectAll();
			}
			else
			{
				javax.swing.Action select = focusedComponent.getActionMap().get("selectAll");
				ActionEvent ae = new ActionEvent(focusedComponent, ActionEvent.ACTION_PERFORMED, "");
				
				try
				{
					select.actionPerformed(ae);
				}
				catch (NullPointerException ex)
				{
					log.warn("Unable to perform 'select' action on currently focussed component");
				}
			}
		}
	}
	
	/**
	 * Copy the currently focused text area to the clipboard.
	 */
	public void copyCurrentReportToClipboard() {
	
		JComponent focusedComponent = getFocusedReportTab();
		
		if (focusedComponent != null)
		{
			if (focusedComponent instanceof JXTable)
			{
				if (focusedComponent.equals(panelResults.table))
				{
					panelResults.adapter.doCopy();
				}
			}
			else if (focusedComponent instanceof JTextArea)
			{
				((JTextArea) focusedComponent).copy();
			}
		}
	}
	
	/**
	 * Get the currently focused text area.
	 * 
	 * @return
	 */
	private JComponent getFocusedReportTab() {
	
		int selectedIndex = tabbedPane.getSelectedIndex();
		
		if (selectedIndex == FILE_VIEWER_INDEX)
		{
			// Show the file viewer component
			return txtFHX;
		}
		else if (selectedIndex == FILE_SUMMARY_INDEX)
		{
			// Show the file summary component
			return txtSummary;
		}
		else if (selectedIndex == ANALYSIS_INDEX)
		{
			// Show the analysis results component
			return panelResults.table;
		}
		else if (selectedIndex == MAP_INDEX)
		{
			// Do not show the map
			return null;
		}
		else if (selectedIndex == CHART_INDEX)
		{
			// Do not show the chart
			return null;
		}
		
		try
		{
			return (JComponent) MainWindow.getInstance().getFrame().getFocusOwner();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		
		log.error("Failed to get currently focused component.");
		return null;
	}
	
	/**
	 * Sets the focus to the chart.
	 */
	protected void setFocusToChartTab() {
	
		tabbedPane.setSelectedIndex(CHART_INDEX);
	}
	
	/**
	 * TODO
	 * 
	 * @param calledProgrammatically
	 */
	public void showParamsDialog(Boolean calledProgrammatically) {
	
		App.prefs.setSilentMode(true);
		ParamConfigDialog dialog = new ParamConfigDialog(this);
		
		if (dialog.havePreferencesChanged())
		{
			App.prefs.setSilentMode(false);
			// App.prefs.firePrefChanged(PrefKey.EVENT_TYPE_TO_PROCESS);
			
			if (!calledProgrammatically)
			{
				populateAnalysisReports();
			}
			
			runAnalyses();
			panelResults.setupTable(true);
		}
		
		App.prefs.setSilentMode(false);
		MainWindow.getInstance().repaintFileList();
	}
	
	/**
	 * TODO
	 */
	public void runAnalyses() {
	
		if (fhxFiles == null || fhxFiles.size() == 0)
			return;
		
		// Show paramConfigDialog if necessary
		/*
		 * Boolean showParamsDialogIfRequired = true; if(showParamsDialogIfRequired &&
		 * !App.prefs.getBooleanPref(PrefKey.DONT_REQUEST_PARAM_CONFIRMATION, false)) { App.prefs.setSilentMode(true); ParamConfigDialog
		 * dialog = new ParamConfigDialog(this); App.prefs.setSilentMode(false);
		 * 
		 * parent.repaintFileList(); if(dialog.havePreferencesChanged()) { App.prefs.firePrefChanged(PrefKey.EVENT_TYPE_TO_PROCESS);
		 * panelResults.setupTable(true); } else { return; }
		 * 
		 * }
		 */
		
		AnalysisProgressDialog dialog = new AnalysisProgressDialog(tabbedPane, fhxFiles);
		panelResults.setSeasonalityModel(dialog.getSeasonalitySummaryModel());
		panelResults.setIntervalsSummaryModel(dialog.getIntervalsSummaryModel());
		panelResults.setIntervalsExceedenceModel(dialog.getIntervalsExceedenceModel());
		panelResults.setBin00Model(dialog.getBin00Model());
		panelResults.setBin01Model(dialog.getBin01Model());
		panelResults.setBin10Model(dialog.getBin10Model());
		panelResults.setBin11Model(dialog.getBin11Model());
		panelResults.setBinSumModel(dialog.getBinSumModel());
		panelResults.setSCOHModel(dialog.getSCOHModel());
		panelResults.setDSCOHModel(dialog.getDSCOHModel());
		panelResults.setSJACModel(dialog.getSJACModel());
		panelResults.setDSJACModel(dialog.getDSJACModel());
		panelResults.setTreeSummaryModel(dialog.getTreeModel());
		panelResults.setSiteSummaryModel(dialog.getSiteModel());
		panelResults.setNTPModel(dialog.getNTPModel());
		panelResults.setGeneralSummaryModel(dialog.getGeneralSummaryModel());
		
		panelResults.seasonalitySummaryFile = dialog.getSeasonalityFile();
		panelResults.intervalsSummaryFile = dialog.getIntervalsSummaryFile();
		panelResults.intervalsExceedenceFile = dialog.getIntervalsExceedenceFile();
		panelResults.bin00File = dialog.getBin00File();
		panelResults.bin01File = dialog.getBin01File();
		panelResults.bin10File = dialog.getBin10File();
		panelResults.bin11File = dialog.getBin11File();
		panelResults.binSumFile = dialog.getBinSumFile();
		panelResults.SCOHFile = dialog.getSCOHFile();
		panelResults.DSCOHFile = dialog.getDSCOHFile();
		panelResults.SJACFile = dialog.getSJACFile();
		panelResults.DSJACFile = dialog.getDSJACFile();
		panelResults.treeSummaryFile = dialog.getTreeSummaryFile();
		panelResults.siteSummaryFile = dialog.getSiteSummaryFile();
		panelResults.NTPFile = dialog.getNTPFile();
		panelResults.generalSummaryFile = dialog.getGeneralSummaryFile();
		
		panelResults.setFHMatrix(dialog.getFHMatrixClass());
		
		this.populateSingleFileReports();
		
		try
		{
			log.debug("NTPFile " + dialog.getNTPFile().getAbsoluteFile());
		}
		catch (Exception e)
		{
			log.error("failed to get NTP");
		}
		
		panelResults.setNTPModel(dialog.getNTPModel());
		panelResults.setSiteSummaryModel(dialog.getSiteModel());
		
		panelResults.setupTable(true);
		panelResults.repaintTree();
		dialog.dispose();
	}
	
	/**
	 * TODO
	 */
	@Override
	public void prefChanged(PrefsEvent e) {
	
		log.debug("Pref change picked up by ReportPanel");
		PrefKey key = e.getPref();
		
		String keystring = key.name();
		
		log.debug("Pref keystring : " + keystring);
		
		if (keystring.startsWith("SEASONALITY") || keystring.startsWith("MATRIX") || keystring.startsWith("INTERVAL")
				|| keystring.startsWith("RANGE") || keystring.startsWith("ANALYSIS_LABEL_TYPE"))
		{
			log.debug("ReportPanel redoing analyses as preferences have changed");
			// populateMultiFileReports(false);
		}
	}
	
	/**
	 * Show the popup menu.
	 * 
	 * @param component
	 * @param popup
	 */
	private static void addPopup(Component component, final JPopupMenu popup) {
	
		component.addMouseListener(new MouseAdapter() {
			
			@Override
			public void mousePressed(MouseEvent e) {
			
				if (e.isPopupTrigger())
				{
					showMenu(e);
				}
			}
			
			@Override
			public void mouseReleased(MouseEvent e) {
			
				if (e.isPopupTrigger())
				{
					showMenu(e);
				}
			}
			
			private void showMenu(MouseEvent e) {
			
				popup.show(e.getComponent(), e.getX(), e.getY());
			}
		});
	}
	
	/**
	 * Initialize the main GUI components.
	 */
	public void initGUI() {
	
		App.prefs.addPrefsListener(this);
		if (Platform.isOSX())
			setBackground(MainWindow.MAC_BACKGROUND_COLOR);
		
		setLayout(new BorderLayout(0, 0));
		JPanel panelRight = new JPanel();
		if (Platform.isOSX())
			panelRight.setBackground(MainWindow.MAC_BACKGROUND_COLOR);
		
		add(panelRight, BorderLayout.CENTER);
		panelRight.setLayout(new BorderLayout(0, 0));
		
		// Create tabbed pane for holding reports
		tabbedPane = new JTabbedPane(JTabbedPane.BOTTOM);
		if (Platform.isOSX())
			tabbedPane.setBackground(MainWindow.MAC_BACKGROUND_COLOR);
		panelRight.add(tabbedPane);
		
		// Create FHX viewer tab
		JPanel panelFHX = new JPanel();
		if (Platform.isOSX())
			panelFHX.setBackground(MainWindow.MAC_BACKGROUND_COLOR);
		
		panelFHX.setToolTipText("Original FHX file contents");
		tabbedPane.addTab("File Viewer  ", Builder.getImageIcon("fileviewer.png"), panelFHX, null);
		panelFHX.setLayout(new BorderLayout(0, 0));
		
		JPanel fhxButtonPanel = new JPanel();
		if (Platform.isOSX())
			fhxButtonPanel.setBackground(MainWindow.MAC_BACKGROUND_COLOR);
		
		fhxButtonPanel.setLayout(new BorderLayout());
		errorMessage = new JTextArea();
		errorMessage.setEditable(false);
		errorMessage.setLineWrap(true);
		errorMessage.setWrapStyleWord(true);
		errorMessage.setVisible(false);
		errorMessage.setForeground(Color.RED);
		
		btnEditFile = new JButton();
		btnEditFile.setVisible(false);
		btnEditFile.setAction(MainWindow.getInstance().actionEditFile);
		btnEditFile.setIcon(Builder.getImageIcon("bad.png"));
		btnEditFile.setText("Fix errors");
		
		fhxButtonPanel.add(btnEditFile, BorderLayout.EAST);
		fhxButtonPanel.add(errorMessage, BorderLayout.CENTER);
		panelFHX.add(fhxButtonPanel, BorderLayout.NORTH);
		
		JScrollPane scrollPaneFHX = new JScrollPane();
		if (Platform.isOSX())
			scrollPaneFHX.setBackground(MainWindow.MAC_BACKGROUND_COLOR);
		
		panelFHX.add(scrollPaneFHX, BorderLayout.CENTER);
		
		txtFHX = new JTextArea();
		
		txtFHX.setFont(new Font("Monospaced", Font.PLAIN, 12));
		scrollPaneFHX.setViewportView(txtFHX);
		TextLineNumber tln = new TextLineNumber(txtFHX);
		scrollPaneFHX.setRowHeaderView(tln);
		txtFHX.setEditable(false);
		
		// Create popup menu for file viewer
		JMenuItem mntmSelectAll5 = new JMenuItem(actionSelectAll);
		JMenuItem mntmCopy5 = new JMenuItem(actionCopy);
		JPopupMenu popupSummary5 = new JPopupMenu();
		addPopup(txtFHX, popupSummary5);
		JMenuItem mntmEditFile = new JMenuItem(MainWindow.getInstance().actionEditFile);
		popupSummary5.add(mntmEditFile);
		popupSummary5.add(mntmSelectAll5);
		popupSummary5.add(mntmCopy5);
		
		// Create Summary tab
		JPanel panelSummary = new JPanel();
		if (Platform.isOSX())
			panelSummary.setBackground(MainWindow.MAC_BACKGROUND_COLOR);
		
		tabbedPane.addTab("File summary  ", Builder.getImageIcon("info.png"), panelSummary, null);
		panelSummary.setLayout(new BorderLayout(0, 0));
		
		JPanel summaryButtonPanel = new JPanel();
		if (Platform.isOSX())
			summaryButtonPanel.setBackground(MainWindow.MAC_BACKGROUND_COLOR);
		panelSummary.add(summaryButtonPanel, BorderLayout.NORTH);
		
		JScrollPane scrollPaneSummary = new JScrollPane();
		if (Platform.isOSX())
			scrollPaneSummary.setBackground(MainWindow.MAC_BACKGROUND_COLOR);
		panelSummary.add(scrollPaneSummary, BorderLayout.CENTER);
		
		txtSummary = new JTextArea();
		txtSummary.setEditable(false);
		txtSummary.setText("REPORT/n \n FORMAT REPORT FOR FILE: uscdp001.FHX\n\tThis Report was created on: Sat May 18 12:48:23 MDT");
		txtSummary.setFont(new Font("Monospaced", Font.PLAIN, 12));
		scrollPaneSummary.setViewportView(txtSummary);
		
		// Create popup menu for summary
		JMenuItem mntmSelectAll = new JMenuItem(actionSelectAll);
		JMenuItem mntmCopy = new JMenuItem(actionCopy);
		JPopupMenu popupSummary = new JPopupMenu();
		addPopup(txtSummary, popupSummary);
		popupSummary.add(mntmSelectAll);
		popupSummary.add(mntmCopy);
		
		// Report Panel
		panelResults = new AnalysisResultsPanel();
		if (Platform.isOSX())
			panelResults.setBackground(MainWindow.MAC_BACKGROUND_COLOR);
		tabbedPane.addTab("Analysis ", Builder.getImageIcon("results.png"), panelResults, null);
		
		JPanel resultsButtonPanel = new JPanel();
		if (Platform.isOSX())
			resultsButtonPanel.setBackground(MainWindow.MAC_BACKGROUND_COLOR);
		panelResults.add(resultsButtonPanel, BorderLayout.NORTH);
		
		JButton btnConfig = new JButton(actionParamConfig);
		btnConfig.setToolTipText(actionParamConfig.getToolTipText());
		resultsButtonPanel.add(btnConfig);
		
		JButton btnHelp = new JButton(actionResultsHelp);
		btnHelp.setToolTipText(actionResultsHelp.getToolTipText());
		actionResultsHelp.setEnabled(false);
		resultsButtonPanel.add(btnHelp);
		
		// Create popup menu for results panel
		JMenuItem mntmSelectAll4 = new JMenuItem(actionSelectAll);
		JMenuItem mntmCopy4 = new JMenuItem(actionCopy);
		JPopupMenu popupResults = new JPopupMenu();
		addPopup(panelResults.table, popupResults);
		popupResults.add(mntmSelectAll4);
		popupResults.add(mntmCopy4);
		
		// Map Panel
		panelMap = new MapPanel();
		if (Platform.isOSX())
			panelMap.setBackground(MainWindow.MAC_BACKGROUND_COLOR);
		tabbedPane.addTab("Map  ", Builder.getImageIcon("map.png"), panelMap, null);
		
		// Chart panel
		panelChart = new NeoFHChart();
		MainWindow.chartActions = new ChartActions(panelChart);
		tabbedPane.addTab("Chart  ", Builder.getImageIcon("chart.png"), panelChart, null);
		
		// Popup menu for chart display
		JPopupMenu popupMenu = new JPopupMenu();
		addPopup(panelChart.svgCanvas, popupMenu);
		
		JCheckBoxMenuItem btnIndex = new JCheckBoxMenuItem(MainWindow.chartActions.actionShowIndexPlot);
		JCheckBoxMenuItem btnChronology = new JCheckBoxMenuItem(MainWindow.chartActions.actionShowChronologyPlot);
		JCheckBoxMenuItem btnComposite = new JCheckBoxMenuItem(MainWindow.chartActions.actionCompositePlot);
		JCheckBoxMenuItem btnShowLegend = new JCheckBoxMenuItem(MainWindow.chartActions.actionShowLegend);
		JMenuItem btnChartProperties = new JMenuItem(MainWindow.chartActions.actionShowChartProperties);
		JMenuItem btnZoomIn = new JMenuItem(MainWindow.chartActions.actionZoomIn);
		JMenuItem btnZoomOut = new JMenuItem(MainWindow.chartActions.actionZoomOut);
		JMenuItem btnZoomReset = new JMenuItem(MainWindow.chartActions.actionZoomReset);
		
		popupMenu.add(btnIndex);
		popupMenu.add(btnChronology);
		popupMenu.add(btnComposite);
		popupMenu.add(btnShowLegend);
		popupMenu.addSeparator();
		popupMenu.add(btnChartProperties);
		popupMenu.addSeparator();
		popupMenu.add(btnZoomIn);
		popupMenu.add(btnZoomOut);
		popupMenu.add(btnZoomReset);
	}
	
	/**
	 * Initialize the menu/toolbar actions.
	 */
	private void initActions() {
	
		/*
		 * SELECT ALL
		 */
		actionSelectAll = new FHAESAction("Select all", "selectall.png") {
			
			private static final long serialVersionUID = 1L;
			
			@Override
			public void actionPerformed(ActionEvent event) {
			
				selectAll();
			}
		};
		
		/*
		 * COPY
		 */
		actionCopy = new FHAESAction("Copy", "edit_copy.png") {
			
			private static final long serialVersionUID = 1L;
			
			@Override
			public void actionPerformed(ActionEvent event) {
			
				copyCurrentReportToClipboard();
			}
		};
		
		/*
		 * ANALYSIS OPTIONS
		 */
		actionParamConfig = new FHAESAction("Analysis options", "configure.png", "Configure") {
			
			private static final long serialVersionUID = 1L;
			
			@Override
			public void actionPerformed(ActionEvent event) {
			
				showParamsDialog(false);
			}
		};
		actionParamConfig.setToolTipText("View/edit analysis options");
		
		/*
		 * HELP
		 */
		actionResultsHelp = new FHAESAction("Help", "help.png") {
			
			private static final long serialVersionUID = 1L;
			
			@Override
			public void actionPerformed(ActionEvent event) {
			
				Platform.browseWebpage(RemoteHelp.HELP_ANALYSIS_RESULTS, null);
			}
		};
		actionResultsHelp.setToolTipText("Get help on the current analysis type");
	}
}
