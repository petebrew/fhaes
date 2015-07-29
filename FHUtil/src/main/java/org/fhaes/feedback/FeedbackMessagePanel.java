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
package org.fhaes.feedback;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EtchedBorder;

import org.fhaes.enums.FeedbackMessageType;
import org.fhaes.feedback.FeedbackDictionaryManager.FeedbackDictionary;
import org.fhaes.preferences.App;

import net.miginfocom.swing.MigLayout;

/**
 * FeedbackMessagePanel Class.
 */
public class FeedbackMessagePanel extends JPanel {
	
	private static final long serialVersionUID = 1L;
	
	// Declare local constants
	private final String EMPTY_MESSAGE_TEXT = "";
	
	// Declare local variables
	private static JLabel statusMessageText;
	private JLabel statusMessageIcon;
	private JButton dismissButton;
	private JButton hideMessagesButton;
	
	/**
	 * Creates new a FeedbackMessagePanel.
	 */
	public FeedbackMessagePanel() {
		
		initGUI();
	}
	
	/**
	 * Gets the string text of the current message.
	 * 
	 * @return the text contained in statusMessageText
	 */
	public String getCurrentMessage() {
		
		return statusMessageText.getText();
	}
	
	/**
	 * Stops showing the specified feedback message. This action is permanent until the "reset all feedback message preferences" button is
	 * pressed on the MainWindow.
	 */
	public void stopShowingMessage(FeedbackDictionary message) {
		
		if (message.getAssociatedKey() != null)
		{
			App.prefs.setBooleanPref(message.getAssociatedKey(), false);
		}
	}
	
	/**
	 * Clears the feedback message and hides the FeedbackMessagePanel.
	 */
	public void clearFeedbackMessage() {
		
		statusMessageIcon.setIcon(FeedbackMessageType.INFO.getIcon());
		statusMessageText.setForeground(FeedbackMessageType.INFO.getColor());
		statusMessageText.setText("<html>" + EMPTY_MESSAGE_TEXT + "</html>");
		this.setVisible(false);
	}
	
	/**
	 * Updates the feedback message according to the input and displays the FeedbackMessagePanel.
	 * 
	 * @param messageType
	 * @param messageString
	 */
	public void updateFeedbackMessage(FeedbackMessageType messageType, String messageString) {
		
		if (messageString == null || messageString.length() == 0)
		{
			clearFeedbackMessage();
			this.setVisible(false);
		}
		else
		{
			statusMessageIcon.setIcon(messageType.getIcon());
			statusMessageText.setForeground(messageType.getColor());
			statusMessageText.setText("<html>" + messageType.getPrefix() + messageString + "</html>");
			this.setVisible(true);
		}
	}
	
	/**
	 * Initializes the GUI components.
	 */
	private void initGUI() {
		
		// Initialize settings for the panel itself
		this.setVisible(false);
		this.setBorder(new EtchedBorder(EtchedBorder.RAISED, null, null));
		this.setLayout(new MigLayout("hidemode 2", "[][10px:10px:10px][grow][][]", "[grow]"));
		
		// Setup the message icon
		statusMessageIcon = new JLabel();
		this.add(statusMessageIcon, "cell 0 0");
		
		// Setup the message textbox
		statusMessageText = new JLabel(EMPTY_MESSAGE_TEXT);
		statusMessageText.setBackground(null);
		statusMessageText.setBorder(null);
		statusMessageText.setFont(new Font("Dialog", Font.PLAIN, 14));
		statusMessageText.setText("Some info or warning...");
		this.add(statusMessageText, "cell 2 0,growx,aligny center");
		
		/*
		 * DISMISS BUTTON
		 */
		dismissButton = new JButton("Dismiss");
		dismissButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				
				clearFeedbackMessage();
			}
		});
		this.add(dismissButton, "cell 3 0,growx,aligny center");
		
		/*
		 * HIDE MESSAGES BUTTON
		 */
		hideMessagesButton = new JButton("Hide these Messages");
		hideMessagesButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				
				clearFeedbackMessage();
			}
		});
		this.add(hideMessagesButton, "cell 4 0,growx,aligny center");
	}
}
