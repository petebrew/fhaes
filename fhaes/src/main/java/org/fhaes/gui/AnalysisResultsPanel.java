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
import java.awt.CardLayout;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Date;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.swing.ImageIcon;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTree;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import org.apache.commons.io.FileUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.fhaes.analysis.FHMatrix;
import org.fhaes.enums.AnalysisType;
import org.fhaes.enums.EventTypeToProcess;
import org.fhaes.enums.FHAESResult;
import org.fhaes.enums.FireFilterType;
import org.fhaes.filefilter.CSVFileFilter;
import org.fhaes.model.FHAESCategoryTreeNode;
import org.fhaes.model.FHAESResultTreeNode;
import org.fhaes.preferences.App;
import org.fhaes.preferences.FHAESPreferences.PrefKey;
import org.fhaes.util.Builder;
import org.fhaes.util.FHAESAction;
import org.fhaes.util.IOUtil;
import org.fhaes.util.JTableSpreadsheetByRowAdapter;
import org.fhaes.util.Platform;
import org.jdesktop.swingx.JXTable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tridas.io.util.IOUtils;

import com.ibm.icu.text.SimpleDateFormat;

/**
 * AnalysisResultsPanel Class.
 */
public class AnalysisResultsPanel extends JPanel implements TreeSelectionListener {
	
	private static final long serialVersionUID = 1L;
	protected JXTable table;
	
	private static Integer excelRowColLimit = 256;
	private static final Logger log = LoggerFactory.getLogger(AnalysisResultsPanel.class);
	protected JTableSpreadsheetByRowAdapter adapter;
	
	private JScrollPane scrollPane;
	private DefaultTreeModel treeModel;
	private DefaultMutableTreeNode rootNode;
	private DefaultMutableTreeNode categorySimMatrices;
	private DefaultMutableTreeNode categoryDisSimMatrices;
	private DefaultMutableTreeNode categoryInterval;
	private DefaultMutableTreeNode categorySeasonality;
	private DefaultMutableTreeNode categoryBinaryMatrices;
	private DefaultMutableTreeNode categoryBinarySummaryMatrices;
	private DefaultMutableTreeNode categoryGeneral;
	
	private FHAESResultTreeNode itemJaccard;
	private FHAESResultTreeNode itemCohen;
	private FHAESResultTreeNode itemJaccardD;
	private FHAESResultTreeNode itemCohenD;
	private FHAESResultTreeNode itemIntervalSummary;
	private FHAESResultTreeNode itemExceedence;
	private FHAESResultTreeNode itemSeasonalitySummary;
	private FHAESResultTreeNode itemBin00;
	private FHAESResultTreeNode itemBin01;
	private FHAESResultTreeNode itemBin10;
	private FHAESResultTreeNode itemBin11;
	private FHAESResultTreeNode itemBinSum;
	private FHAESResultTreeNode itemBinSiteSummary;
	private FHAESResultTreeNode itemBinTreeSummary;
	private FHAESResultTreeNode itemNTP;
	private FHAESResultTreeNode itemGeneralSummary;
	private FHAESResultTreeNode itemSingleFileSummary;
	private FHAESResultTreeNode itemSingleEventSummary;
	
	private DefaultMutableTreeNode previouslySelectedNode;
	protected GoldFishPanel goldFishPanel;
	
	private JTree treeResults;
	private JSplitPane splitPane;
	
	private JPanel cards;
	private PickResultPanel pickResultPanel;
	private RunAnalysisPanel runAnalysisPanel;
	private JPanel emptyPanel = new JPanel();
	private JPanel panelResult;
	private CardLayout cl;
	
	private DefaultTableModel seasonalitySummaryModel = null;
	private DefaultTableModel intervalsExceedenceModel = null;
	private DefaultTableModel intervalsSummaryModel = null;
	private DefaultTableModel bin00Model = null;
	private DefaultTableModel bin10Model = null;
	private DefaultTableModel bin01Model = null;
	private DefaultTableModel bin11Model = null;
	private DefaultTableModel binSumModel = null;
	private DefaultTableModel DSCOHModel = null;
	private DefaultTableModel DSJACModel = null;
	private DefaultTableModel SCOHModel = null;
	private DefaultTableModel siteSummaryModel = null;
	private DefaultTableModel SJACModel = null;
	private DefaultTableModel NTPModel = null;
	private DefaultTableModel treeSummaryModel = null;
	private DefaultTableModel generalSummaryModel = null;
	private DefaultTableModel singleFileSummaryModel = null;
	private DefaultTableModel singleEventSummaryModel = null;
	private FHMatrix fhm;
	
	protected File seasonalitySummaryFile = null;
	protected File intervalsExceedenceFile = null;
	protected File intervalsSummaryFile = null;
	protected File bin00File = null;
	protected File bin10File = null;
	protected File bin01File = null;
	protected File bin11File = null;
	protected File binSumFile = null;
	protected File DSCOHFile = null;
	protected File DSJACFile = null;
	protected File SCOHFile = null;
	protected File siteSummaryFile = null;
	protected File SJACFile = null;
	protected File NTPFile = null;
	protected File treeSummaryFile = null;
	protected File generalSummaryFile = null;
	protected File singleFileSummaryFile = null;
	protected File singleEventSummaryFile = null;
	
	final static String RESULTSPANEL = "Results panel";
	final static String PICKRESULTPANEL = "Pick result panel";
	final static String RUNANALYSIS = "Run analysis panel";
	final static String EMPTYPANEL = "Empty panel";
	
	private CellStyle doubleStyle;
	private JSplitPane splitPaneResult;
	
	/**
	 * Create the panel.
	 */
	public AnalysisResultsPanel() {
	
		initGUI();
	}
	
	/**
	 * TODO
	 */
	public void repaintTree() {
	
		treeResults.repaint();
	}
	
	/**
	 * TODO
	 * 
	 * @param fhm
	 */
	public void setFHMatrix(FHMatrix fhm) {
	
		this.fhm = fhm;
	}
	
	/**
	 * TODO
	 * 
	 * @param f
	 */
	public void setSingleFileSummaryModel(DefaultTableModel f) {
	
		singleFileSummaryModel = f;
		if (f != null)
		{
			setResultsEnabled(true);
			setupTable(true);
		}
		setSingleFileSummaryStatus();
	}
	
	/**
	 * TODO
	 * 
	 * @param f
	 */
	public void setSingleEventSummaryModel(DefaultTableModel f) {
	
		singleEventSummaryModel = f;
		if (f != null)
		{
			setResultsEnabled(true);
			setupTable(true);
		}
		setSingleEventSummaryStatus();
	}
	
	/**
	 * Enabled/Disable SingleFileSummary item depending on whether there is a model or not
	 */
	public void setSingleFileSummaryStatus() {
	
		itemSingleFileSummary.setEnabled(singleFileSummaryModel != null);
	}
	
	/**
	 * Enabled/Disable SingleFileSummary item depending on whether there is a model or not
	 */
	public void setSingleEventSummaryStatus() {
	
		itemSingleEventSummary.setEnabled(singleEventSummaryModel != null);
	}
	
	/**
	 * TODO
	 * 
	 * @param f
	 */
	public void setSeasonalityModel(DefaultTableModel f) {
	
		seasonalitySummaryModel = f;
		if (f != null)
			setResultsEnabled(true);
		setSeasonalitySummaryStatus();
	}
	
	/**
	 * Enabled/Disable SeasonalitySummary item depending on whether there is a model or not
	 */
	public void setSeasonalitySummaryStatus() {
	
		itemSeasonalitySummary.setEnabled(seasonalitySummaryModel != null);
	}
	
	/**
	 * TODO
	 * 
	 * @param f
	 */
	public void setIntervalsSummaryModel(DefaultTableModel f) {
	
		intervalsSummaryModel = f;
		if (f != null)
			setResultsEnabled(true);
		setIntervalsSummaryStatus();
	}
	
	/**
	 * Enabled/Disable IntervalSummary item depending on whether there is a model or not
	 */
	public void setIntervalsSummaryStatus() {
	
		itemIntervalSummary.setEnabled(intervalsSummaryModel != null);
	}
	
	/**
	 * TODO
	 * 
	 * @param f
	 */
	public void setIntervalsExceedenceModel(DefaultTableModel f) {
	
		intervalsExceedenceModel = f;
		if (f != null)
			setResultsEnabled(true);
		setIntervalsExceedenceStatus();
	}
	
	/**
	 * Enabled/Disable IntervalsExceedence item depending on whether there is a model or not
	 */
	public void setIntervalsExceedenceStatus() {
	
		this.itemExceedence.setEnabled(this.intervalsExceedenceModel != null);
	}
	
	/**
	 * TODO
	 * 
	 * @param f
	 */
	public void setBin00Model(DefaultTableModel f) {
	
		this.bin00Model = f;
		if (f != null)
			setResultsEnabled(true);
		setBin00Status();
	}
	
	/**
	 * Enabled/Disable Binary00 item depending on whether there is a model or not
	 */
	public void setBin00Status() {
	
		this.itemBin00.setEnabled(this.bin00Model != null);
	}
	
	/**
	 * TODO
	 * 
	 * @param f
	 */
	public void setBin01Model(DefaultTableModel f) {
	
		this.bin01Model = f;
		if (f != null)
			setResultsEnabled(true);
		setBin01Status();
	}
	
	/**
	 * Enabled/Disable Binary01 item depending on whether there is a model or not
	 */
	public void setBin01Status() {
	
		this.itemBin01.setEnabled(this.bin01Model != null);
	}
	
	/**
	 * TODO
	 * 
	 * @param f
	 */
	public void setBin10Model(DefaultTableModel f) {
	
		this.bin10Model = f;
		if (f != null)
			setResultsEnabled(true);
		setBin10Status();
	}
	
	/**
	 * Enabled/Disable Binary10 item depending on whether there is a model or not
	 */
	public void setBin10Status() {
	
		this.itemBin10.setEnabled(this.bin10Model != null);
	}
	
	/**
	 * TODO
	 * 
	 * @param f
	 */
	public void setBin11Model(DefaultTableModel f) {
	
		this.bin11Model = f;
		if (f != null)
			setResultsEnabled(true);
		
		setBin11Status();
	}
	
	/**
	 * Enabled/Disable Binary11 item depending on whether there is a model or not
	 */
	public void setBin11Status() {
	
		this.itemBin11.setEnabled(this.bin11Model != null);
	}
	
	/**
	 * TODO
	 * 
	 * @param f
	 */
	public void setBinSumModel(DefaultTableModel f) {
	
		this.binSumModel = f;
		if (f != null)
			setResultsEnabled(true);
		
		setBinSumStatus();
	}
	
	/**
	 * Enabled/Disable BinarySum item depending on whether there is a model or not
	 */
	public void setBinSumStatus() {
	
		this.itemBinSum.setEnabled(this.bin11Model != null);
	}
	
	/**
	 * TODO
	 * 
	 * @param f
	 */
	public void setDSCOHModel(DefaultTableModel f) {
	
		this.DSCOHModel = f;
		if (f != null)
			setResultsEnabled(true);
		setDSCOHStatus();
	}
	
	/**
	 * Enabled/Disable Cohen Dissimilarity item depending on whether there is a model or not
	 */
	public void setDSCOHStatus() {
	
		this.itemCohenD.setEnabled(this.DSCOHModel != null);
	}
	
	/**
	 * TODO
	 * 
	 * @param f
	 */
	public void setDSJACModel(DefaultTableModel f) {
	
		this.DSJACModel = f;
		if (f != null)
			setResultsEnabled(true);
		setDSCOHStatus();
	}
	
	/**
	 * Enabled/Disable Jaccard Dissimilarity item depending on whether there is a model or not
	 */
	public void setDSJACStatus() {
	
		this.itemJaccardD.setEnabled(this.DSJACModel != null);
	}
	
	/**
	 * TODO
	 * 
	 * @param f
	 */
	public void setSCOHModel(DefaultTableModel f) {
	
		this.SCOHModel = f;
		if (f != null)
			setResultsEnabled(true);
		setSCOHStatus();
	}
	
	/**
	 * Enabled/Disable Cohen similarity item depending on whether there is a model or not
	 */
	public void setSCOHStatus() {
	
		this.itemCohen.setEnabled(this.SCOHModel != null);
	}
	
	/**
	 * TODO
	 * 
	 * @param f
	 */
	public void setSJACModel(DefaultTableModel f) {
	
		this.SJACModel = f;
		if (f != null)
			setResultsEnabled(true);
		setSJACStatus();
	}
	
	/**
	 * Enabled/Disable Jaccard similarity item depending on whether there is a model or not
	 */
	public void setSJACStatus() {
	
		this.itemJaccard.setEnabled(this.SJACModel != null);
	}
	
	/**
	 * TODO
	 * 
	 * @param f
	 */
	public void setNTPModel(DefaultTableModel f) {
	
		this.NTPModel = f;
		if (f != null)
			setResultsEnabled(true);
		setNTPStatus();
	}
	
	/**
	 * Enabled/Disable NTP item depending on whether there is a model or not
	 */
	public void setNTPStatus() {
	
		this.itemNTP.setEnabled(this.NTPModel != null);
	}
	
	/**
	 * TODO
	 * 
	 * @param f
	 */
	public void setGeneralSummaryModel(DefaultTableModel f) {
	
		this.generalSummaryModel = f;
		if (f != null)
			setResultsEnabled(true);
		setGeneralSummaryStatus();
	}
	
	/**
	 * Enabled/Disable GeneralSummary item depending on whether there is a model or not
	 */
	public void setGeneralSummaryStatus() {
	
		this.itemGeneralSummary.setEnabled(generalSummaryModel != null);
	}
	
	/**
	 * TODO
	 * 
	 * @param f
	 */
	public void setSiteSummaryModel(DefaultTableModel f) {
	
		this.siteSummaryModel = f;
		if (f != null)
			setResultsEnabled(true);
		setSiteSummaryStatus();
	}
	
	/**
	 * Enabled/Disable Site Summary item depending on whether there is a model or not
	 */
	public void setSiteSummaryStatus() {
	
		this.itemBinSiteSummary.setEnabled(siteSummaryModel != null);
	}
	
	/**
	 * TODO
	 * 
	 * @param f
	 */
	public void setTreeSummaryModel(DefaultTableModel f) {
	
		this.treeSummaryModel = f;
		if (f != null)
			setResultsEnabled(true);
		setTreeSummaryStatus();
	}
	
	/**
	 * Enabled/Disable Tree Summary item depending on whether there is a model or not
	 */
	public void setTreeSummaryStatus() {
	
		this.itemBinTreeSummary.setEnabled(treeSummaryModel != null);
	}
	
	/**
	 * TODO
	 * 
	 * @return
	 */
	public FHMatrix getFHMatrix() {
	
		return fhm;
	}
	
	/**
	 * Set up the AnalysisResults GUI
	 */
	private void initGUI() {
	
		setLayout(new BorderLayout(0, 0));
		if (Platform.isOSX())
			setBackground(MainWindow.MAC_BACKGROUND_COLOR);
		
		ImageIcon iconMultipleTables = Builder.getImageIcon("multipletables16.png");
		ImageIcon iconTable = Builder.getImageIcon("table16.png");
		
		// ImageIcon iconChart = Builder.getImageIcon("chart16.png");
		
		// Categories
		rootNode = new FHAESCategoryTreeNode("FHAES analysis results");
		categoryGeneral = new FHAESCategoryTreeNode("Descriptive summaries", Builder.getImageIcon("interval16.png"));
		categoryInterval = new FHAESCategoryTreeNode("Interval analysis", Builder.getImageIcon("interval16.png"));
		categorySeasonality = new FHAESCategoryTreeNode("Seasonality", Builder.getImageIcon("seasonality16.png"));
		categoryBinarySummaryMatrices = new FHAESCategoryTreeNode("Binary summary matrices", Builder.getImageIcon("matrix16.png"));
		categoryBinaryMatrices = new FHAESCategoryTreeNode("Binary comparison matrices", Builder.getImageIcon("matrix16.png"));
		categorySimMatrices = new FHAESCategoryTreeNode("Similarity matrices", Builder.getImageIcon("matrix16.png"));
		categoryDisSimMatrices = new FHAESCategoryTreeNode("Dissimilarity matrices", Builder.getImageIcon("matrix16.png"));
		
		// Menu actions
		
		// Results
		
		itemJaccard = new FHAESResultTreeNode(FHAESResult.JACCARD_SIMILARITY_MATRIX, iconMultipleTables);
		itemJaccard.addAction(new FHAESAction("Save to CSV", "formatcsv.png") {
			
			private static final long serialVersionUID = 1L;
			
			@Override
			public void actionPerformed(ActionEvent event) {
			
				saveFileToDisk(SJACFile, new CSVFileFilter());
			}
		});
		
		itemCohen = new FHAESResultTreeNode(FHAESResult.COHEN_SIMILARITITY_MATRIX, iconMultipleTables);
		itemCohen.addAction(new FHAESAction("Save to CSV", "formatcsv.png") {
			
			private static final long serialVersionUID = 1L;
			
			@Override
			public void actionPerformed(ActionEvent event) {
			
				saveFileToDisk(SCOHFile, new CSVFileFilter());
			}
		});
		
		itemJaccardD = new FHAESResultTreeNode(FHAESResult.JACCARD_SIMILARITY_MATRIX_D, iconMultipleTables);
		itemJaccardD.addAction(new FHAESAction("Save to CSV", "formatcsv.png") {
			
			private static final long serialVersionUID = 1L;
			
			@Override
			public void actionPerformed(ActionEvent event) {
			
				saveFileToDisk(DSJACFile, new CSVFileFilter());
			}
		});
		
		itemCohenD = new FHAESResultTreeNode(FHAESResult.COHEN_SIMILARITITY_MATRIX_D, iconMultipleTables);
		itemCohenD.addAction(new FHAESAction("Save to CSV", "formatcsv.png") {
			
			private static final long serialVersionUID = 1L;
			
			@Override
			public void actionPerformed(ActionEvent event) {
			
				saveFileToDisk(DSCOHFile, new CSVFileFilter());
			}
		});
		
		itemIntervalSummary = new FHAESResultTreeNode(FHAESResult.INTERVAL_SUMMARY, iconMultipleTables);
		itemIntervalSummary.addAction(new FHAESAction("Save to CSV", "formatcsv.png") {
			
			private static final long serialVersionUID = 1L;
			
			@Override
			public void actionPerformed(ActionEvent event) {
			
				saveFileToDisk(intervalsSummaryFile, new CSVFileFilter());
			}
		});
		
		itemExceedence = new FHAESResultTreeNode(FHAESResult.INTERVAL_EXCEEDENCE_TABLE, iconMultipleTables);
		itemExceedence.addAction(new FHAESAction("Save to CSV", "formatcsv.png") {
			
			private static final long serialVersionUID = 1L;
			
			@Override
			public void actionPerformed(ActionEvent event) {
			
				saveFileToDisk(intervalsExceedenceFile, new CSVFileFilter());
			}
		});
		
		itemSeasonalitySummary = new FHAESResultTreeNode(FHAESResult.SEASONALITY_SUMMARY, iconMultipleTables);
		itemSeasonalitySummary.addAction(new FHAESAction("Save to CSV", "formatcsv.png") {
			
			private static final long serialVersionUID = 1L;
			
			@Override
			public void actionPerformed(ActionEvent event) {
			
				saveFileToDisk(seasonalitySummaryFile, new CSVFileFilter());
			}
		});
		
		itemBin00 = new FHAESResultTreeNode(FHAESResult.BINARY_MATRIX_00, iconMultipleTables);
		itemBin00.addAction(new FHAESAction("Save to CSV", "formatcsv.png") {
			
			private static final long serialVersionUID = 1L;
			
			@Override
			public void actionPerformed(ActionEvent event) {
			
				saveFileToDisk(bin00File, new CSVFileFilter());
			}
		});
		
		itemBin01 = new FHAESResultTreeNode(FHAESResult.BINARY_MATRIX_01, iconMultipleTables);
		itemBin01.addAction(new FHAESAction("Save to CSV", "formatcsv.png") {
			
			private static final long serialVersionUID = 1L;
			
			@Override
			public void actionPerformed(ActionEvent event) {
			
				saveFileToDisk(bin01File, new CSVFileFilter());
			}
		});
		
		itemBin10 = new FHAESResultTreeNode(FHAESResult.BINARY_MATRIX_10, iconMultipleTables);
		itemBin10.addAction(new FHAESAction("Save to CSV", "formatcsv.png") {
			
			private static final long serialVersionUID = 1L;
			
			@Override
			public void actionPerformed(ActionEvent event) {
			
				saveFileToDisk(bin10File, new CSVFileFilter());
			}
		});
		
		itemBin11 = new FHAESResultTreeNode(FHAESResult.BINARY_MATRIX_11, iconMultipleTables);
		itemBin11.addAction(new FHAESAction("Save to CSV", "formatcsv.png") {
			
			private static final long serialVersionUID = 1L;
			
			@Override
			public void actionPerformed(ActionEvent event) {
			
				saveFileToDisk(bin11File, new CSVFileFilter());
			}
		});
		
		itemBinSum = new FHAESResultTreeNode(FHAESResult.BINARY_MATRIX_SUM, iconMultipleTables);
		itemBinSum.addAction(new FHAESAction("Save to CSV", "formatcsv.png") {
			
			private static final long serialVersionUID = 1L;
			
			@Override
			public void actionPerformed(ActionEvent event) {
			
				saveFileToDisk(binSumFile, new CSVFileFilter());
			}
			
		});
		itemBinSiteSummary = new FHAESResultTreeNode(FHAESResult.BINARY_MATRIX_SITE, iconMultipleTables);
		itemBinSiteSummary.addAction(new FHAESAction("Save to CSV", "formatcsv.png") {
			
			private static final long serialVersionUID = 1L;
			
			@Override
			public void actionPerformed(ActionEvent event) {
			
				saveFileToDisk(siteSummaryFile, new CSVFileFilter());
			}
		});
		itemBinSiteSummary.addAction(new FHAESAction("Export to shapefile", "formatshp.png") {
			
			private static final long serialVersionUID = 1L;
			
			@Override
			public void actionPerformed(ActionEvent event) {
			
				ShapeFileDialog sfd = new ShapeFileDialog(App.mainFrame, fhm);
				sfd.setVisible(true);
			}
		});
		
		itemBinTreeSummary = new FHAESResultTreeNode(FHAESResult.BINARY_MATRIX_TREE, iconMultipleTables);
		itemBinTreeSummary.addAction(new FHAESAction("Save to CSV", "formatcsv.png") {
			
			private static final long serialVersionUID = 1L;
			
			@Override
			public void actionPerformed(ActionEvent event) {
			
				saveFileToDisk(treeSummaryFile, new CSVFileFilter());
			}
		});
		
		itemNTP = new FHAESResultTreeNode(FHAESResult.BINARY_MATRIX_NTP, iconMultipleTables);
		itemNTP.addAction(new FHAESAction("Save to CSV", "formatcsv.png") {
			
			private static final long serialVersionUID = 1L;
			
			@Override
			public void actionPerformed(ActionEvent event) {
			
				saveFileToDisk(NTPFile, new CSVFileFilter());
			}
		});
		
		this.itemGeneralSummary = new FHAESResultTreeNode(FHAESResult.GENERAL_SUMMARY, iconMultipleTables);
		itemGeneralSummary.addAction(new FHAESAction("Save to CSV", "formatcsv.png") {
			
			private static final long serialVersionUID = 1L;
			
			@Override
			public void actionPerformed(ActionEvent event) {
			
				saveFileToDisk(generalSummaryFile, new CSVFileFilter());
			}
			
		});
		
		this.itemSingleFileSummary = new FHAESResultTreeNode(FHAESResult.SINGLE_FILE_SUMMARY, iconTable);
		itemSingleFileSummary.addAction(new FHAESAction("Save to CSV", "formatcsv.png") {
			
			private static final long serialVersionUID = 1L;
			
			@Override
			public void actionPerformed(ActionEvent event) {
			
				saveFileToDisk(singleFileSummaryFile, new CSVFileFilter());
			}
			
		});
		
		this.itemSingleEventSummary = new FHAESResultTreeNode(FHAESResult.SINGLE_EVENT_SUMMARY, iconTable);
		itemSingleEventSummary.addAction(new FHAESAction("Save to CSV", "formatcsv.png") {
			
			private static final long serialVersionUID = 1L;
			
			@Override
			public void actionPerformed(ActionEvent event) {
			
				saveFileToDisk(singleEventSummaryFile, new CSVFileFilter());
			}
			
		});
		
		// Add results to categories
		categoryGeneral.add(itemGeneralSummary);
		categoryGeneral.add(itemSingleFileSummary);
		categoryGeneral.add(itemSingleEventSummary);
		categorySimMatrices.add(itemJaccard);
		categorySimMatrices.add(itemCohen);
		categoryDisSimMatrices.add(itemJaccardD);
		categoryDisSimMatrices.add(itemCohenD);
		categoryInterval.add(itemIntervalSummary);
		categoryInterval.add(itemExceedence);
		categorySeasonality.add(itemSeasonalitySummary);
		categoryBinaryMatrices.add(itemBin11);
		categoryBinaryMatrices.add(itemBin01);
		categoryBinaryMatrices.add(itemBin10);
		categoryBinaryMatrices.add(itemBin00);
		categoryBinaryMatrices.add(itemBinSum);
		categoryBinarySummaryMatrices.add(itemBinSiteSummary);
		categoryBinarySummaryMatrices.add(itemBinTreeSummary);
		categoryBinarySummaryMatrices.add(itemNTP);
		
		// Add categories to root of tree
		rootNode.add(categoryGeneral);
		rootNode.add(categoryInterval);
		rootNode.add(categorySeasonality);
		rootNode.add(categoryBinarySummaryMatrices);
		rootNode.add(categoryBinaryMatrices);
		rootNode.add(categorySimMatrices);
		rootNode.add(categoryDisSimMatrices);
		
		treeModel = new DefaultTreeModel(rootNode);
		
		splitPane = new JSplitPane();
		if (Platform.isOSX())
			splitPane.setBackground(MainWindow.MAC_BACKGROUND_COLOR);
		
		splitPane.setResizeWeight(0.9);
		add(splitPane, BorderLayout.CENTER);
		
		JPanel panelTree = new JPanel();
		splitPane.setRightComponent(panelTree);
		panelTree.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		panelTree.setLayout(new BorderLayout(0, 0));
		
		// Build tree
		treeResults = new JTree();
		panelTree.add(treeResults);
		treeResults.setModel(treeModel);
		
		treeResults.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		treeResults.setCellRenderer(new FHAESResultTreeRenderer());
		
		pickResultPanel = new PickResultPanel();
		runAnalysisPanel = new RunAnalysisPanel();
		
		cards = new JPanel();
		cl = new CardLayout();
		cards.setLayout(cl);
		cards.add(pickResultPanel, PICKRESULTPANEL);
		cards.add(runAnalysisPanel, RUNANALYSIS);
		cards.add(emptyPanel, EMPTYPANEL);
		
		splitPane.setLeftComponent(cards);
		
		cl.show(cards, RUNANALYSIS);
		
		splitPaneResult = new JSplitPane();
		splitPaneResult.setOneTouchExpandable(true);
		splitPaneResult.setOrientation(JSplitPane.VERTICAL_SPLIT);
		cards.add(splitPaneResult, RESULTSPANEL);
		
		panelResult = new JPanel();
		
		panelResult.setBorder(new TitledBorder(null, "", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panelResult.setLayout(new BorderLayout(0, 0));
		
		goldFishPanel = new GoldFishPanel();
		splitPaneResult.setRightComponent(goldFishPanel);
		
		// Build table
		scrollPane = new JScrollPane();
		
		panelResult.add(scrollPane);
		table = new JXTable();
		adapter = new JTableSpreadsheetByRowAdapter(table);
		
		table.setModel(new DefaultTableModel());
		table.setHorizontalScrollEnabled(true);
		scrollPane.setViewportView(table);
		splitPaneResult.setLeftComponent(panelResult);
		
		// OSX Style hack
		if (Platform.isOSX())
			panelResult.setBackground(MainWindow.MAC_BACKGROUND_COLOR);
		if (Platform.isOSX())
			scrollPane.setBackground(MainWindow.MAC_BACKGROUND_COLOR);
		
		// Expand all nodes
		for (int i = 0; i < treeResults.getRowCount(); i++)
		{
			treeResults.expandRow(i);
		}
		
		treeResults.addTreeSelectionListener(this);
		
		treeResults.addMouseListener(new MouseListener() {
			
			@Override
			public void mouseClicked(MouseEvent e) {
			
				if (SwingUtilities.isRightMouseButton(e))
				{
					int x = e.getX();
					int y = e.getY();
					JTree tree = (JTree) e.getSource();
					TreePath path = tree.getPathForLocation(x, y);
					if (path == null)
						return;
					if (!tree.isEnabled())
						return;
					
					tree.setSelectionPath(path);
					Component mc = e.getComponent();
					
					if (path != null && path.getLastPathComponent() instanceof FHAESResultTreeNode)
					{
						FHAESResultTreeNode node = (FHAESResultTreeNode) path.getLastPathComponent();
						
						if (!node.isEnabled())
							return;
						
						FHAESResultPopupMenu popupMenu = new FHAESResultPopupMenu(node.getArrayOfActions());
						popupMenu.show(mc, e.getX(), e.getY());
					}
				}
			}
			
			@Override
			public void mouseEntered(MouseEvent arg0) {
			
			}
			
			@Override
			public void mouseExited(MouseEvent arg0) {
			
			}
			
			@Override
			public void mousePressed(MouseEvent arg0) {
			
			}
			
			@Override
			public void mouseReleased(MouseEvent arg0) {
			
			}
			
		});
		
		this.splitPaneResult.setDividerLocation(10000);
		this.splitPaneResult.setDividerSize(3);
		this.splitPaneResult.setResizeWeight(1);
	}
	
	/**
	 * Save a file to disk, asking the user for a filename. Add the selected FileFilter to the save dialog box.
	 * 
	 * @param fileToSave
	 * @param filter
	 */
	private void saveFileToDisk(File fileToSave, FileFilter filter) {
	
		File outfile = IOUtil.getOutputFile(filter);
		
		if (outfile == null)
			return;
		
		if (outfile.exists())
		{
			Object[] options = { "Overwrite", "No", "Cancel" };
			int response = JOptionPane.showOptionDialog(App.mainFrame, "This file already exists.  Are you sure you want to overwrite?",
					"Confirm", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, // do not use a custom Icon
					options, // the titles of buttons
					options[0]); // default button title
			
			if (response != JOptionPane.YES_OPTION)
			{
				return;
			}
			
		}
		
		try
		{
			FileUtils.copyFile(fileToSave, outfile);
		}
		catch (IOException e)
		{
			JOptionPane.showMessageDialog(App.mainFrame, "Error saving file:\n" + e.getMessage());
			e.printStackTrace();
		}
	}
	
	/**
	 * Simple popup menu class for nodes in the results tree.
	 */
	class FHAESResultPopupMenu extends JPopupMenu {
		
		private static final long serialVersionUID = 1L;
		
		public FHAESResultPopupMenu(ArrayList<FHAESAction> actions) {
		
			for (FHAESAction action : actions)
			{
				JMenuItem menuItem = new JMenuItem(action);
				add(menuItem);
			}
		}
	}
	
	/**
	 * Clear the analysis results table and show the relevant instructional card page, either 'pick analysis' or 'run analysis' depending on
	 * whether we have analyses to show
	 */
	private void clearTable() {
	
		panelResult.setBorder(new TitledBorder(null, "", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		table.setModel(new DefaultTableModel());
		
		MainWindow.getInstance().getReportPanel().actionResultsHelp.setEnabled(false);
		
		if (this.generalSummaryModel == null)
		{
			// No general sumamry model so analyses have NOT been run
			// Show 'run analysis' card
			cl.show(cards, RUNANALYSIS);
		}
		else
		{
			// If there is a general summary model then the analysis have been run.
			// Show the 'pick results' card
			cl.show(cards, PICKRESULTPANEL);
		}
		
		this.repaint();
	}
	
	/**
	 * Draw the analysis results table depending on the current tree-node
	 */
	public void setupTable() {
	
		setupTable(false);
	}
	
	/**
	 * Draw the analysis results table depending on the current tree-node forcing the GUI to refresh regardless
	 * 
	 * @param forceRefresh
	 */
	public void setupTable(Boolean forceRefresh) {
	
		cl.show(cards, RESULTSPANEL);
		MainWindow.getInstance().getReportPanel().actionResultsHelp.setEnabled(true);
		
		goldFishPanel.setParamsText();
		
		try
		{
			log.debug("Setting cursor to wait");
			table.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
			treeResults.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
			
			DefaultMutableTreeNode selectednode = (DefaultMutableTreeNode) treeResults.getLastSelectedPathComponent();
			
			if (selectednode == null)
			{
				clearTable();
				return;
			}
			
			log.debug("Node selected: " + selectednode.toString());
			
			// If selection hasn't changed don't do anything
			if (previouslySelectedNode != null && selectednode.equals(previouslySelectedNode) && !forceRefresh)
			{
				log.debug("Node selection hasn't changed so not doing anything");
				return;
			}
			
			previouslySelectedNode = selectednode;
			
			if (!(selectednode instanceof FHAESResultTreeNode))
			{
				clearTable();
				return;
			}
			
			FHAESResultTreeNode resultnode = (FHAESResultTreeNode) treeResults.getLastSelectedPathComponent();
			
			FHAESResult result = resultnode.getFHAESResult();
			panelResult.setBorder(new TitledBorder(null, result.getFullName(), TitledBorder.LEADING, TitledBorder.TOP, null, null));
			
			if (result.equals(FHAESResult.SEASONALITY_SUMMARY))
			{
				
				log.debug("Seasonality summary node selected");
				
				if (seasonalitySummaryModel == null)
				{
					clearTable();
					return;
				}
				
				table.setModel(seasonalitySummaryModel);
				// table.setSortOrder(0, SortOrder.ASCENDING);
				setDefaultTableAlignment(SwingConstants.RIGHT);
			}
			
			else if (result.equals(FHAESResult.INTERVAL_SUMMARY))
			{
				
				log.debug("Interval summary node selected");
				
				if (intervalsSummaryModel == null)
				{
					clearTable();
					return;
				}
				
				table.setModel(intervalsSummaryModel);
				// table.setSortOrder(0, SortOrder.ASCENDING);
				setDefaultTableAlignment(SwingConstants.RIGHT);
				
			}
			
			else if (result.equals(FHAESResult.INTERVAL_EXCEEDENCE_TABLE))
			{
				
				log.debug("Interval exceedence node selected");
				
				if (intervalsExceedenceModel == null)
				{
					clearTable();
					return;
				}
				
				table.setModel(intervalsExceedenceModel);
				// table.setSortOrder(0, SortOrder.ASCENDING);
				setDefaultTableAlignment(SwingConstants.RIGHT);
			}
			
			else if (result.equals(FHAESResult.BINARY_MATRIX_00))
			{
				
				if (this.bin00Model == null)
				{
					clearTable();
					return;
				}
				
				table.setModel(bin00Model);
				// table.setSortOrder(0, SortOrder.ASCENDING);
				setDefaultTableAlignment(SwingConstants.RIGHT);
			}
			
			else if (result.equals(FHAESResult.BINARY_MATRIX_01))
			{
				
				if (this.bin01Model == null)
				{
					clearTable();
					return;
				}
				
				table.setModel(bin01Model);
				// table.setSortOrder(0, SortOrder.ASCENDING);
				setDefaultTableAlignment(SwingConstants.RIGHT);
			}
			
			else if (result.equals(FHAESResult.BINARY_MATRIX_10))
			{
				
				if (this.bin10Model == null)
				{
					clearTable();
					return;
				}
				
				table.setModel(bin10Model);
				// table.setSortOrder(0, SortOrder.ASCENDING);
				setDefaultTableAlignment(SwingConstants.RIGHT);
			}
			else if (result.equals(FHAESResult.BINARY_MATRIX_11))
			{
				
				if (this.bin11Model == null)
				{
					clearTable();
					return;
				}
				
				table.setModel(bin11Model);
				// table.setSortOrder(0, SortOrder.ASCENDING);
				setDefaultTableAlignment(SwingConstants.RIGHT);
			}
			else if (result.equals(FHAESResult.BINARY_MATRIX_SUM))
			{
				
				if (this.binSumModel == null)
				{
					clearTable();
					return;
				}
				table.setModel(binSumModel);
				setDefaultTableAlignment(SwingConstants.RIGHT);
			}
			else if (result.equals(FHAESResult.JACCARD_SIMILARITY_MATRIX))
			{
				
				if (this.SJACModel == null)
				{
					clearTable();
					return;
				}
				table.setModel(SJACModel);
				setDefaultTableAlignment(SwingConstants.RIGHT);
			}
			else if (result.equals(FHAESResult.COHEN_SIMILARITITY_MATRIX))
			{
				
				if (this.SCOHModel == null)
				{
					clearTable();
					return;
				}
				table.setModel(SCOHModel);
				setDefaultTableAlignment(SwingConstants.RIGHT);
			}
			else if (result.equals(FHAESResult.JACCARD_SIMILARITY_MATRIX_D))
			{
				
				if (this.DSJACModel == null)
				{
					clearTable();
					return;
				}
				table.setModel(DSJACModel);
				setDefaultTableAlignment(SwingConstants.RIGHT);
			}
			else if (result.equals(FHAESResult.COHEN_SIMILARITITY_MATRIX_D))
			{
				
				if (this.DSCOHModel == null)
				{
					clearTable();
					return;
				}
				table.setModel(DSCOHModel);
				setDefaultTableAlignment(SwingConstants.RIGHT);
			}
			else if (result.equals(FHAESResult.BINARY_MATRIX_NTP))
			{
				
				log.debug("doing NTP");
				if (this.NTPModel == null)
				{
					log.debug("fileNTP is null so clearing table");
					
					clearTable();
					return;
				}
				table.setModel(NTPModel);
				setDefaultTableAlignment(SwingConstants.RIGHT);
			}
			else if (result.equals(FHAESResult.BINARY_MATRIX_SITE))
			{
				
				if (this.siteSummaryModel == null)
				{
					clearTable();
					return;
				}
				table.setModel(siteSummaryModel);
				setDefaultTableAlignment(SwingConstants.RIGHT);
			}
			else if (result.equals(FHAESResult.BINARY_MATRIX_TREE))
			{
				
				if (this.treeSummaryModel == null)
				{
					clearTable();
					return;
				}
				table.setModel(treeSummaryModel);
				setDefaultTableAlignment(SwingConstants.RIGHT);
			}
			
			else if (result.equals(FHAESResult.GENERAL_SUMMARY))
			{
				
				if (this.generalSummaryModel == null)
				{
					clearTable();
					return;
				}
				table.setModel(generalSummaryModel);
				setDefaultTableAlignment(SwingConstants.RIGHT);
			}
			
			else if (result.equals(FHAESResult.SINGLE_FILE_SUMMARY))
			{
				
				if (this.singleFileSummaryModel == null)
				{
					clearTable();
					return;
				}
				table.setModel(singleFileSummaryModel);
				setDefaultTableAlignment(SwingConstants.RIGHT);
			}
			
			else if (result.equals(FHAESResult.SINGLE_EVENT_SUMMARY))
			{
				
				if (this.singleEventSummaryModel == null)
				{
					clearTable();
					return;
				}
				table.setModel(singleEventSummaryModel);
				setDefaultTableAlignment(SwingConstants.RIGHT);
				setTableColumnAlignment(SwingConstants.LEFT, 2);
			}
			
			else
			{
				log.warn("Unhandled FHAESResult type");
				clearTable();
			}
			
			table.packAll();
			
			table.setColumnControlVisible(true);
			table.setAutoCreateRowSorter(true);
			
		}
		catch (Exception e)
		{
			log.debug("Caught exception loading table data");
			
		}
		finally
		{
			log.debug("Clearing cursor");
			
			table.setCursor(Cursor.getDefaultCursor());
			treeResults.setCursor(Cursor.getDefaultCursor());
		}
		
	}
	
	private void setDefaultTableAlignment(int align) {
	
		DefaultTableCellRenderer renderer = new DefaultTableCellRenderer();
		renderer.setHorizontalAlignment(align);
		
		for (int i = 1; i < table.getModel().getColumnCount(); i++)
		{
			table.getColumnModel().getColumn(i).setCellRenderer(renderer);
		}
	}
	
	private void setTableColumnAlignment(int align, int col) {
	
		DefaultTableCellRenderer renderer = new DefaultTableCellRenderer();
		renderer.setHorizontalAlignment(align);
		table.getColumnModel().getColumn(col).setCellRenderer(renderer);
	}
	
	/**
	 * The selection of the tree widget has changed to set up the results table
	 */
	@Override
	public void valueChanged(TreeSelectionEvent evt) {
	
		log.debug("Tree item selected");
		
		setupTable();
		
	}
	
	/**
	 * Clear all the results from the table and tree.
	 */
	public void clearResults() {
	
		seasonalitySummaryModel = null;
		intervalsExceedenceModel = null;
		intervalsSummaryModel = null;
		bin00Model = null;
		bin10Model = null;
		bin01Model = null;
		bin11Model = null;
		binSumModel = null;
		DSCOHModel = null;
		DSJACModel = null;
		SCOHModel = null;
		siteSummaryModel = null;
		SJACModel = null;
		NTPModel = null;
		treeSummaryModel = null;
		singleFileSummaryModel = null;
		generalSummaryModel = null;
		
		setResultsEnabled(false);
		table.setModel(new DefaultTableModel());
	}
	
	/**
	 * Show the run analysis tab.
	 */
	public void showRunAnalysisTab() {
	
		if (MainWindow.getInstance().isFileListPopulated())
		{
			cl.show(cards, RUNANALYSIS);
			MainWindow.getInstance().getReportPanel().actionResultsHelp.setEnabled(false);
			clearResults();
			repaintTree();
		}
		else
		{
			cl.show(cards, EMPTYPANEL);
			clearResults();
			repaintTree();
		}
	}
	
	/**
	 * Sets whether results GUI should be enabled or not.
	 * 
	 * @param b
	 */
	private void setResultsEnabled(boolean b) {
	
		treeResults.setEnabled(b);
		MainWindow.getInstance().actionSaveResults.setEnabled(b);
		
		if (b)
		{
			setSeasonalitySummaryStatus();
			setSingleFileSummaryStatus();
			setGeneralSummaryStatus();
			setIntervalsSummaryStatus();
			setIntervalsExceedenceStatus();
			setBin00Status();
			setBin01Status();
			setBin10Status();
			setBin11Status();
			setBinSumStatus();
			setDSCOHStatus();
			setSCOHStatus();
			setDSJACStatus();
			setSJACStatus();
			setNTPStatus();
			setSiteSummaryStatus();
			setTreeSummaryStatus();
		}
	}
	
	/**
	 * Get a text file containing the parameters the user has selected in the analysis properties dialog
	 * 
	 * @return
	 */
	private File getParamsAsFile() {
	
		try
		{
			
			Writer wr;
			/*
			 * Start writing information into the files
			 */
			
			String report = "Parameter, Value" + System.getProperty("line.separator");
			report = report + "Interval analysis type, "
					+ App.prefs.getAnalysisTypePref(PrefKey.INTERVALS_ANALYSIS_TYPE, AnalysisType.COMPOSITE)
					+ System.getProperty("line.separator");
			report = report + "Include intervals after last event, "
					+ App.prefs.getBooleanPref(PrefKey.JSEA_INCLUDE_INCOMPLETE_WINDOW, false) + System.getProperty("line.separator");
			report = report + "Event type for analysis, "
					+ App.prefs.getEventTypePref(PrefKey.EVENT_TYPE_TO_PROCESS, EventTypeToProcess.FIRE_EVENT)
					+ System.getProperty("line.separator");
			report = report + "Minimum year overlap for comparisons, " + App.prefs.getIntPref(PrefKey.RANGE_OVERLAP_REQUIRED, 25)
					+ System.getProperty("line.separator");
			report = report + "Composite fire threshold, "
					+ App.prefs.getFireFilterTypePref(PrefKey.COMPOSITE_FILTER_TYPE, FireFilterType.NUMBER_OF_EVENTS)
					+ System.getProperty("line.separator");
			report = report + "Composite fire filter value, " + App.prefs.getIntPref(PrefKey.COMPOSITE_FILTER_VALUE, 1)
					+ System.getProperty("line.separator");
			report = report + "First season combination, " + this.getFirstSeasonCombinationDescription()
					+ System.getProperty("line.separator");
			report = report + "Second season combination, " + this.getSecondSeasonCombinationDescription();
			
			File paramsfile = File.createTempFile("FHParameters", "csv");
			paramsfile.deleteOnExit();
			wr = new BufferedWriter(new FileWriter(paramsfile));
			wr.write(report);
			wr.close();
			return paramsfile;
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		
		return null;
		
	}
	
	/**
	 * Get a README text file to be including in the analysis results zip file.
	 * 
	 * @return
	 */
	private File getReadmeFile() {
	
		try
		{
			Writer wr;
			/*
			 * Start writing information into the files
			 */
			
			SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");// dd/MM/yyyy
			Date now = new Date();
			String strDate = sdfDate.format(now);
			
			String report = "This zip file contains the results of fire history analyses performed by FHAES in comma separated value (CSV) "
					+ "text files. Also included is a file 'Parameters.csv' containing the parameters that were used to run the "
					+ "analyses\n\n";
			report = report + "FHAES version      : " + Builder.getVersionAndBuild() + System.getProperty("line.separator");
			report = report + "Results saved      : " + strDate + System.getProperty("line.separator");
			report = report + "User               : " + System.getProperty("user.name") + System.getProperty("line.separator");
			
			File paramsfile = File.createTempFile("Readme", "txt");
			paramsfile.deleteOnExit();
			wr = new BufferedWriter(new FileWriter(paramsfile));
			wr.write(report);
			wr.close();
			return paramsfile;
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * Save the current results into a zip file
	 * 
	 * @param outputfile
	 */
	public void saveZipOfResults(File outputfile) {
	
		try
		{
			FileOutputStream fos = new FileOutputStream(outputfile.getAbsolutePath());
			ZipOutputStream zos = new ZipOutputStream(fos);
			
			addToZipFile(getParamsAsFile(), "Parameters.csv", zos);
			addToZipFile(this.generalSummaryFile, "General summary.csv", zos);
			addToZipFile(this.intervalsSummaryFile, "Intervals summary.csv", zos);
			addToZipFile(this.intervalsExceedenceFile, "Intervals exceedence.csv", zos);
			addToZipFile(this.seasonalitySummaryFile, "Seasonality summary.csv", zos);
			addToZipFile(this.siteSummaryFile, "Binary site summary.csv", zos);
			addToZipFile(this.treeSummaryFile, "Binary tree summary.csv", zos);
			addToZipFile(this.NTPFile, "NTP matrix.csv", zos);
			addToZipFile(this.bin00File, "Matrix A.csv", zos);
			addToZipFile(this.bin01File, "Matrix B.csv", zos);
			addToZipFile(this.bin10File, "Matrix C.csv", zos);
			addToZipFile(this.bin11File, "Matrix D.csv", zos);
			addToZipFile(this.binSumFile, "Matrix L.csv", zos);
			addToZipFile(this.SJACFile, "JACCARD similarity.csv", zos);
			addToZipFile(this.SCOHFile, "COHEN similarity.csv", zos);
			addToZipFile(this.DSJACFile, "JACCARD dissimilarity.csv", zos);
			addToZipFile(this.DSCOHFile, "COHEN dissimilarity.csv", zos);
			addToZipFile(this.singleFileSummaryFile, "Single file summary.csv", zos);
			addToZipFile(this.singleEventSummaryFile, "Single file event summary.csv", zos);
			
			addToZipFile(getReadmeFile(), "readme.txt", zos);
			
			zos.close();
			fos.close();
			
		}
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		
	}
	
	/**
	 * Add a file to a zip file stream
	 * 
	 * @param file
	 * @param filename
	 * @param zos
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public static void addToZipFile(File file, String filename, ZipOutputStream zos) throws FileNotFoundException, IOException {
	
		if (file == null)
			return;
		if (!file.exists())
			return;
		
		FileInputStream fis = new FileInputStream(file);
		ZipEntry zipEntry = new ZipEntry(filename);
		zos.putNextEntry(zipEntry);
		
		byte[] bytes = new byte[1024];
		int length;
		while ((length = fis.read(bytes)) >= 0)
		{
			zos.write(bytes, 0, length);
		}
		
		zos.closeEntry();
		fis.close();
	}
	
	/**
	 * Save the current analyses results to a multi-tabbed Excel file
	 * 
	 * @param outputfile
	 */
	public void saveXLSXOfResults(File outputfile) {
	
		Workbook workbook = new XSSFWorkbook();
		CreationHelper createHelper = workbook.getCreationHelper();
		
		doubleStyle = workbook.createCellStyle();
		doubleStyle.setDataFormat(createHelper.createDataFormat().getFormat("0.000"));
		
		writeParametersToXLSXSheet(workbook.createSheet("Parameters"));
		writeModelToXLSXSheet(workbook.createSheet("General Summary"), generalSummaryModel);
		writeModelToXLSXSheet(workbook.createSheet("Interval Summary"), intervalsSummaryModel);
		writeModelToXLSXSheet(workbook.createSheet("Interval Exceedence"), intervalsExceedenceModel);
		writeModelToXLSXSheet(workbook.createSheet("Seasonality Summary"), seasonalitySummaryModel);
		writeModelToXLSXSheet(workbook.createSheet("Binary Site Summary"), siteSummaryModel);
		writeModelToXLSXSheet(workbook.createSheet("Binary Tree Summary"), treeSummaryModel);
		writeModelToXLSXSheet(workbook.createSheet("NTP Matrix"), NTPModel);
		writeModelToXLSXSheet(workbook.createSheet("COHEN Dissimilarity"), DSCOHModel);
		writeModelToXLSXSheet(workbook.createSheet("JACCARD Dissimilarity"), DSJACModel);
		writeModelToXLSXSheet(workbook.createSheet("COHEN Similarity"), SCOHModel);
		writeModelToXLSXSheet(workbook.createSheet("JACCARD Similarity"), SJACModel);
		writeModelToXLSXSheet(workbook.createSheet("Matrix A (1-1)"), bin11Model);
		writeModelToXLSXSheet(workbook.createSheet("Matrix B (0-1)"), bin01Model);
		writeModelToXLSXSheet(workbook.createSheet("Matrix C (1-0)"), bin10Model);
		writeModelToXLSXSheet(workbook.createSheet("Matrix D (0-0)"), bin00Model);
		writeModelToXLSXSheet(workbook.createSheet("Matrix L (Sum)"), binSumModel);
		writeModelToXLSXSheet(workbook.createSheet("Single File Summary"), singleFileSummaryModel);
		writeModelToXLSXSheet(workbook.createSheet("Single File Event Summary"), singleEventSummaryModel);
		
		OutputStream os = IOUtils.createOutput(outputfile);
		try
		{
			workbook.write(os);
			
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		finally
		{
			try
			{
				os.close();
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
		
	}
	
	/**
	 * TODO
	 * 
	 * @return
	 */
	private String getFirstSeasonCombinationDescription() {
	
		String cellvalue = "";
		if (App.prefs.getBooleanPref(PrefKey.SEASONALITY_FIRST_GROUP_DORMANT, true))
		{
			cellvalue = cellvalue + "Dormant; ";
		}
		if (App.prefs.getBooleanPref(PrefKey.SEASONALITY_FIRST_GROUP_EARLY_EARLY, true))
		{
			cellvalue = cellvalue + "Early earlywood; ";
		}
		if (App.prefs.getBooleanPref(PrefKey.SEASONALITY_FIRST_GROUP_MIDDLE_EARLY, true))
		{
			cellvalue = cellvalue + "Middle earlywood; ";
		}
		if (App.prefs.getBooleanPref(PrefKey.SEASONALITY_FIRST_GROUP_LATE_EARLY, false))
		{
			cellvalue = cellvalue + "Late earlywood; ";
		}
		if (App.prefs.getBooleanPref(PrefKey.SEASONALITY_FIRST_GROUP_LATE, false))
		{
			cellvalue = cellvalue + "Latewood; ";
		}
		if (cellvalue.length() > 0)
		{
			cellvalue = cellvalue.substring(0, cellvalue.length() - 2);
		}
		
		return cellvalue;
	}
	
	/**
	 * TODO
	 * 
	 * @return
	 */
	private String getSecondSeasonCombinationDescription() {
	
		String cellvalue = "";
		if (App.prefs.getBooleanPref(PrefKey.SEASONALITY_SECOND_GROUP_DORMANT, false))
		{
			cellvalue = cellvalue + "Dormant; ";
		}
		if (App.prefs.getBooleanPref(PrefKey.SEASONALITY_SECOND_GROUP_EARLY_EARLY, false))
		{
			cellvalue = cellvalue + "Early earlywood; ";
		}
		if (App.prefs.getBooleanPref(PrefKey.SEASONALITY_SECOND_GROUP_MIDDLE_EARLY, false))
		{
			cellvalue = cellvalue + "Middle earlywood; ";
		}
		if (App.prefs.getBooleanPref(PrefKey.SEASONALITY_SECOND_GROUP_LATE_EARLY, true))
		{
			cellvalue = cellvalue + "Late earlywood; ";
		}
		if (App.prefs.getBooleanPref(PrefKey.SEASONALITY_SECOND_GROUP_LATE, true))
		{
			cellvalue = cellvalue + "Latewood; ";
		}
		if (cellvalue.length() > 0)
		{
			cellvalue = cellvalue.substring(0, cellvalue.length() - 2);
		}
		
		return cellvalue;
	}
	
	/**
	 * Write the selected analysis parameters to the provided Excel spreadsheet
	 * 
	 * @param dataSheet
	 */
	private void writeParametersToXLSXSheet(Sheet dataSheet) {
	
		int rows = 10;
		
		for (int i = 0; i < rows; i++)
		{
			dataSheet.createRow(i);
		}
		
		Cell cell;
		
		int i = 0;
		// Header row
		cell = dataSheet.getRow(i).createCell(0);
		cell.setCellValue("Parameter");
		cell = dataSheet.getRow(i).createCell(1);
		cell.setCellValue("Value");
		
		// Parameter rows
		i++;
		cell = dataSheet.getRow(i).createCell(0);
		cell.setCellValue("Interval analysis type");
		cell = dataSheet.getRow(i).createCell(1);
		cell.setCellValue(App.prefs.getAnalysisTypePref(PrefKey.INTERVALS_ANALYSIS_TYPE, AnalysisType.COMPOSITE).toString());
		
		i++;
		cell = dataSheet.getRow(i).createCell(0);
		cell.setCellValue("Include intervals after last event?");
		cell = dataSheet.getRow(i).createCell(1);
		cell.setCellValue(App.prefs.getBooleanPref(PrefKey.JSEA_INCLUDE_INCOMPLETE_WINDOW, false));
		
		i++;
		cell = dataSheet.getRow(i).createCell(0);
		cell.setCellValue("Event type for analysis");
		cell = dataSheet.getRow(i).createCell(1);
		cell.setCellValue(App.prefs.getEventTypePref(PrefKey.EVENT_TYPE_TO_PROCESS, EventTypeToProcess.FIRE_EVENT).toString());
		
		i++;
		cell = dataSheet.getRow(i).createCell(0);
		cell.setCellValue("Min. year overlap for comparisons");
		cell = dataSheet.getRow(i).createCell(1);
		cell.setCellValue(App.prefs.getIntPref(PrefKey.RANGE_OVERLAP_REQUIRED, 25));
		
		i++;
		cell = dataSheet.getRow(i).createCell(0);
		cell.setCellValue("Composite fire threshold type");
		cell = dataSheet.getRow(i).createCell(1);
		cell.setCellValue(App.prefs.getFireFilterTypePref(PrefKey.COMPOSITE_FILTER_TYPE, FireFilterType.NUMBER_OF_EVENTS).toString());
		
		i++;
		cell = dataSheet.getRow(i).createCell(0);
		cell.setCellValue("Composite fire threshold value");
		cell = dataSheet.getRow(i).createCell(1);
		cell.setCellValue(App.prefs.getIntPref(PrefKey.COMPOSITE_FILTER_VALUE, 1));
		
		i++;
		cell = dataSheet.getRow(i).createCell(0);
		cell.setCellValue("First season combination");
		cell = dataSheet.getRow(i).createCell(1);
		cell.setCellValue(this.getFirstSeasonCombinationDescription());
		
		i++;
		cell = dataSheet.getRow(i).createCell(0);
		cell.setCellValue("Second season combination");
		cell = dataSheet.getRow(i).createCell(1);
		cell.setCellValue(this.getSecondSeasonCombinationDescription());
		
	}
	
	/**
	 * Write the specified DefaultTableModel to the provided Excel spreadsheet
	 * 
	 * @param dataSheet
	 * @param model
	 */
	private void writeModelToXLSXSheet(Sheet dataSheet, DefaultTableModel model) {
	
		if (model == null)
		{
			Row headerrow = dataSheet.createRow(0);
			Cell cell = headerrow.createCell(0);
			cell.setCellValue("No results available");
			return;
		}
		
		if (model.getColumnCount() > excelRowColLimit || model.getRowCount() > excelRowColLimit)
		{
			Row headerrow = dataSheet.createRow(0);
			Cell cell = headerrow.createCell(0);
			cell.setCellValue("Number of rows and/or columns too large to write to Excel");
			return;
		}
		
		Row headerrow = dataSheet.createRow(0);
		
		// Write column headers
		for (int i = 0; i < model.getColumnCount(); i++)
		{
			Cell cell = headerrow.createCell(i);
			cell.setCellValue(model.getColumnName(i));
		}
		
		// Write data values
		for (int r = 0; r < model.getRowCount(); r++)
		{
			Row row = dataSheet.createRow(r + 1);
			
			for (int c = 0; c < model.getColumnCount(); c++)
			{
				Cell cell = row.createCell(c);
				
				String value;
				
				if (model.getValueAt(r, c) == null)
				{
					value = "";
				}
				else
				
				{
					value = model.getValueAt(r, c).toString();
				}
				
				try
				{
					Double d = Double.valueOf(value);
					cell.setCellValue(d);
					cell.setCellStyle(doubleStyle);
					
				}
				catch (NumberFormatException ex)
				{
					cell.setCellValue(value);
				}
			}
		}
	}
}
