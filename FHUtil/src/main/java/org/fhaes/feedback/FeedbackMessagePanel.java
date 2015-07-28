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

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EtchedBorder;

import org.fhaes.preferences.App;
import org.fhaes.preferences.FHAESPreferences.PrefKey;
import org.fhaes.util.Builder;

import net.miginfocom.swing.MigLayout;

/**
 * FeedbackMessagePanel Class.
 */
public class FeedbackMessagePanel extends JPanel {
	
	private static final long serialVersionUID = 1L;
	
	/**
	 * This enumerator is used to determine the message-type of status pane messages.
	 */
	public enum FeedbackMessageType {
		ERROR, WARNING, INFO;
	}
	
	/**
	 * Enumerators to represent the different types of column data.
	 */
	public enum FeedbackMessageID {
		
		NO_SPECIFIED_MESSAGE_ID(""),
		
		FILE_SAVED_MESSAGE("File was saved successfully"),
		
		FHX2_SAMPLE_NAME_LENGTH_MESSAGE("Sample name is too long for the original FHX2 program requirements."),
		
		FHX2_META_DATA_LENGTH_MESSAGE("Cannot enforce length restrictions without losing data! Please revise the highlighted fields."),
		
		MINIMUM_SAMPLE_NAME_LENGTH_MESSAGE("Sample name must be at least 3 characters in length.");
		
		// Declare local variables
		private String message;
		
		// Constructor
		FeedbackMessageID(String inMessage) {
			
			message = inMessage;
		}
		
		@Override
		public String toString() {
			
			return message;
		}
	}
	
	// Declare local GUI objects
	private static JLabel statusMessageText;
	protected JLabel statusMessageIcon;
	private JButton dismissButton;
	private JButton hideMessagesButton;
	
	// Declare local variables
	private static FeedbackMessageID currentMessageID = FeedbackMessageID.NO_SPECIFIED_MESSAGE_ID;
	
	/**
	 * Creates new a GUI_StatusBarPanel.
	 */
	public FeedbackMessagePanel() {
		
		initComponents();
	}
	
	/**
	 * Initializes the GUI components.
	 */
	private void initComponents() {
		
		this.setVisible(false);
		this.setBorder(new EtchedBorder(EtchedBorder.RAISED, null, null));
		this.setLayout(new MigLayout("hidemode 2", "[][10px:10px:10px][grow][][]", "[grow]"));
		
		statusMessageIcon = new JLabel("");
		this.add(statusMessageIcon, "cell 0 0");
		
		statusMessageText = new JLabel();
		statusMessageText.setText("Some info or warning");
		statusMessageText.setBackground(null);
		statusMessageText.setBorder(null);
		statusMessageText.setFont(new Font("Dialog", Font.PLAIN, 14));
		this.add(statusMessageText, "cell 2 0,growx,aligny center");
		
		dismissButton = new JButton("Dismiss");
		dismissButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				
				clearStatusMessage();
			}
		});
		this.add(dismissButton, "cell 3 0,growx,aligny center");
		
		hideMessagesButton = new JButton("Hide these Messages");
		hideMessagesButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				
				stopDisplayingMessage(currentMessageID);
				clearStatusMessage();
			}
		});
		this.add(hideMessagesButton, "cell 4 0,growx,aligny center");
	}
	
	/**
	 * Stops the status bar from displaying a specific type of message.
	 * 
	 * @param flagToToggle
	 */
	private void stopDisplayingMessage(FeedbackMessageID messageToToggle) {
		
		if (messageToToggle == FeedbackMessageID.FILE_SAVED_MESSAGE)
		{
			App.prefs.setPref(PrefKey.SHOW_FILE_SAVED_MESSAGE, "FALSE");
		}
	}
	
	/**
	 * Gets the special identifier of the current message.
	 * 
	 * @return
	 */
	public static FeedbackMessageID getCurrentMessageID() {
		
		return currentMessageID;
	}
	
	/**
	 * Clears the status bar message and hides the status bar panel.
	 */
	public void clearStatusMessage() {
		
		currentMessageID = FeedbackMessageID.NO_SPECIFIED_MESSAGE_ID;
		statusMessageText.setText("");
		this.setVisible(false);
	}
	
	/**
	 * Updates the status bar message to the input message and shows the status bar panel.
	 */
	public void updateStatusMessage(FeedbackMessageType messageType, Color inColor, FeedbackMessageID inID, String inText) {
		
		// Do not show the status bar message if any of the following conditions are met
		if (inText == null || inText.length() == 0)
		{
			clearStatusMessage();
			return;
		}
		
		if (inID == null)
		{
			clearStatusMessage();
			return;
		}
		
		if (inColor == null)
		{
			clearStatusMessage();
			return;
		}
		
		if (inID == FeedbackMessageID.FILE_SAVED_MESSAGE)
		{
			if (App.prefs.getPref(PrefKey.SHOW_FILE_SAVED_MESSAGE, "TRUE").equals("FALSE"))
			{
				clearStatusMessage();
				return;
			}
		}
		
		// Display the appropriate icon on the status bar as determined by the messageType
		if (messageType.equals(FeedbackMessageType.ERROR))
		{
			statusMessageIcon.setIcon(Builder.getImageIcon("delete.png"));
		}
		else if (messageType.equals(FeedbackMessageType.INFO))
		{
			statusMessageIcon.setIcon(Builder.getImageIcon("info.png"));
		}
		else if (messageType.equals(FeedbackMessageType.WARNING))
		{
			statusMessageIcon.setIcon(Builder.getImageIcon("warning.png"));
		}
		else
		{
			statusMessageIcon.setIcon(null);
		}
		
		// Do not show the hideMessagesButton if the input message is not flagged
		if (inID == FeedbackMessageID.NO_SPECIFIED_MESSAGE_ID)
		{
			dismissButton.setVisible(false);
			hideMessagesButton.setVisible(false);
		}
		else
		{
			dismissButton.setVisible(true);
			hideMessagesButton.setVisible(true);
		}
		
		currentMessageID = inID;
		statusMessageText.setForeground(inColor);
		statusMessageText.setText("<html>" + inText);
		this.setVisible(true);
	}
}
