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
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import org.fhaes.preferences.App;
import org.fhaes.preferences.FHAESPreferences.PrefKey;
import org.fhaes.preferences.wrappers.CheckBoxWrapper;
import org.fhaes.util.Builder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.miginfocom.swing.MigLayout;

/**
 * FHRecorderWarningDialog Class.
 */
public class FHRecorderWarningDialog extends JDialog {
	
	private static final long serialVersionUID = 1L;
	private final JPanel contentPanel = new JPanel();
	private Boolean shallWeContinue = false;
	private static final Logger log = LoggerFactory.getLogger(FHRecorderWarningDialog.class);
	
	/**
	 * TODO
	 * 
	 * @return
	 */
	public Boolean getResult() {
		
		return shallWeContinue;
	}
	
	/**
	 * TODO
	 * 
	 * @param fileCount
	 * @return
	 */
	public static Boolean showWarning(Integer fileCount) {
		
		if (!App.prefs.getBooleanPref(PrefKey.LARGE_DATASET_WARNING_DISABLED, false))
		{
			FHRecorderWarningDialog dialog = new FHRecorderWarningDialog(fileCount);
			return dialog.getResult();
		}
		else
		{
			log.debug("User has asked not to be warned about large datasets");
		}
		
		return true;
	}
	
	/**
	 * Create the dialog.
	 */
	public FHRecorderWarningDialog(Integer fileCount) {
		
		setBounds(100, 100, 328, 250);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(new MigLayout("", "[][grow]", "[][grow,top]"));
		{
			JLabel lblIcon = new JLabel("");
			lblIcon.setIcon(Builder.getImageIcon("warning.png"));
			contentPanel.add(lblIcon, "cell 0 0");
			
		}
		{
			JLabel lblTitle = new JLabel("WARNING! Large dataset");
			lblTitle.setFont(new Font("Dialog", Font.BOLD, 14));
			contentPanel.add(lblTitle, "cell 1 0");
		}
		{
			JLabel txtWarning = new JLabel();
			txtWarning.setFont(new Font("Dialog", Font.PLAIN, 12));
			txtWarning.setText("<html>You are attempting to load and analyze " + fileCount
					+ " FHX files.  FHAES uses text files to handle data which become increasingly inefficient as the number of datasets increases.  The actual limit will depend on the memory available in your computer.  A large number of records may make FHAES sluggish or cause it to freeze.  If that happens you'll need to limit your datasets or simplify your data by creating composite files.<br/><br/>\n\nAre you sure you want to continue?</html>");
			contentPanel.add(txtWarning, "cell 0 1 2 1,grow");
		}
		{
			JPanel buttonPane = new JPanel();
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			buttonPane.setLayout(new MigLayout("", "[172px][grow][][][]", "[25px]"));
			{
				JCheckBox chkNoMoreWarnings = new JCheckBox("Don't warn me again");
				new CheckBoxWrapper(chkNoMoreWarnings, PrefKey.LARGE_DATASET_WARNING_DISABLED, false);
				buttonPane.add(chkNoMoreWarnings, "cell 0 0,alignx left,aligny center");
			}
			{
				JButton btnYes = new JButton("Yes");
				btnYes.addActionListener(new ActionListener() {
					
					@Override
					public void actionPerformed(ActionEvent arg0) {
						
						shallWeContinue = true;
						setVisible(false);
						
					}
					
				});
				btnYes.setActionCommand("OK");
				buttonPane.add(btnYes, "cell 2 0,alignx left,aligny top");
				getRootPane().setDefaultButton(btnYes);
			}
			{
				JButton btnNo = new JButton("No");
				btnNo.addActionListener(new ActionListener() {
					
					@Override
					public void actionPerformed(ActionEvent arg0) {
						
						setVisible(false);
						
					}
					
				});
				buttonPane.add(btnNo, "cell 3 0,alignx left,aligny top");
			}
			{
				JButton btnCancel = new JButton("Cancel");
				btnCancel.addActionListener(new ActionListener() {
					
					@Override
					public void actionPerformed(ActionEvent arg0) {
						
						setVisible(false);
						
					}
					
				});
				btnCancel.setActionCommand("Cancel");
				buttonPane.add(btnCancel, "cell 4 0,alignx left,aligny top");
			}
		}
		
		this.setResizable(false);
		this.setModal(true);
		this.setIconImage(Builder.getApplicationIcon());
		setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		this.setSize(new Dimension(500, 250));
		this.setLocationRelativeTo(null);
		setVisible(true);
	}
	
}
