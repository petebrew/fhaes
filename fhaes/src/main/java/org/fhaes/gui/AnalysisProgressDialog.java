package org.fhaes.gui;

/*******************************************************************************
 * Copyright (C) 2013 Peter Brewer
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * Contributors:
 *     Peter Brewer
 ******************************************************************************/

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingWorker;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;

import net.miginfocom.swing.MigLayout;

import org.fhaes.analysis.FHInterval;
import org.fhaes.analysis.FHMatrix;
import org.fhaes.analysis.FHSeasonality;
import org.fhaes.analysis.FHSummary;
import org.fhaes.enums.AnalysisType;
import org.fhaes.enums.EventTypeToProcess;
import org.fhaes.enums.FireFilterType;
import org.fhaes.enums.NoDataLabel;
import org.fhaes.model.FHFile;
import org.fhaes.preferences.App;
import org.fhaes.preferences.FHAESPreferences.PrefKey;
import org.fhaes.util.TableUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * AnalysisProgressDialog Class. This is a dialog that shows progress for calculating results. The dialog runs in a background thread to
 * ensure the application stays responsive. At the moment the progress bar is indeterminate.
 * 
 * @author pwb48
 */
public class AnalysisProgressDialog extends JDialog implements PropertyChangeListener {

	private static final long serialVersionUID = 1L;
	private static final Logger log = LoggerFactory.getLogger(AnalysisProgressDialog.class);
	private ArrayList<FHFile> fileList = new ArrayList<FHFile>();
	private JProgressBar progressBar;

	private File generalFilesSummaryFile = null;
	private File seasonalitySummaryFile = null;
	private File intervalsExceedenceFile = null;
	private File intervalsSummaryFile = null;
	private File bin00File = null;
	private File bin10File = null;
	private File bin01File = null;
	private File bin11File = null;
	private File binSumFile = null;
	private File fileDSCOH = null;
	private File fileDSJAC = null;
	private File fileSCOH = null;
	private File fileSite = null;
	private File fileSJAC = null;
	private File fileNTP = null;
	private File fileTree = null;

	private DefaultTableModel generalFilesSummaryModel = null;
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
	private DefaultTableModel SiteModel = null;
	private DefaultTableModel SJACModel = null;
	private DefaultTableModel NTPModel = null;
	private DefaultTableModel TreeModel = null;

	private FHMatrix fhm;

	JLabel lblInfo;
	private JPanel panel;

	public AnalysisProgressDialog(Component parent, ArrayList<FHFile> files) {

		if (files == null || files.size() == 0)
			return;

		this.fileList = files;

		final Task task = new Task();
		// this.getContentPane().setBackground(Color.WHITE);
		getContentPane().setLayout(new BorderLayout(0, 0));

		panel = new JPanel();
		// panel.setBackground(Color.WHITE);
		panel.setBorder(new LineBorder(new Color(0, 0, 0)));
		getContentPane().add(panel);
		panel.setLayout(new MigLayout("", "[grow][][]", "[][]"));

		progressBar = new JProgressBar();
		panel.add(progressBar, "cell 0 0 2 1,growx");
		progressBar.setStringPainted(false);

		progressBar.setVisible(true);
		progressBar.setMaximum(110);
		progressBar.setValue(0);
		progressBar.setIndeterminate(true);

		lblInfo = new JLabel("Running analysis, please wait...");
		panel.add(lblInfo, "flowx,cell 0 1");
		lblInfo.setFont(new Font("Dialog", Font.PLAIN, 10));

		JButton btnCancel = new JButton("X");
		btnCancel.setFocusable(false);
		panel.add(btnCancel, "cell 1 1");
		btnCancel.setFont(new Font("Dialog", Font.PLAIN, 8));
		btnCancel.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {

				task.cancel(true);

			}

		});

		this.setUndecorated(true);

		this.setModal(true);
		this.pack();

		task.addPropertyChangeListener(this);
		task.execute();

		this.setLocationRelativeTo(parent);
		this.setVisible(true);

	}

	public ArrayList<FHFile> getFileList() {

		return fileList;
	}

	class Task extends SwingWorker<Void, Void> {

		/*
		 * Main task. Executed in background thread.
		 */
		@Override
		public Void doInBackground() {

			if (fileList == null || fileList.size() == 0)
				return null;

			FHFile[] array = fileList.toArray(new FHFile[fileList.size()]);

			// Run seasonality analysis
			setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
			App.mainFrame.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
			setProgress(0);
			lblInfo.setText("Calculating seasonality...");
			try
			{

				seasonalitySummaryFile = File.createTempFile("FHSeasonality", ".tmp");
				seasonalitySummaryFile.deleteOnExit();

				FHSeasonality.runAnalysis(null, seasonalitySummaryFile, array,
						App.prefs.getBooleanPref(PrefKey.SEASONALITY_FIRST_GROUP_DORMANT, true),
						App.prefs.getBooleanPref(PrefKey.SEASONALITY_FIRST_GROUP_EARLY_EARLY, true),
						App.prefs.getBooleanPref(PrefKey.SEASONALITY_FIRST_GROUP_MIDDLE_EARLY, false),
						App.prefs.getBooleanPref(PrefKey.SEASONALITY_FIRST_GROUP_LATE_EARLY, false),
						App.prefs.getBooleanPref(PrefKey.SEASONALITY_FIRST_GROUP_LATE, false),
						App.prefs.getBooleanPref(PrefKey.SEASONALITY_SECOND_GROUP_DORMANT, false),
						App.prefs.getBooleanPref(PrefKey.SEASONALITY_SECOND_GROUP_EARLY_EARLY, false),
						App.prefs.getBooleanPref(PrefKey.SEASONALITY_SECOND_GROUP_MIDDLE_EARLY, true),
						App.prefs.getBooleanPref(PrefKey.SEASONALITY_SECOND_GROUP_LATE_EARLY, true),
						App.prefs.getBooleanPref(PrefKey.SEASONALITY_SECOND_GROUP_LATE, true),
						App.prefs.getIntPref(PrefKey.RANGE_FIRST_YEAR, 0), App.prefs.getIntPref(PrefKey.RANGE_LAST_YEAR, 0),
						App.prefs.getEventTypePref(PrefKey.EVENT_TYPE_TO_PROCESS, EventTypeToProcess.FIRE_EVENT));

				seasonalitySummaryModel = getTableModelFromCSV(seasonalitySummaryFile);

			}
			catch (Exception e)
			{
				log.error("Error caught during seasonality calculations");
				JOptionPane.showMessageDialog(App.mainFrame,
						"Error running seasonality analysis.  Please check logs and inform developers", "Error", JOptionPane.ERROR_MESSAGE);
				e.printStackTrace();
			}

			// Run Intervals analysis
			setProgress(5);
			lblInfo.setText("Calculating intervals...");
			try
			{
				FHInterval fhint = new FHInterval(array, App.prefs.getAnalysisTypePref(PrefKey.INTERVALS_ANALYSIS_TYPE,
						AnalysisType.COMPOSITE), App.prefs.getIntPref(PrefKey.RANGE_FIRST_YEAR, 0), App.prefs.getIntPref(
						PrefKey.RANGE_LAST_YEAR, 0), App.prefs.getFireFilterTypePref(PrefKey.COMPOSITE_FILTER_TYPE,
						FireFilterType.NUMBER_OF_EVENTS), (double) App.prefs.getIntPref(PrefKey.COMPOSITE_FILTER_VALUE, 1),
						App.prefs.getBooleanPref(PrefKey.INTERVALS_INCLUDE_OTHER_INJURIES, false), App.prefs.getEventTypePref(
								PrefKey.EVENT_TYPE_TO_PROCESS, EventTypeToProcess.FIRE_EVENT), App.prefs.getDoublePref(
								PrefKey.INTERVALS_ALPHA_LEVEL, 0.125));
				setProgress(10);
				intervalsExceedenceFile = fhint.getExceedence();
				intervalsExceedenceModel = getTableModelFromCSV(intervalsExceedenceFile);
				setProgress(15);
				intervalsSummaryFile = fhint.getSummary();
				intervalsSummaryModel = getTableModelFromCSV(intervalsSummaryFile);

			}
			catch (Exception e)
			{
				e.printStackTrace();
				log.error("Error caught when running FHInterval");
				JOptionPane.showMessageDialog(App.mainFrame, "Error running intervals analysis.  Please check logs and inform developers",
						"Error", JOptionPane.ERROR_MESSAGE);

			}

			// Run Matrix analysis
			setProgress(20);

			// But only if there is more than 1 input files.
			if (array.length > 1)
			{

				lblInfo.setText("Calculating matrices...");
				try
				{
					fhm = new FHMatrix(array, App.prefs.getIntPref(PrefKey.RANGE_FIRST_YEAR, 0), App.prefs.getIntPref(
							PrefKey.RANGE_LAST_YEAR, 0), App.prefs.getFireFilterTypePref(PrefKey.COMPOSITE_FILTER_TYPE,
							FireFilterType.NUMBER_OF_EVENTS), App.prefs.getEventTypePref(PrefKey.EVENT_TYPE_TO_PROCESS,
							EventTypeToProcess.FIRE_EVENT), (double) App.prefs.getIntPref(PrefKey.COMPOSITE_FILTER_VALUE, 1),
							App.prefs.getIntPref(PrefKey.RANGE_OVERLAP_REQUIRED, 25), NoDataLabel.MINUS_99);

					bin00File = fhm.getFileMatrix00Result();
					bin00Model = getTableModelFromCSV(bin00File);
					setProgress(25);

					bin10File = fhm.getFileMatrix10Result();
					bin10Model = getTableModelFromCSV(bin10File);
					setProgress(30);

					bin01File = fhm.getFileMatrix01Result();
					bin01Model = getTableModelFromCSV(bin01File);
					setProgress(35);

					bin11File = fhm.getFileMatrix11Result();
					bin11Model = getTableModelFromCSV(bin11File);
					setProgress(40);

					binSumFile = fhm.getFileSumResult();
					binSumModel = getTableModelFromCSV(binSumFile);
					setProgress(45);

					fileDSCOH = fhm.getFileDSCOHResult();
					DSCOHModel = getTableModelFromCSV(fileDSCOH);
					setProgress(50);

					fileDSJAC = fhm.getFileDSJACResult();
					DSJACModel = getTableModelFromCSV(fileDSJAC);
					setProgress(55);

					fileSCOH = fhm.getFileSCOHResult();
					SCOHModel = getTableModelFromCSV(fileSCOH);
					setProgress(60);

					fileSite = fhm.getFileSiteResult();
					SiteModel = getTableModelFromCSV(fileSite);
					setProgress(65);

					fileSJAC = fhm.getFileSJACResult();
					SJACModel = getTableModelFromCSV(fileSJAC);
					setProgress(70);

					fileNTP = fhm.getFileNTPResult();
					NTPModel = getTableModelFromCSV(fileNTP);
					setProgress(75);

					fileTree = fhm.getTreeSummaryFile();
					TreeModel = getTableModelFromCSV(fileTree);
					setProgress(80);

				}
				catch (Exception e)
				{
					log.error("Error caught when running FHMatrix");
					JOptionPane.showMessageDialog(App.mainFrame, "Error running matrix analysis.  Please check logs and inform developers",
							"Error", JOptionPane.ERROR_MESSAGE);
					e.printStackTrace();
				}
			}
			setProgress(100);

			// Run summary analysis
			lblInfo.setText("Calculating summary...");
			try
			{
				FHSummary fhsum = new FHSummary(array);

				generalFilesSummaryFile = fhsum.getFilesSummaryAsCSVFile();
				generalFilesSummaryModel = getTableModelFromCSV(generalFilesSummaryFile);

			}
			catch (Exception e)
			{
				e.printStackTrace();
				log.error("Error caught when running FHSummary");
				JOptionPane.showMessageDialog(App.mainFrame, "Error running summary analysis.  Please check logs and inform developers",
						"Error", JOptionPane.ERROR_MESSAGE);

			}
			setProgress(110);

			lblInfo.setText("Done...");
			return null;
		}

		/*
		 * Executed in event dispatching thread
		 */
		@Override
		public void done() {

			setCursor(Cursor.getDefaultCursor()); // turn off the wait cursor
			App.mainFrame.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

			finish();
		}
	}

	private DefaultTableModel getTableModelFromCSV(File f) {

		FileReader reader;
		try
		{

			reader = new FileReader(f);

			BufferedReader br = new BufferedReader(reader);
			DefaultTableModel model = TableUtil.createTableModel(br, null);

			return model;

		}
		catch (FileNotFoundException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public File getSeasonalityFile() {

		return seasonalitySummaryFile;
	}

	public File getIntervalsSummaryFile() {

		return this.intervalsSummaryFile;
	}

	public File getIntervalsExceedenceFile() {

		return this.intervalsExceedenceFile;
	}

	public File getBin00File() {

		return this.bin00File;
	}

	public File getBin10File() {

		return this.bin10File;
	}

	public File getBin01File() {

		return this.bin01File;
	}

	public File getBin11File() {

		return this.bin11File;
	}

	public FHMatrix getFHMatrixClass() {

		return this.fhm;
	}

	public File getBinSumFile() {

		return this.binSumFile;
	}

	public File getDSCOHFile() {

		return this.fileDSCOH;
	}

	public File getSCOHFile() {

		return this.fileSCOH;
	}

	public File getDSJACFile() {

		return this.fileDSJAC;
	}

	public File getSJACFile() {

		return this.fileSJAC;
	}

	public File getSiteSummaryFile() {

		return this.fileSite;
	}

	public File getNTPFile() {

		return this.fileNTP;
	}

	public File getGeneralSummaryFile() {

		return this.generalFilesSummaryFile;
	}

	public File getTreeSummaryFile() {

		return this.fileTree;
	}

	public DefaultTableModel getSeasonalitySummaryModel() {

		return seasonalitySummaryModel;
	}

	public DefaultTableModel getIntervalsSummaryModel() {

		return intervalsSummaryModel;
	}

	public DefaultTableModel getIntervalsExceedenceModel() {

		return intervalsExceedenceModel;
	}

	public DefaultTableModel getBin00Model() {

		return bin00Model;
	}

	public DefaultTableModel getBin01Model() {

		return bin01Model;
	}

	public DefaultTableModel getBin10Model() {

		return bin10Model;
	}

	public DefaultTableModel getBin11Model() {

		return bin11Model;
	}

	public DefaultTableModel getBinSumModel() {

		return binSumModel;
	}

	public DefaultTableModel getDSCOHModel() {

		return DSCOHModel;
	}

	public DefaultTableModel getDSJACModel() {

		return DSJACModel;
	}

	public DefaultTableModel getSCOHModel() {

		return SCOHModel;
	}

	public DefaultTableModel getSiteModel() {

		return SiteModel;
	}

	public DefaultTableModel getSJACModel() {

		return SJACModel;
	}

	public DefaultTableModel getNTPModel() {

		return NTPModel;
	}

	public DefaultTableModel getGeneralSummaryModel() {

		return this.generalFilesSummaryModel;
	}

	public DefaultTableModel getTreeModel() {

		return TreeModel;
	}

	public void propertyChange(PropertyChangeEvent evt) {

		if ("progress" == evt.getPropertyName())
		{
			int progress = (Integer) evt.getNewValue();
			progressBar.setValue(progress);
		}

	}

	private void finish() {

		this.setVisible(false);
	}

}
