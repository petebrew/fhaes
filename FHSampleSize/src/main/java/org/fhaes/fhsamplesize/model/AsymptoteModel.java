/**************************************************************************************************
 * Fire History Analysis and Exploration System (FHAES), Copyright (C) 2015
 * 
 * Contributors: Peter Brewer
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
package org.fhaes.fhsamplesize.model;

import org.fhaes.segmentation.SegmentModel;

/**
 * AsymptoteModel Class.
 * 
 * @author Peter Brewer
 */
public class AsymptoteModel {
	
	private String type;
	private SegmentModel segment;
	private Double asymptote;
	private Double r2adj;
	private Double dfds;
	
	public String getType() {
		
		return type;
	}
	
	public void setType(String type) {
		
		this.type = type;
	}
	
	public SegmentModel getSegment() {
		
		return segment;
	}
	
	public void setSegment(SegmentModel segment) {
		
		this.segment = segment;
	}
	
	public Double getAsymptote() {
		
		return asymptote;
	}
	
	public void setAsymptote(Double asymptote) {
		
		this.asymptote = asymptote;
	}
	
	public Double getR2adj() {
		
		return r2adj;
	}
	
	public void setR2adj(Double r2adj) {
		
		this.r2adj = r2adj;
	}
	
	public Double getDfds() {
		
		return dfds;
	}
	
	public void setDfds(Double dfds) {
		
		this.dfds = dfds;
	}
}
