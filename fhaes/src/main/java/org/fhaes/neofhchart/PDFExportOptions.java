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
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.print.PageFormat;
import java.awt.print.Paper;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.border.EmptyBorder;

import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.print.PrintTranscoder;
import org.fhaes.preferences.App;
import org.fhaes.util.Builder;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.ImgTemplate;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfTemplate;
import com.itextpdf.text.pdf.PdfWriter;

import net.miginfocom.swing.MigLayout;

/**
 * PDFExportOptions Class. JDialog for getting export information when saving to PDF files.
 * 
 * @author Peter Brewer
 */
public class PDFExportOptions extends JDialog implements ActionListener {
	
	private static final long serialVersionUID = 1L;
	private final JPanel contentPanel = new JPanel();
	private JComboBox<Object> cboPaperSize;
	private JRadioButton radPortrait;
	private JRadioButton radLandscape;
	private FireChartSVG currentChart;
	private File outputFile;
	private JLabel lblSize;
	
	public final static Object[] PAGESIZES = { "Default", PageSize.LETTER, PageSize.LEGAL, PageSize.EXECUTIVE, PageSize.A5, PageSize.A4,
			PageSize.A3, PageSize.A2, PageSize.A1, PageSize.A0 };
			
	/**
	 * Create the dialog.
	 */
	public PDFExportOptions(FireChartSVG currentChart, File outputFile) {
		
		this.setModal(true);
		this.setTitle("Export to PDF");
		this.setIconImage(Builder.getApplicationIcon());
		this.currentChart = currentChart;
		this.outputFile = outputFile;
		
		setBounds(100, 100, 450, 176);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(new MigLayout("", "[][grow][]", "[][15px:n][][]"));
		{
			JLabel lblPaperSize = new JLabel("Paper size:");
			contentPanel.add(lblPaperSize, "cell 0 0,alignx trailing");
		}
		{
			cboPaperSize = new JComboBox<Object>(PAGESIZES);
			cboPaperSize.setRenderer(new PageSizeRenderer());
			cboPaperSize.setActionCommand("PaperSize");
			cboPaperSize.addActionListener(this);
			contentPanel.add(cboPaperSize, "cell 1 0 2 1,growx");
		}
		{
			lblSize = new JLabel("");
			lblSize.setFont(new Font("Dialog", Font.PLAIN, 10));
			contentPanel.add(lblSize, "cell 1 1,alignx right,aligny top");
		}
		{
			JLabel lblNewLabel = new JLabel("");
			lblNewLabel.setIcon(Builder.getImageIcon("pdf48.png"));
			contentPanel.add(lblNewLabel, "cell 2 1 1 3");
		}
		{
			radPortrait = new JRadioButton("Portrait");
			radPortrait.setEnabled(false);
			contentPanel.add(radPortrait, "cell 1 2");
		}
		{
			radLandscape = new JRadioButton("Landscape");
			radLandscape.setEnabled(false);
			radLandscape.setSelected(true);
			
			ButtonGroup bg = new ButtonGroup();
			bg.add(radLandscape);
			bg.add(radPortrait);
			contentPanel.add(radLandscape, "cell 1 3");
		}
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton okButton = new JButton("OK");
				okButton.setActionCommand("OK");
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
		this.setLocationRelativeTo(App.mainFrame);
	}
	
	/**
	 * TODO
	 */
	@Override
	public void actionPerformed(ActionEvent evt) {
		
		// Do the export
		if (evt.getActionCommand().equals("OK"))
		{
			Document document = null;
			
			if (cboPaperSize.getSelectedItem() instanceof Rectangle)
			{
				Rectangle rect = (Rectangle) cboPaperSize.getSelectedItem();
				if (radLandscape.isSelected())
					rect = rect.rotate();
				document = new Document(rect, 10, 10, 10, 10);
			}
			else
			{
				Rectangle rect = new Rectangle(currentChart.getTotalWidth(), currentChart.getTotalHeight());
				document = new Document(rect, 10, 10, 10, 10);
			}
			
			try
			{
				currentChart.setVisibilityOfNoExportElements(false);
				
				PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(outputFile.getAbsolutePath()));
				document.open();
				
				int width = (int) document.getPageSize().getWidth();
				int height = (int) document.getPageSize().getHeight();
				
				PdfContentByte cb = writer.getDirectContent();
				PdfTemplate template = cb.createTemplate(width, height);
				
				@SuppressWarnings("deprecation")
				Graphics2D g2 = template.createGraphics(width, height);
				
				PrintTranscoder prm = new PrintTranscoder();
				TranscoderInput ti = new TranscoderInput(currentChart.doc);
				prm.transcode(ti, null);
				
				PageFormat pg = new PageFormat();
				Paper pp = new Paper();
				pp.setSize(width, height);
				pp.setImageableArea(0, 0, width, height);
				pg.setPaper(pp);
				prm.print(g2, pg, 0);
				g2.dispose();
				
				ImgTemplate img = new ImgTemplate(template);
				document.add(img);
			}
			catch (DocumentException e)
			{
				System.err.println(e);
			}
			catch (IOException e)
			{
				System.err.println(e);
			}
			finally
			{
				currentChart.setVisibilityOfNoExportElements(true);
			}
			document.close();
			
			dispose();
		}
		
		if (evt.getActionCommand().equals("Cancel"))
		{
			dispose();
		}
		
		// Update the paper size label
		if (evt.getActionCommand().equals("PaperSize"))
		{
			if (this.cboPaperSize.getSelectedItem() instanceof Rectangle)
			{
				Object value = this.cboPaperSize.getSelectedItem();
				if (value.equals(PageSize.A5))
				{
					this.lblSize.setText("148 x 210mm  /  5.83 x 8.27\"");
				}
				else if (value.equals(PageSize.A4))
				{
					this.lblSize.setText("210 x 297mm  /  8.27 x 11.7\"");
				}
				else if (value.equals(PageSize.A3))
				{
					this.lblSize.setText("297 x 420mm  /  11.7 x 16.5\"");
				}
				else if (value.equals(PageSize.A2))
				{
					this.lblSize.setText("420 x 594mm  /  16.5 x 23.4\"");
				}
				else if (value.equals(PageSize.A1))
				{
					this.lblSize.setText("594 x 841mm  /  23.4 x 33.1\"");
				}
				else if (value.equals(PageSize.A0))
				{
					this.lblSize.setText("841 x 1189mm  /  33.1 x 46.8\"");
				}
				else if (value.equals(PageSize.LETTER))
				{
					this.lblSize.setText("215.9 x 279.4mm  /  8.5 x 11\"");
				}
				else if (value.equals(PageSize.LEGAL))
				{
					this.lblSize.setText("215.9 x 355.6mm  /  8.5 x 14\"");
				}
				else if (value.equals(PageSize.EXECUTIVE))
				{
					this.lblSize.setText("184 x 267mm  /  7.25 x 10.5\"");
				}
				else
				{
					this.lblSize.setText("");
				}
				
				radLandscape.setEnabled(true);
				radPortrait.setEnabled(true);
			}
			else
			{
				this.lblSize.setText("");
				radLandscape.setEnabled(false);
				radPortrait.setEnabled(false);
			}
		}
	}
}
