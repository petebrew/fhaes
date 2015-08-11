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
package org.fhaes.fhxrecorder.view;

import java.util.ArrayList;
import java.util.Collections;

import javax.swing.DefaultCellEditor;
import javax.swing.JComboBox;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

import org.fhaes.fhxrecorder.compare.CompareEventYears;
import org.fhaes.fhxrecorder.model.FHX2_Event;
import org.fhaes.fhxrecorder.model.FHX2_Sample;

/**
 * EventTable Class. Creates a table that holds all of the events for a sample. Each event also holds information regarding what type of
 * injury occurred, which season it occurred during, the start year, and when an event finished recording.
 * 
 * @author Brendan Compton, Dylan Jones, and Alex Richter
 */
public class EventTable extends JTable {
	
	private static final long serialVersionUID = 1L;
	private static final int ROW_HEIGHT = 25;
	
	private ArrayList<FHX2_Event> events;
	private FHX2_Sample sample;
	
	/**
	 * Holds information on the event types.
	 */
	public enum EventTypes {
		
		FIRE_SCAR("Fire Scar", 0),
		
		OTHER_INJURY("Other Injury", 1);
		
		// Declare local variables
		private String string;
		private int i;
		
		EventTypes(String str, int in) {
			
			string = str;
			i = in;
		}
		
		@Override
		public String toString() {
			
			return string;
		}
		
		public int getInt() {
			
			return i;
		}
	};
	
	/**
	 * Holds information about the event season.
	 */
	public enum EventSeasons {
		
		DORMANT("Dormant season", 'D', 0),
		
		EARLY_EARLY("Early earlywood", 'E', 1),
		
		MIDDLE_EARLY("Middle earlywood", 'M', 2),
		
		LATE_EARLY("Late earlywood", 'L', 3),
		
		LATEWOOD("Latewood", 'A', 4),
		
		UNDETERMINED("Undetermined", 'U', 5);
		
		// Declare local variables
		private String string;
		private char type;
		private int i;
		
		EventSeasons(String str, char t, int in) {
			
			string = str;
			type = t;
			i = in;
		}
		
		@Override
		public String toString() {
			
			return string;
		}
		
		public char typeChar() {
			
			return type;
		}
		
		public int getInt() {
			
			return i;
		}
	};
	
	/**
	 * Settings for the columns.
	 */
	public enum Columns {
		
		EVENT_TYPE("Event Type", 0),
		
		EVENT_SEASON("Event Season", 1),
		
		EVENT_YEAR("Event Year", 2);
		
		// Declare local variables
		private String string;
		private int i;
		
		Columns(String str, int in) {
			
			string = str;
			i = in;
		}
		
		@Override
		public String toString() {
			
			return string;
		}
		
		public int getInt() {
			
			return i;
		}
	}
	
	/**
	 * The main function for the event table, essentially initializes all of the table settings.
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public EventTable(FHX2_Sample s) {
		
		events = new ArrayList<FHX2_Event>();
		sample = s;
		getTableHeader().setReorderingAllowed(false);
		
		// We need to edit the cells
		setCellSelectionEnabled(true);
		
		// Set the row height so we can actually see the info
		setRowHeight(ROW_HEIGHT);
		
		// Set up the columns
		DefaultTableModel tableModel = (DefaultTableModel) getModel();
		tableModel.addColumn(Columns.EVENT_TYPE.toString());
		tableModel.addColumn(Columns.EVENT_SEASON.toString());
		tableModel.addColumn(Columns.EVENT_YEAR.toString());
		
		// Set up the combo boxes
		JComboBox eventTypeBox = new JComboBox();
		eventTypeBox.addItem(EventTypes.FIRE_SCAR.toString());
		eventTypeBox.addItem(EventTypes.OTHER_INJURY.toString());
		TableColumn eventTypeColumn = getColumnModel().getColumn(Columns.EVENT_TYPE.getInt());
		eventTypeColumn.setCellEditor(new DefaultCellEditor(eventTypeBox));
		
		JComboBox eventSeasonBox = new JComboBox();
		eventSeasonBox.addItem(EventSeasons.DORMANT.toString());
		eventSeasonBox.addItem(EventSeasons.EARLY_EARLY.toString());
		eventSeasonBox.addItem(EventSeasons.MIDDLE_EARLY.toString());
		eventSeasonBox.addItem(EventSeasons.LATE_EARLY.toString());
		eventSeasonBox.addItem(EventSeasons.LATEWOOD.toString());
		eventSeasonBox.addItem(EventSeasons.UNDETERMINED.toString());
		TableColumn eventSeasonColumn = getColumnModel().getColumn(Columns.EVENT_SEASON.getInt());
		eventSeasonColumn.setCellEditor(new DefaultCellEditor(eventSeasonBox));
		
		// Set up the text fields
		TableColumn eventYearColumn = getColumnModel().getColumn(Columns.EVENT_YEAR.getInt());
		eventYearColumn.setCellEditor(new DefaultCellEditor(new JTextField()));
	}
	
	/**
	 * Gets an event from the table based on the given index.
	 * 
	 * @param index
	 * @return the event, if it exists
	 */
	public FHX2_Event getEvent(int index) {
		
		if (index < events.size() && index > -1)
			return events.get(index);
		return null;
	}
	
	/**
	 * Returns the ArrayList of FHX2 Events.
	 * 
	 * @return the ArrayList of events
	 */
	public ArrayList<FHX2_Event> getEvents() {
		
		return events;
	}
	
	/**
	 * Returns the total number of events in the table.
	 * 
	 * @return the number of events in the table
	 */
	public int getNumOfEvents() {
		
		return events.size();
	}
	
	/**
	 * Gets the earliest event in the table
	 * 
	 * @return the earliest event
	 */
	public FHX2_Event getEarliestEvent() {
		
		int minIndex = 0;
		for (int i = 0; i < getNumOfEvents(); i++)
			if (events.get(i).getEventYear() < events.get(minIndex).getEventYear())
				minIndex = i;
		return events.get(minIndex);
	}
	
	/**
	 * Gets the latest event in the table
	 * 
	 * @return the latest event
	 */
	public FHX2_Event getLatestEvent() {
		
		int maxIndex = 0;
		for (int i = 0; i < getNumOfEvents(); i++)
			if (events.get(i).getEventYear() > events.get(maxIndex).getEventYear())
				maxIndex = i;
		return events.get(maxIndex);
	}
	
	/**
	 * Adds a new event to the table and redraws it.
	 */
	public void addNewEvent() {
		
		events.add(new FHX2_Event());
		redrawTable();
	}
	
	/**
	 * Used to load up events to the table, then redraws.
	 * 
	 * @param event
	 */
	public void addEvent(FHX2_Event event) {
		
		events.add(event);
		redrawTable();
	}
	
	/**
	 * Removes an event from the table, then redraws.
	 * 
	 * @param index
	 */
	public void removeEvent(int index) {
		
		if (index < events.size() && index > -1)
			events.remove(index);
		redrawTable();
	}
	
	/**
	 * Removes all events from the table.
	 */
	public void deleteAllEvents() {
		
		events.clear();
		redrawTable();
	}
	
	/**
	 * Deletes the event at the year specified by the input parameter.
	 * 
	 * @param year
	 */
	public void deleteEventInYear(int year) {
		
		for (int i = 0; i < events.size(); i++)
		{
			if (events.get(i).containsYear(year))
			{
				events.remove(i);
				redrawTable();
				return;
			}
		}
	}
	
	/**
	 * Deletes any events not in the range given.
	 * 
	 * @param startYear
	 * @param endYear
	 */
	public void deleteEventsNotInRange(int startYear, int endYear) {
		
		for (int i = events.size() - 1; i >= 0; i--)
			if (events.get(i).getEventYear() < startYear || events.get(i).getEventYear() > endYear)
				events.remove(i);
		redrawTable();
	}
	
	/**
	 * Adjusts the type of the given event based on the input parameter.
	 * 
	 * @param inEventIndex
	 * @param inEventType
	 * @return the year of the event that was changed
	 */
	public int changeEventType(int inEventIndex, char inEventType) {
		
		FHX2_Event temp = events.get(inEventIndex);
		temp.setEventType(inEventType);
		
		redrawTable();
		return temp.getEventYear();
	}
	
	/**
	 * Ensures that cells are always in an editable state.
	 */
	@Override
	public boolean isCellEditable(int row, int col) {
		
		return true;
	}
	
	/**
	 * Controls the redrawing of the table for whenever there is either an addition or a removal of an event.
	 */
	public void redrawTable() {
		
		// First we remove all the rows in case something has changed
		DefaultTableModel tableModel = (DefaultTableModel) getModel();
		int rowCount = tableModel.getRowCount();
		for (int i = rowCount - 1; i >= 0; i--)
			tableModel.removeRow(i);
			
		// Add the events back in as rows
		for (int i = 0; i < events.size(); i++)
		{
			Object[] row = new Object[tableModel.getColumnCount()];
			char eventType = events.get(i).getEventType();
			if (Character.isUpperCase(eventType))
			{
				row[Columns.EVENT_TYPE.getInt()] = EventTypes.FIRE_SCAR.toString();
			}
			else if (Character.isLowerCase(eventType))
			{
				row[Columns.EVENT_TYPE.getInt()] = EventTypes.OTHER_INJURY.toString();
			}
			
			eventType = Character.toUpperCase(eventType);
			if (eventType == EventSeasons.DORMANT.typeChar())
			{
				row[Columns.EVENT_SEASON.getInt()] = EventSeasons.DORMANT.toString();
			}
			else if (eventType == EventSeasons.EARLY_EARLY.typeChar())
			{
				row[Columns.EVENT_SEASON.getInt()] = EventSeasons.EARLY_EARLY.toString();
			}
			else if (eventType == EventSeasons.MIDDLE_EARLY.typeChar())
			{
				row[Columns.EVENT_SEASON.getInt()] = EventSeasons.MIDDLE_EARLY.toString();
			}
			else if (eventType == EventSeasons.LATE_EARLY.typeChar())
			{
				row[Columns.EVENT_SEASON.getInt()] = EventSeasons.LATE_EARLY.toString();
			}
			else if (eventType == EventSeasons.LATEWOOD.typeChar())
			{
				row[Columns.EVENT_SEASON.getInt()] = EventSeasons.LATEWOOD.toString();
			}
			else
			{
				row[Columns.EVENT_SEASON.getInt()] = EventSeasons.UNDETERMINED.toString();
			}
			row[Columns.EVENT_YEAR.getInt()] = ((Integer) events.get(i).getEventYear()).toString();
			tableModel.addRow(row);
		}
		revalidate();
		repaint();
	}
	
	/**
	 * Method that controls when values are set along a row. Checks for when a cell is changed in the table, checks to see if it is valid,
	 * and updates the appropriate data.
	 */
	@Override
	public void setValueAt(final Object value, final int r, final int c) {
		
		try
		{
			int col = super.convertColumnIndexToModel(c);
			int row = super.convertRowIndexToModel(r);
			if (col == Columns.EVENT_TYPE.getInt())
			{
				if (value.toString() == EventTypes.FIRE_SCAR.toString())
				{
					events.get(row).setEventType(Character.toUpperCase(events.get(row).getEventType()));
				}
				else if (value.toString() == EventTypes.OTHER_INJURY.toString())
				{
					events.get(row).setEventType(Character.toLowerCase(events.get(row).getEventType()));
				}
				super.setValueAt(value, r, c);
			}
			else if (col == Columns.EVENT_SEASON.getInt())
			{
				char newEventType;
				if (value.toString() == EventSeasons.DORMANT.toString())
				{
					newEventType = EventSeasons.DORMANT.typeChar();
				}
				else if (value.toString() == EventSeasons.EARLY_EARLY.toString())
				{
					newEventType = EventSeasons.EARLY_EARLY.typeChar();
				}
				else if (value.toString() == EventSeasons.MIDDLE_EARLY.toString())
				{
					newEventType = EventSeasons.MIDDLE_EARLY.typeChar();
				}
				else if (value.toString() == EventSeasons.LATE_EARLY.toString())
				{
					newEventType = EventSeasons.LATE_EARLY.typeChar();
				}
				else if (value.toString() == EventSeasons.LATEWOOD.toString())
				{
					newEventType = EventSeasons.LATEWOOD.typeChar();
				}
				else
				{
					newEventType = EventSeasons.UNDETERMINED.typeChar();
				}
				
				if (Character.isUpperCase(events.get(row).getEventType()))
				{
					events.get(row).setEventType(Character.toUpperCase(newEventType));
				}
				else
				{
					events.get(row).setEventType(Character.toLowerCase(newEventType));
				}
				super.setValueAt(value, r, c);
			}
			else if (col == Columns.EVENT_YEAR.getInt())
			{
				int eventYear = Integer.parseInt(value.toString());
				if (eventYear <= sample.getSampleFirstYear())
				{
					eventYear = sample.getSampleFirstYear();
					if (eventYear == 0)
					{
						eventYear = 1;
					}
				}
				else if (eventYear >= sample.getSampleLastYear())
				{
					eventYear = sample.getSampleLastYear();
					if (eventYear == 0)
					{
						eventYear = -1;
					}
				}
				else if (eventYear == 0)
				{
					eventYear = 1;
				}
				
				// The event must be in a recording range
				if (sample.isYearInRecordings(eventYear) && sample.eventYearAvailable(eventYear))
				{
					events.get(row).setEventYear(eventYear);
					super.setValueAt(Integer.toString(eventYear), r, c);
					sortEvents();
					
					SampleInputPanel.setCheckBoxEnabledValues();
				}
			}
			else
			{
				super.setValueAt(value, r, c);
			}
		}
		catch (NumberFormatException e)
		{
			return;
		}
	}
	
	/**
	 * Sorts the event table.
	 */
	public void sortEvents() {
		
		Collections.sort(events, new CompareEventYears());
		redrawTable();
	}
	
	/**
	 * Returns whether or not an event has occurred on the input year.
	 * 
	 * @param year
	 * @return true if there was an event, false otherwise
	 */
	public boolean yearHasAnEvent(int year) {
		
		for (FHX2_Event event : events)
			if (event.containsYear(year))
				return true;
		return false;
	}
}
