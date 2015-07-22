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
