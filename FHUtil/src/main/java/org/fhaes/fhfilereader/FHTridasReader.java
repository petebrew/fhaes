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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
//import org.tridas.io.exceptions.InvalidDendroFileException;
//import org.tridas.io.formats.tridas.TridasReader;
//import org.tridas.schema.TridasTridas;

/**
 * FHTridasReader Class. Skeleton of new TRiDaS Reader class that when complete can be used to replace FHX2FileReader.
 * 
 * @author Peter Brewer
 */
public class FHTridasReader extends AbstractFireHistoryReader {
	
	@SuppressWarnings("unused")
	private static final Logger log = LoggerFactory.getLogger(FHTridasReader.class);
	
	// private TridasTridas container;
	private final File file;
	private final boolean hasFailed = false;
	
	public FHTridasReader(File file) {
		
		// container = null;
		this.file = file;
		
		// TridasReader reader = new TridasReader();
		// try {
		// reader.loadFile(file.getAbsolutePath());
		// } catch (IOException e) {
		// log.info(e.getLocalizedMessage());
		// hasFailed = true;
		// } catch (InvalidDendroFileException e) {
		// e.printStackTrace();
		// hasFailed = true;
		// }
		//
		// // Extract the TridasProject
		// container = reader.getTridasContainer();
	}
	
	@Override
	public String getFileFormat() {
		
		return "TRiDaS";
	}
	
	@Override
	public boolean passesBasicSyntaxCheck() {
		
		return !hasFailed;
	}
	
	@Override
	public String getName() {
		
		return file.getName();
	}
	
	@Override
	public File getFile() {
		
		return file;
	}
	
	@Override
	public Integer getFirstIndicatorYear() {
		
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public Integer getFirstInjuryYear() {
		
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public ArrayList<Integer> getFireEventsArray() {
		
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public ArrayList<Integer> getOtherInjuriesArray() {
		
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public ArrayList<Integer> getFiresAndInjuriesArray() {
		
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public ArrayList<Integer> getYearArray() {
		
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public ArrayList<String> getData() {
		
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public ArrayList<String> getRawRowData() {
		
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public ArrayList<Integer> getBadDataLineNumbers() {
		
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public int[] getTotals() {
		
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public ArrayList<ArrayList<Integer>> getEventDataArrays(EventTypeToProcess eventType) {
		
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public ArrayList<ArrayList<Integer>> getCapsYearperSample2d() {
		
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public ArrayList<ArrayList<Integer>> getCalosYearperSample2d() {
		
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public ArrayList<ArrayList<Character>> getCapsperSample2d() {
		
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public ArrayList<ArrayList<Character>> getCalosperSample2d() {
		
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public ArrayList<ArrayList<Double>> getFilterArrays(EventTypeToProcess eventType) {
		
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public int[] getStartYearIndexPerSample() {
		
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public int[] getStartYearPerSample() {
		
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public int[] getLastYearIndexPerSample() {
		
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public int[] getLastYearPerSample() {
		
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public int[] getPithIndexPerSample() {
		
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public int[] getTotalRecorderYearsPerSample() {
		
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public int[] getInnerMostperTree() {
		
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public int[] getInnerMostYearPerTree() {
		
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public int[] getOutterMostperTree() {
		
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public int[] getOuterMostYearPerTree() {
		
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public int[] getBarkIndexPerTree() {
		
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public Integer getFirstYear() {
		
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public ArrayList<String> getSeriesNameArray() {
		
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public boolean hasFireEventsOrInjuries() {
		
		// TODO Auto-generated method stub
		return false;
	}
	
	@Override
	public boolean hasFireEvents() {
		
		// TODO Auto-generated method stub
		return false;
	}
	
	@Override
	public boolean hasInjuryEvents() {
		
		// TODO Auto-generated method stub
		return false;
	}
	
	@Override
	public Integer getNumberOfSeries() {
		
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public Integer getLengthOfSeriesName() {
		
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public Integer getLastYear() {
		
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public void makeDecompSyb2d() {
		
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public Integer getFirstFireYear() {
		
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	protected void populateSeriesList() {
		
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public ArrayList<ArrayList<Integer>> getRecorderYears2DArray() {
		
		// TODO Auto-generated method stub
		return null;
	}
	
}
