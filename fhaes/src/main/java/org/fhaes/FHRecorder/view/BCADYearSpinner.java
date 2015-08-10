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
package org.fhaes.FHRecorder.view;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JButton;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.plaf.basic.BasicSpinnerUI;
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
	private Integer previousValue;
	
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
	 * Gets the number formatter for the spinner.
	 * 
	 * @return the spinner's number formatter
	 */
	private NumberFormatter getNumberFormatter() {
		
		return ((NumberFormatter) ((JSpinner.NumberEditor) this.getEditor()).getTextField().getFormatter());
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
	 * Gets the spinner's previous value.
	 */
	@Override
	public Integer getPreviousValue() {
		
		return previousValue;
	}
	
	/**
	 * Updates the spinner's previous value with the spinner's current value.
	 */
	public void updatePreviousValue() {
		
		previousValue = getValueAsInteger();
	}
	
	/**
	 * Initializes the GUI.
	 * 
	 * @param initialValue
	 * @param minimumValue
	 * @param maximumValue
	 */
	private void initGUI(int initialValue, int minimumValue, int maximumValue) {
		
		this.getNumberFormatter().setAllowsInvalid(false);
		this.getNumberFormatter().setCommitsOnValidEdit(true);
		this.setEditor(new JSpinner.NumberEditor(this, NUMBER_EDITOR_FORMAT));
		this.setMinimumSize(new Dimension(SPINNER_WIDTH, SPINNER_HEIGHT));
		this.setModel(new BCADYearSpinnerModel(initialValue, minimumValue, maximumValue));
		this.setUI(new BCADSpinnerUI(this));
		
		// Workaround to block keyboard editing of the spinner
		((JSpinner.DefaultEditor) this.getEditor()).getTextField().setFocusTraversalKeysEnabled(false);
		((JSpinner.DefaultEditor) this.getEditor()).getTextField().addKeyListener(new KeyListener() {
			
			@Override
			public void keyPressed(KeyEvent evt) {
				
				if (evt.getKeyCode() == KeyEvent.VK_UP || evt.getKeyCode() == KeyEvent.VK_KP_UP)
				{
					evt.consume();
				}
				else if (evt.getKeyCode() == KeyEvent.VK_DOWN || evt.getKeyCode() == KeyEvent.VK_KP_DOWN)
				{
					evt.consume();
				}
			}
			
			@Override
			public void keyReleased(KeyEvent evt) {}
			
			@Override
			public void keyTyped(KeyEvent evt) {}
			
		});
		
		// Make sure to initialize the previous value of the spinner
		updatePreviousValue();
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
		
		@Override
		public Object getValue() {
			
			return super.getValue();
		}
		
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
		
		@Override
		public void setValue(Object object) {
			
			Integer currentValue = (Integer) super.getValue();
			
			if ((Integer) object > maximum)
			{
				super.setValue(maximum);
			}
			else if ((Integer) object < minimum)
			{
				super.setValue(minimum);
			}
			else if ((Integer) object == 0 && currentValue > 0)
			{
				super.setValue(1);
			}
			else if ((Integer) object == 0 && currentValue < 0)
			{
				super.setValue(-1);
			}
			else if ((Integer) object == 0 && currentValue == 0)
			{
				super.setValue(getPreviousValue());
			}
			else
			{
				super.setValue(object);
			}
		}
	}
	
	/**
	 * BCADSpinnerUI Class. Manual workaround for spinner bug which causes the down-arrow to not fire state-changed events.
	 */
	private class BCADSpinnerUI extends BasicSpinnerUI {
		
		// Declare local variables
		private BCADYearSpinner parent;
		
		public BCADSpinnerUI(BCADYearSpinner spinner) {
			
			parent = spinner;
		}
		
		@Override
		protected Component createPreviousButton() {
			
			JButton spinnerDownButton = (JButton) super.createPreviousButton();
			spinnerDownButton.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent ae) {
					
					Object previousValue = parent.getModel().getPreviousValue();
					parent.getModel().setValue(previousValue);
				}
			});
			
			return spinnerDownButton;
		}
	}
}
