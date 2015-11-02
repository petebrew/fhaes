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
 * SampleRecorderPlotElementBuilder Class. This class is used to construct the SVG elements necessary for drawing the sample or recorder
 * depths plot.
 * 
 * @author Joshua Brogan and Peter Brewer
 */
public class SampleRecorderPlotElementBuilder {
	
	/**
	 * Returns a depth count text element based on the input parameters.
	 * 
	 * @param doc
	 * @param labelY
	 * @param fontSize
	 * @param tickNum
	 * @param tickSpacing
	 * @return depthCountTextElement
	 */
	protected static Element getDepthCountTextElement(Document doc, int labelY, int fontSize, int tickNum, int tickSpacing) {
		
		Text depthCountText = doc.createTextNode(Integer.toString(tickNum * tickSpacing));
		
		Element depthCountTextElement = doc.createElementNS(SVGDOMImplementation.SVG_NAMESPACE_URI, "text");
		depthCountTextElement.setAttributeNS(null, "x", "-3");
		depthCountTextElement.setAttributeNS(null, "y", labelY + "");
		depthCountTextElement.setAttributeNS(null, "font-family", App.prefs.getPref(PrefKey.CHART_FONT_FAMILY, "Verdana"));
		depthCountTextElement.setAttributeNS(null, "font-size", fontSize + "");
		depthCountTextElement.setAttributeNS(null, "text-anchor", "end");
		depthCountTextElement.appendChild(depthCountText);
		
		return depthCountTextElement;
	}
	
	/**
	 * Returns a sample depth text element based on the input parameters.
	 * 
	 * @param doc
	 * @return sampleDepthTextElement
	 */
	protected static Element getSampleDepthTextElement(Document doc) {
		
		Text sampleDepthText = doc.createTextNode(App.prefs.getPref(PrefKey.CHART_AXIS_Y1_LABEL, "Sample Depth"));
		
		Element sampleDepthTextElement = doc.createElementNS(SVGDOMImplementation.SVG_NAMESPACE_URI, "text");
		sampleDepthTextElement.setAttributeNS(null, "x", "0");
		sampleDepthTextElement.setAttributeNS(null, "y", "0");
		sampleDepthTextElement.setAttributeNS(null, "font-family", App.prefs.getPref(PrefKey.CHART_FONT_FAMILY, "Verdana"));
		sampleDepthTextElement.setAttributeNS(null, "font-size", App.prefs.getIntPref(PrefKey.CHART_AXIS_Y1_FONT_SIZE, 10) + "");
		sampleDepthTextElement.appendChild(sampleDepthText);
		
		return sampleDepthTextElement;
	}
	
	/**
	 * Returns a horizontal tick to be used in the sample or recorder depths plot.
	 * 
	 * @param doc
	 * @param unscaleY
	 * @param tickNum
	 * @param tickSpacing
	 * @return horizontalTick
	 */
	protected static Element getHorizontalTick(Document doc, double unscaleY, int tickNum, int tickSpacing) {
		
		Element horizontalTick = doc.createElementNS(SVGDOMImplementation.SVG_NAMESPACE_URI, "line");
		horizontalTick.setAttributeNS(null, "x1", "-5");
		horizontalTick.setAttributeNS(null, "y1", Integer.toString(tickNum * tickSpacing));
		horizontalTick.setAttributeNS(null, "x2", "0");
		horizontalTick.setAttributeNS(null, "y2", Integer.toString(tickNum * tickSpacing));
		horizontalTick.setAttributeNS(null, "stroke", "black");
		horizontalTick.setAttributeNS(null, "stroke-width", Double.toString(0 - unscaleY));
		
		return horizontalTick;
	}
	
	/**
	 * Returns a horizontal trend line part to be used in the sample or recorder depths plot.
	 * 
	 * @param doc
	 * @param lineColor
	 * @param scaleY
	 * @param x1Position
	 * @param x2Position
	 * @param yPosition
	 * @return horizontalTrendLinePart
	 */
	protected static Element getHorizontalTrendLinePart(Document doc, String lineColor, double scaleY, int x1Position, int x2Position,
			int yPosition) {
			
		Element horizontalTrendLinePart = doc.createElementNS(SVGDOMImplementation.SVG_NAMESPACE_URI, "line");
		horizontalTrendLinePart.setAttributeNS(null, "x1", Double.toString(x1Position));
		horizontalTrendLinePart.setAttributeNS(null, "y1", Double.toString(yPosition));
		horizontalTrendLinePart.setAttributeNS(null, "x2", Double.toString(x2Position));
		horizontalTrendLinePart.setAttributeNS(null, "y2", Double.toString(yPosition));
		horizontalTrendLinePart.setAttributeNS(null, "stroke", lineColor);
		horizontalTrendLinePart.setAttributeNS(null, "stroke-width", Double.toString(-1.0 / scaleY));
		
		return horizontalTrendLinePart;
	}
	
	/**
	 * Returns a vertical trend line part to be used in the sample or recorder depths plot.
	 * 
	 * @param doc
	 * @param lineColor
	 * @param xPosition
	 * @param y1Position
	 * @param y2Position
	 * @param chartWidth
	 * @param firstChartYear
	 * @param lastChartYear
	 * @return verticalTrendLinePart
	 */
	protected static Element getVerticalTrendLinePart(Document doc, String lineColor, int xPosition, int y1Position, int y2Position,
			int chartWidth, int firstChartYear, int lastChartYear) {
			
		String strokeWidth = Double.toString(FireChartUtil.pixelsToYears(1, chartWidth, firstChartYear, lastChartYear));
		
		Element verticalTrendLinePart = doc.createElementNS(SVGDOMImplementation.SVG_NAMESPACE_URI, "line");
		verticalTrendLinePart.setAttributeNS(null, "x1", Double.toString(xPosition));
		verticalTrendLinePart.setAttributeNS(null, "y1", Double.toString(y1Position));
		verticalTrendLinePart.setAttributeNS(null, "x2", Double.toString(xPosition));
		verticalTrendLinePart.setAttributeNS(null, "y2", Double.toString(y2Position));
		verticalTrendLinePart.setAttributeNS(null, "stroke", lineColor);
		verticalTrendLinePart.setAttributeNS(null, "stroke-width", strokeWidth);
		
		return verticalTrendLinePart;
	}
	
	/**
	 * Returns a threshold line to be used in the sample or recorder depths plot.
	 * 
	 * @param doc
	 * @param scaleY
	 * @param largestSampleDepth
	 * @param firstChartYear
	 * @param lastChartYear
	 * @return thresholdLine
	 */
	protected static Element getThresholdLine(Document doc, double scaleY, int largestSampleDepth, int firstChartYear, int lastChartYear) {
		
		int thresholdSampleDepthValue = App.prefs.getIntPref(PrefKey.CHART_DEPTH_THRESHOLD_VALUE, 10);
		String stroke = FireChartUtil.colorToHexString(App.prefs.getColorPref(PrefKey.CHART_DEPTH_THRESHOLD_COLOR, Color.RED));
		
		Element thresholdLine = doc.createElementNS(SVGDOMImplementation.SVG_NAMESPACE_URI, "line");
		thresholdLine.setAttributeNS(null, "id", "threshold_line");
		thresholdLine.setAttributeNS(null, "x1", Integer.toString(0));
		thresholdLine.setAttributeNS(null, "y1", Double.toString(thresholdSampleDepthValue));
		thresholdLine.setAttributeNS(null, "x2", Integer.toString(lastChartYear - firstChartYear));
		thresholdLine.setAttributeNS(null, "y2", Double.toString(thresholdSampleDepthValue));
		thresholdLine.setAttributeNS(null, "stroke", stroke);
		thresholdLine.setAttributeNS(null, "stroke-width", Double.toString(-1.0 / scaleY));
		
		if (!App.prefs.getBooleanPref(PrefKey.CHART_SHOW_DEPTH_THRESHOLD, false) || thresholdSampleDepthValue > largestSampleDepth
				|| thresholdSampleDepthValue < 0)
		{
			thresholdLine.setAttributeNS(null, "display", "none");
		}
		
		return thresholdLine;
	}
}
