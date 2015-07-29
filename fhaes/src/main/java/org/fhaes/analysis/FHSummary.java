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
package org.fhaes.analysis;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import org.fhaes.enums.EventTypeToProcess;
import org.fhaes.fhfilereader.FHX2FileReader;
import org.fhaes.model.FHFile;
import org.fhaes.preferences.App;
import org.fhaes.preferences.FHAESPreferences.PrefKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * FHSummary Class.
 */
public class FHSummary {
	
	private static final Logger log = LoggerFactory.getLogger(FHSummary.class);
	private FHFile[] inputFileArray;
	
	/**
	 * Constructor for FHSummary
	 * 
	 * @param inputFileArray
	 */
	public FHSummary(FHFile[] inputFileArray) {
	
		this.inputFileArray = inputFileArray;
	}
	
	/**
	 * Generate a CSV file containing summary information for the files specified in the constructor
	 * 
	 * @return
	 */
	public File getFilesSummaryAsCSVFile() {
	
		String str = getFilesSummaryAsString();
		
		try
		{
			File temp = File.createTempFile("fhsummary", ".tmp");
			temp.deleteOnExit();
			
			BufferedWriter wr = new BufferedWriter(new FileWriter(temp));
			wr.write(str);
			wr.close();
			return temp;
		}
		catch (FileNotFoundException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
		
	}
	
	/**
	 * Generate a string containing CSV delimited summary table
	 * 
	 * @return
	 */
	public String getFilesSummaryAsString() {
	
		EventTypeToProcess ettp = App.prefs.getEventTypePref(PrefKey.EVENT_TYPE_TO_PROCESS, EventTypeToProcess.FIRE_AND_INJURY_EVENT);
		StringBuilder string = new StringBuilder();
		
		string.append("Filename,Site name,Site code,Series name,Sampling date,Lat,Lon,State,Country,First year,Last year,Has pith,Has bark,");
		if (ettp.equals(EventTypeToProcess.FIRE_EVENT))
		{
			string.append("Fire event years,");
			string.append("Fire event year seasonality\n");
		}
		else if (ettp.equals(EventTypeToProcess.INJURY_EVENT))
		{
			string.append("Injury event years,");
			string.append("Injury event year seasonality\n");
		}
		else if (ettp.equals(EventTypeToProcess.FIRE_AND_INJURY_EVENT))
		{
			string.append("Fire and injury event years,");
			string.append("Fire and injury event year seasonality\n");
		}
		
		for (FHFile file : inputFileArray)
		{
			try
			{
				FHX2FileReader fhx = new FHX2FileReader(file);
				
				if (fhx.getNumberOfSeries() == 0)
					continue;
				
				String filename = file.getAbsoluteFile().getName();
				int[] inneryearpith = fhx.getPithIndexPerSample();
				int[] inneryear = fhx.getInnerMostperTree();
				int[] outeryearbark = fhx.getBarkIndexPerTree();
				int[] outeryear = fhx.getOutterMostperTree();
				int firstyear = fhx.getFirstYear();
				ArrayList<ArrayList<Integer>> eventdata = fhx.getEventDataArrays(ettp);
				ArrayList<String> seasonalitydata = fhx.getData();
				
				for (int i = 0; i < fhx.getNumberOfSeries(); i++)
				{
					Integer firstyearind = 0;
					Integer lastyearind = 0;
					Boolean hasPith = false;
					Boolean hasBark = false;
					if (inneryearpith[i] != -1 && inneryear[i] == -1)
					{
						hasPith = true;
					}
					else if (inneryearpith[i] == -1 && inneryear[i] != -1)
					{
						hasPith = false;
					}
					else
					{
						log.debug("Inconsistent first year in file " + filename + " series number " + i);
						log.debug("inneryearpith value = " + inneryearpith[i]);
						log.debug("inneryear value     = " + inneryear[i]);
						continue;
					}
					if (outeryearbark[i] != -1 && outeryear[i] == -1)
					{
						hasBark = true;
					}
					else if (outeryearbark[i] == -1 && outeryear[i] != -1)
					{
						hasBark = false;
					}
					else
					{
						
						log.debug("Inconsistent first year in file " + filename + " series number " + i);
						log.debug("outeryearbark value = " + outeryearbark[i]);
						log.debug("outeryear value elena    = " + outeryear[i]);
						continue;
					}
					
					string.append(filename);
					string.append(", ");
					string.append(file.getSiteName().trim().replace(",", ";") + " ");
					string.append(", ");
					string.append(file.getSiteCode().trim().replace(",", ";") + " ");
					string.append(", ");
					string.append(fhx.getSeriesNameArray().get(i).trim().replace(",", ";") + " ");
					string.append(", ");
					if (file.getFirstCollectionDate() != null)
					{
						string.append(file.getFirstCollectionDate().trim().replace(",", ";") + " ");
					}
					else
					{
						string.append(" ");
					}
					string.append(", ");
					
					if (file.getFirstLatitude() == null)
					{
						string.append(" ");
					}
					else
					{
						string.append(file.getFirstLatitude());
					}
					string.append(", ");
					if (file.getFirstLongitude() == null)
					{
						string.append(" ");
					}
					else
					{
						string.append(file.getFirstLongitude());
					}
					string.append(", ");
					string.append(file.getFirstState().trim().replace(",", ";") + " ");
					string.append(", ");
					string.append(file.getFirstCountry().trim().replace(",", ";") + " ");
					string.append(", ");
					if (hasPith)
					{
						string.append(inneryearpith[i] + firstyear);
						firstyearind = inneryearpith[i];
						
					}
					else
					{
						string.append(inneryear[i] + firstyear);
						firstyearind = inneryear[i];
						
					}
					string.append(", ");
					if (hasBark)
					{
						string.append(outeryearbark[i] + firstyear);
						lastyearind = outeryearbark[i];
					}
					else
					{
						string.append(outeryear[i] + firstyear);
						lastyearind = outeryear[i];
						
					}
					string.append(", ");
					if (hasPith)
					{
						string.append("y");
					}
					else
					{
						string.append("n");
					}
					string.append(", ");
					
					if (hasBark)
					{
						string.append("y");
					}
					else
					{
						string.append("n");
					}
					string.append(", ");
					if (fhx.hasFireEventsOrInjuries())
					{
						ArrayList<Integer> bb = eventdata.get(i);
						
						for (int yrind = firstyearind; yrind < lastyearind; yrind++)
						{
							Integer code = bb.get(yrind);
							if (code.equals(1))
							{
								string.append(yrind + firstyear + " ");
							}
						}
						// string.append(fhx.get)
					}
					string.append(", ");
					if (fhx.hasFireEventsOrInjuries())
					{
						ArrayList<Integer> bb = eventdata.get(i);
						
						for (int yrind = firstyearind; yrind < lastyearind; yrind++)
						{
							Integer code = bb.get(yrind);
							
							String seasonline = seasonalitydata.get(yrind);
							String seasoncode = seasonline.substring(i, i + 1);
							
							if (code.equals(1))
							{
								string.append(yrind + firstyear);
								string.append(seasoncode + " ");
							}
						}
						// string.append(fhx.get)
					}
					string.append("\n");
				}
			}
			catch (Exception e)
			{
				log.debug("The file " + file.getAbsolutePath() + " is invalid");
				
			}
			
		}
		
		return string.toString();
		
	}
}
