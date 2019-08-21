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
package org.fhaes.neofhchart;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.fhaes.components.BCADYearSpinner;
import org.fhaes.components.HelpTipButton;
import org.fhaes.enums.EventTypeToProcess;
import org.fhaes.enums.FireFilterType;
import org.fhaes.enums.JustificationType;
import org.fhaes.enums.LabelOrientation;
import org.fhaes.enums.LineStyle;
import org.fhaes.enums.SampleDepthFilterType;
import org.fhaes.help.LocalHelp;
import org.fhaes.neofhchart.ChartActions.SeriesSortType;
import org.fhaes.preferences.App;
import org.fhaes.preferences.FHAESPreferences.PrefKey;
import org.fhaes.preferences.wrappers.CheckBoxWrapper;
import org.fhaes.preferences.wrappers.EventTypeWrapper;
import org.fhaes.preferences.wrappers.SampleDepthFilterTypeWrapper;
import org.fhaes.util.Builder;
import org.fhaes.util.FontChooserComboBox;
import org.fhaes.util.SharedConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.miginfocom.swing.MigLayout;

/**
 * ChartPropertiesDialog Class.
 */
public class ChartPropertiesDialog extends JDialog implements ActionListener {
	
	private static final Logger log = LoggerFactory.getLogger(ChartPropertiesDialog.class);
	private static final long serialVersionUID = 1L;
	
	// Declare GUI components
	private final JPanel contentPanel = new JPanel();
	private JLabel lblShowChartTitle;
	private JCheckBox chkShowChartTitle;
	private JLabel lblShowLegend;
	private JCheckBox chkShowLegend;
	private JTextField txtAxisY1Label;
	private JTextField txtAxisY2Label;
	private FontChooserComboBox cboFontFamily;
	private BCADYearSpinner firstYearSpinner;
	private BCADYearSpinner lastYearSpinner;
	private JButton btnVerticalGuideColor;
	private JSpinner spnVerticalGuideWeight;
	private JComboBox<LineStyle> cboVerticalGuideStyle;
	private JSpinner spnMajorSpacing;
	private JCheckBox chkVerticalGuides;
	private JCheckBox chkAutoAdjustRange;
	private JCheckBox chkIndexPlot;
	private JSpinner spnIndexPlotHeight;
	private JRadioButton chkSampleDepth;
	private JButton btnSampleDepthColor;
	private JRadioButton chkRecorderDepth;
	private JCheckBox chkPercentScarred;
	private JButton btnPercentScarredColor;
	private JCheckBox chkSampleThreshold;
	private JButton btnThresholdColor;
	private JSpinner spnAxisY1FontSize;
	private JSpinner spnAxisY2FontSize;
	private JSpinner spnThresholdValue;
	private JCheckBox chkChronologyPlot;
	private JCheckBox chkSeriesLabels;
	private JSpinner spnSeriesLabelFontSize;
	private JSpinner spnSeriesSpacing;
	private JCheckBox chkShowPith;
	private JCheckBox chkShowBark;
	private JCheckBox chkShowInnerRing;
	private JCheckBox chkShowOuterRing;
	private JCheckBox chkShowFireEvents;
	private JCheckBox chkShowInjuryEvents;
	private JCheckBox chkCompositePlot;
	private JSpinner spnCompositePlotHeight;
	private JCheckBox chkCompositeYearLabels;
	private JComboBox<FireFilterType> cboFilterType;
	private JSpinner spnFilterValue;
	private JSpinner spnMinNumberOfSamples;
	private NeoFHChart neoFHChart;
	private JCheckBox chkMajorTicks;
	private JCheckBox chkMinorTicks;
	private JSpinner spnMinorSpacing;
	private JSpinner spnCompositeYearLabelFontSize;
	private JTextField txtComposite;
	private JSpinner spnCompositePlotLabelFontSize;
	private JRadioButton radShortYearStyle;
	private JList<Integer> lstHightlightYears;
	private JComboBox<LineStyle> cboHighlightStyle;
	private JCheckBox chkHighlightYears;
	private JSpinner spnHighlightWeight;
	private JButton btnHighlightColor;
	private JButton btnAddYear;
	private JButton btnRemoveYear;
	private JPanel panelIndexPlotGeneral;
	private JPanel panelIndexPlotComponents;
	private JPanel panelIndexPlotAxisY1;
	private JPanel panelIndexPlotAxisY2;
	private JPanel panelChronoPlotSymbols;
	private JPanel panelChronoPlotSeries;
	private JPanel panelCompositePlotYearLabels;
	private JPanel panelCompositePlotFilters;
	private JPanel panelCompositePlotGeneral;
	private YearListModel highlightYearsModel;
	private JRadioButton radLongYearStyle;
	private JLabel lblLabelStyle;
	private JLabel lblLabelPadding;
	private JSpinner spnYearLabelPadding;
	private JLabel lblPx;
	private JLabel lblLabelOrientation;
	private JComboBox<LabelOrientation> cboLabelOrientation;
	private JLabel lblChartTitleFontSize;
	private JSpinner spnChartTitleFontSize;
	private JLabel lblUseDefaultChartTitle;
	private JCheckBox chkUseDefaultName;
	private JLabel lblChartTitleOverrideText;
	private JTextField chartTitleOverrideText;
	private JPanel panelChronoPlotCategories;
	private JCheckBox chkShowCategoryGroups;
	private JCheckBox chkShowCategoryLabels;
	
	// Declare local variables
	private boolean preferencesChanged;
	private JLabel lblAutoAdjustRange;
	private JLabel lblTimelineFontSize;
	private JSpinner spnTimelineFontSize;
	private JLabel lblShowHideChronoLabels;
	private JLabel lblShowPith;
	private JLabel lblShowBark;
	private JLabel lblShowInnerRing;
	private JLabel lblShowOuterRing;
	private JLabel lblShowFireEvents;
	private JLabel lblShowInjuryEvents;
	private JLabel lblShowCategoryGroups;
	private JLabel lblShowCategoryLabels;
	private JLabel lblAutomaticallyColorizeSeries;
	private JLabel lblAutomaticallyColorizeLabels;
	private JCheckBox chkAutomaticallyColorizeSeries;
	private JCheckBox chkAutomaticallyColorizeLabels;
	private JLabel lblCategoryLabelFontSize;
	private JSpinner spnCategoryLabelFontSize;
	private JLabel lblCategoryLabelJustification;
	private JComboBox<JustificationType> cboCategoryLabelJustification;
	private JLabel lblPt_3;
	private JLabel label_1;
	@SuppressWarnings("rawtypes")
	private JComboBox cboSampleDepthFilterType;
	private JLabel lblCompositeBasedOn;
	@SuppressWarnings("rawtypes")
	private JComboBox cboEventToProcess;
	private JCheckBox chkShowFilterInLegend;
	private HelpTipButton helpTipButton;
	private HelpTipButton htbShowChartTitle;
	private HelpTipButton htbDefaultChartTitle;
	private HelpTipButton htbFontFamily;
	private HelpTipButton htbFontSize;
	private HelpTipButton htbChartTitle;
	private HelpTipButton htbAutoRange;
	private HelpTipButton htbTimelineFontSize;
	private HelpTipButton htbYearRange;
	private HelpTipButton htbShowVerticalGuides;
	private HelpTipButton htbShowMajorTicks;
	private HelpTipButton htbShowMinorTicks;
	private HelpTipButton htbMajorSpacing;
	private HelpTipButton htbMinorSpacing;
	private HelpTipButton htbVerticalGuideStyle;
	private HelpTipButton htbShowHighlightedYears;
	private HelpTipButton htbHighlightedYearStyle;
	private JPanel panel_1;
	private HelpTipButton htbIndexPlotHeight;
	private HelpTipButton htbSampleOrRecorderDepth;
	private HelpTipButton htbShowPercentScarred;
	private HelpTipButton htbShowDepthThreshold;
	private HelpTipButton htbDepthThresholdValue;
	private HelpTipButton htbY1AxisLabel;
	private HelpTipButton htbY1FontSize;
	private HelpTipButton htbY2AxisLabel;
	private HelpTipButton htbY2FontSize;
	private HelpTipButton htbShowIndexPlot;
	private HelpTipButton htbShowChronologyPlot;
	private HelpTipButton htbShowSeriesLabels;
	private HelpTipButton htbSeriesLabelsFontSize;
	private HelpTipButton htbSeriesSpacing;
	private HelpTipButton htbShowSymbols;
	private HelpTipButton htbShowCategoryGroups;
	private HelpTipButton htbShowCategoryLabels;
	private HelpTipButton htbCategoryLabelFontSize;
	private HelpTipButton htbCategoryLabelJustification;
	private HelpTipButton htbAutoColorizeCategoryLines;
	private HelpTipButton htbAutorColorizeCategoryLabels;
	private HelpTipButton htbShowCompositePlot;
	private HelpTipButton htbCompositePlotHeight;
	private HelpTipButton htbCompositePlotFontSize;
	private HelpTipButton htbCompositePlotTitle;
	private HelpTipButton htbCompositeBasedOn;
	private HelpTipButton htbCompositeFilter1;
	private HelpTipButton htbCompositeFilter2;
	private HelpTipButton htbShowCompositeFilterInLegend;
	private HelpTipButton htbShowYearLabels;
	private HelpTipButton htbYearLabelStyle;
	private HelpTipButton htbYearLabelPadding;
	private HelpTipButton htbYearLabelFontSize;
	private HelpTipButton htbYearLabelOrientation;
	private JLabel lblSortSamplesBy;
	private JComboBox<SeriesSortType> cboSortType;
	private HelpTipButton htbSortSamples;
	private JLabel lblIndexLineWidth;
	private JSpinner spnPercentScarredLineWidth;
	private JCheckBox chkRememberChartPrefs;
	
	/**
	 * Create the dialog.
	 */
	public ChartPropertiesDialog(NeoFHChart neoFHChart) {
		
		this.neoFHChart = neoFHChart;
		initGUI();
	}
	
	/**
	 * TODO
	 * 
	 * @param parent
	 * @param neoFHChart
	 */
	public static void showDialog(Component parent, NeoFHChart neoFHChart) {
		
		App.prefs.setSilentMode(true);
		ChartPropertiesDialog dialog = new ChartPropertiesDialog(neoFHChart);
		dialog.setLocationRelativeTo(parent);
		dialog.setVisible(true);
		
		if (dialog.havePreferencesChanged() && neoFHChart.currentChart != null)
		{
			App.prefs.setSilentMode(false);
			
			neoFHChart.redrawChart();
		}
		App.prefs.setSilentMode(false);
		
	}
	
	/**
	 * TODO
	 */
	private void setXAxisGui() {
		
		if (this.chkAutoAdjustRange.isSelected())
		{
			this.firstYearSpinner.setEnabled(false);
			this.lastYearSpinner.setEnabled(false);
		}
		else
		{
			this.firstYearSpinner.setEnabled(true);
			this.lastYearSpinner.setEnabled(true);
		}
	}
	
	/**
	 * TODO
	 */
	@Override
	public void actionPerformed(ActionEvent evt) {
		
		if (evt.getActionCommand().equals("OK"))
		{
			saveToPreferences();
			dispose();
		}
		else if (evt.getActionCommand().equals("Cancel"))
		{
			dispose();
		}
		else if (evt.getActionCommand().equals("Reset"))
		{
			setChartPreferencesToDefaults();
			setFromPreferences();
		}
		else if (evt.getActionCommand().equals("VerticalGuides"))
		{
			this.setVerticalGuideGUI();
		}
		else if (evt.getActionCommand().equals("HighlightYears"))
		{
			this.setHighlightYearsGUI();
		}
		else if (evt.getActionCommand().equals("IndexPlot"))
		{
			this.setIndexPlotGUI();
		}
		else if (evt.getActionCommand().equals("ChronologyPlot"))
		{
			this.setChronologyPlotGUI();
		}
		else if (evt.getActionCommand().equals("CompositePlot"))
		{
			this.setCompositePlotGUI();
		}
		else if (evt.getActionCommand().equals("CompositeYearLabels"))
		{
			this.setCompositeYearLabelsGUI();
		}
		else if (evt.getActionCommand().equals("AddHighlightYear"))
		{
			JSpinner spnYear = new JSpinner();
			JSpinner.NumberEditor editor = new JSpinner.NumberEditor(spnYear, "#");
			spnYear.setEditor(editor);
			
			int result = JOptionPane.showConfirmDialog(this, spnYear, "Highlight Year", JOptionPane.OK_CANCEL_OPTION,
					JOptionPane.QUESTION_MESSAGE);
			if (result == JOptionPane.OK_OPTION)
			{
				int highlightyear = (int) spnYear.getValue();
				highlightYearsModel.addYear(highlightyear);
				highlightYearsModel.sort();
			}
		}
		else if (evt.getActionCommand().equals("RemoveHighlightYear"))
		{
			this.highlightYearsModel.removeYearAtIndex(this.lstHightlightYears.getSelectedIndex());
		}
	}
	
	/**
	 * TODO
	 * 
	 * @return
	 */
	public boolean havePreferencesChanged() {
		
		return preferencesChanged;
	}
	
	/**
	 * Try and set the YAxis label to match the depth type, but only do so if the user hasn't already overridden with their own text.
	 */
	private void tryAutoSetYAxisLabel() {
		
		String text = this.txtAxisY1Label.getText();
		if (text.toLowerCase().equals("recorder depth") || text.toLowerCase().equals("sample depth"))
		{
			if (this.chkSampleDepth.isSelected())
			{
				txtAxisY1Label.setText("Sample Depth");
			}
			else
			{
				txtAxisY1Label.setText("Recorder Depth");
			}
		}
		
	}
	
	/**
	 * Enable/disable GUI based components based on selections.
	 */
	private void setVerticalGuideGUI() {
		
		cboVerticalGuideStyle.setEnabled(chkVerticalGuides.isSelected());
		spnVerticalGuideWeight.setEnabled(chkVerticalGuides.isSelected());
		btnVerticalGuideColor.setEnabled(chkVerticalGuides.isSelected());
	}
	
	/**
	 * Enable/disable GUI based components based on selections.
	 */
	private void setHighlightYearsGUI() {
		
		cboHighlightStyle.setEnabled(chkHighlightYears.isSelected());
		spnHighlightWeight.setEnabled(chkHighlightYears.isSelected());
		btnHighlightColor.setEnabled(chkHighlightYears.isSelected());
		lstHightlightYears.setEnabled(chkHighlightYears.isSelected());
		btnAddYear.setEnabled(chkHighlightYears.isSelected());
		btnRemoveYear.setEnabled(chkHighlightYears.isSelected());
	}
	
	/**
	 * Enable/disable GUI based components based on selections.
	 */
	private void setIndexPlotGUI() {
		
		setEnablePanelComponents(panelIndexPlotGeneral, chkIndexPlot.isSelected());
		setEnablePanelComponents(panelIndexPlotComponents, chkIndexPlot.isSelected());
		setEnablePanelComponents(panelIndexPlotAxisY1, chkIndexPlot.isSelected());
		setEnablePanelComponents(panelIndexPlotAxisY2, chkIndexPlot.isSelected());
		lstHightlightYears.setEnabled(chkIndexPlot.isSelected());
		
	}
	
	/**
	 * Enable/disable GUI based components based on selections.
	 */
	private void setChronologyPlotGUI() {
		
		setEnablePanelComponents(panelChronoPlotSeries, chkChronologyPlot.isSelected());
		setEnablePanelComponents(panelChronoPlotSymbols, chkChronologyPlot.isSelected());
		
	}
	
	/**
	 * Enable/disable GUI based components based on selections.
	 */
	private void setCompositePlotGUI() {
		
		setEnablePanelComponents(panelCompositePlotFilters, chkCompositePlot.isSelected());
		setEnablePanelComponents(panelCompositePlotGeneral, chkCompositePlot.isSelected());
		setEnablePanelComponents(panelCompositePlotYearLabels, chkCompositePlot.isSelected());
	}
	
	/**
	 * Enable/disable GUI based components based on selections.
	 */
	private void setCompositeYearLabelsGUI() {
		
		if (!this.chkCompositeYearLabels.isEnabled())
			return;
		
		radShortYearStyle.setEnabled(chkCompositeYearLabels.isSelected());
		radLongYearStyle.setEnabled(chkCompositeYearLabels.isSelected());
		spnCompositeYearLabelFontSize.setEnabled(chkCompositeYearLabels.isSelected());
		spnYearLabelPadding.setEnabled(chkCompositeYearLabels.isSelected());
		cboLabelOrientation.setEnabled(chkCompositeYearLabels.isSelected());
		lblPx.setEnabled(chkCompositeYearLabels.isSelected());
	}
	
	/**
	 * TODO
	 * 
	 * @param panel
	 * @param enable
	 */
	private void setEnablePanelComponents(JPanel panel, boolean enable) {
		
		if (panel == null)
			return;
		if (panel.getComponents().length == 0)
			return;
		
		panel.setEnabled(enable);
		
		for (Component component : panel.getComponents())
		{
			component.setEnabled(enable);
		}
	}
	
	/**
	 * TODO
	 * 
	 * @return
	 */
	public JCheckBox getChkCompositeYearLabels() {
		
		return chkCompositeYearLabels;
	}
	
	/**
	 * TODO
	 * 
	 * @param component
	 * @param background
	 */
	public static void setComponentColours(Component component, Color background) {
		
		component.setBackground(background);
		component.setForeground(getContrastingLabelColour(background));
	}
	
	/**
	 * TODO
	 * 
	 * @param newcolor
	 * @return
	 */
	public static Color getContrastingLabelColour(Color newcolor) {
		
		double brightness = 0.299 * newcolor.getRed() + 0.587 * newcolor.getGreen() + 0.114 * newcolor.getBlue();
		
		if (brightness > 125.0)
		{
			return Color.BLACK;
		}
		
		return Color.WHITE;
		
	}
	
	/**
	 * Return all the chart properties to their default values.
	 */
	public static void setChartPreferencesToDefaults() {
		
		// Clear preferences first then set all to default
		
		// General tab
		App.prefs.clearPref(PrefKey.CHART_SHOW_CHART_TITLE);
		App.prefs.clearPref(PrefKey.CHART_TITLE_FONT_SIZE);
		App.prefs.clearPref(PrefKey.CHART_TITLE_USE_DEFAULT_NAME);
		App.prefs.clearPref(PrefKey.CHART_TITLE_OVERRIDE_VALUE);
		App.prefs.clearPref(PrefKey.CHART_SHOW_LEGEND);
		App.prefs.clearPref(PrefKey.CHART_FONT_FAMILY);
		App.prefs.clearPref(PrefKey.CHART_AXIS_X_AUTO_RANGE);
		App.prefs.clearPref(PrefKey.CHART_AXIS_X_MIN);
		App.prefs.clearPref(PrefKey.CHART_AXIS_X_MAX);
		App.prefs.clearPref(PrefKey.CHART_TIMELINE_FONT_SIZE);
		App.prefs.clearPref(PrefKey.CHART_VERTICAL_GUIDES);
		App.prefs.clearPref(PrefKey.CHART_VERTICAL_GUIDE_COLOR);
		App.prefs.clearPref(PrefKey.CHART_VERTICAL_GUIDE_WEIGHT);
		App.prefs.clearPref(PrefKey.CHART_VERTICAL_GUIDE_STYLE);
		App.prefs.clearPref(PrefKey.CHART_XAXIS_MAJOR_TICK_SPACING);
		App.prefs.clearPref(PrefKey.CHART_XAXIS_MINOR_TICK_SPACING);
		App.prefs.clearPref(PrefKey.CHART_XAXIS_MAJOR_TICKS);
		App.prefs.clearPref(PrefKey.CHART_XAXIS_MINOR_TICKS);
		App.prefs.clearPref(PrefKey.CHART_HIGHLIGHT_YEARS);
		App.prefs.clearPref(PrefKey.CHART_HIGHLIGHT_YEAR_STYLE);
		App.prefs.clearPref(PrefKey.CHART_HIGHLIGHT_YEARS_COLOR);
		App.prefs.clearPref(PrefKey.CHART_HIGHLIGHT_YEARS_WEIGHT);
		App.prefs.clearPref(PrefKey.CHART_HIGHLIGHT_YEARS_ARRAY);
		App.prefs.clearPref(PrefKey.CHART_FONT_BOLD);
		App.prefs.clearPref(PrefKey.CHART_FONT_ITALIC);
		
		// Index plot tab
		App.prefs.clearPref(PrefKey.CHART_SHOW_INDEX_PLOT);
		App.prefs.clearPref(PrefKey.CHART_INDEX_PLOT_HEIGHT);
		App.prefs.clearPref(PrefKey.CHART_SHOW_SAMPLE_DEPTH);
		App.prefs.clearPref(PrefKey.CHART_SAMPLE_OR_RECORDER_DEPTH_COLOR);
		App.prefs.clearPref(PrefKey.CHART_SHOW_PERCENT_SCARRED);
		App.prefs.clearPref(PrefKey.CHART_PERCENT_SCARRED_COLOR);
		App.prefs.clearPref(PrefKey.CHART_SHOW_DEPTH_THRESHOLD);
		App.prefs.clearPref(PrefKey.CHART_DEPTH_THRESHOLD_COLOR);
		App.prefs.clearPref(PrefKey.CHART_DEPTH_THRESHOLD_VALUE);
		App.prefs.clearPref(PrefKey.CHART_AXIS_Y1_LABEL);
		App.prefs.clearPref(PrefKey.CHART_AXIS_Y2_LABEL);
		App.prefs.clearPref(PrefKey.CHART_AXIS_Y1_FONT_SIZE);
		App.prefs.clearPref(PrefKey.CHART_AXIS_Y2_FONT_SIZE);
		
		// Chronology plot tab
		App.prefs.clearPref(PrefKey.CHART_SHOW_CHRONOLOGY_PLOT);
		App.prefs.clearPref(PrefKey.CHART_SHOW_CHRONOLOGY_PLOT_LABELS);
		App.prefs.clearPref(PrefKey.CHART_CHRONOLOGY_PLOT_LABEL_FONT_SIZE);
		App.prefs.clearPref(PrefKey.CHART_CHRONOLOGY_PLOT_SPACING);
		App.prefs.clearPref(PrefKey.CHART_SHOW_PITH_SYMBOL);
		App.prefs.clearPref(PrefKey.CHART_SHOW_BARK_SYMBOL);
		App.prefs.clearPref(PrefKey.CHART_SHOW_INNER_RING_SYMBOL);
		App.prefs.clearPref(PrefKey.CHART_SHOW_OUTER_RING_SYMBOL);
		App.prefs.clearPref(PrefKey.CHART_SHOW_FIRE_EVENT_SYMBOL);
		App.prefs.clearPref(PrefKey.CHART_SHOW_INJURY_SYMBOL);
		App.prefs.clearPref(PrefKey.CHART_SHOW_CATEGORY_GROUPS);
		App.prefs.clearPref(PrefKey.CHART_SHOW_CATEGORY_LABELS);
		App.prefs.clearPref(PrefKey.CHART_CATEGORY_LABEL_FONT_SIZE);
		App.prefs.clearPref(PrefKey.CHART_CATEGORY_LABEL_JUSTIFICATION);
		App.prefs.clearPref(PrefKey.CHART_AUTOMATICALLY_COLORIZE_SERIES);
		App.prefs.clearPref(PrefKey.CHART_AUTOMATICALLY_COLORIZE_LABELS);
		App.prefs.clearPref(PrefKey.CHART_SORT_BY_PREFERENCE);
		
		// Composite plot tab
		App.prefs.clearPref(PrefKey.CHART_SHOW_COMPOSITE_PLOT);
		App.prefs.clearPref(PrefKey.CHART_COMPOSITE_HEIGHT);
		App.prefs.clearPref(PrefKey.CHART_SHOW_COMPOSITE_YEAR_LABELS);
		App.prefs.clearPref(PrefKey.CHART_COMPOSITE_FILTER_TYPE);
		App.prefs.clearPref(PrefKey.CHART_COMPOSITE_FILTER_VALUE);
		App.prefs.clearPref(PrefKey.CHART_COMPOSITE_MIN_NUM_SAMPLES);
		App.prefs.clearPref(PrefKey.CHART_COMPOSITE_YEAR_LABEL_FONT_SIZE);
		App.prefs.clearPref(PrefKey.CHART_COMPOSITE_LABEL_TEXT);
		App.prefs.clearPref(PrefKey.CHART_COMPOSITE_PLOT_LABEL_FONT_SIZE);
		App.prefs.clearPref(PrefKey.CHART_COMPOSITE_YEAR_LABELS_TWO_DIGIT);
		App.prefs.clearPref(PrefKey.CHART_COMPOSITE_YEAR_LABEL_BUFFER);
		App.prefs.clearPref(PrefKey.CHART_COMPOSITE_LABEL_ALIGNMENT);
	}
	
	/**
	 * Save the chart properties to the preferences.
	 */
	private void saveToPreferences() {
		
		// General tab
		App.prefs.setBooleanPref(PrefKey.CHART_SHOW_CHART_TITLE, chkShowChartTitle.isSelected());
		App.prefs.setIntPref(PrefKey.CHART_TITLE_FONT_SIZE, (Integer) spnChartTitleFontSize.getValue());
		App.prefs.setBooleanPref(PrefKey.CHART_TITLE_USE_DEFAULT_NAME, chkUseDefaultName.isSelected());
		App.prefs.setPref(PrefKey.CHART_TITLE_OVERRIDE_VALUE, chartTitleOverrideText.getText());
		App.prefs.setBooleanPref(PrefKey.CHART_SHOW_LEGEND, chkShowLegend.isSelected());
		App.prefs.setPref(PrefKey.CHART_FONT_FAMILY, cboFontFamily.getSelectedFontName());
		App.prefs.setBooleanPref(PrefKey.CHART_AXIS_X_AUTO_RANGE, chkAutoAdjustRange.isSelected());
		App.prefs.setIntPref(PrefKey.CHART_AXIS_X_MIN, (Integer) firstYearSpinner.getValue());
		App.prefs.setIntPref(PrefKey.CHART_AXIS_X_MAX, (Integer) lastYearSpinner.getValue());
		App.prefs.setIntPref(PrefKey.CHART_TIMELINE_FONT_SIZE, (Integer) spnTimelineFontSize.getValue());
		App.prefs.setBooleanPref(PrefKey.CHART_VERTICAL_GUIDES, chkVerticalGuides.isSelected());
		App.prefs.setColorPref(PrefKey.CHART_VERTICAL_GUIDE_COLOR, btnVerticalGuideColor.getBackground());
		App.prefs.setIntPref(PrefKey.CHART_VERTICAL_GUIDE_WEIGHT, (Integer) spnVerticalGuideWeight.getValue());
		App.prefs.setLineStylePref(PrefKey.CHART_VERTICAL_GUIDE_STYLE, (LineStyle) cboVerticalGuideStyle.getSelectedItem());
		App.prefs.setIntPref(PrefKey.CHART_XAXIS_MAJOR_TICK_SPACING, (Integer) spnMajorSpacing.getValue());
		App.prefs.setIntPref(PrefKey.CHART_XAXIS_MINOR_TICK_SPACING, (Integer) spnMinorSpacing.getValue());
		App.prefs.setBooleanPref(PrefKey.CHART_XAXIS_MAJOR_TICKS, chkMajorTicks.isSelected());
		App.prefs.setBooleanPref(PrefKey.CHART_XAXIS_MINOR_TICKS, chkMinorTicks.isSelected());
		App.prefs.setBooleanPref(PrefKey.CHART_HIGHLIGHT_YEARS, chkHighlightYears.isSelected());
		App.prefs.setLineStylePref(PrefKey.CHART_HIGHLIGHT_YEAR_STYLE, (LineStyle) cboHighlightStyle.getSelectedItem());
		App.prefs.setIntPref(PrefKey.CHART_HIGHLIGHT_YEARS_WEIGHT, (int) spnHighlightWeight.getValue());
		App.prefs.setColorPref(PrefKey.CHART_HIGHLIGHT_YEARS_COLOR, btnHighlightColor.getBackground());
		App.prefs.setIntegerArrayPref(PrefKey.CHART_HIGHLIGHT_YEARS_ARRAY, highlightYearsModel.getAllYears());
		App.prefs.setBooleanPref(PrefKey.CHART_SHOW_LEGEND, this.chkShowLegend.isSelected());
		
		// Index plot tab
		App.prefs.setBooleanPref(PrefKey.CHART_SHOW_INDEX_PLOT, chkIndexPlot.isSelected());
		App.prefs.setIntPref(PrefKey.CHART_INDEX_PLOT_HEIGHT, (Integer) spnIndexPlotHeight.getValue());
		App.prefs.setIntPref(PrefKey.CHART_INDEX_PERCENT_SCARRED_LINE_WIDTH, (Integer) spnPercentScarredLineWidth.getValue());
		
		App.prefs.setBooleanPref(PrefKey.CHART_SHOW_SAMPLE_DEPTH, chkSampleDepth.isSelected());
		App.prefs.setColorPref(PrefKey.CHART_SAMPLE_OR_RECORDER_DEPTH_COLOR, btnSampleDepthColor.getBackground());
		App.prefs.setBooleanPref(PrefKey.CHART_SHOW_PERCENT_SCARRED, chkPercentScarred.isSelected());
		App.prefs.setColorPref(PrefKey.CHART_PERCENT_SCARRED_COLOR, btnPercentScarredColor.getBackground());
		App.prefs.setBooleanPref(PrefKey.CHART_SHOW_DEPTH_THRESHOLD, chkSampleThreshold.isSelected());
		App.prefs.setColorPref(PrefKey.CHART_DEPTH_THRESHOLD_COLOR, btnThresholdColor.getBackground());
		App.prefs.setIntPref(PrefKey.CHART_DEPTH_THRESHOLD_VALUE, (Integer) spnThresholdValue.getValue());
		App.prefs.setPref(PrefKey.CHART_AXIS_Y1_LABEL, txtAxisY1Label.getText());
		App.prefs.setPref(PrefKey.CHART_AXIS_Y2_LABEL, txtAxisY2Label.getText());
		App.prefs.setIntPref(PrefKey.CHART_AXIS_Y1_FONT_SIZE, (Integer) spnAxisY1FontSize.getValue());
		App.prefs.setIntPref(PrefKey.CHART_AXIS_Y2_FONT_SIZE, (Integer) spnAxisY2FontSize.getValue());
		
		// Chronology plot tab
		App.prefs.setBooleanPref(PrefKey.CHART_SHOW_CHRONOLOGY_PLOT, chkChronologyPlot.isSelected());
		App.prefs.setBooleanPref(PrefKey.CHART_SHOW_CHRONOLOGY_PLOT_LABELS, chkSeriesLabels.isSelected());
		App.prefs.setIntPref(PrefKey.CHART_CHRONOLOGY_PLOT_LABEL_FONT_SIZE, (Integer) spnSeriesLabelFontSize.getValue());
		App.prefs.setIntPref(PrefKey.CHART_CHRONOLOGY_PLOT_SPACING, (Integer) spnSeriesSpacing.getValue());
		App.prefs.setBooleanPref(PrefKey.CHART_SHOW_PITH_SYMBOL, chkShowPith.isSelected());
		App.prefs.setBooleanPref(PrefKey.CHART_SHOW_BARK_SYMBOL, chkShowBark.isSelected());
		App.prefs.setBooleanPref(PrefKey.CHART_SHOW_INNER_RING_SYMBOL, chkShowInnerRing.isSelected());
		App.prefs.setBooleanPref(PrefKey.CHART_SHOW_OUTER_RING_SYMBOL, chkShowOuterRing.isSelected());
		App.prefs.setBooleanPref(PrefKey.CHART_SHOW_FIRE_EVENT_SYMBOL, chkShowFireEvents.isSelected());
		App.prefs.setBooleanPref(PrefKey.CHART_SHOW_INJURY_SYMBOL, chkShowInjuryEvents.isSelected());
		App.prefs.setBooleanPref(PrefKey.CHART_SHOW_CATEGORY_GROUPS, chkShowCategoryGroups.isSelected());
		App.prefs.setBooleanPref(PrefKey.CHART_SHOW_CATEGORY_LABELS, chkShowCategoryLabels.isSelected());
		App.prefs.setIntPref(PrefKey.CHART_CATEGORY_LABEL_FONT_SIZE, (Integer) spnCategoryLabelFontSize.getValue());
		App.prefs.setJustificationTypePref(PrefKey.CHART_CATEGORY_LABEL_JUSTIFICATION,
				(JustificationType) cboCategoryLabelJustification.getSelectedItem());
		App.prefs.setBooleanPref(PrefKey.CHART_AUTOMATICALLY_COLORIZE_SERIES, chkAutomaticallyColorizeSeries.isSelected());
		App.prefs.setBooleanPref(PrefKey.CHART_AUTOMATICALLY_COLORIZE_LABELS, chkAutomaticallyColorizeLabels.isSelected());
		String b = ((SeriesSortType) cboSortType.getSelectedItem()).toString();
		App.prefs.setPref(PrefKey.CHART_SORT_BY_PREFERENCE, b);
		
		// Composite plot tab
		App.prefs.setBooleanPref(PrefKey.CHART_SHOW_COMPOSITE_PLOT, chkCompositePlot.isSelected());
		App.prefs.setIntPref(PrefKey.CHART_COMPOSITE_HEIGHT, (Integer) spnCompositePlotHeight.getValue());
		App.prefs.setBooleanPref(PrefKey.CHART_SHOW_COMPOSITE_YEAR_LABELS, chkCompositeYearLabels.isSelected());
		App.prefs.setFireFilterTypePref(PrefKey.CHART_COMPOSITE_FILTER_TYPE, (FireFilterType) cboFilterType.getSelectedItem());
		App.prefs.setIntPref(PrefKey.CHART_COMPOSITE_FILTER_VALUE, (Integer) spnFilterValue.getValue());
		App.prefs.setIntPref(PrefKey.CHART_COMPOSITE_MIN_NUM_SAMPLES, (Integer) spnMinNumberOfSamples.getValue());
		App.prefs.setIntPref(PrefKey.CHART_COMPOSITE_YEAR_LABEL_FONT_SIZE, (Integer) spnCompositeYearLabelFontSize.getValue());
		App.prefs.setPref(PrefKey.CHART_COMPOSITE_LABEL_TEXT, txtComposite.getText());
		App.prefs.setIntPref(PrefKey.CHART_COMPOSITE_PLOT_LABEL_FONT_SIZE, (Integer) spnCompositePlotLabelFontSize.getValue());
		App.prefs.setBooleanPref(PrefKey.CHART_COMPOSITE_YEAR_LABELS_TWO_DIGIT, radShortYearStyle.isSelected());
		App.prefs.setIntPref(PrefKey.CHART_COMPOSITE_YEAR_LABEL_BUFFER, (Integer) spnYearLabelPadding.getValue());
		App.prefs.setLabelOrientationPref(PrefKey.CHART_COMPOSITE_LABEL_ALIGNMENT,
				(LabelOrientation) cboLabelOrientation.getSelectedItem());
		
		this.preferencesChanged = true;
	}
	
	/**
	 * Set the form items to match the values currently in the preferences. If no preference has previously been set then use the default
	 * value.
	 */
	private void setFromPreferences() {
		
		// General tab
		chkShowChartTitle.setSelected(App.prefs.getBooleanPref(PrefKey.CHART_SHOW_CHART_TITLE, true));
		spnChartTitleFontSize.setValue(App.prefs.getIntPref(PrefKey.CHART_TITLE_FONT_SIZE, 20));
		chkUseDefaultName.setSelected(App.prefs.getBooleanPref(PrefKey.CHART_TITLE_USE_DEFAULT_NAME, true));
		chartTitleOverrideText.setEnabled(!App.prefs.getBooleanPref(PrefKey.CHART_TITLE_USE_DEFAULT_NAME, true));
		chartTitleOverrideText.setText(App.prefs.getPref(PrefKey.CHART_TITLE_OVERRIDE_VALUE, "Fire Chart"));
		chkShowLegend.setSelected(App.prefs.getBooleanPref(PrefKey.CHART_SHOW_LEGEND, true));
		cboFontFamily.setSelectedItem(App.prefs.getPref(PrefKey.CHART_FONT_FAMILY, "Verdana"));
		chkAutoAdjustRange.setSelected(App.prefs.getBooleanPref(PrefKey.CHART_AXIS_X_AUTO_RANGE, true));
		firstYearSpinner.setValue(App.prefs.getIntPref(PrefKey.CHART_AXIS_X_MIN, 1900));
		lastYearSpinner.setValue(App.prefs.getIntPref(PrefKey.CHART_AXIS_X_MAX, 2000));
		spnTimelineFontSize.setValue(App.prefs.getIntPref(PrefKey.CHART_TIMELINE_FONT_SIZE, 8));
		chkVerticalGuides.setSelected(App.prefs.getBooleanPref(PrefKey.CHART_VERTICAL_GUIDES, true));
		setComponentColours(btnVerticalGuideColor, App.prefs.getColorPref(PrefKey.CHART_VERTICAL_GUIDE_COLOR, Color.DARK_GRAY));
		spnVerticalGuideWeight.setValue(App.prefs.getIntPref(PrefKey.CHART_VERTICAL_GUIDE_WEIGHT, 1));
		cboVerticalGuideStyle.setSelectedItem(App.prefs.getLineStylePref(PrefKey.CHART_VERTICAL_GUIDE_STYLE, LineStyle.DOTTED));
		chkMajorTicks.setSelected(App.prefs.getBooleanPref(PrefKey.CHART_XAXIS_MAJOR_TICKS, true));
		chkMinorTicks.setSelected(App.prefs.getBooleanPref(PrefKey.CHART_XAXIS_MINOR_TICKS, true));
		spnMinorSpacing.setValue(App.prefs.getIntPref(PrefKey.CHART_XAXIS_MINOR_TICK_SPACING, 10));
		spnMajorSpacing.setValue(App.prefs.getIntPref(PrefKey.CHART_XAXIS_MAJOR_TICK_SPACING, 50));
		chkHighlightYears.setSelected(App.prefs.getBooleanPref(PrefKey.CHART_HIGHLIGHT_YEARS, false));
		cboHighlightStyle.setSelectedItem(App.prefs.getLineStylePref(PrefKey.CHART_HIGHLIGHT_YEAR_STYLE, LineStyle.SOLID));
		spnHighlightWeight.setValue(App.prefs.getIntPref(PrefKey.CHART_HIGHLIGHT_YEARS_WEIGHT, 1));
		setComponentColours(btnHighlightColor, App.prefs.getColorPref(PrefKey.CHART_HIGHLIGHT_YEARS_COLOR, Color.YELLOW));
		this.highlightYearsModel.clearYears();
		this.highlightYearsModel.addYears(App.prefs.getIntegerArrayPref(PrefKey.CHART_HIGHLIGHT_YEARS_ARRAY, null));
		
		// Index plot tab
		chkIndexPlot.setSelected(App.prefs.getBooleanPref(PrefKey.CHART_SHOW_INDEX_PLOT, true));
		spnIndexPlotHeight.setValue(App.prefs.getIntPref(PrefKey.CHART_INDEX_PLOT_HEIGHT, 100));
		this.spnPercentScarredLineWidth.setValue(App.prefs.getIntPref(PrefKey.CHART_INDEX_PERCENT_SCARRED_LINE_WIDTH, 1));
		if (App.prefs.getBooleanPref(PrefKey.CHART_SHOW_SAMPLE_DEPTH, false))
		{
			chkSampleDepth.setSelected(true);
		}
		else
		{
			chkRecorderDepth.setSelected(true);
		}
		setComponentColours(btnSampleDepthColor, App.prefs.getColorPref(PrefKey.CHART_SAMPLE_OR_RECORDER_DEPTH_COLOR, Color.BLUE));
		chkPercentScarred.setSelected(App.prefs.getBooleanPref(PrefKey.CHART_SHOW_PERCENT_SCARRED, true));
		setComponentColours(btnPercentScarredColor, App.prefs.getColorPref(PrefKey.CHART_PERCENT_SCARRED_COLOR, Color.BLACK));
		chkSampleThreshold.setSelected(App.prefs.getBooleanPref(PrefKey.CHART_SHOW_DEPTH_THRESHOLD, false));
		setComponentColours(btnThresholdColor, App.prefs.getColorPref(PrefKey.CHART_DEPTH_THRESHOLD_COLOR, Color.RED));
		spnThresholdValue.setValue(App.prefs.getIntPref(PrefKey.CHART_DEPTH_THRESHOLD_VALUE, 10));
		txtAxisY1Label.setText(App.prefs.getPref(PrefKey.CHART_AXIS_Y1_LABEL, "Recorder Depth"));
		txtAxisY2Label.setText(App.prefs.getPref(PrefKey.CHART_AXIS_Y2_LABEL, "% Scarred"));
		spnAxisY1FontSize.setValue(App.prefs.getIntPref(PrefKey.CHART_AXIS_Y1_FONT_SIZE, 10));
		spnAxisY2FontSize.setValue(App.prefs.getIntPref(PrefKey.CHART_AXIS_Y2_FONT_SIZE, 10));
		
		// Chronology plot tab
		chkChronologyPlot.setSelected(App.prefs.getBooleanPref(PrefKey.CHART_SHOW_CHRONOLOGY_PLOT, true));
		chkSeriesLabels.setSelected(App.prefs.getBooleanPref(PrefKey.CHART_SHOW_CHRONOLOGY_PLOT_LABELS, true));
		spnSeriesLabelFontSize.setValue(App.prefs.getIntPref(PrefKey.CHART_CHRONOLOGY_PLOT_LABEL_FONT_SIZE, 10));
		spnSeriesSpacing.setValue(App.prefs.getIntPref(PrefKey.CHART_CHRONOLOGY_PLOT_SPACING, 5));
		chkShowPith.setSelected(App.prefs.getBooleanPref(PrefKey.CHART_SHOW_PITH_SYMBOL, true));
		chkShowBark.setSelected(App.prefs.getBooleanPref(PrefKey.CHART_SHOW_BARK_SYMBOL, true));
		chkShowInnerRing.setSelected(App.prefs.getBooleanPref(PrefKey.CHART_SHOW_INNER_RING_SYMBOL, true));
		chkShowOuterRing.setSelected(App.prefs.getBooleanPref(PrefKey.CHART_SHOW_OUTER_RING_SYMBOL, true));
		chkShowFireEvents.setSelected(App.prefs.getBooleanPref(PrefKey.CHART_SHOW_FIRE_EVENT_SYMBOL, true));
		chkShowInjuryEvents.setSelected(App.prefs.getBooleanPref(PrefKey.CHART_SHOW_INJURY_SYMBOL, true));
		chkShowCategoryGroups.setSelected(App.prefs.getBooleanPref(PrefKey.CHART_SHOW_CATEGORY_GROUPS, true));
		chkShowCategoryLabels.setSelected(App.prefs.getBooleanPref(PrefKey.CHART_SHOW_CATEGORY_LABELS, true));
		spnCategoryLabelFontSize.setValue(App.prefs.getIntPref(PrefKey.CHART_CATEGORY_LABEL_FONT_SIZE, 18));
		cboCategoryLabelJustification
				.setSelectedItem(App.prefs.getJustificationTypePref(PrefKey.CHART_CATEGORY_LABEL_JUSTIFICATION, JustificationType.CENTER));
		chkAutomaticallyColorizeSeries.setSelected(App.prefs.getBooleanPref(PrefKey.CHART_AUTOMATICALLY_COLORIZE_SERIES, false));
		chkAutomaticallyColorizeLabels.setSelected(App.prefs.getBooleanPref(PrefKey.CHART_AUTOMATICALLY_COLORIZE_LABELS, false));
		
		try
		{
			String prefStr = App.prefs.getPref(PrefKey.CHART_SORT_BY_PREFERENCE, SeriesSortType.AS_IN_FILE.toString());
			SeriesSortType sortpref = SeriesSortType.getSeriesSortTypeFromString(prefStr);
			this.cboSortType.setSelectedItem(sortpref);
		}
		catch (Exception e)
		{
			log.debug("Error setting sample sort from preferences");
			this.cboSortType.setSelectedItem(SeriesSortType.AS_IN_FILE);
		}
		
		// Composite plot tab
		chkCompositePlot.setSelected(App.prefs.getBooleanPref(PrefKey.CHART_SHOW_COMPOSITE_PLOT, true));
		spnCompositePlotHeight.setValue(App.prefs.getIntPref(PrefKey.CHART_COMPOSITE_HEIGHT, 70));
		chkCompositeYearLabels.setSelected(App.prefs.getBooleanPref(PrefKey.CHART_SHOW_COMPOSITE_YEAR_LABELS, true));
		cboFilterType
				.setSelectedItem(App.prefs.getFireFilterTypePref(PrefKey.CHART_COMPOSITE_FILTER_TYPE, FireFilterType.NUMBER_OF_EVENTS));
		spnFilterValue.setValue(App.prefs.getIntPref(PrefKey.CHART_COMPOSITE_FILTER_VALUE, 2));
		spnMinNumberOfSamples.setValue(App.prefs.getIntPref(PrefKey.CHART_COMPOSITE_MIN_NUM_SAMPLES, 2));
		spnCompositeYearLabelFontSize.setValue(App.prefs.getIntPref(PrefKey.CHART_COMPOSITE_YEAR_LABEL_FONT_SIZE, 8));
		txtComposite.setText(App.prefs.getPref(PrefKey.CHART_COMPOSITE_LABEL_TEXT, "Composite"));
		spnCompositePlotLabelFontSize.setValue(App.prefs.getIntPref(PrefKey.CHART_COMPOSITE_PLOT_LABEL_FONT_SIZE, 10));
		radShortYearStyle.setSelected(App.prefs.getBooleanPref(PrefKey.CHART_COMPOSITE_YEAR_LABELS_TWO_DIGIT, false));
		radLongYearStyle.setSelected(!App.prefs.getBooleanPref(PrefKey.CHART_COMPOSITE_YEAR_LABELS_TWO_DIGIT, false));
		spnYearLabelPadding.setValue(App.prefs.getIntPref(PrefKey.CHART_COMPOSITE_YEAR_LABEL_BUFFER, 5));
		cboLabelOrientation
				.setSelectedItem(App.prefs.getLabelOrientationPref(PrefKey.CHART_COMPOSITE_LABEL_ALIGNMENT, LabelOrientation.HORIZONTAL));
		
		tryAutoSetYAxisLabel();
		setVerticalGuideGUI();
		setHighlightYearsGUI();
		setIndexPlotGUI();
		setChronologyPlotGUI();
		setCompositePlotGUI();
		setCompositeYearLabelsGUI();
		this.setXAxisGui();
	}
	
	/**
	 * Initializes the GUI.
	 */
	@SuppressWarnings("rawtypes")
	private void initGUI() {
		
		this.setIconImage(Builder.getApplicationIcon());
		this.setTitle("Chart Properties");
		this.setModal(true);
		this.setBounds(100, 100, 843, 670);
		
		this.getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		this.getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(new BorderLayout(0, 0));
		
		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		contentPanel.add(tabbedPane, BorderLayout.CENTER);
		
		JPanel genericOptionsPanel = new JPanel();
		tabbedPane.addTab("Generic Options ", Builder.getImageIcon("advancedsettings.png"), genericOptionsPanel, null);
		genericOptionsPanel.setLayout(new MigLayout("", "[grow,right]", "[][][23.00][grow]"));
		
		JPanel panelGenericOptionsGeneral = new JPanel();
		panelGenericOptionsGeneral.setBorder(new TitledBorder(null, "General", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		genericOptionsPanel.add(panelGenericOptionsGeneral, "cell 0 0,grow");
		panelGenericOptionsGeneral.setLayout(new MigLayout("", "[180px][16.00][37.00,left][150px,right][][grow,fill][]", "[30][30][30]"));
		
		lblShowLegend = new JLabel("Show legend:");
		panelGenericOptionsGeneral.add(lblShowLegend, "cell 0 0,alignx right,aligny center");
		
		chkShowLegend = new JCheckBox();
		chkShowLegend.setSelected(true);
		panelGenericOptionsGeneral.add(chkShowLegend, "cell 1 0,alignx left");
		
		helpTipButton = new HelpTipButton(LocalHelp.SHOW_LEGEND);
		panelGenericOptionsGeneral.add(helpTipButton, "cell 2 0,alignx left");
		
		JLabel lblFont = new JLabel("Font family:");
		panelGenericOptionsGeneral.add(lblFont, "cell 3 0,alignx right,aligny center");
		
		cboFontFamily = new FontChooserComboBox();
		cboFontFamily.addItemListener(new ItemListener() {
			
			@Override
			public void itemStateChanged(ItemEvent arg0) {
				
				String font = cboFontFamily.getSelectedFontName();
				
				if (font != null)
				{
					App.prefs.setPref(PrefKey.CHART_FONT_FAMILY, font);
				}
			}
		});
		
		if (App.prefs.getPref(PrefKey.CHART_FONT_FAMILY, null) != null)
		{
			cboFontFamily.setSelectedItem(App.prefs.getPref(PrefKey.CHART_FONT_FAMILY, null));
		}
		else
		{
			cboFontFamily.setSelectedItem("Verdana");
		}
		
		panelGenericOptionsGeneral.add(cboFontFamily, "cell 4 0 2 1,growx,aligny center");
		
		htbFontFamily = new HelpTipButton(LocalHelp.CHART_FONT_FAMILY);
		panelGenericOptionsGeneral.add(htbFontFamily, "cell 6 0");
		
		lblShowChartTitle = new JLabel("Show chart title:");
		panelGenericOptionsGeneral.add(lblShowChartTitle, "cell 0 1,alignx right,aligny center");
		
		chkShowChartTitle = new JCheckBox();
		chkShowChartTitle.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				
				if (chkShowChartTitle.isSelected())
				{
					chartTitleOverrideText.setEnabled(false);
				}
				else
				{
					chartTitleOverrideText.setEnabled(true);
				}
			}
		});
		panelGenericOptionsGeneral.add(chkShowChartTitle, "cell 1 1,alignx left");
		
		htbShowChartTitle = new HelpTipButton(LocalHelp.SHOW_CHART_TITLE);
		panelGenericOptionsGeneral.add(htbShowChartTitle, "cell 2 1");
		
		lblChartTitleFontSize = new JLabel("Chart title font size:");
		panelGenericOptionsGeneral.add(lblChartTitleFontSize, "cell 3 1,alignx right,aligny center");
		
		spnChartTitleFontSize = new JSpinner();
		panelGenericOptionsGeneral.add(spnChartTitleFontSize, "cell 4 1,growx,aligny center");
		
		htbFontSize = new HelpTipButton(LocalHelp.CHART_TITLE_FONT_SIZE);
		panelGenericOptionsGeneral.add(htbFontSize, "cell 5 1");
		
		lblUseDefaultChartTitle = new JLabel("Use default chart title:");
		panelGenericOptionsGeneral.add(lblUseDefaultChartTitle, "cell 0 2,alignx right,aligny center");
		
		chkUseDefaultName = new JCheckBox();
		chkUseDefaultName.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				
				if (chkUseDefaultName.isSelected())
				{
					chartTitleOverrideText.setEnabled(false);
				}
				else
				{
					chartTitleOverrideText.setEnabled(true);
				}
			}
		});
		panelGenericOptionsGeneral.add(chkUseDefaultName, "cell 1 2,alignx left");
		
		htbDefaultChartTitle = new HelpTipButton(LocalHelp.SHOW_CHART_TITLE);
		panelGenericOptionsGeneral.add(htbDefaultChartTitle, "cell 2 2");
		
		lblChartTitleOverrideText = new JLabel("Chart title override text:");
		panelGenericOptionsGeneral.add(lblChartTitleOverrideText, "cell 3 2,alignx right,aligny center");
		
		chartTitleOverrideText = new JTextField();
		chartTitleOverrideText.setColumns(10);
		panelGenericOptionsGeneral.add(chartTitleOverrideText, "cell 4 2,growx,aligny center");
		
		htbChartTitle = new HelpTipButton(LocalHelp.CHART_TITLE);
		panelGenericOptionsGeneral.add(htbChartTitle, "cell 5 2");
		
		JPanel panelGenericOptionsXAxis = new JPanel();
		panelGenericOptionsXAxis.setBorder(new TitledBorder(null, "Timeline (X Axis)", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		genericOptionsPanel.add(panelGenericOptionsXAxis, "cell 0 1,grow");
		panelGenericOptionsXAxis
				.setLayout(new MigLayout("", "[180px][80,left][34.00][right][67.00,grow,fill][][62.00,grow,fill][]", "[][15px]"));
		
		lblAutoAdjustRange = new JLabel("Auto adjust range:");
		panelGenericOptionsXAxis.add(lblAutoAdjustRange, "cell 0 0,alignx right,aligny center");
		
		chkAutoAdjustRange = new JCheckBox();
		chkAutoAdjustRange.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				
				setXAxisGui();
			}
			
		});
		panelGenericOptionsXAxis.add(chkAutoAdjustRange, "flowx,cell 1 0,alignx left,aligny center");
		
		JLabel lblNewLabel = new JLabel("Year range:");
		panelGenericOptionsXAxis.add(lblNewLabel, "cell 3 0,alignx right,aligny center");
		
		firstYearSpinner = new BCADYearSpinner(neoFHChart.currentChart.getReader().getFirstYear(), SharedConstants.EARLIEST_ALLOWED_YEAR,
				SharedConstants.CURRENT_YEAR - 1);
		
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
		panelGenericOptionsXAxis.add(firstYearSpinner, "cell 4 0,growx,aligny center");
		
		lastYearSpinner = new BCADYearSpinner(neoFHChart.currentChart.getReader().getLastYear(), SharedConstants.EARLIEST_ALLOWED_YEAR,
				SharedConstants.CURRENT_YEAR);
		
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
		
		JLabel lblTo = new JLabel("to");
		panelGenericOptionsXAxis.add(lblTo, "cell 5 0,alignx center,aligny center");
		panelGenericOptionsXAxis.add(lastYearSpinner, "cell 6 0,growx,aligny center");
		
		htbYearRange = new HelpTipButton(LocalHelp.XAXIS_RANGE);
		panelGenericOptionsXAxis.add(htbYearRange, "cell 7 0");
		
		lblTimelineFontSize = new JLabel("Timeline font size:");
		panelGenericOptionsXAxis.add(lblTimelineFontSize, "cell 0 1,alignx right,aligny center");
		
		spnTimelineFontSize = new JSpinner();
		panelGenericOptionsXAxis.add(spnTimelineFontSize, "cell 1 1,growx,aligny center");
		
		htbTimelineFontSize = new HelpTipButton(LocalHelp.XAXIS_FONT_SIZE);
		panelGenericOptionsXAxis.add(htbTimelineFontSize, "cell 2 1");
		
		htbAutoRange = new HelpTipButton(LocalHelp.XAXIS_AUTO_RANGE);
		panelGenericOptionsXAxis.add(htbAutoRange, "cell 1 0");
		
		JPanel panelGenericOptionsTicksAndGuides = new JPanel();
		panelGenericOptionsTicksAndGuides
				.setBorder(new TitledBorder(null, "Ticks and Vertical Guides", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		genericOptionsPanel.add(panelGenericOptionsTicksAndGuides, "cell 0 2,grow");
		panelGenericOptionsTicksAndGuides.setLayout(new MigLayout("", "[180px,right][][36.00][right][67][grow][][67][][]", "[][][]"));
		
		JLabel lblShowhide = new JLabel("Vertical guides:");
		panelGenericOptionsTicksAndGuides.add(lblShowhide, "cell 0 0");
		
		chkVerticalGuides = new JCheckBox("");
		chkVerticalGuides.setActionCommand("VerticalGuides");
		chkVerticalGuides.addActionListener(this);
		chkVerticalGuides.setSelected(true);
		panelGenericOptionsTicksAndGuides.add(chkVerticalGuides, "flowx,cell 1 0,alignx left,aligny center");
		
		htbShowVerticalGuides = new HelpTipButton(LocalHelp.SHOW_VERTICAL_GUIDES);
		panelGenericOptionsTicksAndGuides.add(htbShowVerticalGuides, "cell 2 0");
		
		JLabel lblStyle = new JLabel("Style:");
		panelGenericOptionsTicksAndGuides.add(lblStyle, "flowx,cell 3 0,alignx right,aligny center");
		
		JLabel lblWeight = new JLabel("Weight:");
		panelGenericOptionsTicksAndGuides.add(lblWeight, "cell 6 0,alignx right,aligny center");
		
		spnVerticalGuideWeight = new JSpinner();
		spnVerticalGuideWeight.setModel(new SpinnerNumberModel(1, 1, 10, 1));
		
		panelGenericOptionsTicksAndGuides.add(spnVerticalGuideWeight, "cell 7 0,growx,aligny center");
		
		btnVerticalGuideColor = new JButton("Color");
		setComponentColours(btnVerticalGuideColor, App.prefs.getColorPref(PrefKey.CHART_VERTICAL_GUIDE_COLOR, Color.BLACK));
		btnVerticalGuideColor.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				
				Color newcolor = JColorChooser.showDialog(App.mainFrame, "Vertical Guide Color",
						App.prefs.getColorPref(PrefKey.CHART_VERTICAL_GUIDE_COLOR, Color.BLACK));
				
				if (newcolor != null)
				{
					App.prefs.setColorPref(PrefKey.CHART_VERTICAL_GUIDE_COLOR, newcolor);
					setComponentColours(btnVerticalGuideColor, newcolor);
				}
			}
		});
		panelGenericOptionsTicksAndGuides.add(btnVerticalGuideColor, "cell 8 0,growx,aligny center");
		
		htbVerticalGuideStyle = new HelpTipButton(LocalHelp.VERTICAL_GUIDE_STYLE);
		panelGenericOptionsTicksAndGuides.add(htbVerticalGuideStyle, "cell 9 0");
		
		JLabel lblYearSpacing = new JLabel("Major ticks:");
		panelGenericOptionsTicksAndGuides.add(lblYearSpacing, "cell 0 1,alignx trailing");
		
		chkMajorTicks = new JCheckBox("");
		panelGenericOptionsTicksAndGuides.add(chkMajorTicks, "cell 1 1,alignx left,aligny center");
		
		htbShowMajorTicks = new HelpTipButton(LocalHelp.SHOW_MAJOR_TICKS);
		panelGenericOptionsTicksAndGuides.add(htbShowMajorTicks, "cell 2 1");
		
		JLabel lblYearSpacing_1 = new JLabel("Year spacing:");
		panelGenericOptionsTicksAndGuides.add(lblYearSpacing_1, "cell 3 1,alignx right,aligny center");
		
		spnMajorSpacing = new JSpinner();
		spnMajorSpacing.setModel(new SpinnerNumberModel(50, 5, 1000, 10));
		panelGenericOptionsTicksAndGuides.add(spnMajorSpacing, "cell 4 1,growx,aligny center");
		
		htbMajorSpacing = new HelpTipButton(LocalHelp.MAJOR_TICK_SPACING);
		panelGenericOptionsTicksAndGuides.add(htbMajorSpacing, "cell 5 1");
		
		JLabel lblMinorTicks = new JLabel("Minor ticks:");
		panelGenericOptionsTicksAndGuides.add(lblMinorTicks, "cell 0 2");
		
		chkMinorTicks = new JCheckBox("");
		panelGenericOptionsTicksAndGuides.add(chkMinorTicks, "cell 1 2,alignx left,aligny center");
		
		htbShowMinorTicks = new HelpTipButton(LocalHelp.SHOW_MINOR_TICKS);
		panelGenericOptionsTicksAndGuides.add(htbShowMinorTicks, "cell 2 2");
		
		JLabel label = new JLabel("Year spacing:");
		panelGenericOptionsTicksAndGuides.add(label, "cell 3 2,alignx right,aligny center");
		
		cboVerticalGuideStyle = new JComboBox<>();
		
		for (int i = 0; i < LineStyle.values().length; i++)
		{
			cboVerticalGuideStyle.addItem(LineStyle.values()[i]);
		}
		
		cboVerticalGuideStyle.setRenderer(new LineStyleRenderer());
		panelGenericOptionsTicksAndGuides.add(cboVerticalGuideStyle, "cell 4 0 2 1,growx,aligny center");
		
		spnMinorSpacing = new JSpinner();
		spnMinorSpacing.setModel(new SpinnerNumberModel(10, 1, 1000, 1));
		panelGenericOptionsTicksAndGuides.add(spnMinorSpacing, "cell 4 2,growx,aligny center");
		
		htbMinorSpacing = new HelpTipButton(LocalHelp.MINOR_TICK_SPACING);
		panelGenericOptionsTicksAndGuides.add(htbMinorSpacing, "cell 5 2");
		
		JPanel panelGenericOptionsHighlightedYears = new JPanel();
		panelGenericOptionsHighlightedYears
				.setBorder(new TitledBorder(null, "Highlighted Years", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		genericOptionsPanel.add(panelGenericOptionsHighlightedYears, "cell 0 3,grow");
		panelGenericOptionsHighlightedYears
				.setLayout(new MigLayout("", "[180,right][58:58.00][][grow][][67][fill][]", "[fill][top][top][][grow]"));
		
		JLabel lblShowHighlights = new JLabel("Show highlighted years:");
		panelGenericOptionsHighlightedYears.add(lblShowHighlights, "cell 0 0,alignx right,aligny center");
		
		chkHighlightYears = new JCheckBox("");
		chkHighlightYears.setActionCommand("HighlightYears");
		chkHighlightYears.addActionListener(this);
		panelGenericOptionsHighlightedYears.add(chkHighlightYears, "flowx,cell 1 0,alignx left,aligny center");
		
		JLabel lblStyle_1 = new JLabel("Style:");
		panelGenericOptionsHighlightedYears.add(lblStyle_1, "cell 2 0,alignx right,aligny center");
		
		cboHighlightStyle = new JComboBox<>();
		
		for (int i = 0; i < LineStyle.values().length; i++)
		{
			cboHighlightStyle.addItem(LineStyle.values()[i]);
		}
		
		cboHighlightStyle.setRenderer(new LineStyleRenderer());
		panelGenericOptionsHighlightedYears.add(cboHighlightStyle, "flowx,cell 3 0,growx,aligny center");
		
		JLabel lblNewLabel_2 = new JLabel("Weight:");
		panelGenericOptionsHighlightedYears.add(lblNewLabel_2, "cell 4 0,alignx right,aligny center");
		
		spnHighlightWeight = new JSpinner();
		spnHighlightWeight.setModel(new SpinnerNumberModel(1, 1, 10, 1));
		panelGenericOptionsHighlightedYears.add(spnHighlightWeight, "cell 5 0,growx,aligny center");
		
		btnHighlightColor = new JButton("Color");
		btnHighlightColor.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				
				Color newcolor = JColorChooser.showDialog(App.mainFrame, "Year Highlighter Color",
						App.prefs.getColorPref(PrefKey.CHART_HIGHLIGHT_YEARS_COLOR, Color.YELLOW));
				
				if (newcolor != null)
				{
					App.prefs.setColorPref(PrefKey.CHART_HIGHLIGHT_YEARS_COLOR, newcolor);
					setComponentColours(btnHighlightColor, newcolor);
				}
			}
		});
		panelGenericOptionsHighlightedYears.add(btnHighlightColor, "cell 6 0,growx,aligny center");
		
		htbHighlightedYearStyle = new HelpTipButton(LocalHelp.HIGHLIGHTED_YEARS_STYLE);
		panelGenericOptionsHighlightedYears.add(htbHighlightedYearStyle, "cell 7 0");
		
		JLabel lblHighlightYears = new JLabel("Highlight years:");
		panelGenericOptionsHighlightedYears.add(lblHighlightYears, "cell 0 1,alignx right,aligny center");
		
		JScrollPane scrollPane = new JScrollPane();
		panelGenericOptionsHighlightedYears.add(scrollPane, "cell 1 1 5 4,grow");
		
		lstHightlightYears = new JList<>();
		lstHightlightYears.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		lstHightlightYears.setLayoutOrientation(JList.HORIZONTAL_WRAP);
		lstHightlightYears.setVisibleRowCount(-1);
		this.highlightYearsModel = new YearListModel();
		lstHightlightYears.setModel(highlightYearsModel);
		scrollPane.setViewportView(lstHightlightYears);
		
		btnAddYear = new JButton("");
		btnAddYear.setIcon(Builder.getImageIcon("edit_add.png"));
		btnAddYear.setActionCommand("AddHighlightYear");
		btnAddYear.addActionListener(this);
		panelGenericOptionsHighlightedYears.add(btnAddYear, "cell 6 1,growx,aligny center");
		
		btnRemoveYear = new JButton("");
		btnRemoveYear.setIcon(Builder.getImageIcon("delete.png"));
		btnRemoveYear.setActionCommand("RemoveHighlightYear");
		btnRemoveYear.addActionListener(this);
		panelGenericOptionsHighlightedYears.add(btnRemoveYear, "cell 6 2,growx,aligny center");
		
		htbShowHighlightedYears = new HelpTipButton(LocalHelp.SHOW_HIGHLIGHTED_YEARS);
		panelGenericOptionsHighlightedYears.add(htbShowHighlightedYears, "cell 1 0");
		
		panelIndexPlotComponents = new JPanel();
		panelIndexPlotComponents.setBorder(new TitledBorder(null, "Components", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		JPanel indexPlotPanel = new JPanel();
		
		indexPlotPanel.setLayout(new MigLayout("", "[432px,grow,fill]", "[][][130px][]"));
		
		panel_1 = new JPanel();
		indexPlotPanel.add(panel_1, "cell 0 0,grow");
		panel_1.setLayout(new MigLayout("", "[432px,grow,fill]", "[]"));
		
		chkIndexPlot = new JCheckBox("Show/Hide Index Plot");
		panel_1.add(chkIndexPlot, "flowx,cell 0 0");
		chkIndexPlot.setActionCommand("IndexPlot");
		chkIndexPlot.addActionListener(this);
		chkIndexPlot.setFont(new Font("Dialog", Font.BOLD, 16));
		
		htbShowIndexPlot = new HelpTipButton(LocalHelp.SHOW_INDEX_PLOT);
		panel_1.add(htbShowIndexPlot, "cell 0 0,growx");
		
		panelIndexPlotGeneral = new JPanel();
		panelIndexPlotGeneral.setBorder(new TitledBorder(null, "General", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		indexPlotPanel.add(panelIndexPlotGeneral, "cell 0 1,grow");
		panelIndexPlotGeneral.setLayout(new MigLayout("", "[180px][80][grow]", "[][]"));
		
		JLabel lblChartHeight = new JLabel("Chart height:");
		panelIndexPlotGeneral.add(lblChartHeight, "cell 0 0,alignx right,aligny center");
		
		spnIndexPlotHeight = new JSpinner();
		spnIndexPlotHeight.setModel(new SpinnerNumberModel(100, 20, 500, 1));
		panelIndexPlotGeneral.add(spnIndexPlotHeight, "cell 1 0,growx,aligny center");
		
		JLabel lblPx_1 = new JLabel("px");
		panelIndexPlotGeneral.add(lblPx_1, "flowx,cell 2 0,alignx left,aligny center");
		
		htbIndexPlotHeight = new HelpTipButton(LocalHelp.INDEX_PLOT_HEIGHT);
		panelIndexPlotGeneral.add(htbIndexPlotHeight, "cell 2 0");
		
		lblIndexLineWidth = new JLabel("Percent scarred line width:");
		panelIndexPlotGeneral.add(lblIndexLineWidth, "cell 0 1,alignx right");
		
		spnPercentScarredLineWidth = new JSpinner();
		spnPercentScarredLineWidth.setModel(new SpinnerNumberModel(1, 1, 5, 1));
		panelIndexPlotGeneral.add(spnPercentScarredLineWidth, "cell 1 1,growx");
		
		indexPlotPanel.add(panelIndexPlotComponents, "cell 0 2,alignx left,aligny top");
		tabbedPane.addTab("Index Plot ", Builder.getImageIcon("fireindexplot.png"), indexPlotPanel, null);
		panelIndexPlotComponents.setLayout(new MigLayout("", "[180px][][][][80][67][grow]", "[][][][]"));
		
		JLabel lblSampleDepth = new JLabel("Sample depth:");
		panelIndexPlotComponents.add(lblSampleDepth, "cell 0 0,alignx right,aligny center");
		
		ButtonGroup group = new ButtonGroup();
		
		chkSampleDepth = new JRadioButton("");
		chkSampleDepth.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				
				tryAutoSetYAxisLabel();
			}
		});
		group.add(chkSampleDepth);
		panelIndexPlotComponents.add(chkSampleDepth, "cell 1 0");
		
		btnSampleDepthColor = new JButton("Color");
		setComponentColours(btnSampleDepthColor, App.prefs.getColorPref(PrefKey.CHART_SAMPLE_OR_RECORDER_DEPTH_COLOR, Color.BLUE));
		btnSampleDepthColor.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				
				Color newcolor = JColorChooser.showDialog(App.mainFrame, "Sample Depth Color",
						App.prefs.getColorPref(PrefKey.CHART_SAMPLE_OR_RECORDER_DEPTH_COLOR, Color.BLUE));
				
				if (newcolor != null)
				{
					App.prefs.setColorPref(PrefKey.CHART_SAMPLE_OR_RECORDER_DEPTH_COLOR, newcolor);
					setComponentColours(btnSampleDepthColor, newcolor);
				}
			}
		});
		
		htbSampleOrRecorderDepth = new HelpTipButton(LocalHelp.SAMPLE_OR_RECORDER_DEPTH);
		panelIndexPlotComponents.add(htbSampleOrRecorderDepth, "cell 2 0 1 2");
		panelIndexPlotComponents.add(btnSampleDepthColor, "cell 3 0 1 2,growx,aligny center");
		
		JLabel lblRecorderDepth = new JLabel("Recorder depth:");
		panelIndexPlotComponents.add(lblRecorderDepth, "cell 0 1,alignx right,aligny center");
		
		chkRecorderDepth = new JRadioButton("");
		chkRecorderDepth.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				
				tryAutoSetYAxisLabel();
			}
		});
		
		group.add(chkRecorderDepth);
		panelIndexPlotComponents.add(chkRecorderDepth, "cell 1 1");
		
		JLabel lblPercentScarred = new JLabel("Percent scarred:");
		panelIndexPlotComponents.add(lblPercentScarred, "cell 0 2,alignx right,aligny center");
		
		chkPercentScarred = new JCheckBox("");
		panelIndexPlotComponents.add(chkPercentScarred, "cell 1 2");
		
		btnPercentScarredColor = new JButton("Color");
		setComponentColours(btnPercentScarredColor, App.prefs.getColorPref(PrefKey.CHART_PERCENT_SCARRED_COLOR, Color.BLACK));
		btnPercentScarredColor.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				
				Color newcolor = JColorChooser.showDialog(App.mainFrame, "Percent Scarred Color",
						App.prefs.getColorPref(PrefKey.CHART_PERCENT_SCARRED_COLOR, Color.BLACK));
				
				if (newcolor != null)
				{
					App.prefs.setColorPref(PrefKey.CHART_PERCENT_SCARRED_COLOR, newcolor);
					setComponentColours(btnPercentScarredColor, newcolor);
				}
			}
		});
		
		htbShowPercentScarred = new HelpTipButton(LocalHelp.SHOW_PERCENT_SCARRED);
		panelIndexPlotComponents.add(htbShowPercentScarred, "cell 2 2");
		
		panelIndexPlotComponents.add(btnPercentScarredColor, "cell 3 2,growx,aligny center");
		
		JLabel lblSamplerecorderThreshold = new JLabel("Sample / recorder threshold:");
		panelIndexPlotComponents.add(lblSamplerecorderThreshold, "cell 0 3,alignx right,aligny center");
		
		chkSampleThreshold = new JCheckBox("");
		panelIndexPlotComponents.add(chkSampleThreshold, "cell 1 3");
		
		btnThresholdColor = new JButton("Color");
		setComponentColours(btnThresholdColor, App.prefs.getColorPref(PrefKey.CHART_DEPTH_THRESHOLD_COLOR, Color.RED));
		btnThresholdColor.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				
				Color newcolor = JColorChooser.showDialog(App.mainFrame, "Depth Threshold Color",
						App.prefs.getColorPref(PrefKey.CHART_DEPTH_THRESHOLD_COLOR, Color.RED));
				
				if (newcolor != null)
				{
					App.prefs.setColorPref(PrefKey.CHART_DEPTH_THRESHOLD_COLOR, newcolor);
					setComponentColours(btnThresholdColor, newcolor);
				}
			}
		});
		
		htbShowDepthThreshold = new HelpTipButton(LocalHelp.SHOW_DEPTH_THRESHOLD);
		panelIndexPlotComponents.add(htbShowDepthThreshold, "cell 2 3");
		panelIndexPlotComponents.add(btnThresholdColor, "cell 3 3,growx,aligny center");
		
		JLabel lblValue = new JLabel("Value:");
		panelIndexPlotComponents.add(lblValue, "cell 4 3,alignx right");
		
		spnThresholdValue = new JSpinner();
		spnThresholdValue.setModel(new SpinnerNumberModel(new Integer(10), new Integer(1), null, new Integer(1)));
		panelIndexPlotComponents.add(spnThresholdValue, "cell 5 3,growx,aligny center");
		
		htbDepthThresholdValue = new HelpTipButton(LocalHelp.DEPTH_THRESHOLD_VALUE);
		panelIndexPlotComponents.add(htbDepthThresholdValue, "cell 6 3");
		
		panelIndexPlotAxisY1 = new JPanel();
		panelIndexPlotAxisY1.setBorder(new TitledBorder(null, "Axis Y1", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		indexPlotPanel.add(panelIndexPlotAxisY1, "flowx,cell 0 3,grow");
		panelIndexPlotAxisY1.setLayout(new MigLayout("", "[60][67][grow][]", "[][]"));
		
		JLabel lblLabel = new JLabel("Label:");
		panelIndexPlotAxisY1.add(lblLabel, "cell 0 0,alignx right,aligny center");
		
		txtAxisY1Label = new JTextField();
		panelIndexPlotAxisY1.add(txtAxisY1Label, "cell 1 0 2 1,growx,aligny center");
		txtAxisY1Label.setColumns(10);
		
		htbY1AxisLabel = new HelpTipButton(LocalHelp.Y1_AXIS_LABEL);
		panelIndexPlotAxisY1.add(htbY1AxisLabel, "cell 3 0");
		
		JLabel lblFontSize = new JLabel("Font size:");
		panelIndexPlotAxisY1.add(lblFontSize, "cell 0 1,alignx right,aligny center");
		
		spnAxisY1FontSize = new JSpinner();
		spnAxisY1FontSize.setModel(new SpinnerNumberModel(10, 4, 64, 1));
		panelIndexPlotAxisY1.add(spnAxisY1FontSize, "cell 1 1,growx,aligny center");
		
		JLabel lblPt = new JLabel("pt");
		panelIndexPlotAxisY1.add(lblPt, "flowx,cell 2 1,alignx left,aligny center");
		
		htbY1FontSize = new HelpTipButton(LocalHelp.Y1_FONT_SIZE);
		panelIndexPlotAxisY1.add(htbY1FontSize, "cell 2 1");
		
		panelIndexPlotAxisY2 = new JPanel();
		panelIndexPlotAxisY2.setBorder(new TitledBorder(null, "Axis Y2", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		indexPlotPanel.add(panelIndexPlotAxisY2, "cell 0 3,grow");
		panelIndexPlotAxisY2.setLayout(new MigLayout("", "[60][67][grow][]", "[][]"));
		
		JLabel lblLabel2 = new JLabel("Label:");
		panelIndexPlotAxisY2.add(lblLabel2, "cell 0 0,alignx right,aligny center");
		
		txtAxisY2Label = new JTextField();
		txtAxisY2Label.setColumns(10);
		panelIndexPlotAxisY2.add(txtAxisY2Label, "cell 1 0 2 1,growx,aligny center");
		
		htbY2AxisLabel = new HelpTipButton(LocalHelp.Y2_AXIS_LABEL);
		panelIndexPlotAxisY2.add(htbY2AxisLabel, "cell 3 0");
		
		JLabel lblFontSize2 = new JLabel("Font size:");
		panelIndexPlotAxisY2.add(lblFontSize2, "cell 0 1,alignx right,aligny center");
		
		spnAxisY2FontSize = new JSpinner();
		spnAxisY2FontSize.setModel(new SpinnerNumberModel(10, 4, 64, 1));
		panelIndexPlotAxisY2.add(spnAxisY2FontSize, "cell 1 1,growx,aligny center");
		
		JLabel lblPt_1 = new JLabel("pt");
		panelIndexPlotAxisY2.add(lblPt_1, "flowx,cell 2 1,alignx left,aligny center");
		
		htbY2FontSize = new HelpTipButton(LocalHelp.Y2_FONT_SIZE);
		panelIndexPlotAxisY2.add(htbY2FontSize, "cell 2 1");
		
		JPanel chronoPlotPanel = new JPanel();
		tabbedPane.addTab("Chronology Plot ", Builder.getImageIcon("firechronologyplot.png"), chronoPlotPanel, null);
		chronoPlotPanel.setLayout(new MigLayout("", "[grow]", "[][][][][grow]"));
		
		chkChronologyPlot = new JCheckBox("Show/Hide Chronology Plot");
		chkChronologyPlot.setActionCommand("ChronologyPlot");
		chkChronologyPlot.addActionListener(this);
		chkChronologyPlot.setFont(new Font("Dialog", Font.BOLD, 16));
		chronoPlotPanel.add(chkChronologyPlot, "flowx,cell 0 0");
		
		panelChronoPlotSeries = new JPanel();
		panelChronoPlotSeries.setBorder(new TitledBorder(null, "Series", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		chronoPlotPanel.add(panelChronoPlotSeries, "cell 0 1,grow");
		panelChronoPlotSeries.setLayout(new MigLayout("", "[180px][67][67][grow]", "[][][][][]"));
		
		JButton btnChooseSeriesToPlot = new JButton("Choose series to plot");
		btnChooseSeriesToPlot.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				
				SeriesListDialog.showDialog(neoFHChart.currentChart, neoFHChart.svgCanvas);
			}
		});
		
		lblShowHideChronoLabels = new JLabel("Show/hide labels:");
		panelChronoPlotSeries.add(lblShowHideChronoLabels, "cell 0 0,alignx right,aligny center");
		
		chkSeriesLabels = new JCheckBox("");
		chkSeriesLabels.setSelected(true);
		panelChronoPlotSeries.add(chkSeriesLabels, "flowx,cell 1 0,alignx left,aligny center");
		
		lblSortSamplesBy = new JLabel("Sort samples by:");
		panelChronoPlotSeries.add(lblSortSamplesBy, "cell 0 1,alignx trailing");
		
		cboSortType = new JComboBox<>();
		for (int i = 0; i < SeriesSortType.values().length; i++)
		{
			cboSortType.addItem(SeriesSortType.values()[i]);
		}
		
		panelChronoPlotSeries.add(cboSortType, "cell 1 1 2 1,growx");
		
		htbSortSamples = new HelpTipButton("Choose how samples should be sorted in the chronology plot");
		panelChronoPlotSeries.add(htbSortSamples, "cell 3 1");
		panelChronoPlotSeries.add(btnChooseSeriesToPlot, "flowx,cell 1 2 2 1,growx,aligny center");
		
		JLabel lblFontSize3 = new JLabel("Font size:");
		panelChronoPlotSeries.add(lblFontSize3, "cell 0 3,alignx right,aligny center");
		
		spnSeriesLabelFontSize = new JSpinner();
		spnSeriesLabelFontSize.setModel(new SpinnerNumberModel(8, 4, 64, 1));
		panelChronoPlotSeries.add(spnSeriesLabelFontSize, "cell 1 3,growx,aligny center");
		
		JLabel lblPt_2 = new JLabel("pt");
		panelChronoPlotSeries.add(lblPt_2, "flowx,cell 2 3,alignx left,aligny center");
		
		JLabel lblSpacing = new JLabel("Spacing:");
		panelChronoPlotSeries.add(lblSpacing, "cell 0 4,alignx right,aligny center");
		
		spnSeriesSpacing = new JSpinner();
		spnSeriesSpacing.setModel(new SpinnerNumberModel(5, 0, 50, 1));
		panelChronoPlotSeries.add(spnSeriesSpacing, "cell 1 4,growx,aligny center");
		
		JLabel lblPx_2 = new JLabel("px");
		panelChronoPlotSeries.add(lblPx_2, "flowx,cell 2 4,alignx left,aligny center");
		
		htbShowSeriesLabels = new HelpTipButton(LocalHelp.SHOW_SERIES_LABELS);
		panelChronoPlotSeries.add(htbShowSeriesLabels, "cell 1 0");
		
		htbSeriesLabelsFontSize = new HelpTipButton(LocalHelp.SERIES_LABELS_FONT_SIZE);
		panelChronoPlotSeries.add(htbSeriesLabelsFontSize, "cell 2 3");
		
		htbSeriesSpacing = new HelpTipButton(LocalHelp.SERIES_SPACING);
		panelChronoPlotSeries.add(htbSeriesSpacing, "cell 2 4");
		
		panelChronoPlotSymbols = new JPanel();
		panelChronoPlotSymbols.setBorder(new TitledBorder(null, "Symbols", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		chronoPlotPanel.add(panelChronoPlotSymbols, "cell 0 2,grow");
		panelChronoPlotSymbols.setLayout(new MigLayout("", "[][180px][50][][50][][grow]", "[][]"));
		
		htbShowSymbols = new HelpTipButton(LocalHelp.SHOW_SYMBOLS);
		panelChronoPlotSymbols.add(htbShowSymbols, "cell 0 0");
		
		lblShowPith = new JLabel("Show pith:");
		panelChronoPlotSymbols.add(lblShowPith, "cell 1 0,alignx right,aligny center");
		
		chkShowPith = new JCheckBox("");
		chkShowPith.setSelected(true);
		panelChronoPlotSymbols.add(chkShowPith, "flowx,cell 2 0,alignx left,aligny center");
		
		lblShowInnerRing = new JLabel("Show inner ring:");
		panelChronoPlotSymbols.add(lblShowInnerRing, "cell 3 0,alignx right,aligny center");
		
		chkShowInnerRing = new JCheckBox("");
		chkShowInnerRing.setSelected(true);
		panelChronoPlotSymbols.add(chkShowInnerRing, "flowx,cell 4 0,alignx left,aligny center");
		
		lblShowFireEvents = new JLabel("Show fire events:");
		panelChronoPlotSymbols.add(lblShowFireEvents, "cell 5 0,alignx right,aligny center");
		
		chkShowFireEvents = new JCheckBox("");
		chkShowFireEvents.setSelected(true);
		panelChronoPlotSymbols.add(chkShowFireEvents, "flowx,cell 6 0,alignx left,aligny center");
		
		lblShowBark = new JLabel("Show bark:");
		panelChronoPlotSymbols.add(lblShowBark, "cell 1 1,alignx right,aligny center");
		
		chkShowBark = new JCheckBox("");
		chkShowBark.setSelected(true);
		panelChronoPlotSymbols.add(chkShowBark, "flowx,cell 2 1,alignx left,aligny center");
		
		lblShowOuterRing = new JLabel("Show outer ring:");
		panelChronoPlotSymbols.add(lblShowOuterRing, "cell 3 1,alignx right,aligny center");
		
		chkShowOuterRing = new JCheckBox("");
		chkShowOuterRing.setSelected(true);
		panelChronoPlotSymbols.add(chkShowOuterRing, "flowx,cell 4 1,alignx left,aligny center");
		
		lblShowInjuryEvents = new JLabel("Show injury events:");
		panelChronoPlotSymbols.add(lblShowInjuryEvents, "cell 5 1,alignx right,aligny center");
		
		chkShowInjuryEvents = new JCheckBox("");
		chkShowInjuryEvents.setSelected(true);
		panelChronoPlotSymbols.add(chkShowInjuryEvents, "flowx,cell 6 1,alignx left,aligny center");
		
		panelChronoPlotCategories = new JPanel();
		panelChronoPlotCategories.setBorder(new TitledBorder(null, "Categories", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		chronoPlotPanel.add(panelChronoPlotCategories, "cell 0 3,grow");
		panelChronoPlotCategories.setLayout(new MigLayout("", "[180px][67][][][][]", "[][][]"));
		
		lblShowCategoryGroups = new JLabel("Show category groups on plot:");
		panelChronoPlotCategories.add(lblShowCategoryGroups, "cell 0 0,alignx right,aligny center");
		
		chkShowCategoryGroups = new JCheckBox("");
		chkShowCategoryGroups.setSelected(true);
		panelChronoPlotCategories.add(chkShowCategoryGroups, "flowx,cell 1 0,alignx left,aligny center");
		
		lblAutomaticallyColorizeSeries = new JLabel("Automatically colorize series lines:");
		panelChronoPlotCategories.add(lblAutomaticallyColorizeSeries, "cell 4 0,alignx right,aligny center");
		
		chkAutomaticallyColorizeSeries = new JCheckBox("");
		panelChronoPlotCategories.add(chkAutomaticallyColorizeSeries, "flowx,cell 5 0,alignx left,aligny center");
		
		lblShowCategoryLabels = new JLabel("Show labels on category groups:");
		panelChronoPlotCategories.add(lblShowCategoryLabels, "cell 0 1,alignx right,aligny center");
		
		chkShowCategoryLabels = new JCheckBox("");
		chkShowCategoryLabels.setSelected(true);
		panelChronoPlotCategories.add(chkShowCategoryLabels, "flowx,cell 1 1,alignx left,aligny center");
		
		lblAutomaticallyColorizeLabels = new JLabel("Automatically colorize series labels:");
		panelChronoPlotCategories.add(lblAutomaticallyColorizeLabels, "cell 4 1,alignx right,aligny center");
		
		chkAutomaticallyColorizeLabels = new JCheckBox("");
		panelChronoPlotCategories.add(chkAutomaticallyColorizeLabels, "flowx,cell 5 1,alignx left,aligny center");
		
		lblCategoryLabelFontSize = new JLabel("Category label font size:");
		panelChronoPlotCategories.add(lblCategoryLabelFontSize, "cell 0 2,alignx right,aligny center");
		
		spnCategoryLabelFontSize = new JSpinner();
		panelChronoPlotCategories.add(spnCategoryLabelFontSize, "cell 1 2,growx,aligny center");
		
		lblPt_3 = new JLabel("pt");
		panelChronoPlotCategories.add(lblPt_3, "flowx,cell 2 2,alignx left,aligny center");
		
		lblCategoryLabelJustification = new JLabel("Category label justification");
		panelChronoPlotCategories.add(lblCategoryLabelJustification, "cell 4 2,alignx trailing,aligny center");
		
		htbShowCategoryGroups = new HelpTipButton(LocalHelp.SHOW_CATEGORY_GROUPS);
		panelChronoPlotCategories.add(htbShowCategoryGroups, "cell 1 0");
		
		htbShowCategoryLabels = new HelpTipButton(LocalHelp.SHOW_CATEGORY_LABELS);
		panelChronoPlotCategories.add(htbShowCategoryLabels, "cell 1 1");
		
		htbCategoryLabelFontSize = new HelpTipButton(LocalHelp.CATEGORY_FONT_SIZE);
		panelChronoPlotCategories.add(htbCategoryLabelFontSize, "cell 2 2");
		
		cboCategoryLabelJustification = new JComboBox<>();
		panelChronoPlotCategories.add(cboCategoryLabelJustification, "flowx,cell 5 2,growx,aligny center");
		for (int i = 0; i < JustificationType.values().length; i++)
		{
			cboCategoryLabelJustification.addItem(JustificationType.values()[i]);
		}
		
		htbAutoColorizeCategoryLines = new HelpTipButton(LocalHelp.AUTO_COLOR_CATEGORY_LINES);
		panelChronoPlotCategories.add(htbAutoColorizeCategoryLines, "cell 5 0");
		
		htbAutorColorizeCategoryLabels = new HelpTipButton(LocalHelp.AUTO_COLOR_CATEGORY_LABELS);
		panelChronoPlotCategories.add(htbAutorColorizeCategoryLabels, "cell 5 1");
		
		htbCategoryLabelJustification = new HelpTipButton(LocalHelp.CATEGORY_JUSTIFICATION);
		panelChronoPlotCategories.add(htbCategoryLabelJustification, "cell 5 2");
		
		htbShowChronologyPlot = new HelpTipButton(LocalHelp.SHOW_CHRONOLOGY_PLOT);
		chronoPlotPanel.add(htbShowChronologyPlot, "cell 0 0");
		
		JPanel compositePlotPanel = new JPanel();
		tabbedPane.addTab("CompositePlot ", Builder.getImageIcon("firecompositeplot.png"), compositePlotPanel, null);
		compositePlotPanel.setLayout(new MigLayout("", "[grow]", "[][][][]"));
		
		chkCompositePlot = new JCheckBox("Show/Hide Composite Plot");
		chkCompositePlot.addActionListener(this);
		chkCompositePlot.setActionCommand("CompositePlot");
		chkCompositePlot.setFont(new Font("Dialog", Font.BOLD, 16));
		compositePlotPanel.add(chkCompositePlot, "flowx,cell 0 0");
		
		panelCompositePlotGeneral = new JPanel();
		panelCompositePlotGeneral.setBorder(new TitledBorder(null, "General", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		compositePlotPanel.add(panelCompositePlotGeneral, "cell 0 1,grow");
		panelCompositePlotGeneral.setLayout(new MigLayout("", "[180px][67][grow][]", "[][][]"));
		
		JLabel lblChartHeight2 = new JLabel("Chart height:");
		panelCompositePlotGeneral.add(lblChartHeight2, "cell 0 0,alignx right,aligny center");
		
		spnCompositePlotHeight = new JSpinner();
		spnCompositePlotHeight.setModel(new SpinnerNumberModel(70, 20, 500, 10));
		panelCompositePlotGeneral.add(spnCompositePlotHeight, "cell 1 0,growx,aligny center");
		
		JLabel lblPx_3 = new JLabel("px");
		panelCompositePlotGeneral.add(lblPx_3, "flowx,cell 2 0,alignx left,aligny center");
		
		JLabel lblCompositePlotChart = new JLabel("Composite plot chart label:");
		panelCompositePlotGeneral.add(lblCompositePlotChart, "cell 0 1,alignx right,aligny center");
		
		txtComposite = new JTextField();
		txtComposite.setText("Composite");
		panelCompositePlotGeneral.add(txtComposite, "cell 1 1 2 1,growx,aligny center");
		txtComposite.setColumns(10);
		
		htbCompositePlotTitle = new HelpTipButton(LocalHelp.SHOW_COMPOSITE_TITLE);
		panelCompositePlotGeneral.add(htbCompositePlotTitle, "cell 3 1");
		
		JLabel lblFontSize_2 = new JLabel("Font size:");
		panelCompositePlotGeneral.add(lblFontSize_2, "cell 0 2,alignx right,aligny center");
		
		spnCompositePlotLabelFontSize = new JSpinner();
		spnCompositePlotLabelFontSize.setModel(new SpinnerNumberModel(10, 4, 32, 1));
		panelCompositePlotGeneral.add(spnCompositePlotLabelFontSize, "cell 1 2,growx,aligny center");
		
		htbCompositePlotHeight = new HelpTipButton(LocalHelp.COMPOSITE_HEIGHT);
		panelCompositePlotGeneral.add(htbCompositePlotHeight, "cell 2 0");
		
		htbCompositePlotFontSize = new HelpTipButton(LocalHelp.COMPOSITE_PLOT_FONT_SIZE);
		panelCompositePlotGeneral.add(htbCompositePlotFontSize, "cell 2 2");
		
		panelCompositePlotFilters = new JPanel();
		panelCompositePlotFilters
				.setBorder(new TitledBorder(null, "Composite Filters", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		compositePlotPanel.add(panelCompositePlotFilters, "cell 0 2,grow");
		panelCompositePlotFilters.setLayout(new MigLayout("", "[180px][][][][]", "[][][][]"));
		
		cboFilterType = new JComboBox<>();
		for (int i = 0; i < FireFilterType.values().length; i++)
		{
			cboFilterType.addItem(FireFilterType.values()[i]);
		}
		
		lblCompositeBasedOn = new JLabel("Composite based on:");
		panelCompositePlotFilters.add(lblCompositeBasedOn, "cell 0 0,alignx trailing");
		
		cboEventToProcess = new JComboBox();
		new EventTypeWrapper(cboEventToProcess, PrefKey.CHART_COMPOSITE_EVENT_TYPE, EventTypeToProcess.FIRE_EVENT);
		panelCompositePlotFilters.add(cboEventToProcess, "cell 1 0 3 1,growx");
		
		htbCompositeBasedOn = new HelpTipButton(LocalHelp.COMPOSITE_BASED_ON);
		panelCompositePlotFilters.add(htbCompositeBasedOn, "cell 4 0");
		panelCompositePlotFilters.add(cboFilterType, "cell 1 1,growx,aligny center");
		
		JLabel lblValue_1 = new JLabel(">=");
		panelCompositePlotFilters.add(lblValue_1, "cell 2 1,alignx right,aligny center");
		
		spnFilterValue = new JSpinner();
		spnFilterValue.setModel(new SpinnerNumberModel(1, 1, 9999, 1));
		panelCompositePlotFilters.add(spnFilterValue, "cell 3 1,growx,aligny center");
		
		htbCompositeFilter1 = new HelpTipButton(LocalHelp.COMPOSITE_FILTER1);
		panelCompositePlotFilters.add(htbCompositeFilter1, "cell 4 1");
		
		cboSampleDepthFilterType = new JComboBox();
		new SampleDepthFilterTypeWrapper(cboSampleDepthFilterType, PrefKey.CHART_COMPOSITE_SAMPLE_DEPTH_TYPE,
				SampleDepthFilterType.MIN_NUM_SAMPLES);
		panelCompositePlotFilters.add(cboSampleDepthFilterType, "cell 1 2,growx");
		
		label_1 = new JLabel(">=");
		panelCompositePlotFilters.add(label_1, "cell 2 2");
		
		spnMinNumberOfSamples = new JSpinner();
		spnMinNumberOfSamples.setModel(new SpinnerNumberModel(1, 1, 9999, 1));
		panelCompositePlotFilters.add(spnMinNumberOfSamples, "cell 3 2,growx,aligny center");
		
		htbCompositeFilter2 = new HelpTipButton(LocalHelp.COMPOSITE_FILTER2);
		panelCompositePlotFilters.add(htbCompositeFilter2, "cell 4 2");
		
		chkShowFilterInLegend = new JCheckBox("Show filter details in legend");
		new CheckBoxWrapper(chkShowFilterInLegend, PrefKey.CHART_SHOW_FILTER_IN_LEGEND, false);
		
		panelCompositePlotFilters.add(chkShowFilterInLegend, "cell 1 3 3 1");
		
		htbShowCompositeFilterInLegend = new HelpTipButton(LocalHelp.SHOW_FILTER_IN_LEGEND);
		panelCompositePlotFilters.add(htbShowCompositeFilterInLegend, "cell 4 3");
		
		panelCompositePlotYearLabels = new JPanel();
		panelCompositePlotYearLabels.setBorder(new TitledBorder(null, "Year Labels", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		compositePlotPanel.add(panelCompositePlotYearLabels, "cell 0 3,grow");
		panelCompositePlotYearLabels.setLayout(new MigLayout("", "[180px][][][grow]", "[15px][][][][]"));
		
		JLabel lblShowHideYearLabels = new JLabel("Show / hide year labels");
		panelCompositePlotYearLabels.add(lblShowHideYearLabels, "cell 0 0,alignx right,aligny center");
		
		chkCompositeYearLabels = new JCheckBox("");
		chkCompositeYearLabels.setActionCommand("CompositeYearLabels");
		chkCompositeYearLabels.addActionListener(this);
		panelCompositePlotYearLabels.add(chkCompositeYearLabels, "flowx,cell 1 0,alignx left,aligny center");
		
		lblLabelStyle = new JLabel("Label style:");
		panelCompositePlotYearLabels.add(lblLabelStyle, "cell 0 1,alignx right,aligny center");
		
		radLongYearStyle = new JRadioButton("Long (e.g. 1996)");
		panelCompositePlotYearLabels.add(radLongYearStyle, "cell 1 1,growx,aligny center");
		
		radShortYearStyle = new JRadioButton("Short (e.g. '96)");
		radShortYearStyle.setSelected(true);
		panelCompositePlotYearLabels.add(radShortYearStyle, "flowx,cell 2 1,growx,aligny center");
		
		ButtonGroup butgroup = new ButtonGroup();
		butgroup.add(radLongYearStyle);
		butgroup.add(radShortYearStyle);
		
		lblLabelOrientation = new JLabel("Label orientation:");
		panelCompositePlotYearLabels.add(lblLabelOrientation, "cell 0 2,alignx right,aligny center");
		
		cboLabelOrientation = new JComboBox<>();
		for (int i = 0; i < LabelOrientation.values().length; i++)
		{
			cboLabelOrientation.addItem(LabelOrientation.values()[i]);
		}
		panelCompositePlotYearLabels.add(cboLabelOrientation, "flowx,cell 1 2 2 1,growx,aligny center");
		
		htbYearLabelOrientation = new HelpTipButton(LocalHelp.YEAR_LABEL_ORIENTATION);
		panelCompositePlotYearLabels.add(htbYearLabelOrientation, "cell 3 2");
		
		lblLabelPadding = new JLabel("Padding around labels:");
		panelCompositePlotYearLabels.add(lblLabelPadding, "cell 0 3,alignx right,aligny center");
		
		spnYearLabelPadding = new JSpinner();
		spnYearLabelPadding.setModel(new SpinnerNumberModel(5, 0, 50, 1));
		panelCompositePlotYearLabels.add(spnYearLabelPadding, "flowx,cell 1 3,growx,aligny center");
		
		lblPx = new JLabel("px");
		panelCompositePlotYearLabels.add(lblPx, "flowx,cell 2 3");
		
		JLabel lblFontSize_1 = new JLabel("Font size:");
		panelCompositePlotYearLabels.add(lblFontSize_1, "cell 0 4,alignx right,aligny center");
		
		spnCompositeYearLabelFontSize = new JSpinner();
		spnCompositeYearLabelFontSize.setModel(new SpinnerNumberModel(8, 4, 20, 1));
		panelCompositePlotYearLabels.add(spnCompositeYearLabelFontSize, "cell 1 4,growx,aligny center");
		
		htbShowYearLabels = new HelpTipButton(LocalHelp.SHOW_YEAR_LABELS);
		panelCompositePlotYearLabels.add(htbShowYearLabels, "cell 1 0");
		
		htbYearLabelStyle = new HelpTipButton(LocalHelp.YEAR_LABEL_STYLE);
		panelCompositePlotYearLabels.add(htbYearLabelStyle, "cell 2 1");
		
		htbYearLabelPadding = new HelpTipButton(LocalHelp.YEAR_LABEL_PADDING);
		panelCompositePlotYearLabels.add(htbYearLabelPadding, "cell 2 3");
		
		htbYearLabelFontSize = new HelpTipButton(LocalHelp.YEAR_LABEL_FONT_SIZE);
		panelCompositePlotYearLabels.add(htbYearLabelFontSize, "cell 2 4");
		
		htbShowCompositePlot = new HelpTipButton(LocalHelp.SHOW_COMPOSITE_PLOT);
		compositePlotPanel.add(htbShowCompositePlot, "cell 0 0");
		
		JPanel buttonPane = new JPanel();
		this.getContentPane().add(buttonPane, BorderLayout.SOUTH);
		buttonPane.setLayout(new MigLayout("", "[157px][][grow][][73px]", "[25px]"));
		
		JButton btnReset = new JButton("Reset to defaults");
		btnReset.setActionCommand("Reset");
		btnReset.addActionListener(this);
		
		buttonPane.add(btnReset, "cell 0 0,alignx left,aligny top");
		
		JButton btnCancel = new JButton("Cancel");
		btnCancel.addActionListener(this);
		btnCancel.setActionCommand("Cancel");
		
		JButton btnOk = new JButton("OK");
		btnOk.setActionCommand("OK");
		btnOk.addActionListener(this);
		
		chkRememberChartPrefs = new JCheckBox("Remember style after restart");
		new CheckBoxWrapper(chkRememberChartPrefs, PrefKey.CHART_REMEMBER_CHART_PREFS_AFTER_RESTART, false);
		
		buttonPane.add(chkRememberChartPrefs, "cell 1 0");
		buttonPane.add(btnOk, "cell 3 0");
		
		buttonPane.add(btnCancel, "cell 4 0,alignx left,aligny top");
		getRootPane().setDefaultButton(btnCancel);
		
		setFromPreferences();
	}
	
	/**
	 * LineStyleRenderer Class.
	 */
	private class LineStyleRenderer extends JLabel implements ListCellRenderer<LineStyle> {
		
		private static final long serialVersionUID = 1L;
		
		public LineStyleRenderer() {
			
			setOpaque(true);
			setVerticalAlignment(CENTER);
		}
		
		@Override
		public Component getListCellRendererComponent(JList<? extends LineStyle> list, LineStyle item, int index, boolean isSelected,
				boolean cellHasFocus) {
			
			// extract the component from the item's value
			this.setBackground(Color.WHITE);
			
			if (item == null)
				return this;
			
			if (item.equals(LineStyle.SOLID))
			{
				this.setIcon(Builder.getImageIcon("linestyle-solid.png"));
				this.setText(" - Solid");
			}
			else if (item.equals(LineStyle.DOTTED))
			{
				this.setIcon(Builder.getImageIcon("linestyle-dotted.png"));
				this.setText(" - Dotted");
				
			}
			else if (item.equals(LineStyle.DASHED))
			{
				this.setIcon(Builder.getImageIcon("linestyle-dashed.png"));
				this.setText(" - Dashed");
			}
			else if (item.equals(LineStyle.LONG_DASH))
			{
				this.setIcon(null);
				this.setText(" - Long dashes");
			}
			
			return this;
		}
	}
	
}
