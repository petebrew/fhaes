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
 * ResamplingType Enum. Enumeration containing the options for how resampling is done
 * 
 * @author pbrewer
 */
public enum ResamplingType {

	WITH_REPLACEMENT("With replacement"), WITHOUT_REPLACEMENT("Without replacement");

	private String humanreadable;

	ResamplingType(String s) {

		humanreadable = s;
	}

	/**
	 * Get the human readable string name for this ResamplingType.
	 */
	@Override
	public String toString() {

		return humanreadable;
	}

	/**
	 * Create an ResamplingType from a string name. If there is no ResamplingType that matches the string then null is returned.
	 * 
	 * @param name
	 * @return
	 */
	public static ResamplingType fromName(String name) {

		for (ResamplingType type : ResamplingType.values())
		{
			if (type.humanreadable.equals(name))
				return type;
		}

		return null;
	}
}
