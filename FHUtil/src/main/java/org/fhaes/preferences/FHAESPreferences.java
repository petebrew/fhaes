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
package org.fhaes.preferences;

import java.awt.Color;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.prefs.Preferences;

import org.fhaes.enums.AnalysisLabelType;
import org.fhaes.enums.AnalysisType;
import org.fhaes.enums.EventTypeToProcess;
import org.fhaes.enums.FireFilterType;
import org.fhaes.enums.JustificationType;
import org.fhaes.enums.LabelOrientation;
import org.fhaes.enums.LineStyle;
import org.fhaes.enums.NoDataLabel;
import org.fhaes.enums.OperatorEnum;
import org.fhaes.enums.ResamplingType;
import org.fhaes.enums.SampleDepthFilterType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * FHAESPreferences Class.
 * 
 * <p>
 * The FHAESPreference system enables us to save and retrieve values across sessions. The values are stored in locations specific to the
 * host operating system. For instance in Windows they are stored in the registry and in Linux they are stored in a system file in the users
 * home.
 * </p>
 * 
 * <p>
 * Preferences should be accessed and set through a static variable e.g. App.prefs. There are a variety of setter functions depending on the
 * data type trying to be stored e.g. setIntPref() setDoublePref() but they all follow the same style taking a PrefKey and a value. The
 * PrefKey is an enumeration listed in this class containing the key names that are stored in the users preferences file/registry.
 * </p>
 * 
 * <p>
 * Similarly there are accessor methods depending on the data type being extracted e.g. getIntPref() getDoublePref(). These all require a
 * PrefKey and a default value to be returned when no value is found.
 * </p>
 * 
 * 
 * @author Peter Brewer
 */
public class FHAESPreferences {
	
	// Declare preferences and logger
	private static Preferences prefs = Preferences.userNodeForPackage(FHAESPreferences.class);
	private static final Logger log = LoggerFactory.getLogger(FHAESPreferences.class);
	
	// Declare local constants
	private static final String ARRAY_DELIMITER = ";";
	
	// Declare local variables
	private Boolean silentMode = false;
	private final ArrayList<PrefsListener> listeners = new ArrayList<PrefsListener>();
	
	/**
	 * This enum contains the keys for the preference values stored and used by FHAES.
	 * 
	 * @author Peter Brewer
	 */
	public enum PrefKey {
		
		/**
		 * 
		 * GENERAL PREFERENCES
		 * 
		 */
		
		LOCALE_LANGUAGE_CODE("locale.language"),
		
		LOCALE_COUNTRY_CODE("locale.country"),
		
		PREF_LAST_READ_FOLDER("LastFolderVisited"),
		
		PREF_LAST_READ_TIME_SERIES_FOLDER("TimeSeriesFolderVisited"),
		
		PREF_LAST_READ_EVENT_LIST_FOLDER("EventListFolderVisited"),
		
		PREF_LAST_EXPORT_FOLDER("LastExportFolder"),
		
		PREF_LAST_EXPORT_FORMAT("LastExportFormat"),
		
		RECENT_DOCUMENT_COUNT("RecentDocumentCount"),
		
		RECENT_DOCUMENT_LIST("RecentDocumentList"),
		
		DONT_CHECK_FOR_UPDATES("noUpdates"),
		
		UPDATES_LATE_CHECKED("updatesLastChecked"),
		
		SCREEN_BOUNDS_X("screenBoundsX"),
		
		SCREEN_BOUNDS_Y("screenBoundsY"),
		
		SCREEN_WIDTH("screenWidth"),
		
		SCREEN_HEIGHT("screenHeight"),
		
		SCREEN_MAXIMIZED("screenMaximized"),
		
		DONT_REQUEST_PARAM_CONFIRMATION("dontRequestParamConfirmation"),
		
		SHOW_QUICK_LAUNCH_AT_STARTUP("showQuickLaunchAtStartup"),
		
		AUTO_LOAD_CATEGORIES("autoloadCategories"),
		
		MATRIX_NO_DATA_LABEL("matrixNoDataLabel"),
		
		EVENT_TYPE_TO_PROCESS("matrixEventType"),
		
		ANALYSIS_LABEL_TYPE("AnalysisLabelType"),
		
		LARGE_DATASET_WARNING_DISABLED("largeDatasetWarningDisabled"),
		
		SHAPEFILE_OUTPUT_STYLE("shapefileOutputStyle"),
		
		AUTO_DETECT_CHAR_ENC("autoDetectCharacterEncoding"),
		
		FORCE_CHAR_ENC_TO("forceCharacterEncodingTo"),
		
		/**
		 * 
		 * SEASONALITY PREFERENCES
		 * 
		 */
		
		SEASONALITY_FIRST_GROUP_DORMANT("seasonalityFirstGroupDormant"),
		
		SEASONALITY_FIRST_GROUP_EARLY_EARLY("seasonalityFirstGroupEarlyEarly"),
		
		SEASONALITY_FIRST_GROUP_MIDDLE_EARLY("seasonalityFirstGroupMiddleEarly"),
		
		SEASONALITY_FIRST_GROUP_LATE_EARLY("seasonalityFirstGroupLateEarly"),
		
		SEASONALITY_FIRST_GROUP_LATE("seasonalityFirstGroupLate"),
		
		SEASONALITY_SECOND_GROUP_DORMANT("seasonalitySecondGroupDormant"),
		
		SEASONALITY_SECOND_GROUP_EARLY_EARLY("seasonalitySecondGroupEarlyEarly"),
		
		SEASONALITY_SECOND_GROUP_MIDDLE_EARLY("seasonalitySecondGroupMiddleEarly"),
		
		SEASONALITY_SECOND_GROUP_LATE_EARLY("seasonalitySecondGroupLateEarly"),
		
		SEASONALITY_SECOND_GROUP_LATE("seasonalitySecondGroupLate"),
		
		/**
		 * 
		 * JSEA PREFERENCES
		 * 
		 */
		
		JSEA_CONTINUOUS_TIME_SERIES_FILE("continuousTimeSeriesFile"),
		
		JSEA_EVENT_LIST_FILE("eventListFile"),
		
		JSEA_CHART_TITLE("jseaChartTitle"),
		
		JSEA_YAXIS_LABEL("jseaYAxisLabel"),
		
		JSEA_LAGS_PRIOR_TO_EVENT("jseaLagsPriorToEvent"),
		
		JSEA_LAGS_AFTER_EVENT("jseaLagsAfterEvent"),
		
		JSEA_INCLUDE_INCOMPLETE_WINDOW("jseaIncludeIncompleteWindow"),
		
		JSEA_SIMULATION_COUNT("jseaSimulationCount"),
		
		JSEA_SEED_NUMBER("jseaSeedNumber"),
		
		JSEA_P_VALUE("jseaPValue"),
		
		JSEA_FIRST_YEAR("jseaFirstYear"),
		
		JSEA_LAST_YEAR("jseaLastYear"),
		
		JSEA_Z_SCORE("jseaZScore"),
		
		/**
		 * 
		 * SSIZ PREFERENCES
		 * 
		 */
		
		SSIZ_SIMULATION_COUNT("ssizSimulationCount"),
		
		SSIZ_SEED_NUMBER("ssizSeedNumber"),
		
		SSIZ_ALL_YEARS("ssizProcessAllYears"),
		
		SSIZ_RESAMPLING_TYPE("ssizResamplingType"),
		
		SSIZ_CHK_COMMON_YEARS("ssizChkCommonYears"),
		
		SSIZ_CHK_EXCLUDE_SERIES_WITH_NO_EVENTS("ssizExcludeSeriesWithNoEvents"),
		
		/**
		 * 
		 * COMPOSITE PREFERENCES
		 * 
		 */
		
		COMPOSITE_FILTER_TYPE("compositeFilterType"),
		
		COMPOSITE_EVENT_TYPE("compositeEventType"),
		
		COMPOSITE_FILTER_TYPE_WITH_ALL_TREES("compositeFilterTypeWithAllTrees"),
		
		COMPOSITE_FILTER_OPERATOR("compositeFilterOperator"),
		
		COMPOSITE_SAMPLE_DEPTH_TYPE("compositeSampleDepthType"),
		
		COMPOSITE_FILTER_VALUE("compositeFilterValue"),
		
		COMPOSITE_DISTANCE_THRESHOLD_KM("compositeDistanceThreshold"),
		
		COMPOSITE_MIN_SAMPLES("compositeMinimumSamples"),
		
		/**
		 * 
		 * INTERVALS PREFERENCES
		 * 
		 */
		
		INTERVALS_ANALYSIS_TYPE("intervalsAnalysisType"),
		
		INTERVALS_INCLUDE_OTHER_INJURIES("intervalsIncludeOtherInjuries"),
		
		INTERVALS_ALPHA_LEVEL("intervalsAlphaLevel"),
		
		/**
		 * 
		 * RANGE PREFERENCES
		 * 
		 */
		
		RANGE_CALC_OVER_ALL_YEARS("allYears"),
		
		RANGE_FIRST_YEAR("firstYear"),
		
		RANGE_LAST_YEAR("lastYear"),
		
		RANGE_OVERLAP_REQUIRED("overlapRequired"),
		
		/**
		 * 
		 * FHRECORDER PREFERENCES
		 * 
		 */
		
		ENFORCE_FHX2_RESTRICTIONS("enforceFHX2Restrictions"),
		
		COLORBAR_GROUPNAME_1("colorbarGroupName1"),
		
		COLORBAR_GROUPNAME_2("colorbarGroupName2"),
		
		COLORBAR_GROUPNAME_3("colorbarGroupName3"),
		
		COLORBAR_GROUPNAME_4("colorbarGroupName4"),
		
		COLORBAR_GROUPNAME_5("colorbarGroupName5"),
		
		COLORBAR_GROUPNAME_6("colorbarGroupName6"),
		
		COLORBAR_COLOR_1("colorbarColor1"),
		
		COLORBAR_COLOR_2("colorbarColor2"),
		
		COLORBAR_COLOR_3("colorbarColor3"),
		
		COLORBAR_COLOR_4("colorbarColor4"),
		
		COLORBAR_COLOR_5("colorbarColor5"),
		
		COLORBAR_COLOR_6("colorbarColor6"),
		
		COLORBAR_ITEMS_GROUP0("colorbarItemsGroup0"),
		
		COLORBAR_ITEMS_GROUP1("colorbarItemsGroup1"),
		
		COLORBAR_ITEMS_GROUP2("colorbarItemsGroup2"),
		
		COLORBAR_ITEMS_GROUP3("colorbarItemsGroup3"),
		
		COLORBAR_ITEMS_GROUP4("colorbarItemsGroup4"),
		
		COLORBAR_ITEMS_GROUP5("colorbarItemsGroup5"),
		
		COLORBAR_ITEMS_GROUP6("colorbarItemsGroup6"),
		
		/**
		 * 
		 * FHCHART PREFERENCES
		 * 
		 */
		
		CHART_INDEX_PLOT_HEIGHT("chartIndexPlotHeight"),
		
		CHART_INDEX_PERCENT_SCARRED_LINE_WIDTH("chartIndexPercentScarredLineWidth"),
		
		CHART_SHOW_SAMPLE_DEPTH("chartShowSampleDepth"),
		
		CHART_SHOW_PERCENT_SCARRED("chartShowPercentScarred"),
		
		CHART_SHOW_DEPTH_THRESHOLD("chartShowDepthThreshold"),
		
		CHART_DEPTH_THRESHOLD_VALUE("chartDepthThresholdValue"),
		
		CHART_SAMPLE_OR_RECORDER_DEPTH_COLOR("chartSampleDepthColor"),
		
		CHART_RECORDER_DEPTH_COLOR("chartRecorderDepthColor"),
		
		CHART_PERCENT_SCARRED_COLOR("chartPercentScarredColor"),
		
		CHART_DEPTH_THRESHOLD_COLOR("chartDepthThresholdColor"),
		
		CHART_AXIS_Y1_LABEL("chartAxisY1Label"),
		
		CHART_AXIS_Y1_FONT_SIZE("chartAxisY1FontSize"),
		
		CHART_AXIS_Y2_LABEL("chartAxisY2Label"),
		
		CHART_AXIS_Y2_FONT_SIZE("chartAxisY2FontSize"),
		
		CHART_TITLE_FONT_SIZE("chartTitleFontSize"),
		
		CHART_TITLE_USE_DEFAULT_NAME("chartTitleUseDefaultName"),
		
		CHART_TITLE_OVERRIDE_VALUE("chartTitleOverrideValue"),
		
		CHART_SHOW_CHART_TITLE("chartShowChartTitle"),
		
		CHART_SHOW_LEGEND("chartShowLegend"),
		
		CHART_SHOW_CATEGORY_GROUPS("chartShowCategoryGroups"),
		
		CHART_SHOW_CATEGORY_LABELS("chartShowCategoryLabels"),
		
		CHART_CATEGORY_LABEL_FONT_SIZE("chartCategoryLabelFontSize"),
		
		CHART_CATEGORY_LABEL_JUSTIFICATION("chartCategoryLabelJustification"),
		
		CHART_AUTOMATICALLY_COLORIZE_SERIES("chartAutomaticallyColorizeSeries"),
		
		CHART_AUTOMATICALLY_COLORIZE_LABELS("chartAutomaticallyColorizeLabels"),
		
		CHART_SORT_BY_PREFERENCE("chartSortByPreference"),
		
		CHART_FONT_FAMILY("chartFontFamily"),
		
		CHART_LAST_EXPORT_FOLDER("chartLastExportFolder"),
		
		CHART_AXIS_X_AUTO_RANGE("chartAxisXAutoRange"),
		
		CHART_AXIS_X_MIN("chartAxisXMin"),
		
		CHART_AXIS_X_MAX("chartAxisXMax"),
		
		CHART_TIMELINE_FONT_SIZE("chartTimelineFontSize"),
		
		CHART_VERTICAL_GUIDES("chartVerticalGuides"),
		
		CHART_XAXIS_MAJOR_TICK_SPACING("chartXAxisMajorTickSpacing"),
		
		CHART_XAXIS_MINOR_TICK_SPACING("chartXAxisMinorTickSpacing"),
		
		CHART_VERTICAL_GUIDE_COLOR("chartVerticalGuideColor"),
		
		CHART_VERTICAL_GUIDE_STYLE("chartVerticalGuideStyle"),
		
		CHART_VERTICAL_GUIDE_WEIGHT("chartVerticalGuideWeight"),
		
		CHART_SHOW_CHRONOLOGY_PLOT_LABELS("chartShowChronologyPlotLabels"),
		
		CHART_CHRONOLOGY_PLOT_LABEL_FONT_SIZE("chartChronologyPlotLabelFontSize"),
		
		CHART_CHRONOLOGY_PLOT_SPACING("chartChronologyPlotSpacing"),
		
		CHART_SHOW_PITH_SYMBOL("chartShowPithSymbol"),
		
		CHART_SHOW_BARK_SYMBOL("chartShowBarkSymbol"),
		
		CHART_SHOW_INNER_RING_SYMBOL("chartShowInnerRingSymbol"),
		
		CHART_SHOW_OUTER_RING_SYMBOL("chartShowOuterRingSymbol"),
		
		CHART_SHOW_FIRE_EVENT_SYMBOL("chartShowFireEventSymbol"),
		
		CHART_SHOW_INJURY_SYMBOL("chartShowInjurySymbol"),
		
		CHART_COMPOSITE_HEIGHT("chartCompositeHeight"),
		
		CHART_SHOW_COMPOSITE_YEAR_LABELS("chartShowCompositeYearLabels"),
		
		CHART_SHOW_INDEX_PLOT("chartShowIndexPlot"),
		
		CHART_SHOW_CHRONOLOGY_PLOT("chartShowChronologyPlot"),
		
		CHART_SHOW_COMPOSITE_PLOT("chartShowCompositePlot"),
		
		CHART_COMPOSITE_FILTER_TYPE("chartCompositeFilterType"),
		
		CHART_COMPOSITE_SAMPLE_DEPTH_TYPE("compositeSampleDepthType"),
		
		CHART_COMPOSITE_FILTER_VALUE("chartCompositeFilterValue"),
		
		CHART_COMPOSITE_MIN_NUM_SAMPLES("chartCompositeFilterMinSamples"),
		
		CHART_XAXIS_MAJOR_TICKS("chartXAxisMajorTicks"),
		
		CHART_XAXIS_MINOR_TICKS("chartXAxisMinorTicks"),
		
		CHART_COMPOSITE_YEAR_LABEL_FONT_SIZE("chartCompositeYearLabelFontSize"),
		
		CHART_COMPOSITE_YEAR_LABEL_BUFFER("chartCompositeYearLabelBuffer"),
		
		CHART_COMPOSITE_LABEL_TEXT("chartCompositeLabelText"),
		
		CHART_COMPOSITE_PLOT_LABEL_FONT_SIZE("chartCompositePlotLabelFontSize"),
		
		CHART_COMPOSITE_LABEL_ALIGNMENT("chartCompositeLabelAlignment"),
		
		CHART_COMPOSITE_YEAR_LABELS_TWO_DIGIT("chartCompositeYearLabelsTwoDigit"),
		
		CHART_COMPOSITE_EVENT_TYPE("chartCompositeEventType"),
		
		CHART_HIGHLIGHT_YEAR_STYLE("chartHighlightYearStyle"),
		
		CHART_HIGHLIGHT_YEARS("chartHighlightYears"),
		
		CHART_HIGHLIGHT_YEARS_WEIGHT("chartHighlightYearsWeight"),
		
		CHART_HIGHLIGHT_YEARS_COLOR("chartHighlightYearsColor"),
		
		CHART_HIGHLIGHT_YEARS_ARRAY("chartHighlightYearsArray"),
		
		CHART_FONT_BOLD("chartFontBold"),
		
		CHART_FONT_ITALIC("chartFontItalic"),
		
		CHART_SHOW_FILTER_IN_LEGEND("chartShowFilterInLegend"),
		
		CHART_REMEMBER_CHART_PREFS_AFTER_RESTART("chartRememberPrefsAfterRestart"),
		
		/**
		 * 
		 * FEEDBACK MESSAGE PREFERENCES
		 * 
		 */
		
		SHOW_CATEGORY_FILE_SAVED_MESSAGE("showCategoryFileSavedMessage"),
		
		SHOW_FHRECORDER_FILE_SAVED_MESSAGE("showFHRecorderFileSavedMessage"),
		
		SHOW_NEOFHCHART_BULK_EXPORT_MESSAGE("showNeoFHChartBulkExportMessage"),
		
		SHOW_NEOFHCHART_PDF_EXPORT_MESSAGE("showNeoFHChartPDFExportMessage"),
		
		SHOW_NEOFHCHART_PNG_EXPORT_MESSAGE("showNeoFHChartPNGExportMessage"),
		
		SHOW_NEOFHCHART_SVG_EXPORT_MESSAGE("showNeoFHChartSVGExportMessage");
		
		/**
		 * 
		 * KEY ACCESS METHODS
		 * 
		 */
		
		// Declare local variables
		private final String key;
		
		/**
		 * Initialize the preference key for the PrefKey.
		 * 
		 * @param inKeyValue
		 */
		private PrefKey(String inKeyValue) {
			
			this.key = inKeyValue;
		}
		
		/**
		 * Get the preference key for this PrefKey.
		 * 
		 * @return key
		 */
		public String getValue() {
			
			return key;
		}
	}
	
	/**
	 * Fire a preference change event for a specific PrefKey.
	 * 
	 * @param pref
	 */
	public void firePrefChanged(PrefKey pref) {
		
		if (this.silentMode)
			return;
		
		log.debug("Preference change fired");
		
		PrefsEvent e = new PrefsEvent(FHAESPreferences.class, pref);
		
		for (PrefsListener l : listeners)
		{
			l.prefChanged(e);
		}
	}
	
	/**
	 * Sets the silentMode on and off. see isSilentMode.
	 * 
	 * @param mode
	 */
	public void setSilentMode(Boolean mode) {
		
		this.silentMode = mode;
		
	}
	
	/**
	 * Whether the silentMode is on. When silentMode is on, PrefsListeners are not notified of a change. This is useful if you are intending
	 * to save multiple preference values and don't want them each to fire off separately. You *MUST* ensure that you turn the silent mode
	 * off again once you are done!
	 * 
	 * @return
	 */
	public boolean isSilentMode() {
		
		return this.silentMode;
	}
	
	/**
	 * Add a listener for changes to preferences.
	 * 
	 * @param l
	 */
	public void addPrefsListener(PrefsListener l) {
		
		log.debug("PrefsListener added");
		
		listeners.add(l);
	}
	
	/**
	 * Remove a listener when you no longer what to hear about changes to preferences.
	 * 
	 * @param l
	 */
	public void removePrefsListener(PrefsListener l) {
		
		listeners.remove(l);
	}
	
	/**
	 * Remove a preference value.
	 * 
	 * @param key
	 */
	public void clearPref(PrefKey key) {
		
		prefs.remove(key.getValue());
	}
	
	/**
	 * Remove a preference value.
	 * 
	 * @param key
	 */
	public static void removePref(PrefKey key) {
		
		prefs.remove(key.getValue());
	}
	
	/**
	 * Add a string to a preference array. If string is present then remove it first then add at the top of the list.
	 * 
	 * @param key
	 * @param str
	 * @param maxSize
	 */
	public void addStringtoPrefArray(PrefKey key, String str, Integer maxSize) {
		
		str = str.replace(",", "|||");
		
		LinkedList<String> arr = getStringArrayPref(key);
		
		if (arr == null)
		{
			arr = new LinkedList<String>();
		}
		else
		{
			// Remove it first if it is already there
			removeStringFromPrefArray(key, str);
			arr = getStringArrayPref(key);
			// Add new item to top of list
			arr.push(str);
		}
		
		// Remove items when there are too many in list
		while (arr.size() > maxSize)
		{
			arr.pollLast();
		}
		
		// Save array to preferences
		setStringArrayPref(key, arr);
	}
	
	/**
	 * Remove a string from a preference array.
	 * 
	 * @param key
	 * @param str
	 */
	public void removeStringFromPrefArray(PrefKey key, String str) {
		
		LinkedList<String> arr = getStringArrayPref(key);
		
		if (arr == null)
			return;
		
		int indexFound = -1;
		int i = 0;
		
		for (String item : arr)
		{
			
			if (item.equals(str))
			{
				indexFound = i;
				break;
			}
			i++;
		}
		
		if (indexFound != -1)
		{
			arr.remove(indexFound);
			setStringArrayPref(key, arr);
		}
	}
	
	/**
	 * Method for getting the string value of a preference.
	 * 
	 * @param key
	 * @param defaultValue
	 * @return
	 */
	public String getPref(PrefKey key, String defaultValue) {
		
		return prefs.get(key.getValue(), defaultValue);
	}
	
	/**
	 * Store a string preference for a given key.
	 * 
	 * @param key
	 * @param value
	 */
	public void setPref(PrefKey key, String value) {
		
		prefs.put(key.getValue(), value);
		firePrefChanged(key);
	}
	
	/**
	 * Method for getting the boolean value of a preference.
	 * 
	 * @param key
	 * @param defaultValue
	 * @return
	 */
	public Boolean getBooleanPref(PrefKey key, Boolean defaultValue) {
		
		String value = "";
		if (defaultValue == null)
		{
			value = prefs.get(key.getValue(), null);
		}
		else
		{
			value = prefs.get(key.getValue(), Boolean.toString(defaultValue));
		}
		if (value == null)
			return defaultValue;
		
		return Boolean.parseBoolean(value);
	}
	
	/**
	 * Set the value of a preference to the specified boolean.
	 * 
	 * @param key
	 * @param value
	 */
	public void setBooleanPref(PrefKey key, boolean value) {
		
		setPref(key, Boolean.toString(value));
	}
	
	/**
	 * Get the specified preference as an int.
	 * 
	 * @param key
	 * @param defaultValue
	 * @return
	 */
	public int getIntPref(PrefKey key, int defaultValue) {
		
		// log.debug("Getting integer pref for key: "+key);
		String value = prefs.get(key.getValue(), Integer.toString(defaultValue));
		// log.debug("Value is = "+value);
		if (value == null)
			return defaultValue;
		try
		{
			return Integer.parseInt(value);
		}
		catch (NumberFormatException nfe)
		{
			log.warn("Invalid integer for preference '" + key.getValue() + "': " + value);
			return defaultValue;
		}
	}
	
	/**
	 * Set the value of a preference to the specified int.
	 * 
	 * @param pref
	 * @param value
	 */
	public void setIntPref(PrefKey pref, int value) {
		
		// log.debug("Setting integer pref value for key "+pref+" to value: "+value);
		setPref(pref, Integer.toString(value));
	}
	
	/**
	 * Get the specified preference as an Double.
	 * 
	 * @param key
	 * @param defaultValue
	 * @return
	 */
	public Double getDoublePref(PrefKey key, Double defaultValue) {
		
		String value = prefs.get(key.getValue(), Double.toString(defaultValue));
		if (value == null)
			return defaultValue;
		try
		{
			return Double.parseDouble(value);
		}
		catch (NumberFormatException nfe)
		{
			log.warn("Invalid double for preference '" + key.getValue() + "': " + value);
			return defaultValue;
		}
	}
	
	/**
	 * Set the value of a preference to the specified Double.
	 * 
	 * @param pref
	 * @param value
	 */
	public void setDoublePref(PrefKey pref, Double value) {
		
		setPref(pref, Double.toString(value));
	}
	
	/**
	 * TODO
	 * 
	 * @param key
	 * @param defaultValue
	 * @return
	 */
	public ArrayList<String> getArrayListPref(PrefKey key, ArrayList<String> defaultValue) {
		
		String value = prefs.get(key.getValue(), null);
		if (value == null)
		{
			return defaultValue;
		}
		
		String[] values = value.split(ARRAY_DELIMITER);
		
		ArrayList<String> arrlist = new ArrayList<String>();
		
		for (String v : values)
		{
			arrlist.add(v);
		}
		
		if (values.length > 0)
		{
			return arrlist;
		}
		
		return defaultValue;
	}
	
	/**
	 * Set the value of a preference.
	 * 
	 * @param key
	 * @param values
	 */
	public void setArrayListPref(PrefKey key, ArrayList<String> values) {
		
		String pref = key.getValue();
		String arrAsStr = "";
		
		// support removing via set(null)
		if (values == null)
		{
			prefs.remove(pref);
		}
		else
		{
			
			for (String value : values)
			{
				arrAsStr += value + ARRAY_DELIMITER;
			}
			setPref(key, arrAsStr);
		}
	}
	
	/**
	 * TODO
	 * 
	 * @param key
	 * @param defaultValue
	 * @return
	 */
	public ArrayList<Integer> getIntegerArrayPref(PrefKey key, ArrayList<Integer> defaultValue) {
		
		String value = prefs.get(key.getValue(), null);
		if (value == null)
		{
			return defaultValue;
		}
		
		String[] values = value.split(ARRAY_DELIMITER);
		
		ArrayList<Integer> arrlist = new ArrayList<Integer>();
		
		for (String v : values)
		{
			
			if (v == null || v.length() == 0)
				continue;
			
			try
			{
				
				arrlist.add(Integer.parseInt(v));
			}
			catch (NumberFormatException e)
			{
				log.error("Error casting pref value of '" + v + "' for key " + key + " to integer");
			}
		}
		
		if (values.length > 0)
		{
			return arrlist;
		}
		
		return defaultValue;
	}
	
	/**
	 * TODO
	 * 
	 * @param key
	 * @param values
	 */
	public void setIntegerArrayPref(PrefKey key, ArrayList<Integer> values) {
		
		String pref = key.getValue();
		String arrAsStr = "";
		
		// support removing via set(null)
		if (values == null)
		{
			prefs.remove(pref);
		}
		else
		{
			
			for (Integer value : values)
			{
				arrAsStr += value + ARRAY_DELIMITER;
			}
			setPref(key, arrAsStr);
		}
	}
	
	/**
	 * Get a preference as a ArrayList.
	 * 
	 * @param key
	 * @return
	 */
	public LinkedList<String> getStringArrayPref(PrefKey key) {
		
		String raw = prefs.get(key.getValue(), null);
		// log.debug("Raw string array as string is : "+raw);
		
		if (raw == null)
			return null;
		if (!raw.startsWith("[") || !raw.endsWith("]"))
		{
			return null;
		}
		
		raw = raw.substring(1, raw.length() - 1);
		
		LinkedList<String> arr = new LinkedList<String>();
		
		String[] split = raw.split(", ");
		
		// log.debug("Pref array contains "+split.length+" items");
		
		if (split.length < 1)
			return null;
		
		for (String s : split)
		{
			// Trim off brackets and save
			arr.add(s.replace("|||", ","));
		}
		
		return arr;
	}
	
	/**
	 * Store a string ArrayList preference for a given key.
	 * 
	 * @param key
	 * @param arr
	 */
	public void setStringArrayPref(PrefKey key, List<String> arr) {
		
		if (arr == null)
		{
			prefs.put(key.getValue(), "");
		}
		else
		{
			String s = arr.toString();
			prefs.put(key.getValue(), s);
		}
		
		firePrefChanged(key);
	}
	
	/**
	 * Get the value of an AnalysisType preference.
	 * 
	 * @param key
	 * @param defaultValue
	 * @return
	 */
	public AnalysisType getAnalysisTypePref(PrefKey key, AnalysisType defaultValue) {
		
		String value = "";
		if (defaultValue == null)
		{
			value = prefs.get(key.getValue(), null);
		}
		else
		{
			value = prefs.get(key.getValue(), defaultValue.name());
		}
		
		if (value == null)
			return defaultValue;
		if (AnalysisType.fromName(value) == null)
			return defaultValue;
		return AnalysisType.fromName(value);
	}
	
	/**
	 * Set the value of a preference to the specified AnalysisType.
	 * 
	 * @param key
	 * @param value
	 */
	public void setAnalysisTypePref(PrefKey key, AnalysisType value) {
		
		setPref(key, value.toString());
	}
	
	/**
	 * Get the value of a AnalysisLabelType preference.
	 * 
	 * @param key
	 * @param defaultValue
	 * @return
	 */
	public AnalysisLabelType getAnalysisLabelTypePref(PrefKey key, AnalysisLabelType defaultValue) {
		
		String value = "";
		if (defaultValue == null)
		{
			value = prefs.get(key.getValue(), null);
		}
		else
		{
			value = prefs.get(key.getValue(), defaultValue.name());
		}
		if (value == null)
			return defaultValue;
		
		return AnalysisLabelType.fromName(value);
	}
	
	/**
	 * Set the value of a preference to the specified AnalysisLabelType.
	 * 
	 * @param key
	 * @param value
	 */
	public void setAnalysisLabelTypePref(PrefKey key, AnalysisLabelType value) {
		
		if (value == null)
		{
			setPref(key, null);
		}
		else
		{
			setPref(key, value.toString());
		}
	}
	
	/**
	 * Get a color for the specified preference key.
	 * 
	 * @param key
	 * @param defaultValue
	 * @return
	 */
	public Color getColorPref(PrefKey key, Color defaultValue) {
		
		String value = prefs.get(key.getValue(), null);
		if (value == null)
			return defaultValue;
		try
		{
			return Color.decode(value);
		}
		catch (NumberFormatException nfe)
		{
			log.warn("Invalid color for preference '" + key.getValue() + "': " + value);
			return defaultValue;
		}
	}
	
	/**
	 * Set the value of a preference to the specified color.
	 * 
	 * @param pref
	 * @param value
	 */
	public void setColorPref(PrefKey pref, Color value) {
		
		String encoded = "#" + Integer.toHexString(value.getRGB() & 0x00ffffff);
		setPref(pref, encoded);
	}
	
	public void setCharsetPref(PrefKey pref, Charset charset) {
		
		setPref(pref, charset.toString());
	}
	
	/**
	 * Get the value of a EventTypeToProcess preference.
	 * 
	 * @param key
	 * @param defaultValue
	 * @return
	 */
	public EventTypeToProcess getEventTypePref(PrefKey key, EventTypeToProcess defaultValue) {
		
		String value = "";
		if (defaultValue == null)
		{
			value = prefs.get(key.getValue(), null);
		}
		else
		{
			value = prefs.get(key.getValue(), defaultValue.name());
		}
		if (value == null)
			return defaultValue;
		if (EventTypeToProcess.fromName(value) == null)
			return defaultValue;
		return EventTypeToProcess.fromName(value);
	}
	
	/**
	 * Set the value of a preference to the specified EventTypeToProcess.
	 * 
	 * @param key
	 * @param value
	 */
	public void setEventTypePref(PrefKey key, EventTypeToProcess value) {
		
		setPref(key, value.toString());
	}
	
	/**
	 * Get the value of an FireFilterType preference.
	 * 
	 * @param key
	 * @param defaultValue
	 * @return
	 */
	public FireFilterType getFireFilterTypePref(PrefKey key, FireFilterType defaultValue) {
		
		String value = "";
		if (defaultValue == null)
		{
			value = prefs.get(key.getValue(), null);
		}
		else
		{
			value = prefs.get(key.getValue(), defaultValue.name());
		}
		if (value == null)
			return defaultValue;
		if (FireFilterType.fromName(value) == null)
			return defaultValue;
		return FireFilterType.fromName(value);
	}
	
	/**
	 * Set the value of a preference to the specified FireFilterType.
	 * 
	 * @param key
	 * @param value
	 */
	public void setFireFilterTypePref(PrefKey key, FireFilterType value) {
		
		setPref(key, value.toString());
	}
	
	/**
	 * Get the value of an OperatorEnum preference.
	 * 
	 * @param key
	 * @param defaultValue
	 * @return
	 */
	public OperatorEnum getOperatorPref(PrefKey key, OperatorEnum defaultValue) {
		
		String value = "";
		if (defaultValue == null)
		{
			value = prefs.get(key.getValue(), null);
		}
		else
		{
			value = prefs.get(key.getValue(), defaultValue.name());
		}
		if (value == null)
			return defaultValue;
		if (FireFilterType.fromName(value) == null)
			return defaultValue;
		return OperatorEnum.fromName(value);
	}
	
	/**
	 * Set the value of a preference to the specified OperatorEnum.
	 * 
	 * @param key
	 * @param value
	 */
	public void setOperatorPref(PrefKey key, OperatorEnum value) {
		
		setPref(key, value.toString());
	}
	
	public Charset getCharsetPref(PrefKey key, Charset defaultValue) {
		
		String value = "";
		if (defaultValue == null)
		{
			value = prefs.get(key.getValue(), null);
		}
		else
		{
			value = prefs.get(key.getValue(), defaultValue.name());
		}
		if (value == null)
			return defaultValue;
		
		try
		{
			return Charset.forName(value);
		}
		catch (Exception e)
		{
			return defaultValue;
		}
		
	}
	
	/**
	 * Get the value of an SampleDepthFilterType preference.
	 * 
	 * @param key
	 * @param defaultValue
	 * @return
	 */
	public SampleDepthFilterType getSampleDepthFilterTypePref(PrefKey key, SampleDepthFilterType defaultValue) {
		
		String value = "";
		if (defaultValue == null)
		{
			value = prefs.get(key.getValue(), null);
		}
		else
		{
			value = prefs.get(key.getValue(), defaultValue.name());
		}
		if (value == null)
			return defaultValue;
		if (SampleDepthFilterType.fromName(value) == null)
			return defaultValue;
		return SampleDepthFilterType.fromName(value);
	}
	
	/**
	 * Set the value of a preference to the specified SampleDepthFilterType.
	 * 
	 * @param key
	 * @param value
	 */
	public void setSampleDepthFilterTypePref(PrefKey key, SampleDepthFilterType value) {
		
		setPref(key, value.toString());
	}
	
	/**
	 * Get the value of a JustificationType preference.
	 * 
	 * @param key
	 * @param defaultValue
	 * @return
	 */
	public JustificationType getJustificationTypePref(PrefKey key, JustificationType defaultValue) {
		
		String value = "";
		if (defaultValue == null)
		{
			value = prefs.get(key.getValue(), null);
		}
		else
		{
			value = prefs.get(key.getValue(), defaultValue.name());
		}
		if (value == null)
			return defaultValue;
		
		return JustificationType.fromName(value);
	}
	
	/**
	 * TODO
	 * 
	 * @param key
	 * @param value
	 */
	public void setJustificationTypePref(PrefKey key, JustificationType value) {
		
		if (value == null)
		{
			setPref(key, null);
		}
		else
		{
			setPref(key, value.toString());
		}
	}
	
	/**
	 * Get the value of a LabelAlignment preference.
	 * 
	 * @param key
	 * @param defaultValue
	 * @return
	 */
	public LabelOrientation getLabelOrientationPref(PrefKey key, LabelOrientation defaultValue) {
		
		String value = "";
		if (defaultValue == null)
		{
			value = prefs.get(key.getValue(), null);
		}
		else
		{
			value = prefs.get(key.getValue(), defaultValue.name());
		}
		if (value == null)
			return defaultValue;
		
		return LabelOrientation.fromName(value);
	}
	
	/**
	 * TODO
	 * 
	 * @param key
	 * @param value
	 */
	public void setLabelOrientationPref(PrefKey key, LabelOrientation value) {
		
		if (value == null)
		{
			setPref(key, null);
		}
		else
		{
			setPref(key, value.toString());
		}
	}
	
	/**
	 * TODO
	 * 
	 * @param key
	 * @param defaultValue
	 * @return
	 */
	public LineStyle getLineStylePref(PrefKey key, LineStyle defaultValue) {
		
		String value = "";
		if (defaultValue == null)
		{
			value = prefs.get(key.getValue(), null);
		}
		else
		{
			value = prefs.get(key.getValue(), defaultValue.name());
		}
		
		if (value == null)
			return LineStyle.SOLID;
		
		return LineStyle.fromString(value);
	}
	
	/**
	 * TODO
	 * 
	 * @param key
	 * @param value
	 */
	public void setLineStylePref(PrefKey key, LineStyle value) {
		
		setPref(key, value.toString());
	}
	
	/**
	 * TODO
	 * 
	 * @param key
	 * @param defaultValue
	 * @return
	 */
	public NoDataLabel getNoDataLabelPref(PrefKey key, NoDataLabel defaultValue) {
		
		String value = "";
		if (defaultValue == null)
		{
			value = prefs.get(key.getValue(), null);
		}
		else
		{
			value = prefs.get(key.getValue(), defaultValue.name());
		}
		
		if (value == null)
			return NoDataLabel.NULL;
		
		return NoDataLabel.fromString(value);
	}
	
	/**
	 * TODO
	 * 
	 * @param key
	 * @param value
	 */
	public void setNoDataLabelPref(PrefKey key, NoDataLabel value) {
		
		setPref(key, value.toString());
	}
	
	/**
	 * Get the value of a AnalysisLabelType preference.
	 * 
	 * @param key
	 * @param defaultValue
	 * @return
	 */
	public ResamplingType getResamplingTypePref(PrefKey key, ResamplingType defaultValue) {
		
		String value = "";
		if (defaultValue == null)
		{
			value = prefs.get(key.getValue(), null);
		}
		else
		{
			value = prefs.get(key.getValue(), defaultValue.name());
		}
		if (value == null)
			return defaultValue;
		
		return ResamplingType.fromName(value);
	}
	
	/**
	 * Set the value of a preference to the specified ResamplingType.
	 * 
	 * @param key
	 * @param value
	 */
	public void setResamplingTypePref(PrefKey key, ResamplingType value) {
		
		setPref(key, value.toString());
	}
}
