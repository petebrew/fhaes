/**************************************************************************************************
 * Fire History Analysis and Exploration System (FHAES), Copyright (C) 2015
 * 
 * Contributors: Alex Beatty, Clayton Bodendein, Kyle Hartmann, Scott Goble, and Peter Brewer
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
package org.fhaes.fhrecorder.model;

import java.io.Serializable;

import org.fhaes.fhrecorder.controller.FileController;

/**
 * FHX2_Event Class. This class is used to represent fire scars and other events corresponding to the event year.
 * 
 * @author Alex Beatty, Clayton Bodendein, Kyle Hartmann, Scott Goble
 */
public class FHX2_Event implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	public static int DEFAULT_EVENT_FIRST_YEAR = 0;
	public static int DEFAULT_EVENT_LAST_YEAR = FileController.CURRENT_YEAR;
	
	private char eventType; // Event type could be 'D', 'E', 'M', 'L', or 'A'
	private int eventYear;
	
	/**
	 * Default constructor; makes the event a dormant season fire scar that sets at DEFAULT_FIRST_YEAR.
	 */
	public FHX2_Event() {
		
		eventType = 'U';
		eventYear = DEFAULT_EVENT_FIRST_YEAR;
	}
	
	/**
	 * Creates an event with the given parameters; assumes event type to be the default event type, which is the dormant season fire scar
	 * and sets the event year as the year selected by the user
	 * 
	 * @param inEventYear year of event
	 */
	public FHX2_Event(int inEventYear) {
		
		eventType = 'U';
		eventYear = inEventYear;
	}
	
	/**
	 * Creates an event with the given parameters as passed by the user which is updating the event type and the event year
	 * 
	 * @param inEventType type of Event (Earlywood, Middlewood, etc)
	 * @param inEventtYear year of event
	 */
	public FHX2_Event(char inEventType, int inEventYear) {
		
		eventType = inEventType;
		eventYear = inEventYear;
	}
	
	/**
	 * Returns a value representing the event's type which could be 'D', 'E', 'M','L','A'
	 * 
	 * @return eventType, the event type
	 */
	public char getEventType() {
		
		return eventType;
	}
	
	/**
	 * The parameter inEventType passed must be set to the eventType.
	 * 
	 * @param inEventType
	 */
	public void setEventType(char inEventType) {
		
		eventType = inEventType;
	}
	
	/**
	 * Gets the year of the event's occurrence.
	 * 
	 * @return the event year
	 */
	public int getEventYear() {
		
		return eventYear;
	}
	
	/**
	 * The parameter inEventYear passed must be set to the eventYear of the event.
	 * 
	 * @param inEventYear
	 */
	public void setEventYear(int inEventYear) {
		
		eventYear = inEventYear;
	}
	
	/**
	 * Returns true if the event has inYear between its firstYear and lastYear, inclusive
	 * 
	 * @param inYear
	 */
	public boolean containsYear(int inYear) {
		
		return (eventYear == inYear);
	}
}
