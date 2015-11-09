/**************************************************************************************************
 * Fire History Analysis and Exploration System (FHAES), Copyright (C) 2015
 * 
 * Contributors: Elena Velasquez and Peter Brewer
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
package org.fhaes.fhfilereader;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.TimeZone;

import org.apache.commons.io.FilenameUtils;
import org.codehaus.plexus.util.FileUtils;
import org.fhaes.enums.AnalysisLabelType;
import org.fhaes.enums.EventTypeToProcess;
import org.fhaes.fhfilechecker.FHFileChecker;
import org.fhaes.model.FHCategoryEntry;
import org.fhaes.model.FHSeries;
import org.fhaes.preferences.App;
import org.fhaes.preferences.FHAESPreferences.PrefKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tridas.io.exceptions.InvalidDendroFileException;
import org.tridas.io.exceptions.InvalidDendroFileException.PointerType;
import org.tridas.io.formats.fhx2.FHX2Reader;
import org.tridas.io.util.TridasUtils;
import org.tridas.schema.TridasGenericField;
import org.tridas.schema.TridasObject;
import org.tridas.schema.TridasProject;
import org.tridas.spatial.GMLPointSRSHandler;

/**
 * FHFile Class. Simple extension of java.io.File which includes functions for checking whether this is a valid FHX format file, and if not,
 * to access the error reporting features of the DendroFileIO library.
 * 
 * @author Peter Brewer
 */
public class FHFile extends File {
	
	private static final long serialVersionUID = 1L;
	
	// Declare logger
	private static final Logger log = LoggerFactory.getLogger(FHFile.class);
	
	// Declare local variables
	private AbstractFireHistoryReader fhaesReader;
	private boolean isFileValid;
	private boolean isInitialised = false;
	private String categoryFilePath = null;
	private String errorMessage = null;
	private String report = "";
	private Integer lineNumberError = null;
	private TridasProject[] projects;
	private BigDecimal latitude;
	private BigDecimal longitude;
	private FHX2Reader tricycleReader;
	private Boolean locationInitialize = false;
	
	/**
	 * Initializes a new FHFile.
	 * 
	 * @param file
	 */
	public FHFile(File file) {
	
		super(file.getAbsolutePath());
		init();
	}
	
	public FHFile(File file, boolean doCheck) {
	
		super(file.getAbsolutePath());
		init();
	}
	
	/**
	 * Construct a new FHFile using a string containing the full filename
	 * 
	 * @param filename
	 */
	public FHFile(String filename) {
	
		super(filename);
		init();
	}
	
	/**
	 * Get the AbstractFireHistoryReader for this file
	 * 
	 * @return
	 */
	public AbstractFireHistoryReader getFireHistoryReader() {
	
		return fhaesReader;
	}
	
	/**
	 * Get a summary report about this file as a string. This report is normally displayed in the file summary tab of FHAES
	 * 
	 * @return
	 */
	public String getReport() {
	
		return report;
	}
	
	/**
	 * Get a string containing an error message reporting problems with the file. If the file is valid then this will return null.
	 * 
	 * @return
	 */
	public String getErrorMessage() {
	
		if (!isInitialised)
			init();
		
		EventTypeToProcess eventType = App.prefs.getEventTypePref(PrefKey.EVENT_TYPE_TO_PROCESS, EventTypeToProcess.FIRE_EVENT);
		
		// If file is valid but is missing the required events then override the error messages from the libraries
		if (this.isFileValid)
		{
			if (eventType.equals(EventTypeToProcess.FIRE_EVENT))
			{
				if (!fhaesReader.hasFireEvents())
				{
					return "N.B. The analysis parameters are currently set to analyze files for fire scars.  "
							+ "This file, however contains no fire scars so will be excluded from the analyses";
				}
			}
			else if (eventType.equals(EventTypeToProcess.INJURY_EVENT))
			{
				if (!fhaesReader.hasInjuryEvents())
				{
					return "N.B. The analysis parameters are currently set to analyze files for other indicators.  "
							+ "This file, however contains no other indicators so will be excluded from the analyses";
				}
			}
			else if (eventType.equals(EventTypeToProcess.FIRE_AND_INJURY_EVENT))
			{
				if (!fhaesReader.hasInjuryEvents() && !fhaesReader.hasFireEvents())
				{
					return "N.B. This file contains no fire or other indicators so will be excluded from the analyses";
				}
			}
		}
		
		return errorMessage;
	}
	
	/**
	 * Check whether this is a valid FHX file
	 * 
	 * @return
	 */
	public boolean isValidFHXFile() {
	
		if (!isInitialised)
			init();
		try
		{
			return isFileValid;
		}
		catch (Exception e)
		{
			return false;
		}
	}
	
	/**
	 * If this is not a valid FHX file then get the line number at which the first error exists
	 * 
	 * @return
	 */
	public Integer getErrorLine() {
	
		if (!isInitialised)
			init();
		try
		{
			return lineNumberError;
		}
		catch (Exception e)
		{
			return null;
		}
	}
	
	/**
	 * Does this file have fire events or other injuries?
	 * 
	 * @return
	 */
	public boolean hasFireEventsOrInjuries() {
	
		if (!isInitialised)
			init();
		try
		{
			return fhaesReader.hasFireEventsOrInjuries();
		}
		catch (Exception e)
		{
			return false;
		}
	}
	
	/**
	 * Does this file have any fire events?
	 * 
	 * @return
	 */
	public boolean hasFireEvents() {
	
		if (!isInitialised)
			init();
		try
		{
			return fhaesReader.hasFireEvents();
		}
		catch (Exception e)
		{
			return false;
		}
	}
	
	/**
	 * Does this file have any injuries recorded?
	 * 
	 * @return
	 */
	public boolean hasInjuryEvents() {
	
		if (!isInitialised)
			init();
		try
		{
			return fhaesReader.hasInjuryEvents();
		}
		catch (Exception e)
		{
			return false;
		}
	}
	
	/**
	 * Get the first year in this file
	 * 
	 * @return
	 */
	public Integer getFirstYear() {
	
		if (!isInitialised)
			init();
		try
		{
			return fhaesReader.getFirstYear();
		}
		catch (Exception e)
		{
			return null;
		}
	}
	
	/**
	 * Get the last year in this file
	 * 
	 * @return
	 */
	public Integer getLastYear() {
	
		if (!isInitialised)
			init();
		try
		{
			return fhaesReader.getLastYear();
		}
		catch (Exception e)
		{
			return null;
		}
	}
	
	/**
	 * Get the site code from the file metadata. If no site code is included returns "Unknown site code".
	 * 
	 * @return
	 */
	public String getSiteCode() {
	
		if (!isInitialised)
			init();
		try
		{
			projects = tricycleReader.getProjects();
			if (projects == null)
				return "Unknown site code";
			if (projects.length == 0)
				return "Unknown site code";
			
			TridasObject o = projects[0].getObjects().get(0);
			
			TridasGenericField objCodeField = TridasUtils.getGenericFieldByName(o, "tellervo.objectLabCode");
			return objCodeField.getValue();
		}
		catch (Exception e)
		{
			
		}
		
		return "Unknown site code";
	}
	
	/**
	 * Get the site name from the file's metadata. If no site name is included then it returns "Unknown site".
	 * 
	 * @return
	 */
	public String getSiteName() {
	
		if (!isInitialised)
			init();
		try
		{
			projects = tricycleReader.getProjects();
			if (projects == null)
				return "Unknown site";
			if (projects.length == 0)
				return "Unknown site";
			
			TridasObject o = projects[0].getObjects().get(0);
			
			return o.getTitle();
		}
		catch (Exception e)
		{
			
		}
		
		return "Unknown site";
	}
	
	/**
	 * Get the string to label this file by in analysis results. The type of label is determined by the users preference settings.
	 * 
	 * @return
	 */
	public String getLabel() {
	
		AnalysisLabelType lt = App.prefs.getAnalysisLabelTypePref(PrefKey.ANALYSIS_LABEL_TYPE, AnalysisLabelType.INPUT_FILENAME);
		String label = "";
		
		if (lt != null)
		{
			if (lt.equals(AnalysisLabelType.INPUT_FILENAME))
			{
				label = getFileNameWithoutExtension().replace(",", "_");
			}
			else if (lt.equals(AnalysisLabelType.SITE_CODE))
			{
				label = getSiteCode().replace(",", "_");
			}
			else if (lt.equals(AnalysisLabelType.SITE_NAME))
			{
				label = getSiteName().replace(",", "_");
			}
			else
			{
				label = getFileNameWithoutExtension().replace(",", "_");
			}
		}
		
		if (label == null || label.length() == 0)
		{
			label = getFileNameWithoutExtension().replace(",", "_");
			if (label == null || label.length() == 0)
			{
				label = "-";
			}
		}
		
		return label;
	}
	
	/**
	 * Get the sampling date of the first samples within this file.
	 * 
	 * @return
	 */
	public String getFirstCollectionDate() {
	
		projects = tricycleReader.getProjects();
		if (projects == null)
			return null;
		if (projects.length == 0)
			return null;
		
		TridasProject p = projects[0];
		if (p.isSetObjects())
		{
			try
			{
				Date dt = p.getObjects().get(0).getElements().get(0).getSamples().get(0).getSamplingDate().getValue().toGregorianCalendar()
						.getTime();
				DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
				df.setTimeZone(TimeZone.getTimeZone("UTC"));
				return df.format(dt);
			}
			catch (Exception e)
			{
			}
		}
		
		return null;
	}
	
	/**
	 * Get's the first state or province specified in the metadata or null if no state is specified.
	 * 
	 * @return
	 */
	public String getFirstState() {
	
		projects = tricycleReader.getProjects();
		if (projects == null)
			return null;
		if (projects.length == 0)
			return null;
		
		TridasProject p = projects[0];
		if (p.isSetObjects())
		{
			try
			{
				return p.getObjects().get(0).getLocation().getAddress().getStateProvinceRegion();
			}
			catch (Exception e)
			{
			}
		}
		
		return null;
	}
	
	/**
	 * Get's the first country specified in the metadata or null if no country is specified.
	 * 
	 * @return
	 */
	public String getFirstCountry() {
	
		projects = tricycleReader.getProjects();
		if (projects == null)
			return null;
		if (projects.length == 0)
			return null;
		
		TridasProject p = projects[0];
		if (p.isSetObjects())
		{
			try
			{
				return p.getObjects().get(0).getLocation().getAddress().getCountry();
			}
			catch (Exception e)
			{
			}
		}
		
		return null;
	}
	
	/**
	 * Parse the location information from the metadata and store in latitude and longitude fields
	 */
	private void parseLocation() {
	
		if (!isInitialised)
			init();
		locationInitialize = true;
		
		// Parse the legacy data file
		try
		{
			// TridasEntitiesFromDefaults def = new TridasEntitiesFromDefaults();
			
			projects = tricycleReader.getProjects();
			
			if (projects == null)
				return;
			if (projects.length == 0)
				return;
			
			TridasProject p = projects[0];
			
			try
			{
				// Set coordinates using the projection handler to make sure we're reading correctly
				TridasObject o = p.getObjects().get(0);
				if (o.isSetLocation())
				{
					if (o.getLocation().isSetLocationGeometry())
					{
						if (o.getLocation().getLocationGeometry().isSetPoint())
						{
							GMLPointSRSHandler tph = new GMLPointSRSHandler(o.getLocation().getLocationGeometry().getPoint());
							
							latitude = BigDecimal.valueOf(tph.getWGS84LatCoord());
							// latitude = tph.getWGS84LatCoord().toString();
							// longitude = tph.getWGS84LongCoord().toString();
							longitude = BigDecimal.valueOf(tph.getWGS84LongCoord());
							
						}
					}
				}
				
			}
			catch (Exception e1)
			{
				return;
			}
		}
		catch (Exception e)
		{
			log.error("Exception parsing FHFile");
			e.printStackTrace();
		}
	}
	
	/**
	 * Get the latitude specified in the metadata as a string
	 * 
	 * @return
	 */
	public String getFirstLatitude() {
	
		if (!locationInitialize)
			parseLocation();
		
		try
		{
			return latitude.toString();
		}
		catch (NullPointerException e)
		{
			return null;
		}
	}
	
	/**
	 * Get the longitude specified in the metadata as a string
	 * 
	 * @return
	 */
	public String getFirstLongitude() {
	
		if (!locationInitialize)
			parseLocation();
		
		try
		{
			return longitude.toString();
		}
		catch (NullPointerException e)
		{
			return null;
		}
	}
	
	/**
	 * Get the latitude specified in the metadata as a double
	 * 
	 * @return
	 */
	public Double getFirstLatitudeDbl() {
	
		if (!locationInitialize)
			parseLocation();
		
		try
		{
			return latitude.doubleValue();
		}
		catch (Exception e)
		{
			return null;
		}
	}
	
	/**
	 * Get the longitude specified in the metadata as a double
	 * 
	 * @return
	 */
	public Double getFirstLongitudeDbl() {
	
		if (!locationInitialize)
			parseLocation();
		
		try
		{
			return longitude.doubleValue();
		}
		catch (Exception e)
		{
			return null;
		}
	}
	
	/**
	 * Returns the name of the file without the extension. e.g. if the name is "sample-test.fhx" this would return "sample-test".
	 * 
	 * @return filename with no extension
	 */
	public String getFileNameWithoutExtension() {
	
		String ext = "." + FileUtils.getExtension(this.getName());
		return this.getName().substring(0, this.getName().length() - ext.length());
	}
	
	/**
	 * Returns the file path of the default-named category file for this FHFile. e.g. if the name is "sample-test.fhx" this would return the
	 * absolute path of the file "sample-test-categories.csv", which is assumed to be located in the same directory.
	 * 
	 * @return the filepath of the default-named category file
	 */
	public String getDefaultCategoryFilePath() {
	
		return FilenameUtils.removeExtension(this.getAbsolutePath()) + "-categories.csv";
	}
	
	/**
	 * Gets the absolute file path of the category file pertaining to this FHFile.
	 * 
	 * @return categoryFilePath
	 */
	public String getCategoryFilePath() {
	
		return categoryFilePath;
	}
	
	/**
	 * Sets the absolute file path of the category file pertaining to this FHFile.
	 * 
	 * @param inPath
	 */
	public void setCategoryFilePath(String inPath) {
	
		categoryFilePath = inPath;
	}
	
	/**
	 * Attaches the category entries to their corresponding series in the FHX file.
	 * 
	 * @param categoryEntries
	 */
	public void attachCategoriesToFile(ArrayList<FHCategoryEntry> categoryEntries) {
	
		// Make a new list for storing the modified series
		ArrayList<FHSeries> seriesListWithCategories = new ArrayList<FHSeries>();
		
		// Loop through all series in the file, add categories if necessary, and add them to the new list
		for (int i = 0; i < getFireHistoryReader().getSeriesList().size(); i++)
		{
			FHSeries currentSeries = getFireHistoryReader().getSeriesList().get(i);
			
			for (int j = 0; j < categoryEntries.size(); j++)
			{
				FHCategoryEntry currentEntry = categoryEntries.get(j);
				
				if (currentSeries.getTitle().equals(currentEntry.getSeriesTitle()))
				{
					currentSeries.getCategoryEntries().add(currentEntry);
				}
			}
			
			seriesListWithCategories.add(currentSeries);
		}
		
		// Replace the file's existing series list with the new series list
		getFireHistoryReader().replaceSeriesList(seriesListWithCategories);
	}
	
	/**
	 * Clears all category entries for all series in the FireHistoryReader for this FHFile.
	 */
	public void clearAllCategoryEntries() {
	
		for (int i = 0; i < getFireHistoryReader().getSeriesList().size(); i++)
		{
			FHSeries currentSeries = getFireHistoryReader().getSeriesList().get(i);
			currentSeries.getCategoryEntries().clear();
		}
	}
	
	/**
	 * Initializes all properties of a new FHFile.
	 */
	private void init() {
	
		log.debug("Initialising file: " + this.getName());
		isInitialised = true;
		isFileValid = false;
		
		File outputFile = null;
		try
		{
			outputFile = File.createTempFile("fhaes", ".tmp");
			outputFile.deleteOnExit();
		}
		catch (IOException e1)
		{
			e1.printStackTrace();
		}
		
		File[] inputFileArr = new File[1];
		inputFileArr[0] = this;
		
		// Parse the legacy data file
		try
		{
			// Create a new converter
			tricycleReader = new FHX2Reader();
			log.debug("Checking file using DendroFileIO...");
			
			// TridasEntitiesFromDefaults def = new TridasEntitiesFromDefaults();
			tricycleReader.loadFile(super.getAbsolutePath());
			log.debug("DendroFileIO is happy with file");
			isFileValid = true;
		}
		catch (IOException e)
		{
			// Standard IO Exception
			log.info("IO Exception in DendroFileIO...  " + e.getLocalizedMessage());
			errorMessage = "Unable to open file";
			isFileValid = false;
			return;
		}
		catch (InvalidDendroFileException e)
		{
			// Fatal error interpreting file
			log.info(e.getLocalizedMessage());
			errorMessage = e.getLocalizedMessage();
			isFileValid = false;
			
			if (e.getPointerType().equals(PointerType.LINE) && e.getPointerNumber() != null)
			{
				try
				{
					lineNumberError = Integer.parseInt(e.getPointerNumber());
				}
				catch (NumberFormatException ex)
				{
					// Do nothing!
				}
			}
			return;
		}
		
		log.debug("DendroFileIO was happy with file, but let's make sure that FHAES parser is happy too...");
		
		fhaesReader = new FHX2FileReader(this);
		try
		{
			FHFileChecker checker = new FHFileChecker();
			isFileValid = checker.doCheck(null, inputFileArr, outputFile, false, true);
			report = checker.getReport();
		}
		catch (Exception e)
		{
			log.error("Elena's file checker crashed.  Cannot read file.");
			errorMessage = "Error parsing file using FHAES stage 2 parser.  Unknown error";
			report = "An unhandled error was encountered when checking this file.\nPlease contact the developers for further information. Technical details are as follows:\n\nException type:  "
					+ e.getClass().getSimpleName() + "\nError:           " + e.getLocalizedMessage();
			
			e.printStackTrace();
			return;
		}
		finally
		{
			outputFile.delete();
		}
		
		fhaesReader = new FHX2FileReader(this);
		
		if (isFileValid)
		{
			log.debug("Elena's file checker is happy with file");
			return;
		}
		else
		{
			log.debug("Elena's file checker found an error");
			errorMessage = "FHAES stage 2 parser found an error with this file.  See summary tab for more information.";
		}
	}
}
