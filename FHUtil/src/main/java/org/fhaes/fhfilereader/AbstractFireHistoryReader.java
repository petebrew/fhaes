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
package org.fhaes.fhfilereader;

import java.util.ArrayList;

import org.fhaes.enums.EventTypeToProcess;
import org.fhaes.enums.FireFilterType;
import org.fhaes.enums.SampleDepthFilterType;
import org.fhaes.model.FHSeries;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * AbstractFireHistoryReader Class. This abstract class provides methods that calculate basic data from the underlying raw data provided by
 * the implementing classes.
 * 
 * @author Peter Brewer
 */
public abstract class AbstractFireHistoryReader implements IFHAESReader {
	
	private static final Logger log = LoggerFactory.getLogger(AbstractFireHistoryReader.class);
	
	protected ArrayList<FHSeries> seriesList;
	private boolean needToPopulateSeriesList = true;
	
	/**
	 * Construct a list of FHSeries, one for each series in the file.
	 */
	protected abstract void populateSeriesList();
	
	/**
	 * Replaces the existing series list with the input list. Note that directly modifying the series list means its contents will differ
	 * from the source file on the disk.
	 * 
	 * @param seriesList
	 */
	public void replaceSeriesList(ArrayList<FHSeries> inSeriesList) {
	
		seriesList.clear();
		seriesList.addAll(inSeriesList);
	}
	
	/**
	 * Get an ArrayList of FHSeries classes, each representing a single series in the data file.
	 * 
	 * @return
	 */
	public ArrayList<FHSeries> getSeriesList() {
	
		if (needToPopulateSeriesList)
		{
			populateSeriesList();
			needToPopulateSeriesList = false;
		}
		
		return seriesList;
	}
	
	/**
	 * Returns an array of ints with size equal to the number of years in the file. The values in the array are the number trees for each
	 * year in the file (regardless of recording status), starting with the earliest, and finishing with the most recent year.
	 * 
	 * @return
	 */
	public int[] getSampleDepths() {
	
		// Instantiate the array ready to populate
		int[] arr = new int[this.getYearArray().size()];
		int totaltreeperyear = 0;
		
		ArrayList<ArrayList<Integer>> data = this.getRecorderYears2DArray(EventTypeToProcess.FIRE_EVENT);
		
		if (data == null)
			return null;
		
		for (int yearIndex = 0; yearIndex < data.get(0).size(); yearIndex++)
		{
			totaltreeperyear = 0;
			for (int sampleIndex = 0; sampleIndex < this.getNumberOfSeries(); sampleIndex++)
			{
				try
				{
					if ((data.get(sampleIndex).get(yearIndex) == 1) || (data.get(sampleIndex).get(yearIndex) == 0))
					{
						totaltreeperyear = totaltreeperyear + 1;
					}
				}
				catch (IndexOutOfBoundsException e)
				{
					log.error("Failed on Year index " + yearIndex + " and sample " + sampleIndex);
				}
			}
			arr[yearIndex] = totaltreeperyear;
		}
		
		return arr;
		
	}
	
	/**
	 * Returns an array of ints with size equal to the number of years in the file. The values in the array are the number of samples that
	 * are in recording status for each year in the file, starting with the earliest, and finishing with the most recent year.
	 * 
	 * @return
	 */
	public int[] getRecordingDepths(EventTypeToProcess eventTypeToProcess) {
	
		// Instantiate the array ready to populate
		int[] arr = new int[this.getYearArray().size()];
		int totalrecorderperyear;
		
		ArrayList<ArrayList<Integer>> data = this.getRecorderYears2DArray(eventTypeToProcess);
		
		for (int j = 0; j < this.getYearArray().size(); j++)
		{
			totalrecorderperyear = 0;
			for (int k = 0; k < this.getNumberOfSeries(); k++)
			{
				if (data.get(k).get(j) == 1)
				{
					totalrecorderperyear = totalrecorderperyear + 1;
				}
			}
			arr[j] = totalrecorderperyear;
		}
		
		return arr;
		
	}
	
	/**
	 * Returns an array of doubles with size equal to the number of years in the file. The values in the array are the percentage of samples
	 * that are in recording status and which have an event. The type of event (fire, injury or both) is specified as a parameter.
	 * 
	 * @param eventTypeToProcess - whether to calculate the percentage based on fire events, injury events, or both.
	 * @return
	 */
	public double[] getPercentOfRecordingScarred(EventTypeToProcess eventTypeToProcess) {
	
		// Instantiate the array ready to populate
		double[] arrpc = new double[this.getYearArray().size()];
		int[] arr1 = new int[this.getYearArray().size()];
		
		for (int j = 0; j < this.getYearArray().size(); j++)
		{
			int totalrecorderperyear = 0;
			for (int k = 0; k < this.getNumberOfSeries(); k++)
			{
				if (this.getRecorderYears2DArray(eventTypeToProcess).get(k).get(j) == 1)
				{
					totalrecorderperyear = totalrecorderperyear + 1;
				}
			}
			arr1[j] = totalrecorderperyear;
			
			if (arr1[j] != 0)
			{
				arrpc[j] = (this.getFilterArrays(eventTypeToProcess).get(0).get(j) / arr1[j]) * 100;
			}
			else
			{
				arrpc[j] = 0.0;
			}
		}
		
		return arrpc;
	}
	
	/**
	 * Returns an array of doubles with size equal to the number of years in the file. The values in the array are the percentage of samples
	 * that are present in that specific year. The type of event (fire, injury or both) is specified as a parameter.
	 * 
	 * @param eventTypeToProcess - whether to calculate the percentage based on fire events, injury events, or both.
	 * @return
	 */
	public double[] getPercentOfAllScarred(EventTypeToProcess eventTypeToProcess) {
	
		// Instantiate the array ready to populate
		double[] arrpc = new double[this.getYearArray().size()];
		int[] arr1 = getSampleDepths();
		
		for (int j = 0; j < this.getYearArray().size(); j++)
		{
			if (arr1[j] != 0)
			{
				arrpc[j] = (this.getFilterArrays(eventTypeToProcess).get(0).get(j) / arr1[j]) * 100;
			}
			else
			{
				arrpc[j] = 0.0;
			}
		}
		
		return arrpc;
	}
	
	/**
	 * Returns an ArrayList of years which fulfill the composite filter options specified.
	 * 
	 * @param eventTypeToProcess - whether to calculate the composite based on fire events, injury events, or both
	 * @param filterType - whether to filter on absolute numbers of fires, or percentage of recording trees
	 * @param filterValue - either the number of samples or percentage of samples that must be scared before the year is included in the
	 *            composite
	 * @param minNumberOfSamples - The minimum number of samples that must be in recording status for the year to be considered for
	 *            inclusion in the composite, regardless of filterValue.
	 * @param sampleDepthFilterType
	 * @return
	 */
	public ArrayList<Integer> getCompositeFireYears(EventTypeToProcess eventTypeToProcess, FireFilterType filterType, double filterValue,
			int minNumberOfSamples, SampleDepthFilterType sampleDepthFilterType) {
	
		// Instantiate the array ready to populate
		ArrayList<Integer> compositeYears = new ArrayList<Integer>();
		
		// Create filter based on min number of samples/recorder samples
		int[] depths = null;
		if (sampleDepthFilterType.equals(SampleDepthFilterType.MIN_NUM_SAMPLES))
		{
			log.debug("Minimum sample count filter is of type SampleDepthFilterType.MIN_NUM_SAMPLES");
			depths = getSampleDepths();
		}
		else if (sampleDepthFilterType.equals(SampleDepthFilterType.MIN_NUM_RECORDER_SAMPLES))
		{
			log.debug("Minimum sample count filter is of type SampleDepthFilterType.MIN_NUM_RECORDER_SAMPLES");
			depths = getRecordingDepths(eventTypeToProcess);
		}
		else
		{
			log.error("Unknown sample depth filter type.");
			return null;
		}
		
		// Calculate composite based on filter type
		if (filterType.equals(FireFilterType.NUMBER_OF_EVENTS))
		{
			
			ArrayList<Double> dataArray = this.getFilterArrays(eventTypeToProcess).get(0);
			
			Integer currentYear = this.getFirstYear();
			for (int i = 0; i < dataArray.size(); i++)
			{
				if (dataArray.get(i) >= filterValue)
				{
					
					if (depths[i] >= minNumberOfSamples)
					{
						compositeYears.add(currentYear);
						// log.trace("Keeping " + currentYear + ": ");
					}
					else
					{
						// log.debug("Rejected because sample count of " + depths[i] + " is below threshold of " + minNumberOfSamples);
						
					}
					
				}
				
				currentYear++;
			}
			
		}
		else if (filterType.equals(FireFilterType.PERCENTAGE_OF_ALL_TREES))
		{
			// log.debug("Doing all trees");
			
			double[] percentScarred = getPercentOfAllScarred(eventTypeToProcess);
			
			Integer currentYear = this.getFirstYear();
			for (int i = 0; i < percentScarred.length; i++)
			{
				if (percentScarred[i] >= filterValue)
				{
					if (depths[i] >= minNumberOfSamples)
					{
						compositeYears.add(currentYear);
						// log.debug("Keeping   " + currentYear + ": percentofallscarred = " + percentScarred[i] + "; number of trees = "
						// + numberOfTrees.get(i) + "; number of events = " + numberOfEvents.get(i));
					}
					else
					{
						// log.debug("Rejected because sample count of " + depths[i] + " is below threshold of " + minNumberOfSamples);
					}
				}
				else
				{
					// log.debug("Rejecting " + currentYear + ": percentofallscarred = " + percentScarred[i] + "; number of trees = "
					// + numberOfTrees.get(i) + "; number of events = " + numberOfEvents.get(i));
				}
				
				currentYear++;
			}
			
		}
		else if (filterType.equals(FireFilterType.PERCENTAGE_OF_RECORDING))
		{
			// log.debug("Doing recording trees");
			
			ArrayList<Double> numberOfEvents = this.getFilterArrays(eventTypeToProcess).get(0);
			int[] recordingDepths = this.getRecordingDepths(eventTypeToProcess);
			
			Integer currentYear = this.getFirstYear();
			for (int i = 0; i < recordingDepths.length; i++)
			{
				double datavalue = 0.0;
				try
				{
					datavalue = (numberOfEvents.get(i) / recordingDepths[i]) * 100;
				}
				catch (Exception e)
				{
					log.debug("Rejected " + currentYear + " because of calc error");
					continue;
					
				}
				if (datavalue >= filterValue)
				{
					if (depths[i] >= minNumberOfSamples)
					{
						compositeYears.add(currentYear);
						// log.debug("Keeping   " + currentYear + ": percentofrecscarred = " + datavalue + "; number of events = "
						// + numberOfEvents.get(i) + "; number of recording trees = " + recordingDepths[i]);
					}
					else
					{
						// log.debug("Rejected because sample count of " + depths[i] + " is below threshold of " + minNumberOfSamples);
					}
				}
				else
				{
					// log.debug("Rejecting " + currentYear + ": percentofrecscarred = " + datavalue + "; number of events = "
					// + numberOfEvents.get(i) + "; number of recording trees = " + recordingDepths[i]);
				}
				currentYear++;
			}
			
		}
		else
		{
			log.error("Unsupported FireFilterType");
			return null;
		}
		
		return compositeYears;
	}
}
