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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.math.BigDecimal;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import org.fhaes.FHRecorder.controller.FileController;
import org.fhaes.FHRecorder.controller.IOController;
import org.fhaes.FHRecorder.model.FHX2_FileOptionalPart;
import org.fhaes.FHRecorder.utility.LengthRestrictedDocument;
import org.fhaes.FHRecorder.utility.MetaDataTextField;
import org.fhaes.components.HelpTipButton;
import org.fhaes.feedback.FeedbackMessagePanel;
import org.fhaes.feedback.FeedbackMessagePanel.FeedbackMessageID;
import org.fhaes.feedback.FeedbackMessagePanel.FeedbackMessageType;
import org.fhaes.help.LocalHelp;
import org.fhaes.preferences.FHAESPreferences.PrefKey;
import org.fhaes.preferences.wrappers.CheckBoxWrapper;

import net.miginfocom.swing.MigLayout;

/**
 * MetaDataPanel Class. This class displays all of the meta data of a FHX2 file.
 * 
 * @author Alex Beatty, Clayton Bodendein, Kyle Hartmann, Scott Goble
 */
public class MetaDataPanel extends javax.swing.JPanel {
	
	private static final long serialVersionUID = 1L;
	
	private final int TWO_CHARACTERS = 2;
	private final int THREE_CHARACTERS = 3;
	private final int FOUR_CHARACTERS = 4;
	private final int FIVE_CHARACTERS = 5;
	private final int SIX_CHARACTERS = 6;
	private final int TEN_CHARACTERS = 10;
	private final int FIFTEEN_CHARACTERS = 15;
	private final int TWENTY_CHARACTERS = 20;
	private final int TWENTYFIVE_CHARACTERS = 25;
	private final int THIRTY_CHARACTERS = 30;
	private final int FORTY_CHARACTERS = 40;
	private final int SEVENTY_CHARACTERS = 70;
	
	private final int ALL_FIELDS_COMPATIBLE = 29;
	private final int DOCUMENT_LIMIT = 99999;
	
	private MetaDataTextField areaSampledText;
	private MetaDataTextField aspectText;
	private MetaDataTextField collectionDateText;
	private MetaDataTextField collectorNamesText;
	private MetaDataTextField commonNamesText;
	private MetaDataTextField countryText;
	private MetaDataTextField countyText;
	private MetaDataTextField daterNamesText;
	private MetaDataTextField forestText;
	private MetaDataTextField habitatText;
	private MetaDataTextField highestElevationText;
	private javax.swing.JLabel siteNameLabel;
	private javax.swing.JLabel countryLabel;
	private javax.swing.JLabel stateLabel;
	private javax.swing.JLabel countyLabel;
	private javax.swing.JLabel parkLabel;
	private javax.swing.JLabel forestLabel;
	private javax.swing.JLabel rangerDistrictLabel;
	private javax.swing.JLabel sectionLabel;
	private javax.swing.JLabel quarterSectionLabel;
	private javax.swing.JLabel utmEastingLabel;
	private javax.swing.JLabel siteCodeLabel;
	private javax.swing.JLabel utmNorthingLabel;
	private javax.swing.JLabel latitudeLabel;
	private javax.swing.JLabel longitudeLabel;
	private javax.swing.JLabel topographicMapLabel;
	private javax.swing.JLabel highestElevationLabel;
	private javax.swing.JLabel slopeLabel;
	private javax.swing.JLabel aspectLabel;
	private javax.swing.JLabel areaSampledLabel;
	private javax.swing.JLabel substrateLabel;
	private javax.swing.JLabel collectionDateLabel;
	private javax.swing.JLabel rangeLabel;
	private javax.swing.JLabel collectorNamesLabel;
	private javax.swing.JLabel daterNamesLabel;
	private javax.swing.JLabel numSamplesLabel;
	private javax.swing.JLabel latinNamesLabel;
	private javax.swing.JLabel commonNamesLabel;
	private javax.swing.JLabel habitatLabel;
	private javax.swing.JPanel metaDataPanel;
	private javax.swing.JScrollPane metaDataScrollPane;
	private MetaDataTextField latinNamesText;
	private MetaDataTextField latitudeText;
	private MetaDataTextField longitudeText;
	private MetaDataTextField lowestElevationText;
	private MetaDataTextField parkText;
	private MetaDataTextField quarterSectionText;
	private MetaDataTextField rangeText;
	private MetaDataTextField rangerDistrictText;
	private MetaDataTextField sectionText;
	private MetaDataTextField siteCodeText;
	private MetaDataTextField siteNameText;
	private MetaDataTextField slopeText;
	private MetaDataTextField stateText;
	private MetaDataTextField substrateText;
	private MetaDataTextField topographicMapText;
	private MetaDataTextField townshipText;
	private MetaDataTextField utmEastingText;
	private MetaDataTextField utmNorthingText;
	private static MetaDataTextField numSamplesText;
	private JButton convertLatToDecimalButton;
	private JButton convertLonToDecimalButton;
	private HelpTipButton siteCodeHelpTipButton;
	private HelpTipButton collectionDateHelpTipButton;
	private HelpTipButton collectorNamesHelpTipButton;
	private HelpTipButton daterNamesHelpTipButton;
	private HelpTipButton latinNamesHelpTipButton;
	private HelpTipButton commonNamesHelpTipButton;
	private HelpTipButton habitatHelpTipButton;
	private HelpTipButton countryHelpTipButton;
	private HelpTipButton stateHelpTipButton;
	private HelpTipButton countyHelpTipButton;
	private HelpTipButton parkHelpTipButton;
	private HelpTipButton forestHelpTipButton;
	private HelpTipButton rangerDistrictHelpTipButton;
	private HelpTipButton townshipHelpTipButton;
	private HelpTipButton rangeHelpTipButton;
	private HelpTipButton sectionHelpTipButton;
	private HelpTipButton quarterSectionHelpTipButton;
	private HelpTipButton utmEastingHelpTipButton;
	private HelpTipButton utmNorthingHelpTipButton;
	private HelpTipButton latitudeHelpTipButton;
	private HelpTipButton longitudeHelpTipButton;
	private HelpTipButton topographicMapHelpTipButton;
	private HelpTipButton highestElevationHelpTipButton;
	private HelpTipButton lowestElevationHelpTipButton;
	private HelpTipButton slopeHelpTipButton;
	private HelpTipButton aspectHelpTipButton;
	private HelpTipButton areaSampledHelpTipButton;
	private HelpTipButton substrateHelpTipButton;
	private HelpTipButton numSamplesHelpTipButton;
	private JCheckBox enforceOldReqsCheckBox;
	private JLabel fieldLengthLabel;
	private JTextField siteNameCountBox;
	private JTextField siteCodeCountBox;
	private JTextField collectionDateCountBox;
	private JTextField collectorNamesCountBox;
	private JTextField daterNamesCountBox;
	private JTextField latinNamesCountBox;
	private JTextField commonNamesCountBox;
	private JTextField habitatCountBox;
	private JTextField countryCountBox;
	private JTextField stateCountBox;
	private JTextField countyCountBox;
	private JTextField parkCountBox;
	private JTextField forestCountBox;
	private JTextField rangerDistrictCountBox;
	private JTextField townshipCountBox;
	private JTextField rangeCountBox;
	private JTextField sectionCountBox;
	private JTextField quarterSectionCountBox;
	private JTextField utmEastingCountBox;
	private JTextField utmNorthingCountBox;
	private JTextField topographicMapCountBox;
	private JTextField highestElevationCountBox;
	private JTextField lowestElevationCountBox;
	private JTextField slopeCountBox;
	private JTextField aspectCountBox;
	private JTextField areaSampledCountBox;
	private JTextField substrateCountBox;
	
	private FHX2_FileOptionalPart optionalData;
	private boolean saveToData;
	private JTextField latitudeCountBox;
	private JTextField longitudeCountBox;
	
	/**
	 * Creates new form MetaDataPanel
	 */
	public MetaDataPanel(FHX2_FileOptionalPart inOptPart) {
		
		initComponents();
		optionalData = inOptPart;
		
		fillTextFields();
		saveToData = true;
		
		updateGUIRestrictions(this.enforceOldReqsCheckBox.isSelected());
	}
	
	/**
	 * Hide/Show restricted fields info depending on preference
	 */
	private void updateGUIRestrictions(boolean enforce) {
		
		if (enforce)
		{
			if (CheckForNonCompatibleFieldLengths())
				enableTextFieldEnforcement();
				
			setFieldLengthBoxesVisible(true);
		}
		else
		{
			disableTextFieldEnforcement();
			setFieldLengthBoxesVisible(false);
			resetTextBoxColors();
		}
		
		metaDataPanel.validate();
	}
	
	/**
	 * Initializes the GUI components.
	 */
	private void initComponents() {
		
		metaDataScrollPane = new javax.swing.JScrollPane();
		metaDataPanel = new javax.swing.JPanel();
		
		addComponentListener(new java.awt.event.ComponentAdapter() {
			
			@Override
			public void componentHidden(java.awt.event.ComponentEvent evt) {
				
				if (saveToData)
					saveInfoToData();
			}
			
			@Override
			public void componentShown(java.awt.event.ComponentEvent evt) {
				
				fillTextFields();
				siteNameText.requestFocusInWindow();
			}
		});
		
		metaDataScrollPane.getVerticalScrollBar().setUnitIncrement(17);
		metaDataScrollPane.setAutoscrolls(true);
		metaDataScrollPane.setFocusable(false);
		
		metaDataPanel.setAutoscrolls(true);
		metaDataPanel.setFocusable(false);
		metaDataPanel.setLayout(new MigLayout("hidemode 1", "[right][368.00,grow][fill][:20:20]",
				"[:26:26][:24:24][:24:24][:24:24][:24:24][:24:24][:24:24][:24:24][:24:24][:24:24][:24:24][:24:24][:24:24][:24:24][:24:24][:24:24][:24:24][:24:24][:24:24][:24:24][:24:24][:24:24,grow][:24:24][:24:24][:24:24][:24:24][:24:24][:24:24][:24:24][:24:24][:24:24]"));
				
		enforceOldReqsCheckBox = new JCheckBox("Enforce original FHX2 format length requirements?");
		new CheckBoxWrapper(enforceOldReqsCheckBox, PrefKey.ENFORCE_FHX2_RESTRICTIONS, false);
		
		enforceOldReqsCheckBox.addItemListener(new ItemListener() { // maintenance request: missing features #3
			
			@Override
			public void itemStateChanged(ItemEvent arg0) {
				
				updateGUIRestrictions(enforceOldReqsCheckBox.isSelected());
			}
		});
		enforceOldReqsCheckBox
				.setToolTipText("This will constrain the amount of information that can be entered into each of the fields below.");
		metaDataPanel.add(enforceOldReqsCheckBox, "flowx,cell 1 0,grow");
		siteNameText = new MetaDataTextField();
		siteNameText.addKeyListener(new KeyAdapter() {
			
			@Override
			public void keyReleased(KeyEvent e) {
				
				if (enforceOldReqsCheckBox.isSelected())
					if (CheckForNonCompatibleFieldLengths())
						enableTextFieldEnforcement();
			}
		});
		
		fieldLengthLabel = new JLabel("Field Length:");
		fieldLengthLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		metaDataPanel.add(fieldLengthLabel, "cell 2 0,grow");
		siteNameLabel = new javax.swing.JLabel();
		metaDataPanel.add(siteNameLabel, "cell 0 1");
		
		siteNameLabel.setText("Name of site:");
		
		siteNameText.setDocument(new LengthRestrictedDocument(DOCUMENT_LIMIT));
		metaDataPanel.add(siteNameText, "flowx,cell 1 1,grow");
		
		siteNameCountBox = new JTextField();
		metaDataPanel.add(siteNameCountBox, "cell 2 1,alignx right,growy");
		siteNameCountBox.setHorizontalAlignment(SwingConstants.RIGHT);
		siteNameCountBox.setEditable(false);
		
		HelpTipButton siteNameHelpTipButton = new HelpTipButton(LocalHelp.FIELD_SITE_NAME);
		metaDataPanel.add(siteNameHelpTipButton, "cell 3 1");
		siteCodeText = new MetaDataTextField();
		siteCodeText.addKeyListener(new KeyAdapter() {
			
			@Override
			public void keyReleased(KeyEvent e) {
				
				if (enforceOldReqsCheckBox.isSelected())
					if (CheckForNonCompatibleFieldLengths())
						enableTextFieldEnforcement();
			}
		});
		siteCodeLabel = new javax.swing.JLabel();
		metaDataPanel.add(siteCodeLabel, "cell 0 2");
		
		siteCodeLabel.setText("Site code:");
		
		siteCodeText.setDocument(new LengthRestrictedDocument(DOCUMENT_LIMIT));
		metaDataPanel.add(siteCodeText, "flowx,cell 1 2,grow");
		
		siteCodeCountBox = new JTextField();
		metaDataPanel.add(siteCodeCountBox, "cell 2 2,alignx right,growy");
		siteCodeCountBox.setHorizontalAlignment(SwingConstants.RIGHT);
		siteCodeCountBox.setEditable(false);
		
		siteCodeHelpTipButton = new HelpTipButton(LocalHelp.FIELD_SITE_CODE);
		metaDataPanel.add(siteCodeHelpTipButton, "cell 3 2");
		collectionDateText = new MetaDataTextField();
		collectionDateText.addKeyListener(new KeyAdapter() {
			
			@Override
			public void keyReleased(KeyEvent e) {
				
				if (enforceOldReqsCheckBox.isSelected())
					if (CheckForNonCompatibleFieldLengths())
						enableTextFieldEnforcement();
			}
		});
		collectionDateLabel = new javax.swing.JLabel();
		metaDataPanel.add(collectionDateLabel, "cell 0 3");
		
		collectionDateLabel.setText("Collection date(s):");
		
		collectionDateText.setDocument(new LengthRestrictedDocument(DOCUMENT_LIMIT));
		metaDataPanel.add(collectionDateText, "flowx,cell 1 3,grow");
		
		collectionDateCountBox = new JTextField();
		metaDataPanel.add(collectionDateCountBox, "cell 2 3,alignx right,growy");
		collectionDateCountBox.setHorizontalAlignment(SwingConstants.RIGHT);
		collectionDateCountBox.setEditable(false);
		
		collectionDateHelpTipButton = new HelpTipButton(LocalHelp.FIELD_COLLECTION_DATE);
		metaDataPanel.add(collectionDateHelpTipButton, "cell 3 3");
		collectorNamesText = new MetaDataTextField();
		collectorNamesText.addKeyListener(new KeyAdapter() {
			
			@Override
			public void keyReleased(KeyEvent e) {
				
				if (enforceOldReqsCheckBox.isSelected())
					if (CheckForNonCompatibleFieldLengths())
						enableTextFieldEnforcement();
			}
		});
		collectorNamesLabel = new javax.swing.JLabel();
		metaDataPanel.add(collectorNamesLabel, "cell 0 4");
		
		collectorNamesLabel.setText("Names of collector(s):");
		
		collectorNamesText.setDocument(new LengthRestrictedDocument(DOCUMENT_LIMIT));
		metaDataPanel.add(collectorNamesText, "flowx,cell 1 4,grow");
		
		collectorNamesCountBox = new JTextField();
		metaDataPanel.add(collectorNamesCountBox, "cell 2 4,alignx right,growy");
		collectorNamesCountBox.setHorizontalAlignment(SwingConstants.RIGHT);
		collectorNamesCountBox.setEditable(false);
		
		collectorNamesHelpTipButton = new HelpTipButton(LocalHelp.FIELD_COLLECTORS_NAME);
		metaDataPanel.add(collectorNamesHelpTipButton, "cell 3 4");
		daterNamesText = new MetaDataTextField();
		daterNamesText.addKeyListener(new KeyAdapter() {
			
			@Override
			public void keyReleased(KeyEvent e) {
				
				if (enforceOldReqsCheckBox.isSelected())
					if (CheckForNonCompatibleFieldLengths())
						enableTextFieldEnforcement();
			}
		});
		daterNamesLabel = new javax.swing.JLabel();
		metaDataPanel.add(daterNamesLabel, "cell 0 5");
		
		daterNamesLabel.setText("Names of dater(s):");
		
		daterNamesText.setDocument(new LengthRestrictedDocument(DOCUMENT_LIMIT));
		metaDataPanel.add(daterNamesText, "flowx,cell 1 5,grow");
		
		daterNamesCountBox = new JTextField();
		metaDataPanel.add(daterNamesCountBox, "cell 2 5,alignx right,growy");
		daterNamesCountBox.setHorizontalAlignment(SwingConstants.RIGHT);
		daterNamesCountBox.setEditable(false);
		
		daterNamesHelpTipButton = new HelpTipButton(LocalHelp.FIELD_DATERS_NAME);
		metaDataPanel.add(daterNamesHelpTipButton, "cell 3 5");
		latinNamesText = new MetaDataTextField();
		latinNamesText.addKeyListener(new KeyAdapter() {
			
			@Override
			public void keyReleased(KeyEvent e) {
				
				if (enforceOldReqsCheckBox.isSelected())
					if (CheckForNonCompatibleFieldLengths())
						enableTextFieldEnforcement();
			}
		});
		latinNamesLabel = new javax.swing.JLabel();
		metaDataPanel.add(latinNamesLabel, "cell 0 6");
		
		latinNamesLabel.setText("Latin name(s):");
		
		latinNamesText.setDocument(new LengthRestrictedDocument(DOCUMENT_LIMIT));
		metaDataPanel.add(latinNamesText, "flowx,cell 1 6,grow");
		
		latinNamesCountBox = new JTextField();
		metaDataPanel.add(latinNamesCountBox, "cell 2 6,alignx right,growy");
		latinNamesCountBox.setHorizontalAlignment(SwingConstants.RIGHT);
		latinNamesCountBox.setEditable(false);
		
		latinNamesHelpTipButton = new HelpTipButton(LocalHelp.FIELD_LATIN_NAMES);
		metaDataPanel.add(latinNamesHelpTipButton, "cell 3 6");
		commonNamesText = new MetaDataTextField();
		commonNamesText.addKeyListener(new KeyAdapter() {
			
			@Override
			public void keyReleased(KeyEvent e) {
				
				if (enforceOldReqsCheckBox.isSelected())
					if (CheckForNonCompatibleFieldLengths())
						enableTextFieldEnforcement();
			}
		});
		commonNamesLabel = new javax.swing.JLabel();
		metaDataPanel.add(commonNamesLabel, "cell 0 7");
		
		commonNamesLabel.setText("Species common name(s):");
		
		commonNamesText.setDocument(new LengthRestrictedDocument(DOCUMENT_LIMIT));
		metaDataPanel.add(commonNamesText, "flowx,cell 1 7,grow");
		
		commonNamesCountBox = new JTextField();
		metaDataPanel.add(commonNamesCountBox, "cell 2 7,alignx right,growy");
		commonNamesCountBox.setHorizontalAlignment(SwingConstants.RIGHT);
		commonNamesCountBox.setEditable(false);
		
		commonNamesHelpTipButton = new HelpTipButton(LocalHelp.FIELD_COMMON_NAMES);
		metaDataPanel.add(commonNamesHelpTipButton, "cell 3 7");
		habitatText = new MetaDataTextField();
		habitatText.addKeyListener(new KeyAdapter() {
			
			@Override
			public void keyReleased(KeyEvent e) {
				
				if (enforceOldReqsCheckBox.isSelected())
					if (CheckForNonCompatibleFieldLengths())
						enableTextFieldEnforcement();
			}
		});
		habitatLabel = new javax.swing.JLabel();
		metaDataPanel.add(habitatLabel, "cell 0 8");
		
		habitatLabel.setText("Habitat type:");
		
		habitatText.setDocument(new LengthRestrictedDocument(DOCUMENT_LIMIT));
		metaDataPanel.add(habitatText, "flowx,cell 1 8,grow");
		
		habitatCountBox = new JTextField();
		metaDataPanel.add(habitatCountBox, "cell 2 8,alignx right,growy");
		habitatCountBox.setHorizontalAlignment(SwingConstants.RIGHT);
		habitatCountBox.setEditable(false);
		
		habitatHelpTipButton = new HelpTipButton(LocalHelp.FIELD_HABITAT_TYPE);
		metaDataPanel.add(habitatHelpTipButton, "cell 3 8");
		countryText = new MetaDataTextField();
		countryText.addKeyListener(new KeyAdapter() {
			
			@Override
			public void keyReleased(KeyEvent e) {
				
				if (enforceOldReqsCheckBox.isSelected())
					if (CheckForNonCompatibleFieldLengths())
						enableTextFieldEnforcement();
			}
		});
		
		countryText.setDocument(new LengthRestrictedDocument(DOCUMENT_LIMIT));
		countryText.addFocusListener(new java.awt.event.FocusAdapter() {
			
			@Override
			public void focusGained(java.awt.event.FocusEvent evt) {
				
				metaDataPanel.repaint();
			}
		});
		countryLabel = new javax.swing.JLabel();
		metaDataPanel.add(countryLabel, "cell 0 9");
		
		countryLabel.setText("Country:");
		metaDataPanel.add(countryText, "flowx,cell 1 9,grow");
		
		countryCountBox = new JTextField();
		metaDataPanel.add(countryCountBox, "cell 2 9,alignx right,growy");
		countryCountBox.setHorizontalAlignment(SwingConstants.RIGHT);
		countryCountBox.setEditable(false);
		
		countryHelpTipButton = new HelpTipButton(LocalHelp.FIELD_COUNTRY);
		metaDataPanel.add(countryHelpTipButton, "cell 3 9");
		stateText = new MetaDataTextField();
		stateText.addKeyListener(new KeyAdapter() {
			
			@Override
			public void keyReleased(KeyEvent e) {
				
				if (enforceOldReqsCheckBox.isSelected())
					if (CheckForNonCompatibleFieldLengths())
						enableTextFieldEnforcement();
			}
		});
		stateLabel = new javax.swing.JLabel();
		metaDataPanel.add(stateLabel, "cell 0 10");
		
		stateLabel.setText("State:");
		
		stateText.setDocument(new LengthRestrictedDocument(DOCUMENT_LIMIT));
		metaDataPanel.add(stateText, "flowx,cell 1 10,grow");
		
		stateCountBox = new JTextField();
		metaDataPanel.add(stateCountBox, "cell 2 10,alignx right,growy");
		stateCountBox.setHorizontalAlignment(SwingConstants.RIGHT);
		stateCountBox.setEditable(false);
		
		stateHelpTipButton = new HelpTipButton(LocalHelp.FIELD_STATE);
		metaDataPanel.add(stateHelpTipButton, "cell 3 10");
		countyText = new MetaDataTextField();
		countyText.addKeyListener(new KeyAdapter() {
			
			@Override
			public void keyReleased(KeyEvent e) {
				
				if (enforceOldReqsCheckBox.isSelected())
					if (CheckForNonCompatibleFieldLengths())
						enableTextFieldEnforcement();
			}
		});
		countyLabel = new javax.swing.JLabel();
		metaDataPanel.add(countyLabel, "cell 0 11");
		
		countyLabel.setText("County:");
		
		countyText.setDocument(new LengthRestrictedDocument(DOCUMENT_LIMIT));
		metaDataPanel.add(countyText, "flowx,cell 1 11,grow");
		
		countyCountBox = new JTextField();
		metaDataPanel.add(countyCountBox, "cell 2 11,alignx right,growy");
		countyCountBox.setHorizontalAlignment(SwingConstants.RIGHT);
		countyCountBox.setEditable(false);
		
		countyHelpTipButton = new HelpTipButton(LocalHelp.FIELD_COUNTY);
		metaDataPanel.add(countyHelpTipButton, "cell 3 11");
		parkText = new MetaDataTextField();
		parkText.addKeyListener(new KeyAdapter() {
			
			@Override
			public void keyReleased(KeyEvent e) {
				
				if (enforceOldReqsCheckBox.isSelected())
					if (CheckForNonCompatibleFieldLengths())
						enableTextFieldEnforcement();
			}
		});
		parkLabel = new javax.swing.JLabel();
		metaDataPanel.add(parkLabel, "cell 0 12");
		
		parkLabel.setText("Park:");
		
		parkText.setDocument(new LengthRestrictedDocument(DOCUMENT_LIMIT));
		metaDataPanel.add(parkText, "flowx,cell 1 12,grow");
		
		parkCountBox = new JTextField();
		metaDataPanel.add(parkCountBox, "cell 2 12,alignx right,growy");
		parkCountBox.setHorizontalAlignment(SwingConstants.RIGHT);
		parkCountBox.setEditable(false);
		
		parkHelpTipButton = new HelpTipButton(LocalHelp.FIELD_PARK);
		metaDataPanel.add(parkHelpTipButton, "cell 3 12");
		forestText = new MetaDataTextField();
		forestText.addKeyListener(new KeyAdapter() {
			
			@Override
			public void keyReleased(KeyEvent e) {
				
				if (enforceOldReqsCheckBox.isSelected())
					if (CheckForNonCompatibleFieldLengths())
						enableTextFieldEnforcement();
			}
		});
		forestLabel = new javax.swing.JLabel();
		metaDataPanel.add(forestLabel, "cell 0 13");
		
		forestLabel.setText("Forest:");
		
		forestText.setDocument(new LengthRestrictedDocument(DOCUMENT_LIMIT));
		metaDataPanel.add(forestText, "flowx,cell 1 13,grow");
		
		forestCountBox = new JTextField();
		metaDataPanel.add(forestCountBox, "cell 2 13,alignx right,growy");
		forestCountBox.setHorizontalAlignment(SwingConstants.RIGHT);
		forestCountBox.setEditable(false);
		
		forestHelpTipButton = new HelpTipButton(LocalHelp.FIELD_FOREST);
		metaDataPanel.add(forestHelpTipButton, "cell 3 13");
		rangerDistrictText = new MetaDataTextField();
		rangerDistrictText.addKeyListener(new KeyAdapter() {
			
			@Override
			public void keyReleased(KeyEvent e) {
				
				if (enforceOldReqsCheckBox.isSelected())
					if (CheckForNonCompatibleFieldLengths())
						enableTextFieldEnforcement();
			}
		});
		rangerDistrictLabel = new javax.swing.JLabel();
		metaDataPanel.add(rangerDistrictLabel, "cell 0 14");
		
		rangerDistrictLabel.setText("Ranger District of the site:");
		
		rangerDistrictText.setDocument(new LengthRestrictedDocument(DOCUMENT_LIMIT));
		metaDataPanel.add(rangerDistrictText, "flowx,cell 1 14,grow");
		
		rangerDistrictCountBox = new JTextField();
		metaDataPanel.add(rangerDistrictCountBox, "cell 2 14,alignx right,growy");
		rangerDistrictCountBox.setHorizontalAlignment(SwingConstants.RIGHT);
		rangerDistrictCountBox.setEditable(false);
		
		rangerDistrictHelpTipButton = new HelpTipButton(LocalHelp.FIELD_RANGER_DISTRICT);
		metaDataPanel.add(rangerDistrictHelpTipButton, "cell 3 14");
		townshipText = new MetaDataTextField();
		townshipText.addKeyListener(new KeyAdapter() {
			
			@Override
			public void keyReleased(KeyEvent e) {
				
				if (enforceOldReqsCheckBox.isSelected())
					if (CheckForNonCompatibleFieldLengths())
						enableTextFieldEnforcement();
			}
		});
		
		JLabel townshipLabel = new JLabel("Township of the site:");
		metaDataPanel.add(townshipLabel, "cell 0 15");
		
		townshipText.setDocument(new LengthRestrictedDocument(DOCUMENT_LIMIT));
		metaDataPanel.add(townshipText, "flowx,cell 1 15,grow");
		
		townshipCountBox = new JTextField();
		metaDataPanel.add(townshipCountBox, "cell 2 15,alignx right,growy");
		townshipCountBox.setHorizontalAlignment(SwingConstants.RIGHT);
		townshipCountBox.setEditable(false);
		
		townshipHelpTipButton = new HelpTipButton(LocalHelp.FIELD_TOWNSHIP_AND_RANGE);
		metaDataPanel.add(townshipHelpTipButton, "cell 3 15");
		rangeText = new MetaDataTextField();
		rangeText.addKeyListener(new KeyAdapter() {
			
			@Override
			public void keyReleased(KeyEvent e) {
				
				if (enforceOldReqsCheckBox.isSelected())
					if (CheckForNonCompatibleFieldLengths())
						enableTextFieldEnforcement();
			}
		});
		rangeLabel = new javax.swing.JLabel();
		metaDataPanel.add(rangeLabel, "cell 0 16");
		
		rangeLabel.setText("Range of the site:");
		
		rangeText.setDocument(new LengthRestrictedDocument(DOCUMENT_LIMIT));
		metaDataPanel.add(rangeText, "flowx,cell 1 16,grow");
		
		rangeCountBox = new JTextField();
		metaDataPanel.add(rangeCountBox, "cell 2 16,alignx right,growy");
		rangeCountBox.setHorizontalAlignment(SwingConstants.RIGHT);
		rangeCountBox.setEditable(false);
		
		rangeHelpTipButton = new HelpTipButton(LocalHelp.FIELD_TOWNSHIP_AND_RANGE);
		metaDataPanel.add(rangeHelpTipButton, "cell 3 16");
		sectionText = new MetaDataTextField();
		sectionText.addKeyListener(new KeyAdapter() {
			
			@Override
			public void keyReleased(KeyEvent e) {
				
				if (enforceOldReqsCheckBox.isSelected())
					if (CheckForNonCompatibleFieldLengths())
						enableTextFieldEnforcement();
			}
		});
		sectionLabel = new javax.swing.JLabel();
		metaDataPanel.add(sectionLabel, "cell 0 17");
		
		sectionLabel.setText("Section:");
		
		sectionText.setDocument(new LengthRestrictedDocument(DOCUMENT_LIMIT));
		metaDataPanel.add(sectionText, "flowx,cell 1 17,grow");
		
		sectionCountBox = new JTextField();
		metaDataPanel.add(sectionCountBox, "cell 2 17,alignx right,growy");
		sectionCountBox.setHorizontalAlignment(SwingConstants.RIGHT);
		sectionCountBox.setEditable(false);
		
		sectionHelpTipButton = new HelpTipButton(LocalHelp.FIELD_TOWNSHIP_AND_RANGE);
		metaDataPanel.add(sectionHelpTipButton, "cell 3 17");
		quarterSectionText = new MetaDataTextField();
		quarterSectionText.addKeyListener(new KeyAdapter() {
			
			@Override
			public void keyReleased(KeyEvent e) {
				
				if (enforceOldReqsCheckBox.isSelected())
					if (CheckForNonCompatibleFieldLengths())
						enableTextFieldEnforcement();
			}
		});
		quarterSectionLabel = new javax.swing.JLabel();
		metaDataPanel.add(quarterSectionLabel, "cell 0 18");
		
		quarterSectionLabel.setText("Quarter Section:");
		
		quarterSectionText.setDocument(new LengthRestrictedDocument(DOCUMENT_LIMIT));
		metaDataPanel.add(quarterSectionText, "flowx,cell 1 18,grow");
		
		quarterSectionCountBox = new JTextField();
		metaDataPanel.add(quarterSectionCountBox, "cell 2 18,alignx right,growy");
		quarterSectionCountBox.setHorizontalAlignment(SwingConstants.RIGHT);
		quarterSectionCountBox.setEditable(false);
		
		quarterSectionHelpTipButton = new HelpTipButton(LocalHelp.FIELD_TOWNSHIP_AND_RANGE);
		metaDataPanel.add(quarterSectionHelpTipButton, "cell 3 18");
		utmEastingText = new MetaDataTextField();
		utmEastingText.addKeyListener(new KeyAdapter() {
			
			@Override
			public void keyReleased(KeyEvent e) {
				
				if (enforceOldReqsCheckBox.isSelected())
					if (CheckForNonCompatibleFieldLengths())
						enableTextFieldEnforcement();
			}
		});
		utmEastingLabel = new javax.swing.JLabel();
		metaDataPanel.add(utmEastingLabel, "cell 0 19");
		
		utmEastingLabel.setText("UTM Easting:");
		
		utmEastingText.setDocument(new LengthRestrictedDocument(DOCUMENT_LIMIT));
		metaDataPanel.add(utmEastingText, "flowx,cell 1 19,grow");
		
		utmEastingCountBox = new JTextField();
		metaDataPanel.add(utmEastingCountBox, "cell 2 19,alignx right,growy");
		utmEastingCountBox.setHorizontalAlignment(SwingConstants.RIGHT);
		utmEastingCountBox.setEditable(false);
		
		utmEastingHelpTipButton = new HelpTipButton(LocalHelp.FIELD_UTM_EASTING);
		metaDataPanel.add(utmEastingHelpTipButton, "cell 3 19");
		utmNorthingText = new MetaDataTextField();
		utmNorthingText.addKeyListener(new KeyAdapter() {
			
			@Override
			public void keyReleased(KeyEvent e) {
				
				if (enforceOldReqsCheckBox.isSelected())
					if (CheckForNonCompatibleFieldLengths())
						enableTextFieldEnforcement();
			}
		});
		utmNorthingLabel = new javax.swing.JLabel();
		metaDataPanel.add(utmNorthingLabel, "cell 0 20");
		
		utmNorthingLabel.setText("UTM Northing:");
		
		utmNorthingText.setDocument(new LengthRestrictedDocument(DOCUMENT_LIMIT));
		metaDataPanel.add(utmNorthingText, "flowx,cell 1 20,grow");
		
		utmNorthingCountBox = new JTextField();
		metaDataPanel.add(utmNorthingCountBox, "cell 2 20,alignx right,growy");
		utmNorthingCountBox.setHorizontalAlignment(SwingConstants.RIGHT);
		utmNorthingCountBox.setEditable(false);
		
		utmNorthingHelpTipButton = new HelpTipButton(LocalHelp.FIELD_UTM_NORTHING);
		metaDataPanel.add(utmNorthingHelpTipButton, "cell 3 20");
		latitudeLabel = new javax.swing.JLabel();
		metaDataPanel.add(latitudeLabel, "cell 0 21");
		
		latitudeLabel.setText("Latitude:");
		latitudeText = new MetaDataTextField();
		latitudeText.setDocument(new LengthRestrictedDocument(DOCUMENT_LIMIT));
		latitudeText.addKeyListener(new KeyAdapter() {
			
			@Override
			public void keyReleased(KeyEvent e) {
				
				if (enforceOldReqsCheckBox.isSelected())
					if (CheckForNonCompatibleFieldLengths())
						enableTextFieldEnforcement();
			}
		});
		metaDataPanel.add(latitudeText, "flowx,cell 1 21,grow");
		
		latitudeCountBox = new JTextField();
		latitudeCountBox.setText("9");
		latitudeCountBox.setHorizontalAlignment(SwingConstants.RIGHT);
		latitudeCountBox.setEditable(false);
		
		metaDataPanel.add(latitudeCountBox, "cell 2 21,growx");
		
		latitudeHelpTipButton = new HelpTipButton(LocalHelp.FIELD_LATITUDE);
		metaDataPanel.add(latitudeHelpTipButton, "cell 3 21");
		longitudeLabel = new javax.swing.JLabel();
		metaDataPanel.add(longitudeLabel, "cell 0 22");
		
		longitudeLabel.setText("Longitude:");
		longitudeText = new MetaDataTextField();
		longitudeText.addKeyListener(new KeyAdapter() {
			
			@Override
			public void keyReleased(KeyEvent e) {
				
				if (enforceOldReqsCheckBox.isSelected())
					if (CheckForNonCompatibleFieldLengths())
						enableTextFieldEnforcement();
			}
		});
		metaDataPanel.add(longitudeText, "flowx,cell 1 22,grow");
		
		longitudeCountBox = new JTextField();
		longitudeText.setDocument(new LengthRestrictedDocument(DOCUMENT_LIMIT));
		longitudeCountBox.setText("9");
		longitudeCountBox.setHorizontalAlignment(SwingConstants.RIGHT);
		longitudeCountBox.setEditable(false);
		metaDataPanel.add(longitudeCountBox, "cell 2 22,growx");
		
		longitudeHelpTipButton = new HelpTipButton(LocalHelp.FIELD_LONGITUDE);
		metaDataPanel.add(longitudeHelpTipButton, "cell 3 22");
		topographicMapText = new MetaDataTextField();
		topographicMapText.addKeyListener(new KeyAdapter() {
			
			@Override
			public void keyReleased(KeyEvent e) {
				
				if (enforceOldReqsCheckBox.isSelected())
					if (CheckForNonCompatibleFieldLengths())
						enableTextFieldEnforcement();
			}
		});
		topographicMapLabel = new javax.swing.JLabel();
		metaDataPanel.add(topographicMapLabel, "cell 0 23");
		
		topographicMapLabel.setText("Topographic map:");
		
		topographicMapText.setDocument(new LengthRestrictedDocument(DOCUMENT_LIMIT));
		metaDataPanel.add(topographicMapText, "flowx,cell 1 23,grow");
		
		topographicMapCountBox = new JTextField();
		metaDataPanel.add(topographicMapCountBox, "cell 2 23,alignx right,growy");
		topographicMapCountBox.setHorizontalAlignment(SwingConstants.RIGHT);
		topographicMapCountBox.setEditable(false);
		
		topographicMapHelpTipButton = new HelpTipButton(LocalHelp.FIELD_TOPO_MAP);
		metaDataPanel.add(topographicMapHelpTipButton, "cell 3 23");
		highestElevationText = new MetaDataTextField();
		highestElevationText.addKeyListener(new KeyAdapter() {
			
			@Override
			public void keyReleased(KeyEvent e) {
				
				if (enforceOldReqsCheckBox.isSelected())
					if (CheckForNonCompatibleFieldLengths())
						enableTextFieldEnforcement();
			}
		});
		highestElevationLabel = new javax.swing.JLabel();
		metaDataPanel.add(highestElevationLabel, "cell 0 24");
		
		highestElevationLabel.setText("Highest Elevation:");
		
		highestElevationText.setDocument(new LengthRestrictedDocument(DOCUMENT_LIMIT));
		metaDataPanel.add(highestElevationText, "flowx,cell 1 24,grow");
		
		highestElevationCountBox = new JTextField();
		metaDataPanel.add(highestElevationCountBox, "cell 2 24,alignx right,growy");
		highestElevationCountBox.setHorizontalAlignment(SwingConstants.RIGHT);
		highestElevationCountBox.setEditable(false);
		
		highestElevationHelpTipButton = new HelpTipButton(LocalHelp.FIELD_HIGHEST_ELEV);
		metaDataPanel.add(highestElevationHelpTipButton, "cell 3 24");
		lowestElevationText = new MetaDataTextField();
		lowestElevationText.addKeyListener(new KeyAdapter() {
			
			@Override
			public void keyReleased(KeyEvent e) {
				
				if (enforceOldReqsCheckBox.isSelected())
					if (CheckForNonCompatibleFieldLengths())
						enableTextFieldEnforcement();
			}
		});
		
		JLabel lowestElevationLabel = new JLabel("Lowest elevation:");
		metaDataPanel.add(lowestElevationLabel, "cell 0 25");
		
		lowestElevationText.setDocument(new LengthRestrictedDocument(DOCUMENT_LIMIT));
		metaDataPanel.add(lowestElevationText, "flowx,cell 1 25,grow");
		
		lowestElevationCountBox = new JTextField();
		metaDataPanel.add(lowestElevationCountBox, "cell 2 25,alignx right,growy");
		lowestElevationCountBox.setHorizontalAlignment(SwingConstants.RIGHT);
		lowestElevationCountBox.setEditable(false);
		
		lowestElevationHelpTipButton = new HelpTipButton(LocalHelp.FIELD_LOWEST_ELEV);
		metaDataPanel.add(lowestElevationHelpTipButton, "cell 3 25");
		slopeText = new MetaDataTextField();
		slopeText.addKeyListener(new KeyAdapter() {
			
			@Override
			public void keyReleased(KeyEvent e) {
				
				if (enforceOldReqsCheckBox.isSelected())
					if (CheckForNonCompatibleFieldLengths())
						enableTextFieldEnforcement();
			}
		});
		slopeLabel = new javax.swing.JLabel();
		metaDataPanel.add(slopeLabel, "cell 0 26");
		
		slopeLabel.setText("Slope:");
		
		slopeText.setDocument(new LengthRestrictedDocument(DOCUMENT_LIMIT));
		metaDataPanel.add(slopeText, "flowx,cell 1 26,grow");
		
		slopeCountBox = new JTextField();
		metaDataPanel.add(slopeCountBox, "cell 2 26,alignx right,growy");
		slopeCountBox.setHorizontalAlignment(SwingConstants.RIGHT);
		slopeCountBox.setEditable(false);
		
		slopeHelpTipButton = new HelpTipButton(LocalHelp.FIELD_SLOPE);
		metaDataPanel.add(slopeHelpTipButton, "cell 3 26");
		aspectText = new MetaDataTextField();
		aspectText.addKeyListener(new KeyAdapter() {
			
			@Override
			public void keyReleased(KeyEvent e) {
				
				if (enforceOldReqsCheckBox.isSelected())
					if (CheckForNonCompatibleFieldLengths())
						enableTextFieldEnforcement();
			}
		});
		aspectLabel = new javax.swing.JLabel();
		metaDataPanel.add(aspectLabel, "cell 0 27");
		
		aspectLabel.setText("Aspect:");
		
		aspectText.setDocument(new LengthRestrictedDocument(DOCUMENT_LIMIT));
		metaDataPanel.add(aspectText, "flowx,cell 1 27,grow");
		
		aspectCountBox = new JTextField();
		metaDataPanel.add(aspectCountBox, "cell 2 27,alignx right,growy");
		aspectCountBox.setHorizontalAlignment(SwingConstants.RIGHT);
		aspectCountBox.setEditable(false);
		
		aspectHelpTipButton = new HelpTipButton(LocalHelp.FIELD_SLOPE_ASPECT);
		metaDataPanel.add(aspectHelpTipButton, "cell 3 27");
		areaSampledText = new MetaDataTextField();
		areaSampledText.addKeyListener(new KeyAdapter() {
			
			@Override
			public void keyReleased(KeyEvent e) {
				
				if (enforceOldReqsCheckBox.isSelected())
					if (CheckForNonCompatibleFieldLengths())
						enableTextFieldEnforcement();
			}
		});
		areaSampledLabel = new javax.swing.JLabel();
		metaDataPanel.add(areaSampledLabel, "cell 0 28");
		
		areaSampledLabel.setText("Area sampled:");
		
		areaSampledText.setDocument(new LengthRestrictedDocument(DOCUMENT_LIMIT));
		metaDataPanel.add(areaSampledText, "flowx,cell 1 28,grow");
		
		areaSampledCountBox = new JTextField();
		metaDataPanel.add(areaSampledCountBox, "cell 2 28,alignx right,growy");
		areaSampledCountBox.setHorizontalAlignment(SwingConstants.RIGHT);
		areaSampledCountBox.setEditable(false);
		
		areaSampledHelpTipButton = new HelpTipButton(LocalHelp.FIELD_AREA_SAMPLED);
		metaDataPanel.add(areaSampledHelpTipButton, "cell 3 28");
		
		metaDataScrollPane.setViewportView(metaDataPanel);
		substrateText = new MetaDataTextField();
		substrateText.addKeyListener(new KeyAdapter() {
			
			@Override
			public void keyReleased(KeyEvent e) {
				
				if (enforceOldReqsCheckBox.isSelected())
					if (CheckForNonCompatibleFieldLengths())
						enableTextFieldEnforcement();
			}
		});
		substrateLabel = new javax.swing.JLabel();
		metaDataPanel.add(substrateLabel, "cell 0 29");
		
		substrateLabel.setText("Substrate type:");
		substrateText.setDocument(new LengthRestrictedDocument(DOCUMENT_LIMIT));
		metaDataPanel.add(substrateText, "flowx,cell 1 29,grow");
		
		substrateCountBox = new JTextField();
		metaDataPanel.add(substrateCountBox, "cell 2 29,alignx right,growy");
		substrateCountBox.setHorizontalAlignment(SwingConstants.RIGHT);
		substrateCountBox.setEditable(false);
		
		substrateHelpTipButton = new HelpTipButton(LocalHelp.FIELD_SUBSTRATE);
		metaDataPanel.add(substrateHelpTipButton, "cell 3 29");
		numSamplesLabel = new javax.swing.JLabel();
		metaDataPanel.add(numSamplesLabel, "cell 0 30");
		
		numSamplesLabel.setText("Number of samples:");
		numSamplesText = new MetaDataTextField();
		numSamplesText.setFocusable(false);
		
		numSamplesText.setBackground(new java.awt.Color(240, 240, 240));
		numSamplesText.setEditable(false);
		metaDataPanel.add(numSamplesText, "cell 1 30,grow");
		
		convertLatToDecimalButton = new JButton("Convert to decimal degrees");
		convertLatToDecimalButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				
				if (latitudeText.getText() != null && latitudeText.getText().length() > 0)
				{
					try
					{
						Double coord = parseLatLonFromHalfLatLongString(latitudeText.getText());
						
						if (coord != null)
							latitudeText.setText(coord.toString());
							
					}
					catch (NumberFormatException ex)
					{
						JOptionPane.showMessageDialog(FileController.thePrimaryWindow,
								"Unable to convert latitude to decimal degrees.\n" + ex.getLocalizedMessage(), "Error",
								JOptionPane.ERROR_MESSAGE);
					}
				}
			}
		});
		metaDataPanel.add(convertLatToDecimalButton, "cell 1 21,alignx right,growy");
		
		convertLonToDecimalButton = new JButton("Convert to decimal degrees");
		convertLonToDecimalButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				
				if (longitudeText.getText() != null && longitudeText.getText().length() > 0)
				{
					try
					{
						Double coord = parseLatLonFromHalfLatLongString(longitudeText.getText());
						if (coord != null)
							longitudeText.setText(coord.toString());
					}
					catch (NumberFormatException ex)
					{
						JOptionPane.showMessageDialog(FileController.thePrimaryWindow,
								"Unable to convert longitude to decimal degrees.\n" + ex.getLocalizedMessage(), "Error",
								JOptionPane.ERROR_MESSAGE);
					}
				}
			}
		});
		setLayout(new BorderLayout(0, 0));
		
		metaDataPanel.add(convertLonToDecimalButton, "cell 1 22,alignx right,growy");
		
		numSamplesHelpTipButton = new HelpTipButton(LocalHelp.FIELD_SAMPLE_COUNT);
		metaDataPanel.add(numSamplesHelpTipButton, "cell 3 30");
		add(metaDataScrollPane);
	}
	
	/**
	 * Sets text fields to their appropriate values.
	 */
	private void fillTextFields() {
		
		siteNameText.setText(optionalData.getNameOfSite());
		siteCodeText.setText(optionalData.getSiteCode());
		collectionDateText.setText(optionalData.getCollectionDate());
		collectorNamesText.setText(optionalData.getCollectors());
		daterNamesText.setText(optionalData.getCrossdaters());
		latinNamesText.setText(optionalData.getSpeciesName());
		commonNamesText.setText(optionalData.getCommonName());
		habitatText.setText(optionalData.getHabitatType());
		countryText.setText(optionalData.getCountry());
		stateText.setText(optionalData.getState());
		countyText.setText(optionalData.getState());
		parkText.setText(optionalData.getParkMonument());
		forestText.setText(optionalData.getNationalForest());
		rangerDistrictText.setText(optionalData.getRangerDistrict());
		townshipText.setText(optionalData.getTownship());
		rangeText.setText(optionalData.getRange());
		sectionText.setText(optionalData.getSection());
		quarterSectionText.setText(optionalData.getSection());
		utmEastingText.setText(optionalData.getUtmEasting());
		utmNorthingText.setText(optionalData.getUtmNorthing());
		latitudeText.setText(optionalData.getLatitude());
		longitudeText.setText(optionalData.getLongitude());
		topographicMapText.setText(optionalData.getTopographicMap());
		highestElevationText.setText(optionalData.getHighestElev());
		lowestElevationText.setText(optionalData.getLowestElev());
		slopeText.setText(optionalData.getSlope());
		aspectText.setText(optionalData.getAspect());
		areaSampledText.setText(optionalData.getAreaSampled());
		substrateText.setText(optionalData.getSubstrateType());
		updateNumSamplesField();
		String t = siteNameText.getText();
		siteNameCountBox.setText("" + t.length());
		t = siteCodeText.getText();
		siteCodeCountBox.setText("" + t.length());
		t = collectionDateText.getText();
		collectionDateCountBox.setText("" + t.length());
		t = collectorNamesText.getText();
		collectorNamesCountBox.setText("" + t.length());
		t = daterNamesText.getText();
		daterNamesCountBox.setText("" + t.length());
		t = latinNamesText.getText();
		latinNamesCountBox.setText("" + t.length());
		t = commonNamesText.getText();
		commonNamesCountBox.setText("" + t.length());
		t = habitatText.getText();
		habitatCountBox.setText("" + t.length());
		t = countryText.getText();
		countryCountBox.setText("" + t.length());
		t = stateText.getText();
		stateCountBox.setText("" + t.length());
		t = countyText.getText();
		countyCountBox.setText("" + t.length());
		t = parkText.getText();
		parkCountBox.setText("" + t.length());
		t = forestText.getText();
		forestCountBox.setText("" + t.length());
		t = rangerDistrictText.getText();
		rangerDistrictCountBox.setText("" + t.length());
		t = townshipText.getText();
		townshipCountBox.setText("" + t.length());
		t = rangeText.getText();
		rangeCountBox.setText("" + t.length());
		t = sectionText.getText();
		sectionCountBox.setText("" + t.length());
		t = quarterSectionText.getText();
		quarterSectionCountBox.setText("" + t.length());
		t = utmEastingText.getText();
		utmEastingCountBox.setText("" + t.length());
		t = utmNorthingText.getText();
		utmNorthingCountBox.setText("" + t.length());
		t = topographicMapText.getText();
		topographicMapCountBox.setText("" + t.length());
		t = highestElevationText.getText();
		highestElevationCountBox.setText("" + t.length());
		t = lowestElevationText.getText();
		lowestElevationCountBox.setText("" + t.length());
		t = slopeText.getText();
		slopeCountBox.setText("" + t.length());
		t = aspectText.getText();
		aspectCountBox.setText("" + t.length());
		t = areaSampledText.getText();
		areaSampledCountBox.setText("" + t.length());
		t = substrateText.getText();
		substrateCountBox.setText("" + t.length());
	}
	
	/**
	 * Saves all data as it is displayed on the form.
	 */
	public void saveInfoToData() {
		
		IOController.getFile().getOptionalPart().setNameOfSite(siteNameText.getText());
		IOController.getFile().getOptionalPart().setSiteCode(siteCodeText.getText());
		IOController.getFile().getOptionalPart().setCollectionDate(collectionDateText.getText());
		IOController.getFile().getOptionalPart().setCollectors(collectorNamesText.getText());
		IOController.getFile().getOptionalPart().setCrossdaters(daterNamesText.getText());
		IOController.getFile().getOptionalPart().setSpeciesName(latinNamesText.getText());
		IOController.getFile().getOptionalPart().setCommonName(commonNamesText.getText());
		IOController.getFile().getOptionalPart().setHabitatType(habitatText.getText());
		IOController.getFile().getOptionalPart().setCountry(countryText.getText());
		IOController.getFile().getOptionalPart().setState(stateText.getText());
		IOController.getFile().getOptionalPart().setCounty(countyText.getText());
		IOController.getFile().getOptionalPart().setParkMonument(parkText.getText());
		IOController.getFile().getOptionalPart().setNationalForest(forestText.getText());
		IOController.getFile().getOptionalPart().setRangerDistrict(rangerDistrictText.getText());
		IOController.getFile().getOptionalPart().setTownship(townshipText.getText());
		IOController.getFile().getOptionalPart().setRange(rangeText.getText());
		IOController.getFile().getOptionalPart().setSection(sectionText.getText());
		IOController.getFile().getOptionalPart().setQuarterSection(quarterSectionText.getText());
		IOController.getFile().getOptionalPart().setUtmEasting(utmEastingText.getText());
		IOController.getFile().getOptionalPart().setUtmNorthing(utmNorthingText.getText());
		IOController.getFile().getOptionalPart().setLatitude(latitudeText.getText());
		IOController.getFile().getOptionalPart().setLongitude(longitudeText.getText());
		IOController.getFile().getOptionalPart().setTopographicMap(topographicMapText.getText());
		IOController.getFile().getOptionalPart().setLowestElev(lowestElevationText.getText());
		IOController.getFile().getOptionalPart().setHighestElev(highestElevationText.getText());
		IOController.getFile().getOptionalPart().setSlope(slopeText.getText());
		IOController.getFile().getOptionalPart().setAspect(aspectText.getText());
		IOController.getFile().getOptionalPart().setAreaSampled(areaSampledText.getText());
		IOController.getFile().getOptionalPart().setSubstrateType(substrateText.getText());
	}
	
	/**
	 * Updates the value of the numSamples text field.
	 */
	public static void updateNumSamplesField() {
		
		numSamplesText.setText("" + IOController.getFile().getRequiredPart().getNumSamples());
	}
	
	/**
	 * Attempt to convert a string to a decimal latitude or longitude value.
	 * 
	 * This function was copied in from org.tridas.spatialutils.
	 * 
	 * @param str
	 * @return
	 */
	public static Double parseLatLonFromHalfLatLongString(String str) throws NumberFormatException {
		
		str = str.trim();
		str = str.toUpperCase();
		if (str == null)
			return null;
		if (str == "")
			return null;
			
		String regex;
		Pattern p;
		Matcher m;
		Double deg = null;
		Double min = null;
		Double sec = null;
		String sign = null;
		
		// CHECK FOR DECIMAL DEGREE NOTATION
		regex = "[^\\d.-]";
		p = Pattern.compile(regex, Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
		m = p.matcher(str);
		if (!m.find())
		{
			// Only digits, minus sign and/or decimal point found
			Double val = Double.parseDouble(str);
			if (val <= 180.0 && val >= -180.0)
				// Value is within likely bounds so return
				return val;
			else
				// Value too big or small for a lat/long so return null
				throw new NumberFormatException("Parsed coordinate value outside lat/long bounds");
		}
		
		// CHECK FOR DMS NOTATION
		regex = "[^\\d|^.|^-]+";
		String[] val = str.split(regex);
		if (val.length == 0)
			// Either no numbers found, or more than three numbers were found
			throw new NumberFormatException("Coordinate string in unknown format");
		else if (val.length > 3)
			throw new NumberFormatException("Coordinate string in unknown format");
			
		try
		{
			deg = Double.parseDouble(val[0]);
		}
		catch (Exception e)
		{
			throw new NumberFormatException("Coordinate string in unknown format");
		}
		
		if (val.length >= 2)
			min = Double.parseDouble(val[1]);
		if (val.length == 3)
			sec = Double.parseDouble(val[2]);
			
		// Determine if direction sign is present
		String firstChar = str.substring(0, 1);
		String lastChar = str.substring(str.length() - 1, str.length());
		if (str.startsWith("N") || str.startsWith("S") || str.startsWith("E") || str.startsWith("W"))
			sign = firstChar;
		else if (str.endsWith("N") || str.endsWith("S") || str.endsWith("E") || str.endsWith("W"))
			sign = lastChar;
		else
		{
			// First and last chars aren't NSEW so check they are digits otherwise fail
			regex = "[\\d|-]";
			p = Pattern.compile(regex, Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
			m = p.matcher(firstChar);
			if (!m.find())
				throw new NumberFormatException(
						"Invalid direction sign found in coordinate string.  Direction sign must be one of N,S,E or W");
			regex = "[\\d|.]";
			p = Pattern.compile(regex, Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
			m = p.matcher(lastChar);
			if (!m.find())
				throw new NumberFormatException(
						"Invalid direction sign found in coordinate string.  Direction sign must be one of N,S,E or W");
		}
		if (sign != null)
			return getDecimalCoords(sign, deg, min, sec);
		else
			return getDecimalCoords(deg, min, sec);
	}
	
	/**
	 * Convert DMS with NSEW sign into decimal coordinates.
	 * 
	 * This function was copied in from org.tridas.spatialutils.
	 * 
	 * @param sign
	 * @param degrees
	 * @param minutes
	 * @param seconds
	 * @return
	 */
	public static Double getDecimalCoords(String sign, Double degrees, Double minutes, Double seconds) throws NumberFormatException {
		
		Double coords = getDecimalCoords(degrees, minutes, seconds);
		
		sign = sign.trim();
		
		if (sign.equalsIgnoreCase("S") || sign.equalsIgnoreCase("W"))
		{
			coords = 0 - coords;
			return coords;
		}
		else if (sign.equalsIgnoreCase("N") || sign.equalsIgnoreCase("E"))
			return coords;
			
		throw new NumberFormatException("Coordinate direction must be one of N,S,E or W, but direction was '" + sign + "'");
	}
	
	/**
	 * Convert DMS format coordinate into decimal degrees, where W and S are indicated by negative degrees.
	 * 
	 * This function was copied in from org.tridas.spatialutils.
	 * 
	 * @param degrees
	 * @param minutes
	 * @param seconds
	 * @return
	 */
	public static Double getDecimalCoords(Double degrees, Double minutes, Double seconds) throws NumberFormatException {
		
		Double coords = 0.0;
		Integer significantFigures = 0;
		
		if (degrees != null)
		{
			if (degrees <= 180.0 && degrees >= -180.0)
				coords = degrees;
			else
				throw new NumberFormatException("Degrees out of bounds");
		}
		else
			throw new NumberFormatException("Degrees cannot be null in a coordinate");
			
		if (minutes != null)
		{
			significantFigures = 4;
			if (minutes >= 0.0 && minutes < 60.0)
				coords = coords + Double.valueOf(minutes) / 60.0;
			else
				throw new NumberFormatException("Minutes out of bounds");
		}
		
		if (seconds != null)
		{
			significantFigures = 6;
			if (seconds >= 0.0 && seconds < 60.0)
			{
				Double secpart = ((Double.valueOf(seconds) / 60.0) / 60.0);
				coords = coords + secpart;
			}
			else
				throw new NumberFormatException("Seconds out of bounds");
		}
		BigDecimal bd = new BigDecimal(coords);
		bd = bd.setScale(significantFigures, BigDecimal.ROUND_CEILING);
		return bd.doubleValue();
	}
	
	/**
	 * Checks whether or not the current information in the site info fields violates any of the original FHX2 length requirements.
	 * 
	 * @return true if all fields are good, false if any field violates the requirements
	 */
	public boolean CheckForNonCompatibleFieldLengths() {
		
		int numFieldsCompatible = 0;
		
		// Handles updating of the site name display components
		String t = siteNameText.getText();
		if (t.length() > SEVENTY_CHARACTERS)
		{
			siteNameText.setForeground(Color.red);
			siteNameCountBox.setForeground(Color.red);
		}
		else
		{
			siteNameText.setForeground(Color.black);
			siteNameCountBox.setForeground(Color.black);
			numFieldsCompatible++;
		}
		siteNameCountBox.setText("" + t.length() + " / 70");
		
		// Handles updating of the site code display components
		t = siteCodeText.getText();
		if (t.length() > THREE_CHARACTERS)
		{
			siteCodeText.setForeground(Color.red);
			siteCodeCountBox.setForeground(Color.red);
		}
		else
		{
			siteCodeText.setForeground(Color.black);
			siteCodeCountBox.setForeground(Color.black);
			numFieldsCompatible++;
		}
		siteCodeCountBox.setText("" + t.length() + " / 3");
		
		// Handles updating of the collection date display components
		t = collectionDateText.getText();
		if (t.length() > TWENTY_CHARACTERS)
		{
			collectionDateText.setForeground(Color.red);
			collectionDateCountBox.setForeground(Color.red);
		}
		else
		{
			collectionDateText.setForeground(Color.black);
			collectionDateCountBox.setForeground(Color.black);
			numFieldsCompatible++;
		}
		collectionDateCountBox.setText("" + t.length() + " / 20");
		
		// Handles updating of the collector names display components
		t = collectorNamesText.getText();
		if (t.length() > SEVENTY_CHARACTERS)
		{
			collectorNamesText.setForeground(Color.red);
			collectorNamesCountBox.setForeground(Color.red);
		}
		else
		{
			collectorNamesText.setForeground(Color.black);
			collectorNamesCountBox.setForeground(Color.black);
			numFieldsCompatible++;
		}
		collectorNamesCountBox.setText("" + t.length() + " / 70");
		
		// Handles updating of the dater names display components
		t = daterNamesText.getText();
		if (t.length() > SEVENTY_CHARACTERS)
		{
			daterNamesText.setForeground(Color.red);
			daterNamesCountBox.setForeground(Color.red);
		}
		else
		{
			daterNamesText.setForeground(Color.black);
			daterNamesCountBox.setForeground(Color.black);
			numFieldsCompatible++;
		}
		daterNamesCountBox.setText("" + t.length() + " / 70");
		
		// Handles updating of the latin names display components
		t = latinNamesText.getText();
		if (t.length() > SEVENTY_CHARACTERS)
		{
			latinNamesText.setForeground(Color.red);
			latinNamesCountBox.setForeground(Color.red);
		}
		else
		{
			latinNamesText.setForeground(Color.black);
			latinNamesCountBox.setForeground(Color.black);
			numFieldsCompatible++;
		}
		latinNamesCountBox.setText("" + t.length() + " / 70");
		
		// Handles updating of the common names display components
		t = commonNamesText.getText();
		if (t.length() > SEVENTY_CHARACTERS)
		{
			commonNamesText.setForeground(Color.red);
			commonNamesCountBox.setForeground(Color.red);
		}
		else
		{
			commonNamesText.setForeground(Color.black);
			commonNamesCountBox.setForeground(Color.black);
			numFieldsCompatible++;
		}
		commonNamesCountBox.setText("" + t.length() + " / 70");
		
		// Handles updating of the habitat display components
		t = habitatText.getText();
		if (t.length() > SEVENTY_CHARACTERS)
		{
			habitatText.setForeground(Color.red);
			habitatCountBox.setForeground(Color.red);
		}
		else
		{
			habitatText.setForeground(Color.black);
			habitatCountBox.setForeground(Color.black);
			numFieldsCompatible++;
		}
		habitatCountBox.setText("" + t.length() + " / 70");
		
		// Handles updating of the country display components
		t = countryText.getText();
		if (t.length() > TWENTY_CHARACTERS)
		{
			countryText.setForeground(Color.red);
			countryCountBox.setForeground(Color.red);
		}
		else
		{
			countryText.setForeground(Color.black);
			countryCountBox.setForeground(Color.black);
			numFieldsCompatible++;
		}
		countryCountBox.setText("" + t.length() + " / 20");
		
		// Handles updating of the state display components
		t = stateText.getText();
		if (t.length() > FIFTEEN_CHARACTERS)
		{
			stateText.setForeground(Color.red);
			stateCountBox.setForeground(Color.red);
		}
		else
		{
			stateText.setForeground(Color.black);
			stateCountBox.setForeground(Color.black);
			numFieldsCompatible++;
		}
		stateCountBox.setText("" + t.length() + " / 15");
		
		// Handles updating of the county display components
		t = countyText.getText();
		if (t.length() > THIRTY_CHARACTERS)
		{
			countyText.setForeground(Color.red);
			countyCountBox.setForeground(Color.red);
		}
		else
		{
			countyText.setForeground(Color.black);
			countyCountBox.setForeground(Color.black);
			numFieldsCompatible++;
		}
		countyCountBox.setText("" + t.length() + " / 30");
		
		// Handles updating of the park display components
		t = parkText.getText();
		if (t.length() > FORTY_CHARACTERS)
		{
			parkText.setForeground(Color.red);
			parkCountBox.setForeground(Color.red);
		}
		else
		{
			parkText.setForeground(Color.black);
			parkCountBox.setForeground(Color.black);
			numFieldsCompatible++;
		}
		parkCountBox.setText("" + t.length() + " / 40");
		
		// Handles updating of the forest display components
		t = forestText.getText();
		if (t.length() > FORTY_CHARACTERS)
		{
			forestText.setForeground(Color.red);
			forestCountBox.setForeground(Color.red);
		}
		else
		{
			forestText.setForeground(Color.black);
			forestCountBox.setForeground(Color.black);
			numFieldsCompatible++;
		}
		forestCountBox.setText("" + t.length() + " / 40");
		
		// Handles updating of the ranger district display components
		t = rangerDistrictText.getText();
		if (t.length() > TWENTYFIVE_CHARACTERS)
		{
			rangerDistrictText.setForeground(Color.red);
			rangerDistrictCountBox.setForeground(Color.red);
		}
		else
		{
			rangerDistrictText.setForeground(Color.black);
			rangerDistrictCountBox.setForeground(Color.black);
			numFieldsCompatible++;
		}
		rangerDistrictCountBox.setText("" + t.length() + " / 25");
		
		// Handles updating of the township display components
		t = townshipText.getText();
		if (t.length() > FIVE_CHARACTERS)
		{
			townshipText.setForeground(Color.red);
			townshipCountBox.setForeground(Color.red);
		}
		else
		{
			townshipText.setForeground(Color.black);
			townshipCountBox.setForeground(Color.black);
			numFieldsCompatible++;
		}
		townshipCountBox.setText("" + t.length() + " / 5");
		
		// Handles updating of the range display components
		t = rangeText.getText();
		if (t.length() > FIVE_CHARACTERS)
		{
			rangeText.setForeground(Color.red);
			rangeCountBox.setForeground(Color.red);
		}
		else
		{
			rangeText.setForeground(Color.black);
			rangeCountBox.setForeground(Color.black);
			numFieldsCompatible++;
		}
		rangeCountBox.setText("" + t.length() + " / 5");
		
		// Handles updating of the section display components
		t = sectionText.getText();
		if (t.length() > TWO_CHARACTERS)
		{
			sectionText.setForeground(Color.red);
			sectionCountBox.setForeground(Color.red);
		}
		else
		{
			sectionText.setForeground(Color.black);
			sectionCountBox.setForeground(Color.black);
			numFieldsCompatible++;
		}
		sectionCountBox.setText("" + t.length() + " / 2");
		
		// Handles updating of the quarter section display components
		t = quarterSectionText.getText();
		if (t.length() > SIX_CHARACTERS)
		{
			quarterSectionText.setForeground(Color.red);
			quarterSectionCountBox.setForeground(Color.red);
		}
		else
		{
			quarterSectionText.setForeground(Color.black);
			quarterSectionCountBox.setForeground(Color.black);
			numFieldsCompatible++;
		}
		quarterSectionCountBox.setText("" + t.length() + " / 6");
		
		// Handles updating of the UTM easting display components
		t = utmEastingText.getText();
		if (t.length() > TWENTY_CHARACTERS)
		{
			utmEastingText.setForeground(Color.red);
			utmEastingCountBox.setForeground(Color.red);
		}
		else
		{
			utmEastingText.setForeground(Color.black);
			utmEastingCountBox.setForeground(Color.black);
			numFieldsCompatible++;
		}
		utmEastingCountBox.setText("" + t.length() + " / 20");
		
		// Handles updating of the UTM northing display components
		t = utmNorthingText.getText();
		if (t.length() > TWENTY_CHARACTERS)
		{
			utmNorthingText.setForeground(Color.red);
			utmNorthingCountBox.setForeground(Color.red);
		}
		else
		{
			utmNorthingText.setForeground(Color.black);
			utmNorthingCountBox.setForeground(Color.black);
			numFieldsCompatible++;
		}
		utmNorthingCountBox.setText("" + t.length() + " / 20");
		
		t = latitudeText.getText();
		if (t.length() > FIFTEEN_CHARACTERS)
		{
			latitudeText.setForeground(Color.red);
			latitudeCountBox.setForeground(Color.red);
		}
		else
		{
			latitudeText.setForeground(Color.black);
			latitudeCountBox.setForeground(Color.black);
			numFieldsCompatible++;
		}
		latitudeCountBox.setText("" + t.length() + " / 15");
		
		t = longitudeText.getText();
		if (t.length() > FIFTEEN_CHARACTERS)
		{
			longitudeText.setForeground(Color.red);
			longitudeCountBox.setForeground(Color.red);
		}
		else
		{
			longitudeText.setForeground(Color.black);
			longitudeCountBox.setForeground(Color.black);
			numFieldsCompatible++;
		}
		longitudeCountBox.setText("" + t.length() + " / 15");
		
		// Handles updating of the topographic map display components
		t = topographicMapText.getText();
		if (t.length() > THIRTY_CHARACTERS)
		{
			topographicMapText.setForeground(Color.red);
			topographicMapCountBox.setForeground(Color.red);
		}
		else
		{
			topographicMapText.setForeground(Color.black);
			topographicMapCountBox.setForeground(Color.black);
			numFieldsCompatible++;
		}
		topographicMapCountBox.setText("" + t.length() + " / 30");
		
		// Handles updating of the highest elevation display components
		t = highestElevationText.getText();
		if (t.length() > FOUR_CHARACTERS)
		{
			highestElevationText.setForeground(Color.red);
			highestElevationCountBox.setForeground(Color.red);
		}
		else
		{
			highestElevationText.setForeground(Color.black);
			highestElevationCountBox.setForeground(Color.black);
			numFieldsCompatible++;
		}
		highestElevationCountBox.setText("" + t.length() + " / 4");
		
		// Handles updating of the lowest elevation display components
		t = lowestElevationText.getText();
		if (t.length() > FOUR_CHARACTERS)
		{
			lowestElevationText.setForeground(Color.red);
			lowestElevationCountBox.setForeground(Color.red);
		}
		else
		{
			lowestElevationText.setForeground(Color.black);
			lowestElevationCountBox.setForeground(Color.black);
			numFieldsCompatible++;
		}
		lowestElevationCountBox.setText("" + t.length() + " / 4");
		
		// Handles updating of the slope display components
		t = slopeText.getText();
		if (t.length() > TEN_CHARACTERS)
		{
			slopeText.setForeground(Color.red);
			slopeCountBox.setForeground(Color.red);
		}
		else
		{
			slopeText.setForeground(Color.black);
			slopeCountBox.setForeground(Color.black);
			numFieldsCompatible++;
		}
		slopeCountBox.setText("" + t.length() + " / 10");
		
		// Handles updating of the aspect display components
		t = aspectText.getText();
		if (t.length() > FIVE_CHARACTERS)
		{
			aspectText.setForeground(Color.red);
			aspectCountBox.setForeground(Color.red);
		}
		else
		{
			aspectText.setForeground(Color.black);
			aspectCountBox.setForeground(Color.black);
			numFieldsCompatible++;
		}
		aspectCountBox.setText("" + t.length() + " / 5");
		
		// Handles updating of the area sampled display components
		t = areaSampledText.getText();
		if (t.length() > TEN_CHARACTERS)
		{
			areaSampledText.setForeground(Color.red);
			areaSampledCountBox.setForeground(Color.red);
		}
		else
		{
			areaSampledText.setForeground(Color.black);
			areaSampledCountBox.setForeground(Color.black);
			numFieldsCompatible++;
		}
		areaSampledCountBox.setText("" + t.length() + " / 10");
		
		// Handles updating of the substrate display components
		t = substrateText.getText();
		if (t.length() > SEVENTY_CHARACTERS)
		{
			substrateText.setForeground(Color.red);
			substrateCountBox.setForeground(Color.red);
		}
		else
		{
			substrateText.setForeground(Color.black);
			substrateCountBox.setForeground(Color.black);
			numFieldsCompatible++;
		}
		substrateCountBox.setText("" + t.length() + " / 70");
		
		// Updates the status pane according to the compatibility of the above fields
		if (numFieldsCompatible == ALL_FIELDS_COMPATIBLE)
		{
			if (FeedbackMessagePanel.getCurrentMessageID() == FeedbackMessageID.FHX2_META_DATA_LENGTH_MESSAGE)
				FireHistoryRecorder.clearFeedbackMessage();
			return true;
		}
		else
		{
			FireHistoryRecorder.updateFeedbackMessage(FeedbackMessageType.WARNING, Color.red,
					FeedbackMessageID.FHX2_META_DATA_LENGTH_MESSAGE, FeedbackMessageID.FHX2_META_DATA_LENGTH_MESSAGE.toString());
			return false;
		}
	}
	
	/**
	 * Resets the site info text field colors to black after changing to the new unrestricted data length format.
	 */
	private void resetTextBoxColors() {
		
		if (FeedbackMessagePanel.getCurrentMessageID() == FeedbackMessageID.FHX2_META_DATA_LENGTH_MESSAGE)
			FireHistoryRecorder.clearFeedbackMessage();
			
		siteNameText.setForeground(Color.black);
		siteNameCountBox.setForeground(Color.black);
		siteNameCountBox.setText("" + siteNameText.getText().length());
		siteCodeText.setForeground(Color.black);
		siteCodeCountBox.setForeground(Color.black);
		siteCodeCountBox.setText("" + siteCodeText.getText().length());
		collectionDateText.setForeground(Color.black);
		collectionDateCountBox.setForeground(Color.black);
		collectionDateCountBox.setText("" + collectionDateText.getText().length());
		collectorNamesText.setForeground(Color.black);
		collectorNamesCountBox.setForeground(Color.black);
		collectorNamesCountBox.setText("" + collectorNamesText.getText().length());
		daterNamesText.setForeground(Color.black);
		daterNamesCountBox.setForeground(Color.black);
		daterNamesCountBox.setText("" + daterNamesText.getText().length());
		latinNamesText.setForeground(Color.black);
		latinNamesCountBox.setForeground(Color.black);
		latinNamesCountBox.setText("" + latinNamesText.getText().length());
		commonNamesText.setForeground(Color.black);
		commonNamesCountBox.setForeground(Color.black);
		commonNamesCountBox.setText("" + commonNamesText.getText().length());
		habitatText.setForeground(Color.black);
		habitatCountBox.setForeground(Color.black);
		habitatCountBox.setText("" + habitatText.getText().length());
		countryText.setForeground(Color.black);
		countryCountBox.setForeground(Color.black);
		countryCountBox.setText("" + countryText.getText().length());
		stateText.setForeground(Color.black);
		stateCountBox.setForeground(Color.black);
		stateCountBox.setText("" + stateText.getText().length());
		countyText.setForeground(Color.black);
		countyCountBox.setForeground(Color.black);
		countyCountBox.setText("" + countyText.getText().length());
		parkText.setForeground(Color.black);
		parkCountBox.setForeground(Color.black);
		parkCountBox.setText("" + parkText.getText().length());
		forestText.setForeground(Color.black);
		forestCountBox.setForeground(Color.black);
		forestCountBox.setText("" + forestText.getText().length());
		rangerDistrictText.setForeground(Color.black);
		rangerDistrictCountBox.setForeground(Color.black);
		rangerDistrictCountBox.setText("" + rangerDistrictText.getText().length());
		townshipText.setForeground(Color.black);
		townshipCountBox.setForeground(Color.black);
		townshipCountBox.setText("" + townshipText.getText().length());
		rangeText.setForeground(Color.black);
		rangeCountBox.setForeground(Color.black);
		rangeCountBox.setText("" + rangeText.getText().length());
		sectionText.setForeground(Color.black);
		sectionCountBox.setForeground(Color.black);
		sectionCountBox.setText("" + sectionText.getText().length());
		quarterSectionText.setForeground(Color.black);
		quarterSectionCountBox.setForeground(Color.black);
		quarterSectionCountBox.setText("" + quarterSectionText.getText().length());
		utmEastingText.setForeground(Color.black);
		utmEastingCountBox.setForeground(Color.black);
		utmEastingCountBox.setText("" + utmEastingText.getText().length());
		utmNorthingText.setForeground(Color.black);
		utmNorthingCountBox.setForeground(Color.black);
		utmNorthingCountBox.setText("" + utmNorthingText.getText().length());
		topographicMapText.setForeground(Color.black);
		topographicMapCountBox.setForeground(Color.black);
		topographicMapCountBox.setText("" + topographicMapText.getText().length());
		highestElevationText.setForeground(Color.black);
		highestElevationCountBox.setForeground(Color.black);
		highestElevationCountBox.setText("" + highestElevationText.getText().length());
		lowestElevationText.setForeground(Color.black);
		lowestElevationCountBox.setForeground(Color.black);
		lowestElevationCountBox.setText("" + lowestElevationText.getText().length());
		slopeText.setForeground(Color.black);
		slopeCountBox.setForeground(Color.black);
		slopeCountBox.setText("" + slopeText.getText().length());
		aspectText.setForeground(Color.black);
		aspectCountBox.setForeground(Color.black);
		aspectCountBox.setText("" + aspectText.getText().length());
		areaSampledText.setForeground(Color.black);
		areaSampledCountBox.setForeground(Color.black);
		areaSampledCountBox.setText("" + areaSampledText.getText().length());
		substrateText.setForeground(Color.black);
		substrateCountBox.setForeground(Color.black);
		substrateCountBox.setText("" + substrateText.getText().length());
	}
	
	/**
	 * Enables length enforcement on all meta data text fields.
	 */
	private void enableTextFieldEnforcement() {
		
		String t = siteNameText.getText();
		siteNameText.setDocument(new LengthRestrictedDocument(SEVENTY_CHARACTERS));
		siteNameText.setText(t);
		t = siteCodeText.getText();
		siteCodeText.setDocument(new LengthRestrictedDocument(THREE_CHARACTERS));
		siteCodeText.setText(t);
		t = collectionDateText.getText();
		collectionDateText.setDocument(new LengthRestrictedDocument(TWENTY_CHARACTERS));
		collectionDateText.setText(t);
		t = collectorNamesText.getText();
		collectorNamesText.setDocument(new LengthRestrictedDocument(SEVENTY_CHARACTERS));
		collectorNamesText.setText(t);
		t = daterNamesText.getText();
		daterNamesText.setDocument(new LengthRestrictedDocument(SEVENTY_CHARACTERS));
		daterNamesText.setText(t);
		t = latinNamesText.getText();
		latinNamesText.setDocument(new LengthRestrictedDocument(SEVENTY_CHARACTERS));
		latinNamesText.setText(t);
		t = commonNamesText.getText();
		commonNamesText.setDocument(new LengthRestrictedDocument(SEVENTY_CHARACTERS));
		commonNamesText.setText(t);
		t = habitatText.getText();
		habitatText.setDocument(new LengthRestrictedDocument(SEVENTY_CHARACTERS));
		habitatText.setText(t);
		t = countryText.getText();
		countryText.setDocument(new LengthRestrictedDocument(TWENTY_CHARACTERS));
		countryText.setText(t);
		t = stateText.getText();
		stateText.setDocument(new LengthRestrictedDocument(FIFTEEN_CHARACTERS));
		stateText.setText(t);
		t = countyText.getText();
		countyText.setDocument(new LengthRestrictedDocument(THIRTY_CHARACTERS));
		countyText.setText(t);
		t = parkText.getText();
		parkText.setDocument(new LengthRestrictedDocument(FORTY_CHARACTERS));
		parkText.setText(t);
		t = forestText.getText();
		forestText.setDocument(new LengthRestrictedDocument(FORTY_CHARACTERS));
		forestText.setText(t);
		t = rangerDistrictText.getText();
		rangerDistrictText.setDocument(new LengthRestrictedDocument(TWENTYFIVE_CHARACTERS));
		rangerDistrictText.setText(t);
		t = townshipText.getText();
		townshipText.setDocument(new LengthRestrictedDocument(FIVE_CHARACTERS));
		townshipText.setText(t);
		t = rangeText.getText();
		rangeText.setDocument(new LengthRestrictedDocument(FIVE_CHARACTERS));
		rangeText.setText(t);
		t = sectionText.getText();
		sectionText.setDocument(new LengthRestrictedDocument(TWO_CHARACTERS));
		sectionText.setText(t);
		t = quarterSectionText.getText();
		quarterSectionText.setDocument(new LengthRestrictedDocument(SIX_CHARACTERS));
		quarterSectionText.setText(t);
		t = utmEastingText.getText();
		utmEastingText.setDocument(new LengthRestrictedDocument(TWENTY_CHARACTERS));
		utmEastingText.setText(t);
		t = utmNorthingText.getText();
		utmNorthingText.setDocument(new LengthRestrictedDocument(TWENTY_CHARACTERS));
		utmNorthingText.setText(t);
		t = topographicMapText.getText();
		topographicMapText.setDocument(new LengthRestrictedDocument(THIRTY_CHARACTERS));
		topographicMapText.setText(t);
		t = highestElevationText.getText();
		highestElevationText.setDocument(new LengthRestrictedDocument(FOUR_CHARACTERS));
		highestElevationText.setText(t);
		t = lowestElevationText.getText();
		lowestElevationText.setDocument(new LengthRestrictedDocument(FOUR_CHARACTERS));
		lowestElevationText.setText(t);
		t = slopeText.getText();
		slopeText.setDocument(new LengthRestrictedDocument(TEN_CHARACTERS));
		slopeText.setText(t);
		t = aspectText.getText();
		aspectText.setDocument(new LengthRestrictedDocument(TEN_CHARACTERS));
		aspectText.setText(t);
		t = areaSampledText.getText();
		areaSampledText.setDocument(new LengthRestrictedDocument(TEN_CHARACTERS));
		areaSampledText.setText(t);
		t = substrateText.getText();
		substrateText.setDocument(new LengthRestrictedDocument(SEVENTY_CHARACTERS));
		substrateText.setText(t);
	}
	
	/**
	 * Disables length enforcement on all meta data text fields.
	 */
	private void disableTextFieldEnforcement() {
		
		String t = siteNameText.getText();
		siteNameText.setDocument(new LengthRestrictedDocument(DOCUMENT_LIMIT));
		siteNameText.setText(t);
		t = siteCodeText.getText();
		siteCodeText.setDocument(new LengthRestrictedDocument(DOCUMENT_LIMIT));
		siteCodeText.setText(t);
		t = collectionDateText.getText();
		collectionDateText.setDocument(new LengthRestrictedDocument(DOCUMENT_LIMIT));
		collectionDateText.setText(t);
		t = collectorNamesText.getText();
		collectorNamesText.setDocument(new LengthRestrictedDocument(DOCUMENT_LIMIT));
		collectorNamesText.setText(t);
		t = daterNamesText.getText();
		daterNamesText.setDocument(new LengthRestrictedDocument(DOCUMENT_LIMIT));
		daterNamesText.setText(t);
		t = latinNamesText.getText();
		latinNamesText.setDocument(new LengthRestrictedDocument(DOCUMENT_LIMIT));
		latinNamesText.setText(t);
		t = commonNamesText.getText();
		commonNamesText.setDocument(new LengthRestrictedDocument(DOCUMENT_LIMIT));
		commonNamesText.setText(t);
		t = habitatText.getText();
		habitatText.setDocument(new LengthRestrictedDocument(DOCUMENT_LIMIT));
		habitatText.setText(t);
		t = countryText.getText();
		countryText.setDocument(new LengthRestrictedDocument(DOCUMENT_LIMIT));
		countryText.setText(t);
		t = stateText.getText();
		stateText.setDocument(new LengthRestrictedDocument(DOCUMENT_LIMIT));
		stateText.setText(t);
		t = countyText.getText();
		countyText.setDocument(new LengthRestrictedDocument(DOCUMENT_LIMIT));
		countyText.setText(t);
		t = parkText.getText();
		parkText.setDocument(new LengthRestrictedDocument(DOCUMENT_LIMIT));
		parkText.setText(t);
		t = forestText.getText();
		forestText.setDocument(new LengthRestrictedDocument(DOCUMENT_LIMIT));
		forestText.setText(t);
		t = rangerDistrictText.getText();
		rangerDistrictText.setDocument(new LengthRestrictedDocument(DOCUMENT_LIMIT));
		rangerDistrictText.setText(t);
		t = townshipText.getText();
		townshipText.setDocument(new LengthRestrictedDocument(DOCUMENT_LIMIT));
		townshipText.setText(t);
		t = rangeText.getText();
		rangeText.setDocument(new LengthRestrictedDocument(DOCUMENT_LIMIT));
		rangeText.setText(t);
		t = sectionText.getText();
		sectionText.setDocument(new LengthRestrictedDocument(DOCUMENT_LIMIT));
		sectionText.setText(t);
		t = quarterSectionText.getText();
		quarterSectionText.setDocument(new LengthRestrictedDocument(DOCUMENT_LIMIT));
		quarterSectionText.setText(t);
		t = utmEastingText.getText();
		utmEastingText.setDocument(new LengthRestrictedDocument(DOCUMENT_LIMIT));
		utmEastingText.setText(t);
		t = utmNorthingText.getText();
		utmNorthingText.setDocument(new LengthRestrictedDocument(DOCUMENT_LIMIT));
		utmNorthingText.setText(t);
		t = topographicMapText.getText();
		topographicMapText.setDocument(new LengthRestrictedDocument(DOCUMENT_LIMIT));
		topographicMapText.setText(t);
		t = highestElevationText.getText();
		highestElevationText.setDocument(new LengthRestrictedDocument(DOCUMENT_LIMIT));
		highestElevationText.setText(t);
		t = lowestElevationText.getText();
		lowestElevationText.setDocument(new LengthRestrictedDocument(DOCUMENT_LIMIT));
		lowestElevationText.setText(t);
		t = slopeText.getText();
		slopeText.setDocument(new LengthRestrictedDocument(DOCUMENT_LIMIT));
		slopeText.setText(t);
		t = aspectText.getText();
		aspectText.setDocument(new LengthRestrictedDocument(DOCUMENT_LIMIT));
		aspectText.setText(t);
		t = areaSampledText.getText();
		areaSampledText.setDocument(new LengthRestrictedDocument(DOCUMENT_LIMIT));
		areaSampledText.setText(t);
		t = substrateText.getText();
		substrateText.setDocument(new LengthRestrictedDocument(DOCUMENT_LIMIT));
		substrateText.setText(t);
	}
	
	/**
	 * Set visibility of field length boxes.
	 */
	private void setFieldLengthBoxesVisible(boolean b) {
		
		fieldLengthLabel.setVisible(b);
		siteNameCountBox.setVisible(b);
		siteCodeCountBox.setVisible(b);
		collectionDateCountBox.setVisible(b);
		collectorNamesCountBox.setVisible(b);
		daterNamesCountBox.setVisible(b);
		latinNamesCountBox.setVisible(b);
		commonNamesCountBox.setVisible(b);
		habitatCountBox.setVisible(b);
		countryCountBox.setVisible(b);
		stateCountBox.setVisible(b);
		countyCountBox.setVisible(b);
		parkCountBox.setVisible(b);
		forestCountBox.setVisible(b);
		rangerDistrictCountBox.setVisible(b);
		townshipCountBox.setVisible(b);
		rangeCountBox.setVisible(b);
		sectionCountBox.setVisible(b);
		quarterSectionCountBox.setVisible(b);
		utmEastingCountBox.setVisible(b);
		utmNorthingCountBox.setVisible(b);
		latitudeCountBox.setVisible(b);
		longitudeCountBox.setVisible(b);
		topographicMapCountBox.setVisible(b);
		highestElevationCountBox.setVisible(b);
		lowestElevationCountBox.setVisible(b);
		slopeCountBox.setVisible(b);
		aspectCountBox.setVisible(b);
		areaSampledCountBox.setVisible(b);
		substrateCountBox.setVisible(b);
	}
}
