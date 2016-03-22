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
package org.fhaes.segmentation;

import java.awt.Component;
import java.text.DecimalFormat;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableCellRenderer;

import org.jdesktop.swingx.JXTable;

/**
 * SegmentTable Class. A specialist version of JXTable used for listing Segments for fire history analysis.
 * 
 * @author Peter Brewer
 */
public class SegmentTable extends JXTable {
	
	private static final long serialVersionUID = 1L;
	public SegmentTableModel tableModel;
	private int earliestYear;
	private int latestYear;
	
	/**
	 * TODO
	 */
	public SegmentTable() {
	
		tableModel = new SegmentTableModel();
		
		this.setModel(tableModel);
		
		YearFormatRenderer yearrenderer = new YearFormatRenderer();
		
		getColumnModel().getColumn(1).setCellRenderer(yearrenderer);
		getColumnModel().getColumn(2).setCellRenderer(yearrenderer);
		getColumnModel().getColumn(0).setPreferredWidth(10);
		getColumnModel().getColumn(0).setResizable(false);
		this.setRowSelectionAllowed(true);
		this.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
	}
	
	/**
	 * Set the oldest year in this dataset.
	 * 
	 * @param y
	 */
	public void setEarliestYear(int y) {
	
		this.earliestYear = y;
		setYearBounds();
	}
	
	/**
	 * Set the most recent year in this dataset.
	 * 
	 * @param y
	 */
	public void setLatestYear(int y) {
	
		this.latestYear = y;
		setYearBounds();
	}
	
	/**
	 * Gets the oldest year in the dataset.
	 * 
	 * @return
	 */
	public int getEarliestYear() {
	
		return earliestYear;
	}
	
	/**
	 * Get the most recent year available in the dataset.
	 * 
	 * @return
	 */
	public int getLatestYear() {
	
		return this.latestYear;
	}
	
	/**
	 * Sets the limits to the acceptable values for years in the table.
	 */
	private void setYearBounds() {
	
		getColumnModel().getColumn(1).setCellEditor(new IntegerEditor(earliestYear, latestYear));
		getColumnModel().getColumn(2).setCellEditor(new IntegerEditor(earliestYear, latestYear));
	}
	
	/**
	 * Format a year number correctly with no comma delimiters.
	 * 
	 * @author Peter Brewer
	 */
	static class YearFormatRenderer extends DefaultTableCellRenderer {
		
		private static final long serialVersionUID = 1L;
		
		@Override
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
		
			DecimalFormat formatter = new DecimalFormat("#");
			value = formatter.format(value);
			Component item = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
			this.setHorizontalAlignment(JLabel.RIGHT);
			return item;
		}
	}
}
