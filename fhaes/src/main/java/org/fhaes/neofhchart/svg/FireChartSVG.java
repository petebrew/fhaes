/**************************************************************************************************
 * Fire History Analysis and Exploration System (FHAES), Copyright (C) 2015
 * 
 * Contributors: Aaron Decker, Michael Ababio, Zachariah Ferree, Matthew Willie, Peter Brewer
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
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.batik.bridge.BridgeException;
import org.apache.batik.dom.svg.SVGDOMImplementation;
import org.apache.batik.dom.svg.SVGLocatableSupport;
import org.fhaes.enums.AnnoteMode;
import org.fhaes.enums.EventTypeToProcess;
import org.fhaes.enums.FireFilterType;
import org.fhaes.enums.LabelOrientation;
import org.fhaes.enums.LineStyle;
import org.fhaes.fhfilereader.AbstractFireHistoryReader;
import org.fhaes.model.FHSeries;
import org.fhaes.neofhchart.FHSeriesSVG;
import org.fhaes.preferences.App;
import org.fhaes.preferences.FHAESPreferences.PrefKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.w3c.dom.svg.SVGRect;

/**
 * FireChartSVG Class. Graphs a fire history chart as an svg using FHUtil's AbstractFireHistoryReader.
 * 
 * @author Aaron Decker, Michael Ababio, Zachariah Ferree, Matthew Willie, Peter Brewer
 */
public class FireChartSVG {
	
	// Declare logger
	private static final Logger log = LoggerFactory.getLogger(FireChartSVG.class);
	
	// Declare DOMImplementation (this is the Document Object Model API which is used for creating SVG documents)
	private DOMImplementation impl = SVGDOMImplementation.getDOMImplementation();
	
	// Declare SVG document namespace (this is what tells the API that we are creating an SVG document)
	private String svgNS = SVGDOMImplementation.SVG_NAMESPACE_URI;
	
	// Declare SVG document instance (this is the actual SVG document)
	private Document doc = impl.createDocument(svgNS, "svg", null);
	
	// Declare local variables
	private AbstractFireHistoryReader reader;
	private int seriesHeight = 10;
	private int tickHeight = 10;
	private int chartXOffset = 50;
	private int chart_width = 1000;
	private int widestChronologyLabelSize = 0;
	private boolean showFires = true;
	private boolean showInjuries = true;
	private boolean showPith = true;
	private boolean showBark = true;
	private boolean showInnerRing = true;
	private boolean showOuterRing = true;
	private String fontFamily = null;
	private EventTypeToProcess fire_event_type = EventTypeToProcess.FIRE_AND_INJURY_EVENT;
	private AnnoteMode annotemode = AnnoteMode.NONE;
	public boolean traditionalData = false;
	private ArrayList<FHSeriesSVG> series_list = new ArrayList<FHSeriesSVG>();
	
	// Java <-> ECMAScript interop used for message passing with ECMAScript. Note not thread-safe
	private static int chart_counter = 0;
	private static int line_gensym = 0; // only used in drawRect -- I just need a unique id
	private static Map<Integer, FireChartSVG> chart_map;
	private int totalHeight = 0;
	private int chart_num;
	
	/**
	 * The constructor builds the DOM of the SVG.
	 * 
	 * @param f
	 */
	public FireChartSVG(AbstractFireHistoryReader f) {
		
		updateFontFamily();
		
		// assign number for message passing from ECMAscript
		chart_num = chart_counter;
		chart_counter++;
		
		if (chart_map == null)
		{
			chart_map = new HashMap<Integer, FireChartSVG>();
		}
		chart_map.put(chart_num, this);
		
		reader = f;
		
		ArrayList<FHSeriesSVG> temp_list = FireChartConversionUtil.seriesListToSeriesSVGList(f.getSeriesList());
		if (!series_list.isEmpty())
		{
			series_list.clear();
		}
		for (int i = 0; i < temp_list.size(); i++)
		{
			try
			{
				series_list.add(new FHSeriesSVG(temp_list.get(i)));
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
		
		Element svgRoot = doc.getDocumentElement();
		
		// Set up the scripts for Java / ECMAScript interop
		Element script = doc.createElementNS(svgNS, "script");
		script.setAttributeNS(null, "type", "text/ecmascript");
		
		try
		{
			// File script_file = new File("./script.js");
			
			ClassLoader cl = org.fhaes.neofhchart.svg.FireChartSVG.class.getClassLoader();
			Scanner scanner = new Scanner(cl.getResourceAsStream("script.js"));
			
			String script_string = "";
			while (scanner.hasNextLine())
			{
				script_string += scanner.nextLine();
			}
			script_string += ("var chart_num = " + chart_num + ";");
			Text script_text = doc.createTextNode(script_string);
			script.appendChild(script_text);
			scanner.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		svgRoot.appendChild(script);
		
		// The padding_grouper is used to add in some padding around the chart as a whole
		Element padding_grouper = doc.createElementNS(svgNS, "g");
		padding_grouper.setAttributeNS(null, "id", "padding_g");
		padding_grouper.setAttributeNS(null, "transform", "translate (" + chartXOffset + ",20)");
		svgRoot.appendChild(padding_grouper);
		
		// Build grouper to hold annotation elements
		Element annote_g = doc.createElementNS(svgNS, "g");
		annote_g.setAttributeNS(null, "id", "annote_g");
		padding_grouper.appendChild(annote_g);
		
		// Build the time axis
		Element time_axis_g = doc.createElementNS(svgNS, "g");
		time_axis_g.setAttributeNS(null, "id", "time_axis_g");
		padding_grouper.appendChild(time_axis_g);
		
		// Build index plot
		Element index_plot_g = doc.createElementNS(svgNS, "g");
		index_plot_g.setAttributeNS(null, "id", "index_plot_g");
		padding_grouper.appendChild(index_plot_g);
		
		// Build chronology plot
		Element chrono_plot_g = doc.createElementNS(svgNS, "g");
		chrono_plot_g.setAttributeNS(null, "id", "chrono_plot_g");
		padding_grouper.appendChild(chrono_plot_g);
		
		// Build composite plot
		Element comp_plot_g = doc.createElementNS(svgNS, "g");
		comp_plot_g.setAttributeNS(null, "id", "comp_plot_g");
		padding_grouper.appendChild(comp_plot_g);
		
		// Build legend
		Element legend_g = doc.createElementNS(svgNS, "g");
		legend_g.setAttributeNS(null, "id", "legend_g");
		padding_grouper.appendChild(legend_g);
		
		buildElements();
		positionSeriesLines();
		positionChartGroupersAndDrawTimeAxis();
	};
	
	/**
	 * TODO
	 * 
	 * @return
	 */
	public Document getSVGDocument() {
		
		return doc;
	}
	
	/**
	 * TODO
	 * 
	 * @return
	 */
	public AbstractFireHistoryReader getReader() {
		
		return reader;
	}
	
	/**
	 * Get the name of the file being read.
	 * 
	 * @return
	 */
	public String getName() {
		
		return reader.getName();
	}

	/**
	 * TODO
	 * 
	 * @return
	 */
	public int getChartNum() {
		
		return chart_num;
	}

	/**
	 * Get the FireChartSVG with the specified ID.
	 * 
	 * @param id
	 * @return
	 */
	public static FireChartSVG getChart(Integer id) {
		
		return chart_map.get(id);
	}

	/**
	 * Save the current SVG to the specified file.
	 * 
	 * @param f
	 */
	public void saveSVGToDisk(File f) {
		
		if (!f.getAbsolutePath().toLowerCase().endsWith("svg"))
		{
			f = new File(f.getAbsolutePath() + ".svg");
		}
		
		try
		{
			if (!f.exists())
			{
				f.createNewFile();
			}
			FileOutputStream fstream = new FileOutputStream(f);
			printDocument(doc, fstream);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	/**
	 * Helper function that deletes all child tags of the specified element.
	 * 
	 * @param e
	 */
	private void deleteAllChildren(Element e) {
		
		if (e != null)
		{
			NodeList n = e.getChildNodes();
			for (int i = 0; i < n.getLength(); i++)
			{
				e.removeChild(n.item(i));
			}
		}
	}
	
	/**
	 * Get the first year in the chart. Will be the first year in the file, or the year specified by the user if different.
	 * 
	 * @return
	 */
	public int getFirstChartYear() {
		
		if (App.prefs.getBooleanPref(PrefKey.CHART_AXIS_X_AUTO_RANGE, true))
		{
			return reader.getFirstYear();
		}
		else
		{
			return App.prefs.getIntPref(PrefKey.CHART_AXIS_X_MIN, reader.getFirstYear());
		}
	}

	/**
	 * Get the last year in the chart. Will be the last year in the file, or the year specified by the user if different.
	 * 
	 * @return
	 */
	public int getLastChartYear() {
		
		if (App.prefs.getBooleanPref(PrefKey.CHART_AXIS_X_AUTO_RANGE, true))
		{
			return reader.getLastYear();
		}
		else
		{
			return App.prefs.getIntPref(PrefKey.CHART_AXIS_X_MAX, reader.getLastYear());
		}
	}

	/**
	 * TODO
	 * 
	 * @return
	 */
	public int getTotalWidth() {
		
		return chart_width + this.widestChronologyLabelSize + 300;
	}

	/**
	 * TODO
	 * 
	 * @return
	 */
	public int getTotalHeight() {
		
		return totalHeight;
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
	private Integer getStringWidth(String fontFamily, int fontStyle, int fontSize, String text) {
		
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
	private Integer getStringHeight(String fontFamily, int fontStyle, int fontSize, String text) {
		
		Font font = new Font(fontFamily, fontStyle, fontSize);
		
		JComponent graphics = new JPanel();
		FontMetrics metrics = graphics.getFontMetrics(font);
		return metrics.getMaxAscent();
	}

	/**
	 * This function returns the up-to-date list of series.
	 * 
	 * @return the current list of series
	 */
	public ArrayList<FHSeriesSVG> getCurrentSeriesList() {
		
		return series_list;
	}
	
	/**
	 * This method swaps the selected series with the series above it.
	 * 
	 * @param series_name: Name of the series to move up
	 */
	public void moveSeriesUp(String series_name) {
		
		int i = 0;
		
		for (i = 0; i < series_list.size() && !series_list.get(i).getTitle().equals(series_name); i++)
		{
			; // loop until the index of the desired series is found
		}
		
		if (i > 0)
		{
			do
			{
				FHSeries s = series_list.get(i);
				series_list.set(i, series_list.get(i - 1));
				try
				{
					series_list.set(i - 1, new FHSeriesSVG(s));
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
				i--;
				positionSeriesLines();
			}
			while (i > 0 && !series_list.get(i + 1).isVisible());
		}
	}
	
	/**
	 * This method swaps the selected series with the series below it.
	 * 
	 * @param series_name: Name of the series to move down
	 */
	public void moveSeriesDown(String series_name) {
		
		int i = 0;
		
		for (i = 0; i < series_list.size() && !series_list.get(i).getTitle().equals(series_name); i++)
		{
			; // loop until the index of the desired series is found
		}
		
		if (i < series_list.size() - 1)
		{
			do
			{
				FHSeries s = series_list.get(i);
				series_list.set(i, series_list.get(i + 1));
				try
				{
					series_list.set(i + 1, new FHSeriesSVG(s));
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
				i++;
				positionSeriesLines();
			}
			while (i < series_list.size() - 1 && !series_list.get(i - 1).isVisible());
		}
	}
	
	/**
	 * TODO
	 */
	private void positionSeriesLines() {
		
		int series_spacing_and_height = App.prefs.getIntPref(PrefKey.CHART_CHRONOLOGY_PLOT_SPACING, 5) + seriesHeight;
		int hidden = 0;
		for (int i = 0; i < series_list.size(); i++)
		{
			FHSeries s = series_list.get(i);
			Element series_group = doc.getElementById("series_group_" + s.getTitle());
			String visibility_string = series_list.get(i).isVisible() ? "inline" : "none";
			if (series_list.get(i).isVisible())
			{
				series_group.setAttributeNS(null, "transform",
						"translate(0," + Integer.toString((i - hidden) * series_spacing_and_height) + ")");
			}
			else
			{
				hidden++;
			}
			
			series_group.setAttributeNS(null, "display", visibility_string);
		}
	}

	/**
	 * Positions the various parts of the fire chart. This method also re-creates the time axis since it is dependent on the total height of
	 * the svg, due to the dashed tick lines that run vertically to denote years.
	 */
	public void positionChartGroupersAndDrawTimeAxis() {
		
		// Calculate plot dimensions
		int cur_bottom = 0; // used for tracking where the bottom of the chart is as it is being built
		int index_plot_height = App.prefs.getIntPref(PrefKey.CHART_INDEX_PLOT_HEIGHT, 100);
		int series_spacing_and_height = App.prefs.getIntPref(PrefKey.CHART_CHRONOLOGY_PLOT_SPACING, 5) + seriesHeight;
		
		if (App.prefs.getBooleanPref(PrefKey.CHART_SHOW_INDEX_PLOT, true))
		{
			cur_bottom += index_plot_height + series_spacing_and_height;
		}
		
		int chronology_plot_y = cur_bottom;
		int num_visible = 0;
		for (int i = 0; i < series_list.size(); i++)
		{
			if (series_list.get(i).isVisible())
			{
				num_visible++;
			}
		}
		
		int chronology_plot_height = num_visible * series_spacing_and_height + seriesHeight;
		if (App.prefs.getBooleanPref(PrefKey.CHART_SHOW_CHRONOLOGY_PLOT, true))
		{
			cur_bottom += chronology_plot_height + series_spacing_and_height;
		}
		
		int composite_plot_y = cur_bottom;
		int composite_plot_height = App.prefs.getIntPref(PrefKey.CHART_COMPOSITE_HEIGHT, 70);
		if (App.prefs.getBooleanPref(PrefKey.CHART_SHOW_COMPOSITE_PLOT, true))
		{
			cur_bottom += composite_plot_height + series_spacing_and_height;
		}
		
		int total_height = cur_bottom + series_spacing_and_height;
		
		// reset svg dimensions
		Element svgRoot = doc.getDocumentElement();
		
		// build time axis
		Element time_axis_g = doc.getElementById("time_axis_g");
		// delete everything in the current time axis
		NodeList n = time_axis_g.getChildNodes(); // because getChildNodes doesn't return a seq
		for (int i = 0; i < n.getLength(); i++)
		{ // no, instead we get a non-iterable custom data-structure :(
			time_axis_g.removeChild(n.item(i));
		}
		// add in the new time axis
		time_axis_g.appendChild(getTimeAxis(total_height));
		
		// set the translations for the chart groupers
		Element chrono_plot_g = doc.getElementById("chrono_plot_g");
		chrono_plot_g.setAttributeNS(null, "transform", "translate(0," + chronology_plot_y + ")");
		
		Element comp_plot_g = doc.getElementById("comp_plot_g");
		comp_plot_g.setAttributeNS(null, "transform", "translate(0," + composite_plot_y + ")");
		
		// move the legend
		Element legend_g = doc.getElementById("legend_g");
		legend_g.setAttributeNS(null, "transform",
				"translate(" + (chart_width + 10 + this.widestChronologyLabelSize + 50) + ", " + 0 + ")");
				
		// set the annote canvas dimensions (so it can catch key bindings)
		Element annote_canvas = doc.getElementById("annote_canvas");
		annote_canvas.setAttributeNS(null, "width", Integer.toString(chart_width));
		annote_canvas.setAttributeNS(null, "height", Integer.toString(total_height));
		
		// set document dimensions for png and pdf export
		// svgRoot.setAttributeNS(null, "width", (chart_width + this.widestChronologyLabelSize + 150 + 350) + "px");
		svgRoot.setAttributeNS(null, "width", getTotalWidth() + "px");
		
		int root_height = (total_height + 50 > 400) ? total_height + 50 : 400;
		totalHeight = root_height;
		svgRoot.setAttributeNS(null, "height", root_height + "px");
	}

	/**
	 * Clear out the groupers and build the chart components.
	 */
	public void buildElements() {
		
		updateFontFamily();
		
		// Build annotation canvas
		Element annote_g = doc.getElementById("annote_g");
		deleteAllChildren(annote_g);
		Element canvas = getAnnoteCanvas();
		if (canvas != null)
		{
			annote_g.appendChild(canvas);
		}
		
		// Build index plot
		Element index_plot_g = doc.getElementById("index_plot_g");
		deleteAllChildren(index_plot_g);
		index_plot_g.appendChild(getIndexPlot());
		
		// Build chronology plot
		Element chrono_plot_g = doc.getElementById("chrono_plot_g");
		deleteAllChildren(chrono_plot_g);
		chrono_plot_g.appendChild(getChronologyPlot());
		positionSeriesLines();
		
		// Build composite plot
		Element comp_plot_g = doc.getElementById("comp_plot_g");
		deleteAllChildren(comp_plot_g);
		comp_plot_g.appendChild(getCompositePlot());
		
		// Build legend
		Element legend_g = doc.getElementById("legend_g");
		deleteAllChildren(legend_g);
		legend_g.appendChild(getLegend());
		
		positionChartGroupersAndDrawTimeAxis();
	}
	
	/**
	 * TODO
	 * 
	 * @param s
	 * @return
	 */
	private Element buildSingleSeries(FHSeriesSVG s) {
		
		Element series_group = doc.createElementNS(svgNS, "g");
		series_group.setAttributeNS(null, "id", s.getTitle());
		
		// draw in the recording and non-recording lines
		Element line_group = doc.createElementNS(svgNS, "g");
		boolean[] recording_years = s.getRecordingYears();
		
		int begin_index = 0;
		int last_index = recording_years.length - 1;
		if (recording_years.length != 0)
		{
			if (s.getLastYear() > this.getLastChartYear())
			{
				last_index = (recording_years.length) - (s.getLastYear() - this.getLastChartYear());
			}
			
			boolean isRecording = recording_years[0];
			for (int j = 0; j <= last_index; j++)
			{
				if (isRecording != recording_years[j] || j == last_index)
				{ // need to draw a line
					Element series_line = isRecording ? SeriesElementBuilder.getRecorderLine(doc, svgNS)
							: SeriesElementBuilder.getNonRecorderLine(doc, svgNS, getFirstChartYear(), getLastChartYear());
					series_line.setAttributeNS(null, "x1", Integer.toString(begin_index));
					series_line.setAttributeNS(null, "y1", "0");
					series_line.setAttributeNS(null, "stroke", FireChartConversionUtil.colorToHexString(s.getLineColor()));
					
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
			boolean[] fire_years = s.getEventYears();
			for (int j = 0; j < fire_years.length; j++)
			{
				if (fire_years[j] && j <= last_index)
				{
					Element fire_event_g = doc.createElementNS(svgNS, "g");
					fire_event_g.setAttributeNS(null, "class", "fire_marker");
					fire_event_g.setAttributeNS(null, "stroke", FireChartConversionUtil.colorToHexString(s.getLineColor()));
					String translate = "translate("
							+ Double.toString(
									j - FireChartConversionUtil.pixelsToYears(0.5, chart_width, getFirstChartYear(), getLastChartYear()))
							+ "," + Integer.toString(-seriesHeight / 2) + ")";
					fire_event_g.setAttributeNS(null, "transform", translate + "scale("
							+ FireChartConversionUtil.pixelsToYears(chart_width, getFirstChartYear(), getLastChartYear()) + ",1)");
					fire_event_g.appendChild(SeriesElementBuilder.getFireYearMarker(doc, svgNS, seriesHeight, s.getLineColor()));
					series_fire_events.appendChild(fire_event_g);
				}
			}
			series_group.appendChild(series_fire_events);
		}
		
		// add in injury events
		if (showInjuries)
		{
			Element series_injury_events = doc.createElementNS(svgNS, "g");
			boolean[] injury_years = s.getInjuryYears();
			for (int j = 0; j < injury_years.length; j++)
			{
				if (injury_years[j] && j <= last_index)
				{
					Element injury_event_g = doc.createElementNS(svgNS, "g");
					injury_event_g.setAttributeNS(null, "class", "injury_marker");
					injury_event_g.setAttributeNS(null, "stroke", FireChartConversionUtil.colorToHexString(s.getLineColor()));
					String transform = "translate("
							+ Double.toString(
									j - FireChartConversionUtil.pixelsToYears(1.5, chart_width, getFirstChartYear(), getLastChartYear()))
							+ "," + Integer.toString(-seriesHeight / 2) + ")";
					String scale = "scale(" + FireChartConversionUtil.pixelsToYears(chart_width, getFirstChartYear(), getLastChartYear())
							+ ",1)";
					injury_event_g.setAttributeNS(null, "transform", transform + " " + scale);
					injury_event_g.appendChild(SeriesElementBuilder.getInjuryYearMarker(doc, svgNS, 3, seriesHeight, s.getLineColor()));
					
					series_injury_events.appendChild(injury_event_g);
				}
			}
			series_group.appendChild(series_injury_events);
		}
		
		// add in inner year pith marker
		if (showPith && s.hasPith() || showInnerRing && !s.hasPith())
		{
			if (s.getFirstYear() >= this.getFirstChartYear())
			{
				Element pith_marker_g = doc.createElementNS(svgNS, "g");
				// no translation because the innery year is at year=0
				String translate = "translate("
						+ (0 - FireChartConversionUtil.pixelsToYears(0.5, chart_width, getFirstChartYear(), getLastChartYear())) + ",0)";
				pith_marker_g.setAttributeNS(null, "transform", translate + "scale("
						+ FireChartConversionUtil.pixelsToYears(chart_width, getFirstChartYear(), getLastChartYear()) + ",1)");
				pith_marker_g.appendChild(SeriesElementBuilder.getInnerYearPithMarker(doc, svgNS, s.hasPith(), 5, s.getLineColor()));
				series_group.appendChild(pith_marker_g);
			}
		}
		
		// add in outer year bark marker
		if ((showBark && s.hasBark()) || (showOuterRing && !s.hasBark()))
		{
			if (s.getLastYear() <= this.getLastChartYear())
			{
				Element bark_marker_g = doc.createElementNS(svgNS, "g");
				String translate = "translate(" + (s.getLastYear() - s.getFirstYear()) + ",0)"; // minus one because the
				bark_marker_g.setAttribute("transform", translate + " scale("
						+ FireChartConversionUtil.pixelsToYears(chart_width, getFirstChartYear(), getLastChartYear()) + ",1)");
				bark_marker_g.appendChild(SeriesElementBuilder.getOuterYearBarkMarker(doc, svgNS, s.hasBark(), 5, s.getLineColor()));
				series_group.appendChild(bark_marker_g);
			}
		}
		
		return series_group;
	}
	
	/**
	 * Get the index plot.
	 * 
	 * @return
	 */
	private Element getIndexPlot() {
		
		Element index_g = doc.createElementNS(svgNS, "g");
		index_g.setAttribute("id", "indexplot");
		
		index_g.appendChild(getSampleOrRecorderDepthsPlot(App.prefs.getBooleanPref(PrefKey.CHART_SHOW_SAMPLE_DEPTH, false)));
		index_g.appendChild(getPercentScarredPlot());
		
		return index_g;
	}
	
	/**
	 * TODO
	 * 
	 * @return
	 */
	private Element getChronologyPlot() {
		
		Element chronologyPlot = doc.createElementNS(svgNS, "g");
		chronologyPlot.setAttributeNS(null, "id", "chronology_plot");
		chronologyPlot.setAttributeNS(null, "display", "inline");
		
		// build all of the series
		ArrayList<Boolean> series_visible = new ArrayList<Boolean>();
		ArrayList<FHSeriesSVG> series_arr = FireChartConversionUtil.seriesListToSeriesSVGList(reader.getSeriesList());
		
		this.showPith = App.prefs.getBooleanPref(PrefKey.CHART_SHOW_PITH_SYMBOL, true);
		this.showBark = App.prefs.getBooleanPref(PrefKey.CHART_SHOW_BARK_SYMBOL, true);
		this.showFires = App.prefs.getBooleanPref(PrefKey.CHART_SHOW_FIRE_EVENT_SYMBOL, true);
		this.showInjuries = App.prefs.getBooleanPref(PrefKey.CHART_SHOW_INJURY_SYMBOL, true);
		this.showInnerRing = App.prefs.getBooleanPref(PrefKey.CHART_SHOW_INNER_RING_SYMBOL, true);
		this.showOuterRing = App.prefs.getBooleanPref(PrefKey.CHART_SHOW_OUTER_RING_SYMBOL, true);
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
			FHSeriesSVG s = series_arr.get(i);
			
			Element series_group = doc.createElementNS(svgNS, "g");
			series_group.setAttributeNS(null, "id", "series_group_" + s.getTitle());
			
			// add in the series group, which has the lines and ticks
			Element series_line = buildSingleSeries(s);
			series_line.setAttributeNS(null, "id", "series_line_" + s.getTitle());
			int x_offset = s.getFirstYear() - getFirstChartYear();
			String translate_string = "translate(" + Integer.toString(x_offset) + ",0)";
			String scale_string = "scale(" + FireChartConversionUtil.yearsToPixels(chart_width, getFirstChartYear(), getLastChartYear())
					+ ",1)";
			series_line.setAttributeNS(null, "transform", scale_string + " " + translate_string);
			
			// add in the label for the series
			Text series_name_text = doc.createTextNode(s.getTitle());
			Element series_name = doc.createElementNS(svgNS, "text");
			series_name.setAttribute("id", "series_label_" + s.getTitle());
			series_name.setAttribute("x", Double.toString(chart_width + 10));
			series_name.setAttribute("y", Integer.toString((seriesHeight / 2)));
			series_name.setAttribute("font-family", fontFamily);
			series_name.setAttribute("font-size", +fontsize + "");
			series_name.setAttribute("fill", FireChartConversionUtil.colorToHexString(s.getLabelColor()));
			series_name.appendChild(series_name_text);
			
			// add in the up/down buttons
			Element up_button_g = doc.createElementNS(svgNS, "g");
			up_button_g.setAttributeNS(null, "id", "up_button" + i);
			up_button_g.setAttributeNS(null, "class", "no_export");
			up_button_g.setAttributeNS(null, "transform",
					"translate(" + Double.toString(chart_width + 15 + widestChronologyLabelSize) + ",-2)");
			up_button_g.setAttributeNS(null, "onclick",
					"FireChartSVG.getChart(chart_num).moveSeriesUp(\"" + s.getTitle() + "\"); evt.target.setAttribute('opacity', '0.2');");
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
					"translate(" + Double.toString(chart_width + 10 + widestChronologyLabelSize + 15) + ",-2)");
			down_button_g.setAttributeNS(null, "onclick", "FireChartSVG.getChart(chart_num).moveSeriesDown(\"" + s.getTitle()
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
	 * Get the composite plot.
	 * 
	 * @return
	 */
	private Element getCompositePlot() {
		
		boolean useAbbreviatedYears = App.prefs.getBooleanPref(PrefKey.CHART_COMPOSITE_YEAR_LABELS_TWO_DIGIT, false);
		
		String textStr = "2099";
		
		if (useAbbreviatedYears)
		{
			textStr = "'99";
		}
		
		int compositeYearLabelFontSize = App.prefs.getIntPref(PrefKey.CHART_COMPOSITE_YEAR_LABEL_FONT_SIZE, 8);
		int rotateLabelsAngle = App.prefs.getLabelOrientationPref(PrefKey.CHART_COMPOSITE_LABEL_ALIGNMENT, LabelOrientation.HORIZONTAL)
				.getAngle();
		int stringbuffer = App.prefs.getIntPref(PrefKey.CHART_COMPOSITE_YEAR_LABEL_BUFFER, 5);
		
		int compositeYearLabelMaxWidth = 0;
		int compositeYearLabelHeight = 0;
		
		if (rotateLabelsAngle == 0)
		{
			compositeYearLabelMaxWidth = getStringWidth(fontFamily, Font.PLAIN, compositeYearLabelFontSize, textStr) + stringbuffer;
			compositeYearLabelHeight = getStringHeight(fontFamily, Font.PLAIN, compositeYearLabelFontSize, textStr);
		}
		else if (rotateLabelsAngle == 315)
		{
			int width = getStringWidth(fontFamily, Font.PLAIN, compositeYearLabelFontSize, textStr);
			
			double widthsq = width * width;
			double hyp = Math.sqrt(widthsq + widthsq);
			
			compositeYearLabelMaxWidth = (int) ((hyp + stringbuffer)) / 2;
			compositeYearLabelHeight = (int) hyp;
		}
		else
		{
			compositeYearLabelMaxWidth = getStringHeight(fontFamily, Font.PLAIN, compositeYearLabelFontSize, textStr);
			compositeYearLabelHeight = getStringWidth(fontFamily, Font.PLAIN, compositeYearLabelFontSize, textStr) + stringbuffer;
		}
		
		// compositePlot is centered off of the year 0 A.D.
		Element composite_plot = doc.createElementNS(svgNS, "g");
		composite_plot.setAttributeNS(null, "id", "comp_plot");
		composite_plot.setAttributeNS(null, "transform",
				"scale(" + FireChartConversionUtil.yearsToPixels(chart_width, getFirstChartYear(), getLastChartYear()) + "," + 1
						+ ") translate(-" + getFirstChartYear() + ",0) ");
						
		// draw the vertical lines for fire years
		ArrayList<Integer> composite_years = reader.getCompositeFireYears(fire_event_type,
				App.prefs.getFireFilterTypePref(PrefKey.CHART_COMPOSITE_FILTER_TYPE, FireFilterType.NUMBER_OF_EVENTS),
				App.prefs.getIntPref(PrefKey.CHART_COMPOSITE_FILTER_VALUE, 1),
				App.prefs.getIntPref(PrefKey.CHART_COMPOSITE_MIN_NUM_SAMPLES, 1));
				
		// Remove out-of-range years if necessary
		if (!App.prefs.getBooleanPref(PrefKey.CHART_AXIS_X_AUTO_RANGE, true))
		{
			ArrayList<Integer> composite_years2 = new ArrayList<Integer>();
			for (Integer v : composite_years)
			{
				if (v > this.getLastChartYear() || v < this.getFirstChartYear())
					continue;
					
				composite_years2.add(v);
			}
			
			composite_years = composite_years2;
		}
		
		int overlap_margin = (compositeYearLabelMaxWidth);
		int cur_offset_level = 0;
		int prev_i = 0;
		int max_offset_level = 0;
		
		// run through the array once to see how many layers we need to keep year labels from overlapping
		boolean showLabels = App.prefs.getBooleanPref(PrefKey.CHART_SHOW_COMPOSITE_YEAR_LABELS, true);
		if (showLabels)
		{
			for (int i : composite_years)
			{
				double pixelsBetweenLabels = FireChartConversionUtil.yearsToPixels(i, chart_width, getFirstChartYear(), getLastChartYear())
						- FireChartConversionUtil.yearsToPixels(prev_i, chart_width, getFirstChartYear(), getLastChartYear());
				if (pixelsBetweenLabels < overlap_margin)
				{
					cur_offset_level = cur_offset_level + 1;
					if (cur_offset_level > max_offset_level)
					{
						max_offset_level = cur_offset_level;
					}
				}
				else
				{
					cur_offset_level = 0;
					prev_i = i;
				}
			}
		}
		else
		{
			cur_offset_level = 0;
		}
		
		int num_layers = max_offset_level + 1;
		// int num_layers = (max_offset_level > 0) ? max_offset_level : 1; // number of layers for putting the offset year labels
		double chart_height = App.prefs.getIntPref(PrefKey.CHART_COMPOSITE_HEIGHT, 70);
		
		if (num_layers > (chart_height / compositeYearLabelFontSize))
		{
			// ensure height is non-negative
			num_layers = (int) Math.floor(chart_height / compositeYearLabelHeight);
		}
		
		if (showLabels)
		{
			if (rotateLabelsAngle == 315)
			{
				chart_height = chart_height - (num_layers * (compositeYearLabelHeight / 3));
			}
			else
			{
				// height of the composite plot minus the year labels
				chart_height = chart_height - (num_layers * compositeYearLabelHeight);
			}
		}
		
		cur_offset_level = 0;
		prev_i = 0;
		for (int i : composite_years)
		{
			Element event_line = doc.createElementNS(svgNS, "line");
			event_line.setAttributeNS(null, "x1", Integer.toString(i));
			event_line.setAttributeNS(null, "x2", Integer.toString(i));
			event_line.setAttributeNS(null, "y1", "0");
			event_line.setAttributeNS(null, "y2", Double.toString(chart_height));
			event_line.setAttributeNS(null, "stroke-width",
					Double.toString(FireChartConversionUtil.pixelsToYears(1, chart_width, getFirstChartYear(), getLastChartYear())));
			event_line.setAttributeNS(null, "stroke", "black");
			composite_plot.appendChild(event_line);
			
			// calculate the offsets for the labels
			if (FireChartConversionUtil.yearsToPixels(i - prev_i, chart_width, getFirstChartYear(), getLastChartYear()) < overlap_margin)
			{
				cur_offset_level = (cur_offset_level + 1) % num_layers;
			}
			else
			{
				cur_offset_level = 0;
			}
			
			Element text_g = doc.createElementNS(svgNS, "g");
			String scale_str = "scale(" + FireChartConversionUtil.pixelsToYears(chart_width, getFirstChartYear(), getLastChartYear())
					+ ", 1)";
					
			if (rotateLabelsAngle == 270)
			{
				double offset = chart_height + (cur_offset_level * compositeYearLabelHeight) + compositeYearLabelHeight;
				String translate_str = "translate(" + (Double.toString(i + (FireChartConversionUtil
						.pixelsToYears(compositeYearLabelMaxWidth / 2, chart_width, getFirstChartYear(), getLastChartYear())))) + ","
						+ offset + ")";
				text_g.setAttributeNS(null, "transform", translate_str + scale_str + " rotate(" + rotateLabelsAngle + ")");
			}
			else if (rotateLabelsAngle == 315)
			{
				double offset = chart_height + (cur_offset_level * (compositeYearLabelHeight / 3)) + compositeYearLabelHeight / 1.3;
				String translate_str = "translate(" + (Double.toString(i - (FireChartConversionUtil
						.pixelsToYears(compositeYearLabelMaxWidth / 2, chart_width, getFirstChartYear(), getLastChartYear())))) + ","
						+ offset + ")";
				text_g.setAttributeNS(null, "transform", translate_str + scale_str + " rotate(" + rotateLabelsAngle + ")");
			}
			else
			{
				double offset = chart_height + (cur_offset_level * compositeYearLabelHeight) + compositeYearLabelHeight;
				String translate_str = "translate(" + (Double.toString(i - (FireChartConversionUtil
						.pixelsToYears(compositeYearLabelMaxWidth / 2, chart_width, getFirstChartYear(), getLastChartYear())))) + ","
						+ offset + ")";
				text_g.setAttributeNS(null, "transform", translate_str + scale_str);
			}
			
			// Grab label string depending on style
			String year_str = "";
			if (useAbbreviatedYears)
			{
				year_str = "'";
				if (i % 100 < 10)
				{
					year_str += "0";
				}
				year_str += Integer.toString(i % 100);
			}
			else
			{
				year_str = Integer.toString(i);
			}
			
			Text year_text_t = doc.createTextNode(year_str);
			Element year_text = doc.createElementNS(svgNS, "text");
			// year_text.setAttributeNS(null, "x", Integer.toString(i));
			// year_text.setAttributeNS(null, "x", "0");
			// year_text.setAttributeNS(null, "y", "0");
			year_text.setAttributeNS(null, "font-family", fontFamily);
			year_text.setAttributeNS(null, "font-size", Integer.toString(compositeYearLabelFontSize));
			
			if (showLabels)
			{
				year_text.appendChild(year_text_t);
				text_g.appendChild(year_text);
			}
			
			composite_plot.appendChild(text_g);
			prev_i = i;
		}
		
		// draw a rectangle around it
		// Needs to be 4 lines to cope with stroke width in different coord sys in x and y
		Element comp_line = doc.createElementNS(svgNS, "line");
		comp_line.setAttributeNS(null, "x1", Double.toString(getFirstChartYear()));
		comp_line.setAttributeNS(null, "x2", Double.toString(getLastChartYear()));
		comp_line.setAttributeNS(null, "y1", "0");
		comp_line.setAttributeNS(null, "y2", "0");
		comp_line.setAttributeNS(null, "stroke-width", "1");
		comp_line.setAttributeNS(null, "stroke", "black");
		comp_line.setAttributeNS(null, "stroke-linecap", "butt");
		composite_plot.appendChild(comp_line);
		
		Element comp_line2 = doc.createElementNS(svgNS, "line");
		comp_line2.setAttributeNS(null, "x1", Integer.toString(getFirstChartYear()));
		comp_line2.setAttributeNS(null, "x2", Integer.toString(getLastChartYear()));
		comp_line2.setAttributeNS(null, "y1", Double.toString(chart_height));
		comp_line2.setAttributeNS(null, "y2", Double.toString(chart_height));
		comp_line2.setAttributeNS(null, "stroke-width", "1");
		comp_line2.setAttributeNS(null, "stroke", "black");
		comp_line2.setAttributeNS(null, "stroke-linecap", "butt");
		composite_plot.appendChild(comp_line2);
		
		Element comp_line3 = doc.createElementNS(svgNS, "line");
		comp_line3.setAttributeNS(null, "x1", Integer.toString(getFirstChartYear()));
		comp_line3.setAttributeNS(null, "x2", Integer.toString(getFirstChartYear()));
		comp_line3.setAttributeNS(null, "y1", "0");
		comp_line3.setAttributeNS(null, "y2", Double.toString(chart_height));
		comp_line3.setAttributeNS(null, "stroke-width",
				FireChartConversionUtil.pixelsToYears(1, chart_width, getFirstChartYear(), getLastChartYear()) + "");
		comp_line3.setAttributeNS(null, "stroke", "black");
		comp_line3.setAttributeNS(null, "stroke-linecap", "butt");
		composite_plot.appendChild(comp_line3);
		
		Element comp_line4 = doc.createElementNS(svgNS, "line");
		comp_line4.setAttributeNS(null, "x1", Integer.toString(getLastChartYear()));
		comp_line4.setAttributeNS(null, "x2", Integer.toString(getLastChartYear()));
		comp_line4.setAttributeNS(null, "y1", "0");
		comp_line4.setAttributeNS(null, "y2", Double.toString(chart_height));
		comp_line4.setAttributeNS(null, "stroke-width",
				FireChartConversionUtil.pixelsToYears(1, chart_width, getFirstChartYear(), getLastChartYear()) + "");
		comp_line4.setAttributeNS(null, "stroke", "black");
		comp_line4.setAttributeNS(null, "stroke-linecap", "butt");
		composite_plot.appendChild(comp_line4);
		/*
		 * comp_line.setAttributeNS(null, "x", Integer.toString(getFirstChartYear())); comp_line.setAttributeNS(null, "y", "0");
		 * comp_line.setAttributeNS(null, "width", Integer.toString(getLastChartYear() - getFirstChartYear()));
		 * comp_line.setAttributeNS(null, "height", Double.toString(chart_height)); comp_line.setAttributeNS(null, "fill", "none");
		 * comp_line.setAttributeNS(null, "stroke", "black"); comp_line.setAttributeNS(null, "stroke-width",
		 * Double.toString(pixelsToYears(1))); composite_plot.appendChild(comp_line);
		 */
		
		// add the label
		Element comp_name_text_g = doc.createElementNS(svgNS, "g");
		String translate_string = "translate("
				+ Double.toString(getLastChartYear()
						+ FireChartConversionUtil.pixelsToYears(10, chart_width, getFirstChartYear(), getLastChartYear()))
				+ ", "
				+ ((chart_height / 2) + (this.getStringHeight(fontFamily, Font.PLAIN,
						App.prefs.getIntPref(PrefKey.CHART_COMPOSITE_PLOT_LABEL_FONT_SIZE, 10),
						App.prefs.getPref(PrefKey.CHART_COMPOSITE_LABEL_TEXT, "Composite"))) / 2)
				+ ")";
		String scale_string = "scale(" + FireChartConversionUtil.pixelsToYears(chart_width, getFirstChartYear(), getLastChartYear())
				+ ", 1)";
		comp_name_text_g.setAttributeNS(null, "transform", translate_string + scale_string);
		Text composite_name_text = doc.createTextNode(App.prefs.getPref(PrefKey.CHART_COMPOSITE_LABEL_TEXT, "Composite"));
		Element composite_name = doc.createElementNS(svgNS, "text");
		composite_name.setAttributeNS(null, "x", "0");
		composite_name.setAttributeNS(null, "y", "0");
		composite_name.setAttributeNS(null, "font-family", fontFamily);
		composite_name.setAttributeNS(null, "font-size",
				Integer.toString(App.prefs.getIntPref(PrefKey.CHART_COMPOSITE_PLOT_LABEL_FONT_SIZE, 10)));
		composite_name.appendChild(composite_name_text);
		comp_name_text_g.appendChild(composite_name);
		composite_plot.appendChild(comp_name_text_g);
		
		if (App.prefs.getBooleanPref(PrefKey.CHART_SHOW_COMPOSITE_PLOT, true))
		{
			composite_plot.setAttributeNS(null, "display", "inline");
		}
		else
		{
			composite_plot.setAttributeNS(null, "display", "none");
		}
		
		return composite_plot;
	}

	/**
	 * This function creates a legend dynamically, based on the current event(s) displayed on the canvas (Fire, Injury, or Fire and Injury).
	 */
	private Element getLegend() {
		
		int labelwidth = this.getStringWidth(fontFamily, Font.PLAIN, 8, "Outer year without bark");
		int labelHeight = this.getStringHeight(fontFamily, Font.PLAIN, 8, "Outer year without bark");
		int currentY = 0; // to help position symbols
		int moveValue = 20;
		int leftJustified = 20;
		
		Element legend = doc.createElementNS(svgNS, "g");
		legend.setAttributeNS(null, "id", "legend");
		// legend.setAttributeNS(null, "transform", "translate(" + chart_width + ", " + 200 + ")");
		
		// make a "g" for each element, and append to that, so each element is
		// in a group. then append groups to legend. This is so when the legend moves,
		// you wont have to manually move all the other elements again.
		
		// Get current symbols on canvas and append to legend
		
		// RECORDER YEAR
		Element recorder_g = doc.createElementNS(svgNS, "g");
		recorder_g.setAttributeNS(null, "id", "recorder");
		Element recorder = doc.createElementNS(svgNS, "line");
		recorder.setAttributeNS(null, "x1", "0");
		recorder.setAttributeNS(null, "y1", "0");
		recorder.setAttributeNS(null, "x2", "15");
		recorder.setAttributeNS(null, "y2", "0");
		recorder.setAttributeNS(null, "stroke", "black");
		recorder.setAttributeNS(null, "stroke-width", "1");
		recorder_g.appendChild(recorder);
		Element desc = createDescription("Recorder year", leftJustified, currentY + (labelHeight / 2));
		recorder_g.appendChild(desc);
		legend.appendChild(recorder_g);
		
		// NON-RECORDER YEAR
		currentY += moveValue;
		Element nonrecorder_g = doc.createElementNS(svgNS, "g");
		Element nonrecorder = doc.createElementNS(svgNS, "line");
		nonrecorder.setAttributeNS(null, "x1", "0");
		nonrecorder.setAttributeNS(null, "y1", Integer.toString(currentY));
		nonrecorder.setAttributeNS(null, "x2", "15");
		nonrecorder.setAttributeNS(null, "y2", Integer.toString(currentY));
		nonrecorder.setAttributeNS(null, "stroke", "black");
		nonrecorder.setAttributeNS(null, "stroke-width", "1");
		nonrecorder.setAttributeNS(null, "stroke-dasharray", LineStyle.DASHED.getCode());
		nonrecorder_g.appendChild(nonrecorder);
		desc = createDescription("Non-recorder year", leftJustified, currentY + (labelHeight / 2));
		nonrecorder_g.appendChild(desc);
		legend.appendChild(nonrecorder_g);
		
		// currentY += moveValue * 2; // so next symbol is at spot y = 100
		
		if (App.prefs.getBooleanPref(PrefKey.CHART_SHOW_FIRE_EVENT_SYMBOL, true))
		{
			// create a fire injury event
			currentY += moveValue;
			Element fireMarker_g = doc.createElementNS(svgNS, "g");
			Element fireMarker = SeriesElementBuilder.getFireYearMarker(doc, svgNS, 10, Color.BLACK);
			fireMarker.setAttributeNS(null, "width", "2");
			fireMarker_g.appendChild(fireMarker);
			fireMarker_g.setAttributeNS(null, "transform", "translate(0, " + currentY + ")");
			desc = createDescription("Fire event", leftJustified, (labelHeight / 2));
			fireMarker_g.appendChild(desc);
			legend.appendChild(fireMarker_g);
		}
		
		if (App.prefs.getBooleanPref(PrefKey.CHART_SHOW_INJURY_SYMBOL, true))
		{
			// create an injury event marker
			currentY += moveValue;
			Element injuryMarker_g = doc.createElementNS(svgNS, "g");
			Element injuryMarker = SeriesElementBuilder.getInjuryYearMarker(doc, svgNS, 3, 10, Color.BLACK);
			injuryMarker_g.appendChild(injuryMarker);
			injuryMarker_g.setAttributeNS(null, "transform", "translate(0, " + Integer.toString(currentY) + ")");
			desc = createDescription("Injury event", leftJustified, (labelHeight / 2));
			injuryMarker_g.appendChild(desc);
			legend.appendChild(injuryMarker_g);
		}
		
		// create inner year with pith
		currentY += moveValue;
		Element innerPith_g = doc.createElementNS(svgNS, "g");
		Element innerPith = SeriesElementBuilder.getInnerYearPithMarker(doc, svgNS, true, 5, Color.BLACK);
		innerPith_g.appendChild(innerPith);
		innerPith_g.setAttributeNS(null, "transform", "translate(0, " + Integer.toString(currentY) + ")");
		Element pithNonrecorder_g = doc.createElementNS(svgNS, "g");
		Element pithNonrecorder = doc.createElementNS(svgNS, "line");
		pithNonrecorder.setAttributeNS(null, "x1", "0");
		pithNonrecorder.setAttributeNS(null, "y1", "0");
		pithNonrecorder.setAttributeNS(null, "x2", "10");
		pithNonrecorder.setAttributeNS(null, "y2", "0");
		pithNonrecorder.setAttributeNS(null, "stroke", "black");
		pithNonrecorder.setAttributeNS(null, "stroke-width", "1");
		pithNonrecorder.setAttributeNS(null, "stroke-dasharray", LineStyle.DASHED.getCode());
		pithNonrecorder_g.appendChild(pithNonrecorder);
		innerPith_g.appendChild(pithNonrecorder_g);
		desc = createDescription("Inner year with pith", leftJustified, (labelHeight / 2));
		innerPith_g.appendChild(desc);
		legend.appendChild(innerPith_g);
		
		// create inner year without pith
		currentY += moveValue;
		Element withoutPith_g = doc.createElementNS(svgNS, "g");
		Element withoutPith = SeriesElementBuilder.getInnerYearPithMarker(doc, svgNS, false, 10, Color.BLACK);
		withoutPith_g.appendChild(withoutPith);
		withoutPith_g.setAttributeNS(null, "transform", "translate(0, " + Integer.toString(currentY) + ")");
		Element withoutPithNonrecorder_g = doc.createElementNS(svgNS, "g");
		Element withoutPithNonrecorder = doc.createElementNS(svgNS, "line");
		withoutPithNonrecorder.setAttributeNS(null, "x1", "0");
		withoutPithNonrecorder.setAttributeNS(null, "y1", "-0.5");
		withoutPithNonrecorder.setAttributeNS(null, "x2", "10");
		withoutPithNonrecorder.setAttributeNS(null, "y2", "-0.5");
		withoutPithNonrecorder.setAttributeNS(null, "stroke", "black");
		withoutPithNonrecorder.setAttributeNS(null, "stroke-width", "1");
		withoutPithNonrecorder.setAttributeNS(null, "stroke-dasharray", LineStyle.DASHED.getCode());
		withoutPithNonrecorder_g.appendChild(withoutPithNonrecorder);
		withoutPith_g.appendChild(withoutPithNonrecorder_g);
		desc = createDescription("Inner year without pith", leftJustified, (labelHeight / 2));
		withoutPith_g.appendChild(desc);
		legend.appendChild(withoutPith_g);
		
		// create outer year with bark
		currentY += moveValue;
		Element withBark_g = doc.createElementNS(svgNS, "g");
		Element withBark = SeriesElementBuilder.getOuterYearBarkMarker(doc, svgNS, true, 5, Color.BLACK);
		withBark_g.appendChild(withBark);
		withBark_g.setAttributeNS(null, "transform", "translate(5, " + Integer.toString(currentY) + ")");
		Element barkRecorder_g = doc.createElementNS(svgNS, "g");
		Element barkRecorder = doc.createElementNS(svgNS, "line");
		barkRecorder.setAttributeNS(null, "x1", "0");
		barkRecorder.setAttributeNS(null, "y1", "0");
		barkRecorder.setAttributeNS(null, "x2", "-8");
		barkRecorder.setAttributeNS(null, "y2", "0");
		barkRecorder.setAttributeNS(null, "stroke", "black");
		barkRecorder.setAttributeNS(null, "stroke-width", "1");
		barkRecorder_g.appendChild(barkRecorder);
		withBark_g.appendChild(barkRecorder_g);
		desc = createDescription("Outer year with bark", leftJustified - 5, (labelHeight / 2));
		withBark_g.appendChild(desc);
		legend.appendChild(withBark_g);
		
		// create outer year without bark
		currentY += moveValue;
		Element withoutBark_g = doc.createElementNS(svgNS, "g");
		Element withoutBark = SeriesElementBuilder.getOuterYearBarkMarker(doc, svgNS, false, 10, Color.BLACK);
		withoutBark_g.appendChild(withoutBark);
		withoutBark_g.setAttributeNS(null, "transform", "translate(5, " + Integer.toString(currentY) + ")");
		
		Element withoutBarkRecorder_g = doc.createElementNS(svgNS, "g");
		Element withoutBarkRecorder = doc.createElementNS(svgNS, "line");
		withoutBarkRecorder.setAttributeNS(null, "x1", "0");
		withoutBarkRecorder.setAttributeNS(null, "y1", "-0.5");
		withoutBarkRecorder.setAttributeNS(null, "x2", "-8");
		withoutBarkRecorder.setAttributeNS(null, "y2", "-0.5");
		withoutBarkRecorder.setAttributeNS(null, "stroke", "black");
		withoutBarkRecorder.setAttributeNS(null, "stroke-width", "1");
		withoutBarkRecorder_g.appendChild(withoutBarkRecorder);
		withoutBark_g.appendChild(withoutBarkRecorder_g);
		desc = createDescription("Outer year without bark", leftJustified - 5, (labelHeight / 2));
		withoutBark_g.appendChild(desc);
		legend.appendChild(withoutBark_g);
		
		// Add rectangle around legend and append
		legend.setAttributeNS(null, "transform", "scale(1.0)");
		Element chart_rect = doc.createElementNS(svgNS, "rect");
		chart_rect.setAttributeNS(null, "x", "-10");
		chart_rect.setAttributeNS(null, "y", "-10");
		chart_rect.setAttributeNS(null, "width", labelwidth + 40 + "");
		chart_rect.setAttributeNS(null, "height", Integer.toString(currentY + 20));
		chart_rect.setAttributeNS(null, "stroke", "black");
		chart_rect.setAttributeNS(null, "stroke-width", "0.5");
		chart_rect.setAttributeNS(null, "fill", "none");
		legend.appendChild(chart_rect);
		
		if (App.prefs.getBooleanPref(PrefKey.CHART_SHOW_LEGEND, true))
		{
			legend.setAttributeNS(null, "display", "inline");
		}
		else
		{
			legend.setAttributeNS(null, "display", "none");
		}
		
		return legend;
	}

	/**
	 * Set the visibility of the index plot based on the preferences.
	 */
	public void setIndexPlotVisibility() {
		
		boolean isVisible = App.prefs.getBooleanPref(PrefKey.CHART_SHOW_INDEX_PLOT, true);
		
		Element plot_grouper1 = doc.getElementById("scarred");
		Element plot_grouper2 = doc.getElementById("depths");
		if (!isVisible)
		{
			plot_grouper1.setAttributeNS(null, "display", "none");
			plot_grouper2.setAttributeNS(null, "display", "none");
		}
		else
		{
			plot_grouper1.setAttributeNS(null, "display", "inline");
			plot_grouper2.setAttributeNS(null, "display", "inline");
		}
		positionChartGroupersAndDrawTimeAxis();
	}

	/**
	 * Set the visibility of the chronology plot based on the preferences.
	 */
	public void setChronologyPlotVisibility() {
		
		boolean isVisible = App.prefs.getBooleanPref(PrefKey.CHART_SHOW_CHRONOLOGY_PLOT, true);
		
		Element plot_grouper = doc.getElementById("chronology_plot");
		if (!isVisible)
		{
			plot_grouper.setAttributeNS(null, "display", "none");
		}
		else
		{
			plot_grouper.setAttributeNS(null, "display", "inline");
		}
		positionChartGroupersAndDrawTimeAxis();
	}

	
	
	/*
	 * public boolean setCommonTickAttrib(int weight, Color color, LineStyle style) {
	 * 
	 * tickLineWeight = weight; tickLineStyle = style; setTickColor(color); positionChartGroupersAndDrawTimeAxis(); return false; }
	 */
	
	/**
	 * TODO
	 */
	public void setCompositePlotVisibility() {
		
		boolean isVisible = App.prefs.getBooleanPref(PrefKey.CHART_SHOW_COMPOSITE_PLOT, true);
		Element plot_grouper = doc.getElementById("comp_plot");
		if (!isVisible)
		{
			plot_grouper.setAttributeNS(null, "display", "none");
		}
		else
		{
			plot_grouper.setAttributeNS(null, "display", "inline");
		}
		positionChartGroupersAndDrawTimeAxis();
	}

	
	
	/*
	 * public boolean setCommonTickAttrib(int weight, Color color, LineStyle style) {
	 * 
	 * tickLineWeight = weight; tickLineStyle = style; setTickColor(color); positionChartGroupersAndDrawTimeAxis(); return false; }
	 */
	
	/**
	 * TODO
	 */
	public void setLegendVisibility() {
		
		boolean legendVisible = App.prefs.getBooleanPref(PrefKey.CHART_SHOW_LEGEND, true);
		Element legend = doc.getElementById("legend");
		if (legendVisible)
		{
			legend.setAttributeNS(null, "display", "inline");
		}
		else
		{
			legend.setAttributeNS(null, "display", "none");
		}
	}

	/**
	 * Aids in the creation of descriptions for the legend.
	 * 
	 * @param text The description to be entered
	 * @param xLoc The x-location of the text
	 * @param yLoc The y-location of the text
	 * @return A description element
	 */
	private Element createDescription(String text, int xLoc, int yLoc) {
		
		Text descriptionText = doc.createTextNode(text);
		Element desc = doc.createElementNS(svgNS, "text");
		desc.setAttributeNS(null, "x", Integer.toString(xLoc));
		desc.setAttributeNS(null, "y", Integer.toString(yLoc));
		desc.setAttributeNS(null, "font-family", fontFamily);
		desc.setAttributeNS(null, "font-size", Integer.toString(8));
		desc.appendChild(descriptionText);
		return desc;
	}

	/**
	 * Get the time axis including the guide and highlight lines.
	 * 
	 * @param height
	 * @return
	 */
	private Element getTimeAxis(int height) {
		
		// time axis is centered off of the first year in the reader
		Element timeAxis = doc.createElementNS(svgNS, "g");
		String scale = "scale(" + FireChartConversionUtil.yearsToPixels(chart_width, getFirstChartYear(), getLastChartYear()) + ",1)";
		timeAxis.setAttributeNS(null, "transform", scale + " translate(-" + this.getFirstChartYear() + ",0)");
		int majorTickInterval = App.prefs.getIntPref(PrefKey.CHART_XAXIS_MAJOR_TICK_SPACING, 50);
		int minorTickInterval = App.prefs.getIntPref(PrefKey.CHART_XAXIS_MINOR_TICK_SPACING, 10);
		boolean majorTicks = App.prefs.getBooleanPref(PrefKey.CHART_XAXIS_MAJOR_TICKS, true);
		boolean minorTicks = App.prefs.getBooleanPref(PrefKey.CHART_XAXIS_MINOR_TICKS, true);
		boolean vertGuides = App.prefs.getBooleanPref(PrefKey.CHART_VERTICAL_GUIDES, true);
		ArrayList<Integer> years = App.prefs.getIntegerArrayPref(PrefKey.CHART_HIGHLIGHT_YEARS_ARRAY, null);
		
		// Add highlight lines
		if (years != null && App.prefs.getBooleanPref(PrefKey.CHART_HIGHLIGHT_YEARS, false))
		{
			for (Integer i : years)
			{
				// Don't plot out of range years
				if (i > this.getLastChartYear() || i < this.getFirstChartYear())
					continue;
					
				Element highlight_line = doc.createElementNS(svgNS, "line");
				highlight_line.setAttributeNS(null, "x1", Integer.toString(i));
				highlight_line.setAttributeNS(null, "x2", Integer.toString(i));
				highlight_line.setAttributeNS(null, "y1", "0");
				highlight_line.setAttributeNS(null, "y2", Double.toString(height - (2 * tickHeight)));
				highlight_line.setAttributeNS(null, "stroke-width",
						Double.toString(App.prefs.getIntPref(PrefKey.CHART_HIGHLIGHT_YEARS_WEIGHT, 1)
								* FireChartConversionUtil.pixelsToYears(chart_width, getFirstChartYear(), getLastChartYear())));
				highlight_line.setAttributeNS(null, "stroke-dasharray",
						App.prefs.getLineStylePref(PrefKey.CHART_HIGHLIGHT_YEAR_STYLE, LineStyle.SOLID).getCode());
				highlight_line.setAttributeNS(null, "stroke", FireChartConversionUtil
						.colorToHexString(App.prefs.getColorPref(PrefKey.CHART_HIGHLIGHT_YEARS_COLOR, Color.YELLOW)));
				timeAxis.appendChild(highlight_line);
			}
		}
		
		for (int i = getFirstChartYear(); i < getLastChartYear(); i++)
		{
			if (i % majorTickInterval == 0)
			{ // year is a multiple of tickInterval
				if (vertGuides)
				{
					Element vertical_guide = doc.createElementNS(svgNS, "line");
					vertical_guide.setAttributeNS(null, "x1", Integer.toString(i));
					vertical_guide.setAttributeNS(null, "x2", Integer.toString(i));
					vertical_guide.setAttributeNS(null, "y1", "0");
					vertical_guide.setAttributeNS(null, "y2", Double.toString(height - (2 * tickHeight)));
					vertical_guide.setAttributeNS(null, "stroke-width",
							Double.toString(App.prefs.getIntPref(PrefKey.CHART_VERTICAL_GUIDE_WEIGHT, 1)
									* FireChartConversionUtil.pixelsToYears(chart_width, getFirstChartYear(), getLastChartYear())));
					vertical_guide.setAttributeNS(null, "stroke-dasharray",
							App.prefs.getLineStylePref(PrefKey.CHART_VERTICAL_GUIDE_STYLE, LineStyle.SOLID).getCode());
					vertical_guide.setAttributeNS(null, "stroke", FireChartConversionUtil
							.colorToHexString(App.prefs.getColorPref(PrefKey.CHART_VERTICAL_GUIDE_COLOR, Color.BLACK)));
					timeAxis.appendChild(vertical_guide);
				}
				
				if (majorTicks)
				{
					Element major_tick = doc.createElementNS(svgNS, "line");
					major_tick.setAttributeNS(null, "x1", Integer.toString(i));
					major_tick.setAttributeNS(null, "x2", Integer.toString(i));
					major_tick.setAttributeNS(null, "y1", Integer.toString(height - (2 * tickHeight)));
					major_tick.setAttributeNS(null, "y2", Integer.toString(height - (tickHeight)));
					major_tick.setAttributeNS(null, "stroke-width",
							Double.toString(App.prefs.getIntPref(PrefKey.CHART_VERTICAL_GUIDE_WEIGHT, 1)
									* FireChartConversionUtil.pixelsToYears(chart_width, getFirstChartYear(), getLastChartYear())));
					major_tick.setAttributeNS(null, "stroke-dasharray", LineStyle.SOLID.getCode());
					major_tick.setAttributeNS(null, "stroke", FireChartConversionUtil.colorToHexString(Color.BLACK));
					timeAxis.appendChild(major_tick);
				}
				
				Element year_text_g = doc.createElementNS(svgNS, "g");
				year_text_g.setAttributeNS(null, "transform", "translate(" + i + "," + height + ") scale("
						+ FireChartConversionUtil.pixelsToYears(chart_width, getFirstChartYear(), getLastChartYear()) + ",1)");
				Element year_text_holder = doc.createElementNS(svgNS, "text");
				Text year_text = doc.createTextNode(Integer.toString(i));
				year_text_holder.setAttributeNS(null, "x", "0");
				year_text_holder.setAttributeNS(null, "y", "0");
				year_text_holder.setAttributeNS(null, "font-family", fontFamily);
				year_text_holder.setAttributeNS(null, "font-size", "8");
				year_text_holder.appendChild(year_text);
				year_text_g.appendChild(year_text_holder);
				timeAxis.appendChild(year_text_g);
			}
			
			if (minorTicks && i % minorTickInterval == 0) // && i % tickInterval != 0)
			{
				Element minor_tick = doc.createElementNS(svgNS, "line");
				minor_tick.setAttributeNS(null, "x1", Integer.toString(i));
				minor_tick.setAttributeNS(null, "x2", Integer.toString(i));
				minor_tick.setAttributeNS(null, "y1", Integer.toString(height - (2 * tickHeight)));
				minor_tick.setAttributeNS(null, "y2", Double.toString(height - (1.5 * tickHeight)));
				minor_tick.setAttributeNS(null, "stroke-width", Double.toString(App.prefs.getIntPref(PrefKey.CHART_VERTICAL_GUIDE_WEIGHT, 1)
						* FireChartConversionUtil.pixelsToYears(chart_width, getFirstChartYear(), getLastChartYear())));
				minor_tick.setAttributeNS(null, "stroke-dasharray", LineStyle.SOLID.getCode());
				minor_tick.setAttributeNS(null, "stroke", FireChartConversionUtil.colorToHexString(Color.BLACK));
				timeAxis.appendChild(minor_tick);
			}
		}
		
		Element theaxis = doc.createElementNS(svgNS, "line");
		theaxis.setAttributeNS(null, "x1", this.getFirstChartYear() + "");
		theaxis.setAttributeNS(null, "x2", this.getLastChartYear() + "");
		theaxis.setAttributeNS(null, "y1", Integer.toString(height - (2 * seriesHeight)));
		theaxis.setAttributeNS(null, "y2", Integer.toString(height - (2 * seriesHeight)));
		theaxis.setAttributeNS(null, "stroke-dasharray", LineStyle.SOLID.getCode());
		theaxis.setAttributeNS(null, "stroke", FireChartConversionUtil.colorToHexString(Color.BLACK));
		timeAxis.appendChild(theaxis);
		
		return timeAxis;
	}

	/**
	 * Get the percent scarred plot including bounding box and y2 axis.
	 * 
	 * @return
	 */
	public Element getPercentScarredPlot() {
		
		// determine y scaling
		double scale_y = -1 * ((double) App.prefs.getIntPref(PrefKey.CHART_INDEX_PLOT_HEIGHT, 100)) / (100);
		double unscale_y = 1 / scale_y;
		
		Element scarred_g = doc.createElementNS(svgNS, "g");
		scarred_g.setAttributeNS(null, "id", "scarred");
		scarred_g.setAttributeNS(null, "transform",
				"translate(0," + App.prefs.getIntPref(PrefKey.CHART_INDEX_PLOT_HEIGHT, 100) + ") scale(1," + scale_y + ")");
				
		// only x-scale the drawn part -- not the labels
		Element scarred_scale_g = doc.createElementNS(svgNS, "g");
		scarred_scale_g.setAttributeNS(null, "transform",
				"scale(" + FireChartConversionUtil.yearsToPixels(chart_width, getFirstChartYear(), getLastChartYear()) + ",1)");
		scarred_g.appendChild(scarred_scale_g);
		
		// draw in vertical bars
		double[] percent_arr = reader.getPercentScarred(fire_event_type);
		
		// Limit to specified years if necessary
		if (!App.prefs.getBooleanPref(PrefKey.CHART_AXIS_X_AUTO_RANGE, true))
		{
			
			int startindex_file = this.getFirstChartYear() - reader.getFirstYear();
			double[] percent_file = percent_arr.clone();
			percent_arr = new double[(this.getLastChartYear() - this.getFirstChartYear()) + 1];
			
			int ind_in_file = startindex_file;
			for (int ind_in_newarr = 0; ind_in_newarr < percent_arr.length; ind_in_newarr++)
			{
				
				if (ind_in_file > percent_file.length - 1)
				{
					// Reached end of data we are extracting
					break;
				}
				
				if (ind_in_file < 0)
				{
					// Before data we are extracting so keep going
					ind_in_file++;
					continue;
				}
				
				percent_arr[ind_in_newarr] = percent_file[ind_in_file];
				ind_in_file++;
			}
		}
		
		for (int i = 0; i < percent_arr.length; i++)
		{
			if (percent_arr[i] != 0)
			{
				double percent = percent_arr[i];
				percent = (percent > 100) ? 100 : percent; // don't allow values over 100%
				Element vertical_line = doc.createElementNS(svgNS, "line");
				vertical_line.setAttributeNS(null, "x1", Integer.toString(i));
				vertical_line.setAttributeNS(null, "y1", "0");
				vertical_line.setAttributeNS(null, "x2", Integer.toString(i));
				vertical_line.setAttributeNS(null, "y2", Double.toString(percent));
				vertical_line.setAttributeNS(null, "stroke", "black");
				vertical_line.setAttributeNS(null, "stroke-width",
						Double.toString(FireChartConversionUtil.pixelsToYears(1, chart_width, getFirstChartYear(), getLastChartYear())));
				scarred_scale_g.appendChild(vertical_line);
			}
		}
		
		// draw in the bounding rectangle
		
		/*
		 * Element chart_rect = doc.createElementNS(svgNS, "rect"); chart_rect.setAttributeNS(null, "x", "0");
		 * chart_rect.setAttributeNS(null, "y", "0"); chart_rect.setAttributeNS(null, "width", Integer.toString(getLastChartYear() -
		 * getFirstChartYear())); chart_rect.setAttributeNS(null, "height", "100"); chart_rect.setAttributeNS(null, "stroke", "black");
		 * chart_rect.setAttributeNS(null, "stroke-width", Double.toString(pixelsToYears(1))); chart_rect.setAttributeNS(null, "fill",
		 * "none"); scarred_scale_g.appendChild(chart_rect);
		 */
		
		// draw a rectangle around it
		// Needs to be 4 lines to cope with stroke width in different coord sys in x and y
		Element comp_line = doc.createElementNS(svgNS, "line");
		comp_line.setAttributeNS(null, "x1", "0");
		comp_line.setAttributeNS(null, "y1", "0");
		comp_line.setAttributeNS(null, "x2", "0");
		comp_line.setAttributeNS(null, "y2", "100");
		comp_line.setAttributeNS(null, "stroke-width",
				FireChartConversionUtil.pixelsToYears(1, chart_width, getFirstChartYear(), getLastChartYear()) + "");
		comp_line.setAttributeNS(null, "stroke", "black");
		comp_line.setAttributeNS(null, "stroke-linecap", "butt");
		scarred_scale_g.appendChild(comp_line);
		
		Element comp_line2 = doc.createElementNS(svgNS, "line");
		comp_line2.setAttributeNS(null, "x1", "0");
		comp_line2.setAttributeNS(null, "y1", "100");
		comp_line2.setAttributeNS(null, "x2", Integer.toString(getLastChartYear() - getFirstChartYear()));
		comp_line2.setAttributeNS(null, "y2", "100");
		comp_line2.setAttributeNS(null, "stroke-width", 0 - unscale_y + "");
		comp_line2.setAttributeNS(null, "stroke", "black");
		comp_line2.setAttributeNS(null, "stroke-linecap", "butt");
		scarred_scale_g.appendChild(comp_line2);
		
		Element comp_line3 = doc.createElementNS(svgNS, "line");
		comp_line3.setAttributeNS(null, "x1", Integer.toString(getLastChartYear() - getFirstChartYear()));
		comp_line3.setAttributeNS(null, "y1", "100");
		comp_line3.setAttributeNS(null, "x2", Integer.toString(getLastChartYear() - getFirstChartYear()));
		comp_line3.setAttributeNS(null, "y2", "0");
		comp_line3.setAttributeNS(null, "stroke-width",
				FireChartConversionUtil.pixelsToYears(1, chart_width, getFirstChartYear(), getLastChartYear()) + "");
		comp_line3.setAttributeNS(null, "stroke", "black");
		comp_line3.setAttributeNS(null, "stroke-linecap", "butt");
		scarred_scale_g.appendChild(comp_line3);
		
		Element comp_line4 = doc.createElementNS(svgNS, "line");
		comp_line4.setAttributeNS(null, "x1", Integer.toString(getLastChartYear() - getFirstChartYear()));
		comp_line4.setAttributeNS(null, "y1", "0");
		comp_line4.setAttributeNS(null, "x2", "0");
		comp_line4.setAttributeNS(null, "y2", "0");
		comp_line4.setAttributeNS(null, "stroke-width", 0 - unscale_y + "");
		comp_line4.setAttributeNS(null, "stroke", "black");
		comp_line4.setAttributeNS(null, "stroke-linecap", "butt");
		scarred_scale_g.appendChild(comp_line4);
		
		// draw in the labels
		int yaxis_fontsize = App.prefs.getIntPref(PrefKey.CHART_AXIS_Y2_FONT_SIZE, 10);
		int labelHeight = this.getStringHeight(fontFamily, Font.PLAIN, yaxis_fontsize, "100");
		for (int i = 0; i <= 100; i += 25)
		{
			
			Text percent_tick_text_t = doc.createTextNode(Integer.toString(i));
			Element percent_tick_text = doc.createElementNS(svgNS, "text");
			Element unscale_g = doc.createElementNS(svgNS, "g");
			Element horiz_tick = doc.createElementNS(svgNS, "line");
			
			String x = Double.toString(chart_width);
			String y = Integer.toString(i);
			unscale_g.setAttributeNS(null, "transform", "translate(" + x + "," + y + ") scale(1," + unscale_y + ")");
			
			horiz_tick.setAttributeNS(null, "x1", "0");
			horiz_tick.setAttributeNS(null, "y1", "0");
			horiz_tick.setAttributeNS(null, "x2", "5");
			horiz_tick.setAttributeNS(null, "y2", "0");
			horiz_tick.setAttributeNS(null, "stroke",
					FireChartConversionUtil.colorToHexString(App.prefs.getColorPref(PrefKey.CHART_PERCENT_SCARRED_COLOR, Color.BLACK)));
			horiz_tick.setAttributeNS(null, "stroke-width", Double.toString(0 - unscale_y));
			
			int labelx = 7;
			int labely = labelHeight / 2;
			if (i == 0)
			{
				// labelx = 7 + zeroLabelWidth + zeroLabelWidth;
				labely = 0;
			}
			else if (i >= 25 && i <= 75)
			{
				// labelx = 7 + zeroLabelWidth;
			}
			percent_tick_text.setAttributeNS(null, "x", labelx + "");
			percent_tick_text.setAttributeNS(null, "y", labely + "");
			percent_tick_text.setAttributeNS(null, "font-family", fontFamily);
			percent_tick_text.setAttributeNS(null, "font-size", yaxis_fontsize + "");
			
			percent_tick_text.appendChild(percent_tick_text_t);
			unscale_g.appendChild(percent_tick_text);
			unscale_g.appendChild(horiz_tick);
			scarred_g.appendChild(unscale_g);
		}
		
		// add in the label that says "% Scarred"
		Element unscale_g = doc.createElementNS(svgNS, "g");
		unscale_g.setAttributeNS(null, "transform", "scale(1," + unscale_y + ")");
		
		Element rotate_g = doc.createElementNS(svgNS, "g");
		String x = Double.toString(chart_width + 5 + 10 + this.getStringWidth(fontFamily, Font.PLAIN, yaxis_fontsize, "100"));
		String y = Double.toString(scale_y * 100);
		rotate_g.setAttributeNS(null, "transform", "translate(" + x + "," + y + ") rotate(90)");
		
		Text label_t = doc.createTextNode(App.prefs.getPref(PrefKey.CHART_AXIS_Y2_LABEL, "% Scarred"));
		Element label = doc.createElementNS(svgNS, "text");
		label.setAttributeNS(null, "font-family", fontFamily);
		label.setAttributeNS(null, "font-size", App.prefs.getIntPref(PrefKey.CHART_AXIS_Y2_FONT_SIZE, 10) + "");
		
		label.appendChild(label_t);
		rotate_g.appendChild(label);
		unscale_g.appendChild(rotate_g);
		scarred_g.appendChild(unscale_g);
		
		if (App.prefs.getBooleanPref(PrefKey.CHART_SHOW_INDEX_PLOT, true)
				&& App.prefs.getBooleanPref(PrefKey.CHART_SHOW_PERCENT_SCARRED, true))
		{
			scarred_g.setAttributeNS(null, "display", "inline");
		}
		else
		{
			scarred_g.setAttributeNS(null, "display", "none");
		}
		
		return scarred_g;
	}

	

	

	
	
	/*
	 * public boolean setCommonTickAttrib(int weight, Color color, LineStyle style) {
	 * 
	 * tickLineWeight = weight; tickLineStyle = style; setTickColor(color); positionChartGroupersAndDrawTimeAxis(); return false; }
	 */
	
	/**
	 * TODO
	 */
	public void setSeriesLabelsVisibility() {
		
		boolean isSeriesLabelVisible = App.prefs.getBooleanPref(PrefKey.CHART_SHOW_CHRONOLOGY_PLOT_LABELS, true);
		
		for (FHSeriesSVG svgSeries : series_list)
		{
			Element ser = doc.getElementById("series_label_" + svgSeries.getTitle());
			if (isSeriesLabelVisible)
				ser.setAttributeNS(null, "display", "inline");
			else
				ser.setAttributeNS(null, "display", "none");
		}
		
		for (int i = 0; i < series_list.size(); i++)
		{
			Element upButton = doc.getElementById("up_button" + i);
			Element downButton = doc.getElementById("down_button" + i);
			if (isSeriesLabelVisible)
			{
				upButton.setAttributeNS(null, "display", "inline");
				downButton.setAttributeNS(null, "display", "inline");
			}
			else
			{
				upButton.setAttributeNS(null, "display", "none");
				downButton.setAttributeNS(null, "display", "none");
			}
		}
	}

	/**
	 * This function toggles the visibility of the series at the given location.
	 * 
	 * @param index of the series to hide
	 */
	public void toggleVisibilityOfSeries(int index) {
		
		FHSeriesSVG temp = series_list.get(index);
		temp.toggleVisibility();
		series_list.set(index, temp);
		positionSeriesLines();
		positionChartGroupersAndDrawTimeAxis();
	}

	

	

	/*
	 * public boolean setCommonTickAttrib(int weight, Color color, LineStyle style) {
	 * 
	 * tickLineWeight = weight; tickLineStyle = style; setTickColor(color); positionChartGroupersAndDrawTimeAxis(); return false; }
	 */
	
	/**
	 * Get the sample or recorder depth plot.
	 * 
	 * @param plotSampleNorRecordingDepth
	 * @return
	 */
	private Element getSampleOrRecorderDepthsPlot(boolean plotSampleNorRecordingDepth) {
		
		Element sample_g = doc.createElementNS(svgNS, "g");
		Element sample_g_chart = doc.createElementNS(svgNS, "g"); // scales the years on the x direction
		
		sample_g.setAttributeNS(null, "id", "depths");
		
		int[] sample_depths;
		if (plotSampleNorRecordingDepth)
			sample_depths = reader.getSampleDepths();
		else
			sample_depths = reader.getRecordingDepths();
			
		// Limit to specified years if necessary
		if (!App.prefs.getBooleanPref(PrefKey.CHART_AXIS_X_AUTO_RANGE, true))
		{
			
			int startindex_file = this.getFirstChartYear() - reader.getFirstYear();
			int[] sample_depths_file = sample_depths.clone();
			sample_depths = new int[(this.getLastChartYear() - this.getFirstChartYear()) + 1];
			
			int ind_in_file = startindex_file;
			for (int ind_in_newarr = 0; ind_in_newarr < sample_depths.length; ind_in_newarr++)
			{
				
				if (ind_in_file > sample_depths_file.length - 1)
				{
					// Reached end of data we are extracting
					break;
				}
				
				if (ind_in_file < 0)
				{
					// Before data we are extracting so keep going
					ind_in_file++;
					continue;
				}
				
				sample_depths[ind_in_newarr] = sample_depths_file[ind_in_file];
				ind_in_file++;
			}
		}
		
		int[] sample_depths_sorted = sample_depths.clone(); // sort to find the max.
		Arrays.sort(sample_depths_sorted);
		int largest_sample_depth = sample_depths_sorted[sample_depths_sorted.length - 1];
		
		// Make the max value 110% of the actually max value so there is a bit of breathing room at top of the chart
		if (largest_sample_depth > 0)
		{
			largest_sample_depth = (int) Math.ceil(largest_sample_depth * 1.1);
		}
		else
		{
			largest_sample_depth = 1;
		}
		
		// the trend line is constructed as if the first year is 0 A.D.
		String translation = "translate(0," + App.prefs.getIntPref(PrefKey.CHART_INDEX_PLOT_HEIGHT, 100) + ")";
		double scale_y = -1 * ((double) App.prefs.getIntPref(PrefKey.CHART_INDEX_PLOT_HEIGHT, 100)) / (largest_sample_depth);
		double unscale_y = 1 / scale_y;
		
		// String scale = "scale("+yearsToPixels()+"," + scale_y + ")";
		String t = translation + "scale(1," + scale_y + ")";
		sample_g.setAttributeNS(null, "transform", t);
		sample_g_chart.setAttributeNS(null, "transform",
				"scale(" + FireChartConversionUtil.yearsToPixels(chart_width, getFirstChartYear(), getLastChartYear()) + ",1)");
				
		// error check
		if (sample_depths.length == 0)
		{
			return sample_g;
		}
		
		// build the trend line
		int begin_index = 0;
		String linecolor = FireChartConversionUtil
				.colorToHexString(App.prefs.getColorPref(PrefKey.CHART_SAMPLE_OR_RECORDER_DEPTH_COLOR, Color.BLUE));
		for (int i = 1; i < sample_depths.length; i++)
		{
			if (sample_depths[i] != sample_depths[begin_index])
			{
				Element vline = doc.createElementNS(svgNS, "line");
				vline.setAttributeNS(null, "x1", Double.toString(i));
				vline.setAttributeNS(null, "y1", Double.toString(sample_depths[begin_index]));
				vline.setAttributeNS(null, "x2", Double.toString(i));
				vline.setAttributeNS(null, "y2", Double.toString(sample_depths[i]));
				vline.setAttributeNS(null, "stroke", linecolor);
				vline.setAttributeNS(null, "stroke-width",
						Double.toString(FireChartConversionUtil.pixelsToYears(1, chart_width, getFirstChartYear(), getLastChartYear())));
				sample_g_chart.appendChild(vline);
				
				Element hline = doc.createElementNS(svgNS, "line");
				hline.setAttributeNS(null, "x1", Double.toString(begin_index));
				hline.setAttributeNS(null, "y1", Double.toString(sample_depths[begin_index]));
				hline.setAttributeNS(null, "x2", Double.toString(i));
				hline.setAttributeNS(null, "y2", Double.toString(sample_depths[begin_index]));
				hline.setAttributeNS(null, "stroke", linecolor);
				hline.setAttributeNS(null, "stroke-width", Double.toString(-1.0 / scale_y));
				sample_g_chart.appendChild(hline);
				
				begin_index = i;
			}
			
			// draw in the final line
			if (i + 1 == sample_depths.length)
			{
				Element final_hline = doc.createElementNS(svgNS, "line");
				final_hline.setAttributeNS(null, "x1", Double.toString(begin_index));
				final_hline.setAttributeNS(null, "y1", Double.toString(sample_depths[begin_index]));
				final_hline.setAttributeNS(null, "x2", Double.toString(i));
				final_hline.setAttributeNS(null, "y2", Double.toString(sample_depths[begin_index]));
				final_hline.setAttributeNS(null, "stroke", linecolor);
				final_hline.setAttributeNS(null, "stroke-width", Double.toString(-1.0 / scale_y));
				sample_g_chart.appendChild(final_hline);
			}
		}
		
		// add the threshold depth
		int threshold_sample_depth_value = App.prefs.getIntPref(PrefKey.CHART_DEPTH_THRESHOLD_VALUE, 10);
		Element threshold_line = doc.createElementNS(svgNS, "line");
		threshold_line.setAttributeNS(null, "id", "threshold_line");
		threshold_line.setAttributeNS(null, "x1", Integer.toString(0));
		threshold_line.setAttributeNS(null, "y1", Double.toString(threshold_sample_depth_value));
		threshold_line.setAttributeNS(null, "x2", Integer.toString(getLastChartYear() - getFirstChartYear()));
		threshold_line.setAttributeNS(null, "y2", Double.toString(threshold_sample_depth_value));
		threshold_line.setAttributeNS(null, "stroke",
				FireChartConversionUtil.colorToHexString(App.prefs.getColorPref(PrefKey.CHART_DEPTH_THRESHOLD_COLOR, Color.RED)));
		threshold_line.setAttributeNS(null, "stroke-width", Double.toString(-1.0 / scale_y));
		if (!App.prefs.getBooleanPref(PrefKey.CHART_SHOW_DEPTH_THRESHOLD, false) || threshold_sample_depth_value > largest_sample_depth
				|| threshold_sample_depth_value < 0)
		{
			threshold_line.setAttributeNS(null, "display", "none");
		}
		sample_g_chart.appendChild(threshold_line);
		
		// add in the tick lines
		int num_ticks = 4;
		int tick_spacing = (int) Math.ceil((double) largest_sample_depth / (double) num_ticks);
		int font_size = App.prefs.getIntPref(PrefKey.CHART_AXIS_Y1_FONT_SIZE, 10);
		int labelheight = this.getStringHeight(fontFamily, Font.PLAIN, font_size, "9");
		for (int i = 0; i < num_ticks; i++)
		{
			Element horiz_tick = doc.createElementNS(svgNS, "line");
			int labelwidth = this.getStringWidth(fontFamily, Font.PLAIN, font_size, i * tick_spacing + "");
			
			horiz_tick.setAttributeNS(null, "x1", "-5");
			horiz_tick.setAttributeNS(null, "y1", Integer.toString(i * tick_spacing));
			horiz_tick.setAttributeNS(null, "x2", "0");
			horiz_tick.setAttributeNS(null, "y2", Integer.toString(i * tick_spacing));
			horiz_tick.setAttributeNS(null, "stroke", "black");
			horiz_tick.setAttributeNS(null, "stroke-width", Double.toString(0 - unscale_y));
			sample_g.appendChild(horiz_tick);
			
			Element unscale_g = doc.createElementNS(svgNS, "g");
			unscale_g.setAttributeNS(null, "transform", "translate(-5," + (i * tick_spacing) + ") scale(1," + (1.0 / scale_y) + ")");
			Text depth_text_t = doc.createTextNode(Integer.toString(i * tick_spacing));
			Element depth_text = doc.createElementNS(svgNS, "text");
			depth_text.setAttributeNS(null, "x", 0 - labelwidth + "");
			depth_text.setAttributeNS(null, "y", Double.toString(0 - (labelheight / 2.0) * (1.0 / scale_y) + 2));
			depth_text.setAttributeNS(null, "font-family", fontFamily);
			depth_text.setAttributeNS(null, "font-size", font_size + "");
			depth_text.appendChild(depth_text_t);
			unscale_g.appendChild(depth_text);
			sample_g.appendChild(unscale_g);
		}
		
		// add in label that says "Sample Depth"
		int labelwidth = this.getStringWidth(fontFamily, Font.PLAIN, font_size, num_ticks * tick_spacing + "");
		
		Element unscale_g = doc.createElementNS(svgNS, "g");
		unscale_g.setAttributeNS(null, "transform",
				"translate(" + (-5 - labelwidth - 10) + "," + 0 + ") scale(1," + (1.0 / scale_y) + ") rotate(270)");
		Text label_t = doc.createTextNode(App.prefs.getPref(PrefKey.CHART_AXIS_Y1_LABEL, "Sample Depth"));
		Element label = doc.createElementNS(svgNS, "text");
		label.setAttributeNS(null, "x", "0");
		label.setAttributeNS(null, "y", "0");
		label.setAttributeNS(null, "font-family", fontFamily);
		label.setAttributeNS(null, "font-size", App.prefs.getIntPref(PrefKey.CHART_AXIS_Y1_FONT_SIZE, 10) + "");
		label.appendChild(label_t);
		unscale_g.appendChild(label);
		sample_g.appendChild(unscale_g);
		sample_g.appendChild(sample_g_chart);
		
		if (App.prefs.getBooleanPref(PrefKey.CHART_SHOW_INDEX_PLOT, true))
		{
			sample_g.setAttributeNS(null, "display", "inline");
		}
		else
		{
			sample_g.setAttributeNS(null, "display", "none");
		}
		
		return sample_g;
	}

	
	
	
	
	
	
	/*
	 * public boolean setCommonTickAttrib(int weight, Color color, LineStyle style) {
	 * 
	 * tickLineWeight = weight; tickLineStyle = style; setTickColor(color); positionChartGroupersAndDrawTimeAxis(); return false; }
	 */
	
	/**
	 * TODO
	 * 
	 * @param isVisible
	 */
	public void setVisibilityOfNoExportElements(boolean isVisible) {
		
		String visibility_setting = isVisible ? "inline" : "none";
		NodeList n = doc.getElementsByTagName("*");
		for (int i = 0; i < n.getLength(); i++)
		{
			Element temp = (Element) n.item(i);
			if (temp.getAttribute("class").equals("no_export"))
			{
				temp.setAttributeNS(null, "display", visibility_setting);
			}
		}
	}
	
	// ============== Annotation ============
	// There is a <rect id="annote_canvas"> element in the DOM under <g id="annote_g">.
	// It is used to catch mouse events in order to add, resize, or delete annotation rectangles.
	// The enum Mode is used to track whether the user has selected add, resize, &etc.
	// All mode checking will be done java-side to simplify the js.
	// In other words, rectangles will always call deleteAnnoteRect when clicked, and it is up
	// to deleteAnnoteRect to ensure that the rect only gets deleted when the user is in the eraser mode
	// ======================================
	
	/**
	 * TODO
	 * 
	 * @return
	 */
	public Element getAnnoteCanvas() {
		
		try
		{
			Element annote_canvas = doc.createElementNS(svgNS, "rect");
			annote_canvas.setAttributeNS(null, "id", "annote_canvas");
			annote_canvas.setAttributeNS(null, "width", this.chart_width + "");
			annote_canvas.setAttributeNS(null, "height", "999");
			annote_canvas.setAttributeNS(null, "onmousedown", "paddingGrouperOnClick(evt)");
			annote_canvas.setAttributeNS(null, "opacity", "0.0");
			return annote_canvas;
		}
		catch (BridgeException e)
		{
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * TODO
	 * 
	 * @param x
	 * @return
	 */
	public String drawAnnoteLine(int x) {
		
		if (annotemode == AnnoteMode.LINE)
		{
			Element annote_g = doc.getElementById("annote_g");
			Element annote_canvas = doc.getElementById("annote_canvas");
			Element annote_line = doc.createElementNS(svgNS, "line");
			SVGRect annote_canvas_rc = SVGLocatableSupport.getBBox(annote_canvas);
			
			String id = "annote_line_" + (line_gensym++);
			annote_line.setAttributeNS(null, "id", id);
			annote_line.setAttributeNS(null, "x1", Float.toString(x - chartXOffset));
			annote_line.setAttributeNS(null, "y1", "0");
			annote_line.setAttributeNS(null, "x2", Float.toString(x - chartXOffset));
			annote_line.setAttributeNS(null, "y2", Float.toString(annote_canvas_rc.getHeight()));
			annote_line.setAttributeNS(null, "stroke", "black");
			annote_line.setAttributeNS(null, "stroke-width", "3");
			annote_line.setAttributeNS(null, "opacity", "0.5");
			annote_line.setAttributeNS(null, "onmousedown", "FireChartSVG.getChart(chart_num).deleteAnnoteLine('" + id + "')");
			
			annote_g.appendChild(annote_line);
			annotemode = AnnoteMode.NONE;
			return id;
		}
		return "wrong_annotemode";
	}
	
	/**
	 * TODO
	 * 
	 * @param id
	 * @return
	 */
	public boolean deleteAnnoteLine(String id) {
		
		if (annotemode == AnnoteMode.ERASE)
		{
			Element annote_g = doc.getElementById("annote_g");
			Element annote_line = doc.getElementById(id);
			if (annote_line == null)
			{
				return false;
			}
			
			annote_g.removeChild(annote_line);
			return true;
		}
		return false;
	}
	
	/**
	 * TODO
	 * 
	 * @param m
	 */
	public void setAnnoteMode(AnnoteMode m) {
		
		annotemode = m;
		
		// Changing the cursor would be cool, but I couldn't get it
		// to load a custom graphic. A base-64 encoded directly in
		// this files didn't work either.
		// Element annote_canvas = doc.getElementById("annote_canvas");
		// if( annotemode == AnnoteMode.LINE ) {
		// annote_canvas.setAttributeNS(null, "cursor", "move");
		// }
		// else {
		// annote_canvas.setAttributeNS(null, "cursor", "auto");
		// }
	}

	/**
	 * Returns a dimension in years (time coordinate system) for the specified proportion of the chart width.
	 * 
	 * @param prop
	 * @return
	 */
	public Double standardChartUnits(int prop) {
		
		Double pixelsForProportion = this.chart_width * (prop / 1000.0);
		return FireChartConversionUtil.pixelsToYears(pixelsForProportion, chart_width, getFirstChartYear(), getLastChartYear());
	}

	/**
	 * TODO
	 * 
	 * @param doc
	 * @param out
	 */
	public static void printDocument(Document doc, OutputStream out) {
		
		try
		{
			TransformerFactory tf = TransformerFactory.newInstance();
			Transformer t = tf.newTransformer();
			t.setOutputProperty(OutputKeys.METHOD, "xml");
			t.setOutputProperty(OutputKeys.INDENT, "yes");
			t.transform(new DOMSource(doc), new StreamResult(new OutputStreamWriter(out, "UTF-8")));
			
			log.debug("Document printed successfully.");
		}
		catch (Exception ex)
		{
			System.out.println("Error: Could not printDocument\n");
		}
	}

	/**
	 * Update the font family used in the plot.
	 */
	private void updateFontFamily() {
		
		fontFamily = App.prefs.getPref(PrefKey.CHART_FONT_FAMILY, "Verdana");
	}

	/**
	 * Sort the series by name.
	 */
	public void sortByName() {
		
		Comparator<FHSeriesSVG> comparator = new Comparator<FHSeriesSVG>() {
			
			@Override
			public int compare(FHSeriesSVG c1, FHSeriesSVG c2) {
				
				return c1.getTitle().compareTo(c2.getTitle());
			}
		};
		
		Collections.sort(series_list, comparator);
		positionSeriesLines();
	}

	/**
	 * Sort the series by first fire year.
	 */
	public void sortByFirstFireYear() {
		
		Comparator<FHSeriesSVG> comparator = new Comparator<FHSeriesSVG>() {
			
			@Override
			public int compare(FHSeriesSVG c1, FHSeriesSVG c2) {
				
				boolean[] c1_events = c1.getEventYears();
				boolean[] c2_events = c2.getEventYears();
				
				int i = 0;
				for (i = 0; i < c1_events.length && !c1_events[i]; i++)
				{
					; // loop until the index of the first fire year for c1 is found
				}
				int c1_first_fire_year = c1.getFirstYear() + i;
				
				int j = 0;
				for (j = 0; j < c2_events.length && !c2_events[j]; j++)
				{
					; // loop until the index of the first fire year for c2 is found
				}
				int c2_first_fire_year = c2.getFirstYear() + j;
				
				return c2_first_fire_year - c1_first_fire_year;
			}
		};
		
		Collections.sort(series_list, comparator);
		positionSeriesLines();
	}

	/**
	 * Sort the series by start year.
	 */
	public void sortBySampleStartYear() {
		
		Comparator<FHSeriesSVG> comparator = new Comparator<FHSeriesSVG>() {
			
			@Override
			public int compare(FHSeriesSVG c1, FHSeriesSVG c2) {
				
				return c2.getFirstYear() - c1.getFirstYear();
			}
		};
		
		Collections.sort(series_list, comparator);
		positionSeriesLines();
	}

	/**
	 * Sort the series by end year.
	 */
	public void sortBySampleEndYear() {
		
		Comparator<FHSeriesSVG> comparator = new Comparator<FHSeriesSVG>() {
			
			@Override
			public int compare(FHSeriesSVG c1, FHSeriesSVG c2) {
				
				return c2.getLastYear() - c1.getLastYear();
			}
		};
		
		Collections.sort(series_list, comparator);
		positionSeriesLines();
	}
}
