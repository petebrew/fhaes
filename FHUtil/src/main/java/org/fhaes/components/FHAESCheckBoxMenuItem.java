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
	public Icon getIcon() {

		if (Platform.isOSX())
		{
			return null;
		}

		return super.getIcon();
	}
}
