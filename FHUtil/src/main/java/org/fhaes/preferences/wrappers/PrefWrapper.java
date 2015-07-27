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
package org.fhaes.preferences.wrappers;

import org.fhaes.enums.AnalysisLabelType;
import org.fhaes.enums.AnalysisType;
import org.fhaes.enums.EventTypeToProcess;
import org.fhaes.enums.FireFilterType;
import org.fhaes.enums.LabelOrientation;
import org.fhaes.enums.LineStyle;
import org.fhaes.enums.NoDataLabel;
import org.fhaes.enums.ResamplingType;
import org.fhaes.preferences.App;
import org.fhaes.preferences.FHAESPreferences;
import org.fhaes.preferences.FHAESPreferences.PrefKey;

/**
 * PrefWrapper<OBJTYPE> Abstract Class. A wrapper around a preference string Defaults to autocommit (you set a preference value, it is saved
 * automatically).
 * 
 * This class is not suitable for generic-ization because compile-time checks are useless; the underlying prefs class doesn't use generics.
 * Thus, OBJTYPE is only used to type-check setValue and getValue (and remove ugly casting)
 * 
 * @author lucasm
 */
public abstract class PrefWrapper<OBJTYPE> {
	
	protected PrefKey prefName;
	private Object prefValue;
	private Object defaultValue;
	private Class<?> baseClass;
	private boolean valueModified;
	private final boolean autocommit = true;
	
	/**
	 * Create a new wrapper for this preference name, wrapping the specified type.
	 * 
	 * @param prefName
	 * @param defaultValue
	 * @param baseClass
	 */
	public PrefWrapper(PrefKey prefName, Object defaultValue, Class<?> baseClass) {
		
		this.setPrefName(prefName);
		this.baseClass = baseClass;
		this.defaultValue = defaultValue;
		
		valueModified = false;
		load();
	}
	
	/**
	 * Shortcut for creating a string-based pref.
	 * 
	 * @param prefName
	 * @param defaultValue
	 */
	public PrefWrapper(PrefKey prefName, Object defaultValue) {
		
		this(prefName, defaultValue, String.class);
	}
	
	/**
	 * Shortcut for creating a string-based pref with no default.
	 * 
	 * @param prefName
	 */
	public PrefWrapper(PrefKey prefName) {
		
		this(prefName, null, String.class);
	}
	
	/**
	 * TODO
	 * 
	 * @return
	 */
	public boolean isModified() {
		
		return valueModified;
	}
	
	/**
	 * Set the value of this preference.
	 * 
	 * @param value
	 */
	public void setValue(OBJTYPE value) {
		
		// same value? ignore it
		if (prefValue == value)
			return;
			
		// they equal the same thing? ignore it
		if (prefValue != null && prefValue.equals(value))
			return;
			
		valueModified = true;
		prefValue = value;
		
		if (autocommit)
			commit();
	}
	
	/**
	 * Get the value of the pref referenced by this wrapper.
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public OBJTYPE getValue() {
		
		return (OBJTYPE) prefValue;
	}
	
	/**
	 * Commit the value represented in this pref to prefs storage. Not useful to call if autocommit is on (which it is by default).
	 */
	public void commit() {
		
		if (!valueModified)
			return;
			
		if (prefValue == null)
		{
			FHAESPreferences.removePref(getPrefName());
			valueModified = false;
			return;
		}
		
		if (baseClass == String.class)
			App.prefs.setPref(getPrefName(), (String) prefValue);
		/*
		 * else if(baseClass == Font.class) App.prefs.setFontPref(getPrefName(), (Font) prefValue);
		 */
		else if (baseClass == Integer.class)
			App.prefs.setIntPref(getPrefName(), (Integer) prefValue);
		/*
		 * else if(baseClass == Dimension.class) App.prefs.setDimensionPref(getPrefName(), (Dimension) prefValue); else if(baseClass ==
		 * Color.class) App.prefs.setColorPref(getPrefName(), (Color) prefValue);
		 */
		else if (baseClass == Boolean.class)
			App.prefs.setBooleanPref(getPrefName(), (Boolean) prefValue);
		else if (baseClass == AnalysisType.class)
		{
			App.prefs.setAnalysisTypePref(getPrefName(), (AnalysisType) prefValue);
		}
		else if (baseClass == FireFilterType.class)
		{
			App.prefs.setFireFilterTypePref(getPrefName(), (FireFilterType) prefValue);
		}
		else if (baseClass == EventTypeToProcess.class)
		{
			App.prefs.setEventTypePref(getPrefName(), (EventTypeToProcess) prefValue);
		}
		else if (baseClass == AnalysisLabelType.class)
		{
			App.prefs.setAnalysisLabelTypePref(getPrefName(), (AnalysisLabelType) prefValue);
		}
		else if (baseClass == ResamplingType.class)
		{
			App.prefs.setResamplingTypePref(getPrefName(), (ResamplingType) prefValue);
		}
		else if (baseClass == Double.class)
		{
			App.prefs.setDoublePref(getPrefName(), (Double) prefValue);
		}
		else if (baseClass == NoDataLabel.class)
		{
			App.prefs.setNoDataLabelPref(getPrefName(), (NoDataLabel) prefValue);
		}
		else if (baseClass == LineStyle.class)
		{
			App.prefs.setLineStylePref(getPrefName(), (LineStyle) prefValue);
		}
		else if (baseClass == LabelOrientation.class)
		{
			App.prefs.setLabelOrientationPref(getPrefName(), (LabelOrientation) prefValue);
		}
		else
			throw new IllegalArgumentException("I don't know how to save a pref for type " + baseClass);
			
		valueModified = false;
	}
	
	/**
	 * TODO
	 */
	private void load() {
		
		if (baseClass == String.class)
			prefValue = App.prefs.getPref(getPrefName(), (String) defaultValue);
		/*
		 * else if(baseClass == Font.class) prefValue = App.prefs.getFontPref(getPrefName(), (Font) defaultValue);
		 */
		else if (baseClass == Integer.class)
			prefValue = App.prefs.getIntPref(getPrefName(), (Integer) defaultValue);
		/*
		 * else if(baseClass == Dimension.class) prefValue = App.prefs.getDimensionPref(getPrefName(), (Dimension) defaultValue); else
		 * if(baseClass == Color.class) prefValue = App.prefs.getColorPref(getPrefName(), (Color) defaultValue);
		 */
		else if (baseClass == Boolean.class)
			prefValue = App.prefs.getBooleanPref(getPrefName(), (Boolean) defaultValue);
		/*
		 * else if(baseClass == Double.class) prefValue = App.prefs.getDoublePref(getPrefName(), (Double) defaultValue);
		 */
		else if (baseClass == AnalysisType.class)
		{
			prefValue = App.prefs.getAnalysisTypePref(getPrefName(), (AnalysisType) defaultValue);
		}
		else if (baseClass == FireFilterType.class)
		{
			prefValue = App.prefs.getFireFilterTypePref(getPrefName(), (FireFilterType) defaultValue);
		}
		else if (baseClass == EventTypeToProcess.class)
		{
			prefValue = App.prefs.getEventTypePref(getPrefName(), (EventTypeToProcess) defaultValue);
		}
		else if (baseClass == AnalysisLabelType.class)
		{
			prefValue = App.prefs.getAnalysisLabelTypePref(getPrefName(), (AnalysisLabelType) defaultValue);
		}
		else if (baseClass == ResamplingType.class)
		{
			prefValue = App.prefs.getResamplingTypePref(getPrefName(), (ResamplingType) defaultValue);
		}
		else if (baseClass == Double.class)
		{
			prefValue = App.prefs.getDoublePref(getPrefName(), (Double) defaultValue);
		}
		else if (baseClass == NoDataLabel.class)
		{
			prefValue = App.prefs.getNoDataLabelPref(getPrefName(), (NoDataLabel) defaultValue);
		}
		else if (baseClass == LineStyle.class)
		{
			prefValue = App.prefs.getLineStylePref(getPrefName(), (LineStyle) defaultValue);
		}
		else if (baseClass == LabelOrientation.class)
		{
			prefValue = App.prefs.getLabelOrientationPref(getPrefName(), (LabelOrientation) defaultValue);
		}
		else
		{
			throw new IllegalArgumentException("I don't know how to load a pref for type " + baseClass);
		}
	}
	
	/**
	 * TODO
	 * 
	 * @param prefName
	 */
	public void setPrefName(PrefKey prefName) {
		
		this.prefName = prefName;
	}
	
	/**
	 * TODO
	 * 
	 * @return
	 */
	public PrefKey getPrefName() {
		
		return prefName;
	}
}
