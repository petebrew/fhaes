/**************************************************************************************************
 * Fire History Analysis and Exploration System (FHAES), Copyright (C) 2015
 * 
 * Contributors: Joshua Brogan and Peter Brewer
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
package org.fhaes.enums;

import java.awt.Color;

import javax.swing.Icon;

import org.fhaes.util.Builder;

/**
 * FeedbackMessageType Enum.
 */
public enum FeedbackMessageType {
	
	INFO("INFO", Color.black, Builder.getImageIcon("info.png")),
	
	WARNING("WARNING", Color.red, Builder.getImageIcon("warning.png")),
	
	ERROR("ERROR", Color.red, Builder.getImageIcon("delete.png"));
	
	// Declare local variables
	private String messageString;
	private Color messageColor;
	private Icon messageIcon;
	
	/**
	 * Initialize the message string, color, and icon for the FeedbackMessageType.
	 * 
	 * @param str
	 * @param col
	 * @param ico
	 */
	FeedbackMessageType(String str, Color col, Icon ico) {
		
		messageString = str;
		messageColor = col;
		messageIcon = ico;
	}
	
	/**
	 * Get the message color for this FeedbackMessageType.
	 * 
	 * @return messageColor
	 */
	public Color getColor() {
		
		return messageColor;
	}
	
	/**
	 * Get the message icon for this FeedbackMessageType.
	 * 
	 * @return messageIcon
	 */
	public Icon getIcon() {
		
		return messageIcon;
	}
	
	/**
	 * Get the message string prefix for this FeedbackMessageType.
	 * 
	 * @return messageString with a colon and space
	 */
	public String getPrefix() {
		
		return messageString + ": ";
	}
}
