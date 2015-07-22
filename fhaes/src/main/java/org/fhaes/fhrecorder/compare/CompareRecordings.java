/*******************************************************************************
 * Copyright (C) 2014 Brendan Compton, Dylan Jones, Alex Richter, 
 * Chris Wald and Peter Brewer
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
package org.fhaes.fhrecorder.compare;

import java.util.Comparator;

import org.fhaes.fhrecorder.model.FHX2_Recording;

/**
 * CompareRecordings Class. A class that compares different recordings and returns the difference.
 */
public class CompareRecordings implements Comparator<FHX2_Recording> {

	public int compare(FHX2_Recording rec1, FHX2_Recording rec2) {

		return rec1.getStartYear() - rec2.getStartYear();
	}
}
