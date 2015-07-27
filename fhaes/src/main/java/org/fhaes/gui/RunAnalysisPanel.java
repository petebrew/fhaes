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

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;

import org.fhaes.util.Builder;

import net.miginfocom.swing.MigLayout;

/**
 * RunAnalysisPanel Class.
 */
public class RunAnalysisPanel extends JPanel {
	
	private static final long serialVersionUID = 1L;
	
	/**
	 * Create the panel.
	 */
	public RunAnalysisPanel() {
		
		setLayout(new MigLayout("", "[222px,grow,fill]", "[grow,fill][center][grow]"));
		
		JPanel panel = new JPanel();
		add(panel, "cell 0 1,alignx right,aligny top");
		
		JButton btnRun = new JButton("Run analysis");
		btnRun.setFont(new Font("Dialog", Font.BOLD, 15));
		btnRun.setIcon(Builder.getImageIcon("run64.png"));
		
		btnRun.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				
				MainWindow.getInstance().rightSplitPanel.actionParamConfig.perform(this);
			}
		});
		
		panel.add(btnRun);
	}
}
