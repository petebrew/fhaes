/**************************************************************************************************
 * Fire History Analysis and Exploration System (FHAES), Copyright (C) 2015
 * 
 * Contributors: Cody Calhoun, Anthony Messerschmidt, Seth Westphal, Scott Goble, and Peter Brewer
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
package org.fhaes.FHRecorder.util;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.fhaes.preferences.App;
import org.fhaes.preferences.FHAESPreferences.PrefKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * CustomOptions Class. Allows for the customization of the data displayed. You can customize values displayed, colors, and names.
 * 
 * @author Seth Westphal
 */
public class CustomOptions {
	
	// private List<List<DataItem>> groups;
	private static final Logger log = LoggerFactory.getLogger(CustomOptions.class);
	
	private ArrayList<String> defaultGroup0Items = new ArrayList<String>();
	private ArrayList<String> defaultGroup1Items = new ArrayList<String>();
	private ArrayList<String> defaultGroup2Items = new ArrayList<String>();
	private ArrayList<String> defaultGroup3Items = new ArrayList<String>();
	private ArrayList<String> defaultGroup4Items = new ArrayList<String>();
	private ArrayList<String> defaultGroup5Items = new ArrayList<String>();
	private ArrayList<String> defaultGroup6Items = new ArrayList<String>();
	private List<Color> defaultColors;
	private List<String> defaultNames;
	
	/**
	 * Enumerator to differentiate the different types of events/sample statistics to represent.
	 */
	public enum DataItem {
		
		DORMANT_SEASON("Dormant season"),
		
		EARLY_EARLYWOOD("Early earlywood"),
		
		MIDDLE_EARLYWOOD("Middle earlywood"),
		
		LATE_EARLYWOOD("Late earlywood"),
		
		LATEWOOD("Latewood"),
		
		UNDETERMINED("Undetermined"),
		
		BLANK_YEARS("Blank years");
		
		// Declare local variables
		private final String text;
		
		DataItem(String text) {
			
			this.text = text;
		}
		
		@Override
		public String toString() {
			
			return text;
		}
		
		public static DataItem fromString(String s) {
			
			if (s == null || s.length() == 0)
				return null;
				
			for (DataItem di : DataItem.values())
			{
				if (di.toString().equals(s))
					return di;
			}
			
			log.error("Unable to find DataItem to match string: " + s);
			return null;
		}
	};
	
	/**
	 * Constructor for custom options. Sets all items to default values, colors, and settings.
	 */
	public CustomOptions() {
		
		defaultGroup0Items = new ArrayList<String>();
		
		// Temporarily removing Recording years
		// defaultGroup0Items.add(DataItem.RECORDING_YEARS.toString());
		defaultGroup0Items.add(DataItem.BLANK_YEARS.toString());
		
		defaultGroup1Items = new ArrayList<String>();
		defaultGroup1Items.add(DataItem.DORMANT_SEASON.toString());
		
		defaultGroup2Items = new ArrayList<String>();
		defaultGroup2Items.add(DataItem.EARLY_EARLYWOOD.toString());
		
		defaultGroup3Items = new ArrayList<String>();
		defaultGroup3Items.add(DataItem.MIDDLE_EARLYWOOD.toString());
		
		defaultGroup4Items = new ArrayList<String>();
		defaultGroup4Items.add(DataItem.LATE_EARLYWOOD.toString());
		
		defaultGroup5Items = new ArrayList<String>();
		defaultGroup5Items.add(DataItem.LATEWOOD.toString());
		
		defaultGroup6Items = new ArrayList<String>();
		defaultGroup6Items.add(DataItem.UNDETERMINED.toString());
		
		defaultNames = Arrays.asList(DataItem.DORMANT_SEASON.toString(), DataItem.EARLY_EARLYWOOD.toString(),
				DataItem.MIDDLE_EARLYWOOD.toString(), DataItem.LATE_EARLYWOOD.toString(), DataItem.LATEWOOD.toString(),
				DataItem.UNDETERMINED.toString());
		// Default LibreOffice chart colors
		defaultColors = Arrays.asList(new Color(0x004586), new Color(0xff420e), new Color(0xffd320), new Color(0x579d1c),
				new Color(0x7e0021), new Color(0x83caff));
	}
	
	/**
	 * Gets the items contained at the specified location. setGroupName
	 * 
	 * @param n the index of the group to get.
	 * @return the list of items contained in the group with index n.
	 */
	public List<DataItem> getDataItems(int n) {
		
		ArrayList<String> stringItems = null;
		switch (n)
		{
			case 0:
				stringItems = App.prefs.getArrayListPref(PrefKey.COLORBAR_ITEMS_GROUP0, defaultGroup0Items);
				break;
			case 1:
				stringItems = App.prefs.getArrayListPref(PrefKey.COLORBAR_ITEMS_GROUP1, defaultGroup1Items);
				break;
			case 2:
				stringItems = App.prefs.getArrayListPref(PrefKey.COLORBAR_ITEMS_GROUP2, defaultGroup2Items);
				break;
			case 3:
				stringItems = App.prefs.getArrayListPref(PrefKey.COLORBAR_ITEMS_GROUP3, defaultGroup3Items);
				break;
			case 4:
				stringItems = App.prefs.getArrayListPref(PrefKey.COLORBAR_ITEMS_GROUP4, defaultGroup4Items);
				break;
			case 5:
				stringItems = App.prefs.getArrayListPref(PrefKey.COLORBAR_ITEMS_GROUP5, defaultGroup5Items);
				break;
			case 6:
				stringItems = App.prefs.getArrayListPref(PrefKey.COLORBAR_ITEMS_GROUP6, defaultGroup6Items);
				break;
		}
		
		ArrayList<DataItem> dataItems = new ArrayList<DataItem>();
		
		for (String v : stringItems)
		{
			DataItem di = DataItem.fromString(v);
			if (di != null)
			{
				dataItems.add(di);
			}
			else
			{
				log.error("Unknown DataItem found in preferences.  Ignoring.");
			}
		}
		
		return dataItems;
	}
	
	/**
	 * Sets the group at n so it contains the specified objects.
	 * 
	 * @param n the index of the of the group to update.
	 * @param items the array of objects to put in the group.
	 */
	public void setGroupDataItems(int n, Object[] items) {
		
		ArrayList<String> list = new ArrayList<String>(items.length);
		for (Object item : items)
		{
			if (item == null)
				continue;
			list.add(item.toString());
		}
		
		switch (n)
		{
			case 0:
				App.prefs.setArrayListPref(PrefKey.COLORBAR_ITEMS_GROUP0, list);
			case 1:
				App.prefs.setArrayListPref(PrefKey.COLORBAR_ITEMS_GROUP1, list);
			case 2:
				App.prefs.setArrayListPref(PrefKey.COLORBAR_ITEMS_GROUP2, list);
			case 3:
				App.prefs.setArrayListPref(PrefKey.COLORBAR_ITEMS_GROUP3, list);
			case 4:
				App.prefs.setArrayListPref(PrefKey.COLORBAR_ITEMS_GROUP4, list);
			case 5:
				App.prefs.setArrayListPref(PrefKey.COLORBAR_ITEMS_GROUP5, list);
			case 6:
				App.prefs.setArrayListPref(PrefKey.COLORBAR_ITEMS_GROUP6, list);
			default:
				return;
		}
		
	}
	
	/**
	 * Gets the name of the specified index.
	 * 
	 * @param n index of the group to get the name of.
	 * @return the name at index n.
	 */
	
	public String getGroupName(int n) {
		
		switch (n)
		{
			case 1:
				return App.prefs.getPref(PrefKey.COLORBAR_GROUPNAME_1, defaultNames.get(0));
			case 2:
				return App.prefs.getPref(PrefKey.COLORBAR_GROUPNAME_2, defaultNames.get(1));
			case 3:
				return App.prefs.getPref(PrefKey.COLORBAR_GROUPNAME_3, defaultNames.get(2));
			case 4:
				return App.prefs.getPref(PrefKey.COLORBAR_GROUPNAME_4, defaultNames.get(3));
			case 5:
				return App.prefs.getPref(PrefKey.COLORBAR_GROUPNAME_5, defaultNames.get(4));
			case 6:
				return App.prefs.getPref(PrefKey.COLORBAR_GROUPNAME_6, defaultNames.get(5));
			default:
				return null;
		}
		
	}
	
	/**
	 * Sets the name for the group with the specified index.
	 * 
	 * @param n the index of the group to change the name of.
	 * @param name the name to set the group's name to.
	 */
	
	public void setGroupName(int n, String name) {
		
		switch (n)
		{
			case 1:
				App.prefs.setPref(PrefKey.COLORBAR_GROUPNAME_1, name);
			case 2:
				App.prefs.setPref(PrefKey.COLORBAR_GROUPNAME_2, name);
			case 3:
				App.prefs.setPref(PrefKey.COLORBAR_GROUPNAME_3, name);
			case 4:
				App.prefs.setPref(PrefKey.COLORBAR_GROUPNAME_4, name);
			case 5:
				App.prefs.setPref(PrefKey.COLORBAR_GROUPNAME_5, name);
			case 6:
				App.prefs.setPref(PrefKey.COLORBAR_GROUPNAME_6, name);
			default:
				return;
		}
	}
	
	/**
	 * Gets the color of the group with the specified index.
	 * 
	 * @param n the index of the group to get the color of.
	 * @return the color of the specified group.
	 */
	public Color getGroupColor(int n) {
		
		switch (n)
		{
			case 1:
				return App.prefs.getColorPref(PrefKey.COLORBAR_COLOR_1, defaultColors.get(0));
			case 2:
				return App.prefs.getColorPref(PrefKey.COLORBAR_COLOR_2, defaultColors.get(1));
			case 3:
				return App.prefs.getColorPref(PrefKey.COLORBAR_COLOR_3, defaultColors.get(2));
			case 4:
				return App.prefs.getColorPref(PrefKey.COLORBAR_COLOR_4, defaultColors.get(3));
			case 5:
				return App.prefs.getColorPref(PrefKey.COLORBAR_COLOR_5, defaultColors.get(4));
			case 6:
				return App.prefs.getColorPref(PrefKey.COLORBAR_COLOR_6, defaultColors.get(5));
			default:
				return null;
		}
	}
	
	/**
	 * Sets the color of the group with the specified index.
	 * 
	 * @param n the index of the group to change the color of.
	 * @param color the color to set the group's color to.
	 */
	public void setGroupColor(int n, Color color) {
		
		switch (n)
		{
			case 1:
				App.prefs.setColorPref(PrefKey.COLORBAR_COLOR_1, color);
			case 2:
				App.prefs.setColorPref(PrefKey.COLORBAR_COLOR_2, color);
			case 3:
				App.prefs.setColorPref(PrefKey.COLORBAR_COLOR_3, color);
			case 4:
				App.prefs.setColorPref(PrefKey.COLORBAR_COLOR_4, color);
			case 5:
				App.prefs.setColorPref(PrefKey.COLORBAR_COLOR_5, color);
			case 6:
				App.prefs.setColorPref(PrefKey.COLORBAR_COLOR_6, color);
			default:
				return;
		}
	}
	
	/**
	 * Reset options to default values
	 */
	public void setDefaultOptions() {
		
		App.prefs.setPref(PrefKey.COLORBAR_GROUPNAME_1, defaultNames.get(0));
		App.prefs.setPref(PrefKey.COLORBAR_GROUPNAME_2, defaultNames.get(1));
		App.prefs.setPref(PrefKey.COLORBAR_GROUPNAME_3, defaultNames.get(2));
		App.prefs.setPref(PrefKey.COLORBAR_GROUPNAME_4, defaultNames.get(3));
		App.prefs.setPref(PrefKey.COLORBAR_GROUPNAME_5, defaultNames.get(4));
		App.prefs.setPref(PrefKey.COLORBAR_GROUPNAME_6, defaultNames.get(5));
		App.prefs.setColorPref(PrefKey.COLORBAR_COLOR_1, defaultColors.get(0));
		App.prefs.setColorPref(PrefKey.COLORBAR_COLOR_2, defaultColors.get(1));
		App.prefs.setColorPref(PrefKey.COLORBAR_COLOR_3, defaultColors.get(2));
		App.prefs.setColorPref(PrefKey.COLORBAR_COLOR_4, defaultColors.get(3));
		App.prefs.setColorPref(PrefKey.COLORBAR_COLOR_5, defaultColors.get(4));
		App.prefs.setColorPref(PrefKey.COLORBAR_COLOR_6, defaultColors.get(5));
		App.prefs.setArrayListPref(PrefKey.COLORBAR_ITEMS_GROUP0, defaultGroup0Items);
		App.prefs.setArrayListPref(PrefKey.COLORBAR_ITEMS_GROUP1, defaultGroup1Items);
		App.prefs.setArrayListPref(PrefKey.COLORBAR_ITEMS_GROUP2, defaultGroup2Items);
		App.prefs.setArrayListPref(PrefKey.COLORBAR_ITEMS_GROUP3, defaultGroup3Items);
		App.prefs.setArrayListPref(PrefKey.COLORBAR_ITEMS_GROUP4, defaultGroup4Items);
		App.prefs.setArrayListPref(PrefKey.COLORBAR_ITEMS_GROUP5, defaultGroup5Items);
		App.prefs.setArrayListPref(PrefKey.COLORBAR_ITEMS_GROUP6, defaultGroup6Items);
		
	}
}
