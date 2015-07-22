/*******************************************************************************
 * Copyright (C) 2014 Peter Brewer and Joshua Brogan
 * 
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 * 
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 * 
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *     
 *     Contributors:
 *     		Peter Brewer
 *     		Joshua Brogan
 ******************************************************************************/
package org.fhaes.fhsamplesize.model;

import java.util.ArrayList;

import javax.swing.table.AbstractTableModel;

/**
 * AsymptoteTableModel Class.
 * 
 * @author Joshua Brogan and Peter Brewer
 */
public class AsymptoteTableModel extends AbstractTableModel {

	private static final long serialVersionUID = 1L;
	private final int COLUMN_COUNT = 6;
	private ArrayList<AsymptoteModel> items = new ArrayList<AsymptoteModel>();

	@Override
	public String getColumnName(int col) {

		switch (col)
		{
			case 0:
				return "Type";
			case 1:
				return "Seg Start";
			case 2:
				return "Seg End";
			case 3:
				return "Asymptote";
			case 4:
				return "r2 adj";
			case 5:
				return "δf/ δs";
			default:
				return null;
		}
	}

	/**
	 * TODO
	 */
	@Override
	public int getColumnCount() {

		return COLUMN_COUNT;
	}

	/**
	 * TODO
	 */
	@Override
	public int getRowCount() {

		if (items != null)
		{
			return items.size();
		}

		return 0;
	}

	/**
	 * TODO
	 * 
	 * @param row
	 * @return
	 */
	public AsymptoteModel getRow(int row) {

		return items.get(row);
	}

	/**
	 * TODO
	 */
	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {

		AsymptoteModel row = getRow(rowIndex);

		if (row == null)
			return null;

		switch (columnIndex)
		{
			case 0:
				return row.getType();
			case 1:
				return row.getSegment().getFirstYear();
			case 2:
				return row.getSegment().getLastYear();
			case 3:
				return row.getAsymptote();
			case 4:
				return row.getR2adj();
			case 5:
				return row.getDfds();
			default:
				return null;
		}
	}
}
