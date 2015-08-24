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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;

import javax.swing.AbstractListModel;
import javax.swing.JList;

import org.fhaes.enums.EventTypeToProcess;
import org.fhaes.preferences.App;
import org.fhaes.preferences.FHAESPreferences.PrefKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.emory.mathcs.backport.java.util.Collections;

/**
 * FileListModel Class.
 */
@SuppressWarnings("rawtypes")
public class FileListModel extends AbstractListModel {
	
	private static final long serialVersionUID = 1L;
	
	// Declare logger
	private static final Logger log = LoggerFactory.getLogger(FileListModel.class);
	
	// Declare local variables
	private ArrayList<FHFile> list;
	private boolean listenersEnabled = true;
	
	/**
	 * Creates a new FileListModel.
	 */
	public FileListModel() {
		
		this.list = new ArrayList<FHFile>();
	}
	
	/**
	 * Creates a new FileListModel based on the input list.
	 * 
	 * @param list
	 */
	public FileListModel(ArrayList<FHFile> list) {
		
		if (list == null)
		{
			list = new ArrayList<FHFile>();
		}
		else
		{
			this.list = list;
		}
	}
	
	/**
	 * TODO
	 * 
	 * @return
	 */
	public boolean getListenersEnabled() {
		
		return listenersEnabled;
	}
	
	/**
	 * TODO
	 * 
	 * @param enabled
	 */
	public void setListenersEnabled(boolean enabled) {
		
		listenersEnabled = enabled;
	}
	
	/**
	 * TODO
	 */
	@Override
	public void fireContentsChanged(Object source, int index0, int index1) {
		
		if (getListenersEnabled())
		{
			super.fireContentsChanged(source, index0, index1);
		}
	}
	
	/**
	 * TODO
	 */
	@Override
	public void fireIntervalAdded(Object source, int index0, int index1) {
		
		if (getListenersEnabled())
		{
			super.fireIntervalAdded(source, index0, index1);
		}
	}
	
	/**
	 * TODO
	 */
	@Override
	public void fireIntervalRemoved(Object source, int index0, int index1) {
		
		if (getListenersEnabled())
		{
			super.fireIntervalAdded(source, index0, index1);
		}
	}
	
	/**
	 * Get a list of *all* files from this model, regardless of whether they are valid or not.
	 * 
	 * @return
	 */
	public ArrayList<FHFile> getCompleteFileList() {
		
		return list;
	}

	/**
	 * Get an ArrayList of all the *valid* FHFiles in this model.
	 * 
	 * @return
	 */
	public ArrayList<FHFile> getValidFileList() {
		
		ArrayList<FHFile> tempList = new ArrayList<FHFile>();
		
		for (FHFile f : list)
		{
			if (f.isValidFHXFile())
				tempList.add(f);
		}
		
		return tempList;
	}
	
	/**
	 * Get the valid selected files from a JList of FHFiles regardless of whether they have events in on or not.
	 * 
	 * @param list of FHFiles
	 * @return
	 */
	@SuppressWarnings("deprecation")
	public static ArrayList<FHFile> getValidSelectedFileList(JList list) {
		
		Object[] selected = list.getSelectedValues();
		ArrayList<FHFile> tempList = new ArrayList<FHFile>();
		
		for (Object obj : selected)
		{
			FHFile f = null;
			try
			{
				f = (FHFile) obj;
			}
			catch (Exception e)
			{
				log.error("getValidSelectedFileList() called with invalid JList");
				e.printStackTrace();
				return null;
			}
			
			if (f.isValidFHXFile())
				tempList.add(f);
		}
		
		return tempList;
	}
	
	/**
	 * Get the valid selected files from a JList of FHFiles but only include those with events as specified by the current event type.
	 * 
	 * @param list
	 * @return
	 */
	@SuppressWarnings("deprecation")
	public static ArrayList<FHFile> getValidSelectedFileListWithEvents(JList list) {
		
		Object[] selected = list.getSelectedValues();
		ArrayList<FHFile> tempList = new ArrayList<FHFile>();
		EventTypeToProcess eventType = App.prefs.getEventTypePref(PrefKey.EVENT_TYPE_TO_PROCESS, EventTypeToProcess.FIRE_EVENT);
		
		for (Object obj : selected)
		{
			FHFile f = null;
			try
			{
				f = (FHFile) obj;
			}
			catch (Exception e)
			{
				log.error("getValidSelectedFileListWithEvents() called with invalid JList");
				e.printStackTrace();
				return null;
			}
			
			if (eventType.equals(EventTypeToProcess.FIRE_EVENT))
			{
				if (f.isValidFHXFile() && f.hasFireEvents())
					tempList.add(f);
			}
			else if (eventType.equals(EventTypeToProcess.INJURY_EVENT))
			{
				if (f.isValidFHXFile() && f.hasInjuryEvents())
					tempList.add(f);
			}
			else if (eventType.equals(EventTypeToProcess.FIRE_AND_INJURY_EVENT))
			{
				if (f.isValidFHXFile() && (f.hasInjuryEvents() || f.hasInjuryEvents()))
					tempList.add(f);
			}
			else
			{
				log.error("Unsupported event type");
			}
		}
		
		log.debug("getValidSelectedFileListWithEvents() returning a list of " + tempList.size() + " items");
		return tempList;
	}
	
	/**
	 * Get an ArrayList of all the *valid* FHFiles with fire or injury events depending on current preferences.
	 * 
	 * @return
	 */
	public ArrayList<FHFile> getValidFileListWithEvents() {
		
		ArrayList<FHFile> tempList = new ArrayList<FHFile>();
		
		EventTypeToProcess eventType = App.prefs.getEventTypePref(PrefKey.EVENT_TYPE_TO_PROCESS, EventTypeToProcess.FIRE_EVENT);
		
		if (eventType.equals(EventTypeToProcess.FIRE_EVENT))
		{
			for (FHFile f : list)
			{
				if (f.isValidFHXFile() && f.hasFireEvents())
					tempList.add(f);
			}
		}
		else if (eventType.equals(EventTypeToProcess.INJURY_EVENT))
		{
			for (FHFile f : list)
			{
				if (f.isValidFHXFile() && f.hasInjuryEvents())
					tempList.add(f);
			}
		}
		else if (eventType.equals(EventTypeToProcess.FIRE_AND_INJURY_EVENT))
		{
			for (FHFile f : list)
			{
				if (f.isValidFHXFile() && (f.hasFireEvents() || f.hasInjuryEvents()))
					tempList.add(f);
			}
		}
		else
		{
			log.error("Unsupported event type");
		}
		
		log.debug("getValidFileListWithEvents() returning a list of " + tempList.size() + " items");
		
		return tempList;
	}
	
	/**
	 * TODO
	 * 
	 * @param list
	 */
	public void addAllElements(Collection<FHFile> list) {
		
		listenersEnabled = false;
		
		for (FHFile f : list)
		{
			this.addElement(f);
		}
		
		listenersEnabled = true;
		fireContentsChanged(this, 0, this.getSize());
	}
	
	/**
	 * TODO
	 */
	@Override
	public FHFile getElementAt(int arg0) {
		
		try
		{
			return list.get(arg0);
		}
		catch (Exception ex)
		{
			return null;
		}
	}
	
	/**
	 * TODO
	 * 
	 * @param f
	 */
	public void addElement(FHFile f) {
		
		list.add(f);
		fireContentsChanged(this, 0, this.getSize());
	}
	
	/**
	 * TODO
	 * 
	 * @param index
	 * @param f
	 */
	public void addElementAt(int index, FHFile f) {
		
		list.add(index, f);
	}
	
	/**
	 * TODO
	 */
	@Override
	public int getSize() {
		
		if (list == null)
			return 0;
			
		return list.size();
	}
	
	/**
	 * TODO
	 * 
	 * @param f
	 */
	public void removeElement(FHFile f) {
		
		if (list.remove(f))
		{
			fireContentsChanged(this, 0, this.getSize());
		}
	}
	
	/**
	 * TODO
	 */
	public void clear() {
		
		list = new ArrayList<FHFile>();
		fireContentsChanged(this, 0, this.getSize());
	}
	
	/**
	 * TODO
	 * 
	 * @param index
	 */
	public void removeElementAt(int index) {
		
		try
		{
			list.remove(index);
		}
		catch (IndexOutOfBoundsException e)
		{
			return;
		}
		
		fireContentsChanged(this, 0, this.getSize());
		
	}
	
	/**
	 * TODO
	 * 
	 * @param list
	 */
	public void setFileList(ArrayList<FHFile> list) {
		
		this.list = list;
		fireContentsChanged(this, 0, list.size());
		
	}
	
	/**
	 * TODO
	 * 
	 * @param comp
	 */
	@SuppressWarnings("unchecked")
	public void sortAscending(Comparator<FHFile> comp) {
		
		setListenersEnabled(false);
		log.debug("Sorting file list ascending");
		
		ArrayList<FHFile> tempList = (ArrayList<FHFile>) list.clone();
		
		Collections.sort(tempList, comp);
		
		setFileList(tempList);
		setListenersEnabled(true);
		
	}
	
	/**
	 * TODO
	 * 
	 * @param comp
	 */
	@SuppressWarnings("unchecked")
	public void sortDescending(Comparator<FHFile> comp) {
		
		setListenersEnabled(false);
		log.debug("Sorting file list descending");
		ArrayList<FHFile> tempList = (ArrayList<FHFile>) list.clone();
		
		Collections.sort(tempList, comp);
		Collections.reverse(tempList);
		
		setFileList(tempList);
		setListenersEnabled(true);
		
	}
}
