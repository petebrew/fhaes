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
import java.util.ArrayList;

/**
 * ColorPalette Class. Returns a sequence of colors on each request in an attempt to give a nice palette for drawing.
 * 
 * @author Peter Brewer
 */
public class ColorPalette {
	
	ArrayList<Color> colors = new ArrayList<Color>();
	int currentColor = 0;
	
	/**
	 * Instantiate a ColorPalette with default colors
	 */
	public ColorPalette() {
	
		colors.add(Color.RED);
		colors.add(Color.GREEN);
		colors.add(Color.BLUE);
		colors.add(Color.MAGENTA);
		colors.add(Color.YELLOW);
		colors.add(Color.CYAN);
		colors.add(Color.WHITE);
		colors.add(new Color(255, 127, 0, 255));
		colors.add(new Color(255, 127, 0, 255));
		colors.add(new Color(0, 255, 127, 255));
		colors.add(new Color(0, 255, 127, 255));
		colors.add(new Color(127, 0, 255, 255));
		colors.add(new Color(127, 255, 0, 255));
		colors.add(new Color(0, 127, 255, 255));
		colors.add(new Color(0, 127, 255, 255));
		colors.add(new Color(255, 0, 127, 255));
		colors.add(Color.GRAY);
		colors.add(Color.BLACK);
		colors.add(Color.RED.darker());
		colors.add(Color.GREEN.darker());
		colors.add(Color.BLUE.darker());
		colors.add(Color.MAGENTA.darker());
		colors.add(Color.YELLOW.darker());
		colors.add(Color.CYAN.darker());
		colors.add(new Color(255, 127, 0, 255).darker());
		colors.add(new Color(255, 127, 0, 255).darker());
		colors.add(new Color(0, 255, 127, 255).darker());
		colors.add(new Color(0, 255, 127, 255).darker());
		colors.add(new Color(127, 0, 255, 255).darker());
		colors.add(new Color(127, 255, 0, 255).darker());
		colors.add(new Color(0, 127, 255, 255).darker());
		colors.add(new Color(0, 127, 255, 255).darker());
		colors.add(new Color(255, 0, 127, 255).darker());
	}
	
	/**
	 * Get the next color in the sequence from the palette
	 * 
	 * @return
	 */
	public Color getNextColor() {
	
		Color c = colors.get(currentColor);
		if ((currentColor + 1) < colors.size())
		{
			// Increment color
			currentColor++;
		}
		else
		{
			// Used up all our palette so start again at the beginning
			currentColor = 0;
		}
		
		return c;
	}
}
