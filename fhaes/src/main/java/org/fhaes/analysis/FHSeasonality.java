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

import java.awt.Component;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;

import org.fhaes.enums.EventTypeToProcess;
import org.fhaes.fhfilereader.FHFile;
import org.fhaes.fhfilereader.FHX2FileReader;
import org.fhaes.filefilter.CSVFileFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * FHSeasonality Class.
 */
public class FHSeasonality {
	
	private static final Logger log = LoggerFactory.getLogger(FHInterval.class);
	
	/**
	 * Run the FHSeasonality analysis
	 * 
	 * @param parent - parent component for centering dialogs
	 * @param outputFile - the output filename
	 * @param inputFile - the input filename
	 * @param jCheckdormant1 - is dormant in first season
	 * @param jCheckeewood1 - is early earlywood in first season
	 * @param jCheckmewood1 - is middle earlywood in first season
	 * @param jChecklewood1 - is late earlywood in first season
	 * @param jChecklatewood1 - is latewood in first season
	 * @param jCheckdormant2 - is dormant in second season
	 * @param jCheckeewood2 - is early earlywood in second season
	 * @param jCheckmewood2 - is middle earlywood in second season
	 * @param jChecklewood2 - is late earlywood in second season
	 * @param jChecklatewood2 - is latewood in second season
	 * @param jTextBeginningYear1 - first year in range to calculate
	 * @param jTextEndingYear - end year in range to calculate
	 * @param eventTypeToProcess - whether to do calculations on injuries or fires
	 */
	@SuppressWarnings("deprecation")
	public static void runAnalysis(Component parent, File outputFile, FHFile[] inputFile, Boolean jCheckdormant1, Boolean jCheckeewood1,
			Boolean jCheckmewood1, Boolean jChecklewood1, Boolean jChecklatewood1, Boolean jCheckdormant2, Boolean jCheckeewood2,
			Boolean jCheckmewood2, Boolean jChecklewood2, Boolean jChecklatewood2, int jTextBeginningYear1, int jTextEndingYear,
			EventTypeToProcess eventTypeToProcess) {
	
		boolean run = runSanityChecks(inputFile, jCheckdormant1, jCheckeewood1, jCheckmewood1, jChecklewood1, jChecklatewood1,
				jCheckdormant2, jCheckeewood2, jCheckmewood2, jChecklewood2, jChecklatewood2, jTextBeginningYear1, jTextEndingYear,
				eventTypeToProcess);
		
		/*
		 * If at least one file has been choosen then the progam will run otherwise get message
		 */
		
		// MAIN RUN
		if (run)
		{
			int fileBrowseReturn = 0;
			ArrayList<FHX2FileReader> myReader = new ArrayList<FHX2FileReader>();
			ArrayList<Integer> firstYears = new ArrayList<Integer>();
			ArrayList<Integer> lastYears = new ArrayList<Integer>();
			Integer minFirstYear = new Integer(9999);
			Integer maxLastYear = new Integer(0);
			String combination1 = new String();
			String combination2 = new String();
			String savePath = new String();
			savePath = inputFile[0].getAbsolutePath();
			DecimalFormat onePlace = new DecimalFormat("0.0");
			// DecimalFormat twoPlace = new DecimalFormat("0.00");
			
			// Create the name of the combination1
			combination1 = "";
			if (eventTypeToProcess.equals(EventTypeToProcess.FIRE_EVENT))
			{
				if (jCheckdormant1)
					combination1 = combination1 + "D";
				if (jCheckeewood1)
					combination1 = combination1 + "E";
				if (jCheckmewood1)
					combination1 = combination1 + "M";
				if (jChecklewood1)
					combination1 = combination1 + "L";
				if (jChecklatewood1)
					combination1 = combination1 + "A";
			}
			else if (eventTypeToProcess.equals(EventTypeToProcess.INJURY_EVENT))
			{
				if (jCheckdormant1)
					combination1 = combination1 + "d";
				if (jCheckeewood1)
					combination1 = combination1 + "e";
				if (jCheckmewood1)
					combination1 = combination1 + "m";
				if (jChecklewood1)
					combination1 = combination1 + "l";
				if (jChecklatewood1)
					combination1 = combination1 + "a";
			}
			else if (eventTypeToProcess.equals(EventTypeToProcess.FIRE_AND_INJURY_EVENT))
			{
				if (jCheckdormant1)
					combination1 = combination1 + "Dd";
				if (jCheckeewood1)
					combination1 = combination1 + "Ee";
				if (jCheckmewood1)
					combination1 = combination1 + "Mm";
				if (jChecklewood1)
					combination1 = combination1 + "Ll";
				if (jChecklatewood1)
					combination1 = combination1 + "Aa";
			}
			else
			{
				log.error("Unsupported event type specified");
			}
			log.debug("combination1 is " + combination1);
			
			// Create the name of the combination2
			combination2 = "";
			if (eventTypeToProcess.equals(EventTypeToProcess.FIRE_EVENT))
			{
				if (jCheckdormant2)
					combination2 = combination2 + "D";
				if (jCheckeewood2)
					combination2 = combination2 + "E";
				if (jCheckmewood2)
					combination2 = combination2 + "M";
				if (jChecklewood2)
					combination2 = combination2 + "L";
				if (jChecklatewood2)
					combination2 = combination2 + "A";
			}
			else if (eventTypeToProcess.equals(EventTypeToProcess.INJURY_EVENT))
			{
				if (jCheckdormant2)
					combination2 = combination2 + "d";
				if (jCheckeewood2)
					combination2 = combination2 + "e";
				if (jCheckmewood2)
					combination2 = combination2 + "m";
				if (jChecklewood2)
					combination2 = combination2 + "l";
				if (jChecklatewood2)
					combination2 = combination2 + "a";
			}
			else if (eventTypeToProcess.equals(EventTypeToProcess.FIRE_AND_INJURY_EVENT))
			{
				if (jCheckdormant2)
					combination2 = combination2 + "Dd";
				if (jCheckeewood2)
					combination2 = combination2 + "Ee";
				if (jCheckmewood2)
					combination2 = combination2 + "Mm";
				if (jChecklewood2)
					combination2 = combination2 + "Ll";
				if (jChecklatewood2)
					combination2 = combination2 + "Aa";
			}
			else
			{
				log.error("Unsupported event type specified");
			}
			log.debug("combination2 is " + combination2);
			
			// Get first and last years for all files
			for (int i = 0; i < inputFile.length; i++)
			{
				myReader.add(new FHX2FileReader(inputFile[i]));
				firstYears.add(myReader.get(i).getFirstYear());
				lastYears.add(myReader.get(i).getLastYear());
			}
			
			// Sort first and last years into sequence
			Collections.sort(firstYears);
			Collections.sort(lastYears);
			
			// Set the beginning year accounting for the filter for the join
			if (jTextBeginningYear1 == 0)
			{
				minFirstYear = firstYears.get(0);
			}
			else
			{
				if (jTextBeginningYear1 > firstYears.get(0))
				{
					minFirstYear = jTextBeginningYear1;
				}
				else
				{
					minFirstYear = firstYears.get(0);
				}
			}
			
			// Set the last year accounting for the filter
			if (jTextEndingYear == 0)
			{
				maxLastYear = lastYears.get((lastYears.size() - 1));
			}
			else
			{
				if (jTextEndingYear < lastYears.get((lastYears.size() - 1)))
				{
					maxLastYear = jTextEndingYear;
				}
				else
				{
					maxLastYear = lastYears.get((lastYears.size() - 1));
				}
			}
			
			/*
			 * Calculate the listYears the common years where the file will be analyzed for the join
			 */
			ArrayList<Integer> listYears = new ArrayList<Integer>();
			for (int i = 0; i < maxLastYear - minFirstYear + 1; i++)
			{
				listYears.add(minFirstYear + i);
			}
			
			/*
			 * Declaration of all the array lists needed in the process
			 */
			String[] seaspertableparam = new String[10];
			if (eventTypeToProcess.equals(EventTypeToProcess.FIRE_EVENT))
			{
				seaspertableparam[0] = "Total number of fires";
				seaspertableparam[1] = "of events with season recorded";
				seaspertableparam[2] = "of events with undetermined season";
				seaspertableparam[3] = "of 'D' fires";
				seaspertableparam[4] = "of 'E' fires";
				seaspertableparam[5] = "of 'M' fires";
				seaspertableparam[6] = "of 'L' fires";
				seaspertableparam[7] = "of 'A' fires";
				seaspertableparam[8] = "of '" + combination1 + "' fires";
				seaspertableparam[9] = "of '" + combination2 + "' fires";
			}
			else if (eventTypeToProcess.equals(EventTypeToProcess.INJURY_EVENT))
			{
				seaspertableparam[0] = "Total number of indicators";
				seaspertableparam[1] = "of events with season recorded";
				seaspertableparam[2] = "of events with undetermined season";
				seaspertableparam[3] = "of 'd' indicators";
				seaspertableparam[4] = "of 'e' indicators";
				seaspertableparam[5] = "of 'm' indicators";
				seaspertableparam[6] = "of 'l' indicators";
				seaspertableparam[7] = "of 'a' indicators";
				seaspertableparam[8] = "of '" + combination1 + "' indicators";
				seaspertableparam[9] = "of '" + combination2 + "' indicators";
			}
			else if (eventTypeToProcess.equals(EventTypeToProcess.FIRE_AND_INJURY_EVENT))
			{
				seaspertableparam[0] = "Total number of fires and other indicators";
				seaspertableparam[1] = "of events with season recorded";
				seaspertableparam[2] = "of events with undetermined season";
				seaspertableparam[3] = "of 'Dd' fires and other indicators";
				seaspertableparam[4] = "of 'Ee' fires and other indicators";
				seaspertableparam[5] = "of 'Mm' fires and other indicators";
				seaspertableparam[6] = "of 'Ll' fires and other indicators";
				seaspertableparam[7] = "of 'Aa' fires and other indicators";
				seaspertableparam[8] = "of '" + combination1 + "' fires and other indicators";
				seaspertableparam[9] = "of '" + combination2 + "' fires and other indicators";
			}
			else
			{
				
				log.error("Unsupported event type caught");
			}
			;
			
			double[][] totalsDecomp = new double[seaspertableparam.length][myReader.size()];
			double[][] percentDecomp = new double[seaspertableparam.length][myReader.size()];
			
			/*
			 * start processing each file individually:
			 */
			for (int i = 0; i < myReader.size(); i++)
			{
				
				/*
				 * get the vector Year containing the vector of year of a given fhx file load it into the array list InitalYearsVector.
				 */
				// InitialYearsVector = myReader.get(i).getYear();
				/*
				 * make decompSyb2d
				 */
				myReader.get(i).makeDecompSyb2d();
				
				/*
				 * load decompsyb into an arraylist
				 */
				// decompVector2= myReader.get(i).getDecompSyb2d();
				
				/*
				 * create the table of output with totals and percents
				 */
				myReader.get(i).getTotals();
				
				if (eventTypeToProcess.equals(EventTypeToProcess.FIRE_EVENT))
				{
					totalsDecomp[0][i] = myReader.get(i).getTotals()[0];
					percentDecomp[0][i] = -99.0;
					totalsDecomp[1][i] = (myReader.get(i).getTotals()[0] - myReader.get(i).getTotals()[7]);
					totalsDecomp[2][i] = myReader.get(i).getTotals()[7];
					if (totalsDecomp[0][i] != 0)
					{
						percentDecomp[1][i] = totalsDecomp[1][i] * 100.0 / totalsDecomp[0][i];
						percentDecomp[2][i] = totalsDecomp[2][i] * 100.0 / totalsDecomp[0][i];
					}
					else
					{
						percentDecomp[1][i] = -99.0;
						percentDecomp[2][i] = -99.0;
					}
					for (int j = 3; j < 8; j++)
					{
						totalsDecomp[j][i] = myReader.get(i).getTotals()[j - 1];
						if (totalsDecomp[1][i] != 0)
						{
							percentDecomp[j][i] = totalsDecomp[j][i] * 100.0 / totalsDecomp[1][i];
						}
						else
						{
							percentDecomp[j][i] = -99.0;
						}
						
					}
					/*
					 * Find the Totals and percents of combo1 and combo 2
					 */
					// log.debug("the combo 1 before total is "+totalsDecomp[8][i]);
					// for(int k=0; k<combo1.length(); k++){
					if (jCheckdormant1)
					{
						totalsDecomp[8][i] = totalsDecomp[8][i] + totalsDecomp[3][i];
						log.debug("d " + totalsDecomp[8][i]);
					}
					;
					if (jCheckeewood1)
					{
						totalsDecomp[8][i] = totalsDecomp[8][i] + totalsDecomp[4][i];
						log.debug("e " + totalsDecomp[8][i]);
					}
					;
					if (jCheckmewood1)
					{
						totalsDecomp[8][i] = totalsDecomp[8][i] + totalsDecomp[5][i];
						log.debug("m " + totalsDecomp[8][i]);
					}
					;
					if (jChecklewood1)
					{
						totalsDecomp[8][i] = totalsDecomp[8][i] + totalsDecomp[6][i];
						log.debug("l " + totalsDecomp[8][i]);
					}
					;
					if (jChecklatewood1)
					{
						totalsDecomp[8][i] = totalsDecomp[8][i] + totalsDecomp[7][i];
						log.debug("a " + totalsDecomp[8][i]);
					}
					;
					// }
					// totalsDecomp[8][i]=totalsDecomp[8][i];
					if (totalsDecomp[8][i] != 0)
					{
						percentDecomp[8][i] = totalsDecomp[8][i] * 100.0 / totalsDecomp[1][i];
					}
					else
					{
						percentDecomp[8][i] = 0.0;
					}
					// log.debug("the combo 1 total is "+totalsDecomp[8][i]+" "+percentDecomp[8][i]);
					// log.debug("the combo 2 before total is "+totalsDecomp[9][i]);
					// for(int k=0; k<combo2.length(); k++){
					if (jCheckdormant2)
					{
						totalsDecomp[9][i] = totalsDecomp[9][i] + totalsDecomp[3][i];
					}
					;
					if (jCheckeewood2)
					{
						totalsDecomp[9][i] = totalsDecomp[9][i] + totalsDecomp[4][i];
					}
					;
					if (jCheckmewood2)
					{
						totalsDecomp[9][i] = totalsDecomp[9][i] + totalsDecomp[5][i];
					}
					;
					if (jChecklewood2)
					{
						totalsDecomp[9][i] = totalsDecomp[9][i] + totalsDecomp[6][i];
					}
					;
					if (jChecklatewood2)
					{
						totalsDecomp[9][i] = totalsDecomp[9][i] + totalsDecomp[7][i];
					}
					;
					
					// }
					// totalsDecomp[9][i]=totalsDecomp[9][i];
					log.debug("the combo 2  total is " + totalsDecomp[9][i]);
					if (totalsDecomp[9][i] != 0)
					{
						percentDecomp[9][i] = totalsDecomp[9][i] * 100.0 / totalsDecomp[1][i];
					}
					else
					{
						percentDecomp[9][i] = 0.0;
					}
					log.debug("the combo 2  total is " + totalsDecomp[9][i] + " " + percentDecomp[9][i]);
				}
				else if (eventTypeToProcess.equals(EventTypeToProcess.INJURY_EVENT))
				{
					totalsDecomp[0][i] = myReader.get(i).getTotals()[1];
					percentDecomp[0][i] = -99.0;
					totalsDecomp[1][i] = (myReader.get(i).getTotals()[1] - myReader.get(i).getTotals()[13]);
					totalsDecomp[2][i] = myReader.get(i).getTotals()[13];
					if (totalsDecomp[0][i] != 0)
					{
						percentDecomp[1][i] = totalsDecomp[1][i] * 100.0 / totalsDecomp[0][i];
						percentDecomp[2][i] = totalsDecomp[2][i] * 100.0 / totalsDecomp[0][i];
					}
					else
					{
						percentDecomp[1][i] = -99.0;
						percentDecomp[2][i] = -99.0;
					}
					for (int j = 3; j < 8; j++)
					{
						totalsDecomp[j][i] = myReader.get(i).getTotals()[j + 5];
						if (totalsDecomp[1][i] != 0)
						{
							percentDecomp[j][i] = totalsDecomp[j][i] * 100.0 / totalsDecomp[1][i];
						}
						else
						{
							percentDecomp[j][i] = -99.0;
						}
						
					}
					/*
					 * Find the Totals and percents of combo1 and combo 2
					 */
					// log.debug("the combo 1 before total is "+totalsDecomp[8][i]);
					// for(int k=0; k<combo1.length(); k++){
					if (jCheckdormant1)
					{
						totalsDecomp[8][i] = totalsDecomp[8][i] + totalsDecomp[3][i];
						log.debug("d " + totalsDecomp[8][i]);
					}
					;
					if (jCheckeewood1)
					{
						totalsDecomp[8][i] = totalsDecomp[8][i] + totalsDecomp[4][i];
						log.debug("e " + totalsDecomp[8][i]);
					}
					;
					if (jCheckmewood1)
					{
						totalsDecomp[8][i] = totalsDecomp[8][i] + totalsDecomp[5][i];
						log.debug("m " + totalsDecomp[8][i]);
					}
					;
					if (jChecklewood1)
					{
						totalsDecomp[8][i] = totalsDecomp[8][i] + totalsDecomp[6][i];
						log.debug("l " + totalsDecomp[8][i]);
					}
					;
					if (jChecklatewood1)
					{
						totalsDecomp[8][i] = totalsDecomp[8][i] + totalsDecomp[7][i];
						log.debug("a " + totalsDecomp[8][i]);
					}
					;
					// }
					// totalsDecomp[8][i]=totalsDecomp[8][i];
					if (totalsDecomp[8][i] != 0)
					{
						percentDecomp[8][i] = totalsDecomp[8][i] * 100.0 / totalsDecomp[1][i];
					}
					else
					{
						percentDecomp[8][i] = 0.0;
					}
					// log.debug("the combo 1 total is "+totalsDecomp[8][i]+" "+percentDecomp[8][i]);
					// log.debug("the combo 2 before total is "+totalsDecomp[9][i]);
					// for(int k=0; k<combo2.length(); k++){
					if (jCheckdormant2)
					{
						totalsDecomp[9][i] = totalsDecomp[9][i] + totalsDecomp[3][i];
					}
					;
					if (jCheckeewood2)
					{
						totalsDecomp[9][i] = totalsDecomp[9][i] + totalsDecomp[4][i];
					}
					;
					if (jCheckmewood2)
					{
						totalsDecomp[9][i] = totalsDecomp[9][i] + totalsDecomp[5][i];
					}
					;
					if (jChecklewood2)
					{
						totalsDecomp[9][i] = totalsDecomp[9][i] + totalsDecomp[6][i];
					}
					;
					if (jChecklatewood2)
					{
						totalsDecomp[9][i] = totalsDecomp[9][i] + totalsDecomp[7][i];
					}
					;
					
					// }
					// totalsDecomp[9][i]=totalsDecomp[9][i];
					log.debug("the combo 2  total is " + totalsDecomp[9][i]);
					if (totalsDecomp[9][i] != 0)
					{
						percentDecomp[9][i] = totalsDecomp[9][i] * 100.0 / totalsDecomp[1][i];
					}
					else
					{
						percentDecomp[9][i] = 0.0;
					}
					log.debug("the combo 2  total is " + totalsDecomp[9][i] + " " + percentDecomp[9][i]);
				}
				else if (eventTypeToProcess.equals(EventTypeToProcess.FIRE_AND_INJURY_EVENT))
				{
					
					totalsDecomp[0][i] = myReader.get(i).getTotals()[0] + myReader.get(i).getTotals()[1];
					percentDecomp[0][i] = -99.0;
					totalsDecomp[1][i] = (myReader.get(i).getTotals()[0] - myReader.get(i).getTotals()[7])
							+ (myReader.get(i).getTotals()[1] - myReader.get(i).getTotals()[13]);
					totalsDecomp[2][i] = myReader.get(i).getTotals()[7] + myReader.get(i).getTotals()[13];
					if (totalsDecomp[0][i] != 0)
					{
						percentDecomp[1][i] = totalsDecomp[1][i] * 100.0 / totalsDecomp[0][i];
						percentDecomp[2][i] = totalsDecomp[2][i] * 100.0 / totalsDecomp[0][i];
					}
					else
					{
						percentDecomp[1][i] = -99.0;
						percentDecomp[2][i] = -99.0;
					}
					for (int j = 3; j < 8; j++)
					{
						totalsDecomp[j][i] = myReader.get(i).getTotals()[j - 1] + myReader.get(i).getTotals()[j + 5];
						if (totalsDecomp[1][i] != 0)
						{
							percentDecomp[j][i] = totalsDecomp[j][i] * 100.0 / totalsDecomp[1][i];
						}
						else
						{
							percentDecomp[j][i] = -99.0;
						}
						
					}
					/*
					 * Find the Totals and percents of combo1 and combo 2
					 */
					// log.debug("the combo 1 before total is "+totalsDecomp[8][i]);
					// for(int k=0; k<combo1.length(); k++){
					if (jCheckdormant1)
					{
						totalsDecomp[8][i] = totalsDecomp[8][i] + totalsDecomp[3][i];
						log.debug("Dd " + totalsDecomp[8][i]);
					}
					;
					if (jCheckeewood1)
					{
						totalsDecomp[8][i] = totalsDecomp[8][i] + totalsDecomp[4][i];
						log.debug("Ee " + totalsDecomp[8][i]);
					}
					;
					if (jCheckmewood1)
					{
						totalsDecomp[8][i] = totalsDecomp[8][i] + totalsDecomp[5][i];
						log.debug("Mm " + totalsDecomp[8][i]);
					}
					;
					if (jChecklewood1)
					{
						totalsDecomp[8][i] = totalsDecomp[8][i] + totalsDecomp[6][i];
						log.debug("Ll " + totalsDecomp[8][i]);
					}
					;
					if (jChecklatewood1)
					{
						totalsDecomp[8][i] = totalsDecomp[8][i] + totalsDecomp[7][i];
						log.debug("Aa " + totalsDecomp[8][i]);
					}
					;
					// }
					// totalsDecomp[8][i]=totalsDecomp[8][i];
					if (totalsDecomp[8][i] != 0)
					{
						percentDecomp[8][i] = totalsDecomp[8][i] * 100.0 / totalsDecomp[1][i];
					}
					else
					{
						percentDecomp[8][i] = 0.0;
					}
					// log.debug("the combo 1 total is "+totalsDecomp[8][i]+" "+percentDecomp[8][i]);
					// log.debug("the combo 2 before total is "+totalsDecomp[9][i]);
					// for(int k=0; k<combo2.length(); k++){
					if (jCheckdormant2)
					{
						totalsDecomp[9][i] = totalsDecomp[9][i] + totalsDecomp[3][i];
					}
					;
					if (jCheckeewood2)
					{
						totalsDecomp[9][i] = totalsDecomp[9][i] + totalsDecomp[4][i];
					}
					;
					if (jCheckmewood2)
					{
						totalsDecomp[9][i] = totalsDecomp[9][i] + totalsDecomp[5][i];
					}
					;
					if (jChecklewood2)
					{
						totalsDecomp[9][i] = totalsDecomp[9][i] + totalsDecomp[6][i];
					}
					;
					if (jChecklatewood2)
					{
						totalsDecomp[9][i] = totalsDecomp[9][i] + totalsDecomp[7][i];
					}
					;
					
					// }
					// totalsDecomp[9][i]=totalsDecomp[9][i];
					log.debug("the combo 2  total is " + totalsDecomp[9][i]);
					if (totalsDecomp[9][i] != 0)
					{
						percentDecomp[9][i] = totalsDecomp[9][i] * 100.0 / totalsDecomp[1][i];
					}
					else
					{
						percentDecomp[9][i] = 0.0;
					}
					log.debug("the combo 2  total is " + totalsDecomp[9][i] + " " + percentDecomp[9][i]);
					
				}
				else
				{
					
					log.error("Unsupported event type caught");
				}
				
			} // end of i loop processing each file
			
			// setCursor(Cursor.getDefaultCursor());
			
			/*
			 * create JFileChooser object to generate a browsing capabilities
			 */
			JFileChooser fileBrowse = new JFileChooser();
			if (outputFile == null)
			{
				fileBrowse = new JFileChooser(savePath.substring(0, savePath.lastIndexOf(File.separator)));
				
				// set multiselect on (even though we don't need it)
				fileBrowse.setMultiSelectionEnabled(true);
				
				// set file and folder directive
				fileBrowse.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
				
				// set file type: coma delimited file csv
				FileFilter filter1 = new CSVFileFilter();
				fileBrowse.setFileFilter(filter1);
				
				// set dialog text: select the name and location of the matrix files
				fileBrowse.setDialogTitle("Select the name and location of the Stats Summary file:");
				
				// here we get the save button information
				fileBrowseReturn = fileBrowse.showSaveDialog(parent);
				
				if (fileBrowseReturn == 0)
				{
					/*
					 * set the save file
					 */
					outputFile = fileBrowse.getSelectedFile();
					log.debug("DEBUG: fileBrowse.getSelectedFile = " + fileBrowse.getSelectedFile().toString());
					int l = outputFile.getName().length();
					/*
					 * set the extension of the file
					 */
					if (l <= 4 || !(outputFile.getName().substring(l - 4, l).equals(".csv")))
					{
						// seasonTable = new File(outputFile.getAbsolutePath()+"seasontable.csv");
						outputFile = new File(outputFile.getAbsolutePath() + "summaryseason.csv");
						
					}
				}
			}
			
			/*
			 * create the writer object for each of the files to be created
			 */
			Writer wr;
			// Writer wrSTable;
			
			/*
			 * set delimiter in this case we are using comas ","
			 */
			String delim = ",";
			
			/*
			 * Start writing information into the files
			 */
			try
			{
				wr = new BufferedWriter(new FileWriter(outputFile));
				
				/*
				 * write the heading to the files
				 */
				String buffer = "";
				wr.write("Parameter" + delim);
				for (int i = 0; i < inputFile.length; i++)
				{
					buffer = buffer + inputFile[i].getLabel() + delim;
				}
				wr.write(buffer.substring(0, buffer.length() - 1) + System.getProperty("line.separator"));
				buffer = "";
				// wr.write(" " +delim);
				/*
				 * for(int i=0;i<inputFile.length;i++){ wr.write("Totals" + delim+ "Percent (%)" + delim); }
				 */
				// wr.write(System.getProperty("line.separator"));
				
				// TOTALS Line
				wr.write(seaspertableparam[0] + delim);
				for (int k = 0; k < inputFile.length; k++)
				{
					if (totalsDecomp[0][k] != 0)
					{
						buffer = buffer + Double.valueOf(totalsDecomp[0][k]).intValue() + delim;
					}
					else
					{
						buffer = buffer + " NA" + delim;
					}
				} // end of k loop for number of files
				wr.write(buffer.substring(0, buffer.length() - 1) + System.getProperty("line.separator"));
				buffer = "";
				
				// NUMBERS Lines
				for (int j = 1; j < seaspertableparam.length; j++)
				{
					wr.write("Number " + seaspertableparam[j] + delim);
					for (int k = 0; k < inputFile.length; k++)
					{
						if (totalsDecomp[0][k] != 0)
						{
							if (j == 0)
							{
								buffer = buffer + Double.valueOf(totalsDecomp[j][k]).intValue() + delim;
							}
							else
							{
								if (totalsDecomp[1][k] != 0)
								{
									buffer = buffer + Double.valueOf(totalsDecomp[j][k]).intValue() + delim;
								}
								else
								{
									buffer = buffer + Double.valueOf(totalsDecomp[j][k]).intValue() + delim;
								}
							}
							
						}
						else
						{
							buffer = buffer + " NA" + delim;
						}
					} // end of k loop for number of files
					wr.write(buffer.substring(0, buffer.length() - 1) + System.getProperty("line.separator"));
					buffer = "";
					
				}
				
				// PERCENTAGES Lines
				for (int j = 1; j < seaspertableparam.length; j++)
				{
					
					wr.write("Percentage " + seaspertableparam[j] + delim);
					for (int k = 0; k < inputFile.length; k++)
					{
						if (totalsDecomp[0][k] != 0)
						{
							if (j == 0)
							{
								buffer = buffer + "NA" + delim;
							}
							else
							{
								if (totalsDecomp[1][k] != 0)
								{
									buffer = buffer + onePlace.format(percentDecomp[j][k]) + delim;
								}
								else
								{
									buffer = buffer + "NA" + delim;
								}
							}
							
						}
						else
						{
							buffer = buffer + " NA" + delim;
						}
					} // end of k loop for number of files
					wr.write(buffer.substring(0, buffer.length() - 1) + System.getProperty("line.separator"));
					buffer = "";
					
				} // end of j loop for seasonality summary parameters
				wr.close();
				//
				//
				// wrSTable = new BufferedWriter(new FileWriter(seasonTable));
				/*
				 * write the heading to the files
				 */
				// wrSTable.write("table" +delim);
				// for(int i=0;i<inputFile.length;i++){
				// wrSTable.write(inputFile[i].getName().substring(0,inputFile[i].getName().length()-4) + delim);
				// }
				// wrSTable.write(System.getProperty("line.separator"));
				// for(int j=0;j<fixvalt.length;j++){
				// wrSTable.write(threePlace.format(fixvalt[j])+delim);
				// for(int k=0;k<inputFile.length; k++){
				// wrSTable.write(twoPlace.format(ExceeProbcomp[j][k])+delim);
				
				// }
				// wrSTable.write(System.getProperty("line.separator"));
				// }
				
				// wr.close();
				// wrSTable.close();
				
			} // end of Try
			catch (IOException ex)
			{
				ex.printStackTrace();
			}
			finally
			{
				
			}
			
		} // end of if for at least one file selected and one analysis (if run))
		else
		{
			// JOptionPane.showMessageDialog(null,
			// "Eggs are not supposed to be green.\nSelect at least One file and At least one analysis before continuing.", "Warning",
			// JOptionPane.WARNING_MESSAGE);
		}
	}// end of if action perform for the run button
	
	/**
	 * Check the input parameters for sanity
	 * 
	 * @param inputFile - the input filename
	 * @param jCheckdormant1 - is dormant in first season
	 * @param jCheckeewood1 - is early earlywood in first season
	 * @param jCheckmewood1 - is middle earlywood in first season
	 * @param jChecklewood1 - is late earlywood in first season
	 * @param jChecklatewood1 - is latewood in first season
	 * @param jCheckdormant2 - is dormant in second season
	 * @param jCheckeewood2 - is early earlywood in second season
	 * @param jCheckmewood2 - is middle earlywood in second season
	 * @param jChecklewood2 - is late earlywood in second season
	 * @param jChecklatewood2 - is latewood in second season
	 * @param jTextBeginningYear1 - first year in range to calculate
	 * @param jTextEndingYear - end year in range to calculate
	 * @param eventType - whether to do calculations on injuries or fires
	 * @return
	 */
	private static boolean runSanityChecks(File[] inputFile, Boolean jCheckdormant1, Boolean jCheckeewood1, Boolean jCheckmewood1,
			Boolean jChecklewood1, Boolean jChecklatewood1, Boolean jCheckdormant2, Boolean jCheckeewood2, Boolean jCheckmewood2,
			Boolean jChecklewood2, Boolean jChecklatewood2, Integer jTextBeginningYear1, Integer jTextEndingYear,
			EventTypeToProcess eventType) {
	
		Boolean run = false;
		
		// FIRST CHECK
		if (inputFile != null)
		{
			// log.debug("I am here before the checks and inputFile is not null");
			run = true;
			// SECOND CHECK
			if ((jCheckdormant1 || jCheckeewood1 || jCheckmewood1 || jChecklewood1 || jChecklatewood1)
					&& (jCheckdormant2 || jCheckeewood2 || jCheckmewood2 || jChecklewood2 || jChecklatewood2))
			{
				// if (jCheckJoin.isSelected()||jCheckComp.isSelected()||jCheckJoinnumb.isSelected()){
				run = true;
				// THIRD CHECK
				if (jTextBeginningYear1 <= jTextEndingYear)
				{
					run = true;
					// Fourth check
					if (eventType == null)
					{
						run = false;
						JOptionPane.showMessageDialog(null, "No event type specified", "Warning", JOptionPane.WARNING_MESSAGE);
					}
				}
				else
				{
					run = false;
					JOptionPane.showMessageDialog(null, "Trees cannot grow after they die :'(.", "Warning", JOptionPane.WARNING_MESSAGE);
				}
			}
			else
			{
				run = false;
				JOptionPane.showMessageDialog(null, "At least one season must be selected in eacho composition.", "Warning",
						JOptionPane.WARNING_MESSAGE);
			}
		} // end of if for checks file selected, file type selected and begining and end year correct (201-229)
		else
		{
			run = false;
			JOptionPane.showMessageDialog(null, "Select at least one file.", "Warning", JOptionPane.WARNING_MESSAGE);
		} // end of else of if checks (201-229)
		
		return run;
	}
	
}
