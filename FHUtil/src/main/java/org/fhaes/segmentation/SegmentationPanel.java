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
package org.fhaes.segmentation;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.TitledBorder;

import net.miginfocom.swing.MigLayout;

import org.fhaes.util.Builder;

/**
 * SegmentationPanel Class. A panel containing components that allows the user to define one or more segments for fire history analysis.
 * 
 * @author Peter Brewer
 */
public class SegmentationPanel extends JPanel implements ActionListener {
	
	private static final long serialVersionUID = 1L;
	public SegmentTable table;
	private final JButton btnAutoPopulate;
	private final JButton btnAddSegment;
	private final JButton btnRemoveSegment;
	private final JScrollPane scrollPane;
	public JCheckBox chkSegmentation;
	
	/**
	 * Create the panel.
	 */
	public SegmentationPanel() {
	
		setBorder(new TitledBorder(null, "Segmentation", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		setLayout(new MigLayout("", "[grow][][]", "[][][grow]"));
		
		chkSegmentation = new JCheckBox("Process dataset in segments?");
		chkSegmentation.setActionCommand("SegmentationMode");
		chkSegmentation.addActionListener(this);
		add(chkSegmentation, "cell 0 0");
		
		btnAutoPopulate = new JButton("Auto populate");
		btnAutoPopulate.setToolTipText("Populate segment list automatically using specified criteria");
		btnAutoPopulate.setActionCommand("Autopopulate");
		btnAutoPopulate.addActionListener(this);
		btnAutoPopulate.setEnabled(false);
		btnAutoPopulate.setIcon(Builder.getImageIcon("refresh.png"));
		add(btnAutoPopulate, "cell 0 1");
		
		btnAddSegment = new JButton("");
		btnAddSegment.setToolTipText("Add new segment to list");
		btnAddSegment.setEnabled(false);
		btnAddSegment.setIcon(Builder.getImageIcon("edit_add.png"));
		btnAddSegment.setActionCommand("AddSegment");
		btnAddSegment.addActionListener(this);
		add(btnAddSegment, "cell 1 1");
		
		btnRemoveSegment = new JButton("");
		btnRemoveSegment.setToolTipText("Remove selected segment(s) from list");
		btnRemoveSegment.setEnabled(false);
		btnRemoveSegment.setActionCommand("DeleteSegment");
		btnRemoveSegment.addActionListener(this);
		btnRemoveSegment.setIcon(Builder.getImageIcon("delete.png"));
		add(btnRemoveSegment, "cell 2 1");
		
		scrollPane = new JScrollPane();
		scrollPane.setEnabled(false);
		add(scrollPane, "cell 0 2 3 1,grow");
		
		table = new SegmentTable();
		table.setEnabled(false);
		table.setShowHorizontalLines(true);
		table.setShowGrid(false);
		scrollPane.setViewportView(table);
	}
	
	@Override
	public void actionPerformed(ActionEvent evt) {
	
		if (evt.getActionCommand().equals("AddSegment"))
		{
			// Add a new segment to the list. Defaults to the full range of years
			table.tableModel.addSegment(new SegmentModel(table.getEarliestYear(), table.getLatestYear()));
		}
		else if (evt.getActionCommand().equals("DeleteSegment"))
		{
			// Delete the currently selected segments
			table.tableModel.removeSegments(table.getSelectedRows());
		}
		else if (evt.getActionCommand().equals("SegmentationMode"))
		{
			// Enable/Disable buttons depending on whether we are doing segmentation or not
			this.btnAddSegment.setEnabled(chkSegmentation.isSelected());
			this.btnRemoveSegment.setEnabled(chkSegmentation.isSelected());
			this.btnAutoPopulate.setEnabled(chkSegmentation.isSelected());
			this.scrollPane.setEnabled(chkSegmentation.isSelected());
			this.table.setEnabled(chkSegmentation.isSelected());
			
			// If not doing segmentation then clear the segments listed in the table
			if (!chkSegmentation.isSelected())
			{
				table.tableModel.clearSegments();
			}
		}
		else if (evt.getActionCommand().equals("Autopopulate"))
		{
			// Autopopulate the segmentation table
			
			// Ask user what they want
			SegmentPopulateDialog dialog = new SegmentPopulateDialog(this, table.getEarliestYear(), table.getLatestYear());
			
			if (!dialog.isSuccessful())
				return;
			
			// Remove existing segments
			table.tableModel.clearSegments();
			
			// Generate segments based on what user chose
			Integer segmentLength = dialog.getLength();
			Integer segmentLag = dialog.getLag();
			Integer segmentBeginningYear = dialog.getStartYear();
			Integer firstYearOfProcess = table.getEarliestYear();
			Integer lastYearOfProcess = table.getLatestYear();
			Integer startSegmentLoop;
			
			if (Math.abs((segmentBeginningYear.intValue() - firstYearOfProcess) % segmentLag.intValue()) == 0)
			{
				startSegmentLoop = segmentBeginningYear.intValue() + segmentLag.intValue();
				// Check we don't go beyond last year in dataset
				int lastyear = (segmentBeginningYear + segmentLength - 1);
				if (lastyear > lastYearOfProcess)
					lastyear = lastYearOfProcess;
				SegmentModel seg = new SegmentModel(segmentBeginningYear, lastyear);
				table.tableModel.addSegment(seg);
			}
			else
			{
				startSegmentLoop = (firstYearOfProcess.intValue()
						+ (segmentLag.intValue() - Math.abs((segmentBeginningYear.intValue() - firstYearOfProcess.intValue())
								% segmentLag.intValue())) + segmentLag.intValue());
				
				// Check we don't go beyond last year in dataset
				int lastyear = (firstYearOfProcess + segmentLength - 1);
				if (lastyear > lastYearOfProcess)
					lastyear = lastYearOfProcess;
				SegmentModel seg = new SegmentModel(firstYearOfProcess, lastyear);
				table.tableModel.addSegment(seg);
			}
			
			for (int i = startSegmentLoop; i < (lastYearOfProcess.intValue() - 1); i = i + segmentLag.intValue())
			{
				if (i <= (lastYearOfProcess.intValue() - segmentLength.intValue() + 1))
				{
					Integer firstyear = Math.min(i, (lastYearOfProcess.intValue() - segmentLength.intValue() + 1));
					Integer lastyear = Math.min(i, (lastYearOfProcess.intValue() - segmentLength.intValue() + 1)) + segmentLength - 1;
					// Check we don't go beyond last year in dataset
					if (lastyear > lastYearOfProcess)
						lastyear = lastYearOfProcess;
					SegmentModel seg = new SegmentModel(firstyear, lastyear);
					table.tableModel.addSegment(seg);
				}
				else
				{
					if (((i - Math.min(i, (lastYearOfProcess.intValue() - segmentLength.intValue() + 1)))) <= segmentLag.intValue())
					{
						Integer firstyear = (Math.min(i, (lastYearOfProcess.intValue() - segmentLength.intValue() + 1)));
						Integer lastyear = (Math.min(i, (lastYearOfProcess.intValue() - segmentLength.intValue() + 1)) + segmentLength - 1);
						// Check we don't go beyond last year in dataset
						if (lastyear > lastYearOfProcess)
							lastyear = lastYearOfProcess;
						SegmentModel seg = new SegmentModel(firstyear, lastyear);
						table.tableModel.addSegment(seg);
					}
					break;
				}
			}
		}
	}
	
	@Override
	public void setEnabled(boolean b) {
	
		super.setEnabled(b);
		this.chkSegmentation.setEnabled(b);
		this.table.setEnabled(b);
		
		if (b == false)
		{
			this.table.tableModel.clearSegments();
		}
	}
}
