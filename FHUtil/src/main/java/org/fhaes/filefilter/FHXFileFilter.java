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

import org.apache.commons.io.FilenameUtils;

/**
 * FHXFileFilter Class.
 */
public class FHXFileFilter extends FHAESFileFilter {

	/**
	 * TODO
	 */
	@Override
	public boolean accept(File f) {

		if (FilenameUtils.getExtension(f.getAbsolutePath()).toLowerCase().equals("fhx") || f.isDirectory())
		{
			return true;
		}
		return false;
	}

	/**
	 * TODO
	 */
	@Override
	public String getDescription() {

		return "Fire History Exchange format (.fhx)";
	}

	/**
	 * TODO
	 */
	@Override
	public String getPreferredFileExtension() {

		return "fhx";
	}
}
