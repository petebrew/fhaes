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
package org.fhaes.model;

import java.util.ArrayList;
import java.util.Vector;

import javax.swing.table.DefaultTableModel;

/**
 * ReadOnlyDefaultTableModel Class.
 */
public class ReadOnlyDefaultTableModel extends DefaultTableModel {
	
	private static final long serialVersionUID = 1L;
	@SuppressWarnings("rawtypes")
	ArrayList<Class> classList;
	
	/**
	 * TODO
	 * 
	 * @param rows
	 * @param header
	 */
	public ReadOnlyDefaultTableModel(Vector<Vector<Object>> rows, Vector<Object> header) {
		
		super(rows, header);
	}
	
	/**
	 * TODO
	 */
	@Override
	public boolean isCellEditable(int row, int col) {
		
		return false;
	}
	
	/**
	 * TODO
	 * 
	 * @param col
	 * @param clazz
	 */
	@SuppressWarnings("rawtypes")
	public void setColumnClass(int col, Class clazz) {
		
		if (classList == null)
		{
			classList = new ArrayList<Class>();
			for (int i = 0; i < this.getColumnCount(); i++)
			{
				classList.add(String.class);
			}
		}
		
		try
		{
			classList.set(col, clazz);
		}
		catch (Exception e)
		{
		
		}
	}
	
	/**
	 * TODO
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public Class getColumnClass(int col) {
		
		if (classList == null)
			return String.class;
			
		try
		{
			return classList.get(col);
		}
		catch (Exception e)
		{
			return String.class;
		}
	}
}
