/*******************************************************************************
 * Copyright (C) 2014 Peter Brewer
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
 *     
 *     Contributors:
 *     		Peter Brewer
 ******************************************************************************/
package org.fhaes.segmentation;

/**
 * SegmentModel Class.
 */
public class SegmentModel {

	private int firstyear;
	private int lastyear;
	private Boolean isBadSegment;

	/**
	 * TODO
	 * 
	 * @param firstyear
	 * @param lastyear
	 * @throws NumberFormatException
	 */
	public SegmentModel(int firstyear, int lastyear) throws NumberFormatException {

		this.firstyear = firstyear;
		this.lastyear = lastyear;
		this.isBadSegment = false;

		if (firstyear == 0 || lastyear == 0)
			throw new NumberFormatException("No such year as 0BC/AD");
	}

	public int getFirstYear() {

		return firstyear;
	}

	public int getLastYear() {

		return lastyear;
	}

	public void setFirstYear(int firstyear) {

		if (firstyear == 0)
			throw new NumberFormatException("No such year as 0BC/AD");

		this.firstyear = firstyear;

	}

	public void setLastYear(int lastyear) {

		if (lastyear == 0)
			throw new NumberFormatException("No such year as 0BC/AD");

		this.lastyear = lastyear;

	}

	public void setBadSegmentFlag(Boolean b) {

		this.isBadSegment = b;
	}

	public Boolean isBadSegment() {

		return isBadSegment;
	}

	public int getLength() {

		if (lastyear > 0)
		{
			return (lastyear - firstyear) + 1;
		}
		else
		{
			return lastyear - firstyear;
		}
	}

	public String toString() {

		return firstyear + " - " + lastyear;
	}
}
