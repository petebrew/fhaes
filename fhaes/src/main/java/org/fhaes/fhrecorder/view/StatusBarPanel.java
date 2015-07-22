/*******************************************************************************
 * Copyright (C) 2014 Josh Brogan and Peter Brewer
 * 
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 * 
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 * 
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package org.fhaes.fhrecorder.view;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EtchedBorder;

import net.miginfocom.swing.MigLayout;

import org.fhaes.fhrecorder.view.FireHistoryRecorder.MessageType;
import org.fhaes.preferences.App;
import org.fhaes.preferences.FHAESPreferences.PrefKey;
import org.fhaes.util.Builder;

/**
 * GUI_StatusBarPanel Class.
 */
public class StatusBarPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	public static final int NO_SPECIFIED_MESSAGE_ID = -1;
	public static final int FILE_SAVED_MESSAGE_ID = 0;
	public static final int FHX2_SAMPLE_NAME_LENGTH_MESSAGE_ID = 1;
	public static final int FHX2_META_DATA_FIELD_LENGTH_MESSAGE_ID = 2;
	public static final int MINIMUM_SAMPLE_NAME_LENGTH_MESSAGE_ID = 3;

	private static JLabel statusMessageText;
	protected JLabel statusMessageIcon;
	private JButton dismissButton;
	private JButton hideMessagesButton;

	private static Integer currentMessageID;

	/**
	 * Creates new a GUI_StatusBarPanel.
	 */
	public StatusBarPanel() {

		initComponents();
	}

	/**
	 * Initializes the GUI components.
	 */
	private void initComponents() {

		currentMessageID = NO_SPECIFIED_MESSAGE_ID;

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

			public void actionPerformed(ActionEvent arg0) {

				clearStatusMessage();
			}
		});
		this.add(dismissButton, "cell 3 0,growx,aligny center");

		hideMessagesButton = new JButton("Hide these Messages");
		hideMessagesButton.addActionListener(new ActionListener() {

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
	private void stopDisplayingMessage(int messageToToggle) {

		if (messageToToggle == FILE_SAVED_MESSAGE_ID)
			App.prefs.setPref(PrefKey.SHOW_FILE_SAVED_MESSAGE, "FALSE");
	}

	/**
	 * Gets the special identifier of the current message.
	 * 
	 * @return
	 */
	public static Integer getCurrentMessageID() {

		return currentMessageID;
	}

	/**
	 * Clears the status bar message and hides the status bar panel.
	 */
	public void clearStatusMessage() {

		currentMessageID = NO_SPECIFIED_MESSAGE_ID;
		statusMessageText.setText("");
		this.setVisible(false);
	}

	/**
	 * Updates the status bar message to the input message and shows the status bar panel.
	 */
	public void updateStatusMessage(MessageType messageType, Color inColor, Integer inID, String inText) {

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

		if (inID == FILE_SAVED_MESSAGE_ID)
		{
			if (App.prefs.getPref(PrefKey.SHOW_FILE_SAVED_MESSAGE, "TRUE").equals("FALSE"))
			{
				clearStatusMessage();
				return;
			}
		}

		// Display the appropriate icon on the status bar as determined by the messageType
		if (messageType.equals(MessageType.ERROR))
		{
			statusMessageIcon.setIcon(Builder.getImageIcon("delete.png"));
		}
		else if (messageType.equals(MessageType.INFO))
		{
			statusMessageIcon.setIcon(Builder.getImageIcon("info.png"));
		}
		else if (messageType.equals(MessageType.WARNING))
		{
			statusMessageIcon.setIcon(Builder.getImageIcon("warning.png"));
		}
		else
		{
			statusMessageIcon.setIcon(null);
		}

		// Do not show the hideMessagesButton if the input message is not flagged
		if (inID == NO_SPECIFIED_MESSAGE_ID)
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
