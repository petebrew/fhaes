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
package org.fhaes.model;

/*******************************************************************************
 * Copyright (C) 2013 Peter Brewer
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * Contributors:
 *     Peter Brewer
 ******************************************************************************/

import java.util.Comparator;

/**
 * FHFileFirstYearComparator Class.
 */
public class FHFileFirstYearComparator implements Comparator<FHFile> {

	@Override
	public int compare(FHFile o1, FHFile o2) {

		if (o1.getFirstYear() == null)
			return -1;
		if (o2.getFirstYear() == null)
			return +1;
		return o1.getFirstYear().compareTo(o2.getFirstYear());
	}
}
