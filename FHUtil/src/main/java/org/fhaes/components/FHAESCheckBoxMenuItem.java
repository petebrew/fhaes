/**************************************************************************************************
 * Fire History Analysis and Exploration System (FHAES), Copyright (C) 2015
 * 
 * Contributors: Joshua Brogan and Peter Brewer
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

import javax.swing.Icon;
import javax.swing.JCheckBoxMenuItem;

import org.fhaes.util.FHAESAction;
import org.fhaes.util.Platform;

/**
 * FHAESCheckBoxMenuItem Class.
 * 
 * @author Joshua Brogan and Peter Brewer
 */
public class FHAESCheckBoxMenuItem extends JCheckBoxMenuItem {
	
	private static final long serialVersionUID = 1L;
	
	/**
	 * Construct a FHAESCheckBoxMenuItem from a FHAESAction.
	 * 
	 * @param action
	 */
	public FHAESCheckBoxMenuItem(FHAESAction action) {
		
		super(action);
		
		if (Platform.isOSX())
		{
			this.setIcon(null);
		}
	}
	
	/**
	 * Get the icon associated with this MenuItem. If the current platform is OSX then the icon is null to fit with the standard look and
	 * feel of the OS.
	 * 
	 * @return the icon
	 */
	@Override
	public Icon getIcon() {
		
		if (Platform.isOSX())
		{
			return null;
		}
		
		return super.getIcon();
	}
}
