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
 * ResamplingType Enum. Enumeration containing the options for how resampling is done
 * 
 * @author Peter Brewer
 */
public enum ResamplingType {
	
	WITH_REPLACEMENT("With replacement"),
	
	WITHOUT_REPLACEMENT("Without replacement");
	
	// Declare local variables
	private String humanreadable;
	
	ResamplingType(String s) {
		
		humanreadable = s;
	}
	
	/**
	 * Get the human readable string name for this ResamplingType.
	 */
	@Override
	public String toString() {
		
		return humanreadable;
	}
	
	/**
	 * Create an ResamplingType from a string name. If there is no ResamplingType that matches the string then null is returned.
	 * 
	 * @param name
	 * @return
	 */
	public static ResamplingType fromName(String name) {
		
		for (ResamplingType type : ResamplingType.values())
		{
			if (type.humanreadable.equals(name))
				return type;
		}
		
		return null;
	}
}
