/**************************************************************************************************
 * Fire History Analysis and Exploration System (FHAES), Copyright (C) 2015
 * 
 * Contributors: Cody Calhoun, Anthony Messerschmidt, Seth Westphal, Scott Goble, and Peter Brewer
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
package org.fhaes.FHRecorder.utility;

import org.fhaes.FHRecorder.model.FHX2_Event;
import org.fhaes.FHRecorder.model.FHX2_FileRequiredPart;
import org.fhaes.FHRecorder.model.FHX2_Recording;
import org.fhaes.FHRecorder.model.FHX2_Sample;

/**
 * YearSummary Class. Contains data summarizing what happened in a given year of the data file.
 * 
 * @author Seth Westphal
 */
public class YearSummary {
	
	private int year;
	private int numRecorders = 0;
	private int numSamples = 0;
	private int numDormantSeason = 0;
	private int numEarlyEarlywood = 0;
	private int numMiddleEarlywood = 0;
	private int numLateEarlywood = 0;
	private int numLatewood = 0;
	private int numUndetermined = 0;
	
	/**
	 * Constructor for year summary.
	 * 
	 * @param file the information the object will use to get its data.
	 * @param year the year that this object will be for.
	 */
	public YearSummary(FHX2_FileRequiredPart file, int year) {
		
		this.year = year;
		
		for (FHX2_Sample sample : file.getSampleList())
		{
			if (sample.containsYear(year) && sample.getSampleFirstYear() != year && sample.getSampleLastYear() != year)
				numSamples++;
				
			for (FHX2_Recording range : sample.getRecordings())
				if (range.containsYear(year))
					numRecorders++;
					
			for (FHX2_Event event : sample.getEvents())
			{
				if (event.getEventYear() == year)
				{
					numRecorders++;
					for (FHX2_Recording range : sample.getRecordings())
						if (range.containsYear(year))
							numRecorders--; // Already counted
							
					switch (event.getEventType())
					{
						case 'D':
						case 'd':
							numDormantSeason++;
							break;
						case 'E':
						case 'e':
							numEarlyEarlywood++;
							break;
						case 'M':
						case 'm':
							numMiddleEarlywood++;
							break;
						case 'L':
						case 'l':
							numLateEarlywood++;
							break;
						case 'A':
						case 'a':
							numLatewood++;
							break;
						case 'U':
						case 'u':
							numUndetermined++;
							break;
					}
				}
			}
		}
	}
	
	/**
	 * Constructor that allows the specification of values directly.
	 * 
	 * @param year the year.
	 * @param numRecorders the number of recorders.
	 * @param numSamples the number of samples.
	 * @param numDormantSeason the number of dormant events.
	 * @param numEarlyEarlywood the number of early earlywood events.
	 * @param numMiddleEarlywood the number of middle earlywood events.
	 * @param numLateEarlywood the number of late earlywood events.
	 * @param numLatewood the number of latewood events.
	 * @param numUndetermined the number of undetermined events.
	 */
	public YearSummary(int year, int numRecorders, int numSamples, int numDormantSeason, int numEarlyEarlywood, int numMiddleEarlywood,
			int numLateEarlywood, int numLatewood, int numUndetermined) {
			
		this.year = year;
		this.numRecorders = numRecorders;
		this.numSamples = numSamples;
		this.numDormantSeason = numDormantSeason;
		this.numEarlyEarlywood = numEarlyEarlywood;
		this.numMiddleEarlywood = numMiddleEarlywood;
		this.numLateEarlywood = numLateEarlywood;
		this.numLatewood = numLatewood;
		this.numUndetermined = numUndetermined;
	}
	
	/**
	 * Calculates the percentage of samples that are recording that include events
	 * 
	 * @return 0 if there are 0 events, otherwise returns the calculated percentage of recording samples that have events.
	 */
	public float getPercentScarred() {
		
		if (getNumEvents() == 0)
		{
			return 0;
		}
		return 100.0f * getNumEvents() / numRecorders;
	}
	
	/**
	 * Gets the total number of events.
	 * 
	 * @return total number of events.
	 */
	public int getNumEvents() {
		
		return numDormantSeason + numEarlyEarlywood + numMiddleEarlywood + numLateEarlywood + numLatewood + numUndetermined;
	}
	
	/**
	 * Gets the number of samples that are active but not recording.
	 * 
	 * @return the number of samples not recording.
	 */
	public int getNumBlank() {
		
		return numSamples - numRecorders;
	}
	
	public int getYear() {
		
		return year;
	}
	
	/**
	 * WARNING - the value of recording years does not match that of FHFileReader. There are special cases not currently handled by
	 * FHRecorder
	 * 
	 * @return
	 */
	public int getNumRecorders() {
		
		return numRecorders;
	}
	
	public int getNumSamples() {
		
		return numSamples;
	}
	
	public int getNumDormantSeason() {
		
		return numDormantSeason;
	}
	
	public int getNumEarlyEarlywood() {
		
		return numEarlyEarlywood;
	}
	
	public int getNumMiddleEarlywood() {
		
		return numMiddleEarlywood;
	}
	
	public int getNumLateEarlywood() {
		
		return numLateEarlywood;
	}
	
	public int getNumLatewood() {
		
		return numLatewood;
	}
	
	public int getNumUndetermined() {
		
		return numUndetermined;
	}
}
