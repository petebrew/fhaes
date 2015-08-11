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
package org.fhaes.fhxrecorder.compare;

import java.util.Comparator;

import org.fhaes.fhxrecorder.model.FHX2_Event;

/**
 * CompareEventYears Class. This class is used to compare the difference between two events.
 * 
 * @author Alex Beatty, Clayton Bodendein, Kyle Hartmann, Scott Goble
 */
public class CompareEventYears implements Comparator<FHX2_Event> {
	
	/**
	 * Compares two events according to the year in which they occurred.
	 * 
	 * @return the difference between the two event's years of occurrence
	 */
	@Override
	public int compare(FHX2_Event t, FHX2_Event t1) {
		
		return t.getEventYear() - t1.getEventYear();
	}
}
