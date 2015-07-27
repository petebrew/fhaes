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
package org.fhaes.util;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;

import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.KeyStroke;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * JTableSpreadsheetByRowAdapter Class. ExcelAdapter enables Copy-Paste Clipboard functionality on JTables. The clipboard data format used
 * by the adapter is compatible with the clipboard format used by Excel. This provides for clipboard interoperability between enabled
 * JTables and Excel.
 */
public class JTableSpreadsheetByRowAdapter implements ActionListener {
	
	private final static Logger log = LoggerFactory.getLogger(JTableSpreadsheetByRowAdapter.class);
	private Clipboard system;
	private StringSelection stsel;
	private JTable mainTable;
	
	/**
	 * The Excel Adapter is constructed with a JTable on which it enables Copy-Paste and acts as a Clipboard listener.
	 */
	public JTableSpreadsheetByRowAdapter(JTable myJTable) {
		
		mainTable = myJTable;
		KeyStroke copy = KeyStroke.getKeyStroke(KeyEvent.VK_C, ActionEvent.CTRL_MASK, false);
		// Identifying the copy KeyStroke user can modify this
		// to copy on some other Key combination.
		KeyStroke paste = KeyStroke.getKeyStroke(KeyEvent.VK_V, ActionEvent.CTRL_MASK, false);
		// KeyStroke pasteappend =
		// KeyStroke.getKeyStroke(KeyEvent.VK_V,ActionEvent.CTRL_MASK+ActionEvent.SHIFT_MASK,false);
		// Identifying the Paste KeyStroke user can modify this
		// to copy on some other Key combination.
		mainTable.registerKeyboardAction(this, "Copy", copy, JComponent.WHEN_FOCUSED);
		mainTable.registerKeyboardAction(this, "Paste", paste, JComponent.WHEN_FOCUSED);
		system = Toolkit.getDefaultToolkit().getSystemClipboard();
	}
	
	/**
	 * Public Accessor methods for the Table on which this adapter acts.
	 */
	public JTable getJTable() {
		
		return mainTable;
	}
	
	public void setJTable(JTable tbl) {
		
		this.mainTable = tbl;
	}
	
	public void saveToCSV(File f) throws IOException {
		
		StringBuffer sbf = new StringBuffer();
		int numrows = mainTable.getRowCount();
		
		// First add column headers
		for (int c = 0; c < mainTable.getColumnCount(); c++)
		{
			sbf.append(mainTable.getColumnName(c));
			sbf.append("\t");
		}
		sbf.append("\n");
		
		for (int i = 0; i < numrows; i++)
		{
			for (int j = 0; j < mainTable.getColumnCount(); j++)
			{
				
				Object value = mainTable.getValueAt(i, j);
				
				sbf.append(value);
				
				sbf.append("\t");
			}
			sbf.append("\n");
		}
		
		FileUtils.writeStringToFile(f, sbf.toString());
		
	}
	
	public void doCopy() {
		
		log.debug("doCopy() called");
		
		StringBuffer sbf = new StringBuffer();
		// Check to ensure we have selected only a contiguous block of
		// cells
		// int numcols=mainTable.getSelectedColumnCount();
		int numrows = mainTable.getSelectedRowCount();
		int[] rowsselected = mainTable.getSelectedRows();
		// int[] colsselected=mainTable.getSelectedColumns();
		
		/*
		 * log.debug("selected rows: "); for(int i : rowsselected) { log.debug("    - "+i); } log.debug("selected cols: "); for(int i :
		 * colsselected) { log.debug("    - "+i); }
		 * 
		 * if (!((numrows-1==rowsselected[rowsselected.length-1]-rowsselected[0] && numrows==rowsselected.length) &&
		 * (numcols-1==colsselected[colsselected.length-1]-colsselected[0] && numcols==colsselected.length))) {
		 * JOptionPane.showMessageDialog(mainTable, "Invalid copy selection.  Selected cells must be contiguous.", "Invalid",
		 * JOptionPane.ERROR_MESSAGE); return; }
		 */
		
		// First add column headers
		for (int c = 0; c < mainTable.getColumnCount(); c++)
		{
			sbf.append(mainTable.getColumnName(c));
			sbf.append("\t");
		}
		sbf.append("\n");
		
		for (int i = 0; i < numrows; i++)
		{
			for (int j = 0; j < mainTable.getColumnCount(); j++)
			{
				
				Object value = mainTable.getValueAt(rowsselected[i], j);
				
				sbf.append(value);
				
				sbf.append("\t");
			}
			sbf.append("\n");
		}
		stsel = new StringSelection(sbf.toString());
		system = Toolkit.getDefaultToolkit().getSystemClipboard();
		system.setContents(stsel, stsel);
		
	}
	
	public Integer getRowCountFromClipboard() {
		
		log.debug("Clipboard contents: " + system.getName());
		try
		{
			String trstring = (String) (system.getContents(this).getTransferData(DataFlavor.stringFlavor));
			
			String[] lines = StringUtils.splitByLines(trstring);
			
			if (lines.length == 0)
				return null;
				
			Integer lineCount = lines.length;
			
			String firstColName = mainTable.getColumnName(mainTable.getSelectedColumns()[0]);
			
			if (lines[0].startsWith(firstColName))
			{
				lineCount--;
			}
			
			for (String line : lines)
			{
				if (line.trim().length() == 0)
				{
					lineCount--;
				}
			}
			
			return lineCount;
			
		}
		catch (UnsupportedFlavorException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
		
	}
	
	public void doPaste() {
	
	}
	
	public void doPasteAppend() {
	
	}
	
	/**
	 * This method is activated on the Keystrokes we are listening to in this implementation. Here it listens for Copy and Paste
	 * ActionCommands. Selections comprising non-adjacent cells result in invalid selection and then copy action cannot be performed. Paste
	 * is done by aligning the upper left corner of the selection with the 1st element in the current selection of the JTable.
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		
		log.debug("Action command: " + e.getActionCommand());
		
		if (e.getActionCommand().compareTo("Copy") == 0)
		{
			doCopy();
		}
		if (e.getActionCommand().compareTo("Paste") == 0)
		{
			doPaste();
		}
		if (e.getActionCommand().compareTo("PasteAppend") == 0)
		{
			doPaste();
		}
	}
}
