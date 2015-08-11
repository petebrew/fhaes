/**************************************************************************************************
 * Fire History Analysis and Exploration System (FHAES), Copyright (C) 2015
 * 
 * Contributors: Alex Beatty, Clayton Bodendein, Kyle Hartmann, Scott Goble, and Peter Brewer
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
package org.fhaes.fhrecorder.model;

import java.io.Serializable;
import java.util.ArrayList;

import org.fhaes.fhrecorder.util.ErrorTrackerInterface;
import org.fhaes.fhrecorder.util.SampleErrorModel;

/**
 * FHX2_File Class. This class contains both the optional and required parts of the FHX file.
 * 
 * @author Alex Beatty, Clayton Bodendein, Kyle Hartmann, Scott Goble
 */
public class FHX2_File implements Serializable, ErrorTrackerInterface {
	
	private static final long serialVersionUID = 1L;
	
	private FHX2_FileRequiredPart fhx2Req; // Handling the required part of the file
	private FHX2_FileOptionalPart fhx2Opt; // Handling the optional part of the file (the header and the comments)
	
	/**
	 * Default Constructor for FHX2_File to initialize the objects for the Required part and the Optional part of the file
	 */
	public FHX2_File() {
		
		fhx2Req = new FHX2_FileRequiredPart();
		fhx2Opt = new FHX2_FileOptionalPart();
	}
	
	/**
	 * Gets the required part of the FHX2 file which consists of the sample, events and recordings.
	 * 
	 * @return fhx2Req, the required part of the FHX2 file
	 */
	public FHX2_FileRequiredPart getRequiredPart() {
		
		return fhx2Req;
	}
	
	/**
	 * Gets the optional part of the FHX2 file which is the comments and the header for the file
	 * 
	 * @return fhx2Opt, the optional part of the FHX2 file
	 */
	public FHX2_FileOptionalPart getOptionalPart() {
		
		return fhx2Opt;
	}
	
	/**
	 * Checks whether or not the FHX2 file contains valid data. The file does no contain valid data if the optional part has no values and
	 * the required part has no samples
	 * 
	 * @return true if there is no valid data, false otherwise
	 */
	public boolean fileHasNoValidData() {
		
		return (!(fhx2Opt.hasValues()) && !(fhx2Req.getNumSamples() > 0));
	}
	
	/**
	 * Returns a list of all the errors that were detected while loading the file.
	 */
	@Override
	public ArrayList<SampleErrorModel> getErrors() {
		
		ArrayList<SampleErrorModel> errors = new ArrayList<SampleErrorModel>();
		errors.addAll(fhx2Req.getErrors());
		return errors;
	}
}
