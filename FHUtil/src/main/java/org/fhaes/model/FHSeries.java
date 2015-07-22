package org.fhaes.model;

import java.util.ArrayList;

/**
 * FHSeries Class.
 */
public class FHSeries {

	// Declare local variables
	private final String title;
	private final int firstYear;
	private final boolean pith;
	private final boolean bark;
	private final boolean[] recordingYears;
	private final boolean[] eventYears;
	private final boolean[] injuryYears;
	private ArrayList<FHCategoryEntry> categoryEntries;

	/**
	 * This class is a container for data about a single series within a fire history data file.
	 * 
	 * @param title, the name which identifies the series in the file
	 * @param firstYear
	 * @param hasPith
	 * @param hasBark
	 * @param recordingYears
	 * @param eventYears
	 * @param injuryYears
	 * @throws Exception
	 */
	public FHSeries(String title, int firstYear, boolean hasPith, boolean hasBark, boolean[] recordingYears, boolean[] eventYears,
			boolean[] injuryYears) throws Exception {

		// Sanity checks
		if (title == null)
			throw new NullPointerException();
		if (recordingYears == null)
			throw new NullPointerException();
		if (eventYears == null)
			throw new NullPointerException();
		if (injuryYears == null)
			throw new NullPointerException();
		if (recordingYears.length != eventYears.length || recordingYears.length != injuryYears.length)
		{
			throw new Exception("Reading years, event years and injury years arrays must be the same size");
		}

		this.title = title;
		this.firstYear = firstYear;
		this.pith = hasPith;
		this.bark = hasBark;
		this.recordingYears = recordingYears;
		this.eventYears = eventYears;
		this.injuryYears = injuryYears;
		this.categoryEntries = new ArrayList<FHCategoryEntry>();
	}

	/**
	 * Copy constructor for FHSeries.
	 * 
	 * @param inSeries
	 */
	public FHSeries(FHSeries inSeries) {

		this.title = inSeries.title;
		this.firstYear = inSeries.firstYear;
		this.pith = inSeries.pith;
		this.bark = inSeries.bark;
		this.recordingYears = inSeries.recordingYears;
		this.eventYears = inSeries.eventYears;
		this.injuryYears = inSeries.injuryYears;
		this.categoryEntries = new ArrayList<FHCategoryEntry>();
		this.categoryEntries.addAll(inSeries.getCategoryEntries());
	}

	/**
	 * Get the title/label for this series.
	 * 
	 * @return title
	 */
	public String getTitle() {

		return title;
	}

	/**
	 * Get the year for the first year in this series e.g. the year of the first ring of the sample - the pith if present.
	 * 
	 * @return firstYear
	 */
	public int getFirstYear() {

		return firstYear;
	}

	/**
	 * Get the year for the last year for this series e.g. the year of the last ring of the sample - the bark if present.
	 * 
	 * @return the last year for this series
	 */
	public int getLastYear() {

		return firstYear + recordingYears.length - 1;
	}

	/**
	 * Get the number of years that this series covers.
	 * 
	 * @return the number of years this series covers
	 */
	public int getLength() {

		return recordingYears.length;
	}

	/**
	 * Returns a boolean indicating whether the sample had pith or not.
	 * 
	 * @return pith
	 */
	public boolean hasPith() {

		return pith;
	}

	/**
	 * Returns a boolean indicating whether the sample had bark or not.
	 * 
	 * @return bark
	 */
	public boolean hasBark() {

		return bark;
	}

	/**
	 * Get a boolean[] with a size the same as the number of years this series covers. The boolean values will be false if the tree was not
	 * in 'recording status' for the year, or true if it was.
	 * 
	 * @return recordingYears
	 */
	public boolean[] getRecordingYears() {

		return recordingYears;
	}

	/**
	 * Get a boolean[] with a size the same as the number of years this series covers. The boolean values will be false if the tree did not
	 * record a fire event in the year, and true if it did.
	 * 
	 * @return eventYears
	 */
	public boolean[] getEventYears() {

		return eventYears;
	}

	/**
	 * Get a boolean[] with a size the same as the number of years this series covers. The boolean values will be false if the tree did not
	 * record an injury event in the year, and true if it did.
	 * 
	 * @return injuryYears
	 */
	public boolean[] getInjuryYears() {

		return injuryYears;
	}

	/**
	 * Get the list of category entries.
	 * 
	 * @return categoryEntries
	 */
	public ArrayList<FHCategoryEntry> getCategoryEntries() {

		return categoryEntries;
	}
}
