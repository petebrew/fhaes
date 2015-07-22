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
package org.fhaes.gui;

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

import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.border.TitledBorder;

import org.fhaes.util.JTableSpreadsheetByRowAdapter;
import org.jdesktop.swingx.JXTable;

/**
 * IntervalsPanel Class.
 */
public class IntervalsPanel extends JPanel {

	private static final long serialVersionUID = 1L;
	protected JXTable tblExceedence;
	protected JXTable tblSummary;
	protected JTableSpreadsheetByRowAdapter adapterExceedence;
	protected JTableSpreadsheetByRowAdapter adapterSummary;

	/**
	 * Create the panel.
	 */
	public IntervalsPanel() {

		setLayout(new BorderLayout(0, 0));

		JSplitPane splitPane = new JSplitPane();
		splitPane.setContinuousLayout(true);
		splitPane.setOneTouchExpandable(true);
		splitPane.setResizeWeight(0.5);
		splitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
		add(splitPane, BorderLayout.CENTER);

		JPanel panelSummary = new JPanel();
		panelSummary.setBorder(new TitledBorder(null, "Intervals summary", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		splitPane.setLeftComponent(panelSummary);
		panelSummary.setLayout(new BorderLayout(0, 0));

		JScrollPane scrollPaneSummary = new JScrollPane();
		scrollPaneSummary.getViewport().setBackground(Color.WHITE);
		panelSummary.add(scrollPaneSummary, BorderLayout.CENTER);

		tblSummary = new JXTable();
		scrollPaneSummary.setViewportView(tblSummary);
		tblSummary.setRowSelectionAllowed(true);
		adapterSummary = new JTableSpreadsheetByRowAdapter(tblSummary);
		tblSummary.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		tblSummary.setColumnControlVisible(true);

		JPanel panelExceedence = new JPanel();
		panelExceedence.setBorder(new TitledBorder(null, "Exceedence", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		splitPane.setRightComponent(panelExceedence);
		panelExceedence.setLayout(new BorderLayout(0, 0));

		JScrollPane scrollPaneExceedence = new JScrollPane();
		scrollPaneExceedence.getViewport().setBackground(Color.WHITE);
		panelExceedence.add(scrollPaneExceedence, BorderLayout.CENTER);

		tblExceedence = new JXTable();
		tblExceedence.setColumnControlVisible(true);

		scrollPaneExceedence.setViewportView(tblExceedence);
		tblExceedence.setRowSelectionAllowed(true);
		adapterExceedence = new JTableSpreadsheetByRowAdapter(tblExceedence);
		tblExceedence.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

	}

	public JXTable getExceedenceTable() {

		return tblExceedence;
	}
}
