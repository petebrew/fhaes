/**************************************************************************************************
 * Fire History Analysis and Exploration System (FHAES), Copyright (C) 2015
 * 
 * Contributors: Elena Velasquez and Peter Brewer
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

import javax.swing.JTextArea;

import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.spi.LoggingEvent;

/**
 * TextAreaLogger Class.
 */
public class TextAreaLogger extends AppenderSkeleton {
	
	private final JTextArea myTextArea;
	private final PatternLayout layout;
	
	/**
	 * TODO
	 * 
	 * @param lyout
	 * @param myTextArea
	 */
	public TextAreaLogger(PatternLayout lyout, JTextArea myTextArea) {
		
		this.myTextArea = myTextArea;
		this.layout = lyout;
	}
	
	/**
	 * TODO
	 */
	@Override
	protected void append(LoggingEvent arg0) {
		
		myTextArea.append(layout.format(arg0));
	}
	
	/**
	 * TODO
	 */
	@Override
	public void close() {
	
	}
	
	/**
	 * TOOD
	 */
	@Override
	public boolean requiresLayout() {
		
		return false;
	}
}
