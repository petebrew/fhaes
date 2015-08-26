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
import org.fhaes.neofhchart.FHSeriesSVG;
import org.fhaes.preferences.App;
import org.fhaes.preferences.FHAESPreferences.PrefKey;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;

/**
 * SeriesElementBuilder Class. This class is used to construct the SVG elements necessary for drawing a fire history series.
 * 
 * @author Joshua Brogan and Peter Brewer
 */
public class SeriesElementBuilder {
	
	/**
	 * Returns a series name text element based on the input parameters.
	 * 
	 * @param doc
	 * @param seriesSVG
	 * @param fontSize
	 * @param chartWidth
	 * @return seriesNameTextElement
	 */
	protected static Element getSeriesNameElement(Document doc, FHSeriesSVG seriesSVG, int fontSize, int chartWidth) {
		
		Element seriesNameTextElement = doc.createElementNS(SVGDOMImplementation.SVG_NAMESPACE_URI, "text");
		
		Text seriesNameText = doc.createTextNode(seriesSVG.getTitle());
		seriesNameTextElement.setAttribute("id", "series_label_" + seriesSVG.getTitle());
		seriesNameTextElement.setAttribute("x", Double.toString(chartWidth + 10));
		seriesNameTextElement.setAttribute("y", Integer.toString((FireChartSVG.SERIES_HEIGHT / 2)));
		seriesNameTextElement.setAttribute("font-family", App.prefs.getPref(PrefKey.CHART_FONT_FAMILY, "Verdana"));
		seriesNameTextElement.setAttribute("font-size", +fontSize + "");
		seriesNameTextElement.setAttribute("fill", FireChartConversions.colorToHexString(seriesSVG.getLabelColor()));
		seriesNameTextElement.appendChild(seriesNameText);
		
		return seriesNameTextElement;
	}
	
	/**
	 * Returns a category label text element based on the input parameters.
	 * 
	 * @param doc
	 * @param categoryLabel
	 * @param chartWidth
	 * @return categoryLabelTextElement
	 */
	protected static Element getCategoryLabelTextElement(Document doc, String categoryLabel, int chartWidth) {
		
		Element categoryLabelTextElement = doc.createElementNS(SVGDOMImplementation.SVG_NAMESPACE_URI, "text");
		
		Text categoryLabelText = doc.createTextNode(categoryLabel.toUpperCase());
		categoryLabelTextElement.setAttributeNS(null, "x", Integer.toString(chartWidth / 2));
		categoryLabelTextElement.setAttributeNS(null, "y", "0");
		categoryLabelTextElement.setAttributeNS(null, "font-family", App.prefs.getPref(PrefKey.CHART_FONT_FAMILY, "Verdana"));
		categoryLabelTextElement.setAttributeNS(null, "font-size", "16");
		categoryLabelTextElement.appendChild(categoryLabelText);
		
		return categoryLabelTextElement;
	}
	
	/**
	 * Returns a recorder line that is pre-configured. You will probably need to change the x1 y1 x2 y2 attributes to your liking.
	 * 
	 * @param doc
	 * @return recorderLine
	 */
	protected static Element getRecorderLine(Document doc) {
		
		Element recorderLine = doc.createElementNS(SVGDOMImplementation.SVG_NAMESPACE_URI, "line");
		
		recorderLine.setAttributeNS(null, "x1", "0");
		recorderLine.setAttributeNS(null, "y1", "0");
		recorderLine.setAttributeNS(null, "x2", "10");
		recorderLine.setAttributeNS(null, "y2", "0");
		recorderLine.setAttributeNS(null, "stroke", "black");
		recorderLine.setAttributeNS(null, "stroke-width", "1");
		
		return recorderLine;
	}
	
	/**
	 * Returns a non-recorder line that is pre-configured. You will probably need to change the x1 y1 x2 y2 attributes to your liking.
	 * 
	 * @param doc
	 * @param firstChartYear
	 * @param lastChartYear
	 * @return nonRecorderLine
	 */
	protected static Element getNonRecorderLine(Document doc, int firstChartYear, int lastChartYear) {
		
		Element nonRecorderLine = doc.createElementNS(SVGDOMImplementation.SVG_NAMESPACE_URI, "line");
		
		nonRecorderLine.setAttributeNS(null, "x1", "0");
		nonRecorderLine.setAttributeNS(null, "y1", "0");
		nonRecorderLine.setAttributeNS(null, "x2", "0");
		nonRecorderLine.setAttributeNS(null, "y2", "0");
		nonRecorderLine.setAttributeNS(null, "stroke", "black");
		nonRecorderLine.setAttributeNS(null, "stroke-width", "1");
		nonRecorderLine.setAttributeNS(null, "stroke-dasharray", LineStyle.DASHED.getCodeForChartYearCount(lastChartYear - firstChartYear));
		
		return nonRecorderLine;
	}
	
	/**
	 * Returns a fire year marker element based on the input color.
	 * 
	 * @param doc
	 * @param color
	 * @return fireYearMarker
	 */
	protected static Element getFireYearMarker(Document doc, Color color) {
		
		Element fireYearMarker = doc.createElementNS(SVGDOMImplementation.SVG_NAMESPACE_URI, "rect");
		
		fireYearMarker.setAttributeNS(null, "x", "0");
		fireYearMarker.setAttributeNS(null, "y", "0");
		fireYearMarker.setAttributeNS(null, "width", "1");
		fireYearMarker.setAttributeNS(null, "height", Integer.toString(FireChartSVG.SERIES_HEIGHT));
		fireYearMarker.setAttributeNS(null, "fill", FireChartConversions.colorToHexString(color));
		fireYearMarker.setAttributeNS(null, "stroke", FireChartConversions.colorToHexString(color));
		
		return fireYearMarker;
	}
	
	/**
	 * Returns an injury year marker element based on the input width and color.
	 * 
	 * @param doc
	 * @param width
	 * @param color
	 * @return injuryYearMarker
	 */
	protected static Element getInjuryYearMarker(Document doc, int width, Color color) {
		
		Element injuryYearMarker = doc.createElementNS(SVGDOMImplementation.SVG_NAMESPACE_URI, "rect");
		
		injuryYearMarker.setAttributeNS(null, "x", "0");
		injuryYearMarker.setAttributeNS(null, "y", "0");
		injuryYearMarker.setAttributeNS(null, "width", Integer.toString(width));
		injuryYearMarker.setAttributeNS(null, "height", Integer.toString(FireChartSVG.SERIES_HEIGHT));
		injuryYearMarker.setAttributeNS(null, "fill", "none");
		injuryYearMarker.setAttributeNS(null, "stroke", FireChartConversions.colorToHexString(color));
		
		return injuryYearMarker;
	}
	
	/**
	 * Returns an inner-year pith marker based on the input pith value, height, and color.
	 * 
	 * @param doc
	 * @param hasPith
	 * @param height
	 * @param color
	 * @return innerYearPithMarker
	 */
	protected static Element getInnerYearPithMarker(Document doc, boolean hasPith, int height, Color color) {
		
		if (hasPith)
		{
			Element pithMarker = doc.createElementNS(SVGDOMImplementation.SVG_NAMESPACE_URI, "rect");
			
			pithMarker.setAttributeNS(null, "x", "0");
			pithMarker.setAttributeNS(null, "y", Integer.toString(-height / 2));
			pithMarker.setAttributeNS(null, "width", "1");
			pithMarker.setAttributeNS(null, "height", Integer.toString(height));
			pithMarker.setAttributeNS(null, "fill", FireChartConversions.colorToHexString(color));
			pithMarker.setAttributeNS(null, "stroke", FireChartConversions.colorToHexString(color));
			
			return pithMarker;
		}
		else
		{
			Element noPithMarker = doc.createElementNS(SVGDOMImplementation.SVG_NAMESPACE_URI, "polygon");
			
			noPithMarker.setAttributeNS(null, "points", "-2,0.5 5,-5 2,0.5");
			noPithMarker.setAttributeNS(null, "fill", FireChartConversions.colorToHexString(color));
			noPithMarker.setAttributeNS(null, "stroke", FireChartConversions.colorToHexString(color));
			
			return noPithMarker;
		}
	}
	
	/**
	 * Returns an outer-year bark marker based on the input bark value, height, and color.
	 * 
	 * @param doc
	 * @param hasBark
	 * @param height
	 * @param color
	 * @return outerYearBarkMarker
	 */
	protected static Element getOuterYearBarkMarker(Document doc, boolean hasBark, int height, Color color) {
		
		if (hasBark)
		{
			Element barkMarker = doc.createElementNS(SVGDOMImplementation.SVG_NAMESPACE_URI, "rect");
			
			barkMarker.setAttributeNS(null, "x", "0");
			barkMarker.setAttributeNS(null, "y", Integer.toString(-height / 2));
			barkMarker.setAttributeNS(null, "width", "1");
			barkMarker.setAttributeNS(null, "height", Integer.toString(height));
			barkMarker.setAttributeNS(null, "fill", FireChartConversions.colorToHexString(color));
			barkMarker.setAttributeNS(null, "stroke", FireChartConversions.colorToHexString(color));
			
			return barkMarker;
		}
		else
		{
			Element noBarkMarker = doc.createElementNS(SVGDOMImplementation.SVG_NAMESPACE_URI, "polygon");
			
			noBarkMarker.setAttributeNS(null, "points", "2,0.5 -5,-5 -2,0.5");
			noBarkMarker.setAttributeNS(null, "fill", FireChartConversions.colorToHexString(color));
			noBarkMarker.setAttributeNS(null, "stroke", FireChartConversions.colorToHexString(color));
			
			return noBarkMarker;
		}
	}
	
	/**
	 * Returns an up button.
	 * 
	 * @param doc
	 * @return upButton
	 */
	protected static Element getUpButton(Document doc) {
		
		Element upButton = doc.createElementNS(SVGDOMImplementation.SVG_NAMESPACE_URI, "polygon");
		
		upButton.setAttributeNS(null, "points", "2,8 2,4 0,4 4,0 8,4 8,4 6,4 6,8");
		upButton.setAttributeNS(null, "fill", "black");
		upButton.setAttributeNS(null, "opacity", "0.2");
		
		return upButton;
	}
	
	/**
	 * Returns a down button.
	 * 
	 * @param doc
	 * @return downButton
	 */
	protected static Element getDownButton(Document doc) {
		
		Element downButton = doc.createElementNS(SVGDOMImplementation.SVG_NAMESPACE_URI, "polygon");
		
		downButton.setAttributeNS(null, "points", "2,0 2,4 0,4 4,8 8,4 8,4 6,4 6,0");
		downButton.setAttributeNS(null, "fill", "black");
		downButton.setAttributeNS(null, "opacity", "0.2");
		
		return downButton;
	}
}
