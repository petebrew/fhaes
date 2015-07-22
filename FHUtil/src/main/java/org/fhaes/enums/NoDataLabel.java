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
 * NoDataLabel Enum.
 */
public enum NoDataLabel {
	MINUS_99(-99d), ZERO(0d), NULL(null), NAN(Double.NaN);

	private Double value;

	NoDataLabel(Double d) {

		value = d;
	}

	/**
	 * Get the human readable string name for this NoDataLabel.
	 */
	@Override
	public String toString() {

		if (value == null)
			return "Null";

		return value.toString();
	}

	public Double toDouble() {

		return value;
	}

	/**
	 * Create an NoDataLabel from a string name. If there is no NoDataLabel that matches the string then null is returned.
	 * 
	 * @param dbl
	 * @return
	 */
	public static NoDataLabel fromDouble(Double dbl) {

		if (dbl == null)
			return NoDataLabel.NULL;

		for (NoDataLabel type : NoDataLabel.values())
		{
			if (type.toDouble() == null)
				continue;
			if (type.toDouble().equals(dbl))
				return type;
		}

		return NoDataLabel.NULL;
	}

	/**
	 * TODO
	 * 
	 * @param str
	 * @return
	 */
	public static NoDataLabel fromString(String str) {

		if (str.equals(NoDataLabel.MINUS_99.toString()))
		{
			return NoDataLabel.MINUS_99;
		}
		else if (str.equals(NoDataLabel.ZERO.toString()))
		{
			return NoDataLabel.ZERO;
		}
		else if (str.equals(NoDataLabel.NAN.toString()))
		{
			return NoDataLabel.NAN;
		}
		else
		{
			return NoDataLabel.NULL;
		}
	}
}
