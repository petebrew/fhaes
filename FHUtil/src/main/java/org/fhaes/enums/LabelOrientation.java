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
 * LabelOrientation Enum.
 */
public enum LabelOrientation {
	HORIZONTAL("Horizontal", 0), VERTICAL("Vertical", 270), ANGLED("Angled", 315);

	private String humanreadable;
	private int angle;

	LabelOrientation(String s, int angle) {

		humanreadable = s;
		this.angle = angle;
	}

	@Override
	public String toString() {

		return humanreadable;
	}

	public static LabelOrientation fromName(String name) {

		for (LabelOrientation type : LabelOrientation.values())
		{
			if (type.humanreadable.toLowerCase().equals(name.toLowerCase()))
				return type;
		}

		return null;
	}

	public int getAngle() {

		return angle;
	}
}
