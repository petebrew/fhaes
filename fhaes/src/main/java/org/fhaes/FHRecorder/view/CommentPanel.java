/**************************************************************************************************
 * Fire History Analysis and Exploration System (FHAES), Copyright (C) 2015
 * 
 * Contributors: Alex Beatty, Clayton Bodendein, Kyle Hartmann, Scott Goble, and Peter Brewer
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

import java.awt.BorderLayout;

import javax.swing.JLabel;

import org.fhaes.FHRecorder.controller.IOController;
import org.fhaes.FHRecorder.model.FHX2_FileOptionalPart;

/**
 * CommentPanel Class. This UI is used to display and modify the additional comments block of the fhx2 file format.
 * 
 * @author Alex Beatty, Clayton Bodendein, Kyle Hartmann, Scott Goble
 */
public class CommentPanel extends javax.swing.JPanel {
	
	private static final long serialVersionUID = 1L;
	
	private javax.swing.JScrollPane CommentsScrollPane;
	private javax.swing.JTextArea CommentsTextArea;
	
	private boolean saveToData;
	
	/**
	 * Creates new a GUI_CommentPanel.
	 */
	public CommentPanel() {
		
		initComponents();
	}
	
	/**
	 * Creates a new GUI_CommentPanel and loads the comments from an existing file.
	 * 
	 * @param inData
	 */
	public CommentPanel(FHX2_FileOptionalPart inData) {
		
		initComponents();
		CommentsTextArea.setLineWrap(true);
		String comments = IOController.getFile().getOptionalPart().getComments();
		CommentsTextArea.setText(comments);
		saveToData = true;
	}
	
	/**
	 * Initializes the GUI components.
	 */
	private void initComponents() {
		
		CommentsScrollPane = new javax.swing.JScrollPane();
		CommentsTextArea = new javax.swing.JTextArea();
		
		addComponentListener(new java.awt.event.ComponentAdapter() {
			
			@Override
			public void componentHidden(java.awt.event.ComponentEvent evt) {
				
				if (saveToData)
					saveComments();
			}
			
			@Override
			public void componentShown(java.awt.event.ComponentEvent evt) {
				
				CommentsTextArea.requestFocusInWindow();
			}
		});
		setLayout(new BorderLayout(0, 0));
		
		CommentsScrollPane.setPreferredSize(new java.awt.Dimension(166, 93));
		
		CommentsTextArea.setColumns(20);
		CommentsTextArea.setRows(5);
		CommentsScrollPane.setViewportView(CommentsTextArea);
		add(CommentsScrollPane);
		
		JLabel commentsLabel = new JLabel("Comments:");
		add(commentsLabel, BorderLayout.NORTH);
	}
	
	/**
	 * Saves comment information to the FHX file.
	 */
	public void saveComments() {
		
		IOController.getFile().getOptionalPart().setComments(CommentsTextArea.getText());
	}
}
