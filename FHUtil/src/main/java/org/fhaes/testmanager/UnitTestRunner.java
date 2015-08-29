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

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * UnitTestRunner Class. This class contains the main method for running all unit tests in the FHAES suite.
 * 
 * @author Joshua Brogan and Peter Brewer
 */
public class UnitTestRunner {
	
	// Declare logger
	private static final Logger log = LoggerFactory.getLogger(UnitTestRunner.class);
	
	/**
	 * Runs all unit tests defined in the FHAES test suite.
	 */
	@Test
	public void runUnitTests() {
		
		Result result = JUnitCore.runClasses(FHAESTestSuite.class);
		
		if (result.getFailureCount() > 0)
		{
			for (Failure failure : result.getFailures())
			{
				log.error(failure.getException().toString());
				log.error("error occurred in: " + failure.getTestHeader());
				
				// Report to the JUnit window that a failure has been encountered
				Assert.fail(failure.getTrace());
			}
		}
		else
		{
			log.info("All tests passed for FHAESTestSuite");
		}
	}
}
