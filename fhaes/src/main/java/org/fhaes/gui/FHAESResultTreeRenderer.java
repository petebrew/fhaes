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
package org.fhaes.gui;

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

import java.awt.Component;

import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;

import org.fhaes.model.FHAESCategoryTreeNode;
import org.fhaes.model.FHAESResultTreeNode;

/**
 * FHAESResultTreeRenderer Class.
 */
public class FHAESResultTreeRenderer extends DefaultTreeCellRenderer {

	private static final long serialVersionUID = 1L;

	/**
	 * TODO
	 */
	@Override
	public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row,
			boolean hasFocus) {

		Component component = super.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, hasFocus);

		if (value instanceof FHAESResultTreeNode)
		{
			this.setIcon(((FHAESResultTreeNode) value).getIcon());
			if (!((FHAESResultTreeNode) value).isEnabled())
				this.setEnabled(false);
		}
		else if (value instanceof FHAESCategoryTreeNode)
		{
			// this.setIcon(((FHAESCategoryTreeNode) value).getIcon());
			this.setIcon(null);
		}

		return component;
	}
}
