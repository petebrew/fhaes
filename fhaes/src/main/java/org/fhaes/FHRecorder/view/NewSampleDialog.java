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

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.text.ParseException;

import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.fhaes.FHRecorder.controller.FileController;
import org.fhaes.FHRecorder.controller.IOController;
import org.fhaes.FHRecorder.controller.SampleController;
import org.fhaes.FHRecorder.model.FHX2_FileRequiredPart;
import org.fhaes.FHRecorder.model.FHX2_Sample;
import org.fhaes.enums.FeedbackDisplayProtocol;
import org.fhaes.enums.FeedbackMessageType;
import org.fhaes.util.Builder;

import net.miginfocom.swing.MigLayout;

/**
 * NewSampleDialog Class. Form for creating a new sample.
 * 
 * @author Alex Beatty, Clayton Bodendein, Kyle Hartmann, Scott Goble
 */
public class NewSampleDialog extends javax.swing.JDialog implements KeyListener {
	
	private static final long serialVersionUID = 1L;
	
	// Declare local constants
	private final String MINIMUM_SAMPLE_NAME_LENGTH_MESSAGE = "Sample name must be at least 3 characters in length.";
	
	// Declare GUI components
	private JPanel buttonPanel;
	private javax.swing.JCheckBox barkCheckBox;
	private javax.swing.JButton cancelButton;
	private javax.swing.JButton okButton;
	private javax.swing.JLabel sampleNameLabel;
	private javax.swing.JLabel firstYearLabel;
	private javax.swing.JLabel lastYearLabel;
	private javax.swing.JCheckBox pithCheckBox;
	private javax.swing.JTextField sampleNameTextBox;
	private javax.swing.JSpinner firstYearSpinner;
	private javax.swing.JSpinner lastYearSpinner;
	
	// Declare local variables
	private FHX2_Sample sample;
	private int index;
	private int previousValueFYS; // previous value of first year spinner
	private int previousValueLYS; // previous value of last year spinner
	
	/**
	 * Creates new form NewSampleDialog.
	 * 
	 * @param parent parent of form
	 * @param modal
	 * @param inIndex Index of sample
	 */
	public NewSampleDialog(java.awt.Frame parent, int inIndex) {
		
		super(parent);
		
		initComponents();
		
		index = inIndex;
		if (index > -1)
			sample = IOController.getFile().getRequiredPart().getSample(index);
		else
		{
			FHX2_FileRequiredPart temp = IOController.getFile().getRequiredPart();
			sample = new FHX2_Sample(temp.getDataSetFirstYear(), temp.getDataSetLastYear());
		}
		
		postInitialization();
		this.setLocationRelativeTo(parent);
		this.setIconImage(Builder.getApplicationIcon());
	}
	
	/**
	 * Initializes the GUI components.
	 */
	private void initComponents() {
		
		this.setModalityType(ModalityType.APPLICATION_MODAL);
		
		sampleNameLabel = new javax.swing.JLabel();
		sampleNameTextBox = new javax.swing.JTextField();
		firstYearLabel = new javax.swing.JLabel();
		pithCheckBox = new javax.swing.JCheckBox();
		lastYearLabel = new javax.swing.JLabel();
		barkCheckBox = new javax.swing.JCheckBox();
		
		setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
		
		sampleNameLabel.setText("Sample name:");
		sampleNameTextBox.addKeyListener(this);
		firstYearLabel.setText("First year:");
		pithCheckBox.setText("Pith");
		lastYearLabel.setText("Last year:");
		barkCheckBox.setText("Bark");
		
		firstYearSpinner = new javax.swing.JSpinner();
		firstYearSpinner.setModel(new SpinnerNumberModel(FileController.CURRENT_YEAR - 1, FileController.EARLIEST_ALLOWED_YEAR,
				FileController.CURRENT_YEAR - 1, 1));
		firstYearSpinner.setEditor(new JSpinner.NumberEditor(firstYearSpinner, "#####"));
		
		// Workaround to enable manual editing of spinner
		((JSpinner.DefaultEditor) firstYearSpinner.getEditor()).getTextField().setFocusTraversalKeysEnabled(false);
		((JSpinner.DefaultEditor) firstYearSpinner.getEditor()).getTextField().addKeyListener(new KeyListener() {
			
			@Override
			public void keyPressed(KeyEvent evt) {
			
			}
			
			@Override
			public void keyReleased(KeyEvent evt) {
			
			}
			
			@Override
			public void keyTyped(KeyEvent evt) {
				
				if (evt.getKeyChar() == KeyEvent.VK_TAB || evt.getKeyChar() == KeyEvent.VK_ENTER)
				{
					try
					{
						((JSpinner.DefaultEditor) firstYearSpinner.getEditor()).getTextField().commitEdit();
						pithCheckBox.requestFocusInWindow();
					}
					catch (ParseException e)
					{
					}
				}
				else if (evt.getKeyChar() == KeyEvent.VK_BACK_SPACE)
				{
					((JSpinner.DefaultEditor) firstYearSpinner.getEditor()).getTextField().selectAll();
					evt.consume();
				}
				else if (evt.getKeyChar() == KeyEvent.VK_PLUS)
				{
					((JSpinner.DefaultEditor) firstYearSpinner.getEditor()).getTextField().select(0, 0);
					int currentSpinnerValue = (Integer) ((JSpinner.DefaultEditor) firstYearSpinner.getEditor()).getTextField().getValue();
					if (currentSpinnerValue < 0)
						currentSpinnerValue = currentSpinnerValue * -1;
					evt.consume();
				}
				else if (evt.getKeyChar() == KeyEvent.VK_MINUS)
				{
					((JSpinner.DefaultEditor) firstYearSpinner.getEditor()).getTextField().select(0, 0);
					int currentSpinnerValue = (Integer) ((JSpinner.DefaultEditor) firstYearSpinner.getEditor()).getTextField().getValue();
					if (currentSpinnerValue > 0)
						currentSpinnerValue = currentSpinnerValue * -1;
					evt.consume();
				}
			}
		});
		
		firstYearSpinner.addChangeListener(new ChangeListener() {
			
			@Override
			public void stateChanged(ChangeEvent e) {
				
				if ((Integer) firstYearSpinner.getValue() == 0 && previousValueFYS == 1)
					firstYearSpinner.setValue(-1);
				else if ((Integer) firstYearSpinner.getValue() == 0 && previousValueFYS == -1)
					firstYearSpinner.setValue(1);
				else if ((Integer) firstYearSpinner.getValue() == 0)
					firstYearSpinner.setValue(1);
				else if ((Integer) firstYearSpinner.getValue() >= (Integer) lastYearSpinner.getValue())
					firstYearSpinner.setValue(previousValueFYS);
				previousValueFYS = (Integer) firstYearSpinner.getValue();
			}
		});
		
		lastYearSpinner = new javax.swing.JSpinner();
		lastYearSpinner.setModel(
				new SpinnerNumberModel(FileController.CURRENT_YEAR, FileController.EARLIEST_ALLOWED_YEAR, FileController.CURRENT_YEAR, 1));
		lastYearSpinner.setEditor(new JSpinner.NumberEditor(lastYearSpinner, "#####"));
		
		// Workaround to enable manual editing of spinner
		((JSpinner.DefaultEditor) lastYearSpinner.getEditor()).getTextField().setFocusTraversalKeysEnabled(false);
		((JSpinner.DefaultEditor) lastYearSpinner.getEditor()).getTextField().addKeyListener(new KeyListener() {
			
			@Override
			public void keyPressed(KeyEvent evt) {
			
			}
			
			@Override
			public void keyReleased(KeyEvent evt) {
			
			}
			
			@Override
			public void keyTyped(KeyEvent evt) {
				
				if (evt.getKeyChar() == KeyEvent.VK_TAB || evt.getKeyChar() == KeyEvent.VK_ENTER)
				{
					try
					{
						((JSpinner.DefaultEditor) lastYearSpinner.getEditor()).getTextField().commitEdit();
						barkCheckBox.requestFocusInWindow();
					}
					catch (ParseException e)
					{
					}
				}
				else if (evt.getKeyChar() == KeyEvent.VK_BACK_SPACE)
				{
					((JSpinner.DefaultEditor) lastYearSpinner.getEditor()).getTextField().selectAll();
					evt.consume();
				}
				else if (evt.getKeyChar() == KeyEvent.VK_PLUS)
				{
					((JSpinner.DefaultEditor) lastYearSpinner.getEditor()).getTextField().select(0, 0);
					int currentSpinnerValue = (Integer) ((JSpinner.DefaultEditor) lastYearSpinner.getEditor()).getTextField().getValue();
					if (currentSpinnerValue < 0)
						currentSpinnerValue = currentSpinnerValue * -1;
					evt.consume();
				}
				else if (evt.getKeyChar() == KeyEvent.VK_MINUS)
				{
					((JSpinner.DefaultEditor) lastYearSpinner.getEditor()).getTextField().select(0, 0);
					int currentSpinnerValue = (Integer) ((JSpinner.DefaultEditor) lastYearSpinner.getEditor()).getTextField().getValue();
					if (currentSpinnerValue > 0)
						currentSpinnerValue = currentSpinnerValue * -1;
					evt.consume();
				}
			}
		});
		
		lastYearSpinner.addChangeListener(new ChangeListener() {
			
			@Override
			public void stateChanged(ChangeEvent e) {
				
				if ((Integer) lastYearSpinner.getValue() == 0 && previousValueLYS == 1)
					lastYearSpinner.setValue(-1);
				else if ((Integer) lastYearSpinner.getValue() == 0 && previousValueLYS == -1)
					lastYearSpinner.setValue(1);
				else if ((Integer) lastYearSpinner.getValue() == 0)
					lastYearSpinner.setValue(1);
				else if ((Integer) lastYearSpinner.getValue() <= (Integer) firstYearSpinner.getValue())
					lastYearSpinner.setValue(previousValueLYS);
				previousValueLYS = (Integer) lastYearSpinner.getValue();
			}
		});
		
		setTitle("New Sample");
		setIconImage(Builder.getApplicationIcon());
		pack();
	}
	
	/**
	 * Components to be initialized after initComponents().
	 */
	private void postInitialization() {
		
		if (sample.getSampleFirstYear() == 0)
			sample.setSampleFirstYear(FileController.CURRENT_YEAR - 1);
			
		if (sample.getSampleLastYear() == 0)
			sample.setSampleLastYear(FileController.CURRENT_YEAR);
			
		firstYearSpinner.setValue(sample.getSampleFirstYear());
		lastYearSpinner.setValue(sample.getSampleLastYear());
		
		previousValueFYS = (Integer) firstYearSpinner.getValue();
		previousValueLYS = (Integer) lastYearSpinner.getValue();
		
		barkCheckBox.setSelected(sample.hasBark());
		pithCheckBox.setSelected(sample.hasPith());
		sampleNameTextBox.setText(sample.getSampleName());
		
		getContentPane().setLayout(new MigLayout("insets 0", "[:2:2][right][93.00px,grow][81px][:2:2]", "[:2:2][19px][20px][20px][]"));
		getContentPane().add(lastYearLabel, "cell 1 3,alignx right,aligny center");
		getContentPane().add(firstYearLabel, "cell 1 2,alignx right,aligny center");
		getContentPane().add(sampleNameLabel, "cell 1 1,alignx right,aligny center");
		getContentPane().add(barkCheckBox, "cell 3 3,alignx left,aligny center");
		getContentPane().add(pithCheckBox, "cell 3 2,alignx left,aligny center");
		getContentPane().add(sampleNameTextBox, "cell 2 1 2 1,growx,aligny center");
		getContentPane().add(firstYearSpinner, "cell 2 2,growx,aligny center");
		getContentPane().add(lastYearSpinner, "cell 2 3,growx,aligny center");
		
		buttonPanel = new JPanel();
		FlowLayout fl_buttonPanel = (FlowLayout) buttonPanel.getLayout();
		fl_buttonPanel.setAlignment(FlowLayout.RIGHT);
		getContentPane().add(buttonPanel, "cell 0 4 5 1,grow");
		
		okButton = new javax.swing.JButton();
		okButton.setText("OK");
		okButton.setEnabled(false);
		buttonPanel.add(okButton);
		okButton.addActionListener(new java.awt.event.ActionListener() {
			
			@Override
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				
				confirmButtonActionPerformed(evt);
			}
		});
		cancelButton = new javax.swing.JButton();
		cancelButton.setText("Cancel");
		buttonPanel.add(cancelButton);
		cancelButton.addActionListener(new java.awt.event.ActionListener() {
			
			@Override
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				
				cancelButtonActionPerformed(evt);
			}
		});
		pack();
	}
	
	/**
	 * Handles when the "OK" button is clicked.
	 * 
	 * @param evt
	 */
	private void confirmButtonActionPerformed(java.awt.event.ActionEvent evt) {
		
		if (sampleNameTextBox.getText().length() < 3)
		{
			FireHistoryRecorder.getFeedbackMessagePanel().updateFeedbackMessage(FeedbackMessageType.WARNING,
					FeedbackDisplayProtocol.STATE_DEPENDENT, MINIMUM_SAMPLE_NAME_LENGTH_MESSAGE);
					
			sampleNameTextBox.setForeground(Color.red);
		}
		else if (sampleNameTextBox.getText().length() > FileController.FHX2_MAX_SAMPLE_NAME_LENGTH
				&& FileController.isEnforcingOldReqs() == true)
		{
			FireHistoryRecorder.getFeedbackMessagePanel().updateFeedbackMessage(FeedbackMessageType.WARNING,
					FeedbackDisplayProtocol.STATE_DEPENDENT, "Sample name is too long for the original FHX2 program requirements.");
					
			sampleNameTextBox.setForeground(Color.red);
		}
		else if ((Integer) firstYearSpinner.getValue() >= (Integer) lastYearSpinner.getValue())
		{
			FireHistoryRecorder.getFeedbackMessagePanel().updateFeedbackMessage(FeedbackMessageType.WARNING,
					FeedbackDisplayProtocol.STATE_DEPENDENT, "The first year of a sample cannot be after its last year.");
					
			firstYearSpinner.setValue((Integer) lastYearSpinner.getValue() - 1);
		}
		else if ((Integer) firstYearSpinner.getValue() < 501 && FileController.isEnforcingOldReqs() == true)
		{
			FireHistoryRecorder.getFeedbackMessagePanel().updateFeedbackMessage(FeedbackMessageType.WARNING,
					FeedbackDisplayProtocol.STATE_DEPENDENT, "The original FHX2 program doesn't support years prior to 501BC.");
		}
		else if ((Integer) firstYearSpinner.getValue() > 2020 && FileController.isEnforcingOldReqs() == true)
		{
			FireHistoryRecorder.getFeedbackMessagePanel().updateFeedbackMessage(FeedbackMessageType.WARNING,
					FeedbackDisplayProtocol.STATE_DEPENDENT, "The original FHX2 program doesn't support years after 2020AD.");
		}
		else
		{
			if (FireHistoryRecorder.getFeedbackMessagePanel().getCurrentMessage() == MINIMUM_SAMPLE_NAME_LENGTH_MESSAGE)
			{
				FireHistoryRecorder.getFeedbackMessagePanel().clearFeedbackMessage();
			}
			else if (FireHistoryRecorder.getFeedbackMessagePanel().getCurrentMessage() == FHX2_Sample.FHX2_SAMPLE_NAME_LENGTH_MESSAGE)
			{
				FireHistoryRecorder.getFeedbackMessagePanel().clearFeedbackMessage();
			}
			
			sample.setSampleName(sampleNameTextBox.getText());
			sample.setSampleFirstYear((Integer) firstYearSpinner.getValue());
			sample.setSampleLastYear((Integer) lastYearSpinner.getValue());
			sample.setBark(barkCheckBox.isSelected());
			sample.setPith(pithCheckBox.isSelected());
			
			SampleController.saveSample(index, sample);
			this.setVisible(false);
		}
	}
	
	/**
	 * Handles when the "cancel" button is clicked.
	 * 
	 * @param evt
	 */
	private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {
		
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
