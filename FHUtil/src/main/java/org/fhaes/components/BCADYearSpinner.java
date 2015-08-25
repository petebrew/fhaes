/**************************************************************************************************
 * Fire History Analysis and Exploration System (FHAES), Copyright (C) 2015
 * 
 * Contributors: Joshua Brogan and Peter Brewer
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
package org.fhaes.components;

import java.awt.Dimension;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.SpinnerNumberModel;
import javax.swing.text.NumberFormatter;

/**
 * BCADYearSpinner Class. A spinner that supports a model for representing BC and AD years.
 * 
 * @author Joshua Brogan
 */
public class BCADYearSpinner extends javax.swing.JSpinner {
	
	private static final long serialVersionUID = 1L;
	
	// Declare local constants
	private final String NUMBER_EDITOR_FORMAT = "#####";
	private final int SPINNER_HEIGHT = 28;
	private final int SPINNER_WIDTH = 82;
	
	// Declare local variables
	private Integer mostRecentValue;
	
	/**
	 * Constructs a new BCADYearSpinner.
	 * 
	 * @param initialValue
	 * @param minimumValue
	 * @param maximumValue
	 */
	public BCADYearSpinner(int initialValue, int minimumValue, int maximumValue) {
		
		initGUI(initialValue, minimumValue, maximumValue);
	}
	
	/**
	 * Gets the number editor for the spinner.
	 * 
	 * @return the spinner's number editor
	 */
	private NumberEditor getNumberEditor() {
		
		return (NumberEditor) this.getEditor();
	}
	
	/**
	 * Gets the number formatter for the spinner.
	 * 
	 * @return the spinner's number formatter
	 */
	private NumberFormatter getNumberFormatter() {
		
		return ((NumberFormatter) this.getNumberEditor().getTextField().getFormatter());
	}
	
	/**
	 * Gets the spinner's current value as an integer.
	 * 
	 * @return the spinner's current value as an integer
	 */
	public Integer getValueAsInteger() {
		
		return (Integer) this.getModel().getValue();
	}
	
	/**
	 * Gets the spinner's most recently displayed value.
	 */
	public Integer getMostRecentValue() {
		
		return mostRecentValue;
	}
	
	/**
	 * Updates the spinner's most recently displayed value with the spinner's current value.
	 */
	public void updateMostRecentValue() {
		
		mostRecentValue = getValueAsInteger();
	}
	
	/**
	 * Initializes the GUI.
	 * 
	 * @param initialValue
	 * @param minimumValue
	 * @param maximumValue
	 */
	private void initGUI(int initialValue, int minimumValue, int maximumValue) {
		
		// Assign the initial value to the spinner BEFORE setting up the custom model
		this.setValue(initialValue);
		
		// Setup the rest of the properties for the spinner
		this.getNumberFormatter().setAllowsInvalid(false);
		this.getNumberFormatter().setCommitsOnValidEdit(true);
		this.setEditor(new NumberEditor(this, NUMBER_EDITOR_FORMAT));
		this.setMinimumSize(new Dimension(SPINNER_WIDTH, SPINNER_HEIGHT));
		this.setModel(new BCADYearSpinnerModel(initialValue, minimumValue, maximumValue));
		
		// Make sure to initialize the most recent value of the spinner
		updateMostRecentValue();
		
		// Workaround to enable keyboard-based interaction with the spinner
		this.getNumberEditor().getTextField().setFocusTraversalKeysEnabled(false);
		this.getNumberEditor().getTextField().addKeyListener(new KeyListener() {
			
			@Override
			public void keyPressed(KeyEvent evt) {}
			
			@Override
			public void keyReleased(KeyEvent evt) {}
			
			@Override
			public void keyTyped(KeyEvent evt) {
				
				if (evt.getKeyChar() == KeyEvent.VK_TAB || evt.getKeyChar() == KeyEvent.VK_ENTER)
				{
					try
					{
						Integer textAsInteger = new Integer(getNumberEditor().getTextField().toString());
						
						if (textAsInteger != 0)
						{
							getNumberEditor().getTextField().commitEdit();
						}
						else
						{
							getNumberEditor().getTextField().setText(getMostRecentValue().toString());
						}
					}
					catch (Exception ex)
					{
						getNumberEditor().getTextField().setText(getMostRecentValue().toString());
					}
				}
			}
		});
	}
	
	/**
	 * BCADYearSpinnerModel Class. Enforces valid input for BC/AD years and ensures that zero can never appear in the spinner.
	 */
	private class BCADYearSpinnerModel extends SpinnerNumberModel {
		
		private static final long serialVersionUID = 1L;
		
		// Declare local constants
		private static final int STEP_SIZE = 1;
		
		// Declare local variables
		private final int maximum;
		private final int minimum;
		
		public BCADYearSpinnerModel(int initialValue, int minimumValue, int maximumValue) {
			
			// Call to SpinnerNumberModel so that the spinner arrows work properly
			super(initialValue, minimumValue, maximumValue, STEP_SIZE);
			
			// Initialize the local variables
			maximum = maximumValue;
			minimum = minimumValue;
		}
		
		/**
		 * Gets the current value of the spinner.
		 */
		@Override
		public Object getValue() {
			
			return super.getValue();
		}
		
		/**
		 * Gets the next value of the spinner (the value returned if the up arrow is pressed).
		 */
		@Override
		public Object getNextValue() {
			
			Integer nextValue = (Integer) super.getNextValue();
			
			if (nextValue != null)
			{
				if (nextValue == 0)
				{
					return 1;
				}
				else if (nextValue > maximum)
				{
					return maximum;
				}
				else
				{
					return nextValue;
				}
			}
			else
			{
				return super.getValue();
			}
		}
		
		/**
		 * Gets the previous value of the spinner (the value returned if the down arrow is pressed).
		 */
		@Override
		public Object getPreviousValue() {
			
			Integer previousValue = (Integer) super.getPreviousValue();
			
			if (previousValue != null)
			{
				if (previousValue == 0)
				{
					return -1;
				}
				else if (previousValue < minimum)
				{
					return minimum;
				}
				else
				{
					return previousValue;
				}
			}
			else
			{
				return super.getValue();
			}
		}
		
		/**
		 * Sets the value of the spinner according to the input parameter.
		 */
		@Override
		public void setValue(Object object) {
			
			Integer inputValue = (Integer) object;
			Integer currentValue = (Integer) super.getValue();
			
			if (inputValue > maximum)
			{
				super.setValue(maximum);
			}
			else if (inputValue < minimum)
			{
				super.setValue(minimum);
			}
			else if (inputValue == 0 && currentValue >= 0)
			{
				super.setValue(1);
			}
			else if (inputValue == 0 && currentValue < 0)
			{
				super.setValue(-1);
			}
			else
			{
				super.setValue(inputValue);
			}
		}
	}
}
