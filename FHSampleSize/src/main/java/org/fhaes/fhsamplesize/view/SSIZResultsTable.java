/**************************************************************************************************
 * Fire History Analysis and Exploration System (FHAES), Copyright (C) 2015
 * 
 * Contributors: Joshua Brogan and Peter Brewer
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
package org.fhaes.fhsamplesize.view;

import java.awt.Component;
import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import org.fhaes.fhsamplesize.model.AnalysisResultsModel;
import org.fhaes.util.JTableSpreadsheetByRowAdapter;
import org.jdesktop.swingx.JXTable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * SSIZResultsTable Class.
 * 
 * @author Joshua Brogan and Peter Brewer
 */
public class SSIZResultsTable extends JXTable {
	
	private static final Logger log = LoggerFactory.getLogger(SSIZResultsTable.class);
	
	protected JTableSpreadsheetByRowAdapter adapter;
	
	private static final long serialVersionUID = 1L;
	private static final int ROW_HEIGHT = 25;
	
	private Boolean initialSortHasBeenApplied = false;
	
	/**
	 * Enumerators to represent the different types of column data.
	 */
	public enum Columns {
		
		N("N", 0),
		
		SEGMENT_START("Seg Start", 1),
		
		SEGMENT_END("Seg End", 2),
		
		MEAN_EVENTS_PER_CENTURY("Mean", 3),
		
		MEDIAN_EVENTS_PER_CENTURY("Median", 4),
		
		CONFIDENCE_INTERVAL_95("CI 95", 5),
		
		CONFIDENCE_INTERVAL_99("CI 99", 6),
		
		WEIBULL_MEAN("Weibull Mean", 7),
		
		WEIBULL_MEDIAN("Weibull Median", 8),
		
		WEIBULL_CONFIDENCE_INTERVAL_95("Weibull CI95", 9),
		
		WEIBULL_CONFIDENCE_INTERVAL_99("Weibull CI99", 10);
		
		// Declare local variables
		private String string;
		private int i;
		
		Columns(String str, int in) {
			
			string = str;
			i = in;
		}
		
		@Override
		public String toString() {
			
			return string;
		}
		
		public int getInt() {
			
			return i;
		}
	}
	
	/**
	 * Initializes the SSIZResultsTable.
	 */
	public SSIZResultsTable() {
		
		// Block reordering of the table columns
		getTableHeader().setReorderingAllowed(false);
		
		// Set a fixed height for all of the rows to maintain their visibility
		setRowHeight(ROW_HEIGHT);
		
		DefaultTableModel tableModel = new DefaultTableModel() {
			
			private static final long serialVersionUID = 1L;
			
			@SuppressWarnings({ "rawtypes", "unchecked" })
			@Override
			public Class getColumnClass(int column) {
				
				switch (column)
				{
					case 0:
						return Integer.class;
					case 1:
						return Integer.class;
					case 2:
						return Integer.class;
					default:
						return Double.class;
				}
			}
		};
		
		tableModel.addColumn(Columns.N.toString());
		tableModel.addColumn(Columns.SEGMENT_START.toString());
		tableModel.addColumn(Columns.SEGMENT_END.toString());
		tableModel.addColumn(Columns.MEAN_EVENTS_PER_CENTURY.toString());
		tableModel.addColumn(Columns.MEDIAN_EVENTS_PER_CENTURY.toString());
		tableModel.addColumn(Columns.CONFIDENCE_INTERVAL_95.toString());
		tableModel.addColumn(Columns.CONFIDENCE_INTERVAL_99.toString());
		tableModel.addColumn(Columns.WEIBULL_MEAN.toString());
		tableModel.addColumn(Columns.WEIBULL_MEDIAN.toString());
		tableModel.addColumn(Columns.WEIBULL_CONFIDENCE_INTERVAL_95.toString());
		tableModel.addColumn(Columns.WEIBULL_CONFIDENCE_INTERVAL_99.toString());
		
		this.setModel(tableModel);
		
		int numberdec = 3;
		DecimalFormatRenderer renderer = new DecimalFormatRenderer(numberdec);
		YearFormatRenderer yearrenderer = new YearFormatRenderer();
		
		getColumnModel().getColumn(1).setCellRenderer(yearrenderer);
		getColumnModel().getColumn(2).setCellRenderer(yearrenderer);
		
		getColumnModel().getColumn(3).setCellRenderer(renderer);
		getColumnModel().getColumn(4).setCellRenderer(renderer);
		getColumnModel().getColumn(5).setCellRenderer(renderer);
		getColumnModel().getColumn(6).setCellRenderer(renderer);
		getColumnModel().getColumn(7).setCellRenderer(renderer);
		getColumnModel().getColumn(8).setCellRenderer(renderer);
		getColumnModel().getColumn(9).setCellRenderer(renderer);
		getColumnModel().getColumn(10).setCellRenderer(renderer);
		
		this.setColumnControlVisible(true);
		adapter = new JTableSpreadsheetByRowAdapter(this);
	}
	
	/**
	 * Removes all rows from the SSIZResultsTable.
	 */
	public void removeAllRows() {
		
		if (this.getModel().getRowCount() > 0)
			for (int i = this.getModel().getRowCount() - 1; i >= 0; i--)
				((DefaultTableModel) this.getModel()).removeRow(i);
	}
	
	/**
	 * Redraws the SSIZResultsTable with the new analysis results.
	 */
	public void redrawTable(ArrayList<AnalysisResultsModel> analysisResults) {
		
		if (analysisResults == null || analysisResults.size() == 0)
		{
			log.debug("No analysis results to draw");
			return;
		}
		
		// First remove all rows in the table
		DefaultTableModel tableModel = (DefaultTableModel) getModel();
		int rowCount = tableModel.getRowCount();
		for (int i = rowCount - 1; i >= 0; i--)
			tableModel.removeRow(i);
			
		// Then re-add the new set of analysis results as rows
		for (AnalysisResultsModel res : analysisResults)
		{
			Object[] row = new Object[tableModel.getColumnCount()];
			
			row[Columns.N.getInt()] = res.getNumberOfSamples();
			row[Columns.SEGMENT_START.getInt()] = res.getSegment().getFirstYear();
			row[Columns.SEGMENT_END.getInt()] = res.getSegment().getLastYear();
			row[Columns.MEAN_EVENTS_PER_CENTURY.getInt()] = res.getMean();
			row[Columns.MEDIAN_EVENTS_PER_CENTURY.getInt()] = res.getMedian();
			row[Columns.CONFIDENCE_INTERVAL_95.getInt()] = res.getConfidenceInterval95();
			row[Columns.CONFIDENCE_INTERVAL_99.getInt()] = res.getConfidenceInterval99();
			row[Columns.WEIBULL_MEAN.getInt()] = res.getWeibullMean();
			row[Columns.WEIBULL_MEDIAN.getInt()] = res.getWeibullMedian();
			row[Columns.WEIBULL_CONFIDENCE_INTERVAL_95.getInt()] = res.getWeibullConfidenceInterval95Lower();
			row[Columns.WEIBULL_CONFIDENCE_INTERVAL_99.getInt()] = res.getWeibullConfidenceInterval99();
			
			tableModel.addRow(row);
		}
		
		// Set the default sort to descending so that results can be viewed as they are generated
		if (!initialSortHasBeenApplied)
		{
			// This is called twice: one time to initialize the sort to ascending, the other to toggle it to descending
			this.getSortController().toggleSortOrder(0);
			this.getSortController().toggleSortOrder(0);
			initialSortHasBeenApplied = true;
		}
		
		this.packAll();
		
		revalidate();
		repaint();
	}
	
	/**
	 * Exports the SSIZResultsTable to a tab-delimited file format for external use.
	 */
	public static void exportResultsTableToTAB(File fileToSave, JTableSpreadsheetByRowAdapter adapter) {
		
		try
		{
			adapter.saveToCSV(fileToSave);
		}
		catch (IOException ex)
		{
			ex.printStackTrace();
		}
	}
	
	/**
	 * Ensures that cells are always in a non-editable state.
	 */
	@Override
	public boolean isCellEditable(int row, int col) {
		
		return false;
	}
	
	/**
	 * Format a decimal number in a table to the number of decimal places specified in the constructor.
	 * 
	 * @author Peter Brewer
	 */
	static class DecimalFormatRenderer extends DefaultTableCellRenderer {
		
		private static final long serialVersionUID = 1L;
		private final int dp;
		
		public DecimalFormatRenderer(int dp) {
			
			this.dp = dp;
		}
		
		@Override
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row,
				int column) {
				
			String formatstring = "0.";
			
			for (int i = 0; i < dp; i++)
				formatstring += "0";
				
			if (value.equals(Double.NaN))
				return super.getTableCellRendererComponent(table, "NaN", isSelected, hasFocus, row, column);
				
			DecimalFormat formatter = new DecimalFormat(formatstring);
			
			// First format the cell value as required
			value = formatter.format(value);
			
			// And pass it on to parent class
			return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
		}
	}
	
	/**
	 * Format a year number correctly with no comma delimiters.
	 * 
	 * @author Peter Brewer
	 */
	static class YearFormatRenderer extends DefaultTableCellRenderer {
		
		private static final long serialVersionUID = 1L;
		
		@Override
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row,
				int column) {
				
			DecimalFormat formatter = new DecimalFormat("#");
			value = formatter.format(value);
			Component item = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
			this.setHorizontalAlignment(JLabel.RIGHT);
			return item;
		}
	}
}
