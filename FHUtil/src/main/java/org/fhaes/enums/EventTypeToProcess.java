/**************************************************************************************************
 * Fire History Analysis and Exploration System (FHAES), Copyright (C) 2015
 * 
 * Contributors: Elena Velasquez and Peter Brewer
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
package org.fhaes.enums;

/**
 * EventTypeToProcess Enum.
 */
public enum EventTypeToProcess {
	
	FIRE_EVENT("Fire events"),
	
	INJURY_EVENT("Other indicators"),
	
	FIRE_AND_INJURY_EVENT("Fire events and other indicators");
	
	// Declare local variables
	private String humanReadable;
	
	/**
	 * Initialize the human-readable string for the EventTypeToProcess.
	 * 
	 * @param str
	 */
	EventTypeToProcess(String str) {
		
		humanReadable = str;
	}
	
	/**
	 * Get the human-readable string name for this EventTypeToProcess.
	 */
	@Override
	public String toString() {
		
		return humanReadable;
	}
	
	/**
	 * Create an EventTypeToProcess from a string name. If there is no EventTypeToProcess that matches the string then null is returned.
	 * 
	 * @param name
	 * @return
	 */
	public static EventTypeToProcess fromName(String name) {
		
		for (EventTypeToProcess type : EventTypeToProcess.values())
		{
			if (type.humanReadable.equals(name))
				return type;
		}
		
		return null;
	}
}
