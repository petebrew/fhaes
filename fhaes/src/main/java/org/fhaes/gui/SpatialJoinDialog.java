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
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;

import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.fhaes.enums.FireFilterType;
import org.fhaes.model.FHFile;
import org.fhaes.model.FHFileGroup;
import org.fhaes.model.FHFileGroupCellEditor;
import org.fhaes.model.FHFileGroupTableModel;
import org.fhaes.model.FHFileListCellRenderer;
import org.fhaes.preferences.App;
import org.fhaes.preferences.FHAESPreferences.PrefKey;
import org.fhaes.preferences.PrefsEvent;
import org.fhaes.preferences.PrefsListener;
import org.fhaes.preferences.wrappers.SpinnerWrapper;
import org.fhaes.tools.FHOperations;
import org.fhaes.util.Builder;
import org.fhaes.util.FHCluster;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.miginfocom.swing.MigLayout;

/**
 * SpatialJoinDialog Class.
 */
public class SpatialJoinDialog extends JDialog implements PrefsListener, ActionListener {
	
	private static final long serialVersionUID = 1L;
	private static final Logger log = LoggerFactory.getLogger(SpatialJoinDialog.class);
	private final JPanel contentPanel = new JPanel();
	private FHCluster fhc;
	private JTable tblGroups;
	@SuppressWarnings("rawtypes")
	private JList lstFiles;
	private JPanel panelMap;
	private FHFileGroupTableModel groupModel;
	@SuppressWarnings("rawtypes")
	private JComboBox cboOutputType;
	
	/**
	 * Create the dialog.
	 */
	public SpatialJoinDialog(ArrayList<FHFile> files) {
		
		if (files == null || files.size() == 0)
		{
			log.warn("SpatialJoinDialog opened with no files");
		}
		
		fhc = new FHCluster(null);
		
		fhc.setFileList(files);
		
		setupGUI();
		
		App.prefs.addPrefsListener(this);
		setGroups();
	}
	
	/**
	 * TODO
	 */
	private void setGroups() {
		
		groupModel = new FHFileGroupTableModel(fhc.getGroups());
		tblGroups.setModel(groupModel);
		tblGroups.getColumnModel().getColumn(0).setCellEditor(new FHFileGroupCellEditor());
		setupMap();
	}
	
	/**
	 * TODO
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void setFiles() {
		
		FHFileGroup g = (FHFileGroup) groupModel.getValueAt(tblGroups.getSelectedRow(), 0);
		
		if (g == null)
			return;
		if (g.getFiles() == null)
			return;
		if (g.getFiles().size() == 0)
			return;
			
		DefaultListModel fileModel = new DefaultListModel();
		for (FHFile f : g.getFiles())
		{
			fileModel.addElement(f);
		}
		
		lstFiles.setModel(fileModel);
		lstFiles.setCellRenderer(new FHFileListCellRenderer());
		
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void setupGUI() {
		
		setBounds(100, 100, 747, 536);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(new MigLayout("", "[right][59.00,grow][grow]", "[][][83.00,grow]"));
		{
			JLabel lblJoinSitesCloser = new JLabel("Join sites closer than:");
			contentPanel.add(lblJoinSitesCloser, "cell 0 0");
		}
		{
			JSpinner spnDistanceThreshold = new JSpinner();
			spnDistanceThreshold.setModel(new SpinnerNumberModel(new Integer(5), new Integer(1), null, new Integer(1)));
			new SpinnerWrapper(spnDistanceThreshold, PrefKey.COMPOSITE_DISTANCE_THRESHOLD_KM, 5);
			contentPanel.add(spnDistanceThreshold, "cell 1 0,growx");
		}
		{
			JLabel lblKm = new JLabel("km");
			contentPanel.add(lblKm, "cell 2 0");
		}
		{
			JLabel lblOutputFileType = new JLabel("Output file type:");
			contentPanel.add(lblOutputFileType, "cell 0 1,alignx trailing");
		}
		{
			cboOutputType = new JComboBox();
			cboOutputType.setModel(new DefaultComboBoxModel(new String[] { "Composite file", "Merge file", "Event file" }));
			contentPanel.add(cboOutputType, "cell 1 1,growx");
		}
		{
			JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
			contentPanel.add(tabbedPane, "cell 0 2 3 1,grow");
			{
				JPanel panelGroups = new JPanel();
				tabbedPane.addTab("Groups", null, panelGroups, null);
				panelGroups.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
				panelGroups.setLayout(new MigLayout("", "[339.00,grow]", "[]"));
				{
					JSplitPane splitPane = new JSplitPane();
					splitPane.setResizeWeight(0.5);
					panelGroups.add(splitPane, "cell 0 0,grow");
					{
						JPanel panel_1 = new JPanel();
						splitPane.setLeftComponent(panel_1);
						panel_1.setLayout(new MigLayout("", "[grow,fill]", "[grow,fill]"));
						{
							JLabel lblGroups = new JLabel("Groups:");
							panel_1.add(lblGroups, "flowy,cell 0 0");
						}
						{
							JScrollPane scrollPane = new JScrollPane();
							scrollPane.setBackground(Color.WHITE);
							scrollPane.getViewport().setBackground(Color.WHITE);
							panel_1.add(scrollPane, "cell 0 0");
							{
								tblGroups = new JTable();
								tblGroups.setBackground(Color.WHITE);
								
								tblGroups.setTableHeader(null);
								scrollPane.setViewportView(tblGroups);
								{
									JPanel panel_2 = new JPanel();
									splitPane.setRightComponent(panel_2);
									panel_2.setLayout(new MigLayout("", "[610.00,grow,fill]", "[][grow,fill]"));
									{
										JLabel lblFilesWithinGroup = new JLabel("File(s) within selected group:");
										panel_2.add(lblFilesWithinGroup, "cell 0 0");
									}
									{
										JScrollPane scrollPane_1 = new JScrollPane();
										panel_2.add(scrollPane_1, "cell 0 1,grow");
										{
											lstFiles = new JList();
											scrollPane_1.setViewportView(lstFiles);
										}
									}
								}
								
								tblGroups.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
									
									@Override
									public void valueChanged(ListSelectionEvent evt) {
										
										setFiles();
										
									}
									
								});
							}
						}
					}
					splitPane.setDividerLocation(0.45);
				}
			}
			{
				panelMap = new JPanel();
				tabbedPane.addTab("Map", null, panelMap, null);
				panelMap.setLayout(new BorderLayout(0, 0));
			}
		}
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton okButton = new JButton("Save");
				okButton.setActionCommand("Save");
				okButton.addActionListener(this);
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
			}
			{
				JButton cancelButton = new JButton("Cancel");
				cancelButton.setActionCommand("Cancel");
				cancelButton.addActionListener(this);
				buttonPane.add(cancelButton);
			}
		}
		
		this.setTitle("Spatial Join");
		this.setIconImage(Builder.getApplicationIcon());
		this.setLocationRelativeTo(null);
	}
	
	private void setupMap() {
		
		panelMap.removeAll();
		MapPanel map = new MapPanel();
		map.setFHFileGroups(fhc.getGroups());
		panelMap.add(map, BorderLayout.CENTER);
		
	}
	
	private Boolean save() {
		
		File file = null;
		JFileChooser fc;
		
		// Open file chooser in last folder if possible
		if (App.prefs.getPref(PrefKey.PREF_LAST_EXPORT_FOLDER, null) != null)
		{
			fc = new JFileChooser(App.prefs.getPref(PrefKey.PREF_LAST_EXPORT_FOLDER, null));
		}
		else
		{
			fc = new JFileChooser();
		}
		
		// Show dialog and get specified file
		fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		int returnVal = fc.showSaveDialog(this);
		if (returnVal == JFileChooser.APPROVE_OPTION)
		{
			file = fc.getSelectedFile();
			App.prefs.setPref(PrefKey.PREF_LAST_EXPORT_FOLDER, file.getAbsolutePath());
		}
		else
		{
			return false;
		}
		
		CompositeFilterDialog compositeDialog = null;
		TemporalFilterDialog temporalDialog = null;
		if (cboOutputType.getSelectedIndex() == 0)
		{
			// Creating composite file so need composite and temporal parameters
			compositeDialog = new CompositeFilterDialog();
			compositeDialog.setIconImage(Builder.getApplicationIcon());
			compositeDialog.setModal(true);
			compositeDialog.setVisible(true);
			if (!compositeDialog.success())
				return false;
		}
		else if (cboOutputType.getSelectedIndex() == 2)
		{
			// Creating event file so need composite, temporal parameters and comments
			compositeDialog = new CompositeFilterDialog(true);
			compositeDialog.setIconImage(Builder.getApplicationIcon());
			compositeDialog.setModal(true);
			compositeDialog.setVisible(true);
			if (!compositeDialog.success())
				return false;
		}
		else
		{
			// Creating merge file so only need temporal parameters
			temporalDialog = new TemporalFilterDialog();
			temporalDialog.setIconImage(Builder.getApplicationIcon());
			temporalDialog.setModal(true);
			temporalDialog.setVisible(true);
			if (!temporalDialog.success())
				return false;
		}
		
		for (int i = 0; i < groupModel.getRowCount(); i++)
		{
			FHFileGroup gr = (FHFileGroup) groupModel.getValueAt(i, 0);
			File outputfile = null;
			
			if (cboOutputType.getSelectedIndex() == 0)
			{
				outputfile = new File(file.getAbsolutePath() + File.separator + gr.getName() + ".fhx");
				
				// Composite
				new FHOperations(App.mainFrame, gr.getFiles().toArray(new File[gr.getFiles().size()]), outputfile,
						compositeDialog.getStartYear(), compositeDialog.getEndYear(), compositeDialog.getFireFilterValue(),
						compositeDialog.getFireFilterType(), false, true, false, compositeDialog.getMinNumberOfSamples(), null);
			}
			else if (cboOutputType.getSelectedIndex() == 1)
			{
				outputfile = new File(file.getAbsolutePath() + File.separator + gr.getName() + ".fhx");
				
				// Merge
				new FHOperations(App.mainFrame, gr.getFiles().toArray(new File[gr.getFiles().size()]), outputfile,
						temporalDialog.getStartYear(), temporalDialog.getEndYear(), 1.0, FireFilterType.NUMBER_OF_EVENTS, true, false,
						false, 1, null);
			}
			else if (cboOutputType.getSelectedIndex() == 2)
			{
				outputfile = new File(file.getAbsolutePath() + File.separator + gr.getName() + ".txt");
				
				// Event file
				new FHOperations(App.mainFrame, gr.getFiles().toArray(new File[gr.getFiles().size()]), outputfile,
						compositeDialog.getStartYear(), compositeDialog.getEndYear(), compositeDialog.getFireFilterValue(),
						compositeDialog.getFireFilterType(), false, false, true, compositeDialog.getMinNumberOfSamples(),
						compositeDialog.getComments());
						
			}
			
		}
		return true;
	}
	
	@Override
	public void prefChanged(PrefsEvent e) {
		
		log.debug("Preference change for key " + e.getPref() + " picked up by SpatialJoinDialog");
		
		if (e.getPref().equals(PrefKey.COMPOSITE_DISTANCE_THRESHOLD_KM))
		{
			fhc.process();
			setGroups();
		}
		
	}
	
	@Override
	public void actionPerformed(ActionEvent evt) {
		
		if (evt.getActionCommand().equals("Save"))
		{
			Boolean success = save();
			if (success)
				dispose();
		}
		else if (evt.getActionCommand().equals("Cancel"))
		{
			dispose();
		}
		
	}
	
}
