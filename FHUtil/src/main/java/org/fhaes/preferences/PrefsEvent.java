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

import java.util.EventObject;

import org.fhaes.preferences.FHAESPreferences.PrefKey;

/**
 * PrefsEvent Class.
 */
public class PrefsEvent extends EventObject {
	
	private static final long serialVersionUID = 1L;
	private final PrefKey pref;
	
	/**
	 * Make a new PrefsEvent.
	 * 
	 * @param source the object which fired this event
	 * @param pref the key for the preference which was changed
	 */
	public PrefsEvent(Object source, PrefKey pref) {
		
		super(source);
		this.pref = pref;
	}
	
	/**
	 * Get the key for the preference which was changed.
	 * 
	 * @return the key of the pref which was changed
	 */
	public PrefKey getPref() {
		
		return pref;
	}
}
