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
package org.fhaes.neofhchart.util;

import java.util.ArrayList;

import org.fhaes.model.FHSeries;
import org.fhaes.neofhchart.SeriesSVG;

/**
 * ChartController Class.
 * 
 * @author Joshua Brogan and Peter Brewer
 */
public class ConversionUtil {
	
	/**
	 * Converts an array list of FHseries objects to an array list of SeriesSVG objects.
	 * 
	 * @param list
	 * @return
	 */
	public static ArrayList<SeriesSVG> convertFHSeriesToSeriesSVGList(ArrayList<FHSeries> list) {
		
		ArrayList<SeriesSVG> svgSeriesList = new ArrayList<SeriesSVG>();
		
		for (FHSeries series : list)
		{
			try
			{
				svgSeriesList.add(new SeriesSVG(series));
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
		
		return svgSeriesList;
	}
}
