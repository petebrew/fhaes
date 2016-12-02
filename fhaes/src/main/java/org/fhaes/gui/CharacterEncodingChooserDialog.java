package org.fhaes.gui;

import java.awt.event.ActionEvent;
import java.nio.charset.Charset;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;

import net.miginfocom.swing.MigLayout;

import org.fhaes.preferences.FHAESPreferences.PrefKey;
import org.fhaes.preferences.wrappers.CharsetWrapper;
import org.fhaes.preferences.wrappers.CheckBoxWrapper;
import org.fhaes.util.Builder;
import org.fhaes.util.DescriptiveDialog;

public class CharacterEncodingChooserDialog extends DescriptiveDialog {
	
	private static final long serialVersionUID = 1L;
	private JCheckBox chkAuto;
	private JComboBox<Charset> cboCharEnc;
	
	/**
	 * Create the dialog.
	 */
	public CharacterEncodingChooserDialog() {
	
		super(
				null,
				"Character Encoding",
				"The FHX file format does not mandate a specific character encoding.  Files with characters beyond the original ASCII characters can be stored with a variety of character encodings"
						+ " depending on the software and operating system used.  By default FHAES uses the ICU library to attempt to detect the correct encoding but this is not always possible. "
						+ "If you know your files are in a particular encoding and ICU is "
						+ "not correctly detecting, you can override this here. Note that FHAES will always save to UTF-8 encoding unless support for FHX2 is specified in the edit file dialog, in"
						+ " which case it will use IBM437 which supports far fewer characters.", Builder
						.getImageIcon("accessories_character_map.png"));
		getMainPanel().setLayout(new MigLayout("", "[][grow]", "[23px][]"));
		
		chkAuto = new JCheckBox("Attempt to detect character encodings automatically");
		chkAuto.setActionCommand("autoEncoding");
		chkAuto.addActionListener(this);
		getMainPanel().add(chkAuto, "cell 0 0 2 1,growx,aligny top");
		this.setIconImage(Builder.getApplicationIcon());
		new CheckBoxWrapper(chkAuto, PrefKey.AUTO_DETECT_CHAR_ENC, true);
		
		JLabel lblCharacterEncoding = new JLabel("Force character encoding to:");
		lblCharacterEncoding.setEnabled(false);
		getMainPanel().add(lblCharacterEncoding, "cell 0 1,alignx trailing");
		
		cboCharEnc = new JComboBox<Charset>();
		
		cboCharEnc.setEnabled(false);
		getMainPanel().add(cboCharEnc, "cell 1 1,growx");
		
		Charset defaultCharset = Charset.defaultCharset();
		new CharsetWrapper(cboCharEnc, PrefKey.FORCE_CHAR_ENC_TO, defaultCharset);
		pingGUI();
		this.btnCancel.setVisible(false);
		this.btnOK.setText("Close");
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
	
		if (e.getActionCommand().equals("autoEncoding"))
		{
			pingGUI();
		}
		else if (e.getActionCommand().equals("OK"))
		{
			this.dispose();
		}
	}
	
	private void pingGUI() {
	
		cboCharEnc.setEnabled(!chkAuto.isSelected());
	}
	
}
