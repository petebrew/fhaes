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
package org.fhaes.testmanager;

import org.fhaes.components.BCADYearSpinner.BCADYearSpinnerUnitTest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * UnitTestManager Class. This class contains the main method for running all unit tests in the FHAES suite.
 * 
 * @author Joshua Brogan and Peter Brewer
 */
public class UnitTestManager {
	
	// Declare logger
	private static final Logger log = LoggerFactory.getLogger(UnitTestManager.class);
	
	/**
	 * Run the defined unit tests.
	 */
	public static void main(final String[] args) {
		
		try
		{
			BCADYearSpinnerUnitTest.runTest();
			log.info("All tests passed for BCADYearSpinner");
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			log.error("Unit testing failed (see stack trace for more information)");
		}
	}
}
