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

import org.apache.batik.dom.svg.SVGDOMImplementation;
import org.fhaes.enums.JustificationType;
import org.fhaes.enums.LineStyle;
import org.fhaes.neofhchart.FHSeriesSVG;
import org.fhaes.preferences.App;
import org.fhaes.preferences.FHAESPreferences.PrefKey;
import org.w3c.dom.Element;
import org.w3c.dom.Text;

/**
 * SeriesElementBuilder Class. This class is used to construct the SVG elements necessary for drawing a fire history series.
 * 
 * @author Joshua Brogan and Peter Brewer
 */
public class SeriesElementBuilder {
	
	// Declare FireChartSVG parent
	private final FireChartSVG parent;
	
	/**
	 * Initializes the parent object for SeriesElementBuilder.
	 * 
	 * @param inParent
	 */
	public SeriesElementBuilder(FireChartSVG inParent) {
		
		parent = inParent;
	}
	
	/**
	 * Returns a series name text element based on the input parameters.
	 * 
	 * @param seriesSVG
	 * @param fontSize
	 * @return seriesNameTextElement
	 */
	public Element getSeriesNameTextElement(FHSeriesSVG seriesSVG, int fontSize) {
		
		Text seriesNameText = parent.getSVGDocument().createTextNode(seriesSVG.getTitle());
		
		Element seriesNameTextElement = parent.getSVGDocument().createElementNS(SVGDOMImplementation.SVG_NAMESPACE_URI, "text");
		seriesNameTextElement.setAttribute("id", "series_label_" + seriesSVG.getTitle());
		seriesNameTextElement.setAttribute("x", Double.toString(parent.getChartWidth() + 10));
		seriesNameTextElement.setAttribute("y", Integer.toString((FireChartSVG.SERIES_HEIGHT / 2)));
		seriesNameTextElement.setAttribute("font-family", App.prefs.getPref(PrefKey.CHART_FONT_FAMILY, "Verdana"));
		seriesNameTextElement.setAttribute("font-size", +fontSize + "");
		seriesNameTextElement.setAttribute("fill", FireChartUtil.colorToHexString(seriesSVG.getLabelColor()));
		seriesNameTextElement.appendChild(seriesNameText);
		
		return seriesNameTextElement;
	}
	
	/**
	 * Returns a category label text element based on the input parameters.
	 * 
	 * @param categoryLabel
	 * @param labelColor
	 * @return categoryLabelTextElement
	 */
	public Element getCategoryLabelTextElement(String categoryLabel, Color labelColor) {
		
		String xPosition;
		JustificationType justification = App.prefs.getJustificationTypePref(PrefKey.CHART_CATEGORY_LABEL_JUSTIFICATION,
				JustificationType.CENTER);
				
		if (justification == JustificationType.CENTER)
		{
			int paddingAmountToCenterText = FireChartUtil.getStringWidth(Font.PLAIN, 16, categoryLabel.toUpperCase());
			xPosition = Integer.toString((parent.getChartWidth() / 2) - (paddingAmountToCenterText / 2));
		}
		else if (justification == JustificationType.RIGHT)
		{
			int widthOfLabelString = FireChartUtil.getStringWidth(Font.PLAIN, 16, categoryLabel.toUpperCase());
			xPosition = Integer.toString(parent.getChartWidth() - widthOfLabelString);
		}
		else
		{
			xPosition = "0"; // used when the justification is set to LEFT
		}
		
		String fontSize = Integer.toString(App.prefs.getIntPref(PrefKey.CHART_CATEGORY_LABEL_FONT_SIZE, 18));
		Text categoryLabelText = parent.getSVGDocument().createTextNode(categoryLabel.toUpperCase());
		
		Element categoryLabelTextElement = parent.getSVGDocument().createElementNS(SVGDOMImplementation.SVG_NAMESPACE_URI, "text");
		categoryLabelTextElement.setAttributeNS(null, "x", xPosition);
		categoryLabelTextElement.setAttributeNS(null, "y", "0");
		categoryLabelTextElement.setAttributeNS(null, "font-family", App.prefs.getPref(PrefKey.CHART_FONT_FAMILY, "Verdana"));
		categoryLabelTextElement.setAttributeNS(null, "font-size", fontSize);
		categoryLabelTextElement.setAttribute("fill", FireChartUtil.colorToHexString(labelColor));
		categoryLabelTextElement.appendChild(categoryLabelText);
		
		return categoryLabelTextElement;
	}
	
	/**
	 * Returns a series line element based on the input parameters.
	 * 
	 * @param isRecording
	 * @param x1Position
	 * @param x2Position
	 * @param lineColor
	 * @return seriesLine
	 */
	public Element getSeriesLine(boolean isRecording, int x1Position, int x2Position, Color lineColor) {
		
		Element seriesLine;
		
		if (isRecording)
		{
			seriesLine = getRecorderLine();
		}
		else
		{
			seriesLine = getNonRecorderLine();
		}
		
		seriesLine.setAttributeNS(null, "x1", Integer.toString(x1Position));
		seriesLine.setAttributeNS(null, "y1", "0");
		seriesLine.setAttributeNS(null, "x2", Integer.toString(x2Position));
		seriesLine.setAttributeNS(null, "y2", "0");
		seriesLine.setAttributeNS(null, "stroke", FireChartUtil.colorToHexString(lineColor));
		
		return seriesLine;
	}
	
	/**
	 * Returns a recorder line that is pre-configured. You will probably need to change the x1 y1 x2 y2 attributes to your liking.
	 * 
	 * @return recorderLine
	 */
	private Element getRecorderLine() {
		
		Element recorderLine = parent.getSVGDocument().createElementNS(SVGDOMImplementation.SVG_NAMESPACE_URI, "line");
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
	 * @return nonRecorderLine
	 */
	private Element getNonRecorderLine() {
		
		Element nonRecorderLine = parent.getSVGDocument().createElementNS(SVGDOMImplementation.SVG_NAMESPACE_URI, "line");
		nonRecorderLine.setAttributeNS(null, "x1", "0");
		nonRecorderLine.setAttributeNS(null, "y1", "0");
		nonRecorderLine.setAttributeNS(null, "x2", "0");
		nonRecorderLine.setAttributeNS(null, "y2", "0");
		nonRecorderLine.setAttributeNS(null, "stroke", "black");
		nonRecorderLine.setAttributeNS(null, "stroke-width", "1");
		nonRecorderLine.setAttributeNS(null, "stroke-dasharray",
				LineStyle.DASHED.getCodeForChartYearCount(parent.getLastChartYear() - parent.getFirstChartYear()));
				
		return nonRecorderLine;
	}
	
	/**
	 * Returns a fire year marker element based on the input color.
	 * 
	 * @param color
	 * @return fireYearMarker
	 */
	public Element getFireYearMarker(Color color) {
		
		Element fireYearMarker = parent.getSVGDocument().createElementNS(SVGDOMImplementation.SVG_NAMESPACE_URI, "rect");
		fireYearMarker.setAttributeNS(null, "x", "0");
		fireYearMarker.setAttributeNS(null, "y", "0");
		fireYearMarker.setAttributeNS(null, "width", "1");
		fireYearMarker.setAttributeNS(null, "height", Integer.toString(FireChartSVG.SERIES_HEIGHT));
		fireYearMarker.setAttributeNS(null, "fill", FireChartUtil.colorToHexString(color));
		fireYearMarker.setAttributeNS(null, "stroke", FireChartUtil.colorToHexString(color));
		
		return fireYearMarker;
	}
	
	/**
	 * Returns an injury year marker element based on the input width and color.
	 * 
	 * @param width
	 * @param color
	 * @return injuryYearMarker
	 */
	public Element getInjuryYearMarker(int width, Color color) {
		
		Element injuryYearMarker = parent.getSVGDocument().createElementNS(SVGDOMImplementation.SVG_NAMESPACE_URI, "rect");
		injuryYearMarker.setAttributeNS(null, "x", "0");
		injuryYearMarker.setAttributeNS(null, "y", "0");
		injuryYearMarker.setAttributeNS(null, "width", Integer.toString(width));
		injuryYearMarker.setAttributeNS(null, "height", Integer.toString(FireChartSVG.SERIES_HEIGHT));
		injuryYearMarker.setAttributeNS(null, "fill", "none");
		injuryYearMarker.setAttributeNS(null, "stroke", FireChartUtil.colorToHexString(color));
		
		return injuryYearMarker;
	}
	
	/**
	 * Returns an inner-year pith marker based on the input pith value, height, and color.
	 * 
	 * @param hasPith
	 * @param height
	 * @param color
	 * @return innerYearPithMarker
	 */
	public Element getInnerYearPithMarker(boolean hasPith, int height, Color color) {
		
		if (hasPith)
		{
			Element pithMarker = parent.getSVGDocument().createElementNS(SVGDOMImplementation.SVG_NAMESPACE_URI, "rect");
			pithMarker.setAttributeNS(null, "x", "0");
			pithMarker.setAttributeNS(null, "y", Integer.toString(-height / 2));
			pithMarker.setAttributeNS(null, "width", "1");
			pithMarker.setAttributeNS(null, "height", Integer.toString(height));
			pithMarker.setAttributeNS(null, "fill", FireChartUtil.colorToHexString(color));
			pithMarker.setAttributeNS(null, "stroke", FireChartUtil.colorToHexString(color));
			
			return pithMarker;
		}
		else
		{
			Element noPithMarker = parent.getSVGDocument().createElementNS(SVGDOMImplementation.SVG_NAMESPACE_URI, "polygon");
			noPithMarker.setAttributeNS(null, "points", "-2,0.5 5,-5 2,0.5");
			noPithMarker.setAttributeNS(null, "fill", FireChartUtil.colorToHexString(color));
			noPithMarker.setAttributeNS(null, "stroke", FireChartUtil.colorToHexString(color));
			
			return noPithMarker;
		}
	}
	
	/**
	 * Returns an outer-year bark marker based on the input bark value, height, and color.
	 * 
	 * @param hasBark
	 * @param height
	 * @param color
	 * @return outerYearBarkMarker
	 */
	public Element getOuterYearBarkMarker(boolean hasBark, int height, Color color) {
		
		if (hasBark)
		{
			Element barkMarker = parent.getSVGDocument().createElementNS(SVGDOMImplementation.SVG_NAMESPACE_URI, "rect");
			barkMarker.setAttributeNS(null, "x", "0");
			barkMarker.setAttributeNS(null, "y", Integer.toString(-height / 2));
			barkMarker.setAttributeNS(null, "width", "1");
			barkMarker.setAttributeNS(null, "height", Integer.toString(height));
			barkMarker.setAttributeNS(null, "fill", FireChartUtil.colorToHexString(color));
			barkMarker.setAttributeNS(null, "stroke", FireChartUtil.colorToHexString(color));
			
			return barkMarker;
		}
		else
		{
			Element noBarkMarker = parent.getSVGDocument().createElementNS(SVGDOMImplementation.SVG_NAMESPACE_URI, "polygon");
			noBarkMarker.setAttributeNS(null, "points", "2,0.5 -5,-5 -2,0.5");
			noBarkMarker.setAttributeNS(null, "fill", FireChartUtil.colorToHexString(color));
			noBarkMarker.setAttributeNS(null, "stroke", FireChartUtil.colorToHexString(color));
			
			return noBarkMarker;
		}
	}
	
	/**
	 * Returns an up button for use on the Fire Chart.
	 * 
	 * @return upButton
	 */
	public Element getUpButton() {
		
		Element upButton = parent.getSVGDocument().createElementNS(SVGDOMImplementation.SVG_NAMESPACE_URI, "polygon");
		upButton.setAttributeNS(null, "points", "2,8 2,4 0,4 4,0 8,4 8,4 6,4 6,8");
		upButton.setAttributeNS(null, "fill", "black");
		upButton.setAttributeNS(null, "opacity", "0.2");
		
		return upButton;
	}
	
	/**
	 * Returns a down button for use on the Fire Chart.
	 * 
	 * @return downButton
	 */
	public Element getDownButton() {
		
		Element downButton = parent.getSVGDocument().createElementNS(SVGDOMImplementation.SVG_NAMESPACE_URI, "polygon");
		downButton.setAttributeNS(null, "points", "2,0 2,4 0,4 4,8 8,4 8,4 6,4 6,0");
		downButton.setAttributeNS(null, "fill", "black");
		downButton.setAttributeNS(null, "opacity", "0.2");
		
		return downButton;
	}
}
