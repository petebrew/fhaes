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
 * MiddleMetric Enum.
 */
public enum MiddleMetric {
	MEAN("Mean"), MEDIAN("Median"), WEIBULL_MEAN("Weibull mean"), WEIBULL_MEDIAN("Weibull median");

	private String humanreadable;

	MiddleMetric(String s) {

		humanreadable = s;
	}

	@Override
	public String toString() {

		return humanreadable;
	}

	public static MiddleMetric fromName(String name) {

		for (MiddleMetric type : MiddleMetric.values())
		{
			if (type.humanreadable.equals(name))
				return type;
		}

		return null;
	}
}
