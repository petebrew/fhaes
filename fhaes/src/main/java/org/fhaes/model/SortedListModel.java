/**************************************************************************************************
 * Fire History Analysis and Exploration System (FHAES), Copyright (C) 2015
 * 
 * Contributors: Peter Brewer
 * 
 * 		This program is free software: you can redistribute it and/or modify it under the terms of
 * 		the GNU General Public License as published by the Free Software Foundation, either version
 * 		3 of the License, or (at your option) any later version.
 * 
 * 		This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * 		without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * 		See the GNU General Public License for more details.
 * 
 * 		You should have received a copy of the GNU General Public License along with this program.
 * 		If not, see <http://www.gnu.org/licenses/>.
 * 
 *************************************************************************************************/
package org.fhaes.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.swing.AbstractListModel;

/**
 * SortedListModel Class.
 */
@SuppressWarnings("rawtypes")
public class SortedListModel extends AbstractListModel {
	
	private static final long serialVersionUID = 1L;
	
	// Define a SortedSet
	SortedSet model;
	
	/**
	 * TODO
	 */
	public SortedListModel() {
		
		// Create a TreeSet
		// Store it in SortedSet variable
		model = new TreeSet();
	}
	
	// ListModel methods
	@Override
	public int getSize() {
		
		// Return the model size
		return model.size();
	}
	
	@Override
	public Object getElementAt(int index) {
		
		// Return the appropriate element
		return model.toArray()[index];
	}
	
	// Other methods
	@SuppressWarnings("unchecked")
	public void addElement(Object element) {
		
		if (model.add(element))
		{
			fireContentsChanged(this, 0, getSize());
		}
	}
	
	@SuppressWarnings("unchecked")
	public void addAll(Object elements[]) {
		
		Collection c = Arrays.asList(elements);
		model.addAll(c);
		fireContentsChanged(this, 0, getSize());
	}
	
	public void clear() {
		
		model.clear();
		fireContentsChanged(this, 0, getSize());
	}
	
	public boolean contains(Object element) {
		
		return model.contains(element);
	}
	
	public Object firstElement() {
		
		// Return the appropriate element
		return model.first();
	}
	
	public Iterator iterator() {
		
		return model.iterator();
	}
	
	public Object lastElement() {
		
		// Return the appropriate element
		return model.last();
	}
	
	public boolean removeElement(Object element) {
		
		boolean removed = model.remove(element);
		if (removed)
		{
			fireContentsChanged(this, 0, getSize());
		}
		return removed;
	}
	
	@SuppressWarnings("unchecked")
	public ArrayList getAllElements() {
		
		Object[] items = model.toArray();
		ArrayList list = new ArrayList();
		for (Object a : items)
		{
			list.add(a);
		}
		return list;
	}
}
