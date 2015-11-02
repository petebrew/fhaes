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
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;

/**
 * PercentScarredPlotElementBuilder Class. This class is used to construct the SVG elements necessary for drawing the percent scarred plot.
 * 
 * @author Joshua Brogan and Peter Brewer
 */
public class PercentScarredPlotElementBuilder {
	
	/**
	 * Returns one part of the rectangular box which surrounds the percent scarred plot.
	 * 
	 * @param doc
	 * @param chartWidth
	 * @param firstChartYear
	 * @param lastChartYear
	 * @return borderLine1
	 */
	protected static Element getBorderLine1(Document doc, int chartWidth, int firstChartYear, int lastChartYear) {
		
		String strokeWidth = Double.toString(FireChartUtil.pixelsToYears(1, chartWidth, firstChartYear, lastChartYear));
		
		Element borderLine1 = doc.createElementNS(SVGDOMImplementation.SVG_NAMESPACE_URI, "line");
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
	 * @param doc
	 * @param unscaleY
	 * @param firstChartYear
	 * @param lastChartYear
	 * @return borderLine2
	 */
	protected static Element getBorderLine2(Document doc, double unscaleY, int firstChartYear, int lastChartYear) {
		
		Element borderLine2 = doc.createElementNS(SVGDOMImplementation.SVG_NAMESPACE_URI, "line");
		borderLine2.setAttributeNS(null, "x1", "0");
		borderLine2.setAttributeNS(null, "y1", "100");
		borderLine2.setAttributeNS(null, "x2", Integer.toString(lastChartYear - firstChartYear));
		borderLine2.setAttributeNS(null, "y2", "100");
		borderLine2.setAttributeNS(null, "stroke-width", 0 - unscaleY + "");
		borderLine2.setAttributeNS(null, "stroke", "black");
		borderLine2.setAttributeNS(null, "stroke-linecap", "butt");
		
		return borderLine2;
	}
	
	/**
	 * Returns one part of the rectangular box which surrounds the percent scarred plot.
	 * 
	 * @param doc
	 * @param chartWidth
	 * @param firstChartYear
	 * @param lastChartYear
	 * @return borderLine3
	 */
	protected static Element getBorderLine3(Document doc, int chartWidth, int firstChartYear, int lastChartYear) {
		
		String strokeWidth = Double.toString(FireChartUtil.pixelsToYears(1, chartWidth, firstChartYear, lastChartYear));
		
		Element borderLine3 = doc.createElementNS(SVGDOMImplementation.SVG_NAMESPACE_URI, "line");
		borderLine3.setAttributeNS(null, "x1", Integer.toString(lastChartYear - firstChartYear));
		borderLine3.setAttributeNS(null, "y1", "100");
		borderLine3.setAttributeNS(null, "x2", Integer.toString(lastChartYear - firstChartYear));
		borderLine3.setAttributeNS(null, "y2", "0");
		borderLine3.setAttributeNS(null, "stroke-width", strokeWidth);
		borderLine3.setAttributeNS(null, "stroke", "black");
		borderLine3.setAttributeNS(null, "stroke-linecap", "butt");
		
		return borderLine3;
	}
	
	/**
	 * Returns one part of the rectangular box which surrounds the percent scarred plot.
	 * 
	 * @param doc
	 * @param unscaleY
	 * @param firstChartYear
	 * @param lastChartYear
	 * @return borderLine4
	 */
	protected static Element getBorderLine4(Document doc, double unscaleY, int firstChartYear, int lastChartYear) {
		
		Element borderLine4 = doc.createElementNS(SVGDOMImplementation.SVG_NAMESPACE_URI, "line");
		borderLine4.setAttributeNS(null, "x1", Integer.toString(lastChartYear - firstChartYear));
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
	 * @param doc
	 * @param labelY
	 * @param percentTextAsNum
	 * @param yAxisFontSize
	 * @return percentScarredTextElement
	 */
	protected static Element getPercentScarredTextElement(Document doc, int labelY, int percentTextAsNum, int yAxisFontSize) {
		
		Text percentScarredText = doc.createTextNode(Integer.toString(percentTextAsNum));
		
		Element percentScarredTextElement = doc.createElementNS(SVGDOMImplementation.SVG_NAMESPACE_URI, "text");
		percentScarredTextElement.setAttributeNS(null, "x", "7");
		percentScarredTextElement.setAttributeNS(null, "y", labelY + "");
		percentScarredTextElement.setAttributeNS(null, "font-family", App.prefs.getPref(PrefKey.CHART_FONT_FAMILY, "Verdana"));
		percentScarredTextElement.setAttributeNS(null, "font-size", yAxisFontSize + "");
		percentScarredTextElement.appendChild(percentScarredText);
		
		return percentScarredTextElement;
	}
	
	/**
	 * Returns a horizontal tick to be used in the percent scarred plot.
	 * 
	 * @param doc
	 * @param unscaleY
	 * @return horizontalTick
	 */
	protected static Element getHorizontalTick(Document doc, double unscaleY) {
		
		String stroke = FireChartUtil.colorToHexString(App.prefs.getColorPref(PrefKey.CHART_PERCENT_SCARRED_COLOR, Color.BLACK));
		
		Element horizontalTick = doc.createElementNS(SVGDOMImplementation.SVG_NAMESPACE_URI, "line");
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
	 * @param doc
	 * @param percent
	 * @param percentArrayIndex
	 * @param chartWidth
	 * @param firstChartYear
	 * @param lastChartYear
	 * @return verticalLine
	 */
	protected static Element getVerticalLine(Document doc, double percent, int percentArrayIndex, int chartWidth, int firstChartYear,
			int lastChartYear) {
			
		String strokeWidth = Double.toString(FireChartUtil.pixelsToYears(1, chartWidth, firstChartYear, lastChartYear));
		
		Element verticalLine = doc.createElementNS(SVGDOMImplementation.SVG_NAMESPACE_URI, "line");
		verticalLine.setAttributeNS(null, "x1", Integer.toString(percentArrayIndex));
		verticalLine.setAttributeNS(null, "y1", "0");
		verticalLine.setAttributeNS(null, "x2", Integer.toString(percentArrayIndex));
		verticalLine.setAttributeNS(null, "y2", Double.toString(percent));
		verticalLine.setAttributeNS(null, "stroke", "black");
		verticalLine.setAttributeNS(null, "stroke-width", strokeWidth);
		
		return verticalLine;
	}
}