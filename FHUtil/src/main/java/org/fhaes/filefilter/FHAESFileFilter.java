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
package org.fhaes.filefilter;

import java.io.File;

import javax.swing.filechooser.FileFilter;

/**
 * FHAESFileFilter Class.
 */
public abstract class FHAESFileFilter extends FileFilter {

	public abstract String getPreferredFileExtension();

	/**
	 * Method to get the extension of the file, in lowercase.
	 */
	protected String getExtension(File f) {

		String s = f.getName();
		int i = s.lastIndexOf('.');
		if (i > 0 && i < s.length() - 1)
			return s.substring(i + 1).toLowerCase();
		return "";
	}
}
