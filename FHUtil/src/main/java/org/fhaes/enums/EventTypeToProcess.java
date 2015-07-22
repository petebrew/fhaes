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
 * EventTypeToProcess Enum.
 */
public enum EventTypeToProcess {
	FIRE_EVENT("Fire events"), INJURY_EVENT("Other indicators"), FIRE_AND_INJURY_EVENT("Fire events and other indicators");

	private String humanreadable;

	EventTypeToProcess(String s) {

		humanreadable = s;
	}

	@Override
	public String toString() {

		return humanreadable;
	}

	public static EventTypeToProcess fromName(String name) {

		for (EventTypeToProcess type : EventTypeToProcess.values())
		{
			if (type.humanreadable.equals(name))
				return type;
		}

		return null;
	}
}
