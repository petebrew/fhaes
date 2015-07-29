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
 * PredefinedMessageManager Class.
 */
public class PredefinedMessageManager {
	
	/**
	 * This enum contains a set of predefined feedback messages which each are associated with their own show/hide preference key.
	 */
	public enum PredefinedMessage {
		
		FHX2_META_DATA_LENGTH_MESSAGE("Cannot enforce length restrictions without losing data! Please revise the highlighted fields.",
				null),
				
		FHX2_SAMPLE_NAME_LENGTH_MESSAGE("Sample name is too long for the original FHX2 program requirements.", null),
		
		FILE_SAVED_MESSAGE("File was saved successfully", PrefKey.SHOW_FILE_SAVED_MESSAGE),
		
		MINIMUM_SAMPLE_NAME_LENGTH_MESSAGE("Sample name must be at least 3 characters in length.", null);
		
		// Declare local variables
		private final String messageText;
		private final PrefKey associatedKey;
		
		/**
		 * Initialize the message text for the PredefinedMessage.
		 * 
		 * @param message
		 */
		PredefinedMessage(String message, PrefKey key) {
			
			messageText = message;
			associatedKey = key;
		}
		
		/**
		 * Get the message text for the PredefinedMessage.
		 * 
		 * @return messageText
		 */
		public String getMessage() {
			
			return messageText;
		}
		
		/**
		 * Get the associated preference key for the PredefinedMessage.
		 * 
		 * @return associatedKey
		 */
		public PrefKey getAssociatedKey() {
			
			return associatedKey;
		}
	}
	
	/**
	 * Resets all feedback message preferences so that every message is displayed on default.
	 */
	public static void ResetAllFeedbackMessagePrefs() {
		
		App.prefs.setBooleanPref(PrefKey.SHOW_FILE_SAVED_MESSAGE, true);
	}
}
