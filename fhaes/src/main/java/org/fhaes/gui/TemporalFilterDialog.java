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
import javax.swing.SpinnerNumberModel;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.fhaes.fhxrecorder.controller.FileController;

import net.miginfocom.swing.MigLayout;

/**
 * TemporalFilterDialog Class.
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
public class TemporalFilterDialog extends JDialog implements ActionListener {
	
	private static final long serialVersionUID = 1L;
	private final JPanel contentPanel = new JPanel();
	private JSpinner spnStart;
	private JSpinner spnEnd;
	private JButton btnOK;
	private Boolean allDone = false;
	private JComboBox cboStyle;
	
	/**
	 * Create the dialog.
	 */
	public TemporalFilterDialog() {
		
		setBounds(100, 100, 257, 169);
		this.setLocationRelativeTo(null);
		this.setTitle("Choose year range");
		this.setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(new MigLayout("", "[right][grow]", "[][][]"));
		{
			JLabel lblRange = new JLabel("Range:");
			contentPanel.add(lblRange, "cell 0 0,alignx trailing");
		}
		{
			cboStyle = new JComboBox();
			cboStyle.setModel(new DefaultComboBoxModel(new String[] { "All years", "Restricted years" }));
			cboStyle.setActionCommand("Style");
			cboStyle.addActionListener(this);
			
			contentPanel.add(cboStyle, "cell 1 0,growx");
		}
		{
			JLabel lblStartYear = new JLabel("Start year:");
			contentPanel.add(lblStartYear, "cell 0 1");
		}
		int thisyear = FileController.CURRENT_YEAR;
		{
			spnStart = new JSpinner();
			spnStart.setModel(new SpinnerNumberModel(thisyear - 1, null, thisyear - 1, new Integer(1)));
			spnStart.setEditor(new JSpinner.NumberEditor(spnStart, "####"));
			spnStart.addChangeListener(new ChangeListener() {
				
				@Override
				public void stateChanged(ChangeEvent arg0) {
					
					updateGUI();
				}
				
			});
			contentPanel.add(spnStart, "cell 1 1,growx");
		}
		{
			JLabel lblEndYear = new JLabel("End year:");
			contentPanel.add(lblEndYear, "cell 0 2");
		}
		{
			spnEnd = new JSpinner();
			spnEnd.setModel(new SpinnerNumberModel(thisyear, null, thisyear, new Integer(1)));
			spnEnd.setEditor(new JSpinner.NumberEditor(spnEnd, "####"));
			spnEnd.addChangeListener(new ChangeListener() {
				
				@Override
				public void stateChanged(ChangeEvent arg0) {
					
					updateGUI();
				}
				
			});
			contentPanel.add(spnEnd, "cell 1 2,growx");
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
	}
	
	/**
	 * Update the gui depending on current selections
	 * 
	 */
	private void updateGUI() {
		
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
	 * Whether the dialog was successfully completed
	 * 
	 * @return
	 */
	public Boolean success() {
		
		return allDone;
	}
	
}
