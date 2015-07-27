/**************************************************************************************************
 * Fire History Analysis and Exploration System (FHAES), Copyright (C) 2015
 * 
 * Contributors: Cody Calhoun, Anthony Messerschmidt, Seth Westphal, Scott Goble, and Peter Brewer
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
package org.fhaes.FHRecorder.view;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;

import org.codehaus.plexus.util.FileUtils;
import org.fhaes.FHRecorder.controller.FileController;
import org.fhaes.FHRecorder.utility.ColorBar;
import org.fhaes.FHRecorder.utility.YearSummary;
import org.fhaes.filefilter.CSVFileFilter;
import org.fhaes.filefilter.TXTFileFilter;
import org.fhaes.preferences.App;
import org.fhaes.preferences.FHAESPreferences.PrefKey;
import org.fhaes.util.Builder;
import org.fhaes.util.JTableSpreadsheetByRowAdapter;
import org.jdesktop.swingx.JXTable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.miginfocom.swing.MigLayout;

/**
 * GUI_SummaryPanel Class.
 * 
 * @author Cody Calhoun, Anthony Messerschmidt and Seth Westphal
 */
public class SummaryPanel extends javax.swing.JPanel {
	
	private static final Logger log = LoggerFactory.getLogger(SummaryPanel.class);
	
	private static final long serialVersionUID = 1L;
	protected JTableSpreadsheetByRowAdapter adapter;
	
	private JXTable summaryTable;
	List<YearSummary> data;
	
	// Temporarily remove Recording Samples column
	// private String[] columnHeaders = new String[] { "Year", "Events", "Recording Samples", "Total Samples", "% Scarred", "D", "E", "M",
	// "L", "A", "U", "Color Bar" };
	
	private String[] columnHeaders = new String[] { "Year", "Events", "Total Samples", "% Scarred", "D", "E", "M", "L", "A", "U",
			"Color Bar" };
			
	/**
	 * Constructor for the Summary Panel. Sets up the layout and settings of all components.
	 */
	public SummaryPanel() {
		
		setLayout(new MigLayout("", "[grow,right]", "[fill][300px,grow,fill]"));
		
		JButton customizeButton = new JButton("Customize");
		customizeButton.setIcon(Builder.getImageIcon("configure.png"));
		customizeButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				
				showCustomizeWindow();
			}
		});
		
		JButton exportButton = new JButton("Export summary");
		exportButton.setIcon(Builder.getImageIcon("formatcsv.png"));
		exportButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				
				exportTable();
			}
		});
		
		add(exportButton, "flowx,cell 0 0");
		add(customizeButton, "cell 0 0");
		
		JScrollPane scrollPane = new JScrollPane();
		add(scrollPane, "cell 0 1,grow");
		
		summaryTable = new JXTable() {
			
			private static final long serialVersionUID = 1L;
			
			@Override
			public boolean isCellEditable(int row, int column) {
				
				return false;
			};
			
			@Override
			public String getToolTipText(MouseEvent e) {
				
				String tip = null;
				java.awt.Point p = e.getPoint();
				int rowIndex = rowAtPoint(p);
				int colIndex = columnAtPoint(p);
				
				try
				{
					if (colIndex == 11)
					{
						MouseEvent e2 = new MouseEvent(this, 1, 1, 1,
								e.getLocationOnScreen().x - (getCellRect(rowIndex, colIndex, true).x + getLocationOnScreen().x), 10, 1,
								true);
						tip = ((ColorBar) getValueAt(rowIndex, colIndex)).getToolTipText(e2);
					}
				}
				catch (RuntimeException ex)
				{
					// catch null pointer exception if mouse is over an empty line
				}
				return tip;
			}
		};
		adapter = new JTableSpreadsheetByRowAdapter(summaryTable);
		
		summaryTable.addMouseListener(new MouseAdapter() {
			
			@Override
			public void mousePressed(MouseEvent e) {
				
				if (e.isPopupTrigger())
				{
					showMenu(e);
				}
			}
			
			@Override
			public void mouseReleased(MouseEvent e) {
				
				if (e.isPopupTrigger())
				{
					showMenu(e);
				}
			}
			
			private void showMenu(MouseEvent e) {
				
				JPopupMenu popup = new JPopupMenu();
				
				JMenuItem selectAll = new JMenuItem("Select all");
				selectAll.addActionListener(new ActionListener() {
					
					@Override
					public void actionPerformed(ActionEvent arg0) {
						
						summaryTable.selectAll();
						
					}
					
				});
				
				JMenuItem copy = new JMenuItem("Copy");
				copy.addActionListener(new ActionListener() {
					
					@Override
					public void actionPerformed(ActionEvent e) {
						
						adapter.doCopy();
					}
					
				});
				
				JMenuItem export = new JMenuItem("Export");
				export.addActionListener(new ActionListener() {
					
					@Override
					public void actionPerformed(ActionEvent e) {
						
						exportTable();
						
					}
					
				});
				
				popup.add(selectAll);
				popup.add(copy);
				popup.addSeparator();
				popup.add(export);
				
				popup.show(e.getComponent(), e.getX(), e.getY());
				
			}
		});
		
		summaryTable.getTableHeader().setReorderingAllowed(false);
		scrollPane.setViewportView(summaryTable);
		
		refreshTable();
	}
	
	/**
	 * Export the summary table to an CSV file
	 */
	private void exportTable() {
		
		File f = getOutputFile();
		
		try
		{
			adapter.saveToCSV(f);
		}
		catch (IOException e)
		{
			JOptionPane.showMessageDialog(this, "Error saving file to disk", "Save error", JOptionPane.ERROR_MESSAGE);
		}
	}
	
	/**
	 * Prompt the user for an output filename
	 * 
	 * @param filter
	 * @return
	 */
	public static File getOutputFile() {
		
		String lastVisitedFolder = App.prefs.getPref(PrefKey.PREF_LAST_EXPORT_FOLDER, null);
		File outputFile;
		CSVFileFilter csvff = new CSVFileFilter();
		TXTFileFilter txtff = new TXTFileFilter();
		
		// Create a file chooser
		final JFileChooser fc = new JFileChooser(lastVisitedFolder);
		
		fc.setAcceptAllFileFilterUsed(true);
		
		fc.addChoosableFileFilter(csvff);
		fc.addChoosableFileFilter(txtff);
		
		fc.setFileFilter(csvff);
		
		fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
		fc.setMultiSelectionEnabled(false);
		fc.setDialogTitle("Save as...");
		
		// In response to a button click:
		int returnVal = fc.showOpenDialog(App.mainFrame);
		
		if (returnVal == JFileChooser.APPROVE_OPTION)
		{
			outputFile = fc.getSelectedFile();
			
			if (FileUtils.getExtension(outputFile.getAbsolutePath()) == "")
			{
				log.debug("Output file extension not set by user");
				
				if (fc.getFileFilter().getDescription().equals(new TXTFileFilter().getDescription()))
				{
					log.debug("Adding txt extension to output file name");
					outputFile = new File(outputFile.getAbsolutePath() + ".txt");
				}
				else if (fc.getFileFilter().getDescription().equals(new CSVFileFilter().getDescription()))
				{
					log.debug("Adding csv extension to output file name");
					outputFile = new File(outputFile.getAbsolutePath() + ".csv");
				}
			}
			else
			{
				log.debug("Output file extension set my user to '" + FileUtils.getExtension(outputFile.getAbsolutePath()) + "'");
			}
			
			App.prefs.setPref(PrefKey.PREF_LAST_EXPORT_FOLDER, outputFile.getAbsolutePath());
		}
		else
		{
			return null;
		}
		
		if (outputFile.exists())
		{
			Object[] options = { "Overwrite", "No", "Cancel" };
			int response = JOptionPane.showOptionDialog(App.mainFrame,
					"The file '" + outputFile.getName() + "' already exists.  Are you sure you want to overwrite?", "Confirm",
					JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, // do not use a custom Icon
					options, // the titles of buttons
					options[0]); // default button title
					
			if (response != JOptionPane.YES_OPTION)
			{
				return null;
			}
		}
		
		return outputFile;
	}
	
	/**
	 * Takes in a table and changes its preferred and minimum widths for columns. Also sets the row height.
	 * 
	 * @param table the table on which to set the column widths
	 */
	private void setColumnWidths(JTable table) {
		
		table.getColumnModel().getColumn(0).setPreferredWidth(40);
		table.getColumnModel().getColumn(0).setMinWidth(40);
		table.getColumnModel().getColumn(1).setMinWidth(75);
		
		// Temporarily removing Recording years column
		// table.getColumnModel().getColumn(2).setPreferredWidth(130);
		// table.getColumnModel().getColumn(2).setMinWidth(130);
		
		table.getColumnModel().getColumn(2).setPreferredWidth(100);
		table.getColumnModel().getColumn(2).setMinWidth(100);
		table.getColumnModel().getColumn(3).setMinWidth(75);
		table.getColumnModel().getColumn(4).setPreferredWidth(30);
		table.getColumnModel().getColumn(4).setMinWidth(30);
		table.getColumnModel().getColumn(5).setPreferredWidth(30);
		table.getColumnModel().getColumn(5).setMinWidth(30);
		table.getColumnModel().getColumn(6).setPreferredWidth(30);
		table.getColumnModel().getColumn(6).setMinWidth(30);
		table.getColumnModel().getColumn(7).setPreferredWidth(30);
		table.getColumnModel().getColumn(7).setMinWidth(30);
		table.getColumnModel().getColumn(8).setPreferredWidth(30);
		table.getColumnModel().getColumn(8).setMinWidth(30);
		table.getColumnModel().getColumn(9).setPreferredWidth(30);
		table.getColumnModel().getColumn(9).setMinWidth(30);
		table.getColumnModel().getColumn(10).setPreferredWidth(100);
		table.getColumnModel().getColumn(10).setMinWidth(60);
		table.setAutoResizeMode(JTable.AUTO_RESIZE_SUBSEQUENT_COLUMNS);
		table.setRowHeight(20);
	}
	
	/**
	 * Refreshes all data in the table, adding years and updating existing year data.
	 */
	public void refreshTable() {
		
		DefaultTableModel model = (DefaultTableModel) summaryTable.getModel();
		
		data = FileController.getYearSummaryList();
		int numRows = 0;
		Object[][] rows = new Object[data.size()][];
		DecimalFormat df = new DecimalFormat("#.##");
		
		for (YearSummary year : data)
		{
			
			ColorBar colorBar = null;
			if (numRows < model.getRowCount())
			{
				// use current colorBar if available
				colorBar = (ColorBar) model.getValueAt(numRows, 10);
			}
			
			// Temporarily remove Recording Samples column
			/*
			 * rows[numRows++] = new Object[] { year.getYear(), year.getNumEvents(), year.getNumRecorders(), year.getNumSamples(),
			 * df.format(year.getPercentScarred()) + "%", year.getNumDormantSeason(), year.getNumEarlyEarlywood(),
			 * year.getNumMiddleEarlywood(), year.getNumLateEarlywood(), year.getNumLatewood(), year.getNumUndetermined(), colorBar };
			 */
			
			rows[numRows++] = new Object[] { year.getYear(), year.getNumEvents(), year.getNumSamples(),
					df.format(year.getPercentScarred()) + "%", year.getNumDormantSeason(), year.getNumEarlyEarlywood(),
					year.getNumMiddleEarlywood(), year.getNumLateEarlywood(), year.getNumLatewood(), year.getNumUndetermined(), colorBar };
					
		}
		model.setDataVector(rows, columnHeaders);
		setColumnWidths(summaryTable);
		summaryTable.getColumn("Color Bar").setCellRenderer(new YearSummaryColorBarRenderer());
	}
	
	/**
	 * TODO
	 */
	private void showCustomizeWindow() {
		
		if (new CustomizeDialog(this, true).showDialog())
			refreshTable();
	}
	
	/**
	 * Custom renderer created to put a custom color bar into the table.
	 */
	private class YearSummaryColorBarRenderer implements TableCellRenderer {
		
		@Override
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row,
				int column) {
				
			ColorBar panel = (ColorBar) value;
			
			if (panel == null)
				panel = new ColorBar(data.get(row));
				
			panel.updateChart(data.get(row), FileController.getCustomOptions());
			
			if (isSelected)
			{
				panel.setBackground(table.getSelectionBackground());
				panel.setForeground(table.getSelectionForeground());
			}
			else
			{
				panel.setBackground(table.getBackground());
				panel.setForeground(table.getForeground());
			}
			
			table.setValueAt(panel, row, column);
			return panel;
		}
	}
}
