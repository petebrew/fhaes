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
package org.fhaes.components;

import java.awt.Component;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.net.URL;

import javax.swing.JLabel;

import org.fhaes.util.Builder;
import org.fhaes.util.Platform;

/**
 * WarningTipButton Class. A small warning icon which when clicked shows either a small popup box of text, or links to a webpage.
 * 
 * @author Peter Brewer
 * @see HelpTipButton
 */
public class WarningTipButton extends JLabel {
	
	private static final long serialVersionUID = 1L;
	String tiptext;
	URL url;
	
	/**
	 * Constructor for the WarningTipButton. String parameter will be interpreted as a URL if possible, in which case clicking the button
	 * will open up a webpage. Otherwise, the string will be displayed as a HelpTip.
	 * 
	 * @param helpinfo - Text string, or URL
	 */
	public WarningTipButton(String warnInfo) {
		
		setText("");
		if (warnInfo == null || warnInfo == "")
		{
			setIcon(null);
			return;
		}
		else
		{
			this.setIcon(Builder.getImageIcon("warning.png"));
			
			try
			{
				url = new URL(warnInfo);
			}
			catch (Exception e)
			{
				url = null;
				tiptext = warnInfo;
			}
			
			final Component glue = this;
			addMouseListener(new MouseListener() {
				
				@Override
				public void mouseClicked(MouseEvent evt) {
					
					if (url != null)
					{
						// Launch webpage
						Platform.browseWebpage(tiptext);
					}
					else
					{
						new HelpTip(tiptext, glue);
					}
				}
				
				@Override
				public void mouseEntered(MouseEvent arg0) {
				
				}
				
				@Override
				public void mouseExited(MouseEvent arg0) {
				
				}
				
				@Override
				public void mousePressed(MouseEvent arg0) {
				
				}
				
				@Override
				public void mouseReleased(MouseEvent arg0) {
				
				}
			});
		}
	}
	
}
