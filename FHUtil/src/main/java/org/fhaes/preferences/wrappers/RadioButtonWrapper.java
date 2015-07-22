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
import java.util.Enumeration;

import javax.swing.AbstractButton;
import javax.swing.ButtonGroup;
import javax.swing.JRadioButton;

import org.fhaes.preferences.FHAESPreferences.PrefKey;

/**
 * RadioButtonWrapper Class.
 */
public class RadioButtonWrapper extends ActionWrapper<String> {

	/**
	 * TODO
	 * 
	 * @param buttons
	 * @param prefName
	 * @param defaultValue
	 */
	public RadioButtonWrapper(ButtonGroup buttons, PrefKey prefName, Object defaultValue) {

		super(prefName, defaultValue, String.class);

		String selectedValue = getValue();

		Enumeration<AbstractButton> allRadioButton = buttons.getElements();
		while (allRadioButton.hasMoreElements())
		{
			JRadioButton temp = (JRadioButton) allRadioButton.nextElement();
			if (temp.getActionCommand().equalsIgnoreCase(selectedValue))
				temp.setSelected(true);

			temp.addActionListener(this);
		}
	}

	/**
	 * TODO
	 */
	@Override
	public void actionPerformed(ActionEvent e) {

		setValue(e.getActionCommand());
	}
}
