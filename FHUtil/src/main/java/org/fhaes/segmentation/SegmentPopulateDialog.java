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
package org.fhaes.segmentation;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.EmptyBorder;

import org.fhaes.util.Builder;

import net.miginfocom.swing.MigLayout;

/**
 * SegmentPopulateDialog Class.
 */
public class SegmentPopulateDialog extends JDialog implements ActionListener {
	
	private static final long serialVersionUID = 1L;
	private final JPanel contentPanel = new JPanel();
	private JSpinner spnLength;
	private JSpinner spnLag;
	private JSpinner spnStartYear;
	private boolean success = false;
	
	/**
	 * Create the dialog.
	 */
	public SegmentPopulateDialog(Component parent, int firstyear) {
		
		this.setModal(true);
		setBounds(100, 100, 281, 140);
		setTitle("Generate segments");
		this.setIconImage(Builder.getApplicationIcon());
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(new MigLayout("", "[right][fill]", "[][][]"));
		{
			JLabel lblStartYear = new JLabel("Start year:");
			contentPanel.add(lblStartYear, "cell 0 0");
		}
		{
			spnStartYear = new JSpinner();
			spnStartYear.setModel(new SpinnerNumberModel(firstyear, firstyear, 2014, 1));
			spnStartYear.setEditor(new JSpinner.NumberEditor(spnStartYear, "####"));
			contentPanel.add(spnStartYear, "cell 1 0");
		}
		{
			JLabel lblSegmentLength = new JLabel("Segment length:");
			contentPanel.add(lblSegmentLength, "cell 0 1");
		}
		{
			spnLength = new JSpinner();
			spnLength.setModel(new SpinnerNumberModel(100, 1, 999, 1));
			contentPanel.add(spnLength, "cell 1 1");
		}
		{
			JLabel lblLag = new JLabel("Lag:");
			contentPanel.add(lblLag, "cell 0 2");
		}
		{
			spnLag = new JSpinner();
			spnLag.setModel(new SpinnerNumberModel(50, 1, 999, 1));
			contentPanel.add(spnLag, "cell 1 2");
		}
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton btnOK = new JButton("OK");
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
		
		pack();
		this.setLocationRelativeTo(parent);
		this.setVisible(true);
	}
	
	public int getLength() {
		
		return (Integer) spnLength.getValue();
	}
	
	public int getLag() {
		
		return (Integer) spnLag.getValue();
	}
	
	public int getStartYear() {
		
		return (Integer) spnStartYear.getValue();
	}
	
	public boolean isSuccessful() {
		
		return success;
	}
	
	@Override
	public void actionPerformed(ActionEvent evt) {
		
		if (evt.getActionCommand().equals("OK"))
		{
			success = true;
			this.setVisible(false);
		}
		else if (evt.getActionCommand().equals("Cancel"))
		{
			this.setVisible(false);
		}
	}
}
