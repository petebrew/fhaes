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
package org.fhaes.neofhchart;

import java.awt.Component;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

import com.itextpdf.text.PageSize;
import com.itextpdf.text.Rectangle;

/**
 * PageSizeRenderer Class. List renderer for displaying iText PageSizes nicely.
 * 
 * @author Peter Brewer
 */
public class PageSizeRenderer extends JLabel implements ListCellRenderer<Object> {
	
	private static final long serialVersionUID = 1L;
	
	/**
	 * TODO
	 */
	public PageSizeRenderer() {
		
		setOpaque(true);
		setHorizontalAlignment(LEFT);
		setVerticalAlignment(CENTER);
	}
	
	/**
	 * TODO
	 */
	@SuppressWarnings("rawtypes")
	@Override
	public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
		
		this.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
		
		if (value instanceof Rectangle)
		{
			if (value.equals(PageSize.A5))
			{
				setText("A5");
			}
			else if (value.equals(PageSize.A4))
			{
				setText("A4");
			}
			else if (value.equals(PageSize.A3))
			{
				setText("A3");
			}
			else if (value.equals(PageSize.A2))
			{
				setText("A2");
			}
			else if (value.equals(PageSize.A1))
			{
				setText("A1");
			}
			else if (value.equals(PageSize.A0))
			{
				setText("A0");
			}
			else if (value.equals(PageSize.LETTER))
			{
				setText("US Letter");
			}
			else if (value.equals(PageSize.LEGAL))
			{
				setText("US Legal");
			}
			else if (value.equals(PageSize.EXECUTIVE))
			{
				setText("US Executive");
			}
		}
		else
		{
			setText(value.toString());
		}
		
		if (isSelected)
		{
			setBackground(list.getSelectionBackground());
			setForeground(list.getSelectionForeground());
		}
		else
		{
			setBackground(list.getBackground());
			setForeground(list.getForeground());
		}
		
		return this;
	}
}
