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
package org.fhaes.fhfilechecker;

import java.awt.Color;
import java.awt.Component;
//import java.awt.Cursor;
import java.awt.FlowLayout;
//import javax.swing.JTextField;
import java.awt.GridLayout;
//import java.awt.BorderLayout;
//import javax.swing.Timer;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.text.DecimalFormat;
import java.util.ArrayList;
//import java.util.Collections;
import java.util.Date;
//import java.awt.event.*;
//import java.awt.*;

///import javax.swing.BoxLayout;
//import javax.swing.ButtonGroup;
//import javax.swing.JDialog;
import javax.swing.BorderFactory;
//import javax.swing.JRadioButton;
//import javax.swing.JProgressBar;
import javax.swing.JButton;
//import java.awt.GridBagConstraints;
//import java.awt.Dimension;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import javax.swing.filechooser.FileFilter;

import org.fhaes.enums.EventTypeToProcess;
import org.fhaes.fhfilereader.FHX2FileReader;
import org.fhaes.fhfilereader.IFHAESReader;
import org.fhaes.filefilter.FHXFileFilter;
import org.fhaes.util.Builder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * FHFileChecker Class.
 * 
 * @author elena
 */
public class FHFileChecker extends JFrame {
	
	private static final long serialVersionUID = 1L;
	
	private static final Logger log = LoggerFactory.getLogger(FHFileChecker.class);
	
	private JPanel jPanel = null;
	private JFileChooser fileBrowse = null;
	private File[] inputFile;
	// private File outputFile;
	// private int[] begingYearperSample;
	private String formatReport;
	// private String SummReport;
	// private String IndReport;
	private final String HelpReport = "Here goes the help information";
	// private String savePath;
	
	private JPanel jPanelBrowse = null;
	private JButton buttonBrowse = null;
	private JLabel jLabelBrowse = null;
	
	// private JPanel jPanelBar = null;
	// private JProgressBar prg;
	// private Timer t;
	
	private JPanel jPanelReportType = null;
	private JLabel jlabelformatreport = null;
	private JCheckBox jcheckformatreport = null;
	private JLabel jlabelindivreport = null;
	private JCheckBox jcheckindivreport = null;
	
	// highway is the choice a user has to start the matrix
	// from the first fire year or the first year of data
	
	private JPanel jPanelExit = null;
	private JButton buttonRun = null;
	private JButton buttonHelp = null;
	private JButton buttonExit = null;
	
	// private Date titlenow;
	
	// private int numberoffiles;
	
	// private boolean eventFlag;
	
	/**
	 * This is the default constructor.
	 */
	public FHFileChecker() {
		
		super();
		initialize();
	}
	
	/**
	 * This method initializes this.
	 * 
	 * @return void
	 */
	private void initialize() {
		
		this.setSize(530, 300);
		this.setContentPane(getJPanel());
		// titlenow = new Date();
		String title = "FHAES - Format File Check (version 6/24/2013) ";
		this.setTitle(title);
	}
	
	public String getReport() {
		
		return formatReport;
	}
	
	/**
	 * This method initializes jPanel
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanel() {
		
		if (jPanel == null)
		{
			jPanel = new JPanel();
			GridLayout layout1 = new GridLayout();
			layout1.setColumns(1);
			layout1.setRows(3);
			setIconImage(Builder.getApplicationIcon());
			jPanel.setLayout(layout1);
			jPanel.add(getJPanelBrowse());
			jPanel.add(getJPanelReportType());
			// jPanel.add(getJPanelBar());
			jPanel.add(getJPanelExit());
			
		}
		return jPanel;
	}
	
	/**
	 * This method initializes buttonBrowse
	 * 
	 * @return javax.swing.JButton
	 */
	private JButton getButtonBrowse() {
		
		if (buttonBrowse == null)
		{
			buttonBrowse = new JButton();
			buttonBrowse.setText("Browse ...");
			buttonBrowse.addActionListener(new java.awt.event.ActionListener() {
				
				@Override
				public void actionPerformed(java.awt.event.ActionEvent e) {
					
					int fileBrowseReturn = 0;
					// TODO Event stub for buttonBrowse.actionPerformed()
					// log.debug("buttonBrowse actionPerformed()");
					// fileBrowse = new JFileChooser(new File("C:\\"));
					fileBrowse = new JFileChooser();
					fileBrowse.setMultiSelectionEnabled(true);
					// fileBrowse.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
					fileBrowse.setFileSelectionMode(JFileChooser.FILES_ONLY);
					FileFilter filter1 = new FHXFileFilter();
					
					fileBrowse.setFileFilter(filter1);
					fileBrowse.setDialogTitle("Select One or More FHX2 file");
					fileBrowseReturn = fileBrowse.showOpenDialog(buttonBrowse);
					// log.debug("DEBUG1: fileBrowseReturn = " + fileBrowseReturn);
					// log.debug("her is the return option "+JFileChooser.APPROVE_OPTION);
					if (fileBrowseReturn == 0)
					{
						// log.debug("DEBUG2: fileBrowse.getSelectedFile = " + fileBrowse.getSelectedFile().toString());
						inputFile = fileBrowse.getSelectedFiles();
						// log.debug("DEBUG3: inputFile = " + fileBrowse.getSelectedFiles().length );
						JOptionPane.showMessageDialog(null, " You have selected " + fileBrowse.getSelectedFiles().length + " files",
								"Number Of Selected Files", JOptionPane.INFORMATION_MESSAGE);
					}
					else
					{
						JOptionPane.showMessageDialog(null,
								" You have selected " + fileBrowse.getSelectedFiles().length
										+ " files. You must select at least one fhx file to run this program.",
								"Number Of Selected Files", JOptionPane.INFORMATION_MESSAGE);
					}
				}// end or action performed button browse
			});
		}
		return buttonBrowse;
	}// end of browse button
	
	/**
	 * This method initializes buttonRun
	 * 
	 * @return javax.swing.JButton
	 */
	private JButton getButtonRun() {
		
		if (buttonRun == null)
		{
			buttonRun = new JButton();
			buttonRun.setText("Run & Save Report(s)");
			
			buttonRun.addActionListener(new java.awt.event.ActionListener() {
				
				// TODO Event stub for buttonRun.actionPerformed()
				
				@Override
				public void actionPerformed(java.awt.event.ActionEvent e) {
					
					doCheck(buttonRun, inputFile, null, jcheckformatreport.isSelected(), jcheckindivreport.isSelected());
					
				}// end of if action perform for the run button
				
			});// end of action listerner run button
		} // (183) if nul run button
		return buttonRun;
	}// /end of getbuttonrun function
	
	@SuppressWarnings("unused")
	public boolean doCheck(Component parent, File[] inputFile, File outputFile, Boolean jcheckformatreport, Boolean jcheckindivreport) {
		
		boolean run = false;
		log.debug("buttonRun (check and save) actionPerformed()");
		
		/*
		 * If at least one file has been choosen then the progam will run otherwise get message
		 */
		// FIRST CHECK
		if (inputFile != null)
		{
			// log.debug("I am here before the checks and inputFile is not null");
			run = true;
		} // end of if for checks file selected, file type selected
		else
		{
			run = false;
			log.error("Select at least one file to check validity");
		} // end of else of if checks
		
		boolean passFormat = false;
		
		// MAIN RUN
		if (run)
		{
			int fileBrowseReturn = 0;
			ArrayList<IFHAESReader> myReaderArray = new ArrayList<IFHAESReader>();
			ArrayList<Integer> FyearperSampletemp;
			// ArrayList<ArrayList<Integer>> FyearperSample = new ArrayList<ArrayList<Integer>>();
			ArrayList<Integer> FIyearperSampletemp;
			// ArrayList<ArrayList<Integer>> FIyearperSample = new ArrayList<ArrayList<Integer>>();
			String savePath = new String();
			savePath = inputFile[0].getAbsolutePath();
			// log.debug("DEBUG: savePath = " + savePath);
			// log.debug("DEBUG: savePath plus = " + savePath.substring(0,savePath.lastIndexOf(File.separator)));
			/*
			 * Setting the three decimal format
			 */
			DecimalFormat onePlace = new DecimalFormat("0.0");
			// DecimalFormat oneDplace = new DecimalFormat("%.1f");
			/*
			 * Creating the date of the run of the program
			 */
			Date now = new Date();
			/*
			 * creating the boolean string of passing format
			 */
			
			// int[] beginingYear = new int[12];
			String numericalDataint = "^[0-9 \\-]+$";
			// String numericalDatafloat = "^[0-9 .\\-]+$";
			String fhx2dataline = "^[a-z A-Z . | } { \\[ \\]]+$";
			formatReport = " ";
			// setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
			// done = false;
			// task = new Task();
			// task.addPropertyChangeListener(this);
			// task.execute();
			// log.debug("I am here inside if run I am going to start the timer");
			// starstarstart
			// new Thread(new thread1()).start(); //Start the thread
			// t.start();
			
			// *** CURSOR VARIABLES *** //
			// Cursor hourglassCursor = new Cursor(Cursor.WAIT_CURSOR);
			// Cursor hourglassCursor = Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR);
			// private static Cursor waitCursor = Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR);
			// Cursor normalCursor = new Cursor(Cursor.DEFAULT_CURSOR);
			// setCursor(hourglassCursor);
			// setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
			
			for (int i = 0; i < inputFile.length; i++)
			{
				// int numberoffiles = i;
				// /Check each file
				int linecounter = 0;
				myReaderArray.add(new FHX2FileReader(inputFile[i]));
				ArrayList<ArrayList<Integer>> FyearperSample = new ArrayList<ArrayList<Integer>>();
				ArrayList<ArrayList<Integer>> FIyearperSample = new ArrayList<ArrayList<Integer>>();
				// log.debug("REPORT ");
				// myReader.get(i).PrintReport();
				// myReaderArray.get(i).makeClimate2d();
				// Checking each file
				/*
				 * The check for the format of the fhx file NOTE: the heading of the file with the metadata is not being check. Must check
				 * with Elaine about the comment lines. 1. the file extension is not check also. 2. First and most important line is the
				 * FHX2 FORMAT or FIRE2 format. 3. next line must contain the following integer data (the integer format will be check) a.
				 * Beginning year: this is the beginning year for the fire data file integer b. number of sample in the data: number of
				 * columns integer c. longest code of all samples: integer 4. black (empty line) must come before the fire data start. 5.
				 * fire data : I will check for the symbols lower/Upper case letters | { } [ ] . a. I will check that only this symbols are
				 * used in the data b. I will check that the data is the correct length ie same as the number of samples Warning c. all the
				 * samples should have a starting point either [ , { or Uppercase letter (fire) Warning d. all the samples should have a
				 * closing point either ] or } Warning 6. A report on each file will be created.
				 */
				
				formatReport = formatReport + "\n" + " FORMAT REPORT FOR FILE: " + myReaderArray.get(i).getName() + "\n";
				formatReport = formatReport + "\t" + "This report was created on: " + now + "\n";
				passFormat = true;
				// if(myReaderArray.get(i).isFormatInfoSet())
				if (true)
				{
					// log.debug("I am inside isFormatInfo");
					// get the format line if it was found
					formatReport = formatReport + "\t" + "The format of the file is: " + myReaderArray.get(i).getFileFormat() + "\n";
					// get the next line information
					if (myReaderArray.get(i).getFirstYear().toString().matches(numericalDataint))
					{
						formatReport = formatReport + "\t" + "The first year of your data is: " + myReaderArray.get(i).getFirstYear()
								+ "\n";
					}
					else
					{
						formatReport = formatReport + "\t"
								+ "There an issue with the begining year of fire scared data, please make sure it is an whole number."
								+ "\n";
						passFormat = false;
					}
					
					// display logger information
					log.debug("size of bad lines is : " + myReaderArray.get(i).getBadDataLineNumbers().size());
					log.debug("size of data is : " + myReaderArray.get(i).getData().size());
					log.debug("size of row data is : " + myReaderArray.get(i).getRawRowData().size());
					log.debug("number of series is : " + myReaderArray.get(i).getNumberOfSeries().toString());
					
					if ((myReaderArray.get(i).getNumberOfSeries().toString().matches(numericalDataint))
							&& (myReaderArray.get(i).passesBasicSyntaxCheck()))
					{
						formatReport = formatReport + "\t" + "Expect " + myReaderArray.get(i).getNumberOfSeries()
								+ " samples in this data file. " + "\n";
					}
					else
					{
						if (myReaderArray.get(i).getBadDataLineNumbers().size() == 0)
						{
							log.debug("I am here in BadDataLine == 0 size of badlines is "
									+ myReaderArray.get(i).getBadDataLineNumbers().size());
							formatReport = formatReport + "\t"
									+ "There an issue with the number of samples in your data, please make sure it is an whole number."
									+ "\n";
							passFormat = false;
						}
						else
						{
							for (int j = 0; j < myReaderArray.get(i).getBadDataLineNumbers().size(); j++)
							{
								log.debug("bad line number " + myReaderArray.get(i).getBadDataLineNumbers().get(j).intValue());
								log.debug("bad line length " + myReaderArray.get(i).getRawRowData()
										.get(myReaderArray.get(i).getBadDataLineNumbers().get(j).intValue()).length());
								log.debug("Number of series: " + myReaderArray.get(i).getNumberOfSeries());
								if ((myReaderArray.get(i).getRawRowData()
										.get(myReaderArray.get(i).getBadDataLineNumbers().get(j).intValue()).length()) > myReaderArray
												.get(i).getNumberOfSeries())
								{
									
									formatReport = formatReport + "\t" + "Row number: "
											+ myReaderArray.get(i).getBadDataLineNumbers().get(j).intValue()
											+ " on the fire matrix has more samples than " + myReaderArray.get(i).getNumberOfSeries()
											+ "\n";
									passFormat = false;
								}
								else
								{
									formatReport = formatReport + "\t" + "Row number: "
											+ myReaderArray.get(i).getBadDataLineNumbers().get(j).intValue()
											+ " on the fire matrix has less samples than " + myReaderArray.get(i).getNumberOfSeries()
											+ "\n";
									passFormat = false;
								}
							}
						}
					}
					if (myReaderArray.get(i).getLengthOfSeriesName().toString().matches(numericalDataint))
					{
						formatReport = formatReport + "\t" + "Expect the longest ID code to be "
								+ myReaderArray.get(i).getLengthOfSeriesName() + " characters long." + "\n";
					}
					else
					{
						formatReport = formatReport + "\t"
								+ "There an issue with the longest length of the ID codes of your samples in your data, please make sure it is an whole number."
								+ "\n";
						passFormat = false;
					}
					
					if (myReaderArray.get(i).passesBasicSyntaxCheck())
					{
						// int linecounter = 0;
						myReaderArray.get(i).getData();
						for (int j = 0; j < myReaderArray.get(i).getData().size(); j++)
						{
							if (myReaderArray.get(i).getData().get(j).matches(fhx2dataline))
							{
								linecounter = linecounter + 1;
							}
							else
							{
								// log.debug("I am here in the else the line is "
								// +myReader.get(i).getData().get(j)+"the line is "+j);
								formatReport = formatReport + "\t" + "there is something wrong with at least one of the line in the data"
										+ "\n";
								passFormat = false;
							}
						}
						for (int j = 0; j < myReaderArray.get(i).getNumberOfSeries(); j++)
						{
							if ((myReaderArray.get(i).getStartYearIndexPerSample()[j] == -1))
							{
								
								formatReport = formatReport + "\t"
										+ " WARNING All samples Should have either { or [, or at least one of the following symbols D, E, M, L, A, U. Sample: "
										+ (j + 1) + " seems to have this issue" + "\n";
								// passFormat = false;
							}
							if ((myReaderArray.get(i).getLastYearIndexPerSample()[j] == -1))
							{
								
								formatReport = formatReport + "\t"
										+ " WARNING All samples should have either }, or ], or at least one of the following symbols D,d, E,e, M,m, L,l, A,a, U,u. Sample: "
										+ (j + 1) + " seems to have this issue " + "\n";
								// passFormat = false;
							}
						}
						if (linecounter == (myReaderArray.get(i).getLastYear() - myReaderArray.get(i).getFirstYear() + 1))
						{
							
							formatReport = formatReport + "\t" + "The beginning year of the data is: " + myReaderArray.get(i).getFirstYear()
									+ " The end year of your data is: " + myReaderArray.get(i).getLastYear() + " for a total of : "
									+ linecounter + " lines in the data section \n";
						}
						else
						{
							formatReport = formatReport + "\t"
									+ "Although the all the data lines pass the checked, there seems to be something wrong the the total number of lines of your data section"
									+ "\n";
							passFormat = false;
						}
					}
					else
					{
						formatReport = formatReport + "\t" + "The mandatory blank line before starting the fire data was not found" + "\n";
						passFormat = false;
					}
				} // end of if for finding the format line
				else
				{
					// log.debug("the file did not pass the format line test");
					passFormat = false;
				} // end of else of if format line passes.
				if (passFormat)
				{
					formatReport = formatReport + "\t" + "CONGRATULATIONS THE FILE " + myReaderArray.get(i).getName()
							+ " PASSED THE FORMAT TEST" + "\n";
					if (!myReaderArray.get(i).hasFireEventsOrInjuries())
					{
						formatReport = formatReport + "\t"
								+ "HOWEVER: This file contains no fire events so will not be used in any analyses" + "\n";
					}
					
				}
				else
				{
					formatReport = formatReport + "\t" + "SORRY! this file failed at least one format condition Therefore: THE "
							+ myReaderArray.get(i).getName() + " FILE FAILED TO PASS THE FORMAT TEST" + "\n";
				}
				
				// /end of the Format check
				
				// String SummReport = "";
				
				// if(jcheckformatreport){SummReport=formatReport;}
				// //creating the individual summary for the file if they past the test
				// if starts here
				int totalRecorderYearsPerSite = 0;
				int totalFireScarsPerSite = 0;
				int totalAllIndicatorsPerSite = 0;
				double totalMeanFireIntervals = 0.0;
				int totalYearsWithFirePerSite = 0;
				
				if (jcheckindivreport && passFormat)
				{
					formatReport = formatReport + "\n\n" + " INDIVIDUAL SAMPLE SUMMARY FOR FHX FILE :" + myReaderArray.get(i).getName()
							+ "\n";
							
					// These are generated automatically now
					// myReaderArray.get(i).generateRecorderYearsArray();
					// myReaderArray.get(i).makeClimate();
					
					for (int k = 0; k < myReaderArray.get(i).getNumberOfSeries(); k++)
					{
						
						formatReport = formatReport + "\t\n" + "Sample: " + (k + 1) + "\t" + "Code: "
								+ myReaderArray.get(i).getSeriesNameArray().get(k) + "\n";
						if (myReaderArray.get(i).getPithIndexPerSample()[k] != -1)
						{
							formatReport = formatReport + "\t" + "Pith Ring: "
									+ (myReaderArray.get(i).getPithIndexPerSample()[k] + myReaderArray.get(i).getFirstYear()) + "\n";
						}
						if (myReaderArray.get(i).getInnerMostperTree()[k] != -1)
						{
							formatReport = formatReport + "\t" + "Inner Ring: "
									+ (myReaderArray.get(i).getInnerMostperTree()[k] + myReaderArray.get(i).getFirstYear()) + "\n";
						}
						if (myReaderArray.get(i).getBarkIndexPerTree()[k] != -1)
						{
							formatReport = formatReport + "\t" + "Bark Ring: "
									+ (myReaderArray.get(i).getBarkIndexPerTree()[k] + myReaderArray.get(i).getFirstYear()) + "\n";
						}
						if (myReaderArray.get(i).getOutterMostperTree()[k] != -1)
						{
							formatReport = formatReport + "\t" + "Outer Ring: "
									+ (myReaderArray.get(i).getOutterMostperTree()[k] + myReaderArray.get(i).getFirstYear()) + "\n";
						}
						
						FyearperSampletemp = new ArrayList<Integer>();
						// FIyearperSampletemp = new ArrayList<Integer>();
						for (int j = 0; j < myReaderArray.get(i).getYearArray().size(); j++)
						{
							if ((myReaderArray.get(i).getEventDataArrays(EventTypeToProcess.FIRE_EVENT).get(k).get(j) == 1))
							{
								FyearperSampletemp.add((j + myReaderArray.get(i).getFirstYear()));
							}
						}
						
						formatReport = formatReport + "\t" + "Length of sample: " + ((myReaderArray.get(i).getLastYearIndexPerSample()[k]
								- myReaderArray.get(i).getStartYearIndexPerSample()[k]) + 1) + "\n";
						formatReport = formatReport + "\t" + "Number of recorder years in sample: "
								+ (myReaderArray.get(i).getTotalRecorderYearsPerSample()[k]) + "\n";
						formatReport = formatReport + "\t" + "INFORMATION ON FIRE HISTORY: " + "\n";
						// log.debug("the total recorder years is: " + allrecorYearperSample[k] +
						// " total number of analysis:
						// "+(((myReader.get(i).getlastYearperSample()[k]+myReader.get(i).getFirstYear())-(myReader.get(i).getstartYearperSample()[k]+myReader.get(i).getFirstYear())+1)));
						FyearperSample.add(FyearperSampletemp);
						FIyearperSampletemp = new ArrayList<Integer>();
						int fisumtemp = 0;
						for (int jk = 0; jk < FyearperSample.get(k).size() - 1; jk++)
						{
							FIyearperSampletemp.add(FyearperSample.get(k).get(jk + 1) - FyearperSample.get(k).get(jk));
							fisumtemp = fisumtemp + FIyearperSampletemp.get(jk).intValue();
						}
						FIyearperSample.add(FIyearperSampletemp);
						int FIcount = 0;
						// log.debug("size of FIyearperSample "+ FIyearperSample.size()+ " X "+FIyearperSample.get(k).size());
						for (int j = 0; j < myReaderArray.get(i).getCalosYearperSample2d().get(k).size(); j++)
						{
							if ((myReaderArray.get(i).getCalosperSample2d().get(k).get(j) >= 'a')
									&& (myReaderArray.get(i).getCalosperSample2d().get(k).get(j) <= 'z'))
							{
								formatReport = formatReport + "\t   "
										+ (myReaderArray.get(i).getCalosYearperSample2d().get(k).get(j)
												+ myReaderArray.get(i).getFirstYear())
										+ "   " + myReaderArray.get(i).getCalosperSample2d().get(k).get(j) + "\n";
							}
							else
							{
								// log.debug("first cap year "+myReader.get(i).getCapsYearperSample2d().get(k).get(0)+"the other year
								// "+myReader.get(i).getCalosYearperSample2d().get(k).get(j));
								if (myReaderArray.get(i).getCapsYearperSample2d().get(k).get(0).intValue() == myReaderArray.get(i)
										.getCalosYearperSample2d().get(k).get(j).intValue())
								{
									formatReport = formatReport + "\t   "
											+ (myReaderArray.get(i).getCalosYearperSample2d().get(k).get(j)
													+ myReaderArray.get(i).getFirstYear())
											+ "   " + myReaderArray.get(i).getCalosperSample2d().get(k).get(j) + "\n";
									FIcount = 0;
								}
								else
								{
									formatReport = formatReport + "\t   "
											+ (myReaderArray.get(i).getCalosYearperSample2d().get(k).get(j)
													+ myReaderArray.get(i).getFirstYear())
											+ "   " + myReaderArray.get(i).getCalosperSample2d().get(k).get(j) + "    FI = "
											+ FIyearperSample.get(k).get(FIcount) + "\n";
									FIcount = FIcount + 1;
								}
								
							}
						}
						
						formatReport = formatReport + "\t" + "Total number of fire scars: "
								+ myReaderArray.get(i).getCapsperSample2d().get(k).size() + "\n";
						formatReport = formatReport + "\t" + "Total number of all indicators: "
								+ myReaderArray.get(i).getCalosYearperSample2d().get(k).size() + "\n";
						// log.debug("totalrecorderyearpersample: "+myReader.get(i).gettotalrecYearsperSample()[k]);
						// log.debug("capspersample: "+myReader.get(i).getCapsperSample2d().get(k).size());
						// log.debug("fireintervalsumtemp: "+fisumtemp);
						// log.debug("FireIntervalyearperSample: "+FIyearperSample.get(k).size());
						if (myReaderArray.get(i).getCapsperSample2d().get(k).size() != 0)
						{
							formatReport = formatReport + "\t" + "Average number years per fire: "
									+ onePlace.format((((double) myReaderArray.get(i).getTotalRecorderYearsPerSample()[k])
											/ myReaderArray.get(i).getCapsperSample2d().get(k).size()))
									+ "\n";
						}
						else
						{
							formatReport = formatReport + "\t" + "Average number years per fire: " + " NA \n";
						}
						if (FIyearperSample.get(k).size() != 0)
						{
							formatReport = formatReport + "\t" + "Sample mean fire interval: "
									+ onePlace.format((((double) fisumtemp) / FIyearperSample.get(k).size())) + "\n";
						}
						else
						{
							formatReport = formatReport + "\t" + "Sample mean fire interval: " + " NA \n";
						}
						
						totalRecorderYearsPerSite = totalRecorderYearsPerSite + myReaderArray.get(i).getTotalRecorderYearsPerSample()[k];
						totalFireScarsPerSite = totalFireScarsPerSite + myReaderArray.get(i).getCapsperSample2d().get(k).size();
						totalAllIndicatorsPerSite = totalAllIndicatorsPerSite
								+ myReaderArray.get(i).getCalosYearperSample2d().get(k).size();
						if (FIyearperSample.get(k).size() != 0)
						{
							totalMeanFireIntervals = totalMeanFireIntervals + (((double) fisumtemp) / FIyearperSample.get(k).size());
						}
						
						// log.debug("the size of FyearperSample is: "+ FyearperSample.size()+"X"+FyearperSample.get(k).size() );
					} // end of k loop number of series
					/*
					 * find totals for Recorder years per file totals for fire scars per file totals for all indicators avg number of years
					 * per fire: total number of recorder years/total number of fire scars avg number of years per all injuries: total
					 * number of recorder years/total number of all indicators avg all sample mean fire intervals total number of years with
					 * fire percentage of years with fires: total number of years with fire percentage of years with fire: percentage year
					 * MFI
					 */
					for (int j = 0; j < myReaderArray.get(i).getFireEventsArray().size(); j++)
					{
						
						if (myReaderArray.get(i).getFireEventsArray().get(j) == 1)
						{
							totalYearsWithFirePerSite = totalYearsWithFirePerSite + 1;
						}
					}
					// log.debug("total recorder years: "+ totalRecorderYearsPerSite );
					// log.debug("total fire scars: "+ totalFireScarsPerSite );
					// log.debug("total all indicators: "+ totalAllIndicatorsPerSite );
					// log.debug("total years with fire per site: "+ totalYearsWithFirePerSite );
					// log.debug("linecounter is: "+ linecounter);
					formatReport = formatReport + "\n\n" + " FINAL SUMMARY INFORMATION FOR ENTIRE SITE :" + "\n";
					formatReport = formatReport + "\t" + "Total number of recorder years: " + totalRecorderYearsPerSite + "\n";
					formatReport = formatReport + "\t" + "Total number of fire scars: " + totalFireScarsPerSite + "\n";
					formatReport = formatReport + "\t" + "Total number of all indicators: " + totalAllIndicatorsPerSite + "\n";
					if (totalFireScarsPerSite != 0)
					{
						formatReport = formatReport + "\t" + "Average number of years per fire: "
								+ onePlace.format((((double) totalRecorderYearsPerSite) / totalFireScarsPerSite)) + "\n";
					}
					else
					{
						formatReport = formatReport + "\t" + "Average number of years per fire: " + " NA \n";
					}
					if (totalAllIndicatorsPerSite != 0)
					{
						formatReport = formatReport + "\t" + "Average number of years per all indicators: "
								+ onePlace.format((((double) totalRecorderYearsPerSite) / totalAllIndicatorsPerSite)) + "\n";
					}
					else
					{
						formatReport = formatReport + "\t" + "Average number of years per all indicators: " + " NA \n";
					}
					formatReport = formatReport + "\t" + "Average all sample mean fire intervals: "
							+ onePlace.format(totalMeanFireIntervals / myReaderArray.get(i).getNumberOfSeries()) + "\n";
					formatReport = formatReport + "\t" + "Total number of years with fire: " + totalYearsWithFirePerSite + "\n";
					formatReport = formatReport + "\t" + "Percentage of years with fire: "
							+ onePlace.format(((double) totalYearsWithFirePerSite / linecounter * 100)) + "\n";
					formatReport = formatReport + "\t" + "Percentage of years with without fire: "
							+ onePlace.format((100 - ((double) totalYearsWithFirePerSite / linecounter * 100))) + "\n";
					formatReport = formatReport + "\t" + "Percentage of years with MFI: "
							+ onePlace.format((100 / ((double) totalYearsWithFirePerSite / linecounter * 100))) + "\n";
							
				} // end of if for indvireports
					// prg.setValue(i);
					// if(i==)
			} // end loop firts i
				// endendend
				// t.stop();
				// setCursor(Cursor.getDefaultCursor());
			/*
			 * create JFileChooser object to generate a browsing capabilities
			 */
			JFileChooser fileBrowse = new JFileChooser();
			
			if (outputFile == null)
			{
				
				fileBrowse = new JFileChooser(savePath.substring(0, savePath.lastIndexOf(File.separator)));
				
				/*
				 * set multiselect on (even though we don't need it)
				 */
				
				fileBrowse.setMultiSelectionEnabled(true);
				/*
				 * set file and folder directive
				 */
				
				fileBrowse.setFileSelectionMode(JFileChooser.FILES_ONLY);
				/*
				 * set file type: fire history x file fhx
				 */
				//
				// hola FileFilter filter1 = new ExtensionFileFilter("FHX: Fire History Extension", new String[]{ "FHX" });
				// holafileBrowse.setFileFilter(filter1);
				/*
				 * set dialog text: select the name and location of the matrix files
				 */
				fileBrowse.setDialogTitle("Select the name and location for the Report (outputfile):");
				/*
				 * here we get the save button information
				 */
				fileBrowseReturn = fileBrowse.showSaveDialog(parent);
				/*
				 * If the user wants to save then
				 */
				// log.debug("filebrowerrutorn" + fileBrowseReturn);
				if (fileBrowseReturn == 0)
				{
					/*
					 * set the save file
					 */
					
					outputFile = fileBrowse.getSelectedFile();
					// log.debug("DEBUG: fileBrowse.getSelectedFile = " + fileBrowse.getSelectedFile().toString());
					int l = outputFile.getName().length();
					/*
					 * set the extension of the file
					 */
					if (l <= 4 || !(outputFile.getName().substring(l - 4, l).equals(".txt")))
					{
						outputFile = new File(outputFile.getAbsolutePath() + ".txt");
						// log.debug("DEBUG: fileBrowse.getSelectedFile = " + outputFile.getName());
					}
				}
				
				/*
				 * create the write object for each of the files to be created
				 */
				Writer wr;
				/*
				 * Start writing information into the files
				 */
				try
				{
					/*
					 * First create each file
					 */
					wr = new BufferedWriter(new FileWriter(outputFile));
					wr.write("REPORT" + "\n");
					wr.write(formatReport);
					
					// for (int i = 0; i < inputFile.length; i++)
					// {
					// wr.write(inputFile[i].getName().substring(0,inputFile[i].getName().length()-4));
					// wr.write("\n");
					// }
					wr.close();
					FrameViewOutput dlg = new FrameViewOutput();
					dlg.TArea.setText(formatReport);
					dlg.setSize(800, 750);
					dlg.setVisible(true);
				} // end of Try
				catch (IOException ex)
				{
					ex.printStackTrace();
				}
				finally
				{
				
				}
			} // end of if filebrowserretur==0
				// else {
				// JOptionPane.showMessageDialog(this, "Since you didn't select a filename\nI'm not saving anything. Bye.");
				// }
		} // end of if for at least one file selected and one analysis (if run))
		else
		{
			JOptionPane.showMessageDialog(null,
					"Eggs are not supposed to be green.\nSelect at least One file and At least one report type  before continuing.",
					"Warning", JOptionPane.WARNING_MESSAGE);
		}
		
		return passFormat;
		
	}
	
	/**
	 * This method initializes buttonHelp
	 * 
	 * @return javax.swing.JButton
	 */
	private JButton getButtonHelp() {
		
		if (buttonHelp == null)
		{
			buttonHelp = new JButton();
			buttonHelp.setText("Help");
			buttonHelp.addActionListener(new java.awt.event.ActionListener() {
				
				@Override
				public void actionPerformed(java.awt.event.ActionEvent e) {
					
					// TODO Event stub for buttonHelp.actionPerformed()
					log.debug("buttonHelp actionPerformed()");
					FrameViewHelp dlg = new FrameViewHelp();
					dlg.TArea.setText(HelpReport);
					dlg.setSize(800, 750);
					dlg.setVisible(true);
					// System.exit(1);
				}
			});
		}
		return buttonHelp;
	}
	
	/**
	 * This method initializes buttonExit
	 * 
	 * @return javax.swing.JButton
	 */
	private JButton getButtonExit() {
		
		if (buttonExit == null)
		{
			buttonExit = new JButton();
			buttonExit.setText("Exit");
			buttonExit.addActionListener(new java.awt.event.ActionListener() {
				
				@Override
				public void actionPerformed(java.awt.event.ActionEvent e) {
					
					// TODO Event stub for buttonExit.actionPerformed()
					log.debug("buttonExit actionPerformed()");
					System.exit(1);
				}
			});
		}
		return buttonExit;
	}
	
	/**
	 * This method initializes jPanelBrowse
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanelBrowse() {
		
		if (jPanelBrowse == null)
		{
			jLabelBrowse = new JLabel();
			jLabelBrowse.setText("Click the Browse button to Select fhx file(s): ");
			jPanelBrowse = new JPanel();
			jPanelBrowse.setLayout(new FlowLayout());
			jPanelBrowse.add(jLabelBrowse, null);
			jPanelBrowse.add(getButtonBrowse(), null);
			Border border1 = BorderFactory.createEtchedBorder(Color.white, new Color(158, 158, 158));
			jPanelBrowse.setBorder(border1);
			// Border border2 = new TitledBorder(border1, "");
		}
		return jPanelBrowse;
	}
	
	/**
	 * This method initializes jPanelBar
	 * 
	 * @return javax.swing.JPanel
	 */
	// private JPanel getJPanelBar()
	// {
	// if (jPanelBar == null)
	// {
	// jPanelBar = new JPanel();
	// jPanelBar.setLayout(new FlowLayout());
	// Border border20 = BorderFactory.createEtchedBorder(Color.white, new Color(
	// 158, 158, 158));
	// Border border21 = new TitledBorder(border20, "Progress Bar");
	// jPanelBar.setBorder(border21);
	// prg = new JProgressBar(0,100);
	// tprg = new JProgressBar(0,task.getLengthOfTask());
	// prg.setValue(0);
	// prg.setStringPainted(true);
	// jPanelBar.add(prg);
	// t=new Timer(1000,new ActionListener(){
	// public void actionPerformed(ActionEvent ae){
	// if(numberoffiles==inputFile.length){
	// log.debug("inputFile length is zero");
	// }
	// else{
	// prg.setValue(numberoffiles);
	// log.debug("inputFile length is not zero");
	// }
	// }
	// });
	
	// }
	// return jPanelBar;
	// }
	
	/**
	 * This method initializes jPanelYearsAnother
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanelReportType() {
		
		if (jPanelReportType == null)
		{
			jPanelReportType = new JPanel();
			jPanelReportType.setLayout(new FlowLayout());
			Border border15 = BorderFactory.createEtchedBorder(Color.white, new Color(158, 158, 158));
			Border border16 = new TitledBorder(border15, "Report Type");
			jPanelReportType.setBorder(border16);
			jlabelformatreport = new JLabel();
			jlabelformatreport.setText("Create format report for each file(s)");
			jPanelReportType.add(jlabelformatreport, null);
			jPanelReportType.add(getJCheckformatreport());
			jlabelindivreport = new JLabel();
			jlabelindivreport.setText("Create individual sample summary report for file(s) that passed the format test");
			jPanelReportType.add(jlabelindivreport, null);
			jPanelReportType.add(getJCheckindivreport());
			// jPanelYearsAnother.add(new JLabel("Enter a number of the form 1,2,3,..."));
			
		}
		return jPanelReportType;
	}
	
	/**
	 * This method initializes jPanelExit
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanelExit() {
		
		if (jPanelExit == null)
		{
			jPanelExit = new JPanel();
			Border border3 = BorderFactory.createEtchedBorder(Color.white, new Color(158, 158, 158));
			jPanelExit.setBorder(border3);
			// Border border4 = new TitledBorder(border3, "");
			jPanelExit.setLayout(new FlowLayout());
			jPanelExit.add(getButtonRun(), null);
			jPanelExit.add(getButtonHelp(), null);
			jPanelExit.add(getButtonExit(), null);
		}
		return jPanelExit;
	}
	
	/**
	 * TODO
	 * 
	 * @return
	 */
	private JCheckBox getJCheckformatreport() {
		
		if (jcheckformatreport == null)
		{
			jcheckformatreport = new JCheckBox(" ", true);
		}
		return jcheckformatreport;
	}
	
	/**
	 * TODO
	 * 
	 * @return
	 */
	private JCheckBox getJCheckindivreport() {
		
		if (jcheckindivreport == null)
		{
			jcheckindivreport = new JCheckBox(" ", true);
		}
		return jcheckindivreport;
	}
	
	/**
	 * This method runs the new thread
	 * 
	 * @return javax.swing.
	 */
	// public class thread1 implements Runnable{
	// public void run(){
	
	// for (int i=0; i<=inputFile.length; i++){ //Progressively increment variable i
	// int i;
	// i=numberoffiles;
	// prg.setValue(i); //Set value
	// prg.repaint(); //Refresh graphics
	// try{Thread.sleep(500);} //Sleep 50 milliseconds
	// catch (Exception ex){}
	// }
	// }
	// }
	
}// end of the main class
