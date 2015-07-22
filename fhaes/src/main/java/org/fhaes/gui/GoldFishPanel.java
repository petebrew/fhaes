package org.fhaes.gui;

import java.awt.BorderLayout;

import javax.swing.JPanel;
import javax.swing.JTextArea;

import org.fhaes.enums.AnalysisType;
import org.fhaes.enums.EventTypeToProcess;
import org.fhaes.enums.FireFilterType;
import org.fhaes.preferences.App;
import org.fhaes.preferences.FHAESPreferences.PrefKey;
import org.fhaes.preferences.PrefsEvent;
import org.fhaes.preferences.PrefsListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * GoldFishPanel Class.
 */
public class GoldFishPanel extends JPanel implements PrefsListener {

	private static final long serialVersionUID = 1L;

	// Declare FHAES specialized objects
	private static final Logger log = LoggerFactory.getLogger(GoldFishPanel.class);

	// Declare local variables
	private JTextArea textArea;

	/**
	 * TODO
	 */
	public GoldFishPanel() {

		setLayout(new BorderLayout(0, 0));
		App.prefs.addPrefsListener(this);

		textArea = new JTextArea();
		add(textArea, BorderLayout.CENTER);
		setParamsText();
	}

	/**
	 * TODO
	 */
	@Override
	public void prefChanged(PrefsEvent e) {

		log.debug("Preferences changed");
		setParamsText();
	}

	/**
	 * TODO
	 */
	public void setParamsText() {

		String text = "Filter: " + App.prefs.getFireFilterTypePref(PrefKey.COMPOSITE_FILTER_TYPE, FireFilterType.NUMBER_OF_EVENTS) + " >= "
				+ App.prefs.getIntPref(PrefKey.COMPOSITE_FILTER_VALUE, 1) + System.getProperty("line.separator");

		boolean allyears = App.prefs.getBooleanPref(PrefKey.RANGE_CALC_OVER_ALL_YEARS, true);
		text += "Years: ";

		if (allyears)
		{
			text += "All years" + System.getProperty("line.separator");
		}
		else
		{
			text += App.prefs.getIntPref(PrefKey.RANGE_FIRST_YEAR, 1) + " - " + App.prefs.getIntPref(PrefKey.RANGE_LAST_YEAR, 2)
					+ System.getProperty("line.separator");
		}

		text += "Event type for analysis: " + App.prefs.getEventTypePref(PrefKey.EVENT_TYPE_TO_PROCESS, EventTypeToProcess.FIRE_EVENT)
				+ System.getProperty("line.separator");
		text += "Interval analysis type: " + App.prefs.getAnalysisTypePref(PrefKey.INTERVALS_ANALYSIS_TYPE, AnalysisType.COMPOSITE)
				+ System.getProperty("line.separator");

		boolean includeLastInterval = App.prefs.getBooleanPref(PrefKey.INTERVALS_INCLUDE_OTHER_INJURIES, false);
		text += "Include interval after last event?: ";

		if (includeLastInterval)
		{
			text += "Yes" + System.getProperty("line.separator");
		}
		else
		{
			text += "No" + System.getProperty("line.separator");
		}

		textArea.setText(text);
	}
}
