/*******************************************************************************
 * Copyright (C) 2014 Peter Brewer and Joshua Brogan
 * 
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 * 
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 * 
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *     
 *     Contributors:
 *     		Peter Brewer
 *     		Joshua Brogan
 ******************************************************************************/
package org.fhaes.fhsamplesize.model;

import java.util.ArrayList;
import java.util.Random;

import org.fhaes.enums.EventTypeToProcess;
import org.fhaes.enums.FireFilterType;
import org.fhaes.enums.ResamplingType;
import org.fhaes.fhfilereader.FHX2FileReader;
import org.fhaes.segmentation.SegmentModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * SSIZAnalysisModel Class.
 * 
 * @author Joshua Brogan and Peter Brewer
 */
public class SSIZAnalysisModel {

	private static final Logger log = LoggerFactory.getLogger(SSIZAnalysisModel.class);

	private final FHX2FileReader reader;
	private final EventTypeToProcess eventType;

	private final int seedValue;
	private int numSimulationsToRun;
	private int thresholdValue;
	private int firstYear;
	private int lastYear;

	private ResamplingType resamplingType;
	private FireFilterType thresholdType;

	private Random[] randomArray;

	private ArrayList<ArrayList<Integer>> seriesPoolToAnalyze;
	private ArrayList<SegmentModel> segments;

	/**
	 * Initializes the SSIZAnalysisModel according to the input parameters.
	 * 
	 * @param seedValue
	 * @param reader
	 * @param eventType
	 */
	public SSIZAnalysisModel(int seedValue, FHX2FileReader reader, EventTypeToProcess eventType) {

		this.seedValue = seedValue;
		this.eventType = eventType;
		this.seriesPoolToAnalyze = reader.getEventDataArrays(eventType);
		this.reader = reader;
		this.firstYear = reader.getFirstYear();
		this.lastYear = reader.getLastYear();
		populateRandomArray();
	}

	public FHX2FileReader getReader() {

		return reader;
	}

	/**
	 * Get the eventType e.g. fires, injuries or fires and injuries.
	 * 
	 * @return eventType
	 */
	public EventTypeToProcess getEventType() {

		return eventType;
	}

	/**
	 * TODO
	 * 
	 * @param segmentsFromTable
	 */
	public void setSegmentArray(ArrayList<SegmentModel> segmentsFromTable) {

		// Make new segmentModel array so that the segmentTable segments are not affected during exclusion
		ArrayList<SegmentModel> segments = new ArrayList<SegmentModel>();

		for (int i = 0; i < segmentsFromTable.size(); i++)
			segments.add(segmentsFromTable.get(i));

		if (segments == null || segments.size() == 0)
		{
			// Set to the full span of the file
			this.segments = new ArrayList<SegmentModel>();
			this.segments.add(new SegmentModel(getFirstYear(), getLastYear()));
		}
		else
		{
			// Set specific segments as requested
			this.segments = adjustSegmentYearBoundaries(excludeEmptySegments(segments));
		}
	}

	public ArrayList<SegmentModel> getSegments() {

		return this.segments;
	}

	/**
	 * Excludes all empty segments from the analysis.
	 * 
	 * @param segments
	 * @return a list of segments with the empty ones removed
	 */
	private ArrayList<SegmentModel> excludeEmptySegments(ArrayList<SegmentModel> segments) {

		for (int i = segments.size() - 1; i >= 0; i--)
		{
			SegmentModel thisSegment = segments.get(i);

			// This segment is completely before the year range of this model
			if (thisSegment.getFirstYear() <= firstYear && thisSegment.getLastYear() <= firstYear)
				segments.remove(i);

			// This segment is completely after the year range of this model
			else if (thisSegment.getFirstYear() >= lastYear && thisSegment.getLastYear() >= lastYear)
				segments.remove(i);
		}

		return segments;
	}

	/**
	 * Fixes the boundaries of the segments so that they are within the first and last years of the model.
	 * 
	 * @param segments
	 * @return a list of segments that have corrected year boundaries
	 */
	private ArrayList<SegmentModel> adjustSegmentYearBoundaries(ArrayList<SegmentModel> segments) {

		for (int i = 0; i < segments.size(); i++)
		{
			SegmentModel thisSegment = segments.get(i);

			// Adjust the segment's first year if it is below the model's range
			if (thisSegment.getFirstYear() < firstYear)
				thisSegment.setFirstYear(firstYear);

			// Adjust the segment's last year if it is above the model's range
			if (thisSegment.getLastYear() > lastYear)
				thisSegment.setLastYear(lastYear);
		}

		return segments;
	}

	/**
	 * TODO
	 */
	private void populateRandomArray() {

		// Populate Random array
		randomArray = new Random[reader.getNumberOfSeries()];

		for (int i = 0; i < reader.getNumberOfSeries(); i++)
		{
			Random rand = new Random();
			rand.setSeed(seedValue + i);
			randomArray[i] = rand;
		}
	}

	/**
	 * TODO
	 * 
	 * @param i
	 * @return
	 */
	public Random getRandomGenerator(int i) {

		try
		{
			return randomArray[i];
		}
		catch (IndexOutOfBoundsException e)
		{
			return null;
		}
	}

	/**
	 * Sets numSimulationsToRun equal to the input value.
	 * 
	 * @param inValue
	 */
	public void setNumSimulationsToRun(int inValue) {

		numSimulationsToRun = inValue;
	}

	public int getNumSimulationsToRun() {

		return this.numSimulationsToRun;
	}

	/**
	 * Sets thresholdValue equal to the input value.
	 * 
	 * @param inValue
	 */
	public void setThresholdValue(int inValue) {

		thresholdValue = inValue;
	}

	public int getThresholdValue() {

		return this.thresholdValue;
	}

	/**
	 * Sets the thresholdType according to the input value.
	 * 
	 * @param b
	 */
	public void setThresholdType(FireFilterType b) {

		log.debug("Setting threshold type to " + b);
		thresholdType = b;
	}

	public FireFilterType getThresholdType() {

		return this.thresholdType;
	}

	/**
	 * Sets the resampleWithReplacement flag according to the input value.
	 * 
	 * @param b
	 */
	public void setResamplingType(ResamplingType b) {

		resamplingType = b;
	}

	public ResamplingType getResamplingType() {

		return this.resamplingType;
	}

	/**
	 * Sets the firstYear equal to the input value.
	 * 
	 * @param newFirstYear
	 */
	public void setFirstYear(Integer newFirstYear) {

		firstYear = newFirstYear;
	}

	public Integer getFirstYear() {

		return this.firstYear;
	}

	/**
	 * Sets the lastYear equal to the input value.
	 * 
	 * @param newLastYear
	 */
	public void setLastYear(Integer newLastYear) {

		lastYear = newLastYear;
	}

	public Integer getLastYear() {

		return this.lastYear;
	}

	/**
	 * Updates the contents of seriesPoolToAnalyze to those of the input array.
	 */
	public void setSeriesPoolToAnalyize(ArrayList<ArrayList<Integer>> updatedSeriesPool) {

		this.seriesPoolToAnalyze = updatedSeriesPool;
	}

	/**
	 * Returns a double array list containing the series pool to be analyzed (with selected restrictions applied).
	 * 
	 * @return seriesPoolToAnalyze
	 */
	public ArrayList<ArrayList<Integer>> getSeriesPoolToAnalyze() {

		return this.seriesPoolToAnalyze;
	}
}
