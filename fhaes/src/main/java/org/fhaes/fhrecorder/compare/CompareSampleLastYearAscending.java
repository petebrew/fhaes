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
package org.fhaes.fhrecorder.compare;

import java.util.Comparator;

import org.fhaes.fhrecorder.model.FHX2_Sample;

/**
 * CompareSampleLastYearAscending Class. This class is used to compare the difference between the ending years of two FHX2samples in
 * ascending order.
 * 
 * @author Alex Beatty, Clayton Bodendein, Kyle Hartmann, Scott Goble
 */
public class CompareSampleLastYearAscending implements Comparator<FHX2_Sample> {
	
	/**
	 * Compares two samples according to their respective ending years. This is done in ascending order.
	 * 
	 * @return the difference between the two event's ending years
	 */
	@Override
	public int compare(FHX2_Sample t, FHX2_Sample t1) {
		
		FHX2_Sample s1 = t;
		FHX2_Sample s2 = t1;
		return s1.getSampleLastYear() - s2.getSampleLastYear();
	}
}
