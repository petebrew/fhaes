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
import java.awt.FontMetrics;
import java.util.ArrayList;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.fhaes.enums.LineStyle;
import org.fhaes.fhfilereader.AbstractFireHistoryReader;
import org.fhaes.model.FHSeries;
import org.fhaes.neofhchart.FHSeriesSVG;
import org.fhaes.preferences.App;
import org.fhaes.preferences.FHAESPreferences.PrefKey;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;

/**
 * DocumentBuilder Class.
 * 
 * @author Joshua Brogan and Peter Brewer
 */
public class DocumentBuilder {
	
	// Declare local variables
	private static boolean showFires = true;
	private static boolean showInjuries = true;
	private static boolean showPith = true;
	private static boolean showBark = true;
	private static boolean showInnerRing = true;
	private static boolean showOuterRing = true;
	
	/**
	 * TODO
	 * 
	 * @param doc
	 * @param svgNS
	 * @param series
	 * @return
	 */
	protected static Element buildSingleSeries(Document doc, String svgNS, FHSeriesSVG series, int seriesHeight, int chartWidth,
			int firstChartYear, int lastChartYear) {
			
		Element series_group = doc.createElementNS(svgNS, "g");
		series_group.setAttributeNS(null, "id", series.getTitle());
		
		// draw in the recording and non-recording lines
		Element line_group = doc.createElementNS(svgNS, "g");
		boolean[] recording_years = series.getRecordingYears();
		
		int begin_index = 0;
		int last_index = recording_years.length - 1;
		if (recording_years.length != 0)
		{
			if (series.getLastYear() > lastChartYear)
			{
				last_index = (recording_years.length) - (series.getLastYear() - lastChartYear);
			}
			
			boolean isRecording = recording_years[0];
			for (int j = 0; j <= last_index; j++)
			{
				if (isRecording != recording_years[j] || j == last_index)
				{ // need to draw a line
					Element series_line = isRecording ? getRecorderLine(doc, svgNS)
							: getNonRecorderLine(doc, svgNS, firstChartYear, lastChartYear);
					series_line.setAttributeNS(null, "x1", Integer.toString(begin_index));
					series_line.setAttributeNS(null, "y1", "0");
					series_line.setAttributeNS(null, "stroke", ConversionUtil.getColorAsHex(series.getLineColor()));
					
					// Following kludge fixes but I don't understand why.
					/*
					 * if (j == recording_years.length - 2) { series_line.setAttributeNS(null, "x2", Integer.toString(j + 1)); } else {
					 */
					series_line.setAttributeNS(null, "x2", Integer.toString(j));
					// }
					series_line.setAttributeNS(null, "y2", "0");
					line_group.appendChild(series_line);
					begin_index = j;
					isRecording = recording_years[j];
				}
			}
		}
		series_group.appendChild(line_group);
		
		// add in fire events
		
		if (showFires)
		{
			Element series_fire_events = doc.createElementNS(svgNS, "g");
			boolean[] fire_years = series.getEventYears();
			for (int j = 0; j < fire_years.length; j++)
			{
				if (fire_years[j] && j <= last_index)
				{
					Element fire_event_g = doc.createElementNS(svgNS, "g");
					fire_event_g.setAttributeNS(null, "class", "fire_marker");
					fire_event_g.setAttributeNS(null, "stroke", ConversionUtil.getColorAsHex(series.getLineColor()));
					String translate = "translate("
							+ Double.toString(j - ConversionUtil.pixelsToYears(0.5, chartWidth, firstChartYear, lastChartYear)) + ","
							+ Integer.toString(-seriesHeight / 2) + ")";
					fire_event_g.setAttributeNS(null, "transform",
							translate + "scale(" + ConversionUtil.pixelsToYears(chartWidth, firstChartYear, lastChartYear) + ",1)");
					fire_event_g.appendChild(getFireYearMarker(doc, svgNS, seriesHeight, series.getLineColor()));
					series_fire_events.appendChild(fire_event_g);
				}
			}
			series_group.appendChild(series_fire_events);
		}
		
		// add in injury events
		if (showInjuries)
		{
			Element series_injury_events = doc.createElementNS(svgNS, "g");
			boolean[] injury_years = series.getInjuryYears();
			for (int j = 0; j < injury_years.length; j++)
			{
				if (injury_years[j] && j <= last_index)
				{
					Element injury_event_g = doc.createElementNS(svgNS, "g");
					injury_event_g.setAttributeNS(null, "class", "injury_marker");
					injury_event_g.setAttributeNS(null, "stroke", ConversionUtil.getColorAsHex(series.getLineColor()));
					String transform = "translate("
							+ Double.toString(j - ConversionUtil.pixelsToYears(1.5, chartWidth, firstChartYear, lastChartYear)) + ","
							+ Integer.toString(-seriesHeight / 2) + ")";
					String scale = "scale(" + ConversionUtil.pixelsToYears(chartWidth, firstChartYear, lastChartYear) + ",1)";
					injury_event_g.setAttributeNS(null, "transform", transform + " " + scale);
					injury_event_g.appendChild(getInjuryYearMarker(doc, svgNS, 3, seriesHeight, series.getLineColor()));
					
					series_injury_events.appendChild(injury_event_g);
				}
			}
			series_group.appendChild(series_injury_events);
		}
		
		// add in inner year pith marker
		if (showPith && series.hasPith() || showInnerRing && !series.hasPith())
		{
			if (series.getFirstYear() >= firstChartYear)
			{
				Element pith_marker_g = doc.createElementNS(svgNS, "g");
				// no translation because the inner year is at year=0
				String translate = "translate(" + (0 - ConversionUtil.pixelsToYears(0.5, chartWidth, firstChartYear, lastChartYear))
						+ ",0)";
				pith_marker_g.setAttributeNS(null, "transform",
						translate + "scale(" + ConversionUtil.pixelsToYears(chartWidth, firstChartYear, lastChartYear) + ",1)");
				pith_marker_g.appendChild(getInnerYearPithMarker(doc, svgNS, series.hasPith(), 5, series.getLineColor()));
				series_group.appendChild(pith_marker_g);
			}
		}
		
		// add in outer year bark marker
		if ((showBark && series.hasBark()) || (showOuterRing && !series.hasBark()))
		{
			if (series.getLastYear() <= lastChartYear)
			{
				Element bark_marker_g = doc.createElementNS(svgNS, "g");
				String translate = "translate(" + (series.getLastYear() - series.getFirstYear()) + ",0)"; // minus one because the
				bark_marker_g.setAttribute("transform",
						translate + " scale(" + ConversionUtil.pixelsToYears(chartWidth, firstChartYear, lastChartYear) + ",1)");
				bark_marker_g.appendChild(getOuterYearBarkMarker(doc, svgNS, series.hasBark(), 5, series.getLineColor()));
				series_group.appendChild(bark_marker_g);
			}
		}
		
		return series_group;
	}
	
	/**
	 * TODO
	 * 
	 * @return
	 */
	protected static Element getChronologyPlot(Document doc, String svgNS, AbstractFireHistoryReader reader, int widestChronologyLabelSize,
			String fontFamily, int seriesHeight, int chartWidth, int firstChartYear, int lastChartYear) {
			
		Element chronologyPlot = doc.createElementNS(svgNS, "g");
		chronologyPlot.setAttributeNS(null, "id", "chronology_plot");
		chronologyPlot.setAttributeNS(null, "display", "inline");
		
		// build all of the series
		ArrayList<Boolean> series_visible = new ArrayList<Boolean>();
		ArrayList<FHSeriesSVG> series_arr = ConversionUtil.convertToFHSeriesSVGList(reader.getSeriesList());
		
		showPith = App.prefs.getBooleanPref(PrefKey.CHART_SHOW_PITH_SYMBOL, true);
		showBark = App.prefs.getBooleanPref(PrefKey.CHART_SHOW_BARK_SYMBOL, true);
		showFires = App.prefs.getBooleanPref(PrefKey.CHART_SHOW_FIRE_EVENT_SYMBOL, true);
		showInjuries = App.prefs.getBooleanPref(PrefKey.CHART_SHOW_INJURY_SYMBOL, true);
		showInnerRing = App.prefs.getBooleanPref(PrefKey.CHART_SHOW_INNER_RING_SYMBOL, true);
		showOuterRing = App.prefs.getBooleanPref(PrefKey.CHART_SHOW_OUTER_RING_SYMBOL, true);
		int fontsize = App.prefs.getIntPref(PrefKey.CHART_CHRONOLOGY_PLOT_LABEL_FONT_SIZE, 8);
		
		String longestLabel = "A";
		for (int i = 0; i < series_arr.size(); i++)
		{
			FHSeries s = series_arr.get(i);
			if (s.getTitle().length() > longestLabel.length())
				longestLabel = s.getTitle();
		}
		
		widestChronologyLabelSize = getStringWidth(fontFamily, Font.PLAIN,
				App.prefs.getIntPref(PrefKey.CHART_CHRONOLOGY_PLOT_LABEL_FONT_SIZE, 10), longestLabel);
				
		for (int i = 0; i < series_arr.size(); i++)
		{
			series_visible.add(true);
			FHSeriesSVG series = series_arr.get(i);
			
			Element series_group = doc.createElementNS(svgNS, "g");
			series_group.setAttributeNS(null, "id", "series_group_" + series.getTitle());
			
			// add in the series group, which has the lines and ticks
			Element series_line = buildSingleSeries(doc, svgNS, series, seriesHeight, chartWidth, firstChartYear, lastChartYear);
			series_line.setAttributeNS(null, "id", "series_line_" + series.getTitle());
			int x_offset = series.getFirstYear() - firstChartYear;
			String translate_string = "translate(" + Integer.toString(x_offset) + ",0)";
			String scale_string = "scale(" + ConversionUtil.yearsToPixels(chartWidth, firstChartYear, lastChartYear) + ",1)";
			series_line.setAttributeNS(null, "transform", scale_string + " " + translate_string);
			
			// add in the label for the series
			Text series_name_text = doc.createTextNode(series.getTitle());
			Element series_name = doc.createElementNS(svgNS, "text");
			series_name.setAttribute("id", "series_label_" + series.getTitle());
			series_name.setAttribute("x", Double.toString(chartWidth + 10));
			series_name.setAttribute("y", Integer.toString((seriesHeight / 2)));
			series_name.setAttribute("font-family", fontFamily);
			series_name.setAttribute("font-size", +fontsize + "");
			series_name.setAttribute("fill", ConversionUtil.getColorAsHex(series.getLabelColor()));
			series_name.appendChild(series_name_text);
			
			// add in the up/down buttons
			Element up_button_g = doc.createElementNS(svgNS, "g");
			up_button_g.setAttributeNS(null, "id", "up_button" + i);
			up_button_g.setAttributeNS(null, "class", "no_export");
			up_button_g.setAttributeNS(null, "transform",
					"translate(" + Double.toString(chartWidth + 15 + widestChronologyLabelSize) + ",-2)");
			up_button_g.setAttributeNS(null, "onclick", "FireChartSVG.getChart(chart_num).moveSeriesUp(\"" + series.getTitle()
					+ "\"); evt.target.setAttribute('opacity', '0.2');");
			up_button_g.setAttributeNS(null, "onmouseover", "evt.target.setAttribute('opacity', '1');");
			up_button_g.setAttributeNS(null, "onmouseout", "evt.target.setAttribute('opacity', '0.2');");
			
			Element up_button = doc.createElementNS(svgNS, "polygon");
			up_button.setAttributeNS(null, "points", "2,8 2,4 0,4 4,0 8,4 8,4 6,4 6,8");
			up_button.setAttributeNS(null, "fill", "black");
			up_button.setAttributeNS(null, "opacity", "0.2");
			up_button_g.appendChild(up_button);
			
			Element down_button_g = doc.createElementNS(svgNS, "g");
			down_button_g.setAttributeNS(null, "id", "down_button" + i);
			down_button_g.setAttributeNS(null, "class", "no_export");
			down_button_g.setAttributeNS(null, "transform",
					"translate(" + Double.toString(chartWidth + 10 + widestChronologyLabelSize + 15) + ",-2)");
			down_button_g.setAttributeNS(null, "onclick", "FireChartSVG.getChart(chart_num).moveSeriesDown(\"" + series.getTitle()
					+ "\"); evt.target.setAttribute('opacity', '0.2');");
			down_button_g.setAttributeNS(null, "onmouseover", "evt.target.setAttribute('opacity', '1');");
			down_button_g.setAttributeNS(null, "onmouseout", "evt.target.setAttribute('opacity', '0.2');");
			Element down_button = doc.createElementNS(svgNS, "polygon");
			down_button.setAttributeNS(null, "points", "2,0 2,4 0,4 4,8 8,4 8,4 6,4 6,0");
			down_button.setAttributeNS(null, "fill", "black");
			down_button.setAttributeNS(null, "opacity", "0.2");
			down_button_g.appendChild(down_button);
			
			if (App.prefs.getBooleanPref(PrefKey.CHART_SHOW_CHRONOLOGY_PLOT_LABELS, true))
			{
				series_name.setAttributeNS(null, "display", "inline");
				up_button_g.setAttributeNS(null, "display", "inline");
				down_button_g.setAttributeNS(null, "display", "inline");
			}
			else
			{
				series_name.setAttributeNS(null, "display", "none");
				up_button_g.setAttributeNS(null, "display", "none");
				down_button_g.setAttributeNS(null, "display", "none");
			}
			
			series_group.appendChild(series_line);
			series_group.appendChild(series_name);
			series_group.appendChild(up_button_g);
			series_group.appendChild(down_button_g);
			chronologyPlot.appendChild(series_group);
		}
		
		if (App.prefs.getBooleanPref(PrefKey.CHART_SHOW_CHRONOLOGY_PLOT, true))
		{
			chronologyPlot.setAttributeNS(null, "display", "inline");
		}
		else
		{
			chronologyPlot.setAttributeNS(null, "display", "none");
		}
		
		return chronologyPlot;
	}
	
	/**
	 * Returns a recorder line that is pre-configured. You will probably need to change the x1 y1 x2 y2 attributes to your liking.
	 * 
	 * @return
	 */
	private static Element getRecorderLine(Document doc, String svgNS) {
		
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
	 * @return
	 */
	private static Element getNonRecorderLine(Document doc, String svgNS, int firstChartYear, int lastChartYear) {
		
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
	 * TODO
	 * 
	 * @param height
	 * @return
	 */
	private static Element getFireYearMarker(Document doc, String svgNS, int height, Color color) {
		
		Element fire_event = doc.createElementNS(svgNS, "rect");
		fire_event.setAttributeNS(null, "x", "0");
		fire_event.setAttributeNS(null, "y", "0");
		fire_event.setAttributeNS(null, "width", "1");
		fire_event.setAttributeNS(null, "height", Integer.toString(height));
		fire_event.setAttributeNS(null, "fill", ConversionUtil.getColorAsHex(color));
		fire_event.setAttributeNS(null, "stroke", ConversionUtil.getColorAsHex(color));
		return fire_event;
	}
	
	/**
	 * TODO
	 * 
	 * @param width
	 * @param height
	 * @return
	 */
	private static Element getInjuryYearMarker(Document doc, String svgNS, int width, int height, Color color) {
		
		Element injury_event = doc.createElementNS(svgNS, "rect");
		injury_event.setAttributeNS(null, "x", "0");
		injury_event.setAttributeNS(null, "y", "0");
		injury_event.setAttributeNS(null, "width", Integer.toString(width));
		injury_event.setAttributeNS(null, "height", Integer.toString(height));
		injury_event.setAttributeNS(null, "fill", "none");
		injury_event.setAttributeNS(null, "stroke", ConversionUtil.getColorAsHex(color));
		return injury_event;
	}
	
	/**
	 * TODO
	 * 
	 * @param hasPith
	 * @param height
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
			pith_marker.setAttributeNS(null, "fill", ConversionUtil.getColorAsHex(color));
			pith_marker.setAttributeNS(null, "stroke", ConversionUtil.getColorAsHex(color));
			return pith_marker;
		}
		else
		{
			Element no_pith_marker = doc.createElementNS(svgNS, "polygon");
			no_pith_marker.setAttributeNS(null, "points", "-2,0.5 5,-5 2,0.5");
			no_pith_marker.setAttributeNS(null, "fill", ConversionUtil.getColorAsHex(color));
			no_pith_marker.setAttributeNS(null, "stroke", ConversionUtil.getColorAsHex(color));
			return no_pith_marker;
		}
	}
	
	/**
	 * TODO
	 * 
	 * @param hasBark
	 * @param height
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
			bark_marker.setAttributeNS(null, "fill", ConversionUtil.getColorAsHex(color));
			bark_marker.setAttributeNS(null, "stroke", ConversionUtil.getColorAsHex(color));
			return bark_marker;
		}
		else
		{
			Element no_bark_marker = doc.createElementNS(svgNS, "polygon");
			no_bark_marker.setAttributeNS(null, "points", "2,0.5 -5,-5 -2,0.5");
			no_bark_marker.setAttributeNS(null, "fill", ConversionUtil.getColorAsHex(color));
			no_bark_marker.setAttributeNS(null, "stroke", ConversionUtil.getColorAsHex(color));
			return no_bark_marker;
		}
	}
	
	/**
	 * Get an approximate width for a string with the specified font. The should really be taken from the SVG but I haven't worked out a
	 * good way to do this without rendering first.
	 * 
	 * @param fontFamily
	 * @param fontSize
	 * @param fontStyle
	 * @param text
	 * @return
	 */
	protected static Integer getStringWidth(String fontFamily, int fontStyle, int fontSize, String text) {
		
		Font font = new Font(fontFamily, fontStyle, fontSize);
		
		JComponent graphics = new JLabel(text);
		FontMetrics metrics = graphics.getFontMetrics(font);
		int val = metrics.stringWidth(text);
		return val;
	}
	
	/**
	 * Get an approximate height for a string with the specified font. The should really be taken from the SVG but I haven't worked out a
	 * good way to do this without rendering first.
	 * 
	 * @param fontFamily
	 * @param fontSize
	 * @param fontStyle
	 * @param text
	 * @return
	 */
	protected static Integer getStringHeight(String fontFamily, int fontStyle, int fontSize, String text) {
		
		Font font = new Font(fontFamily, fontStyle, fontSize);
		
		JComponent graphics = new JPanel();
		FontMetrics metrics = graphics.getFontMetrics(font);
		return metrics.getMaxAscent();
	}
}
