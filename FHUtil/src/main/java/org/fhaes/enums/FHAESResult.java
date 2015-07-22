/*******************************************************************************
 * Copyright (c) 2013 Peter Brewer
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 * 
 * Contributors:
 *     Peter Brewer
 *     Elena Velasquez
 ******************************************************************************/
package org.fhaes.enums;

/**
 * FHAESResult Enum.
 */
public enum FHAESResult {
	JACCARD_SIMILARITY_MATRIX("JACCARD similarity matrix", "JACCARD"), COHEN_SIMILARITITY_MATRIX("COHEN similarity matrix", "COHEN"), JACCARD_SIMILARITY_MATRIX_D(
			"JACCARD dissimilarity matrix", "JACCARD"), COHEN_SIMILARITITY_MATRIX_D("COHEN dissimilarity matrix", "COHEN"), INTERVAL_SUMMARY(
			"Interval summary", "Summary"), INTERVAL_EXCEEDENCE_TABLE("Interval exceedence table", "Exceedence table"), SEASONALITY_SUMMARY(
			"Seasonality summary", "Summary"), BINARY_MATRIX_11("Binary matrix A (1-1)", "Matrix A (1-1)"), BINARY_MATRIX_01(
			"Binary matrix B (0-1)", "Matrix B (0-1)"), BINARY_MATRIX_10("Binary matrix C (1-0)", "Matrix C (1-0)"), BINARY_MATRIX_00(
			"Binary matrix D (0-0)", "Matrix D (0-0)"), BINARY_MATRIX_SUM("Binary matrix L (sum)", "Matrix L (sum)"), BINARY_MATRIX_SITE(
			"Binary summary by site", "Binary site summary"), BINARY_MATRIX_TREE("Binary summary by tree", "Binary tree summary"), BINARY_MATRIX_NTP(
			"Binary summary: number of fires, number of trees and percentage scarred trees", "NTP Matrix"), GENERAL_SUMMARY(
			"General input file summary", "General summary");

	private String fullname;
	private String shortname;

	FHAESResult(String fullname, String shortname) {

		this.fullname = fullname;
		this.shortname = shortname;
	}

	@Override
	public String toString() {

		return fullname;
	}

	public String getShortName() {

		return shortname;
	}

	public String getFullName() {

		return fullname;
	}

	public static FHAESResult fromFullName(String name) {

		for (FHAESResult type : FHAESResult.values())
		{
			if (type.fullname.equals(name))
				return type;
		}

		return null;
	}

	public static FHAESResult fromShortName(String name) {

		for (FHAESResult type : FHAESResult.values())
		{
			if (type.shortname.equals(name))
				return type;
		}

		return null;
	}
}
