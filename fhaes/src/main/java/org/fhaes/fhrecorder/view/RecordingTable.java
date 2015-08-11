/**************************************************************************************************
 * Fire History Analysis and Exploration System (FHAES), Copyright (C) 2015
 * 
 * Contributors: Brendan Compton, Dylan Jones, Alex Richter, Chris Wald, and Peter Brewer
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
package org.fhaes.fhrecorder.view;

import java.util.ArrayList;
import java.util.Collections;

import javax.swing.DefaultCellEditor;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

import org.fhaes.fhrecorder.compare.CompareRecordings;
import org.fhaes.fhrecorder.controller.FileController;
import org.fhaes.fhrecorder.model.FHX2_Recording;
import org.fhaes.fhrecorder.model.FHX2_Sample;

/**
 * RecordingTable Class. A class that is a table for holding the information regarding the recording years.
 * 
 * @author Brendan Compton, Dylan Jones, and Alex Richter
 */
public class RecordingTable extends JTable {
	
	private static final long serialVersionUID = 1L;
	private static final int ROW_HEIGHT = 25;
	
	private ArrayList<FHX2_Recording> recordings;
	private FHX2_Sample sample;
	
	/**
	 * TODO
	 * 
	 * @param s
	 */
	public RecordingTable(FHX2_Sample s) {
		
		recordings = new ArrayList<FHX2_Recording>();
		sample = s;
		getTableHeader().setReorderingAllowed(false);
		
		// We need to edit the cells
		setCellSelectionEnabled(true);
		// Set the row height so we can actually see the info
		setRowHeight(ROW_HEIGHT);
		
		// Set up the columns
		DefaultTableModel tableModel = (DefaultTableModel) getModel();
		tableModel.addColumn("Recording Start Year");
		tableModel.addColumn("Recording End Year");
		
		// Set up the text fields
		TableColumn startedRecordingColumn = getColumnModel().getColumn(0);
		startedRecordingColumn.setCellEditor(new DefaultCellEditor(new JTextField()));
		TableColumn stoppedRecordingColumn = getColumnModel().getColumn(1);
		stoppedRecordingColumn.setCellEditor(new DefaultCellEditor(new JTextField()));
	}
	
	/**
	 * Gets a recording from the table based on the given index.
	 * 
	 * @param index
	 * @return the recording, if it exists
	 */
	public FHX2_Recording getRecording(int index) {
		
		if (index < recordings.size() && index > -1)
			return recordings.get(index);
		return null;
	}
	
	/**
	 * Returns the ArrayList of FHX2 Recordings.
	 * 
	 * @return the ArrayList of recordings
	 */
	public ArrayList<FHX2_Recording> getRecordings() {
		
		return recordings;
	}
	
	/**
	 * Returns the total number of recordings in the table.
	 * 
	 * @return the number of recordings in the table
	 */
	public int getNumOfRecordings() {
		
		return recordings.size();
	}
	
	/**
	 * Returns the maximum number of events that can be logically present in the table.
	 * 
	 * @return the max number of events
	 */
	public int getMaxNumOfEvents() {
		
		if (recordings.size() <= 0)
			return 0;
		else
		{
			int maxNumOfEvents = 0;
			for (FHX2_Recording recording : recordings)
				maxNumOfEvents += recording.getNumOfYears();
				
			return maxNumOfEvents;
		}
	}
	
	/**
	 * Adds a new recording to the table.
	 */
	public void addNewRecording() {
		
		recordings.add(new FHX2_Recording());
		redrawTable();
	}
	
	/**
	 * Adds an existing recording to the table.
	 * 
	 * @param recording
	 */
	public void addRecording(FHX2_Recording recording) {
		
		recordings.add(recording);
		sortRecordings();
		redrawTable();
	}
	
	/**
	 * Add a recording for the whole sample
	 */
	public void addRecordingForWholeSample() {
		
		FHX2_Recording rec = new FHX2_Recording();
		rec.setStartYear(sample.getSampleFirstYear());
		rec.setEndYear(sample.getSampleLastYear());
		recordings.add(rec);
		sortRecordings();
		redrawTable();
		
	}
	
	/**
	 * Removes a recording from the table.
	 * 
	 * @param index
	 */
	public void removeRecording(int index) {
		
		if (index < recordings.size() && index > -1)
			recordings.remove(index);
		redrawTable();
	}
	
	/**
	 * Removes all recordings from the table.
	 */
	public void deleteAllRecordings() {
		
		recordings.clear();
		redrawTable();
	}
	
	/**
	 * Closes any recordings that are still open.
	 * 
	 * @param eventLastYear The last year of the event being close
	 */
	public void closeLastRecording(int lastYear) {
		
		int indexOfPreviousRecording = findUnclosedRecording();
		if (indexOfPreviousRecording >= 0)
		{
			FHX2_Recording previousRecording = recordings.get(indexOfPreviousRecording);
			previousRecording.setEndYear(lastYear);
		}
		redrawTable();
	}
	
	/**
	 * Finds any recording event with the default last year.
	 * 
	 * @return The index of the first event with a default last year.
	 */
	public int findUnclosedRecording() {
		
		// This should always be the latest recording
		int index = recordings.size() - 1;
		while (recordings.size() > 0 && index >= 0)
		{
			FHX2_Recording tempRecording = recordings.get(index);
			if (tempRecording.getEndYear() == FileController.CURRENT_YEAR)
				return index;
			index--;
		}
		return index;
	}
	
	/**
	 * Ensures that cells are always in an editable state.
	 */
	@Override
	public boolean isCellEditable(int row, int col) {
		
		return true;
	}
	
	/**
	 * Checks to see if the specified year is within any recording ranges.
	 * 
	 * @param year
	 * @return true if it is within a recording range, false otherwise
	 */
	public boolean isYearInRecordings(int year) {
		
		for (FHX2_Recording recording : recordings)
			if (recording.containsYear(year))
				return true;
		return false;
	}
	
	/**
	 * Merges any overlapping recordings, including those that are directly next to the start or end year of a recording.
	 */
	public void mergeOverlappingRecordings() {
		
		// Must be more than one to do anything
		if (recordings.size() > 1)
		{
			sortRecordings();
			for (int i = recordings.size() - 1; i >= 0; i--)
			{
				for (int j = recordings.size() - 1; j >= 0; j--)
				{
					// Don't want to merge the same recording
					if (i != j && i < recordings.size() && j < recordings.size() - 1)
					{
						if (recordingsOverlap(recordings.get(i), recordings.get(j)))
						{
							// Merge them
							int startYear = recordings.get(i).getStartYear();
							if (recordings.get(j).getStartYear() < startYear)
							{
								startYear = recordings.get(j).getStartYear();
							}
							int endYear = recordings.get(i).getEndYear();
							if (recordings.get(j).getEndYear() > endYear)
							{
								endYear = recordings.get(j).getEndYear();
							}
							
							recordings.remove(i);
							recordings.remove(j);
							recordings.add(new FHX2_Recording(startYear, endYear));
						}
					}
				}
			}
			sortRecordings();
			redrawTable();
		}
	}
	
	/**
	 * Moves all recordings to fit inside the range given.
	 * 
	 * @param startYear
	 * @param endYear
	 */
	public void moveRecordingsIntoRange(int startYear, int endYear) {
		
		for (FHX2_Recording recording : recordings)
		{
			int recStartYear = recording.getStartYear();
			int recEndYear = recording.getEndYear();
			if (recStartYear <= startYear)
			{
				if (startYear != -1)
				{
					recording.setStartYear(startYear);
				}
				else
				{
					recording.setStartYear(1);
				}
			}
			if (recEndYear <= startYear)
			{
				if (startYear != -1)
				{
					recording.setEndYear(startYear);
				}
				else
				{
					recording.setEndYear(1);
				}
			}
			if (recStartYear >= endYear)
			{
				if (endYear != 1)
				{
					recording.setStartYear(endYear);
				}
				else
				{
					recording.setStartYear(-1);
				}
			}
			if (recEndYear >= endYear)
			{
				if (endYear != 1)
				{
					recording.setEndYear(endYear);
				}
				else
				{
					recording.setEndYear(-1);
				}
			}
		}
		mergeOverlappingRecordings();
		redrawTable();
	}
	
	/**
	 * Returns whether or not certain recordings over lap each other.
	 * 
	 * @param rec1
	 * @param rec2
	 * @return true if an overlap has been detected, false otherwise
	 */
	private boolean recordingsOverlap(FHX2_Recording rec1, FHX2_Recording rec2) {
		
		return (rec1.getStartYear() <= (rec2.getEndYear() + 1) && rec1.getEndYear() >= (rec2.getStartYear() - 1));
	}
	
	/**
	 * Redraws the table after changes were made.
	 */
	public void redrawTable() {
		
		// First remove all the rows
		DefaultTableModel tableModel = (DefaultTableModel) getModel();
		int rowCount = tableModel.getRowCount();
		for (int i = rowCount - 1; i >= 0; i--)
			tableModel.removeRow(i);
			
		// Add the recordings back in
		for (int i = 0; i < recordings.size(); i++)
		{
			Object[] row = new Object[tableModel.getColumnCount()];
			row[0] = ((Integer) recordings.get(i).getStartYear()).toString();
			row[1] = ((Integer) recordings.get(i).getEndYear()).toString();
			tableModel.addRow(row);
		}
		revalidate();
		repaint();
	}
	
	/**
	 * Sets a value at a certain cell location.
	 */
	@Override
	public void setValueAt(final Object value, final int r, final int c) {
		
		try
		{
			int col = super.convertColumnIndexToModel(c);
			int row = super.convertRowIndexToModel(r);
			if (col == 0)
			{
				int startYear = Integer.parseInt(value.toString());
				if (startYear <= sample.getSampleFirstYear())
				{
					startYear = sample.getSampleFirstYear();
					if (startYear == 0)
					{
						startYear = 1;
					}
				}
				else if (startYear >= sample.getSampleLastYear())
				{
					startYear = sample.getSampleLastYear();
					if (startYear == 0)
					{
						startYear = -1;
					}
				}
				else if (startYear == 0)
				{
					startYear = 1;
				}
				
				if (startYear > recordings.get(row).getEndYear())
				{
					startYear = recordings.get(row).getStartYear();
				}
				recordings.get(row).setStartYear(startYear);
				super.setValueAt(Integer.toString(startYear), r, c);
			}
			else if (col == 1)
			{
				int endYear = Integer.parseInt(value.toString());
				if (endYear <= sample.getSampleFirstYear())
				{
					endYear = sample.getSampleFirstYear();
					if (endYear == 0)
					{
						endYear = 1;
					}
				}
				else if (endYear >= sample.getSampleLastYear())
				{
					endYear = sample.getSampleLastYear();
					if (endYear == 0)
					{
						endYear = -1;
					}
				}
				else if (endYear == 0)
				{
					endYear = 1;
				}
				
				if (endYear < recordings.get(row).getStartYear())
				{
					endYear = recordings.get(row).getStartYear();
				}
				recordings.get(row).setEndYear(endYear);
				super.setValueAt(Integer.toString(endYear), r, c);
			}
		}
		catch (NumberFormatException e)
		{
			return;
		}
	}
	
	/**
	 * Sorts the recording table.
	 */
	public void sortRecordings() {
		
		Collections.sort(recordings, new CompareRecordings());
		redrawTable();
	}
}
