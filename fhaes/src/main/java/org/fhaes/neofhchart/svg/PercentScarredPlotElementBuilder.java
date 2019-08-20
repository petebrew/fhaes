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
import org.fhaes.preferences.App;
import org.fhaes.preferences.FHAESPreferences.PrefKey;
import org.w3c.dom.Element;
import org.w3c.dom.Text;

/**
 * PercentScarredPlotElementBuilder Class. This class is used to construct the SVG elements necessary for drawing the percent scarred plot.
 * 
 * @author Joshua Brogan and Peter Brewer
 */
public class PercentScarredPlotElementBuilder {
	
	// Declare FireChartSVG parent
	private final FireChartSVG parent;
	
	/**
	 * Initializes the parent object for PercentScarredPlotElementBuilder.
	 * 
	 * @param inParent
	 */
	public PercentScarredPlotElementBuilder(FireChartSVG inParent) {
		
		parent = inParent;
	}
	
	/**
	 * Returns one part of the rectangular box which surrounds the percent scarred plot.
	 * 
	 * @return borderLine1
	 */
	public Element getBorderLine1() {
		
		String strokeWidth = Double
				.toString(FireChartUtil.pixelsToYears(1, parent.getChartWidth(), parent.getFirstChartYear(), parent.getLastChartYear()));
		
		Element borderLine1 = parent.getSVGDocument().createElementNS(SVGDOMImplementation.SVG_NAMESPACE_URI, "line");
		borderLine1.setAttributeNS(null, "x1", "0");
		borderLine1.setAttributeNS(null, "y1", "0");
		borderLine1.setAttributeNS(null, "x2", "0");
		borderLine1.setAttributeNS(null, "y2", "100");
		borderLine1.setAttributeNS(null, "stroke-width", strokeWidth);
		borderLine1.setAttributeNS(null, "stroke", "black");
		borderLine1.setAttributeNS(null, "stroke-linecap", "butt");
		
		return borderLine1;
	}
	
	/**
	 * Returns one part of the rectangular box which surrounds the percent scarred plot.
	 * 
	 * @param unscaleY
	 * @return borderLine2
	 */
	public Element getBorderLine2(double unscaleY) {
		
		Element borderLine2 = parent.getSVGDocument().createElementNS(SVGDOMImplementation.SVG_NAMESPACE_URI, "line");
		borderLine2.setAttributeNS(null, "x1", "0");
		borderLine2.setAttributeNS(null, "y1", "100");
		borderLine2.setAttributeNS(null, "x2", Integer.toString(parent.getLastChartYear() - parent.getFirstChartYear()));
		borderLine2.setAttributeNS(null, "y2", "100");
		borderLine2.setAttributeNS(null, "stroke-width", 0 - unscaleY + "");
		borderLine2.setAttributeNS(null, "stroke", "black");
		borderLine2.setAttributeNS(null, "stroke-linecap", "butt");
		
		return borderLine2;
	}
	
	/**
	 * Returns one part of the rectangular box which surrounds the percent scarred plot.
	 * 
	 * @return borderLine3
	 */
	public Element getBorderLine3() {
		
		String strokeWidth = Double
				.toString(FireChartUtil.pixelsToYears(1, parent.getChartWidth(), parent.getFirstChartYear(), parent.getLastChartYear()));
		
		Element borderLine3 = parent.getSVGDocument().createElementNS(SVGDOMImplementation.SVG_NAMESPACE_URI, "line");
		borderLine3.setAttributeNS(null, "x1", Integer.toString(parent.getLastChartYear() - parent.getFirstChartYear()));
		borderLine3.setAttributeNS(null, "y1", "100");
		borderLine3.setAttributeNS(null, "x2", Integer.toString(parent.getLastChartYear() - parent.getFirstChartYear()));
		borderLine3.setAttributeNS(null, "y2", "0");
		borderLine3.setAttributeNS(null, "stroke-width", strokeWidth);
		borderLine3.setAttributeNS(null, "stroke", "black");
		borderLine3.setAttributeNS(null, "stroke-linecap", "butt");
		
		return borderLine3;
	}
	
	/**
	 * Returns one part of the rectangular box which surrounds the percent scarred plot.
	 * 
	 * @param unscaleY
	 * @return borderLine4
	 */
	public Element getBorderLine4(double unscaleY) {
		
		Element borderLine4 = parent.getSVGDocument().createElementNS(SVGDOMImplementation.SVG_NAMESPACE_URI, "line");
		borderLine4.setAttributeNS(null, "x1", Integer.toString(parent.getLastChartYear() - parent.getFirstChartYear()));
		borderLine4.setAttributeNS(null, "y1", "0");
		borderLine4.setAttributeNS(null, "x2", "0");
		borderLine4.setAttributeNS(null, "y2", "0");
		borderLine4.setAttributeNS(null, "stroke-width", 0 - unscaleY + "");
		borderLine4.setAttributeNS(null, "stroke", "black");
		borderLine4.setAttributeNS(null, "stroke-linecap", "butt");
		
		return borderLine4;
	}
	
	/**
	 * Returns a percent scarred text element based on the input parameters.
	 * 
	 * @param yPosition
	 * @param percentTextAsNum
	 * @param yAxisFontSize
	 * @return percentScarredTextElement
	 */
	public Element getPercentScarredTextElement(int yPosition, int percentTextAsNum, int yAxisFontSize) {
		
		Text percentScarredText = parent.getSVGDocument().createTextNode(Integer.toString(percentTextAsNum));
		
		Element percentScarredTextElement = parent.getSVGDocument().createElementNS(SVGDOMImplementation.SVG_NAMESPACE_URI, "text");
		percentScarredTextElement.setAttributeNS(null, "x", "7");
		percentScarredTextElement.setAttributeNS(null, "y", Integer.toString(yPosition));
		percentScarredTextElement.setAttributeNS(null, "font-family", App.prefs.getPref(PrefKey.CHART_FONT_FAMILY, "Verdana"));
		percentScarredTextElement.setAttributeNS(null, "font-size", yAxisFontSize + "");
		percentScarredTextElement.appendChild(percentScarredText);
		
		return percentScarredTextElement;
	}
	
	/**
	 * Returns a horizontal tick to be used in the percent scarred plot.
	 * 
	 * @param unscaleY
	 * @return horizontalTick
	 */
	public Element getHorizontalTick(double unscaleY) {
		
		String stroke = FireChartUtil.colorToHexString(App.prefs.getColorPref(PrefKey.CHART_PERCENT_SCARRED_COLOR, Color.BLACK));
		
		Element horizontalTick = parent.getSVGDocument().createElementNS(SVGDOMImplementation.SVG_NAMESPACE_URI, "line");
		horizontalTick.setAttributeNS(null, "x1", "0");
		horizontalTick.setAttributeNS(null, "y1", "0");
		horizontalTick.setAttributeNS(null, "x2", "5");
		horizontalTick.setAttributeNS(null, "y2", "0");
		horizontalTick.setAttributeNS(null, "stroke", stroke);
		horizontalTick.setAttributeNS(null, "stroke-width", Double.toString(0 - unscaleY));
		
		return horizontalTick;
	}
	
	/**
	 * Returns a vertical line to be used in the percent scarred plot.
	 * 
	 * @param xPosition
	 * @param y2Position
	 * @return verticalLine
	 */
	public Element getVerticalLine(int xPosition, double y2Position) {
		
		// String strokeWidth = Double
		// .toString(FireChartUtil.pixelsToYears(1, parent.getChartWidth(), parent.getFirstChartYear(), parent.getLastChartYear()));
		String strokeWidth = Double
				.toString(FireChartUtil.pixelsToYears(1, parent.getChartWidth(), parent.getFirstChartYear(), parent.getLastChartYear())
						* App.prefs.getIntPref(PrefKey.CHART_INDEX_PERCENT_SCARRED_LINE_WIDTH, 1));
		
		Element verticalLine = parent.getSVGDocument().createElementNS(SVGDOMImplementation.SVG_NAMESPACE_URI, "line");
		verticalLine.setAttributeNS(null, "x1", Integer.toString(xPosition));
		verticalLine.setAttributeNS(null, "y1", "0");
		verticalLine.setAttributeNS(null, "x2", Integer.toString(xPosition));
		verticalLine.setAttributeNS(null, "y2", Double.toString(y2Position));
		verticalLine.setAttributeNS(null, "stroke", "black");
		verticalLine.setAttributeNS(null, "stroke-width", strokeWidth);
		verticalLine.setAttributeNS(null, "stroke-linecap", "butt");
		
		return verticalLine;
	}
}
