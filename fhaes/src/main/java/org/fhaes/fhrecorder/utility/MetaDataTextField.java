/*******************************************************************************
 * Copyright (C) 2013 Alex Beatty, Clayton Bodendein, Kyle Hartmann, 
 * Scott Goble and Peter Brewer
 * 
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 * 
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 * 
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/

/*******************************************************************************
 * Maintenance Log (Spring 2014)
 * 
 *     All maintenance work was performed collectively by Josh Brogan, 
 *     Jake Lokkesmoe and Chinmay Shah.
 *     
 *     1) Added various method comments and normalized general code structure.
 ******************************************************************************/
package org.fhaes.fhrecorder.utility;

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

	public void keyPressed(KeyEvent e) {

		if (e.getKeyChar() == KeyEvent.VK_ENTER)
			this.transferFocus();
	}

	public void keyReleased(KeyEvent e) {

	}

	public void keyTyped(KeyEvent e) {

	}

	public void focusGained(FocusEvent fe) {

	}

	public void focusLost(FocusEvent fe) {

	}
}
