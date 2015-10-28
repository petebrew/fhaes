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
package org.fhaes.model;

import java.awt.Component;
import java.text.DecimalFormat;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;

import org.fhaes.enums.EventTypeToProcess;
import org.fhaes.fhfilereader.FHFile;
import org.fhaes.preferences.App;
import org.fhaes.preferences.FHAESPreferences.PrefKey;
import org.fhaes.util.Builder;

/**
 * FHFileListCellRenderer Class.
 */
@SuppressWarnings({ "rawtypes" })
public class FHFileListCellRenderer extends DefaultListCellRenderer {
	
	private static final long serialVersionUID = 1L;
	
	/**
	 * Controls the rendering behavior of the FHFileList elements.
	 */
	@Override
	public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
		
		// Get FHFile
		FHFile file = null;
		try
		{
			file = (FHFile) value;
		}
		catch (NullPointerException e)
		{
			return this;
		}
		
		// Set colors
		if (isSelected)
		{
			setBackground(list.getSelectionBackground());
			setForeground(list.getSelectionForeground());
		}
		else
		{
			setBackground(list.getBackground());
			setForeground(list.getForeground());
		}
		
		// Set icon
		if (file.isValidFHXFile())
		{
			EventTypeToProcess eventType = App.prefs.getEventTypePref(PrefKey.EVENT_TYPE_TO_PROCESS, EventTypeToProcess.FIRE_EVENT);
			
			this.setIcon(Builder.getImageIcon("good.png"));
			
			// override good icon with warning if missing events
			if (eventType.equals(EventTypeToProcess.FIRE_EVENT) && !(file.hasFireEvents()))
			{
				this.setIcon(Builder.getImageIcon("warning.png"));
			}
			else if (eventType.equals(EventTypeToProcess.INJURY_EVENT) && !(file.hasInjuryEvents()))
			{
				this.setIcon(Builder.getImageIcon("warning.png"));
			}
		}
		else
		{
			this.setIcon(Builder.getImageIcon("bad.png"));
		}
		
		// Set tooltip
		DecimalFormat df = new DecimalFormat("#.####");
		
		String categoryFileAttached = "false";
		if (file.getCategoryFilePath() != null)
			categoryFileAttached = "true";
			
		try
		{
			this.setToolTipText(
					"<html> File name:" + file.getAbsolutePath() + "<br/>First year: " + file.getFirstYear() + "<br/>Last year: "
							+ file.getLastYear() + "<br/>Latitude: " + df.format(file.getFirstLatitudeDbl()) + "<br/>Longitude: "
							+ df.format(file.getFirstLongitudeDbl()) + "<br/>Category file attached: " + categoryFileAttached + "</html>");
		}
		catch (Exception e1)
		{
			try
			{
				this.setToolTipText("<html> File name:" + file.getAbsolutePath() + "<br/>First year: " + file.getFirstYear()
						+ "<br/>Last year: " + file.getLastYear() + "<br/>Latitude: " + file.getFirstLatitude() + "<br/>Longitude: "
						+ file.getFirstLongitude() + "<br/>Category file attached: " + categoryFileAttached + "</html>");
			}
			catch (Exception e2)
			{
				try
				{
					this.setToolTipText("<html> File name:" + file.getAbsolutePath() + "</html>");
				}
				catch (Exception e3)
				{
					// TODO
				}
			}
		}
		
		// Set text
		this.setText(file.getName());
		
		return this;
	}
}
