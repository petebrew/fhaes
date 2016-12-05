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
package org.fhaes.fhrecorder.view;

import java.awt.BorderLayout;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import javax.swing.JLabel;

import org.fhaes.enums.FeedbackDisplayProtocol;
import org.fhaes.enums.FeedbackMessageType;
import org.fhaes.fhrecorder.controller.IOController;
import org.fhaes.fhrecorder.model.FHX2_FileOptionalPart;
import org.fhaes.preferences.App;
import org.fhaes.preferences.FHAESPreferences.PrefKey;

/**
 * CommentPanel Class. This UI is used to display and modify the additional comments block of the fhx2 file format.
 * 
 * @author Alex Beatty, Clayton Bodendein, Kyle Hartmann, Scott Goble
 */
public class CommentPanel extends javax.swing.JPanel {
	
	private static final long serialVersionUID = 1L;
	private static String ENCODING_ERROR_MSG = "Comments include extended characters that can't be encoded for FHX2.  Please remove before continuing";
	
	private javax.swing.JScrollPane commentsScrollPane;
	private javax.swing.JTextArea commentsTextArea;
	
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
		commentsTextArea.setLineWrap(true);
		String comments = IOController.getFile().getOptionalPart().getComments();
		commentsTextArea.setText(comments);
		saveToData = true;
	}
	
	/**
	 * Initializes the GUI components.
	 */
	private void initComponents() {
	
		commentsScrollPane = new javax.swing.JScrollPane();
		commentsTextArea = new javax.swing.JTextArea();
		
		addComponentListener(new java.awt.event.ComponentAdapter() {
			
			@Override
			public void componentHidden(java.awt.event.ComponentEvent evt) {
			
				if (saveToData)
					saveComments();
			}
			
			@Override
			public void componentShown(java.awt.event.ComponentEvent evt) {
			
				commentsTextArea.requestFocusInWindow();
			}
		});
		setLayout(new BorderLayout(0, 0));
		
		commentsScrollPane.setPreferredSize(new java.awt.Dimension(166, 93));
		
		commentsTextArea.setColumns(20);
		commentsTextArea.setRows(5);
		
		commentsTextArea.addKeyListener(new KeyListener() {
			
			@Override
			public void keyPressed(KeyEvent arg0) {
			
			}
			
			@Override
			public void keyReleased(KeyEvent arg0) {
			
			}
			
			@Override
			public void keyTyped(KeyEvent arg0) {
			
				checkEncoding();
				
			}
			
		});
		
		commentsScrollPane.setViewportView(commentsTextArea);
		add(commentsScrollPane);
		
		JLabel commentsLabel = new JLabel("Comments:");
		add(commentsLabel, BorderLayout.NORTH);
	}
	
	private void checkEncoding() {
	
		Charset charset = StandardCharsets.UTF_8;
		
		if (App.prefs.getBooleanPref(PrefKey.ENFORCE_FHX2_RESTRICTIONS, false))
			charset = StandardCharsets.ISO_8859_1;
		
		if (MetaDataPanel.canEncodeString(commentsTextArea.getText(), charset))
		{
			if (FireHistoryRecorder.getFeedbackMessagePanel().getCurrentMessage().equals(ENCODING_ERROR_MSG))
			{
				FireHistoryRecorder.getFeedbackMessagePanel().clearFeedbackMessage();
			}
		}
		else
		{
			FireHistoryRecorder.getFeedbackMessagePanel().updateFeedbackMessage(FeedbackMessageType.WARNING,
					FeedbackDisplayProtocol.MANUAL_HIDE, ENCODING_ERROR_MSG);
		}
	}
	
	/**
	 * Saves comment information to the FHX file.
	 */
	public void saveComments() {
	
		IOController.getFile().getOptionalPart().setComments(commentsTextArea.getText());
		
	}
}
