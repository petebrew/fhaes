/*******************************************************************************
 * Copyright (C) 2013 Alex Beatty, Clayton Bodendein, Kyle Hartmann, 
 * Scott Goble and Peter Brewer
 * 
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 * 
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 * 
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/

/*******************************************************************************
 * Maintenance Log (Spring 2014)
 * 
 *     All maintenance work was performed collectively by Josh Brogan, 
 *     Jake Lokkesmoe and Chinmay Shah.
 *     
 *     1) Added various method comments and normalized general code structure.
 ******************************************************************************/
package org.fhaes.fhrecorder.compare;

import java.util.Comparator;

import org.fhaes.fhrecorder.model.FHX2_Sample;

/**
 * CompareSampleLastYearDescending Class. This class is used to compare the difference between the ending years of two FHX2samples in
 * descending order.
 * 
 * @author Alex Beatty, Clayton Bodendein, Kyle Hartmann, Scott Goble
 */
public class CompareSampleLastYearDescending implements Comparator<FHX2_Sample> {

	/**
	 * Compares two samples according to their respective ending years. This is done in descending order.
	 * 
	 * @return the difference between the two event's ending years
	 */
	public int compare(FHX2_Sample t, FHX2_Sample t1) {

		FHX2_Sample s1 = (FHX2_Sample) t;
		FHX2_Sample s2 = (FHX2_Sample) t1;
		return s2.getSampleLastYear() - s1.getSampleLastYear();
	}
}
