package org.fhaes.tools;

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

import java.awt.Desktop;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;

import javax.swing.JOptionPane;
import javax.swing.SwingWorker;

import org.fhaes.preferences.App;
import org.fhaes.preferences.FHAESPreferences.PrefKey;
import org.fhaes.util.Builder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * UpdateChecker Class.
 */
public class UpdateChecker {

	private static final Logger log = LoggerFactory.getLogger(UpdateChecker.class);

	private static String downloadURL = "http://download.fhaes.org/";
	private static String updateCheckURL = "http://download.fhaes.org/.currentversioninfo";

	/**
	 * Standard call for automatic update check.
	 */
	public void programmaticCheckForUpdates() {

		// Only check if the user hasn't forbidden it
		if (!App.prefs.getBooleanPref(PrefKey.DONT_CHECK_FOR_UPDATES, false))
		{
			int currtime = (int) (System.currentTimeMillis() / 1000);
			int prevtime = App.prefs.getIntPref(PrefKey.UPDATES_LATE_CHECKED, 0);
			int threshold = currtime - (60 * 60 * 24 * 2);

			if (prevtime < threshold)
			{
				log.debug("Checking FHAES server for any available updates...");
				Task task = new Task(false);
				task.execute();
			}
			else
			{
				log.debug("Not long enough since last update check so not checking");
			}
		}
		else
		{
			log.debug("User has requested that we don't check for updates");
		}
	}

	/**
	 * Check for updates, overriding pref that might cause update to skip. Also shows confirmation at end.
	 */
	public void manualCheckForUpdates() {

		log.debug("Forcing check of FHAES server for any available updates...");
		Task task = new Task(true);
		task.execute();
		log.debug("Available version: " + task.available);
	}

	private static void showDownloadFailedDialog() {

		JOptionPane.showMessageDialog(App.mainFrame, "Unable to access download site.  Please update manually.");
	}

	/**
	 * Query the tridas.org server for the latest available build number. Returns null on IO error.
	 * 
	 * @return
	 */
	private static String getAvailableVersion() {

		URL url;

		try
		{
			url = new URL(updateCheckURL);
		}
		catch (MalformedURLException e)
		{
			return null;
		}

		try
		{
			URLConnection conn = url.openConnection();
			BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			String inputLine;

			while ((inputLine = in.readLine()) != null)
				return inputLine;
			in.close();

		}
		catch (IOException e)
		{
			return null;
		}

		return null;
	}

	class Task extends SwingWorker<Void, Void> {

		Boolean showConfirmation;
		Boolean dontaskagain = false;
		Boolean upgradeAvailable;
		Integer available;
		Integer current;
		private final Logger tasklog = LoggerFactory.getLogger(SwingWorker.class);

		public Task(Boolean showConfirmation) {

			this.showConfirmation = showConfirmation;
		}

		@Override
		public Void doInBackground() {

			// Check server for current available version
			String availableVersion = getAvailableVersion();
			if (availableVersion == null)
			{
				tasklog.warn("Unable to determine latest available version from server");
				return null;
			}

			try
			{
				available = Integer.parseInt(availableVersion);
				tasklog.info("Revision " + available + " is available on the FHAES website");
			}
			catch (Exception e)
			{
				tasklog.warn("Unable to determine latest available version from server");
				return null;
			}

			try
			{

				current = Integer.parseInt(Builder.getRevisionNumber());
				tasklog.info("You are currently running revision " + current);

			}
			catch (Exception e)
			{
				tasklog.warn("Unable to determine current version number.  You are probably running a development snapshot.");
				return null;
			}

			// Compare available and current build numbers
			upgradeAvailable = available.compareTo(current) > 0;

			return null;
		}

		@Override
		public void done() {

			int currtime = (int) (System.currentTimeMillis() / 1000);
			App.prefs.setIntPref(PrefKey.UPDATES_LATE_CHECKED, currtime);

			log.debug("Current version  : " + current);
			log.debug("Available version: " + available);

			if (upgradeAvailable == null)
			{
				log.warn("Failed to determine if an update is available");

				if (showConfirmation)
				{
					if (current == null)
					{
						JOptionPane
								.showMessageDialog(App.mainFrame,
										"Failed to determine your current version number.  You are probably running a\ndevelopment snapshot and should check for updates manually.");
						return;
					}
					else if (available == null)
					{
						JOptionPane.showMessageDialog(App.mainFrame,
								"Failed to determine the most recent version number.  Please check the FHAES website directly.");
						return;
					}

					JOptionPane.showMessageDialog(App.mainFrame, "Failed to determine if an update is available.");
				}

				return;
			}
			else if (upgradeAvailable)
			{
				log.info("Newer version of FHAES is available");
				Object[] options = { "Yes", "Maybe later", "Don't ask again" };
				int response = JOptionPane.showOptionDialog(App.mainFrame,
						"A new version of FHAES is available.  Would you like to download it now?", "Confirm",
						JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null,     // do not use a custom Icon
						options,  // the titles of buttons
						options[0]); // default button title

				if (response == JOptionPane.YES_OPTION)
				{
					try
					{
						Desktop.getDesktop().browse(new URI(downloadURL));
					}
					catch (IOException e)
					{
						showDownloadFailedDialog();
						return;
					}
					catch (URISyntaxException e)
					{
						showDownloadFailedDialog();
						return;
					}
				}
				else if (response == JOptionPane.CANCEL_OPTION)
				{
					App.prefs.setBooleanPref(PrefKey.DONT_CHECK_FOR_UPDATES, true);
				}

			}
			else
			{
				tasklog.info("FHAES is up-to-date");
				if (showConfirmation)
				{
					JOptionPane.showMessageDialog(App.mainFrame, "Your installation of FHAES is up-to-date.");
				}
			}
		}
	}

}
