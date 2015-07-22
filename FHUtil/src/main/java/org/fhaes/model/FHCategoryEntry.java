package org.fhaes.model;

/**
 * FHCategoryEntry Class.
 * 
 * @author Joshua Brogan and Peter Brewer
 */
public class FHCategoryEntry {

	// Declare local variables
	private final String seriesTitle;
	private final String category;
	private final String content;

	/**
	 * Initializes a new FHCategoryEntry.
	 * 
	 * @param inCategory, the actual category classification (e.g. genus, species)
	 * @param inContent, the content of this category entry
	 */
	public FHCategoryEntry(String inSeriesTitle, String inCategory, String inContent) {

		this.seriesTitle = inSeriesTitle;
		this.category = inCategory;
		this.content = inContent;
	}

	/**
	 * Copy constructor for FHCategoryEntry.
	 * 
	 * @param inEntry
	 */
	public FHCategoryEntry(FHCategoryEntry inEntry) {

		this.seriesTitle = inEntry.seriesTitle;
		this.category = inEntry.category;
		this.content = inEntry.content;
	}

	/**
	 * Getter for seriesTitle field.
	 * 
	 * @return category
	 */
	public String getSeriesTitle() {

		return seriesTitle;
	}

	/**
	 * Getter for category field.
	 * 
	 * @return category
	 */
	public String getCategory() {

		return category;
	}

	/**
	 * Getter for content field.
	 * 
	 * @return content
	 */
	public String getContent() {

		return content;
	}
}
