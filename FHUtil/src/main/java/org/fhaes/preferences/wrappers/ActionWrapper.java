package org.fhaes.preferences.wrappers;

/*******************************************************************************
 * Copyright (C) 2011 Peter Brewer.
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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import org.fhaes.preferences.FHAESPreferences.PrefKey;

/**
 * ActionWrapper<OBJTYPE> Abstract Class. A prefwrapper around an Action (e.g., jcombobox, jcheckbox); implements ActionListener. Useful if
 * you have a boolean or a list of Strings, for instance.
 * 
 * @author lucasm
 * 
 * @param <OBJTYPE>
 */
public abstract class ActionWrapper<OBJTYPE> extends PrefWrapper<OBJTYPE> implements ActionListener {

	/**
	 * TODO
	 * 
	 * @param prefName
	 * @param defaultValue
	 * @param baseClass
	 */
	public ActionWrapper(PrefKey prefName, Object defaultValue, Class<?> baseClass) {

		super(prefName, defaultValue, baseClass);
	}

	/**
	 * TODO
	 * 
	 * @param prefName
	 * @param defaultValue
	 */
	public ActionWrapper(PrefKey prefName, Object defaultValue) {

		super(prefName, defaultValue);
	}

	/**
	 * TODO
	 * 
	 * @param prefName
	 */
	public ActionWrapper(PrefKey prefName) {

		super(prefName);
	}

	/**
	 * TODO
	 */
	public abstract void actionPerformed(ActionEvent e);
}
