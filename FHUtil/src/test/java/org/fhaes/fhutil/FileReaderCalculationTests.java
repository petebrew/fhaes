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
package org.fhaes.fhutil;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;

import junit.framework.TestCase;

import org.fhaes.enums.EventTypeToProcess;
import org.fhaes.enums.FireFilterType;
import org.fhaes.enums.SampleDepthFilterType;
import org.fhaes.fhfilereader.FHFile;
import org.fhaes.fhfilereader.FHX2FileReader;
import org.fhaes.model.FHSeries;
import org.fhaes.preferences.App;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * FileReaderCalculationTests Class. Unit test for simple App.
 */
public class FileReaderCalculationTests extends TestCase {
	
	// Declare logger
	private static final Logger log = LoggerFactory.getLogger(FileReaderCalculationTests.class);
	
	public FileReaderCalculationTests() {
	
		App.init();
	}
	
	/**
	 * TODO
	 * 
	 * @param folder
	 * @return
	 */
	private File[] getFilesFromFolder(String folder) {
	
		File dir = new File(folder);
		FilenameFilter filter = new FilenameFilter() {
			
			@Override
			public boolean accept(File dir, String name) {
			
				return !name.startsWith(".");
			}
		};
		String[] strarr = dir.list(filter);
		
		File[] fileList = new File[strarr.length];
		
		for (int i = 0; i < strarr.length; i++)
		{
			fileList[i] = new File(folder + File.separator + strarr[i]);
		}
		
		return fileList;
	}
	
	/**
	 * Test the access to the data required to plot the FHChart index plot.
	 */
	public void testGetFireIndexPlotData() {
	
		// Parameters for testing
		String folder = "TestData/";
		File[] files = getFilesFromFolder(folder);
		
		if (files.length == 0)
		{
			fail();
		}
		
		for (File file : files)
		{
			log.debug("Get fire index plot data for: " + file);
			
			EventTypeToProcess eventTypeToProcess = EventTypeToProcess.FIRE_EVENT;
			
			FHFile f = new FHFile(file);
			FHX2FileReader fr = new FHX2FileReader(f);
			
			int firstyear = fr.getFirstYear();
			int[] sampledepths = fr.getSampleDepths();
			int[] recordingDepths = fr.getRecordingDepths(eventTypeToProcess);
			double[] percentScarred = fr.getPercentOfAllScarred(eventTypeToProcess);
			
			int currentyear = firstyear;
			log.debug("YEAR, SAMPLE DEPTH, RECORDING DEPTH,  PERCENT ALL SCARRED ");
			for (int i = 0; i < sampledepths.length; i++)
			{
				log.debug(currentyear + ", " + sampledepths[i] + ", " + recordingDepths[i] + ", " + +percentScarred[i]);
				currentyear++;
			}
		}
	}
	
	/**
	 * Test access to the data required for the FHChart chronology plot.
	 */
	public void testGetChronologyPlotData() {
	
		// Parameters for testing
		String folder = "TestData/";
		File[] files = getFilesFromFolder(folder);
		
		if (files.length == 0)
		{
			fail();
		}
		
		for (File file : files)
		{
			FHFile f = new FHFile(file);
			FHX2FileReader fr = new FHX2FileReader(f);
			
			ArrayList<FHSeries> seriesList = fr.getSeriesList();
			
			log.debug("File contains " + seriesList.size() + " series");
			
			for (FHSeries series : seriesList)
			{
				log.debug("Series          : " + series.getTitle());
				log.debug("   First year   : " + series.getFirstYear());
				log.debug("   Last year    : " + series.getLastYear());
				log.debug("   Series length: " + series.getLength());
				
				// boolean[] eventYears = series.getEventYears();
				// boolean[] injuryYears = series.getInjuryYears();
				// boolean[] recorderYears = series.getRecordingYears();
			}
		}
	}
	
	/**
	 * Get a list of years that match the composite filter. This data is typically used in the FHChart composite plot.
	 */
	public void testGetCompositeFireYears() {
	
		// Parameters for testing
		String folder = "TestData/";
		File[] files = getFilesFromFolder(folder);
		
		if (files.length == 0)
		{
			fail();
		}
		
		for (File file : files)
		{
			// Parameters for testing
			FireFilterType filterType = FireFilterType.PERCENTAGE_OF_RECORDING;
			EventTypeToProcess eventType = EventTypeToProcess.FIRE_AND_INJURY_EVENT;
			Double filterValue = 80.0;
			Integer minNumberOfSamples = 1;
			
			FHFile f = new FHFile(file);
			FHX2FileReader fr = new FHX2FileReader(f);
			ArrayList<Integer> fireYears = fr.getCompositeFireYears(eventType, filterType, filterValue, minNumberOfSamples,
					SampleDepthFilterType.MIN_NUM_RECORDER_SAMPLES);
			
			if (fireYears.size() > 0)
			{
				log.debug("Total number of composite fire years = " + fireYears.size());
			}
			else
			{
				log.debug("No composite fire years match criteria in this file");
				return;
			}
			
			// Debug print each value
			int ind = 1;
			for (Integer value : fireYears)
			{
				log.debug("Composite year " + ind + " = " + value);
				ind++;
			}
		}
	}
}
