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

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;

import org.fhaes.enums.LineStyle;
import org.fhaes.preferences.FHAESPreferences.PrefKey;

/**
 * LineStyleWrapper Class.
 */
public class LineStyleWrapper extends ItemWrapper<LineStyle> {
	
	LineStyle[] styles;
	
	@SuppressWarnings("rawtypes")
	public LineStyleWrapper(JComboBox cbo, PrefKey key, Object defaultValue, LineStyle[] values) {
		
		super(key, defaultValue, LineStyle.class);
		initGeneric(cbo, values);
	}
	
	@SuppressWarnings("rawtypes")
	public LineStyleWrapper(JComboBox cbo, PrefKey key, Object defaultValue) {
		
		super(key, defaultValue, LineStyle.class);
		initFormats(cbo);
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void initFormats(JComboBox cbo) {
		
		// show a sample for each format thingy...
		styles = OPTIONS;
		int selectedIdx = -1;
		
		for (int i = 0; i < OPTIONS.length; i++)
		{
			if (OPTIONS[i].equals(getValue()))
				selectedIdx = i;
		}
		
		cbo.setModel(new DefaultComboBoxModel(styles));
		if (selectedIdx >= 0)
			cbo.setSelectedIndex(selectedIdx);
			
		cbo.addItemListener(this);
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void initGeneric(JComboBox cbo, LineStyle[] values) {
		
		styles = values;
		int selectedIdx = -1;
		
		for (int i = 0; i < values.length; i++)
		{
			if (styles[i].equals(getValue()))
				selectedIdx = i;
		}
		
		cbo.setModel(new DefaultComboBoxModel(values));
		if (selectedIdx >= 0)
			cbo.setSelectedIndex(selectedIdx);
			
		cbo.addItemListener(this);
	}
	
	@SuppressWarnings("rawtypes")
	@Override
	public void itemStateChanged(ItemEvent e) {
		
		int selectedIdx = ((JComboBox) e.getSource()).getSelectedIndex();
		
		if (selectedIdx >= 0)
			setValue(styles[selectedIdx]);
		else
			setValue(null);
	}
	
	private final static LineStyle[] OPTIONS = LineStyle.values();
	
}
