/**************************************************************************************************
 * Fire History Analysis and Exploration System (FHAES), Copyright (C) 2015
 * 
 * Contributors: Alex Beatty, Clayton Bodendein, Kyle Hartmann, Scott Goble, and Peter Brewer
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
package org.fhaes.FHRecorder.controller;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import org.fhaes.FHRecorder.utility.CustomOptions;
import org.fhaes.FHRecorder.utility.YearSummary;
import org.fhaes.FHRecorder.view.FireHistoryRecorder;
import org.fhaes.enums.FeedbackDisplayProtocol;
import org.fhaes.enums.FeedbackMessageType;
import org.fhaes.exceptions.CompositeFileException;
import org.fhaes.filefilter.FHXFileFilter;
import org.fhaes.preferences.App;
import org.fhaes.preferences.FHAESPreferences.PrefKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * FileController Class. This file contains the FHX2 file management functions of the service layer.
 * 
 * @author Alex Beatty, Clayton Bodendein, Kyle Hartmann, Scott Goble
 */
public class FileController {
	
	public static final int CURRENT_YEAR = Calendar.getInstance().get(Calendar.YEAR);
	public static final int EARLIEST_ALLOWED_YEAR = -99999;
	
	public static final int MAX_VISIBLE_GRAPH_COLUMNS = 20;
	
	public static final int FHX2_MAX_FILE_NAME_LENGTH = 8;
	public static final int FHX2_MAX_SAMPLE_NAME_LENGTH = 8;
	public static final int FHX2_MAX_NUMBER_OF_SAMPLES = 254;
	public static final int FHX2_YEAR_LOWER_BOUNDARY = 1200;
	public static final int FHX2_YEAR_UPPER_BOUNDARY = 2020;
	
	private static final Logger log = LoggerFactory.getLogger(FileController.class);
	private static CustomOptions customOptions = new CustomOptions();
	
	public static FireHistoryRecorder thePrimaryWindow;
	public static String progName = "Fire History Recorder";
	public static String filePath = null;
	public static String fileName = null;
	
	private static Boolean isChangedSinceLastSave = false;
	private static Boolean isChangedSinceOpened = false;
	private static Boolean isCorrupted = false;
	private static Boolean isNewFile = true;
	private static Boolean lastYearDefinedInFile = true;
	private static boolean overrideCompositeWarnings = false;
	
	/**
	 * Returns a value indicating whether or not the old FHX2 file requirements are to be enforced.
	 * 
	 * @return true if yes, false if no
	 */
	public static Boolean isEnforcingOldReqs() {
		
		return App.prefs.getBooleanPref(PrefKey.ENFORCE_FHX2_RESTRICTIONS, false);
	}
	
	/**
	 * Gets the value of isChangedSinceOpened.
	 * 
	 * @return true if the data has been changed since the file was opened, otherwise false
	 */
	public static Boolean isChangedSinceOpened() {
		
		return isChangedSinceOpened;
	}
	
	/**
	 * Sets the value of isChangedSinceOpened based on the input parameter.
	 * 
	 * @param b
	 */
	public static void setIsChangedSinceOpened(Boolean b) {
		
		isChangedSinceOpened = b;
	}
	
	/**
	 * Gets the value of isChangedSinceLastSave.
	 * 
	 * @return true if the file has been modified since the last save, otherwise false
	 */
	public static Boolean isChangedSinceLastSave() {
		
		return isChangedSinceLastSave;
	}
	
	/**
	 * Sets the value of isChangedSinceLastSave based on the input parameter.
	 * 
	 * @param b
	 */
	public static void setIsChangedSinceLastSave(Boolean b) {
		
		isChangedSinceLastSave = b;
		if (b)
			isChangedSinceOpened = true;
	}
	
	/**
	 * Returns the value indicating whether or not an error has been found while parsing the file from the disk.
	 * 
	 * @return true if errors were detected, false otherwise
	 */
	public static Boolean isFileCorrupted() {
		
		return isCorrupted;
	}
	
	public static Boolean isFileNew() {
		
		return isNewFile;
	}
	
	/**
	 * Updates the value indicating whether or not an error has been found while parsing the file from the disk.
	 * 
	 * @param b
	 */
	public static void setCorruptedState(Boolean b) {
		
		isCorrupted = b;
	}
	
	/**
	 * Gets the path of the file currently loaded into FireHistoryRecorder.
	 * 
	 * @return null if a new file was created, file path if a file was loaded
	 */
	public static File getSavedFile() {
		
		if (filePath == null)
			return null;
		File f = new File(filePath);
		return f;
	}
	
	/**
	 * Sets the title of the form based on the input parameter.
	 * 
	 * @param name
	 */
	public static void setTitleName(String name) {
		
		thePrimaryWindow.setTitle(name);
	}
	
	/**
	 * Returns a value indicating the special case in which the last year of the data set was not originally defined in the FHX2 file.
	 * 
	 * @return true if it was originally defined, false otherwise
	 */
	public static Boolean wasLastYearDefinedInFile() {
		
		return lastYearDefinedInFile;
	}
	
	/**
	 * Sets lastYearDefinedInFile according to the input parameter.
	 * 
	 * @param inValue
	 */
	protected static void setLastYearDefinedInFile(Boolean inValue) {
		
		lastYearDefinedInFile = inValue;
	}
	
	protected static void setIsNewFile(Boolean b) {
		
		isNewFile = b;
	}
	
	/**
	 * Calls the "showInput" method in GUI_FireHistoryRecorder.
	 */
	public static void showInput() {
		
		thePrimaryWindow.showInput();
	}
	
	/**
	 * Calls the "showInfo" method in GUI_FireHistoryRecorder.
	 */
	public static void showInfo() {
		
		thePrimaryWindow.showInfo();
	}
	
	/**
	 * Calls the "showComments" method in GUI_FireHistoryRecorder.
	 */
	public static void showComments() {
		
		thePrimaryWindow.showComments();
	}
	
	/**
	 * Calls the "redrawSampleInputPanel" method in GUI_FireHistoryRecorder.
	 */
	public static void redrawSampleInputPanel() {
		
		thePrimaryWindow.redrawSampleInputPanel();
	}
	
	/**
	 * Calls the "redrawEventPanel" method in GUI_FireHistoryRecorder.
	 */
	public static void redrawEventPanel() {
		
		thePrimaryWindow.redrawEventPanel();
	}
	
	/**
	 * Calls the "enableCloseMenu" method in GUI_FireHistoryRecorder.
	 */
	public static void enableCloseMenu() {
		
		thePrimaryWindow.enableDependentMenuItems();
	}
	
	/**
	 * Calls the "disableCloseMenu" method in GUI_FireHistoryRecorder.
	 */
	public static void disableCloseMenu() {
		
		thePrimaryWindow.disableDependentMenuItems();
	}
	
	/**
	 * Redraws the necessary components to reflect updates that have been performed on the file since the last redraw.
	 */
	public static void displayUpdatedFile() {
		
		SampleController.setSelectedSampleIndex(0);
		thePrimaryWindow.generateScreens(IOController.getFile());
		isChangedSinceLastSave = false;
		showInput();
		thePrimaryWindow.selectFirstSample();
	}
	
	/**
	 * Issues a warning if the number of samples exceeds the original capabilities of FHX2
	 */
	public static void checkIfNumSamplesExceedsFHX2Reqs() {
		
		if (IOController.getFile().getRequiredPart().getNumSamples() > FileController.FHX2_MAX_NUMBER_OF_SAMPLES
				&& FileController.isEnforcingOldReqs())
		{
			FireHistoryRecorder.getFeedbackMessagePanel().updateFeedbackMessage(FeedbackMessageType.WARNING,
					FeedbackDisplayProtocol.PROGRAMATICALLY_HIDE,
					"The current number of samples (" + IOController.getFile().getRequiredPart().getNumSamples()
							+ ") exceeds the capabilities of the original FHX2 software (maximum of 254).");
		}
		else
		{
			FireHistoryRecorder.getFeedbackMessagePanel().clearFeedbackMessage();
		}
	}
	
	/**
	 * Issues a warning if the data-set first year is below the minimum supported year of FHX2
	 */
	public static void checkIfYearLowerBoundaryIsWithinFHX2Reqs() {
		
		if (IOController.getFile().getRequiredPart().getDataSetFirstYear() < FileController.FHX2_YEAR_LOWER_BOUNDARY
				&& FileController.isEnforcingOldReqs())
		{
			FireHistoryRecorder.getFeedbackMessagePanel().updateFeedbackMessage(FeedbackMessageType.WARNING,
					FeedbackDisplayProtocol.PROGRAMATICALLY_HIDE,
					"The earliest year in the dataset (" + IOController.getFile().getRequiredPart().getDataSetFirstYear()
							+ ") is below the minimum supported year of FHX2 (1200).");
		}
		else
		{
			FireHistoryRecorder.getFeedbackMessagePanel().clearFeedbackMessage();
		}
	}
	
	/**
	 * Issues a warning if the data-set last year is above the minimum supported year of FHX2
	 */
	public static void checkIfYearUpperBoundaryIsWithinFHX2Reqs() {
		
		if (IOController.getFile().getRequiredPart().getDataSetLastYear() > FileController.FHX2_YEAR_UPPER_BOUNDARY
				&& FileController.isEnforcingOldReqs())
		{
			FireHistoryRecorder.getFeedbackMessagePanel().updateFeedbackMessage(FeedbackMessageType.WARNING,
					FeedbackDisplayProtocol.PROGRAMATICALLY_HIDE,
					"The latest year in the dataset (" + IOController.getFile().getRequiredPart().getDataSetLastYear()
							+ ") is above the maximum supported year of FHX2 (2020).");
		}
		else
		{
			FireHistoryRecorder.getFeedbackMessagePanel().clearFeedbackMessage();
		}
	}
	
	/**
	 * Handles the GUI-side setup of a new FHX2 file.
	 */
	public static void newFile() {
		
		IOController.createNewFile();
		setTitleName(progName);
		SampleController.setSelectedSampleIndex(-1);
		thePrimaryWindow.generateScreens(IOController.getFile());
		filePath = null;
		fileName = null;
		isChangedSinceOpened = false;
		isChangedSinceLastSave = false;
		setCorruptedState(false);
		setIsNewFile(true);
		enableCloseMenu();
		showInput();
	}
	
	/**
	 * Loads an FHX file from the disk and stores the data in theFHX2File.
	 * 
	 * @param theImportedFile
	 * @throws CompositeFileException
	 */
	public static void importFile(File theImportedFile) throws CompositeFileException {
		
		filePath = theImportedFile.getPath();
		fileName = theImportedFile.getName();
		setCorruptedState(false);
		setIsNewFile(false);
		
		try
		{
			BufferedReader br = new BufferedReader(new FileReader(theImportedFile));
			IOController.readFileFromBufferedReader(br);
			br.close();
		}
		catch (FileNotFoundException ex)
		{
			log.error("FileNotFoundException in importFile method of FileController");
			log.error(ex.getMessage());
		}
		catch (IOException ex)
		{
			log.error("IOException in importFile method of FileController");
			log.error(ex.getMessage());
		}
		
		SampleController.setSelectedSampleIndex(0);
		
		enableCloseMenu();
		displayUpdatedFile();
		checkIfNumSamplesExceedsFHX2Reqs();
		
		lastYearDefinedInFile = true;
		isChangedSinceOpened = false;
		isChangedSinceLastSave = false;
	}
	
	/**
	 * Determines the actions of the loading dialog when importing new files.
	 * 
	 * @throws CompositeFileException
	 */
	public static void loadDialog() throws CompositeFileException {
		
		String lastVisitedFolder = App.prefs.getPref(PrefKey.PREF_LAST_READ_FOLDER, null);
		
		JFileChooser chooser = new JFileChooser(lastVisitedFolder);
		chooser.setFileFilter(new FHXFileFilter());
		chooser.setAcceptAllFileFilterUsed(false);
		int returnValue = chooser.showOpenDialog(thePrimaryWindow);
		
		if (returnValue == JFileChooser.APPROVE_OPTION)
		{
			File selectedFile = chooser.getSelectedFile();
			App.prefs.setPref(PrefKey.PREF_LAST_READ_FOLDER, selectedFile.getAbsolutePath());
			String fileExt = selectedFile.getName().substring(selectedFile.getName().indexOf(".") + 1);
			
			if (fileExt.equals("fhx") || fileExt.equals("FHX"))
			{
				importFile(selectedFile);
			}
			else
			{
				JOptionPane.showMessageDialog(null, "The file opened is not compatible with this program. Please use a .fhx file",
						"Wrong File Type ", JOptionPane.INFORMATION_MESSAGE);
			}
		}
	}
	
	/**
	 * Checks if a path for the current theFHX2File exists and calls doSaveFileFunctionality(). If the path does not exist the saveAs()
	 * method is called to create one.
	 */
	public static void save() {
		
		if (filePath == null)
			saveAs();
		else
			doSaveFileFunctionality();
	}
	
	/**
	 * Saves the data stored in theFHX2File as a FHX file at filePath.
	 */
	private static void doSaveFileFunctionality() {
		
		File saveFile = new File(filePath);
		try
		{
			if (!saveFile.exists())
				saveFile.createNewFile();
				
			FileWriter fw = new FileWriter(saveFile.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);
			
			IOController.writeFileToDisk(bw);
			bw.close();
			
			setTitleName(progName + " - " + fileName);
			isChangedSinceLastSave = false;
			isChangedSinceOpened = true;
		}
		catch (IOException e)
		{
			log.error("Error saving FHX file");
			e.printStackTrace();
		}
	}
	
	/**
	 * Opens a JFileChooser for the user to set the filepath to save theFHX2File. to, then calls doSaveFileFunctionality()
	 */
	public static void saveAs() {
		
		String lastVisitedFolder = App.prefs.getPref(PrefKey.PREF_LAST_EXPORT_FOLDER, null);
		JFileChooser chooser = new JFileChooser(lastVisitedFolder);
		chooser.setFileFilter(new FHXFileFilter());
		chooser.setAcceptAllFileFilterUsed(false);
		int returnValue = chooser.showSaveDialog(thePrimaryWindow);
		if (returnValue == JFileChooser.APPROVE_OPTION)
		{
			File selectedFile = chooser.getSelectedFile();
			filePath = selectedFile.getPath();
			fileName = selectedFile.getName();
			if (selectedFile.getName().length() > FHX2_MAX_FILE_NAME_LENGTH && isEnforcingOldReqs() == true)
			{
				FireHistoryRecorder.getFeedbackMessagePanel().updateFeedbackMessage(FeedbackMessageType.WARNING,
						FeedbackDisplayProtocol.PROGRAMATICALLY_HIDE, "File name is too long for the original FHX program requirements.");
						
				filePath = filePath.substring(0, filePath.length() - fileName.length());
				fileName = fileName.substring(0, FHX2_MAX_FILE_NAME_LENGTH);
				filePath = filePath + fileName + ".fhx";
			}
			
			App.prefs.setPref(PrefKey.PREF_LAST_EXPORT_FOLDER, filePath);
			
			if (!filePath.toLowerCase().endsWith(".fhx"))
			{
				selectedFile = new File(filePath + ".fhx");
				filePath = selectedFile.getPath();
				fileName = selectedFile.getName();
				if (selectedFile.getName().length() > FHX2_MAX_FILE_NAME_LENGTH && isEnforcingOldReqs() == true)
				{
					FireHistoryRecorder.getFeedbackMessagePanel().updateFeedbackMessage(FeedbackMessageType.WARNING,
							FeedbackDisplayProtocol.PROGRAMATICALLY_HIDE,
							"File name is too long for the original FHX program requirements.");
							
					filePath = filePath.substring(0, filePath.length() - fileName.length());
					fileName = fileName.substring(0, FHX2_MAX_FILE_NAME_LENGTH);
					filePath = filePath + fileName + ".fhx";
				}
			}
			doSaveFileFunctionality();
		}
	}
	
	/**
	 * TODO
	 * 
	 * @return
	 */
	public static CustomOptions getCustomOptions() {
		
		return customOptions;
	}
	
	/**
	 * Overwrites the existing option set with the input option set.
	 * 
	 * @param inCustomOptions
	 */
	public static void setCustomOptions(CustomOptions inCustomOptions) {
		
		customOptions = inCustomOptions;
	}
	
	/**
	 * TODO
	 * 
	 * @return
	 */
	public static List<YearSummary> getYearSummaryList() {
		
		int firstYear = IOController.getFile().getRequiredPart().getDataSetFirstYear();
		int lastYear = IOController.getFile().getRequiredPart().getDataSetLastYear();
		List<YearSummary> result = new ArrayList<YearSummary>(lastYear - firstYear);
		for (int i = firstYear + 1; i < lastYear; i++)
			if (i != 0)
				result.add(new YearSummary(IOController.getFile().getRequiredPart(), i));
		return result;
	}
	
	/**
	 * Returns the status of the OverrideCompositeWarnings flag.
	 * 
	 * @return
	 */
	public static boolean isOverrideCompositeWarnings() {
		
		return overrideCompositeWarnings;
	}
	
	/**
	 * Updates the status of the OverrideCompositeWarnings flag.
	 * 
	 * @param overrideCompositeWarnings
	 */
	public static void setOverrideCompositeWarnings(boolean overrideCompositeWarnings) {
		
		FileController.overrideCompositeWarnings = overrideCompositeWarnings;
	}
}
