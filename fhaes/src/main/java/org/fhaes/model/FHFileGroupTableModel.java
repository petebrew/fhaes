/**************************************************************************************************
 * Fire History Analysis and Exploration System (FHAES), Copyright (C) 2015
 * 
 * Contributors: Peter Brewer
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
package org.fhaes.model;

import java.util.ArrayList;

import javax.swing.table.AbstractTableModel;

/**
 * FHFileGroupTableModel Class.
 */
public class FHFileGroupTableModel extends AbstractTableModel {
	
	private static final long serialVersionUID = 1L;
	
	private ArrayList<FHFileGroup> fglist;
	
	/**
	 * TODO
	 * 
	 * @param fglist
	 */
	public FHFileGroupTableModel(ArrayList<FHFileGroup> fglist) {
		
		this.fglist = fglist;
	}
	
	@Override
	public int getColumnCount() {
		
		return 1;
	}
	
	@Override
	public int getRowCount() {
		
		return fglist.size();
	}
	
	@Override
	public Object getValueAt(int row, int col) {
		
		if (row == -1)
		{
			return null;
		}
		
		if (row < getRowCount())
		{
			return fglist.get(row);
		}
		
		return null;
	}
	
	public void setName(String name, int row) {
		
		fglist.get(row).setName(name);
	}
	
	@Override
	public boolean isCellEditable(int row, int col) {
		
		return true;
	}
}
