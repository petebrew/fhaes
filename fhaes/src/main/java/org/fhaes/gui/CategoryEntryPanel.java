package org.fhaes.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;

import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.SwingConstants;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
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
	private final String DEFAULT_CATEGORY_VALUE = "default";
	private final String DEFAULT_CONTENT_VALUE = "new category entry";

	// Declare local variables
	private final CategoryEditor parentEditor;
	private final FHSeries workingSeries;
	private JTree categoryTree;
	private boolean selectedNodeIsBeingEdited = false;
	private DefaultTreeCellRenderer renderer = new DefaultTreeCellRenderer();
	private ArrayList<FHCategoryEntry> categoryEntries = new ArrayList<FHCategoryEntry>();

	/**
	 * Initializes the panel.
	 * 
	 * @param series
	 */
	public CategoryEntryPanel(CategoryEditor editor, FHSeries series) {

		parentEditor = editor;
		workingSeries = series;
		initActions();
		initGUI();
	}

	/**
	 * Handles initialization of the GUI components.
	 */
	private void initGUI() {

		// Set the layout as a borderlayout so it looks nice in windowbuilder
		this.setLayout(new BorderLayout(0, 0));

		// Setup the base panel for displaying the tree and add button
		JPanel basePanel = new JPanel();
		basePanel.setBackground(new Color(255, 255, 255));
		basePanel.setLayout(new BorderLayout(0, 0));
		this.add(basePanel, BorderLayout.CENTER);

		// Setup the tree cell editor for use in the category tree
		JTextField textField = new JTextField();
		CategoryTreeCellEditor editor = new CategoryTreeCellEditor(textField);

		// Setup the tree renderer for displaying custom icons on the category tree
		renderer.setClosedIcon(Builder.getImageIcon("tree.png"));
		renderer.setOpenIcon(Builder.getImageIcon("tree.png"));
		renderer.setLeafIcon(Builder.getImageIcon("node.png"));

		// Setup the category tree
		DefaultMutableTreeNode root = new DefaultMutableTreeNode(workingSeries.getTitle());
		categoryTree = new JTree(root);
		categoryTree.setCellEditor(editor);
		categoryTree.setCellRenderer(renderer);
		categoryTree.setEditable(true);
		categoryTree.setInvokesStopCellEditing(true);
		categoryTree.setToolTipText("Category entries must not contain any commas in order to be valid.");
		categoryTree.getModel().addTreeModelListener(new CategoryTreeModelListener());
		categoryTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		categoryTree.addTreeSelectionListener(new CategoryTreeSelectionListener());
		categoryTree.addMouseMotionListener(new MouseMotionListener() {

			@Override
			public void mouseDragged(MouseEvent mouseEvent) {

				// Do nothing!
			}

			@Override
			public void mouseMoved(MouseEvent mouseEvent) {

				if (!selectedNodeIsBeingEdited)
				{
					int selectedRow = categoryTree.getRowForLocation(mouseEvent.getX(), mouseEvent.getY());
					categoryTree.setSelectionRow(selectedRow);
				}
			}
		});
		basePanel.add(categoryTree, BorderLayout.CENTER);

		// Setup the add-new-category button
		JButton btnAddNewCategory = new JButton(actionAddNewCategory);
		btnAddNewCategory.setHorizontalAlignment(SwingConstants.LEFT);
		basePanel.add(btnAddNewCategory, BorderLayout.SOUTH);

		// Setup the popup menu
		JPopupMenu popupMenu = new JPopupMenu();
		addPopup(categoryTree, popupMenu);
		popupMenu.add(new JMenuItem(actionRemoveSelectedCategory));

		// Add the nodes to the category tree
		if (!workingSeries.getCategoryEntries().isEmpty())
		{
			ArrayList<FHCategoryEntry> categoriesToAdd = workingSeries.getCategoryEntries();

			for (int i = 0; i < categoriesToAdd.size(); i++)
			{
				String nodeText = categoriesToAdd.get(i).getContent();
				root.add(new DefaultMutableTreeNode(nodeText));
			}

			getCategoryTreeModel().reload(root);
		}
		else
		{
			setLeafIconToTree();
		}

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

				DefaultMutableTreeNode nodeToAdd = new DefaultMutableTreeNode(DEFAULT_CONTENT_VALUE);

				if (getRootNodeChildCount() > 0)
				{
					getCategoryTreeModel().insertNodeInto(nodeToAdd, getRootNode(), getRootNodeChildCount());
				}
				else
				{
					// Need to add root manually if root node has no children; this ensures nodes are displayed correctly
					getRootNode().add(nodeToAdd);
					getCategoryTreeModel().reload(getRootNode());
					setLeafIconToBullet();
				}

				// Start editing the new node
				TreePath pathToAddedNode = new TreePath(getChildNodeAtIndex(getRootNodeChildCount() - 1).getPath());
				categoryTree.startEditingAtPath(pathToAddedNode);
				selectedNodeIsBeingEdited = true;
			}
		};

		this.actionRemoveSelectedCategory = new FHAESAction("Remove selected category", "delete.png") {

			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent event) {

				try
				{
					DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) categoryTree.getLastSelectedPathComponent();
					getCategoryTreeModel().removeNodeFromParent(selectedNode);

					if (getRootNodeChildCount() == 0)
					{
						setLeafIconToTree();
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
	 * Updates the leaf icon to appear as a bullet. This is to account for when the tree contains at least one node.
	 */
	private void setLeafIconToBullet() {

		renderer.setLeafIcon(Builder.getImageIcon("node.png"));
	}

	/**
	 * Updates the leaf icon to appear as a tree. This is to account for when the tree contains no nodes.
	 */
	private void setLeafIconToTree() {

		renderer.setLeafIcon(Builder.getImageIcon("tree.png"));
	}

	/**
	 * Clears row selection for the category tree.
	 */
	protected void clearTreeSelectionAndEditing() {

		categoryTree.clearSelection();
		categoryTree.getCellEditor().stopCellEditing();
	}

	/**
	 * Collapses all rows on the category tree.
	 */
	protected void collapseAllEntries() {

		// Must clear selection before collapsing rows, otherwise it does not behave correctly
		categoryTree.clearSelection();

		for (int i = 0; i < categoryTree.getRowCount(); i++)
		{
			categoryTree.collapseRow(i);
		}
	}

	/**
	 * Expands all rows on the category tree.
	 */
	protected void expandAllEntries() {

		// Must clear selection before expanding rows, otherwise it does not behave correctly
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
	protected ArrayList<FHCategoryEntry> getCategoryEntries() {

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
	 * Returns the child node at the given index in the category tree.
	 * 
	 * @param index
	 * @return child node at index
	 */
	private DefaultMutableTreeNode getChildNodeAtIndex(int index) {

		return (DefaultMutableTreeNode) categoryTree.getModel().getChild(getRootNode(), index);
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
	 * Returns the number of children contained by the root node.
	 * 
	 * @return number of children under the root node
	 */
	private int getRootNodeChildCount() {

		return getRootNode().getChildCount();
	}

	/**
	 * Notifies the parent category editor window that a new selection has been made on this panel's category tree.
	 */
	private void notifyParentOfNewSelection() {

		parentEditor.refreshAfterNewSelection(this);
	}

	/**
	 * Clears and repopulates the categoryEntries list from the tree. This method also ensures that all entries are in a valid format.
	 */
	private void refreshCategoryEntriesList() {

		// Clear the list of category entries so that it may be refreshed
		categoryEntries.clear();

		// Validate all category entries and add them to the list
		for (int i = 0; i < getRootNodeChildCount(); i++)
		{
			FHCategoryEntry currentEntry = validateEntryAtIndex(i);

			while (currentEntry == null)
			{
				getChildNodeAtIndex(i).setUserObject(DEFAULT_CONTENT_VALUE);
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

		String entryContent = getChildNodeAtIndex(index).getUserObject().toString();

		if (entryContent.contains(","))
		{
			return null;
		}

		return new FHCategoryEntry(workingSeries.getTitle(), DEFAULT_CATEGORY_VALUE, entryContent);
	}

	/**
	 * Adds a popup menu to the input component.
	 * 
	 * @param component
	 * @param popup
	 */
	private static void addPopup(Component component, final JPopupMenu popup) {

		component.addMouseListener(new MouseAdapter() {

			@Override
			public void mousePressed(MouseEvent e) {

				if (e.isPopupTrigger())
				{
					showMenu(e);
				}
			}

			@Override
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

		@Override
		public void treeNodesChanged(TreeModelEvent e) {

			refreshCategoryEntriesList();
			selectedNodeIsBeingEdited = false;
		}

		@Override
		public void treeNodesInserted(TreeModelEvent e) {

			refreshCategoryEntriesList();
		}

		@Override
		public void treeNodesRemoved(TreeModelEvent e) {

			refreshCategoryEntriesList();
		}

		@Override
		public void treeStructureChanged(TreeModelEvent e) {

			refreshCategoryEntriesList();
		}
	}

	/**
	 * CategoryTreeSelectionListener Class.
	 * 
	 * @author Joshua Brogan
	 */
	class CategoryTreeSelectionListener implements TreeSelectionListener {

		@Override
		public void valueChanged(TreeSelectionEvent e) {

			notifyParentOfNewSelection();
		}
	}
}
