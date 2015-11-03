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
import org.w3c.dom.Element;
import org.w3c.dom.Text;

/**
 * CompositePlotElementBuilder Class. This class is used to construct the SVG elements necessary for drawing the composite plot.
 * 
 * @author Joshua Brogan and Peter Brewer
 */
public class CompositePlotElementBuilder {
	
	// Declare FireChartSVG parent
	private final FireChartSVG parent;
	
	/**
	 * Initializes the parent object for CompositePlotElementBuilder.
	 * 
	 * @param inParent
	 */
	public CompositePlotElementBuilder(FireChartSVG inParent) {
		
		parent = inParent;
	}
	
	/**
	 * Returns one part of the rectangular box which surrounds the composite plot.
	 * 
	 * @return borderLine1
	 */
	public Element getBorderLine1() {
		
		Element borderLine1 = parent.getSVGDocument().createElementNS(SVGDOMImplementation.SVG_NAMESPACE_URI, "line");
		borderLine1.setAttributeNS(null, "x1", Integer.toString(parent.getFirstChartYear()));
		borderLine1.setAttributeNS(null, "x2", Integer.toString(parent.getLastChartYear()));
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
	 * @param chartHeight
	 * @return borderLine2
	 */
	public Element getBorderLine2(double chartHeight) {
		
		Element borderLine2 = parent.getSVGDocument().createElementNS(SVGDOMImplementation.SVG_NAMESPACE_URI, "line");
		borderLine2.setAttributeNS(null, "x1", Integer.toString(parent.getFirstChartYear()));
		borderLine2.setAttributeNS(null, "x2", Integer.toString(parent.getLastChartYear()));
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
	 * @param chartHeight
	 * @return borderLine3
	 */
	public Element getBorderLine3(double chartHeight) {
		
		String strokeWidth = Double
				.toString(FireChartUtil.pixelsToYears(1, parent.getChartWidth(), parent.getFirstChartYear(), parent.getLastChartYear()));
				
		Element borderLine3 = parent.getSVGDocument().createElementNS(SVGDOMImplementation.SVG_NAMESPACE_URI, "line");
		borderLine3.setAttributeNS(null, "x1", Integer.toString(parent.getFirstChartYear()));
		borderLine3.setAttributeNS(null, "x2", Integer.toString(parent.getFirstChartYear()));
		borderLine3.setAttributeNS(null, "y1", "0");
		borderLine3.setAttributeNS(null, "y2", Double.toString(chartHeight));
		borderLine3.setAttributeNS(null, "stroke-width", strokeWidth);
		borderLine3.setAttributeNS(null, "stroke", "black");
		borderLine3.setAttributeNS(null, "stroke-linecap", "butt");
		
		return borderLine3;
	}
	
	/**
	 * Returns one part of the rectangular box which surrounds the composite plot.
	 * 
	 * @param chartHeight
	 * @return borderLine4
	 */
	public Element getBorderLine4(double chartHeight) {
		
		String strokeWidth = Double
				.toString(FireChartUtil.pixelsToYears(1, parent.getChartWidth(), parent.getFirstChartYear(), parent.getLastChartYear()));
				
		Element borderLine4 = parent.getSVGDocument().createElementNS(SVGDOMImplementation.SVG_NAMESPACE_URI, "line");
		borderLine4.setAttributeNS(null, "x1", Integer.toString(parent.getLastChartYear()));
		borderLine4.setAttributeNS(null, "x2", Integer.toString(parent.getLastChartYear()));
		borderLine4.setAttributeNS(null, "y1", "0");
		borderLine4.setAttributeNS(null, "y2", Double.toString(chartHeight));
		borderLine4.setAttributeNS(null, "stroke-width", strokeWidth);
		borderLine4.setAttributeNS(null, "stroke", "black");
		borderLine4.setAttributeNS(null, "stroke-linecap", "butt");
		
		return borderLine4;
	}
	
	/**
	 * Returns a composite label text element based on the input parameters.
	 * 
	 * @return compositeLabelTextElement
	 */
	public Element getCompositeLabelTextElement() {
		
		String fontSize = Integer.toString(App.prefs.getIntPref(PrefKey.CHART_COMPOSITE_PLOT_LABEL_FONT_SIZE, 10));
		Text compositeLabelText = parent.getSVGDocument()
				.createTextNode(App.prefs.getPref(PrefKey.CHART_COMPOSITE_LABEL_TEXT, "Composite"));
				
		Element compositeLabelTextElement = parent.getSVGDocument().createElementNS(SVGDOMImplementation.SVG_NAMESPACE_URI, "text");
		compositeLabelTextElement.setAttributeNS(null, "x", "0");
		compositeLabelTextElement.setAttributeNS(null, "y", "0");
		compositeLabelTextElement.setAttributeNS(null, "font-family", App.prefs.getPref(PrefKey.CHART_FONT_FAMILY, "Verdana"));
		compositeLabelTextElement.setAttributeNS(null, "font-size", fontSize);
		compositeLabelTextElement.appendChild(compositeLabelText);
		
		return compositeLabelTextElement;
	}
	
	/**
	 * Returns an event line element based on the input parameters.
	 * 
	 * @param yearPosition
	 * @param chartHeight
	 * @return eventLine
	 */
	public Element getEventLine(int yearPosition, double chartHeight) {
		
		String strokeWidth = Double
				.toString(FireChartUtil.pixelsToYears(1, parent.getChartWidth(), parent.getFirstChartYear(), parent.getLastChartYear()));
				
		Element eventLine = parent.getSVGDocument().createElementNS(SVGDOMImplementation.SVG_NAMESPACE_URI, "line");
		eventLine.setAttributeNS(null, "x1", Integer.toString(yearPosition));
		eventLine.setAttributeNS(null, "x2", Integer.toString(yearPosition));
		eventLine.setAttributeNS(null, "y1", "0");
		eventLine.setAttributeNS(null, "y2", Double.toString(chartHeight));
		eventLine.setAttributeNS(null, "stroke-width", strokeWidth);
		eventLine.setAttributeNS(null, "stroke", "black");
		
		return eventLine;
	}
}
