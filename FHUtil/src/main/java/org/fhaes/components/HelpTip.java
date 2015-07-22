package org.fhaes.components;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.LineBorder;
import javax.swing.text.View;

import net.miginfocom.swing.MigLayout;

import org.fhaes.util.Builder;

/**
 * HelpTip Class. A simple post-it like modal dialog for showing tips to the user. Unlike a ToolTip the HelpTip is shown on request by the
 * user and must then be dismissed.
 */
public class HelpTip extends JDialog {

	private static final long serialVersionUID = 1L;
	private static final JLabel txtHelp = new JLabel();

	/**
	 * Constructor for the HelpTip.
	 * 
	 * @param text - basic HTML text to display
	 * @param parent - component that launched this dialog
	 */
	public HelpTip(String text, Component parent) {

		setUndecorated(true);
		setModal(true);
		// this.setBounds(0, 0, 300, 150);
		setResizable(false);
		getContentPane().setLayout(new BorderLayout(0, 0));

		JPanel panel = new JPanel();

		panel.setBorder(new LineBorder(Color.GRAY));
		panel.setBackground(new Color(255, 255, 224));
		getContentPane().add(panel, BorderLayout.CENTER);
		panel.setLayout(new MigLayout("", "[grow][]", "[top][grow]"));

		txtHelp.setFont(new Font("Dialog", Font.PLAIN, 11));
		txtHelp.setText("<html><p>" + text + "</p></html>");
		txtHelp.setOpaque(false);
		txtHelp.setFocusable(false);

		txtHelp.setPreferredSize(getLabelSize());

		panel.add(txtHelp, "cell 0 0 1 2,growx,aligny top");

		JLabel btnX = new JLabel("");
		btnX.setIcon(Builder.getImageIcon("closehelptip.png"));
		panel.addMouseListener(new MouseListener() {

			public void mouseClicked(MouseEvent arg0) {

				closeTip();
			}

			public void mouseEntered(MouseEvent arg0) {

			}

			public void mouseExited(MouseEvent arg0) {

			}

			public void mousePressed(MouseEvent arg0) {

			}

			public void mouseReleased(MouseEvent arg0) {

			}

		});
		btnX.setFocusable(false);
		btnX.setFont(new Font("Dialog", Font.PLAIN, 10));
		panel.add(btnX, "cell 1 0,alignx right");
		this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		Double xpos = parent.getLocationOnScreen().getX();
		Double ypos = parent.getLocationOnScreen().getY();
		Double width = this.getLabelSize().getWidth();
		Double newxpos = xpos - width;

		this.setLocation(newxpos.intValue() - 16, ypos.intValue());
		pack();
		setVisible(true);

	}

	/**
	 * Closes this tip
	 */
	private void closeTip() {

		this.setVisible(false);
		this.dispose();
	}

	/**
	 * Returns the preferred size to set a component at in order to render an html string. You can specify the size of one dimension.
	 * 
	 * @return dimension
	 */
	private java.awt.Dimension getLabelSize() {

		int prefSize = 250;
		boolean width = true;
		View view = (View) txtHelp.getClientProperty(javax.swing.plaf.basic.BasicHTML.propertyKey);

		view.setSize(width ? prefSize : 0, width ? 0 : prefSize);

		float w = view.getPreferredSpan(View.X_AXIS);
		float h = view.getPreferredSpan(View.Y_AXIS);

		return new java.awt.Dimension((int) Math.ceil(w), (int) Math.ceil(h));
	}

}
