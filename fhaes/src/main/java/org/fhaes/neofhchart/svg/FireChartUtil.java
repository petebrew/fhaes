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
import java.awt.Font;
import java.awt.FontMetrics;
import java.util.ArrayList;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.fhaes.model.FHSeries;
import org.fhaes.neofhchart.FHSeriesSVG;
import org.fhaes.preferences.App;
import org.fhaes.preferences.FHAESPreferences.PrefKey;

/**
 * FireChartConversions Class.
 * 
 * @author Joshua Brogan and Peter Brewer
 */
public class FireChartUtil {
	
	/**
	 * Converts an array list of FHseries objects to an array list of FHSeriesSVG objects.
	 * 
	 * @param seriesList
	 * @return the converted list
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
	 * Converts a java.awt.Color into a hexadecimal string.
	 * 
	 * @param color
	 * @return the converted color value
	 */
	protected static String colorToHexString(Color color) {
		
		if (color == null)
		{
			return null;
		}
		
		return "#" + Integer.toHexString(color.getRGB()).substring(2);
	}
	
	/**
	 * Get an approximate height for a string with the specified font. The should really be taken from the SVG but I haven't worked out a
	 * good way to do this without rendering first.
	 * 
	 * @param fontSize
	 * @param fontStyle
	 * @param text
	 * @return string height
	 */
	protected static Integer getStringHeight(int fontStyle, int fontSize, String text) {
		
		Font font = new Font(App.prefs.getPref(PrefKey.CHART_FONT_FAMILY, "Verdana"), fontStyle, fontSize);
		
		JComponent graphics = new JPanel();
		FontMetrics metrics = graphics.getFontMetrics(font);
		return metrics.getMaxAscent();
	}
	
	/**
	 * Get an approximate width for a string with the specified font. The should really be taken from the SVG but I haven't worked out a
	 * good way to do this without rendering first.
	 * 
	 * @param fontSize
	 * @param fontStyle
	 * @param text
	 * @return string width
	 */
	protected static Integer getStringWidth(int fontStyle, int fontSize, String text) {
		
		Font font = new Font(App.prefs.getPref(PrefKey.CHART_FONT_FAMILY, "Verdana"), fontStyle, fontSize);
		
		JComponent graphics = new JLabel(text);
		FontMetrics metrics = graphics.getFontMetrics(font);
		return metrics.stringWidth(text);
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
