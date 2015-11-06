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
	
	public static final String RANGE_CALC_ALL_YEARS = "Define whether the calculations should be performed across all years in the input file(s) or limited to a specified time frame";
}
