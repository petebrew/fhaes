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

import java.awt.Image;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Enumeration;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

import javax.swing.ImageIcon;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Builder Class. Class that provides easy access to images, icons, version and build info.
 * 
 * @author pbrewer
 */
public class Builder {

	private final static ClassLoader cl = org.fhaes.util.Builder.class.getClassLoader();
	private static final Logger log = LoggerFactory.getLogger(Builder.class);
	private static final String VERSION = Builder.class.getPackage().getImplementationVersion();

	/**
	 * TODO
	 * 
	 * @param name
	 * @return
	 */
	public static ImageIcon getImageIcon(String name) {

		java.net.URL url = cl.getResource(getResourceURL(name));

		if (url != null)
		{
			return new ImageIcon(url);
		}
		else
		{
			log.error("Unabled to find icon " + name + ".  Replacing with the 'missing icon' icon.");
			return null;
		}

	}

	public static Image getApplicationIcon() {

		return Builder.getImage("logo22x22.png");
	}

	public static Image getImage(String name) {

		java.net.URL url = cl.getResource(getResourceURL(name));
		if (url != null)
			return new ImageIcon(url).getImage();
		else
			return null;
	}

	public static String getResourceURL(String name) {

		StringBuffer urlBuffer = new StringBuffer();

		String packagename = "images";

		urlBuffer.append(packagename);
		urlBuffer.append('/');
		urlBuffer.append(name);

		// log.debug("Icon url: "+urlBuffer.toString());
		return urlBuffer.toString();

	}

	public static String getRevisionNumber() {

		log.debug("Getting revision number");
		String revision = "Unknown";
		@SuppressWarnings("rawtypes")
		Enumeration resEnum;
		try
		{
			resEnum = Thread.currentThread().getContextClassLoader().getResources(JarFile.MANIFEST_NAME);
			while (resEnum.hasMoreElements())
			{
				try
				{
					URL url = (URL) resEnum.nextElement();
					InputStream is = url.openStream();
					if (is != null)
					{
						Manifest manifest = new Manifest(is);
						Attributes mainAttribs = manifest.getMainAttributes();
						revision = mainAttribs.getValue("Implementation-Build");

						if (revision != null && revision.length() > 0)
						{
							log.error("Raw value for 'Implementation-Build' : " + revision);
							if (revision.equals("${buildNumber}"))
								return "Unknown";

							return revision;
						}
					}
				}
				catch (Exception e)
				{
					// Silently ignore wrong manifests on classpath?
				}
			}
		}
		catch (IOException e1)
		{
			// Silently ignore wrong manifests on classpath?
		}
		return "Unknown";
	}

	public static String getBuildTimestamp() {

		String revision = "Unknown";
		@SuppressWarnings("rawtypes")
		Enumeration resEnum;
		try
		{
			resEnum = Thread.currentThread().getContextClassLoader().getResources(JarFile.MANIFEST_NAME);
			while (resEnum.hasMoreElements())
			{
				try
				{
					URL url = (URL) resEnum.nextElement();
					InputStream is = url.openStream();
					if (is != null)
					{
						Manifest manifest = new Manifest(is);
						Attributes mainAttribs = manifest.getMainAttributes();
						revision = mainAttribs.getValue("Implementation-Build-Timestamp");
						if (revision != null && revision.length() > 0)
						{
							return revision;
						}
					}
				}
				catch (Exception e)
				{
					// Silently ignore wrong manifests on classpath?
				}
			}
		}
		catch (IOException e1)
		{
			// Silently ignore wrong manifests on classpath?
		}
		return "Unknown";
	}

	public final static String getVersion() {

		if (Builder.VERSION == null)
		{
			return "development";

		}

		// Replace -SNAPSHOT with beta symbol
		if (Builder.VERSION.contains("-SNAPSHOT"))
		{
			return Builder.VERSION.replace("-SNAPSHOT", "\u03B2");
		}
		return Builder.VERSION;
	}

	public final static String getVersionAndBuild() {

		return getVersion() + " (r." + getRevisionNumber() + ")";
	}

}
