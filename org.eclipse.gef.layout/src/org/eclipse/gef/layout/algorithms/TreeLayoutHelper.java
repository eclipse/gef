/*******************************************************************************
 * Copyright (c) 2005, 2017 The Chisel Group and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 * 
 * Contributors: Ian Bull (The Chisel Group) - initial API and implementation
 *               Mateusz Matela - "Tree Views for Zest" contribution, Google Summer of Code 2009
 *               Matthias Wienand (itemis AG) - refactorings
 *               
 ******************************************************************************/
package org.eclipse.gef.layout.algorithms;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;

import org.eclipse.gef.graph.Node;
import org.eclipse.gef.layout.LayoutContext;

/**
 * A helper class for layout algorithms that are based on tree structure. It
 * keeps track of changes in observed layout context and stores current
 * information about the tree structure - children of each node and several
 * other parameters.
 * 
 * @author Ian Bull
 * @author Mateusz Matela
 * @author mwienand
 */
public class TreeLayoutHelper {

	/**
	 * <code>TreeLayoutObserver</code> uses instance of this class to create
	 * instances of {@link TreeNode}. It may be extended and passed to
	 * <code>TreeLayoutObserver</code>'s constructor in order to build a tree
	 * structure made of <code>TreeNode</code>'s subclasses.
	 */
	public static class TreeNodeFactory {
		/**
		 * Creates a new {@link TreeNode} for the given {@link Node} and
		 * {@link TreeLayoutHelper}.
		 * 
		 * @param nodeLayout
		 *            The {@link Node} that is wrapped.
		 * @param observer
		 *            The {@link TreeLayoutHelper} that initiated the creation.
		 * @return The new {@link TreeNode}.
		 */
		public TreeNode createTreeNode(Node nodeLayout,
				TreeLayoutHelper observer) {
			return new TreeNode(nodeLayout, observer);
		}
	}

	/**
	 * Represents a node in a tree structure and stores all information related
	 * to it. May be subclassed if additional data and behavior is necessary.
	 */
	public static class TreeNode {
		/**
		 * The wrapped {@link Node}.
		 */
		final protected Node node;
		/**
		 * The {@link TreeLayoutHelper} that controls this {@link TreeNode}.
		 */
		final protected TreeLayoutHelper owner;
		/**
		 * The height of the node.
		 */
		protected int height = 0;
		/**
		 * The depth of the node.
		 */
		protected int depth = -1;
		/**
		 * The number of leaves.
		 */
		protected int numOfLeaves = 0;
		/**
		 * The number of descendants.
		 */
		protected int numOfDescendants = 0;
		/**
		 * The order of this node.
		 */
		protected int order = 0;
		/**
		 * The children of this node.
		 */
		protected final List<TreeNode> children = new ArrayList<>();
		/**
		 * The parent of this node.
		 */
		protected TreeNode parent;
		/**
		 * <code>true</code> if this node is the first child, otherwise
		 * <code>false</code>.
		 */
		protected boolean firstChild = false;
		/**
		 * <code>true</code> if this node is the last child, otherwise
		 * <code>false</code>.
		 */
		protected boolean lastChild = false;

		/**
		 * 
		 * @return node layout related to this tree node (null for
		 *         {@link TreeLayoutHelper#getSuperRoot() Super Root})
		 */
		public Node getNode() {
			return node;
		}

		/**
		 * 
		 * @return <code>TreeLayoutObserver</code> owning this tree node
		 */
		public TreeLayoutHelper getOwner() {
			return owner;
		}

		/**
		 * 
		 * @return height of this node in the tree (the longest distance to a
		 *         leaf, 0 for a leaf itself)
		 */
		public int getHeight() {
			return height;
		}

		/**
		 * 
		 * @return depth of this node in the tree (distance from root, 0 for a
		 *         root and -1 for {@link TreeLayoutHelper#getSuperRoot() Super
		 *         Root}
		 */
		public int getDepth() {
			return depth;
		}

		/**
		 * 
		 * @return number of all leaves descending from this node (1 for a leaf
		 *         itself)
		 */
		public int getNumOfLeaves() {
			return numOfLeaves;
		}

		/**
		 * 
		 * @return total number of descendants of this node (0 for leafs)
		 */
		public int getNumOfDescendants() {
			return numOfDescendants;
		}

		/**
		 * Returns order in which nodes are visited during Deep First Search.
		 * Children are visited in the same order as they were added to their
		 * layout context, unless {@link TreeLayoutHelper#computeTree(Node[])}
		 * was called after the nodes were added. In that case the order is
		 * determined by order of nodes returned by
		 * {@link Node#getAllSuccessorNodes()}. Leaves are assigned successive
		 * numbers starting from 0, other nodes have order equal to the smallest
		 * order of their children.
		 * 
		 * @return order of this node
		 */
		public int getOrder() {
			return order;
		}

		/**
		 * 
		 * @return an unmodifiable list of this node's children
		 */
		public List<TreeNode> getChildren() {
			return Collections.unmodifiableList(children);
		}

		/**
		 * 
		 * @return this node's parent
		 */
		public TreeNode getParent() {
			return parent;
		}

		/**
		 * 
		 * @return true if this node is the first child of its parent (has the
		 *         smallest order)
		 */
		public boolean isFirstChild() {
			return firstChild;
		}

		/**
		 * 
		 * @return true if this node is the last child of its parent (has the
		 *         highest order)
		 */
		public boolean isLastChild() {
			return lastChild;
		}

		/**
		 * Creates a tree node related to given layout node
		 * 
		 * @param node
		 *            the layout node
		 * @param owner
		 *            <code>TreeLayoutObserver</code> owning created node
		 */
		protected TreeNode(Node node, TreeLayoutHelper owner) {
			this.node = node;
			this.owner = owner;
		}

		/**
		 * Adds given node to the list of this node's children and set its
		 * parent to this node.
		 * 
		 * @param child
		 *            node to add
		 */
		protected void addChild(TreeNode child) {
			if (child == this)
				return;
			children.add(child);
			child.parent = this;
		}

		/**
		 * Performs a DFS on the tree structure and calculates all parameters of
		 * its nodes. Should be called on {@link TreeLayoutHelper#getSuperRoot()
		 * Super Root}. Uses recurrence to go through all the nodes.
		 */
		protected void precomputeTree() {
			if (children.isEmpty()) {
				height = 0;
				numOfLeaves = 1;
				numOfDescendants = 0;
			} else {
				height = 0;
				numOfLeaves = 0;
				numOfDescendants = 0;
				for (ListIterator<TreeNode> iterator = children
						.listIterator(); iterator.hasNext();) {
					TreeNode child = iterator.next();
					child.depth = this.depth + 1;
					child.order = this.order + this.numOfLeaves;
					child.precomputeTree();
					child.firstChild = (this.numOfLeaves == 0);
					child.lastChild = !iterator.hasNext();

					this.height = Math.max(this.height, child.height + 1);
					this.numOfLeaves += child.numOfLeaves;
					this.numOfDescendants += child.numOfDescendants + 1;
				}
			}
		}

		/**
		 * Finds a node that is the best parent for this node. Add this node as
		 * a child of the found node.
		 */
		protected void findNewParent() {
			if (parent != null)
				parent.children.remove(this);
			Node[] predecessingNodes = node.getAllPredecessorNodes()
					.toArray(new Node[] {});
			parent = null;
			for (int i = 0; i < predecessingNodes.length; i++) {
				TreeNode potentialParent = owner.layoutToTree
						.get(predecessingNodes[i]);
				if (!children.contains(potentialParent)
						&& isBetterParent(potentialParent))
					parent = potentialParent;
			}
			if (parent == null)
				parent = owner.superRoot;

			parent.addChild(this);
		}

		/**
		 * Checks if a potential parent would be better for this node than its
		 * current parent. A better parent has smaller depth (with exception to
		 * {@link TreeLayoutHelper#getSuperRoot() Super Root}, which has depth
		 * equal to -1 but is never a better parent than any other node).
		 * 
		 * @param potentialParent
		 *            potential parent to check
		 * @return true if potentialParent can be a parent of this node and is
		 *         better than its current parent
		 */
		protected boolean isBetterParent(TreeNode potentialParent) {
			if (potentialParent == null)
				return false;
			if (this.parent == null && !this.isAncestorOf(potentialParent))
				return true;
			if (potentialParent.depth <= this.depth
					&& potentialParent.depth != -1)
				return true;
			if (this.parent != null && this.parent.depth == -1
					&& potentialParent.depth >= 0
					&& !this.isAncestorOf(potentialParent))
				return true;
			return false;
		}

		/**
		 * @param descendant
		 *            The {@link TreeNode} in question.
		 * @return true if this node is an ancestor if given descendant node
		 */
		public boolean isAncestorOf(TreeNode descendant) {
			while (descendant.depth > this.depth
					&& descendant != descendant.parent) {
				descendant = descendant.parent;
			}
			return descendant == this;
		}
	}

	/**
	 * A superclass for listeners that can be added to this observer to get
	 * notification whenever the tree structure changes.
	 */
	public static class TreeListener {
		/**
		 * Called when new node is added to the tree structure. The new node
		 * will not have any connections, so it will be a child of
		 * {@link TreeLayoutHelper#getSuperRoot() Super Root}
		 * 
		 * @param newNode
		 *            the added node
		 */
		public void nodeAdded(TreeNode newNode) {
			defaultHandle(newNode);
		}

		/**
		 * Called when a node is removed from the tree structure. The given node
		 * no longer exists in the tree at the moment of call.
		 * 
		 * @param removedNode
		 *            the removed node
		 */
		public void nodeRemoved(TreeNode removedNode) {
			defaultHandle(removedNode);
		}

		/**
		 * Called when a node changes its parent.
		 * 
		 * @param node
		 *            node that changes its parent
		 * @param previousParent
		 *            previous parent of the node
		 */
		public void parentChanged(TreeNode node, TreeNode previousParent) {
			defaultHandle(node);
		}

		/**
		 * A convenience method that can be overridden if a listener reacts the
		 * same way to all events. By default it's called in every event handler
		 * and does nothing.
		 * 
		 * @param changedNode
		 *            the node that has changed
		 */
		protected void defaultHandle(TreeNode changedNode) {
		}
	}

	private final HashMap<Object, TreeNode> layoutToTree = new HashMap<>();
	private final TreeNodeFactory factory;
	private TreeNode superRoot;

	/**
	 * Constructs a new {@link TreeLayoutHelper} for observing the given
	 * {@link LayoutContext}. The given {@link TreeNodeFactory} will be used for
	 * the construction of {@link TreeNode}s. If no factory is supplied, the
	 * {@link TreeNodeFactory} will be used.
	 * 
	 * @param nodeFactory
	 *            The {@link TreeNodeFactory} to use.
	 */
	public TreeLayoutHelper(TreeNodeFactory nodeFactory) {
		if (nodeFactory == null)
			this.factory = new TreeNodeFactory();
		else
			this.factory = nodeFactory;
	}

	/**
	 * Recomputes all the information about the tree structure (the same effect
	 * as creating new <code>TreeLayoutObserver</code>).
	 * 
	 * @param nodes
	 *            nodes
	 */
	public void computeTree(Node[] nodes) {
		superRoot = factory.createTreeNode(null, this);
		layoutToTree.put(null, superRoot);
		createTrees(nodes);
	}

	/**
	 * Returns Super Root, that is an artificial node being a common parent for
	 * all nodes in observed tree structure.
	 * 
	 * @return Super Root
	 */
	protected TreeNode getSuperRoot() {
		return superRoot;
	}

	/**
	 * Returns a {@link TreeNode} related to given node layout. If such a
	 * <code>TreeNode</code> doesn't exist, it's created.
	 * 
	 * @param node
	 *            The {@link Node} for which to return the corresponding
	 *            {@link TreeNode}.
	 * @return The already existing {@link TreeNode} related to the given
	 *         {@link Node} or a newly created one in case there was no related
	 *         {@link TreeNode} before.
	 */
	protected TreeNode getTreeNode(Node node) {
		TreeNode treeNode = layoutToTree.get(node);
		if (treeNode == null) {
			treeNode = factory.createTreeNode(node, this);
			layoutToTree.put(node, treeNode);
		}
		return treeNode;
	}

	/**
	 * Builds a tree structure using BFS method. Created trees are children of
	 * {@link #superRoot}.
	 * 
	 * @param nodes
	 */
	private void createTrees(Node[] nodes) {
		HashSet<Node> alreadyVisited = new HashSet<>();
		LinkedList<Object[]> nodesToAdd = new LinkedList<>();
		for (int i = 0; i < nodes.length; i++) {
			Node root = findRoot(nodes[i], alreadyVisited);
			if (root != null) {
				alreadyVisited.add(root);
				nodesToAdd.addLast(new Object[] { root, superRoot });
			}
		}
		while (!nodesToAdd.isEmpty()) {
			Object[] dequeued = nodesToAdd.removeFirst();
			TreeNode currentNode = factory.createTreeNode((Node) dequeued[0],
					this);
			layoutToTree.put(dequeued[0], currentNode);
			TreeNode currentRoot = (TreeNode) dequeued[1];

			currentRoot.addChild(currentNode);
			Node[] children = currentNode.node.getAllSuccessorNodes()
					.toArray(new Node[] {});
			for (int i = 0; i < children.length; i++) {
				if (!alreadyVisited.contains(children[i])) {
					alreadyVisited.add(children[i]);
					nodesToAdd
							.addLast(new Object[] { children[i], currentNode });
				}
			}
		}
		superRoot.precomputeTree();
	}

	/**
	 * Searches for a root of a tree containing given node by continuously
	 * grabbing a predecessor of current node. If it reaches an node that exists
	 * in alreadyVisited set, it returns null. If it detects a cycle, it returns
	 * the first found node of that cycle. If it reaches a node that has no
	 * predecessors, it returns that node.
	 * 
	 * @param nodeLayout
	 *            starting node
	 * @param alreadyVisited
	 *            set of nodes that can't lay on path to the root (if one does,
	 *            method stops and returns null).
	 * @return
	 */
	private Node findRoot(Node nodeLayout, Set<Node> alreadyVisited) {
		HashSet<Node> alreadyVisitedRoot = new HashSet<>();
		while (true) {
			if (alreadyVisited.contains(nodeLayout))
				return null;
			if (alreadyVisitedRoot.contains(nodeLayout))
				return nodeLayout;
			alreadyVisitedRoot.add(nodeLayout);
			Node[] predecessingNodes = nodeLayout.getAllPredecessorNodes()
					.toArray(new Node[] {});
			if (predecessingNodes.length > 0) {
				nodeLayout = predecessingNodes[0];
			} else {
				return nodeLayout;
			}
		}
	}
}
