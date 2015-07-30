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
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import javax.swing.Action;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DropMode;
import javax.swing.JComboBox;
import javax.swing.JDialog;
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
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JToolBar;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.filechooser.FileFilter;

import org.fhaes.FHRecorder.controller.FileController;
import org.fhaes.FHRecorder.view.FireHistoryRecorder;
import org.fhaes.components.FHAESCheckBoxMenuItem;
import org.fhaes.components.FHAESMenuItem;
import org.fhaes.components.JToolBarButton;
import org.fhaes.components.JToolBarToggleButton;
import org.fhaes.enums.FeedbackDisplayProtocol;
import org.fhaes.enums.FeedbackMessageType;
import org.fhaes.exceptions.CompositeFileException;
import org.fhaes.feedback.FeedbackMessagePanel;
import org.fhaes.feedback.FeedbackPreferenceManager;
import org.fhaes.fhfilereader.FHCategoryReader;
import org.fhaes.fhsamplesize.view.FHSampleSize;
import org.fhaes.filefilter.CSVFileFilter;
import org.fhaes.filefilter.CSVZipFileFilter;
import org.fhaes.filefilter.FHAESFileFilter;
import org.fhaes.filefilter.FHXFileFilter;
import org.fhaes.filefilter.TXTFileFilter;
import org.fhaes.filefilter.XLSXFileFilter;
import org.fhaes.help.RemoteHelp;
import org.fhaes.jsea.JSEAFrame;
import org.fhaes.model.FHFile;
import org.fhaes.model.FHFileFirstYearComparator;
import org.fhaes.model.FHFileLastYearComparator;
import org.fhaes.model.FHFileListCellRenderer;
import org.fhaes.model.FHFileNameComparator;
import org.fhaes.model.FHFileValidityComparator;
import org.fhaes.model.FileDropTargetListener;
import org.fhaes.model.FileListModel;
import org.fhaes.neofhchart.ChartActions;
import org.fhaes.preferences.App;
import org.fhaes.preferences.FHAESPreferences.PrefKey;
import org.fhaes.preferences.PrefsEvent;
import org.fhaes.preferences.PrefsListener;
import org.fhaes.tools.FHOperations;
import org.fhaes.tools.UpdateChecker;
import org.fhaes.util.Builder;
import org.fhaes.util.FHAESAction;
import org.fhaes.util.IOUtil;
import org.fhaes.util.OSX;
import org.fhaes.util.Platform;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.explodingpixels.macwidgets.MacButtonFactory;
import com.explodingpixels.macwidgets.MacUtils;
import com.explodingpixels.macwidgets.UnifiedToolBar;

import net.miginfocom.swing.MigLayout;
import uk.org.lidalia.sysoutslf4j.context.SysOutOverSLF4J;

/**
 * MainWindow Class. This is the main window for FHAES. It contains the menus and toolbars that interact with all the other FHAES modules.
 * 
 * @author Peter Brewer
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
public class MainWindow implements PrefsListener {
	
	// Make static instance of MainWindow - this is a Singleton object
	private static MainWindow instanceOfMainWindow = null;
	
	// Declare FHAES specialized objects
	private static final ResourceBundle BUNDLE = ResourceBundle.getBundle("locale/locale"); // ResourceBundle.getBundle("org.fhaes.gui.locale"); //$NON-NLS-1$
	private static final Logger log = LoggerFactory.getLogger(MainWindow.class);
	
	// Declare static constants
	public static final Color macBGColor = new Color(237, 237, 237);
	
	// Declare local constants
	private final int LARGE_DATASET_THRESHOLD = 250;
	
	// Declare GUI components
	protected JFrame frame;
	private JMenu mnOpenRecent;
	private JMenu mnSave;
	private JComboBox cboFileSort;
	private JSplitPane splitPane;
	private JPanel leftSplitPanel;
	protected ReportPanel rightSplitPanel;
	private FileListModel fileListModel;
	private FileDropTargetListener lstFiles;
	private FeedbackMessagePanel feedbackMessagePanel;
	
	// Declare FHAES actions
	public static FHAESAction actionFileExit;
	private FHAESAction actionFileOpen;
	public static FHAESAction actionHelp;
	public static FHAESAction actionAbout;
	private FHAESAction actionClearList;
	private FHAESAction actionClearCurrent;
	private FHAESAction actionSave;
	private FHAESAction actionSaveCurrentSummary;
	private FHAESAction actionSaveAllSummaries;
	private FHAESAction actionSaveAll;
	private FHAESAction actionSpatialJoin;
	private FHAESAction actionDrawMap;
	protected FHAESAction actionJSEAConfig;
	private FHAESAction actionFileNew;
	protected FHAESAction actionEditFile;
	private FHAESAction actionShowLogViewer;
	private FHAESAction actionMergeFiles;
	private FHAESAction actionCreateEventFile;
	private FHAESAction actionCreateNewEventFile;
	private FHAESAction actionCreateCompositeFile;
	private FHAESAction actionCreateNewCompositeFile;
	private FHAESAction actionCheckForUpdates;
	protected FHAESAction actionSaveResults;
	private FHAESAction actionGenerateSHP;
	private FHAESAction actionFHSampleSize;
	private FHAESAction actionOpenCategoryFile;
	private FHAESAction actionEditCategories;
	private FHAESAction actionShowQuickLaunch;
	protected FHAESAction actionPrefChangeShowQuickLaunch;
	private FHAESAction actionPrefChangeAutoLoadCategories;
	private FHAESAction actionResetAllFeedbackMessagePrefs;
	public static ChartActions chartActions;
	
	// Declare local variables
	private Boolean fileListListenerPaused = false;
	private Integer selectedFileIndex = null;
	private static Log4JViewer logviewer;
	
	/**
	 * Launch the application.
	 */
	public static void main(final String[] args) {
		
		try
		{
			if (args.length > 0)
			{
				// Initialize the MainWindow
				MainWindow.getInstance().doStartup();
			}
			else
			{
				// Initialize the MainWindow and the show quick launch dialog
				MainWindow.getInstance().doStartUpWithQuickLaunchDialog();
			}
			
			for (int i = 0; i < args.length; i++)
			{
				MainWindow.getInstance().loadFileByName(args[i]);
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	/**
	 * Hide the default constructor from other classes.
	 */
	private MainWindow() {
		
		// Do nothing - this is handled in GetInstance()
	}
	
	/**
	 * Singleton instance access method - call this when you want to use the MainWindow.
	 */
	public static MainWindow getInstance() {
		
		if (instanceOfMainWindow == null)
			instanceOfMainWindow = new MainWindow();
			
		return instanceOfMainWindow;
	}
	
	/**
	 * Initializes the MainWindow.
	 * 
	 * @wbp.parser.entryPoint
	 */
	private void doStartup() {
		
		// Redirect System.out calls to logger
		SysOutOverSLF4J.sendSystemOutAndErrToSLF4J();
		
		// Setup our look and feel
		Platform.setLookAndFeel();
		
		// Initialize application
		frame = new JFrame();
		frame.getRootPane().setFocusable(false);
		
		App.init(frame);
		initGUI();
		
		UpdateChecker uc = new UpdateChecker();
		uc.programmaticCheckForUpdates();
	}
	
	/**
	 * Initializes the MainWindow and shows the quick launch dialog.
	 */
	private void doStartUpWithQuickLaunchDialog() {
		
		doStartup();
		new QuickLaunchDialog(true);
	}
	
	/**
	 * Remove all files from the list.
	 */
	private void clearAllFiles() {
		
		if (fileListModel == null || fileListModel.getSize() == 0)
			return;
			
		Object[] options = { "Yes", "No", "Cancel" };
		int response = JOptionPane.showOptionDialog(frame, "Are you sure you want to clear the current file list?", "Confirm",
				JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, // do not use a custom icon
				options, // the titles of buttons
				options[0]); // default button title
				
		if (response == JOptionPane.YES_OPTION)
		{
			fileListModel.clear();
		}
		
		lstFiles.repaint();
	}
	
	/**
	 * Remove the selected file(s) from the list, and warn the user if requested.
	 * 
	 * @param warn
	 */
	private void clearSelectedFiles(boolean warn) {
		
		if (fileListModel == null || fileListModel.getSize() == 0 || lstFiles.getSelectedValuesList().size() == 0)
			return;
			
		if (warn)
		{
			Object[] options = { "Yes", "No", "Cancel" };
			int response = JOptionPane.showOptionDialog(frame, "Are you sure you want to remove the selected file from the list?",
					"Confirm", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, // do not use a custom icon
					options, // the titles of buttons
					options[0]); // default button title
					
			if (response == JOptionPane.YES_OPTION)
			{
				rightSplitPanel.setFile(null);
				List<FHFile> files = lstFiles.getSelectedValuesList();
				
				FileListModel modelcopy = fileListModel;
				for (FHFile f : files)
				{
					modelcopy.removeElement(f);
				}
				
				fileListModel = modelcopy;
				handleFileListChanged();
			}
		}
		else
		{
			rightSplitPanel.setFile(null);
			fileListListenerPaused = true;
			
			List<FHFile> files = lstFiles.getSelectedValuesList();
			for (FHFile f : files)
			{
				fileListModel.removeElement(f);
			}
			
			fileListListenerPaused = false;
			handleFileListChanged();
		}
		
		lstFiles.repaint();
	}
	
	/**
	 * Save reports for all files to disk.
	 */
	private void saveAllOutputFiles() {
		
		File outputFolder = IOUtil.getOutputFolder(frame);
		
		if (outputFolder == null)
			return;
			
		File exceedence = new File(outputFolder + File.separator + "intervals-exceedence.csv");
		File intervals = new File(outputFolder + File.separator + "intervals-summary.csv");
		File seasonality = new File(outputFolder + File.separator + "seasonality.csv");
		
		Boolean confirmedOverwriteOK = false;
		if (exceedence.exists() || intervals.exists() || seasonality.exists())
		{
			Object[] options = { "Overwrite", "No", "Cancel" };
			
			int response = JOptionPane.showOptionDialog(frame,
					"One or more output files already exist.  Are you sure you want to overwrite?", "Confirm",
					JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, // do not use a custom icon
					options, // the titles of buttons
					options[0]); // default button title
					
			if (response != JOptionPane.YES_OPTION)
				return;
				
			confirmedOverwriteOK = true;
		}
		
		saveAllFileSummaries(outputFolder, confirmedOverwriteOK);
	}
	
	/**
	 * Save report for currently selected file to disk.
	 */
	private void saveFileSummary() {
		
		saveFileSummary(null, null);
	}
	
	/**
	 * Save report for specified file to disk.
	 * 
	 * @param fileToSave
	 * @param outputFolder
	 */
	private void saveFileSummary(File fileToSave, File outputFolder) {
		
		if (fileToSave == null)
		{
			fileToSave = (FHFile) lstFiles.getSelectedValue();
		}
		
		if (outputFolder == null)
		{
			outputFolder = IOUtil.getOutputFolder(frame);
		}
		
		log.debug("Saving to " + outputFolder);
		
		// TODO replace access to reports from GUI to File itself
		if (writeTextAreaToDisk(outputFolder + File.separator + fileToSave.getName() + "-summary.txt", rightSplitPanel.txtSummary,
				false) == JOptionPane.CANCEL_OPTION)
			return;
		// if(writeTextAreaToDisk(outputFolder+File.separator+fileToSave.getName()+"-seasonality.txt",
		// rightSplitPanel.txtSeasonality)==JOptionPane.CANCEL_OPTION) return;
		// if(writeTextAreaToDisk(outputFolder+File.separator+fileToSave.getName()+"-intervals.txt",
		// rightSplitPanel.txtIntervals)==JOptionPane.CANCEL_OPTION) return;
	}
	
	/**
	 * Saves the summaries for all files to disk.
	 * 
	 * @param outputFolder
	 * @param confirmedOverwriteOK
	 */
	private void saveAllFileSummaries(File outputFolder, Boolean confirmedOverwriteOK) {
		
		if (outputFolder == null)
			outputFolder = IOUtil.getOutputFolder(frame);
			
		if (outputFolder == null)
			return;
			
		for (int i = 0; i < fileListModel.getSize(); i++)
		{
			lstFiles.setSelectedIndex(i);
			
			File f = fileListModel.getElementAt(i);
			File output = new File(outputFolder + File.separator + f.getName() + "-summary.txt");
			
			if (output.exists() && confirmedOverwriteOK.equals(false))
			{
				Object[] options = { "Overwrite", "No", "Cancel" };
				int response = JOptionPane.showOptionDialog(frame,
						"One or more output files already exist.  Are you sure you want to overwrite?", "Confirm",
						JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, // do not use a custom Icon
						options, // the titles of buttons
						options[0]); // default button title
						
				if (response != JOptionPane.YES_OPTION)
					return;
					
				confirmedOverwriteOK = true;
			}
			
			if (writeTextAreaToDisk(output.getAbsolutePath(), rightSplitPanel.txtSummary,
					confirmedOverwriteOK) == JOptionPane.CANCEL_OPTION)
				return;
		}
	}
	
	/**
	 * Save the summary of the current file to disk.
	 */
	private void saveCurrentFileSummary() {
		
		File outputFile = IOUtil.getOutputFile(new TXTFileFilter());
		
		if (writeTextAreaToDisk(outputFile.getAbsolutePath(), rightSplitPanel.txtSummary, false) == JOptionPane.CANCEL_OPTION)
			return;
	}
	
	/**
	 * Write the specified text area to a file on disk.
	 * 
	 * @param filename
	 * @param ta
	 * @param confirmedOverwriteOK
	 * @return
	 */
	private int writeTextAreaToDisk(String filename, JTextArea ta, Boolean confirmedOverwriteOK) {
		
		if (filename == null || ta == null)
			return JOptionPane.CANCEL_OPTION;
			
		File file = new File(filename);
		if (file.exists() && confirmedOverwriteOK.equals(false))
		{
			Object[] options = { "Overwrite", "No", "Cancel" };
			int response = JOptionPane.showOptionDialog(frame,
					"The file: " + filename + " already exists.\nWould you like to overwrite it?", "Confirm",
					JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, // do not use a custom Icon
					options, // the titles of buttons
					options[0]); // default button title
					
			if (response != JOptionPane.YES_OPTION)
				return response;
		}
		
		FileWriter pw;
		try
		{
			pw = new FileWriter(filename);
			rightSplitPanel.txtSummary.write(pw);
			
		}
		catch (IOException e)
		{
			JOptionPane.showMessageDialog(App.mainFrame, "Error saving file '" + file.getName() + "'\n" + e.getLocalizedMessage());
			e.printStackTrace();
			return JOptionPane.CANCEL_OPTION;
		}
		
		return JOptionPane.OK_OPTION;
	}
	
	/**
	 * Try to load an FHX file.
	 * 
	 * @param file
	 */
	private void loadFile(File file) {
		
		File[] arr = new File[1];
		arr[0] = file;
		loadFiles(arr);
	}
	
	/**
	 * Loads the selected files into FHAES.
	 * 
	 * @param files
	 */
	public void loadFiles(File[] files) {
		
		try
		{
			frame.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
			
			Boolean selectFirst = false;
			
			if (this.fileListModel.getSize() == 0)
			{
				selectFirst = true;
			}
			
			// Warn if loading a large dataset
			if (files.length + fileListModel.getSize() > this.LARGE_DATASET_THRESHOLD)
			{
				Boolean shallWeContinue = LargeDatasetWarningDialog.showWarning(files.length + fileListModel.getSize());
				
				if (shallWeContinue == false)
				{
					return;
				}
			}
			
			FileLoadProgressDialog pd = new FileLoadProgressDialog(leftSplitPanel, files);
			fileListModel.addAllElements(pd.getFileList());
			
			for (File currentFile : files)
			{
				App.prefs.addStringtoPrefArray(PrefKey.RECENT_DOCUMENT_LIST, currentFile.getAbsolutePath(),
						App.prefs.getIntPref(PrefKey.RECENT_DOCUMENT_COUNT, 10));
						
				if (App.prefs.getBooleanPref(PrefKey.AUTO_LOAD_CATEGORIES, true))
				{
					FHFile currentFHFile = new FHFile(currentFile);
					openCategoryFile(new File(currentFHFile.getDefaultCategoryFilePath()));
				}
			}
			
			if (selectFirst)
			{
				rightSplitPanel.setFile(fileListModel.getElementAt(0));
			}
			
			this.cboFileSort.setSelectedIndex(8);
		}
		catch (Exception e)
		{
			log.debug("Exception caught loading files");
			e.printStackTrace();
		}
		finally
		{
			frame.setCursor(Cursor.getDefaultCursor());
		}
	}
	
	/**
	 * Try to load an FHX file specified by a string file name.
	 * 
	 * @param file
	 */
	private void loadFileByName(String file) {
		
		File fhf = null;
		
		try
		{
			fhf = new File(file);
			loadFile(fhf);
		}
		catch (Exception e)
		{
			log.error("Failed to load file " + file);
		}
	}
	
	/**
	 * Open dialog for user to choose FHX files to work on.
	 */
	protected void openFiles() {
		
		String lastVisitedFolder = App.prefs.getPref(PrefKey.PREF_LAST_READ_FOLDER, null);
		JFileChooser fc;
		
		if (lastVisitedFolder != null)
		{
			fc = new JFileChooser(lastVisitedFolder);
		}
		else
		{
			fc = new JFileChooser();
		}
		
		fc.setMultiSelectionEnabled(true);
		fc.setDialogTitle("Open file");
		fc.setAcceptAllFileFilterUsed(false);
		fc.setFileFilter(new FHXFileFilter());
		
		int returnVal = fc.showOpenDialog(frame);
		
		if (returnVal == JFileChooser.APPROVE_OPTION)
		{
			File[] files = fc.getSelectedFiles();
			
			// Set lastPathVisited
			App.prefs.setPref(PrefKey.PREF_LAST_READ_FOLDER, files[0].getParent());
			loadFiles(files);
		}
		
		log.debug("Finished loading files into dialog");
		lstFiles.repaint();
	}
	
	/**
	 * Opens a category file to attach attributes to the selected FHX file.
	 */
	private void openCategoryFile(File categoryFile) {
		
		// Set lastPathVisited
		App.prefs.setPref(PrefKey.PREF_LAST_READ_FOLDER, categoryFile.getParent());
		
		FHCategoryReader reader = new FHCategoryReader(categoryFile);
		int numFilesLoaded = fileListModel.getCompleteFileList().size();
		
		for (int i = 0; i < numFilesLoaded; i++)
		{
			FHFile currentFile = fileListModel.getCompleteFileList().get(i);
			
			if (currentFile.getFileNameWithoutExtension().equals(reader.getNameOfCorrespondingFHXFile()))
			{
				currentFile.attachCategoriesToFile(reader.getCategoryEntryList());
				currentFile.setCategoryFilePath(categoryFile.getAbsolutePath());
				
				log.debug("Categories successfully attached to FHX file: " + currentFile.getFileNameWithoutExtension() + ".");
				return;
			}
		}
		
		log.debug("No FHX files are loaded which correspond to the selected category file.");
	}
	
	/**
	 * Open the FHRecorder frame with the specified file. If f is null then it opens with an empty new file.
	 * 
	 * @param f
	 */
	protected void openFileRecorder(File f) {
		
		frame.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		
		FireHistoryRecorder primaryWindow = new FireHistoryRecorder();
		FileController.thePrimaryWindow = primaryWindow;
		primaryWindow.setLocationRelativeTo(null);
		primaryWindow.setIconImage(Builder.getApplicationIcon());
		primaryWindow.pack();
		
		try
		{
			if (f == null)
			{
				// Creating a new file
				FileController.newFile();
			}
			else
			{
				// Editing an existing file
				FileController.importFile(f);
			}
		}
		catch (CompositeFileException ex)
		{
			// File being edited as a composite file so complain and close
			frame.setCursor(Cursor.getDefaultCursor());
			
			Object[] options = { "Yes", "No", "Cancel" };
			int n = JOptionPane.showOptionDialog(frame,
					"This file appears to be a composite file.  The FHAES editor is only designed to\n"
							+ "edit standard raw data files not composite files.  Erroneous composite files\n"
							+ "should be rebuilt from the corrected raw data files. If you continue, the meaning\n"
							+ "of your file may be unintentionally altered.\n\n" + "Would you like to edit the file anyway?",
					"Composite File Detected", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[2]);
			if (n == JOptionPane.YES_OPTION)
			{
				FileController.setOverrideCompositeWarnings(true);
				
				try
				{
					FileController.importFile(f);
				}
				catch (CompositeFileException e)
				{
					feedbackMessagePanel.updateFeedbackMessage(FeedbackMessageType.ERROR, FeedbackDisplayProtocol.MANUAL_HIDE,
							"Composite file exception thrown despite trying to override.");
							
					return;
				}
				
				FileController.setOverrideCompositeWarnings(false);
			}
			else
			{
				return;
			}
			
		}
		catch (Exception ex)
		{
			// Catch any other sort of error
			frame.setCursor(Cursor.getDefaultCursor());
			
			feedbackMessagePanel.updateFeedbackMessage(FeedbackMessageType.ERROR, FeedbackDisplayProtocol.MANUAL_HIDE,
					"Error opening file in Fire History Recorder.");
					
			ex.printStackTrace();
			return;
		}
		
		// Open FHRecorder dialog
		frame.setCursor(Cursor.getDefaultCursor());
		primaryWindow.setVisible(true);
		
		// Grab the result from the FHRecorder dialog
		File savedFile = FileController.getSavedFile();
		
		// Update the file in our existing list of files if it has been updated
		if (FileController.isChangedSinceOpened() && savedFile != null)
		{
			Integer index = null;
			
			// File was changed by FHRecorder so first search for this file in our existing list of files
			for (int i = 0; i < fileListModel.getSize(); i++)
			{
				File currentFile = fileListModel.getElementAt(i);
				
				if (currentFile.getAbsolutePath().equals(savedFile.getAbsolutePath()))
				{
					index = i;
					break;
				}
			}
			
			if (index != null)
			{
				// File saved by FHRecorder is already in list so replace
				// with the new version
				fileListModel.removeElementAt(index);
				fileListModel.addElementAt(index, new FHFile(savedFile.getAbsolutePath()));
				lstFiles.setSelectedIndex(index);
			}
			else
			{
				// File isn't in list (i.e. it's newly created so just add)
				fileListModel.addElement(new FHFile(savedFile.getAbsoluteFile()));
			}
			
			// Reload the category file for this FHX file if it exists and if the auto-load preference is set
			if (App.prefs.getBooleanPref(PrefKey.AUTO_LOAD_CATEGORIES, true))
			{
				FHFile currentFHFile = new FHFile(f);
				openCategoryFile(new File(currentFHFile.getDefaultCategoryFilePath()));
			}
			
			// Go ahead and force the GUI to update by selecting this file
			handleFileListChanged();
			handleFileSelectionChanged();
		}
	}
	
	/**
	 * Opens FHRecorder to allow editing of the currently selected file, if one has been selected.
	 */
	private void editSelectedFile() {
		
		try
		{
			FHFile file = (FHFile) this.lstFiles.getSelectedValue();
			
			if (file != null)
			{
				openFileRecorder(file);
			}
		}
		catch (Exception e)
		{
			log.error("Caught exception while trying to edit file.");
		}
	}
	
	/**
	 * Get an array of files that are valid and have fire events.
	 * 
	 * @return
	 */
	private File[] getSelectedValidFiles() {
		
		ArrayList<FHFile> tempList = new ArrayList<FHFile>();
		
		for (Object f : lstFiles.getSelectedValuesList())
		{
			FHFile file = (FHFile) f;
			if (file.isValidFHXFile())
			{
				tempList.add(file);
			}
		}
		
		return tempList.toArray(new File[tempList.size()]);
	}
	
	/**
	 * Get an array of files that are valid and have fire events.
	 * 
	 * @return
	 */
	private File[] getSelectedValidFilesWithEvents() {
		
		ArrayList<FHFile> tempList = new ArrayList<FHFile>();
		
		for (Object f : lstFiles.getSelectedValuesList())
		{
			FHFile file = (FHFile) f;
			if (file.isValidFHXFile() && file.hasFireEventsOrInjuries())
			{
				tempList.add(file);
			}
		}
		
		return tempList.toArray(new File[tempList.size()]);
	}
	
	/**
	 * TODO
	 */
	private void drawMap() {
		
		URI uri;
		try
		{
			int goodMarkers = 0;
			int badMarkers = 0;
			String markers = "";
			
			if (lstFiles.getSelectedValuesList().size() > 0)
			{
				
				List<FHFile> files = lstFiles.getSelectedValuesList();
				for (FHFile f : files)
				{
					if (f.getFirstLatitude() != null && f.getFirstLongitude() != null)
					{
						markers += URLEncoder.encode(f.getName(), "UTF-8").replace(",", "_").replace(";", "_") + "," + f.getFirstLatitude()
								+ "," + f.getFirstLongitude() + ";";
						goodMarkers++;
					}
					else
					{
						badMarkers++;
					}
				}
				
				if (goodMarkers == 0)
				{
					JOptionPane.showMessageDialog(frame, "Unable to parse coordinates from latitude and longitude strings.\n"
							+ "Coordinates have been stored in a non-standard format.");
					return;
				}
				if (badMarkers > 0)
				{
					JOptionPane.showMessageDialog(frame, "Failed to parse coordinates from " + badMarkers + " file(s). The map will only\n"
							+ "contain coordinates from " + goodMarkers + " file(s).");
				}
				
				uri = new URI("http://map.fhaes.org/?markers=" + markers);
				
				if (uri.toString().length() > 8177)
				{
					JOptionPane.showMessageDialog(frame, "You have selected more markers than can be plotted by the online map.\n"
							+ "Please select fewer files are try again");
					return;
				}
				log.debug("Length of URI = " + uri.toString().length());
				
				// Open in browser
				Desktop.getDesktop().browse(uri);
			}
			
		}
		catch (URISyntaxException e1)
		{
			log.error("Invalid URI");
			e1.printStackTrace();
		}
		catch (IOException e1)
		{
			e1.printStackTrace();
		}
	}
	
	/**
	 * Open FHChart for currently selected file.
	 */
	@SuppressWarnings("unused")
	private void plotChart() {
		
		if (lstFiles.getSelectedValue() == null)
			return;
			
		frame.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		// PlotWindow plotWindow = new PlotWindow(frame, (FHFile) lstFiles.getSelectedValue());
		frame.setCursor(Cursor.getDefaultCursor());
		
		// plotWindow.setVisible(true);
		// plotWindow.setExtendedState(plotWindow.getExtendedState() | JFrame.MAXIMIZED_BOTH);
	}
	
	/**
	 * Handles when a preference change occurs on MainWindow.
	 */
	@Override
	public void prefChanged(PrefsEvent e) {
		
		log.debug("Preference change for key " + e.getPref() + " picked up by MainWindow");
		
		// Repaint file list if event type has changed as icons will need updating
		if (e.getPref().equals(PrefKey.EVENT_TYPE_TO_PROCESS))
		{
			repaintFileList();
		}
		
		// Update the document list menu
		if (e.getPref().equals(PrefKey.RECENT_DOCUMENT_LIST))
		{
			this.updateRecentDocsMenu();
		}
		
		// Select current file in list again to update reports
		if (this.lstFiles.getSelectedIndex() != -1)
		{
			this.rightSplitPanel.setFile((FHFile) this.lstFiles.getSelectedValue());
		}
	}
	
	/**
	 * Sets the cursor to busy or default state as determined by the input parameter.
	 * 
	 * @param b
	 */
	public void setBusyCursor(boolean b) {
		
		if (b)
		{
			frame.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		}
		else
		{
			frame.setCursor(Cursor.getDefaultCursor());
		}
	}
	
	/**
	 * Returns false if the file list is empty or has not been initialized. Otherwise returns true.
	 * 
	 * @return boolean value indicating whether or not the list contains at least one element
	 */
	protected boolean isFileListPopulated() {
		
		if (fileListModel == null || fileListModel.getSize() == 0)
		{
			return false;
		}
		
		return true;
	}
	
	/**
	 * Repaints the file list.
	 */
	protected void repaintFileList() {
		
		log.debug("Repainting file list");
		
		this.lstFiles.repaint(this.lstFiles.getCellBounds(0, this.fileListModel.getSize() - 1));
	}
	
	/**
	 * Run when file list is changed.
	 */
	private void handleFileListChanged() {
		
		if (fileListListenerPaused)
			return;
			
		boolean isFileListPopulated = fileListModel.getSize() > 0;
		
		// Enabled/Disable buttons depending
		actionClearList.setEnabled(isFileListPopulated);
		actionClearCurrent.setEnabled(isFileListPopulated);
		mnSave.setEnabled(isFileListPopulated);
		actionSaveAll.setEnabled(isFileListPopulated);
		actionSaveCurrentSummary.setEnabled(isFileListPopulated);
		actionSaveAllSummaries.setEnabled(isFileListPopulated);
		actionGenerateSHP.setEnabled(isFileListPopulated);
		rightSplitPanel.actionCopy.setEnabled(isFileListPopulated);
		rightSplitPanel.actionSelectAll.setEnabled(isFileListPopulated);
		rightSplitPanel.actionParamConfig.setEnabled(isFileListPopulated);
		actionCreateCompositeFile.setEnabled(isFileListPopulated);
		actionCreateEventFile.setEnabled(isFileListPopulated);
		// rightSplitPanel.actionResultsHelp.setEnabled(isFileListPopulated);
		rightSplitPanel.panelResults.showRunAnalysisTab();
		
		// If list is empty set file to null
		if (!isFileListPopulated)
			this.rightSplitPanel.setFile(null);
			
		this.rightSplitPanel.setFiles(fileListModel.getValidFileListWithEvents());
		
		if (isFileListPopulated)
		{
			log.debug("Current selected file index = " + lstFiles.getSelectedIndex());
			if (this.lstFiles.getSelectedIndex() == -1)
			{
				this.lstFiles.setSelectedIndex(0);
			}
			else if (!this.rightSplitPanel.isFilePopulated())
			{
				this.lstFiles.setSelectedIndex(0);
			}
			
			chartActions.setNeoChart(rightSplitPanel.panelChart);
		}
		else
		{
			chartActions.setNeoChart(null);
		}
		
		// Enable/disable file sorting depending on number of files loaded
		this.cboFileSort.setEnabled(this.lstFiles.getModel().getSize() > 1);
	}
	
	/**
	 * Handles when an FHX file is selected on the MainWindow.
	 */
	private void handleFileSelectionChanged() {
		
		try
		{
			boolean isFileSelected = (lstFiles.getSelectedValue() != null);
			
			// Actions requiring a file to be selected
			actionOpenCategoryFile.setEnabled(isFileSelected);
			actionDrawMap.setEnabled(isFileSelected);
			actionClearCurrent.setEnabled(isFileSelected);
			actionEditFile.setEnabled(isFileSelected);
			actionEditCategories.setEnabled(isFileSelected);
			actionCreateEventFile.setEnabled(isFileSelected);
			actionCreateCompositeFile.setEnabled(isFileSelected);
			
			// Override and disable these actions if multiple files are selected
			if (lstFiles.getSelectedValuesList().size() > 1)
			{
				actionEditFile.setEnabled(false);
				actionSave.setEnabled(false);
			}
			
			// Actions *requiring* multiple files selected
			actionMergeFiles.setEnabled(lstFiles.getSelectedValuesList().size() > 1);
			actionSpatialJoin.setEnabled(lstFiles.getSelectedValuesList().size() > 1);
			
			// Update report panels
			rightSplitPanel.setFile((FHFile) lstFiles.getSelectedValue());
			
			// Handle chart actions
			if (isFileSelected)
			{
				chartActions.setNeoChart(rightSplitPanel.panelChart);
			}
			else
			{
				chartActions.setNeoChart(null);
			}
		}
		catch (Exception e)
		{
			log.info("Error caught and ignored while evaluating file list");
		}
	}
	
	/**
	 * Adds a popup menu to the input component.
	 * 
	 * @param component
	 * @param popup
	 */
	private void addPopup(final Component component, final JPopupMenu popup) {
		
		component.addMouseListener(new MouseAdapter() {
			
			@Override
			public void mousePressed(MouseEvent e) {
				
				if (e.getClickCount() > 1)
				{
					editSelectedFile();
					return;
				}
				
				if (e.isPopupTrigger())
				{
					int row = lstFiles.locationToIndex(e.getPoint());
					lstFiles.setSelectedIndex(row);
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
	 * TODO
	 */
	private void updateRecentDocsMenu() {
		
		mnOpenRecent.removeAll();
		int i = 1;
		
		if (App.prefs.getStringArrayPref(PrefKey.RECENT_DOCUMENT_LIST) != null)
		{
			mnOpenRecent.setEnabled(true);
			
			List<String> recentfiles = App.prefs.getStringArrayPref(PrefKey.RECENT_DOCUMENT_LIST);
			for (String filename : recentfiles)
			{
				final File file = new File(filename);
				JMenuItem recentFile = null;
				
				if (!file.exists())
				{
					recentFile = new JMenuItem(i + ": " + org.fhaes.util.StringUtils.shortedFileName(file, 29) + "  [missing]");
				}
				else
				{
					recentFile = new JMenuItem(i + ": " + org.fhaes.util.StringUtils.shortedFileName(file, 40));
				}
				
				recentFile.addActionListener(new ActionListener() {
					
					@Override
					public void actionPerformed(ActionEvent evt) {
						
						if (file.exists())
						{
							loadFileByName(file.getAbsolutePath());
						}
						else
						{
							feedbackMessagePanel.updateFeedbackMessage(FeedbackMessageType.ERROR, FeedbackDisplayProtocol.AUTO_HIDE,
									"The specified file '" + file.getName() + "' no longer exists.");
									
							updateRecentDocsMenu();
						}
					}
				});
				
				if (!file.exists())
					recentFile.setEnabled(false);
					
				mnOpenRecent.add(recentFile);
				i++;
			}
			
			mnOpenRecent.addSeparator();
			JMenuItem clearHistory = new JMenuItem("Clear history");
			clearHistory.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent arg0) {
					
					App.prefs.setStringArrayPref(PrefKey.RECENT_DOCUMENT_LIST, null);
				}
			});
			
			mnOpenRecent.add(clearHistory);
		}
		else
		{
			mnOpenRecent.setEnabled(false);
		}
	}
	
	/**
	 * Gets the feedbackMessagePanel instance.
	 */
	public FeedbackMessagePanel getFeedbackMessagePanel() {
		
		return feedbackMessagePanel;
	}
	
	/**
	 * Initialize the main frame.
	 */
	private void initGUI() {
		
		logviewer = new Log4JViewer();
		log.debug("Initializing FHAES application");
		
		// Setup properties for the main frame
		frame.getContentPane().setLayout(new MigLayout("hidemode 2,insets 0", "[700:700,grow]", "[][500:500,grow,fill]"));
		frame.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		frame.setIconImage(Builder.getApplicationIcon());
		frame.setMinimumSize(new Dimension(800, 600));
		frame.setTitle(BUNDLE.getString("MainWindow.frame.title")); //$NON-NLS-1$
		frame.addWindowListener(new WindowAdapter() {
			
			@Override
			public void windowClosing(WindowEvent e) {
				
				// Save frame size and position on close
				Rectangle bounds = frame.getBounds();
				App.prefs.setIntPref(PrefKey.SCREEN_BOUNDS_X, ((Double) bounds.getX()).intValue());
				App.prefs.setIntPref(PrefKey.SCREEN_BOUNDS_Y, ((Double) bounds.getY()).intValue());
				App.prefs.setIntPref(PrefKey.SCREEN_WIDTH, ((Double) bounds.getWidth()).intValue());
				App.prefs.setIntPref(PrefKey.SCREEN_HEIGHT, ((Double) bounds.getHeight()).intValue());
				boolean b = frame.getExtendedState() == JFrame.MAXIMIZED_BOTH;
				App.prefs.setBooleanPref(PrefKey.SCREEN_MAXIMIZED, b);
				System.exit(0);
			}
		});
		
		initActions();
		
		if (Platform.isOSX())
		{
			OSX.configureMenus(actionAbout, actionFileExit);
		}
		
		fileListModel = new FileListModel();
		
		feedbackMessagePanel = new FeedbackMessagePanel();
		frame.getContentPane().add(feedbackMessagePanel, "cell 0 0,grow");
		
		splitPane = new JSplitPane();
		splitPane.setResizeWeight(0);
		splitPane.setOneTouchExpandable(true);
		
		frame.getContentPane().add(splitPane, "cell 0 1,growx,aligny top");
		
		rightSplitPanel = new ReportPanel();
		if (Platform.isOSX())
			rightSplitPanel.setBackground(macBGColor);
			
		splitPane.setRightComponent(rightSplitPanel);
		
		leftSplitPanel = new JPanel();
		if (Platform.isOSX())
			leftSplitPanel.setBackground(macBGColor);
		leftSplitPanel.setMinimumSize(new Dimension(200, 200));
		splitPane.setLeftComponent(leftSplitPanel);
		leftSplitPanel.setLayout(new BorderLayout(0, 0));
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		leftSplitPanel.add(scrollPane);
		
		lstFiles = new FileDropTargetListener();
		lstFiles.setModel(fileListModel);
		lstFiles.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		lstFiles.setCellRenderer(new FHFileListCellRenderer());
		lstFiles.addListSelectionListener(new ListSelectionListener() {
			
			@Override
			public void valueChanged(ListSelectionEvent evt) {
				
				handleFileSelectionChanged();
			}
			
		});
		
		fileListModel.addListDataListener(new ListDataListener() {
			
			@Override
			public void contentsChanged(ListDataEvent evt) {
				
				handleFileListChanged();
			}
			
			@Override
			public void intervalAdded(ListDataEvent evt) {
				
				handleFileListChanged();
			}
			
			@Override
			public void intervalRemoved(ListDataEvent evt) {
				
				handleFileListChanged();
				rightSplitPanel.setFile(null);
				
				// Try and select the previous item in the list
				if (fileListModel == null)
				{
					rightSplitPanel.setFile(null);
				}
				if (fileListModel.getSize() >= selectedFileIndex - 1)
				{
					lstFiles.setSelectedIndex(selectedFileIndex - 1);
				}
				else if (fileListModel.getSize() > 0)
				{
					lstFiles.setSelectedIndex(0);
				}
				else
				{
					lstFiles.setSelectedIndex(-1);
					rightSplitPanel.setFile(null);
				}
			}
			
		});
		
		lstFiles.setDragEnabled(true);
		lstFiles.setDropMode(DropMode.INSERT);
		
		lstFiles.addKeyListener(new KeyListener() {
			
			@Override
			public void keyPressed(KeyEvent arg0) {} // Ignored
			
			@Override
			public void keyReleased(KeyEvent evt) {
				
				if (evt.getKeyCode() == KeyEvent.VK_DELETE || evt.getKeyCode() == KeyEvent.VK_BACK_SPACE)
				{
					clearSelectedFiles(false);
				}
			}
			
			@Override
			public void keyTyped(KeyEvent evt) {} // Ignored
			
		});
		
		scrollPane.setViewportView(lstFiles);
		
		JPopupMenu popupMenu = new JPopupMenu();
		addPopup(lstFiles, popupMenu);
		
		FHAESMenuItem saveCurrent = new FHAESMenuItem(actionSave);
		FHAESMenuItem saveAll = new FHAESMenuItem(actionSaveAll);
		FHAESMenuItem editFile = new FHAESMenuItem(actionEditFile);
		FHAESMenuItem mergeFiles = new FHAESMenuItem(this.actionMergeFiles);
		FHAESMenuItem createEventFile = new FHAESMenuItem(this.actionCreateEventFile);
		FHAESMenuItem sampleSize = new FHAESMenuItem(this.actionFHSampleSize);
		FHAESMenuItem mntmClear = new FHAESMenuItem(actionClearCurrent);
		FHAESMenuItem mntmClearAll = new FHAESMenuItem(actionClearList);
		
		popupMenu.add(saveCurrent);
		popupMenu.add(saveAll);
		popupMenu.addSeparator();
		popupMenu.add(editFile);
		popupMenu.add(mergeFiles);
		popupMenu.add(createEventFile);
		popupMenu.add(sampleSize);
		popupMenu.addSeparator();
		popupMenu.add(mntmClear);
		popupMenu.add(mntmClearAll);
		
		JPanel panel = new JPanel();
		if (Platform.isOSX())
			panel.setBackground(macBGColor);
			
		leftSplitPanel.add(panel, BorderLayout.SOUTH);
		panel.setLayout(new MigLayout("", "[][grow]", "[]"));
		
		JLabel label = new JLabel("Order:");
		panel.add(label, "cell 0 0,alignx trailing");
		
		cboFileSort = new JComboBox();
		cboFileSort.setModel(new DefaultComboBoxModel(new String[] { "Name asc.", "Name desc.", "First year asc.", "First year desc.",
				"Last year asc.", "Last year desc.", "File validity asc.", "File validity desc.", "None" }));
		cboFileSort.setSelectedIndex(8);
		cboFileSort.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				
				if (cboFileSort.getSelectedIndex() == 0)
				{
					fileListModel.sortAscending(new FHFileNameComparator());
					lstFiles.repaint();
				}
				if (cboFileSort.getSelectedIndex() == 1)
				{
					fileListModel.sortDescending(new FHFileNameComparator());
					lstFiles.repaint();
				}
				if (cboFileSort.getSelectedIndex() == 2)
				{
					fileListModel.sortAscending(new FHFileFirstYearComparator());
					lstFiles.repaint();
				}
				if (cboFileSort.getSelectedIndex() == 3)
				{
					fileListModel.sortDescending(new FHFileFirstYearComparator());
					lstFiles.repaint();
				}
				if (cboFileSort.getSelectedIndex() == 4)
				{
					fileListModel.sortAscending(new FHFileLastYearComparator());
					lstFiles.repaint();
				}
				if (cboFileSort.getSelectedIndex() == 5)
				{
					fileListModel.sortDescending(new FHFileLastYearComparator());
					lstFiles.repaint();
				}
				if (cboFileSort.getSelectedIndex() == 6)
				{
					fileListModel.sortAscending(new FHFileValidityComparator());
					lstFiles.repaint();
				}
				if (cboFileSort.getSelectedIndex() == 7)
				{
					fileListModel.sortDescending(new FHFileValidityComparator());
					lstFiles.repaint();
				}
			}
		});
		
		panel.add(cboFileSort, "cell 1 0,growx");
		
		initMenu();
		initToolbar();
		
		if (Platform.isOSX())
		{
			MacUtils.makeWindowLeopardStyle(frame.getRootPane());
			frame.setBackground(macBGColor);
			splitPane.setBackground(macBGColor);
			rightSplitPanel.setBackground(macBGColor);
			leftSplitPanel.setBackground(macBGColor);
		}
		
		frame.pack();
		
		// Try and restore size and location of frame
		if (App.prefs.getBooleanPref(PrefKey.SCREEN_MAXIMIZED, false))
		{
			// Restore maximized if necessary
			frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
		}
		else
		{
			// Set size and location to the same as last session or default if this is the first session
			frame.setBounds(App.prefs.getIntPref(PrefKey.SCREEN_BOUNDS_X, 100), App.prefs.getIntPref(PrefKey.SCREEN_BOUNDS_Y, 100),
					App.prefs.getIntPref(PrefKey.SCREEN_WIDTH, 850), App.prefs.getIntPref(PrefKey.SCREEN_HEIGHT, 600));
					
			if (App.prefs.getIntPref(PrefKey.SCREEN_BOUNDS_X, 0) == 0 && App.prefs.getIntPref(PrefKey.SCREEN_BOUNDS_Y, 0) == 0)
			{
				// No location stored so center instead
				frame.setLocationRelativeTo(null);
			}
		}
		
		splitPane.setDividerLocation(210);
		
		handleFileListChanged();
		App.prefs.addPrefsListener(this);
		
		frame.setVisible(true);
	}
	
	/**
	 * Initialize the menu and toolbar actions.
	 */
	private void initActions() {
		
		chartActions = new ChartActions(null);
		
		this.actionEditCategories = new FHAESAction("Edit categories", "category.png") {
			
			private static final long serialVersionUID = 1L;
			
			@Override
			public void actionPerformed(ActionEvent event) {
				
				FHFile selectedFile = (FHFile) lstFiles.getSelectedValue();
				
				if (selectedFile.isValidFHXFile())
				{
					// Begin editing of categories for the selected file
					new CategoryEditor(selectedFile);
					
					// Reload the categories from the file as it was saved in the category editor
					selectedFile.clearAllCategoryEntries();
					openCategoryFile(new File(selectedFile.getCategoryFilePath()));
				}
				else
				{
					feedbackMessagePanel.updateFeedbackMessage(FeedbackMessageType.ERROR, FeedbackDisplayProtocol.AUTO_HIDE,
							"Cannot edit categories for an invalid FHX file.");
				}
			}
		};
		actionEditCategories.setEnabled(false);
		
		this.actionOpenCategoryFile = new FHAESAction("Open category file", "formatcsv.png") {
			
			private static final long serialVersionUID = 1L;
			
			@Override
			public void actionPerformed(ActionEvent event) {
				
				String lastVisitedFolder = App.prefs.getPref(PrefKey.PREF_LAST_READ_FOLDER, null);
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
				fc.setDialogTitle("Open category file...");
				fc.setAcceptAllFileFilterUsed(false);
				fc.setFileFilter(new CSVFileFilter());
				
				int returnVal = fc.showOpenDialog(frame);
				
				if (returnVal == JFileChooser.APPROVE_OPTION)
				{
					openCategoryFile(fc.getSelectedFile());
				}
			}
		};
		actionOpenCategoryFile.setEnabled(false);
		
		this.actionFHSampleSize = new FHAESAction("Sample size analysis", "samplesize.png") { //$NON-NLS-1$
			
			private static final long serialVersionUID = 1L;
			
			@Override
			public void actionPerformed(ActionEvent event) {
				
				FHSampleSize ssiz = new FHSampleSize(frame);
				ssiz.openFile((File) lstFiles.getSelectedValue());
			}
		};
		// actionFHSampleSize.setEnabled(false);
		
		this.actionEditFile = new FHAESAction("Edit file", "edit.png") { //$NON-NLS-1$
			
			private static final long serialVersionUID = 1L;
			
			@Override
			public void actionPerformed(ActionEvent event) {
				
				editSelectedFile();
			}
		};
		actionEditFile.setEnabled(false);
		
		this.actionFileOpen = new FHAESAction("Open FHX file", "fileopen.png") { //$NON-NLS-1$
			
			private static final long serialVersionUID = 1L;
			
			@Override
			public void actionPerformed(ActionEvent event) {
				
				openFiles();
			}
		};
		
		this.actionFileNew = new FHAESAction("FHX file", "file.png") { //$NON-NLS-1$
			
			private static final long serialVersionUID = 1L;
			
			@Override
			public void actionPerformed(ActionEvent event) {
				
				openFileRecorder(null);
			}
		};
		
		actionFileExit = new FHAESAction(BUNDLE.getString("MainWindow.mntmExit.text"), "close.png") { //$NON-NLS-1$
			
			private static final long serialVersionUID = 1L;
			
			@Override
			public void actionPerformed(ActionEvent event) {
				
				frame.dispose();
			}
		};
		
		actionAbout = new FHAESAction(BUNDLE.getString("MainWindow.mntmAboutFhaes.text"), "info.png") { //$NON-NLS-1$
			
			private static final long serialVersionUID = 1L;
			
			@Override
			public void actionPerformed(ActionEvent event) {
				
				AboutDialog about = new AboutDialog(frame);
				about.setVisible(true);
			}
		};
		
		actionHelp = new FHAESAction(BUNDLE.getString("MainWindow.mnHelp.text"), "help.png") { //$NON-NLS-1$
			
			private static final long serialVersionUID = 1L;
			
			@Override
			public void actionPerformed(ActionEvent event) {
				
				Platform.browseWebpage(RemoteHelp.FHAES_HELP_HOME, frame); // $NON-NLS-1$
			}
		};
		
		this.actionClearList = new FHAESAction(BUNDLE.getString("MainWindow.mntmClearFileList.text"), "edit_clear.png", "Clear files") { //$NON-NLS-1$
			
			private static final long serialVersionUID = 1L;
			
			@Override
			public void actionPerformed(ActionEvent event) {
				
				clearAllFiles();
			}
		};
		
		this.actionClearCurrent = new FHAESAction(BUNDLE.getString("MainWindow.mntmClear.text"), "delete.png") { //$NON-NLS-1$
			
			private static final long serialVersionUID = 1L;
			
			@Override
			public void actionPerformed(ActionEvent event) {
				
				clearSelectedFiles(true);
			}
		};
		actionClearCurrent.setEnabled(false);
		
		this.actionDrawMap = new FHAESAction(BUNDLE.getString("MainWindow.mntmDrawMap.text"), "map.png") { //$NON-NLS-1$
			
			private static final long serialVersionUID = 1L;
			
			@Override
			public void actionPerformed(ActionEvent event) {
				
				drawMap();
			}
		};
		actionDrawMap.setEnabled(false);
		
		this.actionSave = new FHAESAction(BUNDLE.getString("MainWindow.mntmSave.text"), "save.png") { //$NON-NLS-1$
			
			private static final long serialVersionUID = 1L;
			
			@Override
			public void actionPerformed(ActionEvent event) {
				
				saveFileSummary();
			}
		};
		actionSave.setEnabled(false);
		
		this.actionSaveCurrentSummary = new FHAESAction("Current file summary", "blank.png") {
			
			private static final long serialVersionUID = 1L;
			
			@Override
			public void actionPerformed(ActionEvent event) {
				
				saveCurrentFileSummary();
			}
		};
		actionSaveCurrentSummary.setEnabled(false);
		
		this.actionSaveAllSummaries = new FHAESAction("All file summaries", "blank.png") {
			
			private static final long serialVersionUID = 1L;
			
			@Override
			public void actionPerformed(ActionEvent event) {
				
				saveAllFileSummaries(null, false);
			}
		};
		actionSaveAllSummaries.setEnabled(false);
		
		this.actionSaveAll = new FHAESAction(BUNDLE.getString("MainWindow.mntmSaveAll.text"), "save_all.png") { //$NON-NLS-1$
			
			private static final long serialVersionUID = 1L;
			
			@Override
			public void actionPerformed(ActionEvent event) {
				
				saveAllOutputFiles();
			}
		};
		
		this.actionJSEAConfig = new FHAESAction("Run jSEA analysis", "jsea.png", "jSEA") {
			
			private static final long serialVersionUID = 1L;
			
			@Override
			public void actionPerformed(ActionEvent event) {
				
				new JSEAFrame(frame);
			}
		};
		
		this.actionShowLogViewer = new FHAESAction("Error log viewer", "bug.png") {
			
			private static final long serialVersionUID = 1L;
			
			@Override
			public void actionPerformed(ActionEvent event) {
				
				logviewer.setVisible(true);
			}
		};
		
		this.actionCheckForUpdates = new FHAESAction("Check for updates", "update.png") {
			
			private static final long serialVersionUID = 1L;
			
			@Override
			public void actionPerformed(ActionEvent event) {
				
				UpdateChecker uc = new UpdateChecker();
				uc.manualCheckForUpdates();
			}
		};
		
		this.actionMergeFiles = new FHAESAction("Merge selected files", "merge.png", "Merge files") {
			
			private static final long serialVersionUID = 1L;
			
			@Override
			public void actionPerformed(ActionEvent event) {
				
				TemporalFilterDialog dialog = new TemporalFilterDialog();
				dialog.setIconImage(Builder.getApplicationIcon());
				dialog.setModal(true);
				dialog.setVisible(true);
				
				if (!dialog.success())
					return;
					
				try
				{
					File file = FHOperations.joinFiles(frame, getSelectedValidFiles(), dialog.getStartYear(), dialog.getEndYear(), 1);
					
					if (file != null)
					{
						Object[] options = { "Yes", "No", "Cancel" };
						int response = JOptionPane.showOptionDialog(frame, "Would you like to add this file to you project now?", "Confirm",
								JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, // do not use a
								// custom
								// Icon
								options, // the titles of buttons
								options[0]); // default button title
								
						if (response == JOptionPane.YES_OPTION)
						{
							loadFile(file);
						}
					}
				}
				catch (Exception e)
				{
					log.error("Error merging files");
					e.printStackTrace();
				}
			}
		};
		actionMergeFiles.setEnabled(false);
		
		this.actionCreateEventFile = new FHAESAction("Create event file", "event.png", "Event file") {
			
			private static final long serialVersionUID = 1L;
			
			@Override
			public void actionPerformed(ActionEvent event) {
				
				CompositeFilterDialog dialog = new CompositeFilterDialog(true);
				dialog.setIconImage(Builder.getApplicationIcon());
				dialog.setModal(true);
				dialog.setTitle("Event File Options");
				dialog.setVisible(true);
				dialog.setLocationRelativeTo(splitPane);
				
				if (!dialog.success())
					return;
					
				FHOperations.createEventFile(frame, getSelectedValidFilesWithEvents(), dialog.getStartYear(), dialog.getEndYear(),
						dialog.getFireFilterType(), dialog.getFireFilterValue(), dialog.getMinNumberOfSamples(), dialog.getComments());
			}
		};
		actionCreateEventFile.setEnabled(false);
		
		actionCreateNewEventFile = new FHAESAction("Event file", "event.png", "Event file") {
			
			private static final long serialVersionUID = 1L;
			
			@Override
			public void actionPerformed(ActionEvent event) {
				
				String lastVisitedFolder = App.prefs.getPref(PrefKey.PREF_LAST_READ_FOLDER, null);
				JFileChooser fc;
				
				if (lastVisitedFolder != null)
				{
					fc = new JFileChooser(lastVisitedFolder);
				}
				else
				{
					fc = new JFileChooser();
				}
				
				fc.setMultiSelectionEnabled(true);
				fc.setFileFilter(new FHXFileFilter());
				fc.setDialogTitle("Open file...");
				
				int returnVal = fc.showOpenDialog(frame);
				if (returnVal != JFileChooser.APPROVE_OPTION)
					return;
					
				File[] files = fc.getSelectedFiles();
				
				// Set lastPathVisited
				App.prefs.setPref(PrefKey.PREF_LAST_READ_FOLDER, files[0].getParent());
				
				CompositeFilterDialog dialog = new CompositeFilterDialog(true);
				dialog.setIconImage(Builder.getApplicationIcon());
				dialog.setModal(true);
				dialog.setVisible(true);
				
				if (!dialog.success())
					return;
					
				try
				{
					FHOperations.createEventFile(frame, files, dialog.getStartYear(), dialog.getEndYear(), dialog.getFireFilterType(),
							dialog.getFireFilterValue(), dialog.getMinNumberOfSamples(), dialog.getComments());
				}
				catch (Exception e)
				{
					feedbackMessagePanel.updateFeedbackMessage(FeedbackMessageType.ERROR, FeedbackDisplayProtocol.MANUAL_HIDE,
							"Error creating event file. See error log for more information.");
							
					e.printStackTrace();
				}
			}
		};
		
		this.actionCreateCompositeFile = new FHAESAction("Create composite file", "composite.png", "Composite") {
			
			private static final long serialVersionUID = 1L;
			
			@Override
			public void actionPerformed(ActionEvent event) {
				
				CompositeFilterDialog dialog = new CompositeFilterDialog();
				dialog.setIconImage(Builder.getApplicationIcon());
				dialog.setModal(true);
				dialog.setVisible(true);
				
				if (!dialog.success())
					return;
					
				try
				{
					File file = FHOperations.createCompositeFile(frame, getSelectedValidFiles(), dialog.getStartYear(), dialog.getEndYear(),
							dialog.getFireFilterType(), dialog.getFireFilterValue(), dialog.getMinNumberOfSamples());
							
					if (file != null)
					{
						Object[] options = { "Yes", "No", "Cancel" };
						int response = JOptionPane.showOptionDialog(frame, "Would you like to add this file to you project now?", "Confirm",
								JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, // do not use a
								// custom
								// Icon
								options, // the titles of buttons
								options[0]); // default button title
								
						if (response == JOptionPane.YES_OPTION)
						{
							loadFile(file);
						}
					}
				}
				catch (Exception e)
				{
					feedbackMessagePanel.updateFeedbackMessage(FeedbackMessageType.ERROR, FeedbackDisplayProtocol.MANUAL_HIDE,
							"Error creating composite file. See error log for more information.");
							
					e.printStackTrace();
				}
			}
		};
		actionCreateCompositeFile.setEnabled(false);
		
		this.actionCreateNewCompositeFile = new FHAESAction("Composite file", "composite.png", "Composite") {
			
			private static final long serialVersionUID = 1L;
			
			@Override
			public void actionPerformed(ActionEvent event) {
				
				String lastVisitedFolder = App.prefs.getPref(PrefKey.PREF_LAST_READ_FOLDER, null);
				JFileChooser fc;
				
				if (lastVisitedFolder != null)
				{
					fc = new JFileChooser(lastVisitedFolder);
				}
				else
				{
					fc = new JFileChooser();
				}
				
				fc.setMultiSelectionEnabled(true);
				fc.setDialogTitle("Open file");
				
				int returnVal = fc.showOpenDialog(frame);
				if (returnVal != JFileChooser.APPROVE_OPTION)
					return;
					
				File[] files = fc.getSelectedFiles();
				
				// Set lastPathVisited
				App.prefs.setPref(PrefKey.PREF_LAST_READ_FOLDER, files[0].getParent());
				
				CompositeFilterDialog dialog = new CompositeFilterDialog();
				dialog.setIconImage(Builder.getApplicationIcon());
				dialog.setModal(true);
				dialog.setVisible(true);
				
				if (!dialog.success())
					return;
					
				File file = FHOperations.createCompositeFile(frame, files, dialog.getStartYear(), dialog.getEndYear(),
						dialog.getFireFilterType(), dialog.getFireFilterValue(), dialog.getMinNumberOfSamples());
						
				if (file != null)
				{
					Object[] options = { "Yes", "No", "Cancel" };
					int response = JOptionPane.showOptionDialog(frame, "Would you like to add this file to you project now?", "Confirm",
							JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, // do not use a custom
							// Icon
							options, // the titles of buttons
							options[0]); // default button title
							
					if (response == JOptionPane.YES_OPTION)
					{
						loadFile(file);
					}
				}
			}
		};
		
		this.actionGenerateSHP = new FHAESAction("Create site summary shapefile", "layers.png", "Shapefile") {
			
			private static final long serialVersionUID = 1L;
			
			@Override
			public void actionPerformed(ActionEvent event) {
				
				ShapeFileDialog sfd = new ShapeFileDialog(rightSplitPanel, rightSplitPanel.panelResults.getFHMatrix());
				sfd.setVisible(true);
			}
		};
		actionGenerateSHP.setEnabled(false);
		
		this.actionSpatialJoin = new FHAESAction("Spatial join on selected files", "spatialjoin.png", "Spatial join") {
			
			private static final long serialVersionUID = 1L;
			
			@Override
			public void actionPerformed(ActionEvent event) {
				
				SpatialJoinDialog sjd = new SpatialJoinDialog(FileListModel.getValidSelectedFileList(lstFiles));
				sjd.setVisible(true);
			}
		};
		actionSpatialJoin.setEnabled(false);
		
		this.actionSaveResults = new FHAESAction("Save analysis results", "save.png") {
			
			private static final long serialVersionUID = 1L;
			
			@Override
			public void actionPerformed(ActionEvent event) {
				
				String lastVisitedFolder = App.prefs.getPref(PrefKey.PREF_LAST_EXPORT_FOLDER, null);
				String lastExportFormat = App.prefs.getPref(PrefKey.PREF_LAST_EXPORT_FORMAT, new XLSXFileFilter().getDescription());
				
				File outputFile;
				
				// Create a file chooser
				final JFileChooser fc = new JFileChooser(lastVisitedFolder);
				
				fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
				fc.setAcceptAllFileFilterUsed(false);
				
				FileFilter xlsxfilter = new XLSXFileFilter();
				fc.addChoosableFileFilter(xlsxfilter);
				if (xlsxfilter.getDescription().equals(lastExportFormat))
				{
					fc.setFileFilter(xlsxfilter);
				}
				
				FileFilter zipfilter = new CSVZipFileFilter();
				fc.addChoosableFileFilter(zipfilter);
				if (zipfilter.getDescription().equals(lastExportFormat))
				{
					fc.setFileFilter(zipfilter);
				}
				
				fc.setMultiSelectionEnabled(false);
				fc.setDialogTitle("Save as...");
				
				// In response to a button click:
				int returnVal = fc.showOpenDialog(frame);
				
				if (returnVal == JFileChooser.APPROVE_OPTION)
				{
					outputFile = fc.getSelectedFile();
					FHAESFileFilter chosenfilter = (FHAESFileFilter) fc.getFileFilter();
					
					if (!chosenfilter.accept(outputFile))
					{
						outputFile = new File(outputFile.getAbsolutePath() + "." + chosenfilter.getPreferredFileExtension());
					}
					
					if (outputFile.exists())
					{
						Object[] options = { "Overwrite", "No", "Cancel" };
						int response = JOptionPane.showOptionDialog(frame,
								"The file '" + outputFile.getName() + "' already exists.  Are you sure you want to overwrite?", "Confirm",
								JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, // do not use a
								// custom
								// Icon
								options, // the titles of buttons
								options[0]); // default button title
								
						if (response != JOptionPane.YES_OPTION)
						{
							return;
						}
					}
					
					App.prefs.setPref(PrefKey.PREF_LAST_EXPORT_FOLDER, outputFile.getAbsolutePath());
					App.prefs.setPref(PrefKey.PREF_LAST_EXPORT_FORMAT, chosenfilter.getDescription());
					
					try
					{
						rightSplitPanel.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
						
						if (chosenfilter.getDescription().equals(new XLSXFileFilter().getDescription()))
						{
							rightSplitPanel.panelResults.saveXLSXOfResults(outputFile);
						}
						else if (chosenfilter.getDescription().equals(new CSVZipFileFilter().getDescription()))
						{
							rightSplitPanel.panelResults.saveZipOfResults(outputFile);
						}
					}
					catch (Exception e)
					{
						JOptionPane.showMessageDialog(App.mainFrame, "Error saving file:\n" + e.getMessage());
						e.printStackTrace();
					}
					finally
					{
						rightSplitPanel.setCursor(Cursor.getDefaultCursor());
					}
				}
			}
		};
		actionSaveResults.setEnabled(false);
		
		this.actionShowQuickLaunch = new FHAESAction("Show quick launch dialog", "launch.png", "Quick launch") {
			
			private static final long serialVersionUID = 1L;
			
			@Override
			public void actionPerformed(ActionEvent event) {
				
				new QuickLaunchDialog(false);
			}
		};
		actionShowQuickLaunch.putValue(Action.SELECTED_KEY, App.prefs.getBooleanPref(PrefKey.SHOW_QUICK_LAUNCH_AT_STARTUP, true));
		
		this.actionPrefChangeShowQuickLaunch = new FHAESAction("Show quick launch at startup", "gear.png") {
			
			private static final long serialVersionUID = 1L;
			
			@Override
			public void actionPerformed(ActionEvent event) {
				
				boolean val = (Boolean) getValue(Action.SELECTED_KEY);
				App.prefs.setBooleanPref(PrefKey.SHOW_QUICK_LAUNCH_AT_STARTUP, val);
			}
		};
		actionPrefChangeShowQuickLaunch.putValue(Action.SELECTED_KEY, App.prefs.getBooleanPref(PrefKey.SHOW_QUICK_LAUNCH_AT_STARTUP, true));
		
		this.actionPrefChangeAutoLoadCategories = new FHAESAction("Auto load categories", "gear.png") {
			
			private static final long serialVersionUID = 1L;
			
			@Override
			public void actionPerformed(ActionEvent event) {
				
				boolean val = (Boolean) getValue(Action.SELECTED_KEY);
				App.prefs.setBooleanPref(PrefKey.AUTO_LOAD_CATEGORIES, val);
			}
		};
		actionPrefChangeAutoLoadCategories.putValue(Action.SELECTED_KEY, App.prefs.getBooleanPref(PrefKey.AUTO_LOAD_CATEGORIES, true));
		
		this.actionResetAllFeedbackMessagePrefs = new FHAESAction("Reset all feedback message preferences", "reset.png") {
			
			private static final long serialVersionUID = 1L;
			
			@Override
			public void actionPerformed(ActionEvent event) {
				
				FeedbackPreferenceManager.ResetAllFeedbackMessagePrefs();
			}
		};
	}
	
	/**
	 * Initialize the menus.
	 */
	private void initMenu() {
		
		/**
		 * 
		 * FILE MENU
		 * 
		 */
		
		JMenuBar menuBar = new JMenuBar();
		frame.setJMenuBar(menuBar);
		
		JMenu mnFile = new JMenu(BUNDLE.getString("MainWindow.mnFile.text")); //$NON-NLS-1$
		mnFile.setMnemonic('F');
		menuBar.add(mnFile);
		
		JMenu mnNewMenu = new JMenu("New...");
		mnNewMenu.setIcon(Builder.getImageIcon("file.png"));
		mnFile.add(mnNewMenu);
		
		mnNewMenu.add(new FHAESMenuItem(actionFileNew));
		mnNewMenu.addSeparator();
		mnNewMenu.add(new FHAESMenuItem(actionCreateNewEventFile));
		mnNewMenu.add(new FHAESMenuItem(actionCreateNewCompositeFile));
		
		JMenu mnOpenFile = new JMenu("Open...");
		mnOpenFile.setIcon(Builder.getImageIcon("fileOpen.png"));
		mnFile.add(mnOpenFile);
		
		mnOpenFile.add(new FHAESMenuItem(actionFileOpen));
		mnOpenFile.add(new FHAESMenuItem(actionOpenCategoryFile));
		mnOpenFile.addSeparator();
		
		mnOpenRecent = new JMenu("Open recent");
		updateRecentDocsMenu();
		mnOpenFile.add(mnOpenRecent);
		
		mnFile.addSeparator();
		
		mnSave = new JMenu("Save");
		if (!Platform.isOSX())
		{
			mnSave.setIcon(Builder.getImageIcon("save.png"));
		}
		mnFile.add(mnSave);
		
		JMenu exportMenu = new JMenu(chartActions.actionExportChart);
		exportMenu.add(new FHAESMenuItem(chartActions.actionExportChartSVG));
		exportMenu.add(new FHAESMenuItem(chartActions.actionExportChartPDF));
		exportMenu.add(new FHAESMenuItem(chartActions.actionExportChartPNG));
		mnFile.add(exportMenu);
		
		mnSave.add(new FHAESMenuItem(this.actionSaveCurrentSummary));
		mnSave.add(new FHAESMenuItem(this.actionSaveAllSummaries));
		
		// mnSave.add(new FHAESMenuItem(this.actionSaveSeasonality));
		// mnSave.add(new FHAESMenuItem(this.actionSaveIntervalsSummary));
		// mnSave.add(new FHAESMenuItem(this.actionSaveIntervalsExceedence));
		// mnSave.addSeparator();
		// mnSave.add(new FHAESMenuItem(actionSaveAll));
		
		mnSave.add(new FHAESMenuItem(this.actionSaveResults));
		
		// mnFile.add(new FHAESMenuItem(actionSaveAll));
		
		mnFile.addSeparator();
		mnFile.add(new FHAESMenuItem(actionClearList));
		
		// mnFile.add(new FHAESMenuItem(actionPrintSetup));
		
		FHAESMenuItem mntmExit = new FHAESMenuItem(actionFileExit);
		mntmExit.setMnemonic('x');
		mnFile.add(mntmExit);
		
		/**
		 * 
		 * EDIT MENU
		 * 
		 */
		
		JMenu mnEdit = new JMenu(BUNDLE.getString("MainWindow.mnEdit.text")); //$NON-NLS-1$
		mnEdit.setMnemonic('e');
		menuBar.add(mnEdit);
		
		mnEdit.add(new FHAESMenuItem(rightSplitPanel.actionSelectAll));
		mnEdit.add(new FHAESMenuItem(rightSplitPanel.actionCopy));
		mnEdit.addSeparator();
		mnEdit.add(new FHAESMenuItem(actionEditFile));
		
		/**
		 * 
		 * DATA MENU
		 * 
		 */
		
		JMenu mnData = new JMenu("Data");
		mnEdit.setMnemonic('d');
		menuBar.add(mnData);
		
		mnData.add(new FHAESMenuItem(this.actionMergeFiles));
		mnData.add(new FHAESMenuItem(this.actionSpatialJoin));
		mnData.add(new FHAESMenuItem(this.actionCreateEventFile));
		mnData.add(new FHAESMenuItem(this.actionCreateCompositeFile));
		mnData.add(new FHAESMenuItem(this.actionGenerateSHP));
		
		/**
		 * 
		 * CHART MENU
		 * 
		 */
		
		JMenu mnChart = new JMenu("Chart");
		menuBar.add(mnChart);
		
		mnChart.addSeparator();
		mnChart.add(new FHAESCheckBoxMenuItem(chartActions.actionShowIndexPlot));
		mnChart.add(new FHAESCheckBoxMenuItem(chartActions.actionShowChronologyPlot));
		mnChart.add(new FHAESCheckBoxMenuItem(chartActions.actionCompositePlot));
		mnChart.add(new FHAESCheckBoxMenuItem(chartActions.actionShowLegend));
		
		// mnChart.addSeparator();
		// mnChart.add(new JCheckBoxMenuItem(chartActions.actionShowSeriesLabels));
		// mnChart.add(new JCheckBoxMenuItem(chartActions.actionShowSampleDepthThreshold));
		// mnChart.add(new JCheckBoxMenuItem(chartActions.actionShowMinorTickMarks));
		// mnChart.add(new JCheckBoxMenuItem(chartActions.actionShowCommonTickLine));
		
		mnChart.addSeparator();
		mnChart.add(new FHAESMenuItem(chartActions.actionShowSeriesList));
		
		JMenu mnSort = new JMenu(chartActions.actionSortSeriesBy);
		mnChart.add(mnSort);
		
		mnSort.add(new FHAESMenuItem(chartActions.actionSortName));
		mnSort.add(new FHAESMenuItem(chartActions.actionSortStartYear));
		mnSort.add(new FHAESMenuItem(chartActions.actionSortEndYear));
		mnSort.add(new FHAESMenuItem(chartActions.actionSortFirstFireYear));
		
		mnChart.addSeparator();
		mnChart.add(new FHAESMenuItem(actionEditCategories));
		mnChart.add(new FHAESMenuItem(chartActions.actionShowChartProperties));
		
		/**
		 * 
		 * TOOLS MENU
		 * 
		 */
		
		JMenu mnTools = new JMenu(BUNDLE.getString("MainWindow.mnTools.text")); //$NON-NLS-1$
		menuBar.add(mnTools);
		
		mnTools.add(new FHAESMenuItem(rightSplitPanel.actionParamConfig));
		mnTools.addSeparator();
		mnTools.add(new FHAESMenuItem(actionJSEAConfig));
		mnTools.add(new FHAESMenuItem(actionFHSampleSize));
		
		/**
		 * 
		 * PREFERENCES MENU
		 * 
		 */
		
		JMenu mnPreferences = new JMenu("Preferences");
		menuBar.add(mnPreferences);
		
		mnPreferences.add(new FHAESCheckBoxMenuItem(actionPrefChangeShowQuickLaunch));
		mnPreferences.add(new FHAESCheckBoxMenuItem(actionPrefChangeAutoLoadCategories));
		mnPreferences.addSeparator();
		mnPreferences.add(new FHAESMenuItem(actionResetAllFeedbackMessagePrefs));
		
		/**
		 * 
		 * HELP MENU
		 * 
		 */
		
		JMenu mnHelp = new JMenu(BUNDLE.getString("MainWindow.mntmHelp.text")); //$NON-NLS-1$
		mnHelp.setMnemonic('H');
		menuBar.add(mnHelp);
		
		mnHelp.add(new FHAESMenuItem(actionHelp));
		mnHelp.add(new FHAESMenuItem(actionShowLogViewer));
		mnHelp.add(new FHAESMenuItem(actionCheckForUpdates));
		mnHelp.addSeparator();
		mnHelp.add(new FHAESMenuItem(actionAbout));
	}
	
	/**
	 * Initialize the toolbar.
	 */
	private void initToolbar() {
		
		JToolBarButton btnNew = new JToolBarButton(this.actionFileNew);
		JToolBarButton btnOpen = new JToolBarButton(this.actionFileOpen);
		JToolBarButton btnSaveAll = new JToolBarButton(this.actionSaveResults);
		JToolBarButton btnExportChart = new JToolBarButton(MainWindow.chartActions.actionExportChart);
		JToolBarButton btnEditFile = new JToolBarButton(this.actionEditFile);
		JToolBarButton btnSelectAll = new JToolBarButton(rightSplitPanel.actionSelectAll);
		JToolBarButton btnCopy = new JToolBarButton(rightSplitPanel.actionCopy);
		JToolBarButton btnClear = new JToolBarButton(this.actionClearList);
		JToolBarButton btnParameters = new JToolBarButton(rightSplitPanel.actionParamConfig);
		JToolBarButton btnJSEA = new JToolBarButton(this.actionJSEAConfig);
		JToolBarButton btnMergeFiles = new JToolBarButton(this.actionMergeFiles);
		JToolBarButton btnSpatialJoin = new JToolBarButton(this.actionSpatialJoin);
		JToolBarButton btnCreateEventFile = new JToolBarButton(this.actionCreateEventFile);
		JToolBarButton btnCreateCompositeFile = new JToolBarButton(this.actionCreateCompositeFile);
		JToolBarButton btnCreateShapefile = new JToolBarButton(this.actionGenerateSHP);
		JToolBarButton btnFHSampleSize = new JToolBarButton(this.actionFHSampleSize);
		JToolBarToggleButton btnIndex = new JToolBarToggleButton(MainWindow.chartActions.actionShowIndexPlot);
		JToolBarToggleButton btnChronology = new JToolBarToggleButton(MainWindow.chartActions.actionShowChronologyPlot);
		JToolBarToggleButton btnComposite = new JToolBarToggleButton(MainWindow.chartActions.actionCompositePlot);
		JToolBarToggleButton btnShowLegend = new JToolBarToggleButton(MainWindow.chartActions.actionShowLegend);
		JToolBarButton btnChartProperties = new JToolBarButton(MainWindow.chartActions.actionShowChartProperties);
		JToolBarButton btnEditCategories = new JToolBarButton(this.actionEditCategories);
		JToolBarButton btnZoomIn = new JToolBarButton(MainWindow.chartActions.actionZoomIn);
		JToolBarButton btnZoomOut = new JToolBarButton(MainWindow.chartActions.actionZoomOut);
		JToolBarButton btnZoomReset = new JToolBarButton(MainWindow.chartActions.actionZoomReset);
		JToolBarButton btnQuickLaunch = new JToolBarButton(this.actionShowQuickLaunch);
		
		if (Platform.isOSX())
		{
			// OSX specific toolbar
			
			// For some versions of Mac OS X, Java will handle painting the Unified Tool Bar.
			// Calling this method ensures that this painting is turned on if necessary.
			MacUtils.makeWindowLeopardStyle(frame.getRootPane());
			
			UnifiedToolBar toolBar = new UnifiedToolBar();
			
			// This is so that the window can be dragged from anywhere on the toolbar.
			// This is optional, but will make your Java application feel more like an OSX app.
			toolBar.installWindowDraggerOnWindow(frame);
			
			// Add the button to the left side of the toolbar.
			toolBar.addComponentToLeft(MacButtonFactory.makeUnifiedToolBarButton(btnNew));
			toolBar.addComponentToLeft(MacButtonFactory.makeUnifiedToolBarButton(btnOpen));
			toolBar.addComponentToLeft(MacButtonFactory.makeUnifiedToolBarButton(btnSaveAll));
			toolBar.addComponentToLeft(MacButtonFactory.makeUnifiedToolBarButton(btnExportChart));
			toolBar.addComponentToLeft(MacButtonFactory.makeUnifiedToolBarButton(btnSelectAll));
			toolBar.addComponentToLeft(MacButtonFactory.makeUnifiedToolBarButton(btnCopy));
			toolBar.addComponentToLeft(MacButtonFactory.makeUnifiedToolBarButton(btnEditFile));
			toolBar.addComponentToLeft(MacButtonFactory.makeUnifiedToolBarButton(btnClear));
			toolBar.addComponentToLeft(MacButtonFactory.makeUnifiedToolBarButton(btnParameters));
			toolBar.addComponentToLeft(MacButtonFactory.makeUnifiedToolBarButton(btnJSEA));
			toolBar.addComponentToLeft(MacButtonFactory.makeUnifiedToolBarButton(btnFHSampleSize));
			toolBar.addComponentToLeft(MacButtonFactory.makeUnifiedToolBarButton(btnMergeFiles));
			toolBar.addComponentToLeft(MacButtonFactory.makeUnifiedToolBarButton(btnSpatialJoin));
			toolBar.addComponentToLeft(MacButtonFactory.makeUnifiedToolBarButton(btnCreateEventFile));
			toolBar.addComponentToLeft(MacButtonFactory.makeUnifiedToolBarButton(btnCreateCompositeFile));
			toolBar.addComponentToLeft(MacButtonFactory.makeUnifiedToolBarButton(btnCreateShapefile));
			toolBar.addComponentToLeft(MacButtonFactory.makeUnifiedToolBarButton(btnIndex));
			toolBar.addComponentToLeft(MacButtonFactory.makeUnifiedToolBarButton(btnChronology));
			toolBar.addComponentToLeft(MacButtonFactory.makeUnifiedToolBarButton(btnComposite));
			toolBar.addComponentToLeft(MacButtonFactory.makeUnifiedToolBarButton(btnShowLegend));
			toolBar.addComponentToLeft(MacButtonFactory.makeUnifiedToolBarButton(btnChartProperties));
			toolBar.addComponentToLeft(MacButtonFactory.makeUnifiedToolBarButton(btnEditCategories));
			toolBar.addComponentToLeft(MacButtonFactory.makeUnifiedToolBarButton(btnZoomIn));
			toolBar.addComponentToLeft(MacButtonFactory.makeUnifiedToolBarButton(btnZoomOut));
			toolBar.addComponentToLeft(MacButtonFactory.makeUnifiedToolBarButton(btnZoomReset));
			toolBar.addComponentToLeft(MacButtonFactory.makeUnifiedToolBarButton(btnQuickLaunch));
			
			// Add the toolbar to the frame.
			frame.getContentPane().add(toolBar.getComponent(), BorderLayout.NORTH);
			return;
		}
		else
		{
			// Windows and Linux toolbar
			
			JToolBar toolBar = new JToolBar();
			toolBar.setFloatable(false);
			frame.getContentPane().add(toolBar, BorderLayout.NORTH);
			
			toolBar.add(btnNew);
			toolBar.add(btnOpen);
			toolBar.add(btnSaveAll);
			toolBar.add(btnExportChart);
			
			toolBar.addSeparator();
			toolBar.add(btnJSEA);
			toolBar.add(btnFHSampleSize);
			
			toolBar.addSeparator();
			toolBar.add(btnSelectAll);
			toolBar.add(btnCopy);
			toolBar.add(btnEditFile);
			toolBar.add(btnClear);
			toolBar.add(btnParameters);
			
			toolBar.addSeparator();
			toolBar.add(btnMergeFiles);
			toolBar.add(btnSpatialJoin);
			toolBar.add(btnCreateEventFile);
			toolBar.add(btnCreateCompositeFile);
			toolBar.add(btnCreateShapefile);
			
			toolBar.addSeparator();
			toolBar.add(btnIndex);
			toolBar.add(btnChronology);
			toolBar.add(btnComposite);
			toolBar.add(btnShowLegend);
			toolBar.add(btnChartProperties);
			toolBar.add(btnEditCategories);
			toolBar.add(btnZoomIn);
			toolBar.add(btnZoomOut);
			toolBar.add(btnZoomReset);
			
			toolBar.addSeparator();
			toolBar.add(btnQuickLaunch);
		}
	}
}
