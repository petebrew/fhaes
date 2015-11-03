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
import java.awt.Font;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextPane;
import javax.swing.border.EmptyBorder;

import net.miginfocom.swing.MigLayout;

import org.fhaes.help.RemoteHelp;
import org.fhaes.util.Builder;
import org.fhaes.util.Platform;

/**
 * AboutDialog Class. This is a basic about dialog for the application.
 * 
 * @author Peter Brewer
 */
public class AboutDialog extends JDialog implements ActionListener {
	
	private static final long serialVersionUID = 1L;
	private final JPanel contentPanel = new JPanel();
	private Frame parent;
	
	/**
	 * Create the dialog.
	 */
	public AboutDialog(Frame parent) {
	
		this.parent = parent;
		init();
	}
	
	/**
	 * TODO
	 */
	public void init() {
	
		getContentPane().setBackground(Color.WHITE);
		setBounds(100, 100, 614, 517);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBackground(Color.WHITE);
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(new MigLayout("", "[161.00px:n,grow,right][256.00,grow][]",
				"[43.00][][][38.00][][25.00][][grow][grow][41.00,grow][10px:n][grow][]"));
		
		JLabel lblNewLabel = new JLabel("");
		lblNewLabel.setIcon(Builder.getImageIcon("about.png"));
		contentPanel.add(lblNewLabel, "cell 0 0 2 6,alignx left");
		{
			JLabel lblFhaesfireHistory = new JLabel("FHAES");
			lblFhaesfireHistory.setFont(new Font("Dialog", Font.BOLD, 23));
			contentPanel.add(lblFhaesfireHistory, "cell 2 1,alignx right");
		}
		
		JLabel lblF = new JLabel("Fire History Analysis and Exploration System");
		lblF.setFont(new Font("Dialog", Font.BOLD, 10));
		contentPanel.add(lblF, "cell 2 2,alignx right,aligny top");
		
		JLabel lblVersionsnapshot = new JLabel("version: \n" + Builder.getVersion());
		lblVersionsnapshot.setFont(new Font("Dialog", Font.PLAIN, 10));
		contentPanel.add(lblVersionsnapshot, "cell 2 3,alignx right,aligny bottom");
		
		JLabel lblReleasedXxJune = new JLabel("built: " + Builder.getBuildTimestamp());
		lblReleasedXxJune.setFont(new Font("Dialog", Font.PLAIN, 10));
		contentPanel.add(lblReleasedXxJune, "cell 2 4,alignx right,aligny top");
		
		JLabel lblDOI = new JLabel(Builder.getDoiWithLabel());
		lblDOI.setFont(new Font("Dialog", Font.PLAIN, 10));
		contentPanel.add(lblDOI, "cell 2 5,alignx right,aligny top");
		
		JTextPane txtpnWorkingGroup = new JTextPane();
		txtpnWorkingGroup.setEditable(false);
		txtpnWorkingGroup.setFont(new Font("Dialog", Font.BOLD, 12));
		txtpnWorkingGroup.setText("Authors:");
		contentPanel.add(txtpnWorkingGroup, "cell 0 7,alignx right,growy");
		
		JTextPane txtcontributors = new JTextPane();
		txtcontributors.setEditable(false);
		txtcontributors.setFont(new Font("Dialog", Font.BOLD, 12));
		txtcontributors.setText("Elaine Kennedy Sutherland; Peter Brewer; Donald Falk; and M. Elena Velasquez.");
		contentPanel.add(txtcontributors, "cell 1 7 2 1,alignx left,growy");
		
		JTextPane txtpnScientificSteeringCommittee = new JTextPane();
		txtpnScientificSteeringCommittee.setText("Scientific steering committee:");
		txtpnScientificSteeringCommittee.setFont(new Font("Dialog", Font.PLAIN, 9));
		contentPanel.add(txtpnScientificSteeringCommittee, "cell 0 8,alignx right,growy");
		
		JTextPane txtpnElaineKennedySutherland = new JTextPane();
		txtpnElaineKennedySutherland.setText("Elaine Kennedy Sutherland; Tom Swetnam; Donald Falk; Peter Brown; Henri Grissino-Mayer.");
		txtpnElaineKennedySutherland.setFont(new Font("Dialog", Font.PLAIN, 9));
		contentPanel.add(txtpnElaineKennedySutherland, "cell 1 8 2 1,grow");
		
		JTextPane txtpnProgrammingContributions = new JTextPane();
		txtpnProgrammingContributions.setFont(new Font("Dialog", Font.PLAIN, 9));
		txtpnProgrammingContributions.setText("Contributing programmers:");
		contentPanel.add(txtpnProgrammingContributions, "cell 0 9,alignx right,growy");
		
		JTextPane txtProgrammers = new JTextPane();
		txtProgrammers.setFont(new Font("Dialog", Font.PLAIN, 9));
		txtProgrammers
				.setText("Peter Brewer; Elena Velasquez; Michael Ababio; Hidayatullah Ahsan; Alex Beatty; Clayton Bodendein; Joshua Brogan; Code Calhoun; Brendan Compton; Aaron Decker; Zachariah Ferree; Scott Goble; Wendy Gross; Kyle Hartmann; Dylan Jones; Anthony Messerschmidt; Alex Richter; Chinmay Shah; Chris Wald; Seth Westphal; Matthew Willie.");
		contentPanel.add(txtProgrammers, "cell 1 9 2 1,alignx left,growy");
		
		JTextPane txtpnThisProgramIs = new JTextPane();
		txtpnThisProgramIs.setEditable(false);
		txtpnThisProgramIs.setFont(new Font("Dialog", Font.PLAIN, 8));
		txtpnThisProgramIs
				.setText("This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.\n\nThis program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. ");
		contentPanel.add(txtpnThisProgramIs, "cell 0 11 3 1,grow");
		
		JPanel panel = new JPanel();
		panel.setBackground(Color.WHITE);
		panel.setBorder(null);
		contentPanel.add(panel, "cell 0 12 3 1,growx");
		
		JButton btnViewLicense = new JButton("GPL License");
		panel.add(btnViewLicense);
		btnViewLicense.setActionCommand("License");
		btnViewLicense.addActionListener(this);
		btnViewLicense.setFont(new Font("Dialog", Font.PLAIN, 10));
		
		JButton btnFhaesWebsite = new JButton("FHAES Website");
		panel.add(btnFhaesWebsite);
		btnFhaesWebsite.setActionCommand("FHAESWebsite");
		btnFhaesWebsite.addActionListener(this);
		btnFhaesWebsite.setFont(new Font("Dialog", Font.PLAIN, 10));
		
		this.setIconImage(Builder.getApplicationIcon());
		this.setLocationRelativeTo(parent);
		this.setModal(true);
		this.setResizable(false);
		this.setTitle("About FHAES");
	}
	
	/**
	 * TODO
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
	
		if (e.getActionCommand().equals("License"))
		{
			Platform.browseWebpage(RemoteHelp.LICENSE_INFO);
		}
		else if (e.getActionCommand().equals("FHAESWebsite"))
		{
			Platform.browseWebpage(RemoteHelp.FHAES_HOMEPAGE);
		}
	}
}
