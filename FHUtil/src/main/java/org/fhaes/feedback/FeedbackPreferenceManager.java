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
package org.fhaes.feedback;

import org.fhaes.preferences.App;
import org.fhaes.preferences.FHAESPreferences.PrefKey;

/**
 * FeedbackPreferenceManager Class. This class contains a 'dictionary' of feedback messages which have their own show/hide preferences as
 * well as the necessary logic for managing those associated preference keys.
 */
public class FeedbackPreferenceManager {
	
	/**
	 * This enum contains a set of feedback messages with show/hide preferences.
	 */
	public enum FeedbackDictionary {
		
		CATEGORY_FILE_SAVED_MESSAGE("Category file was saved successfully.", PrefKey.SHOW_CATEGORY_FILE_SAVED_MESSAGE),
		
		FHRECORDER_FILE_SAVED_MESSAGE("FHX file was saved successfully.", PrefKey.SHOW_FHRECORDER_FILE_SAVED_MESSAGE),
		
		NEOFHCHART_BULK_EXPORT_MESSAGE("Bulk chart export completed successfully.", PrefKey.SHOW_NEOFHCHART_BULK_EXPORT_MESSAGE),
		
		NEOFHCHART_PDF_EXPORT_MESSAGE("PDF file was exported successfully.", PrefKey.SHOW_NEOFHCHART_PDF_EXPORT_MESSAGE),
		
		NEOFHCHART_PNG_EXPORT_MESSAGE("PNG file was exported successfully.", PrefKey.SHOW_NEOFHCHART_PNG_EXPORT_MESSAGE),
		
		NEOFHCHART_SVG_EXPORT_MESSAGE("SVG file was exported successfully.", PrefKey.SHOW_NEOFHCHART_SVG_EXPORT_MESSAGE);
		
		// Declare local variables
		private final String humanReadable;
		private final PrefKey associatedKey;
		
		/**
		 * Initialize the human-readable string and associated preference key for the feedback message.
		 * 
		 * @param message
		 */
		FeedbackDictionary(String str, PrefKey key) {
			
			humanReadable = str;
			associatedKey = key;
		}
		
		/**
		 * Get the associated preference key for the feedback message.
		 * 
		 * @return associatedKey
		 */
		public PrefKey getAssociatedKey() {
			
			return associatedKey;
		}
		
		/**
		 * Get the human-readable string name for this feedback message.
		 * 
		 * @return humanReadable
		 */
		@Override
		public String toString() {
			
			return humanReadable;
		}
	}
	
	/**
	 * Searches the feedback dictionary for a message which has text that matches the input string. If one is found, this method returns the
	 * associated preference key for that message. Otherwise it will return null.
	 * 
	 * @param inText
	 */
	public static PrefKey GetAssociatedKeyFromMessageText(String inText) {
		
		FeedbackDictionary[] allFeedbackMessages = FeedbackDictionary.values();
		
		for (int i = 0; i < allFeedbackMessages.length; i++)
		{
			if (allFeedbackMessages[i].toString().equals(inText))
			{
				return allFeedbackMessages[i].getAssociatedKey();
			}
		}
		
		return null;
	}
	
	/**
	 * Resets all feedback message preferences so that every message is displayed on default.
	 */
	public static void ResetAllFeedbackMessagePrefs() {
		
		App.prefs.setBooleanPref(PrefKey.SHOW_CATEGORY_FILE_SAVED_MESSAGE, true);
		
		App.prefs.setBooleanPref(PrefKey.SHOW_FHRECORDER_FILE_SAVED_MESSAGE, true);
		
		App.prefs.setBooleanPref(PrefKey.SHOW_NEOFHCHART_BULK_EXPORT_MESSAGE, true);
		
		App.prefs.setBooleanPref(PrefKey.SHOW_NEOFHCHART_PDF_EXPORT_MESSAGE, true);
		
		App.prefs.setBooleanPref(PrefKey.SHOW_NEOFHCHART_PNG_EXPORT_MESSAGE, true);
		
		App.prefs.setBooleanPref(PrefKey.SHOW_NEOFHCHART_SVG_EXPORT_MESSAGE, true);
	}
}
