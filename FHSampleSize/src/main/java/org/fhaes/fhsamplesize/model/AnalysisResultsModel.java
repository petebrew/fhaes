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
package org.fhaes.fhsamplesize.model;

import java.util.ArrayList;

import org.apache.commons.math.stat.descriptive.DescriptiveStatistics;
import org.fhaes.math.Weibull;
import org.fhaes.segmentation.SegmentModel;

/**
 * AnalysisResultsModel Class.
 * 
 * @author Joshua Brogan and Peter Brewer
 */
public class AnalysisResultsModel {
	
	private final int numberOfSamples;
	private final SegmentModel segment;
	private final Double meanEventsPerCentury;
	private final Double std;
	private final Double median;
	private final Double CI95;
	private final Double CI99;
	private final Double weibullMean;
	private final Double weibullMedian;
	private final Double weibullCI95Lower;
	private final Double weibullCI95Upper;
	private final Double weibullCI99;
	
	private static final double STDEV_MULTIPLIER_FOR_95 = 1.960;
	private static final double STDEV_MULTIPLIER_FOR_99 = 2.575;
	// private static final double STDEV_MULTIPLIER_FOR_99_POINT_9 = 3.294;
	
	private final DescriptiveStatistics stats;
	
	public AnalysisResultsModel(ArrayList<Double> firesPerCenturyPerSim, SegmentModel segment, int numberOfSamples) {
		
		this.segment = segment;
		this.numberOfSamples = numberOfSamples;
		
		// Generate Apache Commons descriptive statistics
		stats = new DescriptiveStatistics();
		for (Double val : firesPerCenturyPerSim)
		{
			stats.addValue(val);
		}
		
		meanEventsPerCentury = stats.getMean();
		std = stats.getStandardDeviation();
		median = stats.getPercentile(50);
		CI95 = STDEV_MULTIPLIER_FOR_95 * std;
		CI99 = STDEV_MULTIPLIER_FOR_99 * std;
		
		// Generate Weibull stats
		Weibull weibull = new Weibull(firesPerCenturyPerSim);
		weibullMean = weibull.getMean();
		weibullMedian = weibull.getMedian();
		
		// TODO Elena to check
		weibullCI95Lower = weibull.getExceedencePercentile(5.0);
		weibullCI95Upper = weibull.getExceedencePercentile(95.0);
		
		weibullCI99 = weibull.getExceedencePercentile(99.0) - weibullMedian;
	}
	
	public int getNumberOfSamples() {
		
		return this.numberOfSamples;
	}
	
	public SegmentModel getSegment() {
		
		return this.segment;
	}
	
	public Double getMean() {
		
		return this.meanEventsPerCentury;
	}
	
	public Double getStandardDeviation() {
		
		return this.std;
	}
	
	public Double getMedian() {
		
		return this.median;
	}
	
	public Double getWeibullMean() {
		
		return this.weibullMean;
	}
	
	public Double getWeibullMedian() {
		
		return this.weibullMedian;
	}
	
	public Double getConfidenceInterval95() {
		
		return this.CI95;
	}
	
	public Double getConfidenceInterval99() {
		
		return this.CI99;
	}
	
	public Double getWeibullConfidenceInterval95Lower() {
		
		return this.weibullCI95Lower;
	}
	
	public Double getWeibullConfidenceInterval95Upper() {
		
		return this.weibullCI95Upper;
	}
	
	public Double getWeibullConfidenceInterval99() {
		
		return this.weibullCI99;
	}
	
}
