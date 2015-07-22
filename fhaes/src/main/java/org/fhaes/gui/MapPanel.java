/*******************************************************************************
 * Copyright (c) 2013 Peter Brewer
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 * 
 * Contributors:
 *     Peter Brewer
 *     Elena Velasquez
 ******************************************************************************/
package org.fhaes.gui;

/*******************************************************************************
 * Copyright (C) 2013 Peter Brewer
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
 *     Peter Brewer
 ******************************************************************************/

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.fhaes.components.JToolBarButton;
import org.fhaes.model.FHFile;
import org.fhaes.model.FHFileGroup;
import org.fhaes.util.ColorPalette;
import org.fhaes.util.FHAESMapMarker;
import org.fhaes.util.Platform;
import org.openstreetmap.gui.jmapviewer.DefaultMapController;
import org.openstreetmap.gui.jmapviewer.JMapViewer;
import org.openstreetmap.gui.jmapviewer.JMapViewerTree;
import org.openstreetmap.gui.jmapviewer.Layer;
import org.openstreetmap.gui.jmapviewer.MapMarkerDot;
import org.openstreetmap.gui.jmapviewer.OsmFileCacheTileLoader;
import org.openstreetmap.gui.jmapviewer.OsmTileLoader;
import org.openstreetmap.gui.jmapviewer.events.JMVCommandEvent;
import org.openstreetmap.gui.jmapviewer.interfaces.JMapViewerEventListener;
import org.openstreetmap.gui.jmapviewer.interfaces.TileLoader;
import org.openstreetmap.gui.jmapviewer.interfaces.TileSource;
import org.openstreetmap.gui.jmapviewer.tilesources.BingAerialTileSource;
import org.openstreetmap.gui.jmapviewer.tilesources.MapQuestOpenAerialTileSource;
import org.openstreetmap.gui.jmapviewer.tilesources.MapQuestOsmTileSource;
import org.openstreetmap.gui.jmapviewer.tilesources.OsmTileSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * MapPanel Class.
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
public class MapPanel extends JPanel implements JMapViewerEventListener {

	private static final Logger log = LoggerFactory.getLogger(MapPanel.class);

	private static final long serialVersionUID = 1L;

	private JPanel holder;

	private JMapViewerTree treeMap = null;
	private JLabel lblGeoParseError;
	private Layer fhaesSites;

	/**
	 * TODO
	 * 
	 * @param files
	 */
	public MapPanel(ArrayList<FHFile> files) {

		holder = new JPanel();
		if (Platform.isOSX())
			holder.setBackground(MainWindow.macBGColor);

		holder.setLayout(new BorderLayout(0, 0));
		initGUI();
		setFHFiles(files);
	}

	/**
	 * TODO
	 */
	public MapPanel() {

		holder = new JPanel();
		if (Platform.isOSX())
			holder.setBackground(MainWindow.macBGColor);

		holder.setLayout(new BorderLayout(0, 0));
		initGUI();
		setFHFiles(null);
	}

	/**
	 * TODO
	 * 
	 * @param groups
	 */
	public void setFHFileGroups(ArrayList<FHFileGroup> groups) {

		if (groups == null)
			return;
		if (groups.size() == 0)
			return;

		ColorPalette palette = new ColorPalette();

		for (FHFileGroup group : groups)
		{
			Color color = palette.getNextColor();
			for (FHFile file : group.getFiles())
			{
				Double lat = file.getFirstLatitudeDbl();
				Double lon = file.getFirstLongitudeDbl();
				String label = group.getName() + " - " + file.getLabel();

				if (lat != null && lon != null)
				{
					getMapViewerTree().addMapMarker(new FHAESMapMarker(color, lat, lon));
				}
				else
				{
					log.debug("lat or long is null for " + label);
				}
			}
		}

	}

	public void setFHFiles(ArrayList<FHFile> files) {

		getMapViewerTree().removeAllMapMarkers();

		int failedParseCount = 0;
		if (files != null)
		{
			for (FHFile file : files)
			{
				Double lat = file.getFirstLatitudeDbl();
				Double lon = file.getFirstLongitudeDbl();
				String label = file.getLabel();

				if (lat != null && lon != null)
				{
					log.debug("Adding marker for " + label + " at " + lat.toString() + ", " + lon.toString());
					getMapViewerTree().addMapMarker(new MapMarkerDot(fhaesSites, label, lat, lon));
				}
				else
				{
					log.debug("lat or long is null for " + label);
					failedParseCount++;
				}
			}

		}

		if (failedParseCount == 1)
		{
			lblGeoParseError.setText("Failed to parse coordinates from one input file.");
		}
		else if (failedParseCount > 1)
		{
			lblGeoParseError.setText("Failed to parse coordinates from " + failedParseCount + " input files.");
		}

	}

	private void initGUI() {

		this.removeAll();
		setLayout(new BorderLayout(0, 0));
		if (Platform.isOSX())
			setBackground(MainWindow.macBGColor);

		treeMap = new JMapViewerTree("Zones");
		fhaesSites = treeMap.addLayer("Sites");
		fhaesSites.setVisibleTexts(false);
		holder.removeAll();

		holder.add(treeMap, BorderLayout.CENTER);
		this.add(holder, BorderLayout.CENTER);

		// Listen to the map viewer for user operations so components will
		// receive events and update
		getMapViewerTree().addJMVListener(this);

		setLayout(new BorderLayout());

		JPanel panel = new JPanel();
		if (Platform.isOSX())
			panel.setBackground(MainWindow.macBGColor);

		JPanel panelTop = new JPanel();
		if (Platform.isOSX())
			panelTop.setBackground(MainWindow.macBGColor);

		JPanel panelBottom = new JPanel();
		if (Platform.isOSX())
			panelBottom.setBackground(MainWindow.macBGColor);

		JPanel helpPanel = new JPanel();
		if (Platform.isOSX())
			helpPanel.setBackground(MainWindow.macBGColor);

		DefaultMapController mapController = new DefaultMapController(treeMap.getViewer());
		mapController.setMovementMouseButton(MouseEvent.BUTTON1);

		add(panel, BorderLayout.NORTH);
		add(helpPanel, BorderLayout.SOUTH);
		panel.setLayout(new BorderLayout());
		panel.add(panelTop, BorderLayout.NORTH);
		panel.add(panelBottom, BorderLayout.SOUTH);
		lblGeoParseError = new JLabel("");
		helpPanel.add(lblGeoParseError);
		JToolBarButton btnFitToMarkers = new JToolBarButton("zoomtomarkers.png", "Zoom to markers");

		btnFitToMarkers.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {

				getMapViewerTree().setDisplayToFitMapMarkers();
			}
		});
		JComboBox tileSourceSelector = new JComboBox(new TileSource[] { new OsmTileSource.Mapnik(), new OsmTileSource.CycleMap(),
				new BingAerialTileSource(), new MapQuestOsmTileSource(), new MapQuestOpenAerialTileSource() });
		tileSourceSelector.addItemListener(new ItemListener() {

			public void itemStateChanged(ItemEvent e) {

				getMapViewerTree().setTileSource((TileSource) e.getItem());
			}
		});
		JComboBox tileLoaderSelector;
		try
		{
			tileLoaderSelector = new JComboBox(new TileLoader[] { new OsmFileCacheTileLoader(getMapViewerTree()),
					new OsmTileLoader(getMapViewerTree()) });
		}
		catch (IOException e)
		{
			tileLoaderSelector = new JComboBox(new TileLoader[] { new OsmTileLoader(getMapViewerTree()) });
		}
		tileLoaderSelector.addItemListener(new ItemListener() {

			public void itemStateChanged(ItemEvent e) {

				getMapViewerTree().setTileLoader((TileLoader) e.getItem());
			}
		});
		getMapViewerTree().setTileLoader((TileLoader) tileLoaderSelector.getSelectedItem());

		JToolBarButton btnToggleLabels = new JToolBarButton("label.png", "Toggle labels");
		btnToggleLabels.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {

				fhaesSites.setVisibleTexts(!fhaesSites.isVisibleTexts());
				getMapViewerTree().repaint();
			}
		});

		JLabel lblsource = new JLabel("Map style:");
		panelTop.add(lblsource);
		panelTop.add(tileSourceSelector);
		panelTop.add(btnFitToMarkers);
		panelTop.add(btnToggleLabels);

		// panelTop.add(tileLoaderSelector);
		final JCheckBox showMapMarker = new JCheckBox("Map markers visible");
		showMapMarker.setSelected(getMapViewerTree().getMapMarkersVisible());
		showMapMarker.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {

				getMapViewerTree().setMapMarkerVisible(showMapMarker.isSelected());
			}
		});
		// panelBottom.add(showMapMarker);
		// /
		final JCheckBox showTreeLayers = new JCheckBox("Tree Layers visible");
		showTreeLayers.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {

				treeMap.setTreeVisible(showTreeLayers.isSelected());
			}
		});
		// panelBottom.add(showTreeLayers);
		// /
		final JCheckBox showToolTip = new JCheckBox("ToolTip visible");
		showToolTip.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {

				getMapViewerTree().setToolTipText(null);
			}
		});
		// panelBottom.add(showToolTip);
		// /
		final JCheckBox showTileGrid = new JCheckBox("Tile grid visible");
		showTileGrid.setSelected(getMapViewerTree().isTileGridVisible());
		showTileGrid.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {

				getMapViewerTree().setTileGridVisible(showTileGrid.isSelected());
			}
		});
		// panelBottom.add(showTileGrid);
		final JCheckBox showZoomControls = new JCheckBox("Show zoom controls");
		showZoomControls.setSelected(getMapViewerTree().getZoomContolsVisible());
		showZoomControls.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {

				getMapViewerTree().setZoomContolsVisible(showZoomControls.isSelected());
			}
		});
		// panelBottom.add(showZoomControls);
		final JCheckBox scrollWrapEnabled = new JCheckBox("Scrollwrap enabled");
		scrollWrapEnabled.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {

				getMapViewerTree().setScrollWrapEnabled(scrollWrapEnabled.isSelected());
			}
		});
		// panelBottom.add(scrollWrapEnabled);

		// panelTop.add(zoomLabel);
		// panelTop.add(zoomValue);
		// panelTop.add(mperpLabelName);
		// panelTop.add(mperpLabelValue);

		add(treeMap, BorderLayout.CENTER);

		//

		getMapViewerTree().setDisplayPositionByLatLon(0, 0, 1);
		getMapViewerTree().setDisplayToFitMapMarkers();

		getMapViewerTree().addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent e) {

				if (e.getButton() == MouseEvent.BUTTON1)
				{
					getMapViewerTree().getAttribution().handleAttribution(e.getPoint(), true);
				}
			}
		});

		getMapViewerTree().addMouseMotionListener(new MouseAdapter() {

			@Override
			public void mouseMoved(MouseEvent e) {

				Point p = e.getPoint();
				boolean cursorHand = getMapViewerTree().getAttribution().handleAttributionCursor(p);
				if (cursorHand)
				{
					getMapViewerTree().setCursor(new Cursor(Cursor.HAND_CURSOR));
				}
				else
				{
					getMapViewerTree().setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
				}
				if (showToolTip.isSelected())
					getMapViewerTree().setToolTipText(getMapViewerTree().getPosition(p).toString());
			}
		});

	}

	private JMapViewer getMapViewerTree() {

		return treeMap.getViewer();
	}

	public void processCommand(JMVCommandEvent command) {

		if (command.getCommand().equals(JMVCommandEvent.COMMAND.ZOOM) || command.getCommand().equals(JMVCommandEvent.COMMAND.MOVE))
		{

		}
	}

}
