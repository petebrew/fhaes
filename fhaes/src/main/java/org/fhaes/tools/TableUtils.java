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
package org.fhaes.tools;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import javax.swing.JOptionPane;
import javax.swing.table.TableModel;

import org.fhaes.preferences.App;
import org.jdesktop.swingx.JXTable;

/**
 * TableUtils Class.
 */
public class TableUtils {
	
	/**
	 * Static function for exporting a JXTable to a CSV text file
	 * 
	 * @param table - table to export
	 * @param file - file to export to
	 */
	public static void exportToCSV(JXTable table, File file) {
		
		int i = 0;
		int j = 0;
		
		try
		{
			TableModel model = table.getModel();
			FileWriter csv = new FileWriter(file);
			
			for (i = 0; i < model.getColumnCount(); i++)
			{
				csv.write(model.getColumnName(i) + ",");
			}
			
			csv.write(System.getProperty("line.separator"));
			
			for (i = 0; i < model.getRowCount(); i++)
			{
				for (j = 0; j < (model.getColumnCount()); j++)
				{
					if (model.getValueAt(i, j) == null)
					{
						csv.write("" + ",");
					}
					else
					{
						csv.write(model.getValueAt(i, j).toString() + ",");
					}
				}
				csv.write(System.getProperty("line.separator"));
			}
			csv.close();
		}
		catch (IOException e)
		{
			JOptionPane.showMessageDialog(App.mainFrame, "Error saving file '" + file.getName() + "'\n" + e.getLocalizedMessage());
			e.printStackTrace();
		}
	}
}
