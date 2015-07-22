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
 * AnalysisType Enum.
 */
public enum AnalysisType {
	COMPOSITE("Composite"), SAMPLE("Sample");

	private String humanreadable;

	AnalysisType(String s) {

		humanreadable = s;
	}

	@Override
	public String toString() {

		return humanreadable;
	}

	public static AnalysisType fromName(String name) {

		for (AnalysisType type : AnalysisType.values())
		{
			if (type.humanreadable.equals(name))
				return type;
		}

		return null;
	}
}
