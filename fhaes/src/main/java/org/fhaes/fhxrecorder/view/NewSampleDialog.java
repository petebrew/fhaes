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
package org.fhaes.fhxrecorder.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.WindowConstants;
import javax.swing.border.BevelBorder;
import javax.swing.border.SoftBevelBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.fhaes.enums.FeedbackDisplayProtocol;
import org.fhaes.enums.FeedbackMessageType;
import org.fhaes.fhxrecorder.controller.FileController;
import org.fhaes.fhxrecorder.controller.IOController;
import org.fhaes.fhxrecorder.controller.SampleController;
import org.fhaes.fhxrecorder.model.FHX2_Sample;
import org.fhaes.fhxrecorder.util.LengthRestrictedDocument;
import org.fhaes.util.Builder;

import net.miginfocom.swing.MigLayout;

/**
 * NewSampleDialog Class. Form for creating a new sample.
 * 
 * @author Alex Beatty, Clayton Bodendein, Kyle Hartmann, Scott Goble
 */
public class NewSampleDialog extends JDialog implements KeyListener {
	
	private static final long serialVersionUID = 1L;
	
	// Declare local constants
	private final int OLD_FHX_MAXIMUM_YEAR = 2020;
	private final int OLD_FHX_MINIMUM_YEAR = 501;
	
	// Declare GUI components
	private JPanel basePanel;
	private JPanel buttonPanel;
	private JCheckBox barkCheckBox;
	private JButton cancelButton;
	private JButton okButton;
	private JLabel sampleNameLabel;
	private JLabel firstYearLabel;
	private JLabel lastYearLabel;
	private JCheckBox pithCheckBox;
	private JTextField sampleNameTextBox;
	private BCADYearSpinner firstYearSpinner;
	private BCADYearSpinner lastYearSpinner;
	
	/**
	 * Creates new form NewSampleDialog.
	 * 
	 * @param parent, the parent of this form
	 */
	public NewSampleDialog(Frame parent) {
		
		int firstYearToSet = FileController.CURRENT_YEAR - 1;
		int lastYearToSet = FileController.CURRENT_YEAR;
		
		if (SampleController.getSelectedSampleIndex() != SampleController.INDEX_REPRESENTING_NO_SAMPLES)
		{
			firstYearToSet = IOController.getFile().getRequiredPart().getDataSetFirstYear();
			lastYearToSet = IOController.getFile().getRequiredPart().getDataSetLastYear();
		}
		
		initGUI(firstYearToSet, lastYearToSet);
		
		this.setLocationRelativeTo(parent);
		this.pack();
		this.repaint();
	}
	
	/**
	 * Initializes the GUI.
	 * 
	 * @param firstYearToSet
	 * @param lastYearToSet
	 */
	private void initGUI(int firstYearToSet, int lastYearToSet) {
		
		this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		this.setIconImage(Builder.getApplicationIcon());
		this.setMinimumSize(new Dimension(350, 150));
		this.setModalityType(ModalityType.APPLICATION_MODAL);
		this.setResizable(false);
		this.setTitle("New Sample");
		
		this.getContentPane().setLayout(new BorderLayout(0, 0));
		
		basePanel = new JPanel();
		basePanel.setBorder(new SoftBevelBorder(BevelBorder.LOWERED, null, null, null, null));
		basePanel.setLayout(new MigLayout("insets 2", "[90:90:90,grow,right][100,grow][81px]", "[28:28:28][28:28:28][28:28:28][grow,top]"));
		this.getContentPane().add(basePanel, BorderLayout.NORTH);
		
		sampleNameLabel = new JLabel();
		sampleNameLabel.setText("Sample name:");
		basePanel.add(sampleNameLabel, "cell 0 0,alignx right,aligny center");
		
		sampleNameTextBox = new JTextField();
		sampleNameTextBox.addKeyListener(this);
		sampleNameTextBox.setDocument(new LengthRestrictedDocument(SampleInputPanel.MAXIMUM_SAMPLE_NAME_LENGTH));
		basePanel.add(sampleNameTextBox, "cell 1 0 2 1,grow");
		
		firstYearLabel = new JLabel();
		firstYearLabel.setText("First year:");
		basePanel.add(firstYearLabel, "cell 0 1,alignx right,aligny center");
		
		firstYearSpinner = new BCADYearSpinner(firstYearToSet, FileController.EARLIEST_ALLOWED_YEAR, FileController.CURRENT_YEAR - 1);
		firstYearSpinner.addChangeListener(new ChangeListener() {
			
			@Override
			public void stateChanged(ChangeEvent e) {
				
				// If spinner year is moved above or equal to the last year spinner's value
				if (firstYearSpinner.getValueAsInteger() >= lastYearSpinner.getValueAsInteger())
				{
					firstYearSpinner.setValue(firstYearSpinner.getMostRecentValue());
				}
				
				firstYearSpinner.updateMostRecentValue();
			}
		});
		basePanel.add(firstYearSpinner, "cell 1 1,grow");
		
		pithCheckBox = new JCheckBox();
		pithCheckBox.setText("Pith");
		basePanel.add(pithCheckBox, "cell 2 1,alignx left,aligny center");
		
		lastYearLabel = new JLabel();
		lastYearLabel.setText("Last year:");
		basePanel.add(lastYearLabel, "cell 0 2,alignx right,aligny center");
		
		lastYearSpinner = new BCADYearSpinner(lastYearToSet, FileController.EARLIEST_ALLOWED_YEAR, FileController.CURRENT_YEAR);
		lastYearSpinner.addChangeListener(new ChangeListener() {
			
			@Override
			public void stateChanged(ChangeEvent e) {
				
				// If spinner year is moved below or equal to the first year spinner's value
				if (lastYearSpinner.getValueAsInteger() <= firstYearSpinner.getValueAsInteger())
				{
					lastYearSpinner.setValue(lastYearSpinner.getMostRecentValue());
				}
				
				lastYearSpinner.updateMostRecentValue();
			}
		});
		basePanel.add(lastYearSpinner, "cell 1 2,grow");
		
		barkCheckBox = new JCheckBox();
		barkCheckBox.setText("Bark");
		basePanel.add(barkCheckBox, "cell 2 2,alignx left,aligny center");
		
		buttonPanel = new JPanel();
		buttonPanel.setLayout(new MigLayout("insets 0", "[grow][80:80:80][80:80:80]", "[30:30:30,grow,fill]"));
		this.getContentPane().add(buttonPanel, BorderLayout.SOUTH);
		
		/*
		 * OK BUTTON
		 */
		okButton = new JButton();
		okButton.setText("OK");
		okButton.setEnabled(false);
		okButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent evt) {
				
				handleOkButtonPressed(evt);
			}
		});
		buttonPanel.add(okButton, "cell 1 0,grow");
		
		/*
		 * CANCEL BUTTON
		 */
		cancelButton = new JButton();
		cancelButton.setText("Cancel");
		cancelButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent evt) {
				
				handleCancelButtonPressed(evt);
			}
		});
		buttonPanel.add(cancelButton, "cell 2 0,grow");
	}
	
	/**
	 * Handles when the "OK" button is clicked.
	 * 
	 * @param evt
	 */
	private void handleOkButtonPressed(ActionEvent evt) {
		
		if (sampleNameTextBox.getText().length() < SampleInputPanel.MINIMUM_SAMPLE_NAME_LENGTH)
		{
			FireHistoryRecorder.getFeedbackMessagePanel().updateFeedbackMessage(FeedbackMessageType.WARNING,
					FeedbackDisplayProtocol.AUTO_HIDE, SampleInputPanel.MINIMUM_SAMPLE_NAME_LENGTH_MESSAGE);
					
			sampleNameTextBox.setForeground(Color.RED);
		}
		else if (sampleNameTextBox.getText().length() > FileController.FHX2_MAX_SAMPLE_NAME_LENGTH
				&& FileController.isEnforcingOldReqs() == true)
		{
			FireHistoryRecorder.getFeedbackMessagePanel().updateFeedbackMessage(FeedbackMessageType.WARNING,
					FeedbackDisplayProtocol.AUTO_HIDE, "Sample name is too long for the original FHX2 program requirements.");
					
			sampleNameTextBox.setForeground(Color.RED);
		}
		else if (firstYearSpinner.getValueAsInteger() < OLD_FHX_MINIMUM_YEAR && FileController.isEnforcingOldReqs() == true)
		{
			FireHistoryRecorder.getFeedbackMessagePanel().updateFeedbackMessage(FeedbackMessageType.WARNING,
					FeedbackDisplayProtocol.AUTO_HIDE, "The original FHX2 program doesn't support years prior to 501BC.");
					
			sampleNameTextBox.setForeground(Color.BLACK);
		}
		else if (firstYearSpinner.getValueAsInteger() > OLD_FHX_MAXIMUM_YEAR && FileController.isEnforcingOldReqs() == true)
		{
			FireHistoryRecorder.getFeedbackMessagePanel().updateFeedbackMessage(FeedbackMessageType.WARNING,
					FeedbackDisplayProtocol.AUTO_HIDE, "The original FHX2 program doesn't support years after 2020AD.");
					
			sampleNameTextBox.setForeground(Color.BLACK);
		}
		else
		{
			FireHistoryRecorder.getFeedbackMessagePanel().clearFeedbackMessage();
			
			FHX2_Sample sample = new FHX2_Sample();
			sample.setSampleName(sampleNameTextBox.getText());
			sample.setSampleFirstYear(firstYearSpinner.getValueAsInteger());
			sample.setSampleLastYear(lastYearSpinner.getValueAsInteger());
			sample.setBark(barkCheckBox.isSelected());
			sample.setPith(pithCheckBox.isSelected());
			
			SampleController.saveNewSample(sample);
			this.setVisible(false);
		}
	}
	
	/**
	 * Handles when the "cancel" button is clicked.
	 * 
	 * @param evt
	 */
	private void handleCancelButtonPressed(ActionEvent evt) {
		
		this.setVisible(false);
	}
	
	/**
	 * The following are methods for handling when keys are pressed while this dialog has focus.
	 */
	@Override
	public void keyTyped(KeyEvent ke) {}
	
	@Override
	public void keyPressed(KeyEvent ke) {}
	
	@Override
	public void keyReleased(KeyEvent ke) {
		
		if (sampleNameTextBox.getText() != null && sampleNameTextBox.getText().trim().length() > 0)
		{
			okButton.setEnabled(true);
		}
		else
		{
			okButton.setEnabled(false);
		}
	}
}
