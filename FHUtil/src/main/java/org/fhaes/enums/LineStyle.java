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
package org.fhaes.enums;

/**
 * LineStyle Enum.
 */
public enum LineStyle {
	
	SOLID(1, 0),
	
	DOTTED(1, 1),
	
	DASHED(2, 3),
	
	LONG_DASH(5, 7);
	
	// Declare local variables
	double firstCode = 1;
	double secondCode = 0;
	
	/**
	 * Initialize the firstCode and secondCode values for the LineStyle.
	 * 
	 * @param firstCode
	 * @param secondCode
	 */
	LineStyle(double firstCode, double secondCode) {
		
		this.firstCode = firstCode;
		this.secondCode = secondCode;
	}
	
	/**
	 * TODO
	 * 
	 * @return
	 */
	public String getCode() {
		
		return firstCode + "," + secondCode;
	}
	
	/**
	 * TODO
	 * 
	 * @param scale
	 * @return
	 */
	public String getCode(int scale) {
		
		return (firstCode * scale) + "," + (secondCode * scale);
	}
	
	/**
	 * TODO
	 * 
	 * @param yrs
	 * @return
	 */
	public String getCodeForChartYearCount(int yrs) {
		
		Double scale = (yrs / 500.0);
		
		// Double scale = 1.0 / pxperyear;
		/*
		 * if (yrs <= 50) { scale = 0.05; } else if (yrs <= 100) { scale = 0.1; } else if (yrs <= 200) { scale = 0.2; } else if (yrs <= 300)
		 * { scale = 0.4; } else if (yrs <= 400) { scale = 0.6; } else if (yrs <= 700) { scale = 1.0; } else if (yrs <= 1000) { scale = 2.0;
		 * } else if (yrs <= 1500) { scale = 3.0; } else if (yrs >= 1500) { scale = 4.0; }
		 */
		return (firstCode * scale) + "," + (secondCode * scale);
	}
	
	/**
	 * TODO
	 * 
	 * @param str
	 * @return
	 */
	public static LineStyle fromString(String str) {
		
		if (str.equals(LineStyle.SOLID.toString()))
		{
			return LineStyle.SOLID;
		}
		else if (str.equals(LineStyle.DOTTED.toString()))
		{
			return LineStyle.DOTTED;
		}
		else if (str.equals(LineStyle.DASHED.toString()))
		{
			return LineStyle.DASHED;
		}
		else
		{
			return LineStyle.SOLID;
		}
	}
	
}
