package org.fhaes.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Scanner;

import javax.swing.DefaultCellEditor;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeSelectionModel;

import org.fhaes.model.FHCategoryEntry;
import org.fhaes.model.FHSeries;
import org.fhaes.util.Builder;
import org.fhaes.util.FHAESAction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * CategoryEntryPanel Class.
 * 
 * @author Joshua Brogan
 */
public class CategoryEntryPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	// Declare FHAES specialized objects
	private static final Logger log = LoggerFactory.getLogger(CategoryEntryPanel.class);

	// Declare FHAES actions
	private FHAESAction actionAddNewCategory;
	private FHAESAction actionRemoveSelectedCategory;

	// Declare local constants
	private final String DEFAULT_CATEGORY_ENTRY_VALUES = "category:content";
	private final String DOUBLE_CLICK_TO_ADD_CATEGORY_ENTRY = "double-click to add new category entry...";

	// Declare local variables
	private final FHSeries workingSeries;
	private JTree categoryTree;
	private ArrayList<FHCategoryEntry> categoryEntries = new ArrayList<FHCategoryEntry>();

	/**
	 * Initializes the panel.
	 * 
	 * @param series
	 */
	public CategoryEntryPanel(FHSeries series) {

		workingSeries = series;
		initActions();
		initGUI();
	}

	/**
	 * Handles initialization of the GUI components.
	 */
	private void initGUI() {

		this.setLayout(new BorderLayout(0, 0));

		// Setup the tree cell editor for use in the category tree
		JTextField textField = new JTextField();
		CategoryTreeCellEditor editor = new CategoryTreeCellEditor(textField);

		// Setup the tree cell renderer with the custom icons
		DefaultTreeCellRenderer renderer = new DefaultTreeCellRenderer();
		renderer.setClosedIcon(Builder.getImageIcon("tree.png"));
		renderer.setOpenIcon(Builder.getImageIcon("tree.png"));
		renderer.setLeafIcon(Builder.getImageIcon("node.png"));

		// Initialize the category tree and its contents
		DefaultMutableTreeNode root = new DefaultMutableTreeNode(workingSeries.getTitle());
		categoryTree = new JTree(root);
		categoryTree.setCellEditor(editor);
		categoryTree.setCellRenderer(renderer);
		categoryTree.setEditable(true);
		categoryTree.setInvokesStopCellEditing(true);
		categoryTree.setToolTipText("Category entries must be in the following format: '" + DEFAULT_CATEGORY_ENTRY_VALUES + "'.");
		categoryTree.getModel().addTreeModelListener(new CategoryTreeModelListener());
		categoryTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		this.add(categoryTree, BorderLayout.CENTER);

		// Setup the popup menu
		JPopupMenu popupMenu = new JPopupMenu();
		addPopup(categoryTree, popupMenu);

		JMenuItem mntmAddNewCategory = new JMenuItem(actionAddNewCategory);
		JMenuItem mntmRemoveSelectedCategory = new JMenuItem(actionRemoveSelectedCategory);

		popupMenu.add(mntmAddNewCategory);
		popupMenu.addSeparator();
		popupMenu.add(mntmRemoveSelectedCategory);

		// Add the nodes to the category tree
		if (!workingSeries.getCategoryEntries().isEmpty())
		{
			ArrayList<FHCategoryEntry> categoriesToAdd = workingSeries.getCategoryEntries();

			for (int i = 0; i < categoriesToAdd.size(); i++)
			{
				String nodeText = categoriesToAdd.get(i).getCategory() + ":" + categoriesToAdd.get(i).getContent();
				root.add(new DefaultMutableTreeNode(nodeText));
			}
		}

		// Add the double-click node to the category tree
		root.add(new DefaultMutableTreeNode(DOUBLE_CLICK_TO_ADD_CATEGORY_ENTRY));

		// Perform initial repopulation of the category entries list
		refreshCategoryEntriesList();
	}

	/**
	 * Initialize the menu and toolbar actions.
	 */
	private void initActions() {

		this.actionAddNewCategory = new FHAESAction("Add new category", "edit_add.png") {

			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent event) {

				DefaultMutableTreeNode nodeToAdd = new DefaultMutableTreeNode(DEFAULT_CATEGORY_ENTRY_VALUES);
				getCategoryTreeModel().insertNodeInto(nodeToAdd, getRootNode(), getIndexOfLastChildNode() - 1);
			}
		};

		this.actionRemoveSelectedCategory = new FHAESAction("Remove selected category", "delete.png") {

			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent event) {

				try
				{
					DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) categoryTree.getLastSelectedPathComponent();

					// Remove the selected node only if it is not the double-click node
					if (selectedNode.getUserObject().toString() != DOUBLE_CLICK_TO_ADD_CATEGORY_ENTRY)
					{
						getCategoryTreeModel().removeNodeFromParent(selectedNode);
					}
				}
				catch (IllegalArgumentException ex)
				{
					log.debug("Cannot delete root node from category tree.");
				}
				catch (NullPointerException ex)
				{
					log.debug("Cannot delete root node from category tree.");
				}
			}
		};
	}

	/**
	 * Collapses all rows on the category tree.
	 */
	public void collapseAllEntries() {

		// Must clear selection before collapsing rows
		categoryTree.clearSelection();

		for (int i = 0; i < categoryTree.getRowCount(); i++)
		{
			categoryTree.collapseRow(i);
		}
	}

	/**
	 * Expands all rows on the category tree.
	 */
	public void expandAllEntries() {

		// Must clear selection before expanding rows
		categoryTree.clearSelection();

		for (int i = 0; i < categoryTree.getRowCount(); i++)
		{
			categoryTree.expandRow(i);
		}
	}

	/**
	 * Compiles and returns a list of all category entries stored in categoryTree.
	 * 
	 * @return an array list of the category entries contained in categoryTree
	 */
	public ArrayList<FHCategoryEntry> getCategoryEntries() {

		return categoryEntries;
	}

	/**
	 * Returns the model of the category tree as a DefaultTreeModel.
	 * 
	 * @return model of categoryTree
	 */
	private DefaultTreeModel getCategoryTreeModel() {

		return (DefaultTreeModel) categoryTree.getModel();
	}

	/**
	 * Returns the root node of the category tree.
	 * 
	 * @return root node of categoryTree
	 */
	private DefaultMutableTreeNode getRootNode() {

		return (DefaultMutableTreeNode) categoryTree.getModel().getRoot();
	}

	/**
	 * Returns the child node at the given index in the category tree.
	 * 
	 * @param index
	 * @return child node at index
	 */
	private DefaultMutableTreeNode getChildNodeAtIndex(int index) {

		return (DefaultMutableTreeNode) categoryTree.getModel().getChild(getRootNode(), index);
	}

	/**
	 * Returns the index of the last child node in the category tree.
	 * 
	 * @return index of the last child node
	 */
	private int getIndexOfLastChildNode() {

		return getRootNode().getChildCount();
	}

	/**
	 * Ensures that exactly one double click node is present in the category tree.
	 */
	private void addDoubleClickNode() {

		boolean treeHasDoubleClickNode = false;

		for (int i = 0; i < getIndexOfLastChildNode(); i++)
		{
			if (getChildNodeAtIndex(i).getUserObject().toString() == DOUBLE_CLICK_TO_ADD_CATEGORY_ENTRY)
			{
				treeHasDoubleClickNode = true;
			}
		}

		if (!treeHasDoubleClickNode)
		{
			DefaultMutableTreeNode nodeToAdd = new DefaultMutableTreeNode(DOUBLE_CLICK_TO_ADD_CATEGORY_ENTRY);
			getCategoryTreeModel().insertNodeInto(nodeToAdd, getRootNode(), getIndexOfLastChildNode());
		}
	}

	/**
	 * Clears and repopulates the categoryEntries list from the tree. This method also ensures that all entries are in a valid format.
	 */
	private void refreshCategoryEntriesList() {

		// Clear the list of category entries so that it may be refreshed
		categoryEntries.clear();

		// Validate all category entries and add them to the list
		for (int i = 0; i < getIndexOfLastChildNode() - 1; i++)
		{
			FHCategoryEntry currentEntry = validateEntryAtIndex(i);

			while (currentEntry == null)
			{
				getChildNodeAtIndex(i).setUserObject(DEFAULT_CATEGORY_ENTRY_VALUES);
				currentEntry = validateEntryAtIndex(i);
			}

			categoryEntries.add(currentEntry);
		}

		// Make sure the root node still displays the correct series title
		getRootNode().setUserObject(workingSeries.getTitle());

		// Make sure to expand all entries so that new node additions are shown
		expandAllEntries();
	}

	/**
	 * Verifies the correctness of the category entry at the given index.
	 * 
	 * @param index
	 * @return the category entry from the given index if it is valid, null otherwise
	 */
	private FHCategoryEntry validateEntryAtIndex(int index) {

		Scanner sc = new Scanner(getChildNodeAtIndex(index).getUserObject().toString());
		sc.useDelimiter(":");

		try
		{
			String category = sc.next(); // Verify the category string exists
			String content = sc.next(); // Verify the content string exists

			if (sc.hasNext())
			{
				sc.close();
				return null;
			}

			sc.close();
			return new FHCategoryEntry(workingSeries.getTitle(), category, content);
		}
		catch (Exception ex)
		{
			sc.close();
			return null;
		}
	}

	/**
	 * Adds a popup menu to the input component.
	 * 
	 * @param component
	 * @param popup
	 */
	private static void addPopup(Component component, final JPopupMenu popup) {

		component.addMouseListener(new MouseAdapter() {

			public void mousePressed(MouseEvent e) {

				if (e.isPopupTrigger())
				{
					showMenu(e);
				}
			}

			public void mouseReleased(MouseEvent e) {

				if (e.isPopupTrigger())
				{
					showMenu(e);
				}
			}

			private void showMenu(MouseEvent e) {

				popup.show(e.getComponent(), e.getX(), e.getY());
			}
		});
	}

	/**
	 * CategoryTreeCellEditor Class.
	 * 
	 * @author Joshua Brogan
	 */
	class CategoryTreeCellEditor extends DefaultCellEditor {

		private static final long serialVersionUID = 1L;

		public CategoryTreeCellEditor(JTextField textField) {

			super(textField);
		}

		@Override
		public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {

			return super.getTableCellEditorComponent(table, value, isSelected, row, column);
		}
	}

	/**
	 * CategoryTreeModelListener Class. Maintains the local categoryEntries list and categoryTree so that they contain a mirrored set of
	 * valid entries.
	 * 
	 * @author Joshua Brogan
	 */
	class CategoryTreeModelListener implements TreeModelListener {

		public void treeNodesChanged(TreeModelEvent e) {

			refreshCategoryEntriesList();
			addDoubleClickNode();
		}

		public void treeNodesInserted(TreeModelEvent e) {

			refreshCategoryEntriesList();
		}

		public void treeNodesRemoved(TreeModelEvent e) {

			refreshCategoryEntriesList();
		}

		public void treeStructureChanged(TreeModelEvent e) {

			refreshCategoryEntriesList();
		}
	}
}
