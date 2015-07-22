/*******************************************************************************
 * Copyright (c) 2013 Peter Brewer
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 * 
 * Contributors:
 *     Peter Brewer
 *     Elena Velasquez
 ******************************************************************************/
package org.fhaes.model;

/*******************************************************************************
 * Copyright (C) 2013 Peter Brewer
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * Contributors:
 *     Peter Brewer
 ******************************************************************************/

import java.util.Vector;

import javax.swing.table.DefaultTableModel;

/**
 * ReadOnlyDefaultTableModel Class.
 */
public class ReadOnlyDefaultTableModel extends DefaultTableModel {

	private static final long serialVersionUID = 1L;

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
}
