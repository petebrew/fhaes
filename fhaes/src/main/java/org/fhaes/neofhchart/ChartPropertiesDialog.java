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
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

import org.fhaes.enums.FireFilterType;
import org.fhaes.enums.LabelOrientation;
import org.fhaes.enums.LineStyle;
import org.fhaes.preferences.App;
import org.fhaes.preferences.FHAESPreferences.PrefKey;
import org.fhaes.util.Builder;
import org.fhaes.util.FontChooserComboBox;

import net.miginfocom.swing.MigLayout;

/**
 * ChartPropertiesDialog Class.
 */
public class ChartPropertiesDialog extends JDialog implements ActionListener {
	
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
	private JSpinner spnFirstYear;
	private JSpinner spnLastYear;
	private JButton btnVerticalGuideColor;
	private JSpinner spnVerticalGuideWeight;
	private JComboBox<LineStyle> cboVerticalGuideStyle;
	private JSpinner spnMajorSpacing;
	private JCheckBox chkVerticalGuides;
	private JCheckBox chkAutoRangeXAxis;
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
	private JCheckBox chkPith;
	private JCheckBox chkBark;
	private JCheckBox chkInnerRing;
	private JCheckBox chkOuterRing;
	private JCheckBox chkFireEvent;
	private JCheckBox chkInjuryEvent;
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
	private JPanel panelIndexPlotY1;
	private JPanel panelIndexPlotY2;
	private JPanel panelChronologySymbols;
	private JPanel panelChronologySeries;
	private JPanel panelCompositeYearLabels;
	private JPanel panelCompositeFilters;
	private JPanel panelCompositeGeneral;
	private YearListModel highlightYearsModel;
	private JRadioButton radLongYearStyle;
	private JLabel lblLabelStyle;
	private JLabel lblLabelPadding;
	private JSpinner spnYearLabelPadding;
	private JLabel lblPx;
	private JLabel lblLabelOrientation;
	private JComboBox<LabelOrientation> cboLabelOrientation;
	
	// Declare local variables
	private boolean preferencesChanged;
	private JLabel lblChartTitleFontSize;
	private JSpinner spnChartTitleFontSize;
	private JLabel lblUseDefaultChartTitle;
	private JCheckBox chkUseDefaultName;
	private JLabel lblChartTitleOverrideText;
	private JTextField chartTitleOverrideText;
	
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
		
		if (this.chkAutoRangeXAxis.isSelected())
		{
			this.spnFirstYear.setEnabled(false);
			this.spnLastYear.setEnabled(false);
		}
		else
		{
			this.spnFirstYear.setEnabled(true);
			this.spnLastYear.setEnabled(true);
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
			setToDefaults();
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
		setEnablePanelComponents(panelIndexPlotY1, chkIndexPlot.isSelected());
		setEnablePanelComponents(panelIndexPlotY2, chkIndexPlot.isSelected());
		lstHightlightYears.setEnabled(chkIndexPlot.isSelected());
		
	}
	
	/**
	 * Enable/disable GUI based components based on selections.
	 */
	private void setChronologyPlotGUI() {
		
		setEnablePanelComponents(panelChronologySeries, chkChronologyPlot.isSelected());
		setEnablePanelComponents(panelChronologySymbols, chkChronologyPlot.isSelected());
		
	}
	
	/**
	 * Enable/disable GUI based components based on selections.
	 */
	private void setCompositePlotGUI() {
		
		setEnablePanelComponents(panelCompositeFilters, chkCompositePlot.isSelected());
		setEnablePanelComponents(panelCompositeGeneral, chkCompositePlot.isSelected());
		setEnablePanelComponents(panelCompositeYearLabels, chkCompositePlot.isSelected());
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
	private void setToDefaults() {
		
		// Clear preferences first then set all to default
		
		// General tab
		App.prefs.clearPref(PrefKey.CHART_SHOW_LEGEND);
		App.prefs.clearPref(PrefKey.CHART_FONT_FAMILY);
		App.prefs.clearPref(PrefKey.CHART_AXIS_X_AUTO_RANGE);
		App.prefs.clearPref(PrefKey.CHART_AXIS_X_MIN);
		App.prefs.clearPref(PrefKey.CHART_AXIS_X_MAX);
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
		
		setFromPreferences();
	}
	
	/**
	 * Save the chart properties to the preferences.
	 */
	private void saveToPreferences() {
		
		// General tab
		App.prefs.setBooleanPref(PrefKey.CHART_SHOW_CHART_TITLE, chkShowChartTitle.isSelected());
		App.prefs.setBooleanPref(PrefKey.CHART_SHOW_LEGEND, chkShowLegend.isSelected());
		App.prefs.setPref(PrefKey.CHART_FONT_FAMILY, cboFontFamily.getSelectedFontName());
		App.prefs.setBooleanPref(PrefKey.CHART_AXIS_X_AUTO_RANGE, chkAutoRangeXAxis.isSelected());
		App.prefs.setIntPref(PrefKey.CHART_AXIS_X_MIN, (Integer) spnFirstYear.getValue());
		App.prefs.setIntPref(PrefKey.CHART_AXIS_X_MAX, (Integer) spnLastYear.getValue());
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
		
		// Index plot tab
		App.prefs.setBooleanPref(PrefKey.CHART_SHOW_INDEX_PLOT, chkIndexPlot.isSelected());
		App.prefs.setIntPref(PrefKey.CHART_INDEX_PLOT_HEIGHT, (Integer) spnIndexPlotHeight.getValue());
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
		App.prefs.setBooleanPref(PrefKey.CHART_SHOW_PITH_SYMBOL, chkPith.isSelected());
		App.prefs.setBooleanPref(PrefKey.CHART_SHOW_BARK_SYMBOL, chkBark.isSelected());
		App.prefs.setBooleanPref(PrefKey.CHART_SHOW_INNER_RING_SYMBOL, chkInnerRing.isSelected());
		App.prefs.setBooleanPref(PrefKey.CHART_SHOW_OUTER_RING_SYMBOL, chkOuterRing.isSelected());
		App.prefs.setBooleanPref(PrefKey.CHART_SHOW_FIRE_EVENT_SYMBOL, chkFireEvent.isSelected());
		App.prefs.setBooleanPref(PrefKey.CHART_SHOW_INJURY_SYMBOL, chkInjuryEvent.isSelected());
		
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
		chkShowLegend.setSelected(App.prefs.getBooleanPref(PrefKey.CHART_SHOW_LEGEND, true));
		cboFontFamily.setSelectedItem(App.prefs.getPref(PrefKey.CHART_FONT_FAMILY, "Verdana"));
		chkAutoRangeXAxis.setSelected(App.prefs.getBooleanPref(PrefKey.CHART_AXIS_X_AUTO_RANGE, true));
		spnFirstYear.setValue(App.prefs.getIntPref(PrefKey.CHART_AXIS_X_MIN, 1900));
		spnLastYear.setValue(App.prefs.getIntPref(PrefKey.CHART_AXIS_X_MAX, 2000));
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
		txtAxisY1Label.setText(App.prefs.getPref(PrefKey.CHART_AXIS_Y1_LABEL, "Sample Depth"));
		txtAxisY2Label.setText(App.prefs.getPref(PrefKey.CHART_AXIS_Y2_LABEL, "% Scarred"));
		spnAxisY1FontSize.setValue(App.prefs.getIntPref(PrefKey.CHART_AXIS_Y1_FONT_SIZE, 10));
		spnAxisY2FontSize.setValue(App.prefs.getIntPref(PrefKey.CHART_AXIS_Y2_FONT_SIZE, 10));
		
		// Chronology plot tab
		chkChronologyPlot.setSelected(App.prefs.getBooleanPref(PrefKey.CHART_SHOW_CHRONOLOGY_PLOT, true));
		chkSeriesLabels.setSelected(App.prefs.getBooleanPref(PrefKey.CHART_SHOW_CHRONOLOGY_PLOT_LABELS, true));
		spnSeriesLabelFontSize.setValue(App.prefs.getIntPref(PrefKey.CHART_CHRONOLOGY_PLOT_LABEL_FONT_SIZE, 10));
		spnSeriesSpacing.setValue(App.prefs.getIntPref(PrefKey.CHART_CHRONOLOGY_PLOT_SPACING, 5));
		chkPith.setSelected(App.prefs.getBooleanPref(PrefKey.CHART_SHOW_PITH_SYMBOL, true));
		chkBark.setSelected(App.prefs.getBooleanPref(PrefKey.CHART_SHOW_BARK_SYMBOL, true));
		chkInnerRing.setSelected(App.prefs.getBooleanPref(PrefKey.CHART_SHOW_INNER_RING_SYMBOL, true));
		chkOuterRing.setSelected(App.prefs.getBooleanPref(PrefKey.CHART_SHOW_OUTER_RING_SYMBOL, true));
		chkFireEvent.setSelected(App.prefs.getBooleanPref(PrefKey.CHART_SHOW_FIRE_EVENT_SYMBOL, true));
		chkInjuryEvent.setSelected(App.prefs.getBooleanPref(PrefKey.CHART_SHOW_INJURY_SYMBOL, true));
		
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
	private void initGUI() {
		
		this.setIconImage(Builder.getApplicationIcon());
		this.setTitle("Chart Properties");
		this.setModal(true);
		
		setBounds(100, 100, 739, 695);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(new BorderLayout(0, 0));
		{
			JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
			contentPanel.add(tabbedPane, BorderLayout.CENTER);
			{
				JPanel panel = new JPanel();
				tabbedPane.addTab("Generic Options ", Builder.getImageIcon("advancedsettings.png"), panel, null);
				panel.setLayout(new MigLayout("", "[grow,right]", "[][][23.00][grow]"));
				{
					JPanel panel_1 = new JPanel();
					panel_1.setBorder(new TitledBorder(null, "General", TitledBorder.LEADING, TitledBorder.TOP, null, null));
					panel.add(panel_1, "cell 0 0,grow");
					panel_1.setLayout(new MigLayout("", "[180px][80,fill][150px,right][grow,fill]", "[30][30][30]"));
					{
					
					}
					
					lblShowLegend = new JLabel("Show legend:");
					panel_1.add(lblShowLegend, "cell 0 0,alignx right,aligny center");
					
					chkShowLegend = new JCheckBox("");
					chkShowLegend.setSelected(true);
					panel_1.add(chkShowLegend, "cell 1 0,alignx left");
					{
						JLabel lblFont = new JLabel("Font family:");
						panel_1.add(lblFont, "cell 2 0,alignx right,aligny center");
					}
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
					
					panel_1.add(cboFontFamily, "cell 3 0,grow");
					
					lblShowChartTitle = new JLabel("Show chart title:");
					panel_1.add(lblShowChartTitle, "cell 0 1,alignx right,aligny center");
					
					chkShowChartTitle = new JCheckBox("");
					chkShowChartTitle.setSelected(true);
					panel_1.add(chkShowChartTitle, "cell 1 1,alignx left");
					{
						lblChartTitleFontSize = new JLabel("Chart title font size:");
						panel_1.add(lblChartTitleFontSize, "cell 2 1,alignx right,aligny center");
					}
					{
						spnChartTitleFontSize = new JSpinner();
						panel_1.add(spnChartTitleFontSize, "cell 3 1,grow");
					}
					{
						lblUseDefaultChartTitle = new JLabel("Use default chart title:");
						panel_1.add(lblUseDefaultChartTitle, "cell 0 2,alignx right,aligny center");
					}
					{
						chkUseDefaultName = new JCheckBox("");
						chkUseDefaultName.setSelected(true);
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
						panel_1.add(chkUseDefaultName, "cell 1 2,alignx left");
					}
					{
						lblChartTitleOverrideText = new JLabel("Chart title override text:");
						panel_1.add(lblChartTitleOverrideText, "cell 2 2,alignx right,aligny center");
					}
					{
						chartTitleOverrideText = new JTextField();
						panel_1.add(chartTitleOverrideText, "cell 3 2,grow");
						chartTitleOverrideText.setColumns(10);
					}
				}
				{
					JPanel panel_1 = new JPanel();
					panel_1.setBorder(new TitledBorder(null, "X Axis", TitledBorder.LEADING, TitledBorder.TOP, null, null));
					panel.add(panel_1, "cell 0 1,grow");
					panel_1.setLayout(new MigLayout("", "[180px][67.00,fill][][62.00,fill][grow,fill]", "[][15px]"));
					{
						chkAutoRangeXAxis = new JCheckBox("Auto adjust");
						chkAutoRangeXAxis.addActionListener(new ActionListener() {
							
							@Override
							public void actionPerformed(ActionEvent arg0) {
								
								setXAxisGui();
							}
							
						});
						panel_1.add(chkAutoRangeXAxis, "cell 1 0 3 1,growx");
					}
					{
						JLabel lblNewLabel = new JLabel("Year range:");
						panel_1.add(lblNewLabel, "cell 0 0 1 2,alignx right,aligny center");
					}
					{
						spnFirstYear = new JSpinner();
						JSpinner.NumberEditor editor = new JSpinner.NumberEditor(spnFirstYear, "#");
						spnFirstYear.setEditor(editor);
						panel_1.add(spnFirstYear, "cell 1 1");
					}
					{
						JLabel lblTo = new JLabel("to");
						panel_1.add(lblTo, "cell 2 1");
					}
					{
						spnLastYear = new JSpinner();
						JSpinner.NumberEditor editor = new JSpinner.NumberEditor(spnLastYear, "#");
						spnLastYear.setEditor(editor);
						panel_1.add(spnLastYear, "cell 3 1");
					}
				}
				{
					JPanel panel_1 = new JPanel();
					panel_1.setBorder(
							new TitledBorder(null, "Ticks and vertical guides", TitledBorder.LEADING, TitledBorder.TOP, null, null));
					panel.add(panel_1, "cell 0 2,grow");
					panel_1.setLayout(new MigLayout("", "[180px,right][58:58][right][grow][][][]", "[][][]"));
					{
						JLabel lblShowhide = new JLabel("Vertical guides:");
						panel_1.add(lblShowhide, "cell 0 0");
					}
					{
						chkVerticalGuides = new JCheckBox("");
						chkVerticalGuides.setActionCommand("VerticalGuides");
						chkVerticalGuides.addActionListener(this);
						chkVerticalGuides.setSelected(true);
						panel_1.add(chkVerticalGuides, "flowx,cell 1 0");
					}
					{
						JLabel lblStyle = new JLabel("Style:");
						panel_1.add(lblStyle, "flowx,cell 2 0,alignx trailing");
					}
					{
					}
					{
						JLabel lblWeight = new JLabel("Weight:");
						panel_1.add(lblWeight, "cell 4 0,alignx right");
					}
					{
						spnVerticalGuideWeight = new JSpinner();
						spnVerticalGuideWeight.setModel(new SpinnerNumberModel(1, 1, 10, 1));
						
						panel_1.add(spnVerticalGuideWeight, "cell 5 0");
					}
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
					panel_1.add(btnVerticalGuideColor, "cell 6 0,growy");
					{
						JLabel lblYearSpacing = new JLabel("Major ticks:");
						panel_1.add(lblYearSpacing, "cell 0 1,alignx trailing");
					}
					{
						chkMajorTicks = new JCheckBox("");
						panel_1.add(chkMajorTicks, "cell 1 1");
					}
					{
						JLabel lblYearSpacing_1 = new JLabel("Year spacing:");
						panel_1.add(lblYearSpacing_1, "cell 2 1");
					}
					{
						spnMajorSpacing = new JSpinner();
						spnMajorSpacing.setModel(new SpinnerNumberModel(50, 5, 1000, 10));
						panel_1.add(spnMajorSpacing, "cell 3 1 3 1");
					}
					{
						JLabel lblMinorTicks = new JLabel("Minor ticks:");
						panel_1.add(lblMinorTicks, "cell 0 2");
					}
					{
						chkMinorTicks = new JCheckBox("");
						panel_1.add(chkMinorTicks, "cell 1 2");
					}
					{
						JLabel label = new JLabel("Year spacing:");
						panel_1.add(label, "cell 2 2");
					}
					{
						spnMinorSpacing = new JSpinner();
						spnMinorSpacing.setModel(new SpinnerNumberModel(10, 1, 1000, 1));
						panel_1.add(spnMinorSpacing, "cell 3 2");
					}
					{
						cboVerticalGuideStyle = new JComboBox<LineStyle>();
						
						for (int i = 0; i < LineStyle.values().length; i++)
						{
							cboVerticalGuideStyle.addItem(LineStyle.values()[i]);
						}
						
						cboVerticalGuideStyle.setRenderer(new LineStyleRenderer());
						panel_1.add(cboVerticalGuideStyle, "cell 3 0,growx");
					}
				}
				{
					JPanel panel_1 = new JPanel();
					panel_1.setBorder(new TitledBorder(null, "Highlighted years", TitledBorder.LEADING, TitledBorder.TOP, null, null));
					panel.add(panel_1, "cell 0 3,grow");
					panel_1.setLayout(new MigLayout("", "[180,right][58:58.00][][grow][][][fill]", "[fill][top][top][][grow]"));
					{
						JLabel lblShowHighlights = new JLabel("Show highlighted years:");
						panel_1.add(lblShowHighlights, "cell 0 0");
					}
					{
						chkHighlightYears = new JCheckBox("");
						chkHighlightYears.setActionCommand("HighlightYears");
						chkHighlightYears.addActionListener(this);
						panel_1.add(chkHighlightYears, "cell 1 0");
					}
					{
						JLabel lblStyle_1 = new JLabel("Style:");
						panel_1.add(lblStyle_1, "cell 2 0,alignx trailing");
					}
					{
						cboHighlightStyle = new JComboBox<LineStyle>();
						
						for (int i = 0; i < LineStyle.values().length; i++)
						{
							cboHighlightStyle.addItem(LineStyle.values()[i]);
						}
						
						cboHighlightStyle.setRenderer(new LineStyleRenderer());
						panel_1.add(cboHighlightStyle, "flowx,cell 3 0,growx");
					}
					{
						JLabel lblNewLabel_2 = new JLabel("Weight:");
						panel_1.add(lblNewLabel_2, "cell 4 0");
					}
					{
						spnHighlightWeight = new JSpinner();
						spnHighlightWeight.setModel(new SpinnerNumberModel(1, 1, 10, 1));
						panel_1.add(spnHighlightWeight, "cell 5 0");
					}
					{
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
						panel_1.add(btnHighlightColor, "cell 6 0");
					}
					{
						JLabel lblHighlightYears = new JLabel("Highlight years:");
						panel_1.add(lblHighlightYears, "cell 0 1");
					}
					{
						JScrollPane scrollPane = new JScrollPane();
						panel_1.add(scrollPane, "cell 1 1 5 4,grow");
						{
							lstHightlightYears = new JList<Integer>();
							lstHightlightYears.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
							lstHightlightYears.setLayoutOrientation(JList.HORIZONTAL_WRAP);
							lstHightlightYears.setVisibleRowCount(-1);
							this.highlightYearsModel = new YearListModel();
							lstHightlightYears.setModel(highlightYearsModel);
							scrollPane.setViewportView(lstHightlightYears);
						}
					}
					{
						btnAddYear = new JButton("");
						btnAddYear.setIcon(Builder.getImageIcon("edit_add.png"));
						btnAddYear.setActionCommand("AddHighlightYear");
						btnAddYear.addActionListener(this);
						panel_1.add(btnAddYear, "cell 6 1");
					}
					{
						btnRemoveYear = new JButton("");
						btnRemoveYear.setIcon(Builder.getImageIcon("delete.png"));
						btnRemoveYear.setActionCommand("RemoveHighlightYear");
						btnRemoveYear.addActionListener(this);
						panel_1.add(btnRemoveYear, "cell 6 2");
					}
				}
			}
			{
				panelIndexPlotComponents = new JPanel();
				panelIndexPlotComponents
						.setBorder(new TitledBorder(null, "Components", TitledBorder.LEADING, TitledBorder.TOP, null, null));
				JPanel panelIndex2 = new JPanel();
				
				panelIndex2.setLayout(new MigLayout("", "[432px,grow,fill]", "[][][130px][]"));
				{
					chkIndexPlot = new JCheckBox("Show/Hide Index Plot");
					chkIndexPlot.setActionCommand("IndexPlot");
					chkIndexPlot.addActionListener(this);
					chkIndexPlot.setFont(new Font("Dialog", Font.BOLD, 16));
					panelIndex2.add(chkIndexPlot, "cell 0 0");
				}
				{
					panelIndexPlotGeneral = new JPanel();
					panelIndexPlotGeneral.setBorder(new TitledBorder(null, "General", TitledBorder.LEADING, TitledBorder.TOP, null, null));
					panelIndex2.add(panelIndexPlotGeneral, "cell 0 1,grow");
					panelIndexPlotGeneral.setLayout(new MigLayout("", "[200px][][]", "[]"));
					{
						JLabel lblChartHeight = new JLabel("Chart height:");
						panelIndexPlotGeneral.add(lblChartHeight, "cell 0 0,alignx right,aligny center");
					}
					{
						spnIndexPlotHeight = new JSpinner();
						spnIndexPlotHeight.setModel(new SpinnerNumberModel(100, 20, 500, 1));
						panelIndexPlotGeneral.add(spnIndexPlotHeight, "cell 1 0");
					}
					{
						JLabel lblPx = new JLabel("px");
						panelIndexPlotGeneral.add(lblPx, "cell 2 0");
					}
				}
				panelIndex2.add(panelIndexPlotComponents, "cell 0 2,alignx left,aligny top");
				tabbedPane.addTab("Index Plot ", Builder.getImageIcon("fireindexplot.png"), panelIndex2, null);
				panelIndexPlotComponents.setLayout(new MigLayout("", "[200px,right][][][][61.00]", "[][][][]"));
				{
					JLabel lblSampleDepth = new JLabel("Sample depth:");
					panelIndexPlotComponents.add(lblSampleDepth, "cell 0 0");
				}
				ButtonGroup group = new ButtonGroup();
				{
					chkSampleDepth = new JRadioButton("");
					chkSampleDepth.addActionListener(new ActionListener() {
						
						@Override
						public void actionPerformed(ActionEvent arg0) {
							
							tryAutoSetYAxisLabel();
						}
					});
					group.add(chkSampleDepth);
					panelIndexPlotComponents.add(chkSampleDepth, "cell 1 0");
				}
				{
					btnSampleDepthColor = new JButton("Color");
					setComponentColours(btnSampleDepthColor,
							App.prefs.getColorPref(PrefKey.CHART_SAMPLE_OR_RECORDER_DEPTH_COLOR, Color.BLUE));
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
					panelIndexPlotComponents.add(btnSampleDepthColor, "cell 2 0 1 2");
				}
				{
					JLabel lblRecorderDepth = new JLabel("Recorder depth:");
					panelIndexPlotComponents.add(lblRecorderDepth, "cell 0 1");
				}
				{
					chkRecorderDepth = new JRadioButton("");
					chkRecorderDepth.addActionListener(new ActionListener() {
						
						@Override
						public void actionPerformed(ActionEvent arg0) {
							
							tryAutoSetYAxisLabel();
						}
					});
					
					group.add(chkRecorderDepth);
					panelIndexPlotComponents.add(chkRecorderDepth, "cell 1 1");
				}
				{
					JLabel lblPercentScarred = new JLabel("Percent scarred:");
					panelIndexPlotComponents.add(lblPercentScarred, "cell 0 2");
				}
				{
					chkPercentScarred = new JCheckBox("");
					
					panelIndexPlotComponents.add(chkPercentScarred, "cell 1 2");
				}
				{
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
					
					panelIndexPlotComponents.add(btnPercentScarredColor, "cell 2 2");
				}
				{
					JLabel lblSamplerecorderThreshold = new JLabel("Sample / recorder threshold:");
					panelIndexPlotComponents.add(lblSamplerecorderThreshold, "cell 0 3");
				}
				{
					chkSampleThreshold = new JCheckBox("");
					panelIndexPlotComponents.add(chkSampleThreshold, "cell 1 3");
				}
				{
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
					panelIndexPlotComponents.add(btnThresholdColor, "cell 2 3");
				}
				{
					JLabel lblValue = new JLabel("Value:");
					panelIndexPlotComponents.add(lblValue, "cell 3 3");
				}
				{
					spnThresholdValue = new JSpinner();
					spnThresholdValue.setModel(new SpinnerNumberModel(new Integer(10), new Integer(1), null, new Integer(1)));
					panelIndexPlotComponents.add(spnThresholdValue, "cell 4 3,growx");
				}
				{
					panelIndexPlotY1 = new JPanel();
					panelIndexPlotY1.setBorder(new TitledBorder(null, "Axis Y1", TitledBorder.LEADING, TitledBorder.TOP, null, null));
					panelIndex2.add(panelIndexPlotY1, "flowx,cell 0 3,grow");
					panelIndexPlotY1.setLayout(new MigLayout("", "[][grow]", "[][]"));
					{
						JLabel lblLabel = new JLabel("Label:");
						panelIndexPlotY1.add(lblLabel, "cell 0 0,alignx trailing");
					}
					{
						txtAxisY1Label = new JTextField();
						panelIndexPlotY1.add(txtAxisY1Label, "cell 1 0,growx");
						txtAxisY1Label.setColumns(10);
					}
					{
						JLabel lblFontSize = new JLabel("Font size:");
						panelIndexPlotY1.add(lblFontSize, "cell 0 1");
					}
					{
						spnAxisY1FontSize = new JSpinner();
						spnAxisY1FontSize.setModel(new SpinnerNumberModel(10, 4, 64, 1));
						panelIndexPlotY1.add(spnAxisY1FontSize, "flowx,cell 1 1");
					}
					{
						JLabel lblPt = new JLabel("pt");
						panelIndexPlotY1.add(lblPt, "cell 1 1");
					}
				}
				{
					panelIndexPlotY2 = new JPanel();
					panelIndexPlotY2.setBorder(new TitledBorder(null, "Axis Y2", TitledBorder.LEADING, TitledBorder.TOP, null, null));
					panelIndex2.add(panelIndexPlotY2, "cell 0 3,grow");
					panelIndexPlotY2.setLayout(new MigLayout("", "[][grow]", "[][]"));
					{
						JLabel label = new JLabel("Label:");
						panelIndexPlotY2.add(label, "cell 0 0,alignx trailing");
					}
					{
						txtAxisY2Label = new JTextField();
						txtAxisY2Label.setColumns(10);
						panelIndexPlotY2.add(txtAxisY2Label, "cell 1 0,growx");
					}
					{
						JLabel label = new JLabel("Font size:");
						panelIndexPlotY2.add(label, "cell 0 1");
					}
					{
						spnAxisY2FontSize = new JSpinner();
						spnAxisY2FontSize.setModel(new SpinnerNumberModel(10, 4, 64, 1));
						panelIndexPlotY2.add(spnAxisY2FontSize, "flowx,cell 1 1");
					}
					{
						JLabel lblPt_1 = new JLabel("pt");
						panelIndexPlotY2.add(lblPt_1, "cell 1 1");
					}
				}
			}
			{
				JPanel panel = new JPanel();
				tabbedPane.addTab("Chronology Plot ", Builder.getImageIcon("firechronologyplot.png"), panel, null);
				panel.setLayout(new MigLayout("", "[grow]", "[][][][grow]"));
				{
					chkChronologyPlot = new JCheckBox("Show/Hide Chronology Plot");
					chkChronologyPlot.setActionCommand("ChronologyPlot");
					chkChronologyPlot.addActionListener(this);
					chkChronologyPlot.setFont(new Font("Dialog", Font.BOLD, 16));
					panel.add(chkChronologyPlot, "cell 0 0");
				}
				{
					panelChronologySeries = new JPanel();
					panelChronologySeries.setBorder(new TitledBorder(null, "Series", TitledBorder.LEADING, TitledBorder.TOP, null, null));
					panel.add(panelChronologySeries, "cell 0 1,grow");
					panelChronologySeries.setLayout(new MigLayout("", "[97px,right][grow][]", "[][][][]"));
					{
						chkSeriesLabels = new JCheckBox("Show/hide labels");
						chkSeriesLabels.setSelected(true);
						panelChronologySeries.add(chkSeriesLabels, "cell 1 0");
					}
					{
						JButton btnChooseSeriesToPlot = new JButton("Choose series to plot");
						btnChooseSeriesToPlot.addActionListener(new ActionListener() {
							
							@Override
							public void actionPerformed(ActionEvent arg0) {
								
								SeriesListDialog.showDialog(neoFHChart.currentChart, neoFHChart.svgCanvas);
							}
						});
						panelChronologySeries.add(btnChooseSeriesToPlot, "flowx,cell 1 1");
					}
					{
						JLabel label = new JLabel("Font size:");
						panelChronologySeries.add(label, "cell 0 2");
					}
					{
						spnSeriesLabelFontSize = new JSpinner();
						spnSeriesLabelFontSize.setModel(new SpinnerNumberModel(8, 4, 64, 1));
						panelChronologySeries.add(spnSeriesLabelFontSize, "flowx,cell 1 2");
					}
					{
						JLabel lblSpacing = new JLabel("Spacing:");
						panelChronologySeries.add(lblSpacing, "cell 0 3");
					}
					{
						spnSeriesSpacing = new JSpinner();
						spnSeriesSpacing.setModel(new SpinnerNumberModel(5, 0, 50, 1));
						panelChronologySeries.add(spnSeriesSpacing, "flowx,cell 1 3");
					}
					{
						JLabel lblPt_2 = new JLabel("pt");
						panelChronologySeries.add(lblPt_2, "cell 1 2");
					}
					{
						JLabel lblPx_1 = new JLabel("px");
						panelChronologySeries.add(lblPx_1, "cell 1 3");
					}
				}
				{
					panelChronologySymbols = new JPanel();
					panelChronologySymbols.setBorder(new TitledBorder(null, "Symbols", TitledBorder.LEADING, TitledBorder.TOP, null, null));
					panel.add(panelChronologySymbols, "cell 0 2,grow");
					panelChronologySymbols.setLayout(new MigLayout("", "[97px][][][grow]", "[][]"));
					{
						chkPith = new JCheckBox("Pith");
						chkPith.setSelected(true);
						panelChronologySymbols.add(chkPith, "cell 1 0");
					}
					{
						chkInnerRing = new JCheckBox("Inner ring");
						chkInnerRing.setSelected(true);
						panelChronologySymbols.add(chkInnerRing, "cell 2 0");
					}
					{
						chkFireEvent = new JCheckBox("Fire event");
						chkFireEvent.setSelected(true);
						panelChronologySymbols.add(chkFireEvent, "cell 3 0");
					}
					{
						chkBark = new JCheckBox("Bark");
						chkBark.setSelected(true);
						panelChronologySymbols.add(chkBark, "cell 1 1");
					}
					{
						chkOuterRing = new JCheckBox("Outer ring");
						chkOuterRing.setSelected(true);
						panelChronologySymbols.add(chkOuterRing, "cell 2 1");
					}
					{
						chkInjuryEvent = new JCheckBox("Injury event");
						chkInjuryEvent.setSelected(true);
						panelChronologySymbols.add(chkInjuryEvent, "cell 3 1");
					}
				}
			}
			{
				JPanel panel = new JPanel();
				tabbedPane.addTab("CompositePlot ", Builder.getImageIcon("firecompositeplot.png"), panel, null);
				panel.setLayout(new MigLayout("", "[grow]", "[][][][]"));
				{
					chkCompositePlot = new JCheckBox("Show/Hide Composite Plot");
					chkCompositePlot.addActionListener(this);
					chkCompositePlot.setActionCommand("CompositePlot");
					chkCompositePlot.setFont(new Font("Dialog", Font.BOLD, 16));
					panel.add(chkCompositePlot, "cell 0 0");
				}
				{
					panelCompositeGeneral = new JPanel();
					panelCompositeGeneral.setBorder(new TitledBorder(null, "General", TitledBorder.LEADING, TitledBorder.TOP, null, null));
					panel.add(panelCompositeGeneral, "cell 0 1,grow");
					panelCompositeGeneral.setLayout(new MigLayout("", "[190px,right][][grow]", "[][][]"));
					{
						JLabel label = new JLabel("Chart height:");
						panelCompositeGeneral.add(label, "cell 0 0");
					}
					{
						spnCompositePlotHeight = new JSpinner();
						spnCompositePlotHeight.setModel(new SpinnerNumberModel(70, 20, 500, 10));
						panelCompositeGeneral.add(spnCompositePlotHeight, "cell 1 0");
					}
					{
						JLabel lblPx_2 = new JLabel("px");
						panelCompositeGeneral.add(lblPx_2, "cell 2 0");
					}
					
					JLabel lblCompositePlotChart = new JLabel("Composite plot chart label:");
					panelCompositeGeneral.add(lblCompositePlotChart, "cell 0 1,alignx trailing");
					
					txtComposite = new JTextField();
					txtComposite.setText("Composite");
					panelCompositeGeneral.add(txtComposite, "cell 1 1 2 1,growx");
					txtComposite.setColumns(10);
					
					JLabel lblFontSize_2 = new JLabel("Font size:");
					panelCompositeGeneral.add(lblFontSize_2, "cell 0 2");
					
					spnCompositePlotLabelFontSize = new JSpinner();
					spnCompositePlotLabelFontSize.setModel(new SpinnerNumberModel(10, 4, 32, 1));
					panelCompositeGeneral.add(spnCompositePlotLabelFontSize, "cell 1 2");
				}
				{
					panelCompositeFilters = new JPanel();
					panelCompositeFilters.setBorder(new TitledBorder(UIManager.getBorder("EditorPane.border"), "Composite Filters",
							TitledBorder.LEADING, TitledBorder.TOP, null, null));
					panel.add(panelCompositeFilters, "cell 0 2,grow");
					panelCompositeFilters.setLayout(new MigLayout("", "[190px,right][grow]", "[][][]"));
					{
						JLabel lblFilterType = new JLabel("Filter type:");
						panelCompositeFilters.add(lblFilterType, "cell 0 0,alignx trailing");
					}
					{
						cboFilterType = new JComboBox<FireFilterType>();
						
						for (int i = 0; i < FireFilterType.values().length; i++)
						{
							cboFilterType.addItem(FireFilterType.values()[i]);
						}
						
						panelCompositeFilters.add(cboFilterType, "cell 1 0");
					}
					{
						JLabel lblValue_1 = new JLabel("Value:");
						panelCompositeFilters.add(lblValue_1, "cell 0 1");
					}
					{
						spnFilterValue = new JSpinner();
						spnFilterValue.setModel(new SpinnerNumberModel(1, 1, 9999, 1));
						panelCompositeFilters.add(spnFilterValue, "cell 1 1");
					}
					{
						JLabel lblMinNoOf = new JLabel("Min. number of samples:");
						panelCompositeFilters.add(lblMinNoOf, "cell 0 2");
					}
					{
						spnMinNumberOfSamples = new JSpinner();
						spnMinNumberOfSamples.setModel(new SpinnerNumberModel(1, 1, 9999, 1));
						panelCompositeFilters.add(spnMinNumberOfSamples, "cell 1 2");
					}
				}
				{
					panelCompositeYearLabels = new JPanel();
					panelCompositeYearLabels
							.setBorder(new TitledBorder(null, "Year labels", TitledBorder.LEADING, TitledBorder.TOP, null, null));
					panel.add(panelCompositeYearLabels, "cell 0 3,grow");
					panelCompositeYearLabels.setLayout(new MigLayout("", "[190px][][]", "[15px][][][][]"));
					{
						JLabel lblShowhideLabels = new JLabel("Show/hide year labels");
						panelCompositeYearLabels.add(lblShowhideLabels, "cell 0 0,alignx right,aligny center");
					}
					{
						chkCompositeYearLabels = new JCheckBox("");
						chkCompositeYearLabels.setActionCommand("CompositeYearLabels");
						chkCompositeYearLabels.addActionListener(this);
						panelCompositeYearLabels.add(chkCompositeYearLabels, "cell 1 0");
					}
					{
						lblLabelStyle = new JLabel("Label style:");
						panelCompositeYearLabels.add(lblLabelStyle, "cell 0 1,alignx right");
					}
					{
						radLongYearStyle = new JRadioButton("Long (e.g. 1996)");
						panelCompositeYearLabels.add(radLongYearStyle, "cell 1 1");
					}
					{
						radShortYearStyle = new JRadioButton("Short (e.g. '96)");
						radShortYearStyle.setSelected(true);
						panelCompositeYearLabels.add(radShortYearStyle, "cell 2 1");
						
						ButtonGroup butgroup = new ButtonGroup();
						butgroup.add(radLongYearStyle);
						butgroup.add(radShortYearStyle);
					}
					{
						lblLabelOrientation = new JLabel("Label orientation:");
						panelCompositeYearLabels.add(lblLabelOrientation, "cell 0 2,alignx trailing");
					}
					{
						cboLabelOrientation = new JComboBox<LabelOrientation>();
						
						for (int i = 0; i < LabelOrientation.values().length; i++)
						{
							cboLabelOrientation.addItem(LabelOrientation.values()[i]);
						}
						
						panelCompositeYearLabels.add(cboLabelOrientation, "cell 1 2 2 1");
					}
					{
						lblLabelPadding = new JLabel("Padding around labels:");
						panelCompositeYearLabels.add(lblLabelPadding, "cell 0 3,alignx right");
					}
					{
						spnYearLabelPadding = new JSpinner();
						spnYearLabelPadding.setModel(new SpinnerNumberModel(5, 0, 50, 1));
						panelCompositeYearLabels.add(spnYearLabelPadding, "flowx,cell 1 3");
					}
					
					{
						JLabel lblFontSize_1 = new JLabel("Font size:");
						panelCompositeYearLabels.add(lblFontSize_1, "cell 0 4,alignx right");
					}
					{
						spnCompositeYearLabelFontSize = new JSpinner();
						spnCompositeYearLabelFontSize.setModel(new SpinnerNumberModel(8, 4, 20, 1));
						panelCompositeYearLabels.add(spnCompositeYearLabelFontSize, "cell 1 4");
					}
					{
						lblPx = new JLabel("px");
						panelCompositeYearLabels.add(lblPx, "cell 1 3");
					}
				}
			}
		}
		{
			JPanel buttonPane = new JPanel();
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			buttonPane.setLayout(new MigLayout("", "[157px][grow][][73px]", "[25px]"));
			{
				JButton btnReset = new JButton("Reset to defaults");
				btnReset.setActionCommand("Reset");
				btnReset.addActionListener(this);
				
				buttonPane.add(btnReset, "cell 0 0,alignx left,aligny top");
			}
			{
				JButton btnCancel = new JButton("Cancel");
				btnCancel.addActionListener(this);
				btnCancel.setActionCommand("Cancel");
				{
					JButton btnOk = new JButton("OK");
					btnOk.setActionCommand("OK");
					btnOk.addActionListener(this);
					buttonPane.add(btnOk, "cell 2 0");
				}
				buttonPane.add(btnCancel, "cell 3 0,alignx left,aligny top");
				
				getRootPane().setDefaultButton(btnCancel);
			}
		}
		
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
