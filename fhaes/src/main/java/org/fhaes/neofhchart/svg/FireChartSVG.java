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

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.batik.bridge.BridgeException;
import org.apache.batik.dom.svg.SVGDOMImplementation;
import org.apache.batik.dom.svg.SVGLocatableSupport;
import org.apache.commons.lang.StringUtils;
import org.fhaes.enums.AnnoteMode;
import org.fhaes.enums.EventTypeToProcess;
import org.fhaes.enums.FeedbackDisplayProtocol;
import org.fhaes.enums.FeedbackMessageType;
import org.fhaes.enums.FireFilterType;
import org.fhaes.enums.LabelOrientation;
import org.fhaes.enums.SampleDepthFilterType;
import org.fhaes.feedback.FeedbackPreferenceManager.FeedbackDictionary;
import org.fhaes.fhfilereader.AbstractFireHistoryReader;
import org.fhaes.fhfilereader.FHFile;
import org.fhaes.gui.MainWindow;
import org.fhaes.model.FHCategoryEntry;
import org.fhaes.model.FHSeries;
import org.fhaes.neofhchart.ChartActions.SeriesSortType;
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
 * FireChartSVG Class. Graphs a fire history chart as an SVG using FHUtil's AbstractFireHistoryReader.
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
	
	// Declare protected constants
	protected static final int SERIES_HEIGHT = 10;
	
	// Declare local constants
	private static final int CATEGORY_PADDING_AMOUNT = 40;
	
	// Declare local variables
	private AbstractFireHistoryReader reader;
	private int chartXOffset = 50;
	private int chartWidth = 1000;
	private int categoryGroupPadding = 0;
	private int widestChronologyLabelSize = 0;
	private boolean showFires = true;
	private boolean showInjuries = true;
	private boolean showPith = true;
	private boolean showBark = true;
	private boolean showInnerRing = true;
	private boolean showOuterRing = true;
	public boolean traditionalData = false;
	private AnnoteMode annotemode = AnnoteMode.NONE;
	private SeriesSortType lastTypeSortedBy = SeriesSortType.NAME;
	private ArrayList<FHSeriesSVG> seriesSVGList = new ArrayList<>();
	
	// Declare builder objects
	private final CompositePlotElementBuilder compositePlotEB;
	private final LegendElementBuilder legendEB;
	private final PercentScarredPlotElementBuilder percentScarredPlotEB;
	private final SampleRecorderPlotElementBuilder sampleRecorderPlotEB;
	private final SeriesElementBuilder seriesEB;
	private final TimeAxisElementBuilder timeAxisEB;
	
	// Java <-> ECMAScript interop used for message passing with ECMAScript. Note not thread-safe
	private static int chartCounter = 0;
	private static int lineGensym = 0; // only used in drawRect -- I just need a unique id
	private static Map<Integer, FireChartSVG> chart_map;
	private int totalHeight = 0;
	private int chartNum;
	
	/**
	 * The constructor builds the DOM of the SVG.
	 * 
	 * @param f
	 */
	public FireChartSVG(AbstractFireHistoryReader f) {
		
		// Initialize the builder objects
		compositePlotEB = new CompositePlotElementBuilder(this);
		legendEB = new LegendElementBuilder(this);
		percentScarredPlotEB = new PercentScarredPlotElementBuilder(this);
		sampleRecorderPlotEB = new SampleRecorderPlotElementBuilder(this);
		seriesEB = new SeriesElementBuilder(this);
		timeAxisEB = new TimeAxisElementBuilder(this);
		
		// Assign number for message passing from ECMAscript
		chartNum = chartCounter;
		chartCounter++;
		
		if (chart_map == null)
		{
			chart_map = new HashMap<>();
		}
		chart_map.put(chartNum, this);
		
		reader = f;
		ArrayList<FHSeriesSVG> seriesToAdd = FireChartUtil.seriesListToSeriesSVGList(f.getSeriesList());
		
		if (!seriesSVGList.isEmpty())
		{
			seriesSVGList.clear();
		}
		for (int i = 0; i < seriesToAdd.size(); i++)
		{
			try
			{
				FHSeries currentSeries = seriesToAdd.get(i);
				
				// Add the default category entry if the current series has no defined entries (this is necessary for category groupings)
				if (currentSeries.getCategoryEntries().isEmpty())
				{
					currentSeries.getCategoryEntries().add(new FHCategoryEntry(currentSeries.getTitle(), "default", "default"));
				}
				
				seriesSVGList.add(new FHSeriesSVG(seriesToAdd.get(i), i));
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
			script_string += ("var chart_num = " + chartNum + ";");
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
		
		// Build chart title
		Element chart_title_g = doc.createElementNS(svgNS, "g");
		chart_title_g.setAttributeNS(null, "id", "chart_title_g");
		padding_grouper.appendChild(chart_title_g);
		
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
		
		// Finish up the initialization
		buildElements();
		positionSeriesLines();
		positionChartGroupersAndDrawTimeAxis();
		sortSeriesAccordingToPreference();
	};
	
	/**
	 * Gets the SVG document instance for this chart.
	 * 
	 * @return
	 */
	public Document getSVGDocument() {
		
		return doc;
	}
	
	/**
	 * Gets the abstract fire history reader for this chart.
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
	 * Gets the chart number for use in the ECMAscript.
	 * 
	 * @return
	 */
	public int getChartNum() {
		
		return chartNum;
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
	 * This function returns the up-to-date list of series.
	 * 
	 * @return the current list of series
	 */
	public ArrayList<FHSeriesSVG> getCurrentSeriesList() {
		
		return seriesSVGList;
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
			
			MainWindow.getInstance().getFeedbackMessagePanel().updateFeedbackMessage(FeedbackMessageType.INFO,
					FeedbackDisplayProtocol.AUTO_HIDE, FeedbackDictionary.NEOFHCHART_SVG_EXPORT_MESSAGE.toString());
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
			return applyBCYearOffset(reader.getFirstYear());
		}
		else
		{
			return applyBCYearOffset(App.prefs.getIntPref(PrefKey.CHART_AXIS_X_MIN, reader.getFirstYear()));
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
			return applyBCYearOffset(reader.getLastYear());
		}
		else
		{
			return applyBCYearOffset(App.prefs.getIntPref(PrefKey.CHART_AXIS_X_MAX, reader.getLastYear()));
		}
	}
	
	/**
	 * Returns the magnitude of the year if it is negative, otherwise returns an offset of one. This is effectively the offset which the
	 * chart must apply while rendering series in order to maintain compatibility with BC years.
	 * 
	 * @param year
	 * @return
	 */
	private int applyBCYearOffset(int originalYear) {
		
		int effectiveFirstYear = getEffectiveFirstYear();
		
		// Apply the offset transformation
		if (effectiveFirstYear < 0 && originalYear < 0)
		{
			return originalYear + Math.abs(effectiveFirstYear);
		}
		else if (effectiveFirstYear < 0 && originalYear >= 0)
		{
			return originalYear + Math.abs(effectiveFirstYear) + 1;
		}
		else
		{
			return originalYear;
		}
	}
	
	/**
	 * Performs the inverse operation of applyBCYearOffset.
	 * 
	 * @param year
	 * @return
	 */
	private int removeBCYearOffset(int offsetYear) {
		
		int effectiveFirstYear = getEffectiveFirstYear();
		
		// Remove the offset transformation
		if (effectiveFirstYear < 0 && offsetYear - Math.abs(effectiveFirstYear) < 0)
		{
			return offsetYear - Math.abs(effectiveFirstYear);
		}
		else if (effectiveFirstYear < 0 && offsetYear - Math.abs(effectiveFirstYear) >= 0)
		{
			return offsetYear - Math.abs(effectiveFirstYear) - 1;
		}
		else
		{
			return offsetYear;
		}
	}
	
	/**
	 * Determines what year to use as the effective first year when applying or removing the BC offset.
	 * 
	 * @return the effective first year
	 */
	private int getEffectiveFirstYear() {
		
		// Determine what to reference as the first year during the transformation
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
	 * Gets the base width of this chart.
	 * 
	 * @return chartWidth
	 */
	public int getChartWidth() {
		
		return chartWidth;
	}
	
	/**
	 * Gets the total width of this chart.
	 * 
	 * @return the chartWidth plus the padding due to chronology label size
	 */
	public int getTotalWidth() {
		
		if (!App.prefs.getBooleanPref(PrefKey.CHART_SHOW_LEGEND, true))
		{
			return chartWidth + this.widestChronologyLabelSize + 120;
			
		}
		
		return chartWidth + this.widestChronologyLabelSize + 300;
	}
	
	/**
	 * Gets the total height of this chart.
	 * 
	 * @return totalHeight
	 */
	public int getTotalHeight() {
		
		return totalHeight;
	}
	
	/**
	 * This method swaps the selected series with the series above it.
	 * 
	 * @param series_name: Name of the series to move up
	 */
	public void moveSeriesUp(String series_name) {
		
		int i = 0;
		
		for (i = 0; i < seriesSVGList.size() && !seriesSVGList.get(i).getTitle().equals(series_name); i++)
		{
			; // loop until the index of the desired series is found
		}
		
		if (i > 0)
		{
			do
			{
				FHSeriesSVG series = seriesSVGList.get(i);
				seriesSVGList.set(i, seriesSVGList.get(i - 1));
				
				try
				{
					seriesSVGList.set(i - 1, new FHSeriesSVG(series, series.getSequenceInFile()));
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
				
				i--;
				positionSeriesLines();
			}
			while (i > 0 && !seriesSVGList.get(i + 1).isVisible());
		}
	}
	
	/**
	 * This method swaps the selected series with the series below it.
	 * 
	 * @param series_name: Name of the series to move down
	 */
	public void moveSeriesDown(String series_name) {
		
		int i = 0;
		
		for (i = 0; i < seriesSVGList.size() && !seriesSVGList.get(i).getTitle().equals(series_name); i++)
		{
			; // loop until the index of the desired series is found
		}
		
		if (i < seriesSVGList.size() - 1)
		{
			do
			{
				FHSeriesSVG series = seriesSVGList.get(i);
				seriesSVGList.set(i, seriesSVGList.get(i + 1));
				
				try
				{
					seriesSVGList.set(i + 1, new FHSeriesSVG(series, series.getSequenceInFile()));
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
				
				i++;
				positionSeriesLines();
			}
			while (i < seriesSVGList.size() - 1 && !seriesSVGList.get(i - 1).isVisible());
		}
	}
	
	/**
	 * Handles the positioning of the series lines on the chart.
	 */
	private void positionSeriesLines() {
		
		int series_spacing_and_height = App.prefs.getIntPref(PrefKey.CHART_CHRONOLOGY_PLOT_SPACING, 5) + SERIES_HEIGHT;
		int hidden = 0;
		
		// Reset the amount of padding necessary for category groupings
		categoryGroupPadding = 0;
		
		// Define a string for keeping track of the category groups
		ArrayList<String> categoryGroupsProcessed = new ArrayList<>();
		
		for (int i = 0; i < seriesSVGList.size(); i++)
		{
			FHSeries seriesSVG = seriesSVGList.get(i);
			Element series_group = doc.getElementById("series_group_" + seriesSVG.getTitle());
			String visibility_string = seriesSVGList.get(i).isVisible() ? "inline" : "none";
			
			if (seriesSVGList.get(i).isVisible())
			{
				// Inject the category group spacing and label text as different category groups are positioned
				if (lastTypeSortedBy == SeriesSortType.CATEGORY && App.prefs.getBooleanPref(PrefKey.CHART_SHOW_CATEGORY_GROUPS, true))
				{
					String currentCategoryGroup = seriesSVG.getCategoryEntries().get(0).getContent();
					
					if (!categoryGroupsProcessed.contains(currentCategoryGroup))
					{
						// Keep track of which category groups have already been processed
						categoryGroupsProcessed.add(currentCategoryGroup);
						
						Element label_text_g = doc.createElementNS(svgNS, "g");
						label_text_g.setAttributeNS(null, "transform",
								"translate(0," + Integer.toString(-(CATEGORY_PADDING_AMOUNT / 2)) + ")");
						
						// Apply the label coloring as necessary
						if (App.prefs.getBooleanPref(PrefKey.CHART_AUTOMATICALLY_COLORIZE_LABELS, false))
						{
							Color labelColor = FireChartUtil.pickColorFromInteger(categoryGroupsProcessed.size());
							label_text_g.appendChild(seriesEB.getCategoryLabelTextElement(currentCategoryGroup, labelColor));
						}
						else
						{
							label_text_g.appendChild(seriesEB.getCategoryLabelTextElement(currentCategoryGroup, Color.BLACK));
						}
						series_group.appendChild(label_text_g);
						
						// Handle the padding of category groups depending on whether the label is shown
						if (App.prefs.getBooleanPref(PrefKey.CHART_SHOW_CATEGORY_LABELS, true))
						{
							label_text_g.setAttributeNS(null, "display", "inline");
							categoryGroupPadding += CATEGORY_PADDING_AMOUNT;
						}
						else
						{
							label_text_g.setAttributeNS(null, "display", "none");
							categoryGroupPadding += CATEGORY_PADDING_AMOUNT / 2;
						}
					}
				}
				
				series_group.setAttributeNS(null, "transform",
						"translate(0," + Integer.toString(((i - hidden) * series_spacing_and_height) + categoryGroupPadding) + ")");
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
		int series_spacing_and_height = App.prefs.getIntPref(PrefKey.CHART_CHRONOLOGY_PLOT_SPACING, 5) + SERIES_HEIGHT;
		
		if (App.prefs.getBooleanPref(PrefKey.CHART_SHOW_CHART_TITLE, true))
		{
			cur_bottom += App.prefs.getIntPref(PrefKey.CHART_TITLE_FONT_SIZE, 20) + 10;
		}
		
		if (App.prefs.getBooleanPref(PrefKey.CHART_SHOW_INDEX_PLOT, true))
		{
			cur_bottom += index_plot_height + series_spacing_and_height;
		}
		
		int chronology_plot_y = cur_bottom;
		int num_visible = 0;
		
		for (int i = 0; i < seriesSVGList.size(); i++)
		{
			if (seriesSVGList.get(i).isVisible())
			{
				num_visible++;
			}
		}
		
		int chronology_plot_height = num_visible * series_spacing_and_height + SERIES_HEIGHT;
		
		if (App.prefs.getBooleanPref(PrefKey.CHART_SHOW_CHRONOLOGY_PLOT, true))
		{
			cur_bottom += chronology_plot_height + series_spacing_and_height + categoryGroupPadding;
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
		
		// set the translations for the chronology plot grouper
		Element chrono_plot_g = doc.getElementById("chrono_plot_g");
		chrono_plot_g.setAttributeNS(null, "transform", "translate(0," + chronology_plot_y + ")");
		
		// set the translations for the composite plot grouper
		Element comp_plot_g = doc.getElementById("comp_plot_g");
		comp_plot_g.setAttributeNS(null, "transform", "translate(0," + composite_plot_y + ")");
		
		// move the legend
		Element legend_g = doc.getElementById("legend_g");
		legend_g.setAttributeNS(null, "transform", "translate(" + (chartWidth + 10 + this.widestChronologyLabelSize + 50) + ", " + 0 + ")");
		
		// set the annote canvas dimensions (so it can catch key bindings)
		Element annote_canvas = doc.getElementById("annote_canvas");
		annote_canvas.setAttributeNS(null, "width", Integer.toString(chartWidth));
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
		
		// Rebuild the annotation canvas
		Element annote_g = doc.getElementById("annote_g");
		deleteAllChildren(annote_g);
		
		Element canvas = getAnnoteCanvas();
		if (canvas != null)
		{
			annote_g.appendChild(canvas);
		}
		
		// Rebuild the chart title
		Element chart_title_g = doc.getElementById("chart_title_g");
		deleteAllChildren(chart_title_g);
		if (App.prefs.getBooleanPref(PrefKey.CHART_TITLE_USE_DEFAULT_NAME, true))
		{
			FHFile currentFile = getReader().getFHFile();
			
			if (currentFile.getSiteName().length() > 0)
			{
				chart_title_g.appendChild(getChartTitle(currentFile.getSiteName()));
			}
			else
			{
				chart_title_g.appendChild(getChartTitle(currentFile.getFileNameWithoutExtension()));
			}
		}
		else
		{
			chart_title_g.appendChild(getChartTitle(App.prefs.getPref(PrefKey.CHART_TITLE_OVERRIDE_VALUE, "Fire Chart")));
		}
		if (App.prefs.getBooleanPref(PrefKey.CHART_SHOW_CHART_TITLE, true))
		{
			chart_title_g.setAttributeNS(null, "display", "inline");
		}
		else
		{
			chart_title_g.setAttributeNS(null, "display", "none");
		}
		
		// Rebuild the index plot
		Element index_plot_g = doc.getElementById("index_plot_g");
		deleteAllChildren(index_plot_g);
		index_plot_g.appendChild(getIndexPlot());
		
		sortSeriesAccordingToPreference();
		
		// Rebuild the chronology plot
		rebuildChronologyPlot();
		
		// Rebuild the composite plot
		Element comp_plot_g = doc.getElementById("comp_plot_g");
		deleteAllChildren(comp_plot_g);
		comp_plot_g.appendChild(getCompositePlot());
		
		// Rebuild the legend
		Element legend_g = doc.getElementById("legend_g");
		deleteAllChildren(legend_g);
		legend_g.appendChild(getLegend());
		
		positionChartGroupersAndDrawTimeAxis();
	}
	
	/**
	 * Builds a series line based of the input seriesSVG object.
	 * 
	 * @param seriesSVG
	 * @return a series line element
	 */
	private Element buildSingleSeriesLine(FHSeriesSVG seriesSVG) {
		
		Element series_group = doc.createElementNS(svgNS, "g");
		series_group.setAttributeNS(null, "id", seriesSVG.getTitle());
		
		// draw in the recording and non-recording lines
		Element line_group = doc.createElementNS(svgNS, "g");
		boolean[] recording_years = seriesSVG.getRecordingYears();
		
		int begin_index = 0;
		int last_index = recording_years.length - 1;
		
		if (recording_years.length != 0)
		{
			if (applyBCYearOffset(seriesSVG.getFirstYear()) < this.getFirstChartYear())
			{
				// User has trimmed the start of this data series off
				int firstyear = applyBCYearOffset(seriesSVG.getFirstYear());
				int thisfirstyear = this.getFirstChartYear();
				begin_index = thisfirstyear - firstyear;
			}
			
			if (applyBCYearOffset(seriesSVG.getLastYear()) > this.getLastChartYear())
			{
				// User has trimmed the end of this data series off
				int recleng = recording_years.length;
				int lastyear = applyBCYearOffset(seriesSVG.getLastYear());
				int thislastyear = this.getLastChartYear();
				last_index = recleng - (lastyear - thislastyear) - 1;
			}
			
			boolean isRecording = recording_years[0];
			
			for (int j = 0; j <= last_index; j++)
			{
				if (isRecording != recording_years[j] || j == last_index)
				{
					// Need to draw a line
					line_group.appendChild(seriesEB.getSeriesLine(isRecording, begin_index, j, seriesSVG.getLineColor()));
					
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
			boolean[] fire_years = seriesSVG.getEventYears();
			
			for (int j = 0; j < fire_years.length; j++)
			{
				if (fire_years[j] && j <= last_index)
				{
					String translate = "translate("
							+ Double.toString(j - FireChartUtil.pixelsToYears(0.5, chartWidth, getFirstChartYear(), getLastChartYear()))
							+ "," + Integer.toString(-SERIES_HEIGHT / 2) + ")";
					
					String scale = "scale(" + FireChartUtil.pixelsToYears(chartWidth, getFirstChartYear(), getLastChartYear()) + ",1)";
					
					Element fire_event_g = doc.createElementNS(svgNS, "g");
					fire_event_g.setAttributeNS(null, "class", "fire_marker");
					fire_event_g.setAttributeNS(null, "stroke", FireChartUtil.colorToHexString(seriesSVG.getLineColor()));
					fire_event_g.setAttributeNS(null, "transform", translate + scale);
					fire_event_g.appendChild(seriesEB.getFireYearMarker(seriesSVG.getLineColor()));
					series_fire_events.appendChild(fire_event_g);
				}
			}
			series_group.appendChild(series_fire_events);
		}
		
		// add in injury events
		if (showInjuries)
		{
			Element series_injury_events = doc.createElementNS(svgNS, "g");
			boolean[] injury_years = seriesSVG.getInjuryYears();
			for (int j = 0; j < injury_years.length; j++)
			{
				if (injury_years[j] && j <= last_index)
				{
					String translate = "translate("
							+ Double.toString(j - FireChartUtil.pixelsToYears(1.5, chartWidth, getFirstChartYear(), getLastChartYear()))
							+ "," + Integer.toString(-SERIES_HEIGHT / 2) + ")";
					
					String scale = "scale(" + FireChartUtil.pixelsToYears(chartWidth, getFirstChartYear(), getLastChartYear()) + ",1)";
					
					Element injury_event_g = doc.createElementNS(svgNS, "g");
					injury_event_g.setAttributeNS(null, "class", "injury_marker");
					injury_event_g.setAttributeNS(null, "stroke", FireChartUtil.colorToHexString(seriesSVG.getLineColor()));
					injury_event_g.setAttributeNS(null, "transform", translate + scale);
					injury_event_g.appendChild(seriesEB.getInjuryYearMarker(3, seriesSVG.getLineColor()));
					
					series_injury_events.appendChild(injury_event_g);
				}
			}
			series_group.appendChild(series_injury_events);
		}
		
		// add in inner year pith marker
		if (showPith && seriesSVG.hasPith() || showInnerRing && !seriesSVG.hasPith())
		{
			if (applyBCYearOffset(seriesSVG.getFirstYear()) >= getFirstChartYear())
			{
				// no translation because the inner year is at year=0
				String translate = "translate("
						+ (0 - FireChartUtil.pixelsToYears(0.5, chartWidth, getFirstChartYear(), getLastChartYear())) + ",0)";
				
				String scale = "scale(" + FireChartUtil.pixelsToYears(chartWidth, getFirstChartYear(), getLastChartYear()) + ",1)";
				
				Element pith_marker_g = doc.createElementNS(svgNS, "g");
				pith_marker_g.setAttributeNS(null, "transform", translate + scale);
				pith_marker_g.appendChild(seriesEB.getInnerYearPithMarker(seriesSVG.hasPith(), 5, seriesSVG.getLineColor()));
				series_group.appendChild(pith_marker_g);
			}
		}
		
		// add in outer year bark marker
		if ((showBark && seriesSVG.hasBark()) || (showOuterRing && !seriesSVG.hasBark()))
		{
			if (applyBCYearOffset(seriesSVG.getLastYear()) <= this.getLastChartYear())
			{
				String translate = "translate(" + (applyBCYearOffset(seriesSVG.getLastYear()) - applyBCYearOffset(seriesSVG.getFirstYear()))
						+ ",0)";
				
				String scale = "scale(" + FireChartUtil.pixelsToYears(chartWidth, getFirstChartYear(), getLastChartYear()) + ",1)";
				
				Element bark_marker_g = doc.createElementNS(svgNS, "g");
				bark_marker_g.setAttribute("transform", translate + scale);
				bark_marker_g.appendChild(seriesEB.getOuterYearBarkMarker(seriesSVG.hasBark(), 5, seriesSVG.getLineColor()));
				series_group.appendChild(bark_marker_g);
			}
		}
		
		return series_group;
	}
	
	/**
	 * Returns a chart title element containing the input text.
	 * 
	 * @param chartTitle
	 * @return chartTitleElement
	 */
	private Element getChartTitle(String chartTitle) {
		
		Text chartTitleText = doc.createTextNode(chartTitle);
		Integer chartTitleFontSize = App.prefs.getIntPref(PrefKey.CHART_TITLE_FONT_SIZE, 20);
		
		Element chartTitleElement = doc.createElementNS(svgNS, "text");
		chartTitleElement.setAttributeNS(null, "x", "0");
		chartTitleElement.setAttributeNS(null, "y", "0");
		chartTitleElement.setAttributeNS(null, "font-family", App.prefs.getPref(PrefKey.CHART_FONT_FAMILY, "Verdana"));
		chartTitleElement.setAttributeNS(null, "font-size", chartTitleFontSize.toString());
		chartTitleElement.appendChild(chartTitleText);
		
		return chartTitleElement;
	}
	
	/**
	 * Gets the index plot as an element.
	 * 
	 * @return indexPlot
	 */
	private Element getIndexPlot() {
		
		int indexPlotOffsetAmount = 0;
		
		if (App.prefs.getBooleanPref(PrefKey.CHART_SHOW_CHART_TITLE, true))
		{
			indexPlotOffsetAmount = App.prefs.getIntPref(PrefKey.CHART_TITLE_FONT_SIZE, 20) + 10;
		}
		
		Element indexPlot = doc.createElementNS(svgNS, "g");
		indexPlot.setAttribute("id", "indexplot");
		indexPlot.setAttributeNS(null, "transform", "translate(0," + indexPlotOffsetAmount + ")");
		indexPlot.appendChild(getSampleOrRecorderDepthsPlot(App.prefs.getBooleanPref(PrefKey.CHART_SHOW_SAMPLE_DEPTH, false),
				App.prefs.getEventTypePref(PrefKey.CHART_COMPOSITE_EVENT_TYPE, EventTypeToProcess.FIRE_EVENT)));
		indexPlot.appendChild(getPercentScarredPlot());
		
		return indexPlot;
	}
	
	/**
	 * Gets the chronology plot as an element.
	 * 
	 * @return chronologyPlot
	 */
	private Element getChronologyPlot() {
		
		Element chronologyPlot = doc.createElementNS(svgNS, "g");
		chronologyPlot.setAttributeNS(null, "id", "chronology_plot");
		chronologyPlot.setAttributeNS(null, "display", "inline");
		
		// Build all of the series
		ArrayList<Boolean> series_visible = new ArrayList<>();
		
		this.showPith = App.prefs.getBooleanPref(PrefKey.CHART_SHOW_PITH_SYMBOL, true);
		this.showBark = App.prefs.getBooleanPref(PrefKey.CHART_SHOW_BARK_SYMBOL, true);
		this.showFires = App.prefs.getBooleanPref(PrefKey.CHART_SHOW_FIRE_EVENT_SYMBOL, true);
		this.showInjuries = App.prefs.getBooleanPref(PrefKey.CHART_SHOW_INJURY_SYMBOL, true);
		this.showInnerRing = App.prefs.getBooleanPref(PrefKey.CHART_SHOW_INNER_RING_SYMBOL, true);
		this.showOuterRing = App.prefs.getBooleanPref(PrefKey.CHART_SHOW_OUTER_RING_SYMBOL, true);
		int fontSize = App.prefs.getIntPref(PrefKey.CHART_CHRONOLOGY_PLOT_LABEL_FONT_SIZE, 8);
		
		String longestLabel = "A";
		for (int i = 0; i < seriesSVGList.size(); i++)
		{
			FHSeries series = seriesSVGList.get(i);
			if (series.getTitle().length() > longestLabel.length())
				longestLabel = series.getTitle();
		}
		
		widestChronologyLabelSize = FireChartUtil.getStringWidth(Font.PLAIN,
				App.prefs.getIntPref(PrefKey.CHART_CHRONOLOGY_PLOT_LABEL_FONT_SIZE, 10), longestLabel);
		
		// Define a string for keeping track of the category groups
		ArrayList<String> categoryGroupsProcessed = new ArrayList<>();
		
		for (int i = 0; i < seriesSVGList.size(); i++)
		{
			if (lastTypeSortedBy == SeriesSortType.CATEGORY && App.prefs.getBooleanPref(PrefKey.CHART_SHOW_CATEGORY_GROUPS, true))
			{
				String currentCategoryGroup = seriesSVGList.get(i).getCategoryEntries().get(0).getContent();
				
				// Keep track of which category groups have already been processed
				if (!categoryGroupsProcessed.contains(currentCategoryGroup))
				{
					categoryGroupsProcessed.add(currentCategoryGroup);
				}
				
				// Apply the series coloring as necessary
				if (App.prefs.getBooleanPref(PrefKey.CHART_AUTOMATICALLY_COLORIZE_SERIES, false))
				{
					seriesSVGList.get(i).setLabelColor(FireChartUtil.pickColorFromInteger(categoryGroupsProcessed.size()));
					seriesSVGList.get(i).setLineColor(FireChartUtil.pickColorFromInteger(categoryGroupsProcessed.size()));
				}
				else
				{
					seriesSVGList.get(i).setLabelColor(Color.BLACK);
					seriesSVGList.get(i).setLineColor(Color.BLACK);
				}
			}
			
			FHSeriesSVG seriesSVG = seriesSVGList.get(i);
			series_visible.add(true);
			
			Element series_group = doc.createElementNS(svgNS, "g");
			series_group.setAttributeNS(null, "id", "series_group_" + seriesSVG.getTitle());
			
			// Add in the series group, which has the lines and ticks
			Element series_line = buildSingleSeriesLine(seriesSVG);
			series_line.setAttributeNS(null, "id", "series_line_" + seriesSVG.getTitle());
			int x_offset = applyBCYearOffset(seriesSVG.getFirstYear()) - getFirstChartYear();
			String translate_string = "translate(" + Integer.toString(x_offset) + ",0)";
			String scale_string = "scale(" + FireChartUtil.yearsToPixels(chartWidth, getFirstChartYear(), getLastChartYear()) + ",1)";
			series_line.setAttributeNS(null, "transform", scale_string + " " + translate_string);
			
			// Add in the label for the series
			Element series_name = seriesEB.getSeriesNameTextElement(seriesSVG, fontSize);
			
			// Add in the up button
			Element up_button_g = doc.createElementNS(svgNS, "g");
			up_button_g.setAttributeNS(null, "id", "up_button" + i);
			up_button_g.setAttributeNS(null, "class", "no_export");
			up_button_g.setAttributeNS(null, "transform",
					"translate(" + Double.toString(chartWidth + 15 + widestChronologyLabelSize) + ",-2)");
			up_button_g.setAttributeNS(null, "onclick", "FireChartSVG.getChart(chart_num).moveSeriesUp(\"" + seriesSVG.getTitle()
					+ "\"); evt.target.setAttribute('opacity', '0.2');");
			up_button_g.setAttributeNS(null, "onmouseover", "evt.target.setAttribute('opacity', '1');");
			up_button_g.setAttributeNS(null, "onmouseout", "evt.target.setAttribute('opacity', '0.2');");
			up_button_g.appendChild(seriesEB.getUpButton());
			
			// Add in the down button
			Element down_button_g = doc.createElementNS(svgNS, "g");
			down_button_g.setAttributeNS(null, "id", "down_button" + i);
			down_button_g.setAttributeNS(null, "class", "no_export");
			down_button_g.setAttributeNS(null, "transform",
					"translate(" + Double.toString(chartWidth + 10 + widestChronologyLabelSize + 15) + ",-2)");
			down_button_g.setAttributeNS(null, "onclick", "FireChartSVG.getChart(chart_num).moveSeriesDown(\"" + seriesSVG.getTitle()
					+ "\"); evt.target.setAttribute('opacity', '0.2');");
			down_button_g.setAttributeNS(null, "onmouseover", "evt.target.setAttribute('opacity', '1');");
			down_button_g.setAttributeNS(null, "onmouseout", "evt.target.setAttribute('opacity', '0.2');");
			down_button_g.appendChild(seriesEB.getDownButton());
			
			// Determine whether to draw the chronology plot labels
			if (App.prefs.getBooleanPref(PrefKey.CHART_SHOW_CHRONOLOGY_PLOT_LABELS, true))
			{
				if (lastTypeSortedBy == SeriesSortType.CATEGORY && App.prefs.getBooleanPref(PrefKey.CHART_SHOW_CATEGORY_GROUPS, true))
				{
					// Do not show the up/down buttons if grouping series by category
					up_button_g.setAttributeNS(null, "display", "none");
					down_button_g.setAttributeNS(null, "display", "none");
				}
				else
				{
					series_name.setAttributeNS(null, "display", "inline");
					up_button_g.setAttributeNS(null, "display", "inline");
					down_button_g.setAttributeNS(null, "display", "inline");
				}
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
	 * Get the composite plot as an element.
	 * 
	 * @return compositePlot
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
			compositeYearLabelMaxWidth = FireChartUtil.getStringWidth(Font.PLAIN, compositeYearLabelFontSize, textStr) + stringbuffer;
			compositeYearLabelHeight = FireChartUtil.getStringHeight(Font.PLAIN, compositeYearLabelFontSize, textStr);
		}
		else if (rotateLabelsAngle == 315)
		{
			int width = FireChartUtil.getStringWidth(Font.PLAIN, compositeYearLabelFontSize, textStr);
			
			double widthsq = width * width;
			double hyp = Math.sqrt(widthsq + widthsq);
			
			compositeYearLabelMaxWidth = (int) ((hyp + stringbuffer)) / 2;
			compositeYearLabelHeight = (int) hyp;
		}
		else
		{
			compositeYearLabelMaxWidth = FireChartUtil.getStringHeight(Font.PLAIN, compositeYearLabelFontSize, textStr);
			compositeYearLabelHeight = FireChartUtil.getStringWidth(Font.PLAIN, compositeYearLabelFontSize, textStr) + stringbuffer;
		}
		
		// compositePlot is centered off of the year 0 A.D.
		Element compositePlot = doc.createElementNS(svgNS, "g");
		compositePlot.setAttributeNS(null, "id", "comp_plot");
		compositePlot.setAttributeNS(null, "transform",
				"scale(" + FireChartUtil.yearsToPixels(chartWidth, getFirstChartYear(), getLastChartYear()) + "," + 1 + ") translate(-"
						+ getFirstChartYear() + ",0) ");
		
		// draw the vertical lines for fire years
		ArrayList<Integer> composite_years = reader.getCompositeFireYears(
				App.prefs.getEventTypePref(PrefKey.CHART_COMPOSITE_EVENT_TYPE, EventTypeToProcess.FIRE_EVENT),
				App.prefs.getFireFilterTypePref(PrefKey.CHART_COMPOSITE_FILTER_TYPE, FireFilterType.NUMBER_OF_EVENTS),
				App.prefs.getIntPref(PrefKey.CHART_COMPOSITE_FILTER_VALUE, 1),
				App.prefs.getIntPref(PrefKey.CHART_COMPOSITE_MIN_NUM_SAMPLES, 1),
				App.prefs.getSampleDepthFilterTypePref(PrefKey.CHART_COMPOSITE_SAMPLE_DEPTH_TYPE, SampleDepthFilterType.MIN_NUM_SAMPLES));
		
		// Remove out-of-range years if necessary
		if (!App.prefs.getBooleanPref(PrefKey.CHART_AXIS_X_AUTO_RANGE, true))
		{
			ArrayList<Integer> composite_years2 = new ArrayList<>();
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
				double pixelsBetweenLabels = FireChartUtil.yearsToPixels(i, chartWidth, getFirstChartYear(), getLastChartYear())
						- FireChartUtil.yearsToPixels(prev_i, chartWidth, getFirstChartYear(), getLastChartYear());
				
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
			compositePlot.appendChild(compositePlotEB.getEventLine(i, chart_height));
			
			// calculate the offsets for the labels
			if (FireChartUtil.yearsToPixels(i - prev_i, chartWidth, getFirstChartYear(), getLastChartYear()) < overlap_margin)
			{
				cur_offset_level = (cur_offset_level + 1) % num_layers;
			}
			else
			{
				cur_offset_level = 0;
			}
			
			Element text_g = doc.createElementNS(svgNS, "g");
			String scale_str = "scale(" + FireChartUtil.pixelsToYears(chartWidth, getFirstChartYear(), getLastChartYear()) + ", 1)";
			
			if (rotateLabelsAngle == 270)
			{
				double offset = chart_height + (cur_offset_level * compositeYearLabelHeight) + compositeYearLabelHeight;
				String translate_str = "translate(" + (Double.toString(i + (FireChartUtil.pixelsToYears(compositeYearLabelMaxWidth / 2,
						chartWidth, getFirstChartYear(), getLastChartYear())))) + "," + offset + ")";
				text_g.setAttributeNS(null, "transform", translate_str + scale_str + " rotate(" + rotateLabelsAngle + ")");
			}
			else if (rotateLabelsAngle == 315)
			{
				double offset = chart_height + (cur_offset_level * (compositeYearLabelHeight / 3)) + compositeYearLabelHeight / 1.3;
				String translate_str = "translate(" + (Double.toString(i - (FireChartUtil.pixelsToYears(compositeYearLabelMaxWidth / 2,
						chartWidth, getFirstChartYear(), getLastChartYear())))) + "," + offset + ")";
				text_g.setAttributeNS(null, "transform", translate_str + scale_str + " rotate(" + rotateLabelsAngle + ")");
			}
			else
			{
				double offset = chart_height + (cur_offset_level * compositeYearLabelHeight) + compositeYearLabelHeight;
				String translate_str = "translate(" + (Double.toString(i - (FireChartUtil.pixelsToYears(compositeYearLabelMaxWidth / 2,
						chartWidth, getFirstChartYear(), getLastChartYear())))) + "," + offset + ")";
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
			year_text.setAttributeNS(null, "font-family", App.prefs.getPref(PrefKey.CHART_FONT_FAMILY, "Verdana"));
			year_text.setAttributeNS(null, "font-size", Integer.toString(compositeYearLabelFontSize));
			
			if (showLabels)
			{
				year_text.appendChild(year_text_t);
				text_g.appendChild(year_text);
			}
			
			compositePlot.appendChild(text_g);
			prev_i = i;
		}
		
		// Draw a rectangle around it
		// Needs to be 4 lines to cope with stroke width in different coord sys in x and y
		compositePlot.appendChild(compositePlotEB.getBorderLine1());
		compositePlot.appendChild(compositePlotEB.getBorderLine2(chart_height));
		compositePlot.appendChild(compositePlotEB.getBorderLine3(chart_height));
		compositePlot.appendChild(compositePlotEB.getBorderLine4(chart_height));
		
		// add the label
		String translate_string = "translate("
				+ Double.toString(getLastChartYear() + FireChartUtil.pixelsToYears(10, chartWidth, getFirstChartYear(), getLastChartYear()))
				+ ", "
				+ ((chart_height / 2)
						+ (FireChartUtil.getStringHeight(Font.PLAIN, App.prefs.getIntPref(PrefKey.CHART_COMPOSITE_PLOT_LABEL_FONT_SIZE, 10),
								App.prefs.getPref(PrefKey.CHART_COMPOSITE_LABEL_TEXT, "Composite"))) / 2)
				+ ")";
		String scale_string = "scale(" + FireChartUtil.pixelsToYears(chartWidth, getFirstChartYear(), getLastChartYear()) + ", 1)";
		
		Element comp_name_text_g = doc.createElementNS(svgNS, "g");
		comp_name_text_g.setAttributeNS(null, "transform", translate_string + scale_string);
		comp_name_text_g.appendChild(compositePlotEB.getCompositeLabelTextElement());
		compositePlot.appendChild(comp_name_text_g);
		
		if (App.prefs.getBooleanPref(PrefKey.CHART_SHOW_COMPOSITE_PLOT, true))
		{
			compositePlot.setAttributeNS(null, "display", "inline");
		}
		else
		{
			compositePlot.setAttributeNS(null, "display", "none");
		}
		
		return compositePlot;
	}
	
	/**
	 * This function creates a legend dynamically, based on the current event(s) displayed on the canvas (Fire, Injury, or Fire and Injury).
	 * 
	 * @return legend
	 */
	private Element getLegend() {
		
		if (!App.prefs.getBooleanPref(PrefKey.CHART_SHOW_LEGEND, true))
		{
			Element legend = doc.createElementNS(svgNS, "g");
			return legend;
		}
		
		int labelWidth = FireChartUtil.getStringWidth(Font.PLAIN, 8, "Outer year without bark") + 40;
		int labelHeight = FireChartUtil.getStringHeight(Font.PLAIN, 8, "Outer year without bark");
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
		recorder_g.appendChild(legendEB.getRecorderYearExample());
		Element recorder_desc = legendEB.getDescriptionTextElement("Recorder year", leftJustified, currentY + (labelHeight / 2));
		recorder_g.appendChild(recorder_desc);
		legend.appendChild(recorder_g);
		
		// NON-RECORDER YEAR
		currentY += moveValue;
		Element nonrecorder_g = doc.createElementNS(svgNS, "g");
		nonrecorder_g.appendChild(legendEB.getNonRecorderYearExample(currentY));
		Element nonrecorder_desc = legendEB.getDescriptionTextElement("Non-recorder year", leftJustified, currentY + (labelHeight / 2));
		nonrecorder_g.appendChild(nonrecorder_desc);
		legend.appendChild(nonrecorder_g);
		
		// currentY += moveValue * 2; // so next symbol is at spot y = 100
		
		if (App.prefs.getBooleanPref(PrefKey.CHART_SHOW_FIRE_EVENT_SYMBOL, true))
		{
			// FIRE EVENT MARKER
			currentY += moveValue;
			Element fireMarker_g = doc.createElementNS(svgNS, "g");
			Element fireMarker = seriesEB.getFireYearMarker(Color.BLACK);
			fireMarker.setAttributeNS(null, "width", "2");
			fireMarker_g.appendChild(fireMarker);
			fireMarker_g.setAttributeNS(null, "transform", "translate(0, " + currentY + ")");
			Element fireMarker_desc = legendEB.getDescriptionTextElement("Fire event", leftJustified, (labelHeight / 2));
			fireMarker_g.appendChild(fireMarker_desc);
			legend.appendChild(fireMarker_g);
		}
		
		if (App.prefs.getBooleanPref(PrefKey.CHART_SHOW_INJURY_SYMBOL, true))
		{
			// INJURY EVENT MARKER
			currentY += moveValue;
			Element injuryMarker_g = doc.createElementNS(svgNS, "g");
			injuryMarker_g.appendChild(seriesEB.getInjuryYearMarker(3, Color.BLACK));
			injuryMarker_g.setAttributeNS(null, "transform", "translate(0, " + Integer.toString(currentY) + ")");
			Element injuryMarker_desc = legendEB.getDescriptionTextElement("Injury event", leftJustified, (labelHeight / 2));
			injuryMarker_g.appendChild(injuryMarker_desc);
			legend.appendChild(injuryMarker_g);
		}
		
		// PITH WITH NON-RECORDER LINE
		currentY += moveValue;
		Element innerPith_g = doc.createElementNS(svgNS, "g");
		Element innerPith = seriesEB.getInnerYearPithMarker(true, 5, Color.BLACK);
		innerPith_g.appendChild(innerPith);
		innerPith_g.setAttributeNS(null, "transform", "translate(0, " + Integer.toString(currentY) + ")");
		Element pithNonrecorder_g = doc.createElementNS(svgNS, "g");
		pithNonrecorder_g.appendChild(legendEB.getPithWithNonRecorderLineExample());
		innerPith_g.appendChild(pithNonrecorder_g);
		Element pithNonrecorder_desc = legendEB.getDescriptionTextElement("Inner year with pith", leftJustified, (labelHeight / 2));
		innerPith_g.appendChild(pithNonrecorder_desc);
		legend.appendChild(innerPith_g);
		
		// NO PITH WITH NON-RECORDER LINE
		currentY += moveValue;
		Element withoutPith_g = doc.createElementNS(svgNS, "g");
		Element withoutPith = seriesEB.getInnerYearPithMarker(false, SERIES_HEIGHT, Color.BLACK);
		withoutPith_g.appendChild(withoutPith);
		withoutPith_g.setAttributeNS(null, "transform", "translate(0, " + Integer.toString(currentY) + ")");
		Element withoutPithNonrecorder_g = doc.createElementNS(svgNS, "g");
		withoutPithNonrecorder_g.appendChild(legendEB.getNoPithWithNonRecorderLineExample());
		withoutPith_g.appendChild(withoutPithNonrecorder_g);
		Element withoutPithNonrecorder_desc = legendEB.getDescriptionTextElement("Inner year without pith", leftJustified,
				(labelHeight / 2));
		withoutPith_g.appendChild(withoutPithNonrecorder_desc);
		legend.appendChild(withoutPith_g);
		
		// BARK WITH RECORDER LINE
		currentY += moveValue;
		Element withBark_g = doc.createElementNS(svgNS, "g");
		Element withBark = seriesEB.getOuterYearBarkMarker(true, 5, Color.BLACK);
		withBark_g.appendChild(withBark);
		withBark_g.setAttributeNS(null, "transform", "translate(5, " + Integer.toString(currentY) + ")");
		Element barkRecorder_g = doc.createElementNS(svgNS, "g");
		barkRecorder_g.appendChild(legendEB.getBarkWithRecorderLineExample());
		withBark_g.appendChild(barkRecorder_g);
		Element barkRecorder_desc = legendEB.getDescriptionTextElement("Outer year with bark", leftJustified - 5, (labelHeight / 2));
		withBark_g.appendChild(barkRecorder_desc);
		legend.appendChild(withBark_g);
		
		// NO BARK WITH RECORDER LINE
		currentY += moveValue;
		Element withoutBark_g = doc.createElementNS(svgNS, "g");
		Element withoutBark = seriesEB.getOuterYearBarkMarker(false, SERIES_HEIGHT, Color.BLACK);
		withoutBark_g.appendChild(withoutBark);
		withoutBark_g.setAttributeNS(null, "transform", "translate(5, " + Integer.toString(currentY) + ")");
		Element withoutBarkRecorder_g = doc.createElementNS(svgNS, "g");
		withoutBarkRecorder_g.appendChild(legendEB.getNoBarkWithRecorderLineExample());
		withoutBark_g.appendChild(withoutBarkRecorder_g);
		Element withoutBarkRecorder_desc = legendEB.getDescriptionTextElement("Outer year without bark", leftJustified - 5,
				(labelHeight / 2));
		withoutBark_g.appendChild(withoutBarkRecorder_desc);
		legend.appendChild(withoutBark_g);
		
		// ADD FILTER DETAILS
		if (App.prefs.getBooleanPref(PrefKey.CHART_SHOW_FILTER_IN_LEGEND, false))
		{
			String s = "";
			String longestLabel = s;
			if (App.prefs.getEventTypePref(PrefKey.CHART_COMPOSITE_EVENT_TYPE, EventTypeToProcess.FIRE_EVENT)
					.equals(EventTypeToProcess.FIRE_EVENT))
			{
				currentY += moveValue + (labelHeight * 1.3);
				s = StringUtils.capitalize("Composite based on " + App.prefs
						.getEventTypePref(PrefKey.CHART_COMPOSITE_EVENT_TYPE, EventTypeToProcess.FIRE_EVENT).toString().toLowerCase()
						+ " and filtered by:");
				Element filterType = legendEB.getDescriptionTextElement(s, leftJustified, (labelHeight / 2));
				filterType.setAttributeNS(null, "transform", "translate(-25, " + Integer.toString(currentY) + ")");
				legend.appendChild(filterType);
				longestLabel = s;
			}
			else if (App.prefs.getEventTypePref(PrefKey.CHART_COMPOSITE_EVENT_TYPE, EventTypeToProcess.FIRE_AND_INJURY_EVENT)
					.equals(EventTypeToProcess.FIRE_AND_INJURY_EVENT))
			{
				currentY += moveValue + (labelHeight * 1.3);
				s = StringUtils.capitalize("Composite based on " + App.prefs
						.getEventTypePref(PrefKey.CHART_COMPOSITE_EVENT_TYPE, EventTypeToProcess.FIRE_EVENT).toString().toLowerCase());
				Element filterType = legendEB.getDescriptionTextElement(s, leftJustified, (labelHeight / 2));
				filterType.setAttributeNS(null, "transform", "translate(-25, " + Integer.toString(currentY) + ")");
				legend.appendChild(filterType);
				
				currentY += labelHeight * 1.3;
				filterType = legendEB.getDescriptionTextElement("and filtered by:", leftJustified, (labelHeight / 2));
				filterType.setAttributeNS(null, "transform", "translate(-25, " + Integer.toString(currentY) + ")");
				legend.appendChild(filterType);
				
				longestLabel = s;
			}
			else if (App.prefs.getEventTypePref(PrefKey.CHART_COMPOSITE_EVENT_TYPE, EventTypeToProcess.INJURY_EVENT)
					.equals(EventTypeToProcess.INJURY_EVENT))
			{
				currentY += moveValue + (labelHeight * 1.3);
				s = StringUtils.capitalize("Composite based on " + App.prefs
						.getEventTypePref(PrefKey.CHART_COMPOSITE_EVENT_TYPE, EventTypeToProcess.FIRE_EVENT).toString().toLowerCase()
						+ " and filtered by:");
				Element filterType = legendEB.getDescriptionTextElement(s, leftJustified, (labelHeight / 2));
				filterType.setAttributeNS(null, "transform", "translate(-25, " + Integer.toString(currentY) + ")");
				legend.appendChild(filterType);
				longestLabel = s;
			}
			
			currentY += labelHeight * 1.3;
			s = " - " + StringUtils
					.capitalize(App.prefs.getFireFilterTypePref(PrefKey.CHART_COMPOSITE_FILTER_TYPE, FireFilterType.NUMBER_OF_EVENTS)
							.toString().toLowerCase() + " >= " + App.prefs.getIntPref(PrefKey.CHART_COMPOSITE_FILTER_VALUE, 1));
			Element filterType = legendEB.getDescriptionTextElement(s, leftJustified, (labelHeight / 2));
			filterType.setAttributeNS(null, "transform", "translate(-25, " + Integer.toString(currentY) + ")");
			legend.appendChild(filterType);
			if (s.length() > longestLabel.length())
				longestLabel = s;
			
			currentY += labelHeight * 1.3;
			s = " - " + StringUtils.capitalize(
					App.prefs.getSampleDepthFilterTypePref(PrefKey.CHART_COMPOSITE_SAMPLE_DEPTH_TYPE, SampleDepthFilterType.MIN_NUM_SAMPLES)
							.toString().toLowerCase() + " >= " + App.prefs.getIntPref(PrefKey.CHART_COMPOSITE_MIN_NUM_SAMPLES, 1));
			filterType = legendEB.getDescriptionTextElement(s, leftJustified, (labelHeight / 2));
			filterType.setAttributeNS(null, "transform", "translate(-25, " + Integer.toString(currentY) + ")");
			legend.appendChild(filterType);
			
			labelWidth = FireChartUtil.getStringWidth(Font.PLAIN, 8, longestLabel) + 10;
		}
		
		// Add rectangle around legend and append
		legend.setAttributeNS(null, "transform", "scale(1.0)");
		legend.appendChild(legendEB.getChartRectangle(labelWidth, currentY));
		
		// Only show the chart if the preference has been set to do so
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
	 * Get the time axis including the guide and highlight lines.
	 * 
	 * @param height
	 * @return
	 */
	private Element getTimeAxis(int height) {
		
		// Time axis is centered off of the first year in the reader
		Element timeAxis = doc.createElementNS(svgNS, "g");
		String scale = "scale(" + FireChartUtil.yearsToPixels(chartWidth, getFirstChartYear(), getLastChartYear()) + ",1)";
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
				
				timeAxis.appendChild(timeAxisEB.getHighlightLine(i, height));
			}
		}
		
		for (int i = getFirstChartYear(); i < getLastChartYear(); i++)
		{
			if (i % majorTickInterval == 0)
			{ // year is a multiple of tickInterval
				if (vertGuides)
				{
					int vertGuidesOffsetAmount = 0;
					
					if (App.prefs.getBooleanPref(PrefKey.CHART_SHOW_CHART_TITLE, true))
					{
						vertGuidesOffsetAmount = App.prefs.getIntPref(PrefKey.CHART_TITLE_FONT_SIZE, 20) + 10;
					}
					
					timeAxis.appendChild(timeAxisEB.getVerticalGuide(i, vertGuidesOffsetAmount, height));
				}
				
				if (majorTicks)
				{
					timeAxis.appendChild(timeAxisEB.getMajorTick(i, height));
				}
				
				Element year_text_g = doc.createElementNS(svgNS, "g");
				
				year_text_g.setAttributeNS(null, "transform", "translate(" + i + "," + height + ") scale("
						+ FireChartUtil.pixelsToYears(chartWidth, getFirstChartYear(), getLastChartYear()) + ",1)");
				
				year_text_g.appendChild(timeAxisEB.getYearTextElement(removeBCYearOffset(i)));
				
				timeAxis.appendChild(year_text_g);
			}
			
			if (minorTicks && i % minorTickInterval == 0) // && i % tickInterval != 0)
			{
				timeAxis.appendChild(timeAxisEB.getMinorTick(i, height));
			}
		}
		
		timeAxis.appendChild(timeAxisEB.getTimeAxis(height));
		
		return timeAxis;
	}
	
	/**
	 * Get the percent scarred plot including bounding box and y2 axis.
	 * 
	 * @return
	 */
	private Element getPercentScarredPlot() {
		
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
				"scale(" + FireChartUtil.yearsToPixels(chartWidth, getFirstChartYear(), getLastChartYear()) + ",1)");
		scarred_g.appendChild(scarred_scale_g);
		
		// draw in vertical bars
		double[] percent_arr = reader.getPercentOfRecordingScarred(
				App.prefs.getEventTypePref(PrefKey.CHART_COMPOSITE_EVENT_TYPE, EventTypeToProcess.FIRE_EVENT));
		
		// Limit to specified years if necessary
		if (!App.prefs.getBooleanPref(PrefKey.CHART_AXIS_X_AUTO_RANGE, true))
		{
			int startindex_file = this.getFirstChartYear() - applyBCYearOffset(reader.getFirstYear());
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
				scarred_scale_g.appendChild(percentScarredPlotEB.getVerticalLine(i, percent));
			}
		}
		
		// draw a rectangle around it
		// Needs to be 4 lines to cope with stroke width in different coord sys in x and y
		scarred_scale_g.appendChild(percentScarredPlotEB.getBorderLine1());
		scarred_scale_g.appendChild(percentScarredPlotEB.getBorderLine2(unscale_y));
		scarred_scale_g.appendChild(percentScarredPlotEB.getBorderLine3());
		scarred_scale_g.appendChild(percentScarredPlotEB.getBorderLine4(unscale_y));
		
		// draw in the labels
		int yAxisFontSize = App.prefs.getIntPref(PrefKey.CHART_AXIS_Y2_FONT_SIZE, 10);
		int labelHeight = FireChartUtil.getStringHeight(Font.PLAIN, yAxisFontSize, "100");
		int labelY = labelHeight / 2;
		
		for (int i = 0; i <= 100; i += 25)
		{
			Element unscale_g = doc.createElementNS(svgNS, "g");
			String x = Double.toString(chartWidth);
			String y = Integer.toString(i);
			unscale_g.setAttributeNS(null, "transform", "translate(" + x + "," + y + ") scale(1," + unscale_y + ")");
			unscale_g.appendChild(percentScarredPlotEB.getPercentScarredTextElement(labelY, i, yAxisFontSize));
			unscale_g.appendChild(percentScarredPlotEB.getHorizontalTick(unscale_y));
			scarred_g.appendChild(unscale_g);
		}
		
		// add in the label that says "% Scarred"
		Element unscale_g = doc.createElementNS(svgNS, "g");
		unscale_g.setAttributeNS(null, "transform", "scale(1," + unscale_y + ")");
		
		Element rotate_g = doc.createElementNS(svgNS, "g");
		String x = Double.toString(chartWidth + 5 + 10 + FireChartUtil.getStringWidth(Font.PLAIN, yAxisFontSize, "100"));
		String y = Double.toString(scale_y * 100);
		rotate_g.setAttributeNS(null, "transform", "translate(" + x + "," + y + ") rotate(90)");
		
		Text label_t = doc.createTextNode(App.prefs.getPref(PrefKey.CHART_AXIS_Y2_LABEL, "% Scarred"));
		Element label = doc.createElementNS(svgNS, "text");
		label.setAttributeNS(null, "font-family", App.prefs.getPref(PrefKey.CHART_FONT_FAMILY, "Verdana"));
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
	
	/**
	 * Get the sample or recorder depth plot.
	 * 
	 * @param plotSampleNorRecordingDepth
	 * @return
	 */
	private Element getSampleOrRecorderDepthsPlot(boolean plotSampleNorRecordingDepth, EventTypeToProcess eventTypeToProcess) {
		
		Element sample_g = doc.createElementNS(svgNS, "g");
		Element sample_g_chart = doc.createElementNS(svgNS, "g"); // scales the years on the x direction
		
		sample_g.setAttributeNS(null, "id", "depths");
		
		int[] sample_depths;
		if (plotSampleNorRecordingDepth)
			sample_depths = reader.getSampleDepths();
		else
			sample_depths = reader.getRecordingDepths(eventTypeToProcess);
		
		// Limit to specified years if necessary
		if (!App.prefs.getBooleanPref(PrefKey.CHART_AXIS_X_AUTO_RANGE, true))
		{
			int startindex_file = this.getFirstChartYear() - applyBCYearOffset(reader.getFirstYear());
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
				"scale(" + FireChartUtil.yearsToPixels(chartWidth, getFirstChartYear(), getLastChartYear()) + ",1)");
		
		// error check
		if (sample_depths.length == 0)
		{
			return sample_g;
		}
		
		// build the trend line
		int begin_index = 0;
		String lineColor = FireChartUtil.colorToHexString(App.prefs.getColorPref(PrefKey.CHART_SAMPLE_OR_RECORDER_DEPTH_COLOR, Color.BLUE));
		for (int i = 1; i < sample_depths.length; i++)
		{
			if (sample_depths[i] != sample_depths[begin_index])
			{
				sample_g_chart.appendChild(
						sampleRecorderPlotEB.getVerticalTrendLinePart(lineColor, i, sample_depths[begin_index], sample_depths[i]));
				
				sample_g_chart.appendChild(
						sampleRecorderPlotEB.getHorizontalTrendLinePart(lineColor, scale_y, begin_index, i, sample_depths[begin_index]));
				
				begin_index = i;
			}
			
			// draw in the final line
			if (i + 1 == sample_depths.length)
			{
				sample_g_chart.appendChild(
						sampleRecorderPlotEB.getHorizontalTrendLinePart(lineColor, scale_y, begin_index, i, sample_depths[begin_index]));
			}
		}
		
		// add the threshold depth
		sample_g_chart.appendChild(sampleRecorderPlotEB.getThresholdLine(scale_y, largest_sample_depth));
		
		// add in the tick lines
		int num_ticks = FireChartUtil.calculateNumSampleDepthTicks(largest_sample_depth);
		int tick_spacing = (int) Math.ceil((double) largest_sample_depth / (double) num_ticks);
		int yAxisFontSize = App.prefs.getIntPref(PrefKey.CHART_AXIS_Y1_FONT_SIZE, 10);
		int labelHeight = FireChartUtil.getStringHeight(Font.PLAIN, yAxisFontSize, "9");
		int labelY = labelHeight / 2;
		
		for (int i = 0; i < num_ticks; i++)
		{
			sample_g.appendChild(sampleRecorderPlotEB.getHorizontalTick(unscale_y, i, tick_spacing));
			
			Element unscale_g = doc.createElementNS(svgNS, "g");
			unscale_g.setAttributeNS(null, "transform", "translate(-5," + (i * tick_spacing) + ") scale(1," + (1.0 / scale_y) + ")");
			unscale_g.appendChild(sampleRecorderPlotEB.getDepthCountTextElement(labelY, yAxisFontSize, i, tick_spacing));
			
			sample_g.appendChild(unscale_g);
		}
		
		// add in label that says "Sample Depth"
		int labelWidth = FireChartUtil.getStringWidth(Font.PLAIN, yAxisFontSize, num_ticks * tick_spacing + "");
		
		Element unscale_g = doc.createElementNS(svgNS, "g");
		unscale_g.setAttributeNS(null, "transform",
				"translate(" + (-5 - labelWidth - 10) + "," + 0 + ") scale(1," + (1.0 / scale_y) + ") rotate(270)");
		
		unscale_g.appendChild(sampleRecorderPlotEB.getSampleDepthTextElement());
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
	
	/**
	 * Replaces the currently displayed (or hidden) chronology plot with a freshly generated chronology plot.
	 */
	private void rebuildChronologyPlot() {
		
		Element chrono_plot_g = doc.getElementById("chrono_plot_g");
		deleteAllChildren(chrono_plot_g);
		chrono_plot_g.appendChild(getChronologyPlot());
		positionSeriesLines();
		positionChartGroupersAndDrawTimeAxis();
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
	
	/**
	 * Set the visibility of the composite plot based on the preferences.
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
	
	/**
	 * Set the visibility of the legend based on the preferences.
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
	 * Set the visibility of the series labels based on the preferences.
	 */
	public void setSeriesLabelsVisibility() {
		
		boolean isSeriesLabelVisible = App.prefs.getBooleanPref(PrefKey.CHART_SHOW_CHRONOLOGY_PLOT_LABELS, true);
		
		for (FHSeriesSVG seriesSVG : seriesSVGList)
		{
			Element ser = doc.getElementById("series_label_" + seriesSVG.getTitle());
			if (isSeriesLabelVisible)
				ser.setAttributeNS(null, "display", "inline");
			else
				ser.setAttributeNS(null, "display", "none");
		}
		
		for (int i = 0; i < seriesSVGList.size(); i++)
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
	 * Set the visibility of the no-export elements based on the input parameter.
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
	
	/**
	 * This function toggles the visibility of the series at the given location.
	 * 
	 * @param index of the series to hide
	 */
	public void toggleVisibilityOfSeries(int index) {
		
		FHSeriesSVG seriesToHide = seriesSVGList.get(index);
		seriesToHide.toggleVisibility();
		seriesSVGList.set(index, seriesToHide);
		positionSeriesLines();
		positionChartGroupersAndDrawTimeAxis();
	}
	
	// ============== Annotation ==============
	// There is a <rect id="annote_canvas"> element in the DOM under <g id="annote_g">.
	// It is used to catch mouse events in order to add, resize, or delete annotation rectangles.
	// The enum Mode is used to track whether the user has selected add, resize, &etc.
	// All mode checking will be done java-side to simplify the js.
	// In other words, rectangles will always call deleteAnnoteRect when clicked, and it is up
	// to deleteAnnoteRect to ensure that the rect only gets deleted when the user is in the eraser mode
	// ========================================
	
	/**
	 * Gets the annote canvas as an element.
	 * 
	 * @return annote_canvas
	 */
	public Element getAnnoteCanvas() {
		
		try
		{
			Element annote_canvas = doc.createElementNS(svgNS, "rect");
			annote_canvas.setAttributeNS(null, "id", "annote_canvas");
			annote_canvas.setAttributeNS(null, "width", this.chartWidth + "");
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
	 * Draws a line on the annotation grouper.
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
			
			String id = "annote_line_" + (lineGensym++);
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
	 * Removes a line from the annotation grouper.
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
	 * Sets the annote mode according to the input parameter.
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
		
		Double pixelsForProportion = this.chartWidth * (prop / 1000.0);
		return FireChartUtil.pixelsToYears(pixelsForProportion, chartWidth, getFirstChartYear(), getLastChartYear());
	}
	
	/**
	 * Handles printing of the SVG document.
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
	 * Sorts the series according to the sort by preference.
	 */
	private void sortSeriesAccordingToPreference() {
		
		String sortByPreference = App.prefs.getPref(PrefKey.CHART_SORT_BY_PREFERENCE, SeriesSortType.NAME.toString());
		
		if (sortByPreference.equals(SeriesSortType.NAME.toString()))
		{
			sortByName();
		}
		else if (sortByPreference.equals(SeriesSortType.CATEGORY.toString()))
		{
			sortByCategory();
		}
		else if (sortByPreference.equals(SeriesSortType.FIRST_FIRE_YEAR.toString()))
		{
			sortByFirstFireYear();
		}
		else if (sortByPreference.equals(SeriesSortType.SAMPLE_START_YEAR.toString()))
		{
			sortBySampleStartYear();
		}
		else if (sortByPreference.equals(SeriesSortType.SAMPLE_END_YEAR.toString()))
		{
			sortBySampleEndYear();
		}
		else
		{
			sortByPositionInFile();
		}
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
		
		Collections.sort(seriesSVGList, comparator);
		lastTypeSortedBy = SeriesSortType.NAME;
		rebuildChronologyPlot();
		
		log.debug("Finished sorting chart series by name");
	}
	
	/**
	 * Sort the series by category. Currently this only sorts by the first entry of a series. This will need to be changed once the TRIDAS
	 * format is implemented. TODO
	 */
	public void sortByCategory() {
		
		Comparator<FHSeriesSVG> comparator = new Comparator<FHSeriesSVG>() {
			
			@Override
			public int compare(FHSeriesSVG c1, FHSeriesSVG c2) {
				
				String c1_first_category_entry = c1.getCategoryEntries().get(0).getContent();
				String c2_first_category_entry = c2.getCategoryEntries().get(0).getContent();
				
				return c1_first_category_entry.compareTo(c2_first_category_entry);
			}
		};
		
		Collections.sort(seriesSVGList, comparator);
		lastTypeSortedBy = SeriesSortType.CATEGORY;
		rebuildChronologyPlot();
		
		log.debug("Finished sorting chart series by category");
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
				int c1_first_fire_year = applyBCYearOffset(c1.getFirstYear()) + i;
				
				int j = 0;
				for (j = 0; j < c2_events.length && !c2_events[j]; j++)
				{
					; // loop until the index of the first fire year for c2 is found
				}
				int c2_first_fire_year = applyBCYearOffset(c2.getFirstYear()) + j;
				
				return c2_first_fire_year - c1_first_fire_year;
			}
		};
		
		Collections.sort(seriesSVGList, comparator);
		lastTypeSortedBy = SeriesSortType.FIRST_FIRE_YEAR;
		rebuildChronologyPlot();
		
		log.debug("Finished sorting chart series by first fire year");
	}
	
	/**
	 * Sort the series by start year.
	 */
	public void sortBySampleStartYear() {
		
		Comparator<FHSeriesSVG> comparator = new Comparator<FHSeriesSVG>() {
			
			@Override
			public int compare(FHSeriesSVG c1, FHSeriesSVG c2) {
				
				return applyBCYearOffset(c2.getFirstYear()) - applyBCYearOffset(c1.getFirstYear());
			}
		};
		
		Collections.sort(seriesSVGList, comparator);
		lastTypeSortedBy = SeriesSortType.SAMPLE_START_YEAR;
		rebuildChronologyPlot();
		
		log.debug("Finished sorting chart series by series start year");
	}
	
	/**
	 * Sort the series by end year.
	 */
	public void sortBySampleEndYear() {
		
		Comparator<FHSeriesSVG> comparator = new Comparator<FHSeriesSVG>() {
			
			@Override
			public int compare(FHSeriesSVG c1, FHSeriesSVG c2) {
				
				return applyBCYearOffset(c2.getLastYear()) - applyBCYearOffset(c1.getLastYear());
			}
		};
		
		Collections.sort(seriesSVGList, comparator);
		lastTypeSortedBy = SeriesSortType.SAMPLE_END_YEAR;
		rebuildChronologyPlot();
		
		log.debug("Finished sorting chart series by series end year");
	}
	
	/**
	 * Sort the series by end year.
	 */
	public void sortByPositionInFile() {
		
		Comparator<FHSeriesSVG> comparator = new Comparator<FHSeriesSVG>() {
			
			@Override
			public int compare(FHSeriesSVG c1, FHSeriesSVG c2) {
				
				Integer c1pos = c1.getSequenceInFile();
				Integer c2pos = c2.getSequenceInFile();
				
				return c1pos.compareTo(c2pos);
			}
		};
		
		Collections.sort(seriesSVGList, comparator);
		lastTypeSortedBy = SeriesSortType.AS_IN_FILE;
		rebuildChronologyPlot();
		
		log.debug("Finished sorting chart series by position in file");
	}
	
	// public boolean setCommonTickAttrib(int weight, Color color, LineStyle style) {
	//
	// tickLineWeight = weight;
	// tickLineStyle = style;
	// setTickColor(color);
	// positionChartGroupersAndDrawTimeAxis();
	// return false;
	// }
}
