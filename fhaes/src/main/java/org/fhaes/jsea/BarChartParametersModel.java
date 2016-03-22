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
package org.fhaes.jsea;

import org.jfree.chart.JFreeChart;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * BarChartParametersModel Class.
 */
public class BarChartParametersModel {
	
	private static final Logger log = LoggerFactory.getLogger(JSEAFrame.class);
	
	private String title;
	private double[] meanByWindow;
	private int lengthOfWindow;
	private int yearsPriorOfEvent;
	private int yearsAfterEvent;
	private double[][] leftEndPointSim;
	private double[][] rightEndPointSim;
	private String outputFilePrefix;
	private int alphaLevel;
	private int segmentIndex;
	private int firstyear;
	private int lastyear;
	private int totalSegmentCount;
	
	// private static JFreeChart chart;
	
	/**
	 * TODO
	 * 
	 * @param title
	 * @param meanByWindow
	 * @param lengthOfWindow
	 * @param yearsPriorOfEvent
	 * @param yearsAfterEvent
	 * @param leftEndPointSim
	 * @param rightEndPointSim
	 * @param outputFilePrefix
	 * @param alphaLevel
	 * @param segmentIndex
	 */
	public BarChartParametersModel(String title, double[] meanByWindow, int lengthOfWindow, int yearsPriorOfEvent, int yearsAfterEvent,
			double[][] leftEndPointSim, double[][] rightEndPointSim, String outputFilePrefix, int alphaLevel, int segmentIndex,
			int totalSegmentCount, int firstyear, int lastyear) {
	
		this.title = title;
		this.meanByWindow = meanByWindow;
		this.lengthOfWindow = lengthOfWindow;
		this.yearsPriorOfEvent = yearsPriorOfEvent;
		this.yearsAfterEvent = yearsAfterEvent;
		this.leftEndPointSim = leftEndPointSim;
		this.rightEndPointSim = rightEndPointSim;
		this.outputFilePrefix = outputFilePrefix;
		this.alphaLevel = alphaLevel;
		this.segmentIndex = segmentIndex;
		this.firstyear = firstyear;
		this.lastyear = lastyear;
		this.totalSegmentCount = totalSegmentCount;
	}
	
	protected int getFirstYear() {
	
		return this.firstyear;
	}
	
	protected int getLastYear() {
	
		return this.lastyear;
	}
	
	protected String getTitle() {
	
		log.debug("Getting title for chart...");
		log.debug("Raw title is " + title);
		log.debug("Total segment count: " + totalSegmentCount);
		
		// if (title.contains("{segment}") && totalSegmentCount > 1)
		if (title.contains("{segment}"))
		{
			log.debug("Title contains {segment}");
			String years = firstyear + " - " + lastyear;
			title = title.replace("{segment}", years);
		}
		
		log.debug("Title is now " + title);
		
		title = title.replace("{segment}", "");
		
		log.debug("Final title is now " + title);
		
		return title;
		
	}
	
	protected double[] getMeanByWindow() {
	
		return meanByWindow;
	}
	
	protected int getLengthOfWindow() {
	
		return lengthOfWindow;
	}
	
	protected int getYearsPriorOfEvent() {
	
		return yearsPriorOfEvent;
	}
	
	protected int getYearsAfterEvent() {
	
		return yearsAfterEvent;
	}
	
	protected double[][] getLeftEndPointSim() {
	
		return leftEndPointSim;
	}
	
	protected double[][] getRightEndPointSim() {
	
		return rightEndPointSim;
	}
	
	protected String getOutputFilePrefix() {
	
		return outputFilePrefix;
	}
	
	protected int getAlphaLevel() {
	
		return alphaLevel;
	}
	
	protected int getSegmentIndex() {
	
		return segmentIndex;
	}
	
	protected JFreeChart getChart() {
	
		return JSEABarChart.createChart(getTitle(), getMeanByWindow(), getLengthOfWindow(), getYearsPriorOfEvent(), getYearsAfterEvent(),
				getLeftEndPointSim(), getRightEndPointSim(), getOutputFilePrefix(), getAlphaLevel(), getSegmentIndex());
	}
	
	/*
	 * protected void setChart(JFreeChart inChart) {
	 * 
	 * chart = inChart; }
	 */
}
