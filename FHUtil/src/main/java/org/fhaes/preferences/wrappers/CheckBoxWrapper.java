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
package org.fhaes.preferences.wrappers;

import java.awt.event.ItemEvent;

import javax.swing.AbstractButton;
import javax.swing.JCheckBox;

import org.fhaes.preferences.FHAESPreferences.PrefKey;

/**
 * CheckBoxWrapper Class. This one is nice and simple! :)
 */
public class CheckBoxWrapper extends ItemWrapper<Boolean> {

	/**
	 * TPDP
	 * 
	 * @param cb
	 * @param key
	 * @param defaultValue
	 */
	public CheckBoxWrapper(JCheckBox cb, PrefKey key, Boolean defaultValue) {

		super(key, defaultValue, Boolean.class);

		cb.setSelected(getValue());
		cb.addItemListener(this);
	}

	/**
	 * TODO
	 */
	@Override
	public void itemStateChanged(ItemEvent e) {

		setValue(((AbstractButton) e.getSource()).isSelected());
	}
}
