/**************************************************************************************************
 * Fire History Analysis and Exploration System (FHAES), Copyright (C) 2015
 * 
 * Contributors: Elena Velasquez, Joshua Brogan, and Peter Brewer
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
package org.fhaes.jsea;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

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
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileFilter;

import net.miginfocom.swing.MigLayout;

import org.apache.commons.io.FilenameUtils;
import org.fhaes.components.HelpTipButton;
import org.fhaes.components.JToolBarButton;
import org.fhaes.filefilter.CSVFileFilter;
import org.fhaes.filefilter.PDFFilter;
import org.fhaes.filefilter.PNGFilter;
import org.fhaes.filefilter.TXTFileFilter;
import org.fhaes.gui.MainWindow;
import org.fhaes.help.RemoteHelp;
import org.fhaes.preferences.App;
import org.fhaes.preferences.FHAESPreferences.PrefKey;
import org.fhaes.preferences.wrappers.CheckBoxWrapper;
import org.fhaes.preferences.wrappers.SpinnerWrapper;
import org.fhaes.preferences.wrappers.TextComponentWrapper;
import org.fhaes.segmentation.SegmentModel;
import org.fhaes.segmentation.SegmentTableModel;
import org.fhaes.segmentation.SegmentationPanel;
import org.fhaes.util.Builder;
import org.fhaes.util.FHAESAction;
import org.fhaes.util.JTableSpreadsheetByRowAdapter;
import org.fhaes.util.Platform;
import org.fhaes.util.TableUtil;
import org.jdesktop.swingx.JXTable;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.editor.ChartEditor;
import org.jfree.chart.editor.ChartEditorManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tridas.io.util.SafeIntYear;
import org.tridas.io.util.StringUtils;
import org.tridas.io.util.YearRange;

import au.com.bytecode.opencsv.CSVReader;
import edu.emory.mathcs.backport.java.util.Collections;

/**
 * JSEAFrame Class.
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
public class JSEAFrame extends JFrame implements ActionListener {
	
	private static final long serialVersionUID = 1L;
	
	public static final int MAX_DRAW_HEIGHT = 1080;
	public static final int MAX_DRAW_WIDTH = 1920;
	
	private final JPanel contentPanel = new JPanel();
	private JTextField txtTimeSeriesFile;
	private JTextField txtEventListFile;
	private JTextField txtChartTitle;
	private JTextField txtYAxisLabel;
	private JButton btnEventListFile;
	private JButton btnTimeSeriesFile;
	private JCheckBox chkIncludeIncompleteWindow;
	private JSpinner spnSimulationsToRun;
	protected JComboBox cbxPValue;
	private JSpinner spnSeedNumber;
	private JSpinner spnLagsAfter;
	private JSpinner spnLagsPrior;
	protected JSEAStatsFunctions jsea;
	private JTabbedPane tabbedPane;
	private JMenu mnSave;
	private TextComponentWrapper txtwrapper;
	
	private JTableSpreadsheetByRowAdapter adapterActualTable;
	private JTableSpreadsheetByRowAdapter adapterSimulationTable;
	
	private static final Logger log = LoggerFactory.getLogger(JSEAFrame.class);
	
	protected ArrayList<Integer> chronologyYears = new ArrayList<Integer>();
	protected ArrayList<Double> chronologyActual = new ArrayList<Double>();;
	protected ArrayList<Integer> events = new ArrayList<Integer>();
	private SafeIntYear firstPossibleYear;
	private SafeIntYear lastPossibleYear;
	
	private JSpinner spnFirstYear;
	private JSpinner spnLastYear;
	private JCheckBox chkAllYears;
	
	private FHAESAction actionFileExit;
	private FHAESAction actionReset;
	private FHAESAction actionRun;
	private FHAESAction actionSaveAll;
	private FHAESAction actionSaveData;
	private FHAESAction actionSaveReport;
	private FHAESAction actionSaveChart;
	private FHAESAction actionCopy;
	private FHAESAction actionChartProperties;
	private FHAESAction actionLagMap;
	
	protected SegmentationPanel segmentationPanel;
	
	private JPanel summaryPanel;
	private JTextArea txtSummary;
	private JScrollPane scrollPane;
	
	private JPanel dataPanel;
	private JXTable tblActual;
	private JXTable tblSimulation;
	
	private JPanel chartPanel;
	private JSEABarChart barChart;
	private JLabel plotSegmentLabel;
	private JComboBox segmentComboBox;
	
	/**
	 * Create the dialog.
	 */
	public JSEAFrame(Component parent) {
	
		setupGui();
		this.pack();
		this.setLocationRelativeTo(parent);
		this.txtTimeSeriesFile.setText("");
		this.txtEventListFile.setText("");
		{
			JPanel buttonPanel = new JPanel();
			contentPanel.add(buttonPanel, "cell 0 4,grow");
			buttonPanel.setLayout(new MigLayout("", "[left][grow][right]", "[]"));
			{
				JButton resetButton = new JButton("Reset");
				resetButton.setAction(actionReset);
				buttonPanel.add(resetButton, "cell 0 0,grow");
			}
			{
				JButton runAnalysisButton = new JButton("Run Analysis");
				runAnalysisButton.setAction(actionRun);
				buttonPanel.add(runAnalysisButton, "cell 2 0,grow");
			}
		}
		this.setVisible(true);
	}
	
	/**
	 * Copy the currently table cells to the clipboard.
	 */
	public void copyCurrentSelectionToClipboard() {
	
		Component focusedComponent = this.getFocusOwner();
		
		if (focusedComponent != null)
		{
			if (focusedComponent.equals(tblActual))
			{
				this.adapterActualTable.doCopy();
			}
			else if (focusedComponent.equals(tblSimulation))
			{
				this.adapterSimulationTable.doCopy();
			}
			else if (focusedComponent.equals(txtSummary))
			{
				txtSummary.copy();
			}
		}
	}
	
	/**
	 * Setup toolbar.
	 */
	private void setupToolbar() {
	
		JToolBar toolBar = new JToolBar();
		toolBar.setFloatable(false);
		toolBar.setRollover(true);
		getContentPane().add(toolBar, "cell 0 0");
		{
			JToolBarButton btnNew = new JToolBarButton(actionReset);
			btnNew.setToolTipText("Start new analysis");
			toolBar.add(btnNew);
			
			JToolBarButton btnSaveAll = new JToolBarButton(actionSaveAll);
			btnSaveAll.setToolTipText("Save all results");
			toolBar.add(btnSaveAll);
			
			toolBar.addSeparator();
			
			JToolBarButton btnCopy = new JToolBarButton(actionCopy);
			btnSaveAll.setToolTipText("Copy");
			toolBar.add(btnCopy);
			
			JToolBarButton btnChartProperties = new JToolBarButton(actionChartProperties);
			btnChartProperties.setToolTipText("Chart properties");
			toolBar.add(btnChartProperties);
			
			toolBar.addSeparator();
			
			JToolBarButton btnRun = new JToolBarButton(actionRun);
			btnRun.setToolTipText("Run analysis");
			toolBar.add(btnRun);
			
			toolBar.addSeparator();
			
			JToolBarButton btnLagMap = new JToolBarButton(actionLagMap);
			btnLagMap.setToolTipText("Launch LagMap");
			toolBar.add(btnLagMap);
		}
	}
	
	/**
	 * Setup menu bar
	 */
	private void setupMenu() {
	
		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);
		{
			JMenu mnFile = new JMenu("File");
			menuBar.add(mnFile);
			{
				JMenuItem mntmClose = new JMenuItem(actionFileExit);
				
				{
					JMenuItem mntmNew = new JMenuItem(actionReset);
					mnFile.add(mntmNew);
				}
				{
					mnSave = new JMenu("Save");
					mnSave.setIcon(Builder.getImageIcon("save.png"));
					mnFile.add(mnSave);
					{
						JMenuItem mntmChart = new JMenuItem(actionSaveChart);
						mnSave.add(mntmChart);
					}
					{
						JMenuItem mntmReport = new JMenuItem(actionSaveReport);
						mnSave.add(mntmReport);
					}
					{
						JMenuItem mntmData = new JMenuItem(actionSaveData);
						mnSave.add(mntmData);
					}
					mnSave.addSeparator();
					{
						JMenuItem mntmAll = new JMenuItem(actionSaveAll);
						mnSave.add(mntmAll);
					}
				}
				mnFile.addSeparator();
				mnFile.add(mntmClose);
			}
		}
		{
			JMenu mnEdit = new JMenu("Edit");
			menuBar.add(mnEdit);
			{
				JMenuItem mntmCopy = new JMenuItem(actionCopy);
				mnEdit.add(mntmCopy);
				
				JMenuItem mntmChartProperties = new JMenuItem(actionChartProperties);
				mnEdit.add(mntmChartProperties);
				
				mnEdit.addSeparator();
				JMenuItem mntmRun = new JMenuItem(actionRun);
				mnEdit.add(mntmRun);
			}
		}
		{
			JMenu mnTools = new JMenu("Tools");
			menuBar.add(mnTools);
			{
				JMenuItem mntmLagMap = new JMenuItem(actionLagMap);
				mnTools.add(mntmLagMap);
				
			}
		}
		{
			JMenu mnHelp = new JMenu("Help");
			menuBar.add(mnHelp);
			{
				JMenuItem mntmHelp = new JMenuItem(MainWindow.actionHelp);
				mnHelp.add(mntmHelp);
			}
			mnHelp.addSeparator();
			{
				JMenuItem mntmAbout = new JMenuItem(MainWindow.actionAbout);
				mnHelp.add(mntmAbout);
			}
		}
	}
	
	/**
	 * Initialize the menu/toolbar actions.
	 */
	private void initActions() {
	
		final JFrame glue = this;
		
		actionFileExit = new FHAESAction("Close", "close.png") { //$NON-NLS-1$
		
			private static final long serialVersionUID = 1L;
			
			@Override
			public void actionPerformed(ActionEvent event) {
			
				dispose();
			}
		};
		
		actionChartProperties = new FHAESAction("Chart properties", "properties.png") { //$NON-NLS-1$
		
			private static final long serialVersionUID = 1L;
			
			@Override
			public void actionPerformed(ActionEvent event) {
			
				ChartEditor editor = ChartEditorManager.getChartEditor(jsea.getChartList().get(segmentComboBox.getSelectedIndex())
						.getChart());
				int result = JOptionPane.showConfirmDialog(glue, editor, "Properties", JOptionPane.OK_CANCEL_OPTION,
						JOptionPane.PLAIN_MESSAGE);
				if (result == JOptionPane.OK_OPTION)
				{
					editor.updateChart(jsea.getChartList().get(segmentComboBox.getSelectedIndex()).getChart());
				}
			}
		};
		
		actionReset = new FHAESAction("Reset", "filenew.png") {
			
			private static final long serialVersionUID = 1L;
			
			@Override
			public void actionPerformed(ActionEvent event) {
			
				Object[] options = { "Yes", "No", "Cancel" };
				int n = JOptionPane.showOptionDialog(glue, "Are you sure you want to start a new analysis?", "Confirm",
						JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[2]);
				
				if (n == JOptionPane.YES_OPTION)
				{
					setToDefault();
				}
			}
		};
		
		actionRun = new FHAESAction("Run analysis", "run.png") {
			
			private static final long serialVersionUID = 1L;
			
			@Override
			public void actionPerformed(ActionEvent event) {
			
				runAnalysis();
			}
		};
		
		actionSaveAll = new FHAESAction("Save all results", "save_all.png") {
			
			private static final long serialVersionUID = 1L;
			
			@Override
			public void actionPerformed(ActionEvent event) {
			
				if (jsea == null)
					return;
				
				File file;
				JFileChooser fc;
				
				// Open file chooser in last folder if possible
				if (App.prefs.getPref(PrefKey.PREF_LAST_EXPORT_FOLDER, null) != null)
				{
					fc = new JFileChooser(App.prefs.getPref(PrefKey.PREF_LAST_EXPORT_FOLDER, null));
				}
				else
				{
					fc = new JFileChooser();
				}
				
				fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				
				// Show dialog and get specified file
				int returnVal = fc.showSaveDialog(glue);
				if (returnVal == JFileChooser.APPROVE_OPTION)
				{
					file = fc.getSelectedFile();
					App.prefs.setPref(PrefKey.PREF_LAST_EXPORT_FOLDER, file.getAbsolutePath());
				}
				else
				{
					return;
				}
				
				File f;
				try
				{
					f = new File(file.getAbsolutePath() + File.separator + "report.txt");
					saveReportTXT(f);
					
					f = new File(file.getAbsolutePath() + File.separator + "report.pdf");
					saveReportPDF(f);
					
					f = new File(file.getAbsolutePath() + File.separator + "chart.png");
					saveChartPNG(f);
					
					f = new File(file.getAbsolutePath() + File.separator + "chart.pdf");
					saveChartPDF(f);
					
					f = new File(file.getAbsolutePath() + File.separator + "data.xls");
					saveDataXLS(f);
					
					f = new File(file.getAbsolutePath());
					saveDataCSV(f);
					
				}
				catch (IOException e)
				{
					e.printStackTrace();
				}
			}
		};
		
		actionSaveData = new FHAESAction("Save data tables", "table.png") {
			
			private static final long serialVersionUID = 1L;
			
			@Override
			public void actionPerformed(ActionEvent event) {
			
				if (jsea == null)
					return;
				
				File file;
				JFileChooser fc;
				
				// Open file chooser in last folder if possible
				if (App.prefs.getPref(PrefKey.PREF_LAST_EXPORT_FOLDER, null) != null)
				{
					fc = new JFileChooser(App.prefs.getPref(PrefKey.PREF_LAST_EXPORT_FOLDER, null));
				}
				else
				{
					fc = new JFileChooser();
				}
				
				fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				
				// Show dialog and get specified file
				int returnVal = fc.showSaveDialog(glue);
				if (returnVal == JFileChooser.APPROVE_OPTION)
				{
					file = fc.getSelectedFile();
					App.prefs.setPref(PrefKey.PREF_LAST_EXPORT_FOLDER, file.getAbsolutePath());
				}
				else
				{
					return;
				}
				
				try
				{
					saveDataCSV(file);
				}
				catch (IOException e)
				{
					e.printStackTrace();
				}
			}
		};
		
		actionSaveReport = new FHAESAction("Save report", "report.png") {
			
			private static final long serialVersionUID = 1L;
			
			@Override
			public void actionPerformed(ActionEvent event) {
			
				if (jsea == null)
					return;
				
				File file;
				JFileChooser fc;
				
				// Open file chooser in last folder if possible
				if (App.prefs.getPref(PrefKey.PREF_LAST_EXPORT_FOLDER, null) != null)
				{
					fc = new JFileChooser(App.prefs.getPref(PrefKey.PREF_LAST_EXPORT_FOLDER, null));
				}
				else
				{
					fc = new JFileChooser();
				}
				
				// Set file filters
				fc.setAcceptAllFileFilterUsed(false);
				TXTFileFilter txtfilter = new TXTFileFilter();
				PDFFilter pdffilter = new PDFFilter();
				fc.addChoosableFileFilter(txtfilter);
				fc.addChoosableFileFilter(pdffilter);
				fc.setFileFilter(txtfilter);
				FileFilter chosenFilter;
				
				// Show dialog and get specified file
				int returnVal = fc.showSaveDialog(glue);
				if (returnVal == JFileChooser.APPROVE_OPTION)
				{
					file = fc.getSelectedFile();
					chosenFilter = fc.getFileFilter();
					App.prefs.setPref(PrefKey.PREF_LAST_EXPORT_FOLDER, file.getAbsolutePath());
				}
				else
				{
					return;
				}
				
				// Handle file type and extensions nicely
				if (FilenameUtils.getExtension(file.getAbsolutePath()).equals(""))
				{
					if (chosenFilter.equals(txtfilter))
					{
						file = new File(file.getAbsoluteFile() + ".txt");
					}
					else if (chosenFilter.equals(pdffilter))
					{
						file = new File(file.getAbsoluteFile() + ".pdf");
					}
				}
				else if (FilenameUtils.getExtension(file.getAbsolutePath()).toLowerCase().equals("txt") && chosenFilter.equals("pdf"))
				{
					chosenFilter = txtfilter;
				}
				else if (FilenameUtils.getExtension(file.getAbsolutePath()).toLowerCase().equals("pdf") && chosenFilter.equals("txt"))
				{
					chosenFilter = pdffilter;
				}
				
				// If file already exists confirm overwrite
				if (file.exists())
				{
					// Check we have write access to this file
					if (!file.canWrite())
					{
						JOptionPane.showMessageDialog(glue, "You do not have write permission to this file", "Error",
								JOptionPane.ERROR_MESSAGE);
						return;
					}
					
					int n = JOptionPane.showConfirmDialog(glue, "File: " + file.getName() + " already exists. "
							+ "Would you like to overwrite it?", "Overwrite file?", JOptionPane.YES_NO_OPTION);
					if (n != JOptionPane.YES_OPTION)
					{
						return;
					}
				}
				
				// Do save
				try
				{
					
					if (chosenFilter.equals(txtfilter))
					{
						saveReportTXT(file);
					}
					else if (chosenFilter.equals(pdffilter))
					{
						saveReportPDF(file);
					}
					else
					{
						log.error("No export file format chosen.  Shouldn't be able to get here!");
					}
				}
				catch (IOException e)
				{
					JOptionPane.showMessageDialog(glue, "Unable to save report.  Check log file.", "Warning", JOptionPane.ERROR_MESSAGE);
					e.printStackTrace();
				}
				
			}
		};
		
		actionSaveChart = new FHAESAction("Save chart", "barchart.png") {
			
			private static final long serialVersionUID = 1L;
			
			@Override
			public void actionPerformed(ActionEvent event) {
			
				if (jsea == null)
					return;
				
				File file;
				JFileChooser fc;
				
				// Open file chooser in last folder if possible
				if (App.prefs.getPref(PrefKey.PREF_LAST_EXPORT_FOLDER, null) != null)
				{
					fc = new JFileChooser(App.prefs.getPref(PrefKey.PREF_LAST_EXPORT_FOLDER, null));
				}
				else
				{
					fc = new JFileChooser();
				}
				
				// Set file filters
				fc.setAcceptAllFileFilterUsed(false);
				PNGFilter pngfilter = new PNGFilter();
				PDFFilter pdffilter = new PDFFilter();
				fc.addChoosableFileFilter(pngfilter);
				fc.addChoosableFileFilter(pdffilter);
				fc.setFileFilter(pngfilter);
				FileFilter chosenFilter;
				
				// Show dialog and get specified file
				int returnVal = fc.showSaveDialog(glue);
				if (returnVal == JFileChooser.APPROVE_OPTION)
				{
					file = fc.getSelectedFile();
					chosenFilter = fc.getFileFilter();
					App.prefs.setPref(PrefKey.PREF_LAST_EXPORT_FOLDER, file.getAbsolutePath());
				}
				else
				{
					return;
				}
				
				// Handle file type and extensions nicely
				if (FilenameUtils.getExtension(file.getAbsolutePath()).equals(""))
				{
					if (chosenFilter.equals(pngfilter))
					{
						file = new File(file.getAbsoluteFile() + ".png");
					}
					else if (chosenFilter.equals(pdffilter))
					{
						file = new File(file.getAbsoluteFile() + ".pdf");
					}
				}
				else if (FilenameUtils.getExtension(file.getAbsolutePath()).toLowerCase().equals("png") && chosenFilter.equals("pdf"))
				{
					chosenFilter = pngfilter;
				}
				else if (FilenameUtils.getExtension(file.getAbsolutePath()).toLowerCase().equals("pdf") && chosenFilter.equals("png"))
				{
					chosenFilter = pdffilter;
				}
				
				// If file already exists confirm overwrite
				if (file.exists())
				{
					// Check we have write access to this file
					if (!file.canWrite())
					{
						JOptionPane.showMessageDialog(glue, "You do not have write permission to this file", "Error",
								JOptionPane.ERROR_MESSAGE);
						return;
					}
					
					int n = JOptionPane.showConfirmDialog(glue, "File: " + file.getName() + " already exists. "
							+ "Would you like to overwrite it?", "Overwrite file?", JOptionPane.YES_NO_OPTION);
					if (n != JOptionPane.YES_OPTION)
					{
						return;
					}
				}
				
				// Do save
				try
				{
					
					if (chosenFilter.equals(pngfilter))
					{
						saveChartPNG(file);
						
					}
					else if (chosenFilter.equals(pdffilter))
					{
						
						saveChartPDF(file);
					}
					else
					{
						log.error("No export file format chosen.  Shouldn't be able to get here!");
					}
				}
				catch (IOException e)
				{
					JOptionPane.showMessageDialog(glue, "Unable to save chart.  Check log file.", "Warning", JOptionPane.ERROR_MESSAGE);
					e.printStackTrace();
				}
				
			}
		};
		
		actionCopy = new FHAESAction("Copy", "edit_copy.png") {
			
			private static final long serialVersionUID = 1L;
			
			@Override
			public void actionPerformed(ActionEvent event) {
			
				copyCurrentSelectionToClipboard();
			}
		};
		
		actionLagMap = new FHAESAction("LagMap", "lagmap22.png") {
			
			private static final long serialVersionUID = 1L;
			
			@Override
			public void actionPerformed(ActionEvent event) {
			
				launchLagMap();
			}
		};
		
	}
	
	private void launchLagMap() {
	
		Object[] options = { "Yes", "No", "Cancel" };
		int n = JOptionPane
				.showOptionDialog(
						this,
						"LagMap is an interactive web application written by Wendy Gross and run within your web browser.\nWould you like to continue?",
						"Lauch LagMap", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[2]);
		
		if (n == JOptionPane.YES_OPTION)
			Platform.browseWebpage(RemoteHelp.LAUNCH_LAG_MAP, this);
		
	}
	
	/**
	 * Save the data tables as XLS file
	 * 
	 * @param file
	 * @throws IOException
	 */
	private void saveDataXLS(File file) throws IOException {
	
	}
	
	/**
	 * Save the data tables as CSV file
	 * 
	 * @param folder
	 * @throws IOException
	 */
	private void saveDataCSV(File folder) throws IOException {
	
		File f = new File(folder.getAbsolutePath() + File.separator + "actual-table.csv");
		PrintWriter out = new PrintWriter(f.getAbsoluteFile());
		out.print(jsea.getActualTableText());
		out.close();
		
		f = new File(folder.getAbsolutePath() + File.separator + "simulation-table.csv");
		out = new PrintWriter(f.getAbsoluteFile());
		out.print(jsea.getSimulationTableText());
		out.close();
	}
	
	/**
	 * Save the report as a text file
	 * 
	 * @param file
	 * @throws IOException
	 */
	private void saveReportTXT(File file) throws IOException {
	
		FileWriter pw = new FileWriter(file.getAbsoluteFile());
		txtSummary.write(pw);
	}
	
	/**
	 * Save the report as a PDF
	 * 
	 * @param file
	 * @throws IOException
	 */
	private void saveReportPDF(File file) throws IOException {
	
		jsea.savePDFReport(file.getAbsolutePath());
	}
	
	/**
	 * Save the chart to the specified file in PNG format
	 * 
	 * @param file
	 * @throws IOException
	 */
	private void saveChartPNG(File file) throws IOException {
	
		log.debug("Saving chart as PNG file");
		
		ArrayList<BarChartParametersModel> chartlist = jsea.getChartList();
		log.debug("Chart list size " + chartlist.size());
		JFreeChart chart = null;
		if (chartlist.size() == 0)
		{
			log.debug("No charts in list");
			return;
		}
		else if (chartlist.size() == 1)
		{
			chart = chartlist.get(0).getChart();
		}
		else
		{
			chart = chartlist.get(segmentComboBox.getSelectedIndex()).getChart();
		}
		if (chart != null)
		{
			ChartUtilities.saveChartAsPNG(file, chart, 1000, 500);
		}
		else
		{
			log.error("Cannot save PNG of chart as chart is null!");
		}
	}
	
	/**
	 * Save the chart to the specified file in PDF format
	 * 
	 * @param file
	 * @throws IOException
	 */
	private void saveChartPDF(File file) throws IOException {
	
		log.debug("Saving chart as PDF file");
		/*
		 * OutputStream out = new BufferedOutputStream(new FileOutputStream(file)); JFreeChartManager.writeChartAsPDF(out,
		 * jsea.getChartList().get(segmentComboBox.getSelectedIndex()).getChart(), 1000, 500, new DefaultFontMapper()); out.close();
		 */
		// TODO FIX ME!
	}
	
	/**
	 * Actually perform the Superposed Epoch Analysis
	 */
	private void runAnalysis() {
	
		// Create default segment if segmentation was selected but not defined
		if (segmentationPanel.chkSegmentation.isSelected() && segmentationPanel.table.tableModel.getSegments().isEmpty())
		{
			segmentationPanel.table.tableModel.addSegment(new SegmentModel(Integer.parseInt(this.firstPossibleYear.toString()), Integer
					.parseInt(this.lastPossibleYear.toString())));
			segmentationPanel.table.setEarliestYear(Integer.parseInt(this.firstPossibleYear.toString()));
			segmentationPanel.table.setLatestYear(Integer.parseInt(this.lastPossibleYear.toString()));
		}
		
		// Run the analysis via the JSEAProgressDialog
		new JSEAProgressDialog(this);
		
		try
		{
			if (jsea == null)
				return;
			
			// Populate summary text field
			this.txtSummary.setText(jsea.getReportText());
			setScrollBarToTop();
			
			// Populate actual data table
			tblActual.setModel(TableUtil.createTableModel(jsea.getActualTableText(), null));
			tblActual.packAll();
			
			// Populate simulation data table
			tblSimulation.setModel(TableUtil.createTableModel(jsea.getSimulationTableText(), null));
			tblSimulation.packAll();
			
			// Populate chart panel
			populateSegmentComboBoxAndDrawChart(segmentationPanel.table.tableModel);
			
			setAnalysisAvailable(true);
		}
		catch (Exception e)
		{
			JOptionPane.showMessageDialog(this,
					"An error was caught while running the analysis.\nPlease check the logs for further information", "Error",
					JOptionPane.ERROR_MESSAGE);
			log.error("Error caught when running jSEA analysis");
			e.printStackTrace();
			jsea = null;
			setAnalysisAvailable(false);
		}
		finally
		{
			setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		}
	}
	
	/**
	 * Set up the GUI depending on whether the analysis results are available.
	 * 
	 * @param b
	 */
	private void setAnalysisAvailable(Boolean b) {
	
		actionCopy.setEnabled(b);
		tabbedPane.setEnabled(b);
		mnSave.setEnabled(b);
		actionSaveAll.setEnabled(b);
		actionSaveChart.setEnabled(b);
		actionSaveData.setEnabled(b);
		actionSaveReport.setEnabled(b);
		actionChartProperties.setEnabled(b);
	}
	
	/**
	 * Setup the GUI components
	 */
	private void setupGui() {
	
		setTitle("jSEA - Superposed Epoch Analysis");
		
		getContentPane().setLayout(new MigLayout("", "[1200px,grow,fill]", "[][600px,grow,fill]"));
		
		initActions();
		setupMenu();
		setupToolbar();
		
		this.setIconImage(Builder.getApplicationIcon());
		{
			JSplitPane splitPane = new JSplitPane();
			splitPane.setOneTouchExpandable(true);
			getContentPane().add(splitPane, "cell 0 1,alignx left,aligny top");
			splitPane.setLeftComponent(contentPanel);
			contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
			contentPanel.setLayout(new MigLayout("", "[grow,fill]", "[78.00][][][grow][]"));
			{
				JPanel panel = new JPanel();
				panel.setBorder(new TitledBorder(null, "Input Files", TitledBorder.LEADING, TitledBorder.TOP, null, null));
				contentPanel.add(panel, "cell 0 0,grow");
				panel.setLayout(new MigLayout("", "[][][grow][]", "[][]"));
				{
					JLabel lblContinuousTimeSeries = new JLabel("Continuous time series file:");
					panel.add(lblContinuousTimeSeries, "cell 0 0,alignx trailing");
				}
				{
					HelpTipButton label = new HelpTipButton(
							"Continuous time series data files should be two column comma seperated (CSV) text files.  Column one should contains the years (in sequence), and column two should contain the data values.  If there are header lines or comments in the file, these lines should beginning with a #");
					panel.add(label, "cell 1 0,alignx trailing");
				}
				{
					txtTimeSeriesFile = new JTextField();
					txtwrapper = new TextComponentWrapper(txtTimeSeriesFile, PrefKey.JSEA_CONTINUOUS_TIME_SERIES_FILE, "");
					txtTimeSeriesFile.setEditable(true);
					panel.add(txtTimeSeriesFile, "cell 2 0,growx");
					txtTimeSeriesFile.setColumns(10);
				}
				{
					btnTimeSeriesFile = new JButton();
					btnTimeSeriesFile.setIcon(Builder.getImageIcon("fileopen16.png"));
					btnTimeSeriesFile.setActionCommand("TimeSeriesFileBrowse");
					btnTimeSeriesFile.addActionListener(this);
					btnTimeSeriesFile.setPreferredSize(new Dimension(25, 25));
					btnTimeSeriesFile.setMaximumSize(new Dimension(25, 25));
					btnTimeSeriesFile.putClientProperty("JButton.buttonType", "segmentedTextured");
					btnTimeSeriesFile.putClientProperty("JButton.segmentPosition", "middle");
					
					panel.add(btnTimeSeriesFile, "cell 3 0");
				}
				{
					JLabel lblEventListFile = new JLabel("Event list file:");
					panel.add(lblEventListFile, "cell 0 1,alignx trailing");
				}
				{
					HelpTipButton helpTipButton = new HelpTipButton(
							"Event data files should be a text file with a single column of integer year values.  If there are any header or comment lines, these should begin with a #");
					panel.add(helpTipButton, "cell 1 1,alignx trailing");
				}
				{
					txtEventListFile = new JTextField();
					new TextComponentWrapper(txtEventListFile, PrefKey.JSEA_EVENT_LIST_FILE, "");
					txtEventListFile.setEditable(false);
					panel.add(txtEventListFile, "cell 2 1,growx");
					txtEventListFile.setColumns(10);
				}
				{
					btnEventListFile = new JButton();
					btnEventListFile.setIcon(Builder.getImageIcon("fileopen16.png"));
					btnEventListFile.setActionCommand("EventListFileBrowse");
					btnEventListFile.addActionListener(this);
					btnEventListFile.setPreferredSize(new Dimension(25, 25));
					btnEventListFile.setMaximumSize(new Dimension(25, 25));
					btnEventListFile.putClientProperty("JButton.buttonType", "segmentedTextured");
					btnEventListFile.putClientProperty("JButton.segmentPosition", "middle");
					panel.add(btnEventListFile, "cell 3 1");
				}
			}
			{
				JPanel panel = new JPanel();
				panel.setBorder(new TitledBorder(null, "Window, Simulation and Statistics", TitledBorder.LEADING, TitledBorder.TOP, null,
						null));
				contentPanel.add(panel, "cell 0 1,grow");
				panel.setLayout(new MigLayout("", "[right][][fill][10px:10px:10px][right][][90.00,grow,fill]", "[grow][][][]"));
				{
					JLabel lblYears = new JLabel("Years to analyse:");
					panel.add(lblYears, "cell 0 0");
				}
				{
					HelpTipButton helpTipButton = new HelpTipButton("Specify which years from the dataset to analyse.");
					panel.add(helpTipButton, "cell 1 0");
				}
				{
					JPanel panel_1 = new JPanel();
					panel.add(panel_1, "cell 2 0 5 1,grow");
					panel_1.setLayout(new MigLayout("fill, insets 0", "[80px:80px][][80px:80px,fill][grow]", "[]"));
					{
						spnFirstYear = new JSpinner();
						spnFirstYear.setEnabled(false);
						panel_1.add(spnFirstYear, "cell 0 0,growx");
						spnFirstYear.setModel(new SpinnerNumberModel(new Integer(0), null, null, new Integer(1)));
						spnFirstYear.setEditor(new JSpinner.NumberEditor(spnFirstYear, "#"));
						new SpinnerWrapper(spnFirstYear, PrefKey.JSEA_FIRST_YEAR, 0);
					}
					{
						JLabel lblTo = new JLabel("-");
						panel_1.add(lblTo, "cell 1 0");
					}
					{
						spnLastYear = new JSpinner();
						spnLastYear.setEnabled(false);
						panel_1.add(spnLastYear, "cell 2 0");
						spnLastYear.setModel(new SpinnerNumberModel(new Integer(2020), null, null, new Integer(1)));
						spnLastYear.setEditor(new JSpinner.NumberEditor(spnLastYear, "#"));
						new SpinnerWrapper(spnLastYear, PrefKey.JSEA_LAST_YEAR, 2020);
					}
					{
						chkAllYears = new JCheckBox("all years in series");
						chkAllYears.setSelected(true);
						chkAllYears.setActionCommand("AllYearsCheckbox");
						chkAllYears.addActionListener(this);
						panel_1.add(chkAllYears, "cell 3 0");
					}
				}
				{
					JLabel lblLagsPriorTo = new JLabel("Lags prior to event:");
					panel.add(lblLagsPriorTo, "cell 0 1");
				}
				{
					HelpTipButton helpTipButton = new HelpTipButton("");
					panel.add(helpTipButton, "cell 1 1");
				}
				{
					spnLagsPrior = new JSpinner();
					new SpinnerWrapper(spnLagsPrior, PrefKey.JSEA_LAGS_PRIOR_TO_EVENT, 6);
					panel.add(spnLagsPrior, "cell 2 1,growx");
					// spnLagsPrior.setModel(new SpinnerNumberModel(6, 1, 100, 1));
					spnLagsPrior.addChangeListener(new ChangeListener() {
						
						@Override
						public void stateChanged(ChangeEvent e) {
						
							segmentationPanel.table.tableModel.clearSegments();
							validateForm();
						}
					});
				}
				{
					JLabel lblSimulationsToRun = new JLabel("Simulations:");
					panel.add(lblSimulationsToRun, "cell 4 1");
				}
				{
					HelpTipButton helpTipButton = new HelpTipButton(
							"Number of simulations to run.  Increasing the number of simulations increases the analysis time.");
					panel.add(helpTipButton, "cell 5 1");
				}
				{
					spnSimulationsToRun = new JSpinner();
					new SpinnerWrapper(spnSimulationsToRun, PrefKey.JSEA_SIMULATION_COUNT, 1000);
					panel.add(spnSimulationsToRun, "cell 6 1");
					spnSimulationsToRun.setModel(new SpinnerNumberModel(1000, 1, 10096, 1));
				}
				{
					JLabel lblLagsFollowingThe = new JLabel("Lags following the event:");
					panel.add(lblLagsFollowingThe, "cell 0 2");
				}
				{
					HelpTipButton helpTipButton = new HelpTipButton("");
					panel.add(helpTipButton, "cell 1 2");
				}
				{
					spnLagsAfter = new JSpinner();
					new SpinnerWrapper(spnLagsAfter, PrefKey.JSEA_LAGS_AFTER_EVENT, 4);
					panel.add(spnLagsAfter, "cell 2 2,growx");
					spnLagsAfter.addChangeListener(new ChangeListener() {
						
						@Override
						public void stateChanged(ChangeEvent e) {
						
							segmentationPanel.table.tableModel.clearSegments();
							validateForm();
						}
					});
				}
				{
					JLabel lblSeedNumber = new JLabel("Seed number:");
					panel.add(lblSeedNumber, "cell 4 2");
				}
				{
					HelpTipButton helpTipButton = new HelpTipButton(
							"The analysis requires a pseudo-random component which is seeded with the seed number (a large integer value).  Running analyses with the same seed number enables produces the same results.  You can leave the seed as the default number unless you specifically want to generate results from a different randomised pool.");
					panel.add(helpTipButton, "cell 5 2");
				}
				{
					spnSeedNumber = new JSpinner();
					new SpinnerWrapper(spnSeedNumber, PrefKey.JSEA_SEED_NUMBER, 30188);
					panel.add(spnSeedNumber, "cell 6 2");
					spnSeedNumber.setModel(new SpinnerNumberModel(30188, 10000, 1000000, 1));
				}
				{
					JLabel lblIncludeIncompleteWindow = new JLabel("Include incomplete epoch:");
					panel.add(lblIncludeIncompleteWindow, "cell 0 3");
				}
				{
					HelpTipButton helpTipButton = new HelpTipButton("");
					panel.add(helpTipButton, "cell 1 3");
				}
				{
					chkIncludeIncompleteWindow = new JCheckBox("");
					new CheckBoxWrapper(chkIncludeIncompleteWindow, PrefKey.JSEA_INCLUDE_INCOMPLETE_WINDOW, false);
					
					panel.add(chkIncludeIncompleteWindow, "cell 2 3");
				}
				{
					JLabel lblPvalue = new JLabel("p-value:");
					panel.add(lblPvalue, "cell 4 3");
				}
				{
					HelpTipButton helpTipButton = new HelpTipButton("The cutoff value to use for statistical significance");
					panel.add(helpTipButton, "cell 5 3,alignx trailing");
				}
				{
					cbxPValue = new JComboBox();
					panel.add(cbxPValue, "cell 6 3");
					cbxPValue.setModel(new DefaultComboBoxModel(new Double[] { 0.05, 0.01, 0.001 }));
				}
			}
			{
				JPanel panel = new JPanel();
				panel.setBorder(new TitledBorder(null, "Chart Options", TitledBorder.LEADING, TitledBorder.TOP, null, null));
				contentPanel.add(panel, "cell 0 2,grow");
				
				panel.setLayout(new MigLayout("", "[right][][grow]", "[][]"));
				{
					JLabel lblTitleOfChart = new JLabel("Title of chart:");
					panel.add(lblTitleOfChart, "cell 0 0,alignx trailing");
				}
				{
					HelpTipButton helpTipButton = new HelpTipButton(
							"Title to use on the chart.  The placeholder {segment} is replaced with the years of the segment being plotted.");
					panel.add(helpTipButton, "cell 1 0,alignx trailing");
				}
				{
					txtChartTitle = new JTextField();
					txtChartTitle.setToolTipText("<html>Title to be displayed on the<br/>" + "chart output");
					new TextComponentWrapper(txtChartTitle, PrefKey.JSEA_CHART_TITLE, "Chart title {segment}");
					panel.add(txtChartTitle, "cell 2 0,growx,aligny top");
					txtChartTitle.setColumns(10);
				}
				{
					JLabel lblYaxisLabel = new JLabel("Continuous series (y-axis) label");
					panel.add(lblYaxisLabel, "cell 0 1,alignx trailing");
				}
				{
					txtYAxisLabel = new JTextField();
					txtYAxisLabel.setToolTipText("<html>Label to be displayed on the<br/> " + "continuous series axis");
					new TextComponentWrapper(txtYAxisLabel, PrefKey.JSEA_YAXIS_LABEL, "Y Axis");
					panel.add(txtYAxisLabel, "cell 2 1,growx");
					txtYAxisLabel.setColumns(10);
				}
			}
			{
				// Segmentation implementation used from FHSampleSize
				segmentationPanel = new SegmentationPanel();
				segmentationPanel.chkSegmentation.setText("Process subset or segments of events?");
				segmentationPanel.chkSegmentation.setActionCommand("SegmentationMode");
				segmentationPanel.chkSegmentation.addActionListener(this);
				contentPanel.add(segmentationPanel, "cell 0 3,grow");
			}
			{
				tabbedPane = new JTabbedPane(JTabbedPane.BOTTOM);
				splitPane.setRightComponent(tabbedPane);
				{
					summaryPanel = new JPanel();
					tabbedPane.addTab("Summary ", Builder.getImageIcon("info.png"), summaryPanel, null);
					summaryPanel.setLayout(new BorderLayout(0, 0));
					{
						scrollPane = new JScrollPane();
						summaryPanel.add(scrollPane);
						{
							txtSummary = new JTextArea();
							txtSummary.setEditable(false);
							scrollPane.setViewportView(txtSummary);
							JMenuItem mntmCopy = new JMenuItem(actionCopy);
							JPopupMenu popup = new JPopupMenu();
							addPopup(scrollPane, popup);
							popup.add(mntmCopy);
						}
					}
				}
				{
					dataPanel = new JPanel();
					tabbedPane.addTab("Data ", Builder.getImageIcon("table.png"), dataPanel, null);
					dataPanel.setLayout(new MigLayout("", "[grow,fill]", "[grow]"));
					{
						JSplitPane splitPaneDataTables = new JSplitPane();
						splitPaneDataTables.setResizeWeight(0.5);
						splitPaneDataTables.setOneTouchExpandable(true);
						splitPaneDataTables.setOrientation(JSplitPane.VERTICAL_SPLIT);
						dataPanel.add(splitPaneDataTables, "cell 0 0,grow");
						{
							JPanel panel = new JPanel();
							splitPaneDataTables.setLeftComponent(panel);
							panel.setBorder(new TitledBorder(null, "Actual key events", TitledBorder.LEADING, TitledBorder.TOP, null, null));
							panel.setLayout(new MigLayout("", "[227.00px,grow,fill]", "[68.00px,grow,fill]"));
							{
								JScrollPane scrollPane = new JScrollPane();
								panel.add(scrollPane, "cell 0 0,grow");
								{
									tblActual = new JXTable();
									adapterActualTable = new JTableSpreadsheetByRowAdapter(tblActual);
									scrollPane.setViewportView(tblActual);
									tblActual.setSortable(false);
									JMenuItem mntmCopy = new JMenuItem(actionCopy);
									JPopupMenu popup = new JPopupMenu();
									addPopup(tblActual, popup);
									popup.add(mntmCopy);
								}
							}
						}
						{
							JPanel panel = new JPanel();
							splitPaneDataTables.setRightComponent(panel);
							panel.setBorder(new TitledBorder(null, "Simulation results", TitledBorder.LEADING, TitledBorder.TOP, null, null));
							panel.setLayout(new MigLayout("", "[grow,fill]", "[grow,fill]"));
							{
								JScrollPane scrollPane = new JScrollPane();
								panel.add(scrollPane, "cell 0 0,grow");
								{
									tblSimulation = new JXTable();
									adapterSimulationTable = new JTableSpreadsheetByRowAdapter(tblSimulation);
									scrollPane.setViewportView(tblSimulation);
									tblSimulation.setSortable(false);
									JMenuItem mntmCopy = new JMenuItem(actionCopy);
									JPopupMenu popup = new JPopupMenu();
									addPopup(tblSimulation, popup);
									popup.add(mntmCopy);
								}
							}
						}
						splitPaneDataTables.setDividerLocation(0.5f);
					}
				}
				{
					chartPanel = new JPanel();
					tabbedPane.addTab("Chart ", Builder.getImageIcon("barchart.png"), chartPanel, null);
					chartPanel.setLayout(new MigLayout("", "[][grow]", "[][grow]"));
					{
						segmentComboBox = new JComboBox();
						segmentComboBox.addItemListener(new ItemListener() {
							
							@Override
							public void itemStateChanged(ItemEvent arg0) {
							
								if (segmentComboBox.getItemCount() > 0)
								{
									barChart = new JSEABarChart(jsea.getChartList().get(segmentComboBox.getSelectedIndex()));
									barChart.setMaximumDrawHeight(MAX_DRAW_HEIGHT);
									barChart.setMaximumDrawWidth(MAX_DRAW_WIDTH);
									chartPanel.removeAll();
									chartPanel.add(plotSegmentLabel, "cell 0 0,alignx center,aligny center");
									chartPanel.add(segmentComboBox, "cell 1 0,growx,aligny center");
									chartPanel.add(barChart, "cell 0 1 2 1,grow");
									chartPanel.revalidate();
									chartPanel.repaint();
								}
							}
						});
						{
							plotSegmentLabel = new JLabel("Plot Segment: ");
							chartPanel.add(plotSegmentLabel, "cell 0 0,alignx center,aligny center");
						}
						chartPanel.add(segmentComboBox, "cell 1 0,growx,aligny center");
					}
				}
			}
		}
		
		pack();
		validateForm();
		setAnalysisAvailable(false);
		setExtendedState(this.getExtendedState() | JFrame.MAXIMIZED_BOTH);
	}
	
	/**
	 * Set up the year range GUI depending on whether the 'all years' checkbox is ticked
	 */
	private void setYearRangeGUI() {
	
		spnFirstYear.setEnabled(!chkAllYears.isSelected());
		spnLastYear.setEnabled(!chkAllYears.isSelected());
		
		if (chronologyYears.size() > 1)
		{
			if (chkAllYears.isSelected())
			{
				this.spnFirstYear.setValue(chronologyYears.get(0));
				this.spnLastYear.setValue(chronologyYears.get(chronologyYears.size() - 1));
			}
		}
	}
	
	/**
	 * Populates the segmentComboBox with the segments from the analysis.
	 * 
	 * @param tableModel
	 */
	private void populateSegmentComboBoxAndDrawChart(SegmentTableModel tableModel) {
	
		segmentComboBox.removeAllItems();
		
		if (segmentationPanel.chkSegmentation.isSelected())
		{
			// Populates the segmentComboBox according to the segments from the table
			/*
			 * for (int i = 0; i < tableModel.getSegments().size(); i++) { if
			 * (!segmentationPanel.table.tableModel.getSegment(i).isBadSegment()) {
			 * segmentComboBox.addItem(tableModel.getSegment(i).getFirstYear() + " - " + tableModel.getSegment(i).getLastYear()); } }
			 */
			
			for (int i = 0; i < jsea.getChartList().size(); i++)
			{
				BarChartParametersModel ch = jsea.getChartList().get(i);
				segmentComboBox.addItem(ch.getFirstYear() + " - " + ch.getLastYear());
			}
			
			// Redraws the chart via the segmentComboBox's itemListener
			segmentComboBox.setSelectedIndex(0);
			segmentComboBox.setEnabled(true);
		}
		else
		{
			// Redraws the chart via the default index of the chartList
			barChart = new JSEABarChart(jsea.getChartList().get(0));
			barChart.setMaximumDrawHeight(MAX_DRAW_HEIGHT);
			barChart.setMaximumDrawWidth(MAX_DRAW_WIDTH);
			chartPanel.removeAll();
			chartPanel.add(plotSegmentLabel, "cell 0 0,alignx center,aligny center");
			chartPanel.add(segmentComboBox, "cell 1 0,growx,aligny center");
			chartPanel.add(barChart, "cell 0 1 2 1,grow");
			chartPanel.revalidate();
			chartPanel.repaint();
			
			// Clear the segmentComboBox since there are no segments to choose from
			segmentComboBox.setEnabled(false);
			BarChartParametersModel ch = jsea.getChartList().get(0);
			segmentComboBox.addItem(ch.getFirstYear() + " - " + ch.getLastYear());
			// segmentComboBox.addItem(jsea.getFirstYearOfProcess() + " - " + jsea.getLastYearOfProcess());
		}
		
		segmentComboBox.revalidate();
		segmentComboBox.repaint();
	}
	
	@Override
	public void actionPerformed(ActionEvent event) {
	
		if (event.getActionCommand().equals("SegmentationMode"))
		{
			Boolean $success = this.validateDataFiles();
			
			if ($success == null)
			{
				log.debug("Files not set yet");
				return;
			}
			else if ($success == false)
			{
				log.debug("Invalid file ranges");
				return;
			}
			
			if (segmentationPanel.chkSegmentation.isSelected() && chronologyYears.size() > 1)
			{
				segmentationPanel.table.setEarliestYear(Integer.parseInt(this.firstPossibleYear.toString()));
				segmentationPanel.table.setLatestYear(Integer.parseInt(this.lastPossibleYear.toString()));
			}
			else
			{
				// cannot perform segmentation if there are less than 2 years in the chronology
				segmentationPanel.chkSegmentation.setSelected(false);
			}
		}
		else if (event.getActionCommand().equals("AllYearsCheckbox"))
		{
			setYearRangeGUI();
		}
		else if (event.getActionCommand().equals("TimeSeriesFileBrowse"))
		{
			String lastVisitedFolder = App.prefs.getPref(PrefKey.PREF_LAST_READ_TIME_SERIES_FOLDER,
					App.prefs.getPref(PrefKey.PREF_LAST_READ_FOLDER, null));
			JFileChooser fc;
			
			if (lastVisitedFolder != null)
			{
				fc = new JFileChooser(lastVisitedFolder);
			}
			else
			{
				fc = new JFileChooser();
			}
			
			fc.setMultiSelectionEnabled(false);
			fc.setDialogTitle("Open file");
			
			fc.addChoosableFileFilter(new TXTFileFilter());
			fc.setAcceptAllFileFilterUsed(false);
			fc.setFileFilter(new CSVFileFilter());
			
			int returnVal = fc.showOpenDialog(this);
			if (returnVal == JFileChooser.APPROVE_OPTION)
			{
				txtTimeSeriesFile.setText(fc.getSelectedFile().getAbsolutePath());
				txtwrapper.updatePref();
				
				App.prefs.setPref(PrefKey.PREF_LAST_READ_TIME_SERIES_FOLDER, fc.getSelectedFile().getPath());
				
				if (parseTimeSeriesFile())
				{
					setYearRangeGUI();
				}
				else
				{
					txtTimeSeriesFile.setText("");
				}
				
				validateForm();
			}
		}
		else if (event.getActionCommand().equals("EventListFileBrowse"))
		{
			String lastVisitedFolder = App.prefs.getPref(PrefKey.PREF_LAST_READ_EVENT_LIST_FOLDER,
					App.prefs.getPref(PrefKey.PREF_LAST_READ_FOLDER, null));
			JFileChooser fc;
			
			if (lastVisitedFolder != null)
			{
				fc = new JFileChooser(lastVisitedFolder);
			}
			else
			{
				fc = new JFileChooser();
			}
			
			fc.setMultiSelectionEnabled(false);
			fc.setDialogTitle("Open file");
			
			int returnVal = fc.showOpenDialog(this);
			if (returnVal == JFileChooser.APPROVE_OPTION)
			{
				txtEventListFile.setText(fc.getSelectedFile().getAbsolutePath());
				App.prefs.setPref(PrefKey.PREF_LAST_READ_EVENT_LIST_FOLDER, fc.getSelectedFile().getPath());
				parseEventListFile();
				
				validateForm();
			}
		}
	}
	
	/**
	 * Read a text file and determine what the delimiter is. Checks to see which delimiter provides the most lines with the specified number
	 * of items
	 * 
	 * @param filename
	 * @param countOfGoodItems
	 * @return
	 */
	private char getDelimiter(String filename, Integer countOfGoodItems) {
	
		Integer tabGoodLines = getGoodLineCount(filename, '\t', countOfGoodItems);
		Integer commaGoodLines = getGoodLineCount(txtTimeSeriesFile.getText(), ',', countOfGoodItems);
		Integer spaceGoodLines = getGoodLineCount(txtTimeSeriesFile.getText(), ' ', countOfGoodItems);
		
		if (tabGoodLines > commaGoodLines && tabGoodLines > spaceGoodLines)
		{
			return '\t';
		}
		else if (commaGoodLines > tabGoodLines && commaGoodLines > spaceGoodLines)
		{
			return ',';
		}
		else if (spaceGoodLines > tabGoodLines && spaceGoodLines > commaGoodLines)
		{
			return ' ';
		}
		
		return '\t';
		
	}
	
	/**
	 * Parse a delimited using the specified filename and delimiter are return how many lines contain the correct number of items
	 * 
	 * @param filename
	 * @param delimiter
	 * @param countOfGoodItems
	 * @return
	 */
	private Integer getGoodLineCount(String filename, char delimiter, Integer countOfGoodItems) {
	
		CSVReader reader;
		Integer goodLines = 0;
		try
		{
			reader = new CSVReader(new FileReader(filename), delimiter);
			ArrayList fileContents = (ArrayList) reader.readAll();
			reader.close();
			
			for (Object line : fileContents)
			{
				
				String[] items = (String[]) line;
				if (items == null)
					continue;
				if (items[0].startsWith("*"))
					continue;
				if (items.length == countOfGoodItems)
				{
					goodLines++;
				}
			}
			
		}
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		
		return goodLines;
	}
	
	/**
	 * Parse the events file to extract years with events
	 * 
	 * @return
	 */
	public Boolean parseEventListFile() {
	
		File f = new File(this.txtEventListFile.getText());
		if (!f.exists())
		{
			log.error("Event list file does not exist");
			return false;
		}
		
		FileReader fr = null;
		BufferedReader br = null;
		String record = null;
		
		try
		{
			fr = new FileReader(txtEventListFile.getText());
			br = new BufferedReader(fr);
			
			while ((record = br.readLine()) != null)
			{
				if (!record.contains("*"))
				{
					
					try
					{
						events.add(new Integer(record));
					}
					catch (NumberFormatException e)
					{
						JOptionPane
								.showMessageDialog(
										this,
										"The event file must contain a list of integer values after any comment lines.\nPlease check the file and try again.",
										"Warning", JOptionPane.ERROR_MESSAGE);
						txtEventListFile.setText("");
						fr.close();
						br.close();
						return false;
					}
					
				}
			}
		}
		catch (IOException ex)
		{
			JOptionPane.showMessageDialog(this, "Error reading events file.\nPlease check the file and try again.", "Warning",
					JOptionPane.ERROR_MESSAGE);
			txtEventListFile.setText("");
			
			return false;
		}
		finally
		{
			try
			{
				fr.close();
				br.close();
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
			
		}
		
		Collections.sort(events);
		
		return true;
	}
	
	/**
	 * Parse the specified time series file
	 */
	public Boolean parseTimeSeriesFile() {
	
		chronologyYears = new ArrayList<Integer>();
		chronologyActual = new ArrayList<Double>();
		;
		
		File f = new File(this.txtTimeSeriesFile.getText());
		if (!f.exists())
		{
			log.error("Event list file does not exist");
			return false;
		}
		
		CSVReader reader;
		try
		{
			String filename = txtTimeSeriesFile.getText();
			char delimiter = getDelimiter(filename, 2);
			reader = new CSVReader(new FileReader(filename), delimiter);
			ArrayList fileContents = (ArrayList) reader.readAll();
			reader.close();
			
			int lineNumber = 0;
			
			for (Object line : fileContents)
			{
				lineNumber++;
				String[] items = (String[]) line;
				if (items == null)
					continue;
				if (items[0].startsWith("*"))
					continue;
				if (items.length == 1)
				{
					JOptionPane.showMessageDialog(this, "Invalid number of items on line " + lineNumber
							+ ". There should be only 2 items per line, whereas " + items.length + " was found.", "Invalid file",
							JOptionPane.ERROR_MESSAGE);
					return false;
				}
				if (items.length != 2)
				{
					JOptionPane.showMessageDialog(this, "Invalid number of items on line " + lineNumber
							+ ". There should be only 2 items per line, whereas " + items.length + " were found.", "Invalid file",
							JOptionPane.ERROR_MESSAGE);
					return false;
				}
				
				try
				{
					Integer year = Integer.parseInt(items[0].trim());
					
					if (chronologyYears.size() > 0)
					{
						if (!chronologyYears.get(chronologyYears.size() - 1).equals(year - 1))
						{
							JOptionPane.showMessageDialog(this, "Invalid year value on line number " + lineNumber
									+ ". Years must be sequential.", "Invalid file", JOptionPane.ERROR_MESSAGE);
							return false;
						}
					}
					chronologyYears.add(year);
				}
				catch (NumberFormatException e)
				{
					JOptionPane.showMessageDialog(this, "Invalid year value :" + StringUtils.rightPadWithTrim(items[0], 5)
							+ "' on line number " + lineNumber, "Invalid file", JOptionPane.ERROR_MESSAGE);
					return false;
				}
				
				try
				{
					Double value = Double.parseDouble(items[1].trim());
					chronologyActual.add(value);
				}
				catch (NumberFormatException e)
				{
					JOptionPane.showMessageDialog(this, "Invalid data value '" + StringUtils.rightPadWithTrim(items[0], 5)
							+ "' on line number " + lineNumber + ". Value must be real number", "Invalid file", JOptionPane.ERROR_MESSAGE);
					return false;
				}
			}
			
		}
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		
		return true;
	}
	
	private Boolean validateDataFiles() {
	
		if (events == null || chronologyActual == null || chronologyYears == null || events.size() == 0 || chronologyYears.size() == 0)
		{
			this.firstPossibleYear = null;
			this.lastPossibleYear = null;
			// JOptionPane.showMessageDialog(this, "Data files not ready yet");
			
			this.segmentationPanel.setEnabled(false);
			return null;
		}
		
		SafeIntYear firstEventYear = new SafeIntYear(events.get(0) - App.prefs.getIntPref(PrefKey.JSEA_LAGS_PRIOR_TO_EVENT, 6));
		SafeIntYear lastEventYear = new SafeIntYear(events.get(events.size() - 1) + App.prefs.getIntPref(PrefKey.JSEA_LAGS_AFTER_EVENT, 4));
		
		SafeIntYear firstChronologyYear = new SafeIntYear(chronologyYears.get(0));
		SafeIntYear lastChronologyYear = new SafeIntYear(chronologyYears.get(chronologyYears.size() - 1));
		
		YearRange eventRange = new YearRange(firstEventYear, lastEventYear);
		YearRange chronologyRange = new YearRange(firstChronologyYear, lastChronologyYear);
		
		if (eventRange.overlap(chronologyRange) == 0)
		{
			log.error("No overlap");
			this.firstPossibleYear = null;
			this.lastPossibleYear = null;
			JOptionPane.showMessageDialog(this, "There is no overlap between the continous and event data.");
			this.segmentationPanel.setEnabled(false);
			return false;
		}
		else if (eventRange.overlap(chronologyRange) < 30)
		{
			log.error("Range overlap must be > 30");
			this.firstPossibleYear = null;
			this.lastPossibleYear = null;
			JOptionPane.showMessageDialog(this, "The overlap between continuous and event data must be >30 years.");
			this.segmentationPanel.setEnabled(false);
			return false;
		}
		
		YearRange intersect = eventRange.intersection(chronologyRange);
		
		this.firstPossibleYear = intersect.getStart();
		this.lastPossibleYear = intersect.getEnd();
		
		this.segmentationPanel.setEnabled(true);
		return true;
	}
	
	/**
	 * Sets the value of the scroll bar to zero.
	 */
	private void setScrollBarToTop() {
	
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			
			@Override
			public void run() {
			
				scrollPane.getHorizontalScrollBar().setValue(0);
				scrollPane.getVerticalScrollBar().setValue(0);
			}
		});
	}
	
	/**
	 * Reset parameters to the default values
	 */
	public void setToDefault() {
	
		txtSummary.setText("");
		tblActual = new JXTable();
		tblSimulation = new JXTable();
		chartPanel.removeAll();
		jsea = null;
		chronologyActual = new ArrayList<Double>();
		chronologyYears = new ArrayList<Integer>();
		events = new ArrayList<Integer>();
		
		setAnalysisAvailable(false);
		
		txtTimeSeriesFile.setText("");
		txtEventListFile.setText("");
		
		App.prefs.setPref(PrefKey.JSEA_CHART_TITLE, "Chart title {segment}");
		txtChartTitle.setText("Chart title {segment}");
		
		App.prefs.setPref(PrefKey.JSEA_YAXIS_LABEL, "Y Axis");
		txtYAxisLabel.setText("Y Axis");
		
		spnLagsPrior.setValue(6);
		spnLagsAfter.setValue(4);
		chkIncludeIncompleteWindow.setSelected(false);
		spnSimulationsToRun.setValue(1000);
		spnSeedNumber.setValue(30288);
		cbxPValue.setSelectedIndex(0);
		
		segmentationPanel.chkSegmentation.setSelected(false);
		
		validateForm();
	}
	
	/**
	 * Check whether we have all the info we need to process
	 */
	private void validateForm() {
	
		log.debug("Validating form");
		
		Boolean areDataFilesValid = validateDataFiles();
		if (areDataFilesValid != null && areDataFilesValid == true)
		{
			
			if (chronologyActual.size() > 0 && chronologyYears.size() > 0 && events.size() > 0 && txtTimeSeriesFile.getText() != null
					&& txtEventListFile.getText() != null)
			{
				actionRun.setEnabled(true);
			}
			else
			{
				actionRun.setEnabled(false);
			}
		}
		else
		{
			actionRun.setEnabled(false);
		}
	}
	
	/**
	 * Show popup menu
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
	
}
