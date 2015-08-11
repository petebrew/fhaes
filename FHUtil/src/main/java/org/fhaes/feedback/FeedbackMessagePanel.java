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

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.border.EtchedBorder;

import org.fhaes.enums.FeedbackDisplayProtocol;
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
	private final String BLANK_MESSAGE = "";
	private final float FULL_OPACITY = 1.0f;
	private final int EIGHT_SECOND_DELAY = 8000;
	
	// Declare GUI components
	private JTextArea feedbackMessageText;
	private JLabel feedbackMessageIcon;
	private JButton dismissMessageButton;
	private JButton hideMessageButton;
	private Timer autoHideDelayTimer;
	private AutoHideMessageTask animationTask;
	
	// Declare local variables
	private float currentOpacity = FULL_OPACITY;
	private boolean updateMessageFired = false;
	
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
	 * @param displayProtocol
	 * @param messageString
	 */
	public void updateFeedbackMessage(FeedbackMessageType messageType, FeedbackDisplayProtocol displayProtocol, String messageString) {
		
		PrefKey keyForCurrentMessage = FeedbackPreferenceManager.GetAssociatedKeyFromMessageText(messageString);
		boolean showThisFeedbackMessage = true;
		
		// Handle re-arrangement of the buttons if necessary
		if (displayProtocol == FeedbackDisplayProtocol.MANUAL_HIDE && keyForCurrentMessage == null)
		{
			dismissMessageButton.setVisible(true);
			hideMessageButton.setVisible(false);
		}
		else
		{
			// Get the associated key value for this message, if it has one
			if (keyForCurrentMessage != null)
			{
				this.remove(feedbackMessageText);
				this.add(feedbackMessageText, "cell 1 0,growx,aligny center");
				
				dismissMessageButton.setVisible(false);
				hideMessageButton.setVisible(true);
				showThisFeedbackMessage = App.prefs.getBooleanPref(keyForCurrentMessage, true);
			}
			else
			{
				this.remove(feedbackMessageText);
				this.add(feedbackMessageText, "cell 1 0 3 0,growx,aligny center");
				
				dismissMessageButton.setVisible(false);
				hideMessageButton.setVisible(false);
			}
		}
		
		// Do not show the feedback message if the user has preferred it to be hidden
		if (showThisFeedbackMessage)
		{
			// Do not try to show an empty message
			if (messageString != null && messageString.length() != 0)
			{
				updateMessageFired = true;
				
				feedbackMessageText.setText(messageString);
				feedbackMessageIcon.setIcon(messageType.getDisplayIcon());
				this.setBackground(messageType.getBackgroundColor());
				
				currentOpacity = FULL_OPACITY;
				this.setVisible(true);
				
				// Only do the fade out animation the display protocol is set to auto hide
				if (displayProtocol == FeedbackDisplayProtocol.AUTO_HIDE)
				{
					// Start the auto-hide process after showing the message for five seconds
					cancelAnimationTask();
					animationTask = new AutoHideMessageTask();
					autoHideDelayTimer.schedule(animationTask, EIGHT_SECOND_DELAY);
				}
				else
				{
					cancelAnimationTask();
				}
			}
		}
	}
	
	/**
	 * Cancels the animation task if it is running, then resets the view properties as needed.
	 */
	private void cancelAnimationTask() {
		
		if (animationTask != null)
		{
			animationTask.cancel();
			currentOpacity = FULL_OPACITY;
			this.setVisible(true);
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
		this.setLayout(new MigLayout("hidemode 2,insets 0,gap 0 0", "[50:50:50,center][grow,fill][10:10:10][][center][10:10:10]",
				"[55:55:55,grow,center]"));
		this.setVisible(false);
		
		// Setup the message icon
		feedbackMessageIcon = new JLabel();
		this.add(feedbackMessageIcon, "cell 0 0");
		
		// Setup the message textbox
		feedbackMessageText = new JTextArea(BLANK_MESSAGE);
		feedbackMessageText.setBackground(null);
		feedbackMessageText.setBorder(BorderFactory.createEmptyBorder());
		feedbackMessageText.setFont(new Font("Dialog", Font.PLAIN, 14));
		feedbackMessageText.setLineWrap(true);
		feedbackMessageText.setWrapStyleWord(true);
		feedbackMessageText.setText("Some info or warning...");
		this.add(feedbackMessageText, "cell 1 0,growx,aligny center");
		
		// Initialize the auto-hide delay animation timer
		autoHideDelayTimer = new Timer();
		
		/*
		 * DISMISS MESSAGE BUTTON
		 */
		dismissMessageButton = new JButton("Dismiss this notification");
		dismissMessageButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				
				clearFeedbackMessage();
			}
		});
		this.add(dismissMessageButton, "cell 3 0,alignx center,aligny center");
		
		/*
		 * HIDE MESSAGE BUTTON
		 */
		hideMessageButton = new JButton("Hide these notifications");
		hideMessageButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				
				stopShowingCurrentMessage();
				clearFeedbackMessage();
			}
		});
		this.add(hideMessageButton, "cell 4 0,alignx center,aligny center");
	}
	
	/**
	 * AutoHideMessageTask Class. This task performs the operations required to get the FeedbackMessagePanel to gradually fade out prior to
	 * being hidden on the GUI.
	 * 
	 * @author Joshua Brogan
	 */
	private class AutoHideMessageTask extends TimerTask {
		
		// Declare local constants
		private final int ONE_MILLISECOND = 1;
		private final float NO_OPACITY = 0.0f;
		
		@Override
		public void run() {
			
			updateMessageFired = false;
			
			while (true)
			{
				// Handle the gradual fade-out of the feedback message panel
				if (currentOpacity - 0.1f > NO_OPACITY && !updateMessageFired)
				{
					currentOpacity = currentOpacity - 0.001f;
					repaintFeedbackMessagePanel();
				}
				else if (updateMessageFired)
				{
					currentOpacity = FULL_OPACITY;
					return;
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
