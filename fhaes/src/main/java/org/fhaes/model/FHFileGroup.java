package org.fhaes.model;

import java.util.ArrayList;

/**
 * FHFileGroup Class.
 */
public class FHFileGroup {

	private ArrayList<FHFile> files;
	private String name;

	/**
	 * TODO
	 * 
	 * @param name
	 * @param files
	 */
	public FHFileGroup(String name, ArrayList<FHFile> files) {

		setFiles(files);
		setName(name);
	}

	public String getName() {

		return name;
	}

	public ArrayList<FHFile> getFiles() {

		return files;
	}

	public void setName(String name) {

		this.name = name;
	}

	public void setFiles(ArrayList<FHFile> files) {

		this.files = files;
	}

	public String toString() {

		return getName();
	}
}
