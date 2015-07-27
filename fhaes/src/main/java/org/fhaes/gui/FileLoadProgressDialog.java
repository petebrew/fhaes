/**************************************************************************************************
 * Fire History Analysis and Exploration System (FHAES), Copyright (C) 2015
 * 
 * Contributors: Elena Velasquez and Peter Brewer
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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingWorker;
import javax.swing.border.LineBorder;

import org.fhaes.model.FHFile;
import org.fhaes.preferences.App;
import org.fhaes.preferences.FHAESPreferences.PrefKey;

import net.miginfocom.swing.MigLayout;

/**
 * FileLoadProgressDialog Class. This is a dialog that shows progress for loading multiple files. The dialog runs in a background thread to
 * ensure the application stays responsive.
 * 
 * @author Peter Brewer
 */
public class FileLoadProgressDialog extends JDialog implements PropertyChangeListener {
	
	private static final long serialVersionUID = 1L;
	
	private File[] files;
	private ArrayList<FHFile> fileList = new ArrayList<FHFile>();
	private JProgressBar progressBar;
	
	/**
	 * TODO
	 * 
	 * @param parent
	 * @param files
	 */
	public FileLoadProgressDialog(Component parent, File[] files) {
		
		// getContentPane().setBackground(Color.WHITE);
		
		if (files == null || files.length == 0)
			return;
			
		this.files = files;
		
		final Task task = new Task();
		getContentPane().setLayout(new BorderLayout(0, 0));
		
		JPanel panel = new JPanel();
		// panel.setBackground(Color.WHITE);
		panel.setBorder(new LineBorder(new Color(0, 0, 0)));
		getContentPane().add(panel);
		panel.setLayout(new MigLayout("", "[66.00,grow][]", "[][]"));
		
		progressBar = new JProgressBar();
		panel.add(progressBar, "cell 0 0 2 1,growx");
		progressBar.setStringPainted(true);
		
		progressBar.setVisible(true);
		progressBar.setMaximum(100);
		progressBar.setValue(0);
		
		JLabel lblInfo = new JLabel("Loading files.  Please wait...");
		panel.add(lblInfo, "cell 0 1");
		lblInfo.setFont(new Font("Dialog", Font.PLAIN, 10));
		
		JButton btnCancel = new JButton("X");
		btnCancel.setFocusable(false);
		panel.add(btnCancel, "cell 1 1");
		btnCancel.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				
				task.cancel(true);
			}
		});
		
		btnCancel.setFont(new Font("Dialog", Font.PLAIN, 8));
		
		this.setUndecorated(true);
		this.setModal(true);
		this.pack();
		
		task.addPropertyChangeListener(this);
		task.execute();
		
		this.setLocationRelativeTo(parent);
		this.setVisible(true);
	}
	
	/**
	 * TODO
	 * 
	 * @return
	 */
	public ArrayList<FHFile> getFileList() {
		
		return fileList;
	}
	
	class Task extends SwingWorker<Void, Void> {
		
		/*
		 * Main task. Executed in background thread.
		 */
		@Override
		public Void doInBackground() {
			
			// Initialize progress property.
			setProgress(0);
			
			setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
			
			for (int i = 0; i < files.length; i++)
			{
				
				FHFile fhf = new FHFile(files[i]);
				fileList.add(fhf);
				Double prg = (double) (((double) i / (double) files.length) * 100);
				
				setProgress(prg.intValue());
			}
			// Set lastPathVisited
			App.prefs.setPref(PrefKey.PREF_LAST_READ_FOLDER, files[0].getParent());
			
			return null;
		}
		
		/*
		 * Executed in event dispatching thread
		 */
		@Override
		public void done() {
			
			setCursor(null); // turn off the wait cursor
			finish();
		}
	}
	
	/**
	 * TODO
	 */
	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		
		if ("progress" == evt.getPropertyName())
		{
			int progress = (Integer) evt.getNewValue();
			progressBar.setValue(progress);
		}
	}
	
	/**
	 * TODO
	 */
	private void finish() {
		
		this.setVisible(false);
	}
}
