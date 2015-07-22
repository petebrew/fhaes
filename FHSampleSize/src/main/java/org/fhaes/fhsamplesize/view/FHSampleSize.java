/*******************************************************************************
 * Copyright (C) 2014 Peter Brewer and Joshua Brogan
 * 
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 * 
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 * 
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *     
 *     Contributors:
 *     		Peter Brewer
 *     		Joshua Brogan
 ******************************************************************************/
package org.fhaes.fhsamplesize.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Rectangle;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.JViewport;
import javax.swing.ScrollPaneConstants;
import javax.swing.Scrollable;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingWorker;
import javax.swing.border.TitledBorder;

import net.miginfocom.swing.MigLayout;

import org.codehaus.plexus.util.FileUtils;
import org.fhaes.components.JToolBarButton;
import org.fhaes.enums.EventTypeToProcess;
import org.fhaes.enums.FireFilterType;
import org.fhaes.enums.MiddleMetric;
import org.fhaes.enums.ResamplingType;
import org.fhaes.fhfilereader.FHX2FileReader;
import org.fhaes.fhsamplesize.controller.SSIZController;
import org.fhaes.fhsamplesize.model.AnalysisResultsModel;
import org.fhaes.fhsamplesize.model.SSIZAnalysisModel;
import org.fhaes.filefilter.CSVFileFilter;
import org.fhaes.filefilter.FHXFileFilter;
import org.fhaes.filefilter.PDFFilter;
import org.fhaes.filefilter.TABFilter;
import org.fhaes.preferences.App;
import org.fhaes.preferences.FHAESPreferences.PrefKey;
import org.fhaes.preferences.wrappers.FireFilterTypeWrapper;
import org.fhaes.preferences.wrappers.MatrixEventTypeWrapper;
import org.fhaes.preferences.wrappers.ResamplingTypeWrapper;
import org.fhaes.preferences.wrappers.SpinnerWrapper;
import org.fhaes.segmentation.SegmentModel;
import org.fhaes.segmentation.SegmentationPanel;
import org.fhaes.util.Builder;
import org.fhaes.util.FHAESAction;
import org.fhaes.util.JTableSpreadsheetByRowAdapter;
import org.jfree.chart.editor.ChartEditor;
import org.jfree.chart.editor.ChartEditorManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * FHSampleSize Class.
 * 
 * This is the GUI class for running the FHSampleSize analysis. This is a complete rewrite of the original SSIZ application, extended to
 * perform additional analyses described by Don Falk in his PhD thesis.
 * 
 * @author Joshua Brogan and Peter Brewer
 */
public class FHSampleSize extends JFrame implements ActionListener {

	private static final long serialVersionUID = 1L;
	private static final Logger log = LoggerFactory.getLogger(FHSampleSize.class);

	private static final int MAX_DRAW_HEIGHT = 1080;
	private static final int MAX_DRAW_WIDTH = 1920;

	protected JTableSpreadsheetByRowAdapter adapter;

	private JScrollPane scrollPaneAsymptote;
	private JScrollPane scrollPaneSimulations;

	private JCheckBox chkCommonYears;
	private JCheckBox chkExcludeSeriesWithNoEvents;
	private JSpinner spnSeed;
	private JSpinner spnSimulations;
	private JSpinner spnThresholdValue;
	private JSplitPane splitPaneResults;
	private JPanel panelChart;
	private JTextField txtInputFile;
	@SuppressWarnings("rawtypes")
	private JComboBox cboEventType;
	@SuppressWarnings("rawtypes")
	private JComboBox cboResampling;
	@SuppressWarnings("rawtypes")
	private JComboBox cboThresholdType;
	@SuppressWarnings("rawtypes")
	private JComboBox cboChartMetric;
	@SuppressWarnings("rawtypes")
	private JComboBox cboSegment;

	private JProgressBar progressBar;
	private JPanel panelProgressBar;
	private DrawSSIZAnalysisTask task;

	private JButton btnCancelAnalysis;
	private Boolean taskWasCancelled;

	private SSIZCurveChart curveChart;
	private SSIZResultsTable simulationsTable;
	private AsymptoteTable asymptoteTable;
	private SegmentationPanel segmentationPanel;
	private int segmentsDone = 0;

	private Boolean mouseListenersActive;

	private FHAESAction actionRun;
	private FHAESAction actionBrowse;
	private FHAESAction actionSaveAll;
	private FHAESAction actionExportPDF;
	private FHAESAction actionExportPNG;
	private FHAESAction actionClose;

	private FHX2FileReader reader;
	private Boolean fileDialogWasUsed;

	/**
	 * Launch as stand-alone application.
	 */
	public static void main(String[] args) {

		EventQueue.invokeLater(new Runnable() {

			public void run() {

				try
				{
					FHSampleSize window = new FHSampleSize(null);
					window.setVisible(true);
				}
				catch (Exception ex)
				{
					ex.printStackTrace();
				}
			}
		});
	}

	/**
	 * Standard constructor for the window.
	 * 
	 * @param parent
	 */
	public FHSampleSize(Window parent) {

		initActions();
		initGUI();
		initMenu();
		setLocationRelativeTo(parent);
	}

	/**
	 * Set up the Menu bar using actions wherever possible.
	 */
	private void initMenu() {

		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);

		JMenu mnFile = new JMenu("File");
		menuBar.add(mnFile);

		JMenuItem mntmOpen = new JMenuItem(this.actionBrowse);
		mntmOpen.setText("Open...");
		mnFile.add(mntmOpen);

		JMenuItem mntmSave = new JMenuItem(this.actionSaveAll);
		mnFile.add(mntmSave);

		mnFile.addSeparator();

		JMenuItem mntmExit = new JMenuItem(this.actionClose);
		mnFile.add(mntmExit);
	}

	/**
	 * Initialize shared actions.
	 */
	private void initActions() {

		final FHSampleSize glue = this;

		actionRun = new FHAESAction("Run Analysis", "run.png") {

			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent event) {

				try
				{
					// Run the analysis task normally if the file dialog was used to load a file
					if (fileDialogWasUsed && reader != null)
						runSSIZAnalysisTask();

					// Otherwise get file manually from the path in the input box and attempt analysis from there
					else
					{
						String filePath = txtInputFile.getText();
						File theFHX2File = new File(filePath);
						reader = new FHX2FileReader(theFHX2File);
						setGUIForFHFileReader();

						if (reader != null)
							runSSIZAnalysisTask();
					}
				}
				catch (Exception ex)
				{
					JOptionPane.showMessageDialog(glue,
							"Could not open the selected file. Try opening directly in FHAES for a detailed error message.");
					ex.printStackTrace();

					reader = null;
					actionRun.setEnabled(false);
					actionSaveAll.setEnabled(false);
					actionExportPDF.setEnabled(false);
					actionExportPNG.setEnabled(false);
				}
			}
		};
		actionRun.setEnabled(false);

		actionBrowse = new FHAESAction("Browse", "fileopen.png") {

			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent event) {

				File theFHX2File = loadFromOpenFileDialog();
				openFile(theFHX2File);
			}
		};
		actionBrowse.setEnabled(true);

		actionSaveAll = new FHAESAction("Save all", "save_all.png") {

			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent event) {

				saveAll();
			}
		};
		actionSaveAll.setEnabled(false);

		actionExportPDF = new FHAESAction("Export chart to PDF", "pdf.png") {

			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent event) {

				try
				{
					File fileToSave = getFileFromSaveDialog("PDF");
					if (fileToSave != null)
						SSIZCurveChart.writeAsPDF(fileToSave, curveChart.getWidth(), curveChart.getHeight());
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
			}
		};
		actionExportPDF.setEnabled(false);

		actionExportPNG = new FHAESAction("Export chart to PNG", "formatpng.png") {

			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent event) {

				try
				{
					curveChart.doSaveAs();
				}
				catch (IOException e)
				{
					e.printStackTrace();
				}
			}
		};
		actionExportPNG.setEnabled(false);

		actionClose = new FHAESAction("Close", "close.png") {

			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent event) {

				dispose();
			}
		};
	}

	/**
	 * Open the specified file
	 * 
	 * @param theFHX2File
	 */
	public void openFile(File theFHX2File) {

		try
		{
			if (theFHX2File != null)
			{
				reader = new FHX2FileReader(theFHX2File);
				setGUIForFHFileReader();

				segmentationPanel.chkSegmentation.setEnabled(true);
				fileDialogWasUsed = true;
			}
		}
		catch (Exception ex)
		{
			JOptionPane.showMessageDialog(this,
					"Could not open the selected file. Try opening directly in FHAES for a detailed error message.");
			ex.printStackTrace();

			reader = null;
			actionRun.setEnabled(false);
			actionSaveAll.setEnabled(false);
			actionExportPDF.setEnabled(false);
			actionExportPNG.setEnabled(false);
		}
	}

	/**
	 * Initialize GUI components.
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void initGUI() {

		App.init();

		// setBounds(100, 100, 972, 439);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setIconImage(Builder.getApplicationIcon());
		setTitle("Sample Size Analysis");
		getContentPane().setLayout(new MigLayout("", "[1136px,grow,fill]", "[30px][405px,grow]"));

		JToolBar toolBar = new JToolBar();
		toolBar.setFloatable(false);
		getContentPane().add(toolBar, "cell 0 0,growx,aligny top");

		JToolBarButton btnOpen = new JToolBarButton(actionBrowse);
		btnOpen.setIcon(Builder.getImageIcon("fileopen.png"));
		toolBar.add(btnOpen);

		JToolBarButton btnSave = new JToolBarButton(actionSaveAll);
		btnSave.setIcon(Builder.getImageIcon("save_all.png"));
		toolBar.add(btnSave);

		JToolBarButton btnRun = new JToolBarButton(actionRun);
		btnRun.setIcon(Builder.getImageIcon("run.png"));
		toolBar.add(btnRun);

		JToolBarButton btnExportPDF = new JToolBarButton(actionExportPDF);
		btnExportPDF.setIcon(Builder.getImageIcon("pdf.png"));
		toolBar.add(btnExportPDF);

		JToolBarButton btnExportPNG = new JToolBarButton(actionExportPNG);
		btnExportPNG.setIcon(Builder.getImageIcon("formatpng.png"));
		toolBar.add(btnExportPNG);

		JPanel panelMain = new JPanel();
		getContentPane().add(panelMain, "cell 0 1,grow");
		panelMain.setLayout(new BorderLayout(0, 0));

		JSplitPane splitPaneMain = new JSplitPane();
		splitPaneMain.setOneTouchExpandable(true);
		panelMain.add(splitPaneMain);

		JPanel panelParameters = new JPanel();
		splitPaneMain.setLeftComponent(panelParameters);
		panelParameters.setLayout(new MigLayout("", "[grow,right]", "[][][][193.00,grow,fill][]"));

		JPanel panelInput = new JPanel();
		panelInput.setBorder(new TitledBorder(null, "Input", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panelParameters.add(panelInput, "cell 0 0,grow");
		panelInput.setLayout(new MigLayout("", "[100px:100px:180px,right][grow,fill][]", "[]"));

		JLabel lblInputFile = new JLabel("Input file:");
		panelInput.add(lblInputFile, "cell 0 0");

		txtInputFile = new JTextField();
		panelInput.add(txtInputFile, "cell 1 0,growx");
		txtInputFile.setActionCommand("NewFileTyped");
		txtInputFile.addActionListener(this);
		txtInputFile.setColumns(10);

		JButton btnBrowse = new JButton("");
		panelInput.add(btnBrowse, "cell 2 0");
		btnBrowse.setAction(actionBrowse);
		btnBrowse.setText("");
		btnBrowse.setIcon(Builder.getImageIcon("fileopen16.png"));
		btnBrowse.setPreferredSize(new Dimension(25, 25));
		btnBrowse.setMaximumSize(new Dimension(25, 25));
		btnBrowse.putClientProperty("JButton.buttonType", "segmentedTextured");
		btnBrowse.putClientProperty("JButton.segmentPosition", "middle");

		JPanel panelAnalysisOptions = new JPanel();
		panelAnalysisOptions.setBorder(new TitledBorder(null, "Analysis and filtering options", TitledBorder.LEADING, TitledBorder.TOP,
				null, null));
		panelParameters.add(panelAnalysisOptions, "cell 0 1,grow");
		panelAnalysisOptions.setLayout(new MigLayout("", "[100px:100px:180px,right][][][]", "[][][][]"));

		JLabel lblEventTypes = new JLabel("Event type:");
		panelAnalysisOptions.add(lblEventTypes, "cell 0 0");

		cboEventType = new JComboBox();
		panelAnalysisOptions.add(cboEventType, "cell 1 0 3 1");
		cboEventType.setModel(new DefaultComboBoxModel(EventTypeToProcess.values()));
		new MatrixEventTypeWrapper(cboEventType, PrefKey.EVENT_TYPE_TO_PROCESS, EventTypeToProcess.FIRE_EVENT);

		chkCommonYears = new JCheckBox("<html>Only analyze years all series have in common");
		chkCommonYears.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent arg0) {

				if (chkCommonYears.isSelected())
					App.prefs.setPref(PrefKey.SSIZ_CHK_COMMON_YEARS, "TRUE");
				else
					App.prefs.setPref(PrefKey.SSIZ_CHK_COMMON_YEARS, "FALSE");
			}
		});
		panelAnalysisOptions.add(chkCommonYears, "cell 1 1 3 1");

		chkExcludeSeriesWithNoEvents = new JCheckBox("<html>Exclude series/segments with no events");
		chkExcludeSeriesWithNoEvents.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent arg0) {

				if (chkExcludeSeriesWithNoEvents.isSelected())
					App.prefs.setPref(PrefKey.SSIZ_CHK_EXCLUDE_SERIES_WITH_NO_EVENTS, "TRUE");
				else
					App.prefs.setPref(PrefKey.SSIZ_CHK_EXCLUDE_SERIES_WITH_NO_EVENTS, "FALSE");
			}
		});
		panelAnalysisOptions.add(chkExcludeSeriesWithNoEvents, "cell 1 2 3 1");

		JLabel lblThresholdType = new JLabel("Threshold:");
		panelAnalysisOptions.add(lblThresholdType, "cell 0 3");

		cboThresholdType = new JComboBox();
		panelAnalysisOptions.add(cboThresholdType, "cell 1 3");
		cboThresholdType.setModel(new DefaultComboBoxModel(new String[] { "Number of fires", "Percentage of fires" }));
		new FireFilterTypeWrapper(cboThresholdType, PrefKey.COMPOSITE_FILTER_TYPE, FireFilterType.NUMBER_OF_EVENTS);

		JLabel label = new JLabel(">=");
		panelAnalysisOptions.add(label, "cell 2 3");

		spnThresholdValue = new JSpinner();
		panelAnalysisOptions.add(spnThresholdValue, "cell 3 3");
		spnThresholdValue.setModel(new SpinnerNumberModel(1, 1, 999, 1));
		new SpinnerWrapper(spnThresholdValue, PrefKey.COMPOSITE_FILTER_VALUE, 1);

		JPanel panelSimulations = new JPanel();
		panelSimulations.setBorder(new TitledBorder(null, "Simulations", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panelParameters.add(panelSimulations, "cell 0 2,grow");
		panelSimulations.setLayout(new MigLayout("", "[100px:100px:180px,right][fill]", "[][][]"));

		JLabel lblSimulations = new JLabel("Simulations:");
		panelSimulations.add(lblSimulations, "cell 0 0");

		spnSimulations = new JSpinner();
		panelSimulations.add(spnSimulations, "cell 1 0");
		spnSimulations.setModel(new SpinnerNumberModel(new Integer(1000), new Integer(1), null, new Integer(1)));
		new SpinnerWrapper(spnSimulations, PrefKey.SSIZ_SIMULATION_COUNT, 1000);

		JLabel lblSeedNumber = new JLabel("Seed number:");
		panelSimulations.add(lblSeedNumber, "cell 0 1");

		spnSeed = new JSpinner();
		panelSimulations.add(spnSeed, "cell 1 1");
		spnSeed.setModel(new SpinnerNumberModel(new Integer(30188), null, null, new Integer(1)));
		new SpinnerWrapper(spnSeed, PrefKey.SSIZ_SEED_NUMBER, 30188);

		JLabel lblResampling = new JLabel("Resampling:");
		panelSimulations.add(lblResampling, "cell 0 2");

		cboResampling = new JComboBox();
		panelSimulations.add(cboResampling, "cell 1 2");
		cboResampling.setModel(new DefaultComboBoxModel(new String[] { "With replacement", "Without replacement" }));
		new ResamplingTypeWrapper(cboResampling, PrefKey.SSIZ_RESAMPLING_TYPE, ResamplingType.WITH_REPLACEMENT);

		segmentationPanel = new SegmentationPanel();
		segmentationPanel.chkSegmentation.setText("Process subset or segments of dataset?");
		segmentationPanel.chkSegmentation.setEnabled(false);
		panelParameters.add(segmentationPanel, "cell 0 3,growx");

		JPanel panel_3 = new JPanel();
		panelParameters.add(panel_3, "cell 0 4,grow");
		panel_3.setLayout(new MigLayout("", "[left][grow][right]", "[]"));

		JButton btnReset = new JButton("Reset");
		btnReset.setActionCommand("Reset");
		btnReset.addActionListener(this);
		panel_3.add(btnReset, "cell 0 0,grow");

		JButton btnRunAnalysis = new JButton("Run Analysis");
		btnRunAnalysis.setAction(actionRun);
		panel_3.add(btnRunAnalysis, "cell 2 0,grow");

		JPanel panelResults = new JPanel();
		splitPaneMain.setRightComponent(panelResults);
		panelResults.setLayout(new BorderLayout(0, 0));

		splitPaneResults = new JSplitPane();
		splitPaneResults.setResizeWeight(0.5);
		splitPaneResults.setOneTouchExpandable(true);
		splitPaneResults.setDividerLocation(0.5d);
		panelResults.add(splitPaneResults, BorderLayout.CENTER);
		splitPaneResults.setOrientation(JSplitPane.VERTICAL_SPLIT);

		JPanel panelResultsTop = new JPanel();
		splitPaneResults.setLeftComponent(panelResultsTop);
		panelResultsTop.setLayout(new BorderLayout(0, 0));

		JPanel panelChartOptions = new JPanel();
		panelChartOptions.setBackground(Color.WHITE);
		panelResultsTop.add(panelChartOptions, BorderLayout.SOUTH);
		panelChartOptions.setLayout(new MigLayout("", "[][][][][][grow][grow]", "[15px,center]"));

		JLabel lblNewLabel = new JLabel("Plot:");
		panelChartOptions.add(lblNewLabel, "cell 0 0,alignx trailing,aligny center");

		cboChartMetric = new JComboBox();
		cboChartMetric.setEnabled(false);
		cboChartMetric.setModel(new DefaultComboBoxModel(MiddleMetric.values()));
		panelChartOptions.add(cboChartMetric, "cell 1 0,growx");
		cboChartMetric.setBackground(Color.WHITE);

		JLabel lblOfSegment = new JLabel("of segment:");
		panelChartOptions.add(lblOfSegment, "cell 2 0,alignx trailing");

		cboSegment = new JComboBox();
		cboSegment.setBackground(Color.WHITE);
		cboSegment.setActionCommand("UpdateChart");
		cboSegment.addActionListener(this);

		panelChartOptions.add(cboSegment, "cell 3 0,growx");
		cboChartMetric.setActionCommand("UpdateChart");

		JLabel lblWithAsymptoteType = new JLabel("with asymptote type:");
		panelChartOptions.add(lblWithAsymptoteType, "cell 4 0,alignx trailing");

		JComboBox comboBox = new JComboBox();
		comboBox.setEnabled(false);
		comboBox.setModel(new DefaultComboBoxModel(new String[] { "none", "Weibull", "Michaelis-Menten", "Modified Michaelis-Menten",
				"Logistic", "Modified exponential" }));
		comboBox.setBackground(Color.WHITE);
		panelChartOptions.add(comboBox, "cell 5 0,growx");
		cboChartMetric.addActionListener(this);

		panelChart = new JPanel();
		panelChart.setMinimumSize(new Dimension(200, 200));
		panelResultsTop.add(panelChart, BorderLayout.CENTER);
		panelChart.setLayout(new BorderLayout(0, 0));
		panelChart.setBackground(Color.WHITE);

		JTabbedPane panelResultsBottom = new JTabbedPane(JTabbedPane.BOTTOM);
		splitPaneResults.setRightComponent(panelResultsBottom);

		simulationsTable = new SSIZResultsTable();
		simulationsTable.setEnabled(false);
		simulationsTable.addMouseListener(new TablePopClickListener());
		simulationsTable.setVisibleRowCount(10);

		adapter = new JTableSpreadsheetByRowAdapter(simulationsTable);

		scrollPaneSimulations = new JScrollPane();
		panelResultsBottom.addTab("Simulations", null, scrollPaneSimulations, null);
		scrollPaneSimulations.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scrollPaneSimulations.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		scrollPaneSimulations.setViewportView(simulationsTable);

		JPanel panelAsymptote = new JPanel();

		asymptoteTable = new AsymptoteTable();
		asymptoteTable.setEnabled(false);
		// asymptoteTable.addMouseListener(new TablePopClickListener());
		asymptoteTable.setVisibleRowCount(10);

		scrollPaneAsymptote = new JScrollPane();

		scrollPaneAsymptote.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scrollPaneAsymptote.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		scrollPaneAsymptote.setViewportView(asymptoteTable);
		panelAsymptote.setLayout(new BorderLayout());
		panelAsymptote.add(scrollPaneAsymptote, BorderLayout.CENTER);

		panelResultsBottom.addTab("Asymptote", null, panelAsymptote, null);

		panelProgressBar = new JPanel();
		panelProgressBar.setLayout(new BorderLayout());

		btnCancelAnalysis = new JButton("Cancel");
		btnCancelAnalysis.setIcon(Builder.getImageIcon("delete.png"));
		btnCancelAnalysis.setVisible(false);
		btnCancelAnalysis.setActionCommand("CancelAnalysis");
		btnCancelAnalysis.addActionListener(this);

		progressBar = new JProgressBar();
		panelProgressBar.add(progressBar, BorderLayout.CENTER);
		panelProgressBar.add(btnCancelAnalysis, BorderLayout.EAST);
		progressBar.setStringPainted(true);

		fileDialogWasUsed = false;
		mouseListenersActive = false;

		this.setCheckBoxesToPrefKeyValues();
		this.setGUIForFHFileReader();

		pack();
		this.setExtendedState(this.getExtendedState() | JFrame.MAXIMIZED_BOTH);
		setVisible(true);
	}

	/**
	 * TODO
	 */
	@Override
	public void actionPerformed(ActionEvent evt) {

		if (evt.getActionCommand().equals("Reset"))
		{
			// Reset the GUI for a new run
			int response = JOptionPane.showConfirmDialog(this, "Are you sure you want to start a new analysis?");
			if (response != JOptionPane.YES_OPTION)
				return;

			this.txtInputFile.setText(null);
			this.cboEventType.setSelectedIndex(0);
			this.spnSimulations.setValue(1000);
			this.spnSeed.setValue(30188);
			this.cboResampling.setSelectedIndex(0);
			this.cboThresholdType.setSelectedIndex(0);
			this.spnThresholdValue.setValue(1);
			segmentationPanel.chkSegmentation.setSelected(false);
			segmentationPanel.table.tableModel.clearSegments();
			this.panelChart.removeAll();
			this.panelChart.repaint();
			this.simulationsTable.removeAllRows();
			this.cboChartMetric.setEnabled(false);
			this.cboSegment.setEnabled(false);
		}
		else if (evt.getActionCommand().equals("NewFileTyped"))
		{
			// A new file name was typed
			try
			{
				if (filePathHasValidFile(txtInputFile.getText()))
				{
					actionRun.setEnabled(true);
					actionSaveAll.setEnabled(true);
					segmentationPanel.chkSegmentation.setEnabled(true);
				}
				else
				{
					actionRun.setEnabled(false);
					actionSaveAll.setEnabled(false);
					segmentationPanel.chkSegmentation.setEnabled(false);
				}
			}
			catch (Exception ex)
			{
				actionRun.setEnabled(false);
				actionSaveAll.setEnabled(false);
				segmentationPanel.chkSegmentation.setEnabled(false);
			}
		}
		else if (evt.getActionCommand().equals("UpdateChart"))
		{
			updateChart();
		}
		else if (evt.getActionCommand().equals("CancelAnalysis"))
		{
			// Cancel the analysis that is currently running
			taskWasCancelled = true;
			task.cancel(true);
		}
	}

	/**
	 * Show open file dialog so the user may choose a file to edit.
	 * 
	 * @return the chosen file if okay was pressed, null if cancel was pressed
	 */
	private File loadFromOpenFileDialog() {

		String lastVisitedFolder = App.prefs.getPref(PrefKey.PREF_LAST_READ_FOLDER, null);
		JFileChooser fc;

		if (lastVisitedFolder != null)
			fc = new JFileChooser(lastVisitedFolder);
		else
			fc = new JFileChooser();

		fc.setDialogTitle("Select a FHX2 file for sample size analysis");
		fc.setFileFilter(new FHXFileFilter());
		fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
		fc.setMultiSelectionEnabled(true);

		int returnVal = fc.showOpenDialog(this);
		if (returnVal == JFileChooser.APPROVE_OPTION)
		{
			App.prefs.setPref(PrefKey.PREF_LAST_READ_FOLDER, fc.getSelectedFile().getPath());
			return fc.getSelectedFile();
		}
		return null;
	}

	/**
	 * Open a JFileChooser and return the file that the user specified for saving. Takes a parameter that specifies the type of file. Either
	 * TAB or PNG.
	 * 
	 * @return
	 */
	private File getFileFromSaveDialog(String fileTypeToSave) {

		String lastVisitedFolder = App.prefs.getPref(PrefKey.PREF_LAST_EXPORT_FOLDER, null);
		JFileChooser fc = new JFileChooser(lastVisitedFolder);
		File outputFile;

		if (fileTypeToSave == "TAB")
		{
			TABFilter filterTAB = new TABFilter();
			fc.addChoosableFileFilter(filterTAB);
			fc.setFileFilter(filterTAB);
			fc.setDialogTitle("Export table as text file...");

		}
		else if (fileTypeToSave == "PDF")
		{
			PDFFilter filterPDF = new PDFFilter();
			fc.addChoosableFileFilter(filterPDF);
			fc.setFileFilter(filterPDF);
			fc.setDialogTitle("Export chart as PDF...");
		}

		fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
		fc.setMultiSelectionEnabled(false);

		int returnVal = fc.showSaveDialog(this);
		if (returnVal == JFileChooser.APPROVE_OPTION)
		{
			outputFile = fc.getSelectedFile();

			if (FileUtils.getExtension(outputFile.getAbsolutePath()) == "")
			{
				log.debug("Output file extension not set by user");

				if (fc.getFileFilter().getDescription().equals(new CSVFileFilter().getDescription()))
				{
					log.debug("Adding csv extension to output file name");
					outputFile = new File(outputFile.getAbsolutePath() + ".csv");
				}
				else if (fc.getFileFilter().getDescription().equals(new PDFFilter().getDescription()))
				{
					log.debug("Adding pdf extension to output file name");
					outputFile = new File(outputFile.getAbsolutePath() + ".pdf");
				}
			}
			else
			{
				log.debug("Output file extension set my user to '" + FileUtils.getExtension(outputFile.getAbsolutePath()) + "'");
			}

			App.prefs.setPref(PrefKey.PREF_LAST_EXPORT_FOLDER, outputFile.getAbsolutePath());
		}
		else
		{
			return null;
		}

		if (outputFile.exists())
		{
			Object[] options = { "Overwrite", "No", "Cancel" };

			// notes about parameters: null (don't use custom icon), options (the titles of buttons), options[0] (default button title)
			int response = JOptionPane.showOptionDialog(App.mainFrame, "The file '" + outputFile.getName()
					+ "' already exists.  Are you sure you want to overwrite?", "Confirm", JOptionPane.YES_NO_CANCEL_OPTION,
					JOptionPane.QUESTION_MESSAGE, null, options, options[0]);

			if (response != JOptionPane.YES_OPTION)
				return null;
		}
		return outputFile;
	}

	/**
	 * Checks whether or not the file at the specified path is valid.
	 * 
	 * @return true if the file is valid, false otherwise
	 */
	private Boolean filePathHasValidFile(String inFilePath) {

		if (inFilePath == null)
			return false;

		if (!(inFilePath.substring(inFilePath.length() - 4, inFilePath.length()).equals((String) ".fhx")))
			return false;

		File theFHX2File = new File(inFilePath);
		FHX2FileReader tempReader = new FHX2FileReader(theFHX2File);

		if (tempReader.getNumberOfSeries() < 6)
			return false;

		if (!tempReader.passesBasicSyntaxCheck())
			return false;

		return true;
	}

	/**
	 * Sets up the checkbox values according to the stored preference keys.
	 */
	private void setCheckBoxesToPrefKeyValues() {

		try
		{
			if (App.prefs.getPref(PrefKey.SSIZ_CHK_COMMON_YEARS, null).equals("TRUE"))
				chkCommonYears.setSelected(true);
			else
				chkCommonYears.setSelected(false);
		}
		catch (NullPointerException ex)
		{
			App.prefs.setPref(PrefKey.SSIZ_CHK_COMMON_YEARS, "FALSE");
		}

		try
		{
			if (App.prefs.getPref(PrefKey.SSIZ_CHK_EXCLUDE_SERIES_WITH_NO_EVENTS, null).equals("TRUE"))
				chkExcludeSeriesWithNoEvents.setSelected(true);
			else
				chkExcludeSeriesWithNoEvents.setSelected(false);
		}
		catch (NullPointerException ex)
		{
			App.prefs.setPref(PrefKey.SSIZ_CHK_EXCLUDE_SERIES_WITH_NO_EVENTS, "FALSE");
		}
	}

	/**
	 * Set up analysis parameters in SSIZController according to the selected settings in the GUI.
	 */
	private SSIZAnalysisModel createSSIZAnalysisModel() {

		if (reader == null)
			return null;

		SSIZAnalysisModel model = new SSIZAnalysisModel((Integer) spnSeed.getValue(), reader,
				(EventTypeToProcess) cboEventType.getSelectedItem());

		model.setNumSimulationsToRun((Integer) spnSimulations.getValue());
		model.setResamplingType((ResamplingType) cboResampling.getSelectedItem());
		model.setThresholdType((FireFilterType) cboThresholdType.getSelectedItem());
		model.setThresholdValue((Integer) spnThresholdValue.getValue());

		// Do this before restricting to common years (otherwise common year restriction may have no effect)
		if (chkExcludeSeriesWithNoEvents.isSelected())
			SSIZController.restrictAnalysisToSeriesWithEvents(model);

		// Defaults to the original first and last years if no common years are found among the samples
		if (chkCommonYears.isSelected())
			SSIZController.restrictAnalysisToCommonYears(model);

		model.setSegmentArray(segmentationPanel.table.tableModel.getSegments());

		return model;
	}

	/**
	 * Set up GUI restrictions depending on the current FHFileReader.
	 */
	private void setGUIForFHFileReader() {

		if (reader == null)
		{
			log.debug("File is null so not setting GUI items accordingly");
			actionRun.setEnabled(false);
			return;
		}

		if (reader.getNumberOfSeries() < 6)
		{
			JOptionPane.showMessageDialog(this, "Sample size analysis requires an input file with 5\nor more series. This file has just "
					+ reader.getNumberOfSeries() + " series.", "Not enough series", JOptionPane.ERROR_MESSAGE);
			actionRun.setEnabled(false);
			return;
		}

		if (!reader.passesBasicSyntaxCheck())
		{
			JOptionPane.showMessageDialog(this, "Error reading file", "FHX file error", JOptionPane.ERROR_MESSAGE);
			actionRun.setEnabled(false);
			return;
		}

		int firstyear = reader.getFirstYear();
		int lastyear = reader.getLastYear();

		if (reader.getFirstYear() != null && reader.getLastYear() != null)
		{
			log.debug("Setting year range to " + firstyear + " - " + lastyear);

			segmentationPanel.table.setEarliestYear(firstyear);
			segmentationPanel.table.setLatestYear(lastyear);
		}

		txtInputFile.setText(reader.getFile().getAbsolutePath());

		// Force segments to be specified if they've chosen segmentation
		if (segmentationPanel.chkSegmentation.isSelected()
				&& (segmentationPanel.table.tableModel.getSegments() == null || segmentationPanel.table.tableModel.getSegments().size() == 0))
		{
			actionRun.setEnabled(false);
			actionSaveAll.setEnabled(false);
			return;
		}

		actionRun.setEnabled(true);
		actionSaveAll.setEnabled(true);
	}

	/**
	 * Run the actual analysis task. This function calls a SwingWorker task so the GUI remains responsive during processing.
	 */
	private void runSSIZAnalysisTask() {

		setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		mouseListenersActive = false;

		this.getContentPane().add(panelProgressBar, BorderLayout.SOUTH);
		panelProgressBar.setVisible(true);
		progressBar.setValue(0);

		// Enable and disable the appropriate GUI components during analysis
		actionBrowse.setEnabled(false);
		actionExportPDF.setEnabled(false);
		actionExportPNG.setEnabled(false);
		actionRun.setEnabled(false);
		actionSaveAll.setEnabled(false);
		chkCommonYears.setEnabled(false);
		chkExcludeSeriesWithNoEvents.setEnabled(false);
		segmentationPanel.chkSegmentation.setEnabled(false);
		simulationsTable.setEnabled(false);
		btnCancelAnalysis.setVisible(true);

		SSIZAnalysisModel analysisModel = createSSIZAnalysisModel();

		(task = new DrawSSIZAnalysisTask(analysisModel)).execute();

		validate();
		repaint();
	}

	private static class ResultPair {

		private final Integer progress;
		private final SSIZAnalysisModel model;

		ResultPair(Integer progress, SSIZAnalysisModel model) {

			this.progress = progress;
			this.model = model;
		}
	}

	/**
	 * SwingWorker class for handling the calling of the analysis and drawing the progress bar.
	 */
	private class DrawSSIZAnalysisTask extends SwingWorker<SSIZAnalysisModel, ResultPair> {

		private SSIZAnalysisModel analysisModel;

		public DrawSSIZAnalysisTask(SSIZAnalysisModel analysisModel) {

			super();
			this.analysisModel = analysisModel;
			taskWasCancelled = false;
		}

		@Override
		protected void process(List<ResultPair> progressList) {

			ResultPair current = progressList.get(progressList.size() - 1);

			int maxitems = current.model.getSegments().size() * current.model.getSeriesPoolToAnalyze().size();
			progressBar.setMaximum(maxitems);

			progressBar.setValue(current.progress);
			displayChartAndTableOutput(current.model);

			if (current.model.getSegments().size() > 1 && segmentsDone < current.model.getSegments().size())
			{
				if (segmentsDone > 0)
					cboSegment.setSelectedIndex(segmentsDone);
			}

		}

		/**
		 * Run the analysis in the background updating progress bar as it goes
		 */
		protected SSIZAnalysisModel doInBackground() throws Exception {

			SSIZController.doPreRunSetup(analysisModel);

			segmentsDone = 0;

			// Loop through from 1 to n series
			for (SegmentModel segment : analysisModel.getSegments())
			{
				Double centuryMultiplier = SSIZController.getCenturyMultiplier(analysisModel, segment);

				for (int n = 1; n <= analysisModel.getSeriesPoolToAnalyze().size(); n++)
				{
					if (task.isCancelled())
					{
						log.debug("Current analysis task has been cancelled");
						return null;
					}

					SSIZController.runSampleSizeAnalysisLoopIteration(analysisModel, centuryMultiplier, n, segment);

					Integer currentItem = (segmentsDone * analysisModel.getSeriesPoolToAnalyze().size()) + n;
					// progressBar.setValue(currentItem);

					// Draws the chart and table once for every 25 iterations completed (only when there is one segment)
					// if (currentItem % 25 == 0 && analysisModel.getSegments().size() == 1)
					// displayChartAndTableOutput(analysisModel);
					publish(new ResultPair(currentItem, analysisModel));
				}

				// Draws the chart and table after every segment is completed (when multiple segments have been specified)
				/*
				 * if (analysisModel.getSegments().size() > 1 && segmentsDone < analysisModel.getSegments().size()) { if (segmentsDone > 0)
				 * cboSegment.setSelectedIndex(segmentsDone);
				 * 
				 * displayChartAndTableOutput(analysisModel); }
				 */

				segmentsDone++;
			}
			return analysisModel;
		}

		/**
		 * Function called when the analysis is complete. This draws the results and resets the progress bar/cursor etc.
		 */
		@Override
		public void done() {

			panelProgressBar.setVisible(false);
			getContentPane().remove(panelProgressBar);

			mouseListenersActive = true;
			setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));

			// Enable and disable the appropriate GUI components after analysis
			actionBrowse.setEnabled(true);
			actionExportPDF.setEnabled(true);
			actionExportPNG.setEnabled(true);
			actionRun.setEnabled(true);
			actionSaveAll.setEnabled(true);
			chkCommonYears.setEnabled(true);
			chkExcludeSeriesWithNoEvents.setEnabled(true);
			segmentationPanel.chkSegmentation.setEnabled(true);
			simulationsTable.setEnabled(true);
			btnCancelAnalysis.setVisible(false);

			SSIZAnalysisModel mdl = null;
			try
			{
				mdl = get();
			}
			catch (InterruptedException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			catch (ExecutionException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			if (!taskWasCancelled)
			{
				// Reset segmentsDone so that the chart defaults to the first segment when displaying
				segmentsDone = 0;
				displayChartAndTableOutput(mdl);
			}

			// Reset this flag so that file names may be loaded from the text field again.
			fileDialogWasUsed = false;
		}
	}

	/**
	 * Updates and redraws the results table and curve chart on the GUI.
	 */
	private void displayChartAndTableOutput(SSIZAnalysisModel analysisModel) {

		try
		{
			// Generate the SSIZAnalysisModel for this analysis
			populateFromAnalysisModel(analysisModel);

			// Update the results table with the analysis results
			simulationsTable.redrawTable(SSIZController.getAnalysisResults());

			// Update the curve chart with the analysis results
			updateChart();

			validate();
			repaint();
		}
		catch (Exception e)
		{
			e.printStackTrace();
			log.error("Error redrawing chart and table");
		}
	}

	/**
	 * Generate the SSIZAnalysisModel representing the data and parameters for this analysis.
	 * 
	 * @param model
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void populateFromAnalysisModel(SSIZAnalysisModel model) {

		DefaultComboBoxModel combomodel = new DefaultComboBoxModel();

		for (SegmentModel segment : model.getSegments())
			combomodel.addElement(segment);

		cboSegment.setModel(combomodel);
		cboSegment.setEnabled(model.getSegments().size() > 1);
		cboSegment.setSelectedIndex(segmentsDone);
		cboChartMetric.setEnabled(true);
	}

	/**
	 * Update the chart on the screen using the parameters specified by the user.
	 */
	private void updateChart() {

		curveChart = new SSIZCurveChart(SSIZController.getAnalysisResults().toArray(
				new AnalysisResultsModel[SSIZController.getAnalysisResults().size()]),
				(MiddleMetric) this.cboChartMetric.getSelectedItem(), (SegmentModel) cboSegment.getSelectedItem());
		curveChart.addMouseListener(new ChartPopClickListener());
		curveChart.setMaximumDrawHeight(MAX_DRAW_HEIGHT);
		curveChart.setMaximumDrawWidth(MAX_DRAW_WIDTH);
		panelChart.removeAll();
		panelChart.add(curveChart, BorderLayout.CENTER);
		panelChart.revalidate();
		panelChart.repaint();
	}

	/**
	 * Copy the simulations chart to the system clip-board.
	 */
	private void copyChartToClipboard() {

		curveChart.doCopy();
	}

	/**
	 * Copy the simulations data currently selected in the table to the system clip-board.
	 */
	private void copyTableToClipboard() {

		adapter.doCopy();
	}

	/**
	 * Save the results of the analysis to disk.
	 */
	public void saveAll() {

		// TODO
	}

	/**
	 * TODO
	 */
	@SuppressWarnings("unused")
	private class ScrollViewport extends JViewport implements Scrollable {

		private static final long serialVersionUID = 1L;

		@Override
		public Dimension getPreferredScrollableViewportSize() {

			return getPreferredSize();
		}

		@Override
		public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction) {

			return 30;
		}

		@Override
		public boolean getScrollableTracksViewportHeight() {

			return false;
		}

		@Override
		public boolean getScrollableTracksViewportWidth() {

			return true;
		}

		@Override
		public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction) {

			return 1;
		}
	}

	/**
	 * The pop-up menu for the simulations chart
	 */
	private class ChartPopupMenu extends JPopupMenu {

		private static final long serialVersionUID = 1L;

		JMenuItem exportToPDF;
		JMenuItem exportToPNG;

		public ChartPopupMenu() {

			JMenuItem chartProperties = new JMenuItem("Properties");
			chartProperties.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent arg0) {

					ChartEditor editor = ChartEditorManager.getChartEditor(curveChart.getChart());
					int result = JOptionPane.showConfirmDialog(null, editor, "Properties", JOptionPane.OK_CANCEL_OPTION,
							JOptionPane.PLAIN_MESSAGE);

					if (result == JOptionPane.OK_OPTION)
						editor.updateChart(curveChart.getChart());
				}
			});
			add(chartProperties);

			exportToPDF = new JMenuItem("Export to PDF");
			exportToPDF.setAction(actionExportPDF);
			exportToPDF.setIcon(Builder.getImageIcon("pdf.png"));
			add(exportToPDF);

			exportToPNG = new JMenuItem("Export to PNG");
			exportToPNG.setAction(actionExportPNG);
			exportToPNG.setIcon(Builder.getImageIcon("formatpng.png"));
			add(exportToPNG);

			addSeparator();

			JMenuItem copy = new JMenuItem("Copy");
			copy.setIcon(Builder.getImageIcon("edit_copy.png"));
			copy.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent arg0) {

					copyChartToClipboard();
				}
			});
			add(copy);
		}
	}

	/**
	 * The pop-up menu for the simulations data table
	 */
	private class TablePopupMenu extends JPopupMenu {

		private static final long serialVersionUID = 1L;

		JMenuItem exportToTAB;

		public TablePopupMenu() {

			exportToTAB = new JMenuItem("Export to tab delimited text file");
			exportToTAB.setIcon(Builder.getImageIcon("formattab.png"));
			exportToTAB.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent arg0) {

					File fileToSave = getFileFromSaveDialog("TAB");
					if (fileToSave != null)
						SSIZResultsTable.exportResultsTableToTAB(fileToSave, adapter);
				}
			});
			add(exportToTAB);

			addSeparator();

			JMenuItem selectAll = new JMenuItem("Select all");
			selectAll.setIcon(Builder.getImageIcon("selectall.png"));
			selectAll.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {

					simulationsTable.selectAll();
				}
			});
			add(selectAll);

			JMenuItem copy = new JMenuItem("Copy");
			copy.setIcon(Builder.getImageIcon("edit_copy.png"));
			copy.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {

					copyTableToClipboard();
				}
			});
			add(copy);
		}
	}

	/**
	 * The pop-up listener for the simulations chart
	 */
	private class ChartPopClickListener extends MouseAdapter {

		public void mousePressed(MouseEvent arg0) {

			if (arg0.isPopupTrigger() && mouseListenersActive)
				doPop(arg0);
		}

		public void mouseReleased(MouseEvent arg0) {

			if (arg0.isPopupTrigger() && mouseListenersActive)
				doPop(arg0);
		}

		private void doPop(MouseEvent arg0) {

			ChartPopupMenu menu = new ChartPopupMenu();
			menu.show(arg0.getComponent(), arg0.getX(), arg0.getY());
		}
	}

	/**
	 * The pop-up listener for the simulations data table.
	 */
	private class TablePopClickListener extends MouseAdapter {

		public void mousePressed(MouseEvent arg0) {

			if (arg0.isPopupTrigger() && mouseListenersActive)
				doPop(arg0);
		}

		public void mouseReleased(MouseEvent arg0) {

			if (arg0.isPopupTrigger() && mouseListenersActive)
				doPop(arg0);
		}

		private void doPop(MouseEvent arg0) {

			TablePopupMenu menu = new TablePopupMenu();
			menu.show(arg0.getComponent(), arg0.getX(), arg0.getY());
		}
	}
}
