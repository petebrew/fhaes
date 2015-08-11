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
package org.fhaes.fhxrecorder.controller;

import java.util.ArrayList;
import java.util.Collections;

import org.fhaes.fhxrecorder.compare.CompareEventYears;
import org.fhaes.fhxrecorder.model.FHX2_Event;
import org.fhaes.fhxrecorder.model.FHX2_Recording;
import org.fhaes.fhxrecorder.model.FHX2_Sample;

/**
 * RecordingController Class. This file contains the recording management functions of the service layer.
 * 
 * @author Josh Brogan, Jake Lokkesmoe, Chinmay Shah, Scott Goble
 */
public class RecordingController {
	
	/**
	 * Adds a new recording to the currently selected sample.
	 */
	public static void addNewRecording() {
		
		if (SampleController.getSelectedSampleIndex() > -1)
		{
			FHX2_Recording newRecording = null;
			FHX2_Sample selectedSample = IOController.getFile().getRequiredPart().getSample(SampleController.getSelectedSampleIndex());
			if (selectedSample.getNumOfRecordings() > 0)
			{
				FHX2_Recording prevRecording = selectedSample.getRecording(selectedSample.getNumOfRecordings() - 1);
				int year = selectedSample.getNextAvailableRecordingYear(prevRecording.getEndYear() + 1);
				if (year != 0)
				{
					newRecording = new FHX2_Recording(year, year);
				}
			}
			else
			{
				int firstyear = selectedSample.getNextAvailableRecordingYear();
				int lastyear = selectedSample.getSampleLastYear() - 1;
				if (firstyear != 0 && lastyear != 0)
				{
					newRecording = new FHX2_Recording(firstyear, lastyear);
				}
			}
			if (newRecording != null)
			{
				selectedSample.addRecording(newRecording);
			}
		}
		SampleController.setSelectedSampleIndex(SampleController.getSelectedSampleIndex());
	}
	
	/**
	 * Add the a recording to the selected sample starting in the first event year and ending in the last year of the sample.
	 * 
	 * @param recording
	 */
	public static void addRecordingFromFirstEventToEnd() {
		
		if (SampleController.getSelectedSampleIndex() > -1)
		{
			FHX2_Recording newRecording = new FHX2_Recording();
			FHX2_Sample selectedSample = IOController.getFile().getRequiredPart().getSample(SampleController.getSelectedSampleIndex());
			
			@SuppressWarnings("unchecked")
			ArrayList<FHX2_Event> events = (ArrayList<FHX2_Event>) selectedSample.getEvents().clone();
			
			if (events.size() == 0)
				return;
				
			Collections.sort(events, new CompareEventYears());
			FHX2_Event event = events.get(0);
			
			int firstyear = event.getEventYear();
			int lastyear = selectedSample.getSampleLastYear();
			
			// Handle year 0
			if (firstyear == 0)
				firstyear++;
			if (lastyear == 0)
				lastyear--;
				
			newRecording.setStartYear(firstyear);
			newRecording.setEndYear(lastyear);
			
			selectedSample.addRecording(newRecording);
			
		}
		SampleController.setSelectedSampleIndex(SampleController.getSelectedSampleIndex());
	}
	
	/**
	 * Add the a recording to the selected sample starting in the first event year and ending in the last year of the sample.
	 * 
	 * @param recording
	 */
	public static void addRecordingFromBeginningToEnd() {
		
		if (SampleController.getSelectedSampleIndex() > -1)
		{
			FHX2_Recording newRecording = new FHX2_Recording();
			FHX2_Sample selectedSample = IOController.getFile().getRequiredPart().getSample(SampleController.getSelectedSampleIndex());
			
			int firstyear = selectedSample.getSampleFirstYear();
			int lastyear = selectedSample.getSampleLastYear();
			
			// Handle year 0
			if (firstyear == 0)
				firstyear++;
			if (lastyear == 0)
				lastyear--;
				
			newRecording.setStartYear(firstyear);
			newRecording.setEndYear(lastyear);
			
			selectedSample.addRecording(newRecording);
			
		}
		SampleController.setSelectedSampleIndex(SampleController.getSelectedSampleIndex());
	}
	
	/**
	 * Removes a recording from the currently selected sample.
	 * 
	 * @param index
	 */
	public static void deleteRecording(int index) {
		
		FHX2_Sample selectedSample = IOController.getFile().getRequiredPart().getSample(SampleController.getSelectedSampleIndex());
		selectedSample.removeRecording(index);
	}
	
	/**
	 * Delete all recordings from the currently selected sample but don't delete any events.
	 */
	public static void deleteAllRecordingsButNotEvents() {
		
		FHX2_Sample selectedSample = IOController.getFile().getRequiredPart().getSample(SampleController.getSelectedSampleIndex());
		while (selectedSample.getNumOfRecordings() > 0)
		{
			selectedSample.removeRecording(0, true);
		}
	}
	
}
