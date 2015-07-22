/*******************************************************************************
 * Copyright (C) 2013 Peter Brewer
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * Contributors:
 *     Peter Brewer
 *     Elena Velasquez
 ******************************************************************************/
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
 * @author pbrewer
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
		setBounds(100, 100, 608, 499);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBackground(Color.WHITE);
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(new MigLayout("", "[200px:200px:200px,grow,right][71.00,grow][][167.00,grow]",
				"[43.00][][][38.00][][][grow][41.00,grow][10px:n][grow][]"));

		JLabel lblNewLabel = new JLabel("");
		lblNewLabel.setIcon(Builder.getImageIcon("about.png"));
		contentPanel.add(lblNewLabel, "cell 0 0 2 5");
		{
			JLabel lblFhaesfireHistory = new JLabel("FHAES");
			lblFhaesfireHistory.setFont(new Font("Dialog", Font.BOLD, 23));
			contentPanel.add(lblFhaesfireHistory, "cell 2 1 2 1,alignx right");
		}

		JLabel lblF = new JLabel("Fire History Analysis and Exploration System");
		lblF.setFont(new Font("Dialog", Font.BOLD, 10));
		contentPanel.add(lblF, "cell 2 2 2 1,alignx right,aligny top");

		JLabel lblVersionsnapshot = new JLabel("version: " + Builder.getVersionAndBuild());
		lblVersionsnapshot.setFont(new Font("Dialog", Font.PLAIN, 10));
		contentPanel.add(lblVersionsnapshot, "cell 2 3 2 1,alignx right,aligny bottom");

		JLabel lblReleasedXxJune = new JLabel("built: " + Builder.getBuildTimestamp());
		lblReleasedXxJune.setFont(new Font("Dialog", Font.PLAIN, 10));
		contentPanel.add(lblReleasedXxJune, "cell 2 4 2 1,alignx right,aligny top");

		JTextPane txtpnWorkingGroup = new JTextPane();
		txtpnWorkingGroup.setEditable(false);
		txtpnWorkingGroup.setFont(new Font("Dialog", Font.BOLD, 11));
		txtpnWorkingGroup.setText("Working group:");
		contentPanel.add(txtpnWorkingGroup, "cell 0 6,alignx right,growy");

		JTextPane txtcontributors = new JTextPane();
		txtcontributors.setEditable(false);
		txtcontributors.setFont(new Font("Dialog", Font.BOLD, 11));
		txtcontributors
				.setText("Elaine Kennedy Sutherland; Peter Brown; Donald Falk; Henri Grissino-Mayer; Elena Velasquez; Connie Woodhouse; Peter Brewer.");
		contentPanel.add(txtcontributors, "cell 1 6 3 1,grow");

		JTextPane txtpnProgrammingContributions = new JTextPane();
		txtpnProgrammingContributions.setFont(new Font("Dialog", Font.PLAIN, 9));
		txtpnProgrammingContributions.setText("Programming contributions:");
		contentPanel.add(txtpnProgrammingContributions, "cell 0 7,alignx right,growy");

		JTextPane txtProgrammers = new JTextPane();
		txtProgrammers.setFont(new Font("Dialog", Font.PLAIN, 9));
		txtProgrammers
				.setText("Peter Brewer; Elena Velasquez; Michael Ababio; Hidayatullah Ahsan; Alex Beatty; Clayton Bodendein; Joshua Brogan; Code Calhoun; Brendan Compton; Aaron Decker; Zachariah Ferree; Scott Goble; Wendy Gross; Kyle Hartmann; Dylan Jones; Anthony Messerschmidt; Alex Richter; Chinmay Shah; Chris Wald; Seth Westphal; Matthew Willie.");
		contentPanel.add(txtProgrammers, "cell 1 7 3 1,grow");

		JTextPane txtpnThisProgramIs = new JTextPane();
		txtpnThisProgramIs.setEditable(false);
		txtpnThisProgramIs.setFont(new Font("Dialog", Font.PLAIN, 8));
		txtpnThisProgramIs
				.setText("This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.\n\nThis program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. ");
		contentPanel.add(txtpnThisProgramIs, "cell 0 9 4 1,grow");

		JPanel panel = new JPanel();
		panel.setBackground(Color.WHITE);
		panel.setBorder(null);
		contentPanel.add(panel, "cell 0 10 4 1,growx");

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
