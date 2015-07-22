/*******************************************************************************
 * Copyright (C) 2014 Josh Brogan, Jake Lokkesmoe, Chinmay Shah, Scott Goble
 * and Peter Brewer
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
 *     1) This file was added to account for the fact that the previous version
 *     of this program (before we maintained it) used to treat recording years
 *     and events as a combined entities. The class in this file is how we
 *     implemented our separation of these two entities.
 ******************************************************************************/
package org.fhaes.fhrecorder.model;

import java.io.Serializable;

import org.fhaes.fhrecorder.controller.FileController;

/**
 * FHX2_Recording Class. This class is used to represent a recording year in the FHX2 data.
 * 
 * @author Alex Beatty, Clayton Bodendein, Kyle Hartmann, Scott Goble
 */
public class FHX2_Recording implements Serializable {

	private static final long serialVersionUID = 1L;
	private int recordingStartYear;
	private int recordingEndYear;

	/**
	 * Default Constructor for FHX2_RecordingYear.
	 */
	public FHX2_Recording() {

		recordingStartYear = 1;
		recordingEndYear = FileController.CURRENT_YEAR;
	}

	/**
	 * Alternative Constructor for FHX2_RecordingYear.
	 * 
	 * @param startYear
	 * @param endYear
	 */
	public FHX2_Recording(int startYear, int endYear) {

		recordingStartYear = startYear;
		recordingEndYear = endYear;
	}

	/**
	 * Alternative Constructor for FHX2_RecordingYear. This one sets both the first and last years of the recording to the year in the
	 * parameter.
	 * 
	 * @param startYear
	 */
	public FHX2_Recording(int startYear) {

		recordingStartYear = startYear;
		recordingEndYear = FileController.CURRENT_YEAR;
	}

	/**
	 * Returns an integer representing the starting year of the recording.
	 * 
	 * @return recordingStartYear
	 */
	public int getStartYear() {

		return recordingStartYear;
	}

	/**
	 * Returns an integer representing the ending year of the recording.
	 * 
	 * @return recordingEndYear
	 */
	public int getEndYear() {

		return recordingEndYear;
	}

	/**
	 * Sets recordingFirstYear equal to the value in the parameter.
	 * 
	 * @param startYear
	 */
	public void setStartYear(int startYear) {

		recordingStartYear = startYear;
	}

	/**
	 * Sets recordingLastYear equal to the value in the parameter.
	 * 
	 * @param endYear
	 */
	public void setEndYear(int endYear) {

		recordingEndYear = endYear;
	}

	/**
	 * Returns true if the value of inYear is at or between the values set for recordingFirstYear and recordingLastYear.
	 * 
	 * @param inYear
	 * @return true if inYear is between the values, false otherwise
	 */
	public boolean containsYear(int year) {

		return (year >= recordingStartYear && year <= recordingEndYear);
	}

	/**
	 * Gets the number of years that the recording takes place.
	 * 
	 * @return the number of years
	 */
	public int getNumOfYears() {

		if (containsYear(0))
			return recordingEndYear - recordingStartYear;
		else
			return recordingEndYear - recordingStartYear + 1;
	}
}
