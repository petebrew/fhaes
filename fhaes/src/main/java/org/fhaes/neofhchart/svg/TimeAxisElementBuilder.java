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

import org.apache.batik.dom.svg.SVGDOMImplementation;
import org.fhaes.enums.LineStyle;
import org.fhaes.preferences.App;
import org.fhaes.preferences.FHAESPreferences.PrefKey;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;

/**
 * TimeAxisBuilder Class. This class is used to construct the SVG elements necessary for drawing the time axis display.
 * 
 * @author Joshua Brogan and Peter Brewer
 */
public class TimeAxisElementBuilder {
	
	// Declare local constants
	private static final int TICK_HEIGHT = 10;
	
	/**
	 * Returns a time axis element based on the input parameters.
	 * 
	 * @param doc
	 * @param height
	 * @param firstChartYear
	 * @param lastChartYear
	 * @return timeAxis
	 */
	protected static Element getTimeAxis(Document doc, int height, int firstChartYear, int lastChartYear) {
		
		Element timeAxis = doc.createElementNS(SVGDOMImplementation.SVG_NAMESPACE_URI, "line");
		
		timeAxis.setAttributeNS(null, "x1", firstChartYear + "");
		timeAxis.setAttributeNS(null, "x2", lastChartYear + "");
		timeAxis.setAttributeNS(null, "y1", Integer.toString(height - (2 * FireChartSVG.SERIES_HEIGHT)));
		timeAxis.setAttributeNS(null, "y2", Integer.toString(height - (2 * FireChartSVG.SERIES_HEIGHT)));
		timeAxis.setAttributeNS(null, "stroke-dasharray", LineStyle.SOLID.getCode());
		timeAxis.setAttributeNS(null, "stroke", FireChartUtil.colorToHexString(Color.BLACK));
		
		return timeAxis;
	}
	
	/**
	 * Returns a major tick element based on the input parameters.
	 * 
	 * @param doc
	 * @param yearPosition
	 * @param chartWidth
	 * @param height
	 * @param firstChartYear
	 * @param lastChartYear
	 * @return majorTick
	 */
	protected static Element getMajorTick(Document doc, int yearPosition, int chartWidth, int height, int firstChartYear,
			int lastChartYear) {
			
		Element majorTick = doc.createElementNS(SVGDOMImplementation.SVG_NAMESPACE_URI, "line");
		
		majorTick.setAttributeNS(null, "x1", Integer.toString(yearPosition));
		majorTick.setAttributeNS(null, "x2", Integer.toString(yearPosition));
		majorTick.setAttributeNS(null, "y1", Integer.toString(height - (2 * TICK_HEIGHT)));
		majorTick.setAttributeNS(null, "y2", Integer.toString(height - (TICK_HEIGHT)));
		majorTick.setAttributeNS(null, "stroke-width", Double.toString(App.prefs.getIntPref(PrefKey.CHART_VERTICAL_GUIDE_WEIGHT, 1)
				* FireChartUtil.pixelsToYears(chartWidth, firstChartYear, lastChartYear)));
		majorTick.setAttributeNS(null, "stroke-dasharray", LineStyle.SOLID.getCode());
		majorTick.setAttributeNS(null, "stroke", FireChartUtil.colorToHexString(Color.BLACK));
		
		return majorTick;
	}
	
	/**
	 * Returns a minor tick element based on the input parameters.
	 * 
	 * @param doc
	 * @param yearPosition
	 * @param chartWidth
	 * @param height
	 * @param firstChartYear
	 * @param lastChartYear
	 * @return minorTick
	 */
	protected static Element getMinorTick(Document doc, int yearPosition, int chartWidth, int height, int firstChartYear,
			int lastChartYear) {
			
		Element minorTick = doc.createElementNS(SVGDOMImplementation.SVG_NAMESPACE_URI, "line");
		
		minorTick.setAttributeNS(null, "x1", Integer.toString(yearPosition));
		minorTick.setAttributeNS(null, "x2", Integer.toString(yearPosition));
		minorTick.setAttributeNS(null, "y1", Integer.toString(height - (2 * TICK_HEIGHT)));
		minorTick.setAttributeNS(null, "y2", Double.toString(height - (1.5 * TICK_HEIGHT)));
		minorTick.setAttributeNS(null, "stroke-width", Double.toString(App.prefs.getIntPref(PrefKey.CHART_VERTICAL_GUIDE_WEIGHT, 1)
				* FireChartUtil.pixelsToYears(chartWidth, firstChartYear, lastChartYear)));
		minorTick.setAttributeNS(null, "stroke-dasharray", LineStyle.SOLID.getCode());
		minorTick.setAttributeNS(null, "stroke", FireChartUtil.colorToHexString(Color.BLACK));
		
		return minorTick;
	}
	
	/**
	 * Returns a highlight line element based on the input parameters.
	 * 
	 * @param doc
	 * @param yearPosition
	 * @param chartWidth
	 * @param height
	 * @param tickHeight
	 * @param firstChartYear
	 * @param lastChartYear
	 * @return highlightLine
	 */
	protected static Element getHighlightLine(Document doc, int yearPosition, int chartWidth, int height, int firstChartYear,
			int lastChartYear) {
			
		Element highlightLine = doc.createElementNS(SVGDOMImplementation.SVG_NAMESPACE_URI, "line");
		
		highlightLine.setAttributeNS(null, "x1", Integer.toString(yearPosition));
		highlightLine.setAttributeNS(null, "x2", Integer.toString(yearPosition));
		highlightLine.setAttributeNS(null, "y1", "0");
		highlightLine.setAttributeNS(null, "y2", Double.toString(height - (2 * TICK_HEIGHT)));
		highlightLine.setAttributeNS(null, "stroke-width", Double.toString(App.prefs.getIntPref(PrefKey.CHART_HIGHLIGHT_YEARS_WEIGHT, 1)
				* FireChartUtil.pixelsToYears(chartWidth, firstChartYear, lastChartYear)));
		highlightLine.setAttributeNS(null, "stroke-dasharray",
				App.prefs.getLineStylePref(PrefKey.CHART_HIGHLIGHT_YEAR_STYLE, LineStyle.SOLID).getCode());
		highlightLine.setAttributeNS(null, "stroke",
				FireChartUtil.colorToHexString(App.prefs.getColorPref(PrefKey.CHART_HIGHLIGHT_YEARS_COLOR, Color.YELLOW)));
				
		return highlightLine;
	}
	
	/**
	 * Returns a highlight line element based on the input parameters.
	 * 
	 * @param doc
	 * @param yearPosition
	 * @param vertGuidesOffsetAmount
	 * @param chartWidth
	 * @param height
	 * @param firstChartYear
	 * @param lastChartYear
	 * @return verticalGuide
	 */
	protected static Element getVerticalGuide(Document doc, int yearPosition, int vertGuidesOffsetAmount, int chartWidth, int height,
			int firstChartYear, int lastChartYear) {
			
		Element verticalGuide = doc.createElementNS(SVGDOMImplementation.SVG_NAMESPACE_URI, "line");
		
		verticalGuide.setAttributeNS(null, "x1", Integer.toString(yearPosition));
		verticalGuide.setAttributeNS(null, "x2", Integer.toString(yearPosition));
		verticalGuide.setAttributeNS(null, "y1", Integer.toString(vertGuidesOffsetAmount));
		verticalGuide.setAttributeNS(null, "y2", Double.toString(height - (2 * TICK_HEIGHT)));
		verticalGuide.setAttributeNS(null, "stroke-width", Double.toString(App.prefs.getIntPref(PrefKey.CHART_VERTICAL_GUIDE_WEIGHT, 1)
				* FireChartUtil.pixelsToYears(chartWidth, firstChartYear, lastChartYear)));
		verticalGuide.setAttributeNS(null, "stroke-dasharray",
				App.prefs.getLineStylePref(PrefKey.CHART_VERTICAL_GUIDE_STYLE, LineStyle.SOLID).getCode());
		verticalGuide.setAttributeNS(null, "stroke",
				FireChartUtil.colorToHexString(App.prefs.getColorPref(PrefKey.CHART_VERTICAL_GUIDE_COLOR, Color.BLACK)));
				
		return verticalGuide;
	}
	
	/**
	 * Returns a year text element based on the input parameters.
	 * 
	 * @param doc
	 * @param yearToDisplay
	 * @param yearPosition
	 * @param chartWidth
	 * @param height
	 * @param firstChartYear
	 * @param lastChartYear
	 * @return yearTextElement
	 */
	protected static Element getYearTextElement(Document doc, int yearToDisplay, int readerFirstYear) {
		
		Element yearTextElement = doc.createElementNS(SVGDOMImplementation.SVG_NAMESPACE_URI, "text");
		
		// Display the year text with the correct BC and zero cases accounted for
		if (yearToDisplay >= -1 && readerFirstYear < 0)
			yearToDisplay += 1;
			
		Text yearText = doc.createTextNode(Integer.toString(yearToDisplay));
		yearTextElement.setAttributeNS(null, "x", "0");
		yearTextElement.setAttributeNS(null, "y", "0");
		yearTextElement.setAttributeNS(null, "font-family", App.prefs.getPref(PrefKey.CHART_FONT_FAMILY, "Verdana"));
		yearTextElement.setAttributeNS(null, "font-size", "8");
		yearTextElement.appendChild(yearText);
		
		return yearTextElement;
	}
}
