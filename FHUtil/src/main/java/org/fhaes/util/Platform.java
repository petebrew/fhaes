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

package org.fhaes.util;

import java.awt.Desktop;
import java.awt.Frame;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import javax.swing.JOptionPane;
import javax.swing.UIManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Platform Class. Class for handling OS specific functions.
 * 
 * @author pbrewer
 */
public class Platform {

	private static final Logger log = LoggerFactory.getLogger(Platform.class);

	/**
	 * Determines whether this operating system is MacOSX.
	 * 
	 * @return
	 */
	public static boolean isOSX() {

		return (System.getProperty("os.name").startsWith("Mac"));
	}

	/**
	 * Set the look and feel to Nimbus on non-OSX platforms. On OSX set the standard Apple GUI conventions.
	 */
	public static void setLookAndFeel() {

		if (!Platform.isOSX())
		{
			// For non-MacOSX systems set Nimbus as LnF
			try
			{
				UIManager.setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");
			}
			catch (Exception e)
			{
				log.warn("Error setting Nimbus look at feel");
			}
		}
		else
		{
			// On MacOSX set standard GUI conventions...

			System.setProperty("apple.laf.useScreenMenuBar", "true");
			System.setProperty("com.apple.mrj.application.apple.menu.about.name", "FHAES");
			System.setProperty("com.apple.macos.use-file-dialog-packages", "false"); // for AWT
			System.setProperty("com.apple.mrj.application.growbox.intrudes", "false");
			UIManager.put("JFileChooser.packageIsTraversable", "never"); // for swing
			// new MacOSMods();
		}
	}

	/**
	 * Open the specified URL in a web browser.
	 * 
	 * @param url
	 */
	public static void browseWebpage(String url) {

		Platform.browseWebpage(url, null);
	}

	/**
	 * Open the specified URL in a web browser. Any error message will be centered around the parent frame
	 * 
	 * @param url
	 * @param parent
	 */
	public static void browseWebpage(String url, Frame parent) {

		try
		{
			URI uri = new URI(url);
			Desktop.getDesktop().browse(uri);
		}
		catch (UnsupportedOperationException e1)
		{
			JOptionPane.showMessageDialog(parent, "Unable to open webpage");
		}
		catch (IOException e1)
		{
			log.error("Unable to open URL");
		}
		catch (URISyntaxException e1)
		{
		}
	}
}
