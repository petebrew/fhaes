package org.fhaes.gui;

import javax.swing.JLabel;
import javax.swing.JPanel;

import net.miginfocom.swing.MigLayout;

import org.fhaes.util.Builder;

/**
 * PickResultPanel Class.
 */
public class PickResultPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	/**
	 * Create the panel.
	 */
	public PickResultPanel() {

		setLayout(new MigLayout("", "[222px,grow,fill]", "[grow,fill][center][grow]"));

		JPanel panel = new JPanel();
		add(panel, "cell 0 1,alignx right,aligny top");

		JLabel lblSelectAnAnalysis = new JLabel("<html>Select an analysis result<br/>from the list on the right");
		panel.add(lblSelectAnAnalysis);

		JLabel lblNewLabel = new JLabel("");
		panel.add(lblNewLabel);
		lblNewLabel.setIcon(Builder.getImageIcon("rightarrow.png"));

	}
}
