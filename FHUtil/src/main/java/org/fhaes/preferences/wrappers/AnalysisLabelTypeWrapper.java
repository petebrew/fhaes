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
package org.fhaes.preferences.wrappers;

import java.awt.event.ItemEvent;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;

import org.fhaes.enums.AnalysisLabelType;
import org.fhaes.preferences.FHAESPreferences.PrefKey;

/**
 * AnalysisLabelTypeWrapper Class.
 */
@SuppressWarnings("rawtypes")
public class AnalysisLabelTypeWrapper extends ItemWrapper<AnalysisLabelType> {

	AnalysisLabelType[] labelTypes;

	/**
	 * TODO
	 * 
	 * @param cbo
	 * @param key
	 * @param defaultValue
	 * @param values
	 */
	public AnalysisLabelTypeWrapper(JComboBox cbo, PrefKey key, Object defaultValue, AnalysisLabelType[] values) {

		super(key, defaultValue, AnalysisLabelType.class);
		initGeneric(cbo, values);
	}

	/**
	 * TODO
	 * 
	 * @param cbo
	 * @param key
	 * @param defaultValue
	 */
	public AnalysisLabelTypeWrapper(JComboBox cbo, PrefKey key, Object defaultValue) {

		super(key, defaultValue, AnalysisLabelType.class);
		initFormats(cbo);
	}

	@SuppressWarnings("unchecked")
	private void initFormats(JComboBox cbo) {

		// show a sample for each format thingy...
		labelTypes = OPTIONS;
		int selectedIdx = -1;

		for (int i = 0; i < OPTIONS.length; i++)
		{
			if (OPTIONS[i].equals(getValue()))
				selectedIdx = i;
		}

		cbo.setModel(new DefaultComboBoxModel(labelTypes));
		if (selectedIdx >= 0)
			cbo.setSelectedIndex(selectedIdx);

		cbo.addItemListener(this);
	}

	@SuppressWarnings("unchecked")
	private void initGeneric(JComboBox cbo, AnalysisLabelType[] values) {

		// show a sample for each format thingy...
		labelTypes = values;
		int selectedIdx = -1;

		for (int i = 0; i < values.length; i++)
		{
			if (labelTypes[i].equals(getValue()))
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
			setValue(labelTypes[selectedIdx]);
		else
			setValue(null);
	}

	private final static AnalysisLabelType[] OPTIONS = AnalysisLabelType.values();

}
