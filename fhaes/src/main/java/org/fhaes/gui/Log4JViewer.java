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
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Date;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.fhaes.util.Builder;
import org.fhaes.util.TextAreaLogger;

import com.ibm.icu.text.DateFormat;
import com.ibm.icu.text.SimpleDateFormat;

import net.miginfocom.swing.MigLayout;

/**
 * Log4JViewer Class.
 */
public class Log4JViewer extends JDialog {
	
	private static final long serialVersionUID = 1L;
	private final JPanel contentPanel = new JPanel();
	private JTextArea txtLog;
	private JScrollPane scrollPane;
	
	/**
	 * Create the dialog.
	 */
	public Log4JViewer() {
		
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(new BorderLayout(0, 0));
		{
			scrollPane = new JScrollPane();
			contentPanel.add(scrollPane);
			{
				txtLog = new JTextArea();
				
				DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
				Date date = new Date();
				
				txtLog.setText("********************************\n" + "FHAES Application initialized\n" + "  " + dateFormat.format(date)
						+ "\n" + "********************************\n\n");
						
				txtLog.setFont(new Font("Andale Mono", Font.PLAIN, 11));
				
				txtLog.getDocument().addDocumentListener(new DocumentListener() {
					
					@Override
					public void changedUpdate(DocumentEvent arg0) {
						
						scrollToBottom();
					}
					
					@Override
					public void insertUpdate(DocumentEvent arg0) {
						
						scrollToBottom();
					}
					
					@Override
					public void removeUpdate(DocumentEvent arg0) {
						
						scrollToBottom();
					}
					
				});
				
				TextAreaLogger appender = new TextAreaLogger(new PatternLayout("%-6p : %m [%c{1}]%n"), txtLog);
				Logger.getRootLogger().addAppender(appender);
				scrollPane.setViewportView(txtLog);
			}
		}
		{
			JPanel buttonPane = new JPanel();
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton btnClose = new JButton("Close");
				btnClose.addActionListener(new ActionListener() {
					
					@Override
					public void actionPerformed(ActionEvent arg0) {
						
						setVisible(false);
						
					}
					
				});
				buttonPane.setLayout(new MigLayout("", "[][31.00][91.00,grow][73px]", "[25px]"));
				{
					JButton btnSelectAll = new JButton("Select all");
					btnSelectAll.addActionListener(new ActionListener() {
						
						@Override
						public void actionPerformed(ActionEvent arg0) {
							
							txtLog.selectAll();
							
						}
						
					});
					buttonPane.add(btnSelectAll, "cell 0 0");
				}
				{
					JButton btnCopy = new JButton("Copy");
					btnCopy.addActionListener(new ActionListener() {
						
						@Override
						public void actionPerformed(ActionEvent e) {
							
							txtLog.copy();
							
						}
						
					});
					buttonPane.add(btnCopy, "cell 1 0");
				}
				btnClose.setActionCommand("Cancel");
				buttonPane.add(btnClose, "cell 3 0,alignx left,aligny top");
				getRootPane().setDefaultButton(btnClose);
			}
		}
		setDefaultCloseOperation(HIDE_ON_CLOSE);
		setIconImage(Builder.getApplicationIcon());
		setTitle("Error log viewer");
		pack();
		this.setSize(new Dimension(640, 480));
		this.setLocationRelativeTo(null);
	}
	
	/**
	 * TODO
	 */
	private void scrollToBottom() {
		
		JScrollBar vertical = scrollPane.getVerticalScrollBar();
		vertical.setValue(vertical.getMaximum());
		
	}
	
	/**
	 * TODO
	 */
	public static void showLogViewer() {
		
		Log4JViewer viewer = new Log4JViewer();
		viewer.setVisible(true);
		
	}
}
