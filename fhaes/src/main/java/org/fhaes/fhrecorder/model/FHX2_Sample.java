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
 *     2) Sample toString fixed to correctly return start and last years
 *     even without having any events.
 *     3) Added setSampleFirstYearToFirstEventYear() method to help address
 *     missing feature #2 on our original list of maintenance requests.
 ******************************************************************************/
package org.fhaes.fhrecorder.model;

import java.awt.Color;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Vector;

import javax.swing.event.ChangeListener;

import org.fhaes.exceptions.CompositeFileException;
import org.fhaes.fhrecorder.controller.FileController;
import org.fhaes.fhrecorder.controller.IOController;
import org.fhaes.fhrecorder.controller.SampleController;
import org.fhaes.fhrecorder.utility.ErrorTrackerInterface;
import org.fhaes.fhrecorder.utility.SampleErrorModel;
import org.fhaes.fhrecorder.view.EventTable;
import org.fhaes.fhrecorder.view.FireHistoryRecorder;
import org.fhaes.fhrecorder.view.RecordingTable;
import org.fhaes.fhrecorder.view.StatusBarPanel;
import org.fhaes.fhrecorder.view.FireHistoryRecorder.MessageType;

/**
 * FHX2_Sample Class. This class is used to represent a sample in the FHX2 data.
 * 
 * @author Alex Beatty, Clayton Bodendein, Kyle Hartmann, Scott Goble
 */
public class FHX2_Sample implements Serializable, ErrorTrackerInterface {

	private static final long serialVersionUID = 1L;

	private String sampleName;
	private int sampleFirstYear;
	private int sampleLastYear;
	private char openingChar; // first character in the sample that represents series data
	private char closingChar; // last character in the sample that represents series data
	private boolean pith;
	private boolean bark;

	private ArrayList<SampleErrorModel> errors = new ArrayList<SampleErrorModel>();
	private EventTable eventTable;
	private RecordingTable recordingTable;

	/**
	 * Default Constructor for FHX2_Sample.
	 */
	public FHX2_Sample() {

		sampleName = "";
		openingChar = '.';
		closingChar = '.';
		initializeTables();
	}

	/**
	 * Alternative Constructor for FHX2_Sample.
	 * 
	 * @param inName (the name of the sample)
	 */
	public FHX2_Sample(String inName) {

		sampleName = checkSampleNameLength(inName);
		openingChar = '.';
		closingChar = '.';
		initializeTables();
	}

	/**
	 * Alternative Constructor for FHX2_Sample.
	 * 
	 * @param inSampleFirstYear (the first year of the sample)
	 * @param inSampleLastYear (the last year of the sample)
	 */
	public FHX2_Sample(int inSampleFirstYear, int inSampleLastYear) {

		sampleFirstYear = inSampleFirstYear;
		sampleLastYear = inSampleLastYear;
		openingChar = '.';
		closingChar = '.';
		initializeTables();
	}

	/**
	 * Alternative Constructor for FHX2_Sample.
	 * 
	 * @param inName (the name of the sample)
	 * @param inSampleFirstYear (the first year of the sample)
	 * @param inSampleLastYear (the last year of the sample)
	 * @param inPith (whether or not the sample begins with a pith)
	 * @param inBark (whether or not the sample ends at the bark)
	 */
	public FHX2_Sample(String inName, int inSampleFirstYear, int inSampleLastYear, boolean inPith, boolean inBark) {

		sampleName = checkSampleNameLength(inName);
		sampleFirstYear = inSampleFirstYear;
		sampleLastYear = inSampleLastYear;
		pith = inPith;
		bark = inBark;

		if (inPith)
			openingChar = '[';
		else
			openingChar = '{';

		if (inBark)
			closingChar = ']';
		else
			closingChar = '}';

		initializeTables();
	}

	/**
	 * Initializes the event and recording tables.
	 */
	private void initializeTables() {

		eventTable = new EventTable(this);
		recordingTable = new RecordingTable(this);
	}

	/**
	 * Generates a specially formatted string containing the sample's name, first year, and last year.
	 */
	public String toString() {

		return sampleName + " [" + sampleFirstYear + "–" + sampleLastYear + "]";
	}

	/**
	 * Generates a specially formatted string containing all information between the first and last years given by the input parameters.
	 * 
	 * @param inDatasetFirstYear
	 * @param inDatasetLastYear
	 * @return tempString
	 */
	public String toString(int inDatasetFirstYear, int inDatasetLastYear) {

		String tempString = "";
		for (int i = inDatasetFirstYear; i <= inDatasetLastYear; i++)
			tempString += charAt(i);
		return tempString;
	}

	/**
	 * Returns a string representing the name of the sample.
	 * 
	 * @return sampleName
	 */
	public String getSampleName() {

		return sampleName;
	}

	/**
	 * Renames the sample according to the string in the parameter, then updates the GUI.
	 * 
	 * @param inName
	 */
	public void setSampleName(String inName) {

		sampleName = checkSampleNameLength(inName);
	}

	/**
	 * Returns an integer representing the first year of the sample.
	 * 
	 * @return sampleFirstYear
	 */
	public int getSampleFirstYear() {

		return sampleFirstYear;
	}

	/**
	 * Updates the sampleFirstYear according to the parameter, then updates the GUI.
	 * 
	 * @param inSampleFirstYear
	 */
	public void setSampleFirstYear(int inSampleFirstYear) {

		sampleFirstYear = inSampleFirstYear;
		fixEventsAndRecordingsWithNewYears();
	}

	/**
	 * Returns an integer representing the last year of the sample.
	 * 
	 * @return sampleLastYear
	 */
	public int getSampleLastYear() {

		return sampleLastYear;
	}

	/**
	 * Updates the sampleLastYear according to the parameter, then updates the GUI.
	 * 
	 * @param inSampleLastYear
	 */
	public void setSampleLastYear(int inSampleLastYear) {

		sampleLastYear = inSampleLastYear;
		fixEventsAndRecordingsWithNewYears();
	}

	/**
	 * Returns a value representing the status of pith.
	 * 
	 * @return true if the sample begins with pith, false otherwise
	 */
	public boolean hasPith() {

		return pith;
	}

	/**
	 * Returns a value representing the status of bark.
	 * 
	 * @return true if the sample ends with bark, false otherwise
	 */
	public boolean hasBark() {

		return bark;
	}

	/**
	 * Updates the status of pith according to the parameter, the updates the GUI.
	 * 
	 * @param inPith
	 */
	public void setPith(boolean inPith) {

		pith = inPith;

		// Only update the character if series is not started with an event
		if (!Character.isLetter(openingChar))
		{
			if (inPith)
				openingChar = '[';
			else
				openingChar = '{';
		}
	}

	/**
	 * Updates the status of bark according to the parameter, then updates the GUI.
	 * 
	 * @param inBark
	 */
	public void setBark(boolean inBark) {

		bark = inBark;

		// Only update the character if the series is not ended with an event
		if (!Character.isLetter(closingChar))
		{
			if (inBark)
				closingChar = ']';
			else
				closingChar = '}';
		}
	}

	/**
	 * Checks if the sample's first year is an event.
	 * 
	 * @return true if it is an event, false otherwise
	 */
	public Boolean sampleStartsWithEvent() {

		return sampleFirstYear == SampleController.getYearOfFirstEventInSelectedSample();
	}

	/**
	 * Checks if the sample's last year is an event.
	 * 
	 * @return true if it is an event, false otherwise
	 */
	public Boolean sampleEndsWithEvent() {

		return sampleLastYear == SampleController.getYearOfLastEventInSelectedSample();
	}

	/**
	 * Returns a character representing whether or not the sample begins with a pith.
	 * 
	 * @return openingChar
	 */
	private char sampleFirstYearAsChar() {

		return openingChar;
	}

	/**
	 * Returns a character representing whether or not the sample ends at the bark.
	 * 
	 * @return closingChar
	 */
	private char sampleLastYearAsChar() {

		return closingChar;
	}

	/**
	 * Updates the opening character of the sample.
	 */
	public void updateOpeningChar() {

		if (sampleStartsWithEvent())
			openingChar = eventTable.getEarliestEvent().getEventType();
		else
		{
			if (pith == true)
				openingChar = '[';
			else
				openingChar = '{';
		}
	}

	/**
	 * Updates the closing character of the sample.
	 */
	public void updateClosingChar() {

		if (sampleEndsWithEvent())
			closingChar = eventTable.getLatestEvent().getEventType();
		else
		{
			if (bark == true)
				closingChar = ']';
			else
				closingChar = '}';
		}
	}

	/**
	 * Checks if year is contained in this sample.
	 * 
	 * @param year
	 * @return true if contained, false otherwise
	 */
	public boolean containsYear(int year) {

		return year >= sampleFirstYear && year <= sampleLastYear;
	}

	/**
	 * Checks the length of the sample name against the old FHX2 requirements.
	 * 
	 * @param name
	 * @return "" if the name exceeds the length requirement, name otherwise
	 */
	private String checkSampleNameLength(String inputName) {

		if (inputName.length() > FileController.FHX2_MAX_SAMPLE_NAME_LENGTH && FileController.isEnforcingOldReqs())
		{
			FireHistoryRecorder.updateStatusBarMessage(MessageType.WARNING, Color.red,
					StatusBarPanel.FHX2_SAMPLE_NAME_LENGTH_MESSAGE_ID,
					"Sample name is too long for the original FHX2 program requirements.");
			return "";
		}

		if (StatusBarPanel.getCurrentMessageID() == StatusBarPanel.FHX2_SAMPLE_NAME_LENGTH_MESSAGE_ID)
			FireHistoryRecorder.clearStatusBarMessage();

		return inputName;
	}

// ********************************************************************************************************************
// EventTable Methods
// ********************************************************************************************************************

	/**
	 * Returns an integer representing the total number of events.
	 * 
	 * @return the number of events in the sample
	 */
	public int getNumOfEvents() {

		return eventTable.getNumOfEvents();
	}

	/**
	 * Gets an event from the sample according to its index in the list.
	 * 
	 * @param index
	 * @return the event if it exists, null otherwise
	 */
	public FHX2_Event getEvent(int index) {

		return eventTable.getEvent(index);
	}

	/**
	 * Gets an ArrayList of all events in the eventTable.
	 */
	public ArrayList<FHX2_Event> getEvents() {

		return eventTable.getEvents();
	}

	/**
	 * Gets the eventTable.
	 * 
	 * @return
	 */
	public EventTable getEventTable() {

		return eventTable;
	}

	/**
	 * Returns the next year that is in a recording and not taken by another event.
	 * 
	 * @return 0 if nothing is available (year 0 does not exist)
	 */
	public int getNextAvailableEventYear() {

		return getNextAvailableEventYear(sampleFirstYear);
	}

	/**
	 * Returns the next year that is in a recording and not taken by another event. If possible, it will return the preferredYear.
	 * 
	 * @param preferredYear
	 * @return 0 if nothing is available (year 0 does not exist)
	 */
	public int getNextAvailableEventYear(int preferredYear) {

		int curYear = preferredYear;

		// First check for the preferred year and any years ahead of it
		for (; curYear <= sampleLastYear; curYear++)
			if (eventYearAvailable(curYear))
				return curYear;

		// Now check for any years before the preferred year
		for (curYear = sampleFirstYear; curYear < preferredYear; curYear++)
			if (eventYearAvailable(curYear))
				return curYear;

		// No years available
		return 0;
	}

	/**
	 * Adds an event to the sample.
	 * 
	 * @param inEvent
	 */
	public void addEvent(FHX2_Event inEvent) {

		eventTable.addEvent(inEvent);
	}

	/**
	 * Removes an event from the sample.
	 * 
	 * @param index
	 */
	public void removeEvent(int index) {

		eventTable.removeEvent(index);
	}

	/**
	 * Changes the event type and eventYear in the case that the type changes from "fire scar" to "other injury"
	 * 
	 * @param inEventIndex
	 * @param inEventType
	 */
	public int changeEventType(int inEventIndex, char inEventType) {

		return eventTable.changeEventType(inEventIndex, inEventType);
	}

	/**
	 * Returns whether or not this event year is available.
	 * 
	 * @param year
	 * @return true if available, false otherwise
	 */
	public boolean eventYearAvailable(int year) {

		return (year != 0 && isYearInRecordings(year) && !eventTable.yearHasAnEvent(year));
	}

	/**
	 * Checks whether or not an event has occurred on the input year.
	 * 
	 * @param year
	 * @return true if an event occurred, false otherwise
	 */
	public boolean hasEventInYear(int year) {

		for (FHX2_Event evt : eventTable.getEvents())
			if (evt.containsYear(year))
				return true;
		return false;
	}

	/**
	 * Redraws the eventTable in the GUI.
	 */
	public void redrawEventTable() {

		eventTable.redrawTable();
	}

	/**
	 * Sorts events in the sample according to their year of occurrence.
	 */
	public void sortEvents() {

		eventTable.sortEvents();
	}

// ********************************************************************************************************************
// RecordingTable Methods
// ********************************************************************************************************************

	/**
	 * Returns an integer representing the total number of recordings.
	 * 
	 * @return the number of recordings in the sample
	 */
	public int getNumOfRecordings() {

		return recordingTable.getNumOfRecordings();
	}

	/**
	 * Gets a recording from the index location.
	 * 
	 * @param index
	 * @return
	 */
	public FHX2_Recording getRecording(int index) {

		return recordingTable.getRecording(index);
	}

	/**
	 * Gets an ArrayList of all recordings in the recordingTable.
	 */
	public ArrayList<FHX2_Recording> getRecordings() {

		return recordingTable.getRecordings();
	}

	/**
	 * Gets the recordingTable
	 * 
	 * @return
	 */
	public RecordingTable getRecordingTable() {

		return recordingTable;
	}

	/**
	 * Returns the next year that is not recording.
	 * 
	 * @return 0 if nothing is available (year 0 does not exist)
	 */
	public int getNextAvailableRecordingYear() {

		return getNextAvailableRecordingYear(sampleFirstYear);
	}

	/**
	 * Returns the next year that is not recording. If possible, it will return the preferredYear.
	 * 
	 * @param preferredYear
	 * @return 0 if nothing is available (year 0 does not exist)
	 */
	public int getNextAvailableRecordingYear(int preferredYear) {

		int curYear = preferredYear;

		// First check for the preferred year and any years ahead of it
		for (; curYear <= sampleLastYear; curYear++)
			if (curYear != 0 && !isYearInRecordings(curYear))
				return curYear;

		// Now check for any years before the preferred year
		for (curYear = sampleFirstYear; curYear < preferredYear; curYear++)
			if (curYear != 0 && !isYearInRecordings(curYear))
				return curYear;

		// No years available
		return 0;
	}

	/**
	 * Adds a recording to the sample.
	 * 
	 * @param recording
	 */
	public void addRecording(FHX2_Recording recording) {

		recordingTable.addRecording(recording);
	}

	/**
	 * Removes a recording from the sample.
	 * 
	 * @param index
	 */
	public void removeRecording(int index) {

		removeRecording(index, false);
	}

	/**
	 * Removes a recording from the sample. If ignoreOutOfRangeEvents is true, then events that are outside of the remaining recording years
	 * are not deleted, otherwise they are
	 * 
	 * @param index
	 * @param ignoreOutOfRangeEvents
	 */
	public void removeRecording(int index, boolean ignoreOutOfRangeEvents) {

		recordingTable.removeRecording(index);

		// If there are no recordings left, delete all of the events
		if (recordingTable.getNumOfRecordings() <= 0)
		{
			if (!ignoreOutOfRangeEvents)
				eventTable.deleteAllEvents();
		}
		else
		{
			// Make sure all the events still lie within the recording ranges, delete them if they don't
			ArrayList<FHX2_Event> events = eventTable.getEvents();
			for (int i = events.size() - 1; i >= 0; i--)
			{
				if (!recordingTable.isYearInRecordings(events.get(i).getEventYear()))
				{
					// Delete this event
					if (!ignoreOutOfRangeEvents)
						eventTable.deleteEventInYear(events.get(i).getEventYear());
				}
			}
		}
	}

	/**
	 * Returns whether or not a year is in one of the recordings.
	 * 
	 * @param year
	 * @return true if it's in a recording, false otherwise
	 */
	public boolean isYearInRecordings(int year) {

		return recordingTable.isYearInRecordings(year);
	}

	/**
	 * Adjusts the table values with the new years after a change has occurred.
	 */
	private void fixEventsAndRecordingsWithNewYears() {

		// Delete any events outside of the new range
		eventTable.deleteEventsNotInRange(sampleFirstYear, sampleLastYear);

		// Move the recordings around for the new range
		recordingTable.moveRecordingsIntoRange(sampleFirstYear, sampleLastYear);
	}

// ********************************************************************************************************************
// Sample Parsing-Related Methods
// ********************************************************************************************************************

	/**
	 * Returns a character representing the event at the input year.
	 * 
	 * @param inYear
	 * @return the representing the event at inYear
	 */
	private char charAt(int inYear) {

		char returnChar = '.';
		for (int i = 0; i < recordingTable.getNumOfRecordings(); i++)
			if (recordingTable.getRecording(i).containsYear(inYear))
			{
				returnChar = '|';
				break;
			}

		for (int i = 0; i < eventTable.getNumOfEvents(); i++)
			if (eventTable.getEvent(i).getEventYear() == inYear)
			{
				returnChar = eventTable.getEvent(i).getEventType();
				break;
			}

		// Maintenance request: Fixed General Errors 4, Sample without events does not save start and end year.
		if (inYear == sampleFirstYear)
			returnChar = sampleFirstYearAsChar();

		if (inYear == sampleLastYear && (returnChar == '.' || returnChar == '|'))
			returnChar = sampleLastYearAsChar();

		return returnChar;
	}

	/**
	 * Parses input to the appropriate variables. This is done once for each and every sample in the file.
	 * 
	 * @param inData (the string of data to be parsed)
	 * @param dataSetFirstYear (the first year of data)
	 * @throws CompositeFileException
	 */
	public void parseDataString(String inData, int dataSetFirstYear) throws CompositeFileException {

		boolean openingCharAlreadySet = false; // Valid opening characters include: [, {, or a letter (an event)
		boolean closingCharAlreadySet = false; // Valid closing characters include: ], }, or a letter (an event)
		char previousChar = '.';
		int arrayIndex = 0;
		int yearOffset = 0;

		while (arrayIndex < inData.length())
		{
			char currentChar = inData.charAt(arrayIndex);

			if (currentChar != previousChar || Character.isLetter(currentChar) || currentChar == '[' || currentChar == '{'
					|| currentChar == ']' || currentChar == '}')
			{
				int currentYear = dataSetFirstYear + yearOffset;
				switch (currentChar)
				{
					case '[':

						// Sample starts with pith
						if (!openingCharAlreadySet)
						{
							pith = true;
							sampleFirstYear = currentYear;
							openingCharAlreadySet = true;
							openingChar = '[';
						}

						// Sample has an opening bracket after an event
						else if (Character.isLetter(openingChar))
						{
							FileController.setCorruptedState(true);
							errors.add(new SampleErrorModel("Sample \"" + sampleName
									+ "\" contains more than one start year (opening bracket after event).", currentYear));
						}

						// Sample has multiple opening brackets
						else
						{
							FileController.setCorruptedState(true);
							errors.add(new SampleErrorModel("Sample \"" + sampleName
									+ "\" contains more than one start year (multiple opening brackets).", currentYear));
						}

						break;

					case '{':

						// Sample starts with no pith
						if (!openingCharAlreadySet)
						{
							pith = false;
							sampleFirstYear = currentYear;
							openingCharAlreadySet = true;
							openingChar = '{';
						}

						// Sample has an opening bracket after an event
						else if (Character.isLetter(openingChar))
						{
							FileController.setCorruptedState(true);
							errors.add(new SampleErrorModel("Sample \"" + sampleName
									+ "\" contains more than one start year (opening bracket after event).", currentYear));
						}

						// Sample has multiple opening brackets
						else
						{
							FileController.setCorruptedState(true);
							errors.add(new SampleErrorModel("Sample \"" + sampleName
									+ "\" contains more than one start year (multiple opening brackets).", currentYear));
						}

						break;

					case ']':

						// Sample has a closing bracket before any opening character
						if (!openingCharAlreadySet)
						{
							FileController.setCorruptedState(true);
							errors.add(new SampleErrorModel("Sample \"" + sampleName
									+ "\" contains last year before first year (closing bracket before valid data).", currentYear));
						}

						// Sample ends with bark
						else
						{
							// First closing bracket detected
							if (!closingCharAlreadySet)
							{
								closingCharAlreadySet = true;
							}

							// Sample has multiple closing brackets
							else
							{
								FileController.setCorruptedState(true);
								errors.add(new SampleErrorModel("Sample \"" + sampleName
										+ "\" contains more than one end year (multiple closing brackets).", currentYear));
							}

							bark = true;
							sampleLastYear = currentYear;
							closingChar = ']';
						}

						// Close the last recording at the year before the bracket
						if (previousChar == '|' || Character.isLetter(previousChar))
						{
							recordingTable.closeLastRecording(dataSetFirstYear + yearOffset - 1);
						}

						break;

					case '}':

						// Sample has a closing bracket before any opening character
						if (!openingCharAlreadySet)
						{
							FileController.setCorruptedState(true);
							errors.add(new SampleErrorModel("Sample \"" + sampleName
									+ "\" contains last year before first year (closing bracket before valid data).", currentYear));
						}

						// Sample ends with no bark
						else
						{
							// First closing bracket detected
							if (!closingCharAlreadySet)
							{
								closingCharAlreadySet = true;
							}

							// Sample has multiple closing brackets
							else
							{
								FileController.setCorruptedState(true);
								errors.add(new SampleErrorModel("Sample \"" + sampleName
										+ "\" contains more than one end year (multiple closing brackets).", currentYear));
							}

							bark = false;
							sampleLastYear = currentYear;
							closingChar = '}';
						}

						// Close the last recording at the year before the bracket
						if (previousChar == '|' || Character.isLetter(previousChar))
						{
							recordingTable.closeLastRecording(dataSetFirstYear + yearOffset - 1);
						}

						break;

					case '.':

						// Close the last recording if the previous year was a recording or an event
						if (previousChar == '|' || Character.isLetter(previousChar))
						{
							recordingTable.closeLastRecording(dataSetFirstYear + yearOffset - 1);
						}

						break;

					case '|':
						// Sample starts with a recording year
						if (!openingCharAlreadySet)
						{
							FileController.setCorruptedState(true);
							errors.add(new SampleErrorModel("Sample \"" + sampleName
									+ "\" starts with a recording year (pipe before any opening bracket or event).", currentYear));

							pith = false;
							sampleFirstYear = currentYear;
							openingCharAlreadySet = true;
							openingChar = '{';
						}

						// Start a new recording if the previous year was an opening character or period
						if (previousChar == '.' || previousChar == '{' || previousChar == '[')
						{
							if (previousChar != currentChar)
							{
								recordingTable.addRecording(new FHX2_Recording(dataSetFirstYear + yearOffset));
							}
						}

						// Updates the closing char to pipe in case we never come across a closing bracket or event
						if (!closingCharAlreadySet)
						{
							closingChar = '|';
						}

						break;

					default:

						// Sample starts with an event
						if (!openingCharAlreadySet)
						{
							pith = false;
							sampleFirstYear = currentYear;
							openingCharAlreadySet = true;
							openingChar = currentChar;
						}

						// Updates the closing char to event character in case we never come across a closing bracket
						if (!closingCharAlreadySet)
						{
							sampleLastYear = currentYear;
							closingChar = currentChar;
						}

						// Adds the event and adds a new recording if needed
						eventTable.addEvent(new FHX2_Event(currentChar, dataSetFirstYear + yearOffset));
						if (!recordingTable.isYearInRecordings(currentYear))
						{
							recordingTable.addRecording(new FHX2_Recording(dataSetFirstYear + yearOffset));
						}

						break;
				}
			}
			previousChar = currentChar;
			arrayIndex++;
			yearOffset++;

			// ensures that zero year is excluded from the data
			if (dataSetFirstYear + yearOffset == 0)
			{
				yearOffset++;
			}
		}

		// Close FHRecorder if a composite file is detected (files with samples that end on a pipe are always composite)
		// This is the only check we can run on a sample-by-sample basis
		if (closingChar == '|' && !FileController.isOverrideCompositeWarnings())
			throw new CompositeFileException("One or more series ends with a recording year.");

		// Close FHRecorder if a composite file is detected (files with no header that meet these criteria are always composite)
		/*
		 * if (!IOController.getFile().getOptionalPart().fileHasValidHeader()) { if (openingChar == '{' || Character.isLetter(openingChar)
		 * || Character.isLetter(closingChar)) throw new CompositeFileException(); }
		 */

		// Conclude that the sample is terminated by an event
		if (Character.isLetter(closingChar))
		{
			bark = false;
			closingCharAlreadySet = true;

			recordingTable.closeLastRecording(sampleLastYear);
		}

		// Sample first year and last year were not defined while parsing
		if (!openingCharAlreadySet && !closingCharAlreadySet)
		{
			if (eventTable.getNumOfEvents() == 0)
			{
				sampleFirstYear = dataSetFirstYear;		// maintenance request: missing features #2
				sampleLastYear = dataSetFirstYear;		// they are set to the same year because this sample will be deleted
			}
			else
			{
				FileController.setCorruptedState(true);
				errors.add(new SampleErrorModel("Sample \"" + sampleName
						+ "\" does not have a first or last year (no brackets or events in data).", dataSetFirstYear));

				sampleFirstYear = IOController.getFile().getRequiredPart().getDataSetFirstYear();
				sampleLastYear = IOController.getFile().getRequiredPart().getDataSetLastYear();
			}
		}

		// Sample first year was not defined while parsing
		else if (!openingCharAlreadySet && closingCharAlreadySet)
		{
			FileController.setCorruptedState(true);
			errors.add(new SampleErrorModel("Sample \"" + sampleName + "\" does not have a first year (no opening bracket or event).",
					dataSetFirstYear));
			sampleFirstYear = IOController.getFile().getRequiredPart().getDataSetFirstYear();
		}

		// Sample last year was not defined while parsing
		else if (openingCharAlreadySet && !closingCharAlreadySet)
		{
			FileController.setCorruptedState(true);
			errors.add(new SampleErrorModel("Sample \"" + sampleName + "\" does not have a last year (no closing bracket or event).",
					dataSetFirstYear));
			sampleLastYear = IOController.getFile().getRequiredPart().getDataSetLastYear();
		}

		recordingTable.mergeOverlappingRecordings();
	}

// ********************************************************************************************************************
// Vector ChangeListener Methods
// ********************************************************************************************************************

	/**
	 * Vector ChangeListener utility methods (part of error checking feature)
	 */
	private Vector<ChangeListener> listeners = new Vector<ChangeListener>();

	public synchronized void addChangeListener(ChangeListener l) {

		if (!listeners.contains(l))
			listeners.add(l);
	}

	public synchronized void removeChangeListener(ChangeListener l) {

		listeners.remove(l);
	}

	public ArrayList<SampleErrorModel> getErrors() {

		return errors;
	}
}
