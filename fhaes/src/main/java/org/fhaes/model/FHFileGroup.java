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
package org.fhaes.model;

import java.util.ArrayList;

import org.fhaes.fhfilereader.FHFile;

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
	
	@Override
	public String toString() {
		
		return getName();
	}
}
