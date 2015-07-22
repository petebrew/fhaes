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
 *     Lucas Madar
 ******************************************************************************/
package org.fhaes.util;

import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;

import javax.swing.AbstractAction;
import javax.swing.AbstractButton;
import javax.swing.Action;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * FHAESAction Class.
 */
public class FHAESAction extends AbstractAction {

	private static final long serialVersionUID = 1L;

	// This allows us to gracefully handle booleans
	protected static final String SELECTED_KEY = "fhaes.selected";

	// This is only available in 1.6, but we want it, so use it in 1.5 where it just gets ignored :)
	protected static final String KLUDGE_DISPLAYED_MNEMONIC_INDEX_KEY = "SwingDisplayedMnemonicIndexKey";

	// Toggle button adapters associated with this class
	protected ArrayList<ButtonSelectionActionAdapter> buttonAdapters;

	// Declare FHAES specialized objects
	private static final Logger log = LoggerFactory.getLogger(FHAESAction.class);

	// Declare local constants
	private final int SHORT_NAME_MAX_LENGTH = 13;

	// Declare local variables
	public String i18nKey;
	public String tooltip;
	public String shortName;

	/**
	 * Simple constructor where just the name is specified. You must set other values after construction.
	 * 
	 * @param name
	 */
	public FHAESAction(String name) {

		super(name);

		if (name.length() <= SHORT_NAME_MAX_LENGTH)
			shortName = name;

		tooltip = name;
	}

	/**
	 * Constructor for this action.
	 * 
	 * @param name - Full human readable name for this action
	 * @param iconName - String for the icon file name
	 */
	public FHAESAction(String name, String iconName) {

		super(name, Builder.getImageIcon(iconName));
		tooltip = name;

		if (name.length() <= SHORT_NAME_MAX_LENGTH)
			this.shortName = name;
	}

	/**
	 * Constructor for this action.
	 * 
	 * @param name - Full human readable name for this action
	 * @param iconName - String for the icon file name
	 * @param shortName - Short name of <=13 characters for OSX toolbar buttons
	 */
	public FHAESAction(String name, String iconName, String shortName) {

		super(name, Builder.getImageIcon(iconName));
		tooltip = name;

		if (shortName.length() <= SHORT_NAME_MAX_LENGTH)
		{
			this.shortName = shortName;
		}
		else
		{
			log.error("The specified shortName is too long. Ignoring");
		}
	}

	/**
	 * Constructor for this action.
	 * 
	 * @param name - Full human readable name for this action
	 * @param iconName - String for the icon file name
	 * @param shortName - Short name of <=13 characters for OSX toolbar buttons
	 * @param tooltip - Tool tip to display
	 */
	public FHAESAction(String name, String iconName, String shortName, String tooltip) {

		super(name, Builder.getImageIcon(iconName));
		this.tooltip = tooltip;

		if (shortName.length() <= SHORT_NAME_MAX_LENGTH)
		{
			this.shortName = shortName;
		}
		else
		{
			log.error("The specified shortName is too long. Ignoring");
		}
	}

	/**
	 * Get the short name for this action if specified. A short name is 12 or less characters and typically used for OSX toolbar buttons.
	 * 
	 * @return
	 */
	public String getShortName() {
	
		return shortName;
	}

	/**
	 * Get the tool tip text.
	 * 
	 * @return
	 */
	public String getToolTipText() {

		return tooltip;
	}

	/**
	 * Set the tool tip text to use for this action.
	 * 
	 * @param text
	 */
	public void setToolTipText(String text) {

		tooltip = text;
	}

	/**
	 * Perform this action.
	 * 
	 * @param source
	 */
	public void perform(Object source) {

		ActionEvent ae = new ActionEvent(source == null ? this : source, ActionEvent.ACTION_PERFORMED,
				(String) getValue(Action.ACTION_COMMAND_KEY));

		actionPerformed(ae);
	}

	/**
	 * Called when our selection state changes.
	 * 
	 * @param newSelectedState
	 */
	protected void selectionStateChanged(boolean newSelectedState) {

		// by default, we don't care :)
	}

	/**
	 * Associate a toggleable button with this action's internal toggle state.
	 * 
	 * @param button the button to associate with
	 * @param defaultValue the default boolean value, or null if we should try to automatically figure this out
	 */
	public void connectToggleableButton(AbstractButton button, Boolean defaultValue) {

		if (buttonAdapters == null)
			buttonAdapters = new ArrayList<ButtonSelectionActionAdapter>();

		buttonAdapters.add(new ButtonSelectionActionAdapter(button, defaultValue));
	}

	/**
	 * Associate a toggleable button with this action's internal toggle state (Guess defaults!).
	 * 
	 * @param button
	 */
	public void connectToggleableButton(AbstractButton button) {

		connectToggleableButton(button, null);
	}

	/**
	 * ButtonSelectionActionAdapter Class.
	 */
	private class ButtonSelectionActionAdapter implements PropertyChangeListener, ItemListener {

		private AbstractButton button;
		private Boolean lastValue;

		/**
		 * TODO
		 * 
		 * @param button
		 * @param defaultValue
		 */
		public ButtonSelectionActionAdapter(AbstractButton button, Boolean defaultValue) {

			this.button = button;

			// set the default value
			if (defaultValue != null)
			{
				button.setSelected(defaultValue);
				FHAESAction.this.putValue(SELECTED_KEY, defaultValue);
			}
			else if ((defaultValue = (Boolean) FHAESAction.this.getValue(SELECTED_KEY)) == null)
			{
				// well, use the button's value then
				FHAESAction.this.putValue(SELECTED_KEY, button.isSelected());
			}
			else
			{
				// ok, use the action's value then!
				button.setSelected(defaultValue);
			}

			// tie listeners in
			FHAESAction.this.addPropertyChangeListener(this);
			button.addItemListener(this);
		}

		/**
		 * Called when the Action's value changes
		 */
		public void propertyChange(PropertyChangeEvent evt) {

			// only care about our special selection event
			if (!SELECTED_KEY.equals(evt.getPropertyName()))
				return;

			Boolean selected = (Boolean) evt.getNewValue();

			button.setSelected(selected);

			// notify our superclass
			if (lastValue != selected)
			{
				lastValue = selected;
				FHAESAction.this.selectionStateChanged(selected);
			}
		}

		/**
		 * Called when the button's value changes
		 */
		public void itemStateChanged(ItemEvent e) {

			Boolean selected = (e.getStateChange() == ItemEvent.SELECTED);

			FHAESAction.this.putValue(SELECTED_KEY, selected);

			// notify our superclass
			if (lastValue != selected)
			{
				lastValue = selected;
				FHAESAction.this.selectionStateChanged(selected);
			}
		}
	}

	/**
	 * TODO
	 */
	public void actionPerformed(ActionEvent arg0) {

		// Should be set after construction
	}
}
