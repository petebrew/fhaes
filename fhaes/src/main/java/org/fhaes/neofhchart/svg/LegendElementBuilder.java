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

import org.fhaes.enums.LineStyle;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;

/**
 * LegendElementBuilder Class. This class is used to construct the SVG elements necessary for drawing the legend.
 * 
 * @author Joshua Brogan and Peter Brewer
 */
public class LegendElementBuilder {
	
	// Declare local constants
	private static final int DESCRIPTION_FONT_SIZE = 8;
	
	/**
	 * Returns a description element based on the input parameters.
	 * 
	 * @param doc
	 * @param svgNS
	 * @param text, the description to be entered
	 * @param fontFamily
	 * @param xLoc, the x-location of the text
	 * @param yLoc, the y-location of the text
	 * @return descriptionElement
	 */
	protected static Element getDescriptionElement(Document doc, String svgNS, String fontFamily, String text, int xLoc, int yLoc) {
		
		Element descriptionElement = doc.createElementNS(svgNS, "text");
		
		Text descriptionText = doc.createTextNode(text);
		descriptionElement.setAttributeNS(null, "x", Integer.toString(xLoc));
		descriptionElement.setAttributeNS(null, "y", Integer.toString(yLoc));
		descriptionElement.setAttributeNS(null, "font-family", fontFamily);
		descriptionElement.setAttributeNS(null, "font-size", Integer.toString(DESCRIPTION_FONT_SIZE));
		descriptionElement.appendChild(descriptionText);
		
		return descriptionElement;
	}
	
	/**
	 * Returns the rectangle which is drawn around the legend.
	 * 
	 * @param doc
	 * @param svgNS
	 * @param labelWidth
	 * @param currentY
	 * @return chartRectangle
	 */
	protected static Element getChartRectangle(Document doc, String svgNS, int labelWidth, int currentY) {
		
		Element chartRectangle = doc.createElementNS(svgNS, "rect");
		
		chartRectangle.setAttributeNS(null, "x", "-10");
		chartRectangle.setAttributeNS(null, "y", "-10");
		chartRectangle.setAttributeNS(null, "width", labelWidth + 40 + "");
		chartRectangle.setAttributeNS(null, "height", Integer.toString(currentY + 20));
		chartRectangle.setAttributeNS(null, "stroke", "black");
		chartRectangle.setAttributeNS(null, "stroke-width", "0.5");
		chartRectangle.setAttributeNS(null, "fill", "none");
		
		return chartRectangle;
	}
	
	/**
	 * Returns a recorder year example image to be used in the legend.
	 * 
	 * @param doc
	 * @param svgNS
	 * @return recorderYearExample
	 */
	protected static Element getRecorderYearExample(Document doc, String svgNS) {
		
		Element recorderYearExample = doc.createElementNS(svgNS, "line");
		
		recorderYearExample.setAttributeNS(null, "x1", "0");
		recorderYearExample.setAttributeNS(null, "y1", "0");
		recorderYearExample.setAttributeNS(null, "x2", "15");
		recorderYearExample.setAttributeNS(null, "y2", "0");
		recorderYearExample.setAttributeNS(null, "stroke", "black");
		recorderYearExample.setAttributeNS(null, "stroke-width", "1");
		
		return recorderYearExample;
	}
	
	/**
	 * Returns a non-recorder year example image to be used in the legend.
	 * 
	 * @param doc
	 * @param svgNS
	 * @param currentY
	 * @return nonRecorderYearExample
	 */
	protected static Element getNonRecorderYearExample(Document doc, String svgNS, int currentY) {
		
		Element nonRecorderYearExample = doc.createElementNS(svgNS, "line");
		
		nonRecorderYearExample.setAttributeNS(null, "x1", "0");
		nonRecorderYearExample.setAttributeNS(null, "y1", Integer.toString(currentY));
		nonRecorderYearExample.setAttributeNS(null, "x2", "15");
		nonRecorderYearExample.setAttributeNS(null, "y2", Integer.toString(currentY));
		nonRecorderYearExample.setAttributeNS(null, "stroke", "black");
		nonRecorderYearExample.setAttributeNS(null, "stroke-width", "1");
		nonRecorderYearExample.setAttributeNS(null, "stroke-dasharray", LineStyle.DASHED.getCode());
		
		return nonRecorderYearExample;
	}
	
	/**
	 * Returns a pith with non-recorder line example image to be used in the legend.
	 * 
	 * @param doc
	 * @param svgNS
	 * @return pithWithNonRecorderLineExample
	 */
	protected static Element getPithWithNonRecorderLineExample(Document doc, String svgNS) {
		
		Element pithWithNonRecorderLineExample = doc.createElementNS(svgNS, "line");
		
		pithWithNonRecorderLineExample.setAttributeNS(null, "x1", "0");
		pithWithNonRecorderLineExample.setAttributeNS(null, "y1", "0");
		pithWithNonRecorderLineExample.setAttributeNS(null, "x2", "10");
		pithWithNonRecorderLineExample.setAttributeNS(null, "y2", "0");
		pithWithNonRecorderLineExample.setAttributeNS(null, "stroke", "black");
		pithWithNonRecorderLineExample.setAttributeNS(null, "stroke-width", "1");
		pithWithNonRecorderLineExample.setAttributeNS(null, "stroke-dasharray", LineStyle.DASHED.getCode());
		
		return pithWithNonRecorderLineExample;
	}
	
	/**
	 * Returns a no pith with non-recorder line example image to be used in the legend.
	 * 
	 * @param doc
	 * @param svgNS
	 * @return noPithWithNonRecorderLineExample
	 */
	protected static Element getNoPithWithNonRecorderLineExample(Document doc, String svgNS) {
		
		Element noPithWithNonRecorderLineExample = doc.createElementNS(svgNS, "line");
		
		noPithWithNonRecorderLineExample.setAttributeNS(null, "x1", "0");
		noPithWithNonRecorderLineExample.setAttributeNS(null, "y1", "-0.5");
		noPithWithNonRecorderLineExample.setAttributeNS(null, "x2", "10");
		noPithWithNonRecorderLineExample.setAttributeNS(null, "y2", "-0.5");
		noPithWithNonRecorderLineExample.setAttributeNS(null, "stroke", "black");
		noPithWithNonRecorderLineExample.setAttributeNS(null, "stroke-width", "1");
		noPithWithNonRecorderLineExample.setAttributeNS(null, "stroke-dasharray", LineStyle.DASHED.getCode());
		
		return noPithWithNonRecorderLineExample;
	}
	
	/**
	 * Returns a bark with recorder line example image to be used in the legend.
	 * 
	 * @param doc
	 * @param svgNS
	 * @return barkWithRecorderLineExample
	 */
	protected static Element getBarkWithRecorderLineExample(Document doc, String svgNS) {
		
		Element barkWithRecorderLineExample = doc.createElementNS(svgNS, "line");
		
		barkWithRecorderLineExample.setAttributeNS(null, "x1", "0");
		barkWithRecorderLineExample.setAttributeNS(null, "y1", "0");
		barkWithRecorderLineExample.setAttributeNS(null, "x2", "-8");
		barkWithRecorderLineExample.setAttributeNS(null, "y2", "0");
		barkWithRecorderLineExample.setAttributeNS(null, "stroke", "black");
		barkWithRecorderLineExample.setAttributeNS(null, "stroke-width", "1");
		
		return barkWithRecorderLineExample;
	}
	
	/**
	 * Returns a no bark with recorder line example image to be used in the legend.
	 * 
	 * @param doc
	 * @param svgNS
	 * @return noBarkWithRecorderLineExample
	 */
	protected static Element getNoBarkWithRecorderLineExample(Document doc, String svgNS) {
		
		Element noBarkWithRecorderLineExample = doc.createElementNS(svgNS, "line");
		
		noBarkWithRecorderLineExample.setAttributeNS(null, "x1", "0");
		noBarkWithRecorderLineExample.setAttributeNS(null, "y1", "-0.5");
		noBarkWithRecorderLineExample.setAttributeNS(null, "x2", "-8");
		noBarkWithRecorderLineExample.setAttributeNS(null, "y2", "-0.5");
		noBarkWithRecorderLineExample.setAttributeNS(null, "stroke", "black");
		noBarkWithRecorderLineExample.setAttributeNS(null, "stroke-width", "1");
		
		return noBarkWithRecorderLineExample;
	}
}