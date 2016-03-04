/**************************************************************************************************
 * Fire History Analysis and Exploration System (FHAES), Copyright (C) 2015
 * 
 * Contributors: Josh Brogan, Jake Lokkesmoe, Chinmay Shah, Scott Goble, and Peter Brewer
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
package org.fhaes.fhrecorder.controller;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Writer;
import java.util.List;

import org.fhaes.exceptions.CompositeFileException;
import org.fhaes.fhrecorder.model.FHX2_Event;
import org.fhaes.fhrecorder.model.FHX2_File;
import org.fhaes.fhrecorder.model.FHX2_Sample;
import org.fhaes.fhrecorder.util.SampleErrorModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * IOController Class. This file contains the input/output functions of the service layer.
 * 
 * @author Josh Brogan, Jake Lokkesmoe, Chinmay Shah, Scott Goble
 */
public class IOController {
	
	private static final Logger log = LoggerFactory.getLogger(IOController.class);
	
	public static final String SITE_NAME_PREFIX = "Name of site   :";
	public static final String SITE_CODE_PREFIX = "Site code      :";
	public static final String COLLECTION_DATE_PREFIX = "Collection date:";
	public static final String COLLECTORS_PREFIX = "Collectors     :";
	public static final String CROSSDATERS_PREFIX = "Crossdaters    :";
	public static final String NUMBER_SAMPLES_PREFIX = "Number samples :";
	public static final String SPECIES_NAME_PREFIX = "Species name   :";
	public static final String COMMON_NAME_PREFIX = "Common name    :";
	public static final String HABITAT_TYPE_PREFIX = "Habitat type   :";
	public static final String COUNTRY_PREFIX = "Country        :";
	public static final String STATE_PREFIX = "State          :";
	public static final String COUNTY_PREFIX = "County         :";
	public static final String PARK_MONUMENT_PREFIX = "Park/Monument  :";
	public static final String NATIONAL_FOREST_PREFIX = "National Forest:";
	public static final String RANGER_DISTRICT_PREFIX = "Ranger district:";
	public static final String TOWNSHIP_PREFIX = "Township       :";
	public static final String RANGE_PREFIX = "Range          :";
	public static final String SECTION_PREFIX = "Section        :";
	public static final String QUARTER_SECTION_PREFIX = "Quarter section:";
	public static final String UTM_EASTING_PREFIX = "UTM easting    :";
	public static final String UTM_NORTHING_PREFIX = "UTM northing   :";
	public static final String LATITUDE_PREFIX = "Latitude       :";
	public static final String LONGITUDE_PREFIX = "Longitude      :";
	public static final String TOPOGRAPHIC_MAP_PREFIX = "Topographic map:";
	public static final String LOWEST_ELEV_PREFIX = "Lowest elev    :";
	public static final String HIGHEST_ELEV_PREFIX = "Highest elev   :";
	public static final String SLOPE_PREFIX = "Slope          :";
	public static final String ASPECT_PREFIX = "Aspect         :";
	public static final String AREA_SAMPLED_PREFIX = "Area sampled   :";
	public static final String SUBSTRATE_TYPE_PREFIX = "Substrate type :";
	public static final String BEGIN_COMMENTS_PREFIX = "Begin comments BELOW this line:";
	public static final String END_COMMENTS_PREFIX = "End comments ABOVE this line.";
	public static final String BEGIN_COMMENTS_OLD_PREFIX = "Begin comments :";
	public static final String END_COMMENTS_OLD_PREFIX = "End comments   :";
	public static final int START_OF_VALUE = 17;
	
	private static FHX2_File theFHX2File;
	private static String oldFile = "";
	
	/**
	 * Instantiates a new FHX2 file.
	 */
	protected static void createNewFile() {
	
		theFHX2File = new FHX2_File();
	}
	
	/**
	 * Returns the currently loaded FHX2 file.
	 * 
	 * @return theFHX2File
	 */
	public static FHX2_File getFile() {
	
		return theFHX2File;
	}
	
	/**
	 * Gets the unmodified version of the currently loaded file. This "old file" is generated during the time when a file is loaded into the
	 * program.
	 * 
	 * @return the old unmodified file
	 * @throws FileNotFoundException
	 */
	public static String getOldFile() throws FileNotFoundException {
	
		return oldFile;
	}
	
	/**
	 * Reads an FHX2 file from the disk and initializes all necessary data values within the program.
	 * 
	 * @param br
	 * @throws IOException
	 * @throws CompositeFileException
	 */
	public static void readFileFromBufferedReader(BufferedReader br) throws IOException, CompositeFileException {
	
		oldFile = "";
		FileController.setCorruptedState(false);
		createNewFile();
		readOptionalPartFromFile(br);
		readRequiredPartFromFile(br);
		theFHX2File.getOptionalPart().setDoneLoadingFile();
	}
	
	/**
	 * Reads the optional part of the FHX2 file from the disk and initializes all necessary data values within the program.
	 * 
	 * @param br
	 * @throws IOException
	 */
	private static void readOptionalPartFromFile(BufferedReader br) throws IOException {
	
		String line;
		while (((line = br.readLine()) != null) && !line.equals("FHX2 FORMAT") && !line.equals("FIRE2 FORMAT"))
		{
			if (!line.startsWith(BEGIN_COMMENTS_OLD_PREFIX))
				oldFile += line + "\r\n";
			
			if (line.length() <= START_OF_VALUE)
			{
				// Line too short to be of use
			}
			else if (line.startsWith(SITE_NAME_PREFIX))
				theFHX2File.getOptionalPart().setNameOfSite(line.substring(START_OF_VALUE));
			else if (line.startsWith(SITE_CODE_PREFIX))
				theFHX2File.getOptionalPart().setSiteCode(line.substring(START_OF_VALUE));
			else if (line.startsWith(COLLECTION_DATE_PREFIX))
				theFHX2File.getOptionalPart().setCollectionDate(line.substring(START_OF_VALUE));
			else if (line.startsWith(COLLECTORS_PREFIX))
				theFHX2File.getOptionalPart().setCollectors(line.substring(START_OF_VALUE));
			else if (line.startsWith(CROSSDATERS_PREFIX))
				theFHX2File.getOptionalPart().setCrossdaters(line.substring(START_OF_VALUE));
			else if (line.startsWith(NUMBER_SAMPLES_PREFIX))
				theFHX2File.getOptionalPart().setNumberSamples(line.substring(START_OF_VALUE));
			else if (line.startsWith(SPECIES_NAME_PREFIX))
				theFHX2File.getOptionalPart().setSpeciesName(line.substring(START_OF_VALUE));
			else if (line.startsWith(COMMON_NAME_PREFIX))
				theFHX2File.getOptionalPart().setCommonName(line.substring(START_OF_VALUE));
			else if (line.startsWith(HABITAT_TYPE_PREFIX))
				theFHX2File.getOptionalPart().setHabitatType(line.substring(START_OF_VALUE));
			else if (line.startsWith(COUNTRY_PREFIX))
				theFHX2File.getOptionalPart().setCountry(line.substring(START_OF_VALUE));
			else if (line.startsWith(STATE_PREFIX))
				theFHX2File.getOptionalPart().setState(line.substring(START_OF_VALUE));
			else if (line.startsWith(COUNTY_PREFIX))
				theFHX2File.getOptionalPart().setCounty(line.substring(START_OF_VALUE));
			else if (line.startsWith(PARK_MONUMENT_PREFIX))
				theFHX2File.getOptionalPart().setParkMonument(line.substring(START_OF_VALUE));
			else if (line.startsWith(NATIONAL_FOREST_PREFIX))
				theFHX2File.getOptionalPart().setNationalForest(line.substring(START_OF_VALUE));
			else if (line.startsWith(RANGER_DISTRICT_PREFIX))
				theFHX2File.getOptionalPart().setRangerDistrict(line.substring(START_OF_VALUE));
			else if (line.startsWith(TOWNSHIP_PREFIX))
				theFHX2File.getOptionalPart().setTownship(line.substring(START_OF_VALUE));
			else if (line.startsWith(RANGE_PREFIX))
				theFHX2File.getOptionalPart().setRange(line.substring(START_OF_VALUE));
			else if (line.startsWith(SECTION_PREFIX))
				theFHX2File.getOptionalPart().setSection(line.substring(START_OF_VALUE));
			else if (line.startsWith(QUARTER_SECTION_PREFIX))
				theFHX2File.getOptionalPart().setQuarterSection(line.substring(START_OF_VALUE));
			else if (line.startsWith(UTM_EASTING_PREFIX))
				theFHX2File.getOptionalPart().setUtmEasting(line.substring(START_OF_VALUE));
			else if (line.startsWith(UTM_NORTHING_PREFIX))
				theFHX2File.getOptionalPart().setUtmNorthing(line.substring(START_OF_VALUE));
			else if (line.startsWith(LATITUDE_PREFIX))
				theFHX2File.getOptionalPart().setLatitude(line.substring(START_OF_VALUE));
			else if (line.startsWith(LONGITUDE_PREFIX))
				theFHX2File.getOptionalPart().setLongitude(line.substring(START_OF_VALUE));
			else if (line.startsWith(TOPOGRAPHIC_MAP_PREFIX))
				theFHX2File.getOptionalPart().setTopographicMap(line.substring(START_OF_VALUE));
			else if (line.startsWith(LOWEST_ELEV_PREFIX))
				theFHX2File.getOptionalPart().setLowestElev(line.substring(START_OF_VALUE));
			else if (line.startsWith(HIGHEST_ELEV_PREFIX))
				theFHX2File.getOptionalPart().setHighestElev(line.substring(START_OF_VALUE));
			else if (line.startsWith(SLOPE_PREFIX))
				theFHX2File.getOptionalPart().setSlope(line.substring(START_OF_VALUE));
			else if (line.startsWith(ASPECT_PREFIX))
				theFHX2File.getOptionalPart().setAspect(line.substring(START_OF_VALUE));
			else if (line.startsWith(AREA_SAMPLED_PREFIX))
				theFHX2File.getOptionalPart().setAreaSampled(line.substring(START_OF_VALUE));
			else if (line.startsWith(SUBSTRATE_TYPE_PREFIX))
				theFHX2File.getOptionalPart().setSubstrateType(line.substring(START_OF_VALUE));
			else if (line.startsWith(BEGIN_COMMENTS_PREFIX))
				readCommentsFromFile(br, line, false);
			else if (line.startsWith(BEGIN_COMMENTS_OLD_PREFIX))
				readCommentsFromFile(br, line, true);
		}
		
		if (line != null)
			oldFile += line + "\r\n";
	}
	
	/**
	 * Reads the comments section of the FHX2 file from the disk and loads it into the program.
	 * 
	 * @param br
	 * @throws IOException
	 */
	private static void readCommentsFromFile(BufferedReader br, String line, Boolean usesOldPrefix) throws IOException {
	
		String theComments = "";
		try
		{
			if (usesOldPrefix)
			{
				oldFile += BEGIN_COMMENTS_OLD_PREFIX + "\r\n";
				
				theComments += line.substring(START_OF_VALUE);
			}
			line = br.readLine();
			while (line != null && line.length() > 0 && !line.startsWith(END_COMMENTS_PREFIX) && !line.startsWith(END_COMMENTS_OLD_PREFIX))
			{
				theComments += line;
				line = br.readLine();
			}
		}
		catch (StringIndexOutOfBoundsException e)
		{
			log.debug("No comments");
		}
		
		oldFile += theComments + "\r\n";
		oldFile += line + "\r\n";
		theFHX2File.getOptionalPart().setComments(theComments);
	}
	
	/**
	 * Reads the required part of the FHX2 file from the disk and initializes all necessary data values within the program.
	 * 
	 * @param br
	 * @throws IOException
	 * @throws CompositeFileException
	 */
	private static void readRequiredPartFromFile(BufferedReader br) throws IOException, CompositeFileException {
	
		String line = br.readLine(); // This has the 3 important numbers. Missing feature 2
		if (line != null)
		{
			oldFile += line + "\r\n";
			String[] lineParts = line.split(" ");
			int dataSetFirstYear = Integer.parseInt(lineParts[0]);
			int numSamples = Integer.parseInt(lineParts[1]);
			int idLength = Integer.parseInt(lineParts[2]);
			
			if (dataSetFirstYear == 0)
				dataSetFirstYear = -1;
			
			theFHX2File.getRequiredPart().setDataSetFirstYear(dataSetFirstYear);
			theFHX2File.getRequiredPart().setIDLength(idLength);
			
			// At this point, the currently read in line is the 3 numbers
			FileController.setTitleName(FileController.progName + " - " + FileController.fileName);
			readSamplesFromFile(br, dataSetFirstYear, numSamples, idLength);
		}
	}
	
	/**
	 * TODO
	 * 
	 * @param sampledata
	 * @return
	 */
	private static boolean isSamplePotentiallyCompositeStyle(String sampledata) {
	
		String[] standardChars = { "[", "]", "u", "d", "e", "l", "a", "m", "D", "E", "L", "A", "M" };
		
		for (String thischar : standardChars)
		{
			if (sampledata.contains(thischar))
			{
				return false;
				
			}
		}
		
		return true;
	}
	
	/**
	 * Reads each individual sample from the FHX2 file from the disk and loads the information into the sample list.
	 * 
	 * @param br
	 * @param dataSetFirstYear
	 * @param numSamples
	 * @param idLength
	 * @throws CompositeFileException
	 */
	private static void readSamplesFromFile(BufferedReader br, int dataSetFirstYear, int numSamples, int idLength) throws IOException,
			CompositeFileException {
	
		String[] sampleNameArray = new String[numSamples];
		for (int i = 0; i < sampleNameArray.length; i++)
			sampleNameArray[i] = "";
		String line = br.readLine();
		oldFile += line + "\r\n";
		
		for (int i = 0; i < idLength; i++)
		{
			for (int j = 0; j < numSamples; j++)
			{
				try
				{
					char tempChar = line.charAt(j);
					sampleNameArray[j] += tempChar;
				}
				catch (IndexOutOfBoundsException e)
				{
					log.debug("Sample code ended unexpectedly. Proceeding anyway.");
					continue;
				}
			}
			line = br.readLine();
			oldFile += line + "\r\n";
		}
		
		String[] sampleDataArray = new String[numSamples];
		
		for (int i = 0; i < sampleDataArray.length; i++)
			sampleDataArray[i] = "";
		
		while ((line = br.readLine()) != null && line.length() > 0)
		{
			oldFile += line + "\r\n";
			for (int j = 0; j < numSamples; j++)
			{
				char tempChar = line.charAt(j);
				sampleDataArray[j] += tempChar;
			}
		}
		
		// Run basic checks to detect if file is composite or not
		if (!IOController.getFile().getOptionalPart().fileHasValidHeader() && !FileController.isOverrideCompositeWarnings())
		{
			boolean isComposite = true;
			
			for (String sample : sampleDataArray)
			{
				if (!isSamplePotentiallyCompositeStyle(sample))
				{
					isComposite = false;
				}
			}
			
			if (isComposite)
			{
				throw new CompositeFileException();
			}
		}
		
		for (int i = 0; i < numSamples; i++)
		{
			FHX2_Sample theSample = new FHX2_Sample(sampleNameArray[i]);
			theSample.parseDataString(sampleDataArray[i], dataSetFirstYear);
			
			if (theSample.getSampleFirstYear() != theSample.getSampleLastYear())
				theFHX2File.getRequiredPart().addSample(theSample);
		}
		
		theFHX2File.getRequiredPart().setDataSetFirstYear(dataSetFirstYear);
		if (theFHX2File.getRequiredPart().getDataSetLastYear() == 0)
		{
			theFHX2File.getRequiredPart().setDataSetLastYear(
					theFHX2File.getRequiredPart().getDataSetFirstYear() + sampleDataArray[0].length() - 1);
			FHX2_Sample newSample = new FHX2_Sample("<temporary_name>", theFHX2File.getRequiredPart().getDataSetFirstYear(), theFHX2File
					.getRequiredPart().getDataSetLastYear(), false, false);
			theFHX2File.getRequiredPart().addSample(newSample);
			FileController.setLastYearDefinedInFile(false);
		}
		
	}
	
	/**
	 * Writes all data from the currently loaded FHX2 file to the disk.
	 * 
	 * @param bw
	 * @throws IOException
	 */
	public static void writeFileToDisk(Writer bw) throws Exception {
	
		writeOptionalPartToDisk(bw);
		SampleController.updateAllSampleOpeningAndClosingChars();
		if (theFHX2File.getRequiredPart().getNumSamples() != 0)
			writeRequiredPartToDisk(bw);
	}
	
	/**
	 * Returns null if all is well
	 * 
	 * @return
	 */
	private static boolean checkForMissingBits() {
	
		for (FHX2_Sample sample : theFHX2File.getRequiredPart().getSampleList())
		{
			
			for (FHX2_Event event : sample.getEvents())
			{
				if (event.getEventYear() == null)
				{
					sample.getErrors().add(
							new SampleErrorModel("Sample \"" + sample.getSampleName() + "\" contains and event with a null year"));
					return false;
				}
			}
		}
		
		return true;
	}
	
	/**
	 * Writes the optional part of the the currently loaded FHX2 file to the disk.
	 * 
	 * @param bw
	 * @throws IOException
	 */
	private static void writeOptionalPartToDisk(Writer bw) throws IOException {
	
		bw.write(SITE_NAME_PREFIX + " " + theFHX2File.getOptionalPart().getNameOfSite() + "\r\n");
		bw.write(SITE_CODE_PREFIX + " " + theFHX2File.getOptionalPart().getSiteCode() + "\r\n");
		bw.write(COLLECTION_DATE_PREFIX + " " + theFHX2File.getOptionalPart().getCollectionDate() + "\r\n");
		bw.write(COLLECTORS_PREFIX + " " + theFHX2File.getOptionalPart().getCollectors() + "\r\n");
		bw.write(CROSSDATERS_PREFIX + " " + theFHX2File.getOptionalPart().getCrossdaters() + "\r\n");
		bw.write(NUMBER_SAMPLES_PREFIX + " " + getFile().getRequiredPart().getNumSamples() + "\r\n");
		bw.write(SPECIES_NAME_PREFIX + " " + theFHX2File.getOptionalPart().getSpeciesName() + "\r\n");
		bw.write(COMMON_NAME_PREFIX + " " + theFHX2File.getOptionalPart().getCommonName() + "\r\n");
		bw.write(HABITAT_TYPE_PREFIX + " " + theFHX2File.getOptionalPart().getHabitatType() + "\r\n");
		bw.write(COUNTRY_PREFIX + " " + theFHX2File.getOptionalPart().getCountry() + "\r\n");
		bw.write(STATE_PREFIX + " " + theFHX2File.getOptionalPart().getState() + "\r\n");
		bw.write(COUNTY_PREFIX + " " + theFHX2File.getOptionalPart().getCounty() + "\r\n");
		bw.write(PARK_MONUMENT_PREFIX + " " + theFHX2File.getOptionalPart().getParkMonument() + "\r\n");
		bw.write(NATIONAL_FOREST_PREFIX + " " + theFHX2File.getOptionalPart().getNationalForest() + "\r\n");
		bw.write(RANGER_DISTRICT_PREFIX + " " + theFHX2File.getOptionalPart().getRangerDistrict() + "\r\n");
		bw.write(TOWNSHIP_PREFIX + " " + theFHX2File.getOptionalPart().getTownship() + "\r\n");
		bw.write(RANGE_PREFIX + " " + theFHX2File.getOptionalPart().getRange() + "\r\n");
		bw.write(SECTION_PREFIX + " " + theFHX2File.getOptionalPart().getSection() + "\r\n");
		bw.write(QUARTER_SECTION_PREFIX + " " + theFHX2File.getOptionalPart().getQuarterSection() + "\r\n");
		bw.write(UTM_EASTING_PREFIX + " " + theFHX2File.getOptionalPart().getUtmEasting() + "\r\n");
		bw.write(UTM_NORTHING_PREFIX + " " + theFHX2File.getOptionalPart().getUtmNorthing() + "\r\n");
		bw.write(LATITUDE_PREFIX + " " + theFHX2File.getOptionalPart().getLatitude() + "\r\n");
		bw.write(LONGITUDE_PREFIX + " " + theFHX2File.getOptionalPart().getLongitude() + "\r\n");
		bw.write(TOPOGRAPHIC_MAP_PREFIX + " " + theFHX2File.getOptionalPart().getTopographicMap() + "\r\n");
		bw.write(LOWEST_ELEV_PREFIX + " " + theFHX2File.getOptionalPart().getLowestElev() + "\r\n");
		bw.write(HIGHEST_ELEV_PREFIX + " " + theFHX2File.getOptionalPart().getHighestElev() + "\r\n");
		bw.write(SLOPE_PREFIX + " " + theFHX2File.getOptionalPart().getSlope() + "\r\n");
		bw.write(ASPECT_PREFIX + " " + theFHX2File.getOptionalPart().getAspect() + "\r\n");
		bw.write(AREA_SAMPLED_PREFIX + " " + theFHX2File.getOptionalPart().getAreaSampled() + "\r\n");
		bw.write(SUBSTRATE_TYPE_PREFIX + " " + theFHX2File.getOptionalPart().getSubstrateType() + "\r\n");
		bw.write(BEGIN_COMMENTS_PREFIX + "\r\n");
		if (!theFHX2File.getOptionalPart().getComments().equals(""))
			bw.write(theFHX2File.getOptionalPart().getComments() + "\r\n");
		bw.write(END_COMMENTS_PREFIX + "\r\n");
	}
	
	/**
	 * Writes the required part of the the currently loaded FHX2 file to the disk.
	 * 
	 * @param bw
	 * @throws IOException
	 */
	private static void writeRequiredPartToDisk(Writer bw) throws IOException {
	
		int firstYear = theFHX2File.getRequiredPart().getDataSetFirstYear();
		int lastYear = theFHX2File.getRequiredPart().getDataSetLastYear();
		int idLength = theFHX2File.getRequiredPart().getIDLength();
		int numSamples = theFHX2File.getRequiredPart().getNumSamples();
		
		bw.write("\r\n");
		bw.write("FHX2 FORMAT\r\n");
		bw.write(firstYear + " " + numSamples + " " + idLength + "\r\n");
		
		List<FHX2_Sample> tempList = theFHX2File.getRequiredPart().getSampleList();
		for (int currentChar = 0; currentChar < idLength; currentChar++)
		{
			for (int currentID = 0; currentID < numSamples; currentID++)
			{
				if (tempList.get(currentID).getSampleName().length() > currentChar)
					bw.write(tempList.get(currentID).getSampleName().charAt(currentChar));
				else
					bw.write(" ");
			}
			bw.write("\r\n");
		}
		
		bw.write("\r\n");
		
		String[] sampleDataArray = new String[numSamples];
		for (int i = 0; i < numSamples; i++)
		{
			FHX2_Sample tempSample = theFHX2File.getRequiredPart().getSample(i);
			sampleDataArray[i] = tempSample.toString(firstYear, lastYear);
		}
		
		for (int i = 0; i <= (lastYear - firstYear); i++)
		{
			if (firstYear + i == 0)
				continue;
			for (int j = 0; j < numSamples; j++)
				bw.write(sampleDataArray[j].charAt(i));
			if (i == (lastYear - firstYear))
				bw.write(" " + (firstYear + i));
			else
				bw.write(" " + (firstYear + i) + "\r\n");
		}
	}
}
