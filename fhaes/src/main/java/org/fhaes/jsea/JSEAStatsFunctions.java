/**************************************************************************************************
 * Fire History Analysis and Exploration System (FHAES), Copyright (C) 2015
 * 
 * Contributors: Elena Velasquez, Hidayatullah Ahsan, Joshua Brogan, and Peter Brewer
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
package org.fhaes.jsea;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.Random;

import org.apache.commons.math.stat.StatUtils;
import org.apache.commons.math.stat.descriptive.DescriptiveStatistics;
import org.fhaes.segmentation.SegmentModel;
import org.fhaes.segmentation.SegmentTable;
import org.jfree.chart.ChartUtilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.itextpdf.text.Document;
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

/**
 * JSEAStatsFunctions Class.
 */
public class JSEAStatsFunctions {
	
	private static final Logger log = LoggerFactory.getLogger(JSEAStatsFunctions.class);
	
	// DATA MEMBERS
	String titleForRun;
	String outputFilePrefix;
	Integer yearsPriorToEvent;
	Integer yearsAfterTheEvent;
	Integer numberOfSimulations;
	Integer seedNumber;
	Integer firstYearOfProcess;
	Integer lastYearOfProcess;
	boolean randomSampling;
	boolean includeIncompleteEpochs;
	boolean usingSegmentation;
	SegmentTable segmentTable;
	String chronologyFile;
	boolean alphaLevel95;
	boolean alphaLevel99;
	boolean alphaLevel999;
	
	// statistical info on the Actual Series
	double meanSensitivity;
	double mean;
	double std;
	double median;
	double kurt;
	double skew;
	
	// segmentation info
	int startSegmentLoop;
	ArrayList<Integer> firstYearsArray;
	ArrayList<Integer> lastYearsArray;
	// Statistical info on the Adjusted Series and info
	double meanSensitivityAdj;
	double meanAdj;
	double stdAdj;
	double medianAdj;
	double kurtAdj;
	double skewAdj;
	double autocorrelationAdj;
	double autoNumeSum;
	double autoDenSum;
	int beginingYearAdj;
	int lastYearAdj;
	int numberOfEventsinAdj;
	double meanDiffBetweenEvents;
	double sumOfDiff;
	Integer lengthOfWindow;
	// Events info
	int[] keyEventsAdj;
	int[] keyEvents;
	int[] keyEventsAdjBeYear;
	int[] keyEventsAdjLaYear;
	int[] Simnumdates;
	double[] meanByWindow;
	double[] varianceByWindow;
	double[] standardDevByWindow;
	double[] maximunByWindow;
	double[] minimunByWindow;
	double[][] eventWindowsAct;
	int[][] eventWindowPattern;
	double[] tempVar;
	double[] stdDevMultiplier = { 1.960, 2.575, 3.294 };
	double[][] leftEndPoint;
	double[][] rightEndPoint;
	// Arrays
	double[] dchronoActual;
	double[] chronoAdj;
	double[] diffBetweenEvents;
	
	// simulation stuff
	double[] meanMeanByWindow;
	double[] varianceMeanByWindow;
	double[] standardDevMeanByWindow;
	double[] maxMeanByWindow;
	double[] minMeanByWindow;
	double[][] leftEndPointSim;
	double[][] rightEndPointSim;
	int[] percentileMark;
	double[][] leftEndPointPer;
	double[][] rightEndPointPer;
	int temp;
	int alphaLevel;
	
	Integer[] yearsActual;
	// Integer[] keyEvents;
	Double[] chronoActual;
	// Integer[] keyEventsAdjt;
	
	ArrayList<BarChartParametersModel> chartList = new ArrayList<BarChartParametersModel>();
	ArrayList<Integer> chronologyYears;
	ArrayList<Double> chronologyActual;
	ArrayList<Integer> events;
	ArrayList<Integer> kevents;
	ArrayList<Integer> keventsinadj;
	ArrayList<Integer> keventsinadjyeprior;
	ArrayList<Integer> keventsinadjyeafter;
	ArrayList<Double> test;
	ArrayList<Double> simulationtest;
	ArrayList<Integer> segmentByears;
	ArrayList<Integer> segmentLyears;
	
	// print stuff
	ArrayList<PdfPTable> printTableAct = new ArrayList<PdfPTable>();
	ArrayList<PdfPTable> printTableSim = new ArrayList<PdfPTable>();
	ArrayList<Paragraph> para1 = new ArrayList<Paragraph>();
	ArrayList<Paragraph> para2 = new ArrayList<Paragraph>();
	ArrayList<String> dpara1;
	ArrayList<String> dpara2;
	ArrayList<String> dpara3;
	ArrayList<Boolean> printTableActFlag = new ArrayList<Boolean>();
	ArrayList<Boolean> printTableSimFlag;
	String report;
	String actualTable;
	String simulationTable;
	String cdbuffer;
	String pdfbufferA;
	String pdfbufferB;
	String pdfbufferpar1;
	String pdfbufferpar2;
	Paragraph pdfbufferpar11 = new Paragraph();
	Paragraph pdfbufferpar12 = new Paragraph();
	File outputFile;
	File cdoutputFile;
	File outputpdfFile;
	
	boolean isFirstIteration;
	boolean growth;
	boolean save;
	
	// Default Constructor
	public JSEAStatsFunctions() {
	
		titleForRun = "Title for Chart";
		outputFilePrefix = "ZZZZZ";
		yearsPriorToEvent = 6;
		yearsAfterTheEvent = 4;
		numberOfSimulations = 1256;
		firstYearOfProcess = 0;
		lastYearOfProcess = 2020;
		randomSampling = true;
		includeIncompleteEpochs = false;
		// excludeIncompleteEpochs = true;
		chronologyYears = new ArrayList<Integer>();
		chronologyActual = new ArrayList<Double>();
		events = new ArrayList<Integer>();
		isFirstIteration = true;
		growth = false;
		save = false;
		usingSegmentation = false;
		
		segmentTable = new SegmentTable();
		segmentTable.tableModel.addSegment(new SegmentModel(firstYearOfProcess, lastYearOfProcess));
		segmentTable.setEarliestYear(firstYearOfProcess);
		segmentTable.setLatestYear(lastYearOfProcess);
		
		chronologyFile = " ";
		alphaLevel95 = true;
		alphaLevel99 = false;
		alphaLevel999 = false;
	}
	
	/**
	 * TODO
	 * 
	 * @param titleForRun
	 * @param outputFilePrefix
	 * @param seedNumber
	 * @param yearsPriorToEvent
	 * @param yearsAfterTheEvent
	 * @param numberOfSimulations
	 * @param firstYearOfProcess
	 * @param lastYearOfProcess
	 * @param includeIncompleteEpochs
	 * @param randomSampling
	 * @param chronologyYears
	 * @param chronologyActual
	 * @param events
	 * @param growth
	 * @param save
	 * @param usingSegmentation
	 * @param segmentTable
	 * @param chronologyFile
	 * @param alphaLevel95
	 * @param alphaLevel99
	 * @param alphaLevel999
	 */
	public JSEAStatsFunctions(String titleForRun, String outputFilePrefix, Integer seedNumber, Integer yearsPriorToEvent,
			Integer yearsAfterTheEvent, Integer numberOfSimulations, Integer firstYearOfProcess, Integer lastYearOfProcess,
			boolean includeIncompleteEpochs, boolean randomSampling, ArrayList<Integer> chronologyYears,
			ArrayList<Double> chronologyActual, ArrayList<Integer> events, boolean growth, boolean save, boolean usingSegmentation,
			SegmentTable segmentTable, String chronologyFile, boolean alphaLevel95, boolean alphaLevel99, boolean alphaLevel999) {
	
		long begintime = System.currentTimeMillis();
		this.titleForRun = titleForRun;
		this.outputFilePrefix = outputFilePrefix;
		this.yearsPriorToEvent = yearsPriorToEvent;
		this.yearsAfterTheEvent = yearsAfterTheEvent;
		this.randomSampling = randomSampling;
		this.numberOfSimulations = numberOfSimulations;
		this.seedNumber = seedNumber;
		this.firstYearOfProcess = firstYearOfProcess;
		this.lastYearOfProcess = lastYearOfProcess;
		// this.excludeIncompleteEpochs = excludeIncompleteEpochs;
		this.includeIncompleteEpochs = includeIncompleteEpochs;
		this.chronologyYears = chronologyYears;
		this.chronologyActual = chronologyActual;
		this.events = events;
		this.isFirstIteration = true;
		this.save = save;
		this.growth = growth;
		this.usingSegmentation = usingSegmentation;
		this.segmentTable = segmentTable;
		this.chronologyFile = chronologyFile;
		this.alphaLevel95 = alphaLevel95;
		this.alphaLevel99 = alphaLevel99;
		this.alphaLevel999 = alphaLevel999;
		
		log.debug("this.titleForRun = " + titleForRun);
		log.debug("this.outputFilePrefix = " + outputFilePrefix);
		log.debug("this.yearsPriorToEvent = " + yearsPriorToEvent);
		log.debug("this.yearsAfterTheEvent = " + yearsAfterTheEvent);
		log.debug("this.randomSampling = " + randomSampling);
		log.debug("this.numberOfSimulations = " + numberOfSimulations);
		log.debug("this.seedNumber = " + seedNumber);
		log.debug("this.firstYearOfProcess = " + firstYearOfProcess);
		log.debug("this.lastYearOfProcess = " + lastYearOfProcess);
		// log.debug("this.excludeIncompleteEpochs = "+excludeIncompleteEpochs);
		log.debug("this.includeIncompleteEpochs = " + includeIncompleteEpochs);
		log.debug("this.chronologyYears = " + chronologyYears);
		log.debug("this.chronologyActual = " + chronologyActual);
		log.debug("this.events = " + events);
		log.debug("this.save = " + save);
		log.debug("this.growth = " + growth);
		log.debug("this.usingSegmentation = " + usingSegmentation);
		// log.debug("this.segmentTable = earliestYear " + segmentTable.getEarliestYear() + ", latestYear " + segmentTable.getLatestYear());
		log.debug("this.chronologyFile = " + chronologyFile);
		log.debug("this.alphaLevel95 = " + alphaLevel95);
		log.debug("this.alphaLevel99 = " + alphaLevel99);
		log.debug("this.alphaLevel999 = " + alphaLevel999);
		
		/*
		 * Setting the three decimal format
		 */
		DecimalFormat threePlacess = new DecimalFormat("0.000");
		
		/*
		 * Creating the date of the run of the program
		 */
		Date now = new Date();
		
		/*
		 * Creating the files necessary (two txt files)
		 */
		
		// File outputFile = new File(outputFilePrefix + ".out");
		// Writer wr;
		// String bigbuffer = "";
		
		report = new String("");
		actualTable = new String("");
		simulationTable = new String("");
		cdbuffer = new String("");
		pdfbufferA = new String("");
		pdfbufferB = new String("");
		pdfbufferpar1 = new String("");
		pdfbufferpar2 = new String("");
		
		/*
		 * Converting Arraylists into arrays chronologyActual into chronoActual chronologyYears into yearsActual events into keyEvents
		 */
		
		chronoActual = new Double[chronologyActual.size()];
		chronoActual = chronologyActual.toArray(chronoActual);
		yearsActual = new Integer[chronologyYears.size()];
		yearsActual = chronologyYears.toArray(yearsActual);
		
		Collections.sort(events);
		
		/*
		 * Setting default values for first yearofprocess, lastyearofprocess recall the firstYearchrono is set as the default on the
		 * firtYearOfProcess. also firstYearchrono is set as the default for firstYearsegment lastYearchrono is set as the default of the
		 * lastYearOfProcess
		 */
		
		if (firstYearOfProcess == 0)
		{
			firstYearOfProcess = yearsActual[0];
		}
		
		if (lastYearOfProcess == 0)
		{
			lastYearOfProcess = yearsActual[yearsActual.length];
		}
		
		if (numberOfSimulations == 0)
		{
			System.out.println("the number of simulations need to be set");
		}
		
		/*
		 * 1. statistical Analysis of the whole time series chronology 2. statistical Analysis of the adjusted time series chronologyAdj 3.
		 * statistical Analysis of the whole Event list events 4. print using the method printReport
		 */
		
		// Statistical Analysis for the whole Climate Series
		
		DescriptiveStatistics stats = new DescriptiveStatistics();
		dchronoActual = new double[chronologyActual.size()];
		
		// Add the data from the array
		
		for (int i = 0; i < chronoActual.length; i++)
		{
			stats.addValue(chronoActual[i].doubleValue());
			dchronoActual[i] = chronoActual[i].doubleValue();
		}
		
		// Obtain the mean sensitivity
		
		meanSensitivity = 0;
		for (int i = 1; i < chronoActual.length; i++)
		{
			double senDenominator = Math.abs(dchronoActual[i]) + Math.abs(dchronoActual[i - 1]);
			if (senDenominator != 0)
			{
				meanSensitivity = meanSensitivity + Math.abs(2 * (dchronoActual[i] - dchronoActual[i - 1])) / senDenominator;
			}
		}
		meanSensitivity = meanSensitivity / (dchronoActual.length - 1);
		
		/*
		 * Obtain and display the general statistical information on the whole climate series.
		 */
		
		mean = stats.getMean();
		std = stats.getStandardDeviation();
		median = StatUtils.percentile(dchronoActual, 50);
		kurt = stats.getKurtosis();
		skew = stats.getSkewness();
		
		/*
		 * is segmentlength is different than 0 find the beginning and end year for each segment
		 */
		
		firstYearsArray = new ArrayList<Integer>();
		lastYearsArray = new ArrayList<Integer>();
		
		// NO SEGMENTATION IS USED
		if (!usingSegmentation)
		{
			firstYearsArray.add(firstYearOfProcess);
			lastYearsArray.add(lastYearOfProcess);
		}
		
		// SEGMENTATION IS USED AND HAS BEEN DEFINED
		if (usingSegmentation)
		{
			for (int i = 0; i < segmentTable.tableModel.getSegments().size(); i++)
			{
				firstYearsArray.add(segmentTable.tableModel.getSegment(i).getFirstYear());
				lastYearsArray.add(segmentTable.tableModel.getSegment(i).getLastYear());
			}
		}
		
		/*
		 * set up the loop for the typed of segmentation
		 */
		
		/*
		 * set the adjusted time series 1. set up the loop for the typed of segmentation 3.find the index of the first event in the actual
		 * array. 2. adjust the series by yearsActual[indexofthefirstevent]-yearsPriortToEvent 3. adjust the series by
		 * yearsActual[indexofthelasteventinseries]+yearsAfterTheEvent
		 */
		
		for (int segmentIndex = 0; segmentIndex < firstYearsArray.size(); segmentIndex++)
		{
			beginingYearAdj = chronologyYears.get(0).intValue();
			lastYearAdj = chronologyYears.get(chronologyYears.size() - 1).intValue();
			
			firstYearOfProcess = firstYearsArray.get(segmentIndex);
			lastYearOfProcess = lastYearsArray.get(segmentIndex);
			if (firstYearOfProcess.intValue() > beginingYearAdj)
			{
				beginingYearAdj = firstYearOfProcess.intValue();
			}
			if (lastYearOfProcess.intValue() < lastYearAdj)
			{
				lastYearAdj = lastYearOfProcess.intValue();
			}
			
			/*
			 * Obtain and display information on the Events actual Time span same as the adjusted. number of events. Events.size() and total
			 * number of Events used. Mean years between events minimun differece between event years. *
			 */
			
			keventsinadj = new ArrayList<Integer>();
			keventsinadjyeprior = new ArrayList<Integer>();
			keventsinadjyeafter = new ArrayList<Integer>();
			kevents = new ArrayList<Integer>();
			numberOfEventsinAdj = 0;
			for (int i = 0; i < events.size(); i++)
			{
				if (chronologyYears.contains(events.get(i)))
				{
					// System.out.println("the chronologyYears contains event " + i + "\t"
					// + beginingYearAdj + "\t" + lastYearAdj);
					if ((beginingYearAdj <= events.get(i).intValue()) && (events.get(i).intValue() <= lastYearAdj))
					{
						kevents.add(events.get(i));
					}
				}
				if ((chronologyYears.contains(events.get(i))) && (!includeIncompleteEpochs))
				{
					if (((events.get(i).intValue() - beginingYearAdj) >= yearsPriorToEvent.intValue())
							&& ((lastYearAdj - events.get(i).intValue()) >= yearsAfterTheEvent.intValue()))
					{
						numberOfEventsinAdj = numberOfEventsinAdj + 1;
						keventsinadj.add(events.get(i));
					}
					;
				}
				;// end of exclude incomplete epochs
				if ((chronologyYears.contains(events.get(i))) && (includeIncompleteEpochs))
				{
					if ((beginingYearAdj <= events.get(i).intValue()) && (events.get(i).intValue() <= lastYearAdj))
					{
						numberOfEventsinAdj = numberOfEventsinAdj + 1;
						keventsinadj.add(events.get(i));
						//
						if ((events.get(i).intValue() - beginingYearAdj) < yearsPriorToEvent.intValue())
						{
							keventsinadjyeprior.add(events.get(i).intValue() - beginingYearAdj);
						}
						else
						{
							keventsinadjyeprior.add(yearsPriorToEvent);
						}
						if ((lastYearAdj - events.get(i).intValue()) < yearsAfterTheEvent.intValue())
						{
							keventsinadjyeafter.add(lastYearAdj - events.get(i).intValue());
						}
						else
						{
							keventsinadjyeafter.add(yearsAfterTheEvent.intValue());
						}
						//
					}
					;
				}
				; // end of include incomplete
			}
			;// end of the loop for all events
			
			/*
			 * set up if statement so that if we have two or less key events in the chronology we do not do anything
			 */
			
			// System.out.println("size of kevents is " + kevents.size());
			if (kevents.size() >= 2)
			{
				keyEvents = new int[kevents.size()];
				for (int i = 0; i < kevents.size(); i++)
				{
					keyEvents[i] = kevents.get(i).intValue();
				}
				;
				
				/*
				 * Sorting keyEvents
				 */
				
				Arrays.sort(keyEvents);
				
				if (keventsinadj.size() >= 2)
				{
					
					keyEventsAdj = new int[numberOfEventsinAdj];
					keyEventsAdjBeYear = new int[numberOfEventsinAdj];
					keyEventsAdjLaYear = new int[numberOfEventsinAdj];
					for (int i = 0; i < keventsinadj.size(); i++)
					{
						keyEventsAdj[i] = keventsinadj.get(i).intValue();
						keyEventsAdjBeYear[i] = keyEventsAdj[i] - yearsPriorToEvent.intValue();
						keyEventsAdjLaYear[i] = keyEventsAdj[i] + yearsAfterTheEvent.intValue();
					}
					;
					
					Arrays.sort(keyEventsAdj);
					// Calculate the difference between events load in array
					diffBetweenEvents = new double[keyEvents.length - 1];
					sumOfDiff = 0;
					for (int i = 1; i < keyEvents.length; i++)
					{
						diffBetweenEvents[i - 1] = keyEvents[i] - keyEvents[i - 1];
						sumOfDiff = sumOfDiff + diffBetweenEvents[i - 1];
					}
					;
					
					// Calculate the mean difference between events =
					// sum(y(i)-y(i-1))/total number of differences
					
					meanDiffBetweenEvents = sumOfDiff / diffBetweenEvents.length;
					
					// adjusting the beginning year that that it account for the events
					// years
					// and the beginning year of the process etc
					
					beginingYearAdj = Math.max(beginingYearAdj, (keyEvents[0] - yearsPriorToEvent));
					lastYearAdj = Math.min(lastYearAdj, (keyEvents[keyEvents.length - 1] + yearsAfterTheEvent));
					
					DescriptiveStatistics statsAdj = new DescriptiveStatistics();
					
					chronoAdj = new double[lastYearAdj - beginingYearAdj + 1];
					
					// Add data from the array
					
					for (int i = beginingYearAdj; i < lastYearAdj + 1; i++)
					{
						statsAdj.addValue(chronoActual[chronologyYears.indexOf(i)].doubleValue());
						chronoAdj[i - beginingYearAdj] = chronoActual[chronologyYears.indexOf(i)].doubleValue();
					}
					;
					
					// Obtain the mean sensativity
					
					meanSensitivityAdj = 0;
					for (int i = 1; i < chronoAdj.length; i++)
					{
						double senDenominatorAdj = Math.abs(chronoAdj[i]) + Math.abs(chronoAdj[i - 1]);
						if (senDenominatorAdj != 0)
						{
							meanSensitivityAdj = meanSensitivityAdj + Math.abs(2 * (chronoAdj[i] - chronoAdj[i - 1])) / senDenominatorAdj;
						}
					}
					
					meanSensitivityAdj = meanSensitivityAdj / (chronoAdj.length - 1);
					
					/*
					 * Obtain and display the general statistical information on the whole time series data.
					 */
					
					meanAdj = statsAdj.getMean();
					stdAdj = statsAdj.getStandardDeviation();
					medianAdj = StatUtils.percentile(chronoAdj, 50);
					kurtAdj = statsAdj.getKurtosis();
					skewAdj = statsAdj.getSkewness();
					// new PearsonsCorrelation().correlation(chronoAdj, chronoAdj);
					double autoNumSum = 0.0;
					double autoDemSum = 0.0;
					System.out.println("the length of chronoAdj is " + chronoAdj.length);
					for (int j = 0; j < (chronoAdj.length - 1); j++)
					{
						// System.out.println("j is: "+j + "mean is "+ meanAdj + "chronoadj is "+chronoAdj[j] );
						autoNumSum = autoNumSum + (chronoAdj[j] - meanAdj) * (chronoAdj[j + 1] - meanAdj);
					}
					for (int j = 0; j < chronoAdj.length; j++)
					{
						autoDemSum = autoDemSum + (chronoAdj[j] - meanAdj) * (chronoAdj[j] - meanAdj);
					}
					autocorrelationAdj = autoNumSum / autoDemSum;
					// autocorrelationAdj=new PearsonsCorrelation().correlation(chronoAdj, chronoAdj);
					System.out.println("the autocorrelation of the adjustchonology is: " + autocorrelationAdj);
					
					/*
					 * Calculate the statistical information per window of the Actual Events. load the values of the choronoActual per
					 * window in window into a two dimensional array calculate the mean per row calculate the standard deviation per row
					 * calculate end values of the confidence interval for 95%,99%.99.9% per row
					 */
					
					// Definition of the length of the window of interest.
					
					lengthOfWindow = yearsPriorToEvent + yearsAfterTheEvent + 1;
					
					// define the two dimensional array for the calculations of the Actual
					// Event windows stats
					
					meanByWindow = new double[lengthOfWindow];
					varianceByWindow = new double[lengthOfWindow];
					standardDevByWindow = new double[lengthOfWindow];
					maximunByWindow = new double[lengthOfWindow];
					minimunByWindow = new double[lengthOfWindow];
					eventWindowsAct = new double[lengthOfWindow][];
					eventWindowPattern = new int[lengthOfWindow][];
					Simnumdates = new int[lengthOfWindow];
					test = new ArrayList<Double>();
					for (int k = 0; k < lengthOfWindow; k++)
					{
						eventWindowPattern[k] = new int[keventsinadj.size()];
						int kWindow = k - yearsPriorToEvent.intValue();
						for (int i = 0; i < keventsinadj.size(); i++)
						{
							if ((beginingYearAdj <= (keventsinadj.get(i).intValue() + kWindow))
									&& ((keventsinadj.get(i).intValue() + kWindow) <= lastYearAdj))
							{
								test.add(chronologyActual.get(chronologyYears.indexOf(keventsinadj.get(i).intValue() + kWindow)));
								eventWindowPattern[k][i] = 1;
							}
							else
							{
								eventWindowPattern[k][i] = 0;
							}
						}
						Simnumdates[k] = test.size();
						eventWindowsAct[k] = new double[test.size()]; // new line
						for (int ij = 0; ij < test.size(); ij++)
						{
							eventWindowsAct[k][ij] = test.get(ij).doubleValue();
						}
						test.clear();
						meanByWindow[k] = StatUtils.mean(eventWindowsAct[k]);
						varianceByWindow[k] = StatUtils.variance(eventWindowsAct[k]);
						standardDevByWindow[k] = Math.sqrt(varianceByWindow[k]);
						maximunByWindow[k] = StatUtils.max(eventWindowsAct[k]);
						minimunByWindow[k] = StatUtils.min(eventWindowsAct[k]);
					} // end k loop
					Arrays.sort(Simnumdates);
					temp = Simnumdates[0];
					leftEndPoint = new double[lengthOfWindow][3];
					rightEndPoint = new double[lengthOfWindow][3];
					for (int i = 0; i < lengthOfWindow; i++)
					{
						for (int j = 0; j < 3; j++)
						{
							leftEndPoint[i][j] = meanByWindow[i] - stdDevMultiplier[j] * standardDevByWindow[i];
							rightEndPoint[i][j] = meanByWindow[i] + stdDevMultiplier[j] * standardDevByWindow[i];
						}
					}
					
					/*
					 * calculate the percentile Marks for simulation table
					 */
					
					percentileMark = new int[4];
					percentileMark[1] = (int) Math.max(Math.round(this.numberOfSimulations / 40.0), 1) - 1;
					percentileMark[3] = (int) Math.max(Math.round(this.numberOfSimulations / 200.0), 1) - 1;
					percentileMark[0] = this.numberOfSimulations - percentileMark[1] - 1;
					percentileMark[2] = this.numberOfSimulations - percentileMark[3] - 1;
					
					// System.out.println("percentailmarks "+percentileMark[0]+" , "
					// +percentileMark[1]+" , " + percentileMark[2]+" , " +
					// percentileMark[3]);
					
					// start the simulations: by selecting events.size() number of random
					// years
					
					Random myrand = new Random();
					myrand.setSeed(seedNumber);
					
					double[][] meanByWindowSim = new double[lengthOfWindow][this.numberOfSimulations];
					int[] eventYearSimulation = new int[keventsinadj.size()];// changed
																				// keventsinadj.size()
																				// by temp
					double[][] eventWindowsSims = new double[lengthOfWindow][];
					simulationtest = new ArrayList<Double>();
					
					/*
					 * Simulation Start
					 */
					System.out.println("Before Simulation Time " + (System.currentTimeMillis() - begintime) / 1000F);
					for (int ii = 0; ii < this.numberOfSimulations; ii++)
					{
						for (int i = 0; i < keventsinadj.size(); i++)
						{
							// Here add the two if statement for include and exclude so the
							// range of the selection of years
							if (includeIncompleteEpochs)
							{
								eventYearSimulation[i] = (beginingYearAdj + keventsinadjyeprior.get(i).intValue())
										+ myrand.nextInt((lastYearAdj - keventsinadjyeafter.get(i).intValue())
												- (beginingYearAdj + keventsinadjyeprior.get(i).intValue()) + 1);
							}
							if (!includeIncompleteEpochs)
							{
								eventYearSimulation[i] = (beginingYearAdj + 6)
										+ myrand.nextInt((lastYearAdj - 4) - (beginingYearAdj + 6) + 1);
							}
							
						} // end i loop
						Arrays.sort(eventYearSimulation);
						// System.out.println("after selection of key events in sim " + ii + " time " + (System.currentTimeMillis() -
						// start) / 1000F);
						/*
						 * Once the events have been simulated build the two sised matrix (lengthOfWindow) by events.size()
						 */
						
						for (int k = 0; k < lengthOfWindow; k++)
						{
							eventWindowsSims[k] = new double[keventsinadj.size()];// new line
							int kWindow = k - yearsPriorToEvent.intValue();
							for (int i = 0; i < keventsinadj.size(); i++)
							{
								if (eventWindowPattern[k][i] == 1)
								{
									simulationtest.add(chronologyActual.get(chronologyYears.indexOf(eventYearSimulation[i] + kWindow)));
								}
							} // i loop
							eventWindowsSims[k] = new double[simulationtest.size()]; // new
																						// line
							for (int ij = 0; ij < simulationtest.size(); ij++)
							{
								eventWindowsSims[k][ij] = simulationtest.get(ij).doubleValue();
							} // edn ij loop
							simulationtest.clear();
							meanByWindowSim[k][ii] = StatUtils.mean(eventWindowsSims[k]);
						} // end k loop numberofsimulation loop
					} // end simulatrion loop
					
					System.out.println("I am done with simulation");
					
					// calculate the mean of the means
					double sum = 0.0;
					meanMeanByWindow = new double[lengthOfWindow];
					varianceMeanByWindow = new double[lengthOfWindow];
					standardDevMeanByWindow = new double[lengthOfWindow];
					maxMeanByWindow = new double[lengthOfWindow];
					minMeanByWindow = new double[lengthOfWindow];
					double[] tempMeanMean = new double[this.numberOfSimulations];
					leftEndPointPer = new double[lengthOfWindow][2];
					rightEndPointPer = new double[lengthOfWindow][2];
					
					for (int i = 0; i < lengthOfWindow; i++)
					{
						// int kWindow = i - yearsPriorToEvent.intValue();
						for (int k = 0; k < this.numberOfSimulations; k++)
						{
							// for(int k=0;k < (Integer)numberOfSimulations.intValue();k++){
							if (k < 1)
							{
								// /eSystem.out.println("on the " +i+","+k+" the value is " +
								// meanByWindowSim[i][k]);
							}
							;
							tempMeanMean[k] = meanByWindowSim[i][k];
							sum = sum + tempMeanMean[k];
							// System.out.println("tempMeanMean is " + tempMeanMean[k]);
						}
						meanMeanByWindow[i] = StatUtils.mean(tempMeanMean);
						varianceMeanByWindow[i] = StatUtils.variance(tempMeanMean);
						standardDevMeanByWindow[i] = Math.sqrt(varianceMeanByWindow[i]);
						Arrays.sort(tempMeanMean);
						maxMeanByWindow[i] = StatUtils.max(tempMeanMean);
						minMeanByWindow[i] = StatUtils.min(tempMeanMean);
						
						leftEndPointPer[i][0] = tempMeanMean[percentileMark[1]];
						rightEndPointPer[i][0] = tempMeanMean[percentileMark[0]];
						leftEndPointPer[i][1] = tempMeanMean[percentileMark[3]];
						rightEndPointPer[i][1] = tempMeanMean[percentileMark[2]];
						
						// System.out.println("[ "+
						// Math.round(leftEndPoint[i][j]*1000.0)/1000.0 + " , " +
						// Math.round(rightEndPoint[i][j]*1000.0)/1000.0+"]");
						
						// System.out.println("meanMeanByWindow is " + meanMeanByWindow[i]);
						if (i < 1)
						{
							// /eSystem.out.println("the window "+i+" has mean: " +
							// Math.round(meanMeanByWindow[i]*1000.0)/1000.0);
						}
						;
						// System.out.println("the window "+i+" has variance: " +
						// Math.round(varianceMeanByWindow[i]*1000.0)/1000.0);
						// System.out.println("the window "+i+" has standard dev: " +
						// Math.round(standardDevMeanByWindow[i]*1000.0)/1000.0);
					}
					;// end of i loop
						// }//end of ikj loop
						// Calculate the confidence interval for 95%,99%,99.9%
					
					leftEndPointSim = new double[lengthOfWindow][3];
					rightEndPointSim = new double[lengthOfWindow][3];
					for (int i = 0; i < lengthOfWindow; i++)
					{
						for (int j = 0; j < 3; j++)
						{
							leftEndPointSim[i][j] = meanMeanByWindow[i] - stdDevMultiplier[j] * standardDevMeanByWindow[i];
							rightEndPointSim[i][j] = meanMeanByWindow[i] + stdDevMultiplier[j] * standardDevMeanByWindow[i];
							// System.out.println("[ "+
							// Math.round(leftEndPoint[i][j]*1000.0)/1000.0 + " , " +
							// Math.round(rightEndPoint[i][j]*1000.0)/1000.0+"]");
						}
					}
					// }//end of ikj loop
					
					/*
					 * detecting which p-level was selected in gui
					 */
					if (alphaLevel95)
					{
						alphaLevel = 0;
					}
					else if (alphaLevel99)
					{
						alphaLevel = 1;
					}
					else
					{
						alphaLevel = 2;
					}
					
					/*
					 * adding the chart and the creation on the buffer here
					 */
					BarChartParametersModel m = new BarChartParametersModel(titleForRun, meanByWindow, lengthOfWindow, yearsPriorToEvent,
							yearsAfterTheEvent, leftEndPointSim, rightEndPointSim, outputFilePrefix, alphaLevel, segmentIndex);
					m.setChart(new JSEABarChart(m).getChart());
					this.chartList.add(m);
					
					/*
					 * try { // ChartUtilities.saveChartAsJPEG(new File(outputFilePrefix+"chart"+ikj+ ".jpg"), chart, 500, 300);
					 * ChartUtilities.saveChartAsJPEG(new File(outputFilePrefix+"chart.jpg"), chart, 500, 300); } catch (IOException ex) {
					 * System.err.println(ex.getLocalizedMessage()); }
					 */
					
					// Date now = new Date();
					// System.out.println("the date today is: " + now);
					// adding the cdbuffer stuff
					String delim = ",";
					cdbuffer = cdbuffer + "Range:" + "\n";
					cdbuffer = cdbuffer + beginingYearAdj + delim + lastYearAdj + "\n";
					cdbuffer = cdbuffer + "Lags" + delim + "Events Mean" + delim + "95% CONF INT" + delim + "95% CONF INT" + delim
							+ "99% CONF INT" + delim + "99% CONF INT" + delim + "99.9% CONF INT" + delim + "99.9% CONF INT" + delim + "\n";
					for (int i = 0; i < lengthOfWindow; i++)
					{
						cdbuffer = cdbuffer + (i - yearsPriorToEvent.intValue()) + delim + threePlacess.format(meanByWindow[i]) + delim
								+ threePlacess.format(leftEndPointSim[i][0]) + delim + threePlacess.format(rightEndPointSim[i][0]) + delim
								+ threePlacess.format(leftEndPointSim[i][1]) + "," + threePlacess.format(rightEndPointSim[i][1]) + delim
								+ threePlacess.format(leftEndPointSim[i][2]) + delim + threePlacess.format(rightEndPointSim[i][2]) + "\n";
					}
					// adding the bigbuffer and pdfbufferpar1 stuff
					// Paragraph pdfbufferpar11 = new Paragraph( );
					report = report + "\n";
					report = report + "SUPERPOSED EPOCH ANALYSIS RESULTS" + "\n";
					report = report + "Date: " + now + "\n";
					report = report + "Name of the time series file: " + chronologyFile;
					pdfbufferpar1 = pdfbufferpar1 + "\n";
					pdfbufferpar1 = pdfbufferpar1 + "SUPERPOSED EPOCH ANALYSIS RESULTS" + "\n";
					pdfbufferpar1 = pdfbufferpar1 + "Date: " + now + "\n";
					pdfbufferpar1 = pdfbufferpar1 + "Name of the time series file: " + chronologyFile;
					
					if (firstYearOfProcess.intValue() > chronologyYears.get(0).intValue())
					{
						report = report + "\n" + "First Year= " + firstYearOfProcess;
						pdfbufferpar1 = pdfbufferpar1 + "\n" + "First Year= " + firstYearOfProcess;
					}
					else
					{
						report = report + "\n" + "First Year= " + chronologyYears.get(0);
						pdfbufferpar1 = pdfbufferpar1 + "\n" + "First Year= " + chronologyYears.get(0);
					}
					if (lastYearOfProcess.intValue() < chronologyYears.get(chronologyYears.size() - 1).intValue())
					{
						report = report + "\n" + "Last Year= " + lastYearOfProcess;
						pdfbufferpar1 = pdfbufferpar1 + "\n" + "Last Year= " + lastYearOfProcess;
					}
					else
					{
						report = report + "\n" + "Last Year= " + chronologyYears.get(chronologyYears.size() - 1);
						pdfbufferpar1 = pdfbufferpar1 + "\n" + "Last Year= " + chronologyYears.get(chronologyYears.size() - 1);
					}
					
					/*
					 * Display the general statistical information on the Adjusted time series data.
					 */
					report = report + "\n" + "DESCRIPTIVE STATISTICS INFORMATION ABOUT THE ADJUSTED CONTINUOUS TIME SERIES: " + "\n" + "\n";
					report = report + "\t" + "The adjusted time series RANGES from " + beginingYearAdj + " to " + lastYearAdj + "\n";
					report = report + "\t" + "The NUMBER OF YEARS in the adjusted time series is " + chronoAdj.length + "\n";
					report = report + "\t" + "MEAN of the adjusted time series is " + threePlacess.format(meanAdj) + "\n";
					report = report + "\t" + "MEDIAN of the adjusted time series is " + threePlacess.format(medianAdj) + "\n";
					report = report + "\t" + "MEAN SENSITIVITY for the adjusted time series is " + threePlacess.format(meanSensitivityAdj)
							+ "\n";
					report = report + "\t" + "STANDARD DEVIATION of the adjusted time series is " + threePlacess.format(stdAdj) + "\n";
					report = report + "\t" + "SKEWNESS of the adjusted time series is " + threePlacess.format(skewAdj) + "\n";
					report = report + "\t" + "KURTOSIS of the adjusted time series is  " + threePlacess.format(kurtAdj) + "\n";
					report = report + "\t" + "First Order AUTOCORRELATION Index of the adjusted time series is  "
							+ threePlacess.format(autocorrelationAdj) + "\n";
					
					/*
					 * save the general statistical information on the Adjusted time series data in pdf fie.
					 */
					pdfbufferpar1 = pdfbufferpar1 + "\n" + "DESCRIPTIVE STATISTICS INFORMATION ABOUT THE ADJUSTED CONTINUOUS TIME SERIES: "
							+ "\n" + "\n";
					pdfbufferpar1 = pdfbufferpar1 + "\t" + "The adjusted time series RANGES from " + beginingYearAdj + " to " + lastYearAdj
							+ "\n";
					pdfbufferpar1 = pdfbufferpar1 + "\t" + "The NUMBER OF YEARS in the adjusted time series is " + chronoAdj.length + "\n";
					pdfbufferpar1 = pdfbufferpar1 + "\t" + "MEAN of the adjusted time series is " + threePlacess.format(meanAdj) + "\n";
					pdfbufferpar1 = pdfbufferpar1 + "\t" + "MEDIAN of the adjusted time series is " + threePlacess.format(medianAdj) + "\n";
					pdfbufferpar1 = pdfbufferpar1 + "\t" + "MEAN SENSITIVITY for the adjusted time series is "
							+ threePlacess.format(meanSensitivityAdj) + "\n";
					pdfbufferpar1 = pdfbufferpar1 + "\t" + "STANDARD DEVIATION of the adjusted time series is "
							+ threePlacess.format(stdAdj) + "\n";
					pdfbufferpar1 = pdfbufferpar1 + "\t" + "SKEWNESS of the adjusted time series is " + threePlacess.format(skewAdj) + "\n";
					pdfbufferpar1 = pdfbufferpar1 + "\t" + "KURTOSIS of the adjusted time series is  " + threePlacess.format(kurtAdj)
							+ "\n";
					pdfbufferpar1 = pdfbufferpar1 + "\t" + "First Order AUTOCORRELATION Index of the adjusted time series is  "
							+ threePlacess.format(autocorrelationAdj) + "\n";
					
					/*
					 * Display the general information on the Actual Event list.
					 */
					report = report + "\n" + "THE INFORMATION ON THE ACTUAL KEY EVENTS IS" + "\n" + "\n";
					report = report + "\t" + "Number of key events: " + keyEvents.length + "\n";
					report = report + "\t" + "Number of key events used in analysis: " + numberOfEventsinAdj + "\n";
					report = report + "\t" + "Mean years between events is " + threePlacess.format(meanDiffBetweenEvents) + "\n";
					report = report + "\t" + "Minimum difference is " + StatUtils.min(diffBetweenEvents) + "\n";
					
					/*
					 * write the general information on the Actual Event list to pdf file.
					 */
					pdfbufferpar1 = pdfbufferpar1 + "\n" + "THE INFORMATION ON THE ACTUAL KEY EVENTS IS" + "\n" + "\n";
					pdfbufferpar1 = pdfbufferpar1 + "\t" + "Number of key events: " + keyEvents.length + "\n";
					pdfbufferpar1 = pdfbufferpar1 + "\t" + "Number of key events used in analysis: " + numberOfEventsinAdj + "\n";
					pdfbufferpar1 = pdfbufferpar1 + "\t" + "Mean years between events is " + threePlacess.format(meanDiffBetweenEvents)
							+ "\n";
					pdfbufferpar1 = pdfbufferpar1 + "\t" + "Minimum difference is " + StatUtils.min(diffBetweenEvents) + "\n";
					pdfbufferpar11.add(pdfbufferpar1);
					para1.add(pdfbufferpar11);
					printTableActFlag.add(true);
					
					/*
					 * Write out everything that goes into the actualTable.
					 */
					PdfPTable tableAct = new PdfPTable(7);
					
					if (isFirstIteration)
					{
						String tempStrA = "";
						
						if (alphaLevel95)
						{
							tempStrA = String.format("\t %-12s" + "\t %-8s" + "\t %-8s" + "\t %-8s" + "\t %-20s" + "\t %-8s" + "\t %-8s",
									" SEGMENT ", " LAGS ", " MEAN ", "STA DEV", " 95% CONF INT ", " MIN ", " MAX ");
						}
						else if (alphaLevel99)
						{
							tempStrA = String.format("\t %-12s" + "\t %-8s" + "\t %-8s" + "\t %-8s" + "\t %-20s" + "\t %-8s" + "\t %-8s",
									" SEGMENT ", " LAGS ", " MEAN ", "STA DEV", " 99% CONF INT ", " MIN ", " MAX ");
						}
						else if (alphaLevel999)
						{
							tempStrA = String.format("\t %-12s" + "\t %-8s" + "\t %-8s" + "\t %-8s" + "\t %-20s" + "\t %-8s" + "\t %-8s",
									" SEGMENT ", " LAGS ", " MEAN ", "STA DEV", " 99.9% CONF INT ", " MIN ", " MAX ");
						}
						
						report = report + tempStrA + "\n";
						actualTable = actualTable + tempStrA.substring(1) + "\n";
						
						PdfPCell cell00A = new PdfPCell(new Paragraph(" SEGMENT "));
						tableAct.addCell(cell00A);
						PdfPCell cell01A = new PdfPCell(new Paragraph(" LAGS "));
						tableAct.addCell(cell01A);
						PdfPCell cell02A = new PdfPCell(new Paragraph(" MEAN "));
						tableAct.addCell(cell02A);
						PdfPCell cell03A = new PdfPCell(new Paragraph(" STA DEV "));
						tableAct.addCell(cell03A);
						
						if (alphaLevel95)
						{
							PdfPCell cell04A = new PdfPCell(new Paragraph(" 95% CONF INT "));
							tableAct.addCell(cell04A);
						}
						else if (alphaLevel99)
						{
							PdfPCell cell04A = new PdfPCell(new Paragraph(" 99% CONF INT "));
							tableAct.addCell(cell04A);
						}
						else if (alphaLevel999)
						{
							PdfPCell cell04A = new PdfPCell(new Paragraph(" 99.9% CONF INT "));
							tableAct.addCell(cell04A);
						}
						
						PdfPCell cell05A = new PdfPCell(new Paragraph("  MIN  "));
						tableAct.addCell(cell05A);
						PdfPCell cell06A = new PdfPCell(new Paragraph("  MAX  "));
						tableAct.addCell(cell06A);
					}
					
					for (int i = 0; i < lengthOfWindow; i++)
					{
						if (alphaLevel95)
						{
							pdfbufferA = String.format("\t %-12s" + "\t %-8s" + "\t %-8s" + "\t %-8s" + "\t %-20s" + "\t %-8s" + "\t %-8s",
									(firstYearsArray.get(segmentIndex) + " - " + lastYearsArray.get(segmentIndex)),
									(i - yearsPriorToEvent.intValue()), threePlacess.format(meanByWindow[i]),
									threePlacess.format(standardDevByWindow[i]), "[" + threePlacess.format(leftEndPoint[i][0]) + ","
											+ threePlacess.format(rightEndPoint[i][0]) + "]", threePlacess.format(minimunByWindow[i]),
									threePlacess.format(maximunByWindow[i]));
						}
						else if (alphaLevel99)
						{
							pdfbufferA = String.format("\t %-12s" + "\t %-8s" + "\t %-8s" + "\t %-8s" + "\t %-20s" + "\t %-8s" + "\t %-8s",
									(firstYearsArray.get(segmentIndex) + " - " + lastYearsArray.get(segmentIndex)),
									(i - yearsPriorToEvent.intValue()), threePlacess.format(meanByWindow[i]),
									threePlacess.format(standardDevByWindow[i]), "[" + threePlacess.format(leftEndPoint[i][1]) + ","
											+ threePlacess.format(rightEndPoint[i][1]) + "]", threePlacess.format(minimunByWindow[i]),
									threePlacess.format(maximunByWindow[i]));
						}
						else if (alphaLevel999)
						{
							pdfbufferA = String.format("\t %-12s" + "\t %-8s" + "\t %-8s" + "\t %-8s" + "\t %-20s" + "\t %-8s" + "\t %-8s",
									(firstYearsArray.get(segmentIndex) + " - " + lastYearsArray.get(segmentIndex)),
									(i - yearsPriorToEvent.intValue()), threePlacess.format(meanByWindow[i]),
									threePlacess.format(standardDevByWindow[i]), "[" + threePlacess.format(leftEndPoint[i][2]) + ","
											+ threePlacess.format(rightEndPoint[i][2]) + "]", threePlacess.format(minimunByWindow[i]),
									threePlacess.format(maximunByWindow[i]));
						}
						
						report = report + pdfbufferA + "\n";
						actualTable = actualTable + pdfbufferA.substring(1) + "\n";
						
						PdfPCell cell00A = new PdfPCell(new Paragraph(firstYearsArray.get(segmentIndex) + " - "
								+ lastYearsArray.get(segmentIndex)));
						tableAct.addCell(cell00A);
						PdfPCell cell01A = new PdfPCell(new Paragraph((i - yearsPriorToEvent.intValue())));
						tableAct.addCell(cell01A);
						PdfPCell cell02A = new PdfPCell(new Paragraph(threePlacess.format(meanByWindow[i])));
						tableAct.addCell(cell02A);
						PdfPCell cell03A = new PdfPCell(new Paragraph(threePlacess.format(standardDevByWindow[i])));
						tableAct.addCell(cell03A);
						
						if (alphaLevel95)
						{
							PdfPCell cell04A = new PdfPCell(new Paragraph("[" + threePlacess.format(leftEndPoint[i][0]) + ","
									+ threePlacess.format(rightEndPoint[i][0]) + "]"));
							tableAct.addCell(cell04A);
						}
						else if (alphaLevel99)
						{
							PdfPCell cell04A = new PdfPCell(new Paragraph("[" + threePlacess.format(leftEndPoint[i][1]) + ","
									+ threePlacess.format(rightEndPoint[i][1]) + "]"));
							tableAct.addCell(cell04A);
						}
						else if (alphaLevel999)
						{
							PdfPCell cell04A = new PdfPCell(new Paragraph("[" + threePlacess.format(leftEndPoint[i][2]) + ","
									+ threePlacess.format(rightEndPoint[i][2]) + "]"));
							tableAct.addCell(cell04A);
						}
						
						PdfPCell cell05A = new PdfPCell(new Paragraph(threePlacess.format(minimunByWindow[i])));
						tableAct.addCell(cell05A);
						PdfPCell cell06A = new PdfPCell(new Paragraph(threePlacess.format(maximunByWindow[i])));
						tableAct.addCell(cell06A);
					}
					
					printTableAct.add(tableAct);
					
					/*
					 * Display the general information on the Simulations. (Normality is assumed)
					 */
					report = report + "\n" + "SIMULATIONS RESULTS: " + "\n" + "\n";
					report = report + "\t" + "NUMBER OF SIMULATIONS is: " + this.numberOfSimulations + "\n";
					report = report + "\t" + "RANDOM SEED: " + seedNumber + "\n";
					
					/*
					 * Save the general information on the Simulations. (Normality is assumed) for the pdf file
					 */
					pdfbufferpar2 = pdfbufferpar2 + "\n" + "SIMULATIONS RESULTS: " + "\n" + "\n";
					pdfbufferpar2 = pdfbufferpar2 + "\t" + "NUMBER OF SIMULATIONS is: " + numberOfSimulations + "\n";
					pdfbufferpar2 = pdfbufferpar2 + "\t" + "RANDOM SEED: " + seedNumber + "\n";
					pdfbufferpar12.add(pdfbufferpar2);
					para2.add(pdfbufferpar12);
					
					/*
					 * Write out everything that goes into the simulationTable.
					 */
					PdfPTable tableSim = new PdfPTable(7);
					
					if (isFirstIteration)
					{
						String tempStrB = "";
						
						if (alphaLevel95)
						{
							tempStrB = String.format("\t %-12s" + "\t %-8s" + "\t %-8s" + "\t %-8s" + "\t %-20s" + "\t %-8s" + "\t %-8s",
									" SEGMENT ", " LAGS ", " MEAN ", "STA DEV", " 95% CONF INT ", " MIN ", " MAX ");
						}
						else if (alphaLevel99)
						{
							tempStrB = String.format("\t %-12s" + "\t %-8s" + "\t %-8s" + "\t %-8s" + "\t %-20s" + "\t %-8s" + "\t %-8s",
									" SEGMENT ", " LAGS ", " MEAN ", "STA DEV", " 99% CONF INT ", " MIN ", " MAX ");
						}
						else if (alphaLevel999)
						{
							tempStrB = String.format("\t %-12s" + "\t %-8s" + "\t %-8s" + "\t %-8s" + "\t %-20s" + "\t %-8s" + "\t %-8s",
									" SEGMENT ", " LAGS ", " MEAN ", "STA DEV", " 99.9% CONF INT ", " MIN ", " MAX ");
						}
						
						report = report + tempStrB + "\n";
						simulationTable = simulationTable + tempStrB.substring(1) + "\n";
						
						PdfPCell cell00B = new PdfPCell(new Paragraph(" SEGMENT "));
						tableSim.addCell(cell00B);
						PdfPCell cell01B = new PdfPCell(new Paragraph(" LAGS "));
						tableSim.addCell(cell01B);
						PdfPCell cell02B = new PdfPCell(new Paragraph(" MEAN "));
						tableSim.addCell(cell02B);
						PdfPCell cell03B = new PdfPCell(new Paragraph(" STA DEV "));
						tableSim.addCell(cell03B);
						
						if (alphaLevel95)
						{
							PdfPCell cell04B = new PdfPCell(new Paragraph(" 95% CONF INT "));
							tableSim.addCell(cell04B);
						}
						else if (alphaLevel99)
						{
							PdfPCell cell04B = new PdfPCell(new Paragraph(" 99% CONF INT "));
							tableSim.addCell(cell04B);
						}
						else if (alphaLevel999)
						{
							PdfPCell cell04B = new PdfPCell(new Paragraph(" 99.9% CONF INT "));
							tableSim.addCell(cell04B);
						}
						
						PdfPCell cell05B = new PdfPCell(new Paragraph("  MIN  "));
						tableSim.addCell(cell05B);
						PdfPCell cell06B = new PdfPCell(new Paragraph("  MAX  "));
						tableSim.addCell(cell06B);
						
						isFirstIteration = false;
					}
					
					for (int i = 0; i < lengthOfWindow; i++)
					{
						if (alphaLevel95)
						{
							pdfbufferB = String.format("\t %-12s" + "\t %-8s" + "\t %-8s" + "\t %-8s" + "\t %-20s" + "\t %-8s" + "\t %-8s",
									(firstYearsArray.get(segmentIndex) + " - " + lastYearsArray.get(segmentIndex)),
									(i - yearsPriorToEvent.intValue()), threePlacess.format(meanMeanByWindow[i]),
									threePlacess.format(standardDevMeanByWindow[i]), "[" + threePlacess.format(leftEndPointSim[i][0]) + ","
											+ threePlacess.format(rightEndPointSim[i][0]) + "]", threePlacess.format(minMeanByWindow[i]),
									threePlacess.format(maxMeanByWindow[i]));
						}
						else if (alphaLevel99)
						{
							pdfbufferB = String.format("\t %-12s" + "\t %-8s" + "\t %-8s" + "\t %-8s" + "\t %-20s" + "\t %-8s" + "\t %-8s",
									(firstYearsArray.get(segmentIndex) + " - " + lastYearsArray.get(segmentIndex)),
									(i - yearsPriorToEvent.intValue()), threePlacess.format(meanMeanByWindow[i]),
									threePlacess.format(standardDevMeanByWindow[i]), "[" + threePlacess.format(leftEndPointSim[i][1]) + ","
											+ threePlacess.format(rightEndPointSim[i][1]) + "]", threePlacess.format(minMeanByWindow[i]),
									threePlacess.format(maxMeanByWindow[i]));
						}
						else if (alphaLevel999)
						{
							pdfbufferB = String.format("\t %-12s" + "\t %-8s" + "\t %-8s" + "\t %-8s" + "\t %-20s" + "\t %-8s" + "\t %-8s",
									(firstYearsArray.get(segmentIndex) + " - " + lastYearsArray.get(segmentIndex)),
									(i - yearsPriorToEvent.intValue()), threePlacess.format(meanMeanByWindow[i]),
									threePlacess.format(standardDevMeanByWindow[i]), "[" + threePlacess.format(leftEndPointSim[i][2]) + ","
											+ threePlacess.format(rightEndPointSim[i][2]) + "]", threePlacess.format(minMeanByWindow[i]),
									threePlacess.format(maxMeanByWindow[i]));
						}
						
						report = report + pdfbufferB + "\n";
						simulationTable = simulationTable + pdfbufferB.substring(1) + "\n";
						
						PdfPCell cell00B = new PdfPCell(new Paragraph(firstYearsArray.get(segmentIndex) + " - "
								+ lastYearsArray.get(segmentIndex)));
						tableSim.addCell(cell00B);
						PdfPCell cell01B = new PdfPCell(new Paragraph((i - yearsPriorToEvent.intValue())));
						tableSim.addCell(cell01B);
						PdfPCell cell02B = new PdfPCell(new Paragraph(threePlacess.format(meanMeanByWindow[i])));
						tableSim.addCell(cell02B);
						PdfPCell cell03B = new PdfPCell(new Paragraph(threePlacess.format(standardDevMeanByWindow[i])));
						tableSim.addCell(cell03B);
						
						if (alphaLevel95)
						{
							PdfPCell cell04B = new PdfPCell(new Paragraph("[" + threePlacess.format(leftEndPointSim[i][0]) + ","
									+ threePlacess.format(rightEndPointSim[i][0]) + "]"));
							tableSim.addCell(cell04B);
							
							// PdfPCell cell05B = new PdfPCell(new Paragraph("[" + threePlacess.format(leftEndPointPer[i][0]) + ","
							// + threePlacess.format(rightEndPointPer[i][0]) + "]"));
							// tableSim.addCell(cell05B);
						}
						else if (alphaLevel99)
						{
							PdfPCell cell04B = new PdfPCell(new Paragraph("[" + threePlacess.format(leftEndPointSim[i][1]) + ","
									+ threePlacess.format(rightEndPointSim[i][1]) + "]"));
							tableSim.addCell(cell04B);
							
							// PdfPCell cell05B = new PdfPCell(new Paragraph("[" + threePlacess.format(leftEndPointPer[i][0]) + ","
							// + threePlacess.format(rightEndPointPer[i][0]) + "]"));
							// tableSim.addCell(cell05B);
						}
						else if (alphaLevel999)
						{
							PdfPCell cell04B = new PdfPCell(new Paragraph("[" + threePlacess.format(leftEndPointSim[i][2]) + ","
									+ threePlacess.format(rightEndPointSim[i][2]) + "]"));
							tableSim.addCell(cell04B);
							
							// PdfPCell cell05B = new PdfPCell(new Paragraph("[" + threePlacess.format(leftEndPointPer[i][0]) + ","
							// + threePlacess.format(rightEndPointPer[i][0]) + "]"));
							// tableSim.addCell(cell05B);
						}
						
						PdfPCell cell06B = new PdfPCell(new Paragraph(threePlacess.format(minMeanByWindow[i])));
						tableSim.addCell(cell06B);
						PdfPCell cell07B = new PdfPCell(new Paragraph(threePlacess.format(maxMeanByWindow[i])));
						tableSim.addCell(cell07B);
					}
					
					printTableSim.add(tableSim);
					
				} // end of if keventsinadj >=2
				
				else
				{
					cdbuffer = cdbuffer + "Range:" + "\n";
					cdbuffer = cdbuffer + beginingYearAdj + "," + lastYearAdj + "\n";
					cdbuffer = cdbuffer + "Segment: " + (segmentIndex + 1) + "has not enough events to run the analysis" + "\n";
					
					// ADDED SO THAT BAD SEGMENTS CANNOT BE SELECTED FOR DISPLAY ON THE CHART
					segmentTable.tableModel.getSegment(segmentIndex).setBadSegmentFlag(true);
					
					printTableActFlag.add(false);
					pdfbufferpar1 = pdfbufferpar1 + "\n";
					pdfbufferpar1 = pdfbufferpar1 + "SUPERPOSED EPOCH ANALYSIS RESULTS" + "\n";
					pdfbufferpar1 = pdfbufferpar1 + "Date: " + now + "\n";
					pdfbufferpar1 = pdfbufferpar1 + "Name of the time series file: " + chronologyFile + "\n";
					if (firstYearOfProcess.intValue() > chronologyYears.get(0).intValue())
					{
						report = report + "\n" + "The First year processed: " + firstYearOfProcess + "\n";
						pdfbufferpar1 = pdfbufferpar1 + "\n" + "The First year processed: " + firstYearOfProcess + "\n";
					}
					else
					{
						report = report + "\n" + "The First year processed " + chronologyYears.get(0) + "\n";
						pdfbufferpar1 = pdfbufferpar1 + "\n" + "The First year processed " + chronologyYears.get(0) + "\n";
					}
					if (lastYearOfProcess.intValue() < chronologyYears.get(chronologyYears.size() - 1).intValue())
					{
						report = report + "\n" + "The last year of the process is " + lastYearOfProcess + "\n";
						pdfbufferpar1 = pdfbufferpar1 + "\n" + "The last year of the process is " + lastYearOfProcess + "\n";
					}
					else
					{
						report = report + "\n" + "The last year of the process is " + chronologyYears.get(chronologyYears.size() - 1)
								+ "\n";
						pdfbufferpar1 = pdfbufferpar1 + "\n" + "The last year of the process is "
								+ chronologyYears.get(chronologyYears.size() - 1) + "\n";
					}
					report = report
							+ "Not enough events within the window in the time series (or segment of the time series) to proceed with the analysis  "
							+ keventsinadj.size() + "\n";
					pdfbufferpar1 = pdfbufferpar1
							+ "Not enough events within the window in the time series (or segment of the time series) to proceed with the analysis  "
							+ keventsinadj.size() + "\n";
				}
				;// end of else for if keventsinadd >=2
			} // end of if kevents >=2
			else
			{
				cdbuffer = cdbuffer + "Range:" + "\n";
				cdbuffer = cdbuffer + beginingYearAdj + "," + lastYearAdj + "\n";
				cdbuffer = cdbuffer + "Segement: " + (segmentIndex + 1) + "has not enough events to run the analysis" + "\n";
				
				// ADDED SO THAT BAD SEGMENTS CANNOT BE SELECTED FOR DISPLAY ON THE CHART
				segmentTable.tableModel.getSegment(segmentIndex).setBadSegmentFlag(true);
				
				printTableActFlag.add(false);
				pdfbufferpar1 = pdfbufferpar1 + "\n";
				pdfbufferpar1 = pdfbufferpar1 + "SUPERPOSED EPOCH ANALYSIS RESULTS" + "\n";
				pdfbufferpar1 = pdfbufferpar1 + "Date: " + now + "\n";
				pdfbufferpar1 = pdfbufferpar1 + "Name of the time series file: " + chronologyFile + "\n";
				if (firstYearOfProcess.intValue() > chronologyYears.get(0).intValue())
				{
					report = report + "\n" + "The First year processed: " + firstYearOfProcess + "\n";
					pdfbufferpar1 = pdfbufferpar1 + "\n" + "The First year processed: " + firstYearOfProcess + "\n";
				}
				else
				{
					report = report + "\n" + "The First year processed " + chronologyYears.get(0) + "\n";
					pdfbufferpar1 = pdfbufferpar1 + "\n" + "The First year processed " + chronologyYears.get(0) + "\n";
				}
				if (lastYearOfProcess.intValue() < chronologyYears.get(chronologyYears.size() - 1).intValue())
				{
					report = report + "\n" + "The last year of the process is " + lastYearOfProcess + "\n";
					pdfbufferpar1 = pdfbufferpar1 + "\n" + "The last year of the process is " + lastYearOfProcess + "\n";
				}
				else
				{
					report = report + "\n" + "The last year of the process is " + chronologyYears.get(chronologyYears.size() - 1) + "\n";
					pdfbufferpar1 = pdfbufferpar1 + "\n" + "The last year of the process is "
							+ chronologyYears.get(chronologyYears.size() - 1) + "\n";
				}
				report = report + "Not enough events in the time series (or segment of the time series) to proceed with the analysis "
						+ kevents.size() + "\n";
				pdfbufferpar1 = pdfbufferpar1
						+ "Not enough events in the time series (or segment of the time series) to proceed with the analysis "
						+ kevents.size() + "\n";
			}
			pdfbufferpar1 = "";
			pdfbufferpar2 = "";
		}
		; // ending the huge loop ikj
			// ending of additions
		
	}// end of epoch function
	
	// this method is the new print report method
	public String getReportText() {
	
		return report;
	}
	
	public ArrayList<BarChartParametersModel> getChartList() {
	
		return chartList;
	}
	
	public String getActualTableText() {
	
		return actualTable;
	}
	
	public String getSimulationTableText() {
	
		return simulationTable;
	}
	
	public void savePDFReport(String filename) {
	
		Document document = new Document();
		try
		{
			
			// if you want to save a pdf file using bigbuffer do it here
			PdfWriter.getInstance(document, new FileOutputStream(filename));
			document.open();
			document.add(new Paragraph(para1.get(0)));
			printTableAct.get(0).setWidthPercentage(90);
			printTableAct.get(0).setSpacingBefore(20f);
			printTableAct.get(0).setSpacingAfter(20f);
			document.add(printTableAct.get(0));
			// document.newPage();
			document.add(new Paragraph(para2.get(0)));
			printTableSim.get(0).setWidthPercentage(90);
			printTableSim.get(0).setSpacingBefore(20f);
			printTableSim.get(0).setSpacingAfter(20f);
			document.add(printTableSim.get(0));
			
			// the charts are being saved from an array list to the files
			for (int i = 0; i < chartList.size(); i++)
			{
				try
				{
					
					File f2 = File.createTempFile("chart" + i, ".png");
					ChartUtilities.saveChartAsPNG(f2, chartList.get(i).getChart(), 800, 500);
					Image image = Image.getInstance(f2.getAbsolutePath());
					float fWidth = 0.6f * 800;
					float fHeight = 0.6f * 500;
					image.scaleAbsolute(fWidth, fHeight);
					document.add(image);
					f2.delete();
					
				}
				catch (IOException ex)
				{
					System.err.println(ex.getLocalizedMessage());
				}
			}
		}
		catch (Exception e)
		{
			
		}
		
		document.close();
		
	}
	
	public ArrayList<Integer> getChronologyYears() {
	
		return chronologyYears;
	}
	
	public void setChronologyYears(ArrayList<Integer> chronologyYears) {
	
		this.chronologyYears = chronologyYears;
	}
	
	public ArrayList<Double> getChronologyActual() {
	
		return chronologyActual;
	}
	
	public void setChronologyActual(ArrayList<Double> chronologyActual) {
	
		this.chronologyActual = chronologyActual;
	}
	
	public ArrayList<Integer> getEvents() {
	
		return events;
	}
	
	public void setEvents(ArrayList<Integer> events) {
	
		this.events = events;
	}
	
	// Getters and Setters
	public String getTitleForRun() {
	
		return titleForRun;
	}
	
	public void setTitleForRun(String titleForRun) {
	
		this.titleForRun = titleForRun;
	}
	
	public String getOutputFilePrefix() {
	
		return outputFilePrefix;
	}
	
	public void setOutputFilePrefix(String outputFilePrefix) {
	
		this.outputFilePrefix = outputFilePrefix;
	}
	
	public Integer getYearsPriorToEvent() {
	
		return yearsPriorToEvent;
	}
	
	public void setYearsPriorToEvent(Integer yearsPriorToEvent) {
	
		this.yearsPriorToEvent = yearsPriorToEvent;
	}
	
	public boolean isRandomSampling() {
	
		return randomSampling;
	}
	
	public void setRandomSampling(boolean randomSampling) {
	
		this.randomSampling = randomSampling;
	}
	
	public Integer getYearsAfterTheEvent() {
	
		return yearsAfterTheEvent;
	}
	
	public void setYearsAfterTheEvent(Integer yearsAfterTheEvent) {
	
		this.yearsAfterTheEvent = yearsAfterTheEvent;
	}
	
	public Integer getNumberOfSimulations() {
	
		return numberOfSimulations;
	}
	
	public void setNumberOfSimulations(Integer numberOfSimulations) {
	
		this.numberOfSimulations = numberOfSimulations;
	}
	
	public Integer getSeedNumber() {
	
		return seedNumber;
	}
	
	public void setSeedNumber(Integer seedNumber) {
	
		this.seedNumber = seedNumber;
	}
	
	public Integer getFirstYearOfProcess() {
	
		return firstYearOfProcess;
	}
	
	public void setFirstYearOfProcess(Integer firstYearOfProcess) {
	
		this.firstYearOfProcess = firstYearOfProcess;
	}
	
	public Integer getLastYearOfProcess() {
	
		return lastYearOfProcess;
	}
	
	public void setLastYearOfProcess(Integer lastYearOfProcess) {
	
		this.lastYearOfProcess = lastYearOfProcess;
	}
	
	public boolean isIncludeIncompleteEpochs() {
	
		return includeIncompleteEpochs;
	}
	
	public void setIncludeIncompleteEpochs(boolean includeIncompleteEpochs) {
	
		this.includeIncompleteEpochs = includeIncompleteEpochs;
	}
}
