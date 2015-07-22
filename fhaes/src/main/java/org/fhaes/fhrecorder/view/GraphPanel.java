/*******************************************************************************
 * Copyright (C) 2014 Cody Calhoun, Anthony Messerschmidt, Seth Westphal,
 * Scott Goble and Peter Brewer
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
 ******************************************************************************/

/*******************************************************************************
 * Maintenance Log (Spring 2014)
 * 
 *     All maintenance work was performed collectively by Code Calhoun, 
 *     Anthony Messerschmidt and Seth Westphal.
 *     
 *     1) This file was added to hold all of the graphics information and graphs
 *     for the graphics tab of the FireHistoryRecorder.
 ******************************************************************************/
package org.fhaes.fhrecorder.view;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.List;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JScrollBar;

import net.miginfocom.swing.MigLayout;

import org.fhaes.fhrecorder.controller.FileController;
import org.fhaes.fhrecorder.utility.NumericCategoryAxis;
import org.fhaes.fhrecorder.utility.YearSummary;
import org.fhaes.util.Builder;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.data.category.SlidingCategoryDataset;

/**
 * GUI_GraphPanel Class. Displays graphs for color bars and other statistics.
 * 
 * @author Cody Calhoun, Anthony Messerschmidt, & Seth Westphal
 */
public class GraphPanel extends javax.swing.JPanel {

	private static final long serialVersionUID = 1L;
	List<YearSummary> data;
	private final ColorBarGraph colorPane;
	private final GraphSummaryOverlay overlayPane;
	private final JScrollBar scrollBar;
	private Component rigidArea;
	private int zoomLevel = 1;
	private JButton zoomInButton;
	private JButton zoomOutButton;

	/**
	 * Constructor for the Graphics Panel. Sets up layout and settings of all components.
	 */
	public GraphPanel() {

		data = FileController.getYearSummaryList();
		setLayout(new MigLayout("", "[grow,right]", "[fill][300px,grow,fill][][]"));

		JButton customizeButton = new JButton("Customize");
		customizeButton.setIcon(Builder.getImageIcon("configure.png"));
		customizeButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent arg0) {

				showCustomizeWindow();
			}
		});

		zoomOutButton = new JButton("");
		zoomOutButton.setIcon(Builder.getImageIcon("zoom_out.png"));
		zoomOutButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {

				setZoomLevel(zoomLevel + 1);
			}

		});
		add(zoomOutButton, "flowx,cell 0 0");

		zoomInButton = new JButton("");
		zoomInButton.setIcon(Builder.getImageIcon("zoom_in.png"));
		zoomInButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {

				setZoomLevel(zoomLevel - 1);
			}

		});
		add(zoomInButton, "cell 0 0");
		add(customizeButton, "cell 0 0,alignx right");

		rigidArea = Box.createRigidArea(new Dimension(20, 20));
		rigidArea.setMaximumSize(new Dimension(200, 20));
		rigidArea.setMinimumSize(new Dimension(1, 20));
		add(rigidArea, "cell 0 1");
		colorPane = new ColorBarGraph(data);

		colorPane.addMouseWheelListener(new MouseWheelListener() {

			@Override
			public void mouseWheelMoved(MouseWheelEvent e) {

				int notches = e.getWheelRotation();
				setZoomLevel(zoomLevel + notches);
			}

		});

		add(colorPane, "cell 0 1,growx");

		scrollBar = new JScrollBar();
		scrollBar.setMinimum(0);
		scrollBar.setMaximum(data.size());
		scrollBar.addAdjustmentListener(new AdjustmentListener() {

			public void adjustmentValueChanged(AdjustmentEvent event) {

				setChartsFirstCategoryIndex(event.getValue());
			}
		});

		overlayPane = new GraphSummaryOverlay(data);

		overlayPane.addMouseWheelListener(new MouseWheelListener() {

			@Override
			public void mouseWheelMoved(MouseWheelEvent e) {

				int notches = e.getWheelRotation();
				setZoomLevel(zoomLevel + notches);
			}

		});
		add(overlayPane, "cell 0 2,growx");
		scrollBar.setOrientation(JScrollBar.HORIZONTAL);
		add(scrollBar, "cell 0 3,growx");

		refreshCharts(false);
	}

	/**
	 * Set the level of zoom from 1 to 20. Integers outside this range are ignored. The zoom level * 10 is the number of categories shown on
	 * the graph.
	 * 
	 * @param index
	 */
	private void setZoomLevel(int zoomlevel) {

		if (zoomlevel < 1 || zoomlevel > 20)
			return;

		zoomLevel = zoomlevel;
		setZoom();

	}

	/**
	 * Refreshes the data in the charts and their position.
	 * 
	 * @param savePosition keeps the current scroll bar position and position of the graphs if true, sets to 0 if false.
	 */
	public void refreshCharts(boolean savePosition) {

		data = FileController.getYearSummaryList();
		colorPane.updateChartData(data, savePosition);
		colorPane.updateChartAppearance(FileController.getCustomOptions());
		overlayPane.updateChartData(data, savePosition);

		scrollBar.setMinimum(0);
		scrollBar.setMaximum(data.size());
		if (!savePosition)
			scrollBar.setValue(0);

		rigidArea.setSize(overlayPane.getMaximumRangeLabelWidth(), rigidArea.getHeight());
		setZoom();

	}

	/**
	 * Sets the first category index of the chart.
	 * 
	 * @param index the index to set the first category of the chart to.
	 */
	public void setChartsFirstCategoryIndex(int index) {

		if (index > -1 && index < data.size())
		{
			((SlidingCategoryDataset) colorPane.getChart().getCategoryPlot().getDataset()).setFirstCategoryIndex(index);
			((SlidingCategoryDataset) overlayPane.getChart().getCategoryPlot().getDataset(0)).setFirstCategoryIndex(index);
			((SlidingCategoryDataset) overlayPane.getChart().getCategoryPlot().getDataset(1)).setFirstCategoryIndex(index);
			((SlidingCategoryDataset) overlayPane.getChart().getCategoryPlot().getDataset(2)).setFirstCategoryIndex(index);
			setZoom();
		}
	}

	/**
	 * Set the zoom level on the graphs. This also handles changing the labels on the axis to avoid collisions.
	 */
	private void setZoom() {

		((SlidingCategoryDataset) colorPane.getChart().getCategoryPlot().getDataset()).setMaximumCategoryCount(zoomLevel * 10);
		((SlidingCategoryDataset) overlayPane.getChart().getCategoryPlot().getDataset(0)).setMaximumCategoryCount(zoomLevel * 10);
		((SlidingCategoryDataset) overlayPane.getChart().getCategoryPlot().getDataset(1)).setMaximumCategoryCount(zoomLevel * 10);
		((SlidingCategoryDataset) overlayPane.getChart().getCategoryPlot().getDataset(2)).setMaximumCategoryCount(zoomLevel * 10);

		scrollBar.setMaximum(data.size() - (zoomLevel * 10) + 10);
		System.out.println("Zoom level = " + zoomLevel);
		System.out.println("Max scroll bar value = " + (data.size() - (zoomLevel * 10)));

		CategoryPlot plot = (CategoryPlot) overlayPane.getChart().getPlot();
		NumericCategoryAxis axis = (NumericCategoryAxis) plot.getDomainAxis();

		if (zoomLevel <= 3)
		{
			axis.setLabelEveryXCategories(1);
		}
		else if (zoomLevel >= 4 && zoomLevel <= 8)
		{
			axis.setLabelEveryXCategories(5);
		}
		else
		{
			axis.setLabelEveryXCategories(10);

		}
	}

	/**
	 * Shows the customization window.
	 */
	private void showCustomizeWindow() {

		if (new CustomizeDialog(this, true).showDialog())
			refreshCharts(true);
	}
}
