/**************************************************************************************************
 * Fire History Analysis and Exploration System (FHAES), Copyright (C) 2015 NOAA/NCDC
 * 
 * Contributors: Cay Horstmann, Wendy Gross, and Peter Brewer
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
package org.fhaes.util;

import java.awt.Dimension;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.beans.PropertyVetoException;
import java.io.Serializable;

import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.PlainDocument;

/**
 * FHIntTextBean Class.
 */
public class FHIntTextBean extends JTextField implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	/**
	 * TODO
	 */
	public FHIntTextBean() {
		
		this(0, 10);
	}
	
	/**
	 * TODO
	 * 
	 * @param defval
	 * @param size
	 */
	public FHIntTextBean(int defval, int size) {
		
		super("" + defval, size);
		addFocusListener(new FocusListener() {
			
			@Override
			public void focusGained(FocusEvent event) {
				
				if (!event.isTemporary())
				{
					lastValue = getValue();
				}
			}
			
			@Override
			public void focusLost(FocusEvent event) {
				
				if (!event.isTemporary())
				{
					editComplete();
				}
			}
		});
	}
	
	/**
	 * TODO
	 */
	public void editComplete() {
		
		Integer oldValue = new Integer(lastValue);
		Integer newValue = new Integer(getValue());
		try
		{
			fireVetoableChange("value", oldValue, newValue);
			// survived, therefore no veto
			firePropertyChange("value", oldValue, newValue);
		}
		catch (PropertyVetoException e)
		{ // someone didn't like it
			JOptionPane.showMessageDialog(this, "" + e, "Input Error", JOptionPane.WARNING_MESSAGE);
			setText("" + lastValue);
			requestFocus();
			// doesn't work in all JDK versions--see bug #4128659
		}
	}
	
	public int getValue() {
		
		try
		{
			return Integer.parseInt(getText());
		}
		catch (NumberFormatException exception)
		{
			return -1;
		}
	}
	
	public void setValue(int v) throws PropertyVetoException {
		
		Integer oldValue = new Integer(getValue());
		Integer newValue = new Integer(v);
		fireVetoableChange("value", oldValue, newValue);
		// survived, therefore no veto
		setText("" + v);
		firePropertyChange("value", oldValue, newValue);
	}
	
	@Override
	protected Document createDefaultModel() {
		
		return new IntTextDocument();
	}
	
	@Override
	public Dimension getMinimumSize() {
		
		return new Dimension(XMINSIZE, YMINSIZE);
	}
	
	private int lastValue;
	
	private static final int XMINSIZE = 50;
	
	private static final int YMINSIZE = 20;
	
	// public static void main() {
	// JFrame f = new JFrame()
	// asdf
	// }
}

class IntTextDocument extends PlainDocument {
	
	private static final long serialVersionUID = 1L;
	
	@Override
	public void insertString(int offs, String str, AttributeSet a) throws BadLocationException {
		
		if (str == null)
			return;
		String oldString = getText(0, getLength());
		String newString = oldString.substring(0, offs) + str + oldString.substring(offs);
		try
		{
			Integer.parseInt(newString + "0");
			super.insertString(offs, str, a);
		}
		catch (NumberFormatException e)
		{
		}
	}
}
