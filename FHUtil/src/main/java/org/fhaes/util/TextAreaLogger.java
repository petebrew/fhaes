/*******************************************************************************
 * Copyright (c) 2013 Peter Brewer
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 * 
 * Contributors:
 *     Peter Brewer
 *     Elena Velasquez
 ******************************************************************************/
package org.fhaes.util;

import javax.swing.JTextArea;

import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.spi.LoggingEvent;

/**
 * TextAreaLogger Class.
 */
public class TextAreaLogger extends AppenderSkeleton {

	private JTextArea myTextArea;
	private PatternLayout layout;

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
	public void close() {

	}

	/**
	 * TOOD
	 */
	public boolean requiresLayout() {

		return false;
	}
}
