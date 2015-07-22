/*******************************************************************************
 * Copyright (C) 2012 Hidayatullah Ahsan, Elena Velasquez
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * Contributors:
 *     Elena Velasquez
 *     Hidayatullah Ahsan
 *     Peter Brewer
 *     Joshua Brogan
 ******************************************************************************/
package org.fhaes.jsea;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Paint;
import java.awt.geom.Ellipse2D;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.DatasetRenderingOrder;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.CategoryItemRenderer;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.chart.renderer.category.StandardBarPainter;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.ui.GradientPaintTransformType;
import org.jfree.ui.StandardGradientPaintTransformer;

/**
 * JSEABarChart Class.
 */
public class JSEABarChart extends ChartPanel {

	private static final long serialVersionUID = 1L;

	private static double[] meanByWindow;
	private static int lengthOfWindow;
	private static int yearsPriorOfEvent;
	private static double[][] leftEndPointSim;
	private static double[][] rightEndPointSim;
	private static int alphaLevel;
	private static JFreeChart chart;

	/**
	 * TODO
	 * 
	 * @param title
	 * @param meanByWindow
	 * @param lengthOfWindow
	 * @param yearsPriorOfEvent
	 * @param yearsAfterEvent
	 * @param leftEndPointSim
	 * @param rightEndPointSim
	 * @param outputFilePrefix
	 * @param alphaLevel
	 * @param segmentIndex
	 */
	public JSEABarChart(String title, double[] meanByWindow, int lengthOfWindow, int yearsPriorOfEvent, int yearsAfterEvent,
			double[][] leftEndPointSim, double[][] rightEndPointSim, String outputFilePrefix, int alphaLevel, int segmentIndex) {

		super(createChart(title, meanByWindow, lengthOfWindow, yearsPriorOfEvent, yearsAfterEvent, leftEndPointSim, rightEndPointSim,
				outputFilePrefix, alphaLevel, segmentIndex));
	}

	/**
	 * TODO
	 * 
	 * @param m
	 */
	public JSEABarChart(BarChartParametersModel m) {

		super(createChart(m.getTitle(), m.getMeanByWindow(), m.getLengthOfWindow(), m.getYearsPriorOfEvent(), m.getYearsAfterEvent(),
				m.getLeftEndPointSim(), m.getRightEndPointSim(), m.getOutputFilePrefix(), m.getAlphaLevel(), m.getSegmentIndex()));
	}

	/**
	 * Creates a demo chart.
	 * 
	 * @return A chart.
	 */
	@SuppressWarnings("deprecation")
	public static JFreeChart createChart(String title, double[] meanByWindow, int lengthOfWindow, int yearsPriorOfEvent,
			int yearsAfterEvent, double[][] leftEndPointSim, double[][] rightEndPointSim, String outputFilePrefix, int alphaLevel,
			int segmentIndex) {

		JSEABarChart.meanByWindow = meanByWindow;
		JSEABarChart.lengthOfWindow = lengthOfWindow;
		JSEABarChart.yearsPriorOfEvent = yearsPriorOfEvent;
		JSEABarChart.leftEndPointSim = leftEndPointSim;
		JSEABarChart.rightEndPointSim = rightEndPointSim;
		JSEABarChart.alphaLevel = alphaLevel;

		CategoryPlot plot = new CategoryPlot();
		plot.setDataset(0, createDataset());
		plot.setOrientation(PlotOrientation.VERTICAL);

		CustomBarRenderer renderer = new CustomBarRenderer(createPaint(lengthOfWindow, Color.gray));
		renderer.setBarPainter(new StandardBarPainter());
		renderer.setDrawBarOutline(false);
		renderer.setOutlinePaint(Color.yellow);
		renderer.setOutlineStroke(new BasicStroke(1.1f, BasicStroke.JOIN_ROUND, BasicStroke.JOIN_BEVEL));
		renderer.setGradientPaintTransformer(new StandardGradientPaintTransformer(GradientPaintTransformType.CENTER_HORIZONTAL));
		plot.setRenderer(0, renderer);
		Color allcolors[] = { Color.red, Color.green, Color.blue };

		System.out.println("here is the alphlevel " + alphaLevel);
		// for (int k = 0; k <= 5; k++) {
		// if (k <= 2) {
		// / plot.setDataset(k + 1, createEndDataset(k, true));
		// plot.setRenderer(k + 1, createCategoryItemRenderer(allcolors[k], k));
		// } else {
		// plot.setDataset(k + 1, createEndDataset(k - 3, false));
		// plot.setRenderer(k + 1, createCategoryItemRenderer(allcolors[k - 3], k - 3));
		// }
		// }
		// for (int k = 0; k <1; k++) {
		// if (k <= 2) {
		plot.setDataset(1, createEndDataset(alphaLevel, true));
		plot.setRenderer(1, createCategoryItemRenderer(allcolors[alphaLevel], alphaLevel));
		// } else {
		plot.setDataset(4, createEndDataset(alphaLevel, false));
		plot.setRenderer(4, createCategoryItemRenderer(allcolors[alphaLevel], alphaLevel));
		// }
		// }
		plot.setDatasetRenderingOrder(DatasetRenderingOrder.FORWARD);
		// plot.setBackgroundPaint(Color.WHITE);
		plot.setDomainAxis(new CategoryAxis("LAG"));
		plot.setRangeAxis(new NumberAxis(outputFilePrefix));
		plot.setRangeGridlinesVisible(false);

		JFreeChart chart = new JFreeChart(plot);
		chart.setTitle(title);
		chart.removeLegend();
		chart.setBackgroundPaint(Color.WHITE);

		return chart;
	}

	public JFreeChart getChart() {

		return chart;
	}

	/**
	 * TODO
	 * 
	 * @param c
	 * @param k
	 * @return
	 */
	@SuppressWarnings("deprecation")
	private static CategoryItemRenderer createCategoryItemRenderer(Paint c, int k) {

		CategoryItemRenderer renderer = new LineAndShapeRenderer();
		renderer.setPaint(Color.black);
		renderer.setShape(new Ellipse2D.Double(0, 0, 0, 0));
		if (k == 3)
		{
			renderer.setSeriesStroke(0, new BasicStroke(3.0f));
		}
		if (k == 2)
		{
			renderer.setSeriesStroke(0, new BasicStroke(3.0f, BasicStroke.JOIN_MITER, BasicStroke.JOIN_ROUND, 1.0f, new float[] { 1.0f,
					5.0f }, 0.0f));
		}
		if (k == 1)
		{
			renderer.setSeriesStroke(0, new BasicStroke(3.0f, BasicStroke.JOIN_BEVEL, BasicStroke.JOIN_ROUND, 1.0f, new float[] { 5.0f,
					10.0f }, 0.0f));
		}

		return renderer;
	}

	/**
	 * Creates a dataset for the LAG bars.
	 * 
	 * @return a the dataset.
	 */
	private static DefaultCategoryDataset createDataset() {

		DefaultCategoryDataset dataset = new DefaultCategoryDataset();
		int k;
		for (int n = 0; n < lengthOfWindow; n++)
		{
			k = n - yearsPriorOfEvent;
			dataset.addValue(meanByWindow[n], "LAG", new Integer(k).toString());
		}
		return dataset;
	}

	/**
	 * Creates a dataset for the lines on left.
	 * 
	 * @return a the dataset.
	 */
	private static DefaultCategoryDataset createEndDataset(int level, boolean left) {

		DefaultCategoryDataset dataset = new DefaultCategoryDataset();
		int k;
		for (int n = 0; n < lengthOfWindow; n++)
		{
			k = n - yearsPriorOfEvent;
			if (left == true)
				dataset.addValue(leftEndPointSim[n][level], "", new Integer(k).toString());
			else
				dataset.addValue(rightEndPointSim[n][level], "", new Integer(k).toString());
		}
		return dataset;
	}

	/**
	 * Returns an array of paint objects that will be used for the bar colors. this method sets the colors for each bar.
	 * 
	 * @return An array of paint objects.
	 */
	private static Paint[] createPaint(int lengthOfWindow, Color color) {

		Paint[] colors = new Paint[lengthOfWindow];
		for (int i = 0; i <= lengthOfWindow - 1; i++)
		{
			colors[i] = new GradientPaint(0f, 0f, color, 0f, 0f, color);
			// colors[i] = new GradientPaint(0f, 0f, Color.pink, 0f, 0f, Color.yellow);
		}
		for (int i = 0; i <= lengthOfWindow - 1; i++)
		{
			if (meanByWindow[i] > leftEndPointSim[i][alphaLevel] && meanByWindow[i] < rightEndPointSim[i][alphaLevel])
			{
				colors[i] = new GradientPaint(0f, 0f, Color.darkGray, 0f, 0f, Color.darkGray);
				// colors[i] = new GradientPaint(0f, 0f, Color.CYAN, 0f, 0f,
				// Color.ORANGE);
			}
		}
		return colors;
	}

	static class CustomBarRenderer extends BarRenderer {

		private static final long serialVersionUID = 1L;

		/** The colors. */
		private Paint[] colors;

		/**
		 * Creates a new renderer.
		 * 
		 * @param colors the colors.
		 */
		public CustomBarRenderer(Paint[] colors) {

			this.setShadowVisible(false);
			this.colors = colors;
		}

		/**
		 * Returns the paint for an item. Overrides the default behavior inherited from AbstractSeriesRenderer.
		 * 
		 * @param row the series.
		 * @param column the category.
		 * 
		 * @return The item color.
		 */
		public Paint getItemPaint(int row, int column) {

			return this.colors[column % this.colors.length];
		}
	}
}
