/**************************************************************************************************
 * Fire History Analysis and Exploration System (FHAES), Copyright (C) 2015
 * 
 * Contributors: Peter Brewer
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
package org.fhaes.components;

import javax.swing.JToggleButton;

import org.fhaes.util.FHAESAction;
import org.fhaes.util.Platform;

/**
 * JToolBarToggleButton Class. Extension to the standrad JToggleButton for use on toolbars.
 * 
 * @author Peter Brewer
 * @see JToolBarButton
 */
public class JToolBarToggleButton extends JToggleButton {
	
	private static final long serialVersionUID = 1L;
	
	/**
	 * JToolBarToggleButton constructor which simply takes a FHAESAction
	 * 
	 * @param action
	 */
	public JToolBarToggleButton(FHAESAction action) {
		
		super(action);
		this.setFocusable(false);
		
		this.setToolTipText(action.getToolTipText());
		if (Platform.isOSX() && action.getShortName() != null)
		{
			setText(action.getShortName());
			
		}
	}
	
	/**
	 * JToolBarToggleButton constructor which takes a FHAESAction and a string for use as the tooltip
	 * 
	 * @param action
	 * @param tooltip
	 */
	public JToolBarToggleButton(FHAESAction action, String tooltip) {
		
		super(action);
		this.setToolTipText(tooltip);
		this.setFocusable(false);
		
		if (Platform.isOSX() && action.getShortName() != null)
		{
			setText(action.getShortName());
		}
	}
	
	/**
	 * Get text for this button - always returns null as this is for a toolbar
	 */
	@Override
	public String getText() {
		
		return null;
	}
}
