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
package org.fhaes.enums;

/**
 * JustificationType Enum. This enum models the different kinds of text justification that can be used in FHAES.
 */
public enum JustificationType {
	
	LEFT("Left"),
	
	CENTER("Center"),
	
	RIGHT("Right");
	
	// Declare local variables
	private String humanReadable;
	
	/**
	 * Initialize the human-readable string for the JustificationType.
	 * 
	 * @param str
	 */
	JustificationType(String str) {
		
		humanReadable = str;
	}
	
	/**
	 * Get the human-readable string name for this JustificationType.
	 */
	@Override
	public String toString() {
		
		return humanReadable;
	}
	
	/**
	 * Create a JustificationType from a string name. If there is no JustificationType that matches the string then null is returned.
	 * 
	 * @param name
	 * @return
	 */
	public static JustificationType fromName(String name) {
		
		for (JustificationType type : JustificationType.values())
		{
			if (type.humanReadable.toLowerCase().equals(name.toLowerCase()))
				return type;
		}
		
		return null;
	}
}
