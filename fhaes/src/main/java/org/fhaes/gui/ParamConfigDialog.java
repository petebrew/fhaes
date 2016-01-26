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
import java.awt.Component;
import java.awt.Dimension;
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
import javax.swing.JTabbedPane;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import net.miginfocom.swing.MigLayout;

import org.fhaes.components.HelpTipButton;
import org.fhaes.enums.AnalysisLabelType;
import org.fhaes.enums.AnalysisType;
import org.fhaes.enums.EventTypeToProcess;
import org.fhaes.enums.FireFilterType;
import org.fhaes.enums.NoDataLabel;
import org.fhaes.enums.SampleDepthFilterType;
import org.fhaes.help.LocalHelp;
import org.fhaes.preferences.App;
import org.fhaes.preferences.FHAESPreferences.PrefKey;
import org.fhaes.preferences.wrappers.AnalysisLabelTypeWrapper;
import org.fhaes.preferences.wrappers.AnalysisTypeWrapper;
import org.fhaes.preferences.wrappers.CheckBoxWrapper;
import org.fhaes.preferences.wrappers.DoubleSpinnerWrapper;
import org.fhaes.preferences.wrappers.EventTypeWrapper;
import org.fhaes.preferences.wrappers.FireFilterTypeWrapper;
import org.fhaes.preferences.wrappers.NoDataLabelWrapper;
import org.fhaes.preferences.wrappers.SampleDepthFilterTypeWrapper;
import org.fhaes.preferences.wrappers.SpinnerWrapper;
import org.fhaes.util.Builder;
import org.fhaes.util.I18n;

/**
 * ParamConfigDialog Class. This is the preferences dialog for the analysis modules.
 * 
 * @author Peter Brewer
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
public class ParamConfigDialog extends JDialog implements ActionListener, ChangeListener {
	
	private static final long serialVersionUID = 1L;
	private final JPanel contentPanel = new JPanel();
	
	private JCheckBox cbxDormantFirst;
	private JCheckBox cbxEarlyEarlyFirst;
	private JCheckBox cbxMiddleEarlyFirst;
	private JCheckBox cbxLateEarlyFirst;
	private JCheckBox cbxLateFirst;
	private JCheckBox cbxDormantSecond;
	private JCheckBox cbxEarlyEarlySecond;
	private JCheckBox cbxMiddleEarlySecond;
	private JCheckBox cbxLateEarlySecond;
	private JCheckBox cbxLateSecond;
	private JSpinner spnFirstYear;
	private JSpinner spnLastYear;
	private JButton btnOK;
	private JButton btnCancel;
	private JPanel panel;
	private JCheckBox cbxAllYears;
	private JLabel lblNewLabel;
	private JPanel panel_1;
	private JLabel lblGT1;
	private JSpinner spnFilterValue;
	private JPanel panelAnalysisOptions;
	private JLabel lblIntervalAnalysisType;
	private JComboBox cboAnalysisType;
	private JLabel lblEventType;
	private JComboBox cboEventType;
	private JPanel panelCompositeFilter;
	private JLabel lblYearRange;
	private JCheckBox cbxIncludeIncomplete;
	private JComboBox cboFilterType;
	private JLabel lblLabelResultsBy;
	private JComboBox cboLabelType;
	private Boolean preferencesChanged = false;
	private JLabel lblMinYearOverlap;
	private JSpinner spnOverlap;
	private JTabbedPane tabbedPane;
	private JPanel tabSeasons;
	private JPanel tabAnalysisOptions;
	private JPanel tabFiltersAndYears;
	private HelpTipButton btnHelpOverlap;
	private HelpTipButton btnHelpLabelBy;
	private HelpTipButton btnHelpEventType;
	private HelpTipButton btnHelpIncludeExtraInterval;
	private HelpTipButton btnHelpAnalysisType;
	private HelpTipButton btnHelpAllYears;
	private HelpTipButton btnHelpThreshold;
	private HelpTipButton label;
	private HelpTipButton label_1;
	private JPanel panelCommon;
	private JPanel panelInterval;
	private JPanel panelMatrix;
	private JLabel lblAlphaLevel;
	private JSpinner spnAlphaLevel;
	private JCheckBox chkDontRemind;
	private HelpTipButton helpTipButton;
	private JLabel lblLabelNoData;
	private JComboBox cboNoDataValue;
	private HelpTipButton helpTipButton_1;
	private JLabel lblComp;
	private JComboBox cboSampleDepthFilterType;
	private JLabel lblGT2;
	private JSpinner spnMinSamples;
	private HelpTipButton btnHelpSampleDepthFilterType;
	private JLabel lblCompositeBasedOn;
	
	/**
	 * Create the dialog.
	 */
	public ParamConfigDialog(Component parent) {
	
		setTitle(I18n.getText("paramconfig.title"));
		
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(new MigLayout("", "[106.00,grow]", "[grow]"));
		{
			tabbedPane = new JTabbedPane(JTabbedPane.TOP);
			contentPanel.add(tabbedPane, "cell 0 0,grow");
			{
				tabAnalysisOptions = new JPanel();
				tabbedPane.addTab(I18n.getText("paramconfig.analysisoptions"), null, tabAnalysisOptions, null);
				tabAnalysisOptions.setLayout(new BorderLayout(0, 0));
				{
					panelAnalysisOptions = new JPanel();
					tabAnalysisOptions.add(panelAnalysisOptions);
					panelAnalysisOptions.setBorder(null);
					panelAnalysisOptions.setLayout(new MigLayout("", "[175px,grow]", "[][59.00][80.00][grow]"));
					{
						panelCommon = new JPanel();
						panelCommon.setBorder(new TitledBorder(null, "Common options", TitledBorder.LEADING, TitledBorder.TOP, null, null));
						panelAnalysisOptions.add(panelCommon, "cell 0 0,grow");
						panelCommon.setLayout(new MigLayout("", "[178px:178px,right][grow,fill][]", "[][]"));
						{
							lblEventType = new JLabel("Event type for analysis:");
							panelCommon.add(lblEventType, "cell 0 0");
						}
						{
							cboEventType = new JComboBox();
							panelCommon.add(cboEventType, "cell 1 0");
							cboEventType.setModel(new DefaultComboBoxModel(EventTypeToProcess.values()));
							new EventTypeWrapper(cboEventType, PrefKey.EVENT_TYPE_TO_PROCESS, EventTypeToProcess.FIRE_EVENT);
							cboEventType.addActionListener(this);
							cboEventType.setActionCommand("EventTypeChanged");
						}
						btnHelpEventType = new HelpTipButton(LocalHelp.EVENT_TYPE);
						panelCommon.add(btnHelpEventType, "cell 2 0");
						{
							lblLabelResultsBy = new JLabel("Label results by:");
							panelCommon.add(lblLabelResultsBy, "cell 0 1");
						}
						{
							cboLabelType = new JComboBox();
							panelCommon.add(cboLabelType, "cell 1 1");
							cboLabelType.setModel(new DefaultComboBoxModel(AnalysisLabelType.values()));
							new AnalysisLabelTypeWrapper(cboLabelType, PrefKey.ANALYSIS_LABEL_TYPE, AnalysisLabelType.INPUT_FILENAME);
						}
						{
							btnHelpLabelBy = new HelpTipButton(LocalHelp.LABEL_SAMPLES_BY);
							panelCommon.add(btnHelpLabelBy, "cell 2 1");
						}
					}
					{
						panelInterval = new JPanel();
						panelInterval.setBorder(new TitledBorder(null, "Interval options", TitledBorder.LEADING, TitledBorder.TOP, null,
								null));
						panelAnalysisOptions.add(panelInterval, "cell 0 1,grow");
						panelInterval.setLayout(new MigLayout("", "[178px:178px,right][grow,fill][]", "[][][][][37.00]"));
						{
							lblIntervalAnalysisType = new JLabel("Interval analysis type:");
							panelInterval.add(lblIntervalAnalysisType, "cell 0 0");
						}
						{
							cboAnalysisType = new JComboBox();
							panelInterval.add(cboAnalysisType, "cell 1 0");
							cboAnalysisType.setActionCommand("AnalysisType");
							cboAnalysisType.setModel(new DefaultComboBoxModel(AnalysisType.values()));
							new AnalysisTypeWrapper(cboAnalysisType, PrefKey.INTERVALS_ANALYSIS_TYPE, AnalysisType.COMPOSITE);
							btnHelpAnalysisType = new HelpTipButton(LocalHelp.ANALYSIS_TYPE);
							panelInterval.add(btnHelpAnalysisType, "cell 2 0");
							{
								cbxIncludeIncomplete = new JCheckBox("Include interval after last event");
								panelInterval.add(cbxIncludeIncomplete, "cell 1 1");
								new CheckBoxWrapper(cbxIncludeIncomplete, PrefKey.INTERVALS_INCLUDE_OTHER_INJURIES, false);
							}
							{
								btnHelpIncludeExtraInterval = new HelpTipButton(LocalHelp.INCLUDE_EXTRA_INTERVAL);
								panelInterval.add(btnHelpIncludeExtraInterval, "cell 2 1");
							}
							{
								lblAlphaLevel = new JLabel("Alpha level:");
								panelInterval.add(lblAlphaLevel, "cell 0 3");
							}
							{
								spnAlphaLevel = new JSpinner();
								spnAlphaLevel.setModel(new SpinnerNumberModel(0.125, 0.001, 0.999, 0.001));
								new DoubleSpinnerWrapper(spnAlphaLevel, PrefKey.INTERVALS_ALPHA_LEVEL, 0.125);
								panelInterval.add(spnAlphaLevel, "cell 1 3");
							}
							{
								helpTipButton = new HelpTipButton(LocalHelp.ALPHA_LEVEL);
								panelInterval.add(helpTipButton, "cell 2 3");
							}
							{
								panelMatrix = new JPanel();
								panelMatrix.setBorder(new TitledBorder(null, "Matrix options", TitledBorder.LEADING, TitledBorder.TOP,
										null, null));
								panelAnalysisOptions.add(panelMatrix, "cell 0 2,grow");
								panelMatrix.setLayout(new MigLayout("", "[178px:178px,right][grow,fill][]", "[][]"));
								
								{
									lblMinYearOverlap = new JLabel("Common years required:");
									lblMinYearOverlap.setEnabled(false);
									panelMatrix.add(lblMinYearOverlap, "cell 0 0");
								}
								{
									spnOverlap = new JSpinner();
									spnOverlap.setEnabled(false);
									panelMatrix.add(spnOverlap, "cell 1 0");
									spnOverlap.setModel(new SpinnerNumberModel(new Integer(25), new Integer(5), null, new Integer(1)));
									new SpinnerWrapper(spnOverlap, PrefKey.RANGE_OVERLAP_REQUIRED, 25);
								}
								btnHelpOverlap = new HelpTipButton(LocalHelp.COMMON_YEARS);
								btnHelpOverlap.setEnabled(false);
								panelMatrix.add(btnHelpOverlap, "cell 2 0");
								{
									lblLabelNoData = new JLabel("Label no data with:");
									lblLabelNoData.setEnabled(false);
									panelMatrix.add(lblLabelNoData, "cell 0 1,alignx trailing");
								}
								{
									cboNoDataValue = new JComboBox();
									cboNoDataValue.setEnabled(false);
									cboNoDataValue.setModel(new DefaultComboBoxModel(NoDataLabel.values()));
									new NoDataLabelWrapper(cboNoDataValue, PrefKey.MATRIX_NO_DATA_LABEL, NoDataLabel.NAN);
									panelMatrix.add(cboNoDataValue, "cell 1 1,growx");
								}
								{
									helpTipButton_1 = new HelpTipButton(
											"Choose how 'no data' values should be reported in matrix output.  This value is returned for instance when two files do not have enough common years between them for the matrix analysis to be performed.");
									helpTipButton_1.setEnabled(false);
									panelMatrix.add(helpTipButton_1, "cell 2 1");
								}
							}
							cboAnalysisType.addActionListener(this);
						}
					}
				}
			}
			tabFiltersAndYears = new JPanel();
			tabbedPane.addTab("Filters and Years", null, tabFiltersAndYears, null);
			tabFiltersAndYears.setLayout(new MigLayout("", "[grow,fill]", "[][fill][grow]"));
			JPanel panelRange = new JPanel();
			tabFiltersAndYears.add(panelRange, "cell 0 0");
			panelRange.setBorder(new TitledBorder(null, "Year range", TitledBorder.LEADING, TitledBorder.TOP, null, null));
			panelRange.setLayout(new MigLayout("", "[160px:160.00][159px,grow,fill]", "[][]"));
			panel_1 = new JPanel();
			panelRange.add(panel_1, "cell 1 0,grow");
			panel_1.setLayout(new MigLayout("", "[159px,grow]", "[]"));
			{
				lblNewLabel = new JLabel("Calculate across all years:");
				panel_1.add(lblNewLabel, "flowx,cell 0 0");
			}
			cbxAllYears = new JCheckBox("");
			panel_1.add(cbxAllYears, "cell 0 0");
			cbxAllYears.setSelected(true);
			cbxAllYears.setActionCommand("AllYearsCheckbox");
			new CheckBoxWrapper(cbxAllYears, PrefKey.RANGE_CALC_OVER_ALL_YEARS, true);
			btnHelpAllYears = new HelpTipButton(LocalHelp.RANGE_CALC_ALL_YEARS);
			panel_1.add(btnHelpAllYears, "cell 0 0");
			cbxAllYears.addActionListener(this);
			{
				lblYearRange = new JLabel("Time period:");
				panelRange.add(lblYearRange, "cell 0 1,alignx right,aligny center");
			}
			panel = new JPanel();
			panelRange.add(panel, "cell 1 1,alignx left,aligny top");
			panel.setLayout(new MigLayout("", "[90px,grow,fill][15px][90px,grow,fill]", "[20px]"));
			spnFirstYear = new JSpinner();
			spnFirstYear.setEditor(new JSpinner.NumberEditor(spnFirstYear, "####"));
			
			panel.add(spnFirstYear, "cell 0 0,alignx left,aligny top");
			{
				JLabel lblTo = new JLabel("to");
				panel.add(lblTo, "cell 1 0,alignx left,aligny center");
			}
			spnLastYear = new JSpinner();
			spnLastYear.setEditor(new JSpinner.NumberEditor(spnLastYear, "####"));
			
			panel.add(spnLastYear, "flowx,cell 2 0,alignx left,aligny top");
			panelCompositeFilter = new JPanel();
			tabFiltersAndYears.add(panelCompositeFilter, "cell 0 1");
			panelCompositeFilter.setBorder(new TitledBorder(null, "Composite filters", TitledBorder.LEADING, TitledBorder.TOP, null, null));
			panelCompositeFilter.setLayout(new MigLayout("", "[160px:160,right][fill][][grow,fill][]", "[][][]"));
			{
				lblComp = new JLabel("Composite based on:");
				panelCompositeFilter.add(lblComp, "cell 0 0,alignx trailing,aligny top");
			}
			{
				lblCompositeBasedOn = new JLabel("<html><b>Fire events</b><br><font size=-2><i>[change on analysis options tab]</i>");
				lblCompositeBasedOn.setFont(new Font("Dialog", Font.PLAIN, 12));
				panelCompositeFilter.add(lblCompositeBasedOn, "cell 1 0 4 1");
			}
			{
				cboFilterType = new JComboBox();
				panelCompositeFilter.add(cboFilterType, "cell 1 1");
				cboFilterType.setModel(new DefaultComboBoxModel(FireFilterType.values()));
				new FireFilterTypeWrapper(cboFilterType, PrefKey.COMPOSITE_FILTER_TYPE, FireFilterType.NUMBER_OF_EVENTS);
			}
			{
				lblGT1 = new JLabel(">=");
				panelCompositeFilter.add(lblGT1, "cell 2 1");
			}
			{
				spnFilterValue = new JSpinner();
				panelCompositeFilter.add(spnFilterValue, "cell 3 1");
				spnFilterValue.setModel(new SpinnerNumberModel(new Integer(1), new Integer(1), null, new Integer(1)));
				new SpinnerWrapper(spnFilterValue, PrefKey.COMPOSITE_FILTER_VALUE, 1);
			}
			btnHelpThreshold = new HelpTipButton(LocalHelp.COMPOSITE_FILTER_THRESHOLD);
			panelCompositeFilter.add(btnHelpThreshold, "cell 4 1");
			{
				cboSampleDepthFilterType = new JComboBox();
				new SampleDepthFilterTypeWrapper(cboSampleDepthFilterType, PrefKey.COMPOSITE_SAMPLE_DEPTH_TYPE,
						SampleDepthFilterType.MIN_NUM_SAMPLES);
				panelCompositeFilter.add(cboSampleDepthFilterType, "cell 1 2,growx");
			}
			{
				lblGT2 = new JLabel(">=");
				panelCompositeFilter.add(lblGT2, "cell 2 2");
			}
			{
				spnMinSamples = new JSpinner();
				spnMinSamples.setModel(new SpinnerNumberModel(new Integer(1), new Integer(1), null, new Integer(1)));
				new SpinnerWrapper(spnMinSamples, PrefKey.COMPOSITE_MIN_SAMPLES, 1);
				panelCompositeFilter.add(spnMinSamples, "cell 3 2");
			}
			{
				btnHelpSampleDepthFilterType = new HelpTipButton(LocalHelp.COMPOSITE_FILTER_SAMPLE_DEPTH_FILTER_TYPE);
				panelCompositeFilter.add(btnHelpSampleDepthFilterType, "cell 4 2");
			}
			spnLastYear.addChangeListener(this);
			spnFirstYear.addChangeListener(this);
			{
				tabSeasons = new JPanel();
				tabbedPane.addTab("Seasons", null, tabSeasons, null);
				tabSeasons.setLayout(new MigLayout("", "[grow,fill][grow,fill]", "[fill][grow]"));
				{
					JPanel panelFirstSeason = new JPanel();
					tabSeasons.add(panelFirstSeason, "cell 0 0");
					panelFirstSeason.setBorder(new TitledBorder(null, "First season combination", TitledBorder.LEADING, TitledBorder.TOP,
							null, null));
					panelFirstSeason.setLayout(new MigLayout("", "[][grow]", "[][][][][]"));
					{
						cbxDormantFirst = new JCheckBox("Dormant");
						cbxDormantFirst.setSelected(true);
						cbxDormantFirst.addActionListener(this);
						cbxDormantFirst.setActionCommand("DormantFirst");
						panelFirstSeason.add(cbxDormantFirst, "cell 0 0");
					}
					{
						cbxEarlyEarlyFirst = new JCheckBox("Early earlywood ");
						cbxEarlyEarlyFirst.setSelected(true);
						cbxEarlyEarlyFirst.addActionListener(this);
						{
							label = new HelpTipButton("");
							panelFirstSeason.add(label, "cell 1 0,alignx right");
						}
						cbxEarlyEarlyFirst.setActionCommand("EarlyEarlyFirst");
						panelFirstSeason.add(cbxEarlyEarlyFirst, "cell 0 1");
					}
					{
						cbxMiddleEarlyFirst = new JCheckBox("Middle earlywood ");
						cbxMiddleEarlyFirst.setSelected(true);
						cbxMiddleEarlyFirst.addActionListener(this);
						cbxMiddleEarlyFirst.setActionCommand("MiddleEarlyFirst");
						panelFirstSeason.add(cbxMiddleEarlyFirst, "cell 0 2");
					}
					{
						cbxLateEarlyFirst = new JCheckBox("Late earlywood ");
						cbxLateEarlyFirst.addActionListener(this);
						cbxLateEarlyFirst.setActionCommand("LateEarlyFirst");
						panelFirstSeason.add(cbxLateEarlyFirst, "cell 0 3");
					}
					{
						cbxLateFirst = new JCheckBox("Latewood ");
						cbxLateFirst.addActionListener(this);
						cbxLateFirst.setActionCommand("LateFirst");
						panelFirstSeason.add(cbxLateFirst, "cell 0 4");
					}
				}
				{
					JPanel panelSecondSeason = new JPanel();
					tabSeasons.add(panelSecondSeason, "cell 1 0");
					panelSecondSeason.setBorder(new TitledBorder(null, "Second season combination", TitledBorder.LEADING, TitledBorder.TOP,
							null, null));
					panelSecondSeason.setLayout(new MigLayout("", "[][grow]", "[][][][][]"));
					{
						cbxDormantSecond = new JCheckBox("Dormant ");
						cbxDormantSecond.addActionListener(this);
						cbxDormantSecond.setActionCommand("DormantSecond");
						
						panelSecondSeason.add(cbxDormantSecond, "cell 0 0");
					}
					{
						cbxEarlyEarlySecond = new JCheckBox("Early earlywood ");
						cbxEarlyEarlySecond.addActionListener(this);
						{
							label_1 = new HelpTipButton("");
							panelSecondSeason.add(label_1, "cell 1 0,alignx right");
						}
						cbxEarlyEarlySecond.setActionCommand("EarlyEarlySecond");
						
						panelSecondSeason.add(cbxEarlyEarlySecond, "cell 0 1");
					}
					{
						cbxMiddleEarlySecond = new JCheckBox("Middle earlywood");
						cbxMiddleEarlySecond.addActionListener(this);
						cbxMiddleEarlySecond.setActionCommand("MiddleEarlySecond");
						
						panelSecondSeason.add(cbxMiddleEarlySecond, "cell 0 2");
					}
					{
						cbxLateEarlySecond = new JCheckBox("Late earlywood ");
						cbxLateEarlySecond.setSelected(true);
						cbxLateEarlySecond.addActionListener(this);
						cbxLateEarlySecond.setActionCommand("LateEarlySecond");
						
						panelSecondSeason.add(cbxLateEarlySecond, "cell 0 3");
					}
					{
						cbxLateSecond = new JCheckBox("Latewood ");
						cbxLateSecond.setSelected(true);
						cbxLateSecond.addActionListener(this);
						cbxLateSecond.setActionCommand("LateSecond");
						
						panelSecondSeason.add(cbxLateSecond, "cell 0 4");
					}
				}
			}
		}
		{
			JPanel buttonPane = new JPanel();
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			buttonPane.setLayout(new MigLayout("", "[158px][grow][81px][81px]", "[][25px]"));
			{
				JButton btnResetToDefaults = new JButton("Reset to Defaults");
				btnResetToDefaults.setActionCommand("Reset");
				btnResetToDefaults.addActionListener(this);
				{
					chkDontRemind = new JCheckBox("Use these choices without confirmation for the rest of the session");
					new CheckBoxWrapper(chkDontRemind, PrefKey.DONT_REQUEST_PARAM_CONFIRMATION, false);
					buttonPane.add(chkDontRemind, "cell 0 0 4 1");
				}
				buttonPane.add(btnResetToDefaults, "cell 0 1,alignx left,aligny top");
			}
			{
				btnOK = new JButton("Apply");
				btnOK.setActionCommand("OK");
				btnOK.addActionListener(this);
				buttonPane.add(btnOK, "cell 2 1,growx,aligny top");
				getRootPane().setDefaultButton(btnOK);
			}
			{
				btnCancel = new JButton("Cancel");
				btnCancel.setActionCommand("Cancel");
				btnCancel.addActionListener(this);
				buttonPane.add(btnCancel, "cell 3 1,growx,aligny top");
			}
		}
		
		setFromPreferences();
		
		setMinimumSize(new Dimension(640, 500));
		pack();
		setIconImage(Builder.getApplicationIcon());
		setLocationRelativeTo(parent);
		setModal(true);
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setVisible(true);
	}
	
	/**
	 * Only enable composite filters if composite type has been chosen
	 * 
	 */
	private void showHideFilterGUI() {
	
		Boolean enableFilters = false;
		if (cboAnalysisType.getSelectedItem().equals(AnalysisType.COMPOSITE))
		{
			enableFilters = true;
		}
		
		cboFilterType.setEnabled(enableFilters);
		cboSampleDepthFilterType.setEnabled(enableFilters);
		spnFilterValue.setEnabled(enableFilters);
		spnMinSamples.setEnabled(enableFilters);
		lblCompositeBasedOn.setEnabled(enableFilters);
		lblComp.setEnabled(enableFilters);
		btnHelpSampleDepthFilterType.setEnabled(enableFilters);
		btnHelpThreshold.setEnabled(enableFilters);
		lblGT1.setEnabled(enableFilters);
		lblGT2.setEnabled(enableFilters);
	}
	
	public Boolean havePreferencesChanged() {
	
		return preferencesChanged;
	}
	
	/**
	 * Validate the choices made by the user and change the GUI accordingly
	 * 
	 * @return
	 */
	private boolean validateChoices() {
	
		showHideFilterGUI();
		boolean ret = true;
		if (cbxDormantFirst.isSelected() && cbxEarlyEarlyFirst.isSelected() && cbxMiddleEarlyFirst.isSelected()
				&& cbxLateEarlyFirst.isSelected() && cbxLateFirst.isSelected())
		{
			ret = false;
		}
		if (!cbxDormantFirst.isSelected() && !cbxEarlyEarlyFirst.isSelected() && !cbxMiddleEarlyFirst.isSelected()
				&& !cbxLateEarlyFirst.isSelected() && !cbxLateFirst.isSelected())
		{
			ret = false;
		}
		
		if (((Integer) spnFirstYear.getValue()) > ((Integer) spnLastYear.getValue()))
			ret = false;
		;
		
		if (cbxAllYears.isSelected())
		{
			spnFirstYear.setValue(0);
			spnLastYear.setValue(0);
		}
		
		spnFirstYear.setEnabled(!cbxAllYears.isSelected());
		spnLastYear.setEnabled(!cbxAllYears.isSelected());
		this.lblYearRange.setEnabled(!cbxAllYears.isSelected());
		
		this.lblCompositeBasedOn.setText("<html><b>" + ((EventTypeToProcess) this.cboEventType.getSelectedItem()).toString()
				+ "</b><br><font size=\"-2\"><i>[change on analysis tab]</i>");
		
		btnOK.setEnabled(ret);
		
		return ret;
	}
	
	/**
	 * Set GUI components to their default values
	 */
	public void setToDefault() {
	
		cbxDormantFirst.setSelected(true);
		cbxEarlyEarlyFirst.setSelected(true);
		cbxMiddleEarlyFirst.setSelected(true);
		cbxLateEarlyFirst.setSelected(false);
		cbxLateFirst.setSelected(false);
		cbxDormantSecond.setSelected(false);
		cbxEarlyEarlySecond.setSelected(false);
		cbxMiddleEarlySecond.setSelected(false);
		cbxLateEarlySecond.setSelected(true);
		cbxLateSecond.setSelected(true);
		spnFirstYear.setValue(0);
		spnLastYear.setValue(0);
		cbxAllYears.setSelected(true);
		cboAnalysisType.setSelectedItem(AnalysisType.SAMPLE);
		cbxIncludeIncomplete.setSelected(false);
		cboEventType.setSelectedItem(EventTypeToProcess.FIRE_EVENT);
		cboFilterType.setSelectedItem(FireFilterType.NUMBER_OF_EVENTS);
		cboSampleDepthFilterType.setSelectedItem(SampleDepthFilterType.MIN_NUM_SAMPLES);
		spnMinSamples.setValue(1);
		spnFilterValue.setValue(1);
		cboLabelType.setSelectedItem(AnalysisLabelType.INPUT_FILENAME);
		spnAlphaLevel.setValue(0.125);
		cboNoDataValue.setSelectedItem(NoDataLabel.NAN);
		validateChoices();
	}
	
	/**
	 * Save the settings to the application preferences
	 */
	private void saveToPreferences() {
	
		App.prefs.setIntPref(PrefKey.RANGE_FIRST_YEAR, (Integer) spnFirstYear.getValue());
		App.prefs.setIntPref(PrefKey.RANGE_LAST_YEAR, (Integer) spnLastYear.getValue());
		
		App.prefs.setBooleanPref(PrefKey.SEASONALITY_FIRST_GROUP_DORMANT, cbxDormantFirst.isSelected());
		App.prefs.setBooleanPref(PrefKey.SEASONALITY_FIRST_GROUP_EARLY_EARLY, cbxEarlyEarlyFirst.isSelected());
		App.prefs.setBooleanPref(PrefKey.SEASONALITY_FIRST_GROUP_MIDDLE_EARLY, cbxMiddleEarlyFirst.isSelected());
		App.prefs.setBooleanPref(PrefKey.SEASONALITY_FIRST_GROUP_LATE_EARLY, cbxLateEarlyFirst.isSelected());
		App.prefs.setBooleanPref(PrefKey.SEASONALITY_FIRST_GROUP_LATE, cbxLateFirst.isSelected());
		
		App.prefs.setBooleanPref(PrefKey.SEASONALITY_SECOND_GROUP_DORMANT, cbxDormantSecond.isSelected());
		App.prefs.setBooleanPref(PrefKey.SEASONALITY_SECOND_GROUP_EARLY_EARLY, cbxEarlyEarlySecond.isSelected());
		App.prefs.setBooleanPref(PrefKey.SEASONALITY_SECOND_GROUP_MIDDLE_EARLY, cbxMiddleEarlySecond.isSelected());
		App.prefs.setBooleanPref(PrefKey.SEASONALITY_SECOND_GROUP_LATE_EARLY, cbxLateEarlySecond.isSelected());
		App.prefs.setBooleanPref(PrefKey.SEASONALITY_SECOND_GROUP_LATE, cbxLateSecond.isSelected());
		
		App.prefs.setEventTypePref(PrefKey.EVENT_TYPE_TO_PROCESS, (EventTypeToProcess) this.cboEventType.getSelectedItem());
		
		preferencesChanged = true;
		
	}
	
	private void setFromPreferences() {
	
		cbxDormantFirst.setSelected(App.prefs.getBooleanPref(PrefKey.SEASONALITY_FIRST_GROUP_DORMANT, true));
		cbxEarlyEarlyFirst.setSelected(App.prefs.getBooleanPref(PrefKey.SEASONALITY_FIRST_GROUP_EARLY_EARLY, true));
		cbxMiddleEarlyFirst.setSelected(App.prefs.getBooleanPref(PrefKey.SEASONALITY_FIRST_GROUP_MIDDLE_EARLY, true));
		cbxLateEarlyFirst.setSelected(App.prefs.getBooleanPref(PrefKey.SEASONALITY_FIRST_GROUP_LATE_EARLY, false));
		cbxLateFirst.setSelected(App.prefs.getBooleanPref(PrefKey.SEASONALITY_FIRST_GROUP_LATE, false));
		
		cbxDormantSecond.setSelected(App.prefs.getBooleanPref(PrefKey.SEASONALITY_SECOND_GROUP_DORMANT, false));
		cbxEarlyEarlySecond.setSelected(App.prefs.getBooleanPref(PrefKey.SEASONALITY_SECOND_GROUP_EARLY_EARLY, false));
		cbxMiddleEarlySecond.setSelected(App.prefs.getBooleanPref(PrefKey.SEASONALITY_SECOND_GROUP_MIDDLE_EARLY, false));
		cbxLateEarlySecond.setSelected(App.prefs.getBooleanPref(PrefKey.SEASONALITY_SECOND_GROUP_LATE_EARLY, true));
		cbxLateSecond.setSelected(App.prefs.getBooleanPref(PrefKey.SEASONALITY_SECOND_GROUP_LATE, true));
		
		spnFirstYear.setValue(App.prefs.getIntPref(PrefKey.RANGE_FIRST_YEAR, 0));
		spnLastYear.setValue(App.prefs.getIntPref(PrefKey.RANGE_LAST_YEAR, 0));
		
		validateChoices();
	}
	
	@Override
	public void actionPerformed(ActionEvent evt) {
	
		if (evt.getActionCommand().equals("Cancel"))
		{
			setVisible(false);
		}
		else if (evt.getActionCommand().equals("OK"))
		{
			saveToPreferences();
			setVisible(false);
		}
		else if (evt.getActionCommand().equals("Reset"))
		{
			setToDefault();
		}
		else if (evt.getActionCommand().equals("AllYearsCheckbox"))
		{
			validateChoices();
			
		}
		else if (evt.getActionCommand().equals("AnalysisType"))
		{
			showHideFilterGUI();
		}
		
		// Make season selectors mutually exclusive
		else if (evt.getActionCommand().equals("DormantFirst"))
		{
			cbxDormantSecond.setSelected(!cbxDormantFirst.isSelected());
			validateChoices();
		}
		else if (evt.getActionCommand().equals("DormantSecond"))
		{
			cbxDormantFirst.setSelected(!cbxDormantSecond.isSelected());
			validateChoices();
		}
		else if (evt.getActionCommand().equals("EarlyEarlyFirst"))
		{
			cbxEarlyEarlySecond.setSelected(!cbxEarlyEarlyFirst.isSelected());
			validateChoices();
		}
		else if (evt.getActionCommand().equals("EarlyEarlySecond"))
		{
			cbxEarlyEarlyFirst.setSelected(!cbxEarlyEarlySecond.isSelected());
			validateChoices();
		}
		
		else if (evt.getActionCommand().equals("MiddleEarlyFirst"))
		{
			cbxMiddleEarlySecond.setSelected(!cbxMiddleEarlyFirst.isSelected());
			validateChoices();
		}
		else if (evt.getActionCommand().equals("MiddleEarlySecond"))
		{
			cbxMiddleEarlyFirst.setSelected(!cbxMiddleEarlySecond.isSelected());
			validateChoices();
		}
		
		else if (evt.getActionCommand().equals("LateEarlyFirst"))
		{
			cbxLateEarlySecond.setSelected(!cbxLateEarlyFirst.isSelected());
			validateChoices();
		}
		else if (evt.getActionCommand().equals("LateEarlySecond"))
		{
			cbxLateEarlyFirst.setSelected(!cbxLateEarlySecond.isSelected());
			validateChoices();
		}
		
		else if (evt.getActionCommand().equals("LateFirst"))
		{
			cbxLateSecond.setSelected(!cbxLateFirst.isSelected());
			validateChoices();
		}
		else if (evt.getActionCommand().equals("LateSecond"))
		{
			cbxLateFirst.setSelected(!cbxLateSecond.isSelected());
			validateChoices();
		}
		else if (evt.getActionCommand().equals("EventTypeChanged"))
		{
			validateChoices();
		}
	}
	
	@Override
	public void stateChanged(ChangeEvent e) {
	
		validateChoices();
		
	}
	
}
