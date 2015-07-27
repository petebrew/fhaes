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
package org.fhaes.util;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;

import org.openstreetmap.gui.jmapviewer.MapMarkerDot;

/**
 * FHAESMapMarker Class.
 */
public class FHAESMapMarker extends MapMarkerDot {
	
	double lat;
	double lon;
	Color color;
	
	/**
	 * TODO
	 * 
	 * @param color
	 * @param lat
	 * @param lon
	 */
	public FHAESMapMarker(Color color, double lat, double lon) {
		
		super(color, lat, lon);
		this.color = color;
		this.lat = lat;
		this.lon = lon;
	}
	
	@Override
	public double getLat() {
		
		return lat;
	}
	
	@Override
	public double getLon() {
		
		return lon;
	}
	
	public void paint(Graphics g, Point position) {
		
		int size_h = 5;
		int size = size_h * 2;
		g.setColor(color);
		g.fillOval(position.x - size_h, position.y - size_h, size, size);
		g.setColor(Color.BLACK);
		g.drawOval(position.x - size_h, position.y - size_h, size, size);
	}
	
	@Override
	public String toString() {
		
		return "MapMarker at " + lat + " " + lon;
	}
	
	@Override
	public Color getBackColor() {
		
		return color;
	}
	
	@Override
	public Color getColor() {
		
		return Color.BLACK;
	}
}
