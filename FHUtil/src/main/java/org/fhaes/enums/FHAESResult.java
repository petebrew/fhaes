/**************************************************************************************************
 * Fire History Analysis and Exploration System (FHAES), Copyright (C) 2015
 * 
 * Contributors: Elena Velasquez and Peter Brewer
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
package org.fhaes.enums;

/**
 * FHAESResult Enum.
 */
public enum FHAESResult {
	
	JACCARD_SIMILARITY_MATRIX("JACCARD similarity matrix", "JACCARD"),
	
	COHEN_SIMILARITITY_MATRIX("COHEN similarity matrix", "COHEN"),
	
	JACCARD_SIMILARITY_MATRIX_D("JACCARD dissimilarity matrix", "JACCARD"),
	
	COHEN_SIMILARITITY_MATRIX_D("COHEN dissimilarity matrix", "COHEN"),
	
	INTERVAL_SUMMARY("Interval summary", "Summary"),
	
	INTERVAL_EXCEEDENCE_TABLE("Interval exceedence table", "Exceedence table"),
	
	SEASONALITY_SUMMARY("Seasonality summary", "Summary"),
	
	BINARY_MATRIX_11("Binary matrix A (1-1)", "Matrix A (1-1)"),
	
	BINARY_MATRIX_01("Binary matrix B (0-1)", "Matrix B (0-1)"),
	
	BINARY_MATRIX_10("Binary matrix C (1-0)", "Matrix C (1-0)"),
	
	BINARY_MATRIX_00("Binary matrix D (0-0)", "Matrix D (0-0)"),
	
	BINARY_MATRIX_SUM("Binary matrix L (sum)", "Matrix L (sum)"),
	
	BINARY_MATRIX_SITE("Binary summary by site", "Binary site summary"),
	
	BINARY_MATRIX_TREE("Binary summary by tree", "Binary tree summary"),
	
	BINARY_MATRIX_NTP("Binary summary: number of fires, number of trees and percentage scarred trees", "NTP Matrix"),
	
	GENERAL_SUMMARY("Series-by-series summary", "Series-by-series summary"),
	
	SINGLE_FILE_SUMMARY("Annual summary of currently selected file", "Current file annual summary"),
	
	SINGLE_EVENT_SUMMARY("Summary of events in the currently selected file", "Current file event summary");
	
	// Declare local variables
	private String fullname;
	private String shortname;
	
	/**
	 * Initialize the full and short name strings for the FHAESResult.
	 * 
	 * @param fullname
	 * @param shortname
	 */
	FHAESResult(String fullname, String shortname) {
	
		this.fullname = fullname;
		this.shortname = shortname;
	}
	
	/**
	 * Get the human-readable string name for this FHAESResult.
	 */
	@Override
	public String toString() {
	
		return fullname;
	}
	
	/**
	 * Get the full name for this FHAESResult.
	 */
	public String getFullName() {
	
		return fullname;
	}
	
	/**
	 * Get the short name for this FHAESResult.
	 */
	public String getShortName() {
	
		return shortname;
	}
	
	/**
	 * Create a FHAESResult from a full name. If there is no FHAESResult that matches the string then null is returned.
	 * 
	 * @param name
	 * @return
	 */
	public static FHAESResult fromFullName(String name) {
	
		for (FHAESResult type : FHAESResult.values())
		{
			if (type.fullname.equals(name))
				return type;
		}
		
		return null;
	}
	
	/**
	 * Create a FHAESResult from a short name. If there is no FHAESResult that matches the string then null is returned.
	 * 
	 * @param name
	 * @return
	 */
	public static FHAESResult fromShortName(String name) {
	
		for (FHAESResult type : FHAESResult.values())
		{
			if (type.shortname.equals(name))
				return type;
		}
		
		return null;
	}
}
