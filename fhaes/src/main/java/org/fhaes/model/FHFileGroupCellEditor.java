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
