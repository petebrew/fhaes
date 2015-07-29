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

import java.awt.AlphaComposite;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EtchedBorder;

import org.fhaes.enums.FeedbackMessageType;
import org.fhaes.preferences.App;
import org.fhaes.preferences.FHAESPreferences.PrefKey;

import net.miginfocom.swing.MigLayout;

/**
 * FeedbackMessagePanel Class. This class contains all GUI components and logic pertaining to the communication of informative messages
 * between the FHAES software and the user.
 */
public class FeedbackMessagePanel extends JPanel {
	
	private static final long serialVersionUID = 1L;
	
	// Declare local constants
	private final int FIVE_SECOND_DELAY = 5000;
	private final float FULL_OPACITY = 1.0f;
	private final String BLANK_MESSAGE = "";
	
	// Declare GUI components
	private JLabel feedbackMessageText;
	private JLabel feedbackMessageIcon;
	private JButton hideMessagesButton;
	private Timer autoHideDelayTimer;
	
	// Declare local variables
	private float currentOpacity = FULL_OPACITY;
	
	/**
	 * Creates new a FeedbackMessagePanel.
	 */
	public FeedbackMessagePanel() {
		
		initGUI();
	}
	
	/**
	 * Gets the text of the current message as a string.
	 * 
	 * @return the string contained in feedbackMessageText
	 */
	public String getCurrentMessage() {
		
		return feedbackMessageText.getText();
	}
	
	/**
	 * Clears the text of the current message and hides the FeedbackMessagePanel.
	 */
	public void clearFeedbackMessage() {
		
		feedbackMessageText.setText(BLANK_MESSAGE);
		this.setVisible(false);
	}
	
	/**
	 * Updates the feedback message according to the input and displays the FeedbackMessagePanel.
	 * 
	 * @param messageType
	 * @param messageString
	 */
	public void updateFeedbackMessage(FeedbackMessageType messageType, String messageString) {
		
		PrefKey keyForCurrentMessage = FeedbackPreferenceManager.GetAssociatedKeyFromMessageText(messageString);
		boolean showThisFeedbackMessage = true;
		
		// Get the associated key value for this message, if it has one
		if (keyForCurrentMessage != null)
		{
			showThisFeedbackMessage = App.prefs.getBooleanPref(keyForCurrentMessage, true);
		}
		
		// Do not show the feedback message if the user has preferred it to be hidden
		if (showThisFeedbackMessage)
		{
			// Do not try to show an empty message
			if (messageString != null && messageString.length() != 0)
			{
				currentOpacity = FULL_OPACITY;
				feedbackMessageIcon.setIcon(messageType.getDisplayIcon());
				feedbackMessageText.setText(messageString);
				this.setBackground(messageType.getBackgroundColor());
				this.setVisible(true);
				
				// Only do the fade out animation if the message is of type info
				if (messageType.toString() == FeedbackMessageType.INFO.toString())
				{
					autoHideDelayTimer.schedule(new AutoHideMessageTask(), FIVE_SECOND_DELAY);
				}
			}
		}
	}
	
	/**
	 * Prevents the FeedbackMessagePanel from showing the current message in the future. This action is permanent until the 'reset all
	 * feedback message preferences' button is pressed on the MainWindow.
	 */
	public void stopShowingCurrentMessage() {
		
		PrefKey keyForCurrentMessage = FeedbackPreferenceManager.GetAssociatedKeyFromMessageText(getCurrentMessage());
		
		if (keyForCurrentMessage != null)
		{
			App.prefs.setBooleanPref(keyForCurrentMessage, false);
		}
	}
	
	/**
	 * Allows for the FeedbackMessagePanel to have variable transparency.
	 * 
	 * @param g
	 */
	@Override
	public void paint(Graphics g) {
		
		Graphics2D g2 = (Graphics2D) g.create();
		g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, currentOpacity));
		super.paint(g2);
		g2.dispose();
	}
	
	/**
	 * Repaints the FeedbackMessagePanel.
	 */
	private void repaintFeedbackMessagePanel() {
		
		this.repaint();
	}
	
	/**
	 * Initializes the GUI components.
	 */
	private void initGUI() {
		
		// Initialize settings for the panel itself
		this.setBorder(new EtchedBorder(EtchedBorder.RAISED, null, null));
		this.setLayout(new MigLayout("hidemode 2", "[][10px:10px:10px][grow][10px:10px:10px][]", "[grow]"));
		this.setVisible(false);
		
		// Setup the message icon
		feedbackMessageIcon = new JLabel();
		this.add(feedbackMessageIcon, "cell 0 0");
		
		// Setup the message textbox
		feedbackMessageText = new JLabel(BLANK_MESSAGE);
		feedbackMessageText.setBackground(null);
		feedbackMessageText.setBorder(null);
		feedbackMessageText.setFont(new Font("Dialog", Font.PLAIN, 14));
		feedbackMessageText.setText("Some info or warning...");
		this.add(feedbackMessageText, "cell 2 0,growx,aligny center");
		
		// Initialize the auto-hide delay animation timer
		autoHideDelayTimer = new Timer();
		
		/*
		 * HIDE MESSAGES BUTTON
		 */
		hideMessagesButton = new JButton("Hide these notifications");
		hideMessagesButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				
				stopShowingCurrentMessage();
				clearFeedbackMessage();
			}
		});
		this.add(hideMessagesButton, "cell 4 0,growx,aligny center");
	}
	
	/**
	 * AutoHideMessageTask Class. This task performs the operations required to get the FeedbackMessagePanel to gradually fade out prior to
	 * being hidden on the GUI.
	 * 
	 * @author Joshua Brogan
	 */
	class AutoHideMessageTask extends TimerTask {
		
		// Declare local constants
		private final int ONE_MILLISECOND = 1;
		private final float NO_OPACITY = 0.0f;
		
		@Override
		public void run() {
			
			while (true)
			{
				// Handle the gradual fade-out of the feedback message panel
				if (currentOpacity - 0.1f > NO_OPACITY)
				{
					currentOpacity = currentOpacity - 0.001f;
					repaintFeedbackMessagePanel();
				}
				else
				{
					clearFeedbackMessage();
					return;
				}
				
				// Run this task every fifth of a second
				try
				{
					Thread.sleep(ONE_MILLISECOND);
				}
				catch (InterruptedException ex)
				{
					Thread.currentThread().interrupt();
				}
			}
		}
	}
}
