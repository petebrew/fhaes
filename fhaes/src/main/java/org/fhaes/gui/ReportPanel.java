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

import org.fhaes.help.RemoteHelp;
import org.fhaes.model.FHFile;
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
	private static final Logger log = LoggerFactory.getLogger(ReportPanel.class);
	
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
	
	private ArrayList<FHFile> files;
	private FHFile file;
	
	/**
	 * Create the panel.
	 */
	public ReportPanel() {
		
		initActions();
		initGUI();
	}
	
	/**
	 * Initialize the main GUI components.
	 */
	public void initGUI() {
		
		App.prefs.addPrefsListener(this);
		if (Platform.isOSX())
			setBackground(MainWindow.macBGColor);
			
		setLayout(new BorderLayout(0, 0));
		JPanel panelRight = new JPanel();
		if (Platform.isOSX())
			panelRight.setBackground(MainWindow.macBGColor);
			
		add(panelRight, BorderLayout.CENTER);
		panelRight.setLayout(new BorderLayout(0, 0));
		
		// Create tabbed pane for holding reports
		tabbedPane = new JTabbedPane(JTabbedPane.BOTTOM);
		if (Platform.isOSX())
			tabbedPane.setBackground(MainWindow.macBGColor);
			
		panelRight.add(tabbedPane);
		
		// Create FHX viewer tab
		JPanel panelFHX = new JPanel();
		if (Platform.isOSX())
			panelFHX.setBackground(MainWindow.macBGColor);
			
		panelFHX.setToolTipText("Original FHX file contents");
		tabbedPane.addTab("File Viewer  ", Builder.getImageIcon("fileviewer.png"), panelFHX, null);
		panelFHX.setLayout(new BorderLayout(0, 0));
		
		JPanel fhxButtonPanel = new JPanel();
		if (Platform.isOSX())
			fhxButtonPanel.setBackground(MainWindow.macBGColor);
			
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
			scrollPaneFHX.setBackground(MainWindow.macBGColor);
			
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
			panelSummary.setBackground(MainWindow.macBGColor);
			
		tabbedPane.addTab("File summary  ", Builder.getImageIcon("info.png"), panelSummary, null);
		panelSummary.setLayout(new BorderLayout(0, 0));
		
		JPanel summaryButtonPanel = new JPanel();
		if (Platform.isOSX())
			summaryButtonPanel.setBackground(MainWindow.macBGColor);
		panelSummary.add(summaryButtonPanel, BorderLayout.NORTH);
		
		JScrollPane scrollPaneSummary = new JScrollPane();
		if (Platform.isOSX())
			scrollPaneSummary.setBackground(MainWindow.macBGColor);
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
			panelResults.setBackground(MainWindow.macBGColor);
		tabbedPane.addTab("Analysis ", Builder.getImageIcon("results.png"), panelResults, null);
		
		JPanel resultsButtonPanel = new JPanel();
		if (Platform.isOSX())
			resultsButtonPanel.setBackground(MainWindow.macBGColor);
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
			panelMap.setBackground(MainWindow.macBGColor);
		tabbedPane.addTab("Map  ", Builder.getImageIcon("map.png"), panelMap, null);
		
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
	 * Select all text within the currently focused text area.
	 */
	public void selectAll() {
		
		JComponent focusedComponent = getFocusedReportTab();
		
		log.debug("Focused component is: " + focusedComponent.getName());
		
		if (focusedComponent != null)
		{
			
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
	 * Get the currently focused text area.
	 * 
	 * @return
	 */
	public JComponent getFocusedReportTab() {
		
		int selectedIndex = tabbedPane.getSelectedIndex();
		
		if (selectedIndex == 2)
		{
			// Analysis results
			return panelResults.table;
			
		}
		else if (selectedIndex == 0)
		{
			// File viewer
			return txtFHX;
		}
		else if (selectedIndex == 1)
		{
			// File summary
			return txtSummary;
		}
		else if (selectedIndex == 3)
		{
			// Map
			return null;
			
		}
		
		try
		{
			return (JComponent) MainWindow.getInstance().frame.getFocusOwner();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		
		log.error("Failed to get currently focussed component");
		return null;
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
	 * Initialize the menu/toolbar actions.
	 */
	private void initActions() {
		
		actionSelectAll = new FHAESAction("Select all", "selectall.png") {
			
			private static final long serialVersionUID = 1L;
			
			@Override
			public void actionPerformed(ActionEvent event) {
				
				selectAll();
			}
		};
		
		actionCopy = new FHAESAction("Copy", "edit_copy.png") {
			
			private static final long serialVersionUID = 1L;
			
			@Override
			public void actionPerformed(ActionEvent event) {
				
				copyCurrentReportToClipboard();
			}
		};
		
		actionParamConfig = new FHAESAction("Analysis options", "configure.png", "Configure") {
			
			private static final long serialVersionUID = 1L;
			
			@Override
			public void actionPerformed(ActionEvent event) {
				
				showParamsDialog(false);
			}
		};
		actionParamConfig.setToolTipText("View/edit analysis options");
		
		actionResultsHelp = new FHAESAction("Help", "help.png") {
			
			private static final long serialVersionUID = 1L;
			
			@Override
			public void actionPerformed(ActionEvent event) {
				
				Platform.browseWebpage(RemoteHelp.HELP_ANALYSIS_RESULTS, null);
			}
		};
		actionResultsHelp.setToolTipText("Get help on the current analysis type");
		
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
				populateMultiFileReports();
				
			runAnalyses();
			panelResults.setupTable(true);
			
		}
		App.prefs.setSilentMode(false);
		
		MainWindow.getInstance().repaintFileList();
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
	 * Populate reports that work on all files in the workspace.
	 * 
	 * @param files
	 */
	public void setFiles(ArrayList<FHFile> files) {
		
		// Check the files passed still exist
		
		this.files = new ArrayList<FHFile>();
		
		for (FHFile file : files)
		{
			if (file.exists())
			{
				this.files.add(file);
			}
			else
			{
				JOptionPane.showMessageDialog(App.mainFrame, "The file '" + file.getName() + "' does not exist.", "File not found",
						JOptionPane.ERROR_MESSAGE);
			}
		}
		
		this.populateMultiFileReports();
	}
	
	/**
	 * TODO
	 */
	public void runAnalyses() {
		
		if (files == null || files.size() == 0)
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
		
		AnalysisProgressDialog dialog = new AnalysisProgressDialog(tabbedPane, files);
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
	 * Populate the reports for a specific file.
	 * 
	 * @param file
	 */
	public void setFile(FHFile inFile) {
		
		log.debug("setFile called with file: " + inFile);
		this.file = inFile;
		
		if (inFile != null && !inFile.exists())
		{
			JOptionPane.showMessageDialog(App.mainFrame, "The file '" + inFile.getName() + "' does not exist.", "File not found",
					JOptionPane.ERROR_MESSAGE);
			this.file = null;
			
		}
		
		populateSingleFileReports();
		
	}
	
	/**
	 * TODO
	 * 
	 * @return
	 */
	public Boolean isFilePopulated() {
		
		return file != null;
	}
	
	/**
	 * Populate the reports that take a single file as input.
	 */
	private void populateSingleFileReports() {
		
		// **********
		// First Handle the FHX Viewer panel
		// **********
		
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
		this.btnEditFile.setVisible(false);
		
		if (file != null)
		{
			// Set the error message if necessary
			if (file.getErrorMessage() != null)
			{
				errorMessage.setText(file.getErrorMessage());
				errorMessage.setVisible(true);
				
				if (!file.isValidFHXFile())
				{
					// Show edit file button
					this.btnEditFile.setVisible(true);
				}
				
				// Change focus to 'File Viewer' to highlight problem with file
				tabbedPane.setSelectedIndex(0);
			}
			
			if (file.isValidFHXFile())
			{
				panelChart.loadFile(file.getFireHistoryReader());
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
	 * TODO
	 */
	private void populateMultiFileReports() {
		
		if (files == null || files.size() == 0)
		{
			file = null;
			panelMap.setFHFiles(null);
			panelResults.clearResults();
			populateFileReaderTab();
			populateSummaryTab();
			return;
		}
		
		panelMap.setFHFiles(files);
	}
	
	/**
	 * TODO
	 */
	private void populateSummaryTab() {
		
		if (file == null || file.getReport() == null)
		{
			txtSummary.setText("");
			return;
		}
		
		txtSummary.setText(file.getReport());
		txtSummary.setCaretPosition(0);
	}
	
	/**
	 * TODO
	 */
	private void populateFileReaderTab() {
		
		if (file == null)
		{
			txtFHX.setText("");
			txtFHX.getHighlighter().removeAllHighlights();
			return;
		}
		
		try
		{
			// Try to read file into FHX viewer tab
			FileReader reader = new FileReader(file);
			BufferedReader br = new BufferedReader(reader);
			if (file.length() < 5242880) // 5mb
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
			if (file.getErrorLine() != null && file.getErrorLine() <= txtFHX.getLineCount())
			{
				DefaultHighlightPainter painter = new DefaultHighlighter.DefaultHighlightPainter(Color.YELLOW);
				try
				{
					int start = txtFHX.getLineStartOffset(file.getErrorLine() - 1);
					int end = txtFHX.getLineStartOffset(file.getErrorLine());
					log.debug("Line number offset to highlight: " + start);
					txtFHX.getHighlighter().addHighlight(start, end, painter);
					txtFHX.setCaretPosition(start);
				}
				catch (BadLocationException e)
				{
					log.error("Unable to move caret to position in file.  BadLocationException");
					e.printStackTrace();
				}
			}
		}
		catch (Exception e2)
		{
			log.error("Problems setting up file viewer");
			e2.printStackTrace();
		}
		
		this.txtFHX.setCaretPosition(0);
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
}
