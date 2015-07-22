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
 *     2) The method SortEventsOfAllSamples() was added in order to address
 *     usability improvement #1 on our original list of maintenance requests.
 ******************************************************************************/
package org.fhaes.fhrecorder.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.fhaes.fhrecorder.controller.FileController;
import org.fhaes.fhrecorder.utility.ErrorTrackerInterface;
import org.fhaes.fhrecorder.utility.SampleErrorModel;

/**
 * FHX2_FileRequiredPart Class. This class contains the required part of the FHX file data. This includes samples, events, and recording
 * years as displayed in the data tab.
 * 
 * @author Alex Beatty, Clayton Bodendein, Kyle Hartmann, Scott Goble
 */
public class FHX2_FileRequiredPart implements Serializable, ChangeListener, ErrorTrackerInterface {

	private static final long serialVersionUID = 1L;

	private List<FHX2_Sample> sampleList;
	private int idLength; // idlength is the number of lines used to display the sample name
	private int dataSetFirstYear;
	private int dataSetLastYear;

	/**
	 * Default Constructor for FHX2_FileRequiredPart. FHX2 Format enforces minimum sample name to be three characters, list to hold the
	 * samples is created and the datasetFirstyear and datasetLastyear is set.
	 */
	public FHX2_FileRequiredPart() {

		idLength = 3;
		dataSetFirstYear = 0;
		dataSetLastYear = 0;
		sampleList = new LinkedList<FHX2_Sample>();
	}

	/**
	 * Returns the length of the longest unique identifier for any sample in the current FHX2 file to leave number of lines for displaying
	 * the sample name in the file.
	 * 
	 * @return idLength
	 */
	public int getIDLength() {

		return idLength;
	}

	/**
	 * Sets the length of the unique sample identifier. Ensuring that 3 lines are left for the sample name and updating the idlength for
	 * larger idlength.
	 * 
	 * @param idLength
	 */
	public void setIDLength(int idLength) {

		if (idLength >= 3)
			this.idLength = idLength;
	}

	/**
	 * Returns the year with the earliest instance of recorded data out of all sample in the current FHX2 file.
	 * 
	 * @return datasetFirstYear
	 */
	public int getDataSetFirstYear() {

		return dataSetFirstYear;
	}

	/**
	 * Sets the first year of the data set to value of the input parameter.
	 * 
	 * @param inFirstYear
	 */
	public void setDataSetFirstYear(int inFirstYear) {

		this.dataSetFirstYear = inFirstYear;
	}

	/**
	 * Returns the year with the latest instance of recorded data out of all samples in the current FHX2 file.
	 * 
	 * @return datasetLastYear
	 */
	public int getDataSetLastYear() {

		return dataSetLastYear;
	}

	/**
	 * Sets the last year of the data set to value of the input parameter.
	 * 
	 * @param inFirstYear
	 */
	public void setDataSetLastYear(int inLastYear) {

		this.dataSetLastYear = inLastYear;
	}

	/**
	 * Gets a list of all samples in the current FHX2 file with a type as the FHX2_Sample class.
	 * 
	 * @return sampleList
	 */
	public List<FHX2_Sample> getSampleList() {

		return sampleList;
	}

	/**
	 * Adds a new sample to the data.
	 * 
	 * @param inName (name of sample)
	 * @param inSampleFirstYear (first year of sample)
	 * @param inSampleLastYear (last year of sample)
	 * @param inPith (whether or not there is pith recorded at the first year)
	 * @param inBark (whether or not there is back recorded at the last year)
	 */
	public void addSample(String inName, int inSampleFirstYear, int inSampleLastYear, boolean inPith, boolean inBark) {

		FHX2_Sample sample = new FHX2_Sample(inName, inSampleFirstYear, inSampleLastYear, inPith, inBark);
		addSample(sample);
	}

	/**
	 * Adds a new sample to the data. This method takes an FHX2_Sample as the input.
	 * 
	 * @param inSample
	 */
	public void addSample(FHX2_Sample inSample) {

		inSample.addChangeListener(this);
		sampleList.add(inSample);
		calculateIDLength();
		calculateFirstYear();
		calculateLastYear();
		fireSampleEvent();
	}

	/**
	 * Deletes a sample from the data.
	 * 
	 * @param index (the index of the sample to delete)
	 */
	public void deleteSample(int index) {

		sampleList.remove(index);
		fireSampleEvent();
	}

	/**
	 * Adds an event to a specific sample.
	 * 
	 * @param index
	 * @param inEvent
	 */
	public void addEventToSample(int index, FHX2_Event inEvent) {

		sampleList.get(index).addEvent(inEvent);
	}

	/**
	 * Gets the sample at the specified index of the list.
	 * 
	 * @param index
	 * @return null if the input index is out of bounds, otherwise the sample
	 */
	public FHX2_Sample getSample(int index) {

		if (sampleList == null || sampleList.size() == 0 || index == -1)
			return null;
		return sampleList.get(index);
	}

	/**
	 * Returns the number of samples in the current FHX2 file.
	 * 
	 * @return number of samples
	 */
	public int getNumSamples() {

		return sampleList.size();
	}

	/**
	 * Gets an event of a specific sample.
	 * 
	 * @param sampleIndex (the index of the sample to get)
	 * @param eventIndex (the index of the event of the sample to get)
	 * @return the event
	 */
	public FHX2_Event getEvent(int sampleIndex, int eventIndex) {

		return sampleList.get(sampleIndex).getEvent(eventIndex);
	}

	/**
	 * Calculates the appropriate length for the unique sample identifier.
	 */
	public void calculateIDLength() {

		idLength = 3;
		for (int i = 0; i < sampleList.size(); i++)
			if (idLength < sampleList.get(i).getSampleName().length())
				idLength = sampleList.get(i).getSampleName().length();
	}

	/**
	 * Calculates the starting year or the dataset's first year for given sample.
	 */
	public void calculateFirstYear() {

		if (sampleList.size() > 0)
		{
			int newFirstYear = FileController.CURRENT_YEAR;
			for (int i = 0; i < sampleList.size(); i++)
				if (newFirstYear > sampleList.get(i).getSampleFirstYear())
					newFirstYear = sampleList.get(i).getSampleFirstYear();
			dataSetFirstYear = newFirstYear;
		}
		FileController.checkIfYearLowerBoundaryIsWithinFHX2Reqs();
	}

	/**
	 * Calculates the ending year or the dataset's last year for a given sample.
	 */
	public void calculateLastYear() {

		if (sampleList.size() > 0)
		{
			int newLastYear = FileController.EARLIEST_ALLOWED_YEAR;
			for (int i = 0; i < sampleList.size(); i++)
				if (newLastYear < sampleList.get(i).getSampleLastYear())
					newLastYear = sampleList.get(i).getSampleLastYear();
			dataSetLastYear = newLastYear;
		}
		FileController.checkIfYearUpperBoundaryIsWithinFHX2Reqs();
	}

	/**
	 * Handles when the state of an event is changed.
	 */
	public void stateChanged(ChangeEvent e) {

		fireSampleEvent();
		FileController.setIsChangedSinceLastSave(true);
	}

	/**
	 * Retrieves a complete list of the errors detected in the file during load.
	 */
	@Override
	public ArrayList<SampleErrorModel> getErrors() {

		ArrayList<SampleErrorModel> errors = new ArrayList<SampleErrorModel>();
		for (FHX2_Sample sample : sampleList)
			errors.addAll(sample.getErrors());
		return errors;
	}

	/**
	 * The following methods are for the vector change listener.
	 */
	private Vector<ChangeListener> listeners = new Vector<ChangeListener>();

	public synchronized void addChangeListener(ChangeListener l) {

		if (!listeners.contains(l))
			listeners.add(l);
	}

	public synchronized void removeChangeListener(ChangeListener l) {

		listeners.remove(l);
	}

	@SuppressWarnings("unchecked")
	private void fireSampleEvent() {

		Vector<ChangeListener> l; // alert all listeners
		synchronized (this)
		{
			l = (Vector<ChangeListener>) listeners.clone();
		}

		int size = l.size();
		if (size == 0)
			return;

		for (int i = 0; i < size; i++)
		{
			ChangeListener listener = (ChangeListener) l.elementAt(i);
			ChangeEvent e = new ChangeEvent(this);
			listener.stateChanged(e);
		}
	}
}
