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

import java.awt.Cursor;
import java.awt.Font;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;

import org.fhaes.preferences.App;
import org.fhaes.preferences.FHAESPreferences.PrefKey;
import org.fhaes.util.Builder;

import net.miginfocom.swing.MigLayout;

/**
 * QuickLaunchDialog Class.
 */
public class QuickLaunchDialog extends JDialog implements MouseListener {
	
	private static final long serialVersionUID = 1L;
	
	private JLabel lblFileNew;
	private JLabel lblFileNewIcon;
	private JLabel lblFileOpenIcon;
	private JLabel lblFileOpen;
	// private JLabel lblChartIcon;
	// private JLabel lblCreateChart;
	private JLabel lbljSEAIcon;
	private JLabel lbljSEA;
	
	/**
	 * Initializes a new QuickLaunchDialog window. If usePreferences is set to true the window will only be shown if the show-at-startup
	 * preference is set.
	 * 
	 * @param usePreferences
	 */
	public QuickLaunchDialog(boolean usePreferences) {
		
		this.setModal(true);
		this.setIconImage(Builder.getApplicationIcon());
		
		this.getContentPane().setLayout(new MigLayout("", "[20px:n,grow][][290:290.00,left][20px:n,grow]",
				"[20px:n,grow,fill][39.00,top][][fill][][center][20px:n,grow][]"));
				
		JLabel lblNewLabel = new JLabel("What would you like to do?");
		lblNewLabel.setFont(new Font("Dialog", Font.BOLD, 18));
		this.getContentPane().add(lblNewLabel, "cell 1 1 2 1,alignx center");
		
		lblFileNewIcon = new JLabel("");
		lblFileNewIcon.setCursor(new Cursor(Cursor.HAND_CURSOR));
		lblFileNewIcon.addMouseListener(this);
		lblFileNewIcon.setToolTipText("Create a new FHX file");
		lblFileNewIcon.setIcon(Builder.getImageIcon("filenew64.png"));
		this.getContentPane().add(lblFileNewIcon, "cell 1 2");
		
		lblFileNew = new JLabel("Create a new FHX file");
		lblFileNew.setFont(new Font("Dialog", Font.BOLD, 14));
		lblFileNew.setCursor(new Cursor(Cursor.HAND_CURSOR));
		lblFileNew.addMouseListener(this);
		this.getContentPane().add(lblFileNew, "cell 2 2");
		
		lblFileOpenIcon = new JLabel("");
		lblFileOpenIcon.setIcon(Builder.getImageIcon("fileopen64.png"));
		lblFileOpenIcon.setCursor(new Cursor(Cursor.HAND_CURSOR));
		lblFileOpenIcon.addMouseListener(this);
		lblFileOpenIcon.setToolTipText("Load existing FHX file(s)");
		this.getContentPane().add(lblFileOpenIcon, "cell 1 3");
		
		lblFileOpen = new JLabel("Load existing FHX file(s)");
		lblFileOpen.setFont(new Font("Dialog", Font.BOLD, 14));
		lblFileOpen.setCursor(new Cursor(Cursor.HAND_CURSOR));
		lblFileOpen.addMouseListener(this);
		this.getContentPane().add(lblFileOpen, "cell 2 3");
		
		/*
		 * lblChartIcon = new JLabel(""); lblChartIcon.setIcon(Builder.getImageIcon("chart64.png")); lblChartIcon.setCursor(new
		 * Cursor(Cursor.HAND_CURSOR)); lblChartIcon.addMouseListener(this); lblChartIcon.setToolTipText("Create a fire history chart");
		 * getContentPane().add(lblChartIcon, "cell 1 4"); lblCreateChart = new JLabel("Create a fire history chart");
		 * lblCreateChart.setFont(new Font("Dialog", Font.BOLD, 14)); lblCreateChart.setCursor(new Cursor(Cursor.HAND_CURSOR));
		 * lblCreateChart.addMouseListener(this); getContentPane().add(lblCreateChart, "cell 2 4");
		 */
		
		lbljSEAIcon = new JLabel("");
		lbljSEAIcon.setIcon(Builder.getImageIcon("jsea64.png"));
		lbljSEAIcon.setCursor(new Cursor(Cursor.HAND_CURSOR));
		lbljSEAIcon.addMouseListener(this);
		lbljSEAIcon.setToolTipText("Run Superposed Epoch Analysis (jSEA)");
		this.getContentPane().add(lbljSEAIcon, "cell 1 5");
		
		lbljSEA = new JLabel("Run Superposed Epoch Analysis");
		lbljSEA.setFont(new Font("Dialog", Font.BOLD, 14));
		lbljSEA.setCursor(new Cursor(Cursor.HAND_CURSOR));
		lbljSEA.addMouseListener(this);
		this.getContentPane().add(lbljSEA, "cell 2 5");
		
		JCheckBox cbxShowQuickLaunch = new JCheckBox(MainWindow.getInstance().actionPrefChangeShowQuickLaunch);
		this.getContentPane().add(cbxShowQuickLaunch, "cell 0 7 4 1,alignx right");
		
		if (usePreferences && !App.prefs.getBooleanPref(PrefKey.SHOW_QUICK_LAUNCH_AT_STARTUP, true))
		{
			this.dispose();
		}
		else
		{
			this.pack();
			this.setLocationRelativeTo(MainWindow.getInstance().getReportPanel());
			this.setVisible(true);
		}
	}
	
	/**
	 * Handles when mouse events occur on the QuickLaunchDialog.
	 */
	@Override
	public void mouseClicked(MouseEvent evt) {
		
		Object src = evt.getSource();
		
		if (src.equals(this.lblFileNew) || src.equals(this.lblFileNewIcon))
		{
			// New File clicked
			this.setVisible(false);
			MainWindow.getInstance().openFileRecorder(null);
		}
		else if (src.equals(this.lblFileOpen) || src.equals(this.lblFileOpenIcon))
		{
			// Open file clicked
			this.setVisible(false);
			MainWindow.getInstance().openFiles();
		}
		/*
		 * else if(src.equals(this.lblCreateChart) || src.equals(this.lblChartIcon)) { // Create chart clicked String lastVisitedFolder =
		 * App.prefs.getPref(PrefKey.PREF_LAST_READ_FOLDER, null); JFileChooser fc; File file = null;
		 * 
		 * if (lastVisitedFolder != null) { fc = new JFileChooser(lastVisitedFolder); } else { fc = new JFileChooser(); }
		 * 
		 * fc.setMultiSelectionEnabled(false); fc.setDialogTitle("Open file"); fc.setFileFilter(new FHXFileFilter());
		 * 
		 * 
		 * int returnVal = fc.showOpenDialog(parent.frame); if (returnVal == JFileChooser.APPROVE_OPTION){
		 * 
		 * file = fc.getSelectedFile();
		 * 
		 * // Set lastPathVisited App.prefs.setPref(PrefKey.PREF_LAST_READ_FOLDER, file.getParent());
		 * 
		 * setVisible(false); //PlotWindow plotWindow = new PlotWindow(parent.frame, file); //plotWindow.setVisible(true); } }
		 */
		else if (src.equals(this.lbljSEAIcon) || src.equals(this.lbljSEA))
		{
			// Run jSEA clicked
			this.setVisible(false);
			MainWindow.getInstance().actionJSEAConfig.perform(this);
		}
		
	}
	
	/**
	 * Method overwritten from MouseListener.
	 */
	@Override
	public void mouseEntered(MouseEvent arg0) {
	
	}
	
	/**
	 * Method overwritten from MouseListener.
	 */
	@Override
	public void mouseExited(MouseEvent arg0) {
	
	}
	
	/**
	 * Method overwritten from MouseListener.
	 */
	@Override
	public void mousePressed(MouseEvent arg0) {
	
	}
	
	/**
	 * Method overwritten from MouseListener.
	 */
	@Override
	public void mouseReleased(MouseEvent arg0) {
	
	}
}
