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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JToggleButton;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.text.DefaultFormatter;

import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.image.PNGTranscoder;
import org.fhaes.preferences.App;
import org.fhaes.util.Builder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.miginfocom.swing.MigLayout;

/**
 * PNGExportOptions Class. JDialog for getting size information when saving to a PNG image
 * 
 * @author Peter Brewer
 */
public class PNGExportOptions extends JDialog implements ActionListener {
	
	private final static Logger log = LoggerFactory.getLogger(PNGExportOptions.class);
	
	private static final long serialVersionUID = 1L;
	private final JPanel contentPanel = new JPanel();
	private FireChartSVG currentChart;
	private File outputFile;
	private JSpinner spnHeight;
	private JSpinner spnWidth;
	private Double prop;
	private JToggleButton btnLock;
	private boolean widthSpinnerEnabled = true;
	private boolean heightSpinnerEnabled = true;
	
	/**
	 * Create the dialog.
	 * 
	 * @param outputFile
	 * @param currentChart
	 */
	public PNGExportOptions(FireChartSVG currentChart, File outputFile) {
		
		this.currentChart = currentChart;
		this.outputFile = outputFile;
		prop = ((double) currentChart.getTotalHeight() / (currentChart.getTotalWidth()));
		
		this.setModal(true);
		this.setTitle("Output size");
		this.setIconImage(Builder.getApplicationIcon());
		setBounds(100, 100, 291, 158);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(new MigLayout("", "[][grow,fill][][]", "[][]"));
		{
			JLabel lblHeight = new JLabel("Height:");
			contentPanel.add(lblHeight, "cell 0 0");
		}
		{
			spnHeight = new JSpinner();
			spnHeight.setModel(new SpinnerNumberModel(currentChart.getTotalHeight(), 100, 9999, 1));
			JComponent comp = spnHeight.getEditor();
			JFormattedTextField field = (JFormattedTextField) comp.getComponent(0);
			DefaultFormatter formatter = (DefaultFormatter) field.getFormatter();
			formatter.setCommitsOnValidEdit(true);
			spnHeight.addChangeListener(new ChangeListener() {
				
				@Override
				public void stateChanged(ChangeEvent e) {
					
					if (btnLock.isSelected() && heightSpinnerEnabled)
					{
						Integer height = (Integer) spnHeight.getValue();
						Integer new_width = (int) (height / prop);
						widthSpinnerEnabled = false;
						spnWidth.setValue(new_width);
						widthSpinnerEnabled = true;
					}
				}
			});
			contentPanel.add(spnHeight, "cell 1 0");
		}
		{
			JLabel lblPx = new JLabel("px");
			contentPanel.add(lblPx, "cell 2 0,aligny bottom");
		}
		{
			btnLock = new JToggleButton();
			btnLock.setSelectedIcon(Builder.getImageIcon("chain.png"));
			btnLock.setIcon(Builder.getImageIcon("unchain.png"));
			btnLock.setSelected(true);
			btnLock.setToolTipText("Lock/unlock aspect ratio");
			contentPanel.add(btnLock, "cell 3 0 1 2,growy");
		}
		{
			JLabel lblWidth = new JLabel("Width:");
			contentPanel.add(lblWidth, "cell 0 1");
		}
		{
			spnWidth = new JSpinner();
			spnWidth.setModel(new SpinnerNumberModel(currentChart.getTotalWidth(), 100, 9999, 1));
			JComponent comp = spnWidth.getEditor();
			JFormattedTextField field = (JFormattedTextField) comp.getComponent(0);
			DefaultFormatter formatter = (DefaultFormatter) field.getFormatter();
			formatter.setCommitsOnValidEdit(true);
			spnWidth.addChangeListener(new ChangeListener() {
				
				@Override
				public void stateChanged(ChangeEvent e) {
					
					if (btnLock.isSelected() && widthSpinnerEnabled)
					{
						Integer width = (Integer) spnWidth.getValue();
						Integer new_height = (int) (width * prop);
						heightSpinnerEnabled = false;
						spnHeight.setValue(new_height);
						heightSpinnerEnabled = true;
					}
				}
			});
			contentPanel.add(spnWidth, "cell 1 1");
		}
		{
			JLabel label = new JLabel("px");
			contentPanel.add(label, "cell 2 1");
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
	
	@Override
	public void actionPerformed(ActionEvent evt) {
		
		if (evt.getActionCommand().equals("OK"))
		{
			if (currentChart == null)
				return;
				
			log.debug("Exporting to PNG....");
			try
			{
				
				currentChart.setVisibilityOfNoExportElements(false);
				
				PNGTranscoder t = new PNGTranscoder();
				float width = Double.valueOf((int) this.spnWidth.getValue()).floatValue();
				float height = Double.valueOf((int) this.spnHeight.getValue()).floatValue();
				
				t.addTranscodingHint(PNGTranscoder.KEY_WIDTH, width);
				t.addTranscodingHint(PNGTranscoder.KEY_HEIGHT, height);
				
				TranscoderInput input = new TranscoderInput(currentChart.doc);
				
				String path = outputFile.getAbsolutePath();
				
				if (!path.toLowerCase().endsWith(".png"))
				{
					path = path + ".png";
				}
				
				OutputStream png_ostream = new FileOutputStream(path);
				TranscoderOutput output = new TranscoderOutput(png_ostream);
				t.transcode(input, output);
				png_ostream.flush();
				png_ostream.close();
				currentChart.setVisibilityOfNoExportElements(true);
				// svgCanvas.setDocument(currentChart.doc);
			}
			catch (Exception e)
			{
				log.error("Error charting chart");
				e.printStackTrace();
			}
			dispose();
		}
		if (evt.getActionCommand().equals("Cancel"))
		{
			dispose();
		}
	}
	
}
