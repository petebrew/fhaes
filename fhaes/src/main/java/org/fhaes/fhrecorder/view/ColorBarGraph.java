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
package org.fhaes.fhrecorder.view;

import java.awt.Color;
import java.awt.Font;
import java.util.List;

import org.fhaes.fhrecorder.controller.FileController;
import org.fhaes.fhrecorder.util.CustomOptions;
import org.fhaes.fhrecorder.util.YearSummary;
import org.fhaes.fhrecorder.util.CustomOptions.DataItem;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.labels.StandardCategoryToolTipGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.StackedBarRenderer;
import org.jfree.chart.renderer.category.StandardBarPainter;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.category.SlidingCategoryDataset;

/**
 * ColorBarGraph Class. This is a graph that displays color bars.
 * 
 * @author Seth Westphal
 */
public class ColorBarGraph extends ChartPanel {
	
	private static final long serialVersionUID = 1L;
	
	/**
	 * Constructor creates a new graph.
	 * 
	 * @param years the data to graph.
	 */
	public ColorBarGraph(List<YearSummary> years) {
		
		super(createChart(createDataset(years)));
	}
	
	/**
	 * Creates a data set for graphing.
	 * 
	 * @param years the data
	 * @return the data set
	 */
	private static CategoryDataset createDataset(List<YearSummary> years) {
		
		CustomOptions options = FileController.getCustomOptions();
		DefaultCategoryDataset data = new DefaultCategoryDataset();
		
		for (YearSummary year : years)
			for (int i = 1; i <= 6; i++)
				data.addValue(compileData(options.getDataItems(i), year), options.getGroupName(i), Integer.toString(year.getYear()));
				
		return new SlidingCategoryDataset(data, 0, FileController.MAX_VISIBLE_GRAPH_COLUMNS);
	}
	
	/**
	 * Compiles the appropriate data given the current options.
	 * 
	 * @param items the data items to add.
	 * @param year the year.
	 * @return the total amount of data.
	 */
	private static int compileData(List<DataItem> items, YearSummary year) {
		
		int result = 0;
		for (DataItem item : items)
		{
			if (item == null)
			{
				return 0;
			}
			
			switch (item)
			{
				case DORMANT_SEASON:
					result += year.getNumDormantSeason();
					break;
				case EARLY_EARLYWOOD:
					result += year.getNumEarlyEarlywood();
					break;
				case MIDDLE_EARLYWOOD:
					result += year.getNumMiddleEarlywood();
					break;
				case LATE_EARLYWOOD:
					result += year.getNumLateEarlywood();
					break;
				case LATEWOOD:
					result += year.getNumLatewood();
					break;
				case UNDETERMINED:
					result += year.getNumUndetermined();
					break;
				// Temporarily removing recording years
				/*
				 * case RECORDING_YEARS: result += year.getNumRecorders(); break;
				 */
				case BLANK_YEARS:
					result += year.getNumBlank();
					break;
			}
		}
		return result;
	}
	
	/**
	 * Updates the data of the chart.
	 * 
	 * @param years the data.
	 * @param savePosition true to save the position, else false.
	 */
	public void updateChartData(List<YearSummary> years, boolean savePosition) {
		
		SlidingCategoryDataset dataset = (SlidingCategoryDataset) createDataset(years);
		if (savePosition && years.size() > 0)
		{
			int index = ((SlidingCategoryDataset) getChart().getCategoryPlot().getDataset()).getFirstCategoryIndex();
			dataset.setFirstCategoryIndex(index);
		}
		getChart().getCategoryPlot().setDataset(dataset);
	}
	
	/**
	 * Updates the colors of the graph.
	 * 
	 * @param options the options.
	 */
	public void updateChartAppearance(CustomOptions options) {
		
		for (int i = 1; i <= 6; i++)
			getChart().getCategoryPlot().getRenderer().setSeriesPaint(i - 1, options.getGroupColor(i));
	}
	
	/**
	 * Creates the chart with the data from the given data set.
	 * 
	 * @param dataset the data to plot.
	 * @return the chart.
	 */
	private static JFreeChart createChart(final CategoryDataset dataset) {
		
		final JFreeChart chart = ChartFactory.createStackedBarChart("", "", "", dataset, PlotOrientation.VERTICAL, false, true, false);
		
		StackedBarRenderer renderer = new StackedBarRenderer(true);
		renderer.setShadowVisible(false);
		renderer.setBarPainter(new StandardBarPainter()); // Remove shine
		renderer.setBaseToolTipGenerator(new StandardCategoryToolTipGenerator());
		
		CategoryPlot plot = chart.getCategoryPlot();
		plot.setRenderer(renderer);
		
		plot.setBackgroundPaint(Color.WHITE);
		plot.setDomainGridlinesVisible(true);
		plot.setDomainGridlinePaint(new Color(192, 192, 192));
		
		plot.getDomainAxis().setTickLabelFont(new Font("SansSerif", Font.PLAIN, 12));
		plot.getDomainAxis().setVisible(false);
		plot.getRangeAxis().setVisible(false);
		plot.getDomainAxis().setLowerMargin(.025);
		plot.getDomainAxis().setUpperMargin(.025);
		
		chart.setBackgroundPaint(new Color(214, 217, 233, 30));
		
		return chart;
	}
}
