/**************************************************************************************************
 * Fire History Analysis and Exploration System (FHAES), Copyright (C) 2015
 * 
 * Contributors: Joshua Brogan and Peter Brewer
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
package org.fhaes.segmentation;

import java.util.ArrayList;

import javax.swing.table.AbstractTableModel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * SegmentTableModel Class.
 */
public class SegmentTableModel extends AbstractTableModel {
	
	private static final long serialVersionUID = 1L;
	private static final Logger log = LoggerFactory.getLogger(SegmentTableModel.class);
	
	private ArrayList<SegmentModel> segments = new ArrayList<SegmentModel>();
	
	public SegmentTableModel() {
	
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public Class getColumnClass(int column) {
		
		return Integer.class;
		
	}
	
	@Override
	public boolean isCellEditable(int row, int column) {
		
		if (column == 1 || column == 2)
			return true;
			
		return false;
		
	}
	
	public void addSegment(SegmentModel segment) {
		
		segments.add(segment);
		this.fireTableDataChanged();
		
	}
	
	public void clearSegments() {
		
		segments = new ArrayList<SegmentModel>();
		this.fireTableDataChanged();
		
	}
	
	public void addSegments(ArrayList<SegmentModel> segments) {
		
		segments.addAll(segments);
		this.fireTableDataChanged();
	}
	
	public void removeSegment(int row) {
		
		if (segments.size() > row)
		{
			segments.remove(row);
			this.fireTableDataChanged();
		}
	}
	
	public void removeSegments(int[] is) {
		
		ArrayList<SegmentModel> selectedSegments = new ArrayList<SegmentModel>();
		
		for (int i : is)
		{
			selectedSegments.add(this.getSegment(i));
		}
		
		segments.removeAll(selectedSegments);
		this.fireTableDataChanged();
		
	}
	
	public void removeSegment(SegmentModel segment) {
		
		segments.remove(segment);
		this.fireTableDataChanged();
		
	}
	
	@Override
	public int getColumnCount() {
		
		return 4;
	}
	
	@Override
	public String getColumnName(int col) {
		
		switch (col)
		{
			case 0:
				return "N";
			case 1:
				return "Start";
			case 2:
				return "End";
			case 3:
				return "Length";
			default:
				return null;
		}
	}
	
	@Override
	public Object getValueAt(int row, int col) {
		
		SegmentModel segment = getSegment(row);
		
		if (segment == null)
			return null;
			
		switch (col)
		{
			case 0:
				return row + 1;
			case 1:
				return segment.getFirstYear();
			case 2:
				return segment.getLastYear();
			case 3:
				return segment.getLength();
			default:
				return null;
		}
		
	}
	
	public ArrayList<SegmentModel> getSegments() {
		
		return segments;
	}
	
	public SegmentModel getSegment(int row) {
		
		SegmentModel segment = null;
		try
		{
			segment = segments.get(row);
		}
		catch (Exception e)
		{
			log.error("Row out of bounds");
		}
		
		return segment;
	}
	
	@Override
	public void setValueAt(Object obj, int row, int col) {
		
		if (!isCellEditable(row, col))
			return;
			
		SegmentModel segment = getSegment(row);
		
		if (col == 1)
		{
			segment.setFirstYear((Integer) obj);
		}
		else if (col == 2)
		{
			segment.setLastYear((Integer) obj);
			
		}
		
		fireTableCellUpdated(row, col);
	}
	
	@Override
	public int getRowCount() {
		
		if (segments == null)
			return 0;
			
		return segments.size();
	}
	
}
