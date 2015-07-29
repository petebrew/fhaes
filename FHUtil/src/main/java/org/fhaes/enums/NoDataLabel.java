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
 * NoDataLabel Enum.
 */
public enum NoDataLabel {
	
	MINUS_99(-99d),
	
	ZERO(0d),
	
	NULL(null),
	
	NAN(Double.NaN);
	
	// Declare local variables
	private Double value;
	
	/**
	 * TODO
	 * 
	 * @param d
	 */
	NoDataLabel(Double d) {
		
		value = d;
	}
	
	/**
	 * Get the human readable string name for this NoDataLabel.
	 */
	@Override
	public String toString() {
		
		if (value == null)
			return "Null";
			
		return value.toString();
	}
	
	/**
	 * TODO
	 * 
	 * @return
	 */
	public Double toDouble() {
		
		return value;
	}
	
	/**
	 * Create an NoDataLabel from a string name. If there is no NoDataLabel that matches the string then null is returned.
	 * 
	 * @param dbl
	 * @return
	 */
	public static NoDataLabel fromDouble(Double dbl) {
		
		if (dbl == null)
			return NoDataLabel.NULL;
			
		for (NoDataLabel type : NoDataLabel.values())
		{
			if (type.toDouble() == null)
				continue;
			if (type.toDouble().equals(dbl))
				return type;
		}
		
		return NoDataLabel.NULL;
	}
	
	/**
	 * TODO
	 * 
	 * @param str
	 * @return
	 */
	public static NoDataLabel fromString(String str) {
		
		if (str.equals(NoDataLabel.MINUS_99.toString()))
		{
			return NoDataLabel.MINUS_99;
		}
		else if (str.equals(NoDataLabel.ZERO.toString()))
		{
			return NoDataLabel.ZERO;
		}
		else if (str.equals(NoDataLabel.NAN.toString()))
		{
			return NoDataLabel.NAN;
		}
		else
		{
			return NoDataLabel.NULL;
		}
	}
}
