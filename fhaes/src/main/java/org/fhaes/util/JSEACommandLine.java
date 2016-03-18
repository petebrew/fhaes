package org.fhaes.util;

import java.util.ArrayList;

import org.fhaes.jsea.JSEAStatsFunctions;
import org.fhaes.segmentation.SegmentTable;

public class JSEACommandLine {
	
	public static void main(String[] args) {
	
		Integer seed = 30188;
		Integer lagsprior = 6;
		Integer lagsafter = 4;
		Integer simulationcount = 1000;
		Integer firstyear = 0;
		Integer lastyear = 2020;
		boolean includeIncompleteWindow = false;
		ArrayList<Integer> chronologyYears = new ArrayList<Integer>();
		ArrayList<Double> chronologyActual = new ArrayList<Double>();
		ArrayList<Integer> events = new ArrayList<Integer>();
		boolean growth = false;
		boolean save = false;
		boolean usingSegmentation = false;
		SegmentTable segmentTable = null;
		String timeSeriesFile = null;
		boolean alphaLevel95 = true;
		boolean alphaLevel99 = false;
		boolean alphaLevel999 = false;
		
		JSEAStatsFunctions jsea = new JSEAStatsFunctions("Chart Title", "Y Axis Label", seed, lagsprior, lagsafter, simulationcount,
				firstyear, lastyear, includeIncompleteWindow, true, chronologyYears, chronologyActual, events, growth, save,
				usingSegmentation, segmentTable, timeSeriesFile, alphaLevel95, alphaLevel99, alphaLevel999, false);
		
		System.out.println(jsea.getReportText());
		
	}
	
}
