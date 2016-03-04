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
package org.fhaes.fhrecorder.controller;

import org.fhaes.fhrecorder.model.FHX2_Event;
import org.fhaes.fhrecorder.model.FHX2_Sample;

/**
 * EventController Class. This file contains the event management functions of the service layer.
 * 
 * @author Josh Brogan, Jake Lokkesmoe, Chinmay Shah, Scott Goble
 */
public class EventController {
	
	/**
	 * Adds a FHX2Event to the currently selected FHX2Sample.
	 */
	public static void addNewEvent() {
	
		if (SampleController.getSelectedSampleIndex() > -1)
		{
			FHX2_Event newEvent = new FHX2_Event();
			FHX2_Sample selectedSample = IOController.getFile().getRequiredPart().getSample(SampleController.getSelectedSampleIndex());
			
			/*
			 * if (selectedSample.getNumOfEvents() > 0) { FHX2_Event prevEvent = selectedSample.getEvent(selectedSample.getNumOfEvents() -
			 * 1); int year = selectedSample.getNextAvailableEventYear(prevEvent.getEventYear() + 1); if (year != 0) newEvent = new
			 * FHX2_Event(prevEvent.getEventType(), year); }
			 */
			/*
			 * else { newEvent = new FHX2_Event(null);
			 * 
			 * int year = selectedSample.getNextAvailableEventYear(); if (year != 0) newEvent = new FHX2_Event('U', year); }
			 */
			
			// if (newEvent != null)
			selectedSample.addEvent(newEvent);
		}
		
		SampleController.setSelectedSampleIndex(SampleController.getSelectedSampleIndex());
	}
	
	/**
	 * Removes an event from the currently selected sample.
	 * 
	 * @param index
	 */
	public static void deleteEvent(int index) {
	
		FHX2_Sample selectedSample = IOController.getFile().getRequiredPart().getSample(SampleController.getSelectedSampleIndex());
		selectedSample.removeEvent(index);
	}
}
