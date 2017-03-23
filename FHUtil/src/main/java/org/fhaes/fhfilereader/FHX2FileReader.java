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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
import java.util.regex.Pattern;

import org.fhaes.enums.EventTypeToProcess;
import org.fhaes.model.FHSeries;
import org.fhaes.preferences.App;
import org.fhaes.preferences.FHAESPreferences.PrefKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ibm.icu.text.CharsetDetector;
import com.ibm.icu.text.CharsetMatch;

/**
 * FHX2FileReader Class.
 * 
 * <p>
 * Class for reading FHX format fire history data files. For more information about the FHX file format see the FHAES manual.
 * </p>
 * 
 * <p>
 * Instantiate this class by passing a java.io.File referred to a FHX file. Data and information from the file can then be extracted using a
 * variety of methods. The most commonly used are:
 * </p>
 * 
 * <ul>
 * <li>getEventDataArrays()</li>
 * <li>getFilterArrays()</li>
 * </ul>
 * 
 * <p>
 * Many methods in the class were deprecated in July 2014. These should be replaced by the corresponding new methods indicated in the
 * comments for each.
 * 
 * @author Elena Velasquez
 */
public class FHX2FileReader extends AbstractFireHistoryReader {
	
	private static final Logger log = LoggerFactory.getLogger(FHX2FileReader.class);
	private final File file;
	private String rawContent = "";
	private String format;
	private Integer firstYear;
	private Integer firstFireYear;
	private Integer firstInjuryYear;
	private Integer firstIndicatorYear;
	private Integer numberOfSeries;
	private Integer lengthOfSeriesName;
	private boolean isFormatInfoSet = false;
	private boolean hitBlankline;
	private boolean hitBadline;
	private ArrayList<String> seriesNameLine;
	private ArrayList<String> seriesName = new ArrayList<String>();
	private ArrayList<String> dataBlock;
	private ArrayList<String> dataByRow;
	private ArrayList<Integer> badDataLines;
	
	// *********
	// The following arrays all come in I, II, III variants corresponding to:
	// I - fires only
	// II - injuries only
	// III - fires and injuries
	// *********
	private ArrayList<Integer> climate1dI;
	private ArrayList<Integer> climate1dII;
	private ArrayList<Integer> climate1dIII;
	private ArrayList<ArrayList<Integer>> climate2dI;
	private ArrayList<ArrayList<Integer>> climate2dII;
	private ArrayList<ArrayList<Integer>> climate2dIII;
	private ArrayList<ArrayList<Double>> filters2dI;
	private ArrayList<ArrayList<Double>> filters2dII;
	private ArrayList<ArrayList<Double>> filters2dIII;
	
	// Booleans for keeping track of which arrays have been initialised
	private boolean isClimate1dIinit = false;
	private boolean isClimate1dIIinit = false;
	private boolean isClimate1dIIIinit = false;
	private boolean isClimate2dIinit = false;
	private final boolean isClimate2dIIinit = false;
	private boolean isClimate2dIIIinit = false;
	private boolean isFilterse2dIinit = false;
	private boolean isFilterse2dIIinit = false;
	private boolean isFilterse2dIIIinit = false;
	
	private ArrayList<ArrayList<Character>> capsperSample2d;
	private ArrayList<ArrayList<Integer>> capsYearperSample2d;
	private ArrayList<ArrayList<Character>> lowsperSample2d;
	private ArrayList<ArrayList<Integer>> lowsYearperSample2d;
	private ArrayList<ArrayList<Character>> calosperSample2d;
	private ArrayList<ArrayList<Integer>> calosYearperSample2d;
	private ArrayList<ArrayList<Integer>> recorderYears2DArray;
	private boolean isRecorderYears2DArrayInit = false;
	private ArrayList<ArrayList<Integer>> recorderYears2DArrayII;
	private boolean isRecorderYears2DArrayInitII = false;
	private ArrayList<ArrayList<Integer>> DecompSyb2d;
	private int[] lastFirePerSample;
	private int[] lastInjuryPerSample;
	
	private int[] pithIndexPerSample;
	private int[] totalRecordYearsPerSample;
	private int[] totalRecordYearsPerSampleII;
	private int[] totals;
	private ArrayList<Integer> yearArray;
	private int[] FFyearperTree;
	private int[] startYearIndexPerSample;
	private int[] lastYearIndexPerSample;
	private int[] innerMostPerTree;
	private int[] outerMostPerTree;
	private int[] barkPerTree;
	private int[] fIYearPerTree;
	private int[] fIIYearPerTree;
	private int[] fIIIYearPerTree;
	
	/**
	 * Constructor for reading FHX2 format fire history data files
	 * 
	 * @param inputFile - File to read
	 */
	public FHX2FileReader(File inputFile) {
	
		this.file = inputFile;
		init();
	}
	
	/**
	 * Generate the ArrayList of series names present in this file
	 */
	private void generateSeriesName() {
	
		seriesName = new ArrayList<String>();
		String ts;
		
		for (int i = 0; i < numberOfSeries; i++)
		{
			ts = "";
			for (int j = 0; j < lengthOfSeriesName; j++)
			{
				ts = ts + seriesNameLine.get(j).charAt(i);
			} // end of j loop
			seriesName.add(ts);
			// log.debug("the string is: "+ts);
		} // end of i loop
	}
	
	/**
	 * Generates the standard fireEventsArray
	 */
	private void generate1DEventsI() {
	
		climate1dI = new ArrayList<Integer>();
		String str;
		Integer in;
		
		for (int i = 0; i < dataBlock.size(); i++)
		{
			in = -7;
			str = dataBlock.get(i);
			for (int j = 0; j < str.length(); j++)
			{
				if (str.charAt(j) >= 'A' && str.charAt(j) <= 'Z')
				{
					in = 1;
					// changed in != 1 to in == 1
				}
				else if (in != 1 && ((str.charAt(j) >= 'a' && str.charAt(j) <= 'z') || str.charAt(j) == '|'))
				{
					in = 0;
				}
				else if (in != 0 && in != 1)
				// (in != 0 && in != 1)
				{
					in = -1;
				}
				// log.debug(str.charAt(j) + "\t" + in.toString());
			}
			// log.debug(str + '\t' + in);
			climate1dI.add(in);
		}
		this.isClimate1dIinit = true;
		firstFireYear = firstYear + climate1dI.indexOf(1);
	}
	
	/**
	 * Generates the otherInjuriesArray for storing lowercase events
	 */
	private void generate1DEventsII() {
	
		// instantiate the climate array list
		climate1dII = new ArrayList<Integer>();
		// declare local variables
		String strI;
		Integer inI;
		// log.debug("I am in make climateI injury");
		
		for (int i = 0; i < dataBlock.size(); i++)
		{
			inI = -7;
			strI = dataBlock.get(i);
			for (int j = 0; j < strI.length(); j++)
			{
				if (strI.charAt(j) >= 'a' && strI.charAt(j) <= 'z')
				{
					inI = 1;
					// elena changed in != 1 to in == 1
				}
				else if (inI != 1 && ((strI.charAt(j) >= 'A' && strI.charAt(j) <= 'A') || strI.charAt(j) == '|'))
				{
					inI = 0;
				}
				else if (inI != 0 && inI != 1)
				// (inI != 0 && inI != 1)
				{
					inI = -1;
				}
				// eelog.debug(str.charAt(j) + "\t" + in.toString());
			}
			// eelog.debug(str + '\t' + in);
			climate1dII.add(inI);
		}
		isClimate1dIIinit = true;
		
		firstInjuryYear = firstYear + climate1dII.indexOf(1);
	}
	
	/**
	 * Generates most of the basic arrays of data including climate2D.
	 */
	public void generate2DEventsI() {
	
		climate2dI = new ArrayList<ArrayList<Integer>>();
		capsperSample2d = new ArrayList<ArrayList<Character>>();
		lowsperSample2d = new ArrayList<ArrayList<Character>>();
		calosperSample2d = new ArrayList<ArrayList<Character>>();
		capsYearperSample2d = new ArrayList<ArrayList<Integer>>();
		lowsYearperSample2d = new ArrayList<ArrayList<Integer>>();
		calosYearperSample2d = new ArrayList<ArrayList<Integer>>();
		// declare local variables
		char[][] str = new char[numberOfSeries][dataBlock.size()];
		String ts;
		ArrayList<Integer> eachTree;
		ArrayList<Integer> eachTreeTemp;
		ArrayList<Character> capstemp;
		ArrayList<Integer> capsYtemp;
		ArrayList<Character> lowstemp;
		ArrayList<Integer> lowsYtemp;
		ArrayList<Character> calostemp;
		ArrayList<Integer> calosYtemp;
		Integer in;
		// log.debug("I am in make climate2d");
		// int FirstFireFlag[];
		
		fIYearPerTree = new int[numberOfSeries];
		FFyearperTree = new int[numberOfSeries];
		pithIndexPerSample = new int[numberOfSeries];
		innerMostPerTree = new int[numberOfSeries];
		outerMostPerTree = new int[numberOfSeries];
		barkPerTree = new int[numberOfSeries];
		startYearIndexPerSample = new int[numberOfSeries];
		lastYearIndexPerSample = new int[numberOfSeries];
		lastFirePerSample = new int[numberOfSeries];
		lastInjuryPerSample = new int[numberOfSeries];
		
		// Load the data of the fhx file into the str[j][i]
		// str is of size
		for (int i = 0; i < dataBlock.size(); i++)
		{
			ts = dataBlock.get(i);
			for (int j = 0; j < ts.length(); j++)
			{
				str[j][i] = ts.charAt(j);
			} // end of j loop
		} // end of i loop load
		
		// /new stuff from elena
		for (int i = 0; i < numberOfSeries; i++)
		{
			eachTreeTemp = new ArrayList<Integer>();
			for (int j = 0; j < dataBlock.size(); j++)
			{
				if (str[i][j] >= 'A' && str[i][j] <= 'Z')
				{
					eachTreeTemp.add(1);
				}
				else if (str[i][j] >= 'a' && str[i][j] <= 'z')
				{
					eachTreeTemp.add(6);
				}
				else if (str[i][j] == '[')
				{
					eachTreeTemp.add(2);
				}
				else if (str[i][j] == '{')
				{
					eachTreeTemp.add(3);
				}
				else if (str[i][j] == '}')
				{
					eachTreeTemp.add(4);
				}
				else if (str[i][j] == ']')
				{
					eachTreeTemp.add(5);
				}
				else
				{
					eachTreeTemp.add(0);
				}
				// log.debug(i+" "+str[i][j] +" "
				// +eachTreeTemp.get(j));
			} // end of j loop
			if (eachTreeTemp.contains(1))
			{
				FFyearperTree[i] = eachTreeTemp.indexOf(1);
				// log.debug("the last year of a cap is "+eachTreeTemp.lastIndexOf(1)
				// );
				lastFirePerSample[i] = eachTreeTemp.lastIndexOf(1);
			}
			else
			{
				FFyearperTree[i] = eachTreeTemp.indexOf(1);
				// FFyearperTree[i]=-1;
				lastFirePerSample[i] = eachTreeTemp.lastIndexOf(1);
			}
			if (eachTreeTemp.contains(6))
			{
				fIYearPerTree[i] = eachTreeTemp.indexOf(6);
				// );
				lastInjuryPerSample[i] = eachTreeTemp.lastIndexOf(6);
			}
			else
			{
				lastInjuryPerSample[i] = eachTreeTemp.lastIndexOf(6);
				fIYearPerTree[i] = eachTreeTemp.indexOf(6);
			}
			if (eachTreeTemp.contains(2))
			{
				pithIndexPerSample[i] = eachTreeTemp.indexOf(2);
			}
			else
			{
				pithIndexPerSample[i] = eachTreeTemp.indexOf(2);
				// FFyearperTree[i]=-1;
			}
			if (eachTreeTemp.contains(3))
			{
				innerMostPerTree[i] = eachTreeTemp.indexOf(3);
			}
			else
			{
				innerMostPerTree[i] = eachTreeTemp.indexOf(3);
				// FFyearperTree[i]=-1;
			}
			if (eachTreeTemp.contains(4))
			{
				outerMostPerTree[i] = eachTreeTemp.indexOf(4);
			}
			else
			{
				outerMostPerTree[i] = eachTreeTemp.indexOf(4);
				// FFyearperTree[i]=-1;
			}
			if (eachTreeTemp.contains(5))
			{
				barkPerTree[i] = eachTreeTemp.indexOf(5);
			}
			else
			{
				barkPerTree[i] = eachTreeTemp.indexOf(5);
				// FFyearperTree[i]=-1;
			}
			eachTreeTemp.clear();
			// log.debug(" tree "+ i
			// +"index of firstfire "+FFyearperTree[i]);
		} // end of i loop
			// end new stuff
		
		// Load each crossection (or tree) into the ArrayList variable eachTree
		for (int j = 0; j < numberOfSeries; j++)
		{
			eachTree = new ArrayList<Integer>();
			capstemp = new ArrayList<Character>();
			capsYtemp = new ArrayList<Integer>();
			lowstemp = new ArrayList<Character>();
			lowsYtemp = new ArrayList<Integer>();
			calostemp = new ArrayList<Character>();
			calosYtemp = new ArrayList<Integer>();
			in = -7;
			if (j == 0)
			{
				// log.debug("the FFyearperTree is : " + FFyearperTree[j]);
			}
			for (int i = 0; i < dataBlock.size(); i++)
			{
				// if(j==0){log.debug("charte is "+ str[j][i]);}
				if (i < FFyearperTree[j] || FFyearperTree[j] == -1)
				{
					in = -1;
					if (str[j][i] >= 'a' && str[j][i] <= 'z')
					{
						lowstemp.add(str[j][i]);
						lowsYtemp.add(i);
						calostemp.add(str[j][i]);
						calosYtemp.add(i);
					}
				}
				else
				{
					if (str[j][i] >= 'A' && str[j][i] <= 'Z')
					{
						in = 1;
						capstemp.add(str[j][i]);
						capsYtemp.add(i);
						calostemp.add(str[j][i]);
						calosYtemp.add(i);
					}
					else if (((str[j][i] >= 'a') && (str[j][i] <= 'z')) || str[j][i] == '|')
					{
						in = 0;
						// log.debug("it is a low " + i);
						if (str[j][i] >= 'a' && str[j][i] <= 'z')
						{
							lowstemp.add(str[j][i]);
							lowsYtemp.add(i);
							calostemp.add(str[j][i]);
							calosYtemp.add(i);
						}
					}
					else if (str[j][i] == '.')
						in = -1;
					else
						in = -1;
					
				}
				eachTree.add(in);
			} // end of i loop
			climate2dI.add(eachTree);
			capsperSample2d.add(capstemp);
			capsYearperSample2d.add(capsYtemp);
			lowsperSample2d.add(lowstemp);
			lowsYearperSample2d.add(lowsYtemp);
			calosperSample2d.add(calostemp);
			calosYearperSample2d.add(calosYtemp);
			// if(j==0){log.debug("caps #, Y"+capstemp+" "+ capsYtemp
			// +"lows #, Y"+ lowstemp+" "+ lowsYtemp
			// +"capls #, Y"+calostemp+" "+ calosYtemp);}
		} // end of j loop
		/*
		 * set the start year for each sample it is the pith date or the innermost yearset the Last year for each sample it is the bark or
		 * the outermost year
		 */
		for (int k = 0; k < numberOfSeries; k++)
		{
			if (pithIndexPerSample[k] != -1)
			{
				startYearIndexPerSample[k] = pithIndexPerSample[k];
			}
			else if (innerMostPerTree[k] != -1)
			{
				startYearIndexPerSample[k] = innerMostPerTree[k];
			}
			else if ((FFyearperTree[k] != -1) && (fIYearPerTree[k] != -1))
			{
				if (FFyearperTree[k] <= fIYearPerTree[k])
				{
					startYearIndexPerSample[k] = FFyearperTree[k];
				}
				else
				{
					startYearIndexPerSample[k] = fIYearPerTree[k];
				}
			}
			else
			{
				if ((FFyearperTree[k] != -1) && (fIYearPerTree[k] == -1))
				{
					startYearIndexPerSample[k] = FFyearperTree[k];
				}
				if ((FFyearperTree[k] == -1) && (fIYearPerTree[k] != -1))
				{
					startYearIndexPerSample[k] = fIYearPerTree[k];
				}
				if ((FFyearperTree[k] == -1) && (fIYearPerTree[k] == -1))
				{
					startYearIndexPerSample[k] = -1;
				}
			}
			// lastyear per sample
			if (barkPerTree[k] != -1)
			{
				lastYearIndexPerSample[k] = barkPerTree[k];
			}
			else if (outerMostPerTree[k] != -1)
			{
				lastYearIndexPerSample[k] = outerMostPerTree[k];
			}
			else if ((lastFirePerSample[k] != -1) && (lastInjuryPerSample[k] != -1))
			{
				if (lastFirePerSample[k] <= lastInjuryPerSample[k])
				{
					lastYearIndexPerSample[k] = lastInjuryPerSample[k];
				}
				else
				{
					lastYearIndexPerSample[k] = lastFirePerSample[k];
				}
			}
			else
			{
				if ((lastFirePerSample[k] != -1) && (lastInjuryPerSample[k] == -1))
				{
					lastYearIndexPerSample[k] = lastFirePerSample[k];
				}
				if ((lastFirePerSample[k] == -1) && (lastInjuryPerSample[k] != -1))
				{
					lastYearIndexPerSample[k] = lastInjuryPerSample[k];
				}
				if ((lastFirePerSample[k] == -1) && (lastInjuryPerSample[k] == -1))
				{
					lastYearIndexPerSample[k] = -1;
				}
				
			}
			// log.debug("sample "+k +" the start " +
			// startYearperSample[k]+ " the end " + lastYearperSample[k]);
		}
		
		// determineFirstFireYear2
		
		// firstFireYear = firstYear + climate2d.indexOf(1);
		ArrayList<Integer> ind = new ArrayList<Integer>();
		for (int j = 0; j < numberOfSeries; j++)
		{
			// log.debug("the climate2d.get.indexof(1) is: " +
			// climate2d.get(j).indexOf(1) );
			ind.add(climate2dI.get(j).indexOf(1));
		}
		Collections.sort(ind);
		
		// log.debug("DEBUG Element " + climate2d.get(k).indexOf(1));
		boolean breakme = true;
		for (int j = 0; j < numberOfSeries; j++)
		{
			if (ind.get(j) >= 0 && breakme)
			{
				firstFireYear = firstYear + ind.get(j);
				breakme = false;
			}
		}
		
		// log.debug("DEBUG Internal First Fire Year is" +
		// firstFireYear);
		isClimate2dIinit = true;
	}
	
	/**
	 * Generate multi-dimensional ArrayList of recorder years. A recorder year is where the sample actually recorded an event or a year in
	 * which it was capable of recording an event if one was present
	 */
	private void generateRecorderYearsArray(EventTypeToProcess eventTypeToProcess) {
	
		if (this.isClimate2dIinit == false)
			this.generate2DEventsI();
		
		char Achar;
		char Zchar;
		char AcharInverse;
		char ZcharInverse;
		
		if (eventTypeToProcess.equals(EventTypeToProcess.INJURY_EVENT))
		{
			Achar = 'a';
			Zchar = 'z';
			AcharInverse = 'A';
			ZcharInverse = 'Z';
		}
		else if (eventTypeToProcess.equals(EventTypeToProcess.FIRE_EVENT)
				|| eventTypeToProcess.equals(EventTypeToProcess.FIRE_AND_INJURY_EVENT))
		{
			Achar = 'A';
			Zchar = 'Z';
			AcharInverse = 'a';
			ZcharInverse = 'z';
		}
		else
		{
			log.error("Unsupported EventTypeToProcess");
			return;
			
		}
		
		// instantiate the recorder array list
		ArrayList<ArrayList<Integer>> myRecorderYears2DArray = new ArrayList<ArrayList<Integer>>();
		
		// declare local variables
		char[][] str = new char[numberOfSeries][dataBlock.size()];
		String ts;
		ArrayList<Integer> tempRecorderYearsForSample;
		boolean charFlagFound = false;
		char previousCharToCompare = ' ';
		
		totalRecordYearsPerSample = new int[numberOfSeries];
		
		// Load the data of the fhx file into the str[j][i]
		for (int i = 0; i < dataBlock.size(); i++)
		{
			ts = dataBlock.get(i);
			for (int j = 0; j < ts.length(); j++)
			{
				str[j][i] = ts.charAt(j);
			}
		}
		
		// Loop through all series with index i
		for (int i = 0; i < numberOfSeries; i++)
		{
			tempRecorderYearsForSample = new ArrayList<Integer>();
			totalRecordYearsPerSample[i] = 0;
			
			// Loop through years with index j
			for (int j = 0; j < dataBlock.size(); j++)
			{
				// Only check years which fall in the range of the current
				// sample
				if ((startYearIndexPerSample[i] <= j) && (lastYearIndexPerSample[i] >= j))
				{
					char theCurrentValue = str[i][j];
					
					// All the simple cases first...
					if (theCurrentValue == '|')
					{
						// Data file explicitly defines that the sample was
						// recording
						tempRecorderYearsForSample.add(1);
						totalRecordYearsPerSample[i] = totalRecordYearsPerSample[i] + 1;
					}
					else if (theCurrentValue >= Achar && theCurrentValue <= Zchar)
					{
						// Fire event was found so sample must have been
						// recording
						tempRecorderYearsForSample.add(1);
						totalRecordYearsPerSample[i] = totalRecordYearsPerSample[i] + 1;
					}
					else if (theCurrentValue == '.')
					{
						// Data file explicitly defines that the sample was not
						// recording
						tempRecorderYearsForSample.add(0);
					}
					else if (theCurrentValue == '[')
					{
						// go to the next character if it is a fire event or | then recorder
						// if it is a injury event or . then no recorder
						int l = j;
						if (((str[i][l + 1] == '|') || (str[i][l + 1] >= Achar && str[i][l + 1] <= Zchar))
								&& ((l + 1) <= lastYearIndexPerSample[i]))
						{
							tempRecorderYearsForSample.add(1);
							totalRecordYearsPerSample[i] = totalRecordYearsPerSample[i] + 1;
						}
						else
						{
							tempRecorderYearsForSample.add(0);
						}
					}
					// If first year of sample has no pith then
					// more fun......
					else if (theCurrentValue == '{')
					{
						// If the year following the first year { bracket is a
						// recorder year, or
						// and fire scar than count start bracket as recorder,
						// otherwise don't.
						int n = j;
						if (((str[i][n + 1] == '|') || (str[i][n + 1] >= Achar && str[i][n + 1] <= Zchar))
								&& ((n + 1) <= lastYearIndexPerSample[i]))
						{
							tempRecorderYearsForSample.add(1);
							totalRecordYearsPerSample[i] = totalRecordYearsPerSample[i] + 1;
						}
						else
						{
							tempRecorderYearsForSample.add(0);
						}
					}
					
					// Also complicated for the last year in sample and for
					// injuries
					else if ((theCurrentValue >= AcharInverse && theCurrentValue <= ZcharInverse))
					{
						// first look at the year following the injury
						int ln = j;
						if (((str[i][ln + 1] == '|') || (str[i][ln + 1] >= Achar && str[i][ln + 1] <= Zchar))
								&& ((ln + 1) <= lastYearIndexPerSample[i]))
						{
							tempRecorderYearsForSample.add(1);
							totalRecordYearsPerSample[i] = totalRecordYearsPerSample[i] + 1;
						}
						else
						{
							// Step backwards until we find a . | or fire event
							// int m = j;
							charFlagFound = false;
							for (int k = ln - 1; k >= startYearIndexPerSample[i]; k--)
							{
								// log.debug("k is: "+ k);
								if ((str[i][k] == '.') || (str[i][k] == '|') || (str[i][k] >= Achar && str[i][k] <= Zchar)
										|| (str[i][k] == '{') || (str[i][k] == '['))
								{
									charFlagFound = true;
									previousCharToCompare = str[i][k];
									// log.debug("at k: "+ k+
									// " we hit the charcther "+
									// str[i][k]+" m is "+m);
									break;
								}
							}
							if ((previousCharToCompare == '.') || (previousCharToCompare == '{') || (previousCharToCompare == '['))
							{
								// Previous year was an explicit non-recorder year
								// so count this year as a non-recorder too
								tempRecorderYearsForSample.add(0);
							}
							else if ((previousCharToCompare == '|') || (previousCharToCompare >= Achar && previousCharToCompare <= Zchar))
							{
								// Previous year was an explicit recorder year or
								// event so count this year as recorder too
								tempRecorderYearsForSample.add(1);
								totalRecordYearsPerSample[i] = totalRecordYearsPerSample[i] + 1;
							}
							else if (charFlagFound == false)
							{
								tempRecorderYearsForSample.add(0);
							}
							else
							{
								log.error("Error calculating recorder year matrix.  Should never reach this code!");
							}
						}
					} // adding separate case for } ]. in this case we step backwards (only) until we find a . | or fire event
					else if ((theCurrentValue == '}') || (theCurrentValue == ']'))
					{
						// Step backwards until we find a . | or fire event
						int m = j;
						charFlagFound = false;
						for (int k = m - 1; k >= startYearIndexPerSample[i]; k--)
						{
							// log.debug("k is: "+ k);
							if ((str[i][k] == '.') || (str[i][k] == '|') || (str[i][k] >= Achar && str[i][k] <= Zchar)
									|| (str[i][k] == '{') || (str[i][k] == '['))
							{
								charFlagFound = true;
								previousCharToCompare = str[i][k];
								// log.debug("at k: "+ k+
								// " we hit the charcther "+
								// str[i][k]+" m is "+m);
								break;
							}
						}
						if ((previousCharToCompare == '.') || (previousCharToCompare == '{') || (previousCharToCompare == '['))
						{
							// Previous year was an explicit non-recorder year
							// so count this year as a non-recorder too
							tempRecorderYearsForSample.add(0);
						}
						else if ((previousCharToCompare == '|') || (previousCharToCompare >= Achar && previousCharToCompare <= Zchar))
						{
							// Previous year was an explicit recorder year or
							// event so count this year as recorder too
							tempRecorderYearsForSample.add(1);
							totalRecordYearsPerSample[i] = totalRecordYearsPerSample[i] + 1;
						}
						else if (charFlagFound == false)
						{
							tempRecorderYearsForSample.add(0);
						}
						else
						{
							log.error("Error calculating recorder year matrix.  Should never reach this code!");
						}
					} // end of new stuff
				} // end first if
				else
				{
					// Current year outside of the range of this sample so mark
					// with -1
					tempRecorderYearsForSample.add(-1);
				}
			}
			
			Integer previousSize = null;
			
			if (myRecorderYears2DArray != null && myRecorderYears2DArray.size() > 0)
			{
				previousSize = myRecorderYears2DArray.get(0).size();
			}
			
			if (previousSize != null)
			{
				if (previousSize != tempRecorderYearsForSample.size())
				{
					log.error("Size mismatch: " + previousSize + " v. " + tempRecorderYearsForSample.size());
					
				}
			}
			
			myRecorderYears2DArray.add(tempRecorderYearsForSample);
			
			// + "x" +
			// recorder2d.get(i).size()+" the sum of rec "+totalrecYearsperSample[i]);
		} // end of i loop
			// log.debug("DEBUG: SIZE recorder2d " + recorder2d.size()
			// + "x" + recorder2d.get(2).size());
		
		if (eventTypeToProcess.equals(EventTypeToProcess.INJURY_EVENT))
		{
			recorderYears2DArrayII = myRecorderYears2DArray;
			isRecorderYears2DArrayInitII = true;
		}
		else if (eventTypeToProcess.equals(EventTypeToProcess.FIRE_EVENT)
				|| eventTypeToProcess.equals(EventTypeToProcess.FIRE_AND_INJURY_EVENT))
		{
			recorderYears2DArray = myRecorderYears2DArray;
			isRecorderYears2DArrayInit = true;
		}
		else
		{
			log.error("Unsupported EventTypeToProcess");
			return;
			
		}
		
	}
	
	//
	// NEW Recorder II
	//
	/**
	 * Generate multi-dimensional ArrayList of recorder years. A recorder year is where the sample actually recorded an event or a year in
	 * which it was capable of recording an event if one was present
	 */
	public void generateRecorderYearsArrayII() {
	
		if (this.isClimate2dIinit == false)
			this.generate2DEventsI();
		
		// instantiate the recorder array list
		recorderYears2DArrayII = new ArrayList<ArrayList<Integer>>();
		
		// declare local variables
		char[][] str = new char[numberOfSeries][dataBlock.size()];
		String ts;
		ArrayList<Integer> tempRecorderYearsForSampleII;
		boolean charFlagFound = false;
		char previousCharToCompare = ' ';
		
		totalRecordYearsPerSampleII = new int[numberOfSeries];
		
		// Load the data of the fhx file into the str[j][i]
		for (int i = 0; i < dataBlock.size(); i++)
		{
			ts = dataBlock.get(i);
			for (int j = 0; j < ts.length(); j++)
			{
				str[j][i] = ts.charAt(j);
			}
		}
		
		// Loop through all series with index i
		for (int i = 0; i < numberOfSeries; i++)
		{
			tempRecorderYearsForSampleII = new ArrayList<Integer>();
			totalRecordYearsPerSampleII[i] = 0;
			
			// Loop through years with index j
			for (int j = 0; j < dataBlock.size(); j++)
			{
				// Only check years which fall in the range of the current
				// sample
				if ((startYearIndexPerSample[i] <= j) && (lastYearIndexPerSample[i] >= j))
				{
					char theCurrentValue = str[i][j];
					
					// All the simple cases first...
					if (theCurrentValue == '|')
					{
						// Data file explicitly defines that the sample was
						// recording
						tempRecorderYearsForSampleII.add(1);
						totalRecordYearsPerSampleII[i] = totalRecordYearsPerSampleII[i] + 1;
					}
					else if (theCurrentValue >= 'a' && theCurrentValue <= 'z')
					{
						// injury event was found so sample must have been
						// recording
						tempRecorderYearsForSampleII.add(1);
						totalRecordYearsPerSampleII[i] = totalRecordYearsPerSampleII[i] + 1;
					}
					else if (theCurrentValue == '.')
					{
						// Data file explicitly defines that the sample was not
						// recording
						tempRecorderYearsForSampleII.add(0);
					}
					else if (theCurrentValue == '[')
					{
						// go to the next character if it is a injury event or | then recorder
						// if it is a fire event or . then no recorder
						int l = j;
						if (((str[i][l + 1] == '|') || (str[i][l + 1] >= 'a' && str[i][l + 1] <= 'z'))
								&& ((l + 1) <= lastYearIndexPerSample[i]))
						{
							tempRecorderYearsForSampleII.add(1);
							totalRecordYearsPerSampleII[i] = totalRecordYearsPerSampleII[i] + 1;
						}
						else
						{
							tempRecorderYearsForSampleII.add(0);
						}
					}
					// If first year of sample has no pith then
					// more fun......
					else if (theCurrentValue == '{')
					{
						// If the year following the first year { bracket is a
						// recorder year, or
						// and injury scar than count start bracket as recorder,
						// otherwise don't.
						int n = j;
						if (((str[i][n + 1] == '|') || (str[i][n + 1] >= 'a' && str[i][n + 1] <= 'z'))
								&& ((n + 1) <= lastYearIndexPerSample[i]))
						{
							tempRecorderYearsForSampleII.add(1);
							totalRecordYearsPerSampleII[i] = totalRecordYearsPerSampleII[i] + 1;
						}
						else
						{
							tempRecorderYearsForSampleII.add(0);
						}
					}
					
					// Also complicated for the last year in sample and for
					// fires
					else if ((theCurrentValue >= 'A' && theCurrentValue <= 'Z'))
					{
						// first look at the year following the injury
						int ln = j;
						if (((str[i][ln + 1] == '|') || (str[i][ln + 1] >= 'a' && str[i][ln + 1] <= 'z'))
								&& ((ln + 1) <= lastYearIndexPerSample[i]))
						{
							tempRecorderYearsForSampleII.add(1);
							totalRecordYearsPerSampleII[i] = totalRecordYearsPerSampleII[i] + 1;
						}
						else
						{
							// Step backwards until we find a . | or injury event
							// int m = j;
							charFlagFound = false;
							for (int k = ln - 1; k >= startYearIndexPerSample[i]; k--)
							{
								// log.debug("k is: "+ k);
								if ((str[i][k] == '.') || (str[i][k] == '|') || (str[i][k] >= 'a' && str[i][k] <= 'z')
										|| (str[i][k] == '{') || (str[i][k] == '['))
								{
									charFlagFound = true;
									previousCharToCompare = str[i][k];
									// log.debug("at k: "+ k+
									// " we hit the charcther "+
									// str[i][k]+" m is "+m);
									break;
								}
							}
							if ((previousCharToCompare == '.') || (previousCharToCompare == '{') || (previousCharToCompare == '['))
							{
								// Previous year was an explicit non-recorder year
								// so count this year as a non-recorder too
								tempRecorderYearsForSampleII.add(0);
							}
							else if ((previousCharToCompare == '|') || (previousCharToCompare >= 'a' && previousCharToCompare <= 'z'))
							{
								// Previous year was an explicit recorder year or
								// event so count this year as recorder too
								tempRecorderYearsForSampleII.add(1);
								totalRecordYearsPerSampleII[i] = totalRecordYearsPerSampleII[i] + 1;
							}
							else if (charFlagFound == false)
							{
								tempRecorderYearsForSampleII.add(0);
							}
							else
							{
								log.error("Error calculating recorder year matrix.  Should never reach this code!");
							}
						}
					} // adding separate case for } ]. in this case we step backwards (only) until we find a . | or fire event
					else if ((theCurrentValue == '}') || (theCurrentValue == ']'))
					{
						// Step backwards until we find a . | or injury event
						int m = j;
						charFlagFound = false;
						for (int k = m - 1; k >= startYearIndexPerSample[i]; k--)
						{
							// log.debug("k is: "+ k);
							if ((str[i][k] == '.') || (str[i][k] == '|') || (str[i][k] >= 'a' && str[i][k] <= 'z') || (str[i][k] == '{')
									|| (str[i][k] == '['))
							{
								charFlagFound = true;
								previousCharToCompare = str[i][k];
								// log.debug("at k: "+ k+
								// " we hit the charcther "+
								// str[i][k]+" m is "+m);
								break;
							}
						}
						if ((previousCharToCompare == '.') || (previousCharToCompare == '{') || (previousCharToCompare == '['))
						{
							// Previous year was an explicit non-recorder year
							// so count this year as a non-recorder too
							tempRecorderYearsForSampleII.add(0);
						}
						else if ((previousCharToCompare == '|') || (previousCharToCompare >= 'a' && previousCharToCompare <= 'z'))
						{
							// Previous year was an explicit recorder year or
							// event so count this year as recorder too
							tempRecorderYearsForSampleII.add(1);
							totalRecordYearsPerSampleII[i] = totalRecordYearsPerSampleII[i] + 1;
						}
						else if (charFlagFound == false)
						{
							tempRecorderYearsForSampleII.add(0);
						}
						else
						{
							log.error("Error calculating recorder year matrix.  Should never reach this code!");
						}
					} // end of new stuff
				} // end first if
				else
				{
					// Current year outside of the range of this sample so mark
					// with -1
					tempRecorderYearsForSampleII.add(-1);
				}
			}
			
			Integer previousSize = null;
			
			if (recorderYears2DArrayII != null && recorderYears2DArrayII.size() > 0)
			{
				previousSize = recorderYears2DArrayII.get(0).size();
			}
			
			if (previousSize != null)
			{
				if (previousSize != tempRecorderYearsForSampleII.size())
				{
					log.error("Size mismatch: " + previousSize + " v. " + tempRecorderYearsForSampleII.size());
					
				}
			}
			
			recorderYears2DArrayII.add(tempRecorderYearsForSampleII);
			
			// + "x" +
			// recorder2d.get(i).size()+" the sum of rec "+totalrecYearsperSampleII[i]);
		} // end of i loop
			// log.debug("DEBUG: SIZE recorder2d " + recorder2d.size()
			// + "x" + recorder2d.get(2).size());
		
		isRecorderYears2DArrayInitII = true;
	}
	
	//
	// End of recoder II
	//
	/**
	 * Generates the basic injuries arrays including climate2dII and fIIYearPerTree
	 */
	private void generate2DEventsII() {
	
		// instantiate the climate array list
		climate2dII = new ArrayList<ArrayList<Integer>>();
		// declare local variables
		char[][] strII = new char[numberOfSeries][dataBlock.size()];
		String tsII;
		ArrayList<Integer> eachTreeII;
		ArrayList<Integer> eachTreeTempII;
		Integer inII;
		// log.debug("I am in make climate2dII injury");
		// int FirstFireFlag[];
		
		// FirstFireFlag = new int[numberOfSeries];
		fIIYearPerTree = new int[numberOfSeries];
		
		// Load the data of the fhx file into the strII[j][i]
		// strII is of size
		for (int i = 0; i < dataBlock.size(); i++)
		{
			tsII = dataBlock.get(i);
			for (int j = 0; j < tsII.length(); j++)
			{
				strII[j][i] = tsII.charAt(j);
			} // end of j loop
		} // end of i loop load
		
		// /new stuff from elena
		for (int i = 0; i < numberOfSeries; i++)
		{
			eachTreeTempII = new ArrayList<Integer>();
			for (int j = 0; j < dataBlock.size(); j++)
			{
				if (strII[i][j] >= 'a' && strII[i][j] <= 'z')
				{
					eachTreeTempII.add(1);
				}
				else
				{
					eachTreeTempII.add(0);
				}
				// log.debug(i+" "+str[i][j] +" "
				// +eachTreeTemp.get(j));
			} // end of j loop
			if (eachTreeTempII.contains(1))
			{
				fIIYearPerTree[i] = eachTreeTempII.indexOf(1);
			}
			else
			{
				fIIYearPerTree[i] = eachTreeTempII.indexOf(1);
				// FFyearperTree[i]=-1;
			}
			eachTreeTempII.clear();
			
		} // end of i loop
		
		// Load each crossection (or tree) into the ArrayList variable eachTree
		for (int j = 0; j < numberOfSeries; j++)
		{
			eachTreeII = new ArrayList<Integer>();
			inII = -7;
			for (int i = 0; i < dataBlock.size(); i++)
			{
				if (i < fIIYearPerTree[j] || fIIYearPerTree[j] == -1)
					inII = -1;
				else
				{
					if (strII[j][i] >= 'a' && strII[j][i] <= 'z')
						inII = 1;
					else if ((strII[j][i] >= 'A' && strII[j][i] <= 'Z') || strII[j][i] == '|')
						inII = 0;
					else if (strII[j][i] == '.')
						inII = -1;
					else
						inII = -1;
					
				}
				eachTreeII.add(inII);
			} // end of i loop
			climate2dII.add(eachTreeII);
		} // end of j loop
			// log.debug("DEBUG: SIZE climate2d " + climate2dII.size()
			// + "x" + climate2dII.get(0).size());
			// determineFirstInjuryYear2
		
		// firstFireYear = firstYear + climate2d.indexOf(1);
		ArrayList<Integer> indII = new ArrayList<Integer>();
		for (int j = 0; j < numberOfSeries; j++)
		{
			// log.debug("the climate2dII.get.indexof(1) is: " +
			// climate2dII.get(j).indexOf(1) );
			indII.add(climate2dII.get(j).indexOf(1));
		}
		Collections.sort(indII);
		// for (int k=0; k<numberOfSeries; k++)
		// log.debug("DEBUG Element " + climate2dII.get(k).indexOf(1));
		boolean breakme = true;
		for (int j = 0; j < numberOfSeries; j++)
		{
			if (indII.get(j) >= 0 && breakme)
			{
				firstInjuryYear = firstYear + indII.get(j);
				breakme = false;
			}
		}
		
		isClimate2dIinit = true;
	}
	
	/**
	 * Generates the filters2d array
	 */
	private void generate2DFiltersI() {
	
		if (this.isClimate2dIinit == false)
			this.generate2DEventsI();
		
		// instantiate the climate array list
		filters2dI = new ArrayList<ArrayList<Double>>();
		// declare local variables
		char[][] strfilters = new char[numberOfSeries][dataBlock.size()];
		String tsfilters;
		ArrayList<Double> filtercaps;
		ArrayList<Double> filtertrees;
		ArrayList<Double> filterprotrees;
		double[] filters = new double[3];
		double TotalBars;
		double TotalLowerCase;
		double TotalTrees;
		int[] datayears = new int[dataBlock.size()];
		
		filtercaps = new ArrayList<Double>();
		filtertrees = new ArrayList<Double>();
		filterprotrees = new ArrayList<Double>();
		
		// Load the data of the fhx file into the str[j][i]
		for (int i = 0; i < dataBlock.size(); i++)
		{
			tsfilters = dataBlock.get(i);
			datayears[i] = yearArray.get(i);
			
			for (int j = 0; j < tsfilters.length(); j++)
			{
				strfilters[j][i] = tsfilters.charAt(j);
			}
			
			// Load each cross-section (or tree) into the ArrayList variable
			// eachTree
			
			filters[0] = 0;
			filters[1] = 0;
			filters[2] = 0;
			TotalBars = 0;
			TotalLowerCase = 0;
			TotalTrees = 0;
			
			for (int j = 0; j < numberOfSeries; j++)
			{
				if (strfilters[j][i] >= 'A' && strfilters[j][i] <= 'Z')
					filters[0] = filters[0] + 1;
				if (strfilters[j][i] >= 'a' && strfilters[j][i] <= 'z')
					TotalLowerCase = TotalLowerCase + 1;
				if (strfilters[j][i] == '|')
					TotalBars = TotalBars + 1;
				if ((FFyearperTree[j] != -1) && (yearArray.get(i) >= yearArray.get(FFyearperTree[j]))
						&& (TotalLowerCase >= 1 || TotalBars >= 1 || filters[0] >= 1))
					TotalTrees = TotalTrees + 1;
			} // end j loop
			filtercaps.add(filters[0]);
			filters[1] = TotalTrees;
			filtertrees.add(filters[1]);
			if (filters[1] > 0.0)
				filters[2] = filters[0] / filters[1];
			else
				filters[2] = -99;
			filterprotrees.add(filters[2]);
			
		} // end i loop
		filters2dI.add(filtercaps);
		filters2dI.add(filtertrees);
		filters2dI.add(filterprotrees);
		
		this.isFilterse2dIinit = true;
	}
	
	/**
	 * Generate the filters2dII array
	 */
	private void generate2DFiltersII() {
	
		if (this.isClimate2dIinit == false)
			this.generate2DEventsI();
		if (this.isClimate2dIIinit == false)
			this.generate2DEventsII();
		
		// instantiate the climate array list
		filters2dII = new ArrayList<ArrayList<Double>>();
		// declare local variables
		char[][] strfiltersII = new char[numberOfSeries][dataBlock.size()];
		String tsfiltersII;
		ArrayList<Double> filterlowsII;
		ArrayList<Double> filtertreesII;
		ArrayList<Double> filterprotreesII;
		double[] filtersII = new double[3];
		double TotalBarsII;
		double TotalUpperCaseII;
		double TotalTreesII;
		int[] datayearsII = new int[dataBlock.size()];
		
		filterlowsII = new ArrayList<Double>();
		filtertreesII = new ArrayList<Double>();
		filterprotreesII = new ArrayList<Double>();
		
		// Load the data of the fhx file into the str[j][i]
		for (int i = 0; i < dataBlock.size(); i++)
		{
			tsfiltersII = dataBlock.get(i);
			datayearsII[i] = yearArray.get(i);
			// log.debug(" data "+i+" "+year.get(i) + " "
			// +data.get(i)+" ");
			for (int j = 0; j < tsfiltersII.length(); j++)
			{
				strfiltersII[j][i] = tsfiltersII.charAt(j);
			}
			
			// Load each sample into the ArrayList variable eachTree
			
			filtersII[0] = 0;
			filtersII[1] = 0;
			filtersII[2] = 0;
			TotalBarsII = 0;
			TotalUpperCaseII = 0;
			TotalTreesII = 0;
			
			for (int j = 0; j < numberOfSeries; j++)
			{
				
				if (strfiltersII[j][i] >= 'a' && strfiltersII[j][i] <= 'z')
					filtersII[0] = filtersII[0] + 1;
				if (strfiltersII[j][i] >= 'A' && strfiltersII[j][i] <= 'Z')
					TotalUpperCaseII = TotalUpperCaseII + 1;
				if (strfiltersII[j][i] == '|')
					TotalBarsII = TotalBarsII + 1;
				if ((fIIYearPerTree[j] != -1) && (yearArray.get(i) >= yearArray.get(fIIYearPerTree[j]))
						&& (TotalUpperCaseII >= 1 || TotalBarsII >= 1 || filtersII[0] >= 1))
					TotalTreesII = TotalTreesII + 1;
			} // end j loop
			filterlowsII.add(filtersII[0]);
			filtersII[1] = TotalTreesII;
			filtertreesII.add(filtersII[1]);
			if (filtersII[1] > 0.0)
				filtersII[2] = filtersII[0] / filtersII[1];
			else
				filtersII[2] = -99;
			filterprotreesII.add(filtersII[2]);
			
			// log.debug(filtercapsII.get(i)+" "+filtertreesII.get(i)+
			// " "+ filterprotreesII.get(i));
			
		} // end i loop
		filters2dII.add(filterlowsII);
		filters2dII.add(filtertreesII);
		filters2dII.add(filterprotreesII);
		// log.debug("DEBUG: SIZE Filtersed " + filters2d.size() + "x"
		// + filters2d.get(0).size());
		
		// determineFirstInjuryYear2();
		this.isFilterse2dIIinit = true;
		
	}
	
	/**
	 * Generates the filters2dIII array
	 */
	private void generate2DFiltersIII() {
	
		if (this.isClimate2dIinit == false)
			this.generate2DEventsI();
		if (this.isClimate2dIIIinit == false)
			this.generate2DEventsIII();
		
		// instantiate the climate array list
		filters2dIII = new ArrayList<ArrayList<Double>>();
		// declare local variables
		char[][] strfiltersIII = new char[numberOfSeries][dataBlock.size()];
		String tsfiltersIII;
		ArrayList<Double> filterIndicatorsIII;
		ArrayList<Double> filtertreesIII;
		ArrayList<Double> filterprotreesIII;
		double[] filtersIII = new double[3];
		double TotalBarsIII;
		double TotalIndicatorsIII;
		double TotalTreesIII;
		int[] datayearsIII = new int[dataBlock.size()];
		
		filterIndicatorsIII = new ArrayList<Double>();
		filtertreesIII = new ArrayList<Double>();
		filterprotreesIII = new ArrayList<Double>();
		
		// Load the data of the fhx file into the str[j][i]
		for (int i = 0; i < dataBlock.size(); i++)
		{
			tsfiltersIII = dataBlock.get(i);
			datayearsIII[i] = yearArray.get(i);
			// log.debug(" data "+i+" "+year.get(i) + " "
			// +data.get(i)+" ");
			for (int j = 0; j < tsfiltersIII.length(); j++)
			{
				strfiltersIII[j][i] = tsfiltersIII.charAt(j);
			}
			
			// Load each sample into the ArrayList variable eachTree
			
			filtersIII[0] = 0;
			filtersIII[1] = 0;
			filtersIII[2] = 0;
			TotalBarsIII = 0;
			TotalIndicatorsIII = 0;
			TotalTreesIII = 0;
			
			for (int j = 0; j < numberOfSeries; j++)
			{
				
				if ((strfiltersIII[j][i] >= 'a' && strfiltersIII[j][i] <= 'z')
						|| (strfiltersIII[j][i] >= 'A' && strfiltersIII[j][i] <= 'Z'))
					filtersIII[0] = filtersIII[0] + 1;
				if ((strfiltersIII[j][i] >= 'A' && strfiltersIII[j][i] <= 'Z')
						|| (strfiltersIII[j][i] >= 'a' && strfiltersIII[j][i] <= 'z'))
					TotalIndicatorsIII = TotalIndicatorsIII + 1;
				if (strfiltersIII[j][i] == '|')
					TotalBarsIII = TotalBarsIII + 1;
				if ((fIIIYearPerTree[j] != -1) && (yearArray.get(i) >= yearArray.get(fIIIYearPerTree[j]))
						&& (TotalIndicatorsIII >= 1 || TotalBarsIII >= 1 || filtersIII[0] >= 1))
					TotalTreesIII = TotalTreesIII + 1;
			} // end j loop
			filterIndicatorsIII.add(filtersIII[0]);
			filtersIII[1] = TotalTreesIII;
			filtertreesIII.add(filtersIII[1]);
			if (filtersIII[1] > 0.0)
				filtersIII[2] = filtersIII[0] / filtersIII[1];
			else
				filtersIII[2] = -99;
			filterprotreesIII.add(filtersIII[2]);
			
			// log.debug(filtercapsII.get(i)+" "+filtertreesII.get(i)+
			// " "+ filterprotreesII.get(i));
			
		} // end i loop
		filters2dIII.add(filterIndicatorsIII);
		filters2dIII.add(filtertreesIII);
		filters2dIII.add(filterprotreesIII);
		// log.debug("DEBUG: SIZE Filtersed " + filters2d.size() + "x"
		// + filters2d.get(0).size());
		
		// determineFirstIndicatorYear2();
		this.isFilterse2dIIIinit = true;
		
	}
	
	/**
	 * TODO Documentation needed
	 * 
	 */
	@Override
	public void makeDecompSyb2d() {
	
		// instantiate the DecompSyb ArrayList
		
		DecompSyb2d = new ArrayList<ArrayList<Integer>>();
		ArrayList<Integer> DecompSybtemp;
		// declare local variables
		
		String tsfilters;
		/*
		 * totals = { tcaps,tlows,tD,tE,tM,tL,tA,tU,td, te,tm, tl, ta, tu ,t[]{} } filters = { caps, lows, D, E, M, L, A, U, d, e, m, l, a,
		 * u, [, ], {, }, |, . }
		 */
		int[] filters = new int[20];
		totals = new int[15];
		
		log.debug("I am in make DecompSyb");
		totals[0] = 0;
		totals[1] = 0;
		totals[2] = 0;
		totals[3] = 0;
		totals[4] = 0;
		totals[5] = 0;
		totals[6] = 0;
		totals[7] = 0;
		totals[8] = 0;
		totals[9] = 0;
		totals[10] = 0;
		totals[11] = 0;
		totals[12] = 0;
		totals[13] = 0;
		totals[14] = 0;
		for (int i = 0; i < dataBlock.size(); i++)
		{
			tsfilters = dataBlock.get(i);
			DecompSybtemp = new ArrayList<Integer>();
			
			// Load each crossection (or tree) into the ArrayList variable
			// eachTree
			
			filters[0] = 0;
			filters[1] = 0;
			filters[2] = 0;
			filters[3] = 0;
			filters[4] = 0;
			filters[5] = 0;
			filters[6] = 0;
			filters[7] = 0;
			filters[8] = 0;
			filters[9] = 0;
			filters[10] = 0;
			filters[11] = 0;
			filters[12] = 0;
			filters[13] = 0;
			filters[14] = 0;
			filters[15] = 0;
			filters[16] = 0;
			filters[17] = 0;
			filters[18] = 0;
			filters[19] = 0;
			for (int j = 0; j < tsfilters.length(); j++)
			{
				char c = tsfilters.charAt(j);
				// log.debug("I am in and the makeDecompSyb per tree is "+FFyearperTree[j]);
				if (c >= 'A' && c <= 'Z')
					filters[0] = filters[0] + 1;
				if (c >= 'a' && c <= 'z')
					filters[1] = filters[1] + 1;
				if (c == 'D')
					filters[2] = filters[2] + 1;
				if (c == 'E')
					filters[3] = filters[3] + 1;
				if (c == 'M')
					filters[4] = filters[4] + 1;
				if (c == 'L')
					filters[5] = filters[5] + 1;
				if (c == 'A')
					filters[6] = filters[6] + 1;
				if (c == 'U')
					filters[7] = filters[7] + 1;
				if (c == 'd')
					filters[8] = filters[8] + 1;
				if (c == 'e')
					filters[9] = filters[9] + 1;
				if (c == 'm')
					filters[10] = filters[10] + 1;
				if (c == 'l')
					filters[11] = filters[11] + 1;
				if (c == 'a')
					filters[12] = filters[12] + 1;
				if (c == 'u')
					filters[13] = filters[13] + 1;
				if (c == '[')
					filters[14] = filters[14] + 1;
				if (c == ']')
					filters[15] = filters[15] + 1;
				if (c == '{')
					filters[16] = filters[16] + 1;
				if (c == '}')
					filters[17] = filters[17] + 1;
				if (c == '|')
					filters[18] = filters[18] + 1;
				if (c == '.')
					filters[19] = filters[19] + 1;
			} // end j loop
			for (int k = 0; k < 14; k++)
			{
				totals[k] = totals[k] + filters[k];
			}
			
			totals[14] = totals[14] + filters[14] + filters[15] + filters[16] + filters[17];
			for (int ik = 0; ik < 5; ik++)
			{
				DecompSybtemp.add(totals[ik + 2]);
			}
			DecompSyb2d.add(DecompSybtemp);
		} // end i loop
			// for(int k=0;k<9;k++){
			// log.debug("DEBUG: SIZE totals " + totals.length
			// +" number of totals is "+ totals[k]);
			// }
		
		// log.debug("DEBUG: SIZE DecompSyb " + DecompSyb.size() + "x"
		// + DecompSyb.get(0).size());
		// for(int k=0;k<DecompSyb.get(0).size();k++){
		// log.debug("DEBUG: the value of capitals at k is " + k
		// +" value "+ DecompSyb.get(0).get(k));
		// }
		// log.debug("DEBUG: SIZE DecompSyb2d " + DecompSyb2d.size() +
		// "x" + DecompSyb2d.get(0).size());
		
		// end Decompsyb
		// endDecomp
		// new determnine First injury year
	}
	
	/**
	 * Generate the firesAndInjuriesArray
	 */
	private void generate1DEventsIII() {
	
		// instantiate the climate array list
		climate1dIII = new ArrayList<Integer>();
		// declare local variables
		String strIII;
		Integer inIII;
		// log.debug("I am in make climate");
		
		for (int i = 0; i < dataBlock.size(); i++)
		{
			inIII = -7;
			strIII = dataBlock.get(i);
			for (int j = 0; j < strIII.length(); j++)
			{
				if ((strIII.charAt(j) >= 'A' && strIII.charAt(j) <= 'Z') || (strIII.charAt(j) >= 'a' && strIII.charAt(j) <= 'z'))
				{
					inIII = 1;
					// Ahsan changed in != 1 to in == 1
				}
				else if (inIII != 1 && (strIII.charAt(j) == '|'))
				{
					inIII = 0;
				}
				else if (inIII != 0 && inIII != 1)
				// (in != 0 && in != 1)
				{
					inIII = -1;
				}
				// eelog.debug(str.charAt(j) + "\t" + in.toString());
			}
			// eelog.debug(str + '\t' + in);
			climate1dIII.add(inIII);
		}
		isClimate1dIIIinit = true;
		firstIndicatorYear = firstYear + climate1dIII.indexOf(1);
	}
	
	/**
	 * Generates the basic fire events and injuries arrays including climate2dIII
	 */
	private void generate2DEventsIII() {
	
		// instantiate the climate array list
		climate2dIII = new ArrayList<ArrayList<Integer>>();
		// declare local variables
		char[][] strIII = new char[numberOfSeries][dataBlock.size()];
		String tsIII;
		ArrayList<Integer> eachTreeIII;
		ArrayList<Integer> eachTreeTempIII;
		Integer inIII;
		// log.debug("I am in make climate2dII injury");
		
		fIIIYearPerTree = new int[numberOfSeries];
		
		// Load the data of the fhx file into the strIII[j][i]
		for (int i = 0; i < dataBlock.size(); i++)
		{
			tsIII = dataBlock.get(i);
			for (int j = 0; j < tsIII.length(); j++)
			{
				strIII[j][i] = tsIII.charAt(j);
			} // end of j loop
		} // end of i loop load
		
		// /new stuff from elena
		for (int i = 0; i < numberOfSeries; i++)
		{
			eachTreeTempIII = new ArrayList<Integer>();
			for (int j = 0; j < dataBlock.size(); j++)
			{
				if ((strIII[i][j] >= 'a' && strIII[i][j] <= 'z') || (strIII[i][j] >= 'A' && strIII[i][j] <= 'Z'))
				{
					eachTreeTempIII.add(1);
				}
				else
				{
					eachTreeTempIII.add(0);
				}
				// log.debug(i+" "+str[i][j] +" "
				// +eachTreeTemp.get(j));
			} // end of j loop
			if (eachTreeTempIII.contains(1))
			{
				fIIIYearPerTree[i] = eachTreeTempIII.indexOf(1);
			}
			else
			{
				fIIIYearPerTree[i] = eachTreeTempIII.indexOf(1);
				// FFyearperTree[i]=-1;
			}
			eachTreeTempIII.clear();
			
		} // end of i loop
		
		// Load each crossection (or tree) into the ArrayList variable eachTree
		for (int j = 0; j < numberOfSeries; j++)
		{
			eachTreeIII = new ArrayList<Integer>();
			inIII = -7;
			for (int i = 0; i < dataBlock.size(); i++)
			{
				if (i < fIIIYearPerTree[j] || fIIIYearPerTree[j] == -1)
					inIII = -1;
				else
				{
					if ((strIII[j][i] >= 'a' && strIII[j][i] <= 'z') || (strIII[j][i] >= 'A' && strIII[j][i] <= 'Z'))
						inIII = 1;
					else if (strIII[j][i] == '|')
						inIII = 0;
					else if (strIII[j][i] == '.')
						inIII = -1;
					else
						inIII = -1;
					
				}
				eachTreeIII.add(inIII);
			} // end of i loop
			climate2dIII.add(eachTreeIII);
		} // end of j loop
			// log.debug("DEBUG: SIZE climate2d " + climate2dIII.size()
			// + "x" + climate2dIII.get(0).size());
		
		// determineFirstIndicatorYear2
		
		ArrayList<Integer> indIII = new ArrayList<Integer>();
		for (int j = 0; j < numberOfSeries; j++)
		{
			indIII.add(climate2dIII.get(j).indexOf(1));
		}
		Collections.sort(indIII);
		boolean breakme = true;
		for (int j = 0; j < numberOfSeries; j++)
		{
			if (indIII.get(j) >= 0 && breakme)
			{
				firstIndicatorYear = firstYear + indIII.get(j);
				breakme = false;
			}
		}
		// log.debug("DEBUG Internal First indicator Year" +
		// firstIndicatorYear);
		
		isClimate2dIIIinit = true;
	}
	
	/**
	 * Initialise the FHX2FileReader
	 */
	private void init() {
	
		String record = null;
		String blankName = "";
		BufferedReader br = null;
		FileInputStream is = null;
		InputStreamReader isr = null;
		dataBlock = new ArrayList<String>();
		dataByRow = new ArrayList<String>();
		badDataLines = new ArrayList<Integer>();
		seriesNameLine = new ArrayList<String>();
		Pattern p = Pattern.compile("[\\s]+");
		String spaces = "[\\s]+";
		// String fhx2dataline = "^[a-z A-Z . | } { \\[ \\]]+$";
		String regexNumericalDataint = "^[0-9 \\-]+$";
		int idx;
		
		try
		{
			String charsetName = App.prefs.getCharsetPref(PrefKey.FORCE_CHAR_ENC_TO, Charset.forName("UTF-8")).toString();
			
			if (App.prefs.getBooleanPref(PrefKey.AUTO_DETECT_CHAR_ENC, true))
			{
				CharsetDetector detector;
				CharsetMatch match;
				byte[] byteData = Files.readAllBytes(file.toPath());
				
				detector = new CharsetDetector();
				
				detector.setText(byteData);
				match = detector.detect();
				
				charsetName = match.getName();
			}
			
			is = new FileInputStream(file.toPath().toString());
			isr = new InputStreamReader(is, charsetName);
			log.debug("Opening file using " + charsetName + " charset");
			// BufferedReader buffReader = new BufferedReader(isr);
			
			// fr = ReaderFactory.createReaderFromFile(file);
			br = new BufferedReader(isr);
			
			while ((record = br.readLine()) != null)
			{
				rawContent += record + System.lineSeparator();
				idx = record.lastIndexOf(" ");
				// log.debug("record is: "+ record +" idx is: "+idx);
				if (idx != -1)
				{
					// log.debug("record is: "+ record
					// +" idx is: "+idx);
					// String formatType = record.substring(0,2).toLowerCase();
					// log.debug(record is);
					if (record.toLowerCase().startsWith("fhx2 format") || record.toLowerCase().startsWith("fire2 format"))
					{
						
						isFormatInfoSet = true;
						// set file format type
						if (record.toLowerCase().startsWith("fhx2 format"))
						{
							format = "FHX2";
						}
						else if (record.toLowerCase().startsWith("fire2 format"))
						{
							format = "FIRE2";
						}
						
						record = br.readLine();
						rawContent += record + System.lineSeparator();
						String[] result = p.split(record);
						// if(result[0].t)
						
						firstYear = Integer.parseInt(result[0].trim());
						numberOfSeries = Integer.parseInt(result[1].trim());
						lengthOfSeriesName = Integer.parseInt(result[2].trim());
						// char[][] xsname = new
						// char[this.getNumberOfSeries()][this.getLengthOfSeriesName()];
						for (int i = 0; i <= this.getNumberOfSeries(); i++)
						{
							blankName = blankName + " ";
						}
						for (int i = 0; i <= this.getLengthOfSeriesName(); i++)
						{
							
							// record = br.readLine().trim();
							record = br.readLine();
							rawContent += record + System.lineSeparator();
							// log.debug("length of record is : "+
							// record.length());
							if ((i == this.getLengthOfSeriesName() && record.isEmpty())
									|| (i == this.getLengthOfSeriesName() && record.matches(spaces)))
							{
								hitBlankline = true;
							}
							if (i < this.getLengthOfSeriesName())
							{
								if (record.length() >= this.getNumberOfSeries())
								{
									seriesNameLine.add(record.substring(0, this.getNumberOfSeries()));
								}
								else
								{
									seriesNameLine.add((record.substring(0, record.length()))
											+ (blankName.substring(0, (this.getNumberOfSeries() - record.length()))));
									
								}
							}
							// log.debug("nameLine : "+ record);
						} // endofloopi getting the name of the namelines
						int countblines = 0;
						while ((record = br.readLine()) != null)
						{
							rawContent += record + System.lineSeparator();
							dataByRow.add(record);
							// log.debug("I am here in the while of the loop and countblines is "+countblines);
							// log.debug("Line has "+record.length()+" records in it, whereas format line says "+this.getNumberOfSeries());
							if (record.length() == this.getNumberOfSeries())
							{
								dataBlock.add(record.substring(0, this.getNumberOfSeries()));
							}
							else if (record.length() > this.getNumberOfSeries())
							{
								// log.debug("length is "+record.length()+"piece left is:
								// "+record.substring(this.getNumberOfSeries(),record.length()).trim());
								String yearbit = record.substring(this.getNumberOfSeries(), record.length()).trim();
								if (!yearbit.matches(regexNumericalDataint))
								{
									// log.debug("Year bit of line is wonky. Year bit is:"
									// +yearbit);
									hitBadline = true;
									badDataLines.add(countblines);
									countblines = countblines + 1;
									dataBlock.add(record.substring(0, this.getNumberOfSeries()));
								}
								else
								{
									dataBlock.add(record.substring(0, this.getNumberOfSeries()));
								}
							}
							else if (record.length() == 0)
							{
								log.debug("Skipping empty line");
							}
							else if (record.length() == 1)
							{
								log.debug("Line has just 1 character");
								log.debug("\\u" + Integer.toHexString(record.charAt(0) | 0x10000).substring(1));
							}
							else
							{
								log.debug("Setting hitBadLine because record.length() = " + record.length()
										+ ", whereas getNumberOfSeries() = " + this.getNumberOfSeries());
								
								hitBadline = true;
								badDataLines.add(countblines);
								// log.debug("I am here in recor.length less that series number");
								countblines = countblines + 1;
							}
						}
					}
				}
			}
		}
		catch (FileNotFoundException e)
		{
			log.error("The file '" + file.getName() + "' does not exist");
			return;
		}
		catch (NumberFormatException e)
		{
			e.printStackTrace();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		finally
		{
			
			// Clean up readers
			try
			{
				if (is != null)
					is.close();
				if (isr != null)
					isr.close();
				if (br != null)
					br.close();
			}
			catch (IOException e)
			{
				
			}
			
		}
		yearArray = new ArrayList<Integer>();
		for (int i = 0; i < dataBlock.size(); i++)
			yearArray.add(this.getFirstYear() + i);
		
		generateSeriesName();
		
	}
	
	// **************
	//
	// ACCESSORS
	//
	// **************
	
	/**
	 * Get the first year in which an indicator is found within this file
	 * 
	 * @return
	 */
	@Override
	public Integer getFirstIndicatorYear() {
	
		if (isClimate1dIIIinit == false)
			this.generate1DEventsIII();
		return firstIndicatorYear;
	}
	
	/**
	 * Get the year in which the first injury is found
	 * 
	 * @return
	 */
	@Override
	public Integer getFirstInjuryYear() {
	
		if (isClimate1dIIinit == false)
			this.generate1DEventsII();
		return firstInjuryYear;
	}
	
	@Override
	public boolean passesBasicSyntaxCheck() {
	
		return hitBlankline && !hitBadline;
	}
	
	/**
	 * <p>
	 * Get an ArrayList with length equal to number of years in file, containing Integer codes meaning
	 * </p>
	 * <ul>
	 * <li>-1 = no data</li>
	 * <li>0 = injury event or recording year</li>
	 * <li>1 = fire event</li>
	 * </ul>
	 * 
	 * @return
	 */
	@Override
	public ArrayList<Integer> getFireEventsArray() {
	
		if (isClimate1dIinit == false)
			this.generate1DEventsI();
		return climate1dI;
	}
	
	/**
	 * <p>
	 * Get an ArrayList with length equal to number of years in file, containing Integer codes meaning
	 * </p>
	 * <ul>
	 * <li>1 = injury event</li>
	 * <li>0 = fire event or recording year</li>
	 * <li>-1 = no data</li>
	 * </ul>
	 * 
	 * @return
	 */
	@Override
	public ArrayList<Integer> getOtherInjuriesArray() {
	
		if (this.isClimate1dIIinit == false)
			this.generate1DEventsII();
		return climate1dII;
	}
	
	/**
	 * <p>
	 * Get an ArrayList with length equal to number of years in file, containing Integer codes meaning
	 * </p>
	 * <ul>
	 * <li>1 = fire or injury event</li>
	 * <li>0 = recording year</li>
	 * <li>-1 = no data</li>
	 * </ul>
	 * 
	 * @return
	 */
	@Override
	public ArrayList<Integer> getFiresAndInjuriesArray() {
	
		if (isClimate1dIIIinit == false)
			this.generate1DEventsIII();
		return climate1dIII;
	}
	
	/**
	 * Get an ArrayList of years contained within this file
	 * 
	 * @return
	 */
	@Override
	public ArrayList<Integer> getYearArray() {
	
		return yearArray;
	}
	
	/**
	 * Returns an array of strings, each containing the data portion of the FHX file minus any year value on the end. Each string will
	 * contain the same number of characters as there are series in the file.
	 * 
	 * @return
	 */
	@Override
	public ArrayList<String> getData() {
	
		return dataBlock;
	}
	
	/**
	 * Returns an array of rows containing the raw character data extracted from the data block of the FHX file
	 * 
	 * @return
	 */
	@Override
	public ArrayList<String> getRawRowData() {
	
		return dataByRow;
	}
	
	/**
	 * Get ArrayList of line numbers for all data lines that contain errors
	 * 
	 * @return
	 */
	@Override
	public ArrayList<Integer> getBadDataLineNumbers() {
	
		return badDataLines;
	}
	
	/**
	 * TODO Documentation needed
	 * 
	 * @return
	 */
	@Override
	public int[] getTotals() {
	
		return totals;
	}
	
	/**
	 * <p>
	 * Get a multi-dimensional array with rows = number of years, and columns = number of samples. The integer values within the arrays mean
	 * the following:
	 * </p>
	 * 
	 * <ul>
	 * <li>-1 = no data (equivalent to a dot in the data file)</li>
	 * <li>0 = susceptible to fire (recording) but no event detected (a pipe in the file)</li>
	 * <li>1 = event (a letter in the data file)</li>
	 * </ul>
	 * 
	 * @param eventType
	 * @return
	 */
	@Override
	public ArrayList<ArrayList<Integer>> getEventDataArrays(EventTypeToProcess eventType) {
	
		if (eventType == null)
		{
			return null;
		}
		
		if (eventType.equals(EventTypeToProcess.FIRE_EVENT))
		{
			if (this.isClimate2dIinit == false)
				this.generate2DEventsI();
			return this.climate2dI;
		}
		else if (eventType.equals(EventTypeToProcess.INJURY_EVENT))
		{
			if (this.isClimate2dIIinit == false)
				this.generate2DEventsII();
			return this.climate2dII;
		}
		else if (eventType.equals(EventTypeToProcess.FIRE_AND_INJURY_EVENT))
		{
			if (this.isClimate2dIIIinit == false)
				this.generate2DEventsIII();
			return this.climate2dIII;
		}
		
		return null;
		
	}
	
	/**
	 * TODO Documentation needed
	 * 
	 * @return
	 */
	@Override
	public ArrayList<ArrayList<Integer>> getCapsYearperSample2d() {
	
		if (this.isClimate2dIinit == false)
			this.generate2DEventsI();
		return capsYearperSample2d;
	}
	
	/**
	 * TODO Documentation needed
	 * 
	 * @return
	 */
	@Override
	public ArrayList<ArrayList<Integer>> getCalosYearperSample2d() {
	
		if (this.isClimate2dIinit == false)
			this.generate2DEventsI();
		return calosYearperSample2d;
	}
	
	/**
	 * TODO Documentation needed
	 * 
	 * @return
	 */
	@Override
	public ArrayList<ArrayList<Character>> getCapsperSample2d() {
	
		if (this.isClimate2dIinit == false)
			this.generate2DEventsI();
		return capsperSample2d;
	}
	
	/**
	 * TODO Documentation needed
	 * 
	 * @return
	 */
	@Override
	public ArrayList<ArrayList<Character>> getCalosperSample2d() {
	
		if (this.isClimate2dIinit == false)
			this.generate2DEventsI();
		return calosperSample2d;
	}
	
	/**
	 * <p>
	 * Get a multi-dimensional array with rows = number of years, and columns = 3.
	 * </p>
	 * 
	 * <ul>
	 * <li>Column 0 = number of fires</li>
	 * <li>Column 1 = number of trees</li>
	 * <li>Column 2 = percentage of scarred trees</li>
	 * </ul>
	 * 
	 * <p>
	 * This array is typically used when wanting to filter the data by number of percentage of events
	 * </p>
	 * 
	 * @param eventType
	 * @return
	 */
	@Override
	public ArrayList<ArrayList<Double>> getFilterArrays(EventTypeToProcess eventType) {
	
		if (eventType == null)
		{
			return null;
		}
		
		if (eventType.equals(EventTypeToProcess.FIRE_EVENT))
		{
			if (this.isFilterse2dIinit == false)
				generate2DFiltersI();
			return this.filters2dI;
		}
		else if (eventType.equals(EventTypeToProcess.INJURY_EVENT))
		{
			if (this.isFilterse2dIIinit == false)
				generate2DFiltersII();
			return this.filters2dII;
		}
		else if (eventType.equals(EventTypeToProcess.FIRE_AND_INJURY_EVENT))
		{
			if (this.isFilterse2dIIIinit == false)
				generate2DFiltersIII();
			return this.filters2dIII;
		}
		
		return null;
		
	}
	
	/**
	 * Returns an int[] with one entry per sample in the file with each int containing the index to the earliest year for the sample.
	 * 
	 * @return
	 */
	@Override
	public int[] getStartYearIndexPerSample() {
	
		if (this.isRecorderYears2DArrayInit == false)
			this.generateRecorderYearsArray(EventTypeToProcess.FIRE_EVENT);
		return startYearIndexPerSample;
	}
	
	/**
	 * Returns an int[] with one entry per sample in the file with each int containing the year number for the sample.
	 * 
	 * @return
	 */
	@Override
	public int[] getStartYearPerSample() {
	
		int[] ind = getStartYearIndexPerSample();
		int[] years = new int[ind.length];
		int firstyear = getFirstYear();
		
		for (int i = 0; i < ind.length; i++)
		{
			years[i] = ind[i] + firstyear;
		}
		
		return years;
	}
	
	/**
	 * Returns an int[] with one entry per sample in the file with each int containing the index to the last year for the sample.
	 * 
	 * @return
	 */
	@Override
	public int[] getLastYearIndexPerSample() {
	
		if (this.isRecorderYears2DArrayInit == false)
			this.generateRecorderYearsArray(EventTypeToProcess.FIRE_EVENT);
		return lastYearIndexPerSample;
	}
	
	/**
	 * Returns an int[] with one entry per sample in the file with each int containing the year number for the last year in the sample.
	 * 
	 * @return
	 */
	@Override
	public int[] getLastYearPerSample() {
	
		int[] ind = getLastYearIndexPerSample();
		int[] years = new int[ind.length];
		int firstyear = getFirstYear();
		
		for (int i = 0; i < ind.length; i++)
		{
			years[i] = ind[i] + firstyear;
		}
		
		return years;
	}
	
	/**
	 * Returns an int[] with one entry per sample in the file with each int containing the index to the year in which the pith for the
	 * sample is found. In series where there is no pith, the index to the first year is returned instead
	 * 
	 * @return
	 */
	@Override
	public int[] getPithIndexPerSample() {
	
		if (this.isRecorderYears2DArrayInit == false)
			this.generateRecorderYearsArray(EventTypeToProcess.FIRE_EVENT);
		return pithIndexPerSample;
	}
	
	/**
	 * Get an int array with one entry per sample in the file with each int containing the count of the number of recorder years
	 * 
	 * @return
	 */
	@Override
	public int[] getTotalRecorderYearsPerSample() {
	
		if (this.isRecorderYears2DArrayInit == false)
			this.generateRecorderYearsArray(EventTypeToProcess.FIRE_EVENT);
		return totalRecordYearsPerSample;
	}
	
	/*
	 * public int[] getTotalRecorderYearsPerSampleII() {
	 * 
	 * if (this.isRecorderYears2DArrayInitII == false) this.generateRecorderYearsArrayII(); return totalRecordYearsPerSampleII; }
	 */
	
	/**
	 * TODO
	 * 
	 * @return
	 */
	@Override
	public ArrayList<ArrayList<Integer>> getRecorderYears2DArray(EventTypeToProcess eventTypeToProcess) {
	
		if (eventTypeToProcess.equals(EventTypeToProcess.FIRE_EVENT) || eventTypeToProcess.equals(EventTypeToProcess.FIRE_AND_INJURY_EVENT))
		{
			if (this.isRecorderYears2DArrayInit == false)
				this.generateRecorderYearsArray(EventTypeToProcess.FIRE_EVENT);
			return this.recorderYears2DArray;
		}
		else if (eventTypeToProcess.equals(EventTypeToProcess.INJURY_EVENT))
		{
			if (this.isRecorderYears2DArrayInitII == false)
				this.generateRecorderYearsArray(EventTypeToProcess.INJURY_EVENT);
			return this.recorderYears2DArrayII;
		}
		else
		{
			log.error("Unsupported EventTypeToProcess");
			return null;
		}
		
	}
	
	/**
	 * Get an array containing the index (not year) of the innermost (earliest) ring for each sample where the sample has no pith. The array
	 * will contain one value per series in the file, and will be in the order the series are arranged in the file. Samples with pith will
	 * return -1;
	 * 
	 * @return int[]
	 */
	@Override
	public int[] getInnerMostperTree() {
	
		if (this.isClimate2dIinit == false)
			this.generate2DEventsI();
		return innerMostPerTree;
	}
	
	/**
	 * Get an array containing the year (not index) of the innermost (earliest) ring for each sample. The array will contain one value per
	 * series in the file, and will be in the order the series are arranged in the file.
	 * 
	 * @return
	 */
	@Override
	public int[] getInnerMostYearPerTree() {
	
		if (this.isClimate2dIinit == false)
			this.generate2DEventsI();
		
		int[] arr = new int[innerMostPerTree.length];
		
		for (int i = 0; i < innerMostPerTree.length; i++)
		{
			arr[i] = innerMostPerTree[i] + this.getFirstYear();
		}
		
		return arr;
	}
	
	/**
	 * Get an array containing the index (not year) of the outermost (most recent) ring for each sample. The array with contain one value
	 * per series in the file, and will be in the order the series are arranged in the file.
	 * 
	 * @return int[]
	 */
	@Override
	public int[] getOutterMostperTree() {
	
		if (this.isClimate2dIinit == false)
			this.generate2DEventsI();
		
		return outerMostPerTree;
	}
	
	/**
	 * Get an array containing the year (not index) of the outermost (most recent) ring for each sample. The array with contain one value
	 * per series in the file, and will be in the order the series are arranged in the file.
	 * 
	 * @return int[]
	 */
	@Override
	public int[] getOuterMostYearPerTree() {
	
		if (this.isClimate2dIinit == false)
			this.generate2DEventsI();
		
		int[] arr = new int[outerMostPerTree.length];
		
		for (int i = 0; i < outerMostPerTree.length; i++)
		{
			arr[i] = outerMostPerTree[i] + this.getFirstYear();
		}
		
		return arr;
	}
	
	/**
	 * Returns an int[] with one entry per sample in the file with each int containing the index to the year in which the bark for the
	 * sample is found. In series where there is no bark, the index to the last year is returned instead
	 * 
	 * @return
	 */
	@Override
	public int[] getBarkIndexPerTree() {
	
		if (this.isClimate2dIinit == false)
			this.generate2DEventsI();
		
		return barkPerTree;
	}
	
	/**
	 * Get the first year in the file as indicated by the file header
	 * 
	 * @return
	 */
	@Override
	public Integer getFirstYear() {
	
		return firstYear;
	}
	
	/**
	 * Get an ArrayList of the series names from this file
	 * 
	 * @return
	 */
	@Override
	public ArrayList<String> getSeriesNameArray() {
	
		return seriesName;
	}
	
	/**
	 * Whether this file contains any fire events or injuries
	 * 
	 * @return boolean
	 */
	@Override
	public boolean hasFireEventsOrInjuries() {
	
		for (String line : dataBlock)
		{
			if (line.contains("U") || line.contains("u") || line.contains("A") || line.contains("a") || line.contains("L")
					|| line.contains("l") || line.contains("M") || line.contains("m") || line.contains("E") || line.contains("e")
					|| line.contains("D") || line.contains("d"))
			{
				return true;
			}
		}
		
		return false;
		
	}
	
	/**
	 * Whether this file contains any fire events
	 * 
	 * @return boolean
	 */
	@Override
	public boolean hasFireEvents() {
	
		for (String line : dataBlock)
		{
			if (line.contains("U") || line.contains("A") || line.contains("L") || line.contains("M") || line.contains("E")
					|| line.contains("D"))
			{
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Whether this file contains any injury events
	 * 
	 * @return boolean
	 */
	@Override
	public boolean hasInjuryEvents() {
	
		for (String line : dataBlock)
		{
			if (line.contains("u") || line.contains("a") || line.contains("l") || line.contains("m") || line.contains("e")
					|| line.contains("d"))
			{
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Get the file being read
	 * 
	 * @return
	 */
	@Override
	public File getFile() {
	
		return file;
	}
	
	@Override
	public FHFile getFHFile() {
	
		if (file instanceof FHFile)
			return (FHFile) file;
		
		return new FHFile(file);
	}
	
	/**
	 * Get the first year where a file was recorded in any series within this file
	 * 
	 * @return
	 */
	@Override
	public Integer getFirstFireYear() {
	
		if (isClimate1dIinit == false)
			this.generate1DEventsI();
		return firstFireYear;
	}
	
	/**
	 * Returns the name of the file being read. This is just the last name in the pathname's name sequence. If the pathname's name sequence
	 * is empty, then the empty string is returned.
	 * 
	 * @return String of the name of the file being read
	 */
	@Override
	public String getName() {
	
		return file.getName();
	}
	
	/**
	 * Get the number of series contained in this file
	 * 
	 * @return Integer number of series
	 */
	@Override
	public Integer getNumberOfSeries() {
	
		return numberOfSeries;
	}
	
	/**
	 * Get the maximum length of the series names specified in the file header
	 * 
	 * @return Integer maximum series name length
	 */
	@Override
	public Integer getLengthOfSeriesName() {
	
		return lengthOfSeriesName;
	}
	
	/**
	 * Get the last (most recent) year in the file. Calculated from first year as reported by the header plus the number of years of data
	 * 
	 * @return
	 */
	@Override
	public Integer getLastYear() {
	
		if (this.firstYear == null)
		{
			log.error("Missing first year from file");
			return null;
		}
		
		if (this.dataBlock == null)
		{
			log.error("File has no data in it");
			return null;
		}
		
		return this.firstYear + dataBlock.size() - 1;
	}
	
	/***********************
	 * 
	 * DEPRECATED METHODS
	 * 
	 ***********************/
	
	/**
	 * Use getFireEventsArray() instead
	 * 
	 * @deprecated
	 * @return
	 */
	@Deprecated
	public ArrayList<Integer> getClimate() {
	
		return getFireEventsArray();
	}
	
	/**
	 * Use getOtherInjuriesArray() instead
	 * 
	 * @deprecated
	 * @return
	 */
	@Deprecated
	public ArrayList<Integer> getClimateI() {
	
		return this.getOtherInjuriesArray();
	}
	
	/**
	 * Use getFiresAndInjuriesArray() instead
	 * 
	 * @deprecated
	 * @return
	 */
	@Deprecated
	public ArrayList<Integer> getClimateIII() {
	
		return this.getFiresAndInjuriesArray();
	}
	
	/**
	 * There is no need to run this anymore as the data is generated automatically on demand
	 * 
	 * @deprecated
	 */
	@Deprecated
	public void makeClimate() {
	
		generate1DEventsI();
	}
	
	/**
	 * There is no need to run this anymore as the data is generated automatically on demand
	 * 
	 * @deprecated
	 */
	@Deprecated
	public void makeClimateI() {
	
		generate1DEventsII();
		
	}
	
	/**
	 * There is no need to call this function anymore as it is run automatically when needed
	 * 
	 * @deprecated
	 */
	@Deprecated
	public void makeClimateIII() {
	
		generate1DEventsIII();
		
	}
	
	/**
	 * 
	 * This function name is confusing. Use getYearArray() instead.
	 * 
	 * @deprecated
	 * @return
	 */
	@Deprecated
	public ArrayList<Integer> getYear() {
	
		return yearArray;
	}
	
	/**
	 * @deprecated
	 * @return
	 */
	@Deprecated
	public ArrayList<String> getSeriesNameLine() {
	
		return seriesNameLine;
	}
	
	/**
	 * Has the format info (FHX2 or FIRE2) been set?
	 * 
	 * @deprecated
	 * @return
	 */
	@Deprecated
	public boolean isFormatInfoSet() {
	
		return isFormatInfoSet;
	}
	
	/**
	 * Get the style of file being read. Either FHX2 or FIRE2.
	 * 
	 * @return
	 */
	@Override
	public String getFileFormat() {
	
		return format;
	}
	
	/**
	 * @deprecated
	 * @return
	 * 
	 */
	@Deprecated
	public ArrayList<ArrayList<Integer>> getClimate2d() {
	
		return this.getEventDataArrays(EventTypeToProcess.FIRE_EVENT);
	}
	
	/**
	 * @deprecated
	 * @return
	 */
	@Deprecated
	public ArrayList<ArrayList<Integer>> getClimate2dII() {
	
		return this.getEventDataArrays(EventTypeToProcess.INJURY_EVENT);
	}
	
	/**
	 * @deprecated
	 * @return
	 */
	@Deprecated
	public ArrayList<ArrayList<Integer>> getClimate2dIII() {
	
		return this.getEventDataArrays(EventTypeToProcess.FIRE_AND_INJURY_EVENT);
	}
	
	/**
	 * There is no need to run this manually any more as it is automatically run when required
	 * 
	 * @deprecated
	 */
	@Deprecated
	public void makeClimate2d() {
	
		generate2DEventsI();
	}
	
	/**
	 * There is no need to run this manually any more as it is automatically run when required
	 * 
	 * @deprecated
	 */
	@Deprecated
	public void makeClimate2dII() {
	
		generate2DEventsII();
		
	}
	
	/**
	 * There is no need to run this manually any more as it is automatically run when required
	 * 
	 * @deprecated
	 */
	@Deprecated
	public void makeClimate2dIII() {
	
		generate2DEventsIII();
	}
	
	/**
	 * Use getFilterArrays(EventTypeToProcess.FIRE_EVENT) instead
	 * 
	 * @deprecated
	 * @return
	 */
	@Deprecated
	public ArrayList<ArrayList<Double>> getfilters2d() {
	
		return getFilterArrays(EventTypeToProcess.FIRE_EVENT);
		
	}
	
	/**
	 * Use getFilterArrays(EventTypeToProcess.INJURY_EVENT) instead
	 * 
	 * @deprecated
	 * @return
	 */
	@Deprecated
	public ArrayList<ArrayList<Double>> getfilters2dII() {
	
		return getFilterArrays(EventTypeToProcess.INJURY_EVENT);
	}
	
	/**
	 * Use getFilterArrays(EventTypeToProcess.FIRE_AND_INJURY_EVENT) instead
	 * 
	 * @deprecated
	 * @return
	 */
	@Deprecated
	public ArrayList<ArrayList<Double>> getfilters2dIII() {
	
		return getFilterArrays(EventTypeToProcess.FIRE_AND_INJURY_EVENT);
	}
	
	/**
	 * This is run automatically now when required
	 * 
	 * @deprecated
	 */
	@Deprecated
	public void makeFilters2d() {
	
		generate2DFiltersI();
	}
	
	/**
	 * This is run automatically now when required
	 * 
	 * @deprecated
	 */
	@Deprecated
	public void makefilters2dII() {
	
		generate2DFiltersII();
	}
	
	/**
	 * This is run automatically now when required
	 * 
	 * @deprecated
	 */
	@Deprecated
	public void makefilters2dIII() {
	
		generate2DFiltersIII();
	}
	
	@Override
	protected void populateSeriesList() {
	
		this.seriesList = new ArrayList<FHSeries>();
		
		for (int i = 0; i < getNumberOfSeries(); i++)
		{
			try
			{
				
				String title = getSeriesNameArray().get(i);
				int firstYear = getStartYearPerSample()[i];
				
				// Work out if series has pith and/or bark
				boolean hasPith = false;
				boolean hasBark = false;
				int[] inneryearpith = getPithIndexPerSample();
				int[] inneryear = getInnerMostperTree();
				int[] outeryearbark = getBarkIndexPerTree();
				int[] outeryear = getOutterMostperTree();
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
					
				}
				
				// Calculate inner index, outer index and length of series
				/*
				 * int outer = this.getOutterMostperTree()[i]; int inner = this.getInnerMostperTree()[i]; if (outer == -1) outer =
				 * this.getBarkIndexPerTree()[i]; if (inner == -1) inner = this.getPithIndexPerSample()[i]; int arraylength = outer - inner;
				 */
				int inner = this.startYearIndexPerSample[i];
				int outer = this.lastYearIndexPerSample[i];
				int arraylength = (outer - inner) + 1;
				
				if (arraylength == 1)
				{
					log.info("No data for series " + (i + 1) + " so skipping");
					continue;
				}
				
				// Create boolean data arrays
				boolean[] recordingYears = new boolean[arraylength];
				boolean[] eventYears = new boolean[arraylength];
				boolean[] injuryYears = new boolean[arraylength];
				// boolean[] recordingYearsII = new boolean[arraylength];
				
				ArrayList<ArrayList<Integer>> rya = getRecorderYears2DArray(EventTypeToProcess.FIRE_EVENT);
				ArrayList<ArrayList<Integer>> eda1 = getEventDataArrays(EventTypeToProcess.FIRE_EVENT);
				ArrayList<ArrayList<Integer>> eda2 = getEventDataArrays(EventTypeToProcess.INJURY_EVENT);
				// ArrayList<ArrayList<Integer>> rya2 = getRecorderYears2DArray();
				
				int j = 0;
				for (int ind = inner; ind <= outer; ind++)
				{
					if (rya != null && rya.size() > 0)
						recordingYears[j] = rya.get(i).get(ind) == 1;
					if (eda1 != null && eda1.size() > 0)
						eventYears[j] = eda1.get(i).get(ind) == 1;
					if (eda2 != null && eda2.size() > 0)
						injuryYears[j] = eda2.get(i).get(ind) == 1;
					/*
					 * if (rya2 != null && rya2.size() > 0) recordingYearsII[j] = rya2.get(i).get(ind) == 1;
					 */
					j++;
				}
				
				FHSeries series = new FHSeries(title, firstYear, hasPith, hasBark, recordingYears, eventYears, injuryYears);
				seriesList.add(series);
				
			}
			catch (Exception e)
			{
				log.error("Failed to generate FHSeries");
				e.printStackTrace();
			}
		}
	}
	
	@Override
	public String getFileContentsAsString() {
	
		return this.rawContent;
	}
}
