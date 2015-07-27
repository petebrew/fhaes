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
 * FireFilterType Enum.
 */
public enum FireFilterType {
	
	NUMBER_OF_EVENTS("Number of fires"),
	
	PERCENTAGE_OF_EVENTS("Percent scarred");
	
	// Declare local variables
	private String humanreadable;
	
	FireFilterType(String s) {
		
		humanreadable = s;
	}
	
	@Override
	public String toString() {
		
		return humanreadable;
	}
	
	public static FireFilterType fromName(String name) {
		
		for (FireFilterType type : FireFilterType.values())
		{
			if (type.humanreadable.equals(name))
				return type;
		}
		
		return null;
	}
}
