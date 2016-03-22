/**************************************************************************************************
 * Fire History Analysis and Exploration System (FHAES), Copyright (C) 2015
 * 
 * Contributors: Elena Velasquez, Joshua Brogan, and Peter Brewer
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
package org.fhaes.jsea;

import java.awt.Cursor;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingWorker;
import javax.swing.UIManager;

import net.miginfocom.swing.MigLayout;

import org.fhaes.preferences.App;
import org.fhaes.preferences.FHAESPreferences.PrefKey;

/**
 * JSEAProgressDialog Class. This is a dialog that shows progress for loading multiple files. The dialog runs in a background thread to
 * ensure the application stays responsive.
 * 
 * @author Peter Brewer
 */
public class JSEAProgressDialog extends JDialog implements PropertyChangeListener {
	
	private static final long serialVersionUID = 1L;
	
	private JProgressBar progressBar;
	private JSEAFrame jseaframe;
	
	/**
	 * TODO
	 * 
	 * @param jseaframe
	 */
	public JSEAProgressDialog(final JSEAFrame jseaframe) {
	
		this.jseaframe = jseaframe;
		
		final Task task = new Task();
		
		getContentPane().setLayout(new MigLayout("", "[300px,grow,fill]", "[31.00,grow]"));
		
		JPanel panel = new JPanel();
		panel.setBorder(UIManager.getBorder("TitledBorder.border"));
		getContentPane().add(panel, "cell 0 0,grow");
		panel.setLayout(new MigLayout("", "[grow,fill][]", "[][][]"));
		
		progressBar = new JProgressBar();
		panel.add(progressBar, "cell 0 0 2 1");
		progressBar.setVisible(true);
		progressBar.setIndeterminate(true);
		
		JLabel lblInfo = new JLabel("Running analysis - please wait...");
		panel.add(lblInfo, "flowx,cell 0 1");
		lblInfo.setFont(new Font("Dialog", Font.PLAIN, 10));
		
		JButton btnCancel = new JButton("Cancel");
		btnCancel.setFont(new Font("Dialog", Font.BOLD, 10));
		panel.add(btnCancel, "cell 1 1");
		btnCancel.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
			
				task.done();
				jseaframe.jsea = null;
			}
		});
		
		this.setUndecorated(true);
		this.setModal(true);
		this.pack();
		
		task.addPropertyChangeListener(this);
		task.execute();
		
		this.setLocationRelativeTo(jseaframe);
		this.setVisible(true);
	}
	
	class Task extends SwingWorker<Void, Void> {
		
		/**
		 * Main task. Executed in background thread.
		 */
		@Override
		public Void doInBackground() {
		
			jseaframe.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
			
			jseaframe.jsea = new JSEAStatsFunctions(App.prefs.getPref(PrefKey.JSEA_CHART_TITLE, "Chart Title"), App.prefs.getPref(
					PrefKey.JSEA_YAXIS_LABEL, "Y Axis label"), App.prefs.getIntPref(PrefKey.JSEA_SEED_NUMBER, 30188), App.prefs.getIntPref(
					PrefKey.JSEA_LAGS_PRIOR_TO_EVENT, 6), App.prefs.getIntPref(PrefKey.JSEA_LAGS_AFTER_EVENT, 4), App.prefs.getIntPref(
					PrefKey.JSEA_SIMULATION_COUNT, 1000), App.prefs.getIntPref(PrefKey.JSEA_FIRST_YEAR, 0), App.prefs.getIntPref(
					PrefKey.JSEA_LAST_YEAR, 2020), App.prefs.getBooleanPref(PrefKey.JSEA_INCLUDE_INCOMPLETE_WINDOW, false), true,
					jseaframe.chronologyYears, jseaframe.chronologyActual, jseaframe.events, false, false,
					jseaframe.segmentationPanel.chkSegmentation.isSelected(), jseaframe.segmentationPanel.table, App.prefs.getPref(
							PrefKey.JSEA_CONTINUOUS_TIME_SERIES_FILE, "blah"), jseaframe.cbxPValue.getSelectedIndex() == 0,
					jseaframe.cbxPValue.getSelectedIndex() == 1, jseaframe.cbxPValue.getSelectedIndex() == 2, App.prefs.getBooleanPref(
							PrefKey.JSEA_Z_SCORE, false));
			
			return null;
		}
		
		/**
		 * Executed in event dispatching thread
		 */
		@Override
		public void done() {
		
			jseaframe.setCursor(null); // turn off the wait cursor
			finish();
		}
	}
	
	@Override
	public void propertyChange(PropertyChangeEvent evt) {
	
		if ("progress" == evt.getPropertyName())
		{
			int progress = (Integer) evt.getNewValue();
			progressBar.setValue(progress);
		}
	}
	
	private void finish() {
	
		this.setVisible(false);
	}
}
