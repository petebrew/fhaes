/**************************************************************************************************
 * Fire History Analysis and Exploration System (FHAES), Copyright (C) 2015
 * 
 * Contributors: Cody Calhoun, Anthony Messerschmidt, Seth Westphal, Scott Goble, and Peter Brewer
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
package org.fhaes.fhxrecorder.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.font.FontRenderContext;
import java.awt.geom.AffineTransform;
import java.util.List;

import org.fhaes.fhxrecorder.controller.FileController;
import org.fhaes.fhxrecorder.util.NumericCategoryAxis;
import org.fhaes.fhxrecorder.util.YearSummary;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.labels.StandardCategoryToolTipGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.DatasetRenderingOrder;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.CategoryItemRenderer;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.chart.renderer.category.StandardBarPainter;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.category.SlidingCategoryDataset;

/**
 * GraphSummaryOverlay Class. This is an overlay graph which includes # of events as a bar chart, # of recorders as a line graph, and # of
 * samples as a line graph.
 * 
 * @author Cody Calhoun, Seth Westphal, Anthony Messerschmidt
 */
public class GraphSummaryOverlay extends ChartPanel {
	
	private static final long serialVersionUID = 1L;
	private int maxRange;
	
	/**
	 * This is the constructor for the YearSummaryOverlayPanel. This will create the chart based on the incoming list of YearSummary
	 * objects.
	 * 
	 * @param inData This is the list of the incoming YearSummary objects. It is the data used to create the graphs.
	 */
	public GraphSummaryOverlay(List<YearSummary> inData) {
		
		super(createChart(createEventsDataset(inData), createRecordersDataset(inData), createSamplesDataset(inData)));
		
		// super(createChart(createEventsDataset(inData), createRecordersDataset(inData), createSamplesDataset(inData)));
		updateMaximumRangeValue(inData);
		
	}
	
	/**
	 * This method will create the data set corresponding to the events bar chart (histogram) which JFreeChart uses to create the graph.
	 * 
	 * @param inData This is the list of the incoming YearSummary objects. It is the data used to create the Events histogram.
	 * @return This method returns the CategoryDataset object that JFreeChart needs to create the Event bar graph.
	 */
	private static CategoryDataset createEventsDataset(List<YearSummary> inData) {
		
		DefaultCategoryDataset eventsData = new DefaultCategoryDataset();
		
		for (YearSummary dataItem : inData)
			eventsData.addValue(dataItem.getNumEvents(), "# Of Events", Integer.toString(dataItem.getYear()));
			
		return new SlidingCategoryDataset(eventsData, 0, FileController.MAX_VISIBLE_GRAPH_COLUMNS);
	}
	
	/**
	 * This method will create the data set corresponding to the recorders line graph which JFreeChart uses to create the graph.
	 * 
	 * @param inData This is the list of the incoming YearSummary objects. It is the data used to create the number of recorders line.
	 * @return This method returns the CategoryDataset object that JFreeChart needs to create the recorders graph.
	 */
	private static CategoryDataset createRecordersDataset(List<YearSummary> inData) {
		
		/**
		 * WARNING - the value of recording years does not match that of FHFileReader. There are special cases not currently handled by
		 * FHRecorder
		 */
		DefaultCategoryDataset recordersData = new DefaultCategoryDataset();
		
		for (YearSummary dataItem : inData)
			recordersData.addValue(dataItem.getNumRecorders(), "# Of Recorders", Integer.toString(dataItem.getYear()));
			
		return new SlidingCategoryDataset(recordersData, 0, FileController.MAX_VISIBLE_GRAPH_COLUMNS);
	}
	
	/**
	 * This method will create the data set corresponding to the samples line graph which JFreeChart uses to create the graph.
	 * 
	 * @param inData This is the list of the incoming YearSummary objects. It is the data used to create the number of samples line.
	 * @return This method returns the CategoryDataset object that JFreeChart needs to create the samples graph.
	 */
	private static CategoryDataset createSamplesDataset(List<YearSummary> inData) {
		
		DefaultCategoryDataset samplesData = new DefaultCategoryDataset();
		
		for (YearSummary dataItem : inData)
			samplesData.addValue(dataItem.getNumSamples(), "# Of Samples", Integer.toString(dataItem.getYear()));
			
		return new SlidingCategoryDataset(samplesData, 0, FileController.MAX_VISIBLE_GRAPH_COLUMNS);
	}
	
	/**
	 * This method updates the data in the graph which forces the graph to redraw.
	 * 
	 * @param inData This is the list of the incoming YearSummary objects. It is the data used to update the graphs.
	 * @param savePosition When savePosition is set to true then this method will automatically scroll to the section of the graph that was
	 *            viewable before calling this method. Otherwise when savePosition is false, the graph will scroll to the first year.
	 */
	public void updateChartData(List<YearSummary> inData, boolean savePosition) {
		
		SlidingCategoryDataset eventsDataset = (SlidingCategoryDataset) createEventsDataset(inData);
		SlidingCategoryDataset recordersDataset = (SlidingCategoryDataset) createRecordersDataset(inData);
		SlidingCategoryDataset samplesDataset = (SlidingCategoryDataset) createSamplesDataset(inData);
		if (savePosition && inData.size() > 0)
		{
			int index = ((SlidingCategoryDataset) getChart().getCategoryPlot().getDataset()).getFirstCategoryIndex();
			eventsDataset.setFirstCategoryIndex(index);
			recordersDataset.setFirstCategoryIndex(index);
			samplesDataset.setFirstCategoryIndex(index);
		}
		getChart().getCategoryPlot().setDataset(0, eventsDataset);
		// Temporarily removing recorders dataset
		// getChart().getCategoryPlot().setDataset(1, recordersDataset);
		getChart().getCategoryPlot().setDataset(1, samplesDataset);
		
		updateMaximumRangeValue(inData);
	}
	
	/**
	 * @return the approximate width of the label in pixels corresponding to the largest value of the overlay graph's y axis.
	 */
	public int getMaximumRangeLabelWidth() {
		
		String maxRangeString = Integer.toString(maxRange);
		FontRenderContext frc = new FontRenderContext(new AffineTransform(), true, true);
		Font font = new Font("SansSerif", Font.PLAIN, 10);
		return (int) (font.getStringBounds(maxRangeString, frc).getWidth() + 6);
	}
	
	/**
	 * @param inData This is the list of YearSummary objects (the data) which is used to calculate the maximum value the y axis can be.
	 * @return This method returns the maximum value the y axis of this overlay graph can be.
	 */
	private int getMaximumRangeValue(List<YearSummary> inData) {
		
		int maxRange = 0;
		for (YearSummary dataItem : inData)
			if (dataItem.getNumSamples() > maxRange)
				maxRange = dataItem.getNumSamples();
		return maxRange;
	}
	
	/**
	 * This method will update the maximum value the y axis can be.
	 * 
	 * @param inData This is the List of year summary objects which has the data used to update the maximum value the y axis can be.
	 */
	private void updateMaximumRangeValue(List<YearSummary> inData) {
		
		maxRange = getMaximumRangeValue(inData);
		this.getChart().getCategoryPlot().getRangeAxis().setRange(0, maxRange + 5);
	}
	
	/**
	 * This method creates the JFreeChart based on all of the incoming data sets.
	 * 
	 * @param eventsDataset This is the data set corresponding to the events bar chart graph.
	 * @param recordersDataset This is the data set corresponding to the recorders line graph.
	 * @param samplesDataset This is the data set corresponding to the samples line graph.
	 * @return This returns a JFreeChart with all 3 of the graphs described.
	 */
	private static JFreeChart createChart(final CategoryDataset eventsDataset, final CategoryDataset recordersDataset,
			final CategoryDataset samplesDataset) {
			
		final CategoryItemRenderer eventsRenderer = new BarRenderer();
		((BarRenderer) eventsRenderer).setBarPainter(new StandardBarPainter()); // Remove shine
		((BarRenderer) eventsRenderer).setShadowVisible(false);
		eventsRenderer.setSeriesPaint(0, new Color(224, 0, 51));
		eventsRenderer.setBaseToolTipGenerator(new StandardCategoryToolTipGenerator());
		
		final CategoryPlot plot = new CategoryPlot();
		plot.setDataset(eventsDataset);
		plot.setRenderer(eventsRenderer);
		
		plot.setDomainAxis(new CategoryAxis(""));
		plot.setRangeAxis(new NumberAxis(""));
		
		plot.setOrientation(PlotOrientation.VERTICAL);
		plot.setRangeGridlinesVisible(true);
		plot.setDomainGridlinesVisible(true);
		
		plot.setDomainAxis(new NumericCategoryAxis());
		
		plot.getDomainAxis().setTickLabelFont(new Font("SansSerif", Font.PLAIN, 12));
		
		final CategoryItemRenderer recorderRenderer = new LineAndShapeRenderer(true, false);
		recorderRenderer.setSeriesStroke(0,
				new BasicStroke(2.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 1.0f, new float[] { 10.0f, 6.0f }, 0.0f));
		recorderRenderer.setSeriesPaint(0, new Color(102, 102, 255));
		recorderRenderer.setBaseToolTipGenerator(new StandardCategoryToolTipGenerator());
		
		// Temporarily removing recorders dataset
		// plot.setDataset(1, recordersDataset);
		// plot.setRenderer(1, recorderRenderer);
		
		final CategoryItemRenderer samplesRenderer = new LineAndShapeRenderer(true, false);
		samplesRenderer.setSeriesStroke(0, new BasicStroke(2.0f));
		samplesRenderer.setSeriesPaint(0, new Color(0, 153, 0));
		samplesRenderer.setBaseToolTipGenerator(new StandardCategoryToolTipGenerator());
		plot.setDataset(2, samplesDataset);
		plot.setRenderer(2, samplesRenderer);
		
		plot.setDatasetRenderingOrder(DatasetRenderingOrder.FORWARD);
		
		plot.getDomainAxis().setCategoryLabelPositions(CategoryLabelPositions.UP_90);
		plot.getDomainAxis().setLowerMargin(0.025);
		plot.getDomainAxis().setUpperMargin(0.025);
		
		final JFreeChart chart = new JFreeChart(plot);
		
		return chart;
	}
}
