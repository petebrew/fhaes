/**************************************************************************************************
 * Fire History Analysis and Exploration System (FHAES), Copyright (C) 2015
 * 
 * Contributors: Joshua Brogan and Peter Brewer
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
package org.fhaes.fhsamplesize.controller;

import java.util.ArrayList;

import org.fhaes.enums.FireFilterType;
import org.fhaes.enums.ResamplingType;
import org.fhaes.fhsamplesize.model.AnalysisResultsModel;
import org.fhaes.fhsamplesize.model.SSIZAnalysisModel;
import org.fhaes.segmentation.SegmentModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * SSIZController Class.
 * 
 * @author Joshua Brogan and Peter Brewer
 */
public class SSIZController {
	
	// Declare logger
	private static final Logger log = LoggerFactory.getLogger(SSIZController.class);
	
	// Declare local constants
	private static final int RECORDING_BUT_NO_EVENT = 0;
	private static final int EVENT_RECORDED = 1;
	private static final int NO_DATA = -1;
	
	// Declare local variables
	static double[] stdDevMultiplier = { 1.960, 2.575, 3.294 };
	private static ArrayList<AnalysisResultsModel> analysisResults = new ArrayList<AnalysisResultsModel>();
	
	/**
	 * Return an int[] containing the count of fires in each year. Where the count does not reach the threshold specified in the model then
	 * the value for that year is set to zero. The returned array is also trimmed to the year range requested by the user.
	 * 
	 * @param pool
	 * @return
	 */
	public static Integer[] getFiresByYear(SSIZAnalysisModel model, ArrayList<ArrayList<Integer>> pool, SegmentModel segment) {
		
		int[] firesByYear = new int[pool.get(0).size()];
		int[] recordingByYear = new int[pool.get(0).size()];
		
		for (int i = 0; i < firesByYear.length; i++)
		{
			firesByYear[i] = 0;
			recordingByYear[i] = 0;
		}
		
		// Count
		for (int i = 0; i < pool.size(); i++)
		{
			ArrayList<Integer> series = pool.get(i);
			for (int j = 0; j < series.size(); j++)
			{
				Integer dataForYear = series.get(j);
				if (dataForYear == NO_DATA)
				{
					// No data (dot)
				}
				else if (dataForYear == RECORDING_BUT_NO_EVENT)
				{
					recordingByYear[j]++;
				}
				else if (dataForYear == EVENT_RECORDED)
				{
					firesByYear[j]++;
					recordingByYear[j]++;
				}
			}
		}
		
		ArrayList<ArrayList<Double>> filters = model.getReader().getFilterArrays(model.getEventType());
		
		// Set fire count in a year to zero when it doesn't reach the threshold value
		int rowindex = -1;
		if (model.getThresholdType().equals(FireFilterType.NUMBER_OF_EVENTS))
		{
			for (Double yearval : filters.get(0))
			{
				rowindex++;
				
				if (yearval < model.getThresholdValue() && yearval != 0)
				{
					// log.debug("Filtering out index " + rowindex);
					firesByYear[rowindex] = 0;
				}
			}
		}
		else if (model.getThresholdType().equals(FireFilterType.PERCENTAGE_OF_EVENTS))
		{
			for (Double yearval : filters.get(2))
			{
				rowindex++;
				double val = (yearval * 100);
				
				if (val < model.getThresholdValue())
				{
					// log.debug("Filtering out index " + rowindex);
					firesByYear[rowindex] = 0;
				}
			}
		}
		else
		{
			log.debug("No event threshold type specified so not filtering");
		}
		
		// Trim results to the year range specified by the user
		ArrayList<Integer> yearsArray = model.getReader().getYearArray();
		int firstind = yearsArray.indexOf(segment.getFirstYear());
		int lastind = yearsArray.indexOf(segment.getLastYear());
		ArrayList<Integer> newarr = new ArrayList<Integer>();
		
		for (int i = firstind; i <= lastind; i++)
		{
			newarr.add(firesByYear[i]);
		}
		
		return newarr.toArray(new Integer[newarr.size()]);
	}
	
	/**
	 * Run basic sanity checks on file.
	 * 
	 * @param model
	 * @throws Exception
	 */
	public static void doPreRunSetup(SSIZAnalysisModel model) throws Exception {
		
		// First do sanity checks in file
		if (model.getReader().getFirstYear() == 0 || model.getReader().getLastYear() == 0)
		{
			// Handle year 0
			throw new Exception("First and last years in file cannot be 0 as the year 0BC/AD does not exist");
		}
		if (model.getReader().getFirstYear() >= model.getReader().getLastYear())
		{
			throw new Exception("First year in file must be before last year");
		}
		
		SSIZController.analysisResults = new ArrayList<AnalysisResultsModel>();
	}
	
	/**
	 * TODO
	 * 
	 * @param model
	 * @return
	 * @throws Exception
	 */
	public static Double getCenturyMultiplier(SSIZAnalysisModel model, SegmentModel segment) throws Exception {
		
		// Do sanity checks on requested years
		if (segment.getFirstYear() < model.getReader().getFirstYear())
		{
			throw new Exception("You have requested to process from a year prior to the beginning of the file");
		}
		if (segment.getLastYear() > model.getReader().getLastYear())
		{
			throw new Exception("You have requested to process to a year after the last year in the file");
		}
		if (segment.getFirstYear() == 0 || segment.getLastYear() == 0)
		{
			// Handle year 0
			throw new Exception("You cannot request the first or last year to be 0 as the year 0BC/AD does not exist");
		}
		if (segment.getFirstYear() >= segment.getLastYear())
		{
			throw new Exception("First year must be before last year");
		}
		
		int numberOfYearsToProcess = segment.getLastYear() - segment.getFirstYear();
		
		if (segment.getLastYear() > 0)
		{
			numberOfYearsToProcess++;
		}
		
		log.debug("Number of years to process = " + numberOfYearsToProcess);
		log.debug("Century multiplier = " + (100.0d / numberOfYearsToProcess));
		
		return 100.0d / numberOfYearsToProcess;
	}
	
	/**
	 * Runs an iteration of the sample size analysis loop.
	 * 
	 * @throws Exception
	 */
	public static void runSampleSizeAnalysisLoopIteration(SSIZAnalysisModel model, Double centuryMultiplier, int currentIteration,
			SegmentModel segment) throws Exception {
			
		ArrayList<Double> firesPerCenturyPerSim = new ArrayList<Double>();
		
		// Loop from 0 to number of simulations requested
		for (int sim = 0; sim < model.getNumSimulationsToRun(); sim++)
		{
			ArrayList<ArrayList<Integer>> pool;
			
			if (model.getResamplingType().equals(ResamplingType.WITH_REPLACEMENT))
			{
				pool = performResamplingWithReplacement(currentIteration, model);
			}
			else if (model.getResamplingType().equals(ResamplingType.WITHOUT_REPLACEMENT))
			{
				pool = performResamplingWithoutReplacement(currentIteration, model);
			}
			else
			{
				throw new Exception("Unknown/unsupported resampling type used");
			}
			
			Integer[] firesByYear = getFiresByYear(model, pool, segment);
			int countOfFiresInSim = 0;
			for (int i = 0; i < firesByYear.length; i++)
			{
				if (firesByYear[i] > 0)
					countOfFiresInSim++;
			}
			
			firesPerCenturyPerSim.add(countOfFiresInSim * centuryMultiplier);
		}
		
		AnalysisResultsModel results = new AnalysisResultsModel(firesPerCenturyPerSim, segment, currentIteration);
		// log.debug("Calculating stats for n = " + currentIteration);
		
		try
		{
			SSIZController.analysisResults.add(results);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	/**
	 * TODO
	 * 
	 * @return
	 */
	public static ArrayList<AnalysisResultsModel> getAnalysisResults() {
		
		return analysisResults;
	}
	
	/**
	 * Restricts the series pool to contain only the years shared between all series.
	 */
	public static void restrictAnalysisToCommonYears(SSIZAnalysisModel model) {
		
		ArrayList<Integer> arrayOfFirstYears = getIntegerArrayOfFirstYears(model);
		ArrayList<Integer> arrayOfLastYears = getIntegerArrayOfLastYears(model);
		
		// Find the latest starting year of all series in the pool
		int latestStartingYear = model.getFirstYear();
		for (int i = 0; i < arrayOfFirstYears.size(); i++)
		{
			if (arrayOfFirstYears.get(i) > latestStartingYear)
				latestStartingYear = arrayOfFirstYears.get(i);
		}
		
		// Find the earliest ending year of all series in the pool
		int earliestEndingYear = model.getLastYear();
		for (int i = 0; i < arrayOfLastYears.size(); i++)
		{
			if (arrayOfLastYears.get(i) < earliestEndingYear)
				earliestEndingYear = arrayOfLastYears.get(i);
		}
		
		// Only update the starting and ending years in the model if at least one common year exists
		if (latestStartingYear <= earliestEndingYear)
		{
			model.setFirstYear(latestStartingYear);
			model.setLastYear(earliestEndingYear);
			
			// log.debug("Restricted to common years, FY: " + latestStartingYear + " LY: " + earliestEndingYear);
		}
		else
		{
			// log.debug("This file does not have any common years");
		}
	}
	
	/**
	 * Restricts the series pool to contain only series that have recorded at least one event.
	 */
	public static void restrictAnalysisToSeriesWithEvents(SSIZAnalysisModel model) {
		
		ArrayList<ArrayList<Integer>> currentSeriesPool = model.getSeriesPoolToAnalyze();
		ArrayList<ArrayList<Integer>> newSeriesPool = new ArrayList<ArrayList<Integer>>();
		
		ArrayList<Integer> indexesOfRemovedSeries = new ArrayList<Integer>();
		
		// Remove all series that do not contain any events
		for (int i = 0; i < currentSeriesPool.size(); i++)
		{
			ArrayList<Integer> series = currentSeriesPool.get(i);
			Boolean thisSeriesWasAdded = false;
			
			for (int j = 0; j < series.size(); j++)
			{
				if (series.get(j) == EVENT_RECORDED)
				{
					// Add the series and exit the loop
					newSeriesPool.add(series);
					thisSeriesWasAdded = true;
					j = series.size();
				}
			}
			
			if (!thisSeriesWasAdded)
			{
				// Keep track of which series are removed in this process
				indexesOfRemovedSeries.add(i);
			}
		}
		
		model.setSeriesPoolToAnalyize(newSeriesPool);
		
		// Only recalculate first and last years if at least one series was removed
		if (indexesOfRemovedSeries.size() > 0)
		{
			recalculateFirstAndLastYears(model, indexesOfRemovedSeries);
		}
		else
		{
			// log.debug("Every series in this file contains at least one event");
		}
	}
	
	/**
	 * Recalculate the first and last years of the series pool.
	 * 
	 * @param model
	 * @param indexesOfRemovedSeries
	 */
	private static void recalculateFirstAndLastYears(SSIZAnalysisModel model, ArrayList<Integer> indexesOfRemovedSeries) {
		
		ArrayList<Integer> arrayOfFirstYears = getIntegerArrayOfFirstYears(model);
		ArrayList<Integer> arrayOfLastYears = getIntegerArrayOfLastYears(model);
		
		int earliestStartingYear = model.getLastYear();
		int latestEndingYear = model.getFirstYear();
		
		for (int i = 0; i < model.getReader().getNumberOfSeries(); i++)
		{
			if (!indexesOfRemovedSeries.contains(i))
			{
				if (arrayOfFirstYears.get(i) < earliestStartingYear)
				{
					earliestStartingYear = arrayOfFirstYears.get(i);
				}
				if (arrayOfLastYears.get(i) > latestEndingYear)
				{
					latestEndingYear = arrayOfLastYears.get(i);
				}
			}
		}
		
		model.setFirstYear(earliestStartingYear);
		model.setLastYear(latestEndingYear);
		
		// log.debug("Restricted to series with events, FY: " + earliestStartingYear + " LY: " + latestEndingYear);
	}
	
	/**
	 * Converts the int[] of first years to an ArrayList<Integer> of first years.
	 * 
	 * @param model
	 * @return arrayOfFirstYears
	 */
	private static ArrayList<Integer> getIntegerArrayOfFirstYears(SSIZAnalysisModel model) {
		
		ArrayList<Integer> arrayOfFirstYears = new ArrayList<Integer>();
		
		for (int i = 0; i < model.getReader().getNumberOfSeries(); i++)
			arrayOfFirstYears.add(model.getReader().getStartYearPerSample()[i]);
			
		return arrayOfFirstYears;
	}
	
	/**
	 * Converts the int[] of last years to an ArrayList<Integer> of last years.
	 * 
	 * @param model
	 * @return arrayOfLastYears
	 */
	private static ArrayList<Integer> getIntegerArrayOfLastYears(SSIZAnalysisModel model) {
		
		ArrayList<Integer> arrayOfLastYears = new ArrayList<Integer>();
		
		for (int i = 0; i < model.getReader().getNumberOfSeries(); i++)
			arrayOfLastYears.add(model.getReader().getLastYearPerSample()[i]);
			
		return arrayOfLastYears;
	}
	
	/**
	 * Resample (with replacement) the event data from within the model so that it is of size numSamplesToChoose. It is possible that some
	 * series are represented more than once.
	 * 
	 * @param numSamplesToChoose
	 * @param model
	 * @return
	 */
	private static ArrayList<ArrayList<Integer>> performResamplingWithReplacement(int numSamplesToChoose, SSIZAnalysisModel model) {
		
		ArrayList<ArrayList<Integer>> newPool = new ArrayList<ArrayList<Integer>>();
		
		int sizeOfCompletePool = model.getSeriesPoolToAnalyze().size();
		
		for (int i = 1; i <= numSamplesToChoose; i++)
		{
			int randomIndex = (int) (model.getRandomGenerator(numSamplesToChoose - 1).nextDouble() * sizeOfCompletePool);
			ArrayList<Integer> singleSeries = model.getSeriesPoolToAnalyze().get(randomIndex);
			newPool.add(singleSeries);
		}
		
		return newPool;
	}
	
	/**
	 * Resample (without replacement) the event data from within the model so that it is of size numSamplesToChoose. Each series can only
	 * appear once. The contents of the array have the following meanings:
	 * 
	 * <ul>
	 * <li>-1 = no data (equivalent to a dot in the data file)</li>
	 * <li>1 = event (a letter in the data file)</li>
	 * <li>0 = susceptible to fire (recording) but no event detected (a pipe in the file)</li>
	 * </ul>
	 * 
	 * @param numSamplesToChoose
	 * @param model
	 * @return
	 */
	private static ArrayList<ArrayList<Integer>> performResamplingWithoutReplacement(int numSamplesToChoose, SSIZAnalysisModel model) {
		
		ArrayList<ArrayList<Integer>> tempPool = new ArrayList<ArrayList<Integer>>();
		ArrayList<ArrayList<Integer>> completeSeriesPool = model.getSeriesPoolToAnalyze();
		
		if (completeSeriesPool.size() < numSamplesToChoose)
			throw new ArrayIndexOutOfBoundsException("More samples requested than are in the complete pool");
			
		@SuppressWarnings("unchecked")
		ArrayList<ArrayList<Integer>> remainingSeriesToChooseFrom = (ArrayList<ArrayList<Integer>>) completeSeriesPool.clone();
		
		for (int i = 1; i <= numSamplesToChoose; i++)
		{
			int randomIndex = (int) (model.getRandomGenerator(numSamplesToChoose - 1).nextDouble() * remainingSeriesToChooseFrom.size());
			tempPool.add(remainingSeriesToChooseFrom.get(randomIndex));
			remainingSeriesToChooseFrom.remove(randomIndex);
		}
		
		return tempPool;
	}
}
