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

import java.util.ArrayList;
import java.util.Vector;

import javax.swing.table.DefaultTableModel;

import org.fhaes.enums.EventTypeToProcess;
import org.fhaes.fhfilereader.AbstractFireHistoryReader;
import org.fhaes.model.FHFile;
import org.fhaes.model.ReadOnlyDefaultTableModel;
import org.fhaes.preferences.App;
import org.fhaes.preferences.FHAESPreferences.PrefKey;

/**
 * Provide some simple descriptive statistics for a single FHFile
 * 
 * @author pbrewer
 *
 */
public class FHDescriptiveStats {
	
	public static DefaultTableModel getDescriptiveStatsTableModel(FHFile file) {
	
		AbstractFireHistoryReader fr = file.getFireHistoryReader();
		
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
			row.add(filterArray.get(2).get(i));
			
			rows.add(row);
			i++;
		}
		
		ReadOnlyDefaultTableModel model = new ReadOnlyDefaultTableModel(rows, headers);
		
		return model;
	}
}
