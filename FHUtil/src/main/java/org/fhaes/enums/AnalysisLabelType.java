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
 * AnalysisLabelType Enum. Enumeration containing the options for how analyses should be labelled. Normally analyses are labelled using the
 * file name for the input file, but you can also use site codes and site names too.
 * 
 * @author Peter Brewer
 */
public enum AnalysisLabelType {
	
	INPUT_FILENAME("Input file name"),
	
	SITE_CODE("Site code"),
	
	SITE_NAME("Site name");
	
	// Declare local variables
	private String humanreadable;
	
	AnalysisLabelType(String s) {
		
		humanreadable = s;
	}
	
	/**
	 * Get the human readable string name for this AnalysisLabelType.
	 */
	@Override
	public String toString() {
		
		return humanreadable;
	}
	
	/**
	 * Create an AnalysisLabelType from a string name. If there is no AnalysisLabelType that matches the string then null is returned.
	 * 
	 * @param name
	 * @return
	 */
	public static AnalysisLabelType fromName(String name) {
		
		for (AnalysisLabelType type : AnalysisLabelType.values())
		{
			if (type.humanreadable.equals(name))
				return type;
		}
		
		return null;
	}
}
