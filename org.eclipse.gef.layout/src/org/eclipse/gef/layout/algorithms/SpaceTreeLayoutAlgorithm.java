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
 *               Alexaner Nyßen (itemis AG) - refactorings (bug #469472)
 ******************************************************************************/
package org.eclipse.gef.layout.algorithms;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import org.eclipse.gef.geometry.planar.Dimension;
import org.eclipse.gef.geometry.planar.Point;
import org.eclipse.gef.geometry.planar.Rectangle;
import org.eclipse.gef.graph.Node;
import org.eclipse.gef.layout.ILayoutAlgorithm;
import org.eclipse.gef.layout.LayoutContext;
import org.eclipse.gef.layout.LayoutProperties;
import org.eclipse.gef.layout.algorithms.TreeLayoutHelper.TreeNode;

/**
 * Layout algorithm implementing SpaceTree. It assumes that nodes in the layout
 * context make a tree structure.
 * 
 * It expands and collapses nodes to optimize use of available space. In order
 * to keep the tree structure clearly visible, it also keeps track of the nodes'
 * positions to makes sure they stay in their current layer and don't overlap
 * with each other.
 * 
 * @author Ian Bull
 * @author Mateusz Matela
 * @author mwienand
 */
public class SpaceTreeLayoutAlgorithm implements ILayoutAlgorithm {

	/**
	 * Tree direction constant for which root is placed at the top and branches
	 * spread downwards
	 */
	public final static int TOP_DOWN = 1;

	/**
	 * Tree direction constant for which root is placed at the bottom and
	 * branches spread upwards
	 */
	public final static int BOTTOM_UP = 2;

	/**
	 * Tree direction constant for which root is placed at the left and branches
	 * spread to the right
	 */
	public final static int LEFT_RIGHT = 3;

	/**
	 * Tree direction constant for which root is placed at the right and
	 * branches spread to the left
	 */
	public final static int RIGHT_LEFT = 4;

	private class SpaceTreeNode extends TreeLayoutHelper.TreeNode {
		public boolean expanded = true;
		public double positionInLayer;

		public SpaceTreeNode(Node node, TreeLayoutHelper owner) {
			super(node, owner);
		}

		protected void addChild(TreeLayoutHelper.TreeNode child) {
			super.addChild(child);

			SpaceTreeNode child2 = (SpaceTreeNode) child;
			child2.expanded = false;

			if (child.depth >= 0)
				spaceTreeLayers.get(child.depth).removeNode(child2);

			if (expanded) {
				child.depth = this.depth + 1;

				SpaceTreeLayer childLayer;
				if (child.depth < spaceTreeLayers.size())
					childLayer = spaceTreeLayers.get(child.depth);
				else
					spaceTreeLayers
							.add(childLayer = new SpaceTreeLayer(child.depth));

				if (childLayer.nodes.isEmpty())
					child.order = 0;
				else
					child.order = childLayer.nodes
							.get(childLayer.nodes.size() - 1).order + 1;
				childLayer.addNodes(Arrays.asList(child));
			}
		}

		public void precomputeTree() {
			super.precomputeTree();
			if (this == owner.getSuperRoot()) {
				expanded = true;
				while (spaceTreeLayers.size() <= this.height)
					spaceTreeLayers
							.add(new SpaceTreeLayer(spaceTreeLayers.size()));
			}
		}

		/**
		 * Moves the node back to its layer, as close as possible to given
		 * preferred location.
		 * 
		 * @param preferredLocation
		 *            The location to which the node should be moved.
		 */
		public void adjustPosition(Point preferredLocation) { // !
			protectedNode = (SpaceTreeNode) owner.getSuperRoot();

			double newPositionInLayer = (direction == BOTTOM_UP
					|| direction == TOP_DOWN) ? preferredLocation.x
							: preferredLocation.y;
			if (((SpaceTreeNode) parent).expanded) {
				spaceTreeLayers.get(depth).moveNode(this, newPositionInLayer);
				centerParentsTopDown();
			}
		}

		public double spaceRequiredForNode() {
			if (node == null)
				return 0;
			switch (direction) {
			case TOP_DOWN:
			case BOTTOM_UP:
				return LayoutProperties.getSize(node).width;
			case LEFT_RIGHT:
			case RIGHT_LEFT:
				return LayoutProperties.getSize(node).height;
			}
			throw new RuntimeException("invalid direction");
		}

		public double spaceRequiredForChildren() {
			if (children.isEmpty())
				return 0;
			double result = 0;
			for (Iterator<TreeNode> iterator = children.iterator(); iterator
					.hasNext();) {
				SpaceTreeNode child = (SpaceTreeNode) iterator.next();
				result += child.spaceRequiredForNode();
			}
			result += leafGap * (children.size() - 1);
			return result;
		}

		/**
		 * Checks if nodes in given list have proper positions according to
		 * their children (a parent's position cannot be smaller than its first
		 * child's position nor bigger than its last child's position). If not,
		 * it tries to fix them.
		 * 
		 * @param nodesToCheck
		 *            An {@link ArrayList} of the {@link TreeNode}s that are
		 *            checked for proper positions.
		 * @return true if all locations are correct or could be corrected while
		 *         checking.
		 */
		public boolean childrenPositionsOK(ArrayList<TreeNode> nodesToCheck) {
			for (Iterator<TreeNode> iterator = nodesToCheck.iterator(); iterator
					.hasNext();) {
				SpaceTreeNode node = (SpaceTreeNode) iterator.next();
				if (node.depth < 0 || node.children.isEmpty())
					continue;
				SpaceTreeNode child = ((SpaceTreeNode) node.children.get(0));
				if (child.positionInLayer > node.positionInLayer) {
					spaceTreeLayers.get(node.depth).moveNode(node,
							child.positionInLayer);
					if (child.positionInLayer > node.positionInLayer) {
						spaceTreeLayers.get(child.depth).moveNode(child,
								node.positionInLayer);
						if (child.positionInLayer > node.positionInLayer) {
							return false;
						}
					}
				}
				child = ((SpaceTreeNode) node.children
						.get(node.children.size() - 1));
				if (child.positionInLayer < node.positionInLayer) {
					spaceTreeLayers.get(node.depth).moveNode(node,
							child.positionInLayer);
					if (child.positionInLayer < node.positionInLayer) {
						spaceTreeLayers.get(child.depth).moveNode(child,
								node.positionInLayer);
						if (child.positionInLayer < node.positionInLayer) {
							return false;
						}
					}
				}
			}
			return true;
		}

		public void centerParentsBottomUp() {
			if (!children.isEmpty() && expanded) {
				for (Iterator<TreeNode> iterator = children.iterator(); iterator
						.hasNext();) {
					((SpaceTreeNode) iterator.next()).centerParentsBottomUp();
				}

				if (depth >= 0) {
					SpaceTreeNode firstChild = (SpaceTreeNode) children.get(0);
					SpaceTreeNode lastChild = (SpaceTreeNode) children
							.get(children.size() - 1);
					SpaceTreeLayer layer = spaceTreeLayers.get(depth);
					layer.moveNode(this, (firstChild.positionInLayer
							+ lastChild.positionInLayer) / 2);
				}
			}
		}

		public void centerParentsTopDown() {
			if (this == owner.getSuperRoot()) {
				this.positionInLayer = getAvailableSpace() / 2;
			}
			if (!children.isEmpty() && expanded) {
				SpaceTreeNode firstChild = (SpaceTreeNode) children.get(0);
				SpaceTreeNode lastChild = (SpaceTreeNode) children
						.get(children.size() - 1);
				double offset = this.positionInLayer
						- (firstChild.positionInLayer
								+ lastChild.positionInLayer) / 2;
				if (firstChild.positionInLayer
						- firstChild.spaceRequiredForNode() / 2 + offset < 0)
					offset = -firstChild.positionInLayer
							+ firstChild.spaceRequiredForNode() / 2;
				double availableSpace = getAvailableSpace();
				if (lastChild.positionInLayer
						+ lastChild.spaceRequiredForNode() / 2
						+ offset > availableSpace) {
					offset = availableSpace - lastChild.positionInLayer
							- lastChild.spaceRequiredForNode() / 2;
				}
				SpaceTreeLayer layer = spaceTreeLayers.get(depth + 1);
				layer.fitNodesWithinBounds(children,
						firstChild.positionInLayer + offset,
						lastChild.positionInLayer + offset);

				for (Iterator<TreeNode> iterator = children.iterator(); iterator
						.hasNext();) {
					((SpaceTreeNode) iterator.next()).centerParentsTopDown();
				}
			}
		}

		public void flushExpansionChanges() {
			if (this.expanded) {
				for (Iterator<TreeNode> iterator = children.iterator(); iterator
						.hasNext();) {
					((SpaceTreeNode) iterator.next()).flushExpansionChanges();
				}
			}
		}

		/**
		 * Sets locations of nodes in the graph depending on their current layer
		 * and position in layer.
		 * 
		 * @param thicknessSoFar
		 *            sum of thicknesses and gaps for all layers 'above' this
		 *            node (should be 0 if called on superRoot)
		 * @return true if location of at least one node has changed
		 */
		public boolean flushLocationChanges(double thicknessSoFar) {
			boolean madeChanges = false;
			if (node != null) {
				Dimension nodeSize = LayoutProperties.getSize(node);
				double x = 0, y = 0;
				switch (direction) {
				case TOP_DOWN:
					x = bounds.getX() + positionInLayer;
					y = thicknessSoFar + nodeSize.height / 2;
					break;
				case BOTTOM_UP:
					x = bounds.getX() + positionInLayer;
					y = bounds.getY() + bounds.getHeight() - thicknessSoFar
							- nodeSize.height / 2;
					break;
				case LEFT_RIGHT:
					x = thicknessSoFar + nodeSize.height / 2;
					y = bounds.getY() + positionInLayer;
					break;
				case RIGHT_LEFT:
					x = bounds.getX() + bounds.getWidth() - thicknessSoFar
							- nodeSize.height / 2;
					y = bounds.getY() + positionInLayer;
					break;
				}
				Point currentLocation = LayoutProperties.getLocation(node);
				if (currentLocation.x != x || currentLocation.y != y) {
					LayoutProperties.setLocation(node, new Point(x, y));
					SpaceTreeNode spaceTreeNode = (SpaceTreeNode) treeObserver
							.getTreeNode(node);
					spaceTreeNode
							.adjustPosition(LayoutProperties.getLocation(node));
					spaceTreeLayers.get(depth).refreshThickness();
					madeChanges = true;
				}
			}
			if (expanded) {
				thicknessSoFar += (depth >= 0
						? spaceTreeLayers.get(depth).thickness : 0) + layerGap;
				for (Iterator<TreeNode> iterator = children.iterator(); iterator
						.hasNext();) {
					SpaceTreeNode child = (SpaceTreeNode) iterator.next();
					madeChanges = child.flushLocationChanges(thicknessSoFar)
							|| madeChanges;
				}
			}
			return madeChanges;
		}

		public String toString() {
			StringBuffer sb = new StringBuffer();
			for (int i = 0; i < depth; i++)
				sb.append(" ");
			if (node != null)
				sb.append(node.toString());
			sb.append("|" + this.order);
			sb.append('\n');
			for (Iterator<TreeNode> iterator = children.iterator(); iterator
					.hasNext();) {
				SpaceTreeNode child = (SpaceTreeNode) iterator.next();
				sb.append(child.toString());
			}
			return sb.toString();
		}
	}

	private TreeLayoutHelper.TreeNodeFactory spaceTreeNodeFactory = new TreeLayoutHelper.TreeNodeFactory() {
		public TreeLayoutHelper.TreeNode createTreeNode(Node nodeLayout,
				TreeLayoutHelper observer) {
			return new SpaceTreeNode(nodeLayout, observer);
		};
	};

	private class SpaceTreeLayer {
		public ArrayList<SpaceTreeNode> nodes = new ArrayList<>();
		private final int depth;
		public double thickness = 0;

		public SpaceTreeLayer(int depth) {
			this.depth = depth;
		}

		public void addNodes(List<TreeNode> nodesToAdd) {
			ListIterator<SpaceTreeNode> layerIterator = nodes.listIterator();
			SpaceTreeNode previousNode = null;
			for (Iterator<TreeNode> iterator = nodesToAdd.iterator(); iterator
					.hasNext();) {
				SpaceTreeNode nodeToAdd = (SpaceTreeNode) iterator.next();

				SpaceTreeNode nodeInLayer = null;
				while (layerIterator.hasNext()) {
					nodeInLayer = layerIterator.next();
					if (nodeInLayer.order >= nodeToAdd.order)
						break;
					double expectedPostion = (previousNode == null) ? 0
							: previousNode.positionInLayer + expectedDistance(
									previousNode, nodeInLayer);
					nodeInLayer.positionInLayer = Math
							.max(nodeInLayer.positionInLayer, expectedPostion);
					previousNode = nodeInLayer;
				}

				if (nodeInLayer == null) {
					layerIterator.add(nodeToAdd);
				} else if (nodeInLayer.order == nodeToAdd.order) {
					layerIterator.set(nodeToAdd);
				} else {
					if (nodeInLayer.order > nodeToAdd.order)
						layerIterator.previous();
					layerIterator.add(nodeToAdd);
				}
				layerIterator.previous();
			}
			// move the rest of nodes so that they don't overlap
			while (layerIterator.hasNext()) {
				SpaceTreeNode nodeInLayer = layerIterator.next();
				double expectedPostion = (previousNode == null) ? 0
						: previousNode.positionInLayer
								+ expectedDistance(previousNode, nodeInLayer);
				nodeInLayer.positionInLayer = Math
						.max(nodeInLayer.positionInLayer, expectedPostion);
				previousNode = nodeInLayer;
			}

			refreshThickness();
		}

		public void removeNode(SpaceTreeNode node) {
			if (nodes.remove(node)) {
				spaceTreeLayers.get(depth + 1).removeNodes(node.children);
				refreshThickness();
			}
		}

		public void removeNodes(List<TreeNode> nodesToRemove) {
			if (this.nodes.removeAll(nodesToRemove)) {
				SpaceTreeLayer nextLayer = spaceTreeLayers.get(depth + 1);
				for (Iterator<TreeNode> iterator = nodesToRemove
						.iterator(); iterator.hasNext();) {
					SpaceTreeNode nodeToRemove = (SpaceTreeNode) iterator
							.next();
					nextLayer.removeNodes(nodeToRemove.children);
				}
				refreshThickness();
			}
		}

		public void checkThickness(SpaceTreeNode node) {
			double nodeThickness = 0;
			Dimension size = LayoutProperties.getSize(node.node);
			nodeThickness = (direction == TOP_DOWN || direction == BOTTOM_UP)
					? size.height : size.width;
			this.thickness = Math.max(this.thickness, nodeThickness);
		}

		public void refreshThickness() {
			this.thickness = 0;
			for (Iterator<SpaceTreeNode> iterator = nodes.iterator(); iterator
					.hasNext();) {
				checkThickness(iterator.next());
			}
		}

		public void fitNodesWithinBounds(List<TreeNode> nodeList,
				double startPosition, double endPosition) {
			NodeSnapshot[][] snapShot = takeSnapShot();
			SpaceTreeNode[] nodes = nodeList
					.toArray(new SpaceTreeNode[nodeList.size()]);
			double initialStartPosition = nodes[0].positionInLayer;
			double initialNodesBredth = nodes[nodes.length - 1].positionInLayer
					- initialStartPosition;
			double[] desiredPositions = new double[nodes.length];
			// calculate desired positions for every node, regarding their
			// initial initial proportions
			for (int i = 0; i < nodes.length; i++) {
				double initialPositionAsPercent = (initialNodesBredth > 0)
						? (nodes[i].positionInLayer - initialStartPosition)
								/ initialNodesBredth
						: 0;
				desiredPositions[i] = initialPositionAsPercent
						* (endPosition - startPosition);
			}
			// make sure there's proper distance between each pair of
			// consecutive nodes
			for (int i = 1; i < nodes.length; i++) {
				SpaceTreeNode node = nodes[i];
				SpaceTreeNode previousNode = nodes[i - 1];
				double expectedDistance = expectedDistance(previousNode, node);
				if (desiredPositions[i]
						- desiredPositions[i - 1] < expectedDistance) {
					desiredPositions[i] = desiredPositions[i - 1]
							+ expectedDistance;
				}
			}
			// if the above operation caused some nodes to fall out of requested
			// bounds, push them back
			if (desiredPositions[nodes.length - 1] > (endPosition
					- startPosition)) {
				desiredPositions[nodes.length - 1] = (endPosition
						- startPosition);
				for (int i = nodes.length - 1; i > 0; i--) {
					SpaceTreeNode node = nodes[i];
					SpaceTreeNode previousNode = nodes[i - 1];
					double expectedDistance = expectedDistance(previousNode,
							node);
					if (desiredPositions[i]
							- desiredPositions[i - 1] < expectedDistance) {
						desiredPositions[i - 1] = desiredPositions[i]
								- expectedDistance;
					} else
						break;
				}
			}

			int maxCount = nodeList.size() * 100;
			int totalCount = 0;
			for (int i = 0; i < nodeList.size(); i++) {
				// Stop this cycle if no result can be found
			    // Possible cause: lack of space to lay out nodes without overlapping
			    totalCount++;
				if (totalCount > maxCount) {
					break;
				}
				SpaceTreeNode node = (SpaceTreeNode) nodeList.get(i);
				double desiredPosition = startPosition + desiredPositions[i];
				moveNode(node, desiredPosition);
				if (Math.abs(node.positionInLayer - desiredPosition) > 0.5) {
					startPosition += (node.positionInLayer - desiredPosition);
					i = -1;
					revertToSnapshot(snapShot);
				}
			}
		}

		public void moveNode(SpaceTreeNode node, double newPosition) {
			Collections.sort(nodes, new Comparator<SpaceTreeNode>() {
				public int compare(SpaceTreeNode arg0, SpaceTreeNode arg1) {
					return arg0.order - arg1.order;
				}
			});
			double positionInLayerAtStart = node.positionInLayer;
			if (newPosition >= positionInLayerAtStart)
				moveNodeForward(node, newPosition);
			if (newPosition <= positionInLayerAtStart)
				moveNodeBackward(node, newPosition);
		}

		/**
		 * Tries to increase node's position in layer. It can move a node only
		 * if it doesn't cause nodes to fall out of available space (see
		 * {@link SpaceTreeLayoutAlgorithm#getAvailableSpace()}. If there's not
		 * enough space available, some nodes may be collapsed to increase it as
		 * long as it doesn't cause
		 * {@link SpaceTreeLayoutAlgorithm#protectedNode} or any of its
		 * descendants to be collapsed.
		 * 
		 * @param nodeToMove
		 * @param newPosition
		 */
		private void moveNodeForward(SpaceTreeNode nodeToMove,
				double newPosition) {
			int nodeIndex = nodes.indexOf(nodeToMove);
			if (nodeIndex == -1)
				throw new IllegalArgumentException("node not on this layer");
			// move forward -> check space to the 'right'
			NodeSnapshot[][] snapShot = takeSnapShot();
			boolean firstRun = true;
			mainLoop: while (firstRun
					|| nodeToMove.positionInLayer < newPosition) {
				firstRun = false;
				double requiredSpace = 0;
				SpaceTreeNode previousNode = nodeToMove;
				for (int i = nodeIndex + 1; i < nodes.size(); i++) {
					SpaceTreeNode nextNode = nodes.get(i);
					requiredSpace += expectedDistance(previousNode, nextNode);
					previousNode = nextNode;
				}
				requiredSpace += previousNode.spaceRequiredForNode() / 2;
				if (requiredSpace > getAvailableSpace() - newPosition) {
					// find nodes to remove
					boolean removed = false;
					for (int i = nodeIndex; i < nodes.size(); i++) {
						SpaceTreeNode nextNode = nodes.get(i);
						if (protectedNode == null
								|| (!protectedNode.isAncestorOf(nextNode)
										&& !nextNode.parent
												.isAncestorOf(protectedNode))) {
							collapseNode((SpaceTreeNode) nextNode.parent);
							if (nextNode.parent == nodeToMove.parent)
								break mainLoop;
							removed = true;
							break;
						}
					}
					if (!removed) {
						// not enough space, but we can't collapse anything...
						newPosition = getAvailableSpace() - requiredSpace;
						revertToSnapshot(snapShot);
						continue mainLoop;
					}
				}
				// move the node and all its neighbors to the 'right'
				SpaceTreeNode currentNodeToMove = nodeToMove;
				double newPositionForCurrent = newPosition;
				for (int i = nodeIndex; i < nodes.size(); i++) {
					currentNodeToMove.positionInLayer = newPositionForCurrent;
					// move parent if moved node is its first child
					if (currentNodeToMove.firstChild) {
						SpaceTreeNode parent = (SpaceTreeNode) currentNodeToMove.parent;
						if (depth > 0
								&& parent.positionInLayer <= newPositionForCurrent) {
							SpaceTreeLayer parentLayer = spaceTreeLayers
									.get(depth - 1);
							parentLayer.moveNodeForward(parent,
									newPositionForCurrent);
							if (parent.positionInLayer < newPositionForCurrent) {
								double delta = newPositionForCurrent
										- parent.positionInLayer;
								newPosition -= delta;
								revertToSnapshot(snapShot);
								continue mainLoop;
							}
						}
					}
					// move children if necessary
					if (currentNodeToMove.expanded
							&& !currentNodeToMove.children.isEmpty()) {
						SpaceTreeNode lastChild = (SpaceTreeNode) currentNodeToMove.children
								.get(currentNodeToMove.children.size() - 1);
						if (lastChild.positionInLayer < newPositionForCurrent) {
							// try to move all the children, that is move the
							// first child and the rest will be pushed
							SpaceTreeNode firstChild = (SpaceTreeNode) currentNodeToMove.children
									.get(0);
							SpaceTreeLayer childLayer = spaceTreeLayers
									.get(depth + 1);
							double expectedDistanceBetweenChildren = currentNodeToMove
									.spaceRequiredForChildren()
									- firstChild.spaceRequiredForNode() / 2
									- lastChild.spaceRequiredForNode() / 2;
							childLayer.moveNodeForward(firstChild,
									newPositionForCurrent
											- expectedDistanceBetweenChildren);
							if (currentNodeToMove.expanded
									&& lastChild.positionInLayer < newPositionForCurrent) {
								// the previous attempt failed -> try to move
								// only the last child
								childLayer.moveNodeForward(lastChild,
										newPositionForCurrent);
								if (lastChild.positionInLayer < newPositionForCurrent) {
									// child couldn't be moved as far as needed
									// -> move current node back to the position
									// over the child
									double delta = newPositionForCurrent
											- lastChild.positionInLayer;
									newPosition -= delta;
									revertToSnapshot(snapShot);
									continue mainLoop;
								}
							}
						}
					}

					if (i < nodes.size() - 1) {
						SpaceTreeNode nextNode = nodes.get(i + 1);
						newPositionForCurrent += expectedDistance(
								currentNodeToMove, nextNode);
						currentNodeToMove = nextNode;
						if (currentNodeToMove.positionInLayer > newPositionForCurrent)
							break;
					}
				}
			}
		}

		/**
		 * Method complementary to
		 * {@link #moveNodeForward(SpaceTreeNode, double)}. Decreases node's
		 * position in layer.
		 * 
		 * @param nodeToMove
		 * @param newPosition
		 */
		private void moveNodeBackward(SpaceTreeNode nodeToMove,
				double newPosition) {
			int nodeIndex = nodes.indexOf(nodeToMove);
			if (nodeIndex == -1)
				throw new IllegalArgumentException("node not on this layer");
			// move backward -> check space to the 'left'
			// move and collapse until there's enough space
			NodeSnapshot[][] snapShot = takeSnapShot();
			boolean firstRun = true;
			mainLoop: while (firstRun
					|| nodeToMove.positionInLayer > newPosition) {
				firstRun = false;
				double requiredSpace = 0;
				SpaceTreeNode previousNode = nodeToMove;
				for (int i = nodeIndex - 1; i >= 0; i--) {
					SpaceTreeNode nextNode = nodes.get(i);
					requiredSpace += expectedDistance(previousNode, nextNode);
					previousNode = nextNode;
				}
				requiredSpace += previousNode.spaceRequiredForNode() / 2;
				if (requiredSpace > newPosition) {
					// find nodes to remove
					boolean removed = false;
					for (int i = nodeIndex; i >= 0; i--) {
						SpaceTreeNode nextNode = nodes.get(i);
						if (protectedNode == null
								|| (!protectedNode.isAncestorOf(nextNode)
										&& !nextNode.parent
												.isAncestorOf(protectedNode))) {
							collapseNode((SpaceTreeNode) nextNode.parent);
							if (nextNode.parent == nodeToMove.parent)
								break mainLoop;
							nodeIndex -= nextNode.parent.children.size();
							removed = true;
							break;
						}
					}
					if (!removed) {
						// not enough space, but we can't collapse anything...
						newPosition = requiredSpace;
						revertToSnapshot(snapShot);
						continue mainLoop;
					}
				}
				// move the node and all its neighbors to the 'left'
				SpaceTreeNode currentNodeToMove = nodeToMove;
				double newPositionForCurrent = newPosition;
				for (int i = nodeIndex; i >= 0; i--) {
					currentNodeToMove.positionInLayer = newPositionForCurrent;
					// move parent if moved node is its last child
					if (currentNodeToMove.lastChild) {
						SpaceTreeNode parent = (SpaceTreeNode) currentNodeToMove.parent;
						if (depth > 0
								&& parent.positionInLayer >= newPositionForCurrent) {
							SpaceTreeLayer parentLayer = spaceTreeLayers
									.get(depth - 1);
							parentLayer.moveNodeBackward(parent,
									newPositionForCurrent);
							if (parent.positionInLayer > newPositionForCurrent) {
								double delta = parent.positionInLayer
										- newPositionForCurrent;
								newPosition += delta;
								revertToSnapshot(snapShot);
								continue mainLoop;
							}
						}
					}
					// move children if necessary
					if (currentNodeToMove.expanded
							&& !currentNodeToMove.children.isEmpty()) {
						SpaceTreeNode firstChild = (SpaceTreeNode) currentNodeToMove.children
								.get(0);
						if (firstChild.positionInLayer > newPositionForCurrent) {
							// try to move all the children, that is move the
							// last child and the rest will be pushed
							SpaceTreeNode lastChild = (SpaceTreeNode) currentNodeToMove.children
									.get(currentNodeToMove.children.size() - 1);
							SpaceTreeLayer childLayer = spaceTreeLayers
									.get(depth + 1);
							double expectedDistanceBetweenChildren = currentNodeToMove
									.spaceRequiredForChildren()
									- firstChild.spaceRequiredForNode() / 2
									- lastChild.spaceRequiredForNode() / 2;
							childLayer.moveNodeBackward(lastChild,
									newPositionForCurrent
											+ expectedDistanceBetweenChildren);
							if (currentNodeToMove.expanded
									&& firstChild.positionInLayer > newPositionForCurrent) {
								// the previous attempt failed -> try to move
								// only the first child
								childLayer.moveNodeBackward(firstChild,
										newPositionForCurrent);
								if (firstChild.positionInLayer > newPositionForCurrent) {
									// child couldn't be moved as far as needed
									// -> move current node back to the position
									// over the child
									double delta = firstChild.positionInLayer
											- newPositionForCurrent;
									newPosition += delta;
									revertToSnapshot(snapShot);
									continue mainLoop;
								}
							}
						}
					}
					if (i > 0) {
						SpaceTreeNode nextNode = nodes.get(i - 1);
						newPositionForCurrent -= expectedDistance(
								currentNodeToMove, nextNode);
						currentNodeToMove = nextNode;
						if (currentNodeToMove.positionInLayer < newPositionForCurrent)
							break;
					}
				}
			}
		}

		public String toString() {
			StringBuffer buffer = new StringBuffer();
			buffer.append("Layer ").append(depth).append(": ");
			for (Iterator<SpaceTreeNode> iterator = nodes.iterator(); iterator
					.hasNext();) {
				SpaceTreeNode node = iterator.next();
				buffer.append(node.node).append(", ");
			}
			return buffer.toString();
		}

		private void collapseNode(SpaceTreeNode node) {
			node.expanded = false;
			SpaceTreeLayer layer = spaceTreeLayers.get(node.depth + 1);
			layer.removeNodes(node.children);
			for (Iterator<TreeNode> iterator = node.children
					.iterator(); iterator.hasNext();) {
				SpaceTreeNode child = (SpaceTreeNode) iterator.next();
				if (child.expanded)
					collapseNode(child);
			}
		}
	}

	private int direction = TOP_DOWN;

	private double leafGap = 15;
	private double branchGap = leafGap + 5;
	private double layerGap = 20;

	/**
	 * Sets the distance between leaf nodes to the given value. Default value is
	 * 15.
	 * 
	 * @param value
	 *            The new distance between leaf nodes.
	 * 
	 */
	public void setLeafGap(double value) {
		this.leafGap = value;
	}

	/**
	 * Sets the distance between branches to the given value. Default value is
	 * 20.
	 * 
	 * @param value
	 *            The new distance between branches.
	 * 
	 */
	public void setBranchGap(double value) {
		this.branchGap = value;
	}

	/**
	 * Sets the distance between layers to the given value. Default value is 20.
	 * 
	 * @param value
	 *            The new distance between layers.
	 * 
	 */
	public void setLayerGap(double value) {
		this.layerGap = value;
	}

	/**
	 * Returns the distance between leaf nodes. Default value is 15.
	 * 
	 * @return The distance between leaf nodes.
	 */
	public double getLeafGap() {
		return this.leafGap;
	}

	/**
	 * Returns the distance between branches. Default value is 20.
	 * 
	 * @return The distance between branches.
	 */
	public double getBranchGap() {
		return this.branchGap;
	}

	/**
	 * Returns the distance between layers. Default value is 20.
	 * 
	 * @return The distance between layers.
	 */
	public double getLayerGap() {
		return this.layerGap;
	}

	private TreeLayoutHelper treeObserver;
	private double availableSpace;
	private ArrayList<SpaceTreeLayer> spaceTreeLayers = new ArrayList<>();

	/**
	 * If not null, this node and all of its children shall not be collapsed
	 * during node movements.
	 */
	private SpaceTreeNode protectedNode = null;

	private Rectangle bounds;

	/**
	 * Constructs an instance of <code>SpaceTreeLayoutAlgorithm</code> that
	 * places the root of a tree at the top of the graph.
	 */
	public SpaceTreeLayoutAlgorithm() {
	}

	/**
	 * Constructs an instance of <code>SpaceTreeLayoutAlgorithm</code> that
	 * places the root of a tree according to given direction
	 * 
	 * @param direction
	 *            direction of the tree, sould be one of the following:
	 *            {@link #TOP_DOWN}, {@link #BOTTOM_UP}, {@link #LEFT_RIGHT},
	 *            {@link #RIGHT_LEFT}.
	 */
	public SpaceTreeLayoutAlgorithm(int direction) {
		setDirection(direction);
	}

	/**
	 * 
	 * @return current direction (placement) of the tree
	 */
	public int getDirection() {
		return direction;
	}

	/**
	 * Sets direction (placement) of the tree
	 * 
	 * @param direction
	 *            direction of the tree, sould be one of the following:
	 *            {@link #TOP_DOWN}, {@link #BOTTOM_UP}, {@link #LEFT_RIGHT},
	 *            {@link #RIGHT_LEFT}.
	 */
	public void setDirection(int direction) {
		if (direction == this.direction)
			return;
		if (direction == TOP_DOWN || direction == BOTTOM_UP
				|| direction == LEFT_RIGHT || direction == RIGHT_LEFT) {
			this.direction = direction;
		} else
			throw new IllegalArgumentException(
					"Invalid direction: " + direction);
	}

	public void applyLayout(LayoutContext layoutContext, boolean clean) {
		bounds = LayoutProperties.getBounds(layoutContext.getGraph());
		if (bounds.isEmpty()) {
			return;
		}

		treeObserver = new TreeLayoutHelper(spaceTreeNodeFactory);
		treeObserver.computeTree(layoutContext.getNodes());

		if (clean) {
			maximizeExpansion((SpaceTreeNode) treeObserver.getSuperRoot());
		}

		SpaceTreeNode superRoot = ((SpaceTreeNode) treeObserver.getSuperRoot());
		superRoot.flushExpansionChanges();
		superRoot.flushLocationChanges(0);
	}

	private void maximizeExpansion(SpaceTreeNode nodeToExpand) {
		protectedNode = nodeToExpand;
		double availableSpace = getAvailableSpace();
		double requiredSpace = 0;

		spaceTreeLayers.get(nodeToExpand.depth + 1)
				.removeNodes(nodeToExpand.children);

		ArrayList<TreeNode> nodesInThisLayer = null;
		ArrayList<TreeNode> nodesInNextLayer = new ArrayList<>();
		nodesInNextLayer.add(nodeToExpand);
		double spaceRequiredInNextLayer = nodeToExpand.spaceRequiredForNode();
		for (int layer = 0; !nodesInNextLayer.isEmpty(); layer++) {
			NodeSnapshot[][] snapShot = takeSnapShot();
			requiredSpace = Math.max(requiredSpace, spaceRequiredInNextLayer);
			spaceRequiredInNextLayer = 0;

			nodesInThisLayer = nodesInNextLayer;
			nodesInNextLayer = new ArrayList<>();

			int numOfNodesWithChildren = 0;
			for (Iterator<TreeNode> iterator = nodesInThisLayer
					.iterator(); iterator.hasNext();) {
				SpaceTreeNode node = (SpaceTreeNode) iterator.next();
				if (!node.children.isEmpty()) {
					node.expanded = true;
					spaceRequiredInNextLayer += node.spaceRequiredForChildren();
					nodesInNextLayer.addAll(node.children);
					numOfNodesWithChildren++;
				}
			}

			for (Iterator<TreeNode> iterator = nodesInNextLayer
					.iterator(); iterator.hasNext();) {
				SpaceTreeNode node = (SpaceTreeNode) iterator.next();
				node.expanded = false;
			}

			if (numOfNodesWithChildren == 0)
				break;

			spaceRequiredInNextLayer += branchGap
					* (numOfNodesWithChildren - 1);

			boolean addedNewLayer = false;
			if ((spaceRequiredInNextLayer <= requiredSpace
					|| spaceRequiredInNextLayer <= availableSpace
					|| (layer < 1 && nodeToExpand.depth + layer < 1))
					&& !nodesInNextLayer.isEmpty()) {
				// add next layer and center its nodes

				SpaceTreeLayer childLayer = spaceTreeLayers
						.get(nodeToExpand.depth + layer + 1);
				childLayer.addNodes(nodesInNextLayer);
				SpaceTreeNode firstChild = ((SpaceTreeNode) nodesInNextLayer
						.get(0));
				SpaceTreeNode lastChild = ((SpaceTreeNode) nodesInNextLayer
						.get(nodesInNextLayer.size() - 1));
				double boundsWidth = spaceRequiredInNextLayer
						- firstChild.spaceRequiredForNode() / 2
						- lastChild.spaceRequiredForNode() / 2;
				double startPosition = Math.max(
						(availableSpace - boundsWidth) / 2,
						firstChild.spaceRequiredForNode() / 2);
				setAvailableSpace(spaceRequiredInNextLayer);
				childLayer.fitNodesWithinBounds(nodesInNextLayer, startPosition,
						startPosition + boundsWidth);
				setAvailableSpace(0);
				if (nodeToExpand.childrenPositionsOK(nodesInThisLayer)
						|| layer == 0 || nodeToExpand.depth + layer < 1)
					addedNewLayer = true;
			}
			if (!addedNewLayer) {
				revertToSnapshot(snapShot);
				break;
			}
		}
		nodeToExpand.centerParentsBottomUp();
		nodeToExpand.centerParentsTopDown();
	}

	/**
	 * Available space is the biggest of the following values:
	 * <ul>
	 * <li>Space provided by current context bounds</li>
	 * <li>Space already taken by the widest layer</li>
	 * <li>Value set with {@link #setAvailableSpace(double)}</li>
	 * </ul>
	 * 
	 * @return
	 */
	private double getAvailableSpace() {
		double result = (direction == TOP_DOWN || direction == BOTTOM_UP)
				? bounds.getWidth() : bounds.getHeight();
		result = Math.max(result, this.availableSpace);
		for (Iterator<SpaceTreeLayer> iterator = spaceTreeLayers
				.iterator(); iterator.hasNext();) {
			SpaceTreeLayer layer = iterator.next();
			if (!layer.nodes.isEmpty()) {
				SpaceTreeNode first = layer.nodes.get(0);
				SpaceTreeNode last = layer.nodes.get(layer.nodes.size() - 1);
				result = Math
						.max(result,
								last.positionInLayer - first.positionInLayer
										+ (first.spaceRequiredForNode()
												+ last.spaceRequiredForNode())
												/ 2);
			} else
				break;
		}
		return result;
	}

	/**
	 * This method allows to reserve more space than actual layout bounds
	 * provide or nodes currently occupy.
	 * 
	 * @param availableSpace
	 */
	private void setAvailableSpace(double availableSpace) {
		this.availableSpace = availableSpace;
	}

	private double expectedDistance(SpaceTreeNode node,
			SpaceTreeNode neighbor) {
		double expectedDistance = (node.spaceRequiredForNode()
				+ neighbor.spaceRequiredForNode()) / 2;
		expectedDistance += (node.parent == neighbor.parent) ? leafGap
				: branchGap;
		return expectedDistance;
	}

	private class NodeSnapshot {
		SpaceTreeNode node;
		double position;
		boolean expanded;
	}

	/**
	 * Stores current expansion state of tree nodes and their position in layers
	 * 
	 * @return array containing state of all unpruned nodes
	 */
	private NodeSnapshot[][] takeSnapShot() {
		NodeSnapshot[][] result = new NodeSnapshot[spaceTreeLayers.size()][];
		for (int i = 0; i < result.length; i++) {
			SpaceTreeLayer layer = spaceTreeLayers.get(i);
			result[i] = new NodeSnapshot[layer.nodes.size()];
			for (int j = 0; j < result[i].length; j++) {
				result[i][j] = new NodeSnapshot();
				result[i][j].node = layer.nodes.get(j);
				result[i][j].position = result[i][j].node.positionInLayer;
				result[i][j].expanded = result[i][j].node.expanded;
			}
		}
		return result;
	}

	/**
	 * Restores tree nodes' expansion state and position in layers
	 * 
	 * @param snapShot
	 *            state obtained with {@link #takeSnapShot()}
	 */
	private void revertToSnapshot(NodeSnapshot[][] snapShot) {
		for (int i = 0; i < snapShot.length; i++) {
			SpaceTreeLayer layer = spaceTreeLayers.get(i);
			layer.nodes.clear();
			for (int j = 0; j < snapShot[i].length; j++) {
				snapShot[i][j].node.positionInLayer = snapShot[i][j].position;
				snapShot[i][j].node.expanded = snapShot[i][j].expanded;
				layer.nodes.add(snapShot[i][j].node);
			}
		}
	}
}
