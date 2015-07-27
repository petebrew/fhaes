/**************************************************************************************************
 * Fire History Analysis and Exploration System (FHAES), Copyright (C) 2015
 * 
 * Contributors: Elena Velasquez and Peter Brewer
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
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.fhaes.FHRecorder.controller.FileController;
import org.fhaes.enums.AnalysisType;
import org.fhaes.enums.FireFilterType;
import org.fhaes.preferences.FHAESPreferences.PrefKey;
import org.fhaes.preferences.wrappers.AnalysisTypeWrapper;
import org.fhaes.preferences.wrappers.CheckBoxWrapper;
import org.fhaes.preferences.wrappers.FireFilterTypeWrapper;
import org.fhaes.preferences.wrappers.SpinnerWrapper;
import org.fhaes.util.Builder;

import net.miginfocom.swing.MigLayout;

/**
 * IntervalConfigDialog Class. This is the preferences dialog for the interval module.
 * 
 * @author Peter Brewer
 */
public class IntervalConfigDialog extends JDialog implements ActionListener, ChangeListener {
	
	private static final long serialVersionUID = 1L;
	private final JPanel contentPanel = new JPanel();
	private JSpinner spnFirstYear;
	private JSpinner spnLastYear;
	private JButton btnOK;
	private JButton btnCancel;
	private JCheckBox cbxIncludeInjuries;
	private JSpinner spnThreshold;
	private JCheckBox chkAllYears;
	
	/**
	 * Create the dialog.
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public IntervalConfigDialog(Component parent) {
		
		setTitle("Interval Parameters");
		
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(new MigLayout("", "[grow,center]", "[][grow,fill]"));
		{
			JPanel panel_1 = new JPanel();
			panel_1.setBorder(new TitledBorder(new LineBorder(new Color(99, 130, 191)), "Analysis options", TitledBorder.LEADING,
					TitledBorder.TOP, null, null));
			contentPanel.add(panel_1, "cell 0 0,growx");
			panel_1.setLayout(new MigLayout("", "[170px:170px:170px,right][200px,grow]", "[][][15px,center][][][]"));
			{
				JLabel lblCalculateAllYears = new JLabel("Calculate all years?:");
				panel_1.add(lblCalculateAllYears, "cell 0 1");
			}
			{
				chkAllYears = new JCheckBox("");
				new CheckBoxWrapper(chkAllYears, PrefKey.RANGE_CALC_OVER_ALL_YEARS, true);
				
				chkAllYears.addActionListener(new ActionListener() {
					
					@Override
					public void actionPerformed(ActionEvent arg0) {
						
						pingAllYears();
						
					}
					
				});
				chkAllYears.setSelected(true);
				
				panel_1.add(chkAllYears, "cell 1 1");
			}
			{
				JLabel lblYearRange = new JLabel("Year range: ");
				panel_1.add(lblYearRange, "flowy,cell 0 2,alignx right,aligny center");
			}
			JPanel panel = new JPanel();
			panel_1.add(panel, "cell 1 2,growx");
			panel.setLayout(new MigLayout("", "[50px,grow,fill][13px][50px,grow,fill]", "[20px]"));
			spnFirstYear = new JSpinner();
			panel.add(spnFirstYear, "cell 0 0,alignx left,aligny top");
			{
				JLabel lblTo = new JLabel("to");
				panel.add(lblTo, "cell 1 0,alignx left,aligny center");
				lblTo.setFont(new Font("Dialog", Font.PLAIN, 12));
			}
			new SpinnerWrapper(spnFirstYear, PrefKey.RANGE_FIRST_YEAR, 0);
			
			spnLastYear = new JSpinner();
			new SpinnerWrapper(spnLastYear, PrefKey.RANGE_LAST_YEAR, 0);
			spnFirstYear.setEditor(new JSpinner.NumberEditor(spnFirstYear, "####"));
			spnLastYear.setEditor(new JSpinner.NumberEditor(spnLastYear, "####"));
			
			spnFirstYear.setEnabled(false);
			spnLastYear.setEnabled(false);
			
			panel.add(spnLastYear, "cell 2 0,alignx left,aligny top");
			{
				JLabel lblAnalysisType = new JLabel("Analysis type:");
				panel_1.add(lblAnalysisType, "cell 0 3,alignx trailing");
			}
			{
				JComboBox cboAnalysisType = new JComboBox();
				cboAnalysisType.setModel(new DefaultComboBoxModel(AnalysisType.values()));
				new AnalysisTypeWrapper(cboAnalysisType, PrefKey.INTERVALS_ANALYSIS_TYPE, AnalysisType.COMPOSITE);
				panel_1.add(cboAnalysisType, "cell 1 3,growx");
			}
			
			{
				JLabel lblIncludeInjuries = new JLabel("Include other injuries?:");
				panel_1.add(lblIncludeInjuries, "cell 0 4");
			}
			
			{
				cbxIncludeInjuries = new JCheckBox("");
				new CheckBoxWrapper(cbxIncludeInjuries, PrefKey.INTERVALS_INCLUDE_OTHER_INJURIES, false);
				
				panel_1.add(cbxIncludeInjuries, "cell 1 4");
				cbxIncludeInjuries.setSelected(false);
			}
			spnLastYear.addChangeListener(this);
			spnFirstYear.addChangeListener(this);
		}
		{
			{
				{
					JPanel panel = new JPanel();
					panel.setBorder(new TitledBorder(new LineBorder(new Color(99, 130, 191)), "Composite Fire Threshold",
							TitledBorder.LEADING, TitledBorder.TOP, null, null));
					contentPanel.add(panel, "cell 0 1,grow");
					panel.setLayout(new MigLayout("", "[170px:170px:170px,right][grow]", "[][]"));
					{
						JLabel lblFilterType = new JLabel("Filter type:");
						panel.add(lblFilterType, "cell 0 0,alignx trailing");
					}
					{
						JComboBox cboFilterType = new JComboBox();
						cboFilterType.setModel(new DefaultComboBoxModel(FireFilterType.values()));
						new FireFilterTypeWrapper(cboFilterType, PrefKey.COMPOSITE_FILTER_TYPE, FireFilterType.NUMBER_OF_EVENTS);
						panel.add(cboFilterType, "cell 1 0,growx");
					}
					{
						JLabel lblCriteria = new JLabel("Criteria:");
						panel.add(lblCriteria, "cell 0 1");
					}
					{
						JPanel panel_1 = new JPanel();
						panel.add(panel_1, "cell 1 1");
						panel_1.setLayout(new MigLayout("", "[10px][50px]", "[20px]"));
						{
							JLabel label = new JLabel(">");
							panel_1.add(label, "cell 0 0,alignx left,aligny center");
						}
						{
							spnThreshold = new JSpinner();
							new SpinnerWrapper(spnThreshold, PrefKey.COMPOSITE_FILTER_VALUE, 1);
							spnThreshold.setModel(new SpinnerNumberModel(new Integer(1), new Integer(1), null, new Integer(1)));
							
							panel_1.add(spnThreshold, "cell 1 0,growx,aligny top");
						}
					}
				}
			}
		}
		{
			JPanel buttonPane = new JPanel();
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			buttonPane.setLayout(new MigLayout("", "[158px][grow][81px][81px]", "[25px]"));
			{
				JButton btnResetToDefaults = new JButton("Reset to Defaults");
				btnResetToDefaults.setActionCommand("Reset");
				btnResetToDefaults.addActionListener(this);
				buttonPane.add(btnResetToDefaults, "cell 0 0,alignx left,aligny top");
			}
			{
				btnOK = new JButton("OK");
				btnOK.setActionCommand("OK");
				btnOK.addActionListener(this);
				buttonPane.add(btnOK, "cell 2 0,growx,aligny top");
				getRootPane().setDefaultButton(btnOK);
			}
			{
				btnCancel = new JButton("Cancel");
				btnCancel.setActionCommand("Cancel");
				btnCancel.addActionListener(this);
				buttonPane.add(btnCancel, "cell 3 0,growx,aligny top");
			}
		}
		
		pack();
		
		setIconImage(Builder.getApplicationIcon());
		setLocationRelativeTo(parent);
		setModal(true);
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setVisible(true);
		pingAllYears();
		pingAllYears();
	}
	
	private void pingAllYears() {
		
		if (chkAllYears.isSelected())
		{
			spnFirstYear.setValue(0);
			spnLastYear.setValue(0);
			spnFirstYear.setEnabled(false);
			spnLastYear.setEnabled(false);
		}
		else
		{
			spnFirstYear.setValue(FileController.CURRENT_YEAR - 1);
			spnLastYear.setValue(FileController.CURRENT_YEAR);
			spnFirstYear.setEnabled(true);
			spnLastYear.setEnabled(true);
		}
		
	}
	
	private boolean validateChoices() {
		
		boolean ret = true;
		
		if (((Integer) spnFirstYear.getValue()) > ((Integer) spnLastYear.getValue()))
			ret = false;
		;
		
		btnOK.setEnabled(ret);
		
		return ret;
	}
	
	public void setToDefault() {
		
		spnFirstYear.setValue(0);
		spnLastYear.setValue(0);
		this.chkAllYears.setSelected(true);
		this.cbxIncludeInjuries.setSelected(false);
		
		pingAllYears();
		
	}
	
	/**
	 * Save the settings to the application preferences
	 */
	private void setPreferences() {
	
	}
	
	@Override
	public void actionPerformed(ActionEvent evt) {
		
		if (evt.getActionCommand().equals("Cancel"))
		{
			dispose();
		}
		else if (evt.getActionCommand().equals("OK"))
		{
			setPreferences();
			dispose();
		}
		else if (evt.getActionCommand().equals("Reset"))
		{
			setToDefault();
		}
		
	}
	
	@Override
	public void stateChanged(ChangeEvent e) {
		
		validateChoices();
		
	}
	
}
