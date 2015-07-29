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
 * FeedbackMessagePanel Class.
 */
public class FeedbackMessagePanel extends JPanel {
	
	private static final long serialVersionUID = 1L;
	
	// Declare local constants
	private final int FIVE_SECOND_DELAY = 5000;
	private final float FULL_OPACITY = 1.0f;
	
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
	 * Gets the string text of the current message.
	 * 
	 * @return the string contained in feedbackMessageText
	 */
	public String getCurrentMessage() {
		
		return feedbackMessageText.getText();
	}
	
	/**
	 * Clears the feedback message and hides the FeedbackMessagePanel.
	 */
	public void clearFeedbackMessage() {
		
		this.setVisible(false);
	}
	
	/**
	 * Updates the feedback message according to the input and displays the FeedbackMessagePanel.
	 * 
	 * @param messageType
	 * @param messageString
	 */
	public void updateFeedbackMessage(FeedbackMessageType messageType, String messageString) {
		
		PrefKey keyForCurrentMessage = FeedbackDictionaryManager.GetAssociatedKeyFromMessageText(messageString);
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
				if (messageType.toString() == "INFO")
				{
					autoHideDelayTimer.schedule(new AutoHideTask(), FIVE_SECOND_DELAY);
				}
			}
		}
	}
	
	/**
	 * Prevents the feedback message panel from showing the current message in the future. This action is permanent until the 'reset all
	 * feedback message preferences' button is pressed on the main window.
	 */
	public void stopShowingThisMessage() {
		
		PrefKey keyForCurrentMessage = FeedbackDictionaryManager.GetAssociatedKeyFromMessageText(getCurrentMessage());
		
		if (keyForCurrentMessage != null)
		{
			App.prefs.setBooleanPref(keyForCurrentMessage, false);
		}
	}
	
	/**
	 * Allows for the feedback message panel to have variable transparency.
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
	 * Repaints the feedback message panel.
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
		feedbackMessageText = new JLabel();
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
				
				stopShowingThisMessage();
				clearFeedbackMessage();
			}
		});
		this.add(hideMessagesButton, "cell 4 0,growx,aligny center");
	}
	
	/**
	 * AutoHideTask Class.
	 * 
	 * @author Joshua Brogan
	 */
	class AutoHideTask extends TimerTask {
		
		// Declare local constants
		private final int TWO_HUNDRED_MILLISECONDS = 200;
		private final float NO_OPACITY = 0.0f;
		
		@Override
		public void run() {
			
			while (true)
			{
				// Handle the gradual fade-out of the feedback message panel
				if (currentOpacity - 0.1f > NO_OPACITY)
				{
					currentOpacity = currentOpacity - 0.1f;
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
					Thread.sleep(TWO_HUNDRED_MILLISECONDS);
				}
				catch (InterruptedException ex)
				{
					Thread.currentThread().interrupt();
				}
			}
		}
	}
}
