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

import java.awt.Component;
import java.awt.event.MouseEvent;
import java.util.EventObject;

import javax.swing.AbstractCellEditor;
import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.TableCellEditor;

/**
 * FHFileGroupCellEditor Class.
 */
public class FHFileGroupCellEditor extends AbstractCellEditor implements TableCellEditor {
	
	private static final long serialVersionUID = 1L;
	
	// This is the component that will handle the editing of the cell value
	JComponent component = new JTextField();
	FHFileGroup group;
	
	/**
	 * This method is called when a cell value is edited by the user.
	 */
	@Override
	public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int rowIndex, int vColIndex) {
		
		// 'value' is value contained in the cell located at (rowIndex, vColIndex)
		if (isSelected)
		{
			// cell (and perhaps other cells) are selected
		}
		
		group = (FHFileGroup) value;
		
		// Configure the component with the specified value
		((JTextField) component).setText(group.toString());
		component.setBorder(null);
		
		// Return the configured component
		return component;
	}
	
	/**
	 * This method is called when editing is completed. It must return the new value to be stored in the cell.
	 */
	@Override
	public Object getCellEditorValue() {
		
		group.setName(((JTextField) component).getText());
		return group;
	}
	
	/**
	 * TODO
	 */
	@Override
	public boolean isCellEditable(EventObject evt) {
		
		if (evt instanceof MouseEvent)
		{
			return ((MouseEvent) evt).getClickCount() == 2;
		}
		return false;
	}
}
