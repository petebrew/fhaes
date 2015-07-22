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
 * @author pwb48
 */
public class FHTridasReader extends AbstractFireHistoryReader {

	@SuppressWarnings("unused")
	private static final Logger log = LoggerFactory.getLogger(FHTridasReader.class);

	// private TridasTridas container;
	private File file;
	private boolean hasFailed = false;

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

	public String getFileFormat() {

		return "TRiDaS";
	}

	public boolean passesBasicSyntaxCheck() {

		return !hasFailed;
	}

	public String getName() {

		return file.getName();
	}

	public File getFile() {

		return file;
	}

	public Integer getFirstIndicatorYear() {

		// TODO Auto-generated method stub
		return null;
	}

	public Integer getFirstInjuryYear() {

		// TODO Auto-generated method stub
		return null;
	}

	public ArrayList<Integer> getFireEventsArray() {

		// TODO Auto-generated method stub
		return null;
	}

	public ArrayList<Integer> getOtherInjuriesArray() {

		// TODO Auto-generated method stub
		return null;
	}

	public ArrayList<Integer> getFiresAndInjuriesArray() {

		// TODO Auto-generated method stub
		return null;
	}

	public ArrayList<Integer> getYearArray() {

		// TODO Auto-generated method stub
		return null;
	}

	public ArrayList<String> getData() {

		// TODO Auto-generated method stub
		return null;
	}

	public ArrayList<String> getRawRowData() {

		// TODO Auto-generated method stub
		return null;
	}

	public ArrayList<Integer> getBadDataLineNumbers() {

		// TODO Auto-generated method stub
		return null;
	}

	public int[] getTotals() {

		// TODO Auto-generated method stub
		return null;
	}

	public ArrayList<ArrayList<Integer>> getEventDataArrays(EventTypeToProcess eventType) {

		// TODO Auto-generated method stub
		return null;
	}

	public ArrayList<ArrayList<Integer>> getCapsYearperSample2d() {

		// TODO Auto-generated method stub
		return null;
	}

	public ArrayList<ArrayList<Integer>> getCalosYearperSample2d() {

		// TODO Auto-generated method stub
		return null;
	}

	public ArrayList<ArrayList<Character>> getCapsperSample2d() {

		// TODO Auto-generated method stub
		return null;
	}

	public ArrayList<ArrayList<Character>> getCalosperSample2d() {

		// TODO Auto-generated method stub
		return null;
	}

	public ArrayList<ArrayList<Double>> getFilterArrays(EventTypeToProcess eventType) {

		// TODO Auto-generated method stub
		return null;
	}

	public int[] getStartYearIndexPerSample() {

		// TODO Auto-generated method stub
		return null;
	}

	public int[] getStartYearPerSample() {

		// TODO Auto-generated method stub
		return null;
	}

	public int[] getLastYearIndexPerSample() {

		// TODO Auto-generated method stub
		return null;
	}

	public int[] getLastYearPerSample() {

		// TODO Auto-generated method stub
		return null;
	}

	public int[] getPithIndexPerSample() {

		// TODO Auto-generated method stub
		return null;
	}

	public int[] getTotalRecorderYearsPerSample() {

		// TODO Auto-generated method stub
		return null;
	}

	public int[] getInnerMostperTree() {

		// TODO Auto-generated method stub
		return null;
	}

	public int[] getInnerMostYearPerTree() {

		// TODO Auto-generated method stub
		return null;
	}

	public int[] getOutterMostperTree() {

		// TODO Auto-generated method stub
		return null;
	}

	public int[] getOuterMostYearPerTree() {

		// TODO Auto-generated method stub
		return null;
	}

	public int[] getBarkIndexPerTree() {

		// TODO Auto-generated method stub
		return null;
	}

	public Integer getFirstYear() {

		// TODO Auto-generated method stub
		return null;
	}

	public ArrayList<String> getSeriesNameArray() {

		// TODO Auto-generated method stub
		return null;
	}

	public boolean hasFireEventsOrInjuries() {

		// TODO Auto-generated method stub
		return false;
	}

	public boolean hasFireEvents() {

		// TODO Auto-generated method stub
		return false;
	}

	public boolean hasInjuryEvents() {

		// TODO Auto-generated method stub
		return false;
	}

	public Integer getNumberOfSeries() {

		// TODO Auto-generated method stub
		return null;
	}

	public Integer getLengthOfSeriesName() {

		// TODO Auto-generated method stub
		return null;
	}

	public Integer getLastYear() {

		// TODO Auto-generated method stub
		return null;
	}

	public void makeDecompSyb2d() {

		// TODO Auto-generated method stub

	}

	public Integer getFirstFireYear() {

		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected void populateSeriesList() {

		// TODO Auto-generated method stub

	}

	public ArrayList<ArrayList<Integer>> getRecorderYears2DArray() {

		// TODO Auto-generated method stub
		return null;
	}

}
