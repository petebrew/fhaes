package org.fhaes.util;

import java.awt.Color;
import java.util.ArrayList;

/**
 * ColorPalette Class. Returns a sequence of colors on each request in an attempt to give a nice palette for drawing.
 * 
 * @author pwb48
 */
public class ColorPalette {

	ArrayList<Color> colors = new ArrayList<Color>();
	int currentColor = 0;

	/**
	 * TODO
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
	 * TODO
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
