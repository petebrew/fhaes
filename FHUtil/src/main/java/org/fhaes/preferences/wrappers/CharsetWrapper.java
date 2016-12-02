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
import java.nio.charset.Charset;
import java.util.Collection;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;

import org.fhaes.preferences.FHAESPreferences.PrefKey;

/**
 * CharsetWrapper Class.
 */
@SuppressWarnings("rawtypes")
public class CharsetWrapper extends ItemWrapper<Charset> {
	
	Charset[] charsets;
	
	/**
	 * TODO
	 * 
	 * @param cbo
	 * @param key
	 * @param defaultValue
	 * @param values
	 */
	public CharsetWrapper(JComboBox cbo, PrefKey key, Object defaultValue, Charset[] values) {
	
		super(key, defaultValue, Charset.class);
		initGeneric(cbo, values);
	}
	
	/**
	 * TODO
	 * 
	 * @param cbo
	 * @param key
	 * @param defaultValue
	 */
	public CharsetWrapper(JComboBox cbo, PrefKey key, Object defaultValue) {
	
		super(key, defaultValue, Charset.class);
		initFormats(cbo);
	}
	
	@SuppressWarnings("unchecked")
	private void initFormats(JComboBox cbo) {
	
		// show a sample for each format thingy...
		Collection<Charset> avail = Charset.availableCharsets().values();
		charsets = avail.toArray(new Charset[avail.size()]);
		
		int selectedIdx = -1;
		
		for (int i = 0; i < charsets.length; i++)
		{
			if (charsets[i].equals(getValue()))
				selectedIdx = i;
		}
		
		cbo.setModel(new DefaultComboBoxModel(charsets));
		if (selectedIdx >= 0)
			cbo.setSelectedIndex(selectedIdx);
		
		cbo.addItemListener(this);
	}
	
	@SuppressWarnings("unchecked")
	private void initGeneric(JComboBox cbo, Charset[] values) {
	
		// show a sample for each format thingy...
		charsets = values;
		int selectedIdx = -1;
		
		for (int i = 0; i < values.length; i++)
		{
			if (charsets[i].equals(getValue()))
				selectedIdx = i;
		}
		
		cbo.setModel(new DefaultComboBoxModel(values));
		if (selectedIdx >= 0)
			cbo.setSelectedIndex(selectedIdx);
		
		cbo.addItemListener(this);
	}
	
	@Override
	public void itemStateChanged(ItemEvent e) {
	
		int selectedIdx = ((JComboBox) e.getSource()).getSelectedIndex();
		
		if (selectedIdx >= 0)
			setValue(charsets[selectedIdx]);
		else
			setValue(null);
	}
	
}
