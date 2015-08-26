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

import org.apache.batik.dom.svg.SVGDOMImplementation;
import org.fhaes.preferences.App;
import org.fhaes.preferences.FHAESPreferences.PrefKey;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;

/**
 * CompositePlotElementBuilder Class. This class is used to construct the SVG elements necessary for drawing the composite plot.
 * 
 * @author Joshua Brogan and Peter Brewer
 */
public class CompositePlotElementBuilder {
	
	/**
	 * Returns one part of the rectangular box which surrounds the composite plot.
	 * 
	 * @param doc
	 * @param firstChartYear
	 * @param lastChartYear
	 * @return borderLine1
	 */
	protected static Element getBorderLine1(Document doc, int firstChartYear, int lastChartYear) {
		
		Element borderLine1 = doc.createElementNS(SVGDOMImplementation.SVG_NAMESPACE_URI, "line");
		
		borderLine1.setAttributeNS(null, "x1", Double.toString(firstChartYear));
		borderLine1.setAttributeNS(null, "x2", Double.toString(lastChartYear));
		borderLine1.setAttributeNS(null, "y1", "0");
		borderLine1.setAttributeNS(null, "y2", "0");
		borderLine1.setAttributeNS(null, "stroke-width", "1");
		borderLine1.setAttributeNS(null, "stroke", "black");
		borderLine1.setAttributeNS(null, "stroke-linecap", "butt");
		
		return borderLine1;
	}
	
	/**
	 * Returns one part of the rectangular box which surrounds the composite plot.
	 * 
	 * @param doc
	 * @param chartHeight
	 * @param firstChartYear
	 * @param lastChartYear
	 * @return borderLine2
	 */
	protected static Element getBorderLine2(Document doc, double chartHeight, int firstChartYear, int lastChartYear) {
		
		Element borderLine2 = doc.createElementNS(SVGDOMImplementation.SVG_NAMESPACE_URI, "line");
		
		borderLine2.setAttributeNS(null, "x1", Integer.toString(firstChartYear));
		borderLine2.setAttributeNS(null, "x2", Integer.toString(lastChartYear));
		borderLine2.setAttributeNS(null, "y1", Double.toString(chartHeight));
		borderLine2.setAttributeNS(null, "y2", Double.toString(chartHeight));
		borderLine2.setAttributeNS(null, "stroke-width", "1");
		borderLine2.setAttributeNS(null, "stroke", "black");
		borderLine2.setAttributeNS(null, "stroke-linecap", "butt");
		
		return borderLine2;
	}
	
	/**
	 * Returns one part of the rectangular box which surrounds the composite plot.
	 * 
	 * @param doc
	 * @param chartHeight
	 * @param chartWidth
	 * @param firstChartYear
	 * @param lastChartYear
	 * @return borderLine3
	 */
	protected static Element getBorderLine3(Document doc, double chartHeight, int chartWidth, int firstChartYear, int lastChartYear) {
		
		Element borderLine3 = doc.createElementNS(SVGDOMImplementation.SVG_NAMESPACE_URI, "line");
		
		borderLine3.setAttributeNS(null, "x1", Integer.toString(firstChartYear));
		borderLine3.setAttributeNS(null, "x2", Integer.toString(firstChartYear));
		borderLine3.setAttributeNS(null, "y1", "0");
		borderLine3.setAttributeNS(null, "y2", Double.toString(chartHeight));
		borderLine3.setAttributeNS(null, "stroke-width",
				FireChartConversions.pixelsToYears(1, chartWidth, firstChartYear, lastChartYear) + "");
		borderLine3.setAttributeNS(null, "stroke", "black");
		borderLine3.setAttributeNS(null, "stroke-linecap", "butt");
		
		return borderLine3;
	}
	
	/**
	 * Returns one part of the rectangular box which surrounds the composite plot.
	 * 
	 * @param doc
	 * @param chartHeight
	 * @param chartWidth
	 * @param firstChartYear
	 * @param lastChartYear
	 * @return borderLine4
	 */
	protected static Element getBorderLine4(Document doc, double chartHeight, int chartWidth, int firstChartYear, int lastChartYear) {
		
		Element borderLine4 = doc.createElementNS(SVGDOMImplementation.SVG_NAMESPACE_URI, "line");
		
		borderLine4.setAttributeNS(null, "x1", Integer.toString(lastChartYear));
		borderLine4.setAttributeNS(null, "x2", Integer.toString(lastChartYear));
		borderLine4.setAttributeNS(null, "y1", "0");
		borderLine4.setAttributeNS(null, "y2", Double.toString(chartHeight));
		borderLine4.setAttributeNS(null, "stroke-width",
				FireChartConversions.pixelsToYears(1, chartWidth, firstChartYear, lastChartYear) + "");
		borderLine4.setAttributeNS(null, "stroke", "black");
		borderLine4.setAttributeNS(null, "stroke-linecap", "butt");
		
		return borderLine4;
	}
	
	/**
	 * Returns a composite name text element based on the input parameters.
	 * 
	 * @param doc
	 * @return compositeNameTextElement
	 */
	protected static Element getCompositeNameTextElement(Document doc) {
		
		Element compositeNameTextElement = doc.createElementNS(SVGDOMImplementation.SVG_NAMESPACE_URI, "text");
		
		Text compositeNameText = doc.createTextNode(App.prefs.getPref(PrefKey.CHART_COMPOSITE_LABEL_TEXT, "Composite"));
		compositeNameTextElement.setAttributeNS(null, "x", "0");
		compositeNameTextElement.setAttributeNS(null, "y", "0");
		compositeNameTextElement.setAttributeNS(null, "font-family", App.prefs.getPref(PrefKey.CHART_FONT_FAMILY, "Verdana"));
		compositeNameTextElement.setAttributeNS(null, "font-size",
				Integer.toString(App.prefs.getIntPref(PrefKey.CHART_COMPOSITE_PLOT_LABEL_FONT_SIZE, 10)));
		compositeNameTextElement.appendChild(compositeNameText);
		
		return compositeNameTextElement;
	}
	
	/**
	 * Returns an event line element based on the input parameters.
	 * 
	 * @param doc
	 * @param yearPosition
	 * @param chartWidth
	 * @param chartHeight
	 * @param firstChartYear
	 * @param lastChartYear
	 * @return eventLine
	 */
	protected static Element getEventLine(Document doc, int yearPosition, double chartHeight, int chartWidth, int firstChartYear,
			int lastChartYear) {
			
		Element eventLine = doc.createElementNS(SVGDOMImplementation.SVG_NAMESPACE_URI, "line");
		
		eventLine.setAttributeNS(null, "x1", Integer.toString(yearPosition));
		eventLine.setAttributeNS(null, "x2", Integer.toString(yearPosition));
		eventLine.setAttributeNS(null, "y1", "0");
		eventLine.setAttributeNS(null, "y2", Double.toString(chartHeight));
		eventLine.setAttributeNS(null, "stroke-width",
				Double.toString(FireChartConversions.pixelsToYears(1, chartWidth, firstChartYear, lastChartYear)));
		eventLine.setAttributeNS(null, "stroke", "black");
		
		return eventLine;
	}
}
