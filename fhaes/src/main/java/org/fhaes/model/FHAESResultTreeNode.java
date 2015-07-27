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

import javax.swing.ImageIcon;
import javax.swing.tree.DefaultMutableTreeNode;

import org.fhaes.enums.FHAESResult;
import org.fhaes.util.FHAESAction;

/**
 * FHAESResultTreeNode Class. Tree leaf node representing a result of an analysis
 * 
 * @author Peter Brewer
 */
public class FHAESResultTreeNode extends DefaultMutableTreeNode {
	
	private static final long serialVersionUID = 1L;
	private ImageIcon icon;
	private final FHAESResult fhresult;
	private ArrayList<FHAESAction> popupActions = new ArrayList<FHAESAction>();
	private boolean enabled = true;
	
	/**
	 * TODO
	 * 
	 * @param fhresult
	 * @param icon
	 */
	public FHAESResultTreeNode(FHAESResult fhresult, ImageIcon icon) {
		
		super(fhresult.getShortName());
		this.icon = icon;
		this.fhresult = fhresult;
	}
	
	/**
	 * TODO
	 * 
	 * @param fhresult
	 */
	public FHAESResultTreeNode(FHAESResult fhresult) {
		
		super(fhresult.getShortName());
		this.fhresult = fhresult;
	}
	
	@Override
	public boolean getAllowsChildren() {
		
		return false;
	}
	
	public void setEnabled(boolean b) {
		
		enabled = b;
	}
	
	public boolean isEnabled() {
		
		return enabled;
	}
	
	public ImageIcon getIcon() {
		
		return icon;
	}
	
	public FHAESResult getFHAESResult() {
		
		return fhresult;
	}
	
	public ArrayList<FHAESAction> getArrayOfActions() {
		
		return popupActions;
	}
	
	public void addAction(FHAESAction action) {
		
		popupActions.add(action);
	}
	
	public void clearActions() {
		
		popupActions = new ArrayList<FHAESAction>();
	}
}
