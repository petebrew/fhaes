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
package org.fhaes.fhrecorder.util;

import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.util.Iterator;
import java.util.List;

import org.jfree.chart.axis.AxisState;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPosition;
import org.jfree.chart.axis.CategoryLabelWidthType;
import org.jfree.chart.axis.CategoryTick;
import org.jfree.chart.axis.Tick;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.text.TextBlock;
import org.jfree.ui.RectangleEdge;

/**
 * NumericCategoryAxis Class. This is a "Kludge class" to override a CatagoryAxis labels.
 * 
 * We are using categories for numeric data. Unfortunately the labels on a standard CategoryAxis are all painted and so we end up with
 * collisions when zoomed out. This class allows us to specify a period between categories which aren't painted using the
 * setLabelEveryXCategories() method. This class is only useful if the categories can be cast to Integers.
 * 
 * @author Peter Brewer
 */
public class NumericCategoryAxis extends CategoryAxis {
	
	private static final long serialVersionUID = 1L;
	private int labelEveryXCategories = 5;
	
	/**
	 * TODO
	 * 
	 * @param i
	 */
	public void setLabelEveryXCategories(int i) {
		
		if (i < 1 || i > 100)
			return;
			
		labelEveryXCategories = i;
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public List refreshTicks(Graphics2D g2, AxisState state, Rectangle2D dataArea, RectangleEdge edge) {
		
		List ticks = new java.util.ArrayList();
		
		// sanity check for data area...
		if (dataArea.getHeight() <= 0.0 || dataArea.getWidth() < 0.0)
		{
			return ticks;
		}
		
		CategoryPlot plot = (CategoryPlot) getPlot();
		List categories = plot.getCategoriesForAxis(this);
		double max = 0.0;
		
		if (categories != null)
		{
			CategoryLabelPosition position = this.getCategoryLabelPositions().getLabelPosition(edge);
			float r = this.getMaximumCategoryLabelWidthRatio();
			if (r <= 0.0)
			{
				r = position.getWidthRatio();
			}
			
			float l = 0.0f;
			if (position.getWidthType() == CategoryLabelWidthType.CATEGORY)
			{
				l = (float) calculateCategorySize(categories.size(), dataArea, edge);
			}
			else
			{
				if (RectangleEdge.isLeftOrRight(edge))
				{
					l = (float) dataArea.getWidth();
				}
				else
				{
					l = (float) dataArea.getHeight();
				}
			}
			int categoryIndex = 0;
			Iterator iterator = categories.iterator();
			while (iterator.hasNext())
			{
				Comparable category = (Comparable) iterator.next();
				
				try
				{
					Integer intcategory = Integer.valueOf(category.toString());
					
					int modulus = intcategory % labelEveryXCategories;
					
					if (modulus != 0)
						category = " ";
						
				}
				catch (NumberFormatException e)
				{
				
				}
				
				TextBlock label = createLabel(category, l * r, edge, g2);
				if (edge == RectangleEdge.TOP || edge == RectangleEdge.BOTTOM)
				{
					max = Math.max(max, calculateTextBlockHeight(label, position, g2));
				}
				else if (edge == RectangleEdge.LEFT || edge == RectangleEdge.RIGHT)
				{
					max = Math.max(max, calculateTextBlockWidth(label, position, g2));
				}
				Tick tick = new CategoryTick(category, label, position.getLabelAnchor(), position.getRotationAnchor(), position.getAngle());
				ticks.add(tick);
				categoryIndex = categoryIndex + 1;
			}
		}
		state.setMax(max);
		return ticks;
	}
}
