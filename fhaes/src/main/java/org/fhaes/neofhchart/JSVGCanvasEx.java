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

import javax.swing.ActionMap;

import org.apache.batik.swing.JSVGCanvas;
//import java.awt.Rectangle;
//import org.apache.batik.swing.gvt.Overlay;
//import org.apache.batik.util.gui.DOMViewer;
//import org.apache.batik.util.gui.DOMViewerController;
//import org.apache.batik.util.gui.ElementOverlayManager;
//import org.w3c.dom.Document;

/**
 * This class represents a general-purpose swing SVG component. The <code>JSVGCanvasEx</code> does not provided additional functionalities
 * compared to the <code>JSVGComponent</code> but simply provides an API conformed to the JavaBean specification. The only major change
 * between the <code>JSVGComponent</code> and this component is that interactors and text selection are activated by default.
 *
 * @author <a href="mailto:tkormann@apache.org">Thierry Kormann</a>
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id: JSVGCanvasEx.java 1372129 2012-08-12 15:31:50Z helder $
 */
public class JSVGCanvasEx extends JSVGCanvas {
	
	private static final long serialVersionUID = 1L;
	
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
