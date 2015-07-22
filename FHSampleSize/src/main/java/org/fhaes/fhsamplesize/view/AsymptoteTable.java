/*******************************************************************************
 * Copyright (C) 2014 Peter Brewer
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
 ******************************************************************************/
package org.fhaes.fhsamplesize.view;

import org.fhaes.fhsamplesize.model.AsymptoteTableModel;
import org.fhaes.util.JTableSpreadsheetByRowAdapter;
import org.jdesktop.swingx.JXTable;

/**
 * AsymptoteTable Class.
 * 
 * @author Joshua Brogan and Peter Brewer
 */
public class AsymptoteTable extends JXTable {

	private static final long serialVersionUID = 1L;
	private static final int ROW_HEIGHT = 25;

	private AsymptoteTableModel model;
	protected JTableSpreadsheetByRowAdapter adapter;

	public AsymptoteTable() {

		// Block reordering of the table columns
		getTableHeader().setReorderingAllowed(false);

		// Set a fixed height for all of the rows to maintain their visibility
		setRowHeight(ROW_HEIGHT);

		model = new AsymptoteTableModel();
		this.setModel(model);
	}
}
