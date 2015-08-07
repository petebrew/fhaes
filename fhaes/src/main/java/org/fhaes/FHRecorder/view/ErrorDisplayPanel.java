/**************************************************************************************************
 * Fire History Analysis and Exploration System (FHAES), Copyright (C) 2015
 * 
 * Contributors: Josh Brogan, Jake Lokkesmoe, Chinmay Shah, Scott Goble, and Peter Brewer
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
package org.fhaes.FHRecorder.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.SystemColor;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Highlighter;

import org.fhaes.FHRecorder.controller.IOController;
import org.fhaes.FHRecorder.util.SampleErrorModel;
import org.fhaes.enums.FeedbackDisplayProtocol;
import org.fhaes.enums.FeedbackMessageType;

import net.miginfocom.swing.MigLayout;

/**
 * ErrorDisplayPanel Class. This UI is used to address erroneous FHX2 files whenever they are attempted to be loaded.
 * 
 * @author Josh Brogan, Jake Lokkesmoe, Chinmay Shah, Scott Goble
 */
public class ErrorDisplayPanel extends JPanel {
	
	private static final long serialVersionUID = 6877730448446433391L;
	private JSplitPane splitPane;
	private JPanel suggestedFileContainer;
	private JPanel originalFileContainer;
	private JPanel suggestedFileHeaderContainer;
	private JLabel suggestedFileLabel;
	private JPanel originalFileHeaderContainer;
	private JLabel originalFileLabel;
	private JScrollPane originalFileDisplayScrollPane;
	private JScrollPane suggestedFileDisplayScrollPane;
	private JPanel originalFileHeaderTitleContainer;
	private JPanel originalFileHeaderTextContainer;
	private JPanel suggestedFileHeaderTitleContainer;
	private JPanel suggestedFileHeaderTextContainer;
	private JTextArea originalFileDisplayTextArea;
	private JTextArea suggestedFileDisplayTextArea;
	private JTextArea originalFileInstructionsTextArea;
	private JTextArea suggestedFileInstructionsTextArea;
	private JScrollPane suggestedFileInstructionsScrollPane;
	private JScrollPane originalFileInstructionsScrollPane;
	
	/**
	 * Creates a new GUI_ErrorDisplayPanel form.
	 */
	public ErrorDisplayPanel() {
		
		setLayout(new BorderLayout(0, 0));
		
		splitPane = new JSplitPane();
		add(splitPane, BorderLayout.CENTER);
		
		suggestedFileContainer = new JPanel();
		splitPane.setRightComponent(suggestedFileContainer);
		splitPane.setResizeWeight(0.5);
		suggestedFileContainer.setLayout(new BorderLayout(0, 0));
		
		suggestedFileDisplayScrollPane = new JScrollPane();
		suggestedFileDisplayScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		suggestedFileDisplayScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
		suggestedFileDisplayScrollPane.getViewport().addChangeListener(new ChangeListener() {
			
			@Override
			public void stateChanged(ChangeEvent e) {
				
				originalFileDisplayScrollPane.getVerticalScrollBar()
						.setValue(suggestedFileDisplayScrollPane.getVerticalScrollBar().getValue());
				originalFileDisplayScrollPane.getHorizontalScrollBar()
						.setValue(suggestedFileDisplayScrollPane.getHorizontalScrollBar().getValue());
			}
		});
		suggestedFileContainer.add(suggestedFileDisplayScrollPane, BorderLayout.CENTER);
		
		suggestedFileDisplayTextArea = new JTextArea();
		suggestedFileDisplayTextArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
		
		suggestedFileDisplayTextArea.setWrapStyleWord(false);
		suggestedFileDisplayScrollPane.setViewportView(suggestedFileDisplayTextArea);
		
		suggestedFileHeaderContainer = new JPanel();
		suggestedFileContainer.add(suggestedFileHeaderContainer, BorderLayout.NORTH);
		suggestedFileHeaderContainer.setLayout(new BorderLayout(0, 0));
		
		suggestedFileHeaderTitleContainer = new JPanel();
		suggestedFileHeaderContainer.add(suggestedFileHeaderTitleContainer, BorderLayout.NORTH);
		
		suggestedFileLabel = new JLabel("Modified File with all Suggested Fixes");
		suggestedFileLabel.setFont(new Font("Tahoma", Font.BOLD, 12));
		suggestedFileHeaderTitleContainer.add(suggestedFileLabel);
		
		suggestedFileHeaderTextContainer = new JPanel();
		suggestedFileHeaderContainer.add(suggestedFileHeaderTextContainer, BorderLayout.CENTER);
		suggestedFileHeaderTextContainer.setLayout(new MigLayout("insets 0", "[50,grow]", "[:60:60,grow]"));
		
		suggestedFileInstructionsScrollPane = new JScrollPane();
		suggestedFileInstructionsScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		suggestedFileHeaderTextContainer.add(suggestedFileInstructionsScrollPane, "grow");
		
		suggestedFileInstructionsTextArea = new JTextArea();
		suggestedFileInstructionsTextArea.setBackground(SystemColor.menu);
		suggestedFileInstructionsTextArea.setEditable(false);
		suggestedFileInstructionsTextArea.setFont(new Font("Tahoma", Font.PLAIN, 11));
		suggestedFileInstructionsTextArea.setLineWrap(true);
		suggestedFileInstructionsTextArea.setWrapStyleWord(true);
		suggestedFileInstructionsTextArea.setText("This is a suggested fixed version of the file."
				+ " If the changes shown are what is desired, simply click save at the bottom of the screen."
				+ " If not, feel free to edit the text below as needed to correct any mistakes.");
		suggestedFileInstructionsScrollPane.setViewportView(suggestedFileInstructionsTextArea);
		
		originalFileContainer = new JPanel();
		splitPane.setLeftComponent(originalFileContainer);
		splitPane.setDividerLocation(480);
		originalFileContainer.setLayout(new BorderLayout(0, 0));
		
		originalFileHeaderContainer = new JPanel();
		originalFileContainer.add(originalFileHeaderContainer, BorderLayout.NORTH);
		originalFileHeaderContainer.setLayout(new BorderLayout(0, 0));
		
		originalFileHeaderTitleContainer = new JPanel();
		originalFileHeaderContainer.add(originalFileHeaderTitleContainer, BorderLayout.NORTH);
		
		originalFileLabel = new JLabel("Original File with all Detected Errors");
		originalFileLabel.setFont(new Font("Tahoma", Font.BOLD, 12));
		originalFileHeaderTitleContainer.add(originalFileLabel);
		
		originalFileHeaderTextContainer = new JPanel();
		originalFileHeaderContainer.add(originalFileHeaderTextContainer, BorderLayout.CENTER);
		originalFileHeaderTextContainer.setLayout(new MigLayout("insets 0", "[50,grow]", "[:60:60,grow]"));
		
		originalFileInstructionsScrollPane = new JScrollPane();
		originalFileInstructionsScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		originalFileHeaderTextContainer.add(originalFileInstructionsScrollPane, "grow");
		
		originalFileInstructionsTextArea = new JTextArea();
		originalFileInstructionsTextArea.setBackground(SystemColor.menu);
		originalFileInstructionsTextArea.setEditable(false);
		originalFileInstructionsTextArea.setFont(new Font("Tahoma", Font.PLAIN, 11));
		originalFileInstructionsTextArea.setLineWrap(true);
		originalFileInstructionsTextArea.setWrapStyleWord(true);
		originalFileInstructionsTextArea.setText("This is the currenty file as it has been loaded into the program."
				+ " The highlighted text below describes errors that the program detected as it attempted to load the file.");
		originalFileInstructionsScrollPane.setViewportView(originalFileInstructionsTextArea);
		
		originalFileDisplayScrollPane = new JScrollPane();
		originalFileDisplayScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		originalFileDisplayScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
		originalFileDisplayScrollPane.getViewport().addChangeListener(new ChangeListener() {
			
			@Override
			public void stateChanged(ChangeEvent e) {
				
				suggestedFileDisplayScrollPane.getVerticalScrollBar()
						.setValue(originalFileDisplayScrollPane.getVerticalScrollBar().getValue());
				suggestedFileDisplayScrollPane.getHorizontalScrollBar()
						.setValue(originalFileDisplayScrollPane.getHorizontalScrollBar().getValue());
			}
		});
		originalFileContainer.add(originalFileDisplayScrollPane, BorderLayout.CENTER);
		
		originalFileDisplayTextArea = new JTextArea();
		originalFileDisplayTextArea.setEditable(false);
		originalFileDisplayTextArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
		originalFileDisplayTextArea.setWrapStyleWord(false);
		originalFileDisplayScrollPane.setViewportView(originalFileDisplayTextArea);
	}
	
	/**
	 * Displays and highlights all errors detected within the erroneous FHX2 file.
	 * 
	 * @param errors
	 */
	public void displayFileErrors(ArrayList<SampleErrorModel> errors) {
		
		String displayOriginal = "";
		String displaySuggested = "";
		
		try
		{
			StringWriter sw = new StringWriter();
			IOController.writeFileToDisk(sw);
			displaySuggested = sw.getBuffer().toString() + "\n";
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		
		try
		{
			BufferedReader br = new BufferedReader(new StringReader(IOController.getOldFile()));
			String line;
			Collections.sort(errors, new Comparator<SampleErrorModel>() {
				
				@Override
				public int compare(SampleErrorModel a, SampleErrorModel b) {
					
					return Integer.signum(a.getYear() - b.getYear());
				}
			});
			
			while (((line = br.readLine()) != null) && !line.equals("FHX2 FORMAT") && !line.equals("FIRE2 FORMAT"))
				displayOriginal += line + "\n";
			if (line != null)
				displayOriginal += line + "\n";
			int i = 0;
			
			// writes out each line of the original file and displays the
			// messages about the known errors
			while ((line = br.readLine()) != null)
			{
				while (i < errors.size() && line.indexOf("" + errors.get(i).getYear()) > 0)
				{
					line += " <ERROR #" + (i + 1) + ">: " + errors.get(i).getMessage() + ",";
					i++;
				}
				displayOriginal += line + "\n";
			}
			br.close();
		}
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		
		originalFileDisplayTextArea.setText(displayOriginal);
		suggestedFileDisplayTextArea.setText(displaySuggested);
		highlightErrorLines();
		
		FireHistoryRecorder.getFeedbackMessagePanel().updateFeedbackMessage(FeedbackMessageType.WARNING,
				FeedbackDisplayProtocol.MANUAL_HIDE, "A total of " + errors.size() + " errors were found while loading the file.");
	}
	
	/**
	 * Highlights the lines of the original file that contain detected errors.
	 */
	private void highlightErrorLines() {
		
		String text = originalFileDisplayTextArea.getText();
		String[] lines = text.split("\n");
		for (int i = 0; i < lines.length; i++)
		{
			if (lines[i].contains(" <ERROR #"))
			{
				try
				{
					int startIndex = originalFileDisplayTextArea.getLineStartOffset(i);
					int endIndex = originalFileDisplayTextArea.getLineEndOffset(i);
					Highlighter.HighlightPainter painter = new DefaultHighlighter.DefaultHighlightPainter(new Color(250, 170, 170));
					originalFileDisplayTextArea.getHighlighter().addHighlight(startIndex, endIndex, painter);
				}
				catch (BadLocationException ex)
				{
					ex.printStackTrace();
				}
			}
		}
	}
	
	/**
	 * Returns the fixed version of the file that the program will automatically generate during the loading of an erroneous FHX2 file.
	 * 
	 * @return the automatically fixed version of the FHX2 file
	 */
	public String getFixedFile() {
		
		return suggestedFileDisplayTextArea.getText();
	}
	
	/**
	 * Sets the value of the scroll bars to zero.
	 */
	public void setScrollBarsToTop() {
		
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			
			@Override
			public void run() {
				
				originalFileDisplayScrollPane.getHorizontalScrollBar().setValue(0);
				suggestedFileDisplayScrollPane.getHorizontalScrollBar().setValue(0);
				originalFileDisplayScrollPane.getVerticalScrollBar().setValue(0);
				suggestedFileDisplayScrollPane.getVerticalScrollBar().setValue(0);
			}
		});
	}
}
