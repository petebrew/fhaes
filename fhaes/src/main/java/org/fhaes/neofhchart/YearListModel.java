package org.fhaes.neofhchart;

/*******************************************************************************
 * Copyright (C) 2015 Peter Brewer
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
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import javax.swing.AbstractListModel;

/**
 * YearListModel Class.
 */
public class YearListModel extends AbstractListModel<Integer> {

	private static final long serialVersionUID = 1L;
	ArrayList<Integer> items = new ArrayList<Integer>();

	@Override
	public Integer getElementAt(int index) {

		return items.get(index);
	}

	@Override
	public int getSize() {

		return items.size();
	}

	public void clearYears() {

		items = new ArrayList<Integer>();
		this.fireContentsChanged(this, 0, items.size() - 1);
	}

	public Integer getYearAt(int index) {

		return getElementAt(index);
	}

	public void addYear(Integer value) {

		if (value == null)
			return;
		if (items.contains(value))
			return;
		items.add(value);
		this.fireContentsChanged(this, 0, items.size() - 1);
	}

	public void addYears(Collection<Integer> values) {

		if (values == null)
			return;

		for (Integer item : values)
		{
			addYear(item);
		}
		this.fireContentsChanged(this, 0, items.size() - 1);
	}

	public void removeYear(Integer value) {

		items.remove(value);
		if (items.size() > 0)
		{
			this.fireIntervalRemoved(this, 0, items.size() - 1);
		}
		else
		{
			this.fireIntervalRemoved(this, 0, 0);
		}
	}

	public void removeYearAtIndex(int index) {

		items.remove(index);

		if (items.size() > 0)
		{
			this.fireIntervalRemoved(this, 0, items.size() - 1);
		}
		else
		{
			this.fireIntervalRemoved(this, 0, 0);
		}
	}

	public ArrayList<Integer> getAllYears() {

		return items;
	}

	public void sort() {

		Collections.sort(items);
		fireContentsChanged(this, 0, items.size());
	}

}
