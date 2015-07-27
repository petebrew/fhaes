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
package org.fhaes.preferences.wrappers;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import org.fhaes.preferences.FHAESPreferences.PrefKey;

/**
 * ItemWrapper<OBJTYPE> Abstract Class.
 * 
 * @param <OBJTYPE>
 */
public abstract class ItemWrapper<OBJTYPE> extends PrefWrapper<OBJTYPE>implements ItemListener {
	
	/**
	 * TODO
	 * 
	 * @param prefName
	 * @param defaultValue
	 * @param baseClass
	 */
	public ItemWrapper(PrefKey prefName, Object defaultValue, Class<?> baseClass) {
		
		super(prefName, defaultValue, baseClass);
	}
	
	/**
	 * TODO
	 * 
	 * @param prefName
	 * @param defaultValue
	 */
	public ItemWrapper(PrefKey prefName, Object defaultValue) {
		
		super(prefName, defaultValue);
	}
	
	/**
	 * TODO
	 * 
	 * @param prefName
	 */
	public ItemWrapper(PrefKey prefName) {
		
		super(prefName);
	}
	
	/**
	 * TODO
	 */
	@Override
	public abstract void itemStateChanged(ItemEvent e);
}
