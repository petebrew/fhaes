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
