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
	
	// Define local constants
	private static final Color MS_OFFICE_BLUE = new Color(75, 172, 198);
	private static final Color MS_OFFICE_GREEN = new Color(155, 187, 89);
	private static final Color MS_OFFICE_ORANGE = new Color(247, 150, 70);
	private static final Color MS_OFFICE_PURPLE = new Color(128, 100, 162);
	private static final Color MS_OFFICE_RED = new Color(192, 80, 77);
	
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
	 * Converts a color into a hexadecimal string.
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
	 * Picks a display color based on the input integer value. This is used to automatically colorize series and labels which have been
	 * grouped by category on the chart.
	 * 
	 * @param num
	 * @return a color based on the input integer value
	 */
	protected static Color pickColorFromInteger(int num) {
		
		int lastDigitOfInteger = num % 10;
		
		if (lastDigitOfInteger == 0 || lastDigitOfInteger == 5)
		{
			return MS_OFFICE_GREEN;
		}
		else if (lastDigitOfInteger == 1 || lastDigitOfInteger == 6)
		{
			return MS_OFFICE_PURPLE;
		}
		else if (lastDigitOfInteger == 2 || lastDigitOfInteger == 7)
		{
			return MS_OFFICE_BLUE;
		}
		else if (lastDigitOfInteger == 3 || lastDigitOfInteger == 8)
		{
			return MS_OFFICE_ORANGE;
		}
		else
		{
			return MS_OFFICE_RED;
		}
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
	 * @return a converted value
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
	 * @return a converted value
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
	 * @return a converted value
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
	 * @return a converted value
	 */
	protected static double yearsToPixels(int chartWidth, int firstChartYear, int lastChartYear) {
		
		return yearsToPixels(1.0, chartWidth, firstChartYear, lastChartYear);
	}
}
