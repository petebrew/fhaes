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
 * SeriesElementBuilder Class.
 * 
 * @author Joshua Brogan and Peter Brewer
 */
public class SeriesElementBuilder {
	
	/**
	 * Returns a recorder line that is pre-configured. You will probably need to change the x1 y1 x2 y2 attributes to your liking.
	 * 
	 * @param doc
	 * @param svgNS
	 * @return
	 */
	protected static Element getRecorderLine(Document doc, String svgNS) {
		
		Element recorder = doc.createElementNS(svgNS, "line");
		recorder.setAttributeNS(null, "x1", "0");
		recorder.setAttributeNS(null, "y1", "0");
		recorder.setAttributeNS(null, "x2", "10");
		recorder.setAttributeNS(null, "y2", "0");
		recorder.setAttributeNS(null, "stroke", "black");
		recorder.setAttributeNS(null, "stroke-width", "1");
		return recorder;
	}
	
	/**
	 * Returns a non-recorder line that is pre-configured. You will probably need to change the x1 y1 x2 y2 attributes to your liking.
	 * 
	 * @param doc
	 * @param svgNS
	 * @param firstChartYear
	 * @param lastChartYear
	 * @return
	 */
	protected static Element getNonRecorderLine(Document doc, String svgNS, int firstChartYear, int lastChartYear) {
		
		Element nonrecorder = doc.createElementNS(svgNS, "line");
		nonrecorder.setAttributeNS(null, "x1", "0");
		nonrecorder.setAttributeNS(null, "y1", "0");
		nonrecorder.setAttributeNS(null, "x2", "0");
		nonrecorder.setAttributeNS(null, "y2", "0");
		nonrecorder.setAttributeNS(null, "stroke", "black");
		nonrecorder.setAttributeNS(null, "stroke-width", "1");
		nonrecorder.setAttributeNS(null, "stroke-dasharray", LineStyle.DASHED.getCodeForChartYearCount(lastChartYear - firstChartYear));
		return nonrecorder;
	}
	
	/**
	 * Returns a fire year marker element based on the input height and color.
	 * 
	 * @param doc
	 * @param svgNS
	 * @param height
	 * @param color
	 * @return
	 */
	protected static Element getFireYearMarker(Document doc, String svgNS, int height, Color color) {
		
		Element fire_event = doc.createElementNS(svgNS, "rect");
		fire_event.setAttributeNS(null, "x", "0");
		fire_event.setAttributeNS(null, "y", "0");
		fire_event.setAttributeNS(null, "width", "1");
		fire_event.setAttributeNS(null, "height", Integer.toString(height));
		fire_event.setAttributeNS(null, "fill", FireChartConversionUtil.colorToHexString(color));
		fire_event.setAttributeNS(null, "stroke", FireChartConversionUtil.colorToHexString(color));
		return fire_event;
	}
	
	/**
	 * Returns an injury year marker element based on the input width, height, and color.
	 * 
	 * @param doc
	 * @param svgNS
	 * @param width
	 * @param height
	 * @param color
	 * @return
	 */
	protected static Element getInjuryYearMarker(Document doc, String svgNS, int width, int height, Color color) {
		
		Element injury_event = doc.createElementNS(svgNS, "rect");
		injury_event.setAttributeNS(null, "x", "0");
		injury_event.setAttributeNS(null, "y", "0");
		injury_event.setAttributeNS(null, "width", Integer.toString(width));
		injury_event.setAttributeNS(null, "height", Integer.toString(height));
		injury_event.setAttributeNS(null, "fill", "none");
		injury_event.setAttributeNS(null, "stroke", FireChartConversionUtil.colorToHexString(color));
		return injury_event;
	}
	
	/**
	 * Returns an inner-year pith marker based on the input pith value, height, and color.
	 * 
	 * @param doc
	 * @param svgNS
	 * @param hasPith
	 * @param height
	 * @param color
	 * @return
	 */
	protected static Element getInnerYearPithMarker(Document doc, String svgNS, boolean hasPith, int height, Color color) {
		
		if (hasPith)
		{
			Element pith_marker = doc.createElementNS(svgNS, "rect");
			pith_marker.setAttributeNS(null, "x", "0"); // inner year
			pith_marker.setAttributeNS(null, "y", Integer.toString(-height / 2));
			pith_marker.setAttributeNS(null, "width", "1");
			pith_marker.setAttributeNS(null, "height", Integer.toString(height));
			pith_marker.setAttributeNS(null, "fill", FireChartConversionUtil.colorToHexString(color));
			pith_marker.setAttributeNS(null, "stroke", FireChartConversionUtil.colorToHexString(color));
			return pith_marker;
		}
		else
		{
			Element no_pith_marker = doc.createElementNS(svgNS, "polygon");
			no_pith_marker.setAttributeNS(null, "points", "-2,0.5 5,-5 2,0.5");
			no_pith_marker.setAttributeNS(null, "fill", FireChartConversionUtil.colorToHexString(color));
			no_pith_marker.setAttributeNS(null, "stroke", FireChartConversionUtil.colorToHexString(color));
			return no_pith_marker;
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
	 * @return
	 */
	protected static Element getOuterYearBarkMarker(Document doc, String svgNS, boolean hasBark, int height, Color color) {
		
		if (hasBark)
		{
			Element bark_marker = doc.createElementNS(svgNS, "rect");
			bark_marker.setAttributeNS(null, "x", "0");
			bark_marker.setAttributeNS(null, "y", Integer.toString(-height / 2));
			bark_marker.setAttributeNS(null, "width", "1");
			bark_marker.setAttributeNS(null, "height", Integer.toString(height));
			bark_marker.setAttributeNS(null, "fill", FireChartConversionUtil.colorToHexString(color));
			bark_marker.setAttributeNS(null, "stroke", FireChartConversionUtil.colorToHexString(color));
			return bark_marker;
		}
		else
		{
			Element no_bark_marker = doc.createElementNS(svgNS, "polygon");
			no_bark_marker.setAttributeNS(null, "points", "2,0.5 -5,-5 -2,0.5");
			no_bark_marker.setAttributeNS(null, "fill", FireChartConversionUtil.colorToHexString(color));
			no_bark_marker.setAttributeNS(null, "stroke", FireChartConversionUtil.colorToHexString(color));
			return no_bark_marker;
		}
	}
}
