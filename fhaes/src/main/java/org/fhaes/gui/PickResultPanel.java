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
package org.fhaes.gui;

import javax.swing.JLabel;
import javax.swing.JPanel;

import org.fhaes.util.Builder;

import net.miginfocom.swing.MigLayout;

/**
 * PickResultPanel Class.
 */
public class PickResultPanel extends JPanel {
	
	private static final long serialVersionUID = 1L;
	
	/**
	 * Create the panel.
	 */
	public PickResultPanel() {
		
		setLayout(new MigLayout("", "[222px,grow,fill]", "[grow,fill][center][grow]"));
		
		JPanel panel = new JPanel();
		add(panel, "cell 0 1,alignx right,aligny top");
		
		JLabel lblSelectAnAnalysis = new JLabel("<html>Select an analysis result<br/>from the list on the right");
		panel.add(lblSelectAnAnalysis);
		
		JLabel lblNewLabel = new JLabel("");
		panel.add(lblNewLabel);
		lblNewLabel.setIcon(Builder.getImageIcon("rightarrow.png"));
		
	}
}
