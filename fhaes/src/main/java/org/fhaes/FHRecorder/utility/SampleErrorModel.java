/**************************************************************************************************
 * Fire History Analysis and Exploration System (FHAES), Copyright (C) 2015
 * 
 * Contributors: Josh Brogan, Jake Lokkesmoe, Chinmay Shah, Scott Goble, and Peter Brewer
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
package org.fhaes.FHRecorder.utility;

/**
 * SampleErrorModel Class.
 * 
 * @author Josh Brogan, Jake Lokkesmoe, Chinmay Shah, Scott Goble
 */
public class SampleErrorModel {
	
	private int year;
	private String message;
	
	/**
	 * Default Constructor for SampleErrorModel.
	 * 
	 * @param message
	 * @param year
	 */
	public SampleErrorModel(String message, int year) {
		
		this.year = year;
		this.message = message;
	}
	
	/**
	 * Gets the year of the error.
	 * 
	 * @return year
	 */
	public int getYear() {
		
		return year;
	}
	
	/**
	 * Gets the message associated with the error.
	 * 
	 * @return message
	 */
	public String getMessage() {
		
		return message;
	}
	
	/**
	 * Generates a specially formatted string containing all the details about an error.
	 */
	@Override
	public String toString() {
		
		return String.format("%d: %s", year, message);
	}
}
