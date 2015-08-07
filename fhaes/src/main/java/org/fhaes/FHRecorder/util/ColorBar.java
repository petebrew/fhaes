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
package org.fhaes.FHRecorder.util;

import java.util.List;

import javax.swing.BorderFactory;

import org.fhaes.FHRecorder.controller.FileController;
import org.fhaes.FHRecorder.util.CustomOptions.DataItem;
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
import org.jfree.ui.RectangleInsets;

/**
 * ColorBar Class. A color bar to be displayed by itself.
 * 
 * @author Seth Westphal
 */
public class ColorBar extends ChartPanel {
	
	private static final long serialVersionUID = 1L;
	
	/**
	 * Constructs a new color bar.
	 * 
	 * @param year the data to plot.
	 */
	public ColorBar(YearSummary year) {
		
		super(createChart(createDataset(year, FileController.getCustomOptions())));
		setPreferredSize(new java.awt.Dimension(100, 20));
		setBorder(BorderFactory.createEmptyBorder());
		setDomainZoomable(false); // Disable zooming
		setRangeZoomable(false);
		setPopupMenu(null); // Disable right-click menu
	}
	
	/**
	 * Create a data set to plot.
	 * 
	 * @param year the data.
	 * @param options the customization options.
	 * @return the created data set.
	 */
	private static CategoryDataset createDataset(YearSummary year, CustomOptions options) {
		
		DefaultCategoryDataset data = new DefaultCategoryDataset();
		for (int i = 1; i <= 6; i++)
			data.addValue(compileData(options.getDataItems(i), year), options.getGroupName(i), Integer.toString(year.getYear()));
		return data;
	}
	
	/**
	 * Compiles the data to be displayed based on the data and options given.
	 * 
	 * @param items the customization options.
	 * @param year the data.
	 * @return the value to display.
	 */
	private static int compileData(List<DataItem> items, YearSummary year) {
		
		int result = 0;
		for (DataItem item : items)
		{
			if (item == null)
				return 0;
				
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
	 * Updates the chart data and appearance.
	 * 
	 * @param year the data.
	 * @param options the customization options.
	 */
	public void updateChart(YearSummary year, CustomOptions options) {
		
		getChart().getCategoryPlot().setDataset(createDataset(year, options));
		for (int i = 1; i <= 6; i++)
			getChart().getCategoryPlot().getRenderer().setSeriesPaint(i - 1, options.getGroupColor(i));
	}
	
	/**
	 * Updates the chart data.
	 * 
	 * @param year new data.
	 */
	public void updateChartData(YearSummary year) {
		
		getChart().getCategoryPlot().setDataset(createDataset(year, FileController.getCustomOptions()));
	}
	
	/**
	 * Updates the chart appearance.
	 * 
	 * @param options the customization options.
	 */
	public void updateChartAppearance(CustomOptions options) {
		
		for (int i = 1; i <= 6; i++)
			getChart().getCategoryPlot().getRenderer().setSeriesPaint(i - 1, options.getGroupColor(i));
	}
	
	/**
	 * Creates a chart when given a data set.
	 * 
	 * @param dataset to be plotted.
	 * @return the created chart.
	 */
	private static JFreeChart createChart(final CategoryDataset dataset) {
		
		final JFreeChart chart = ChartFactory.createStackedBarChart("", "", "", dataset, PlotOrientation.HORIZONTAL, false, true, false);
		
		chart.setPadding(RectangleInsets.ZERO_INSETS);
		chart.setBorderVisible(false);
		
		StackedBarRenderer renderer = new StackedBarRenderer();
		renderer.setBarPainter(new StandardBarPainter()); // Remove shine
		renderer.setBaseToolTipGenerator(new StandardCategoryToolTipGenerator());
		renderer.setShadowVisible(false);
		
		CategoryPlot plot = chart.getCategoryPlot();
		plot.setRenderer(renderer);
		// plot.setBackgroundAlpha(0.0f);
		
		plot.setDomainGridlinesVisible(false);
		plot.setRangeGridlinesVisible(false);
		
		plot.getRangeAxis().setVisible(false);
		plot.getRangeAxis().setLowerMargin(0);
		plot.getRangeAxis().setUpperMargin(0);
		
		plot.getDomainAxis().setVisible(false);
		plot.getDomainAxis().setLowerMargin(0);
		plot.getDomainAxis().setUpperMargin(0);
		
		return chart;
	}
}
