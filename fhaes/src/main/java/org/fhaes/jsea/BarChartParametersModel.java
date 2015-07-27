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

/**
 * BarChartParametersModel Class.
 */
public class BarChartParametersModel {
	
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
	
	private static JFreeChart chart;
	
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
			double[][] leftEndPointSim, double[][] rightEndPointSim, String outputFilePrefix, int alphaLevel, int segmentIndex) {
			
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
	}
	
	protected String getTitle() {
		
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
		
		return chart;
	}
	
	protected void setChart(JFreeChart inChart) {
		
		chart = inChart;
	}
}
