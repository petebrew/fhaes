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

import org.fhaes.enums.LineStyle;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * SeriesElementBuilder Class. This class is used to construct the SVG elements necessary for drawing a fire history series.
 * 
 * @author Joshua Brogan and Peter Brewer
 */
public class SeriesElementBuilder {
	
	/**
	 * Returns a recorder line that is pre-configured. You will probably need to change the x1 y1 x2 y2 attributes to your liking.
	 * 
	 * @param doc
	 * @param svgNS
	 * @return recorderLine
	 */
	protected static Element getRecorderLine(Document doc, String svgNS) {
		
		Element recorderLine = doc.createElementNS(svgNS, "line");
		
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
	 * @param svgNS
	 * @param firstChartYear
	 * @param lastChartYear
	 * @return nonRecorderLine
	 */
	protected static Element getNonRecorderLine(Document doc, String svgNS, int firstChartYear, int lastChartYear) {
		
		Element nonRecorderLine = doc.createElementNS(svgNS, "line");
		
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
	 * @param svgNS
	 * @param color
	 * @return fireYearMarker
	 */
	protected static Element getFireYearMarker(Document doc, String svgNS, Color color) {
		
		Element fireYearMarker = doc.createElementNS(svgNS, "rect");
		
		fireYearMarker.setAttributeNS(null, "x", "0");
		fireYearMarker.setAttributeNS(null, "y", "0");
		fireYearMarker.setAttributeNS(null, "width", "1");
		fireYearMarker.setAttributeNS(null, "height", Integer.toString(FireChartSVG.SERIES_HEIGHT));
		fireYearMarker.setAttributeNS(null, "fill", FireChartConversionUtil.colorToHexString(color));
		fireYearMarker.setAttributeNS(null, "stroke", FireChartConversionUtil.colorToHexString(color));
		
		return fireYearMarker;
	}
	
	/**
	 * Returns an injury year marker element based on the input width and color.
	 * 
	 * @param doc
	 * @param svgNS
	 * @param width
	 * @param color
	 * @return injuryYearMarker
	 */
	protected static Element getInjuryYearMarker(Document doc, String svgNS, int width, Color color) {
		
		Element injuryYearMarker = doc.createElementNS(svgNS, "rect");
		
		injuryYearMarker.setAttributeNS(null, "x", "0");
		injuryYearMarker.setAttributeNS(null, "y", "0");
		injuryYearMarker.setAttributeNS(null, "width", Integer.toString(width));
		injuryYearMarker.setAttributeNS(null, "height", Integer.toString(FireChartSVG.SERIES_HEIGHT));
		injuryYearMarker.setAttributeNS(null, "fill", "none");
		injuryYearMarker.setAttributeNS(null, "stroke", FireChartConversionUtil.colorToHexString(color));
		
		return injuryYearMarker;
	}
	
	/**
	 * Returns an inner-year pith marker based on the input pith value, height, and color.
	 * 
	 * @param doc
	 * @param svgNS
	 * @param hasPith
	 * @param height
	 * @param color
	 * @return innerYearPithMarker
	 */
	protected static Element getInnerYearPithMarker(Document doc, String svgNS, boolean hasPith, int height, Color color) {
		
		if (hasPith)
		{
			Element pithMarker = doc.createElementNS(svgNS, "rect");
			
			pithMarker.setAttributeNS(null, "x", "0");
			pithMarker.setAttributeNS(null, "y", Integer.toString(-height / 2));
			pithMarker.setAttributeNS(null, "width", "1");
			pithMarker.setAttributeNS(null, "height", Integer.toString(height));
			pithMarker.setAttributeNS(null, "fill", FireChartConversionUtil.colorToHexString(color));
			pithMarker.setAttributeNS(null, "stroke", FireChartConversionUtil.colorToHexString(color));
			
			return pithMarker;
		}
		else
		{
			Element noPithMarker = doc.createElementNS(svgNS, "polygon");
			
			noPithMarker.setAttributeNS(null, "points", "-2,0.5 5,-5 2,0.5");
			noPithMarker.setAttributeNS(null, "fill", FireChartConversionUtil.colorToHexString(color));
			noPithMarker.setAttributeNS(null, "stroke", FireChartConversionUtil.colorToHexString(color));
			
			return noPithMarker;
		}
	}
	
	/**
	 * Returns an outer-year bark marker based on the input bark value, height, and color.
	 * 
	 * @param doc
	 * @param svgNS
	 * @param hasBark
	 * @param height
	 * @param color
	 * @return outerYearBarkMarker
	 */
	protected static Element getOuterYearBarkMarker(Document doc, String svgNS, boolean hasBark, int height, Color color) {
		
		if (hasBark)
		{
			Element barkMarker = doc.createElementNS(svgNS, "rect");
			
			barkMarker.setAttributeNS(null, "x", "0");
			barkMarker.setAttributeNS(null, "y", Integer.toString(-height / 2));
			barkMarker.setAttributeNS(null, "width", "1");
			barkMarker.setAttributeNS(null, "height", Integer.toString(height));
			barkMarker.setAttributeNS(null, "fill", FireChartConversionUtil.colorToHexString(color));
			barkMarker.setAttributeNS(null, "stroke", FireChartConversionUtil.colorToHexString(color));
			
			return barkMarker;
		}
		else
		{
			Element noBarkMarker = doc.createElementNS(svgNS, "polygon");
			
			noBarkMarker.setAttributeNS(null, "points", "2,0.5 -5,-5 -2,0.5");
			noBarkMarker.setAttributeNS(null, "fill", FireChartConversionUtil.colorToHexString(color));
			noBarkMarker.setAttributeNS(null, "stroke", FireChartConversionUtil.colorToHexString(color));
			
			return noBarkMarker;
		}
	}
}
