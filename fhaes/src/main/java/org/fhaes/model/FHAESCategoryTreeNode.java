package org.fhaes.model;

/*******************************************************************************
 * Copyright (C) 2013 Peter Brewer
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * Contributors:
 *     Peter Brewer
 ******************************************************************************/

import javax.swing.ImageIcon;
import javax.swing.tree.DefaultMutableTreeNode;

/**
 * FHAESCategoryTreeNode Class. Parent tree node representing a category of results
 * 
 * @author pwb48
 */
public class FHAESCategoryTreeNode extends DefaultMutableTreeNode {

	private static final long serialVersionUID = 1L;
	private ImageIcon icon;

	public FHAESCategoryTreeNode(String name, ImageIcon icon) {

		super(name);
		this.icon = icon;
	}

	public FHAESCategoryTreeNode(String name) {

		super(name);
	}

	@Override
	public boolean getAllowsChildren() {

		return true;
	}

	public ImageIcon getIcon() {

		return icon;
	}
}
