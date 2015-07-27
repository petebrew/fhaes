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
package org.fhaes.analysis;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.text.DecimalFormat;
import java.util.ArrayList;

import javax.swing.JOptionPane;

import org.fhaes.enums.EventTypeToProcess;
import org.fhaes.enums.FireFilterType;
import org.fhaes.enums.NoDataLabel;
import org.fhaes.fhfilereader.FHX2FileReader;
import org.fhaes.model.FHFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * FHMatrix Class.
 */
public class FHMatrix {
	
	private static final Logger log = LoggerFactory.getLogger(FHMatrix.class);
	
	private final FHFile[] inputFileArray;
	private File outputFileTree;
	private final Integer startYear;
	private final Integer endYear;
	private final Boolean siteMatrix;
	private final Boolean site00;
	private final Boolean site01;
	private final Boolean site10;
	private final Boolean site11;
	private final Boolean siteSum;
	private final Boolean binaryMatrix;
	private final Boolean ntpMatrix;
	private final Boolean filterByNumber;
	private final Boolean filterByPercentage;
	private final Boolean scohSim;
	private final Boolean sjacSim;
	private final Double filterValue;
	private final Integer overlapRequired;
	private final EventTypeToProcess eventType;
	
	@SuppressWarnings("unused")
	private Double noDataValue = -99.0;
	
	private Boolean highway = true;
	private File outputFileNTP;
	private File outputFileSite;
	private File outputFileM11;
	private File outputFileM10;
	private File outputFileM01;
	private File outputFileM00;
	private File outputFileSum;
	private File outputFileSCOH;
	private File outputFileDSCOH;
	private File outputFileSJAC;
	private File outputFileDSJAC;
	
	private Boolean debugfile = true;
	
	Integer minFirstYear = new Integer(9999);
	Integer maxLastYear = new Integer(0);
	
	/**
	 * New simplified constructor for FHAES GUI
	 * 
	 * @param inputFileArray - files to process
	 * @param startYear - start of year range to process
	 * @param endYear - end of year range to process
	 * @param filterType - composite filter type (number of fires or percentage)
	 * @param eventTypeToProcess - whether to do calculations on injuries or fires
	 * @param filterValue - composite filter value
	 * @param yearOverlapThreshold - number of years that must overlap between two sites
	 * @param noDataLabel - Label to use when comparison is not possible
	 */
	public FHMatrix(FHFile[] inputFileArray, Integer startYear, Integer endYear, FireFilterType filterType,
			EventTypeToProcess eventTypeToProcess, Double filterValue, Integer yearOverlapThreshold, NoDataLabel noDataLabel) {
			
		this.inputFileArray = inputFileArray;
		this.startYear = startYear;
		this.endYear = endYear;
		this.overlapRequired = yearOverlapThreshold;
		this.filterValue = filterValue;
		this.siteMatrix = true;
		this.site00 = true;
		this.site01 = true;
		this.site10 = true;
		this.site11 = true;
		this.siteSum = true;
		this.scohSim = true;
		this.sjacSim = true;
		this.binaryMatrix = true;
		this.ntpMatrix = true;
		this.eventType = eventTypeToProcess;
		this.noDataValue = noDataLabel.toDouble();
		
		if (filterType == null)
		{
			log.warn("FireFilterType in FHMatrix was null. Defaulting to 'Number'");
			this.filterByNumber = true;
			this.filterByPercentage = false;
		}
		else if (filterType.equals(FireFilterType.NUMBER_OF_EVENTS))
		{
			this.filterByNumber = true;
			this.filterByPercentage = false;
		}
		// else if(filterType.equals(FireFilterType.PERCENTAGE_OF_FIRES))
		else
		{
			this.filterByNumber = false;
			this.filterByPercentage = true;
		}
		
		runAnalysis();
		
	}
	
	/**
	 * Constructor for original GUI
	 * 
	 * @param inputFile
	 * @param outputFile
	 * @param startYear
	 * @param endYear
	 * @param siteMatrix
	 * @param site00
	 * @param site01
	 * @param site10
	 * @param site11
	 * @param siteSum
	 * @param binaryMatrix
	 * @param ntpMatrix
	 * @param filterByNumber
	 * @param filterByPercentage
	 * @param scohSim
	 * @param sjacSim
	 * @param fireEvent
	 * @param fireInjury
	 * @param filterValue
	 * @throws Exception
	 */
	@Deprecated
	public FHMatrix(FHFile[] inputFile, File outputFile, Integer startYear, Integer endYear, Boolean siteMatrix, Boolean site00,
			Boolean site01, Boolean site10, Boolean site11, Boolean siteSum, Boolean binaryMatrix, Boolean ntpMatrix,
			Boolean filterByNumber, Boolean filterByPercentage, Boolean scohSim, Boolean sjacSim, Boolean fireEvent, Boolean fireInjury,
			Double filterValue) throws Exception {
			
		this.inputFileArray = inputFile;
		this.outputFileTree = outputFile;
		this.startYear = startYear;
		this.endYear = endYear;
		this.siteMatrix = siteMatrix;
		this.site00 = site00;
		this.site01 = site01;
		this.site10 = site10;
		this.site11 = site11;
		this.siteSum = siteSum;
		this.binaryMatrix = binaryMatrix;
		this.ntpMatrix = ntpMatrix;
		this.filterByNumber = filterByNumber;
		this.filterByPercentage = filterByPercentage;
		this.scohSim = scohSim;
		this.sjacSim = sjacSim;
		
		if (fireEvent && fireInjury)
		{
			eventType = EventTypeToProcess.FIRE_AND_INJURY_EVENT;
		}
		else if (fireEvent)
		{
			eventType = EventTypeToProcess.FIRE_EVENT;
		}
		else if (fireInjury)
		{
			eventType = EventTypeToProcess.INJURY_EVENT;
		}
		else
		{
			throw new Exception("Invalid events specified.  Must have either Fire, Injury or both selected");
		}
		
		this.filterValue = filterValue;
		this.overlapRequired = 25;
		outputFileNTP = new File(outputFile.getAbsolutePath() + "NTP.tmp");
		outputFileSite = new File(outputFile.getAbsolutePath() + "site.tmp");
		outputFileM11 = new File(outputFile.getAbsolutePath() + "M11.tmp");
		outputFileM10 = new File(outputFile.getAbsolutePath() + "M10.tmp");
		outputFileM01 = new File(outputFile.getAbsolutePath() + "M01.tmp");
		outputFileM00 = new File(outputFile.getAbsolutePath() + "M00.tmp");
		outputFileSum = new File(outputFile.getAbsolutePath() + "Sum.tmp");
		outputFileSCOH = new File(outputFile.getAbsolutePath() + "SCOH.tmp");
		outputFileDSCOH = new File(outputFile.getAbsolutePath() + "DSCOH.tmp");
		outputFileSJAC = new File(outputFile.getAbsolutePath() + "SJAC.tmp");
		outputFileDSJAC = new File(outputFile.getAbsolutePath() + "DSJAC.tmp");
		
		/*
		 * outputFileFilters.deleteOnExit(); outputFileSite.deleteOnExit(); outputFileM11.deleteOnExit(); outputFileM10.deleteOnExit();
		 * outputFileM01.deleteOnExit(); outputFileM00.deleteOnExit(); outputFileSum.deleteOnExit(); outputFileSCOH.deleteOnExit();
		 * outputFileDSCOH.deleteOnExit(); outputFileSJAC.deleteOnExit(); outputFileDSJAC.deleteOnExit(); this.outputFile.deleteOnExit();
		 */
		
		runAnalysis();
	}
	
	/**
	 * Run the actual FHMatrix analysis
	 */
	@SuppressWarnings("deprecation")
	private void runAnalysis() {
		
		log.debug("Running FHMatrix analysis");
		
		// TODO Elena to implement
		if (this.overlapRequired > 0)
		{
		
		}
		
		/*
		 * If at least one file has been choosen then the program will run otherwise get message
		 */
		// FIRST CHECK
		if (inputFileArray != null)
		{
			// SECOND CHECK
			if (siteMatrix || site00 || site01 || site10 || site11 || siteSum || binaryMatrix || ntpMatrix)
			{
				// THIRD CHECK
				if (startYear <= endYear)
				{
				}
				else
				{
					JOptionPane.showMessageDialog(null, "Trees cannot grow after they die :'(.", "Warning", JOptionPane.WARNING_MESSAGE);
					return;
				}
			}
			else
			{
				JOptionPane.showMessageDialog(null, "At least one output file should be selected.", "Warning", JOptionPane.WARNING_MESSAGE);
				return;
			}
		}
		else
		{
			JOptionPane.showMessageDialog(null, "Select at least one file.", "Warning", JOptionPane.WARNING_MESSAGE);
			return;
		}
		
		ArrayList<FHX2FileReader> myReader = new ArrayList<FHX2FileReader>();
		
		// *** CURSOR VARIABLES *** //
		// Cursor hourglassCursor = new Cursor(Cursor.WAIT_CURSOR);
		// Cursor hourglassCursor =
		// Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR);
		// private static Cursor waitCursor =
		// Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR);
		// Cursor normalCursor = new Cursor(Cursor.DEFAULT_CURSOR);
		// setCursor(hourglassCursor);
		// setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		
		for (int i = 0; i < inputFileArray.length; i++)
		{
			myReader.add(new FHX2FileReader(inputFileArray[i]));
			System.out.println("read file: " + myReader.get(i).getName());
			if (eventType.equals(EventTypeToProcess.FIRE_EVENT))
			{
				// System.out.println("I am fire 1");
				myReader.get(i).makeClimate2d();
				myReader.get(i).makeFilters2d();
			}
			if (eventType.equals(EventTypeToProcess.INJURY_EVENT))
			{
				// System.out.println("I am Injury 1");
				myReader.get(i).makeClimate2dII();
				myReader.get(i).makefilters2dII();
			}
			if (eventType.equals(EventTypeToProcess.FIRE_AND_INJURY_EVENT))
			{
				// System.out.println("I am fire 1");
				myReader.get(i).makeClimate2dIII();
				myReader.get(i).makefilters2dIII();
			}
			// if (jCheckTree.isSelected())
			// {
			// myReader.get(i).makeClimate2d();
			// myReader.get(i).makefilters2d();
			// }
			// else
			if (siteMatrix || site00 || site01 || site10 || site11 || siteSum)
			{
				if (eventType.equals(EventTypeToProcess.FIRE_EVENT))
				{
					// System.out.println("I am fire 2");
					myReader.get(i).makeClimate();
				}
				if (eventType.equals(EventTypeToProcess.INJURY_EVENT))
				{
					// System.out.println("I am Injury 2");
					myReader.get(i).makeClimateI();
				}
				if (eventType.equals(EventTypeToProcess.FIRE_AND_INJURY_EVENT))
				{
					// System.out.println("I am fire and Injur2 2");
					myReader.get(i).makeClimateIII();
				}
				
			}
			// myReader.get(i).PrintReport();
			/*
			 * set the beginning year accounting for the filter
			 */
			if (eventType.equals(EventTypeToProcess.FIRE_EVENT))
			{
				// System.out.println("I am in fireevent 339 set years");
				if (startYear == 0 && highway)
				{
					// System.out.println("i am here in line 342");
					// System.out.println("First Fire Year " +
					// myReader.get(i).getFirstFireYear());
					// System.out.println("The FIRST YEAR for each file is " +
					// myReader.get(i).getFirstYear());
					// System.out.println("The LAST YEAR for each file is " +
					// myReader.get(i).getLastYear());
					// System.out.println("the minFirstYear is: " +
					// minFirstYear);
					
					if (myReader.get(i).getFirstFireYear() < minFirstYear)
					{
						// System.out.println("i am here in line 350");
						minFirstYear = myReader.get(i).getFirstFireYear();
						// System.out.println("the minFirstYear is: " +
						// minFirstYear);
					}
				}
				else if (startYear != 0 && highway)
				{
					if (myReader.get(i).getFirstYear() < minFirstYear)
					{
						minFirstYear = myReader.get(i).getFirstYear();
					}
				}
				if (startYear != 0)
				{
					minFirstYear = startYear;
					// minFirstYear = minFirstYear+1;
				}
			}
			if (eventType.equals(EventTypeToProcess.INJURY_EVENT))
			{
				// System.out.println("I am in Injury set years");
				if (startYear == 0 && highway)
				{
					// System.out.println("i am here in line 372");
					// System.out.println("First Fire Year " +
					// myReader.get(i).getFirstInjyryEventYear());
					// System.out.println("The FIRST YEAR for each file is " +
					// myReader.get(i).getFirstYear());
					// System.out.println("The LAST YEAR for each file is " +
					// myReader.get(i).getLastYear());
					// System.out.println("the minFirstYear is: " +
					// minFirstYear);
					
					if (myReader.get(i).getFirstInjuryYear() < minFirstYear)
					{
						// System.out.println("i am here in line 380");
						minFirstYear = myReader.get(i).getFirstInjuryYear();
						// System.out.println("the minFirstYear is: " +
						// minFirstYear);
					}
				}
				else if (startYear != 0 && highway)
				{
					if (myReader.get(i).getFirstYear() < minFirstYear)
					{
						minFirstYear = myReader.get(i).getFirstYear();
					}
				}
				if (startYear != 0)
				{
					minFirstYear = startYear;
					// minFirstYear = minFirstYear+1;
				}
			}
			if (eventType.equals(EventTypeToProcess.FIRE_AND_INJURY_EVENT))
			{
				// System.out.println("I am in fireevent 339 set years");
				if (startYear == 0 && highway)
				{
					// System.out.println("i am here in line 342");
					// System.out.println("First Fire Year " +
					// myReader.get(i).getFirstFireYear());
					// System.out.println("The FIRST YEAR for each file is " +
					// myReader.get(i).getFirstYear());
					// System.out.println("The LAST YEAR for each file is " +
					// myReader.get(i).getLastYear());
					// System.out.println("the minFirstYear is: " +
					// minFirstYear);
					
					if (myReader.get(i).getFirstIndicatorYear() < minFirstYear)
					{
						// System.out.println("i am here in line 350");
						minFirstYear = myReader.get(i).getFirstIndicatorYear();
						// System.out.println("the minFirstYear is: " +
						// minFirstYear);
					}
				}
				else if (startYear != 0 && highway)
				{
					if (myReader.get(i).getFirstYear() < minFirstYear)
					{
						minFirstYear = myReader.get(i).getFirstYear();
					}
				}
				if (startYear != 0)
				{
					minFirstYear = startYear;
					// minFirstYear = minFirstYear+1;
				}
			}
			/*
			 * Set the last year accounting for the filter
			 */
			// myReader.get(i).PrintReport();
			if (myReader.get(i).getLastYear() > maxLastYear)
			{
				maxLastYear = myReader.get(i).getLastYear();
			}
			if (endYear != 0)
			{
				maxLastYear = endYear;
			}
		}
		// System.out.println("the input filelength is" + inputFile.length);
		// System.out.println("The FIRST FIRE YEAR is " + minFirstYear);
		// System.out.println("The LAST YEAR is " + maxLastYear);
		
		// System.out.println("Minimum and Maximum years are " + minFirstYear +
		// " " + maxLastYear);
		/*
		 * Calculate the listYears the common years where the file will be analyzed
		 */
		ArrayList<Integer> listYears = new ArrayList<Integer>();
		for (int i = 0; i < maxLastYear - minFirstYear + 1; i++)
			listYears.add(minFirstYear + i);
		/*
		 * Declaration of all the array lists needed in the process
		 */
		ArrayList<ArrayList<Integer>> climateMatrix = new ArrayList<ArrayList<Integer>>();
		ArrayList<ArrayList<Integer>> climateMatrixSite = new ArrayList<ArrayList<Integer>>();
		ArrayList<ArrayList<Double>> filterMatrix = new ArrayList<ArrayList<Double>>();
		ArrayList<Integer> climateVector = new ArrayList<Integer>();
		// ArrayList<ArrayList<Double>> climateVectorFilters = new ArrayList<ArrayList<Double>>();
		ArrayList<ArrayList<Integer>> climateVector2 = new ArrayList<ArrayList<Integer>>();
		ArrayList<ArrayList<Double>> climateVectorFilter2 = new ArrayList<ArrayList<Double>>();
		ArrayList<Integer> climateVectorActual = null;
		ArrayList<Integer> climateVectorActualSite = null;
		ArrayList<Double> filterVectorActual = null;
		ArrayList<Integer> climateYear = new ArrayList<Integer>();
		int[][] temp = new int[listYears.size()][myReader.size()];
		int[][] matrix11 = new int[myReader.size()][];
		int[][] matrix10 = new int[myReader.size()][];
		int[][] matrix01 = new int[myReader.size()][];
		int[][] matrix00 = new int[myReader.size()][];
		int[][] matrixsum = new int[myReader.size()][];
		double[][] matrixApDdL = new double[myReader.size()][];
		double[][] matrixApBdL = new double[myReader.size()][];
		double[][] matrixApCdL = new double[myReader.size()][];
		double[][] matrixSCOHdeno = new double[myReader.size()][];
		double[][] matrixSCOH = new double[myReader.size()][myReader.size()];
		double[][] matrixDSCOH = new double[myReader.size()][myReader.size()];
		double[][] matrixSJACdeno = new double[myReader.size()][];
		double[][] matrixSJAC = new double[myReader.size()][myReader.size()];
		double[][] matrixDSJAC = new double[myReader.size()][myReader.size()];
		// String savePath = new String();
		// savePath = inputFileArray[0].getAbsolutePath();
		
		// ArrayList<String> XsectionName = new ArrayList<String>();
		
		// eeSystem.out.println("DEBUG: myReader's size is " + myReader.size());
		/*
		 * Set up either of the two filters two create the binary matrix on the case of binary analysis there are two possible filters:
		 * Number of fires and percentage of scarred trees.
		 */
		Integer firesFilter1 = new Integer(0);
		Double firesFilter2 = new Double(0);
		if (filterByNumber && filterValue != 1)
		{
			// eeSystem.out.println("number of fires is selected ");
			firesFilter1 = filterValue.intValue();
			// System.out.println("number of fires is selected is: "+
			// firesFilter1);
		}
		if (filterByPercentage && filterValue != 1)
		{
			System.out.println("percentage of fires is selected ");
			firesFilter2 = filterValue / 100.0;
			System.out.println("percentage of fires is selected is: " + firesFilter2);
		}
		
		/*
		 * start processing each file individually: The analysis can be done by either tree (non-binary) or by site (binary). by tree the
		 * box selected is: jCheckTree. by site the box selected is:
		 */
		for (int i = 0; i < myReader.size(); i++)
		{
			if (binaryMatrix || ntpMatrix || filterValue != 1)
			{
				/*
				 * get both matrices: 1. Climate2d binary matrix by tree 2. filters2d matrix composed of the 3 filters number of fires
				 * (total capital letter per row) total number of tree (total lower case letter plus bars counting only after a fire)
				 * percent of scared trees total fires/total trees
				 */
				if (eventType.equals(EventTypeToProcess.FIRE_EVENT))
				{
					// System.out.println("I am fire");
					climateVector2 = myReader.get(i).getClimate2d();
					climateVectorFilter2 = myReader.get(i).getfilters2d();
				}
				if (eventType.equals(EventTypeToProcess.INJURY_EVENT))
				{
					// System.out.println("I am Injury");
					climateVector2 = myReader.get(i).getClimate2dII();
					climateVectorFilter2 = myReader.get(i).getfilters2dII();
				}
				if (eventType.equals(EventTypeToProcess.FIRE_AND_INJURY_EVENT))
				{
					// System.out.println("I am Injury");
					climateVector2 = myReader.get(i).getClimate2dIII();
					climateVectorFilter2 = myReader.get(i).getfilters2dIII();
				}
				// climateVector2 = myReader.get(i).getClimate2d();
				// climateVectorFilter2 = myReader.get(i).getfilters2d();
				// eeSystem.out.println("I got climateVector 2d");
				// eeSystem.out.println("size by "+climateVector2.size()+" "+climateVector2);
			}
			// else
			if (siteMatrix || site00 || site01 || site10 || site11 || siteSum)
			{
				/*
				 * get matrix climate binary matrix by site (binary analysis)
				 */
				if (eventType.equals(EventTypeToProcess.FIRE_EVENT))
				{
					// System.out.println("I am fire");
					climateVector = myReader.get(i).getClimate();
				}
				if (eventType.equals(EventTypeToProcess.INJURY_EVENT))
				{
					// System.out.println("I am Injury");
					climateVector = myReader.get(i).getOtherInjuriesArray();
				}
				if (eventType.equals(EventTypeToProcess.FIRE_AND_INJURY_EVENT))
				{
					// System.out.println("I am fire");
					climateVector = myReader.get(i).getFiresAndInjuriesArray();
				}
				// climateVector = myReader.get(i).getClimate();
				// climateVectorFilters = myReader.get(i).getfilters2d();
				// eeSystem.out.println("I got climateVector");
				// eeSystem.out.println("I got climateVectorFilters");
				// eeSystem.out.println("size by "+climateVector.size()+" "+climateVector);
			}
			
			/*
			 * get the vector Year containing the vector of year of a given fhx file load it into the array list climateYear.
			 */
			
			climateYear = myReader.get(i).getYear();
			if (ntpMatrix || filterValue != 1)
			{
				/*
				 * if by tree analysis is selected create two matrices (array list) 1. filterMatrix containing the tree filter vectors only
				 * in between common years (so using the listYears array list subset of the years vectorf) 2. climateMatrix 2 dimensional
				 * array list containing binary matrices restricted to the listYear list.
				 */
				for (int ik = 0; ik < 3; ik++)
				{
					filterVectorActual = new ArrayList<Double>();
					for (int ij = 0; ij < listYears.size(); ij++)
					{
						if (climateYear.indexOf(listYears.get(ij)) == -1)
						{
							filterVectorActual.add(-1.0);
						}
						else
						{
							filterVectorActual.add(new Double(climateVectorFilter2.get(ik).get(climateYear.indexOf(listYears.get(ij)))));
						}
						
					}
					// System.out.println("size of filterVectorActual is : "+filterVectorActual.size()+" "+filterVectorActual);
					filterMatrix.add(filterVectorActual);
				}
				// System.out.println("size of the FilterMatrix is");
			} // /newforelena
				// System.out.println("filter "+ ik
				// +" is size "+filterVectorActual.size());
				// System.out.println("size of filterMatrix is: "+filterMatrix.size());
			if (ntpMatrix || binaryMatrix)
			{
				for (int k = 0; k < myReader.get(i).getNumberOfSeries(); k++)
				{
					climateVectorActual = new ArrayList<Integer>();
					for (int j = 0; j < listYears.size(); j++)
					{
						if (climateYear.indexOf(listYears.get(j)) == -1)
						{
							climateVectorActual.add(-1);
							
						}
						else
						{
							climateVectorActual.add(climateVector2.get(k).get(climateYear.indexOf(listYears.get(j))));
							
						}
					}
					// eeSystem.out.println("size by tree "+climateVectorActual.size()+" "+climateVectorActual);
					climateMatrix.add(climateVectorActual);
				}
				// myReader.get(i).makeSeriesName();
			} // end of if for jChecktree analysis by tree
				// else
			if (siteMatrix || site00 || site01 || site10 || site11 || siteSum)
			{
				climateVectorActualSite = new ArrayList<Integer>();
				// System.out.println("DEBUG I WAS HERE");
				for (int j = 0; j < listYears.size(); j++)
				{
					if (climateYear.indexOf(listYears.get(j)) == -1)
					{
						climateVectorActualSite.add(-1);
						temp[j][i] = -1;
					}
					else
					{
						if (filterValue != 1)
						{
							if (filterByNumber)
							{
								// climateVectorActualSite.add(climateVector.get(climateYear.indexOf(listYears.get(j))));
								// System.out.println("number of fires is selected is: "+
								// firesFilter1+" "+climateVector.get(climateYear.indexOf(listYears.get(j))));
								System.out.println("fire filter: " + firesFilter1 + " year is: " + listYears.get(j) + " fires: "
										+ filterMatrix.get(3 * i).get(j) + " climatevector: "
										+ climateVector.get(climateYear.indexOf(listYears.get(j))));
								if ((filterMatrix.get(3 * i).get(j) < firesFilter1)
										&& ((climateVector.get(climateYear.indexOf(listYears.get(j)))) != -1.0))
								{
									climateVectorActualSite.add(0);
									temp[j][i] = 0;
								}
								else
								{
									climateVectorActualSite.add(climateVector.get(climateYear.indexOf(listYears.get(j))));
									temp[j][i] = climateVector.get(climateYear.indexOf(listYears.get(j))).intValue();
								}
							}
							if (filterByPercentage)
							{
								// System.out.println("percent of fires is selected is: "+
								// firesFilter2+" "+climateVector.get(climateYear.indexOf(listYears.get(j))));
								// System.out.println("the filter percent of fires is"+filterMatrix.get(2).get(j));
								if ((filterMatrix.get(3 * i + 2).get(j) == -99))
								{
									climateVectorActualSite.add(-1);
									temp[j][i] = -1;
								}
								else
								{
									if ((filterMatrix.get(3 * i + 2).get(j) < firesFilter2)
											&& ((climateVector.get(climateYear.indexOf(listYears.get(j)))) != -1.0))
									{
										climateVectorActualSite.add(0);
										temp[j][i] = 0;
									}
									else
									{
										climateVectorActualSite.add(climateVector.get(climateYear.indexOf(listYears.get(j))));
										temp[j][i] = climateVector.get(climateYear.indexOf(listYears.get(j))).intValue();
									}
								}
							}
						} // end of if filter not equal to 1
						else
						{
							climateVectorActualSite.add(climateVector.get(climateYear.indexOf(listYears.get(j))));
							temp[j][i] = climateVector.get(climateYear.indexOf(listYears.get(j))).intValue();
						}
						// impoclimateVectorActualSite.add(climateVector.get(climateYear.indexOf(listYears.get(j))));
						// impotemp[j][i]=climateVector.get(climateYear.indexOf(listYears.get(j))).intValue();
					}
				}
				// eeSystem.out.println("size by site"+climateVectorActualSite.size()+" "+climateVectorActualSite);
				climateMatrixSite.add(climateVectorActualSite);
			}
			// System.out.println("DEBUG: SIZE climateMatrixSite " +
			// climateMatrixSite.size() + "x" +
			// climateMatrixSite.get(0).size());
			// System.out.println("DEBUG: SIZE tempmatrix " + temp.length + "x"
			// + temp[0].length);
			/*
			 * End of processing each file ie calculation of climateMatrix and filterMatrix
			 */
			System.out.println("Done Processing file " + myReader.get(i).getName());
		} // end of i loop
			// elena adds
		if (site11 || site01 || site10 || site00 || siteSum || scohSim || sjacSim)
		{
			// eeSystem.out.println("DEBUG I WAS HERE in matrix creation");
			for (int r = 0; r < matrix11.length; r++)
			{
				matrix11[r] = new int[r + 1]; // Allocate a row
				matrix10[r] = new int[r + 1];
				matrix01[r] = new int[r + 1];
				matrix00[r] = new int[r + 1];
				matrixsum[r] = new int[r + 1];
				//
				matrixApDdL[r] = new double[r + 1];
				matrixApBdL[r] = new double[r + 1];
				matrixApCdL[r] = new double[r + 1];
				matrixSCOHdeno[r] = new double[r + 1];
				matrixSJACdeno[r] = new double[r + 1];
				// matrixSCOH[r] = new double[r+1];
				// matrixDSCOH[r] = new double[r+1];
			}
			for (int r = 0; r < myReader.size(); r++)
			{
				// matrix11[r] = new int[r+1]; // Allocate a row
				for (int c = 0; c <= r; c++)
				{
					matrix11[r][c] = 0;
					matrix01[r][c] = 0;
					matrix10[r][c] = 0;
					matrix00[r][c] = 0;
					matrixsum[r][c] = 0;
					
					for (int i = 0; i < listYears.size(); i++)
					{
						if (temp[i][c] == 1 && temp[i][r] == 1)
						{
							matrix11[r][c] = matrix11[r][c] + 1;
						}
						else if (temp[i][c] == 1 && temp[i][r] == 0)
						{
							matrix10[r][c] = matrix10[r][c] + 1;
						}
						else if (temp[i][c] == 0 && temp[i][r] == 1)
						{
							matrix01[r][c] = matrix01[r][c] + 1;
						}
						else if (temp[i][c] == 0 && temp[i][r] == 0)
						{
							matrix00[r][c] = matrix00[r][c] + 1;
						}
					} // endofloop i
					matrixsum[r][c] = matrix11[r][c] + matrix01[r][c] + matrix10[r][c] + matrix00[r][c];
					matrixApDdL[r][c] = ((double) matrix11[r][c] + (double) matrix00[r][c]) / matrixsum[r][c];
					matrixApBdL[r][c] = ((double) matrix11[r][c] + (double) matrix01[r][c]) / matrixsum[r][c];
					matrixApCdL[r][c] = ((double) matrix11[r][c] + (double) matrix10[r][c]) / matrixsum[r][c];
					matrixSCOHdeno[r][c] = 1.0 - ((matrixApBdL[r][c]) * (matrixApCdL[r][c]));
					matrixSCOH[r][c] = (matrixApDdL[r][c] - (matrixApBdL[r][c] * matrixApCdL[r][c])) / matrixSCOHdeno[r][c];
					matrixSCOH[c][r] = matrixSCOH[r][c];
					matrixDSCOH[r][c] = 1.0 - matrixSCOH[r][c];
					matrixDSCOH[c][r] = matrixDSCOH[r][c];
					matrixSJACdeno[r][c] = matrix11[r][c] + matrix01[r][c] + matrix10[r][c];
					matrixSJAC[r][c] = matrix11[r][c] / matrixSJACdeno[r][c];
					matrixSJAC[c][r] = matrixSJAC[r][c];
					matrixDSJAC[r][c] = 1.0 - matrixSJAC[r][c];
					matrixDSJAC[c][r] = matrixDSJAC[r][c];
					
				} // end loop c
			} // end of loop r
			
		} // end of if statement
			// needs to add: fill the similarity and dissimilarity matrices
			// end of elena adds
		/*
		 * Start the process of generating the files necessary for each analysis by tree analysis choices: climateMatrix or filterMatrix.
		 * each a comma delimited file. by site analysis choices: climateMatrix and 5 different similarity matrices
		 * Matrix11,Matrix10,Matrix01,Matrix00,MatrixSum
		 */
		// set format
		DecimalFormat threePlaces = new DecimalFormat("0.000");
		/*
		 * create JFileChooser object to generate a browsing capabilities
		 */
		// JFileChooser fileBrowse = new JFileChooser();
		
		// fileBrowse = new JFileChooser(
		// savePath.substring(0,savePath.lastIndexOf(File.separator)));
		
		/*
		 * set multiselect on (even though we don't need it)
		 */
		// fileBrowse.setMultiSelectionEnabled(true);
		/*
		 * set file and folder directive
		 */
		// fileBrowse.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
		/*
		 * set file type: coma delimited file csv
		 */
		//
		// FileFilter filter1 = new
		// ExtensionFileFilter("CSV: Comma-Separated Values File", new String[]
		// { "CSV" });
		// fileBrowse.setFileFilter(filter1);
		/*
		 * set dialog text: select the name and location of the matrix files
		 */
		// fileBrowse.setDialogTitle("Select the name and location of the Fire Matrix file:");
		/*
		 * here we get the save button information
		 */
		// fileBrowseReturn = fileBrowse.showSaveDialog(buttonRun);
		/*
		 * If the user wants to save then
		 */
		// if (fileBrowseReturn == 0)
		// {
		/*
		 * set the save file
		 */
		// eeSystem.out.println("DEBUG: fileBrowse.getSelectedFile = " +
		// fileBrowse.getSelectedFile().toString());
		// outputFile = fileBrowse.getSelectedFile();
		// int l = outputFile.getName().length();
		/*
		 * set the extension of the file
		 */
		// if (l <= 4
		// || !(outputFile.getName().substring(l - 4, l).equals(".tmp"))) {
		
		try
		{
			if (ntpMatrix && outputFileNTP == null)
			{
				// outputFileFilters = new
				// File(outputFile.getAbsolutePath()+"NTP.tmp");
				outputFileNTP = File.createTempFile("FHMatrix", "NTP.tmp");
				outputFileNTP.deleteOnExit();
			}
			if (siteMatrix && outputFileSite == null)
			{
				// outputFileSite = new
				// File(outputFile.getAbsolutePath()+"site.tmp");
				outputFileSite = File.createTempFile("FHMatrix", "site.tmp");
				outputFileSite.deleteOnExit();
			}
			if (site11 && outputFileM11 == null)
			{
				// outputFileM11 = new
				// File(outputFile.getAbsolutePath()+"M11.tmp");
				outputFileM11 = File.createTempFile("FHMatrix", "M11.tmp");
				outputFileM11.deleteOnExit();
			}
			if (site10 && outputFileM10 == null)
			{
				// outputFileM10 = new
				// File(outputFile.getAbsolutePath()+"M10.tmp");
				outputFileM10 = File.createTempFile("FHMatrix", "M10.tmp");
				outputFileM10.deleteOnExit();
			}
			if (site01 && outputFileM01 == null)
			{
				// outputFileM01 = new
				// File(outputFile.getAbsolutePath()+"M01.tmp");
				outputFileM01 = File.createTempFile("FHMatrix", "M01.tmp");
				outputFileM01.deleteOnExit();
			}
			if (site00 && outputFileM00 == null)
			{
				// outputFileM00 = new
				// File(outputFile.getAbsolutePath()+"M00.tmp");
				outputFileM00 = File.createTempFile("FHMatrix", "M00.tmp");
				outputFileM00.deleteOnExit();
			}
			if (siteSum && outputFileSum == null)
			{
				// outputFileSum = new
				// File(outputFile.getAbsolutePath()+"Sum.tmp");
				outputFileSum = File.createTempFile("FHMatrix", "Sum.tmp");
				outputFileSum.deleteOnExit();
			}
			if (scohSim && outputFileSCOH == null)
			{
				// outputFileSCOH = new File(outputFile.getAbsolutePath() +
				// "SCOH.tmp");
				outputFileSCOH = File.createTempFile("FHMatrix", "SCOH.tmp");
				outputFileSCOH.deleteOnExit();
			}
			if (scohSim && outputFileDSCOH == null)
			{
				// outputFileDSCOH = new File(outputFile.getAbsolutePath() +
				// "DSCOH.tmp");
				outputFileDSCOH = File.createTempFile("FHMatrix", "DSCOH.tmp");
				outputFileDSCOH.deleteOnExit();
			}
			if (sjacSim && outputFileSJAC == null)
			{
				// outputFileSJAC = new File(outputFile.getAbsolutePath() +
				// "SJAC.tmp");
				outputFileSJAC = File.createTempFile("FHMatrix", "SJAC.tmp");
				outputFileSJAC.deleteOnExit();
			}
			if (sjacSim && outputFileDSJAC == null)
			{
				// outputFileDSJAC = new File(outputFile.getAbsolutePath() +
				// "DSJAC.tmp");
				outputFileDSJAC = File.createTempFile("FHMatrix", "DSJAC.tmp");
				outputFileDSJAC.deleteOnExit();
			}
			if (binaryMatrix && outputFileTree == null)
			{
				// outputFile = new File(outputFile.getAbsolutePath() +
				// ".tmp");
				outputFileTree = File.createTempFile("FHMatrix", ".tmp");
				outputFileTree.deleteOnExit();
			}
		}
		catch (IOException e)
		{
			log.error("Failed to create output file");
			e.printStackTrace();
			return;
		}
		// }
		/*
		 * create the write object for each of the files to be created
		 */
		Writer wr;
		Writer wrfilters;
		Writer wrSite;
		Writer wrM11;
		Writer wrM10;
		Writer wrM01;
		Writer wrM00;
		Writer wrMSum;
		Writer wrSCOH;
		Writer wrDSCOH;
		Writer wrSJAC;
		Writer wrDSJAC;
		/*
		 * set delimiter in this case we are using comas ","
		 */
		String delim = ",";
		// eeSystem.out.println("myReader size " + myReader.size());
		// eeSystem.out.println("climateMatrix size " + climateMatrix.size());
		/*
		 * Start writing information into the files
		 */
		try
		{
			
			if (binaryMatrix)
			{
				/*
				 * First create each file
				 */
				wr = new BufferedWriter(new FileWriter(outputFileTree));
				/*
				 * Maintain the format for each file the common years is the first column of the file.
				 */
				
				// eeSystem.out.println("listYears size " + listYears.size());
				/*
				 * write header of each file some files have two lines of header.
				 */
				// write empty cell
				wr.write("Site" + delim);
				// for each file write the name of the file
				for (int i = 0; i < inputFileArray.length; i++)
				{
					// int ll=inputFile[i].getName().length();
					// header for by tree matrix
					for (int k = 0; k < myReader.get(i).getNumberOfSeries(); k++)
					{
						
						if ((i == inputFileArray.length - 1) && (k == myReader.get(i).getNumberOfSeries() - 1))
						{
							wr.write(inputFileArray[i].getLabel());
						}
						else
						{
							wr.write(inputFileArray[i].getLabel() + delim);
						}
						
					}
				}
				// add newline
				wr.write(System.getProperty("line.separator")); // to the by tree matrix file
				/*
				 * Second line of the header
				 */
				wr.write("Tree" + delim);
				// write the fhx filenames to the header
				for (int i = 0; i < inputFileArray.length; i++)
				{
					if (binaryMatrix)
					{
						for (int k = 0; k < myReader.get(i).getNumberOfSeries(); k++)
						{
							wr.write(myReader.get(i).getSeriesNameArray().get(k) + delim);
						}
					}
					else
					{
						wr.write(inputFileArray[i].getLabel() + delim);
					}
				}
				wr.write(System.getProperty("line.separator"));
				
				//
				for (int i = 0; i < listYears.size(); i++)
				{
					// eeSystem.out.print(listYears.get(i) + delim);
					wr.write(listYears.get(i) + delim);
					for (int j = 0; j < climateMatrix.size(); j++)
					{
						wr.write(climateMatrix.get(j).get(i) + delim);
						
					}
					wr.write(System.getProperty("line.separator"));
				}
				wr.close();
			}
			// elena add
			if (ntpMatrix)
			{
				wrfilters = new BufferedWriter(new FileWriter(outputFileNTP));
				wrfilters.write(" " + delim);
				for (int i = 0; i < inputFileArray.length; i++)
				{
					// header for by tree matrix
					for (int k = 0; k < 3; k++)
					{
						
						if (k == 2 && (i == inputFileArray.length - 1))
						{
							wrfilters.write(inputFileArray[i].getLabel());
						}
						else
						{
							wrfilters.write(inputFileArray[i].getLabel() + delim);
						}
					}
				}
				wrfilters.write(System.getProperty("line.separator")); // to the filtered matrix file
				wrfilters.write("Years " + delim);
				for (int i = 0; i < inputFileArray.length; i++)
				{
					wrfilters.write("Number of Fires" + delim + "Number of Trees" + delim + "Percent of Scarred Trees" + delim);
				}
				wrfilters.write(System.getProperty("line.separator"));
				for (int i = 0; i < listYears.size(); i++)
				{
					wrfilters.write(listYears.get(i) + delim);
					for (int jf = 0; jf < filterMatrix.size(); jf++)
					{
						wrfilters.write(filterMatrix.get(jf).get(i) + delim);
					}
					wrfilters.write(System.getProperty("line.separator"));
				}
				wrfilters.close();
			} // end if treeV1
			if (siteMatrix)
			{
				/*
				 * First create each file
				 */
				// LatLonInfoArray mylatlonarray = new LatLonInfoArray();
				wrSite = new BufferedWriter(new FileWriter(outputFileSite));
				/*
				 * some files have two lines of header.
				 */
				wrSite.write("Years" + delim);
				FHFile[] fhfileArray = new FHFile[inputFileArray.length];
				// write the FHX labels to the header
				for (int i = 0; i < inputFileArray.length; i++)
				{
					if (i == (inputFileArray.length - 1))
					{
						wrSite.write(inputFileArray[i].getLabel());
					}
					else
					{
						wrSite.write(inputFileArray[i].getLabel() + delim);
					}
					
					// Save FHFile version of file to array
					FHFile fhf = new FHFile(inputFileArray[i].getAbsoluteFile());
					fhfileArray[i] = fhf;
				}
				wrSite.write(System.getProperty("line.separator"));
				
				// Longitude from header
				wrSite.write("Longitude" + delim);
				for (int i = 0; i < inputFileArray.length; i++)
				{
					String value = "N/A";
					if (fhfileArray[i].getFirstLongitude() != null)
					{
						value = fhfileArray[i].getFirstLongitude().toString();
					}
					if (i == (inputFileArray.length - 1))
					{
						wrSite.write(value);
					}
					else
					{
						wrSite.write(value + delim);
					}
				}
				wrSite.write(System.getProperty("line.separator"));
				
				// Latitude from header
				wrSite.write("Latitude" + delim);
				for (int i = 0; i < inputFileArray.length; i++)
				{
					String value = "N/A";
					if (fhfileArray[i].getFirstLatitude() != null)
					{
						value = fhfileArray[i].getFirstLatitude().toString();
					}
					if (i == (inputFileArray.length - 1))
					{
						wrSite.write(value);
					}
					else
					{
						wrSite.write(value + delim);
					}
				}
				wrSite.write(System.getProperty("line.separator"));
				
				//
				for (int i = 0; i < listYears.size(); i++)
				{
					wrSite.write(listYears.get(i) + delim);
					for (int j = 0; j < climateMatrixSite.size(); j++)
					{
						wrSite.write(climateMatrixSite.get(j).get(i) + delim);
					}
					// System.out.print(System.getProperty("line.separator"));
					wrSite.write(System.getProperty("line.separator"));
				}
				wrSite.close();
			} // end if SiteMatrix
			if (site11)
			{
				/*
				 * First create each file
				 */
				// System.out.println("I am here");
				// wrM11 = new BufferedWriter(new FileWriter(outputFileM11));
				wrM11 = new BufferedWriter(new FileWriter(outputFileM11));
				wrM11.write("    " + delim);
				// write the fhx filenames to the header
				for (int i = 0; i < inputFileArray.length; i++)
				{
					if (i == (inputFileArray.length - 1))
					{
						wrM11.write(inputFileArray[i].getLabel());
					}
					else
					{
						wrM11.write(inputFileArray[i].getLabel() + delim);
					}
				}
				wrM11.write(System.getProperty("line.separator"));
				for (int r = 0; r < matrix11.length; r++)
				{
					// wrM11.write(inputFile[r].getName() + delim);
					
					wrM11.write(inputFileArray[r].getLabel() + delim);
					for (int c = 0; c < matrix11[r].length; c++)
					{
						
						if (c == (matrix11.length - 1))
						{
							wrM11.write(matrix11[r][c]);
						}
						else
						{
							wrM11.write(matrix11[r][c] + delim);
						}
						// System.out.print(matrixSCOH[r][c]+"\t");
					}
					;
					// System.out.println();
					wrM11.write(System.getProperty("line.separator"));
				}
				// wrM11.write(System.getProperty("line.separator"));
				wrM11.close();
			} // end if site11
				// /elena add similarities
			
			if (scohSim)
			{
				/*
				 * First create each file
				 */
				wrSCOH = new BufferedWriter(new FileWriter(outputFileSCOH));
				wrSCOH.write("    " + delim);
				// write the fhx filenames to the header
				for (int i = 0; i < inputFileArray.length; i++)
				{
					if (debugfile)
					{
						if (i == (inputFileArray.length - 1))
						{
							// System.out.println("filename");
							wrSCOH.write(inputFileArray[i].getLabel());
						}
						else
						{
							wrSCOH.write(inputFileArray[i].getLabel() + delim);
						}
					}
					else
					{
						if (i == (inputFileArray.length - 1))
						{
							// System.out.println("sitecode");
							wrSCOH.write(inputFileArray[i].getLabel());
						}
						else
						{
							// System.out.println("sitecode");
							wrSCOH.write(inputFileArray[i].getLabel() + delim);
						}
						// System.out.println("sitecode");
						// wrSCOH.write(mylatlonarray.searchByFilename(inputFile[i].getName()).getLabel()
						// + delim);
					}
					// wrSCOH.write(inputFile[i].getName() + delim);
					// wrSCOH.write(mylatlonarray.searchByFilename(inputFile[i].getName()).getLabel()
					// + delim);
				}
				wrSCOH.write(System.getProperty("line.separator"));
				for (int r = 0; r < matrix11.length; r++)
				{
					
					// System.out.println("sitecode");
					wrSCOH.write(inputFileArray[r].getLabel() + delim);
					
					// wrSCOH.write(inputFile[r].getName() + delim);
					// wrSCOH.write(mylatlonarray.searchByFilename(inputFile[r].getName()).getLabel()
					// + delim);
					for (int c = 0; c < matrix11.length; c++)
					{
						if (c == (matrix11.length - 1))
						{
							wrSCOH.write(threePlaces.format(matrixSCOH[r][c]));
							// System.out.print("no coma");
						}
						else
						{
							wrSCOH.write(threePlaces.format(matrixSCOH[r][c]) + delim);
							// System.out.print(matrixDSCOH[r][c]+"\t");
						}
					}
					;
					// System.out.println();
					wrSCOH.write(System.getProperty("line.separator"));
				}
				wrSCOH.close();
			} // end of SCOHmatrix
			
			if (scohSim)
			{
				/*
				 * First create each file
				 */
				wrDSCOH = new BufferedWriter(new FileWriter(outputFileDSCOH));
				wrDSCOH.write("    " + delim);
				// write the fhx filenames to the header
				for (int i = 0; i < inputFileArray.length; i++)
				{
					if (debugfile)
					{
						if (i == (inputFileArray.length - 1))
						{
							// System.out.println("filename");
							wrDSCOH.write(inputFileArray[i].getLabel());
						}
						else
						{
							wrDSCOH.write(inputFileArray[i].getLabel() + delim);
						}
					}
					else
					{
						if (i == (inputFileArray.length - 1))
						{
							// System.out.println("sitecode");
							wrDSCOH.write(inputFileArray[i].getLabel());
						}
						else
						{
							// System.out.println("sitecode");
							wrDSCOH.write(inputFileArray[i].getLabel() + delim);
						}
						// System.out.println("sitecode");
						// wrDSCOH.write(mylatlonarray.searchByFilename(inputFile[i].getName()).getLabel()
						// + delim);
					}
					// wrDSCOH.write(inputFile[i].getName() + delim);
					// wrDSCOH.write(mylatlonarray.searchByFilename(inputFile[i].getName()).getLabel()
					// + delim);
				}
				wrDSCOH.write(System.getProperty("line.separator"));
				for (int r = 0; r < matrix11.length; r++)
				{
					if (debugfile)
					{
						// System.out.println("filename");
						wrDSCOH.write(inputFileArray[r].getLabel() + delim);
					}
					else
					{
						// System.out.println("sitecode");
						wrDSCOH.write(inputFileArray[r].getLabel() + delim);
					}
					// wrDSCOH.write(inputFile[r].getName() + delim);
					// wrDSCOH.write(mylatlonarray.searchByFilename(inputFile[r].getName()).getLabel()
					// + delim);
					for (int c = 0; c < matrix11.length; c++)
					{
						if (c == (matrix11.length - 1))
						{
							wrDSCOH.write(threePlaces.format(matrixDSCOH[r][c]));
							// System.out.print("no coma");
						}
						else
						{
							wrDSCOH.write(threePlaces.format(matrixDSCOH[r][c]) + delim);
							// System.out.print(matrixDSCOH[r][c]+"\t");
						}
					}
					;
					// System.out.println();
					wrDSCOH.write(System.getProperty("line.separator"));
				}
				wrDSCOH.close();
			} // end of DSCOHmatrix
			
			if (sjacSim)
			{
				/*
				 * First create each file
				 */
				wrSJAC = new BufferedWriter(new FileWriter(outputFileSJAC));
				wrSJAC.write("    " + delim);
				// write the fhx filenames to the header
				for (int i = 0; i < inputFileArray.length; i++)
				{
					// System.out.println("main(863) sitecode : " +
					// mylatlonarray.searchByFilename(inputFile[i].getName()).getLabel());
					if (debugfile)
					{
						if (i == (inputFileArray.length - 1))
						{
							// System.out.println("filename");
							wrSJAC.write(inputFileArray[i].getLabel());
							
						}
						else
						{
							// System.out.println("filename");
							wrSJAC.write(inputFileArray[i].getLabel() + delim);
						}
						
					}
					else
					{
						if (i == (inputFileArray.length - 1))
						{
							// System.out.println("sitecode");
							wrSJAC.write(inputFileArray[i].getLabel());
						}
						else
						{
							// System.out.println("sitecode");
							wrSJAC.write(inputFileArray[i].getLabel() + delim);
						}
					}
					// wrSJAC.write(inputFile[i].getName() + delim);
					// wrSJAC.write(mylatlonarray.searchByFilename(inputFile[i].getName()).getLabel()
					// + delim);
				}
				wrSJAC.write(System.getProperty("line.separator"));
				for (int r = 0; r < matrix11.length; r++)
				{
					// wrSJAC.write(inputFile[r].getName() + delim);
					
					// System.out.println("sitecode");
					wrSJAC.write(inputFileArray[r].getLabel() + delim);
					
					// wrSJAC.write(mylatlonarray.searchByFilename(inputFile[r].getName()).getLabel()
					// + delim);
					for (int c = 0; c < matrix11.length; c++)
					{
						if (c == (matrix11.length - 1))
						{
							wrSJAC.write(threePlaces.format(matrixSJAC[r][c]));
						}
						else
						{
							wrSJAC.write(threePlaces.format(matrixSJAC[r][c]) + delim);
						}
						// eeSystem.out.print(threePlaces.format(matrixSJAC[r][c])+"\t");
					}
					;
					// System.out.println();
					wrSJAC.write(System.getProperty("line.separator"));
				}
				wrSJAC.close();
			} // end of SJACmatrix
			
			if (sjacSim)
			{
				/*
				 * First create each file
				 */
				wrDSJAC = new BufferedWriter(new FileWriter(outputFileDSJAC));
				wrDSJAC.write("    " + delim);
				// write the fhx filenames to the header
				for (int i = 0; i < inputFileArray.length; i++)
				{
					// wrDSJAC.write(inputFile[i].getName() + delim);
					if (debugfile)
					{
						if (i == (inputFileArray.length - 1))
						{
							// System.out.println("filename");
							wrDSJAC.write(inputFileArray[i].getLabel());
						}
						else
						{
							// System.out.println("filename");
							wrDSJAC.write(inputFileArray[i].getLabel() + delim);
						}
					}
					else
					{
						if (i == (inputFileArray.length - 1))
						{
							// System.out.println("sitecode");
							wrDSJAC.write(inputFileArray[i].getLabel());
						}
						else
						{
							// System.out.println("sitecode");
							wrDSJAC.write(inputFileArray[i].getLabel() + delim);
						}
					}
					// wrDSJAC.write(mylatlonarray.searchByFilename(inputFile[i].getName()).getLabel()
					// + delim);
				}
				wrDSJAC.write(System.getProperty("line.separator"));
				for (int r = 0; r < matrix11.length; r++)
				{
					// wrDSJAC.write(inputFile[r].getName() + delim);
					
					// System.out.println("sitecode");
					wrDSJAC.write(inputFileArray[r].getLabel() + delim);
					
					// wrDSJAC.write(mylatlonarray.searchByFilename(inputFile[r].getName()).getLabel()
					// + delim);
					for (int c = 0; c < matrix11.length; c++)
					{
						if (c == (matrix11.length - 1))
						{
							wrDSJAC.write(threePlaces.format(matrixDSJAC[r][c]));
							// System.out.print(matrixDSJAC[r][c]+"\t");
						}
						else
						{
							wrDSJAC.write(threePlaces.format(matrixDSJAC[r][c]) + delim);
							// System.out.print(matrixDSJAC[r][c]+"\t");
						}
					}
					;
					// System.out.println();
					wrDSJAC.write(System.getProperty("line.separator"));
				}
				wrDSJAC.close();
			} // end of DSJACmatrix
			
			// /elena end add similarities
			if (site01)
			{
				/*
				 * First create each file
				 */
				// System.out.println("I am here");
				wrM01 = new BufferedWriter(new FileWriter(outputFileM01));
				wrM01.write("    " + delim);
				// write the fhx filenames to the header
				for (int i = 0; i < inputFileArray.length; i++)
				{
					if (i == inputFileArray.length - 1)
					{
						wrM01.write(inputFileArray[i].getLabel());
					}
					else
					{
						wrM01.write(inputFileArray[i].getLabel() + delim);
					}
					
				}
				wrM01.write(System.getProperty("line.separator"));
				for (int r = 0; r < matrix01.length; r++)
				{
					// wrM01.write(inputFile[r].getName() + delim);
					if (debugfile)
					{
						// System.out.println("filename");
						wrM01.write(inputFileArray[r].getLabel() + delim);
					}
					else
					{
						// System.out.println("sitecode");
						wrM01.write(inputFileArray[r].getLabel() + delim);
					}
					// wrM01.write(mylatlonarray.searchByFilename(inputFile[r].getName()).getLabel()
					// + delim);
					for (int c = 0; c < matrix01[r].length; c++)
					{
						wrM01.write(matrix01[r][c] + delim);
					}
					wrM01.write(System.getProperty("line.separator"));
				}
				// wrM11.write(System.getProperty("line.separator"));
				wrM01.close();
			} // end if site01
			if (site10)
			{
				/*
				 * First create each file
				 */
				// System.out.println("I am here");
				wrM10 = new BufferedWriter(new FileWriter(outputFileM10));
				wrM10.write("    " + delim);
				// write the fhx filenames to the header
				for (int i = 0; i < inputFileArray.length; i++)
				{
					if (i == inputFileArray.length - 1)
					{
						wrM10.write(inputFileArray[i].getLabel());
					}
					else
					{
						wrM10.write(inputFileArray[i].getLabel() + delim);
					}
				}
				wrM10.write(System.getProperty("line.separator"));
				for (int r = 0; r < matrix10.length; r++)
				{
					// wrM10.write(inputFile[r].getName() + delim);
					
					// System.out.println("sitecode");
					wrM10.write(inputFileArray[r].getLabel() + delim);
					
					// wrM10.write(mylatlonarray.searchByFilename(inputFile[r].getName()).getLabel()
					// + delim);
					for (int c = 0; c < matrix10[r].length; c++)
					{
						wrM10.write(matrix10[r][c] + delim);
					}
					wrM10.write(System.getProperty("line.separator"));
				}
				// wrM11.write(System.getProperty("line.separator"));
				wrM10.close();
			} // end if site10
			if (site00)
			{
				/*
				 * First create each file
				 */
				// System.out.println("I am here");
				wrM00 = new BufferedWriter(new FileWriter(outputFileM00));
				wrM00.write("    " + delim);
				// write the fhx filenames to the header
				for (int i = 0; i < inputFileArray.length; i++)
				{
					if (i == inputFileArray.length - 1)
					{
						wrM00.write(inputFileArray[i].getLabel());
					}
					else
					{
						wrM00.write(inputFileArray[i].getLabel() + delim);
					}
				}
				wrM00.write(System.getProperty("line.separator"));
				for (int r = 0; r < matrix00.length; r++)
				{
					// wrM00.write(inputFile[r].getName() + delim);
					
					// System.out.println("sitecode");
					wrM00.write(inputFileArray[r].getLabel() + delim);
					
					// wrM00.write(mylatlonarray.searchByFilename(inputFile[r].getName()).getLabel()
					// + delim);
					for (int c = 0; c < matrix00[r].length; c++)
					{
						wrM00.write(matrix00[r][c] + delim);
					}
					wrM00.write(System.getProperty("line.separator"));
				}
				// wrM11.write(System.getProperty("line.separator"));
				wrM00.close();
			} // end if site11
			if (siteSum)
			{
				/*
				 * First create each file
				 */
				// System.out.println("I am here");
				wrMSum = new BufferedWriter(new FileWriter(outputFileSum));
				// wrMSum = new BufferedWriter(new FileWriter(outputFileSum));
				wrMSum.write("    " + delim);
				// write the fhx filenames to the header
				for (int i = 0; i < inputFileArray.length; i++)
				{
					if (i == inputFileArray.length - 1)
					{
						wrMSum.write(inputFileArray[i].getLabel());
					}
					else
					{
						wrMSum.write(inputFileArray[i].getLabel() + delim);
					}
				}
				wrMSum.write(System.getProperty("line.separator"));
				for (int r = 0; r < matrixsum.length; r++)
				{
					// wrMSum.write(inputFile[r].getName() + delim);
					
					// System.out.println("sitecode");
					wrMSum.write(inputFileArray[r].getLabel() + delim);
					
					// wrMSum.write(mylatlonarray.searchByFilename(inputFile[r].getName()).getLabel()
					// + delim);
					for (int c = 0; c < matrixsum[r].length; c++)
					{
						wrMSum.write(matrixsum[r][c] + delim);
					}
					wrMSum.write(System.getProperty("line.separator"));
				}
				// wrM11.write(System.getProperty("line.separator"));
				wrMSum.close();
			} // end if siteSum
				// elenaend adding
			
		} // end of Try
		catch (IOException ex)
		{
			ex.printStackTrace();
		}
		finally
		{
		
		}
	}
	
	public File getFileNTPResult() {
		
		return outputFileNTP;
	}
	
	public File getFileSiteResult() {
		
		return outputFileSite;
	}
	
	public File getTreeSummaryFile() {
		
		return outputFileTree;
	}
	
	public File getFileMatrix00Result() {
		
		return outputFileM00;
	}
	
	public File getFileMatrix01Result() {
		
		return outputFileM01;
	}
	
	public File getFileMatrix10Result() {
		
		return outputFileM10;
	}
	
	public File getFileMatrix11Result() {
		
		return outputFileM11;
	}
	
	public File getFileSumResult() {
		
		return outputFileSum;
	}
	
	public File getFileSCOHResult() {
		
		return outputFileSCOH;
	}
	
	public File getFileDSCOHResult() {
		
		return outputFileDSCOH;
	}
	
	public File getFileSJACResult() {
		
		return outputFileSJAC;
	}
	
	public File getFileDSJACResult() {
		
		return outputFileDSJAC;
	}
	
	public Integer getEarliestYearInOutput() {
		
		return this.minFirstYear;
	}
	
	public Integer getLatestEndYearInOutput() {
		
		return this.maxLastYear;
	}
}
