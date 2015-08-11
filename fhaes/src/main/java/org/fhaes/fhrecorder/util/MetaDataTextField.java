/**************************************************************************************************
 * Fire History Analysis and Exploration System (FHAES), Copyright (C) 2015
 * 
 * Contributors: Alex Beatty, Clayton Bodendein, Kyle Hartmann, Scott Goble, and Peter Brewer
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
package org.fhaes.fhrecorder.util;

import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JTextField;

/**
 * MetaDataTextField Class. This class is a specialized type of text field that will be used to display meta data.
 * 
 * @author beattya
 */
public class MetaDataTextField extends JTextField implements KeyListener, FocusListener {
	
	private static final long serialVersionUID = 1L;
	
	/**
	 * Constructor for MetaDataTextField.
	 */
	public MetaDataTextField() {
		
		this.addKeyListener(this);
		this.addFocusListener(this);
	}
	
	@Override
	public void keyPressed(KeyEvent e) {
		
		if (e.getKeyChar() == KeyEvent.VK_ENTER)
			this.transferFocus();
	}
	
	@Override
	public void keyReleased(KeyEvent e) {
	
	}
	
	@Override
	public void keyTyped(KeyEvent e) {
	
	}
	
	@Override
	public void focusGained(FocusEvent fe) {
	
	}
	
	@Override
	public void focusLost(FocusEvent fe) {
	
	}
}
