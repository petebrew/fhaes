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
package org.fhaes.tools;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;

import org.codehaus.plexus.util.FileUtils;
import org.fhaes.enums.EventTypeToProcess;
import org.fhaes.enums.FireFilterType;
import org.fhaes.enums.SampleDepthFilterType;
import org.fhaes.fhfilereader.FHFile;
import org.fhaes.fhfilereader.FHX2FileReader;
import org.fhaes.filefilter.FHXFileFilter;
import org.fhaes.filefilter.TXTFileFilter;
import org.fhaes.preferences.App;
import org.fhaes.preferences.FHAESPreferences.PrefKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * FHOperations Class.
 */
public class FHOperations {
	
	private JFrame parent;
	private FHFile[] inputFileArray;
	private FHFile outputFile;
	private Integer startYear;
	private Integer endYear;
	private String comments;
	private Double fireFilterValue;
	private FireFilterType fireFilterType;
	private SampleDepthFilterType sampleDepthFilterType;
	private Boolean createJoinFile;
	private Boolean createCompositeFile;
	private Boolean createEventFile;
	private Integer minSamples;
	
	private static final Logger log = LoggerFactory.getLogger(FHOperations.class);
	
	// highway is the choice a user has to start the matrix
	// from the first fire year or the first year of data
	@SuppressWarnings("unused")
	private boolean highway = true;
	
	public FHOperations(JFrame parent, FHFile[] inputFiles, FHFile outputFile, Integer startYear, Integer endYear, Double fireFilterValue,
			FireFilterType fireFilterType, SampleDepthFilterType sampleDepthFilterType, Boolean createJoinFile,
			Boolean createCompositeFile, Boolean createEventFile, Integer minNumberSamples, String comments) {
	
		log.debug("InputFileArray:");
		for (File f : inputFiles)
		{
			log.debug("  - " + f.getName());
		}
		
		this.parent = parent;
		
		this.sampleDepthFilterType = sampleDepthFilterType;
		this.minSamples = minNumberSamples;
		this.inputFileArray = inputFiles;
		this.outputFile = outputFile;
		this.startYear = startYear;
		this.endYear = endYear;
		this.fireFilterValue = fireFilterValue;
		this.fireFilterType = fireFilterType;
		this.createJoinFile = createJoinFile;
		this.createCompositeFile = createCompositeFile;
		this.createEventFile = createEventFile;
		this.comments = comments;
		performOperation();
	}
	
	/**
	 * Create an event file from the specified file array outputting to the specified file and trimmed to the specified years
	 * 
	 * @param parent
	 * @param inputFileArray
	 * @param outputFile
	 * @param firstYear
	 * @param lastYear
	 * @return
	 */
	public static File createEventFile(JFrame parent, FHFile[] inputFileArray, FHFile outputFile, Integer firstYear, Integer lastYear) {
	
		if (outputFile != null)
		{
			new FHOperations(parent, inputFileArray, outputFile, firstYear, lastYear, 1.0, FireFilterType.NUMBER_OF_EVENTS,
					SampleDepthFilterType.MIN_NUM_SAMPLES, false, false, true, 1, null);
			return outputFile;
		}
		
		return null;
	}
	
	/**
	 * Create an event file from the specified file array and trimmed to the specified start and end years
	 * 
	 * @param parent
	 * @param inputFileArray
	 * @param startYear
	 * @param endYear
	 * @param minNumberSamples
	 * @return
	 */
	public static File createEventFile(JFrame parent, FHFile[] inputFileArray, Integer startYear, Integer endYear, Integer minNumberSamples) {
	
		FHFile file = new FHFile(getOutputFile(parent, new TXTFileFilter(), true));
		
		if (file != null)
		{
			new FHOperations(parent, inputFileArray, file, startYear, endYear, 1.0, FireFilterType.NUMBER_OF_EVENTS,
					SampleDepthFilterType.MIN_NUM_SAMPLES, false, false, true, minNumberSamples, null);
		}
		return file;
	}
	
	/**
	 * Create an event file from the specified file array and trimmed to the specified start and end years
	 * 
	 * @param parent
	 * @param inputFileArray
	 * @param startYear
	 * @param endYear
	 * @param fireFilterType
	 * @param sampleDepthFilterType
	 * @param fireFilterValue
	 * @param minNumberSamples
	 * @param comments
	 * @return
	 */
	public static File createEventFile(JFrame parent, FHFile[] inputFileArray, Integer startYear, Integer endYear,
			FireFilterType fireFilterType, SampleDepthFilterType sampleDepthFilterType, Double fireFilterValue, Integer minNumberSamples,
			String comments) {
	
		FHFile file = getOutputFile(parent, new TXTFileFilter(), true);
		
		if (file != null)
		{
			new FHOperations(parent, inputFileArray, file, startYear, endYear, fireFilterValue, fireFilterType, sampleDepthFilterType,
					false, false, true, minNumberSamples, comments);
			return file;
		}
		
		return null;
	}
	
	/**
	 * Create a composite file from the specified file array and trimmed to the specified start and end years
	 * 
	 * @param parent
	 * @param inputFileArray
	 * @param startYear
	 * @param endYear
	 * @param fireFilterType
	 * @param sampleDepthFilterType
	 * @param fireFilterValue
	 * @param minNumberSamples
	 * @return
	 */
	public static File createCompositeFile(JFrame parent, FHFile[] inputFileArray, Integer startYear, Integer endYear,
			FireFilterType fireFilterType, SampleDepthFilterType sampleDepthFilterType, Double fireFilterValue, Integer minNumberSamples) {
	
		FHFile file = getOutputFile(parent, new FHXFileFilter(), true);
		
		log.debug("SampleDepthFilterType = " + sampleDepthFilterType);
		
		if (file != null)
		{
			new FHOperations(parent, inputFileArray, file, startYear, endYear, fireFilterValue, fireFilterType, sampleDepthFilterType,
					false, true, false, minNumberSamples, null);
			return file;
		}
		
		return null;
	}
	
	/**
	 * Join multiple files together into a new FHX file trimmed to the specified years
	 * 
	 * @param inputFileArray
	 * @param outputFile
	 * @param startYear
	 * @param endYear
	 * @param minNumberSamples
	 */
	public static File joinFiles(JFrame parent, FHFile[] inputFileArray, Integer startYear, Integer endYear, Integer minNumberSamples) {
	
		FHFile file = getOutputFile(parent, new FHXFileFilter(), false);
		
		if (file != null)
		{
			new FHOperations(parent, inputFileArray, file, startYear, endYear, 1.0, FireFilterType.NUMBER_OF_EVENTS,
					SampleDepthFilterType.MIN_NUM_SAMPLES, true, false, false, minNumberSamples, null);
			return file;
		}
		
		return null;
	}
	
	/**
	 * Join multiple files together into a new FHX file trimmed to the specified years
	 * 
	 * @param parent
	 * @param inputFileArray
	 * @param outputFile
	 * @param startYear
	 * @param endYear
	 * @return
	 */
	public static File joinFiles(JFrame parent, FHFile[] inputFileArray, FHFile outputFile, Integer startYear, Integer endYear) {
	
		if (outputFile != null)
		{
			new FHOperations(parent, inputFileArray, outputFile, startYear, endYear, 1.0, FireFilterType.NUMBER_OF_EVENTS,
					SampleDepthFilterType.MIN_NUM_SAMPLES, true, false, false, 1, null);
			return outputFile;
		}
		
		return null;
	}
	
	/**
	 * Join multiple files together into a new FHX file trimmed to the specified years
	 * 
	 * @param inputFileArray
	 * @param outputFile
	 * @param startYear
	 * @param endYear
	 * @param minNumberSamples
	 */
	public static void joinFiles(JFrame parent, FHFile[] inputFileArray, FHFile outputFile, Integer startYear, Integer endYear,
			Integer minNumberSamples) {
	
		new FHOperations(parent, inputFileArray, outputFile, startYear, endYear, 1.0, FireFilterType.NUMBER_OF_EVENTS,
				SampleDepthFilterType.MIN_NUM_SAMPLES, true, false, false, minNumberSamples, null);
	}
	
	/**
	 * Create a basic composite file from the specified input files.
	 * 
	 * @param inputFileArray
	 * @param outputFile
	 */
	public static void createCompositeFile(JFrame parent, FHFile[] inputFileArray, FHFile outputFile) {
	
		new FHOperations(parent, inputFileArray, outputFile, 0, 0, 1.0, FireFilterType.NUMBER_OF_EVENTS,
				SampleDepthFilterType.MIN_NUM_SAMPLES, false, true, false, 1, null);
	}
	
	/**
	 * Create a composite file from the specified input files. Limit to the specified years and filter on the specified fire filter type and
	 * value
	 * 
	 * @param parent
	 * @param inputFileArray
	 * @param outputFile
	 * @param startYear
	 * @param endYear
	 * @param fireFilterType
	 * @param sampleDepthFilterType
	 * @param fireFilterValue
	 * @param minNumberSamples
	 */
	public static void createCompositeFile(JFrame parent, FHFile[] inputFileArray, FHFile outputFile, Integer startYear, Integer endYear,
			FireFilterType fireFilterType, SampleDepthFilterType sampleDepthFilterType, Double fireFilterValue, Integer minNumberSamples) {
	
		new FHOperations(parent, inputFileArray, outputFile, startYear, endYear, fireFilterValue, fireFilterType, sampleDepthFilterType,
				false, true, false, minNumberSamples, null);
	}
	
	/**
	 * Run the operation
	 */
	
	private void performOperation() {
	
		// Hard coded to FIRE_EVENT for now.
		// TODO Check we're happy with this
		EventTypeToProcess eventType = EventTypeToProcess.FIRE_EVENT;
		
		boolean run = false;
		
		/**
		 * PERFORM SANITY CHECKSS
		 */
		// FIRST CHECK
		if (inputFileArray != null)
		{
			run = true;
			// SECOND CHECK
			if (createJoinFile || createCompositeFile || this.createEventFile)
			{
				run = true;
				// THIRD CHECK
				if (startYear <= endYear)
				{
					run = true;
				}
				else
				{
					run = false;
					JOptionPane.showMessageDialog(parent, "Trees cannot grow after they die :'(.", "Warning", JOptionPane.WARNING_MESSAGE);
					return;
				}
			}
			else
			{
				run = false;
				JOptionPane.showMessageDialog(parent, "At least one output file should be selected.", "Warning",
						JOptionPane.WARNING_MESSAGE);
				return;
			}
		}
		else
		{
			run = false;
			JOptionPane.showMessageDialog(parent, "Select at least one file.", "Warning", JOptionPane.WARNING_MESSAGE);
		} // end sanity checks
		
		/**
		 * PERFORM OPERATION
		 */
		if (run)
		{
			ArrayList<FHX2FileReader> myReader = new ArrayList<FHX2FileReader>();
			ArrayList<Integer> firstYears = new ArrayList<Integer>();
			ArrayList<Integer> lastYears = new ArrayList<Integer>();
			ArrayList<Integer> startCompYear = new ArrayList<Integer>();
			ArrayList<Integer> SeriesNameLengthJoin = new ArrayList<Integer>();
			ArrayList<Integer> SeriesNameLengthComp = new ArrayList<Integer>();
			ArrayList<String> sampleNameComp = new ArrayList<String>();
			Integer minFirstYear = new Integer(9999);
			Integer minFirstYearComp = new Integer(9999);
			Integer maxLastYear = new Integer(0);
			Integer maxLastYearComp = new Integer(0);
			Integer totalNumberOfseriesJoin = new Integer(0);
			Integer totalNumberOfseriesComp = new Integer(0);
			Integer maxSeriesNameLengthJoin = new Integer(0);
			Integer maxSeriesNameLengthComp = new Integer(0);
			// String sampleNameComp = new String("");
			String dotsTemp = new String();
			int tempstartcomp = 0;
			String noNameTemp = new String();
			
			for (int i = 0; i < inputFileArray.length; i++)
			{
				FHFile f = inputFileArray[i];
				myReader.add(new FHX2FileReader(f));
				log.debug("first fire year : " + myReader.get(i).getFirstFireYear().intValue() + " minfirstyearcomp"
						+ minFirstYearComp.intValue());
				Arrays.sort(myReader.get(i).getStartYearIndexPerSample());
				startCompYear.add(myReader.get(i).getStartYearIndexPerSample()[0] + myReader.get(i).getFirstYear().intValue());
				firstYears.add(myReader.get(i).getFirstYear());
				tempstartcomp = myReader.get(i).getStartYearIndexPerSample()[0] + myReader.get(i).getFirstYear().intValue();
				log.debug("start composite year: " + myReader.get(i).getStartYearIndexPerSample()[0] + " fy "
						+ myReader.get(i).getFirstYear().intValue() + " final " + tempstartcomp);
				lastYears.add(myReader.get(i).getLastYear());
				SeriesNameLengthJoin.add(myReader.get(i).getLengthOfSeriesName());
				totalNumberOfseriesJoin = totalNumberOfseriesJoin + myReader.get(i).getNumberOfSeries();
				// totalNumberOfseriesComp = inputFile.length;
				// SeriesNameLengthComp.add((myReader.get(i).getName().length()-4));
				if (createCompositeFile || createEventFile)
				{
					totalNumberOfseriesComp = inputFileArray.length;
					SeriesNameLengthComp.add((myReader.get(i).getName().length() - 4));
					sampleNameComp.add(myReader.get(i).getName().substring(0, (myReader.get(i).getName().length() - 4)));
					// myReader.get(i).makeClimate2d();
					// myReader.get(i).makefilters2d();
					if (startYear.equals(0))
					{
						if ((myReader.get(i).getStartYearIndexPerSample()[0] + myReader.get(i).getFirstYear().intValue()) < minFirstYearComp)
						{
							minFirstYearComp = (myReader.get(i).getStartYearIndexPerSample()[0] + myReader.get(i).getFirstYear().intValue());
							log.debug("the minFirstYearComp is: " + minFirstYearComp);
						}
					}
					else
					{
						if (startYear > (myReader.get(i).getStartYearIndexPerSample()[0] + myReader.get(i).getFirstYear().intValue()))
						{
							minFirstYearComp = startYear;
						}
						else
						{
							minFirstYearComp = (myReader.get(i).getStartYearIndexPerSample()[0] + myReader.get(i).getFirstYear().intValue());
						}
					}
					
					/*
					 * Set the last year accounting for the filter
					 */
					if (myReader.get(i).getLastYear() > maxLastYearComp)
					{
						maxLastYearComp = myReader.get(i).getLastYear();
					}
					if (!endYear.equals(0))
					{
						maxLastYearComp = endYear;
					}
					log.debug("at i = " + i + " minFirstYear = " + minFirstYearComp + " maxLastYearComp = " + maxLastYearComp);
				} // end of if jcheckcomp
				
			} // end loop first i
			log.debug(" size of Series seriesNameLengthComp " + SeriesNameLengthComp.size() + " size of name comp is "
					+ sampleNameComp.size() + " minFirstYearComp final is " + minFirstYearComp);
			Collections.sort(firstYears);
			Collections.sort(lastYears);
			Collections.sort(SeriesNameLengthJoin);
			// Collections.sort(SeriesNameLengthComp);
			maxSeriesNameLengthJoin = SeriesNameLengthJoin.get((SeriesNameLengthJoin.size() - 1));
			// maxSeriesNameLengthComp = SeriesNameLengthComp.get((SeriesNameLengthComp.size()-1));
			log.debug("the first year of join is: " + firstYears.get(0));
			/*
			 * set the beginning year accounting for the filter for the join
			 */
			if (startYear.equals(0))
			{
				minFirstYear = firstYears.get(0);
			}
			else
			{
				if (startYear > firstYears.get(0))
				{
					minFirstYear = startYear;
				}
				else
				{
					minFirstYear = firstYears.get(0);
				}
			}
			/*
			 * Set the last year accounting for the filter
			 */
			if (endYear.equals(0))
			{
				maxLastYear = lastYears.get((lastYears.size() - 1));
			}
			else
			{
				if (endYear < lastYears.get((lastYears.size() - 1)))
				{
					maxLastYear = endYear;
				}
				else
				{
					maxLastYear = lastYears.get((lastYears.size() - 1));
				}
			}
			// end of setting both beginning and end year accounting for filter
			
			/*
			 * Calculate the listYears the common years where the file will be analyzed for the join
			 */
			ArrayList<Integer> listYears = new ArrayList<Integer>();
			for (int i = 0; i < maxLastYear - minFirstYear + 1; i++)
			{
				listYears.add(minFirstYear + i);
			}
			
			/*
			 * Declaration of all the array lists needed in the process
			 */
			ArrayList<String> JoinMatrix = new ArrayList<String>();
			String joinStringTemp = new String();
			String nameTemp = new String();
			ArrayList<String> joinNameMatrix = new ArrayList<String>();
			
			if (createJoinFile)
			{
				for (int i = 0; i < totalNumberOfseriesJoin; i++)
				{
					dotsTemp = dotsTemp + ".";
					noNameTemp = noNameTemp + " ";
				}
				
				/*
				 * start processing each file individually:
				 */
				/*
				 * get the vector Year containing the vector of year of a given fhx file load it into the array list climateYear.
				 */
				for (int j = 0; j < listYears.size(); j++)
				{
					joinStringTemp = "";
					for (int k = 0; k < myReader.size(); k++)
					{
						if (myReader.get(k).getYearArray().indexOf(listYears.get(j)) == -1)
						{
							joinStringTemp = joinStringTemp + dotsTemp.substring(0, myReader.get(k).getNumberOfSeries());
						}
						else
						{
							joinStringTemp = joinStringTemp
									+ myReader.get(k).getData().get(myReader.get(k).getYearArray().indexOf(listYears.get(j)));
						}
					} // end k loop
					JoinMatrix.add(joinStringTemp);
					
				} // end of j loop
				/*
				 * Start the process of generating the Join file
				 */
				for (int j = 0; j < maxSeriesNameLengthJoin; j++)
				{
					nameTemp = "";
					for (int k = 0; k < myReader.size(); k++)
					{
						log.debug("Length of Series Name " + myReader.get(k).getLengthOfSeriesName());
						if (myReader.get(k).getLengthOfSeriesName() > j)
						{
							
							log.debug("bigger than " + " and j is" + j);
							if (myReader.get(k).getSeriesNameLine().get(j).length() >= myReader.get(k).getNumberOfSeries())
							{
								nameTemp = nameTemp
										+ myReader.get(k).getSeriesNameLine().get(j).substring(0, myReader.get(k).getNumberOfSeries());
							}
							else
							{
								nameTemp = nameTemp
										+ myReader.get(k).getSeriesNameLine().get(j)
												.substring(0, myReader.get(k).getSeriesNameLine().get(j).length())
										+ noNameTemp.substring(0, (myReader.get(k).getNumberOfSeries() - myReader.get(k)
												.getSeriesNameLine().get(j).length()));
							}
							
						}
						else
						{
							log.debug("else " + " and j is" + j);
							nameTemp = nameTemp + noNameTemp.substring(0, myReader.get(k).getNumberOfSeries());
						}
					}
					joinNameMatrix.add(nameTemp);
				}
			} // end of if jcheckjoin.isselected
			
			/*
			 * create arraylist need for the composite
			 */
			ArrayList<ArrayList<Integer>> test = new ArrayList<ArrayList<Integer>>();
			ArrayList<ArrayList<Integer>> climateMatrixSite = new ArrayList<ArrayList<Integer>>();
			ArrayList<ArrayList<Double>> filterMatrix = new ArrayList<ArrayList<Double>>();
			ArrayList<Integer> climateVector = new ArrayList<Integer>();
			ArrayList<ArrayList<Double>> climateVectorFilter2 = new ArrayList<ArrayList<Double>>();
			ArrayList<Integer> climateVectorActualSite = null;
			ArrayList<Double> filterVectorActual = null;
			ArrayList<Integer> minSampleFilter = null;
			ArrayList<Double> percentOfRecordingfilter = null;
			ArrayList<Integer> climateYear = new ArrayList<Integer>();
			
			ArrayList<ArrayList<Character>> nameLine = new ArrayList<ArrayList<Character>>();
			
			ArrayList<Integer> listYearsComp = new ArrayList<Integer>();
			
			if (createCompositeFile || createEventFile)
			{
				log.debug("i am here in jChekckcomp processing File ");
				Collections.sort(SeriesNameLengthComp);
				// maxSeriesNameLengthJoin = SeriesNameLengthJoin.get((SeriesNameLengthJoin.size()-1));
				maxSeriesNameLengthComp = SeriesNameLengthComp.get((SeriesNameLengthComp.size() - 1));
				
				/*
				 * Set up either of the two filters two create the binary matrix on the case of binary analysis there are two possible
				 * filters: Number of fires and percentage of scared trees.
				 */
				Integer firesFilter1 = new Integer(0);
				Double firesFilter2 = new Double(0);
				if (fireFilterType.equals(FireFilterType.NUMBER_OF_EVENTS))
				{
					if (fireFilterValue.intValue() != 1)
					{
						firesFilter1 = fireFilterValue.intValue();
						log.debug("number of fires is selected is:  NUMBER_OF_EVENTS" + firesFilter1);
					}
				}
				else if (fireFilterType.equals(FireFilterType.PERCENTAGE_OF_ALL_TREES))
				{
					if (fireFilterValue.intValue() != 1)
					
					{
						firesFilter2 = fireFilterValue / 100.0;
						log.debug("PERCENTAGE_OF_ALL_TREES is: " + firesFilter2);
					}
				}
				else if (fireFilterType.equals(FireFilterType.PERCENTAGE_OF_RECORDING))
				{
					if (fireFilterValue.intValue() != 1)
					{
						firesFilter2 = fireFilterValue / 100.0;
						log.debug("PERCENTAGE_OF_RECORDING: " + firesFilter2);
					}
				}
				else
				{
					log.error("Unsupported fire filter tpye");
				}
				
				/*
				 * Calculate the listYears the common years where the file will be analyzed
				 */
				for (int i = 0; i < maxLastYearComp - minFirstYearComp + 1; i++)
				{
					listYearsComp.add(minFirstYearComp + i);
				}
				/*
				 * start processing each file individually: The analysis can be done by either tree (non-binary) or by site (binary). by
				 * tree the box selected is: jCheckTree. by site the box selected is:
				 */
				for (int i = 0; i < myReader.size(); i++)
				{
					log.debug("  Starting to Process file in Composite : " + myReader.get(i).getName());
					log.debug(" the first year of the file above is: " + myReader.get(i).getFirstYear());
					log.debug("  sampledepths at year 1565 : " + myReader.get(i).getSampleDepths(eventType)[189]);
					log.debug(" the size of sampledepths is:  " + myReader.get(i).getSampleDepths(eventType).length);
					/*
					 * set the beginning Year accounting for the filter
					 */
					/*
					 * create the arraylist with the names of each file so that we can create the sample name for the fhx file
					 */
					// sampleNameComp.add(myReader.get(i).getName().substring(0,myReader.get(i).getName().length()-4));
					
					// if(myReader.get(i).getClimate2d().get(myReader.get(i).getstartYearperSample()[0]).contains(1)){
					/*
					 * log.debug("size of climate2d is: " + myReader.get(i).getClimate2d().size() + " X " +
					 * myReader.get(i).getClimate2d().get(1).size());
					 */
					/*
					 * get the vector Year containing the vector of year of a given fhx file load it into the array list climateYear.
					 */
					// log.debug("got pass the if ");
					climateYear = myReader.get(i).getYearArray();
					
					// Create filter based on min number of samples/recorder samples
					int[] depths = null;
					if (sampleDepthFilterType.equals(SampleDepthFilterType.MIN_NUM_SAMPLES))
					{
						depths = myReader.get(i).getSampleDepths(eventType);
						log.debug("MIN_NUM_SAMPLES ");
					}
					else if (sampleDepthFilterType.equals(SampleDepthFilterType.MIN_NUM_RECORDER_SAMPLES))
					{
						depths = myReader.get(i).getRecordingDepths(eventType);
						log.debug(" MIN_NUM_RECORDER_SAMPLES");
					}
					else
					{
						log.error("Unknown sample depth filter type.");
						return;
					}
					minSampleFilter = new ArrayList<Integer>();
					for (int ij = 0; ij < listYearsComp.size(); ij++)
					{
						if (climateYear.indexOf(listYearsComp.get(ij)) == -1)
						{
							minSampleFilter.add(-1);
						}
						else
						{
							// log.debug("the sample depth is "
							// + myReader.get(i).getSampleDepths()[climateYear.indexOf(listYearsComp.get(ij))]);
							minSampleFilter.add(new Integer(depths[climateYear.indexOf(listYearsComp.get(ij))]));
						}
						// log.debug(" " + minSampleFilter.get(ij));
					}
					
					if (fireFilterValue.intValue() != 1)
					{
						/*
						 * get both matrices:
						 * 
						 * 2. filters2d matrix composed of the 3 filters number of fires (total capital letter per row) total number of tree
						 * (total lower case letter plus bars counting only after a fire) percent of scared trees total fires/total trees
						 */
						
						climateVectorFilter2 = myReader.get(i).getFilterArrays(EventTypeToProcess.FIRE_EVENT);
						
						/*
						 * if by tree analysis is selected create two matrices (array list) 1. filterMatrix containing the three filter
						 * vectors only in between common years (so using the listYearComp array list subset of the years vector) 2.
						 * climateMatrix 2 dimensional array list containing binary matrices restricted to the listYear list.
						 */
						if (fireFilterType.equals(FireFilterType.PERCENTAGE_OF_RECORDING))
						{
							percentOfRecordingfilter = new ArrayList<Double>();
							
							for (int ij = 0; ij < listYearsComp.size(); ij++)
							{
								
								if (climateYear.indexOf(listYearsComp.get(ij)) == -1)
								{
									percentOfRecordingfilter.add(-1.0);
								}
								else
								{
									if (myReader.get(i).getRecordingDepths(eventType)[climateYear.indexOf(listYearsComp.get(ij))] != 0)
									{
										percentOfRecordingfilter
												.add(new Double(climateVectorFilter2.get(0).get(climateYear.indexOf(listYearsComp.get(ij)))
														/ myReader.get(i).getRecordingDepths(eventType)[climateYear.indexOf(listYearsComp
																.get(ij))]));
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
								for (int ij = 0; ij < listYearsComp.size(); ij++)
								{ // log.debug(" climateYear.indexOf(listYearsComp.get(j))" +
									// climateYear.indexOf(listYearsComp.get(ij)));
									// if(ik==0){log.debug("number of fires
									// "+climateVectorFilter2.get(0).get(climateYear.indexOf(listYearsComp.get(ij)))+" year
									// "+listYearsComp.get(ij));}
									if (climateYear.indexOf(listYearsComp.get(ij)) == -1)
									{
										filterVectorActual.add(-1.0);
									}
									else
									{
										filterVectorActual.add(new Double(climateVectorFilter2.get(ik).get(
												climateYear.indexOf(listYearsComp.get(ij)))));
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
					 * get matrix climate binary matrix by site (binary analysis)
					 */
					climateVector = myReader.get(i).getFireEventsArray();
					// log.debug("the climateyear first is: " + climateYear.get(0)+ " listYearsComp.get(0) "
					// +climateYear.indexOf(listYearsComp.get(0)));
					climateVectorActualSite = new ArrayList<Integer>();
					
					for (int j = 0; j < listYearsComp.size(); j++)
					{
						
						if (climateYear.indexOf(listYearsComp.get(j)) == -1)
						{
							climateVectorActualSite.add(-1);
						}
						else
						{
							if (minSampleFilter.get(j).intValue() >= minSamples.intValue())
							{
								// log.debug("j is: " + j + "inside minsampleFilter " + minSampleFilter.get(j));
								if (fireFilterValue.intValue() != 1)
								{
									// log.debug("inside fileFilter !=1 ");
									if (fireFilterType.equals(FireFilterType.NUMBER_OF_EVENTS))
									{
										// climateVectorActualSite.add(climateVector.get(climateYear.indexOf(listYears.get(j))));
										// log.debug("number of fires is selected is: "+
										// firesFilter1+" "+climateVector.get(climateYear.indexOf(listYears.get(j))));
										// log.debug("fire filter: "+firesFilter1+" year is: "+listYears.get(j)
										// +" fires: "+filterMatrix.get(3*i).get(j)+" climatevector:
										// "+climateVector.get(climateYear.indexOf(listYears.get(j))));
										log.debug("fire filter: " + firesFilter1 + " year is: " + listYearsComp.get(j) + " fires: "
												+ filterMatrix.get(3 * i).get(j) + " climatevector: "
												+ climateVector.get(climateYear.indexOf(listYearsComp.get(j))));
										if ((filterMatrix.get(3 * i).get(j) < firesFilter1)
												&& (climateVector.get(climateYear.indexOf(listYearsComp.get(j)))) != -1.0)
										{
											climateVectorActualSite.add(0);
										}
										else
										{
											climateVectorActualSite.add(climateVector.get(climateYear.indexOf(listYearsComp.get(j))));
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
										// TODO !!!!!
										// NEED ELENA'S INPUT ON THIS!
										
										// log.debug("percent of fires is selected is: "+
										// firesFilter2+" "+climateVector.get(climateYear.indexOf(listYearsComp.get(j))));
										// log.debug("the filter percent of fires is"+filterMatrix.get((3*i+2)).get(j));
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
										log.error("Unsupported fire filter type");
									}
								} // end of if filter not equal to 1
								else
								{
									climateVectorActualSite.add(climateVector.get(climateYear.indexOf(listYearsComp.get(j))));
									// climateVectorActualSite.add(0);
									// log.debug("j is " + j + "numberof fires is "
									// + climateVector.get(climateYear.indexOf(listYearsComp.get(j))));
									// climateVectorActualSite.add(-1);
								} // end of else of if filter not equal to 1
							} // end of if for minsamplefilter bigger or equal to minSamples
							else
							{
								// log.debug("j is " + j + "minSampleFilter is " + minSampleFilter.get(j));
								climateVectorActualSite.add(-1);
								// climateVectorActualSite.add(-1);
							}
						} // end else for if climateYear.indexOf(listYearsComp.get(j)) == -1
							// log.debug("j is " + j + "numberof fires is " +
							// climateVector.get(climateYear.indexOf(listYearsComp.get(j))));
					} // end of j loop listyears
						// log.debug("size by site binary "+climateVectorActualSite.size()+" "+climateVectorActualSite);
					climateMatrixSite.add(climateVectorActualSite);
					
					/*
					 * Create the section of the fhx file with the name of the sample in columns. the names of the samples will be the
					 */
					// log.debug("the reader size is "+ myReader.size());
					// log.debug("the size of sampleNameComp is "+sampleNameComp.size());
					ArrayList<Character> nameVectorActualComp = new ArrayList<Character>();
					for (int j = 0; j < maxSeriesNameLengthComp; j++)
					{
						if (sampleNameComp.get(i).length() > j)
						{
							// log.debug("lenght is "+sampleNameComp.get(i)+" j "+ j );
							nameVectorActualComp.add(sampleNameComp.get(i).charAt(j));
						}
						else
						{
							// log.debug("lenght else is "+sampleNameComp.get(i)+" j "+ j );
							nameVectorActualComp.add(' ');
						}
					}
					nameLine.add(nameVectorActualComp);
					// log.debug("size of nameline "+nameLine.size()+" X "+nameLine.get(i).size());
					// log.debug(" endding to Process file : "+ myReader.get(i).getName());
				} // end of i reader do loop
				
			} // end of if jcheckcomp is selected
			
			// Instantiate the file and writer objects
			File outputFileJoin = outputFile;
			File outputFileComp = outputFile;
			File outputFileEvent = outputFile;
			Writer wr = null;
			Writer wrComp = null;
			Writer wrEvent = null;
			
			/**
			 * Write data to output files
			 */
			try
			{
				
				/**
				 * Create join file if requested
				 */
				if (createJoinFile)
				{
					
					wr = new BufferedWriter(new FileWriter(outputFileJoin));
					
					// write the format heading to the files
					wr.write("FHX2 FORMAT" + System.getProperty("line.separator"));
					wr.write(minFirstYear.toString() + " " + totalNumberOfseriesJoin.toString() + " " + maxSeriesNameLengthJoin.toString()
							+ System.getProperty("line.separator"));
					for (int k = 0; k < joinNameMatrix.size(); k++)
					{
						wr.write(joinNameMatrix.get(k) + System.getProperty("line.separator"));
					}
					wr.write(System.getProperty("line.separator"));
					for (int i = 0; i < listYears.size(); i++)
					{
						wr.write(JoinMatrix.get(i) + " " + listYears.get(i).toString());
						wr.write(System.getProperty("line.separator"));
					}
					
				}
				
				/**
				 * Create composite file if requested
				 */
				if (createCompositeFile)
				{
					wrComp = new BufferedWriter(new FileWriter(outputFileComp));
					
					// write the format heading to the files
					wrComp.write("FHX2 FORMAT" + System.getProperty("line.separator"));
					wrComp.write(minFirstYearComp.toString() + " " + totalNumberOfseriesComp.toString() + " "
							+ maxSeriesNameLengthComp.toString() + System.getProperty("line.separator"));
					for (int j = 0; j < maxSeriesNameLengthComp; j++)
					{
						for (int i = 0; i < nameLine.size(); i++)
						{
							wrComp.write(nameLine.get(i).get(j));
						}
						wrComp.write(System.getProperty("line.separator"));
					}
					wrComp.write(System.getProperty("line.separator"));
					for (int i = 0; i < listYearsComp.size(); i++)
					{
						for (int j = 0; j < climateMatrixSite.size(); j++)
						{
							if ((i + minFirstYearComp) == startCompYear.get(j).intValue())
							{
								if (climateMatrixSite.get(j).get(i) == 1)
								{
									wrComp.write("U");
								}
								if (climateMatrixSite.get(j).get(i) != 1)
								{
									wrComp.write("{");
								}
							}
							else
							{
								if (climateMatrixSite.get(j).get(i) == -1)
								{
									wrComp.write(".");
								}
								if (climateMatrixSite.get(j).get(i) == 0)
								{
									wrComp.write("|");
								}
								if (climateMatrixSite.get(j).get(i) == 1)
								{
									wrComp.write("U");
								}
							}
						}
						wrComp.write(" " + listYearsComp.get(i).toString());
						wrComp.write(System.getProperty("line.separator"));
					}
					
				}
				
				/**
				 * Create event file if requested
				 */
				if (createEventFile)
				{
					Boolean eventFlag;
					wrEvent = new BufferedWriter(new FileWriter(outputFileEvent));
					
					// write the format heading to the file
					wrEvent.write("* this is an Event file created from the following FHX files:" + System.getProperty("line.separator"));
					for (File f : this.inputFileArray)
					{
						wrEvent.write("*   - " + f.getAbsolutePath() + System.getProperty("line.separator"));
					}
					
					// Write any comments
					for (String line : this.comments.split(System.getProperty("line.separator")))
					{
						wrEvent.write("* " + line + System.getProperty("line.separator"));
					}
					
					// write the data to the file
					for (int i = 0; i < listYearsComp.size(); i++)
					{
						eventFlag = false;
						for (int j = 0; j < climateMatrixSite.size(); j++)
						{
							if (climateMatrixSite.get(j).get(i) == 1)
							{
								eventFlag = true;
							}
							
						}
						if (eventFlag)
						{
							wrEvent.write(listYearsComp.get(i).toString());
							wrEvent.write(System.getProperty("line.separator"));
						}
					} // end of i loop
				}
			}
			catch (IOException ex)
			{
				ex.printStackTrace();
				log.error("IOException caught writing merge, composite or event file");
			}
			finally
			{
				// Make sure all writers are closed
				try
				{
					wr.close();
				}
				catch (Exception e)
				{
				}
				try
				{
					wrComp.close();
				}
				catch (Exception e)
				{
				}
				try
				{
					wrEvent.close();
				}
				catch (Exception e)
				{
				}
			}
			// }
		}
	}
	
	/**
	 * Show JFileChooser dialog with specified filter to get a save filename from the user
	 * 
	 * @param parent
	 * @param filter
	 * @param acceptAll
	 * @return
	 */
	private static FHFile getOutputFile(JFrame parent, FileFilter filter, Boolean acceptAll) {
	
		FHFile file;
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
		
		fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
		fc.setAcceptAllFileFilterUsed(acceptAll);
		if (filter != null)
		{
			fc.addChoosableFileFilter(filter);
			fc.setFileFilter(filter);
		}
		
		// Show dialog and get specified file
		int returnVal = fc.showSaveDialog(parent);
		if (returnVal == JFileChooser.APPROVE_OPTION)
		{
			file = new FHFile(fc.getSelectedFile());
			App.prefs.setPref(PrefKey.PREF_LAST_EXPORT_FOLDER, file.getAbsolutePath());
		}
		else
		{
			return null;
		}
		
		log.debug("User selected '" + fc.getFileFilter() + "' file filter");
		
		if (FileUtils.getExtension(file.getAbsolutePath()) == "")
		{
			log.debug("Output file extension not set by user");
			
			if (fc.getFileFilter().getDescription().equals(new FHXFileFilter().getDescription()))
			{
				log.debug("Adding fhx extension to output file name");
				file = new FHFile(file.getAbsolutePath() + ".fhx");
			}
			else if (fc.getFileFilter().getDescription().equals(new TXTFileFilter().getDescription()))
			{
				log.debug("Adding txt extension to output file name");
				file = new FHFile(file.getAbsolutePath() + ".txt");
			}
		}
		else
		{
			log.debug("Output file extension set my user to '" + FileUtils.getExtension(file.getAbsolutePath()) + "'");
		}
		
		if (file.exists())
		{
			Object[] options = { "Overwrite", "No", "Cancel" };
			int response = JOptionPane.showOptionDialog(parent, "The file '" + file.getName()
					+ "' already exists.  Are you sure you want to overwrite?", "Confirm", JOptionPane.YES_NO_CANCEL_OPTION,
					JOptionPane.QUESTION_MESSAGE, null, // do not use a custom Icon
					options, // the titles of buttons
					options[0]); // default button title
			
			if (response != JOptionPane.YES_OPTION)
			{
				return null;
			}
		}
		
		return file;
	}
	
}
