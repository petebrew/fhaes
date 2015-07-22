package org.fhaes.neofhchart;

/*******************************************************************************
 * Copyright (C) 2015 Peter Brewer
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
 ******************************************************************************/

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;
import javax.swing.border.EmptyBorder;

import org.apache.batik.swing.JSVGCanvas;
import org.fhaes.util.Builder;

/**
 * SeriesListDialog Class. Dialog to enable the user to select which series within the file to plot
 * 
 * @author pbrewer
 */
public class SeriesListDialog extends JDialog implements ActionListener {

	private static final long serialVersionUID = 1L;
	private final JPanel contentPanel = new JPanel();

	/**
	 * TODO
	 * 
	 * @param chart
	 * @param svgCanvas
	 */
	public static void showDialog(final FireChartSVG chart, final JSVGCanvas svgCanvas) {

		SeriesListDialog dialog = new SeriesListDialog(chart, svgCanvas);
		dialog.setLocationRelativeTo(svgCanvas);

		dialog.setVisible(true);
	}

	/**
	 * Create the dialog.
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public SeriesListDialog(final FireChartSVG chart, final JSVGCanvas svgCanvas) {

		setBounds(100, 100, 450, 300);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
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
		}

		ArrayList<CheckListItem> listOfChecks = new ArrayList<CheckListItem>();
		ArrayList<SeriesSVG> mySVGSeries = chart.getCurrentSeriesList();
		for (int i = 0; i < mySVGSeries.size(); i++)
		{
			CheckListItem temp = new CheckListItem(mySVGSeries.get(i).getTitle());
			temp.setSelected(mySVGSeries.get(i).isVisible);
			listOfChecks.add(temp);
		}

		JList<CheckListItem> seriesList = new JList(listOfChecks.toArray());
		seriesList.setCellRenderer(new CheckListRenderer());
		seriesList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		seriesList.addMouseListener(new MouseAdapter()// MARKED FOR DELETION--------------------------------------------------
				{

					public void mouseClicked(MouseEvent event) {

						JList list = (JList) event.getSource();

						// Get index of item clicked

						int index = list.locationToIndex(event.getPoint());
						CheckListItem item = (CheckListItem) list.getModel().getElementAt(index);

						// Toggle selected state

						item.setSelected(!item.isSelected());

						// Toggle visibility of the series
						chart.toggleVisibilityOfSeries(index);
						svgCanvas.setDocument(chart.doc);

						// Repaint cell

						list.repaint(list.getCellBounds(index, index));
					}
				});

		JScrollPane listScroller = new JScrollPane(seriesList);
		JPanel thisPanel = new JPanel();
		thisPanel.setLayout(new BorderLayout(0, 0));
		thisPanel.add(listScroller);

		contentPanel.add(thisPanel, BorderLayout.CENTER);
		setModal(true);
		this.setTitle("Choose series to plot");
		this.setIconImage(Builder.getApplicationIcon());
	}

	/**
	 * Specialised class for storing the series and their selection status
	 * 
	 */
	class CheckListItem {

		private String label;
		private boolean isSelected = false;

		public CheckListItem(String label) {

			this.label = label;
		}

		public boolean isSelected() {

			return isSelected;
		}

		public void setSelected(boolean isSelected) {

			this.isSelected = isSelected;
		}

		public String toString() {

			return label;
		}
	}

	/**
	 * Renderer for drawing the series checkbox items
	 * 
	 * @author pbrewer
	 * 
	 */
	@SuppressWarnings({ "serial", "rawtypes" })
	class CheckListRenderer extends JCheckBox implements ListCellRenderer {

		public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean hasFocus) {

			setEnabled(list.isEnabled());
			setSelected(((CheckListItem) value).isSelected());
			setFont(list.getFont());
			setBackground(list.getBackground());
			setForeground(list.getForeground());
			setText(value.toString());
			return this;
		}
	}

	@Override
	public void actionPerformed(ActionEvent evt) {

		if (evt.getActionCommand().equals("OK"))
		{
			this.dispose();
		}
	}

}
