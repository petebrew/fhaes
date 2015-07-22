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
 * FHFileValidityComparator Class.
 */
public class FHFileValidityComparator implements Comparator<FHFile> {

	/**
	 * TODO
	 */
	@Override
	public int compare(FHFile o1, FHFile o2) {

		Integer o1order = getOrderValue(o1);
		Integer o2order = getOrderValue(o2);

		return o1order.compareTo(o2order);

	}

	/**
	 * TODO
	 * 
	 * @param f
	 * @return
	 */
	private Integer getOrderValue(FHFile f) {

		int order = 0;

		if (f.getErrorMessage() == null)
		{
			order = 0;
		}
		else
		{
			if (f.isValidFHXFile())
			{
				order = 1;
			}
			else
			{
				order = 2;
			}
		}
		return order;

	}

}
