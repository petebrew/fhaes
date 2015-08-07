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
package org.fhaes.FHRecorder.util;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

/**
 * LengthRestrictedDocument Class. The source code for this class was found at
 * http://stackoverflow.com/questions/13075564/limiting-length-of-input-in-jtextfield-is-not-working.
 * 
 * @author Duncan Jones
 */
public final class LengthRestrictedDocument extends PlainDocument {
	
	private static final long serialVersionUID = 1L;
	private final int limit;
	
	/**
	 * Constructor for LengthRestrictedDocument.
	 * 
	 * @param limit - length limit of document
	 */
	public LengthRestrictedDocument(int limit) {
		
		super();
		this.limit = limit;
	}
	
	// Overrides the insertString method of Document
	// @param offs offset
	// @param str string
	// @param a set of attributes
	
	/**
	 * Overrides the insertString method of Document.
	 * 
	 * @param offs offset
	 * @param str string
	 * @param a set of attributes
	 */
	@Override
	public void insertString(int offs, String str, AttributeSet a) throws BadLocationException {
		
		if (limit == 0 || getLength() + str.length() <= limit)
		{
			super.insertString(offs, str, a);
		}
	}
}
