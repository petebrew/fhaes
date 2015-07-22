/*******************************************************************************
 * Copyright (C) 2014 Peter Brewer and Joshua Brogan
 * 
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 * 
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 * 
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *     
 *     Contributors:
 *     		Peter Brewer
 *     		Joshua Brogan
 ******************************************************************************/
package org.fhaes.fhsamplesize.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;

import org.fhaes.enums.MiddleMetric;
import org.fhaes.fhsamplesize.model.AnalysisResultsModel;
import org.fhaes.segmentation.SegmentModel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.DeviationRenderer;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.YIntervalSeries;
import org.jfree.data.xy.YIntervalSeriesCollection;
import org.jfree.ui.RectangleInsets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.itextpdf.awt.DefaultFontMapper;
import com.itextpdf.text.Document;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfTemplate;
import com.itextpdf.text.pdf.PdfWriter;

/**
 * SSIZCurveChart Class.
 * 
 * Chart class for displaying the results of the FHSampleSize simulations analysis.
 * 
 * @author Joshua Brogan and Peter Brewer
 */
public class SSIZCurveChart extends ChartPanel {

	private static final Logger log = LoggerFactory.getLogger(SSIZCurveChart.class);
	private static final long serialVersionUID = 1L;

	private static JFreeChart chart;

	/**
	 * Standard constructor requires analysisResults array, which middle metric to chart, and which segment. Includes x,y coordinates for
	 * drawing cross-hairs for marking asymptote.
	 * 
	 * @param analysisResults
	 * @param metricToChart
	 * @param segment
	 * @param xcross
	 * @param ycross
	 */
	public SSIZCurveChart(AnalysisResultsModel[] analysisResults, MiddleMetric metricToChart, SegmentModel segment, Integer xcross,
			Integer ycross) {

		super(createChart(createEventsPerCenturyDataset(analysisResults, metricToChart, segment), xcross, ycross));
	}

	/**
	 * Standard constructor requires analysisResults array, which middle metric to chart, and which segment.
	 * 
	 * @param analysisResults
	 * @param metricToChart
	 * @param segment
	 */
	public SSIZCurveChart(AnalysisResultsModel[] analysisResults, MiddleMetric metricToChart, SegmentModel segment) {

		super(createChart(createEventsPerCenturyDataset(analysisResults, metricToChart, segment), null, null));
	}

	/**
	 * Create the chart.
	 * 
	 * @param eventsPerCenturyDataset
	 * @return
	 */
	private static JFreeChart createChart(final XYDataset eventsPerCenturyDataset, Integer xcross, Integer ycross) {

		// JFreeChart jfreechart = ChartFactory.createTimeSeriesChart(null, "Number of series resampled", "Number of events",
		// eventsPerCenturyDataset, true, true, false);

		JFreeChart jfreechart = ChartFactory.createScatterPlot(null, "Number of series resampled", "Number of events per century",
				eventsPerCenturyDataset);

		jfreechart.setBackgroundPaint(Color.WHITE);
		XYPlot xyplot = (XYPlot) jfreechart.getPlot();
		xyplot.setInsets(new RectangleInsets(5D, 5D, 5D, 20D));
		xyplot.setBackgroundPaint(Color.WHITE);
		xyplot.setAxisOffset(new RectangleInsets(5D, 5D, 5D, 5D));
		// xyplot.setDomainGridlinePaint(Color.white);
		// xyplot.setRangeGridlinePaint(Color.white);
		DeviationRenderer deviationrenderer = new DeviationRenderer(true, false);
		deviationrenderer.setSeriesStroke(0, new BasicStroke(3F, 1, 1));
		deviationrenderer.setSeriesStroke(0, new BasicStroke(3F, 1, 1));
		deviationrenderer.setSeriesStroke(1, new BasicStroke(3F, 1, 1));
		deviationrenderer.setSeriesFillPaint(0, new Color(255, 200, 200));
		deviationrenderer.setSeriesFillPaint(1, new Color(200, 200, 255));
		xyplot.setRenderer(deviationrenderer);
		NumberAxis numberaxis = (NumberAxis) xyplot.getRangeAxis();
		numberaxis.setAutoRangeIncludesZero(false);
		numberaxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
		jfreechart.removeLegend();

		if (xcross != null && ycross != null)
		{
			XYPlot xyp = jfreechart.getXYPlot();

			xyp.setRangeCrosshairVisible(true);
			xyp.setRangeCrosshairValue(ycross, true);
			xyp.setRangeCrosshairLockedOnData(true);

			xyp.setDomainCrosshairVisible(true);
			xyp.setDomainCrosshairValue(xcross, true);
			xyp.setDomainCrosshairLockedOnData(true);
		}

		// Initialize the chart variable for later use
		chart = jfreechart;

		return jfreechart;
	}

	/**
	 * Create the events per century dataset. Requires analysisResults array, which middle metric to chart, and which segment.
	 * 
	 * @param analysisResults
	 * @param metricToChart
	 * @param segment
	 * @return
	 */
	private static XYDataset createEventsPerCenturyDataset(AnalysisResultsModel[] analysisResults, MiddleMetric metricToChart,
			SegmentModel segment) {

		if (analysisResults == null || analysisResults.length == 0)
		{
			log.debug("No analysis results to draw");
			return null;
		}

		YIntervalSeries yintervalseries = new YIntervalSeries("Series");

		for (int i = 0; i < analysisResults.length; i++)
		{
			if (!analysisResults[i].getSegment().equals(segment))
				continue;

			if (metricToChart.equals(MiddleMetric.MEAN))
			{
				yintervalseries.add(analysisResults[i].getNumberOfSamples(), analysisResults[i].getMean(), analysisResults[i].getMean()
						- analysisResults[i].getConfidenceInterval95(),
						analysisResults[i].getMean() + analysisResults[i].getConfidenceInterval95());
			}
			else if (metricToChart.equals(MiddleMetric.MEDIAN))
			{
				yintervalseries.add(analysisResults[i].getNumberOfSamples(), analysisResults[i].getMedian(), analysisResults[i].getMean()
						- analysisResults[i].getConfidenceInterval95(),
						analysisResults[i].getMean() + analysisResults[i].getConfidenceInterval95());
			}
			else if (metricToChart.equals(MiddleMetric.WEIBULL_MEAN))
			{
				yintervalseries.add(analysisResults[i].getNumberOfSamples(), analysisResults[i].getWeibullMean(),
						analysisResults[i].getWeibullConfidenceInterval95Lower(), analysisResults[i].getWeibullConfidenceInterval95Upper());
			}
			else if (metricToChart.equals(MiddleMetric.WEIBULL_MEDIAN))
			{
				yintervalseries.add(analysisResults[i].getNumberOfSamples(), analysisResults[i].getWeibullMedian(),
						analysisResults[i].getWeibullConfidenceInterval95Lower(), analysisResults[i].getWeibullConfidenceInterval95Upper());
			}
			else
			{
				log.error("Chart does not supported the specified middle metric type");
			}

		}
		YIntervalSeriesCollection yintervalseriescollection = new YIntervalSeriesCollection();
		yintervalseriescollection.addSeries(yintervalseries);
		return yintervalseriescollection;
	}

	/**
	 * Save chart as PDF file. Requires iText library.
	 * 
	 * @param chart JFreeChart to save.
	 * @param fileName Name of file to save chart in.
	 * @param width Width of chart graphic.
	 * @param height Height of chart graphic.
	 * @throws Exception if failed.
	 * @see <a href="http://www.lowagie.com/iText">iText</a>
	 */
	@SuppressWarnings("deprecation")
	public static void writeAsPDF(File fileToSave, int width, int height) throws Exception {

		if (chart != null)
		{
			BufferedOutputStream out = null;
			try
			{
				out = new BufferedOutputStream(new FileOutputStream(fileToSave.getAbsolutePath()));

				// convert chart to PDF with iText:
				Rectangle pagesize = new Rectangle(width, height);
				Document document = new Document(pagesize, 50, 50, 50, 50);
				try
				{
					PdfWriter writer = PdfWriter.getInstance(document, out);
					document.addAuthor("JFreeChart");
					document.open();

					PdfContentByte cb = writer.getDirectContent();
					PdfTemplate tp = cb.createTemplate(width, height);
					Graphics2D g2 = tp.createGraphics(width, height, new DefaultFontMapper());

					Rectangle2D r2D = new Rectangle2D.Double(0, 0, width, height);
					chart.draw(g2, r2D, null);
					g2.dispose();
					cb.addTemplate(tp, 0, 0);
				}
				finally
				{
					document.close();
				}
			}
			finally
			{
				if (out != null)
					out.close();
			}
		}
	}

	/**
	 * Overridden methods to prevent right-click menu from popping up.
	 */
	@Override
	public void mousePressed(MouseEvent arg0) {

	}

	@Override
	public void mouseReleased(MouseEvent arg0) {

	}
}
