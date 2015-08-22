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
import org.fhaes.filefilter.CSVFileFilter;
import org.fhaes.filefilter.FHXFileFilter;
import org.fhaes.filefilter.PDFFilter;
import org.fhaes.filefilter.PNGFilter;
import org.fhaes.filefilter.SVGFilter;
import org.fhaes.filefilter.TXTFileFilter;
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
	public JSVGCanvasEx svgCanvas = new JSVGCanvasEx();
	protected FireChartSVG currentChart;
	
	// Declare local variables
	protected boolean paneActive = false;
	
	@SuppressWarnings("unused")
	private HashMap<String, FireChartSVG> chartMap = new HashMap<String, FireChartSVG>();
	
	/**
	 * Constructor for JPanel containing SVG chart.
	 */
	@SuppressWarnings("unchecked")
	public NeoFHChart() {
	
		App.prefs.addPrefsListener(this);
		setLayout(new BorderLayout());
		
		JSVGScrollPane scrollPane = new JSVGScrollPane(svgCanvas);
		svgCanvas.getInteractors().add(this.panInteractor);
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
	 * Custom interactor for panning with mouse drags.
	 */
	protected AbstractPanInteractor panInteractor = new AbstractPanInteractor() {
		
		@Override
		public boolean startInteraction(InputEvent ie) {
		
			int mods = ie.getModifiers();
			return ie.getID() == MouseEvent.MOUSE_PRESSED && (mods & InputEvent.BUTTON1_MASK) != 0;
		}
	};
	
	/**
	 * Clear the current chart from the canvas.
	 */
	public void clearChart() {
	
		currentChart = null;
		svgCanvas.setDocument(null);
	}
	
	/**
	 * Create a chart from the specified AbstractFireHistoryReader. Charts are cached so transient settings are maintains e.g. series order.
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
	 * This method creates the series list dialog if it doesn't exist yet
	 */
	protected void showSeriesPane() {
	
		SeriesListDialog.showDialog(currentChart, svgCanvas);
	}
	
	@Override
	public void prefChanged(PrefsEvent e) {
	
		if (currentChart == null)
			return;
		
		if (e.getPref().getValue().toLowerCase().startsWith("chart"))
		{
			// currentChart.positionChartGroupersAndDrawTimeAxis();
			log.debug("Preference change for key " + e.getPref() + " picked up by NeoFHChart");
			
			App.prefs.setSilentMode(false);
			redrawChart();
			App.prefs.setSilentMode(false);
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
	
	/**
	 * Export the chart to SVG
	 */
	public void doExport() {
	
		doExport("SVG");
	}
	
	/**
	 * Export the chart to one of the following formats: SVG, PDF or PNG.
	 * 
	 * @param format
	 */
	public void doExport(String format) {
	
		if (currentChart == null)
			return;
		
		File outputFile;
		FileFilter selectedFilter;
		String lastVisitedFolder = App.prefs.getPref(PrefKey.PREF_LAST_EXPORT_FOLDER, null);
		
		// Create a file chooser
		final JFileChooser fc = new JFileChooser(lastVisitedFolder);
		
		SVGFilter svgf = new SVGFilter();
		PNGFilter pngf = new PNGFilter();
		PDFFilter pdff = new PDFFilter();
		
		if (format == null)
		{
			fc.addChoosableFileFilter(pngf);
			fc.addChoosableFileFilter(pdff);
			fc.addChoosableFileFilter(svgf);
			fc.setFileFilter(svgf);
		}
		else if (format.equals("SVG"))
		{
			fc.addChoosableFileFilter(svgf);
			fc.setFileFilter(svgf);
		}
		else if (format.equals("PNG"))
		{
			fc.addChoosableFileFilter(pngf);
			fc.setFileFilter(pngf);
		}
		else if (format.equals("PDF"))
		{
			fc.addChoosableFileFilter(pdff);
			fc.setFileFilter(pdff);
		}
		
		fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
		fc.setAcceptAllFileFilterUsed(false);
		fc.setMultiSelectionEnabled(false);
		fc.setDialogTitle("Save as...");
		
		// In response to a button click:
		int returnVal = fc.showOpenDialog(App.mainFrame);
		
		if (returnVal == JFileChooser.APPROVE_OPTION)
		{
			outputFile = fc.getSelectedFile();
			
			if (FileUtils.getExtension(outputFile.getAbsolutePath()) == "")
			{
				log.debug("Output file extension not set by user");
				
				if (fc.getFileFilter().getDescription().equals(new FHXFileFilter().getDescription()))
				{
					log.debug("Adding fhx extension to output file name");
					outputFile = new File(outputFile.getAbsolutePath() + ".fhx");
				}
				else if (fc.getFileFilter().getDescription().equals(new TXTFileFilter().getDescription()))
				{
					log.debug("Adding txt extension to output file name");
					outputFile = new File(outputFile.getAbsolutePath() + ".txt");
				}
				else if (fc.getFileFilter().getDescription().equals(new CSVFileFilter().getDescription()))
				{
					log.debug("Adding csv extension to output file name");
					outputFile = new File(outputFile.getAbsolutePath() + ".csv");
				}
				else if (fc.getFileFilter().getDescription().equals(new PDFFilter().getDescription()))
				{
					log.debug("Adding pdf extension to output file name");
					outputFile = new File(outputFile.getAbsolutePath() + ".pdf");
				}
				else if (fc.getFileFilter().getDescription().equals(new PNGFilter().getDescription()))
				{
					log.debug("Adding png extension to output file name");
					outputFile = new File(outputFile.getAbsolutePath() + ".png");
				}
				else if (fc.getFileFilter().getDescription().equals(new SVGFilter().getDescription()))
				{
					log.debug("Adding svg extension to output file name");
					outputFile = new File(outputFile.getAbsolutePath() + ".svg");
				}
			}
			else
			{
				log.debug("Output file extension set my user to '" + FileUtils.getExtension(outputFile.getAbsolutePath()) + "'");
			}
			
			App.prefs.setPref(PrefKey.PREF_LAST_EXPORT_FOLDER, outputFile.getAbsolutePath());
			
			selectedFilter = fc.getFileFilter();
		}
		else
		{
			return;
		}
		
		if (outputFile.exists())
		{
			Object[] options = { "Overwrite", "No", "Cancel" };
			int response = JOptionPane.showOptionDialog(App.mainFrame, "The file '" + outputFile.getName()
					+ "' already exists.  Are you sure you want to overwrite?", "Confirm", JOptionPane.YES_NO_CANCEL_OPTION,
					JOptionPane.QUESTION_MESSAGE, null, // do not use a custom Icon
					options, // the titles of buttons
					options[0]); // default button title
			
			if (response != JOptionPane.YES_OPTION)
			{
				doExport();
				return;
			}
		}
		
		if (selectedFilter.equals(svgf))
		{
			currentChart.saveSVGToDisk(outputFile);
			return;
		}
		else if (selectedFilter.equals(pngf))
		{
			PNGExportOptionsDialog exp = new PNGExportOptionsDialog(currentChart, outputFile);
			exp.setVisible(true);
		}
		else if (selectedFilter.equals(pdff))
		{
			PDFExportOptionsDialog exp = new PDFExportOptionsDialog(currentChart, outputFile);
			exp.setVisible(true);
		}
	}
}
