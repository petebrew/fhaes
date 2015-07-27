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
package org.fhaes.FHRecorder.view;

import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import org.fhaes.FHRecorder.controller.FileController;
import org.fhaes.exceptions.CompositeFileException;
import org.fhaes.filefilter.FHXFileFilter;
import org.fhaes.preferences.App;
import org.fhaes.preferences.FHAESPreferences.PrefKey;
import org.fhaes.util.Builder;
import org.fhaes.util.Platform;

/**
 * GUI_TestWindow Class.
 */
public class TestWindow {
	
	private JFrame frame;
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		
		EventQueue.invokeLater(new Runnable() {
			
			@Override
			public void run() {
				
				try
				{
					TestWindow window = new TestWindow();
					window.frame.setVisible(true);
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
			}
		});
	}
	
	/**
	 * Create the application.
	 */
	public TestWindow() {
		
		// Setup our look and feel
		Platform.setLookAndFeel();
		
		App.init();
		
		frame = new JFrame();
		frame.setBounds(100, 100, 450, 300);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		frame.setIconImage(Builder.getApplicationIcon());
		
		JButton newFileButton = new JButton("New Sample");
		newFileButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent evt) {
				
				openFileRecorder(null);
			}
			
		});
		frame.getContentPane().add(newFileButton);
		
		JButton editFileButton = new JButton("Edit File");
		editFileButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent evt) {
				
				File fileToEdit = getFileToEdit();
				if (fileToEdit != null)
					openFileRecorder(fileToEdit);
			}
			
		});
		frame.getContentPane().add(editFileButton);
		frame.setTitle("FHRecorder Test GUI");
	}
	
	/**
	 * Open the FHRecorder frame with the specified file. If f is null then it opens with an empty new file
	 * 
	 * 
	 * @param f
	 */
	private void openFileRecorder(File f) {
		
		FileController.thePrimaryWindow = new FireHistoryRecorder();
		// primaryWindow.setLocationRelativeTo(null);
		// primaryWindow.setIconImage(Builder.getApplicationIcon());
		// primaryWindow.pack();
		
		try
		{
			if (f == null)
				FileController.newFile(); // runs when "New Sample" is clicked
			else
				FileController.importFile(f); // runs when "Edit File" is clicked
		}
		catch (CompositeFileException ex)
		{
			JOptionPane.showMessageDialog(frame, "This file contains composite data and cannot be opened in FHRecorder.");
			return;
		}
		catch (Exception ex)
		{
			JOptionPane.showMessageDialog(frame, "Error opening file in FHRecorder.");
			ex.printStackTrace();
			return;
		}
		FileController.thePrimaryWindow.setVisible(true);
		
		// Runs after FHRecorder has been closed
		File savedFile = FileController.getSavedFile();
		if (FileController.isChangedSinceOpened() && savedFile != null)
		{
			JOptionPane.showMessageDialog(frame, "File was altered and saved by FHRecorder.");
		}
		else
		{
			JOptionPane.showMessageDialog(frame, "FHRecorder was closed without making any changes.");
		}
	}
	
	/**
	 * Show file chooser for user to pick a file to edit
	 * 
	 * @return
	 */
	private File getFileToEdit() {
		
		String lastVisitedFolder = App.prefs.getPref(PrefKey.PREF_LAST_READ_FOLDER, null);
		JFileChooser fc;
		
		if (lastVisitedFolder != null)
			fc = new JFileChooser(lastVisitedFolder);
		else
			fc = new JFileChooser();
			
		fc.setMultiSelectionEnabled(false);
		fc.setDialogTitle("Open file");
		fc.setFileFilter(new FHXFileFilter());
		
		int returnVal = fc.showOpenDialog(frame);
		if (returnVal == JFileChooser.APPROVE_OPTION)
		{
			App.prefs.setPref(PrefKey.PREF_LAST_READ_FOLDER, fc.getSelectedFile().getPath());
			return fc.getSelectedFile();
		}
		return null;
	}
}
