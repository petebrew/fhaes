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
 * SeriesSVG Class. This class extends FHSeries to keep track of series visibility as well as metadata not currently supported by FHSeries.
 * 
 * @author Aaron Decker, Michael Ababio, Zachariah Ferree, Matthew Willie, Peter Brewer
 */
public class SeriesSVG extends FHSeries {
	
	boolean isVisible = true;
	private String taxon = "";
	private Color lineColor = Color.BLACK;
	private Color labelColor = Color.BLACK;
	
	/**
	 * Create a series from an FHSeries plus the taxon string.
	 * 
	 * @param series
	 * @param taxon
	 * @throws Exception
	 */
	public SeriesSVG(FHSeries series, String taxon) throws Exception {
		
		super(series.getTitle(), series.getFirstYear(), series.hasPith(), series.hasBark(), series.getRecordingYears(),
				series.getEventYears(), series.getInjuryYears());
		this.taxon = taxon;
	}
	
	/**
	 * Create a series from an FHSeries.
	 * 
	 * @param series
	 * @throws Exception
	 */
	public SeriesSVG(FHSeries series) throws Exception {
		
		super(series.getTitle(), series.getFirstYear(), series.hasPith(), series.hasBark(), series.getRecordingYears(),
				series.getEventYears(), series.getInjuryYears());
	}
	
	/**
	 * Toggle whether this series should be visible in charts.
	 */
	public void toggleVisibility() {
		
		isVisible = !isVisible;
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
	 * Get the line color for this series.
	 * 
	 * @return
	 */
	public Color getLineColor() {
		
		return lineColor;
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
}
