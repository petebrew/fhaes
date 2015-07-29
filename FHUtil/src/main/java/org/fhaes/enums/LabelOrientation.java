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
 * LabelOrientation Enum.
 */
public enum LabelOrientation {
	
	HORIZONTAL("Horizontal", 0),
	
	VERTICAL("Vertical", 270),
	
	ANGLED("Angled", 315);
	
	// Declare local variables
	private String humanReadable;
	private int angle;
	
	/**
	 * Initialize the human-readable string and angle for the LabelOrientation.
	 * 
	 * @param str
	 * @param angle
	 */
	LabelOrientation(String str, int angle) {
		
		humanReadable = str;
		this.angle = angle;
	}
	
	/**
	 * Get the human-readable string name for this LabelOrientation.
	 */
	@Override
	public String toString() {
		
		return humanReadable;
	}
	
	/**
	 * Get the integer angle name for this LabelOrientation.
	 */
	public int getAngle() {
		
		return angle;
	}
	
	/**
	 * Create a LabelOrientation from a string name. If there is no LabelOrientation that matches the string then null is returned.
	 * 
	 * @param name
	 * @return
	 */
	public static LabelOrientation fromName(String name) {
		
		for (LabelOrientation type : LabelOrientation.values())
		{
			if (type.humanReadable.toLowerCase().equals(name.toLowerCase()))
				return type;
		}
		
		return null;
	}
}
