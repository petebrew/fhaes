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
package org.fhaes.gui;

import java.awt.BorderLayout;

import javax.swing.JPanel;
import javax.swing.JTextPane;

import org.fhaes.enums.AnalysisType;
import org.fhaes.enums.EventTypeToProcess;
import org.fhaes.enums.FireFilterType;
import org.fhaes.enums.SampleDepthFilterType;
import org.fhaes.preferences.App;
import org.fhaes.preferences.FHAESPreferences.PrefKey;
import org.fhaes.preferences.PrefsEvent;
import org.fhaes.preferences.PrefsListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Simple text panel for reminder users what parameters they just selected.
 */
public class GoldFishPanel extends JPanel implements PrefsListener {
	
	private static final long serialVersionUID = 1L;
	
	private static final Logger log = LoggerFactory.getLogger(GoldFishPanel.class);
	private JTextPane textArea;
	
	/**
	 * Simple text panel for reminder users what parameters they just selected.
	 */
	public GoldFishPanel() {
	
		setLayout(new BorderLayout(0, 0));
		App.prefs.addPrefsListener(this);
		
		textArea = new JTextPane();
		textArea.setContentType("text/html");
		add(textArea, BorderLayout.CENTER);
		setParamsText();
	}
	
	/**
	 * Preferences have changed so set parameters text
	 */
	@Override
	public void prefChanged(PrefsEvent e) {
	
		setParamsText();
	}
	
	/**
	 * Set the text panel to show details of the parameters used of the analysis
	 */
	public void setParamsText() {
	
		StringBuilder sb = new StringBuilder();
		
		sb.append("<html>");
		
		// EVENT TYPE
		sb.append("<b>Event type for analysis: </b>");
		sb.append(App.prefs.getEventTypePref(PrefKey.EVENT_TYPE_TO_PROCESS, EventTypeToProcess.FIRE_EVENT) + "<br/>");
		
		// FILTERS
		sb.append("<b>Filter: </b>");
		sb.append(App.prefs.getFireFilterTypePref(PrefKey.COMPOSITE_FILTER_TYPE, FireFilterType.NUMBER_OF_EVENTS));
		sb.append(" >= " + App.prefs.getIntPref(PrefKey.COMPOSITE_FILTER_VALUE, 1) + "<br/>");
		
		sb.append("<b>Filter: </b>");
		sb.append(App.prefs.getSampleDepthFilterTypePref(PrefKey.COMPOSITE_SAMPLE_DEPTH_TYPE, SampleDepthFilterType.MIN_NUM_SAMPLES));
		sb.append(" >= " + App.prefs.getIntPref(PrefKey.COMPOSITE_MIN_SAMPLES, 1) + "<br/>");
		
		// TIME PERIOD
		sb.append("<b>Years: </b>");
		if (App.prefs.getBooleanPref(PrefKey.RANGE_CALC_OVER_ALL_YEARS, true))
		{
			sb.append("All years" + "<br/>");
		}
		else
		{
			sb.append(App.prefs.getIntPref(PrefKey.RANGE_FIRST_YEAR, 1) + " - " + App.prefs.getIntPref(PrefKey.RANGE_LAST_YEAR, 2)
					+ "<br/>");
		}
		
		// INTERVAL ANALYSIS TYPE
		sb.append("<b>Interval analysis type: </b>");
		sb.append(App.prefs.getAnalysisTypePref(PrefKey.INTERVALS_ANALYSIS_TYPE, AnalysisType.COMPOSITE) + "<br/>");
		sb.append("<b>Interval analysis alpha level: </b>");
		sb.append(App.prefs.getDoublePref(PrefKey.INTERVALS_ALPHA_LEVEL, 0.125) + "<br/>");
		
		// INCLUDE LAST INTERVAL
		sb.append("<b>Last interval: </b> ");
		if (App.prefs.getBooleanPref(PrefKey.INTERVALS_INCLUDE_OTHER_INJURIES, false))
		{
			sb.append("Include interval after last event" + "<br/>");
		}
		else
		{
			sb.append("Do not include interval after last event" + "<br/>");
		}
		
		// SEASON COMBINATIONS
		sb.append("<b>First season combination: </b> ");
		if (App.prefs.getBooleanPref(PrefKey.SEASONALITY_FIRST_GROUP_DORMANT, false))
			sb.append("Dormant; ");
		if (App.prefs.getBooleanPref(PrefKey.SEASONALITY_FIRST_GROUP_EARLY_EARLY, false))
			sb.append("Early earlywood; ");
		if (App.prefs.getBooleanPref(PrefKey.SEASONALITY_FIRST_GROUP_MIDDLE_EARLY, false))
			sb.append("Middle earlywood; ");
		if (App.prefs.getBooleanPref(PrefKey.SEASONALITY_FIRST_GROUP_LATE_EARLY, false))
			sb.append("Late earlywood; ");
		if (App.prefs.getBooleanPref(PrefKey.SEASONALITY_FIRST_GROUP_LATE, false))
			sb.append("Latewood; ");
		sb.append("<br/>");
		sb.append("<b>Second season combination: </b> ");
		if (App.prefs.getBooleanPref(PrefKey.SEASONALITY_SECOND_GROUP_DORMANT, false))
			sb.append("Dormant; ");
		if (App.prefs.getBooleanPref(PrefKey.SEASONALITY_SECOND_GROUP_EARLY_EARLY, false))
			sb.append("Early earlywood; ");
		if (App.prefs.getBooleanPref(PrefKey.SEASONALITY_SECOND_GROUP_MIDDLE_EARLY, false))
			sb.append("Middle earlywood; ");
		if (App.prefs.getBooleanPref(PrefKey.SEASONALITY_SECOND_GROUP_LATE_EARLY, false))
			sb.append("Late earlywood; ");
		if (App.prefs.getBooleanPref(PrefKey.SEASONALITY_SECOND_GROUP_LATE, false))
			sb.append("Latewood; ");
		sb.append("<br/>");
		
		textArea.setText(sb.toString());
	}
}
