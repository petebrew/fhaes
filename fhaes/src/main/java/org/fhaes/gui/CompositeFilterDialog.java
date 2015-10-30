/**************************************************************************************************
 * Fire History Analysis and Exploration System (FHAES), Copyright (C) 2015
 * 
 * Contributors: Peter Brewer
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
package org.fhaes.gui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import net.miginfocom.swing.MigLayout;

import org.fhaes.enums.EventTypeToProcess;
import org.fhaes.enums.FireFilterType;
import org.fhaes.enums.SampleDepthFilterType;
import org.fhaes.preferences.FHAESPreferences.PrefKey;
import org.fhaes.preferences.wrappers.EventTypeWrapper;
import org.fhaes.preferences.wrappers.FireFilterTypeWrapper;
import org.fhaes.preferences.wrappers.SampleDepthFilterTypeWrapper;
import org.fhaes.preferences.wrappers.SpinnerWrapper;
import org.fhaes.util.SharedConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * CompositeFilterDialog Class.
 */
public class CompositeFilterDialog extends JDialog implements ActionListener {
	
	@SuppressWarnings("unused")
	private static final Logger log = LoggerFactory.getLogger(CompositeFilterDialog.class);
	
	private static final long serialVersionUID = 1L;
	private final JPanel contentPanel = new JPanel();
	private JSpinner spnStart;
	private JSpinner spnEnd;
	private JButton btnOK;
	private Boolean allDone = false;
	
	@SuppressWarnings("rawtypes")
	private JComboBox cboStyle;
	@SuppressWarnings("rawtypes")
	private JComboBox cboFilterType;
	private JSpinner spnFilterValue;
	private JSpinner spnMinSamples;
	private JTextArea txtComments;
	private JPanel panelComments;
	@SuppressWarnings("rawtypes")
	private JComboBox cboSampleDepthFilterType;
	private JLabel lblCompositeBasedOn;
	private JComboBox cboEventToProcess;
	
	/**
	 * TODO
	 */
	public CompositeFilterDialog() {
	
		setupGUI();
		this.setCommentsVisible(false);
		pack();
		
	}
	
	/**
	 * TODO
	 * 
	 * @param includeComments
	 */
	public CompositeFilterDialog(boolean includeComments) {
	
		setupGUI();
		this.setCommentsVisible(includeComments);
		pack();
		
	}
	
	/**
	 * Create the dialog.
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void setupGUI() {
	
		// this.setLocationRelativeTo(null);
		this.setTitle("Composite file options");
		this.setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(new MigLayout("", "[grow,right]", "[][][grow]"));
		int thisYear = SharedConstants.CURRENT_YEAR;
		{
			JPanel panel = new JPanel();
			panel.setBorder(new TitledBorder(null, "Year range", TitledBorder.LEADING, TitledBorder.TOP, null, null));
			contentPanel.add(panel, "cell 0 0,grow");
			panel.setLayout(new MigLayout("", "[][][grow]", "[][][]"));
			{
				JLabel lblRange = new JLabel("Range:");
				panel.add(lblRange, "cell 0 0,alignx right");
			}
			{
				cboStyle = new JComboBox();
				panel.add(cboStyle, "cell 1 0 2 1,growx");
				cboStyle.setModel(new DefaultComboBoxModel(new String[] { "All years", "Restricted years" }));
				cboStyle.setActionCommand("Style");
				cboStyle.addActionListener(this);
			}
			{
				JLabel lblStartYear = new JLabel("Start year:");
				panel.add(lblStartYear, "cell 0 1,alignx right");
			}
			{
				spnStart = new JSpinner();
				panel.add(spnStart, "cell 1 1");
				spnStart.setModel(new SpinnerNumberModel(thisYear - 1, null, thisYear - 1, new Integer(1)));
				spnStart.setEditor(new JSpinner.NumberEditor(spnStart, "####"));
				{
					JLabel lblEndYear = new JLabel("End year:");
					panel.add(lblEndYear, "cell 0 2,alignx right");
				}
				{
					spnEnd = new JSpinner();
					panel.add(spnEnd, "cell 1 2");
					spnEnd.setModel(new SpinnerNumberModel(thisYear, null, thisYear, new Integer(1)));
					spnEnd.setEditor(new JSpinner.NumberEditor(spnEnd, "####"));
					spnEnd.addChangeListener(new ChangeListener() {
						
						@Override
						public void stateChanged(ChangeEvent arg0) {
						
							updateGUI();
						}
						
					});
				}
				spnStart.addChangeListener(new ChangeListener() {
					
					@Override
					public void stateChanged(ChangeEvent arg0) {
					
						updateGUI();
					}
					
				});
			}
		}
		{
			JPanel panel = new JPanel();
			panel.setBorder(new TitledBorder(null, "Composite filter", TitledBorder.LEADING, TitledBorder.TOP, null, null));
			contentPanel.add(panel, "cell 0 1,grow");
			panel.setLayout(new MigLayout("", "[][][21.00][70.00]", "[][][]"));
			{
				lblCompositeBasedOn = new JLabel("Composite based on:");
				lblCompositeBasedOn.setEnabled(false);
				panel.add(lblCompositeBasedOn, "cell 0 0,alignx trailing");
			}
			{
				cboEventToProcess = new JComboBox();
				new EventTypeWrapper(cboEventToProcess, PrefKey.COMPOSITE_EVENT_TYPE, EventTypeToProcess.FIRE_EVENT);
				
				// Disable and force to fire events until implemented
				cboEventToProcess.setSelectedItem(EventTypeToProcess.FIRE_EVENT);
				cboEventToProcess.setEnabled(false);
				
				panel.add(cboEventToProcess, "cell 1 0 3 1,growx");
			}
			{
				cboFilterType = new JComboBox();
				panel.add(cboFilterType, "cell 1 1,growx");
				new FireFilterTypeWrapper(cboFilterType, PrefKey.COMPOSITE_FILTER_TYPE, FireFilterType.NUMBER_OF_EVENTS);
				
			}
			{
				JLabel label = new JLabel(">=");
				panel.add(label, "cell 2 1");
			}
			{
				spnFilterValue = new JSpinner();
				spnFilterValue.setModel(new SpinnerNumberModel(new Integer(1), new Integer(1), null, new Integer(1)));
				panel.add(spnFilterValue, "cell 3 1,growx");
			}
			{
				cboSampleDepthFilterType = new JComboBox();
				new SampleDepthFilterTypeWrapper(cboSampleDepthFilterType, PrefKey.COMPOSITE_SAMPLE_DEPTH_TYPE,
						SampleDepthFilterType.MIN_NUM_SAMPLES);
				panel.add(cboSampleDepthFilterType, "cell 1 2,alignx left");
			}
			{
				JLabel label = new JLabel(">=");
				panel.add(label, "cell 2 2");
			}
			{
				spnMinSamples = new JSpinner();
				spnMinSamples.setModel(new SpinnerNumberModel(new Integer(1), new Integer(1), null, new Integer(1)));
				new SpinnerWrapper(spnMinSamples, PrefKey.COMPOSITE_MIN_SAMPLES, 1);
				panel.add(spnMinSamples, "cell 3 2,growx");
			}
		}
		{
			panelComments = new JPanel();
			panelComments.setBorder(new TitledBorder(null, "Comments", TitledBorder.LEADING, TitledBorder.TOP, null, null));
			contentPanel.add(panelComments, "cell 0 2,grow");
			panelComments.setLayout(new BorderLayout(0, 0));
			{
				txtComments = new JTextArea();
				txtComments.setLineWrap(true);
				txtComments.setWrapStyleWord(true);
				panelComments.add(txtComments);
			}
		}
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				btnOK = new JButton("OK");
				btnOK.setActionCommand("OK");
				btnOK.addActionListener(this);
				buttonPane.add(btnOK);
				getRootPane().setDefaultButton(btnOK);
			}
			{
				JButton btnCancel = new JButton("Cancel");
				btnCancel.setActionCommand("Cancel");
				btnCancel.addActionListener(this);
				buttonPane.add(btnCancel);
			}
		}
		
		updateGUI();
		pack();
	}
	
	/**
	 * Set whether the comments field should be visible or not.
	 * 
	 * @param b
	 */
	private void setCommentsVisible(boolean b) {
	
		panelComments.setVisible(b);
		txtComments.setVisible(b);
	}
	
	/**
	 * Update the GUI depending on current selections.
	 */
	private void updateGUI() {
	
		if (cboFilterType.getSelectedItem().equals(FireFilterType.NUMBER_OF_EVENTS))
		{
			
			Number currval = (Number) spnFilterValue.getValue();
			spnFilterValue.setModel(new SpinnerNumberModel(currval.intValue(), new Integer(1), null, new Integer(1)));
		}
		else if (cboFilterType.getSelectedItem().equals(FireFilterType.PERCENTAGE_OF_RECORDING))
		{
			
			Number currval = (Number) spnFilterValue.getValue();
			if (currval.doubleValue() < 0 || currval.doubleValue() > 100)
				currval = 1.0;
			spnFilterValue.setModel(new SpinnerNumberModel(1.0, 0.0, 100.0, 1.0));
		}
		
		spnStart.setEnabled(cboStyle.getSelectedIndex() > 0);
		spnEnd.setEnabled(cboStyle.getSelectedIndex() > 0);
		
		btnOK.setEnabled(true);
		
		if (cboStyle.getSelectedIndex() == 0)
			return;
		
		Integer start = (Integer) spnStart.getValue();
		Integer end = (Integer) spnEnd.getValue();
		
		if (start == 0 || end == 0)
		{
			btnOK.setEnabled(false);
		}
		
		if (start >= end)
		{
			btnOK.setEnabled(false);
		}
	}
	
	@Override
	public void actionPerformed(ActionEvent evt) {
	
		if (evt.getActionCommand().equals("OK"))
		{
			allDone = true;
			this.setVisible(false);
		}
		else if (evt.getActionCommand().equals("Cancel"))
		{
			allDone = false;
			this.setVisible(false);
		}
		else if (evt.getActionCommand().equals("Style"))
		{
			updateGUI();
		}
	}
	
	/**
	 * Get the start year specified by the user. If 'all years' is selected then this will return 0.
	 * 
	 * @return
	 */
	public Integer getStartYear() {
	
		if (cboStyle.getSelectedIndex() == 0)
		{
			// All years selected so we use 0
			return 0;
		}
		else if (allDone)
		{
			return (Integer) spnStart.getValue();
		}
		else
		{
			return null;
		}
	}
	
	/**
	 * Get the end year specified by the user. If 'all years' is selected then this will return 0.
	 * 
	 * @return
	 */
	public Integer getEndYear() {
	
		if (cboStyle.getSelectedIndex() == 0)
		{
			// All years selected so we use 0
			return 0;
		}
		else if (allDone)
		{
			return (Integer) spnEnd.getValue();
		}
		else
		{
			return null;
		}
		
	}
	
	/**
	 * Get the minimum number of samples required for a composite value is returned.
	 * 
	 * @return
	 */
	public Integer getMinNumberOfSamples() {
	
		return (Integer) this.spnMinSamples.getValue();
		
		/*
		 * if (this.cboSampleDepthFilterType.getSelectedItem().equals(SampleDepthFilterType.MIN_NUM_SAMPLES)) { return (Integer)
		 * this.spnMinSamples.getValue(); } else { return null; }
		 */
	}
	
	/**
	 * Get the fire filter value set by the user
	 * 
	 * @return
	 */
	public Double getFireFilterValue() {
	
		return ((Number) spnFilterValue.getValue()).doubleValue();
	}
	
	/**
	 * Get the value of the minimum number of values spinner
	 * 
	 * @return
	 */
	public Integer getMinSamplesValues() {
	
		return (Integer) this.spnMinSamples.getValue();
		
	}
	
	/**
	 * Get the selected fire filter type
	 * 
	 * @return
	 */
	public FireFilterType getFireFilterType() {
	
		return (FireFilterType) cboFilterType.getSelectedItem();
	}
	
	/**
	 * Get the selected minimum sample filter type
	 * 
	 * @return
	 */
	public SampleDepthFilterType getSampleDepthFilterType() {
	
		return (SampleDepthFilterType) this.cboSampleDepthFilterType.getSelectedItem();
	}
	
	/**
	 * Get the comments specified by the user
	 * 
	 * @return
	 */
	public String getComments() {
	
		if (txtComments.isVisible())
		{
			return txtComments.getText();
		}
		else
		{
			return null;
		}
	}
	
	/**
	 * Whether the dialog was successfully completed
	 * 
	 * @return
	 */
	public Boolean success() {
	
		return allDone;
	}
	
}
