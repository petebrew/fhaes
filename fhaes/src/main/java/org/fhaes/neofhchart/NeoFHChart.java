/**************************************************************************************************
 * Fire History Analysis and Exploration System (FHAES), Copyright (C) 2015
 * 
 * Contributors: Aaron Decker, Michael Ababio, Zachariah Ferree, Matthew Willie, Peter Brewer
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

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.io.File;
import java.util.HashMap;

import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.filechooser.FileFilter;

import org.apache.batik.swing.JSVGCanvas;
import org.apache.batik.swing.JSVGScrollPane;
import org.apache.batik.swing.gvt.AbstractPanInteractor;
import org.codehaus.plexus.util.FileUtils;
import org.fhaes.fhfilereader.AbstractFireHistoryReader;
import org.fhaes.fhfilereader.FHFile;
import org.fhaes.filefilter.PDFFilter;
import org.fhaes.filefilter.PNGFilter;
import org.fhaes.filefilter.SVGFilter;
import org.fhaes.neofhchart.svg.FireChartSVG;
import org.fhaes.preferences.App;
import org.fhaes.preferences.FHAESPreferences.PrefKey;
import org.fhaes.preferences.PrefsEvent;
import org.fhaes.preferences.PrefsListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * NeoFHChart Class. This class creates fire history charts using the AbstractFireHistoryReader in FHUtil. Graphing is done by having
 * FireChartSVG generate an SVG, represented as a org.w3c.dom.Document.
 * 
 * @author Aaron Decker, Michael Ababio, Zachariah Ferree, Matthew Willie, Peter Brewer
 */
public class NeoFHChart extends JPanel implements PrefsListener {
	
	private static final long serialVersionUID = 1L;
	
	// Declare logger
	private static final Logger log = LoggerFactory.getLogger(NeoFHChart.class);
	
	// Declare GUI components
	private JLabel lblLoading;
	protected FireChartSVG currentChart;
	public JSVGCanvasEx svgCanvas = new JSVGCanvasEx();
	
	// Declare local variables
	protected boolean paneActive = false;
	
	@SuppressWarnings("unused")
	private HashMap<String, FireChartSVG> chartMap = new HashMap<>();
	
	/**
	 * Constructor for JPanel containing SVG chart.
	 */
	@SuppressWarnings("unchecked")
	public NeoFHChart() {
		
		App.prefs.addPrefsListener(this);
		this.setLayout(new BorderLayout());
		
		svgCanvas.setAutoFitToCanvas(true);
		JSVGScrollPane scrollPane = new JSVGScrollPane(svgCanvas);
		svgCanvas.getInteractors().add(new AbstractPanInteractor() {
			
			@Override
			public boolean startInteraction(InputEvent ie) {
				
				int mods = ie.getModifiers();
				return ie.getID() == MouseEvent.MOUSE_PRESSED && (mods & InputEvent.BUTTON1_MASK) != 0;
			}
		});
		svgCanvas.addMouseWheelListener(new MouseWheelListener() {
			
			@Override
			public void mouseWheelMoved(MouseWheelEvent evt) {
				
				double rotation = evt.getPreciseWheelRotation();
				ActionMap map = svgCanvas.getActionMap();
				Action action = null;
				if (evt.isControlDown())
				{
					if (rotation > 0.0)
					{
						action = map.get(JSVGCanvasEx.ZOOM_OUT_ACTION);
					}
					else
					{
						action = map.get(JSVGCanvasEx.ZOOM_IN_ACTION);
					}
				}
				else if (evt.isAltDown())
				{
					if (rotation > 0.0)
					{
						action = map.get(JSVGCanvas.SCROLL_LEFT_ACTION);
					}
					else
					{
						action = map.get(JSVGCanvas.SCROLL_RIGHT_ACTION);
					}
				}
				else
				{
					if (rotation > 0.0)
					{
						action = map.get(JSVGCanvas.SCROLL_DOWN_ACTION);
					}
					else
					{
						action = map.get(JSVGCanvas.SCROLL_UP_ACTION);
					}
				}
				
				action.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, null));
			}
		});
		
		add(scrollPane, BorderLayout.CENTER);
		
		lblLoading = new JLabel("Loading...");
		lblLoading.setVisible(false);
		scrollPane.add(lblLoading, BorderLayout.NORTH);
	}
	
	/**
	 * Clear the current chart from the canvas.
	 */
	public void clearChart() {
		
		currentChart = null;
		svgCanvas.setDocument(null);
	}
	
	/**
	 * This method creates the series list dialog if it doesn't exist yet
	 */
	protected void showSeriesPane() {
		
		SeriesListDialog.showDialog(currentChart, svgCanvas);
	}
	
	/**
	 * Create a chart from the specified AbstractFireHistoryReader. Charts are cached so transient settings are maintained e.g. series
	 * order.
	 * 
	 * @param fr
	 */
	public void loadFile(AbstractFireHistoryReader fr) {
		
		if (fr == null)
		{
			// Reader is null so clear chart
			clearChart();
			return;
		}
		
		// if (chartMap.containsKey(fr.getFile().getAbsolutePath()))
		// {
		// Chart has previously been instantiated to retrieve from cache
		// currentChart = chartMap.get(fr.getFile().getAbsolutePath());
		// currentChart.buildElements();
		// }
		// else
		// {
		// New chart so create and cache
		currentChart = new FireChartSVG(fr);
		// chartMap.put(fr.getFile().getAbsolutePath(), currentChart);
		// }
		
		// Add chart to canvas
		svgCanvas.setDocument(currentChart.getSVGDocument());
	}
	
	/**
	 * Export the current chart as a PDF document, PNG image, or SVG file.
	 */
	public void doSingleExport() {
		
		if (currentChart != null)
		{
			String lastVisitedFolder = App.prefs.getPref(PrefKey.CHART_LAST_EXPORT_FOLDER, null);
			final JFileChooser fc = new JFileChooser(lastVisitedFolder);
			
			PDFFilter pdff = new PDFFilter();
			PNGFilter pngf = new PNGFilter();
			SVGFilter svgf = new SVGFilter();
			
			fc.addChoosableFileFilter(pdff);
			fc.addChoosableFileFilter(pngf);
			fc.addChoosableFileFilter(svgf);
			fc.setFileFilter(svgf);
			
			fc.setAcceptAllFileFilterUsed(false);
			fc.setMultiSelectionEnabled(false);
			fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
			fc.setDialogTitle("Export current chart as...");
			
			FHFile currentFile = currentChart.getReader().getFHFile();
			fc.setSelectedFile(new File(currentFile.getFileNameWithoutExtension()));
			
			// In response to a button click:
			int returnVal = fc.showSaveDialog(App.mainFrame);
			if (returnVal == JFileChooser.APPROVE_OPTION)
			{
				File outputFile = fc.getSelectedFile();
				
				if (FileUtils.getExtension(outputFile.getAbsolutePath()) == "")
				{
					log.debug("Output file extension not set by user");
					
					// Make sure to add the file extension if it was not specified
					if (fc.getFileFilter().getDescription().equals(pdff.getDescription()))
					{
						log.debug("Adding pdf extension to output file name");
						outputFile = new File(outputFile.getAbsolutePath() + ".pdf");
					}
					else if (fc.getFileFilter().getDescription().equals(pngf.getDescription()))
					{
						log.debug("Adding png extension to output file name");
						outputFile = new File(outputFile.getAbsolutePath() + ".png");
					}
					else if (fc.getFileFilter().getDescription().equals(svgf.getDescription()))
					{
						log.debug("Adding svg extension to output file name");
						outputFile = new File(outputFile.getAbsolutePath() + ".svg");
					}
				}
				else
				{
					log.debug("Output file extension set my user to '" + FileUtils.getExtension(outputFile.getAbsolutePath()) + "'");
				}
				
				App.prefs.setPref(PrefKey.CHART_LAST_EXPORT_FOLDER, outputFile.getAbsolutePath());
				FileFilter selectedFilter = fc.getFileFilter();
				
				if (outputFile.exists())
				{
					Object[] options = { "Overwrite", "No", "Cancel" };
					int response = JOptionPane.showOptionDialog(App.mainFrame,
							"The file '" + outputFile.getName() + "' already exists.  Are you sure you want to overwrite?", "Confirm",
							JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, // do not use a custom Icon
							options, // the titles of buttons
							options[0]); // default button title
					
					// Go back and redo the file selection in case the user does not want to overwrite the existing file
					if (response != JOptionPane.YES_OPTION)
					{
						doSingleExport();
						return;
					}
				}
				
				if (selectedFilter.equals(pdff))
				{
					new PDFExportOptionsDialog(currentChart, outputFile, false);
				}
				else if (selectedFilter.equals(pngf))
				{
					new PNGExportOptionsDialog(currentChart, outputFile, false);
				}
				else if (selectedFilter.equals(svgf))
				{
					currentChart.saveSVGToDisk(outputFile);
				}
			}
		}
	}
	
	/**
	 * Bulk exports charts for all currently loaded files as PDF documents, PNG images, or SVG files.
	 * 
	 * @param selectedFilter
	 * @param pdff
	 * @param pngf
	 * @param svgf
	 * @param outputFile
	 */
	public void doBulkExport(FileFilter selectedFilter, PDFFilter pdff, PNGFilter pngf, SVGFilter svgf, File outputFile) {
		
		if (selectedFilter.equals(pdff))
		{
			new PDFExportOptionsDialog(currentChart, outputFile, true);
		}
		else if (selectedFilter.equals(pngf))
		{
			new PNGExportOptionsDialog(currentChart, outputFile, true);
		}
		else if (selectedFilter.equals(svgf))
		{
			currentChart.saveSVGToDisk(outputFile);
		}
	}
	
	/**
	 * Handles when a preference is changed on the NeoFHChart.
	 */
	@Override
	public void prefChanged(PrefsEvent e) {
		
		if (currentChart != null)
		{
			if (e.getPref().getValue().toLowerCase().startsWith("chart"))
			{
				// currentChart.positionChartGroupersAndDrawTimeAxis();
				log.debug("Preference change for key " + e.getPref() + " picked up by NeoFHChart");
				
				App.prefs.setSilentMode(false);
				redrawChart();
				App.prefs.setSilentMode(false);
			}
		}
	}
	
	/**
	 * Force redraw of chart
	 */
	public void redrawChart() {
		
		Runnable r = new Runnable() {
			
			@Override
			public void run() {
				
				currentChart.buildElements();
			}
		};
		
		svgCanvas.getUpdateManager().getUpdateRunnableQueue().invokeLater(r);
	}
}
