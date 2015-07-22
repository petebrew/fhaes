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

package org.fhaes.components;

import javax.swing.JButton;

import org.fhaes.util.Builder;
import org.fhaes.util.FHAESAction;
import org.fhaes.util.Platform;

/**
 * JToolBarButton Class. An extension to JButton to style for use on a JToolBar. This set various behaviours like not focusable, tooltips,
 * icon etc
 * 
 * @author pbrewer
 * @see JToolBarToggleButton
 */
public class JToolBarButton extends JButton {

	private static final long serialVersionUID = 1L;

	/**
	 * This constructor takes the name of an iconFile and the text for the tooltip. Note the action for this button must be added manually.
	 * 
	 * @param iconFile - File name within resources/image folder
	 * @param tooltip
	 */
	public JToolBarButton(String iconFile, String tooltip) {

		putClientProperty("JButton.buttonType", "textured");

		setIcon(Builder.getImageIcon(iconFile));
		setToolTipText(tooltip);
		setRolloverEnabled(true);
		setFocusable(false);

	}

	/**
	 * Standard constructor simply takes a FHAESAction and does the rest automatically.
	 * 
	 * @param action
	 */
	public JToolBarButton(FHAESAction action) {

		super(action);

		putClientProperty("JButton.buttonType", "textured");

		setText(null);
		setFocusable(false);
		setToolTipText(action.getToolTipText());
		setRolloverEnabled(true);

		if (Platform.isOSX() && action.getShortName() != null)
		{
			setText(action.getShortName());
		}
	}
}
