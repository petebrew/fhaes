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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.awt.Dimension;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Calendar;

import javax.swing.SpinnerNumberModel;
import javax.swing.text.NumberFormatter;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * BCADYearSpinner Class. A spinner that supports a model for representing BC and AD years.
 * 
 * @author Joshua Brogan
 */
public class BCADYearSpinner extends javax.swing.JSpinner {
	
	private static final long serialVersionUID = 1L;
	
	// Declare logger
	private static final Logger log = LoggerFactory.getLogger(BCADYearSpinner.class);
	
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
	
		// Account for cases where the initial value may be out of range
		if (initialValue < minimumValue)
		{
			log.warn("initialValue parameter was below minimumValue parameter (please adjust the value in the constructer call)");
			initialValue = minimumValue;
		}
		else if (initialValue > maximumValue)
		{
			log.warn("initialValue parameter was above maximumValue parameter (please adjust the value in the constructer call)");
			initialValue = maximumValue;
		}
		
		// Account for cases where the initial value may be zero
		if (initialValue == 0)
		{
			log.warn("initialValue parameter was zero (please adjust the value in the constructer call)");
			initialValue += 1;
		}
		
		// Setup the year spinner with valid values
		initGUI(initialValue, minimumValue, maximumValue);
	}
	
	/**
	 * Constructs a BCADYearSpinner with a value set as the current year, the max value as the current year and the minimum value as the
	 * minimum number supported by a Java int.
	 */
	public BCADYearSpinner() {
	
		int year = Calendar.getInstance().get(Calendar.YEAR);
		// Setup the year spinner with valid values
		initGUI(year, Integer.MIN_VALUE, year);
		
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
	 * Sets the spinner's current value using the input value.
	 * 
	 * @param inValue
	 */
	public void setValueFromInteger(int inValue) {
	
		this.getModel().setValue(inValue);
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
			public void keyPressed(KeyEvent evt) {
			
			}
			
			@Override
			public void keyReleased(KeyEvent evt) {
			
			}
			
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
	
	/**
	 * BCADYearSpinnerUnitTest Class. This class contains the unit test for BCADYearSpinner.
	 */
	public static class BCADYearSpinnerUnitTest {
		
		/**
		 * Runs the unit test for BCADYearSpinner.
		 */
		@Test
		public void runTestCases() {
		
			// Create BCADYearSpinner with nominal values
			{
				BCADYearSpinner testSpinner = new BCADYearSpinner(5, 1, 10);
				assertNotNull(testSpinner);
				assertEquals((Integer) 5, testSpinner.getValueAsInteger());
			}
			
			// Create BCADYearSpinner with initial value below minimum value
			{
				BCADYearSpinner testSpinner = new BCADYearSpinner(-10, 1, 10);
				assertNotNull(testSpinner);
				assertEquals((Integer) 1, testSpinner.getValueAsInteger());
			}
			
			// Create BCADYearSpinner with initial value above maximum value
			{
				BCADYearSpinner testSpinner = new BCADYearSpinner(20, 1, 10);
				assertNotNull(testSpinner);
				assertEquals((Integer) 10, testSpinner.getValueAsInteger());
			}
			
			// Create BCADYearSpinner with initial value as zero
			{
				BCADYearSpinner testSpinner = new BCADYearSpinner(0, -10, 10);
				assertNotNull(testSpinner);
				assertEquals((Integer) 1, testSpinner.getValueAsInteger());
			}
			
			// Test behavior of BCAD model when using getters and setters
			{
				BCADYearSpinner testSpinner = new BCADYearSpinner(1, -10, 10);
				
				// Test nominal value
				testSpinner.setValueFromInteger(5);
				assertEquals((Integer) 5, testSpinner.getValueAsInteger());
				
				// Test set value below minimum
				testSpinner.setValueFromInteger(-20);
				assertEquals((Integer) (-10), testSpinner.getValueAsInteger());
				
				// Test zero case with previous value below zero
				testSpinner.setValueFromInteger(0);
				assertEquals((Integer) (-1), testSpinner.getValueAsInteger());
				
				// Test set value below minimum
				testSpinner.setValueFromInteger(20);
				assertEquals((Integer) 10, testSpinner.getValueAsInteger());
				
				// Test zero case with previous value above zero
				testSpinner.setValueFromInteger(0);
				assertEquals((Integer) 1, testSpinner.getValueAsInteger());
			}
			
			// Test behavior of getNextValue method
			{
				BCADYearSpinner testSpinner = new BCADYearSpinner(1, -10, 10);
				
				// Test nominal value
				assertEquals(2, testSpinner.getNextValue());
				
				// Test at maximum value
				testSpinner.setValueFromInteger(10);
				assertEquals(10, testSpinner.getNextValue());
				
				// Test zero case
				testSpinner.setValueFromInteger(-1);
				assertEquals(1, testSpinner.getNextValue());
			}
			
			// Test behavior of getPreviousValue method
			{
				BCADYearSpinner testSpinner = new BCADYearSpinner(-1, -10, 10);
				
				// Test nominal value
				assertEquals(-2, testSpinner.getPreviousValue());
				
				// Test at minimum value
				testSpinner.setValueFromInteger(-10);
				assertEquals(-10, testSpinner.getPreviousValue());
				
				// Test zero case
				testSpinner.setValueFromInteger(1);
				assertEquals(-1, testSpinner.getPreviousValue());
			}
			
			// Test behavior of mostRecentValue
			{
				BCADYearSpinner testSpinner = new BCADYearSpinner(1, -10, 10);
				
				// Test initial value of mostRecentValue
				assertEquals((Integer) 1, testSpinner.getMostRecentValue());
				
				// Test before and after updating mostRecentValue
				testSpinner.setValueFromInteger(2);
				assertEquals((Integer) 1, testSpinner.getMostRecentValue());
				testSpinner.updateMostRecentValue();
				assertEquals((Integer) 2, testSpinner.getMostRecentValue());
			}
			
			// Notify that all tests have passed for this unit
			log.info("All tests passed for BCADYearSpinner");
		}
	}
}
