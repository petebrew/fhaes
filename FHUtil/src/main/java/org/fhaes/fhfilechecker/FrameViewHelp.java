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
package org.fhaes.fhfilechecker;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.print.PageFormat;
import java.awt.print.PrinterJob;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import org.fhaes.util.Builder;

/**
 * FrameViewHelp Class.
 */
public class FrameViewHelp extends JFrame {
	
	private static final long serialVersionUID = 1L;
	JPanel panel1 = new JPanel();
	BorderLayout borderLayout = new BorderLayout();
	JPanel jPanel = new JPanel();
	JButton btnPrint = new JButton();
	JButton btnClose = new JButton();
	public JTextArea TArea = new JTextArea();
	JScrollPane jScrollPane = new JScrollPane();
	public String out_file_name;
	
	/**
	 * TODO
	 */
	public FrameViewHelp() {
		
		try
		{
			jbInit();
		}
		catch (Exception exception)
		{
			System.out.println("JBINIT EXCEPTION");
			exception.printStackTrace();
		}
	}
	
	/**
	 * TODO
	 * 
	 * @throws Exception
	 */
	private void jbInit() throws Exception {
		
		panel1.setLayout(borderLayout);
		this.setTitle("FHAES File Format Report (Outputfile)");
		setIconImage(Builder.getApplicationIcon());
		btnPrint.setText("Print");
		btnPrint.addActionListener(new Frame_HelpView_btnPrint_actionAdapter(this));
		btnClose.setText("Close");
		btnClose.addActionListener(new Frame_HelpView_btnClose_actionAdapter(this));
		TArea.setText("");
		this.addWindowListener(new Frame_HelpView_this_windowAdapter(this));
		getContentPane().add(panel1);
		jPanel.add(btnPrint);
		jPanel.add(btnClose);
		panel1.add(jPanel, java.awt.BorderLayout.NORTH);
		panel1.add(jScrollPane, java.awt.BorderLayout.CENTER);
		// panel1.add(mygraph, java.awt.BorderLayout.SOUTH);
		jScrollPane.getViewport().add(TArea);
		// System.out.println(jScrollPane.getVerticalScrollBar().getMinimum());
	}
	
	/**
	 * TODO
	 * 
	 * @param e
	 */
	public void btnClose_actionPerformed(ActionEvent e) {
		
		dispose();
	}
	
	/**
	 * TODO
	 * 
	 * @param e
	 */
	// http://java.sun.com/j2se/1.3/docs/guide/2d/spec/j2d-print.fm3.html
	public void btnPrint_actionPerformed(ActionEvent e) {
		
		// Get a PrinterJob
		PrinterJob job = PrinterJob.getPrinterJob();
		// Ask user for page format (e.g., portrait/landscape)
		PageFormat pf = job.pageDialog(job.defaultPage());
		// Specify the Printable is an instance of
		// PrintListingPainter; also provide given PageFormat
		job.setPrintable(new PrintListingPainter(out_file_name), pf);
		// Print 1 copy
		job.setCopies(1);
		// Put up the dialog box
		if (job.printDialog())
		{
			// Print the job if the user didn't cancel printing
			try
			{
				job.print();
			}
			catch (Exception ex)
			{
				/* handle exception */}
		}
	}
	
	/**
	 * TODO
	 * 
	 * @param e
	 */
	public void this_windowOpened(WindowEvent e) {
		
		jScrollPane.getVerticalScrollBar().setValue(0);
	}
}// end of main

/**
 * Frame_HelpView_this_windowAdapter Class.
 */
class Frame_HelpView_this_windowAdapter extends WindowAdapter {
	
	private final FrameViewHelp adaptee;
	
	Frame_HelpView_this_windowAdapter(FrameViewHelp adaptee) {
		
		this.adaptee = adaptee;
	}
	
	@Override
	public void windowOpened(WindowEvent e) {
		
		adaptee.this_windowOpened(e);
	}
}

// may need the class printing... from FrameVeiwOutput class

/**
 * Frame_HelpView_btnPrint_actionAdapter Class.
 */
class Frame_HelpView_btnPrint_actionAdapter implements ActionListener {
	
	private final FrameViewHelp adaptee;
	
	Frame_HelpView_btnPrint_actionAdapter(FrameViewHelp adaptee) {
		
		this.adaptee = adaptee;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		
		adaptee.btnPrint_actionPerformed(e);
	}
}

/**
 * Frame_HelpView_btnClose_actionAdapter Class.
 */
class Frame_HelpView_btnClose_actionAdapter implements ActionListener {
	
	private final FrameViewHelp adaptee;
	
	Frame_HelpView_btnClose_actionAdapter(FrameViewHelp adaptee) {
		
		this.adaptee = adaptee;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		
		adaptee.btnClose_actionPerformed(e);
	}
}
