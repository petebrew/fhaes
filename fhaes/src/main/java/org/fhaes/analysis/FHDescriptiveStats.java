/**************************************************************************************************
 * Fire History Analysis and Exploration System (FHAES), Copyright (C) 2015
 * 
 * Contributors: Elena Velasquez and Peter Brewer
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
package org.fhaes.analysis;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Vector;

import javax.swing.table.DefaultTableModel;

import org.fhaes.enums.EventTypeToProcess;
import org.fhaes.fhfilereader.AbstractFireHistoryReader;
import org.fhaes.fhfilereader.FHFile;
import org.fhaes.model.ReadOnlyDefaultTableModel;
import org.fhaes.preferences.App;
import org.fhaes.preferences.FHAESPreferences.PrefKey;

import au.com.bytecode.opencsv.CSVWriter;

/**
 * Provide some simple descriptive statistics for a single FHFile
 * 
 * @author pbrewer
 *
 */
public class FHDescriptiveStats {
	
	public static DefaultTableModel getDescriptiveStatsTableModel(FHFile file) {
	
		AbstractFireHistoryReader fr = file.getFireHistoryReader();
		DecimalFormat twoPlace = new DecimalFormat("0.00");
		
		int[] sampledepths = fr.getSampleDepths();
		int[] recordingdepths = fr.getRecordingDepths();
		
		EventTypeToProcess eventType = App.prefs.getEventTypePref(PrefKey.EVENT_TYPE_TO_PROCESS, EventTypeToProcess.FIRE_AND_INJURY_EVENT);
		ArrayList<ArrayList<Double>> filterArray = fr.getFilterArrays(eventType);
		
		Vector<Object> headers = new Vector<Object>();
		headers.add("Year");
		headers.add("Sample depth");
		headers.add("Recording depth");
		headers.add("Number of " + eventType.toString());
		headers.add("Percentage of " + eventType.toString());
		Vector<Vector<Object>> rows = new Vector<Vector<Object>>();
		
		int i = 0;
		for (int yr = fr.getFirstYear(); yr <= fr.getLastYear(); yr++)
		{
			Vector<Object> row = new Vector<Object>();
			
			row.add(yr);
			row.add(sampledepths[i]);
			row.add(recordingdepths[i]);
			row.add(filterArray.get(0).get(i));
			if (filterArray.get(2).get(i).equals(-99.0))
			{
				row.add(filterArray.get(2).get(i));
			}
			else
			{
				row.add(twoPlace.format(filterArray.get(2).get(i) * 100));
			}
			
			rows.add(row);
			i++;
		}
		
		ReadOnlyDefaultTableModel model = new ReadOnlyDefaultTableModel(rows, headers);
		
		return model;
	}
	
	public static File getDescriptiveStatsAsFile(FHFile infile, File outfile) {
	
		DefaultTableModel model = getDescriptiveStatsTableModel(infile);
		
		if (outfile == null)
		{
			try
			{
				outfile = File.createTempFile("FHDescriptiveStats", "FileSummary.tmp");
			}
			catch (IOException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		CSVWriter writer;
		try
		{
			writer = new CSVWriter(new FileWriter(outfile.getAbsoluteFile().toString()), '\t');
			// feed in your array (or convert your data to an array)
			
			// First write the column names
			String[] header = new String[model.getColumnCount()];
			for (int col = 0; col < model.getColumnCount(); col++)
			{
				header[col] = model.getColumnName(col);
			}
			writer.writeNext(header);
			
			// Next write the table values
			for (int row = 0; row < model.getRowCount(); row++)
			{
				String[] entries = new String[model.getColumnCount()];
				
				for (int col = 0; col < model.getColumnCount(); col++)
				{
					Object value = model.getValueAt(row, col);
					
					entries[col] = value.toString();
					
				}
				writer.writeNext(entries);
				
			}
			
			writer.close();
			
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return outfile;
		
	}
	
	/**
	 * Returns a table that replicates the event summary table produced by SSIZ (second table in output). The table has three columns:
	 * 
	 * <ol>
	 * <li>Number of trees recording an event</li>
	 * <li>Number of years in which this many trees recording events</li>
	 * <li>List of years in which this happened</li>
	 * </ol>
	 * 
	 * @param file
	 * @return
	 */
	public static DefaultTableModel getEventSummaryTable(FHFile file, EventTypeToProcess eventType) {
	
		AbstractFireHistoryReader fr = file.getFireHistoryReader();
		
		// Create array list to hold data
		ArrayList<ArrayList<Integer>> list = new ArrayList<ArrayList<Integer>>();
		for (int i = 0; i < fr.getNumberOfSeries(); i++)
		{
			list.add(new ArrayList<Integer>());
		}
		
		ArrayList<Double> events = fr.getFilterArrays(eventType).get(0);
		
		int currentYear = fr.getFirstYear();
		
		for (int i = 0; i < events.size(); i++)
		{
			list.get(events.get(i).intValue()).add(currentYear);
			currentYear++;
		}
		
		Vector<Object> headers = new Vector<Object>();
		headers.add("Trees recording");
		headers.add("# of years");
		headers.add("Years");
		Vector<Vector<Object>> rows = new Vector<Vector<Object>>();
		
		for (int i = 0; i < list.size(); i++)
		{
			Vector<Object> row = new Vector<Object>();
			ArrayList<Integer> r = list.get(i);
			
			// Skip indices where there are no years lists
			if (r.size() == 0)
				continue;
			
			row.add(i);
			row.add(r.size());
			String listOfYears = "";
			for (Integer yr : r)
			{
				listOfYears += yr + " ";
			}
			row.add(listOfYears);
			
			rows.add(row);
		}
		
		ReadOnlyDefaultTableModel model = new ReadOnlyDefaultTableModel(rows, headers);
		model.setColumnClass(0, Integer.class);
		model.setColumnClass(1, Integer.class);
		
		return model;
		
	}
	
	public static File getEventSummaryAsFile(FHFile infile, File outfile, EventTypeToProcess eventType) {
	
		DefaultTableModel model = getEventSummaryTable(infile, eventType);
		
		if (outfile == null)
		{
			try
			{
				outfile = File.createTempFile("FHDescriptiveStats", "EventSummary.tmp");
			}
			catch (IOException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		CSVWriter writer;
		try
		{
			writer = new CSVWriter(new FileWriter(outfile.getAbsoluteFile().toString()), '\t');
			// feed in your array (or convert your data to an array)
			
			// First write the column names
			String[] header = new String[model.getColumnCount()];
			for (int col = 0; col < model.getColumnCount(); col++)
			{
				header[col] = model.getColumnName(col);
			}
			writer.writeNext(header);
			
			// Next write the table values
			for (int row = 0; row < model.getRowCount(); row++)
			{
				String[] entries = new String[model.getColumnCount()];
				
				for (int col = 0; col < model.getColumnCount(); col++)
				{
					Object value = model.getValueAt(row, col);
					
					entries[col] = value.toString();
					
				}
				writer.writeNext(entries);
				
			}
			
			writer.close();
			
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return outfile;
		
	}
}