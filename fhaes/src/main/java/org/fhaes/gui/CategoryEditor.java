package org.fhaes.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;

import org.fhaes.fhfilereader.FHCategoryReader;
import org.fhaes.model.FHCategoryEntry;
import org.fhaes.model.FHFile;
import org.fhaes.model.FHSeries;
import org.fhaes.util.Builder;

import au.com.bytecode.opencsv.CSVWriter;
import net.miginfocom.swing.MigLayout;

/**
 * CategoryEditor Class.
 * 
 * @author Joshua Brogan
 */
public class CategoryEditor extends JDialog {

	private static final long serialVersionUID = 1L;

	// Declare local constants
	private final int SCROLL_INCREMENT_AMOUNT = 16;

	// Declare local variables
	private final FHFile workingFile;
	private JPanel categoryListPanel;

	/**
	 * Initializes the dialog.
	 * 
	 * @param file
	 */
	public CategoryEditor(FHFile file) {

		workingFile = file;
		initGUI();
	}

	/**
	 * Handles initialization of the GUI components.
	 */
	private void initGUI() {

		this.getContentPane().setLayout(new BorderLayout(0, 0));

		categoryListPanel = new JPanel();
		categoryListPanel.setBackground(new Color(80, 80, 80));
		categoryListPanel.setLayout(new MigLayout("wrap 1", "[grow,fill]", "[top]"));

		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBorder(BorderFactory.createEmptyBorder());
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		scrollPane.getVerticalScrollBar().setUnitIncrement(SCROLL_INCREMENT_AMOUNT);
		scrollPane.setViewportView(categoryListPanel);
		this.getContentPane().add(scrollPane, BorderLayout.CENTER);

		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new MigLayout("", "[120:120][120:120][grow,fill][80:80][80:80]", "[30:30,fill]"));
		this.getContentPane().add(buttonPanel, BorderLayout.SOUTH);

		/*
		 * EXPAND ALL BUTTON
		 */
		JButton btnExpandAll = new JButton("Expand All");
		btnExpandAll.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {

				expandAllEntries();
			}
		});
		buttonPanel.add(btnExpandAll, "cell 0 0,grow");

		/*
		 * COLLAPSE ALL BUTTON
		 */
		JButton btnCollapseAll = new JButton("Collapse All");
		btnCollapseAll.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {

				collapseAllEntries();
			}
		});
		buttonPanel.add(btnCollapseAll, "cell 1 0,grow");

		/*
		 * SAVE BUTTON
		 */
		JButton btnSave = new JButton("Save");
		btnSave.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {

				if (workingFile.getCategoryFilePath() != null)
				{
					saveCategoriesToFile(workingFile.getCategoryFilePath());
				}
				else
				{
					String categoryFilePath = workingFile.getDefaultCategoryFilePath();
					saveCategoriesToFile(categoryFilePath);
					workingFile.setCategoryFilePath(categoryFilePath);
				}

				dispose();
			}
		});
		buttonPanel.add(btnSave, "cell 3 0,grow");

		/*
		 * CANCEL BUTTON
		 */
		JButton btnCancel = new JButton("Cancel");
		btnCancel.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {

				dispose();
			}
		});
		buttonPanel.add(btnCancel, "cell 4 0,grow");

		// Finish setting up the properties of the category editor
		this.setIconImage(Builder.getApplicationIcon());
		this.setModal(true);
		this.setResizable(false);
		this.setSize(800, 500);
		this.setLocationRelativeTo(null);
		this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		this.setTitle("Edit Categories for FHX file: " + workingFile.getFileNameWithoutExtension());

		// Make sure to populate the entries before showing the dialog
		refreshEntries();
		this.setVisible(true);
	}

	/**
	 * Collapses all entries for all contained instances of categoryEntryPanel.
	 */
	private void collapseAllEntries() {

		for (int i = 0; i < categoryListPanel.getComponentCount(); i++)
		{
			CategoryEntryPanel currentPanel = (CategoryEntryPanel) categoryListPanel.getComponents()[i];
			currentPanel.collapseAllEntries();
		}
	}

	/**
	 * Expands all entries for all contained instances of categoryEntryPanel.
	 */
	private void expandAllEntries() {

		for (int i = 0; i < categoryListPanel.getComponentCount(); i++)
		{
			CategoryEntryPanel currentPanel = (CategoryEntryPanel) categoryListPanel.getComponents()[i];
			currentPanel.expandAllEntries();
		}
	}

	/**
	 * Returns a list of all category entries for all series.
	 * 
	 * @return a complete list of category entries
	 */
	private ArrayList<FHCategoryEntry> getAllCategoryEntries() {

		ArrayList<FHCategoryEntry> categoryEntries = new ArrayList<FHCategoryEntry>();

		for (int i = 0; i < categoryListPanel.getComponentCount(); i++)
		{
			CategoryEntryPanel currentPanel = (CategoryEntryPanel) categoryListPanel.getComponents()[i];
			categoryEntries.addAll(currentPanel.getCategoryEntries());
		}

		return categoryEntries;
	}

	/**
	 * Updates the display with the category entries currently stored in the working file.
	 */
	private void refreshEntries() {

		categoryListPanel.removeAll();

		ArrayList<FHSeries> seriesList = workingFile.getFireHistoryReader().getSeriesList();

		for (int i = 0; i < seriesList.size(); i++)
		{
			categoryListPanel.add(new CategoryEntryPanel(this, seriesList.get(i)));
		}
	}

	/**
	 * Ensures that exactly one CategoryEntryPanel can be selected at any one time.
	 */
	protected void refreshAfterNewSelection(CategoryEntryPanel selectedPanel) {

		for (int i = 0; i < categoryListPanel.getComponentCount(); i++)
		{
			CategoryEntryPanel currentPanel = (CategoryEntryPanel) categoryListPanel.getComponents()[i];

			if (!currentPanel.equals(selectedPanel))
			{
				currentPanel.clearTreeSelection();
			}
		}
	}

	/**
	 * Writes all category entries to the specified csv file.
	 * 
	 * @param filePath
	 */
	private void saveCategoriesToFile(String filePath) {

		try
		{
			CSVWriter writer = new CSVWriter(new FileWriter(filePath), CSVWriter.DEFAULT_SEPARATOR, CSVWriter.NO_QUOTE_CHARACTER, "\r\n");
			ArrayList<FHCategoryEntry> categoryEntries = getAllCategoryEntries();

			String[] headerLine = new String[FHCategoryReader.NUM_COLUMNS_IN_FILE];
			headerLine[FHCategoryReader.INDEX_OF_HEADER] = FHCategoryReader.FHAES_CATEGORY_FILE_HEADER;
			headerLine[FHCategoryReader.INDEX_OF_VERSION] = FHCategoryReader.FHAES_CATEGORY_FILE_VERSION;
			headerLine[FHCategoryReader.INDEX_OF_FILENAME] = workingFile.getFileNameWithoutExtension();
			writer.writeNext(headerLine);

			for (int i = 0; i < categoryEntries.size(); i++)
			{
				String[] nextLine = new String[FHCategoryReader.NUM_COLUMNS_IN_FILE];
				nextLine[FHCategoryReader.INDEX_OF_SERIES_TITLE] = categoryEntries.get(i).getSeriesTitle();
				nextLine[FHCategoryReader.INDEX_OF_CATEGORY] = categoryEntries.get(i).getCategory();
				nextLine[FHCategoryReader.INDEX_OF_CONTENT] = categoryEntries.get(i).getContent();
				writer.writeNext(nextLine);
			}

			writer.close();
		}
		catch (IOException ex)
		{
			ex.printStackTrace();
		}
	}
}
