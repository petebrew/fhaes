/**************************************************************************************************
 * Fire History Analysis and Exploration System (FHAES), Copyright (C) 2015
 * 
 * Contributors: Cody Calhoun, Anthony Messerschmidt, Seth Westphal, Scott Goble, and Peter Brewer
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
package org.fhaes.fhxrecorder.view;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.io.IOException;
import java.util.List;

import javax.activation.ActivationDataFlavor;
import javax.activation.DataHandler;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.DefaultListModel;
import javax.swing.DropMode;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.TransferHandler;

import org.fhaes.fhxrecorder.controller.FileController;
import org.fhaes.fhxrecorder.util.ColorBar;
import org.fhaes.fhxrecorder.util.CustomOptions;
import org.fhaes.fhxrecorder.util.YearSummary;
import org.fhaes.fhxrecorder.util.CustomOptions.DataItem;
import org.fhaes.util.Builder;

import net.miginfocom.swing.MigLayout;

/**
 * CustomizeDialog Class. This dialog box allows for the customization of the color bars in the summary and graphics tabs.
 * 
 * @author Seth Westphal
 */
public class CustomizeDialog extends javax.swing.JDialog {
	
	private static final long serialVersionUID = 8581694583186383770L;
	private boolean result;
	private final JTextField txtNameGroup1, txtNameGroup2, txtNameGroup3, txtNameGroup4, txtNameGroup5, txtNameGroup6;
	private final JButton btnColorGroup1, btnColorGroup2, btnColorGroup3, btnColorGroup4, btnColorGroup5, btnColorGroup6;
	private final JScrollPane listGroup0, listGroup1, listGroup2, listGroup3, listGroup4, listGroup5, listGroup6;
	private final ColorBar colorBar;
	private CustomOptions options = FileController.getCustomOptions();
	private YearSummary previewSummary = new YearSummary(FileController.CURRENT_YEAR, 1, 2, 1, 1, 1, 1, 1, 1);
	private JPanel panel;
	
	/**
	 * Constructor, creates a new dialog. Must call showDialog() after.
	 * 
	 * @param parent the creating JPanel.
	 * @param modal true if focus is only allowed on this dialog.
	 */
	public CustomizeDialog(javax.swing.JPanel parent, boolean modal) {
		
		super();
		this.setModal(modal);
		setTitle("Color Bar Customization");
		setLocation(parent.getLocationOnScreen());
		setIconImage(Builder.getApplicationIcon());
		
		getContentPane().setLayout(new MigLayout("", "[115px,grow][115px,grow][115px,grow][115px,grow][115px,grow][115px,grow][115px,grow]",
				"[][][][][grow][]"));
				
		final TransferHandler transferHandler = new ListItemTransferHandler();
		listGroup0 = makeList(transferHandler, options.getDataItems(0));
		listGroup1 = makeList(transferHandler, options.getDataItems(1));
		listGroup2 = makeList(transferHandler, options.getDataItems(2));
		listGroup3 = makeList(transferHandler, options.getDataItems(3));
		listGroup4 = makeList(transferHandler, options.getDataItems(4));
		listGroup5 = makeList(transferHandler, options.getDataItems(5));
		listGroup6 = makeList(transferHandler, options.getDataItems(6));
		getContentPane().add(listGroup0, "cell 0 1,grow");
		getContentPane().add(listGroup1, "cell 1 1,grow");
		getContentPane().add(listGroup2, "cell 2 1,grow");
		getContentPane().add(listGroup3, "cell 3 1,grow");
		getContentPane().add(listGroup4, "cell 4 1,grow");
		getContentPane().add(listGroup5, "cell 5 1,grow");
		getContentPane().add(listGroup6, "cell 6 1,grow");
		
		getContentPane().add(new JLabel("Unused:"), "cell 0 0");
		getContentPane().add(new JLabel("Group 1:"), "cell 1 0");
		getContentPane().add(new JLabel("Group 2:"), "cell 2 0");
		getContentPane().add(new JLabel("Group 3:"), "cell 3 0");
		getContentPane().add(new JLabel("Group 4:"), "cell 4 0");
		getContentPane().add(new JLabel("Group 5:"), "cell 5 0");
		getContentPane().add(new JLabel("Group 6:"), "cell 6 0");
		
		JLabel label = new JLabel("Name:");
		getContentPane().add(label, "cell 0 2,alignx right");
		
		txtNameGroup1 = new JTextField();
		txtNameGroup1.addFocusListener(new FocusAdapter() {
			
			@Override
			public void focusLost(FocusEvent arg0) {
				
				updatePreviewColorBar();
			}
		});
		getContentPane().add(txtNameGroup1, "cell 1 2,growx,aligny center");
		txtNameGroup1.setColumns(20);
		
		txtNameGroup2 = new JTextField();
		txtNameGroup2.addFocusListener(new FocusAdapter() {
			
			@Override
			public void focusLost(FocusEvent arg0) {
				
				updatePreviewColorBar();
			}
		});
		txtNameGroup2.setText("Dormant Season");
		txtNameGroup2.setColumns(20);
		getContentPane().add(txtNameGroup2, "cell 2 2,growx");
		
		txtNameGroup3 = new JTextField();
		txtNameGroup3.addFocusListener(new FocusAdapter() {
			
			@Override
			public void focusLost(FocusEvent arg0) {
				
				updatePreviewColorBar();
			}
		});
		txtNameGroup3.setColumns(20);
		getContentPane().add(txtNameGroup3, "cell 3 2,growx");
		
		txtNameGroup4 = new JTextField();
		txtNameGroup4.addFocusListener(new FocusAdapter() {
			
			@Override
			public void focusLost(FocusEvent arg0) {
				
				updatePreviewColorBar();
			}
		});
		txtNameGroup4.setColumns(20);
		getContentPane().add(txtNameGroup4, "cell 4 2,growx");
		
		txtNameGroup5 = new JTextField();
		txtNameGroup5.addFocusListener(new FocusAdapter() {
			
			@Override
			public void focusLost(FocusEvent arg0) {
				
				updatePreviewColorBar();
			}
		});
		txtNameGroup5.setColumns(20);
		getContentPane().add(txtNameGroup5, "cell 5 2,growx");
		
		txtNameGroup6 = new JTextField();
		txtNameGroup6.addFocusListener(new FocusAdapter() {
			
			@Override
			public void focusLost(FocusEvent arg0) {
				
				updatePreviewColorBar();
			}
		});
		txtNameGroup6.setColumns(20);
		getContentPane().add(txtNameGroup6, "cell 6 2,growx");
		getContentPane().add(new JLabel("Color:"), "cell 0 3,alignx right");
		
		btnColorGroup2 = new JButton(" ");
		btnColorGroup2.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				
				Color color = getColor(btnColorGroup2.getBackground());
				if (color != null)
				{
					btnColorGroup2.setBackground(color);
					updatePreviewColorBar();
				}
			}
		});
		getContentPane().add(btnColorGroup2, "cell 2 3,growx");
		
		btnColorGroup3 = new JButton(" ");
		btnColorGroup3.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				
				Color color = getColor(btnColorGroup3.getBackground());
				if (color != null)
				{
					btnColorGroup3.setBackground(color);
					updatePreviewColorBar();
				}
			}
		});
		getContentPane().add(btnColorGroup3, "cell 3 3,growx");
		
		btnColorGroup4 = new JButton(" ");
		btnColorGroup4.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				
				Color color = getColor(btnColorGroup4.getBackground());
				if (color != null)
				{
					btnColorGroup4.setBackground(color);
					updatePreviewColorBar();
				}
			}
		});
		getContentPane().add(btnColorGroup4, "cell 4 3,growx");
		
		btnColorGroup5 = new JButton(" ");
		btnColorGroup5.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				
				Color color = getColor(btnColorGroup5.getBackground());
				if (color != null)
				{
					btnColorGroup5.setBackground(color);
					updatePreviewColorBar();
				}
			}
		});
		getContentPane().add(btnColorGroup5, "cell 5 3,growx");
		
		btnColorGroup6 = new JButton(" ");
		btnColorGroup6.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				
				Color color = getColor(btnColorGroup6.getBackground());
				if (color != null)
				{
					btnColorGroup6.setBackground(color);
					updatePreviewColorBar();
				}
			}
		});
		getContentPane().add(btnColorGroup6, "cell 6 3,growx");
		
		JLabel lblPreview = new JLabel("Preview:");
		getContentPane().add(lblPreview, "cell 0 5,alignx right,aligny center");
		colorBar = new ColorBar(previewSummary);
		getContentPane().add(colorBar, "cell 1 5,alignx left,aligny center");
		
		btnColorGroup1 = new JButton(" ");
		getContentPane().add(btnColorGroup1, "cell 1 3,growx,aligny center");
		
		panel = new JPanel();
		getContentPane().add(panel, "cell 2 5 5 1,grow");
		panel.setLayout(new FlowLayout(FlowLayout.RIGHT, 5, 5));
		
		JButton btnDefaults = new JButton("Defaults");
		panel.add(btnDefaults);
		btnDefaults.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				
				options = new CustomOptions();
				options.setDefaultOptions();
				populateFields();
				updatePreviewColorBar();
			}
		});
		
		JButton btnCancel = new JButton("Cancel");
		btnCancel.setVisible(false);
		panel.add(btnCancel);
		btnCancel.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				
				result = false;
				setVisible(false);
				dispose();
			}
		});
		JButton btnOK = new JButton("OK");
		panel.add(btnOK);
		btnOK.setAlignmentX(Component.LEFT_ALIGNMENT);
		
		btnOK.addActionListener(new ActionListener() {
			
			@SuppressWarnings("rawtypes")
			@Override
			public void actionPerformed(ActionEvent arg0) {
				
				result = true;
				
				options.setGroupName(1, txtNameGroup1.getText());
				options.setGroupName(2, txtNameGroup2.getText());
				options.setGroupName(3, txtNameGroup3.getText());
				options.setGroupName(4, txtNameGroup4.getText());
				options.setGroupName(5, txtNameGroup5.getText());
				options.setGroupName(6, txtNameGroup6.getText());
				
				options.setGroupDataItems(0, ((DefaultListModel) ((JList) listGroup0.getViewport().getView()).getModel()).toArray());
				options.setGroupDataItems(1, ((DefaultListModel) ((JList) listGroup1.getViewport().getView()).getModel()).toArray());
				options.setGroupDataItems(2, ((DefaultListModel) ((JList) listGroup2.getViewport().getView()).getModel()).toArray());
				options.setGroupDataItems(3, ((DefaultListModel) ((JList) listGroup3.getViewport().getView()).getModel()).toArray());
				options.setGroupDataItems(4, ((DefaultListModel) ((JList) listGroup4.getViewport().getView()).getModel()).toArray());
				options.setGroupDataItems(5, ((DefaultListModel) ((JList) listGroup5.getViewport().getView()).getModel()).toArray());
				options.setGroupDataItems(6, ((DefaultListModel) ((JList) listGroup6.getViewport().getView()).getModel()).toArray());
				
				options.setGroupColor(1, btnColorGroup1.getBackground());
				options.setGroupColor(2, btnColorGroup2.getBackground());
				options.setGroupColor(3, btnColorGroup3.getBackground());
				options.setGroupColor(4, btnColorGroup4.getBackground());
				options.setGroupColor(5, btnColorGroup5.getBackground());
				options.setGroupColor(6, btnColorGroup6.getBackground());
				
				FileController.setCustomOptions(options);
				
				setVisible(false);
				dispose();
			}
		});
		btnColorGroup1.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				
				Color color = getColor(btnColorGroup1.getBackground());
				if (color != null)
				{
					btnColorGroup1.setBackground(color);
					updatePreviewColorBar();
				}
			}
		});
		
		// Initialize to current values
		populateFields();
		
		colorBar.updateChartAppearance(options);
		this.setMinimumSize(new Dimension(930, 300));
	}
	
	/**
	 * Populates all of the data fields initially with the current values.
	 */
	private void populateFields() {
		
		btnColorGroup1.setBackground(options.getGroupColor(1));
		txtNameGroup1.setText(options.getGroupName(1));
		btnColorGroup2.setBackground(options.getGroupColor(2));
		txtNameGroup2.setText(options.getGroupName(2));
		btnColorGroup3.setBackground(options.getGroupColor(3));
		txtNameGroup3.setText(options.getGroupName(3));
		btnColorGroup4.setBackground(options.getGroupColor(4));
		txtNameGroup4.setText(options.getGroupName(4));
		btnColorGroup5.setBackground(options.getGroupColor(5));
		txtNameGroup5.setText(options.getGroupName(5));
		btnColorGroup6.setBackground(options.getGroupColor(6));
		txtNameGroup6.setText(options.getGroupName(6));
		
		refreshData(listGroup0, options.getDataItems(0));
		refreshData(listGroup1, options.getDataItems(1));
		refreshData(listGroup2, options.getDataItems(2));
		refreshData(listGroup3, options.getDataItems(3));
		refreshData(listGroup4, options.getDataItems(4));
		refreshData(listGroup5, options.getDataItems(5));
		refreshData(listGroup6, options.getDataItems(6));
	}
	
	/**
	 * Refreshes the data in the lists.
	 * 
	 * @param pane the pane to refresh the data of.
	 * @param items the new data items.
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void refreshData(JScrollPane pane, List<DataItem> items) {
		
		DefaultListModel listModel = new DefaultListModel();
		for (DataItem item : items)
			listModel.addElement(item);
		((JList) pane.getViewport().getView()).setModel(listModel);
	}
	
	/**
	 * Opens a choose color dialog.
	 * 
	 * @param initialColor the initial color.
	 * @return the user selected color. InitialColor if cancelled.
	 */
	private Color getColor(Color initialColor) {
		
		return JColorChooser.showDialog(this, "Choose a New Color", initialColor);
	}
	
	/**
	 * Creates the initial lists of DataItems.
	 * 
	 * @param handler handles drag-and-drop operations.
	 * @param items the list of DataItems.
	 * @return the created JScrollPane.
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private static JScrollPane makeList(TransferHandler handler, List<DataItem> items) {
		
		DefaultListModel listModel = new DefaultListModel();
		
		for (DataItem item : items)
			listModel.addElement(item);
			
		JList list = new JList(listModel);
		list.getSelectionModel().setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		list.setDropMode(DropMode.INSERT);
		list.setDragEnabled(true);
		list.setTransferHandler(handler);
		
		// Disable row Cut, Copy, Paste
		AbstractAction dummyAction = new AbstractAction() {
			
			private static final long serialVersionUID = 1L;
			
			@Override
			public void actionPerformed(ActionEvent e) { /* Dummy action */
															
			}
		};
		list.getActionMap().put(TransferHandler.getCutAction().getValue(Action.NAME), dummyAction);
		list.getActionMap().put(TransferHandler.getCopyAction().getValue(Action.NAME), dummyAction);
		list.getActionMap().put(TransferHandler.getPasteAction().getValue(Action.NAME), dummyAction);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setViewportView(list);
		return scrollPane;
	}
	
	/**
	 * Shows this dialog.
	 * 
	 * @return true if OK, else false.
	 */
	public boolean showDialog() {
		
		pack();
		setResizable(false);
		setVisible(true);
		// calculate result
		return result;
	}
	
	/**
	 * Updates the preview color bar's data and appearance.
	 */
	@SuppressWarnings("rawtypes")
	private void updatePreviewColorBar() {
		
		CustomOptions previewOptions = new CustomOptions();
		
		/*
		 * previewOptions.setGroupName(1, txtNameGroup1.getText()); previewOptions.setGroupName(2, txtNameGroup2.getText());
		 * previewOptions.setGroupName(3, txtNameGroup3.getText()); previewOptions.setGroupName(4, txtNameGroup4.getText());
		 * previewOptions.setGroupName(5, txtNameGroup5.getText()); previewOptions.setGroupName(6, txtNameGroup6.getText());
		 */
		
		previewOptions.setGroupDataItems(0, ((DefaultListModel) ((JList) listGroup0.getViewport().getView()).getModel()).toArray());
		previewOptions.setGroupDataItems(1, ((DefaultListModel) ((JList) listGroup1.getViewport().getView()).getModel()).toArray());
		previewOptions.setGroupDataItems(2, ((DefaultListModel) ((JList) listGroup2.getViewport().getView()).getModel()).toArray());
		previewOptions.setGroupDataItems(3, ((DefaultListModel) ((JList) listGroup3.getViewport().getView()).getModel()).toArray());
		previewOptions.setGroupDataItems(4, ((DefaultListModel) ((JList) listGroup4.getViewport().getView()).getModel()).toArray());
		previewOptions.setGroupDataItems(5, ((DefaultListModel) ((JList) listGroup5.getViewport().getView()).getModel()).toArray());
		previewOptions.setGroupDataItems(6, ((DefaultListModel) ((JList) listGroup6.getViewport().getView()).getModel()).toArray());
		
		previewOptions.setGroupColor(1, btnColorGroup1.getBackground());
		previewOptions.setGroupColor(2, btnColorGroup2.getBackground());
		previewOptions.setGroupColor(3, btnColorGroup3.getBackground());
		previewOptions.setGroupColor(4, btnColorGroup4.getBackground());
		previewOptions.setGroupColor(5, btnColorGroup5.getBackground());
		previewOptions.setGroupColor(6, btnColorGroup6.getBackground());
		
		colorBar.updateChart(previewSummary, previewOptions);
	}
	
	/**
	 * A private helper class to aid with drag-and-drop operations. Based on
	 * http://docs.oracle.com/javase/tutorial/uiswing/dnd/dropmodedemo.html.
	 * 
	 * @author Seth Westphal
	 */
	class ListItemTransferHandler extends TransferHandler {
		
		private static final long serialVersionUID = 1L;
		private final DataFlavor localObjectFlavor;
		@SuppressWarnings("rawtypes")
		private JList source = null;
		private int[] indices = null;
		private int addIndex = -1; // Location where items were added
		private int addCount = 0; // Number of items added.
		
		/**
		 * Constructs a new handler.
		 */
		public ListItemTransferHandler() {
			
			super();
			localObjectFlavor = new ActivationDataFlavor(Object[].class, DataFlavor.javaJVMLocalObjectMimeType, "Array of items");
		}
		
		/**
		 * Creates the transferable object.
		 */
		@Override
		@SuppressWarnings({ "rawtypes", "deprecation" })
		protected Transferable createTransferable(JComponent c) {
			
			source = (JList) c;
			indices = source.getSelectedIndices();
			return new DataHandler(source.getSelectedValues(), localObjectFlavor.getMimeType());
		}
		
		/**
		 * Returns true if importing is allowed.
		 */
		@Override
		public boolean canImport(TransferSupport info) {
			
			return info.isDrop() && info.isDataFlavorSupported(localObjectFlavor);
		}
		
		/**
		 * Returns the source actions.
		 */
		@Override
		public int getSourceActions(JComponent c) {
			
			return MOVE;
		}
		
		/**
		 * Handles data importing from a drag-and-drop operation. Returns true on success, or false on fail.
		 */
		@Override
		@SuppressWarnings({ "rawtypes", "unchecked" })
		public boolean importData(TransferSupport info) {
			
			if (!canImport(info))
				return false;
				
			JList target = (JList) info.getComponent();
			JList.DropLocation dl = (JList.DropLocation) info.getDropLocation();
			DefaultListModel listModel = (DefaultListModel) target.getModel();
			int index = dl.getIndex();
			int max = listModel.getSize();
			
			if (index < 0 || index > max)
				index = max;
				
			addIndex = index;
			
			try
			{
				Object[] values = (Object[]) info.getTransferable().getTransferData(localObjectFlavor);
				for (int i = 0; i < values.length; i++)
				{
					int idx = index++;
					listModel.add(idx, values[i]);
					// target.addSelectionInterval(idx, idx);
				}
				addCount = source.equals(target) ? values.length : 0;
				
				return true;
			}
			catch (UnsupportedFlavorException ufe)
			{
				ufe.printStackTrace();
			}
			catch (IOException ioe)
			{
				ioe.printStackTrace();
			}
			return false;
		}
		
		/**
		 * Called when a drag and drop operation finishes exporting from its source.
		 */
		@SuppressWarnings("rawtypes")
		@Override
		protected void exportDone(JComponent c, Transferable data, int action) {
			
			if (action == MOVE && indices != null)
			{
				// If we are moving items around in the same list, we
				// need to adjust the indices accordingly, since those
				// after the insertion point have moved.
				if (addCount > 0)
				{
					for (int i = 0; i < indices.length; i++)
						if (indices[i] >= addIndex)
							indices[i] += addCount;
				}
				JList source = (JList) c;
				DefaultListModel model = (DefaultListModel) source.getModel();
				for (int i = indices.length - 1; i >= 0; i--)
					model.remove(indices[i]);
			}
			indices = null;
			addCount = 0;
			addIndex = -1;
			
			// Update colorBar preview
			updatePreviewColorBar();
		}
	}
}
