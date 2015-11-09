/**************************************************************************************************
 * Fire History Analysis and Exploration System (FHAES), Copyright (C) 2015
 * 
 * Contributors: Peter Brewer
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
package org.fhaes.help;

/**
 * LocalHelp Class. Contains static strings for help tip displayed within FHAES. This keeps the help text in one place making it easier to
 * keep track and update.
 * 
 * @author Peter Brewer
 */
public class LocalHelp {
	
	/**
	 * 
	 * METADATA FIELD INFORMATION
	 * 
	 */
	
	public static final String FIELD_TOWNSHIP_AND_RANGE = "Township, range, section and quarter section fields have been used historically "
			+ "to record the locations using the Public Land Survey System.  If used, the township and range fields are required at a "
			+ "minimum, with the section and quarter section values optionally used depending on the precision and size of area being "
			+ "defined.  These fields are not used by FHAES in spatial analysis, the latitude and longitude fields should be used instead.";
	public static final String FIELD_SITE_NAME = "Human readable name for the site.  To be compatible with FHX2 this field should be no more than "
			+ "70 characters long";
	public static final String FIELD_SITE_CODE = "Short code used to identify the site.  By convention and to remain compatible with FHX2, this should be 3 characters long.";
	public static final String FIELD_COLLECTION_DATE = "Date or dates when the samples included in this file were collected. Should be 20 characters or less to remain compatible with FHX2.";
	public static final String FIELD_COLLECTORS_NAME = "The name or names of those in the field that collected the samples represented in this file. Should be 70 characters or less to remain compatible with FHX2.";
	public static final String FIELD_DATERS_NAME = "Name or names of those who provided the dendrochronological placement for these samples. Should be 70 characters or less to remain compatible with FHX2.";
	public static final String FIELD_LATIN_NAMES = "Latin names of the trees from which these samples were taken.  If more than one species is included in this file, please provide further information in the comments field. Should be 70 characters or less to remain compatible with FHX2.";
	public static final String FIELD_COMMON_NAMES = "Common/vernacular names for the species in this file. Should be 70 characters or less to remain compatible with FHX2.";
	public static final String FIELD_HABITAT_TYPE = "Habitat type at this site.  Please use standardized naming systems applicable to the region wherever possible. Should be 70 characters or less to remain compatible with FHX2.";
	public static final String FIELD_COUNTRY = "Country where this site is located. Should be 20 characters or less to remain compatible with FHX2.";
	public static final String FIELD_STATE = "State where this sample is located. Should be 15 characters or less to remain compatible with FHX2.";
	public static final String FIELD_COUNTY = "County where this sample is located. Should be 30 characters or less to remain compatible with FHX2.";
	public static final String FIELD_PARK = "Park (e.g. National Park) where this site is located. Should be 40 characters or less to remain compatible with FHX2.";
	public static final String FIELD_FOREST = "Name of forest where this site is located. Should be 40 characters or less to remain compatible with FHX2.";
	public static final String FIELD_RANGER_DISTRICT = "The Ranger district that covers this site. Should be 25 characters or less to remain compatible with FHX2.";
	private static final String UTM_RUBBISH = "The FHX specification does not provide a facility for recording the UTM zone so UTM data is ignored by FHAES.  "
			+ "The latitude and longitude fields should be used instead.";
	public static final String FIELD_UTM_EASTING = "<b>Warning Deprecated</b> UTM easting value for the site. " + UTM_RUBBISH;
	public static final String FIELD_UTM_NORTHING = "<b>Warning Deprecated</b> UTM northing value for the site.  " + UTM_RUBBISH;
	private static final String USE_DECIMAL_DEGREES = "Although this is a free text field and any formatting is valid, we strongly recommend using decimal degrees.  "
			+ "Existing free text values can be parsed and converted using the convert button.";
	public static final String FIELD_LATITUDE = "Latitude value for the location of this site.  " + USE_DECIMAL_DEGREES;
	public static final String FIELD_LONGITUDE = "Longitude value for the location of this site.  " + USE_DECIMAL_DEGREES;
	public static final String FIELD_TOPO_MAP = "Reference to the topographic map covering the site. Should be 30 characters or less to remain compatible with FHX2.";
	public static final String FIELD_HIGHEST_ELEV = "Highest elevation of the site.  Please also specify units.. Should be 4 characters or less to remain compatible with FHX2.";
	public static final String FIELD_LOWEST_ELEV = "Lowest elevation of the site.  Please also specify units. . Should be 4 characters or less to remain compatible with FHX2.";
	public static final String FIELD_SLOPE = "Angle of the slope of the site either as a description, or as a value in degrees. Should be 10 characters or less to remain compatible with FHX2.";
	public static final String FIELD_SLOPE_ASPECT = "General aspect of the site, typically recorded as compass direction. Should be 10 characters or less to remain compatible with FHX2.";
	public static final String FIELD_AREA_SAMPLED = "Total area sampled.  Please also specify units.  Should be 10 characters or less to remain compatible with FHX2.";
	public static final String FIELD_SUBSTRATE = "Description of the substrate at this site. Should be 70 characters or less to remain compatible with FHX2.";
	public static final String FIELD_SAMPLE_COUNT = "This field is automatically set by FHAES depending on the number of samples entered in the data screen.";
	
	/**
	 * 
	 * ANALYSIS PARAMETERS INFORMATION
	 * 
	 */
	
	public static final String COMPOSITE_FILTER_THRESHOLD = "Specify how the composite of your samples should be filtered during analyses.  "
			+ "If you choose 'number of fires' with a value of 1 then all fire events will be used.  By specifying "
			+ "a larger value you will filter out events that are not consistently recorded by your samples.  You "
			+ "also have the option of specifying the filter as a percentage occurance.";
	public static final String COMPOSITE_FILTER_SAMPLE_DEPTH_FILTER_TYPE = "Specify the sample depth filter to use when generaing your composite.  The sample "
			+ "depth filter filters out years in which there are not enough samples to run analyses.  If the filter type is set to 'minimum number of recording years' "
			+ "then the number of trees which are in recording status is used, whereas if `minimum number of samples' is chosen then the number of all trees regardless "
			+ "of recording status will be used.";
	public static final String COMMON_YEARS = "Select the number of common years or overlap that must be present between two datasets before "
			+ "comparison matrices can be calculated.";
	public static final String LABEL_SAMPLES_BY = "Select how you would like to label the input files in your results tables and charts.  "
			+ "Note that FHAES will default to file name if you pick site name or code and a file is missing the relevant header information.";
	public static final String EVENT_TYPE = "Select whether to perform analyses on:<br/>"
			+ "  - only fire events (upper case letters in the input files);<br/>" + "  - only other indicators (lower case letters);<br/>"
			+ "  - both fire events <i>and</i> other indicators.<br>"
			+ "Note that choosing one of the first two options may remove one or more of "
			+ "your input files from the analyses if they do not contain enough of the " + "specified event types.";
	public static final String INCLUDE_EXTRA_INTERVAL = "Indicate whether you'd like to treat the interval from the final event marker until the end of the "
			+ "series as an interval or not during interval analyses.";
	public static final String ANALYSIS_TYPE = "Select whether you want to perform analyses based separately upon each "
			+ "sample in your file or by combining samples within a file into a composite.  Note if you select "
			+ "'composite' then you may like to set composite filter options as well.";
	public static final String ALPHA_LEVEL = "Alpha level or error is the maximum probability that a given return interval"
			+ " will be significantly short or long.  The default alpha value for this two-tailed test is 0.125.";
	public static final String SHOW_LEGEND = "Hide/show the chart legend";
	public static final String SHOW_CHART_TITLE = "Hide/show the title text on the chart";
	public static final String USE_DEFAULT_CHART_TITLE = "Use the default chart title based upon either the name of the site or site code taken from the file metadata.";
	public static final String CHART_FONT_FAMILY = "Choose the font family to use for all text on the chart";
	public static final String CHART_TITLE_FONT_SIZE = "Choose the font size to use for the chart title";
	public static final String CHART_TITLE = "Override the title to use on the chart";
	public static final String XAXIS_AUTO_RANGE = "Automatically set the range of the x-axis (timeline)";
	public static final String XAXIS_FONT_SIZE = "Font size to use for the timeline labels";
	public static final String XAXIS_RANGE = "Override the range of years displayed on the chart";
	public static final String SHOW_VERTICAL_GUIDES = "Hide/show vertical guidelines.  These lines are displayed at regular intervals through all parts of the chart making in easier for users to orientate themselves between each subplot.  The spacing of the vertical guides matches the major ticks specified below.";
	public static final String VERTICAL_GUIDE_STYLE = "Set the style, weight and color of the vertical guidelines";
	public static final String SHOW_MAJOR_TICKS = "Hide/show the major ticks on the timeline axis";
	public static final String SHOW_MINOR_TICKS = "Hide/show the minor ticks on the timeline axis";
	public static final String MINOR_TICK_SPACING = "Set the year interval at which the minor ticks should be displayed";
	public static final String MAJOR_TICK_SPACING = "Set the year interval at which the major ticks should be displayed";
	public static final String SHOW_HIGHLIGHTED_YEARS = "Display vertical lines in the specified years to highlight particular features.  Highlighted year lines are similar to vertical guides but are place in one or more specified years rather than at regular intervals.";
	public static final String HIGHLIGHTED_YEARS_STYLE = "Set the style, weight and color of the highlighted year lines";
	public static final String RANGE_CALC_ALL_YEARS = "Define whether the calculations should be performed across all years in the input file(s) or limited to a specified time frame";
	public static final String SHOW_INDEX_PLOT = "Hide/show the index plot";
	public static final String INDEX_PLOT_HEIGHT = "Set the height of the index plot in pixels";
	public static final String SAMPLE_OR_RECORDER_DEPTH = "Choose whether the index plot should show the sample depth or the recorder depth.  Sample depth shows all trees available in a year regardless of their recording status.  Recorder depth shows only those trees that are in recording status.";
	public static final String SHOW_PERCENT_SCARRED = "Hide/show the percent scarred line.  This is calculated as the number of events in a year as a percentage of the number of trees in recording status.";
	public static final String SHOW_DEPTH_THRESHOLD = "Hide/show a user defined depth threshold line.  This is typical used to show the number of samples that need to be present in a analysis for the year to be considered significant.  The value is typicaly calculated using the sample size analysis tool.";
	public static final String DEPTH_THRESHOLD_VALUE = "Set the user defined depth threshold value";
	public static final String Y1_AXIS_LABEL = "Set the label for the first Y axis on the index plot.  This is typically either 'sample depth' or 'recording depth'";
	public static final String Y1_FONT_SIZE = "Set the font size for the first Y axis label";
	public static final String Y2_AXIS_LABEL = "Set the label for the second Y axis on the index plot.  This is typically '% Scarred'";
	public static final String Y2_FONT_SIZE = "Set the font size for the second Y axis label";
	public static final String SHOW_SERIES_LABELS = "Hide/show the series labels on the right of the main chronology plot.";
	public static final String SERIES_LABELS_FONT_SIZE = "Set the font size of the chronology plot series labels";
	public static final String SERIES_SPACING = "Set the spacing in pixels between the chronology plot series";
	public static final String SHOW_SYMBOLS = "Enable/disable the various symbols used in the chronology series plots.";
	public static final String SHOW_CATEGORY_GROUPS = "Hide/show category groups in the chronology plot";
	public static final String SHOW_CATEGORY_LABELS = "Hide/show category labels when category groups are displayed in the chronology plot";
	public static final String CATEGORY_FONT_SIZE = "Set the font size of category labels";
	public static final String AUTO_COLOR_CATEGORY_LINES = "Automatically colorize the series lines by category";
	public static final String AUTO_COLOR_CATEGORY_LABELS = "Automatically colorize the series labels by category";
	public static final String CATEGORY_JUSTIFICATION = "Set the justification fo the category labels";
	public static final String SHOW_CHRONOLOGY_PLOT = "Hide/show the chronology plot";
	public static final String SHOW_COMPOSITE_TITLE = "Override the composite plot chart label";
	public static final String COMPOSITE_BASED_ON = "Set whether the composite is calculated from: fire events; injury events; or both.";
	public static final String COMPOSITE_HEIGHT = "Set the height of the composite plot (including labels) in pixels";
	public static final String COMPOSITE_FILTER1 = "Set the filter threshold above which the year is included in the composite.  Filter choices include: number of trees recording events; percentage of recording trees that are scarred; and percentage of all trees that are scarred. ";
	public static final String COMPOSITE_FILTER2 = "Set the filter threshold above which the year is included in the composite.  Filter choices include: minimum number of samples; and minimum number of recording samples.";
	public static final String COMPOSITE_PLOT_FONT_SIZE = "Set the font size for the composite plot labels";
	public static final String SHOW_FILTER_IN_LEGEND = "Hide/show the composite filter parameters in the legend.  Legend must be enabled for this selection to take affect.";
	public static final String YEAR_LABEL_ORIENTATION = "Orientation of the composite year labels.  Choices are: horizontal, vertical or angled";
	public static final String SHOW_YEAR_LABELS = "Hide/show the composite year labels";
	public static final String YEAR_LABEL_STYLE = "Choose whether the composite year labels should be four digit years (e.g. 1996) or two digit years (e.g. '96)";
	public static final String YEAR_LABEL_PADDING = "Set the number of pixels of padding/space to surround composite year labels";
	public static final String YEAR_LABEL_FONT_SIZE = "Set the font size of the composite year labels";
	public static final String SHOW_COMPOSITE_PLOT = "Hide/show the composite plot";
	
}
