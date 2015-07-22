package org.fhaes.fhfilereader;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

import org.apache.commons.io.FilenameUtils;
import org.fhaes.exceptions.InvalidCategoryFileException;
import org.fhaes.model.FHCategoryEntry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * FHCategoryReader Class.
 * 
 * @author Joshua Brogan and Peter Brewer
 */
public class FHCategoryReader {

	// Declare FHAES specialized objects
	private static final Logger log = LoggerFactory.getLogger(FHCategoryReader.class);

	// Declare header constants
	public static final String FHAES_CATEGORY_FILE_HEADER = "FHAES Category File";
	public static final String FHAES_CATEGORY_FILE_VERSION = "version=1.0";
	public static final int INDEX_OF_HEADER = 0;
	public static final int INDEX_OF_VERSION = 1;
	public static final int INDEX_OF_FILENAME = 2;

	// Declare IO constants
	public static final int INDEX_OF_SERIES_TITLE = 0;
	public static final int INDEX_OF_CATEGORY = 1;
	public static final int INDEX_OF_CONTENT = 2;
	public static final int NUM_COLUMNS_IN_FILE = 3;

	// Declare local constants
	private final int NUM_INITIAL_VALUES_TO_READ = 2;

	// Declare local variables
	private ArrayList<FHCategoryEntry> categoryEntries = new ArrayList<FHCategoryEntry>();
	private String nameOfCorrespondingFHXFile;

	/**
	 * Parses the input category file and stores its contents in the categoryEntries arrayList.
	 * 
	 * @param categoryFile
	 */
	public FHCategoryReader(File categoryFile) {

		try
		{
			// Setup the scanner for reading and storing the category entries from the CSV file
			Scanner sc = new Scanner(categoryFile);
			sc.useDelimiter(",|\r\n");

			// Verify that the category file has the necessary header and version number
			for (int numValuesRead = 0; numValuesRead <= NUM_INITIAL_VALUES_TO_READ; numValuesRead++)
			{
				if (numValuesRead == INDEX_OF_HEADER && sc.hasNext())
				{
					if (!sc.next().equals(FHAES_CATEGORY_FILE_HEADER))
					{
						sc.close();
						throw new InvalidCategoryFileException();
					}
				}
				else if (numValuesRead == INDEX_OF_VERSION && sc.hasNext())
				{
					if (!sc.next().equals(FHAES_CATEGORY_FILE_VERSION))
					{
						sc.close();
						throw new InvalidCategoryFileException();
					}
				}
				else if (numValuesRead == INDEX_OF_FILENAME && sc.hasNext())
				{
					nameOfCorrespondingFHXFile = sc.next();
				}
				else
				{
					sc.close();
					throw new InvalidCategoryFileException();
				}
			}

			// Read the contents of the category file into the categoryEntries array
			while (sc.hasNext())
			{
				String seriesTitle = sc.next();
				String category = sc.next();
				String content = sc.next();

				if (!seriesTitle.equals("") && !category.equals("") && !content.equals(""))
				{
					categoryEntries.add(new FHCategoryEntry(seriesTitle, category, content));
				}
				else
				{
					sc.close();
					throw new InvalidCategoryFileException();
				}
			}

			sc.close();
		}
		catch (FileNotFoundException ex)
		{
			log.error("The category file " + FilenameUtils.getBaseName(categoryFile.getAbsolutePath()) + " does not exist.");
		}
		catch (InvalidCategoryFileException ex)
		{
			log.error("Could not parse category file. File is in an invalid format or has missing entries.");
		}
	}

	/**
	 * Gets the list of category entries which were parsed in by the reader.
	 * 
	 * @return categoryEntries
	 */
	public ArrayList<FHCategoryEntry> getCategoryEntryList() {

		return categoryEntries;
	}

	/**
	 * Gets the name of the FHX file which this category file corresponds to.
	 * 
	 * @return nameOfCorrespondingFHXFile
	 */
	public String getNameOfCorrespondingFHXFile() {

		return nameOfCorrespondingFHXFile;
	}
}
