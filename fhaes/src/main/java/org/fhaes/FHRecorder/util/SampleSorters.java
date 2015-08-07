/**************************************************************************************************
 * Fire History Analysis and Exploration System (FHAES), Copyright (C) 2015
 * 
 * Contributors: Josh Brogan, Jake Lokkesmoe, Chinmay Shah, Scott Goble, and Peter Brewer
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
package org.fhaes.FHRecorder.util;

import java.util.Collections;

import org.fhaes.FHRecorder.compare.CompareSampleFirstYearAscending;
import org.fhaes.FHRecorder.compare.CompareSampleFirstYearDescending;
import org.fhaes.FHRecorder.compare.CompareSampleLastYearAscending;
import org.fhaes.FHRecorder.compare.CompareSampleLastYearDescending;
import org.fhaes.FHRecorder.compare.CompareSampleNameAscending;
import org.fhaes.FHRecorder.compare.CompareSampleNameDescending;
import org.fhaes.FHRecorder.controller.IOController;
import org.fhaes.FHRecorder.controller.SampleController;
import org.fhaes.FHRecorder.model.FHX2_FileRequiredPart;

/**
 * SampleSorters Class. This class contains the comparator functions of the service layer.
 * 
 * @author Josh Brogan, Jake Lokkesmoe, Chinmay Shah, Scott Goble
 */
public class SampleSorters {
	
	/**
	 * Sorts the samples of the currently loaded FHX2 file by name in ascending order.
	 */
	public static void sortSampleNameAscending() {
		
		FHX2_FileRequiredPart temp = IOController.getFile().getRequiredPart();
		Collections.sort(temp.getSampleList(), new CompareSampleNameAscending());
		SampleController.setSelectedSampleIndex(SampleController.getSelectedSampleIndex());
	}
	
	/**
	 * Sorts the samples of the currently loaded FHX2 file by name in descending order.
	 */
	public static void sortSampleNameDescending() {
		
		FHX2_FileRequiredPart temp = IOController.getFile().getRequiredPart();
		Collections.sort(temp.getSampleList(), new CompareSampleNameDescending());
		SampleController.setSelectedSampleIndex(SampleController.getSelectedSampleIndex());
	}
	
	/**
	 * Sorts the samples of the currently loaded FHX2 file by first year in ascending order.
	 */
	public static void sortSampleFirstYearAscending() {
		
		FHX2_FileRequiredPart temp = IOController.getFile().getRequiredPart();
		Collections.sort(temp.getSampleList(), new CompareSampleFirstYearAscending());
		SampleController.setSelectedSampleIndex(SampleController.getSelectedSampleIndex());
	}
	
	/**
	 * Sorts the samples of the currently loaded FHX2 file by first year in descending order.
	 */
	public static void sortSampleFirstYearDescending() {
		
		FHX2_FileRequiredPart temp = IOController.getFile().getRequiredPart();
		Collections.sort(temp.getSampleList(), new CompareSampleFirstYearDescending());
		SampleController.setSelectedSampleIndex(SampleController.getSelectedSampleIndex());
	}
	
	/**
	 * Sorts the samples of the currently loaded FHX2 file by last year in ascending order.
	 */
	public static void sortSampleLastYearAscending() {
		
		FHX2_FileRequiredPart temp = IOController.getFile().getRequiredPart();
		Collections.sort(temp.getSampleList(), new CompareSampleLastYearAscending());
		SampleController.setSelectedSampleIndex(SampleController.getSelectedSampleIndex());
	}
	
	/**
	 * Sorts the samples of the currently loaded FHX2 file by last year in descending order.
	 */
	public static void sortSampleLastYearDescending() {
		
		FHX2_FileRequiredPart temp = IOController.getFile().getRequiredPart();
		Collections.sort(temp.getSampleList(), new CompareSampleLastYearDescending());
		SampleController.setSelectedSampleIndex(SampleController.getSelectedSampleIndex());
	}
}
