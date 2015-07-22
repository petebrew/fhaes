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
 * FireFilterType Enum.
 */
public enum FireFilterType {
	NUMBER_OF_EVENTS("Number of fires"), PERCENTAGE_OF_EVENTS("Percent scarred");

	private String humanreadable;

	FireFilterType(String s) {

		humanreadable = s;
	}

	@Override
	public String toString() {

		return humanreadable;
	}

	public static FireFilterType fromName(String name) {

		for (FireFilterType type : FireFilterType.values())
		{
			if (type.humanreadable.equals(name))
				return type;
		}

		return null;
	}
}
