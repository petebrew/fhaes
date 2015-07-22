/*******************************************************************************
 * Copyright (C) 2014 Josh Brogan, Jake Lokkesmoe, Chinmay Shah, Scott Goble
 * and Peter Brewer
 * 
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 * 
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 * 
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/

/*******************************************************************************
 * Maintenance Log (Spring 2014)
 * 
 *     All maintenance work was performed collectively by Josh Brogan, 
 *     Jake Lokkesmoe and Chinmay Shah.
 *     
 *     1) This file was added as part of the error handling interface.
 ******************************************************************************/
package org.fhaes.fhrecorder.utility;

import java.util.ArrayList;

/**
 * ErrorTrackerInterface Class.
 * 
 * @author Josh Brogan, Jake Lokkesmoe, Chinmay Shah, Scott Goble
 */
public interface ErrorTrackerInterface {

	/**
	 * TODO
	 * 
	 * @return
	 */
	public ArrayList<SampleErrorModel> getErrors();
}
