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

import java.awt.Color;

import org.fhaes.model.FHSeries;

/**
 * FHSeriesSVG Class. This class extends FHSeries to keep track of series visibility and metadata not currently supported by FHSeries.
 * 
 * @author Aaron Decker, Michael Ababio, Zachariah Ferree, Matthew Willie, Peter Brewer
 */
public class FHSeriesSVG extends FHSeries {
	
	// Declare local variables
	private boolean visible = true;
	private String taxon = "";
	private Color lineColor = Color.BLACK;
	private Color labelColor = Color.BLACK;
	final int sequenceInFile;
	
	/**
	 * Create a series from an FHSeries.
	 * 
	 * @param series
	 * @throws Exception
	 */
	public FHSeriesSVG(FHSeries series, int sequenceInFile) throws Exception {
	
		super(series.getTitle(), series.getFirstYear(), series.hasPith(), series.hasBark(), series.getRecordingYears(), series
				.getEventYears(), series.getInjuryYears(), series.getCategoryEntries());
		this.sequenceInFile = sequenceInFile;
	}
	
	/**
	 * Create a series from an FHSeries plus the taxon string.
	 * 
	 * @param series
	 * @param taxon
	 * @throws Exception
	 */
	public FHSeriesSVG(FHSeries series, String taxon, int sequenceInFile) throws Exception {
	
		super(series.getTitle(), series.getFirstYear(), series.hasPith(), series.hasBark(), series.getRecordingYears(), series
				.getEventYears(), series.getInjuryYears(), series.getCategoryEntries());
		
		this.taxon = taxon;
		this.sequenceInFile = sequenceInFile;
	}
	
	/**
	 * Get the index position for this series in the original file
	 * 
	 * @return
	 */
	public int getSequenceInFile() {
	
		return sequenceInFile;
	}
	
	/**
	 * Get the current visibility for this series.
	 * 
	 * @return
	 */
	public boolean isVisible() {
	
		return visible;
	}
	
	/**
	 * Toggle whether this series should be visible in charts.
	 */
	public void toggleVisibility() {
	
		visible = !visible;
	}
	
	/**
	 * Get the color used for labels for this series.
	 * 
	 * @return
	 */
	public Color getLabelColor() {
	
		return labelColor;
	}
	
	/**
	 * Set the label color for this series.
	 * 
	 * @param labelColor
	 */
	public void setLabelColor(Color labelColor) {
	
		this.labelColor = labelColor;
	}
	
	/**
	 * Get the line color for this series.
	 * 
	 * @return
	 */
	public Color getLineColor() {
	
		return lineColor;
	}
	
	/**
	 * Set the color the lines for this series should be drawn using.
	 * 
	 * @param color
	 */
	public void setLineColor(Color color) {
	
		this.lineColor = color;
	}
	
	/**
	 * Get the taxon for this series.
	 * 
	 * @return
	 */
	public String getTaxon() {
	
		return taxon;
	}
	
	/**
	 * Set the taxon for this series.
	 * 
	 * TODO Replace once we have TRiDaS support
	 * 
	 * @param taxon
	 */
	public void setTaxon(String taxon) {
	
		this.taxon = taxon;
	}
}
