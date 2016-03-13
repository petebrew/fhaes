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
import java.util.Collections;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;

import org.apache.commons.math.stat.StatUtils;
import org.apache.commons.math.stat.descriptive.DescriptiveStatistics;
import org.fhaes.enums.AnalysisType;
import org.fhaes.enums.EventTypeToProcess;
import org.fhaes.enums.FireFilterType;
import org.fhaes.enums.SampleDepthFilterType;
import org.fhaes.fhfilereader.FHFile;
import org.fhaes.fhfilereader.FHX2FileReader;
import org.fhaes.filefilter.CSVFileFilter;
import org.fhaes.math.Weibull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * FHInterval Class.
 */
public class FHInterval {
	
	private static final Logger log = LoggerFactory.getLogger(FHInterval.class);
	
	private FHFile[] inputFileArray;
	private Integer startYear;
	private Integer endYear;
	private FireFilterType fireFilterType;
	private Double filterValue;
	private Boolean includeIncomplete;
	private AnalysisType analysisType;
	private EventTypeToProcess eventTypeToProcess;
	private SampleDepthFilterType sampleDepthFilterType = SampleDepthFilterType.MIN_NUM_SAMPLES;
	private Double sampleDepthFilterValue;
	
	private File exceedenceFile = null;
	private File summaryFile = null;
	private Double alphaLevel = 0.125;
	
	/**
	 * Construction for setting up an FHInterval analysis. After construction call doAnalysis() to run the analysis and then get results by
	 * calling getExceedence() and getSummary().
	 * 
	 * @param inputFileArray
	 * @param analysisType
	 * @param startYear
	 * @param endYear
	 * @param filterType
	 * @param filterValue
	 * @param includeIncomplete
	 * @param eventTypeToProcess
	 * @param alphaLevel
	 */
	public FHInterval(FHFile[] inputFileArray, AnalysisType analysisType, Integer startYear, Integer endYear, FireFilterType filterType,
			Double filterValue, Boolean includeIncomplete, EventTypeToProcess eventTypeToProcess, Double alphaLevel,
			SampleDepthFilterType sampleDepthFilterType, Double sampleDepthFilterValue) {
	
		if (inputFileArray == null || inputFileArray.length == 0)
		{
			log.error("FHInterval must be passed an input file array");
			return;
		}
		
		this.inputFileArray = inputFileArray;
		
		try
		{
			summaryFile = File.createTempFile("FHInterval", ".tmp");
			exceedenceFile = File.createTempFile("FHInterval", ".tmp");
			summaryFile.deleteOnExit();
			exceedenceFile.deleteOnExit();
			
		}
		catch (IOException e)
		{
			e.printStackTrace();
			return;
		}
		
		this.analysisType = analysisType;
		this.fireFilterType = filterType;
		this.filterValue = filterValue;
		
		this.startYear = startYear;
		this.endYear = endYear;
		this.includeIncomplete = includeIncomplete;
		this.eventTypeToProcess = eventTypeToProcess;
		this.alphaLevel = alphaLevel;
		this.sampleDepthFilterType = sampleDepthFilterType;
		this.sampleDepthFilterValue = sampleDepthFilterValue;
		
		doAnalysis();
	}
	
	/**
	 * Get CSV file containing Exceedence results#.
	 * 
	 * @return
	 */
	public File getExceedence() {
	
		return exceedenceFile;
	}
	
	/**
	 * Get CSV file containing summary of results.
	 * 
	 * @return
	 */
	public File getSummary() {
	
		return summaryFile;
	}
	
	/**
	 * Actually perform the analysis.
	 */
	@SuppressWarnings("deprecation")
	private void doAnalysis() {
	
		log.debug("INPUT PARAMETERS");
		log.debug("inputFileArray = " + inputFileArray);
		log.debug("analyissType = " + analysisType);
		log.debug("startYear = " + startYear);
		log.debug("endYear = " + endYear);
		log.debug("fireFilterType = " + fireFilterType);
		log.debug("filterValue = " + filterValue);
		log.debug("includeIncomplete = " + includeIncomplete);
		log.debug("alphaLevel = " + alphaLevel);
		
		boolean highway = true;
		
		ArrayList<FHX2FileReader> myReader = new ArrayList<FHX2FileReader>();
		Integer minFirstYear = new Integer(9999);
		Integer maxLastYear = new Integer(0);
		
		String savePath = new String();
		savePath = inputFileArray[0].getAbsolutePath();
		
		for (int i = 0; i < inputFileArray.length; i++)
		{
			myReader.add(new FHX2FileReader(inputFileArray[i]));
			
			/*
			 * set the beginning year accounting for the filter
			 */
			if (eventTypeToProcess.equals(EventTypeToProcess.FIRE_EVENT))
			{
				// myReader.get(i).makeClimate2d();
				if (startYear == 0 && highway)
				{
					if (myReader.get(i).getFirstFireYear() < minFirstYear)
					{
						minFirstYear = myReader.get(i).getFirstFireYear();
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
			else if (eventTypeToProcess.equals(EventTypeToProcess.INJURY_EVENT))
			{
				// myReader.get(i).makeClimate2dII();
				if (startYear == 0 && highway)
				{
					if (myReader.get(i).getFirstInjuryYear() < minFirstYear)
					{
						minFirstYear = myReader.get(i).getFirstInjuryYear();
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
			else if (eventTypeToProcess.equals(EventTypeToProcess.FIRE_AND_INJURY_EVENT))
			{
				// myReader.get(i).makeClimate2dII();
				if (startYear == 0 && highway)
				{
					if (myReader.get(i).getFirstIndicatorYear() < minFirstYear)
					{
						minFirstYear = myReader.get(i).getFirstIndicatorYear();
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
			else
			{
				log.error("Unsupported event type caught");
			}
			/*
			 * Set the last year accounting for the filter
			 */
			if (myReader.get(i).getLastYear() > maxLastYear)
			{
				maxLastYear = myReader.get(i).getLastYear();
			}
			if (endYear != 0)
			{
				maxLastYear = endYear;
			}
		} // end of i loop
		
		log.debug("the input filelength is " + inputFileArray.length);
		log.debug("The FIRST FIRE YEAR is " + minFirstYear);
		log.debug("The LAST YEAR is " + maxLastYear);
		log.debug("Minimum and Maximum years are " + minFirstYear + " " + maxLastYear);
		/*
		 * set the format for the output of the numbers to 2 decimal formats
		 */
		DecimalFormat twoPlace = new DecimalFormat("0.00");
		DecimalFormat threePlace = new DecimalFormat("0.000");
		
		/*
		 * Calculate the listYears the common years where the files will be analyzed
		 */
		ArrayList<Integer> listYears = new ArrayList<Integer>();
		for (int i = 0; i < maxLastYear - minFirstYear + 1; i++)
		{
			listYears.add(minFirstYear + i);
		}
		/*
		 * create arraylist need for the Interval Analysis
		 */
		
		ArrayList<ArrayList<Integer>> climateMatrixSite = new ArrayList<ArrayList<Integer>>();
		ArrayList<ArrayList<Double>> filterMatrix = new ArrayList<ArrayList<Double>>();
		ArrayList<Integer> climateVector = new ArrayList<Integer>();
		ArrayList<ArrayList<Double>> climateVectorFilter2 = new ArrayList<ArrayList<Double>>();
		// ArrayList<Double> fireintervalspersite = new ArrayList<Double>();
		ArrayList<Integer> climateVectorActualSite = null;
		ArrayList<Double> filterVectorActual = null;
		ArrayList<Integer> climateYear = new ArrayList<Integer>();
		ArrayList<Integer> minSampleFilter = null;
		ArrayList<Double> percentOfRecordingfilter = null;
		Double[] Dfireintervalspersite;
		double[] dfireintervalspersite;
		String[] statsparam = new String[22];
		if (eventTypeToProcess.equals(EventTypeToProcess.FIRE_EVENT))
		{
			statsparam[0] = "Total intervals";
			statsparam[1] = "Mean fire interval";
			statsparam[2] = "Median fire interval";
			statsparam[3] = "Standard deviation";
			statsparam[4] = "Fire frequency";
			statsparam[5] = "Coefficient of variation";
			statsparam[6] = "Skewness";
			statsparam[7] = "Kurtosis";
			statsparam[8] = "Minimum fire interval";
			statsparam[9] = "Maximum fire interval";
			statsparam[10] = "Weibull scale parameter";
			statsparam[11] = "Weibull shape parameter";
			statsparam[12] = "Weibull mean";
			statsparam[13] = "Weibull median";
			statsparam[14] = "Weibull mode";
			statsparam[15] = "Weibull standard deviation";
			statsparam[16] = "Weibull fire frequency";
			statsparam[17] = "Weibull skewness";
			statsparam[18] = "Lower exceedance interval";
			statsparam[19] = "Upper exceedance interval";
			statsparam[20] = "Significantly short interval upper bound";
			statsparam[21] = "Significantly long interval lower bound";
		}
		else if (eventTypeToProcess.equals(EventTypeToProcess.INJURY_EVENT))
		{
			statsparam[0] = "Total intervals";
			statsparam[1] = "Mean indicator interval";
			statsparam[2] = "Median indicator interval";
			statsparam[3] = "Standard deviation";
			statsparam[4] = "Indicator frequency";
			statsparam[5] = "Coefficient of variation";
			statsparam[6] = "Skewness";
			statsparam[7] = "Kurtosis";
			statsparam[8] = "Minimum fire interval";
			statsparam[9] = "Maximum indicator interval";
			statsparam[10] = "Weibull scale parameter";
			statsparam[11] = "Weibull shape parameter";
			statsparam[12] = "Weibull mean";
			statsparam[13] = "Weibull median";
			statsparam[14] = "Weibull mode";
			statsparam[15] = "Weibull standard deviation";
			statsparam[16] = "Weibull indicator frequency";
			statsparam[17] = "Weibull skewness";
			statsparam[18] = "Lower exceedance interval";
			statsparam[19] = "Upper exceedance interval";
			statsparam[20] = "Significantly short interval upper bound";
			statsparam[21] = "Significantly long interval lower bound";
		}
		else if (eventTypeToProcess.equals(EventTypeToProcess.FIRE_AND_INJURY_EVENT))
		{
			statsparam[0] = "Total intervals";
			statsparam[1] = "Mean fire and other indicator interval";
			statsparam[2] = "Median fire and other indicator interval";
			statsparam[3] = "Standard deviation";
			statsparam[4] = "Fire and other indicator frequency";
			statsparam[5] = "Coefficient of variation";
			statsparam[6] = "Skewness";
			statsparam[7] = "Kurtosis";
			statsparam[8] = "Minimum fire and other indicator interval";
			statsparam[9] = "Maximum fire interval";
			statsparam[10] = "Weibull scale parameter";
			statsparam[11] = "Weibull shape parameter";
			statsparam[12] = "Weibull mean";
			statsparam[13] = "Weibull median";
			statsparam[14] = "Weibull mode";
			statsparam[15] = "Weibull standard deviation";
			statsparam[16] = "Weibull indicator frequency";
			statsparam[17] = "Weibull skewness";
			statsparam[18] = "Lower exceedance interval";
			statsparam[19] = "Upper exceedance interval";
			statsparam[20] = "Significantly short interval upper bound";
			statsparam[21] = "Significantly long interval lower bound";
		}
		else
		{
			
			log.error("Unsupported event type caught");
		}
		
		double[] fixvalt = { 0.999, 0.99, 0.975, 0.95, 0.9, 0.875, 0.8, 0.75, 0.7, 0.667, 0.5, 0.333, 0.3, 0.25, 0.2, 0.125, 0.1, 0.05,
				0.025, 0.01, 0.001 };
		
		double[][] ExceeProbcomp = new double[fixvalt.length][myReader.size()];
		double[][] ExceeProbsample = new double[fixvalt.length][myReader.size()];
		// log.debug("the size of statsparam is " +
		// statsparam.length);
		double[][] summaryComp = new double[statsparam.length][myReader.size()];
		double[] numberOfintervalscomp = new double[myReader.size()];
		// ArrayList<ArrayList<Integer>>();
		// ArrayList<ArrayList<Integer>> FIyearperSample = new
		// ArrayList<ArrayList<Integer>>();
		ArrayList<Integer> FyearperSampletemp;
		ArrayList<Integer> FIyearperSampletemp;
		// ArrayList<Double> fireintervalspersample = new
		double[] numberOfintervalssamp = new double[myReader.size()];
		double[][] summarySample = new double[statsparam.length][myReader.size()];
		Double[] Dfireintervalspersample;
		double[] dfireintervalspersample;
		/*
		 * Set up either of the two filters two create the binary matrix on the case of composite analysis there are two possible filters:
		 * Number of fires and percentage of scarred trees.
		 */
		Integer firesFilter1 = new Integer(0);
		Double firesFilter2 = new Double(0);
		if (fireFilterType.equals(FireFilterType.NUMBER_OF_EVENTS))
		{
			if (filterValue != 1)
				firesFilter1 = filterValue.intValue();
			// log.debug("number of fires is selected is: "+
			// firesFilter1);
		}
		else if (fireFilterType.equals(FireFilterType.PERCENTAGE_OF_ALL_TREES))
		{
			if (filterValue != 1)
				firesFilter2 = filterValue / 100.0;
			// log.debug("percentage of fires is selected is: "+
			// firesFilter2);
		}
		else if (fireFilterType.equals(FireFilterType.PERCENTAGE_OF_RECORDING))
		{
			if (filterValue != 1)
				firesFilter2 = filterValue / 100.0;
			// TODO ELENA TO CHECK
		}
		else
		{
			log.error("Unknown FireFilterType");
			return;
		}
		
		boolean[] enoughIntComp = new boolean[myReader.size()];
		boolean[] enoughIntSamp = new boolean[myReader.size()];
		// NEW FOR ELENA
		log.debug("Sample depth filter type = " + sampleDepthFilterType);
		log.debug("Sample depth value = " + sampleDepthFilterValue);
		
		// if (sampleDepthFilterType.equals(SampleDepthFilterType.MIN_NUM_SAMPLES))
		// {
		// // TODO ELENA
		// }
		// else if (sampleDepthFilterType.equals(SampleDepthFilterType.MIN_NUM_RECORDER_SAMPLES))
		// {
		// // TODO ELENA
		// }
		/*
		 * start processing each file individually: The analysis can be done by either tree (by sample/non-binary) or by site
		 * (composite/binary). by tree the box selected is: jCheckTree. by site the box selected is:
		 */
		for (int i = 0; i < myReader.size(); i++)
		{
			log.debug("  Starting to Process file : " + myReader.get(i).getName());
			
			/*
			 * get the vector Year containing the vector of year of a given fhx file load it into the array list climateYear.
			 */
			
			climateYear = myReader.get(i).getYearArray();
			
			// new stuff
			// Create filter based on min number of samples/recorder samples
			int[] depths = null;
			if (sampleDepthFilterType.equals(SampleDepthFilterType.MIN_NUM_SAMPLES))
			{
				depths = myReader.get(i).getSampleDepths();
				log.debug("MIN_NUM_SAMPLES ");
			}
			else if (sampleDepthFilterType.equals(SampleDepthFilterType.MIN_NUM_RECORDER_SAMPLES))
			{
				depths = myReader.get(i).getRecordingDepths(eventTypeToProcess);
				log.debug(" MIN_NUM_RECORDER_SAMPLES");
			}
			else
			{
				log.error("Unknown sample depth filter type.");
				return;
			}
			minSampleFilter = new ArrayList<Integer>();
			for (int ij = 0; ij < listYears.size(); ij++)
			{
				if (climateYear.indexOf(listYears.get(ij)) == -1)
				{
					minSampleFilter.add(-1);
				}
				else
				{
					// log.debug("the sample depth is "
					// + myReader.get(i).getSampleDepths()[climateYear.indexOf(listYearsComp.get(ij))]);
					minSampleFilter.add(new Integer(depths[climateYear.indexOf(listYears.get(ij))]));
				}
				// log.debug(" " + minSampleFilter.get(ij));
			}
			
			// end new stuff
			
			/*
			 * get filter matrix for each file.
			 * 
			 * filters2d matrix composed of the 3 filters number of fires (total capital letter per row) total number of tree (total lower
			 * case letter plus bars counting only after a fire) percent of scared trees total fires/total trees
			 */
			
			// climateVectorFilter2 = myReader.get(i).getFilterArrays(eventTypeToProcess);
			/*
			 * More new stuff
			 */
			if (filterValue != 1)
			{
				/*
				 * get both matrices:
				 * 
				 * 2. filters2d matrix composed of the 3 filters number of fires (total capital letter per row) total number of tree (total
				 * lower case letter plus bars counting only after a fire) percent of scared trees total fires/total trees
				 */
				
				climateVectorFilter2 = myReader.get(i).getFilterArrays(eventTypeToProcess);
				
				/*
				 * if by tree analysis is selected create two matrices (array list) 1. filterMatrix containing the three filter vectors only
				 * in between common years (so using the listYearComp array list subset of the years vector) 2. climateMatrix 2 dimensional
				 * array list containing binary matrices restricted to the listYear list.
				 */
				if (fireFilterType.equals(FireFilterType.PERCENTAGE_OF_RECORDING))
				{
					percentOfRecordingfilter = new ArrayList<Double>();
					
					for (int ij = 0; ij < listYears.size(); ij++)
					{
						
						if (climateYear.indexOf(listYears.get(ij)) == -1)
						{
							percentOfRecordingfilter.add(-1.0);
						}
						else
						{
							if (myReader.get(i).getRecordingDepths(eventTypeToProcess)[climateYear.indexOf(listYears.get(ij))] != 0)
							{
								percentOfRecordingfilter.add(new Double(climateVectorFilter2.get(0).get(
										climateYear.indexOf(listYears.get(ij)))
										/ myReader.get(i).getRecordingDepths(eventTypeToProcess)[climateYear.indexOf(listYears.get(ij))]));
							}
							else
							{
								percentOfRecordingfilter.add(-99.0);
							}
						}
						log.debug("PERCENTAGE_OF_RECORDING is: " + percentOfRecordingfilter.get(ij));
					}
				}
				else
				{
					for (int ik = 0; ik < 3; ik++)
					{
						log.debug("filter number is: " + ik);
						filterVectorActual = new ArrayList<Double>();
						for (int ij = 0; ij < listYears.size(); ij++)
						{ // log.debug(" climateYear.indexOf(listYearsComp.get(j))" +
							// climateYear.indexOf(listYearsComp.get(ij)));
							// if(ik==0){log.debug("number of fires
							// "+climateVectorFilter2.get(0).get(climateYear.indexOf(listYears.get(ij)))+" year
							// "+listYearsComp.get(ij));}
							if (climateYear.indexOf(listYears.get(ij)) == -1)
							{
								filterVectorActual.add(-1.0);
							}
							else
							{
								filterVectorActual
										.add(new Double(climateVectorFilter2.get(ik).get(climateYear.indexOf(listYears.get(ij)))));
							}
							if (ik == 2)
							{
								log.debug("filteperc  " + filterVectorActual.get(ij));
							}
						}
						// log.debug("size of filterVectorActual is : "+filterVectorActual.size());
						filterMatrix.add(filterVectorActual);
						// if(ik==0){log.debug("filters is: "+filter);
					}
				} // end of if-else percentageofrecording
					// log.debug("size of the FilterMatrix is" + filterMatrix.size());
				
			} // end of if filters not equal to 1
			/*
			 * end of more new stuff
			 */
			/*
			 * 
			 * 1. Create the filterMatrix containing the tree filter vectors only in between common years (so using the listYearComp array
			 * list subset of the years vector)
			 */
			// for (int ik = 0; ik < 3; ik++)
			// {
			// filterVectorActual = new ArrayList<Double>();
			// for (int ij = 0; ij < listYears.size(); ij++)
			// {
			// if (climateYear.indexOf(listYears.get(ij)) == -1)
			// {
			// filterVectorActual.add(-1.0);
			// }
			// else
			// {
			// filterVectorActual.add(new Double(climateVectorFilter2.get(ik).get(climateYear.indexOf(listYears.get(ij)))));
			// }
			
			// }
			/*
			 * ArrayList filterMatrix containes the filter matrix for each of the files
			 */
			// filterMatrix.add(filterVectorActual);
			// }//end of creating the filter matrix.
			
			/*
			 * get matrix climate binary matrix by site (binary analysis) if Composite is selected.
			 */
			// if ((doComposite)&&(!jTextOfFires.getText().equals("1"))){
			if (analysisType.equals(AnalysisType.COMPOSITE))
			{
				log.debug("inside the comp");
				// System.out.println("inside the comp " + " working on file "+ myReader.get(i).getName() );
				
				if (eventTypeToProcess.equals(EventTypeToProcess.FIRE_EVENT))
				{
					climateVector = myReader.get(i).getFireEventsArray();
				}
				else if (eventTypeToProcess.equals(EventTypeToProcess.INJURY_EVENT))
				{
					climateVector = myReader.get(i).getOtherInjuriesArray();
				}
				else if (eventTypeToProcess.equals(EventTypeToProcess.FIRE_AND_INJURY_EVENT))
				{
					climateVector = myReader.get(i).getFiresAndInjuriesArray();
				}
				else
				{
					
					log.error("Unsupported event type caught");
				}
				
				climateVectorActualSite = new ArrayList<Integer>();
				
				for (int j = 0; j < listYears.size(); j++)
				{
					
					if (climateYear.indexOf(listYears.get(j)) == -1)
					{
						climateVectorActualSite.add(-1);
					}
					else
					{
						if (minSampleFilter.get(j).intValue() >= sampleDepthFilterValue.intValue())
						{
							if (filterValue != 1)
							{
								if (fireFilterType.equals(FireFilterType.NUMBER_OF_EVENTS))
								{
									// log.debug("fire filter: "+firesFilter1+" year is: "+listYears.get(j)
									// +" fires: "+filterMatrix.get(3*i).get(j)+" climatevector:
									// "+climateVector.get(climateYear.indexOf(listYears.get(j))));
									if ((filterMatrix.get(3 * i).get(j) < firesFilter1)
											&& (climateVector.get(climateYear.indexOf(listYears.get(j)))) != -1.0)
									{
										climateVectorActualSite.add(0);
									}
									else
									{
										climateVectorActualSite.add(climateVector.get(climateYear.indexOf(listYears.get(j))));
									}
								}
								else if (fireFilterType.equals(FireFilterType.PERCENTAGE_OF_ALL_TREES))
								{
									// log.debug("percent of fires is selected is: "+
									// firesFilter2+" "+climateVector.get(climateYear.indexOf(listYearsComp.get(j))));
									// log.debug("the filter percent of fires is"+filterMatrix.get((3*i+2)).get(j));
									if ((filterMatrix.get(3 * i + 2).get(j) == -99))
									{
										climateVectorActualSite.add(-1);
									}
									else
									{
										if ((filterMatrix.get(3 * i + 2).get(j) < firesFilter2)
												&& ((climateVector.get(climateYear.indexOf(listYears.get(j)))) != -1.0))
										{
											climateVectorActualSite.add(0);
										}
										else
										{
											climateVectorActualSite.add(climateVector.get(climateYear.indexOf(listYears.get(j))));
										}
									}
								}
								else if (fireFilterType.equals(FireFilterType.PERCENTAGE_OF_RECORDING))
								{
									
									// TODO
									// ELENA TO IMPLEMENT
									if (percentOfRecordingfilter.get(j) == -99)
									{
										climateVectorActualSite.add(-1);
									}
									else
									{
										if ((percentOfRecordingfilter.get(j) < firesFilter2)
												&& ((climateVector.get(climateYear.indexOf(listYears.get(j)))) != -1.0))
										{
											climateVectorActualSite.add(0);
										}
										else
										{
											climateVectorActualSite.add(climateVector.get(climateYear.indexOf(listYears.get(j))));
										}
									}
									
								}
								else
								{
									log.error("Unknown FireFilterType");
									return;
								}
								
							} // end of if filter not equal to 1
							else
							{
								climateVectorActualSite.add(climateVector.get(climateYear.indexOf(listYears.get(j))));
							} // end of else of if filter not equal to 1
						} // end of if the filter minsampedepth
						else
						{
							// log.debug("j is " + j + "minSampleFilter is " + minSampleFilter.get(j));
							climateVectorActualSite.add(-1);
							
						}
					} // end else 645 to 721
				} // end of j loop listyears (420-459)
				/*
				 * climateMatrixSite has the composite information taking in consideration both the filters and common years
				 */
				climateMatrixSite.add(climateVectorActualSite);
				
				/*
				 * Must get the years with Fires from the climateMatrixSite which has been filter already
				 */
				ArrayList<Double> fireintervalspersite = new ArrayList<Double>();
				ArrayList<Integer> yearsWithFires = new ArrayList<Integer>();
				for (int ij = 0; ij < listYears.size(); ij++)
				{
					if (climateMatrixSite.get(i).get(ij) == 1)
					{
						yearsWithFires.add(listYears.get(ij));
						// log.debug("year with fires "
						// +listYears.get(ij));
					}
				}
				
				/*
				 * check that the number of years with fires is bigger of equal than 3 if so make the fire intervals else the test can not
				 * be run and report NA
				 */
				// new swich
				if (yearsWithFires.size() != 0)
				{
					if (includeIncomplete)
					{
						numberOfintervalscomp[i] = yearsWithFires.size();
					}
					else
					{
						numberOfintervalscomp[i] = yearsWithFires.size() - 1;
					}
				}
				// end of new switch
				if (numberOfintervalscomp[i] >= 3)
				{
					enoughIntComp[i] = true;
					ArrayList<Integer> fireIntervals = generateFireIntervals(yearsWithFires);
					for (int ij = 0; ij < fireIntervals.size(); ij++)
					{
						// log.debug("fire intervals are: "+
						// test1.getFireIntervals().get(ij));
						fireintervalspersite.add(fireIntervals.get(ij) * 1.0);
					}
					log.debug("FireintervalsPerSite =" + fireintervalspersite);
					
					/*
					 * Add extra interval if include incomplete is selected. the interval goes from the last fire scar year to the last year
					 * of the fire history.
					 */
					if (includeIncomplete)
					{
						double includeinterval = maxLastYear - yearsWithFires.get(yearsWithFires.size() - 1);
						fireintervalspersite.add(includeinterval);
						System.out.println("the last year is " + maxLastYear + "the last year with fire is "
								+ yearsWithFires.get(yearsWithFires.size() - 1));
						log.debug("the included interval is " + includeinterval);
					}
					
					/*
					 * Get the normal statistics for the fire intervals add the values to the stats and then call them for the stats
					 */
					DescriptiveStatistics stats = new DescriptiveStatistics();
					Dfireintervalspersite = new Double[fireintervalspersite.size()];
					Dfireintervalspersite = fireintervalspersite.toArray(Dfireintervalspersite);
					dfireintervalspersite = new double[fireintervalspersite.size()];
					// summaryComp = new
					// double[statsparam.length][myReader.size()];
					for (int ik = 0; ik < fireintervalspersite.size(); ik++)
					{
						stats.addValue(Dfireintervalspersite[ik].doubleValue());
						dfireintervalspersite[ik] = Dfireintervalspersite[ik].doubleValue();
					}
					/*
					 * load the Summary Analysis for the Composite fire intervals
					 */
					summaryComp[0][i] = fireintervalspersite.size();
					// double mean = stats.getMean();
					summaryComp[1][i] = stats.getMean();
					// double median =
					// StatUtils.percentile(dfireintervalspersite, 50);
					summaryComp[2][i] = StatUtils.percentile(dfireintervalspersite, 50);
					// double std = stats.getStandardDeviation();
					summaryComp[3][i] = stats.getStandardDeviation();
					// double skew = stats.getSkewness();
					summaryComp[4][i] = 1.0 / summaryComp[1][i];
					summaryComp[5][i] = summaryComp[3][i] / summaryComp[1][i];
					summaryComp[6][i] = stats.getSkewness();
					// double kurt = stats.getKurtosis();
					if (numberOfintervalscomp[i] == 3)
					{
						summaryComp[7][i] = -99;
					}
					else
					{
						summaryComp[7][i] = stats.getKurtosis();
					}
					// log.debug("nomean \t\t nostd \t\t nokurt \t noskew \t\t nomedian");
					// log.debug(twoPlace.format(mean)+"\t\t"+twoPlace.format(std)+"\t\t"+twoPlace.format(kurt)+"\t\t"+twoPlace.format(skew)+"\t\t"+twoPlace.format(median));
					
					Weibull weibull = new Weibull(fireintervalspersite);
					
					ArrayList<Double> weibullProb = weibull.getWeibullProbability(fireintervalspersite);
					ArrayList<Double> siglonglowbound = new ArrayList<Double>();
					ArrayList<Double> sigshortupbound = new ArrayList<Double>();
					log.debug("the weibull probability of first element is " + weibullProb.get(0));
					log.debug("the index  the size of the interval is " + weibullProb.indexOf(weibullProb.get(0)));
					for (int ij = 0; ij < weibullProb.size() - 1; ij++)
					{
						if (weibullProb.get(ij) <= alphaLevel)
						{
							siglonglowbound.add(fireintervalspersite.get(ij));
							
						}
						if (weibullProb.get(ij) >= (1 - alphaLevel))
						{
							sigshortupbound.add(fireintervalspersite.get(ij));
							
						}
						
					}
					
					summaryComp[10][i] = weibull.getScale();
					summaryComp[11][i] = weibull.getShape();
					summaryComp[12][i] = weibull.getMean();
					summaryComp[13][i] = weibull.getMedian();
					summaryComp[14][i] = weibull.getMode();
					summaryComp[15][i] = weibull.getSigma();
					summaryComp[16][i] = 1.0 / summaryComp[13][i];
					summaryComp[17][i] = weibull.getSkew();
					summaryComp[18][i] = weibull.getExceedenceProbability2()[0];
					summaryComp[19][i] = weibull.getExceedenceProbability2()[1];
					Collections.sort(sigshortupbound);
					log.debug("siglonglowbound is " + siglonglowbound);
					try
					{
						summaryComp[20][i] = sigshortupbound.get(sigshortupbound.size() - 1);
					}
					catch (Exception e)
					{
						summaryComp[20][i] = Double.NaN;
					}
					Collections.sort(siglonglowbound);
					
					try
					{
						summaryComp[21][i] = siglonglowbound.get(0);
					}
					catch (Exception e)
					{
						summaryComp[21][i] = Double.NaN;
					}
					log.debug("sigshortupbound is " + sigshortupbound);
					Collections.sort(fireintervalspersite);
					summaryComp[8][i] = fireintervalspersite.get(0);
					summaryComp[9][i] = fireintervalspersite.get(fireintervalspersite.size() - 1);
					// log.debug("shape \t\t scale \t\t median ");
					// log.debug(twoPlace.format(test1.Weibull_Parameters(fireintervalspersite)[0])+"\t\t"+twoPlace.format(test1.Weibull_Parameters(fireintervalspersite)[1])+"\t\t"+twoPlace.format(test1.weibull_median(test1.Weibull_Parameters(fireintervalspersite))));
					// log.debug("mean \t\t sigma \t\t mode \t\t skewness");
					// log.debug(twoPlace.format(test1.weibull_mean(test1.Weibull_Parameters(fireintervalspersite)))+"\t\t"+twoPlace.format(test1.weibull_sigma(test1.Weibull_Parameters(fireintervalspersite)))+"\t\t"+twoPlace.format(test1.weibull_mode(test1.Weibull_Parameters(fireintervalspersite)))+"\t\t"+twoPlace.format(test1.weibull_skew(test1.Weibull_Parameters(fireintervalspersite))));
					// log.debug("maxhazard \t\t lei \t\t uei ");
					// log.debug(twoPlace.format(test1.maxhazard_int(test1.Weibull_Parameters(fireintervalspersite)))+"\t\t"+twoPlace.format(test1.weibull_lowuppexcint(test1.Weibull_Parameters(fireintervalspersite))[0])+"\t\t"+twoPlace.format(test1.weibull_lowuppexcint(test1.Weibull_Parameters(fireintervalspersite))[1]));
					// log.debug("the size of YearWith Fires is "+YearsWithFires.size());
					System.out.println("the size of the prb exdc is " + weibull.getExceedenceProbability().length);
					for (int kk = 0; kk < weibull.getExceedenceProbability().length; kk++)
					{
						ExceeProbcomp[kk][i] = weibull.getExceedenceProbability()[kk];
						// log.debug("file "+i+"Exce probability "+
						// ExceeProbcomp[kk][i]);
					}
				} // end of if enoughIntComp
				else
				{
					enoughIntComp[i] = false;
				}
			} // end the if composite is selected
			/*
			 * starting the process for the sample mode.
			 */
			if (analysisType.equals(AnalysisType.SAMPLE))
			{
				log.debug("I am in sample ");
				
				ArrayList<Double> fireintervalspersample = new ArrayList<Double>();
				FIyearperSampletemp = new ArrayList<Integer>();
				// FyearperSampletemp = new ArrayList<Integer>();
				for (int k = 0; k < myReader.get(i).getNumberOfSeries(); k++)
				{
					
					log.debug("Parsing file index " + i + ", series number " + k);
					FyearperSampletemp = new ArrayList<Integer>();
					// log.debug("the size of the years of the file is:"+
					// myReader.get(i).getYear().size());
					// log.debug("years with fires in sample "+k +
					// "years are ");
					// for (int j = 0; j < myReader.get(i).getYearArray().size(); j++)
					for (int j = 0; j < listYears.size(); j++)
					{
						// log.debug("the size climate2d is "+myReader.get(i).getClimate2d().get(k).get(j));
						if (eventTypeToProcess.equals(EventTypeToProcess.FIRE_EVENT))
						{
							if (climateYear.indexOf(listYears.get(j)) != -1)
							{
								if (myReader.get(i).getClimate2d().get(k).get(climateYear.indexOf(listYears.get(j))) == 1)
								{
									// FyearperSampletemp.add((j + myReader.get(i).getFirstYear()));
									FyearperSampletemp.add(listYears.get(j));
								}
							}
							
						}
						// {
						// if ((myReader.get(i).getClimate2d().get(k).get(j) == 1))
						// {
						// / log.debug("I here inside ==1 "+
						// / j+" "+myReader.get(i).getFirstYear());
						// / int temp=j+myReader.get(i).getFirstYear();
						// / log.debug((j+myReader.get(i).getFirstYear()));
						// /// FyearperSampletemp.add((j + myReader.get(i).getFirstYear()));
						// }
						// }
						else if (eventTypeToProcess.equals(EventTypeToProcess.INJURY_EVENT))
						{
							if (climateYear.indexOf(listYears.get(j)) != -1)
							{
								if (myReader.get(i).getClimate2dII().get(k).get(climateYear.indexOf(listYears.get(j))) == 1)
								{
									FyearperSampletemp.add(listYears.get(j));
								}
							}
							// if ((myReader.get(i).getClimate2dII().get(k).get(j) == 1))
							// {
							// FyearperSampletemp.add((j + myReader.get(i).getFirstYear()));
							// }
						}
						else if (eventTypeToProcess.equals(EventTypeToProcess.FIRE_AND_INJURY_EVENT))
						{
							if (climateYear.indexOf(listYears.get(j)) != -1)
							{
								if (myReader.get(i).getClimate2dIII().get(k).get(climateYear.indexOf(listYears.get(j))) == 1)
								{
									FyearperSampletemp.add(listYears.get(j));
								}
							}
							// if ((myReader.get(i).getClimate2dIII().get(k).get(j) == 1))
							// {
							// FyearperSampletemp.add((j + myReader.get(i).getFirstYear()));
							// }
						}
						else
						{
							
							log.error("Unsupported event type caught");
						}
						
					} // / end of the loop for listYears in common (finish loading the fire year per sample
					log.debug("FyearperSampletemp.size() is first" + FyearperSampletemp.size());
					if (FyearperSampletemp.size() != 0)
					{
						if (includeIncomplete)
						{
							numberOfintervalssamp[i] = numberOfintervalssamp[i] + FyearperSampletemp.size();
						}
						else
						{
							numberOfintervalssamp[i] = numberOfintervalssamp[i] + (FyearperSampletemp.size() - 1);
						}
					}
					// new
					if ((FyearperSampletemp.size() == 1) && (includeIncomplete))
					{
						if ((myReader.get(i).getLastYearIndexPerSample()[k] + myReader.get(i).getFirstYear()) != FyearperSampletemp
								.get(FyearperSampletemp.size() - 1))
						{
							FIyearperSampletemp.add((myReader.get(i).getFirstYear() + myReader.get(i).getLastYearIndexPerSample()[k])
									- FyearperSampletemp.get(FyearperSampletemp.size() - 1));
						}
					} // end of if one fire year and includelastyear so we have at least one interval in a given series.
						// endofnew
					if ((FyearperSampletemp.size() >= 2))
					{
						log.debug("FyearperSampletemp.size() is " + FyearperSampletemp.size());
						for (int jk = 0; jk < FyearperSampletemp.size() - 1; jk++)
						{
							// FIyearperSampletemp.add(FyearperSample.get(k).get(jk+1)
							// - FyearperSample.get(k).get(jk));
							log.debug("FyearperSampletemp is " + FyearperSampletemp.get(jk));
							if ((FyearperSampletemp.get(jk + 1) - FyearperSampletemp.get(jk)) > 0)
							{
								FIyearperSampletemp.add(FyearperSampletemp.get(jk + 1) - FyearperSampletemp.get(jk));
							}
							// FIyearperSampletemp.add(FyearperSampletemp.get(jk+1)
							// - FyearperSampletemp.get(jk));
							log.debug("fire intervals for sample " + k + " is " + FIyearperSampletemp.get(jk));
							// fisumtemp= fisumtemp +
							// FIyearperSampletemp.get(jk).intValue();
						}
						if (includeIncomplete)
						{
							if (maxLastYear != FyearperSampletemp.get(FyearperSampletemp.size() - 1))
							// if ((myReader.get(i).getLastYearIndexPerSample()[k] + myReader.get(i).getFirstYear()) != FyearperSampletemp
							// .get(FyearperSampletemp.size() - 1))
							{
								// log.debug("the sample number is "+k+
								// " the size of the fyearpersampletemp is "+
								// FyearperSampletemp.size() );
								log.debug("the last year per sample is "
										+ (myReader.get(i).getLastYearIndexPerSample()[k] + myReader.get(i).getFirstYear()));
								log.debug(" the last fire year per sample " + FyearperSampletemp.get(FyearperSampletemp.size() - 1));
								FIyearperSampletemp.add((maxLastYear) - FyearperSampletemp.get(FyearperSampletemp.size() - 1));
								log.debug("the last intrval in included is on is   "
										+ FIyearperSampletemp.get(FIyearperSampletemp.size() - 1));
							}
						}
					} // end of if at least 2 fier years so we have at least one interval in a given series.
						// log.debug("size of FIyearperSample "+
						// FIyearperSampletemp.size()+
						// " X "+FIyearperSampletemp.get(0).size());
				} // end of the loop for number of series.
					// log.debug("size of FIyearperSample "+
					// FIyearperSampletemp.size());
				for (int j = 0; j < FIyearperSampletemp.size(); j++)
				{
					fireintervalspersample.add(FIyearperSampletemp.get(j) * 1.0);
				}
				/*
				 * Get the normal statistics for the fire intervals add the values to the stats and then call them for the stats
				 */
				if (fireintervalspersample.size() >= 3)
				{
					enoughIntSamp[i] = true;
					DescriptiveStatistics stasample = new DescriptiveStatistics();
					Dfireintervalspersample = new Double[fireintervalspersample.size()];
					Dfireintervalspersample = fireintervalspersample.toArray(Dfireintervalspersample);
					dfireintervalspersample = new double[fireintervalspersample.size()];
					// summarySample = new
					// double[statsparam.length][myReader.size()];
					for (int ik = 0; ik < fireintervalspersample.size(); ik++)
					{
						stasample.addValue(Dfireintervalspersample[ik].doubleValue());
						dfireintervalspersample[ik] = Dfireintervalspersample[ik].doubleValue();
						log.debug("the " + ik + " fire interval is " + dfireintervalspersample[ik]);
					}
					log.debug("the size for dfireintervalspersample is " + dfireintervalspersample.length);
					
					// ADDED BY PETE
					if (dfireintervalspersample.length == 0)
						continue;
					
					/*
					 * load the Summary Analysis for the Sample fire intervals
					 */
					summarySample[0][i] = fireintervalspersample.size();
					// double mean = stats.getMean();
					summarySample[1][i] = stasample.getMean();
					log.debug("mean sample is " + stasample.getMean());
					// double median =
					// StatUtils.percentile(dfireintervalspersite, 50);
					summarySample[2][i] = StatUtils.percentile(dfireintervalspersample, 50);
					log.debug("summarySample[2][] " + i + " " + summarySample[2][i]);
					// double std = stats.getStandardDeviation();
					summarySample[3][i] = stasample.getStandardDeviation();
					log.debug("summarySample[3][] " + i + " " + summarySample[3][i]);
					// double skew = stats.getSkewness();
					summarySample[4][i] = 1.0 / summarySample[1][i];
					log.debug("summarySample[4][] " + i + " " + summarySample[4][i]);
					summarySample[5][i] = summarySample[3][i] / summarySample[1][i];
					log.debug("summarySample[5][] " + i + " " + summarySample[5][i]);
					summarySample[6][i] = stasample.getSkewness();
					log.debug("summarySample[6][] " + i + " " + summarySample[6][i]);
					// double kurt = stats.getKurtosis();
					if (numberOfintervalssamp[i] == 3)
					{
						summarySample[7][i] = -99;
					}
					else
					{
						summarySample[7][i] = stasample.getKurtosis();
					}
					// summarySample[7][i] = stasample.getKurtosis();
					log.debug("summarySample[7][] " + i + " " + summarySample[7][i]);
					// log.debug("nomean \t\t nostd \t\t nokurt \t noskew \t\t nomedian");
					// log.debug(twoPlace.format(mean)+"\t\t"+twoPlace.format(std)+"\t\t"+twoPlace.format(kurt)+"\t\t"+twoPlace.format(skew)+"\t\t"+twoPlace.format(median));
					
					Weibull weibull = new Weibull(fireintervalspersample);
					
					//
					ArrayList<Double> weibullProb = weibull.getWeibullProbability(fireintervalspersample);
					ArrayList<Double> siglonglowbound = new ArrayList<Double>();
					ArrayList<Double> sigshortupbound = new ArrayList<Double>();
					log.debug("the weibull probability of first element is " + weibullProb.get(0));
					log.debug("the index  the size of the interval is " + weibullProb.indexOf(weibullProb.get(0)));
					for (int ij = 0; ij < weibullProb.size() - 1; ij++)
					{
						if (weibullProb.get(ij) <= alphaLevel)
						{
							siglonglowbound.add(fireintervalspersample.get(ij));
							
						}
						if (weibullProb.get(ij) >= (1 - alphaLevel))
						{
							sigshortupbound.add(fireintervalspersample.get(ij));
							
						}
						
					}
					
					//
					
					summarySample[10][i] = weibull.getScale();
					log.debug("summarySample[10][] " + i + " " + summarySample[10][i]);
					summarySample[11][i] = weibull.getShape();
					log.debug("summarySample[11][] " + i + " " + summarySample[11][i]);
					summarySample[12][i] = weibull.getMean();
					summarySample[13][i] = weibull.getMedian();
					summarySample[14][i] = weibull.getMode();
					summarySample[15][i] = weibull.getSigma();
					summarySample[16][i] = 1.0 / summarySample[13][i];
					summarySample[17][i] = weibull.getSkew();
					summarySample[18][i] = weibull.getExceedenceProbability2()[0];
					summarySample[19][i] = weibull.getExceedenceProbability2()[1];
					Collections.sort(sigshortupbound);
					log.debug("siglonglowbound is " + siglonglowbound);
					try
					{
						summarySample[20][i] = sigshortupbound.get(sigshortupbound.size() - 1);
					}
					catch (Exception e)
					{
						summarySample[20][i] = Double.NaN;
					}
					Collections.sort(siglonglowbound);
					
					try
					{
						summarySample[21][i] = siglonglowbound.get(0);
					}
					catch (Exception e)
					{
						summarySample[21][i] = Double.NaN;
					}
					log.debug("sigshortupbound is " + sigshortupbound);
					
					Collections.sort(fireintervalspersample);
					
					try
					{
						summarySample[8][i] = fireintervalspersample.get(0);
					}
					catch (Exception ex)
					{
						log.error("Index out of bounds exception caught: ");
						log.error("    summarySample[8][i] = fireintervalspersample.get(0)");
						ex.printStackTrace();
					}
					summarySample[9][i] = fireintervalspersample.get(fireintervalspersample.size() - 1);
					// log.debug("shape \t\t scale \t\t median ");
					// log.debug(twoPlace.format(test2.Weibull_Parameters(fireintervalspersample)[0])+"\t\t"+twoPlace.format(test2.Weibull_Parameters(fireintervalspersample)[1])+"\t\t"+twoPlace.format(test2.weibull_median(test1.Weibull_Parameters(fireintervalspersample))));
					// log.debug("mean \t\t sigma \t\t mode \t\t skewness");
					// log.debug(twoPlace.format(test1.weibull_mean(test2.Weibull_Parameters(fireintervalspersample)))+"\t\t"+twoPlace.format(test1.weibull_sigma(test2.Weibull_Parameters(fireintervalspersample)))+"\t\t"+twoPlace.format(test2.weibull_mode(test1.Weibull_Parameters(fireintervalspersample)))+"\t\t"+twoPlace.format(test1.weibull_skew(test2.Weibull_Parameters(fireintervalspersample))));
					// log.debug("maxhazard \t\t lei \t\t uei ");
					// log.debug(twoPlace.format(test2.maxhazard_int(test2.Weibull_Parameters(fireintervalspersample)))+"\t\t"+twoPlace.format(test2.weibull_lowuppexcint(test2.Weibull_Parameters(fireintervalspersample))[0])+"\t\t"+twoPlace.format(test2.weibull_lowuppexcint(test2.Weibull_Parameters(fireintervalspersample))[1]));
					// log.debug("the size of YearWith Fires is "+YearsWithFires.size());
					// log.debug("the size of the prb exdc is
					// "+test2.weibull_Exprob(test2.Weibull_Parameters(fireintervalspersample)).length);
					System.out.println("the size of the prb exdc sample  is " + weibull.getExceedenceProbability().length);
					for (int kk = 0; kk < weibull.getExceedenceProbability().length; kk++)
					{
						ExceeProbsample[kk][i] = weibull.getExceedenceProbability()[kk];
						log.debug("file " + i + " Exce probability " + ExceeProbsample[kk][i]);
						// log.debug("the size is "+ExceeProbsample.length);
					}
				} // end of if at least 4 fireintervals
				else
				{
					enoughIntSamp[i] = false;
				}
			} // end of if jRadioSample selected.
				// log.debug("the size of exceeprobsample is "ExceeProbsample.length+" X "+ExceeProbsample[0].length);
		} // end of i readering each file loop do loop (354-1185)
		/*
		 * 
		 */
		// log.debug("size of the climateMatrixSite is "+climateMatrixSite.size()+" X "+climateMatrixSite.get(0).size());
		// for (int j = 0; j < listYears.size(); j++){
		// log.debug(climateMatrixSite.get(0).get(j) + " " +
		// listYears.get(j));
		// }
		// setCursor(Cursor.getDefaultCursor());
		/*
		 * create JFileChooser object to generate a browsing capabilities
		 */
		JFileChooser fileBrowse = new JFileChooser();
		
		fileBrowse = new JFileChooser(savePath.substring(0, savePath.lastIndexOf(File.separator)));
		/*
		 * set multiselect on (even though we don't need it)
		 */
		fileBrowse.setMultiSelectionEnabled(true);
		/*
		 * set file and folder directive
		 */
		fileBrowse.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
		/*
		 * set file type: coma delimited file csv
		 */
		//
		FileFilter filter1 = new CSVFileFilter();
		fileBrowse.setFileFilter(filter1);
		/*
		 * set dialog text: select the name and location of the matrix files
		 */
		fileBrowse.setDialogTitle("Select the name and location of the Stats Summary file:");
		
		/*
		 * create the writer object for each of the files to be created
		 */
		Writer wr;
		Writer wrWDE;
		Writer wrSample;
		Writer wrWDESample;
		/*
		 * set delimiter in this case we are using comas ","
		 */
		String delim = ",";
		
		/*
		 * Start writing information into the files
		 */
		try
		{
			if (analysisType.equals(AnalysisType.COMPOSITE))
			{
				wr = new BufferedWriter(new FileWriter(summaryFile));
				wrWDE = new BufferedWriter(new FileWriter(exceedenceFile));
				/*
				 * write the heading to the files
				 */
				String buffer = "";
				
				buffer = buffer + "Composite Parameters" + delim;
				for (int i = 0; i < inputFileArray.length; i++)
				{
					buffer = buffer + inputFileArray[i].getLabel() + delim;
				}
				;
				wr.write(buffer.substring(0, buffer.length() - 1) + System.getProperty("line.separator"));
				
				buffer = "";
				for (int j = 0; j < statsparam.length; j++)
				{
					buffer = buffer + statsparam[j] + delim;
					
					for (int k = 0; k < inputFileArray.length; k++)
					{
						if (j == 0)
						{
							if (numberOfintervalscomp[k] < 3)
							{
								buffer = buffer + twoPlace.format(numberOfintervalscomp[k]) + delim;
							}
							else
							{
								buffer = buffer + twoPlace.format(summaryComp[0][k]) + delim;
							}
							
						}
						else
						{
							if (enoughIntComp[k])
							{
								if (summaryComp[j][k] == -99)
								{
									buffer = buffer + "�" + delim;
								}
								else
								{
									buffer = buffer + twoPlace.format(summaryComp[j][k]) + delim;
								}
							}
							else
							{
								buffer = buffer + "�" + delim;
							}
						}
					} // end of k loop filearray
					
					wr.write(buffer.substring(0, buffer.length() - 1) + System.getProperty("line.separator"));
					buffer = "";
				} // end of j loop Stats
					// wr.close();
					//
					//
					// wrWDE = new BufferedWriter(new
					// FileWriter(outputWDExceeTable));
				/*
				 * write the heading to the files
				 */
				
				buffer = "";
				wrWDE.write("Exceedence Prob" + delim);
				for (int i = 0; i < inputFileArray.length; i++)
				{
					buffer = buffer + inputFileArray[i].getLabel() + delim;
				}
				wrWDE.write(buffer.substring(0, buffer.length() - 1) + System.getProperty("line.separator"));
				buffer = "";
				
				for (int j = 0; j < fixvalt.length; j++)
				{
					buffer = buffer + threePlace.format(fixvalt[j]) + delim;
					
					for (int k = 0; k < inputFileArray.length; k++)
					{
						if (enoughIntComp[k])
						{
							buffer = buffer + twoPlace.format(ExceeProbcomp[j][k]) + delim;
						}
						else
						{
							buffer = buffer + "�" + delim;
						}
					}
					wrWDE.write(buffer.substring(0, buffer.length() - 1) + System.getProperty("line.separator"));
					buffer = "";
				}
				
				wr.close();
				wrWDE.close();
				
			} // end of if jRadioComp is selecte
			if (analysisType.equals(AnalysisType.SAMPLE))
			{
				wrSample = new BufferedWriter(new FileWriter(summaryFile));
				wrWDESample = new BufferedWriter(new FileWriter(exceedenceFile));
				/*
				 * write the heading to the files
				 */
				wrSample.write("Sample Parameters" + delim);
				for (int i = 0; i < inputFileArray.length; i++)
				{
					wrSample.write(inputFileArray[i].getLabel() + delim);
				}
				wrSample.write(System.getProperty("line.separator"));
				for (int j = 0; j < statsparam.length; j++)
				{
					wrSample.write(statsparam[j] + delim);
					for (int k = 0; k < inputFileArray.length; k++)
					{
						if (j == 0)
						{
							if (numberOfintervalssamp[k] < 3)
							{
								wrSample.write(twoPlace.format(numberOfintervalssamp[k]) + delim);
							}
							else
							{
								wrSample.write(twoPlace.format(summarySample[0][k]) + delim);
							}
							
						}
						else
						{
							if (enoughIntSamp[k])
							{
								if (summarySample[j][k] == -99)
								{
									wrSample.write("�" + delim);
								}
								else
								{
									wrSample.write(twoPlace.format(summarySample[j][k]) + delim);
								}
							}
							else
							{
								wrSample.write("�" + delim);
							}
						}
					} // end of k loop file array
					wrSample.write(System.getProperty("line.separator"));
				} // end of loop j loop stats
					// wrSample.close();
					//
					//
					// log.debug("the size is "+fixvalt.length+" X "+inputFile.length);
					// wrWDESample = new BufferedWriter(new
					// FileWriter(outputWDExceeTablesample));
				/*
				 * write the heading to the files
				 */
				wrWDESample.write("Exceedence Prob" + delim);
				for (int i = 0; i < inputFileArray.length; i++)
				{
					wrWDESample.write(inputFileArray[i].getLabel() + delim);
				}
				wrWDESample.write(System.getProperty("line.separator"));
				for (int j = 0; j < fixvalt.length; j++)
				{
					wrWDESample.write(threePlace.format(fixvalt[j]) + delim);
					for (int k = 0; k < inputFileArray.length; k++)
					{
						// System.out.print(ExceeProbcomp[j][k]+delim);
						if (enoughIntSamp[k])
						{
							wrWDESample.write(twoPlace.format(ExceeProbsample[j][k]) + delim);
						}
						else
						{
							wrWDESample.write("�" + delim);
						}
						
					}
					// System.out.print(System.getProperty("line.separator"));
					wrWDESample.write(System.getProperty("line.separator"));
				}
				wrSample.close();
				wrWDESample.close();
			} // end of jradiosample
			
		} // end of Try
		catch (IOException ex)
		{
			ex.printStackTrace();
		}
		finally
		{
			
		}
		
	}
	
	public static ArrayList<Integer> generateFireIntervals(ArrayList<Integer> YearsWithFires) {
	
		ArrayList<Integer> fireIntervals = new ArrayList<Integer>();
		for (int i = 0; i < YearsWithFires.size() - 1; i++)
		{
			fireIntervals.add(YearsWithFires.get(i + 1) - YearsWithFires.get(i));
			log.debug("here are the FireIntervals " + fireIntervals.get(i));
		}
		
		return fireIntervals;
	}
	
}
