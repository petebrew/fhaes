package org.fhaes.components;

import javax.swing.Icon;
import javax.swing.JMenuItem;

import org.fhaes.util.FHAESAction;
import org.fhaes.util.Platform;

/**
 * FHAESMenuItem Class. This is a specialist type of JMenuItem which simply forces no icon when run from OSX
 * 
 * @author pbrewer
 */
public class FHAESMenuItem extends JMenuItem {

	private static final long serialVersionUID = 1L;

	/**
	 * Construct a FHAESMenuItem from a FHAESAction.
	 * 
	 * @param action
	 */
	public FHAESMenuItem(FHAESAction action) {

		super(action);

		if (Platform.isOSX())
		{
			this.setIcon(null);
		}
	}

	/**
	 * Get the icon associated with this MenuItem. If the current platform is OSX then the icon is null to fit with the standard look and
	 * feel of the OS.
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
