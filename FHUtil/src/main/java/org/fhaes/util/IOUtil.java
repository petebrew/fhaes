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
package org.fhaes.util;

import java.awt.Component;
import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;

import org.codehaus.plexus.util.FileUtils;
import org.fhaes.filefilter.CSVFileFilter;
import org.fhaes.filefilter.FHXFileFilter;
import org.fhaes.filefilter.PDFFilter;
import org.fhaes.filefilter.PNGFilter;
import org.fhaes.filefilter.SVGFilter;
import org.fhaes.filefilter.TXTFileFilter;
import org.fhaes.preferences.App;
import org.fhaes.preferences.FHAESPreferences.PrefKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * IOUtil Class.
 */
public class IOUtil {
	
	private static final Logger log = LoggerFactory.getLogger(IOUtil.class);
	
	/**
	 * Prompt the user for an output folder.
	 * 
	 * @return
	 */
	public static File getOutputFolder(Component frame) {
	
		String lastVisitedFolder = App.prefs.getPref(PrefKey.PREF_LAST_EXPORT_FOLDER, null);
		File outputFolder;
		
		// Create a file chooser
		final JFileChooser fc = new JFileChooser(lastVisitedFolder);
		
		fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		fc.setMultiSelectionEnabled(false);
		fc.setDialogTitle("Select output folder");
		
		// In response to a button click:
		int returnVal = fc.showSaveDialog(frame);
		
		if (returnVal == JFileChooser.APPROVE_OPTION)
		{
			outputFolder = fc.getSelectedFile();
			App.prefs.setPref(PrefKey.PREF_LAST_EXPORT_FOLDER, outputFolder.getAbsolutePath());
		}
		else
		{
			return null;
		}
		
		return outputFolder;
	}
	
	/**
	 * Prompt the user for an output filename.
	 * 
	 * @param filter
	 * @return
	 */
	public static File getOutputFile(FileFilter filter) {
	
		String lastVisitedFolder = App.prefs.getPref(PrefKey.PREF_LAST_EXPORT_FOLDER, null);
		File outputFile;
		
		// Create a file chooser
		final JFileChooser fc = new JFileChooser(lastVisitedFolder);
		
		fc.setAcceptAllFileFilterUsed(true);
		
		if (filter != null)
		{
			fc.addChoosableFileFilter(filter);
			fc.setFileFilter(filter);
		}
		
		fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
		fc.setMultiSelectionEnabled(false);
		fc.setDialogTitle("Save as...");
		
		// In response to a button click:
		int returnVal = fc.showSaveDialog(App.mainFrame);
		
		if (returnVal == JFileChooser.APPROVE_OPTION)
		{
			outputFile = fc.getSelectedFile();
			
			if (FileUtils.getExtension(outputFile.getAbsolutePath()) == "")
			{
				log.debug("Output file extension not set by user");
				
				if (fc.getFileFilter().getDescription().equals(new FHXFileFilter().getDescription()))
				{
					log.debug("Adding fhx extension to output file name");
					outputFile = new File(outputFile.getAbsolutePath() + ".fhx");
				}
				else if (fc.getFileFilter().getDescription().equals(new TXTFileFilter().getDescription()))
				{
					log.debug("Adding txt extension to output file name");
					outputFile = new File(outputFile.getAbsolutePath() + ".txt");
				}
				else if (fc.getFileFilter().getDescription().equals(new CSVFileFilter().getDescription()))
				{
					log.debug("Adding csv extension to output file name");
					outputFile = new File(outputFile.getAbsolutePath() + ".csv");
				}
				else if (fc.getFileFilter().getDescription().equals(new PDFFilter().getDescription()))
				{
					log.debug("Adding pdf extension to output file name");
					outputFile = new File(outputFile.getAbsolutePath() + ".pdf");
				}
				else if (fc.getFileFilter().getDescription().equals(new PNGFilter().getDescription()))
				{
					log.debug("Adding png extension to output file name");
					outputFile = new File(outputFile.getAbsolutePath() + ".png");
				}
				else if (fc.getFileFilter().getDescription().equals(new SVGFilter().getDescription()))
				{
					log.debug("Adding svg extension to output file name");
					outputFile = new File(outputFile.getAbsolutePath() + ".svg");
				}
				
			}
			else
			{
				log.debug("Output file extension set my user to '" + FileUtils.getExtension(outputFile.getAbsolutePath()) + "'");
			}
			
			App.prefs.setPref(PrefKey.PREF_LAST_EXPORT_FOLDER, outputFile.getAbsolutePath());
		}
		else
		{
			return null;
		}
		
		if (outputFile.exists())
		{
			Object[] options = { "Overwrite", "No", "Cancel" };
			int response = JOptionPane.showOptionDialog(App.mainFrame, "The file '" + outputFile.getName()
					+ "' already exists.  Are you sure you want to overwrite?", "Confirm", JOptionPane.YES_NO_CANCEL_OPTION,
					JOptionPane.QUESTION_MESSAGE, null, // do not use a custom Icon
					options, // the titles of buttons
					options[0]); // default button title
			
			if (response != JOptionPane.YES_OPTION)
			{
				return null;
			}
		}
		
		return outputFile;
	}
}
