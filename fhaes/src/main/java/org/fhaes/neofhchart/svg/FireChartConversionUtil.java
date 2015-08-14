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
package org.fhaes.neofhchart.svg;

import java.awt.Color;
import java.util.ArrayList;

import org.fhaes.model.FHSeries;
import org.fhaes.neofhchart.FHSeriesSVG;

/**
 * FireChartConversionUtil Class.
 * 
 * @author Joshua Brogan and Peter Brewer
 */
public class FireChartConversionUtil {
	
	/**
	 * Converts an array list of FHseries objects to an array list of FHSeriesSVG objects.
	 * 
	 * @param seriesList
	 * @return
	 */
	protected static ArrayList<FHSeriesSVG> seriesListToSeriesSVGList(ArrayList<FHSeries> seriesList) {
		
		ArrayList<FHSeriesSVG> seriesSVGList = new ArrayList<FHSeriesSVG>();
		
		for (FHSeries series : seriesList)
		{
			try
			{
				seriesSVGList.add(new FHSeriesSVG(series));
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
		
		return seriesSVGList;
	}
	
	/**
	 * Converts a java.awt.Color to a hex string.
	 * 
	 * @param color
	 * @return
	 */
	protected static String colorToHexString(Color color) {
		
		if (color == null)
		{
			return null;
		}
		
		return "#" + Integer.toHexString(color.getRGB()).substring(2);
	}
	
	/**
	 * Performs the inverse of yearsToPixels.
	 * 
	 * @param dim
	 * @param chartWidth
	 * @param firstChartYear
	 * @param lastChartYear
	 * @return
	 */
	protected static double pixelsToYears(double dim, int chartWidth, int firstChartYear, int lastChartYear) {
		
		return dim * 1 / yearsToPixels(chartWidth, firstChartYear, lastChartYear);
	}
	
	/**
	 * Convenience function to get the scaling factor.
	 * 
	 * @param chartWidth
	 * @param firstChartYear
	 * @param lastChartYear
	 * @return
	 */
	protected static double pixelsToYears(int chartWidth, int firstChartYear, int lastChartYear) {
		
		return pixelsToYears(1.0, chartWidth, firstChartYear, lastChartYear);
	}
	
	/**
	 * Converts dim from years to pixels based off of the chart width and how many years are in the reader.
	 * 
	 * @param dim
	 * @param chartWidth
	 * @param firstChartYear
	 * @param lastChartYear
	 * @return
	 */
	protected static double yearsToPixels(double dim, int chartWidth, int firstChartYear, int lastChartYear) {
		
		return dim * chartWidth / (lastChartYear - firstChartYear);
	}
	
	/**
	 * Convenience function to get the scaling factor.
	 * 
	 * @param chartWidth
	 * @param firstChartYear
	 * @param lastChartYear
	 * @return
	 */
	protected static double yearsToPixels(int chartWidth, int firstChartYear, int lastChartYear) {
		
		return yearsToPixels(1.0, chartWidth, firstChartYear, lastChartYear);
	}
}
