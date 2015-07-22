package org.fhaes.gui;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;

import net.miginfocom.swing.MigLayout;

import org.fhaes.util.Builder;

/**
 * RunAnalysisPanel Class.
 */
public class RunAnalysisPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	/**
	 * Create the panel.
	 */
	public RunAnalysisPanel() {

		setLayout(new MigLayout("", "[222px,grow,fill]", "[grow,fill][center][grow]"));

		JPanel panel = new JPanel();
		add(panel, "cell 0 1,alignx right,aligny top");

		JButton btnRun = new JButton("Run analysis");
		btnRun.setFont(new Font("Dialog", Font.BOLD, 15));
		btnRun.setIcon(Builder.getImageIcon("run64.png"));

		btnRun.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {

				MainWindow.getInstance().rightSplitPanel.actionParamConfig.perform(this);
			}
		});

		panel.add(btnRun);
	}
}
