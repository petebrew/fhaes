/**************************************************************************************************
 * Fire History Analysis and Exploration System (FHAES), Copyright (C) 2015
 * 
 * Contributors: Elena Velasquez and Peter Brewer
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

import java.io.Reader;
import java.util.Arrays;
import java.util.Scanner;
import java.util.Vector;

import org.fhaes.model.ReadOnlyDefaultTableModel;

/**
 * TableUtil Class.
 */
public class TableUtil {
	
	/**
	 * TODO
	 * 
	 * @param in A CSV input stream to parse
	 * @param headers A Vector containing the column headers. If this is null, it's assumed that the first row contains column headers
	 * 			
	 * @return A DefaultTableModel containing the CSV values as type String
	 */
	public static ReadOnlyDefaultTableModel createTableModel(Reader in, Vector<Object> headers) {
		
		ReadOnlyDefaultTableModel model = null;
		Scanner s = null;
		
		try
		{
			Vector<Vector<Object>> rows = new Vector<Vector<Object>>();
			s = new Scanner(in);
			
			while (s.hasNextLine())
			{
				rows.add(new Vector<Object>(Arrays.asList(s.nextLine().split("\\s*,\\s*", -1))));
			}
			
			if (headers == null && rows.size() > 0)
			{
				headers = rows.remove(0);
				model = new ReadOnlyDefaultTableModel(rows, headers);
			}
			else
			{
				model = new ReadOnlyDefaultTableModel(rows, headers);
			}
			
			return model;
		}
		finally
		{
			s.close();
		}
	}
	
	/**
	 * TODO
	 * 
	 * @param in A CSV input stream to parse
	 * @param headers A Vector containing the column headers. If this is null, it's assumed that the first row contains column headers
	 * 			
	 * @return A DefaultTableModel containing the CSV values as type String
	 */
	public static ReadOnlyDefaultTableModel createTableModel(String str, Vector<Object> headers) {
		
		ReadOnlyDefaultTableModel model = null;
		Vector<Vector<Object>> rows = new Vector<Vector<Object>>();
		String[] lines = str.split("\r\n");
		
		if (lines.length == 1)
		{
			// Looks like the file doesn't have Windows line feeds, so try proper \n feeds instead
			lines = str.split("\n");
		}
		
		for (String line : lines)
		{
			rows.add(new Vector<Object>(Arrays.asList(line.split("\\s*\\t\\s*", -1))));
		}
		
		if (headers == null && rows.size() > 0)
		{
			headers = rows.remove(0);
			model = new ReadOnlyDefaultTableModel(rows, headers);
		}
		else
		{
			model = new ReadOnlyDefaultTableModel(rows, headers);
		}
		
		return model;
	}
}
