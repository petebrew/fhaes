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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.ParseException;

import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.JViewport;
import javax.swing.ScrollPaneConstants;
import javax.swing.Scrollable;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingWorker;
import javax.swing.border.LineBorder;
import javax.swing.border.MatteBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.text.NumberFormatter;

import org.fhaes.FHRecorder.controller.EventController;
import org.fhaes.FHRecorder.controller.FileController;
import org.fhaes.FHRecorder.controller.IOController;
import org.fhaes.FHRecorder.controller.RecordingController;
import org.fhaes.FHRecorder.controller.SampleController;
import org.fhaes.FHRecorder.model.FHX2_FileRequiredPart;
import org.fhaes.FHRecorder.model.FHX2_Sample;
import org.fhaes.FHRecorder.util.SampleSorters;
import org.fhaes.util.Builder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.miginfocom.swing.MigLayout;

/**
 * SampleInputPanel Class. User interface for entering and managing fire history sample information.
 * 
 * @author Alex Beatty, Clayton Bodendein, Kyle Hartmann, Scott Goble
 */
public class SampleInputPanel extends javax.swing.JPanel implements ChangeListener, PropertyChangeListener {
	
	private static final Logger log = LoggerFactory.getLogger(SampleInputPanel.class);
	
	private static final long serialVersionUID = 1L;;
	
	public static final int MANUAL_SORTING = 0;
	public static final int NAME_ASCENDING = 1;
	public static final int NAME_DESCENDING = 2;
	public static final int FIRST_YEAR_ASCENDING = 3;
	public static final int FIRST_YEAR_DESCENDING = 4;
	public static final int LAST_YEAR_ASCENDING = 5;
	public static final int LAST_YEAR_DESCENDING = 6;
	
	private EventTable eventTable;
	private RecordingTable recordingTable;
	
	private javax.swing.JButton deleteSampleButton;
	private javax.swing.JMenuItem jMenuItem1;
	private javax.swing.JMenuItem jMenuItem2;
	private javax.swing.JPopupMenu jPopupMenu1;
	private javax.swing.JButton newSampleButton;
	private JPanel sampleListPanel;
	private JPanel sampleDataPanel;
	private JSplitPane splitPane;
	private JPanel buttonsPanel;
	private JScrollPane sampleScrollPane;
	private JPanel sortSamplesPanel;
	private JLabel sortByLabel;
	private JButton moveDownButton;
	private JButton moveUpButton;
	private JPanel headerPanel;
	private JPanel sampleNameContainer;
	private JLabel sampleNameLabel;
	private JTextField sampleNameTextBox;
	private JSpinner firstYearSpinner;
	private JSpinner lastYearSpinner;
	private JLabel firstYearLabel;
	private JLabel lastYearLabel;
	private FHX2_FileRequiredPart inReqPart;
	private JPanel progressBarContainer;
	private JProgressBar progressBar;
	private DrawEventPanelTask task;
	private JScrollPane eventScrollPane;
	private JScrollPane recordingScrollPane;
	private JButton addEventButton;
	private JButton addRecordingButton;
	private JButton deleteEventButton;
	private JButton deleteRecordingButton;
	private JButton consolidateButton;
	private JButton autoPopulateButton;
	
	@SuppressWarnings("rawtypes")
	protected JList sampleListBox;
	@SuppressWarnings("rawtypes")
	private JComboBox sortByComboBox;
	
	private static JCheckBox pithCheckBox;
	private static JCheckBox barkCheckBox;
	
	private int previousValueFYS; // previous value of first year spinner
	private int previousValueLYS; // previous value of last year spinner
	
	private boolean justUpdatedFYS = false;
	private boolean justUpdatedLYS = false;
	private boolean firstTimeLoading = true;
	private boolean ignoreEventsFlag = false;
	private boolean needToRefreshPanel = false;
	private boolean selectedSampleIndexChanged = false;
	private boolean done;
	
	/**
	 * Constructor for SampleinputPanel
	 */
	public SampleInputPanel() {
		
		initComponents();
	}
	
	/**
	 * Alternative constructor for SampleInputPanel.
	 * 
	 * @param inReqPart
	 */
	public SampleInputPanel(FHX2_FileRequiredPart inReqPart) {
		
		this.inReqPart = inReqPart;
		initComponents();
		redrawSampleListPanel();
		headerPanel.setVisible(false);
		inReqPart.addChangeListener(this);
	}
	
	/**
	 * Initializes the GUI components.
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void initComponents() {
		
		jPopupMenu1 = new javax.swing.JPopupMenu();
		jMenuItem1 = new javax.swing.JMenuItem();
		jMenuItem2 = new javax.swing.JMenuItem();
		
		jMenuItem1.setText("jMenuItem1");
		jPopupMenu1.add(jMenuItem1);
		
		jMenuItem2.setText("jMenuItem2");
		jPopupMenu1.add(jMenuItem2);
		
		setMinimumSize(new java.awt.Dimension(790, 450));
		setPreferredSize(new Dimension(1024, 768));
		addComponentListener(new java.awt.event.ComponentAdapter() {
			
			@Override
			public void componentShown(java.awt.event.ComponentEvent evt) {
				
				newSampleButton.requestFocusInWindow();
			}
		});
		
		setLayout(new MigLayout("", "[grow,fill]", "[grow]"));
		
		splitPane = new JSplitPane();
		splitPane.setContinuousLayout(true);
		splitPane.setResizeWeight(0.01);
		add(splitPane, "cell 0 0,grow");
		
		sampleListPanel = new JPanel();
		splitPane.setLeftComponent(sampleListPanel);
		splitPane.setDividerLocation(320);
		sampleListPanel.setBorder(new TitledBorder(new LineBorder(new Color(171, 173, 179)), "Samples in the currently loaded file:",
				TitledBorder.LEADING, TitledBorder.TOP, null, null));
		sampleListPanel.setLayout(new MigLayout("insets 2", "[27.00,grow,left]", "[:35:35,top][grow,fill][:35:35,top]"));
		
		buttonsPanel = new JPanel();
		sampleListPanel.add(buttonsPanel, "cell 0 0,grow");
		buttonsPanel.setLayout(new MigLayout("insets 0", "[50:50:50][50:50:50][grow][50:50:50][50:50:50]", "[:35:35,center]"));
		
		moveDownButton = new JButton();
		moveDownButton.setEnabled(false);
		moveDownButton.setIcon(Builder.getImageIcon("go_down.png"));
		moveDownButton.setToolTipText("Move current sample down in the list");
		moveDownButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				
				if (IOController.getFile().getRequiredPart().getNumSamples() >= 2
						&& SampleController.getSelectedSampleIndex() != IOController.getFile().getRequiredPart().getNumSamples() - 1)
				{
					SampleController.swapSamples(SampleController.getSelectedSampleIndex(), SampleController.getSelectedSampleIndex() + 1);
					redrawSampleListPanel();
					redrawSampleDataPanel(sampleListBox.getSelectedIndex());
				}
			}
			
		});
		
		buttonsPanel.add(moveDownButton, "cell 0 0,grow");
		
		moveUpButton = new JButton();
		moveUpButton.setEnabled(false);
		moveUpButton.setIcon(Builder.getImageIcon("go_up.png"));
		moveUpButton.setToolTipText("Move current sample up in the list");
		moveUpButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				
				if (IOController.getFile().getRequiredPart().getNumSamples() >= 2 && SampleController.getSelectedSampleIndex() != 0)
				{
					SampleController.swapSamples(SampleController.getSelectedSampleIndex(), SampleController.getSelectedSampleIndex() - 1);
					redrawSampleListPanel();
					redrawSampleDataPanel(sampleListBox.getSelectedIndex());
				}
			}
			
		});
		
		buttonsPanel.add(moveUpButton, "cell 1 0,grow");
		newSampleButton = new javax.swing.JButton();
		buttonsPanel.add(newSampleButton, "cell 3 0,grow");
		newSampleButton.setIcon(Builder.getImageIcon("edit_add.png"));
		newSampleButton.setToolTipText("Add new sample to this data set");
		newSampleButton.addActionListener(new java.awt.event.ActionListener() {
			
			@Override
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				
				newSampleButtonActionPerformed(evt);
			}
		});
		
		deleteSampleButton = new javax.swing.JButton();
		buttonsPanel.add(deleteSampleButton, "cell 4 0,grow");
		deleteSampleButton.setEnabled(false);
		deleteSampleButton.setIcon(Builder.getImageIcon("delete.png"));
		deleteSampleButton.setToolTipText("Delete selected sample from data set");
		deleteSampleButton.addActionListener(new java.awt.event.ActionListener() {
			
			@Override
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				
				deleteSampleButtonActionPerformed(evt);
			}
		});
		
		sampleScrollPane = new JScrollPane();
		sampleListPanel.add(sampleScrollPane, "cell 0 1,grow");
		
		sampleListBox = new JList();
		sampleListBox.addListSelectionListener(new ListSelectionListener() {
			
			@Override
			public void valueChanged(ListSelectionEvent arg0) {
				
				handleUpdatedIndex();
			}
		});
		
		sampleScrollPane.setViewportView(sampleListBox);
		sampleListBox.setModel(new DefaultListModel());
		
		sortSamplesPanel = new JPanel();
		sampleListPanel.add(sortSamplesPanel, "cell 0 2,grow");
		sortSamplesPanel.setLayout(new MigLayout("insets 0", "[:2:2][56px][32px,grow,fill]", "[30:30:30]"));
		
		sortByLabel = new JLabel("Sort samples:");
		sortSamplesPanel.add(sortByLabel, "cell 1 0,grow");
		
		sortByComboBox = new JComboBox();
		sortByComboBox.setEnabled(false);
		
		sortByComboBox.setModel(new DefaultComboBoxModel(new String[] { "Manually", "Name (ascending)", "Name (descending)",
				"First Year (ascending)", "First Year (descending)", "Last Year (ascending)", "Last Year (descending)" }));
				
		sortByComboBox.addItemListener(new ItemListener() {
			
			@Override
			public void itemStateChanged(ItemEvent arg0) {
				
				if (IOController.getFile() == null)
					return;
					
				int index = sortByComboBox.getSelectedIndex();
				
				if (index == MANUAL_SORTING) // Manual mode; no sorting applied
				{
					if (IOController.getFile().getRequiredPart().getNumSamples() < 2)
					{
						moveDownButton.setEnabled(false);
						moveUpButton.setEnabled(false);
					}
					else if (sampleListBox.getSelectedIndex() == 0)
					{
						moveDownButton.setEnabled(true);
						moveUpButton.setEnabled(false);
					}
					else if (sampleListBox.getSelectedIndex() == IOController.getFile().getRequiredPart().getNumSamples() - 1)
					{
						moveDownButton.setEnabled(false);
						moveUpButton.setEnabled(true);
					}
					else if (sampleListBox.getSelectedIndex() > -1)
					{
						moveDownButton.setEnabled(true);
						moveUpButton.setEnabled(true);
					}
					return;
				}
				else
				{
					if (index == NAME_ASCENDING)
						SampleSorters.sortSampleNameAscending();
					else if (index == NAME_DESCENDING)
						SampleSorters.sortSampleNameDescending();
					else if (index == FIRST_YEAR_ASCENDING)
						SampleSorters.sortSampleFirstYearAscending();
					else if (index == FIRST_YEAR_DESCENDING)
						SampleSorters.sortSampleFirstYearDescending();
					else if (index == LAST_YEAR_ASCENDING)
						SampleSorters.sortSampleLastYearAscending();
					else if (index == LAST_YEAR_DESCENDING)
						SampleSorters.sortSampleLastYearDescending();
					moveDownButton.setEnabled(false);
					moveUpButton.setEnabled(false);
				}
				redrawSampleListPanel();
				redrawSampleDataPanel(sampleListBox.getSelectedIndex());
			}
		});
		
		sortSamplesPanel.add(sortByComboBox, "cell 2 0,grow");
		
		sampleDataPanel = new JPanel();
		splitPane.setRightComponent(sampleDataPanel);
		
		sampleDataPanel.setMinimumSize(new Dimension(585, 0));
		sampleDataPanel.setBorder(new TitledBorder(new LineBorder(new Color(171, 173, 179)), "No sample data to display:",
				TitledBorder.LEADING, TitledBorder.TOP, null, null));
		sampleDataPanel.setLayout(
				new MigLayout("insets 2", "[][][600,grow][][]", "[41:41:41][35:35:35,baseline][70:n,grow][35:35:35,baseline][70:n,grow]"));
				
		headerPanel = new JPanel();
		sampleDataPanel.add(headerPanel, "cell 0 0 5 0,growx,aligny top");
		headerPanel.setLayout(new BorderLayout(0, 0));
		
		addEventButton = new JButton();
		addEventButton.setEnabled(false);
		addEventButton.setText("Add Event");
		addEventButton.setIcon(Builder.getImageIcon("edit_add.png"));
		addEventButton.setToolTipText("Add an event to the selected sample");
		addEventButton.addActionListener(new java.awt.event.ActionListener() {
			
			@Override
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				
				addEventButtonActionPerformed(evt);
			}
		});
		
		sampleDataPanel.add(addEventButton, "cell 3 1,grow");
		
		deleteEventButton = new JButton();
		deleteEventButton.setEnabled(false);
		deleteEventButton.setText("Delete Event");
		deleteEventButton.setIcon(Builder.getImageIcon("delete.png"));
		deleteEventButton.setToolTipText("Delete the selected event");
		deleteEventButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent evt) {
				
				deleteEventButtonActionPerformed(evt);
			}
		});
		
		sampleDataPanel.add(deleteEventButton, "cell 4 1,grow");
		
		eventScrollPane = new JScrollPane();
		eventScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		eventScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		eventScrollPane.setViewportView(eventTable);
		
		sampleDataPanel.add(eventScrollPane, "cell 0 2 5 1,grow");
		
		addRecordingButton = new JButton();
		addRecordingButton.setEnabled(false);
		addRecordingButton.setText("Add Recording");
		addRecordingButton.setIcon(Builder.getImageIcon("edit_add.png"));
		addRecordingButton.setToolTipText("Add a recording range to the selected sample");
		addRecordingButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent evt) {
				
				addRecordingButtonActionPerformed(evt);
			}
		});
		
		consolidateButton = new JButton();
		consolidateButton.setEnabled(false);
		consolidateButton.setText("Consolidate");
		consolidateButton.setIcon(Builder.getImageIcon("merge.png"));
		consolidateButton.setToolTipText("Merge all adjacent recordings by year");
		consolidateButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent evt) {
				
				mergeRecordingsButtonActionPerformed(evt);
			}
		});
		
		sampleDataPanel.add(consolidateButton, "cell 0 3,grow");
		
		autoPopulateButton = new JButton("Auto Populate");
		autoPopulateButton.setEnabled(false);
		autoPopulateButton.setToolTipText("Automatically create recordings records");
		autoPopulateButton.setIcon(Builder.getImageIcon("refresh.png"));
		autoPopulateButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				
				autoPopulateButtonActionPerformed(arg0);
			}
			
		});
		
		sampleDataPanel.add(autoPopulateButton, "cell 1 3,grow");
		sampleDataPanel.add(addRecordingButton, "cell 3 3,grow");
		
		deleteRecordingButton = new JButton();
		deleteRecordingButton.setEnabled(false);
		deleteRecordingButton.setText("Delete Recording");
		deleteRecordingButton.setIcon(Builder.getImageIcon("delete.png"));
		deleteRecordingButton.setToolTipText("Delete the selected recording range");
		deleteRecordingButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent evt) {
				
				deleteRecordingButtonActionPerformed(evt);
			}
		});
		
		sampleDataPanel.add(deleteRecordingButton, "cell 4 3,grow");
		
		recordingScrollPane = new JScrollPane();
		recordingScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		recordingScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		recordingScrollPane.setViewportView(recordingTable);
		sampleDataPanel.add(recordingScrollPane, "cell 0 4 5 1,grow");
		
		createSampleHeaderPanel();
		
		progressBarContainer = new JPanel();
		progressBarContainer.setLayout(new MigLayout("", "[grow,center]", "[grow,center]"));
		
		progressBar = new JProgressBar();
		progressBarContainer.add(progressBar, "cell 0 0");
		progressBar.setStringPainted(true);
	}
	
	/**
	 * Automatically populate the recording years, either from the first event or the beginning of the sample depending on input from the
	 * user
	 * 
	 * @param evt
	 */
	private void autoPopulateButtonActionPerformed(ActionEvent evt) {
		
		String[] options = { "From first event", "From beginning of sample" };
		
		String res = (String) JOptionPane.showInputDialog(this,
				"Note that auto-populating the recording years will remove any\n" + "existing recording entries."
						+ "\n\nSelect where recording should begin:",
				"Auto populate recordings", JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
				
		if (res != options[0] && res != options[1])
			return;
			
		// Remove any existing records
		if (recordingTable != null)
			RecordingController.deleteAllRecordingsButNotEvents();
			
		// Start recording at first event
		if (res == options[0])
			RecordingController.addRecordingFromFirstEventToEnd();
			
		// Start recording from beginning of sample
		else
			RecordingController.addRecordingFromBeginningToEnd();
	}
	
	/**
	 * Generates the header panel and the components it contains.
	 */
	private void createSampleHeaderPanel() {
		
		sampleNameContainer = new JPanel();
		sampleNameContainer.setBorder(new MatteBorder(0, 0, 1, 0, new Color(128, 128, 128)));
		headerPanel.add(sampleNameContainer, BorderLayout.NORTH);
		sampleNameContainer.setLayout(new MigLayout("insets 0",
				"[:2:2][right][150,grow][1:10:10,grow,shrinkprio 200][][][][1:10:10,grow,shrinkprio 200][][][][:2:2]", "[40:40:40]"));
				
		sampleNameLabel = new JLabel("Sample Name:");
		sampleNameContainer.add(sampleNameLabel, "cell 1 0,alignx right,aligny baseline");
		
		sampleNameTextBox = new JTextField();
		sampleNameContainer.add(sampleNameTextBox, "cell 2 0,growx,aligny center");
		sampleNameTextBox.setColumns(10);
		sampleNameTextBox.addFocusListener(new java.awt.event.FocusAdapter() {
			
			@Override
			public void focusLost(java.awt.event.FocusEvent evt) {
				
				updateSampleNameInData();
			}
		});
		sampleNameTextBox.addKeyListener(new KeyListener() {
			
			@Override
			public void keyPressed(KeyEvent evt) {
				
				if (evt.getKeyCode() == KeyEvent.VK_ENTER)
					updateSampleNameInData();
			}
			
			@Override
			public void keyReleased(KeyEvent evt) {
			
			}
			
			@Override
			public void keyTyped(KeyEvent evt) {
			
			}
		});
		
		firstYearSpinner = new javax.swing.JSpinner();
		firstYearSpinner.setModel(new SpinnerNumberModel(FileController.CURRENT_YEAR - 1, FileController.EARLIEST_ALLOWED_YEAR,
				FileController.CURRENT_YEAR - 1, 1));
		firstYearSpinner.setEditor(new JSpinner.NumberEditor(firstYearSpinner, "#####"));
		
		((NumberFormatter) ((JSpinner.NumberEditor) firstYearSpinner.getEditor()).getTextField().getFormatter()).setAllowsInvalid(false);
		
		// Workaround to enable manual editing of spinner
		((JSpinner.DefaultEditor) firstYearSpinner.getEditor()).getTextField().setFocusTraversalKeysEnabled(false);
		((JSpinner.DefaultEditor) firstYearSpinner.getEditor()).getTextField().addKeyListener(new KeyListener() {
			
			@Override
			public void keyPressed(KeyEvent evt) {
				
				if (evt.getKeyChar() == KeyEvent.VK_BACK_SPACE)
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
			}
		});
		
		// Updates the first year of the sample when the value is changed
		firstYearSpinner.addChangeListener(new ChangeListener() {
			
			@Override
			public void stateChanged(ChangeEvent e) {
				
				if (!selectedSampleIndexChanged && !justUpdatedFYS)
				{
					justUpdatedFYS = true;
					
					if ((Integer) firstYearSpinner.getValue() == 0 && previousValueFYS == 1)
						firstYearSpinner.setValue(-1);
					else if ((Integer) firstYearSpinner.getValue() == 0 && previousValueFYS == -1)
						firstYearSpinner.setValue(1);
					else if ((Integer) firstYearSpinner.getValue() == 0)
						firstYearSpinner.setValue(1);
					// if spinner year is moved above or equal to the last year spinner's value
					else if ((Integer) firstYearSpinner.getValue() >= (Integer) lastYearSpinner.getValue())
					{
						firstYearSpinner.setValue(previousValueFYS);
						return;
					}
					// if spinner year is moved up to the year past an event year
					else if (SampleController.selectedSampleHasEvents())
					{
						if ((Integer) firstYearSpinner.getValue() > SampleController.getYearOfFirstEventInSelectedSample())
						{
							firstYearSpinner.setValue(previousValueFYS);
							return;
						}
					}
					SampleController.changeSampleFirstYear((Integer) firstYearSpinner.getValue());
					redrawSampleDataPanel(sampleListBox.getSelectedIndex());
					previousValueFYS = (Integer) firstYearSpinner.getValue();
				}
				justUpdatedFYS = false;
			}
		});
		
		firstYearLabel = new JLabel("First Year:");
		sampleNameContainer.add(firstYearLabel, "cell 4 0,alignx right,aligny baseline");
		sampleNameContainer.add(firstYearSpinner, "cell 5 0,growx,aligny center");
		
		lastYearSpinner = new javax.swing.JSpinner();
		lastYearSpinner.setModel(
				new SpinnerNumberModel(FileController.CURRENT_YEAR, FileController.EARLIEST_ALLOWED_YEAR, FileController.CURRENT_YEAR, 1));
		lastYearSpinner.setEditor(new JSpinner.NumberEditor(lastYearSpinner, "#####"));
		
		((NumberFormatter) ((JSpinner.NumberEditor) lastYearSpinner.getEditor()).getTextField().getFormatter()).setAllowsInvalid(false);
		
		// Workaround to enable manual editing of spinner
		((JSpinner.DefaultEditor) lastYearSpinner.getEditor()).getTextField().setFocusTraversalKeysEnabled(false);
		((JSpinner.DefaultEditor) lastYearSpinner.getEditor()).getTextField().addKeyListener(new KeyListener() {
			
			@Override
			public void keyPressed(KeyEvent evt) {
				
				if (evt.getKeyChar() == KeyEvent.VK_BACK_SPACE)
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
			}
		});
		
		// Updates the first year of the sample when the value is changed
		lastYearSpinner.addChangeListener(new ChangeListener() {
			
			@Override
			public void stateChanged(ChangeEvent e) {
				
				if (!selectedSampleIndexChanged && !justUpdatedLYS)
				{
					justUpdatedLYS = true;
					
					if ((Integer) lastYearSpinner.getValue() == 0 && previousValueLYS == 1)
						lastYearSpinner.setValue(-1);
					else if ((Integer) lastYearSpinner.getValue() == 0 && previousValueLYS == -1)
						lastYearSpinner.setValue(1);
					else if ((Integer) lastYearSpinner.getValue() == 0)
						lastYearSpinner.setValue(1);
					// if spinner year is moved below or equal to the first year spinner's value
					else if ((Integer) lastYearSpinner.getValue() <= (Integer) firstYearSpinner.getValue())
					{
						lastYearSpinner.setValue(previousValueLYS);
						return;
					}
					// if spinner year is moved down to the year past an event year
					else if (SampleController.selectedSampleHasEvents())
					{
						if ((Integer) lastYearSpinner.getValue() < SampleController.getYearOfLastEventInSelectedSample())
						{
							lastYearSpinner.setValue(previousValueLYS);
							return;
						}
					}
					SampleController.changeSampleLastYear((Integer) lastYearSpinner.getValue());
					redrawSampleDataPanel(sampleListBox.getSelectedIndex());
					previousValueLYS = (Integer) lastYearSpinner.getValue();
				}
				justUpdatedLYS = false;
			}
		});
		
		lastYearLabel = new JLabel("Last Year:");
		sampleNameContainer.add(lastYearLabel, "cell 8 0,alignx right,aligny baseline");
		sampleNameContainer.add(lastYearSpinner, "cell 9 0,growx,aligny center");
		
		pithCheckBox = new JCheckBox();
		pithCheckBox.setText("Pith");
		pithCheckBox.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent evt) {
				
				SampleController.setSamplePith(pithCheckBox.isSelected());
			}
		});
		
		sampleNameContainer.add(pithCheckBox, "cell 6 0,alignx left,aligny baseline");
		
		barkCheckBox = new javax.swing.JCheckBox();
		barkCheckBox.setText("Bark");
		barkCheckBox.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent evt) {
				
				SampleController.setSampleBark(barkCheckBox.isSelected());
			}
		});
		
		sampleNameContainer.add(barkCheckBox, "cell 10 0,alignx left,aligny baseline");
	}
	
	/**
	 * Handles when the "New Sample" button is clicked.
	 * 
	 * @param evt
	 */
	private void newSampleButtonActionPerformed(java.awt.event.ActionEvent evt) {
		
		NewSampleDialog editDialog = new NewSampleDialog(new java.awt.Frame(), -1);
		editDialog.setVisible(true);
		
		// Update the selected index so that the new sample is the one that is displayed
		sampleListBox.setSelectedIndex(IOController.getFile().getRequiredPart().getNumSamples() - 1);
		
		FileController.checkIfNumSamplesExceedsFHX2Reqs();
		
		needToRefreshPanel = true;
		handleUpdatedIndex();
	}
	
	/**
	 * Handles when the "Delete Sample" button is clicked.
	 * 
	 * @param evt
	 */
	private void deleteSampleButtonActionPerformed(java.awt.event.ActionEvent evt) {
		
		if (sampleListBox.getModel().getSize() > 0)
		{
			SampleController.deleteSample();
			
			if (IOController.getFile().getRequiredPart().getNumSamples() > 0)
				sampleListBox.setSelectedIndex(0);
			else
				sampleListBox.setSelectedIndex(-1);
				
			FileController.checkIfNumSamplesExceedsFHX2Reqs();
			
			needToRefreshPanel = true;
			handleUpdatedIndex();
		}
	}
	
	/**
	 * Handles when the "Add Event" button is clicked.
	 * 
	 * @param evt
	 */
	private void addEventButtonActionPerformed(ActionEvent evt) {
		
		if (eventTable != null)
		{
			if (recordingTable.getNumOfRecordings() == 0 || eventTable.getNumOfEvents() == recordingTable.getMaxNumOfEvents())
			{
				RecordingController.addNewRecording();
				EventController.addNewEvent();
				setCheckBoxEnabledValues();
			}
			else if (eventTable.getNumOfEvents() < recordingTable.getMaxNumOfEvents())
			{
				EventController.addNewEvent();
				setCheckBoxEnabledValues();
			}
		}
	}
	
	/**
	 * Handles when the "Delete Event" button is clicked.
	 * 
	 * @param evt
	 */
	private void deleteEventButtonActionPerformed(ActionEvent evt) {
		
		if (eventTable != null)
		{
			int row = eventTable.getSelectedRow();
			if (row > -1)
			{
				EventController.deleteEvent(row);
				setCheckBoxEnabledValues();
			}
		}
	}
	
	/**
	 * Handles when the "Add Recording" button is clicked.
	 * 
	 * @param evt
	 */
	private void addRecordingButtonActionPerformed(ActionEvent evt) {
		
		if (recordingTable != null)
		{
			RecordingController.addNewRecording();
			setCheckBoxEnabledValues();
		}
	}
	
	/**
	 * Handles when the "Delete Recording" button is clicked.
	 * 
	 * @param evt
	 */
	private void deleteRecordingButtonActionPerformed(ActionEvent evt) {
		
		if (recordingTable != null)
		{
			int row = recordingTable.getSelectedRow();
			if (row > -1)
			{
				RecordingController.deleteRecording(row);
				setCheckBoxEnabledValues();
			}
		}
	}
	
	/**
	 * Handles when the "Merge Recordings" button is clicked.
	 * 
	 * @param evt
	 */
	private void mergeRecordingsButtonActionPerformed(ActionEvent evt) {
		
		IOController.getFile().getRequiredPart().getSample(SampleController.getSelectedSampleIndex()).getRecordingTable()
				.mergeOverlappingRecordings();
	}
	
	/**
	 * Updates the text that is displayed on the border of the event panel.
	 * 
	 * @param text
	 */
	private void setEventBorderText(String text) {
		
		sampleDataPanel.setBorder(
				new TitledBorder(new LineBorder(new Color(171, 173, 179)), text, TitledBorder.LEADING, TitledBorder.TOP, null, null));
	}
	
	/**
	 * Sets the index of the sortByComboBox according to the input index.
	 * 
	 * @param index
	 */
	public void setSortByComboBoxValue(int index) {
		
		sortByComboBox.setSelectedIndex(index);
	}
	
	/**
	 * Updates the border text according to the name of the sample that is currently loaded.
	 * 
	 * @param name
	 */
	private void displaySampleName(String name) {
		
		if (name.equals(""))
		{
			sampleNameTextBox.setText("");
			setEventBorderText("No sample data to display:");
		}
		else
		{
			sampleNameTextBox.setText(name);
			setEventBorderText("Data contained within the sample: " + name);
		}
	}
	
	/**
	 * Updates the progress bar whenever the PropertyChangeEvent is triggered.
	 */
	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		
		if (!done)
		{
			int progress = task.getProgress();
			progressBar.setValue(progress);
		}
		else
			progressBar.setValue(100);
	}
	
	/**
	 * Updates the sample name in the data.
	 */
	@SuppressWarnings("unchecked")
	private void updateSampleNameInData() {
		
		SampleController.changeSampleName(sampleNameTextBox.getText());
		displaySampleName(sampleNameTextBox.getText());
		
		@SuppressWarnings("rawtypes")
		DefaultListModel model = (DefaultListModel) this.sampleListBox.getModel();
		model.set(sampleListBox.getSelectedIndex(), sampleListBox.getSelectedValue());
	}
	
	/**
	 * Repaints the sample list.
	 */
	@SuppressWarnings("unchecked")
	public void redrawSampleListPanel() {
		
		FHX2_Sample selected = (FHX2_Sample) sampleListBox.getSelectedValue();
		@SuppressWarnings("rawtypes")
		DefaultListModel model = (DefaultListModel) this.sampleListBox.getModel();
		
		if (model != null)
			model.clear();
		for (FHX2_Sample s : this.inReqPart.getSampleList())
			model.addElement(s);
			
		try
		{
			sampleListBox.setSelectedValue(selected, true);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	/**
	 * Repaints the header, event, and recording panels.
	 * 
	 * @param selectedSampleIndex
	 */
	public void redrawSampleDataPanel(int selectedSampleIndex) {
		
		displaySampleName("");
		if (selectedSampleIndex > -1)
		{
			FHX2_Sample selectedSample = this.inReqPart.getSample(selectedSampleIndex);
			if (selectedSample != null)
			{
				sampleDataPanel.add(progressBarContainer, BorderLayout.SOUTH);
				progressBar.setValue(0);
				
				displaySampleName(selectedSample.getSampleName());
				
				setCheckBoxEnabledValues();
				
				firstYearSpinner.setValue(selectedSample.getSampleFirstYear());
				lastYearSpinner.setValue(selectedSample.getSampleLastYear());
				
				previousValueFYS = (Integer) firstYearSpinner.getValue();
				previousValueLYS = (Integer) lastYearSpinner.getValue();
				
				selectedSampleIndexChanged = false;
				setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
				
				task = new DrawEventPanelTask(selectedSample);
				task.addPropertyChangeListener(this);
				task.execute();
				
				eventTable = selectedSample.getEventTable();
				recordingTable = selectedSample.getRecordingTable();
				
				eventScrollPane.setViewportView(eventTable);
				recordingScrollPane.setViewportView(recordingTable);
				
				enableAndShowDataPanelComponents();
				deleteSampleButton.setEnabled(true);
			}
			else
				disableAndHideDataPanelComponents();
		}
		else
		{
			disableAndHideDataPanelComponents();
			deleteSampleButton.setEnabled(false);
		}
		revalidate();
		repaint();
	}
	
	/**
	 * Enables or disables the pith and bark check-boxes according to whether or not the sample starts or ends with an event.
	 */
	public static void setCheckBoxEnabledValues() {
		
		FHX2_Sample selectedSample = IOController.getFile().getRequiredPart().getSample(SampleController.getSelectedSampleIndex());
		
		if (!selectedSample.sampleStartsWithEvent())
		{
			pithCheckBox.setSelected(selectedSample.hasPith());
			pithCheckBox.setEnabled(true);
		}
		else
		{
			pithCheckBox.setSelected(false);
			pithCheckBox.setEnabled(false);
		}
		
		if (!selectedSample.sampleEndsWithEvent())
		{
			barkCheckBox.setSelected(selectedSample.hasBark());
			barkCheckBox.setEnabled(true);
		}
		else
		{
			barkCheckBox.setSelected(false);
			barkCheckBox.setEnabled(false);
		}
	}
	
	/**
	 * Enables and shows specific components on the sampleDataPanel.
	 */
	private void enableAndShowDataPanelComponents() {
		
		headerPanel.setVisible(true);
		eventTable.setVisible(true);
		recordingTable.setVisible(true);
		
		addEventButton.setEnabled(true);
		deleteEventButton.setEnabled(true);
		addRecordingButton.setEnabled(true);
		deleteRecordingButton.setEnabled(true);
		consolidateButton.setEnabled(true);
		autoPopulateButton.setEnabled(true);
		
		sortByComboBox.setEnabled(true);
		if (sortByComboBox.getSelectedIndex() == MANUAL_SORTING)
		{
			if (IOController.getFile().getRequiredPart().getNumSamples() < 2)
			{
				moveDownButton.setEnabled(false);
				moveUpButton.setEnabled(false);
			}
			else if (sampleListBox.getSelectedIndex() == 0)
			{
				moveDownButton.setEnabled(true);
				moveUpButton.setEnabled(false);
			}
			else if (sampleListBox.getSelectedIndex() == IOController.getFile().getRequiredPart().getNumSamples() - 1)
			{
				moveDownButton.setEnabled(false);
				moveUpButton.setEnabled(true);
			}
			else if (sampleListBox.getSelectedIndex() > -1)
			{
				moveDownButton.setEnabled(true);
				moveUpButton.setEnabled(true);
			}
		}
	}
	
	/**
	 * Disables and hides specific components on the sampleDataPanel.
	 */
	private void disableAndHideDataPanelComponents() {
		
		try
		{
			headerPanel.setVisible(false);
			eventTable.setVisible(false);
			recordingTable.setVisible(false);
		}
		catch (NullPointerException e)
		{
			log.warn("NPE when trying to hide components of the SampleInput panel");
		}
		
		addEventButton.setEnabled(false);
		deleteEventButton.setEnabled(false);
		addRecordingButton.setEnabled(false);
		deleteRecordingButton.setEnabled(false);
		consolidateButton.setEnabled(false);
		autoPopulateButton.setEnabled(false);
		
		sortByComboBox.setEnabled(false);
		moveDownButton.setEnabled(false);
		moveUpButton.setEnabled(false);
	}
	
	/**
	 * TODO
	 */
	private void handleUpdatedIndex() {
		
		int index = sampleListBox.getSelectedIndex();
		if (index != SampleController.getSelectedSampleIndex() || needToRefreshPanel || firstTimeLoading)
		{
			if (firstTimeLoading)
			{
				eventTable = new EventTable(new FHX2_Sample());
				recordingTable = new RecordingTable(new FHX2_Sample());
				firstTimeLoading = false;
			}
			ignoreEventsFlag = true;
			selectedSampleIndexChanged = true;
			SampleController.setSelectedSampleIndex(index);
			redrawSampleDataPanel(index);
			ignoreEventsFlag = false;
			needToRefreshPanel = false;
		}
	}
	
	/**
	 * TODO
	 */
	@Override
	public void stateChanged(ChangeEvent e) {
		
		if (ignoreEventsFlag)
			return;
		redrawSampleListPanel();
	}
	
	/**
	 * TODO
	 */
	private class DrawEventPanelTask extends SwingWorker<ScrollViewport, Object> {
		
		private FHX2_Sample selectedSample;
		ScrollViewport viewPort;
		
		/**
		 * TODO
		 * 
		 * @param sample
		 */
		public DrawEventPanelTask(FHX2_Sample sample) {
			
			super();
			done = false;
			selectedSample = sample;
		}
		
		/**
		 * TODO
		 */
		@Override
		public void done() {
			
			done = true;
			progressBar.setValue(100);
			setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
			sampleDataPanel.remove(progressBarContainer);
			revalidate();
			repaint();
		}
		
		/**
		 * TODO
		 */
		@Override
		protected ScrollViewport doInBackground() throws Exception {
			
			viewPort = new ScrollViewport();
			
			int eventIndex = 0;
			int recordingIndex = 0;
			
			boolean hasMoreEvents = eventIndex < selectedSample.getNumOfEvents();
			boolean hasMoreRecording = recordingIndex < selectedSample.getNumOfRecordings();
			
			for (int year = selectedSample.getSampleFirstYear(); year <= selectedSample.getSampleLastYear(); year++)
			{
				if (year == 0)
					continue;
				else
				{
					if (hasMoreRecording && selectedSample.getRecording(recordingIndex).containsYear(year))
					{
						if (selectedSample.getRecording(recordingIndex).getEndYear() == year)
						{
							recordingIndex++;
							hasMoreRecording = recordingIndex < selectedSample.getNumOfRecordings();
						}
					}
					else if (hasMoreEvents && selectedSample.getEvent(eventIndex).getEventYear() == year)
					{
						eventIndex++;
						hasMoreEvents = eventIndex < selectedSample.getNumOfEvents();
					}
					
				}
				setProgress(((year - selectedSample.getSampleFirstYear()) * 100)
						/ (selectedSample.getSampleLastYear() - selectedSample.getSampleFirstYear()));
			}
			return viewPort;
		}
	}
	
	/**
	 * TODO
	 */
	private class ScrollViewport extends JViewport implements Scrollable {
		
		private static final long serialVersionUID = -895599170681795117L;
		
		/**
		 * TODO
		 */
		@Override
		public Dimension getPreferredScrollableViewportSize() {
			
			return getPreferredSize();
		}
		
		/**
		 * TODO
		 */
		@Override
		public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction) {
			
			return 30;
		}
		
		/**
		 * TODO
		 */
		@Override
		public boolean getScrollableTracksViewportHeight() {
			
			return false;
		}
		
		/**
		 * TODO
		 */
		@Override
		public boolean getScrollableTracksViewportWidth() {
			
			return true;
		}
		
		/**
		 * TODO
		 */
		@Override
		public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction) {
			
			return 1;
		}
	}
}
