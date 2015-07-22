package org.fhaes.util;

import java.util.ArrayList;

import org.fhaes.model.FHFile;
import org.fhaes.model.FHFileGroup;
import org.fhaes.preferences.App;
import org.fhaes.preferences.FHAESPreferences.PrefKey;
import org.geotools.referencing.GeodeticCalculator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * FHCluster Class. Combine files into groups based upon the distance threshold specified in the preferences.
 * 
 * @author pwb48
 */
public class FHCluster {

	private static final Logger log = LoggerFactory.getLogger(FHCluster.class);

	private ArrayList<FHFile> filesToProcess;
	private ArrayList<ArrayList<FHFile>> groups;
	private ArrayList<FHFile> filesWithNoCoords;
	private Integer distanceThreshold;

	/**
	 * Standard constructor. Requires a list of FHFiles.
	 * 
	 * @param files
	 */
	public FHCluster(ArrayList<FHFile> files) {

		if (filesToProcess == null || filesToProcess.size() == 0)
		{
			log.error("No files to process");
			return;
		}

		this.filesToProcess = files;
		process();
	}

	/**
	 * Set the files to process.
	 * 
	 * @param files
	 */
	public void setFileList(ArrayList<FHFile> files) {

		this.filesToProcess = files;
		process();
	}

	/**
	 * Run the clustering process.
	 */
	public void process() {

		distanceThreshold = App.prefs.getIntPref(PrefKey.COMPOSITE_DISTANCE_THRESHOLD_KM, 5);
		groups = new ArrayList<ArrayList<FHFile>>();
		filesWithNoCoords = new ArrayList<FHFile>();

		if (filesToProcess == null || filesToProcess.size() == 0)
		{
			log.error("No files to process");
			return;
		}

		ArrayList<FHFile> group1 = new ArrayList<FHFile>();

		// Seed the groups with the first suitable file
		for (FHFile file : filesToProcess)
		{
			if (file.getFirstLatitudeDbl() != null && file.getFirstLongitudeDbl() != null)
			{
				group1.add(file);
				break;
			}
		}
		groups.add(group1);

		for (FHFile file : filesToProcess)
		{
			searchGroupsForMatch(file);
		}

		recurseRefineGroups(0);
	}

	/**
	 * Check through the groups to see if any are closer than the threshold.
	 * 
	 * @param depth
	 * @return
	 */
	private Boolean recurseRefineGroups(int depth) {

		depth++;

		if (depth > 50)
		{
			log.error("Infinite recursion detected.  Bailing out");
			return false;
		}

		if (groups.size() == 1)
		{
			log.info("One one group so no need to refine further");
			return false;
		}

		GeodeticCalculator gc = new GeodeticCalculator();

		for (int i = 0; i < groups.size(); i++)
		{
			ArrayList<FHFile> focusedgroup = groups.get(i);

			for (int j = 0; j < groups.size(); j++)
			{
				if (j == i)
					continue;
				ArrayList<FHFile> searchgroup = groups.get(j);

				for (FHFile f1 : focusedgroup)
				{
					gc.setStartingGeographicPoint(f1.getFirstLongitudeDbl(), f1.getFirstLatitudeDbl());

					for (FHFile f2 : searchgroup)
					{
						gc.setDestinationGeographicPoint(f2.getFirstLongitudeDbl(), f2.getFirstLatitudeDbl());
						Double distance = gc.getOrthodromicDistance();
						if (distance <= distanceThreshold * 1000)
						{
							// Merge groups i and j
							ArrayList<FHFile> mergedgroups = new ArrayList<FHFile>();
							ArrayList<ArrayList<FHFile>> newgroups = new ArrayList<ArrayList<FHFile>>();
							mergedgroups.addAll(focusedgroup);
							mergedgroups.addAll(searchgroup);
							newgroups.add(mergedgroups);
							for (int k = 0; k < groups.size(); k++)
							{
								ArrayList<FHFile> othergroup = groups.get(k);
								if (k != i && k != j)
								{
									newgroups.add(othergroup);
								}
							}

							groups = newgroups;

							recurseRefineGroups(depth);
							return true;
						}
					}
				}

			}

		}

		return false;
	}

	/**
	 * Search through the existing groups to see if the specified file is close enough to group, otherwise place it in it's own group.
	 * 
	 * @param file
	 * @return
	 */
	private Boolean searchGroupsForMatch(FHFile file) {

		if (file == null)
		{
			return false;
		}

		if (file.getFirstLatitudeDbl() == null || file.getFirstLongitudeDbl() == null)
		{
			log.warn("File " + file.getName() + " has no location information so skipping");
			filesWithNoCoords.add(file);
			return false;
		}

		GeodeticCalculator gc = new GeodeticCalculator();
		gc.setStartingGeographicPoint(file.getFirstLongitudeDbl(), file.getFirstLatitudeDbl());

		for (ArrayList<FHFile> group : groups)
		{
			for (FHFile f : group)
			{
				if (f.equals(file))
				{

					log.error("Found file in group already so skipping");
					return true;
				}

				gc.setDestinationGeographicPoint(f.getFirstLongitudeDbl(), f.getFirstLatitudeDbl());

				Double distance = gc.getOrthodromicDistance();
				log.debug("Distance between " + file.getName() + " and " + f.getName() + " is " + distance.intValue() + "m");

				if (distance <= distanceThreshold * 1000)
				{
					group.add(file);
					return true;
				}
			}
		}

		// No matches found so create a new group for the file
		ArrayList<FHFile> newgroup = new ArrayList<FHFile>();
		newgroup.add(file);
		groups.add(newgroup);
		return true;
	}

	/**
	 * Get the groups calculated by this class. A special group is included for all files with no coordinates.
	 * 
	 * @return
	 */
	public ArrayList<FHFileGroup> getGroups() {

		ArrayList<FHFileGroup> fhfgroups = new ArrayList<FHFileGroup>();
		int i = 0;

		if (groups == null || groups.size() == 0)
		{
			log.warn("No groups to return");
			return fhfgroups;
		}

		for (ArrayList<FHFile> group : groups)
		{
			i++;
			FHFileGroup fhg = new FHFileGroup("Group " + i, group);
			fhfgroups.add(fhg);
		}

		if (filesWithNoCoords.size() > 0)
		{
			FHFileGroup fhg = new FHFileGroup("Files with no location", filesWithNoCoords);
			fhfgroups.add(fhg);
		}

		return fhfgroups;
	}
}
