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

import java.util.Collections;

import org.fhaes.fhrecorder.model.FHX2_FileRequiredPart;
import org.fhaes.fhrecorder.model.FHX2_Sample;
import org.fhaes.fhrecorder.view.MetaDataPanel;
import org.fhaes.util.SharedConstants;

/**
 * SampleController Class. This file contains the sample management functions of the service layer.
 * 
 * @author Josh Brogan, Jake Lokkesmoe, Chinmay Shah, Scott Goble
 */
public class SampleController {
	
	// Declare public constants
	public static final int INDEX_REPRESENTING_NO_SAMPLES = -1;
	
	// Declare local variables
	private static int selectedSampleIndex;
	
	/**
	 * Gets the index of the selected sample.
	 * 
	 * @return the selected sample's index
	 */
	public static int getSelectedSampleIndex() {
		
		return selectedSampleIndex;
	}
	
	/**
	 * Sets the index of the selected sample based on the input parameter.
	 * 
	 * @param i
	 */
	public static void setSelectedSampleIndex(int i) {
		
		FHX2_FileRequiredPart reqTemp = IOController.getFile().getRequiredPart();
		int numSamples = reqTemp.getNumSamples();
		
		if (numSamples == 0)
		{
			selectedSampleIndex = INDEX_REPRESENTING_NO_SAMPLES;
		}
		else if (i >= 0 && i < numSamples)
		{
			selectedSampleIndex = i;
			reqTemp.getSample(selectedSampleIndex).sortEvents();
		}
		else if (i >= numSamples)
		{
			selectedSampleIndex = (numSamples - 1);
			reqTemp.getSample(selectedSampleIndex).sortEvents();
		}
		else
		{
			selectedSampleIndex = 0;
		}
	}
	
	/**
	 * Gets the year of the first event in the sample. This will return the EARLIEST_ALLOWED_YEAR if there are no events.
	 * 
	 * @return
	 */
	public static int getYearOfFirstEventInSelectedSample() {
		
		FHX2_Sample currentSample = IOController.getFile().getRequiredPart().getSample(getSelectedSampleIndex());
		
		if (currentSample.getNumOfEvents() != 0)
		{
			int minIndex = 0;
			for (int i = 0; i < currentSample.getNumOfEvents(); i++)
			{
				Integer eventYear = currentSample.getEvent(i).getEventYear();
				if (eventYear == null)
					return SharedConstants.EARLIEST_ALLOWED_YEAR - 1;
				
				if (eventYear < currentSample.getEvent(minIndex).getEventYear())
					minIndex = i;
			}
			return currentSample.getEvent(minIndex).getEventYear();
		}
		else
		{
			// Return a value that is outside of the valid year range so that there is no erroneous behavior
			return SharedConstants.EARLIEST_ALLOWED_YEAR - 1;
		}
	}
	
	/**
	 * Gets the year of the last event in the sample. This will return the CURRENT_YEAR if there are no events.
	 * 
	 * @return
	 */
	public static int getYearOfLastEventInSelectedSample() {
		
		FHX2_Sample currentSample = IOController.getFile().getRequiredPart().getSample(getSelectedSampleIndex());
		
		if (currentSample.getNumOfEvents() != 0)
		{
			int maxIndex = 0;
			for (int i = 0; i < currentSample.getNumOfEvents(); i++)
			{
				Integer eventYear = currentSample.getEvent(i).getEventYear();
				if (eventYear == null)
					return SharedConstants.CURRENT_YEAR + 1;
				
				if (eventYear > currentSample.getEvent(maxIndex).getEventYear())
					maxIndex = i;
			}
			
			return currentSample.getEvent(maxIndex).getEventYear();
		}
		else
		{
			// Return a value that is outside of the valid year range so that there is no erroneous behavior
			return SharedConstants.CURRENT_YEAR + 1;
		}
	}
	
	/**
	 * Sets the sample's bark status to true or false as determined by the input parameter.
	 * 
	 * @param inBark
	 */
	public static void setSampleBark(boolean inBark) {
		
		IOController.getFile().getRequiredPart().getSample(getSelectedSampleIndex()).setBark(inBark);
	}
	
	/**
	 * Sets the sample's pith status to true or false as determined by the input parameter.
	 * 
	 * @param inPith
	 */
	public static void setSamplePith(boolean inPith) {
		
		IOController.getFile().getRequiredPart().getSample(getSelectedSampleIndex()).setPith(inPith);
	}
	
	/**
	 * Changes the name of the sample.
	 * 
	 * @param inName
	 */
	public static void changeSampleName(String inName) {
		
		IOController.getFile().getRequiredPart().getSample(getSelectedSampleIndex()).setSampleName(inName);
		IOController.getFile().getRequiredPart().calculateIDLength();
	}
	
	/**
	 * Adjusts the sample's first year to the value of the input parameter.
	 * 
	 * @param inFirstYear
	 */
	public static void changeSampleFirstYear(int inFirstYear) {
		
		IOController.getFile().getRequiredPart().getSample(getSelectedSampleIndex()).setSampleFirstYear(inFirstYear);
		IOController.getFile().getRequiredPart().calculateFirstYear();
	}
	
	/**
	 * Adjusts the sample's last year to the value of the input parameter.
	 * 
	 * @param inLastYear
	 */
	public static void changeSampleLastYear(int inLastYear) {
		
		IOController.getFile().getRequiredPart().getSample(getSelectedSampleIndex()).setSampleLastYear(inLastYear);
		IOController.getFile().getRequiredPart().calculateLastYear();
	}
	
	/**
	 * Updates the closing characters of all the samples.
	 */
	public static void updateAllSampleOpeningAndClosingChars() {
		
		for (int i = 0; i < IOController.getFile().getRequiredPart().getNumSamples(); i++)
		{
			FHX2_Sample currentSample = IOController.getFile().getRequiredPart().getSample(i);
			if (currentSample.getNumOfEvents() != 0)
			{
				IOController.getFile().getRequiredPart().getSample(getSelectedSampleIndex()).updateOpeningChar();
				IOController.getFile().getRequiredPart().getSample(getSelectedSampleIndex()).updateClosingChar();
			}
		}
	}
	
	/**
	 * Saves a new sample to the FHX file.
	 * 
	 * @param inSample
	 */
	public static void saveNewSample(FHX2_Sample inSample) {
		
		IOController.getFile().getRequiredPart().addSample(inSample);
		setSelectedSampleIndex(IOController.getFile().getRequiredPart().getNumSamples());
		
		MetaDataPanel.updateNumSamplesField();
		
		IOController.getFile().getRequiredPart().calculateFirstYear();
		IOController.getFile().getRequiredPart().calculateLastYear();
	}
	
	/**
	 * Deletes a sample from the data.
	 */
	public static void deleteSample() {
		
		FHX2_FileRequiredPart temp = IOController.getFile().getRequiredPart();
		if (getSelectedSampleIndex() > -1)
		{
			temp.deleteSample(getSelectedSampleIndex());
			setSelectedSampleIndex(getSelectedSampleIndex() - 1);
			MetaDataPanel.updateNumSamplesField();
			
			IOController.getFile().getRequiredPart().calculateFirstYear();
			IOController.getFile().getRequiredPart().calculateLastYear();
			FileController.setIsChangedSinceLastSave(true);
		}
	}
	
	/**
	 * Deletes the sample at the specified index.
	 * 
	 * @param index
	 */
	public static void deleteSample(int index) {
		
		FHX2_FileRequiredPart temp = IOController.getFile().getRequiredPart();
		temp.deleteSample(index);
		setSelectedSampleIndex(index - 1);
		MetaDataPanel.updateNumSamplesField();
		
		IOController.getFile().getRequiredPart().calculateFirstYear();
		IOController.getFile().getRequiredPart().calculateLastYear();
	}
	
	/**
	 * Checks if a sample contains any events. (Added as part of a maintenance request)
	 * 
	 * @return false if there are no events, otherwise true
	 */
	public static boolean selectedSampleHasEvents() {
		
		FHX2_Sample currentSample = IOController.getFile().getRequiredPart().getSample(getSelectedSampleIndex());
		return currentSample.getNumOfEvents() != 0;
	}
	
	/**
	 * Performs a swap action on the two samples at the input indices.
	 * 
	 * @param moveindex
	 * @param replaceindex
	 */
	public static void swapSamples(int moveindex, int replaceindex) {
		
		FHX2_FileRequiredPart temp = IOController.getFile().getRequiredPart();
		
		try
		{
			Collections.swap(temp.getSampleList(), moveindex, replaceindex);
		}
		catch (IndexOutOfBoundsException e)
		{
			e.printStackTrace();
		}
		
		setSelectedSampleIndex(getSelectedSampleIndex());
	}
}
