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
 * AnalysisLabelType Enum. Enumeration containing the options for how analyses should be labelled. Normally analyses are labelled using the
 * file name for the input file, but you can also use site codes and site names too.
 * 
 * @author pbrewer
 */
public enum AnalysisLabelType {
	INPUT_FILENAME("Input file name"), SITE_CODE("Site code"), SITE_NAME("Site name");

	private String humanreadable;

	AnalysisLabelType(String s) {

		humanreadable = s;
	}

	/**
	 * Get the human readable string name for this AnalysisLabelType.
	 */
	@Override
	public String toString() {

		return humanreadable;
	}

	/**
	 * Create an AnalysisLabelType from a string name. If there is no AnalysisLabelType that matches the string then null is returned.
	 * 
	 * @param name
	 * @return
	 */
	public static AnalysisLabelType fromName(String name) {

		for (AnalysisLabelType type : AnalysisLabelType.values())
		{
			if (type.humanreadable.equals(name))
				return type;
		}

		return null;
	}
}
