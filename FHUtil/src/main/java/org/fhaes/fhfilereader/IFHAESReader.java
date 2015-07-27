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
package org.fhaes.fhfilereader;

import java.io.File;
import java.util.ArrayList;

import org.fhaes.enums.EventTypeToProcess;

/**
 * IFHAESReader Interface. This interface describes the methods that need to be implemented by a class offering the ability to read fire
 * history data from data files. This interface should not be implemented directly but instead through extending the
 * {@link org.fhaes.fhfilereader.AbstractFireHistoryReader} class.
 * 
 * @author Peter Brewer
 */
public interface IFHAESReader {
	
	/**
	 * Get the type of file that is being read.
	 * 
	 * @return
	 */
	public abstract String getFileFormat();
	
	/**
	 * Whether the file passes rudimentary syntax checks.
	 * 
	 * @return
	 */
	public abstract boolean passesBasicSyntaxCheck();
	
	/**
	 * Returns the name of the file being read. This is just the last name in the pathname's name sequence. If the pathname's name sequence
	 * is empty, then the empty string is returned.
	 * 
	 * @return String of the name of the file being read
	 */
	public abstract String getName();
	
	/**
	 * Get the file being read.
	 * 
	 * @return
	 */
	public abstract File getFile();
	
	/**
	 * Get the first year in which an indicator is found within this file.
	 * 
	 * @return
	 */
	public abstract Integer getFirstIndicatorYear();
	
	/**
	 * Get the year in which the first injury is found.
	 * 
	 * @return
	 */
	public abstract Integer getFirstInjuryYear();
	
	/**
	 * <p>
	 * Get an ArrayList with length equal to number of years in file, containing Integer codes meaning.
	 * </p>
	 * <ul>
	 * <li>1 = fire event</li>
	 * <li>0 = injury event or recording year</li>
	 * <li>-1 = no data</li>
	 * </ul>
	 * 
	 * @return
	 */
	public abstract ArrayList<Integer> getFireEventsArray();
	
	/**
	 * <p>
	 * Get an ArrayList with length equal to number of years in file, containing Integer codes meaning.
	 * </p>
	 * <ul>
	 * <li>1 = injury event</li>
	 * <li>0 = fire event or recording year</li>
	 * <li>-1 = no data</li>
	 * </ul>
	 * 
	 * @return
	 */
	public abstract ArrayList<Integer> getOtherInjuriesArray();
	
	/**
	 * <p>
	 * Get an ArrayList with length equal to number of years in file, containing Integer codes meaning.
	 * </p>
	 * <ul>
	 * <li>1 = fire or injury event</li>
	 * <li>0 = recording year</li>
	 * <li>-1 = no data</li>
	 * </ul>
	 * 
	 * @return
	 */
	public abstract ArrayList<Integer> getFiresAndInjuriesArray();
	
	/**
	 * Get an ArrayList of years contained within this file.
	 * 
	 * @return
	 */
	public abstract ArrayList<Integer> getYearArray();
	
	/**
	 * Returns an array of strings, each containing the data portion of the FHX file minus any year value on the end. Each string will
	 * contain the same number of characters as there are series in the file.
	 * 
	 * @see getRawRowData()
	 * @return
	 */
	public abstract ArrayList<String> getData();
	
	/**
	 * Returns an array of rows containing the raw character data extracted from the data block of the FHX file.
	 * 
	 * @see getData()
	 * @return
	 */
	public abstract ArrayList<String> getRawRowData();
	
	/**
	 * Get ArrayList of line numbers for all data lines that contain errors.
	 * 
	 * @return
	 */
	public abstract ArrayList<Integer> getBadDataLineNumbers();
	
	/**
	 * TODO Documentation needed
	 * 
	 * @return
	 */
	public abstract int[] getTotals();
	
	/**
	 * <p>
	 * Get a multi-dimensional array with rows = number of years, and columns = number of samples. The integer values within the arrays mean
	 * the following:
	 * </p>
	 * 
	 * <ul>
	 * <li>-1 = no data (equivalent to a dot in the data file)</li>
	 * <li>1 = event (a letter in the data file)</li>
	 * <li>0 = susceptible to fire (recording) but no event detected (a pipe in the file)</li>
	 * </ul>
	 * 
	 * @param eventType
	 * @return
	 */
	public abstract ArrayList<ArrayList<Integer>> getEventDataArrays(EventTypeToProcess eventType);
	
	/**
	 * TODO Documentation needed
	 * 
	 * @return
	 */
	public abstract ArrayList<ArrayList<Integer>> getCapsYearperSample2d();
	
	/**
	 * TODO Documentation needed
	 * 
	 * @return
	 */
	public abstract ArrayList<ArrayList<Integer>> getCalosYearperSample2d();
	
	/**
	 * TODO Documentation needed
	 * 
	 * @return
	 */
	public abstract ArrayList<ArrayList<Character>> getCapsperSample2d();
	
	/**
	 * TODO Documentation needed
	 * 
	 * @return
	 */
	public abstract ArrayList<ArrayList<Character>> getCalosperSample2d();
	
	/**
	 * <p>
	 * Get a multi-dimensional array with rows = number of years, and columns = 3.
	 * </p>
	 * 
	 * <ul>
	 * <li>Column 0 = number of events</li>
	 * <li>Column 1 = number of trees</li>
	 * <li>Column 2 = percentage of scarred trees</li>
	 * </ul>
	 * 
	 * <p>
	 * This array is typically used when wanting to filter the data by number of percentage of events.
	 * </p>
	 * 
	 * @param eventType
	 * @return
	 */
	public abstract ArrayList<ArrayList<Double>> getFilterArrays(EventTypeToProcess eventType);
	
	/**
	 * Returns an int[] with one entry per sample in the file with each int containing the index to the earliest year for the sample.
	 * 
	 * @see getStartYearPerSample()
	 * @return
	 */
	public abstract int[] getStartYearIndexPerSample();
	
	/**
	 * Returns an int[] with one entry per sample in the file with each int containing the year number for the sample.
	 * 
	 * @see getStartYearIndexPerSample()
	 * @return
	 */
	public abstract int[] getStartYearPerSample();
	
	/**
	 * Returns an int[] with one entry per sample in the file with each int containing the index to the last year for the sample.
	 * 
	 * @see getLastYearPerSample()
	 * @return
	 */
	public abstract int[] getLastYearIndexPerSample();
	
	/**
	 * Returns an int[] with one entry per sample in the file with each int containing the year number for the last year in the sample.
	 * 
	 * @see getLastYearIndexPerSample()
	 * @return
	 */
	public abstract int[] getLastYearPerSample();
	
	/**
	 * Returns an int[] with one entry per sample in the file with each int containing the index to the year in which the pith for the
	 * sample is found. In series where there is no pith, the index to the first year is returned instead.
	 * 
	 * @return
	 */
	public abstract int[] getPithIndexPerSample();
	
	/**
	 * Get an int array with one entry per sample in the file with each int containing the count of the number of recorder years.
	 * 
	 * @return
	 */
	public abstract int[] getTotalRecorderYearsPerSample();
	
	/**
	 * Get an array containing the index (not year) of the innermost (earliest) ring for each sample. The array will contain one value per
	 * series in the file, and will be in the order the series are arranged in the file.
	 * 
	 * @return int[]
	 * @see getInnerMostYearPerTree()
	 */
	public abstract int[] getInnerMostperTree();
	
	/**
	 * Get an array containing the year (not index) of the innermost (earliest) ring for each sample. The array will contain one value per
	 * series in the file, and will be in the order the series are arranged in the file.
	 * 
	 * @return
	 */
	public abstract int[] getInnerMostYearPerTree();
	
	/**
	 * Get an array containing the index (not year) of the outermost (most recent) ring for each sample. The array with contain one value
	 * per series in the file, and will be in the order the series are arranged in the file.
	 * 
	 * @return int[]
	 */
	public abstract int[] getOutterMostperTree();
	
	/**
	 * Get an array containing the year (not index) of the outermost (most recent) ring for each sample. The array with contain one value
	 * per series in the file, and will be in the order the series are arranged in the file.
	 * 
	 * @return int[]
	 */
	public abstract int[] getOuterMostYearPerTree();
	
	/**
	 * Returns an int[] with one entry per sample in the file with each int containing the index to the year in which the bark for the
	 * sample is found. In series where there is no bark, the index to the last year is returned instead.
	 * 
	 * @return
	 */
	public abstract int[] getBarkIndexPerTree();
	
	/**
	 * Get the first year in the file as indicated by the file header.
	 * 
	 * @return
	 */
	public abstract Integer getFirstYear();
	
	/**
	 * Get an ArrayList of the series names from this file.
	 * 
	 * @return
	 */
	public abstract ArrayList<String> getSeriesNameArray();
	
	/**
	 * Whether this file contains any fire events or injuries.
	 * 
	 * @return boolean
	 */
	public abstract boolean hasFireEventsOrInjuries();
	
	/**
	 * Whether this file contains any fire events.
	 * 
	 * @return boolean
	 */
	public abstract boolean hasFireEvents();
	
	/**
	 * Whether this file contains any injury events.
	 * 
	 * @return boolean
	 */
	public abstract boolean hasInjuryEvents();
	
	/**
	 * Get the number of series contained in this file.
	 * 
	 * @return Integer number of series
	 */
	public abstract Integer getNumberOfSeries();
	
	/**
	 * Get the maximum length of the series names specified in the file header.
	 * 
	 * @return Integer maximum series name length
	 */
	public abstract Integer getLengthOfSeriesName();
	
	/**
	 * Get the last (most recent) year in the file. Calculated from first year as reported by the header plus the number of years of data.
	 * 
	 * @return
	 */
	public abstract Integer getLastYear();
	
	/**
	 * TODO ????
	 */
	public abstract void makeDecompSyb2d();
	
	/**
	 * Get the year in which the first fire is recorded.
	 * 
	 * @return
	 */
	public abstract Integer getFirstFireYear();
	
	/**
	 * Returns a 2D array containing information on when the samples are in recording status or not. The array is of size - sample number x
	 * years in file. The array contains Integers with the meaning:
	 * <ul>
	 * <li>-1 = outside the time period for the sample</li>
	 * <li>0 = not in recording status</li>
	 * <li>1 = sample is in recording status</li>
	 * </ul>
	 * 
	 * @return
	 */
	public abstract ArrayList<ArrayList<Integer>> getRecorderYears2DArray();
	
}
