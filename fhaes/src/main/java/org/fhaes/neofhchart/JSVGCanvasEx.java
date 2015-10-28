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
package org.fhaes.neofhchart;

/*

 Licensed to the Apache Software Foundation (ASF) under one or more
 contributor license agreements.  See the NOTICE file distributed with
 this work for additional information regarding copyright ownership.
 The ASF licenses this file to You under the Apache License, Version 2.0
 (the "License"); you may not use this file except in compliance with
 the License.  You may obtain a copy of the License at

 http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.

 */

import java.awt.Dimension;
import java.awt.geom.AffineTransform;

import javax.swing.ActionMap;

import org.apache.batik.bridge.ViewBox;
import org.apache.batik.swing.JSVGCanvas;
import org.apache.batik.swing.svg.SVGUserAgent;
import org.w3c.dom.svg.SVGPreserveAspectRatio;
import org.w3c.dom.svg.SVGRect;
//import java.awt.Rectangle;
//import org.apache.batik.swing.gvt.Overlay;
//import org.apache.batik.util.gui.DOMViewer;
//import org.apache.batik.util.gui.DOMViewerController;
//import org.apache.batik.util.gui.ElementOverlayManager;
//import org.w3c.dom.Document;
import org.w3c.dom.svg.SVGSVGElement;

/**
 * JSVGCanvasEx Class. This class represents a general-purpose swing SVG component. The <code>JSVGCanvasEx</code> does not provided
 * additional functionalities compared to the <code>JSVGComponent</code> but simply provides an API conformed to the JavaBean specification.
 * The only major change between the <code>JSVGComponent</code> and this component is that interactors and text selection are activated by
 * default.
 *
 * @author <a href="mailto:tkormann@apache.org">Thierry Kormann</a>
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id: JSVGCanvasEx.java 1372129 2012-08-12 15:31:50Z helder $
 */
public class JSVGCanvasEx extends JSVGCanvas {
	
	private static final long serialVersionUID = 1L;
	
	protected int xBorder = 8;
	protected int yBorder = 8;
	protected boolean autoFitToCanvas;
	protected boolean stopProcessingOnDispose;
	
	public JSVGCanvasEx() {
	
	}
	
	public JSVGCanvasEx(SVGUserAgent ua, boolean eventsEnabled, boolean selectableText) {
	
		super(ua, eventsEnabled, selectableText);
	}
	
	public boolean isAutoFitToCanvas() {
	
		return autoFitToCanvas;
	}
	
	public void setAutoFitToCanvas(boolean autoFit) {
	
		this.autoFitToCanvas = autoFit;
		if (autoFit)
			setRecenterOnResize(true);
	}
	
	public boolean isStopProcessingOnDispose() {
	
		return stopProcessingOnDispose;
	}
	
	public void setStopProcessingOnDispose(boolean stopProcessingOnDispose) {
	
		this.stopProcessingOnDispose = stopProcessingOnDispose;
	}
	
	/**
	 * note the border will be ignored if canvas width/height <= 4 * borderX/Y
	 **/
	public void setInnerBorderWidth(int borderX, int borderY) {
	
		this.xBorder = borderX;
		this.yBorder = borderY;
	}
	
	@Override
	public void dispose() {
	
		if (stopProcessingOnDispose)
			stopProcessing();
		super.dispose();
	}
	
	/**
	 * overwrites calculateViewingTransform to allow autoFitToCanvas behaviour, if autoFitToCanvas is not set it will use the base's class
	 * method.
	 *
	 * Right now fragIdent != null IS NOT SUPPORTED !!!
	 */
	@Override
	protected AffineTransform calculateViewingTransform(String fragIdent, SVGSVGElement svgElt) {
	
		assert fragIdent == null; // don't understand this parameter, have not found a simple test case yet
		if (!autoFitToCanvas || fragIdent != null)
			return super.calculateViewingTransform(fragIdent, svgElt);
		// canvas size / additional border
		Dimension d = getSize();
		int xb = 0, yb = 0;
		if (d.width < 1)
			d.width = 1;
		if (d.height < 1)
			d.height = 1;
		if (d.width > 4 * xBorder) // if canvas is large enough add border
			d.width -= 2 * (xb = xBorder);
		if (d.height > 4 * yBorder) // if canvas is large enough add border
			d.height -= 2 * (yb = yBorder);
		//
		AffineTransform tf;
		//
		String viewBox = svgElt.getAttributeNS(null, ViewBox.SVG_VIEW_BOX_ATTRIBUTE);
		if (viewBox.length() == 0)
		{
			// no viewbox specified, make an own one
			float[] vb = calculateDefaultViewbox(fragIdent, svgElt);
			tf = ViewBox.getPreserveAspectRatioTransform(vb, SVGPreserveAspectRatio.SVG_PRESERVEASPECTRATIO_XMIDYMID, true, d.width,
					d.height);
		}
		else
		{
			String aspectRatio = svgElt.getAttributeNS(null, ViewBox.SVG_PRESERVE_ASPECT_RATIO_ATTRIBUTE);
			if (aspectRatio.length() > 0)
				tf = ViewBox.getPreserveAspectRatioTransform(svgElt, viewBox, aspectRatio, d.width, d.height);
			else
			{
				float[] vb = ViewBox.parseViewBoxAttribute(svgElt, viewBox);
				tf = ViewBox.getPreserveAspectRatioTransform(vb, SVGPreserveAspectRatio.SVG_PRESERVEASPECTRATIO_XMIDYMAX, true, d.width,
						d.height);
			}
		}
		if (xb > 0 || yb > 0)
		{ // center image
			AffineTransform tf2 = AffineTransform.getTranslateInstance(xb, yb);
			tf2.concatenate(tf);
			tf = tf2;
		}
		return tf;
	}
	
	protected float[] calculateDefaultViewbox(String fragIdent, SVGSVGElement svgElt) {
	
		float[] vb = new float[4];
		SVGRect rc = svgElt.getBBox();
		vb[0] = rc.getX();
		vb[1] = rc.getY();
		vb[2] = rc.getWidth();
		vb[3] = rc.getHeight();
		return vb;
	}
	
	/**
	 * Builds the ActionMap of this canvas with a set of predefined <code>Action</code>s.
	 */
	@Override
	protected void installActions() {
	
		super.installActions();
		ActionMap actionMap = getActionMap();
		
		actionMap.put(ZOOM_IN_ACTION, new ZoomInABitAction());
		actionMap.put(ZOOM_OUT_ACTION, new ZoomOutABitAction());
	}
	
	/**
	 * A swing action to zoom in the canvas.
	 */
	public class ZoomInABitAction extends ZoomAction {
		
		private static final long serialVersionUID = 1L;
		
		ZoomInABitAction() {
		
			super(1.05);
		}
	}
	
	/**
	 * A swing action to zoom out the canvas.
	 */
	public class ZoomOutABitAction extends ZoomAction {
		
		private static final long serialVersionUID = 1L;
		
		ZoomOutABitAction() {
		
			super(0.95);
		}
	}
	
}
