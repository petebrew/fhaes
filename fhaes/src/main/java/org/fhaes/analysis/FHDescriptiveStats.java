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

public class FHDescriptiveStats {
	
	public static DefaultTableModel getDescriptiveStatsTableModel(FHFile file) {
	
		AbstractFireHistoryReader fr = file.getFireHistoryReader();
		
		int[] sampledepths = fr.getSampleDepths();
		int[] recordingdepths = fr.getRecordingDepths();
		
		// Get a multi-dimensional array with rows = number of years, and columns = 3.
		// Column 0 = number of events
		// Column 1 = number of trees
		// Column 2 = percentage of scarred trees
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
