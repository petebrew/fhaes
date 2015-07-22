/*******************************************************************************
 * Copyright (C) 2013 Peter Brewer
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * Contributors:
 *     Peter Brewer
 ******************************************************************************/

package org.fhaes.model;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import javax.swing.JList;

import org.fhaes.gui.MainWindow;

/**
 * FileDropTargetListener Class. Working drag and drop on Windows and Linux with either Nautilus (Gnome) or Konqueror (KDE).
 */
@SuppressWarnings("rawtypes")
public class FileDropTargetListener extends JList implements DropTargetListener {

	// ------------------------------ FIELDS ------------------------------

	private static final long serialVersionUID = 1L;
	DropTarget dropTarget = null;
	private static final String URI_LIST_MIME_TYPE = "text/uri-list;class=java.lang.String";

	// -------------------------- STATIC METHODS --------------------------

	private static List<File> textURIListToFileList(String data) {

		List<File> list = new ArrayList<File>(1);
		for (StringTokenizer st = new StringTokenizer(data, "\r\n"); st.hasMoreTokens();)
		{
			String s = st.nextToken();
			if (s.startsWith("#"))
			{
				// the line is a comment (as per the RFC 2483)
				continue;
			}
			try
			{
				File file = new File(s);
				list.add(file);
			}
			catch (IllegalArgumentException e)
			{
				e.printStackTrace();
			}
		}
		return list;
	}

	// --------------------------- CONSTRUCTORS ---------------------------

	public FileDropTargetListener() {

		dropTarget = new DropTarget(this, this);
	}

	// ------------------------ INTERFACE METHODS ------------------------

	// --------------------- Interface DropTargetListener ---------------------

	/**
	 * TODO
	 */
	public void dragEnter(DropTargetDragEvent event) {

		event.acceptDrag(DnDConstants.ACTION_MOVE);
	}

	/**
	 * TODO
	 */
	public void dragOver(DropTargetDragEvent event) {

	}

	/**
	 * TODO
	 */
	public void dropActionChanged(DropTargetDragEvent event) {

	}

	/**
	 * TODO
	 */
	public void dragExit(DropTargetEvent event) {

	}

	/**
	 * TODO
	 */
	public void drop(DropTargetDropEvent event) {

		Transferable transferable = event.getTransferable();

		ArrayList<File> files = new ArrayList<File>();

		event.acceptDrop(DnDConstants.ACTION_COPY_OR_MOVE);

		DataFlavor uriListFlavor = null;
		try
		{
			uriListFlavor = new DataFlavor(URI_LIST_MIME_TYPE);
		}
		catch (ClassNotFoundException e)
		{
			e.printStackTrace();
		}

		try
		{
			MainWindow.getInstance().setBusyCursor(true);

			if (transferable.isDataFlavorSupported(DataFlavor.javaFileListFlavor))
			{
				List data = (List) transferable.getTransferData(DataFlavor.javaFileListFlavor);
				for (Object o : data)
				{

					File f = new File(o.toString());
					files.add(f);
				}
				System.out.println(data);
			}
			else if (transferable.isDataFlavorSupported(uriListFlavor))
			{
				String data = (String) transferable.getTransferData(uriListFlavor);
				List<File> files2 = textURIListToFileList(data);
				files.addAll(files2);
				System.out.println(files);
			}

		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			MainWindow.getInstance().setBusyCursor(true);
		}

		MainWindow.getInstance().loadFiles(files.toArray(new File[files.size()]));

		// setModel(model);
	}

	/**
	 * Required in Java 6.
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public ArrayList getSelectedValuesList() {

		@SuppressWarnings("deprecation")
		Object[] values = super.getSelectedValues();

		ArrayList returnlist = new ArrayList();
		for (Object v : values)
		{
			returnlist.add(v);
		}

		return returnlist;
	}
}
