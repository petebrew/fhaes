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
	
	// Declare FireChartSVG parent
	private final FireChartSVG parent;
	
	/**
	 * Initializes the parent object for TimeAxisElementBuilder.
	 * 
	 * @param inParent
	 */
	public TimeAxisElementBuilder(FireChartSVG inParent) {
		
		parent = inParent;
	}
	
	/**
	 * Returns a time axis element based on the input parameters.
	 * 
	 * @param height
	 * @return timeAxis
	 */
	public Element getTimeAxis(int height) {
		
		Element timeAxis = parent.getSVGDocument().createElementNS(SVGDOMImplementation.SVG_NAMESPACE_URI, "line");
		timeAxis.setAttributeNS(null, "x1", Integer.toString(parent.getFirstChartYear()));
		timeAxis.setAttributeNS(null, "x2", Integer.toString(parent.getLastChartYear()));
		timeAxis.setAttributeNS(null, "y1", Integer.toString(height - (2 * FireChartSVG.SERIES_HEIGHT)));
		timeAxis.setAttributeNS(null, "y2", Integer.toString(height - (2 * FireChartSVG.SERIES_HEIGHT)));
		timeAxis.setAttributeNS(null, "stroke-dasharray", LineStyle.SOLID.getCode());
		timeAxis.setAttributeNS(null, "stroke", FireChartUtil.colorToHexString(Color.BLACK));
		
		return timeAxis;
	}
	
	/**
	 * Returns a major tick element based on the input parameters.
	 * 
	 * @param yearPosition
	 * @param height
	 * @return majorTick
	 */
	public Element getMajorTick(int yearPosition, int height) {
		
		String strokeWidth = Double.toString(App.prefs.getIntPref(PrefKey.CHART_VERTICAL_GUIDE_WEIGHT, 1)
				* FireChartUtil.pixelsToYears(parent.getChartWidth(), parent.getFirstChartYear(), parent.getLastChartYear()));
				
		Element majorTick = parent.getSVGDocument().createElementNS(SVGDOMImplementation.SVG_NAMESPACE_URI, "line");
		majorTick.setAttributeNS(null, "x1", Integer.toString(yearPosition));
		majorTick.setAttributeNS(null, "x2", Integer.toString(yearPosition));
		majorTick.setAttributeNS(null, "y1", Integer.toString(height - (2 * TICK_HEIGHT)));
		majorTick.setAttributeNS(null, "y2", Integer.toString(height - (TICK_HEIGHT)));
		majorTick.setAttributeNS(null, "stroke-width", strokeWidth);
		majorTick.setAttributeNS(null, "stroke-dasharray", LineStyle.SOLID.getCode());
		majorTick.setAttributeNS(null, "stroke", FireChartUtil.colorToHexString(Color.BLACK));
		
		return majorTick;
	}
	
	/**
	 * Returns a minor tick element based on the input parameters.
	 * 
	 * @param yearPosition
	 * @param height
	 * @return minorTick
	 */
	public Element getMinorTick(int yearPosition, int height) {
		
		String strokeWidth = Double.toString(App.prefs.getIntPref(PrefKey.CHART_VERTICAL_GUIDE_WEIGHT, 1)
				* FireChartUtil.pixelsToYears(parent.getChartWidth(), parent.getFirstChartYear(), parent.getLastChartYear()));
				
		Element minorTick = parent.getSVGDocument().createElementNS(SVGDOMImplementation.SVG_NAMESPACE_URI, "line");
		minorTick.setAttributeNS(null, "x1", Integer.toString(yearPosition));
		minorTick.setAttributeNS(null, "x2", Integer.toString(yearPosition));
		minorTick.setAttributeNS(null, "y1", Integer.toString(height - (2 * TICK_HEIGHT)));
		minorTick.setAttributeNS(null, "y2", Double.toString(height - (1.5 * TICK_HEIGHT)));
		minorTick.setAttributeNS(null, "stroke-width", strokeWidth);
		minorTick.setAttributeNS(null, "stroke-dasharray", LineStyle.SOLID.getCode());
		minorTick.setAttributeNS(null, "stroke", FireChartUtil.colorToHexString(Color.BLACK));
		
		return minorTick;
	}
	
	/**
	 * Returns a highlight line element based on the input parameters.
	 * 
	 * @param yearPosition
	 * @param height
	 * @return highlightLine
	 */
	public Element getHighlightLine(int yearPosition, int height) {
		
		String strokeWidth = Double.toString(App.prefs.getIntPref(PrefKey.CHART_HIGHLIGHT_YEARS_WEIGHT, 1)
				* FireChartUtil.pixelsToYears(parent.getChartWidth(), parent.getFirstChartYear(), parent.getLastChartYear()));
				
		String strokeDashArray = App.prefs.getLineStylePref(PrefKey.CHART_HIGHLIGHT_YEAR_STYLE, LineStyle.SOLID).getCode();
		String stroke = FireChartUtil.colorToHexString(App.prefs.getColorPref(PrefKey.CHART_HIGHLIGHT_YEARS_COLOR, Color.YELLOW));
		
		Element highlightLine = parent.getSVGDocument().createElementNS(SVGDOMImplementation.SVG_NAMESPACE_URI, "line");
		highlightLine.setAttributeNS(null, "x1", Integer.toString(yearPosition));
		highlightLine.setAttributeNS(null, "x2", Integer.toString(yearPosition));
		highlightLine.setAttributeNS(null, "y1", "0");
		highlightLine.setAttributeNS(null, "y2", Double.toString(height - (2 * TICK_HEIGHT)));
		highlightLine.setAttributeNS(null, "stroke-width", strokeWidth);
		highlightLine.setAttributeNS(null, "stroke-dasharray", strokeDashArray);
		highlightLine.setAttributeNS(null, "stroke", stroke);
		
		return highlightLine;
	}
	
	/**
	 * Returns a highlight line element based on the input parameters.
	 * 
	 * @param yearPosition
	 * @param vertGuidesOffsetAmount
	 * @param height
	 * @return verticalGuide
	 */
	public Element getVerticalGuide(int yearPosition, int vertGuidesOffsetAmount, int height) {
		
		String strokeWidth = Double.toString(App.prefs.getIntPref(PrefKey.CHART_VERTICAL_GUIDE_WEIGHT, 1)
				* FireChartUtil.pixelsToYears(parent.getChartWidth(), parent.getFirstChartYear(), parent.getLastChartYear()));
				
		String strokeDashArray = App.prefs.getLineStylePref(PrefKey.CHART_VERTICAL_GUIDE_STYLE, LineStyle.SOLID).getCode();
		String stroke = FireChartUtil.colorToHexString(App.prefs.getColorPref(PrefKey.CHART_VERTICAL_GUIDE_COLOR, Color.BLACK));
		
		Element verticalGuide = parent.getSVGDocument().createElementNS(SVGDOMImplementation.SVG_NAMESPACE_URI, "line");
		verticalGuide.setAttributeNS(null, "x1", Integer.toString(yearPosition));
		verticalGuide.setAttributeNS(null, "x2", Integer.toString(yearPosition));
		verticalGuide.setAttributeNS(null, "y1", Integer.toString(vertGuidesOffsetAmount));
		verticalGuide.setAttributeNS(null, "y2", Double.toString(height - (2 * TICK_HEIGHT)));
		verticalGuide.setAttributeNS(null, "stroke-width", strokeWidth);
		verticalGuide.setAttributeNS(null, "stroke-dasharray", strokeDashArray);
		verticalGuide.setAttributeNS(null, "stroke", stroke);
		
		return verticalGuide;
	}
	
	/**
	 * Returns a year text element based on the input parameters.
	 * 
	 * @param yearToDisplay
	 * @param readerFirstYear
	 * @return yearTextElement
	 */
	public Element getYearTextElement(int yearToDisplay, int readerFirstYear) {
		
		// Display the year text with the correct BC and zero cases accounted for
		if (yearToDisplay >= -1 && readerFirstYear < 0)
			yearToDisplay += 1;
			
		if (yearToDisplay < 0)
			yearToDisplay = 0 - yearToDisplay;
			
		Element yearTextElement = parent.getSVGDocument().createElementNS(SVGDOMImplementation.SVG_NAMESPACE_URI, "text");
		yearTextElement.setAttributeNS(null, "x", "0");
		yearTextElement.setAttributeNS(null, "y", "0");
		yearTextElement.setAttributeNS(null, "text-anchor", "middle");
		yearTextElement.setAttributeNS(null, "font-family", App.prefs.getPref(PrefKey.CHART_FONT_FAMILY, "Verdana"));
		yearTextElement.setAttributeNS(null, "font-size", Integer.toString(App.prefs.getIntPref(PrefKey.CHART_TIMELINE_FONT_SIZE, 8)));
		
		if (yearToDisplay == 0)
		{
			Text yearText = parent.getSVGDocument().createTextNode("BC AD");
			yearTextElement.appendChild(yearText);
		}
		else
		{
			Text yearText = parent.getSVGDocument().createTextNode(Integer.toString(yearToDisplay));
			yearTextElement.appendChild(yearText);
		}
		
		return yearTextElement;
	}
}
