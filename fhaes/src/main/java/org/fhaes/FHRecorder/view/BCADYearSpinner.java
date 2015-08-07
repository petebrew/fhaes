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

import java.awt.Dimension;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.text.ParseException;

import javax.swing.JSpinner;
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
	private Integer previousValue;
	
	/**
	 * Constructs a new BCADYearSpinner.
	 */
	public BCADYearSpinner() {
		
		initGUI();
	}
	
	/**
	 * Gets the default editor for the spinner.
	 * 
	 * @return the editor as a default editor
	 */
	private JSpinner.DefaultEditor getDefaultEditor() {
		
		return (JSpinner.DefaultEditor) this.getEditor();
	}
	
	/**
	 * Gets the number formatter for the spinner.
	 * 
	 * @return the spinner number formatter
	 */
	private NumberFormatter getNumberFormatter() {
		
		return ((NumberFormatter) ((JSpinner.NumberEditor) this.getEditor()).getTextField().getFormatter());
	}
	
	/**
	 * Gets the spinner number model for the spinner.
	 * 
	 * @return the spinner number model
	 */
	private SpinnerNumberModel getNumberModel() {
		
		return (SpinnerNumberModel) this.getModel();
	}
	
	/**
	 * Gets the spinner's current value as an integer.
	 * 
	 * @return the spinner's current value as an integer
	 */
	public Integer getValueAsInteger() {
		
		return (Integer) this.getValue();
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
	 */
	private void initGUI() {
		
		this.getNumberFormatter().setAllowsInvalid(false);
		this.getNumberFormatter().setCommitsOnValidEdit(true);
		this.setEditor(new JSpinner.NumberEditor(this, NUMBER_EDITOR_FORMAT));
		this.setMinimumSize(new Dimension(SPINNER_WIDTH, SPINNER_HEIGHT));
		
		// Workaround to enable manual editing of spinner
		this.getDefaultEditor().getTextField().setFocusTraversalKeysEnabled(false);
		this.getDefaultEditor().getTextField().addKeyListener(new KeyListener() {
			
			@Override
			public void keyPressed(KeyEvent evt) {
				
				if (evt.getKeyChar() == KeyEvent.VK_BACK_SPACE)
				{
					getDefaultEditor().getTextField().selectAll();
					evt.consume();
				}
				else if (evt.getKeyChar() == KeyEvent.VK_PLUS)
				{
					getDefaultEditor().getTextField().select(0, 0);
					int currentSpinnerValue = (Integer) getDefaultEditor().getTextField().getValue();
					
					if (currentSpinnerValue < 0)
					{
						currentSpinnerValue = currentSpinnerValue * -1;
					}
					
					getDefaultEditor().getTextField().setValue(currentSpinnerValue);
					evt.consume();
				}
				else if (evt.getKeyChar() == KeyEvent.VK_MINUS)
				{
					getDefaultEditor().getTextField().select(0, 0);
					int currentSpinnerValue = (Integer) getDefaultEditor().getTextField().getValue();
					
					if (currentSpinnerValue > 0)
					{
						currentSpinnerValue = currentSpinnerValue * -1;
					}
					
					getDefaultEditor().getTextField().setValue(currentSpinnerValue);
					evt.consume();
				}
				else if (evt.getKeyChar() == KeyEvent.VK_UP)
				{
					getDefaultEditor().getTextField().select(0, 0);
					int currentSpinnerValue = (Integer) getDefaultEditor().getTextField().getValue();
					
					if (currentSpinnerValue < (Integer) getNumberModel().getMaximum())
					{
						currentSpinnerValue = currentSpinnerValue + 1;
					}
					
					getDefaultEditor().getTextField().setValue(currentSpinnerValue);
					evt.consume();
				}
				else if (evt.getKeyChar() == KeyEvent.VK_DOWN)
				{
					getDefaultEditor().getTextField().select(0, 0);
					int currentSpinnerValue = (Integer) getDefaultEditor().getTextField().getValue();
					
					if (currentSpinnerValue > (Integer) getNumberModel().getMinimum())
					{
						currentSpinnerValue = currentSpinnerValue - 1;
					}
					
					getDefaultEditor().getTextField().setValue(currentSpinnerValue);
					evt.consume();
				}
			}
			
			@Override
			public void keyReleased(KeyEvent evt) {}
			
			@Override
			public void keyTyped(KeyEvent evt) {
				
				if (evt.getKeyChar() == KeyEvent.VK_TAB || evt.getKeyChar() == KeyEvent.VK_ENTER)
				{
					try
					{
						getDefaultEditor().getTextField().commitEdit();
					}
					catch (ParseException ex)
					{
						ex.printStackTrace();
					}
				}
			}
		});
		
		// Make sure to initialize the previous value of the spinner
		updatePreviousValue();
	}
}
