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
package org.fhaes.neofhchart;

import java.awt.event.ActionEvent;

import javax.swing.Action;
import javax.swing.ActionMap;

import org.apache.batik.swing.JSVGCanvas;
import org.fhaes.gui.MainWindow;
import org.fhaes.preferences.App;
import org.fhaes.preferences.FHAESPreferences.PrefKey;
import org.fhaes.util.FHAESAction;

/**
 * ChartActions Class.
 */
public class ChartActions {
	
	/**
	 * SeriesSortType Enum.
	 */
	public enum SeriesSortType {
		START_YEAR, END_YEAR, FIRST_FIRE_YEAR, NAME
	};
	
	// Declare chart instance
	protected NeoFHChart neoFHChart;
	
	// Declare primary actions
	public FHAESAction actionShowChronologyPlot;
	public FHAESAction actionShowIndexPlot;
	public FHAESAction actionCompositePlot;
	public FHAESAction actionShowChartProperties;
	public FHAESAction actionShowLegend;
	public FHAESAction actionShowSeriesLabels;
	public FHAESAction actionShowCommonTickLine;
	public FHAESAction actionShowMinorTickMarks;
	public FHAESAction actionShowSeriesList;
	public FHAESAction actionShowSampleDepthThreshold;
	public FHAESAction actionSetSampleDepthThreshold;
	public FHAESAction actionShowSampleDepth;
	public FHAESAction actionShowRecorderDepth;
	
	// Declare export actions
	public FHAESAction actionExportCurrentChart;
	public FHAESAction actionBulkExportChartsAsSVG;
	public FHAESAction actionBulkExportChartsAsPNG;
	public FHAESAction actionBulkExportChartsAsPDF;
	
	// Declare sort actions
	public FHAESAction actionSortStartYear;
	public FHAESAction actionSortEndYear;
	public FHAESAction actionSortFirstFireYear;
	public FHAESAction actionSortName;
	public FHAESAction actionSortSeriesBy;
	
	// Declare zoom actions
	public FHAESAction actionZoomIn;
	public FHAESAction actionZoomOut;
	public FHAESAction actionZoomReset;
	
	/*
	 * protected JMenuItem lineDrawTool = new JMenuItem("Vertical Year Marker"); protected JMenuItem eraserDrawTool = new
	 * JMenuItem("Eraser"); protected JMenuItem zoomIn = new JMenuItem("Zoom In"); protected JMenuItem zoomOut = new JMenuItem("Zoom Out");
	 * protected JMenuItem tickInterval = new JMenuItem("Interval"); protected JMenuItem showDialog = new JMenuItem("Show Series List");
	 * 
	 * protected ButtonGroup tickStyleGroup = new ButtonGroup(); protected JRadioButton dotTickStyle = new JRadioButton("Dotted Line");
	 * protected JRadioButton dashTickStyle = new JRadioButton("Dashed Line"); protected JRadioButton lineTickStyle = new JRadioButton(
	 * "Solid Line");
	 * 
	 * protected ButtonGroup composite_group = new ButtonGroup(); protected JRadioButton fireAndInjuryEvents = new JRadioButton(
	 * "Fire and Injury Events"); protected JRadioButton injuryOnlyEvents = new JRadioButton("Injury Events Only"); protected JRadioButton
	 * fireOnlyEvents = new JRadioButton("Fire Events Only");
	 */
	
	/**
	 * Initializes the set of actions which can be performed on a neoFHChart.
	 * 
	 * @param neochart
	 */
	public ChartActions(NeoFHChart neochart) {
		
		/*
		 * SHOW INDEX PLOT
		 */
		actionShowIndexPlot = new FHAESAction("Index plot", "fireindexplot.png", "Index plot", "Hide/show the fire index plot") {
			
			private static final long serialVersionUID = 1L;
			
			@Override
			public void actionPerformed(ActionEvent event) {
				
				boolean val = (Boolean) getValue(Action.SELECTED_KEY);
				App.prefs.setBooleanPref(PrefKey.CHART_SHOW_INDEX_PLOT, val);
			}
		};
		actionShowIndexPlot.putValue(Action.SELECTED_KEY, App.prefs.getBooleanPref(PrefKey.CHART_SHOW_INDEX_PLOT, true));
		actionShowIndexPlot.setEnabled(false);
		
		/*
		 * SHOW CHRONOLOGY PLOT
		 */
		actionShowChronologyPlot = new FHAESAction("Chronology plot", "firechronologyplot.png", "Chronology",
				"Hide/show the chronology plot") {
				
			private static final long serialVersionUID = 1L;
			
			@Override
			public void actionPerformed(ActionEvent event) {
				
				boolean val = (Boolean) getValue(Action.SELECTED_KEY);
				App.prefs.setBooleanPref(PrefKey.CHART_SHOW_CHRONOLOGY_PLOT, val);
			}
		};
		actionShowChronologyPlot.putValue(Action.SELECTED_KEY, App.prefs.getBooleanPref(PrefKey.CHART_SHOW_CHRONOLOGY_PLOT, true));
		actionShowChronologyPlot.setEnabled(false);
		
		/*
		 * SHOW COMPOSITE PLOT
		 */
		actionCompositePlot = new FHAESAction("Composite plot", "firecompositeplot.png", "Composite", "Hide/show the composite plot") {
			
			private static final long serialVersionUID = 1L;
			
			@Override
			public void actionPerformed(ActionEvent event) {
				
				boolean val = (Boolean) getValue(Action.SELECTED_KEY);
				App.prefs.setBooleanPref(PrefKey.CHART_SHOW_COMPOSITE_PLOT, val);
			}
		};
		actionCompositePlot.putValue(Action.SELECTED_KEY, App.prefs.getBooleanPref(PrefKey.CHART_SHOW_COMPOSITE_PLOT, true));
		actionCompositePlot.setEnabled(false);
		
		/*
		 * SHOW LEGEND
		 */
		actionShowLegend = new FHAESAction("Legend", "legend.png", "Legend", "Hide/show the chart legend") {
			
			private static final long serialVersionUID = 1L;
			
			@Override
			public void actionPerformed(ActionEvent event) {
				
				boolean val = (Boolean) getValue(Action.SELECTED_KEY);
				App.prefs.setBooleanPref(PrefKey.CHART_SHOW_LEGEND, val);
			}
		};
		actionShowLegend.putValue(Action.SELECTED_KEY, App.prefs.getBooleanPref(PrefKey.CHART_SHOW_LEGEND, true));
		actionShowLegend.setEnabled(false);
		
		/*
		 * SHOW CHART PROPERTIES
		 */
		actionShowChartProperties = new FHAESAction("Chart Properties", "chartproperties.png", "Chart", "Chart properties") {
			
			private static final long serialVersionUID = 1L;
			
			@Override
			public void actionPerformed(ActionEvent event) {
				
				showChartProperties();
			}
		};
		actionShowChartProperties.setEnabled(false);
		
		/*
		 * ZOOM IN
		 */
		actionZoomIn = new FHAESAction("Zoom in", "zoom_in.png", "Zoom in", "Zoom in") {
			
			private static final long serialVersionUID = 1L;
			
			@Override
			public void actionPerformed(ActionEvent event) {
				
				ActionMap map = neoFHChart.svgCanvas.getActionMap();
				Action action = map.get(JSVGCanvasEx.ZOOM_IN_ACTION);
				action.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, null));
			}
		};
		actionZoomIn.setEnabled(false);
		
		/*
		 * ZOOM OUT
		 */
		actionZoomOut = new FHAESAction("Zoom out", "zoom_out.png", "Zoom out", "Zoom out") {
			
			private static final long serialVersionUID = 1L;
			
			@Override
			public void actionPerformed(ActionEvent event) {
				
				ActionMap map = neoFHChart.svgCanvas.getActionMap();
				Action action = map.get(JSVGCanvasEx.ZOOM_OUT_ACTION);
				action.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, null));
			}
		};
		actionZoomOut.setEnabled(false);
		
		/*
		 * ZOOM RESET
		 */
		actionZoomReset = new FHAESAction("Reset zoom", "zoomfull.png", "Reset zoom", "Reset zoom") {
			
			private static final long serialVersionUID = 1L;
			
			@Override
			public void actionPerformed(ActionEvent event) {
				
				ActionMap map = neoFHChart.svgCanvas.getActionMap();
				Action action = map.get(JSVGCanvas.RESET_TRANSFORM_ACTION);
				action.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, null));
			}
		};
		actionZoomReset.setEnabled(false);
		
		/*
		 * EXPORT CURRENT CHART
		 */
		actionExportCurrentChart = new FHAESAction("Export current chart as...", "document_export.png", "Export Chart",
				"Export current chart to disk") {
				
			private static final long serialVersionUID = 1L;
			
			@Override
			public void actionPerformed(ActionEvent event) {
				
				if (neoFHChart == null)
					return;
					
				neoFHChart.doSingleExport();
			}
		};
		
		/*
		 * BULK EXPORT CHARTS AS PDF
		 */
		actionBulkExportChartsAsPDF = new FHAESAction("Bulk export to PDF", "pdf22.png", "Chart as PDF",
				"Export charts from all loaded files to PDF documents") {
				
			private static final long serialVersionUID = 1L;
			
			@Override
			public void actionPerformed(ActionEvent event) {
				
				MainWindow.getInstance().bulkExportCharts("PDF");
			}
		};
		
		/*
		 * BULK EXPORT CHARTS AS PNG
		 */
		actionBulkExportChartsAsPNG = new FHAESAction("Bulk export to PNG", "png.png", "Chart as PNG",
				"Export charts from all loaded files to PNG images") {
				
			private static final long serialVersionUID = 1L;
			
			@Override
			public void actionPerformed(ActionEvent event) {
				
				MainWindow.getInstance().bulkExportCharts("PNG");
			}
		};
		
		/*
		 * BULK EXPORT CHARTS AS SVG
		 */
		actionBulkExportChartsAsSVG = new FHAESAction("Bulk export to SVG", "svg22.png", "Chart as SVG",
				"Export charts from all loaded files to SVG files") {
				
			private static final long serialVersionUID = 1L;
			
			@Override
			public void actionPerformed(ActionEvent event) {
				
				MainWindow.getInstance().bulkExportCharts("SVG");
			}
		};
		
		/*
		 * SHOW SAMPLE DEPTH THRESHOLD
		 */
		actionShowSampleDepthThreshold = new FHAESAction("Sample depth threshold", null, "Sample depth",
				"Hide/show the sample depth threshold line") {
				
			private static final long serialVersionUID = 1L;
			
			@Override
			public void actionPerformed(ActionEvent event) {
				
				boolean val = (Boolean) getValue(Action.SELECTED_KEY);
				App.prefs.setBooleanPref(PrefKey.CHART_SHOW_DEPTH_THRESHOLD, val);
			}
		};
		actionShowSampleDepthThreshold.putValue(Action.SELECTED_KEY, App.prefs.getBooleanPref(PrefKey.CHART_SHOW_DEPTH_THRESHOLD, false));
		
		/*
		 * SHOW SERIES LABELS
		 */
		actionShowSeriesLabels = new FHAESAction("Show/hide series labels") {
			
			private static final long serialVersionUID = 1L;
			
			@Override
			public void actionPerformed(ActionEvent event) {
				
				boolean val = (Boolean) getValue(Action.SELECTED_KEY);
				App.prefs.setBooleanPref(PrefKey.CHART_SHOW_CHRONOLOGY_PLOT_LABELS, val);
			}
		};
		actionShowSeriesLabels.putValue(Action.SELECTED_KEY, App.prefs.getBooleanPref(PrefKey.CHART_SHOW_CHRONOLOGY_PLOT_LABELS, true));
		
		/*
		 * SHOW COMMON TICKLINE
		 */
		actionShowCommonTickLine = new FHAESAction("Show/hide tick line") {
			
			private static final long serialVersionUID = 1L;
			
			@Override
			public void actionPerformed(ActionEvent event) {
				
				boolean val = (Boolean) getValue(Action.SELECTED_KEY);
				App.prefs.setBooleanPref(PrefKey.CHART_VERTICAL_GUIDES, val);
			}
		};
		actionShowCommonTickLine.putValue(Action.SELECTED_KEY, App.prefs.getBooleanPref(PrefKey.CHART_VERTICAL_GUIDES, true));
		
		/*
		 * SHOW MINOR TICKMARKS
		 */
		actionShowMinorTickMarks = new FHAESAction("Show/hide minor tick marks") {
			
			private static final long serialVersionUID = 1L;
			
			@Override
			public void actionPerformed(ActionEvent event) {
				
				boolean val = (Boolean) getValue(Action.SELECTED_KEY);
				App.prefs.setBooleanPref(PrefKey.CHART_XAXIS_MINOR_TICKS, val);
			}
		};
		actionShowMinorTickMarks.putValue(Action.SELECTED_KEY, App.prefs.getBooleanPref(PrefKey.CHART_XAXIS_MINOR_TICKS, true));
		
		/*
		 * SHOW SERIES LIST
		 */
		actionShowSeriesList = new FHAESAction("Choose series to plot...", "chooseseries.png", "Choose series",
				"Choose which series to plot") {
				
			private static final long serialVersionUID = 1L;
			
			@Override
			public void actionPerformed(ActionEvent event) {
				
				showSeriesList();
			}
		};
		
		/*
		 * SORT SERIES BY
		 */
		this.actionSortSeriesBy = new FHAESAction("Sort series by...", "sort.png", "Sort series", "Sort series by...") {
			
			private static final long serialVersionUID = 1L;
			
			@Override
			public void actionPerformed(ActionEvent event) {
			
			}
		};
		
		/*
		 * SORT BY START YEAR
		 */
		this.actionSortStartYear = new FHAESAction("Start year") {
			
			private static final long serialVersionUID = 1L;
			
			@Override
			public void actionPerformed(ActionEvent event) {
				
				sortSeries(SeriesSortType.START_YEAR);
			}
		};
		
		/*
		 * SORT BY END YEAR
		 */
		this.actionSortEndYear = new FHAESAction("End year") {
			
			private static final long serialVersionUID = 1L;
			
			@Override
			public void actionPerformed(ActionEvent event) {
				
				sortSeries(SeriesSortType.END_YEAR);
			}
		};
		
		/*
		 * SORT BY FIRST FIRE YEAR
		 */
		this.actionSortFirstFireYear = new FHAESAction("First fire year") {
			
			private static final long serialVersionUID = 1L;
			
			@Override
			public void actionPerformed(ActionEvent event) {
				
				sortSeries(SeriesSortType.FIRST_FIRE_YEAR);
			}
		};
		
		/*
		 * SORT BY NAME
		 */
		this.actionSortName = new FHAESAction("Name") {
			
			private static final long serialVersionUID = 1L;
			
			@Override
			public void actionPerformed(ActionEvent event) {
				
				sortSeries(SeriesSortType.NAME);
			}
		};
		
		// Set the current chart
		setNeoChart(neochart);
	}
	
	/**
	 * Sets the neoFHChart to the parameter instance and enables the chart actions accordingly.
	 * 
	 * @param chart
	 */
	public void setNeoChart(NeoFHChart chart) {
		
		this.neoFHChart = chart;
		
		actionShowIndexPlot.setEnabled(chart != null);
		actionShowChronologyPlot.setEnabled(chart != null);
		actionCompositePlot.setEnabled(chart != null);
		actionShowLegend.setEnabled(chart != null);
		actionShowChartProperties.setEnabled(chart != null);
		actionZoomIn.setEnabled(chart != null);
		actionZoomOut.setEnabled(chart != null);
		actionZoomReset.setEnabled(chart != null);
		actionExportCurrentChart.setEnabled(chart != null);
		actionBulkExportChartsAsPDF.setEnabled(chart != null);
		actionBulkExportChartsAsPNG.setEnabled(chart != null);
		actionBulkExportChartsAsSVG.setEnabled(chart != null);
		actionShowCommonTickLine.setEnabled(chart != null);
		actionShowMinorTickMarks.setEnabled(chart != null);
		actionShowSampleDepthThreshold.setEnabled(chart != null);
		actionShowSeriesLabels.setEnabled(chart != null);
		actionShowSeriesList.setEnabled(chart != null);
		actionSortEndYear.setEnabled(chart != null);
		actionSortFirstFireYear.setEnabled(chart != null);
		actionSortName.setEnabled(chart != null);
		actionSortStartYear.setEnabled(chart != null);
		actionSortSeriesBy.setEnabled(chart != null);
	}
	
	/**
	 * Shows a dialog containing all of the configurable properties for the neoFHChart.
	 */
	public void showChartProperties() {
		
		ChartPropertiesDialog.showDialog(App.mainFrame, neoFHChart);
	}
	
	/**
	 * Shows a dialog containing a list of all the series in neoFHChart.
	 */
	private void showSeriesList() {
		
		if (neoFHChart == null)
			return;
			
		SeriesListDialog.showDialog(neoFHChart.currentChart, neoFHChart.svgCanvas);
	}
	
	/**
	 * Handles sorting of series on the neoFHChart.
	 * 
	 * @param type
	 */
	private void sortSeries(final SeriesSortType type) {
		
		if (neoFHChart == null)
			return;
			
		Runnable r = new Runnable() {
			
			@Override
			public void run() {
				
				if (type.equals(SeriesSortType.START_YEAR))
				{
					neoFHChart.currentChart.sortBySampleStartYear();
				}
				else if (type.equals(SeriesSortType.END_YEAR))
				{
					neoFHChart.currentChart.sortBySampleEndYear();
				}
				else if (type.equals(SeriesSortType.FIRST_FIRE_YEAR))
				{
					neoFHChart.currentChart.sortByFirstFireYear();
				}
				else if (type.equals(SeriesSortType.NAME))
				{
					neoFHChart.currentChart.sortByName();
				}
			}
		};
		
		neoFHChart.svgCanvas.getUpdateManager().getUpdateRunnableQueue().invokeLater(r);
	}
	
	// private void setTickColor() {
	//
	// if (neoFHChart == null)
	// return;
	//
	// Runnable r = new Runnable() {
	//
	// @Override
	// public void run() {
	//
	// Color ret = JColorChooser.showDialog(App.mainFrame, "Tick Color", neoFHChart.chart.getTickColor());
	//
	// if (ret != null)
	// neoFHChart.chart.setTickColor(ret);
	// }
	// };
	//
	// neoFHChart.svgCanvas.getUpdateManager().getUpdateRunnableQueue().invokeLater(r);
	// }
}
