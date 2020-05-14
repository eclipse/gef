/*******************************************************************************
 * Copyright (c) 2012, 2017 itemis AG and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Rene Kuhlemann - provided first version of code based on the initial paper
 *    		of Sugiyama et al. (http://dx.doi.org/10.1109/TSMC.1981.4308636),
 *          associated to bugzilla entry #384730  
 *    Adam Kovacs - implements the new LayerProvider and 
 *    		CrossingReducer interfaces
 *    Matthias Wienand (itemis AG) - refactorings
 *    
 *******************************************************************************/
package org.eclipse.gef.layout.algorithms;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.eclipse.gef.geometry.planar.Dimension;
import org.eclipse.gef.geometry.planar.Point;
import org.eclipse.gef.geometry.planar.Rectangle;
import org.eclipse.gef.graph.Edge;
import org.eclipse.gef.graph.Node;
import org.eclipse.gef.layout.ILayoutAlgorithm;
import org.eclipse.gef.layout.LayoutContext;
import org.eclipse.gef.layout.LayoutProperties;

/**
 * The SugiyamaLayoutAlgorithm class implements an algorithm to arrange a
 * directed graph in a layered tree-like layout. The final presentation follows
 * five design principles for enhanced readability:
 * 
 * - Hierarchical layout of vertices - Least crossings of lines (edges) -
 * Straightness of lines when ever possible - Close layout of vertices connected
 * to each other, i.e. short paths - Balanced layout of lines coming into or
 * going from a vertex
 * 
 * For further information see http://dx.doi.org/10.1109/TSMC.1981.4308636
 * 
 * This layout algorithm works only with - directed graphs (otherwise an
 * appropriate RuntimeException is thrown)
 * 
 * @author Rene Kuhlemann
 * @author Adam Kovacs
 * @author mwienand
 */
public class SugiyamaLayoutAlgorithm implements ILayoutAlgorithm {

	/**
	 * Specifies the direction for the {@link SugiyamaLayoutAlgorithm}.
	 */
	public enum Direction {
		/**
		 * Horizontal direction, i.e. left to right.
		 */
		HORIZONTAL,

		/**
		 * Vertical direction, i.e. top to bottom.
		 */
		VERTICAL
	}

	/**
	 * 
	 * An interface for heuristics that reduces edge crossings.
	 * 
	 * @author Adam Kovacs
	 */
	public static interface CrossingReducer {
		/**
		 * From the given nodes it creates a map of NodeLayouts and NodeWrappers
		 * which contains the layers and indexes of the nodes
		 * 
		 * @param nodes
		 *            List of nodes needed to be organized
		 */
		void crossReduction(List<List<NodeWrapper>> nodes);
	}

	/**
	 * @author Rene Kuhlemann
	 */
	public static class BarycentricCrossingReducer implements CrossingReducer {

		private List<List<NodeWrapper>> layers = new ArrayList<>();
		private Map<Node, NodeWrapper> map = new IdentityHashMap<>();
		private static final int MAX_SWEEPS = 35;
		private int last; // index of the last element in a layer after padding
							// process

		/**
		 * Fills in virtual nodes, so the layer system finally becomes an
		 * equidistant grid
		 */
		private void padLayers() {
			last = 0;
			for (List<NodeWrapper> iter : layers)
				if (iter.size() > last)
					last = iter.size();
			last--; // index of the last element of any layer
			for (List<NodeWrapper> iter : layers) { // padding is always
													// added at
				// the END of each layer!
				for (int i = iter.size(); i <= last; i++)
					iter.add(new NodeWrapper());
				updateIndex(iter);
			}
		}

		/**
		 * Removes the padding nodes from the layers
		 */
		private void unpadLayers() {
			for (List<NodeWrapper> layer : layers) {
				Iterator<NodeWrapper> it = layer.iterator();
				while (it.hasNext()) {
					NodeWrapper wrapper = it.next();
					if (wrapper.isPadding()) {
						it.remove();
					}
				}
			}
		}

		/**
		 * Reduces connection crossings between two adjacent layers by a
		 * combined top-down and bottom-up approach. It uses a heuristic
		 * approach based on the predecessor's barycenter.
		 */
		private void reduceCrossings() {
			for (int round = 0; round < MAX_SWEEPS; round++) {
				if ((round & 1) == 0) { // if round is even then do a bottom-up
										// scan
					for (int index = 1; index < layers.size(); index++)
						reduceCrossingsDown(layers.get(index));
				} else { // else top-down
					for (int index = layers.size() - 2; index >= 0; index--)
						reduceCrossingsUp(layers.get(index));
				}
			}
		}

		private void reduceCrossingsDown(List<NodeWrapper> layer) {
			// DOWN: scan PREDECESSORS
			for (NodeWrapper node : layer)
				node.index = node.getBaryCenter(node.pred);
			Collections.sort(layer, new Comparator<NodeWrapper>() {
				public int compare(NodeWrapper node1, NodeWrapper node2) {
					return (node1.index - node2.index);
				}
			});
			updateIndex(layer);
		}

		private void reduceCrossingsUp(List<NodeWrapper> layer) {
			// UP: scan SUCCESSORS
			for (NodeWrapper node : layer)
				node.index = node.getBaryCenter(node.succ);
			Collections.sort(layer, new Comparator<NodeWrapper>() {
				public int compare(NodeWrapper node1, NodeWrapper node2) {
					return (node1.index - node2.index);
				}
			});
			updateIndex(layer);
		}

		private void refineLayers() {
			// from Sugiyama
			// paper: down, up, and down yields best results, wonder why..
			for (int index = 1; index < layers.size(); index++)
				refineLayersDown(layers.get(index));
			for (int index = layers.size() - 2; index >= 0; index--)
				refineLayersUp(layers.get(index));
			for (int index = 1; index < layers.size(); index++)
				refineLayersDown(layers.get(index));
		}

		private void refineLayersDown(List<NodeWrapper> layer) {
			// first, get a priority list
			List<NodeWrapper> list = new ArrayList<>(layer);
			Collections.sort(list, new Comparator<NodeWrapper>() {
				public int compare(NodeWrapper node1, NodeWrapper node2) {
					return (node2.getPriorityDown() - node1.getPriorityDown()); // descending
					// ordering!!!
				}
			});
			// second, remove padding from the layer's end and place them in
			// front of the current node to improve its position
			for (NodeWrapper iter : list) {
				// break, if there are no more "real" nodes
				if (iter.isPadding())
					break;
				// compute distance to new position
				int delta = iter.getBaryCenter(iter.pred) - iter.index;
				for (int i = 0; i < delta; i++)
					layer.add(iter.index, layer.remove(last));
			}
			updateIndex(layer);
		}

		private void refineLayersUp(List<NodeWrapper> layer) {
			// first, get a priority list
			List<NodeWrapper> list = new ArrayList<>(layer);
			Collections.sort(list, new Comparator<NodeWrapper>() {
				public int compare(NodeWrapper node1, NodeWrapper node2) {
					// descending order
					return (node2.getPriorityUp() - node1.getPriorityUp());
				}
			});
			// second, remove padding from the layer's end and place them in
			// front of the current node to improve its position
			for (NodeWrapper iter : list) {
				// break, if there are no more "real" nodes
				if (iter.isPadding())
					break;
				// compute distance to new position
				int delta = iter.getBaryCenter(iter.succ) - iter.index;
				for (int i = 0; i < delta; i++)
					layer.add(iter.index, layer.remove(last));
			}
			updateIndex(layer);
		}

		private void updateIndex(List<NodeWrapper> list) {
			for (int index = 0; index < list.size(); index++) {
				list.get(index).index = index;
				map.put(list.get(index).node, list.get(index));
			}
		}

		public void crossReduction(List<List<NodeWrapper>> nodes) {
			this.layers = nodes;
			padLayers();
			// reduce and refine iteratively, depending on the depth of the
			// graph
			for (int i = 0; i < layers.size(); i++) {
				reduceCrossings();
				refineLayers();
			}
			reduceCrossings();
			unpadLayers();
		}
	}

	/**
	 * Implements the CrossingReducer interface. This algorithm divides each
	 * layer by a pivot node based on the relative position of connected nodes
	 * and decides which side of the pivot point it should be for the fewer edge
	 * crossing.
	 * 
	 * @author Adam Kovacs
	 * 
	 */
	public static class SplitCrossingReducer implements CrossingReducer {
		private final Map<Node, NodeWrapper> map = new IdentityHashMap<>();

		/**
		 * Filters the multiple connections from the two arrays
		 * 
		 * @param a
		 * @param b
		 * @return
		 */
		private ArrayList<Node> unionOfNodes(Collection<Node> a,
				Collection<Node> b) {
			ArrayList<Node> res = new ArrayList<>(a);
			for (Node n : b) {
				if (!res.contains(n)) {
					res.add(n);
				}
			}
			return res;
		}

		/**
		 * Returns the number of crosses between the two nodes and those
		 * connected to them.
		 * 
		 * @param nodeA
		 * @param nodeB
		 * @return
		 */
		private int numberOfCrosses(NodeWrapper nodeA, NodeWrapper nodeB) {
			int numOfCrosses = 0;
			if (nodeA.equals(nodeB))
				return 0;

			// Filter nodes connected with bidirectional edges
			ArrayList<Node> adjacentNodesOfA = unionOfNodes(
					nodeA.node.getPredecessorNodes(),
					nodeA.node.getSuccessorNodes());
			ArrayList<Node> adjacentNodesOfB = unionOfNodes(
					nodeB.node.getPredecessorNodes(),
					nodeB.node.getSuccessorNodes());

			for (Node aNode : adjacentNodesOfA) {
				ArrayList<Integer> alreadyCrossed = new ArrayList<>();
				NodeWrapper aNodeWrapper = map.get(aNode);
				for (int i = 0; i < adjacentNodesOfB.size(); i++) {
					NodeWrapper nw = map.get(adjacentNodesOfB.get(i));
					if (!alreadyCrossed.contains(i) && nw != null) {
						// only if on the same side
						if ((nw.layer > nodeA.layer
								&& aNodeWrapper.layer > nodeA.layer)
								|| (nw.layer < nodeA.layer
										&& aNodeWrapper.layer < nodeA.layer)) {
							if (nodeA.index < nodeB.index) {
								if (aNodeWrapper.index > nw.index) {
									numOfCrosses++;
									alreadyCrossed.add(i);
								} else if (nw.index == aNodeWrapper.index) {
									if (nodeA.index >= nw.index) {
										// implies nodeB.index > nw.index
										if ((aNodeWrapper.layer > nw.layer
												&& nodeA.layer < nw.layer)
												|| (aNodeWrapper.layer < nw.layer
														&& nw.layer < nodeA.layer)) {
											// top-left or bottom-left quarter
											numOfCrosses++;
											alreadyCrossed.add(i);
										}
									} else if (nodeB.index <= nw.index) {
										// implies nodeA.index < nw.index
										if ((aNodeWrapper.layer > nw.layer
												&& aNodeWrapper.layer < nodeB.layer)
												|| (aNodeWrapper.layer < nw.layer
														&& aNodeWrapper.layer > nodeB.layer)) {
											// top-right or bottom-right quarter
											numOfCrosses++;
											alreadyCrossed.add(i);
										}
									}
								}
							} else if (nodeA.index > nodeB.index) {
								if (aNodeWrapper.index < nw.index) {
									numOfCrosses++;
									alreadyCrossed.add(i);
								} else if (nw.index == aNodeWrapper.index) {
									if (nodeB.index >= nw.index) {
										// implies nodeB.index > nw.index
										if ((aNodeWrapper.layer > nw.layer
												&& nodeB.layer > aNodeWrapper.layer)
												|| (aNodeWrapper.layer < nw.layer
														&& aNodeWrapper.layer > nodeB.layer)) {
											// top-left or bottom-left quarter
											numOfCrosses++;
											alreadyCrossed.add(i);
										}
									} else if (nodeA.index <= nw.index) {
										// implies nodeA.index < nw.index
										if ((aNodeWrapper.layer > nw.layer
												&& nw.layer > nodeA.layer)
												|| (aNodeWrapper.layer < nw.layer
														&& nw.layer < nodeA.layer)) {
											// top-right or bottom-right quarter
											numOfCrosses++;
											alreadyCrossed.add(i);
										}
									}
								}
							}
						}
					}
				}
			}

			return numOfCrosses;
		}

		/**
		 * Selects the pivot node by random and decides the order.
		 * 
		 * @param layer
		 * @return
		 */
		private List<NodeWrapper> splitHeuristic(List<NodeWrapper> layer) {
			ArrayList<NodeWrapper> left = new ArrayList<>();
			ArrayList<NodeWrapper> right = new ArrayList<>();

			if (layer.size() < 1)
				return layer;
			Random random = new Random();
			NodeWrapper pivot = layer.get(random.nextInt(layer.size()));
			// NodeWrapper pivot = layer.get(0);
			// NodeWrapper pivot = layer.get((int)(layer.size() / 2));
			for (NodeWrapper node : layer) {
				if (!node.equals(pivot) && node.node != null
						&& pivot.node != null) {
					int num1 = numberOfCrosses(node, pivot);
					int num2 = numberOfCrosses(pivot, node);
					if (num1 < num2)
						left.add(node);
					else if (num1 > num2)
						right.add(node);
					else {
						if (num1 == num2 && num1 > 0) {
							int tmpindex = map.get(pivot.node).index;
							map.get(pivot.node).index = map
									.get(node.node).index;
							map.get(node.node).index = tmpindex;
						}
						if (node.index < pivot.index)
							left.add(node);
						else
							right.add(node);
					}
				}
			}

			ArrayList<NodeWrapper> res = new ArrayList<>();
			res.addAll(splitHeuristic(left));
			res.add(pivot);
			res.addAll(splitHeuristic(right));
			return res;
		}

		public void crossReduction(List<List<NodeWrapper>> nodes) {
			// Building the map
			for (List<NodeWrapper> layer : nodes)
				for (NodeWrapper nw : layer)
					map.put(nw.node, nw);
			for (int i = 0; i < nodes.size(); i++) {
				if (!nodes.get(i).isEmpty()) {
					splitHeuristic(nodes.get(i));
				}
			}
		}
	}

	/**
	 * Implemented the CrossingReducer interface. This algorithm select
	 * neighbouring nodes and decides there order based on the number of edge
	 * crossings between them and those connected to them.
	 * 
	 * @author Adam Kovacs
	 * 
	 */
	public static class GreedyCrossingReducer implements CrossingReducer {
		private final Map<Node, NodeWrapper> map = new IdentityHashMap<>();
		private List<List<NodeWrapper>> layers = new ArrayList<>();
		private Map<Integer, Integer> crossesForLayers = new IdentityHashMap<>();

		/**
		 * Filters the multiple connections from the two arrays.
		 * 
		 * @param a
		 * @param b
		 * @return
		 */
		private ArrayList<Node> unionOfNodes(Collection<Node> a,
				Collection<Node> b) {
			ArrayList<Node> res = new ArrayList<>(a);
			for (Node n : b) {
				if (!res.contains(n)) {
					res.add(n);
				}
			}
			return res;
		}

		/**
		 * Returns the number of crosses between the two nodes and those
		 * connected to them.
		 * 
		 * @param nodeA
		 * @param nodeB
		 * @return
		 */
		private int numberOfCrosses(NodeWrapper nodeA, NodeWrapper nodeB) {
			int numOfCrosses = 0;
			if (nodeA.equals(nodeB))
				return 0;

			// Filter nodes connected with bidirectional edges
			ArrayList<Node> adjacentNodesOfA = unionOfNodes(
					nodeA.node.getPredecessorNodes(),
					nodeA.node.getSuccessorNodes());
			ArrayList<Node> adjacentNodesOfB = unionOfNodes(
					nodeB.node.getPredecessorNodes(),
					nodeB.node.getSuccessorNodes());

			for (Node aNode : adjacentNodesOfA) {
				ArrayList<Integer> alreadyCrossed = new ArrayList<>();
				NodeWrapper aNodeWrapper = map.get(aNode);
				for (int i = 0; i < adjacentNodesOfB.size(); i++) {
					NodeWrapper nw = map.get(adjacentNodesOfB.get(i));
					if (!alreadyCrossed.contains(i) && nw != null) {
						// only if on the same side
						if ((nw.layer > nodeA.layer
								&& aNodeWrapper.layer > nodeA.layer)
								|| (nw.layer < nodeA.layer
										&& aNodeWrapper.layer < nodeA.layer)) {
							if (nodeA.index < nodeB.index) {
								if (aNodeWrapper.index > nw.index) {
									numOfCrosses++;
									alreadyCrossed.add(i);
								} else if (nw.index == aNodeWrapper.index) {
									if (nodeA.index >= nw.index) {
										// implies nodeB.index > nw.index
										if ((aNodeWrapper.layer > nw.layer
												&& nodeA.layer < nw.layer)
												|| (aNodeWrapper.layer < nw.layer
														&& nw.layer < nodeA.layer)) {
											// top-left or bottom-left quarter
											numOfCrosses++;
											alreadyCrossed.add(i);
										}
									} else if (nodeB.index <= nw.index) {
										// implies nodeA.index < nw.index
										if ((aNodeWrapper.layer > nw.layer
												&& aNodeWrapper.layer < nodeB.layer)
												|| (aNodeWrapper.layer < nw.layer
														&& aNodeWrapper.layer > nodeB.layer)) {
											// top-right or bottom-right quarter
											numOfCrosses++;
											alreadyCrossed.add(i);
										}
									}
								}
							} else if (nodeA.index > nodeB.index) {
								if (aNodeWrapper.index < nw.index) {
									numOfCrosses++;
									alreadyCrossed.add(i);
								} else if (nw.index == aNodeWrapper.index) {
									if (nodeB.index >= nw.index) {
										// implies nodeB.index > nw.index
										if ((aNodeWrapper.layer > nw.layer
												&& nodeB.layer > aNodeWrapper.layer)
												|| (aNodeWrapper.layer < nw.layer
														&& aNodeWrapper.layer > nodeB.layer)) {
											// top-left or bottom-left quarter
											numOfCrosses++;
											alreadyCrossed.add(i);
										}
									} else if (nodeA.index <= nw.index) {
										// implies nodeA.index < nw.index
										if ((aNodeWrapper.layer > nw.layer
												&& nw.layer > nodeA.layer)
												|| (aNodeWrapper.layer < nw.layer
														&& nw.layer < nodeA.layer)) {
											// top-right or bottom-right quarter
											numOfCrosses++;
											alreadyCrossed.add(i);
										}
									}
								}
							}
						}
					}
				}
			}

			return numOfCrosses;
		}

		/**
		 * Iterates the list and switches that results in less crossings.
		 * 
		 * @param layer
		 * @return
		 */
		private boolean greedyHeuristic(List<NodeWrapper> layer) {
			boolean res = false;
			if (layer.size() > 1) {
				for (int i = 0; i < layer.size() - 1; i++) {
					if (layer.get(i).node != null
							&& layer.get(i + 1).node != null) {
						int num1 = numberOfCrosses(layer.get(i),
								layer.get(i + 1));
						int num2 = numberOfCrosses(layer.get(i + 1),
								layer.get(i));
						if (num1 > num2 || (num1 == num2 && num1 > 0)) {
							if (!crossesForLayers
									.containsKey((layer.get(i).layer))
									|| crossesForLayers
											.get(layer.get(i).layer) > num2) {
								crossesForLayers.put(layer.get(i).layer, num2);
								res = true;
								int level = layer.get(0).layer;

								NodeWrapper tmp = layers.get(level).get(i);
								int tmpindex = layers.get(level).get(i).index;
								layers.get(level).get(i).index = layers
										.get(level).get(i + 1).index;
								layers.get(level).set(i,
										layers.get(level).get(i + 1));
								layers.get(level).get(i + 1).index = tmpindex;
								layers.get(level).set(i + 1, tmp);
							}
						}
					}
				}
			}
			return res;
		}

		public void crossReduction(List<List<NodeWrapper>> nodes) {
			crossesForLayers.clear();
			layers = nodes;

			// Builds the map
			for (List<NodeWrapper> layer : nodes)
				for (NodeWrapper node : layer)
					map.put(node.node, node);

			// After three iteration with no change it stops
			int iteration = 0;
			boolean change = false;
			while (iteration < 3) {
				change = false;
				for (int i = 0; i < nodes.size(); i++) {
					if (greedyHeuristic(layers.get(i))) {
						change = true;
					}
				}
				if (!change)
					iteration++;
			}
		}
	}

	/**
	 * Structure to store nodes and their positions in the layers. Furthermore
	 * predecessors and successors can be assigned to the nodes.
	 * 
	 * @author Adam Kovacs
	 */
	public static class NodeWrapper {
		/**
		 * The index of this {@link NodeWrapper} (used to find crossings).
		 */
		int index;
		/**
		 * The layer this {@link NodeWrapper} is in (used to find crossings).
		 */
		final int layer;
		/**
		 * The wrapped {@link Node}.
		 */
		final Node node;
		/**
		 * A {@link List} containing the predecessors of this
		 * {@link NodeWrapper}.
		 */
		final List<NodeWrapper> pred = new LinkedList<>();
		/**
		 * A {@link List} containing the successors of this {@link NodeWrapper}.
		 */
		final List<NodeWrapper> succ = new LinkedList<>();

		private static final int PADDING = -1;

		/**
		 * Constructs a new {@link NodeWrapper} to wrap the given {@link Node}.
		 * 
		 * @param n
		 *            The {@link Node} that is wrapped.
		 * @param l
		 *            The layer this {@link NodeWrapper} is on.
		 */
		NodeWrapper(Node n, int l) {
			node = n;
			layer = l;
		} // NodeLayout wrapper

		/**
		 * Constructs a new dummy {@link NodeWrapper} on the specified layer.
		 * Dummy nodes are used to make the hierarchy proper, i.e. it does only
		 * have edges between two consecutive layers.
		 * 
		 * @param l
		 *            The layer this {@link NodeWrapper} is on.
		 */
		NodeWrapper(int l) {
			this(null, l);
		} // Dummy to connect two NodeLayout objects

		/**
		 * Constructs a new padding {@link NodeWrapper} (layer -1).
		 */
		NodeWrapper() {
			this(null, PADDING);
		} // Padding for final refinement phase

		/**
		 * Adds the given {@link NodeWrapper} to the list of predecessors that
		 * is managed by this {@link NodeWrapper}.
		 * 
		 * @param node
		 *            The {@link NodeWrapper} that is added to the list of
		 *            predecessors.
		 */
		void addPredecessor(NodeWrapper node) {
			pred.add(node);
		}

		/**
		 * Adds the given {@link NodeWrapper} to the list of successors that is
		 * managed by this {@link NodeWrapper}.
		 * 
		 * @param node
		 *            The {@link NodeWrapper} that is added to the list of
		 *            successors.
		 */
		void addSuccessor(NodeWrapper node) {
			succ.add(node);
		}

		/**
		 * Returns <code>true</code> if this {@link NodeWrapper} is a dummy.
		 * Otherwise returns <code>false</code>.
		 * 
		 * @return <code>true</code> if this {@link NodeWrapper} is a dummy,
		 *         otherwise <code>false</code>.
		 */
		boolean isDummy() {
			return ((node == null) && (layer != PADDING));
		}

		/**
		 * Returns <code>true</code> if this {@link NodeWrapper} is a padding
		 * node. Otherwise returns <code>false</code>.
		 * 
		 * @return <code>true</code> if this {@link NodeWrapper} is a padding
		 *         node, otherwise <code>false</code>.
		 */
		boolean isPadding() {
			return ((node == null) && (layer == PADDING));
		}

		/**
		 * Computes the barycenter of the given list of {@link NodeWrapper}s.
		 * 
		 * @param list
		 *            The list of {@link NodeWrapper}s for which to compute the
		 *            barycenter.
		 * @return The barycenter of the given {@link NodeWrapper}s.
		 */
		int getBaryCenter(List<NodeWrapper> list) {
			if (list.isEmpty())
				return (this.index);
			if (list.size() == 1)
				return (list.get(0).index);
			double barycenter = 0;
			for (NodeWrapper node : list)
				barycenter += node.index;
			// always round down to avoid wrap around in position refining
			return ((int) (barycenter / list.size()));
		}

		/**
		 * Returns the down priority for this {@link NodeWrapper}:
		 * <ol>
		 * <li>Padding nodes: <code>0</code>
		 * <li>Dummy nodes: <code>Integer.MAX_VALUE >> 1</code>
		 * <li>Dummy nodes with dummy successor: <code>Integer.MAX_VALUE</code>
		 * <li>Otherwise: Number of predecessors.
		 * </ol>
		 * 
		 * @return The down priority for this {@link NodeWrapper}.
		 */
		int getPriorityDown() {
			if (isPadding())
				return (0);
			if (isDummy()) {
				if (succ != null && succ.size() > 0) {
					if (succ.get(0).isDummy())
						// part of a straight line
						return (Integer.MAX_VALUE);
					else
						// start of a straight line
						return (Integer.MAX_VALUE >> 1);
				}
			}
			return (pred.size());
		}

		/**
		 * Returns the up priority for this {@link NodeWrapper}:
		 * <ol>
		 * <li>Padding nodes: <code>0</code>
		 * <li>Dummy nodes: <code>Integer.MAX_VALUE >> 1</code>
		 * <li>Dummy nodes with dummy predecessor:
		 * <code>Integer.MAX_VALUE</code>
		 * <li>Otherwise: Number of successors.
		 * </ol>
		 * 
		 * @return The up priority for this {@link NodeWrapper}.
		 */
		int getPriorityUp() {
			if (isPadding())
				return (0);
			if (isDummy()) {
				if (pred != null && pred.size() > 0) {
					if (pred.get(0).isDummy())
						// part of a straight line
						return (Integer.MAX_VALUE);
					else
						// start of a straight line
						return (Integer.MAX_VALUE >> 1);
				}
			}
			return (succ.size());
		}

	}

	/**
	 * 
	 * An interface for creating layers. Interface for parameterizable layering
	 * heuristics.
	 * 
	 * @author Adam Kovacs
	 */
	public static interface LayerProvider {

		/**
		 * Creating layers of the nodes and makes it possible to assign layers
		 * to those nodes.
		 * 
		 * @param nodes
		 *            List of all the nodes that needs to be organized
		 * @return a list of layers for the given nodes, represented each as a
		 *         list of {@link NodeWrapper}s
		 */
		List<List<NodeWrapper>> calculateLayers(List<Node> nodes);
	}

	/**
	 * Processing the nodes based on depth first search and creating a list of
	 * layers
	 * 
	 * @author Adam Kovacs
	 * 
	 */
	public static class DFSLayerProvider implements LayerProvider {

		private Map<Node, Integer> assignedNodes = new IdentityHashMap<>();

		/**
		 * Returns the mutual connections of the two array given as parameters.
		 * 
		 * @param a
		 * @param b
		 * @return
		 */
		private List<Edge> intersectOfConnections(Collection<Edge> a,
				Collection<Edge> b) {
			ArrayList<Edge> res = new ArrayList<>();

			for (Edge e : a) {
				if (b.contains(e)) {
					res.add(e);
				}
			}
			return res;
		}

		private void addToInitClosedList(Node node, int layout,
				List<Node> initClosedList, Map<Node, NodeWrapper> map) {
			NodeWrapper nw = new NodeWrapper(node, layout);
			map.put(node, nw);
			initClosedList.add(node);
		}

		/**
		 * Finds the root elements in the list of nodes based on their
		 * connections.
		 * 
		 * @param nodes
		 *            The list of {@link Node}s for which to find the root
		 *            elements.
		 * @return the list of root elements
		 */
		public ArrayList<Node> getRoots(List<Node> nodes) {
			ArrayList<Node> res = new ArrayList<>();

			for (Node node : nodes) {
				// directed edges
				if (node.getIncomingEdges().size() == 0)
					res.add(node);
				else {
					int sizeOfIntersect = intersectOfConnections(
							node.getIncomingEdges(), node.getOutgoingEdges())
									.size();
					// there are more outgoing edges, besides the bidirectionals
					if (node.getOutgoingEdges().size() > sizeOfIntersect)
						res.add(node);
					// only bidirectional edges, no incoming directed edges
					if (node.getIncomingEdges().size() == sizeOfIntersect
							&& node.getOutgoingEdges()
									.size() == sizeOfIntersect)
						res.add(node);
				}
			}

			// if no sources then we only have bidirectional edges and/or cycles
			if (res.size() == 0)
				res.add(nodes.get(0));

			return res;
		}

		/**
		 * Returns a {@link Map} that stores the assignment of layers to
		 * {@link Node}s.
		 * 
		 * @return A {@link Map} that stores the assignment of layers to
		 *         {@link Node}s.
		 */
		public Map<Node, Integer> getAssignedNodes() {
			return assignedNodes;
		}

		/**
		 * Assigns the given layer to the given {@link Node}.
		 * 
		 * @param node
		 *            The {@link Node} to which a layer is assigned.
		 * @param layer
		 *            The layer that is assigned to that {@link Node}.
		 */
		public void addAssignedNode(Node node, int layer) {
			assignedNodes.put(node, layer);
		}

		/**
		 * Clears the {@link Map} that stores the layer assignments.
		 */
		public void clearAssignedNodes() {
			assignedNodes.clear();
		}

		private static void updateIndex(List<NodeWrapper> list) {
			for (int index = 0; index < list.size(); index++)
				list.get(index).index = index;
		}

		/**
		 * Creates a new layer and puts the elements of this layer to the map.
		 * 
		 * @param list
		 */
		private void addLayer(List<Node> list, List<List<NodeWrapper>> layers,
				Map<Node, NodeWrapper> map) {
			ArrayList<NodeWrapper> layer = new ArrayList<>(list.size());
			for (Node node : list) {
				// wrap each NodeLayout with the internal data object and
				// provide a corresponding mapping
				NodeWrapper nw = new NodeWrapper(node, layers.size());
				map.put(node, nw);
				layer.add(nw);
			}
			layers.add(layer);
			updateIndex(layer);
		}

		/**
		 * Finds the connected nodes to be processed.
		 * 
		 * @param toUnfold
		 * @return
		 */
		private ArrayList<Node> Unfold(Node toUnfold, Set<Node> openedList,
				Set<Node> closedList) {
			ArrayList<Node> res = new ArrayList<>();

			for (Edge e : toUnfold.getOutgoingEdges()) {
				Node endPoint = e.getTarget();
				if (endPoint.equals(toUnfold))
					endPoint = e.getSource();
				if (!closedList.contains(endPoint)
						&& !openedList.contains(endPoint)
						&& !res.contains(endPoint))
					res.add(endPoint);
			}
			for (Edge e : toUnfold.getIncomingEdges()) {
				Node endPoint = e.getTarget();
				if (endPoint.equals(toUnfold))
					endPoint = e.getSource();
				if (!closedList.contains(endPoint)
						&& !openedList.contains(endPoint)
						&& !res.contains(endPoint))
					res.add(endPoint);
			}

			return res;
		}

		public List<List<NodeWrapper>> calculateLayers(List<Node> nodeLayouts) {
			List<Node> nodes = new ArrayList<>(nodeLayouts);
			Set<Node> openedList = new HashSet<>();
			List<Node> initClosedList = new ArrayList<>();
			Set<Node> closedList = new HashSet<>();
			List<List<NodeWrapper>> layers = new ArrayList<>();
			Map<Node, NodeWrapper> map = new IdentityHashMap<>();

			// Assigns the given nodes to there layers
			if (assignedNodes.size() > 0) {
				for (Node node : nodes) {
					if (assignedNodes.containsKey(node))
						addToInitClosedList(node, assignedNodes.get(node),
								initClosedList, map);
				}
			}

			// Only at first iteration, clearing initClosedList, starting to
			// build layers
			if (initClosedList.size() > 0) {
				closedList.addAll(initClosedList);
				nodes.removeAll(initClosedList);
				initClosedList.clear();

				for (Node node : closedList) {
					if (map.get(node).layer < layers.size()) {
						layers.get(map.get(node).layer).add(map.get(node));
						updateIndex(layers.get(map.get(node).layer));
					} else {
						while (map.get(node).layer != layers.size()) {
							ArrayList<Node> layer = new ArrayList<>();
							addLayer(layer, layers, map);
						}
						ArrayList<Node> layer = new ArrayList<>();
						layer.add(node);
						addLayer(layer, layers, map);
					}
				}
			}

			ArrayList<Node> startPoints = new ArrayList<>();
			// Starts by finding a root or selecting the first from the assigned
			// ones
			if (layers.size() > 0 && layers.get(0).size() > 0)
				startPoints.add(layers.get(0).get(0).node);
			else if (layers.size() == 0) {
				startPoints.add(getRoots(nodes).get(0));
				addLayer(startPoints, layers, map);
			} else {
				startPoints.add(getRoots(nodes).get(0));
				for (Node startPoint : startPoints) {
					if (!map.containsKey(startPoint)) {
						NodeWrapper nw = new NodeWrapper(startPoint, 0);
						map.put(startPoint, nw);
						layers.get(0).add(nw);
					}
				}
				updateIndex(layers.get(0));
			}
			openedList.addAll(startPoints);
			Node toUnfold = startPoints.get(0);

			while (nodes.size() > 0) {
				// while openedList isn't empty it searches for further nodes
				// and adding them to the next layer
				while (openedList.size() != 0) {
					ArrayList<Node> unfolded = Unfold(toUnfold, openedList,
							closedList);
					if (unfolded.size() > 0) {
						int level = map.get(toUnfold).layer + 1;
						if (level < layers.size()) {
							for (Node n : unfolded) {
								if (!map.containsKey(n)) {
									NodeWrapper nw = new NodeWrapper(n, level);
									map.put(n, nw);
									layers.get(level).add(nw);
								}
							}
							updateIndex(layers.get(level));
						} else {
							ArrayList<Node> layer = new ArrayList<>();
							layer.addAll(unfolded);
							addLayer(layer, layers, map);
						}
						openedList.addAll(unfolded);
					}
					closedList.add(toUnfold);
					openedList.remove(toUnfold);
					nodes.remove(toUnfold);

					if (openedList.size() != 0)
						toUnfold = openedList.iterator().next();
				}
				if (nodes.size() > 0) {
					final Node node = nodes.get(0);
					openedList.add(node);
					NodeWrapper nw = new NodeWrapper(node, 0);
					map.put(node, nw);
					layers.get(0).add(nw);
				}
			}
			return layers;
		}
	}

	/**
	 * 
	 * @author Rene Kuhlemann
	 *
	 */
	public static class SimpleLayerProvider implements LayerProvider {

		private static final int MAX_LAYERS = 10;
		private final List<List<NodeWrapper>> layers = new ArrayList<>(
				MAX_LAYERS);
		private final Map<Node, NodeWrapper> map = new IdentityHashMap<>();

		private static List<Node> findRoots(List<Node> list) {
			List<Node> roots = new ArrayList<>();
			for (Node iter : list) {
				// no predecessors means: this is a root, add it to list
				if (iter.getPredecessorNodes().size() == 0)
					roots.add(iter);
			}
			return (roots);
		}

		/**
		 * Wraps all {@link Node} objects into an internal presentation
		 * {@link NodeWrapper} and inserts dummy wrappers into the layers
		 * between an object and their predecessing nodes if necessary. Finally,
		 * all nodes are chained over immediate adjacent layers down to their
		 * predecessors. This is necessary to apply the final step of the
		 * Sugiyama algorithm to refine the node position within a layer.
		 * 
		 * @param list
		 *            : List of all {@link Node} objects within the current
		 *            layer
		 */
		private void addLayer(List<Node> list) {
			ArrayList<NodeWrapper> layer = new ArrayList<>(list.size());
			for (Node node : list) {
				// wrap each NodeLayout with the internal data object and
				// provide a corresponding mapping
				NodeWrapper nw = new NodeWrapper(node, layers.size());
				map.put(node, nw);
				layer.add(nw);
				// insert dummy nodes if the adjacent layer does not contain the
				// predecessor
				for (Node node_predecessor : node.getPredecessorNodes()) {
					NodeWrapper nw_predecessor = map.get(node_predecessor);
					if (nw_predecessor != null) {
						for (int level = nw_predecessor.layer
								+ 1; level < nw.layer; level++) {
							// add "virtual" wrappers (dummies) to the layers in
							// between
							// virtual wrappers are in fact parts of a double
							// linked list
							NodeWrapper nw_dummy = new NodeWrapper(level);
							nw_dummy.addPredecessor(nw_predecessor);
							nw_predecessor.addSuccessor(nw_dummy);
							nw_predecessor = nw_dummy;
							layers.get(level).add(nw_dummy);
						}
						nw.addPredecessor(nw_predecessor);
						nw_predecessor.addSuccessor(nw);
					}
				}
			}
			layers.add(layer);
			updateIndex(layer);
		}

		private static void updateIndex(List<NodeWrapper> list) {
			for (int index = 0; index < list.size(); index++)
				list.get(index).index = index;
		}

		public List<List<NodeWrapper>> calculateLayers(List<Node> nodes) {
			map.clear();

			List<Node> predecessors = findRoots(nodes);
			nodes.removeAll(predecessors);
			// nodes now contains only nodes that are no roots
			addLayer(predecessors);
			for (int level = 1; nodes.isEmpty() == false; level++) {
				if (level > MAX_LAYERS)
					throw new RuntimeException(
							"Graphical tree exceeds maximum depth of "
									+ MAX_LAYERS
									+ "! (Graph not directed? Cycles?)");
				List<Node> layer = new ArrayList<>();
				for (Node item : nodes) {
					if (predecessors.containsAll(item.getPredecessorNodes()))
						layer.add(item);
				}
				if (layer.size() == 0)
					layer.add(nodes.get(0));
				nodes.removeAll(layer);
				predecessors.addAll(layer);
				addLayer(layer);
			}

			return layers;
		}

	}

	private List<List<NodeWrapper>> layers = new ArrayList<>();
	private Map<Node, NodeWrapper> map = new IdentityHashMap<>();
	private final Direction direction;
	private final Dimension dimension;

	// index of the last element in a layer after padding process
	private int last;

	private LayerProvider layerProvider;
	private CrossingReducer crossingReducer;

	/**
	 * Constructs a tree-like, layered layout of a directed graph.
	 * 
	 * @param dir
	 *            {@link Direction#HORIZONTAL}: left to right -
	 *            {@link Direction#VERTICAL} : top to bottom
	 * 
	 * @param dim
	 *            - desired size of the layout area. Uses the BOUNDS_PROPERTY of
	 *            the LayoutContext if not set
	 * 
	 * @param layering
	 *            - implementation of LayerProvider interface
	 * 
	 * @param crossing
	 *            - implementation of CrossingReducer interface
	 */
	public SugiyamaLayoutAlgorithm(Direction dir, Dimension dim,
			LayerProvider layering, CrossingReducer crossing) {
		direction = dir;
		dimension = dim;

		layerProvider = (layering == null) ? new SimpleLayerProvider()
				: layering;
		crossingReducer = (crossing == null) ? new BarycentricCrossingReducer()
				: crossing;
	}

	/**
	 * Constructs a new {@link SugiyamaLayoutAlgorithm} with the given
	 * {@link Direction}, {@link LayerProvider}, and {@link CrossingReducer}.
	 * 
	 * @param dir
	 *            The {@link Direction} for this {@link SugiyamaLayoutAlgorithm}
	 *            .
	 * @param layerProvider
	 *            The LayerProvider for this {@link SugiyamaLayoutAlgorithm}.
	 * @param crossing
	 *            The CrossingReducer for this {@link SugiyamaLayoutAlgorithm}.
	 */
	public SugiyamaLayoutAlgorithm(Direction dir, LayerProvider layerProvider,
			CrossingReducer crossing) {
		this(dir, null, layerProvider, crossing);
	}

	/**
	 * Constructs a new {@link SugiyamaLayoutAlgorithm} with the given
	 * {@link Direction}, {@link LayerProvider}, and a
	 * {@link BarycentricCrossingReducer}.
	 * 
	 * @param dir
	 *            The {@link Direction} for this {@link SugiyamaLayoutAlgorithm}
	 *            .
	 * @param layerProvider
	 *            The LayerProvider for this {@link SugiyamaLayoutAlgorithm}.
	 */
	public SugiyamaLayoutAlgorithm(Direction dir, LayerProvider layerProvider) {
		this(dir, null, layerProvider, new BarycentricCrossingReducer());
	}

	/**
	 * Constructs a new {@link SugiyamaLayoutAlgorithm} with the given
	 * {@link Direction}, and {@link CrossingReducer}.
	 * 
	 * @param dir
	 *            The {@link Direction} for this {@link SugiyamaLayoutAlgorithm}
	 *            .
	 * @param crossing
	 *            The CrossingReducer for this {@link SugiyamaLayoutAlgorithm}.
	 */
	public SugiyamaLayoutAlgorithm(Direction dir, CrossingReducer crossing) {
		this(dir, null, null, crossing);
	}

	/**
	 * Constructs a new {@link SugiyamaLayoutAlgorithm} with the given
	 * {@link Direction}, and the given dimension.
	 * 
	 * @param dir
	 *            The {@link Direction} for this {@link SugiyamaLayoutAlgorithm}
	 *            .
	 * @param dim
	 *            The desired size of the layout area. Uses the BOUNDS_PROPERTY
	 *            of the LayoutContext if not set.
	 */
	public SugiyamaLayoutAlgorithm(Direction dir, Dimension dim) {
		this(dir, dim, null, null);
	}

	/**
	 * Constructs a new {@link SugiyamaLayoutAlgorithm} with the given
	 * {@link Direction}.
	 * 
	 * @param dir
	 *            The {@link Direction} for this {@link SugiyamaLayoutAlgorithm}
	 *            .
	 */
	public SugiyamaLayoutAlgorithm(Direction dir) {
		this(dir, null, null, null);
	}

	/**
	 * Constructs a new {@link SugiyamaLayoutAlgorithm} with
	 * {@link Direction#VERTICAL} direction.
	 */
	public SugiyamaLayoutAlgorithm() {
		this(Direction.VERTICAL, null, null, null);
	}

	public void applyLayout(LayoutContext layoutContext, boolean clean) {
		if (!clean)
			return;
		layers.clear();
		map.clear();

		ArrayList<Node> nodes = new ArrayList<>();
		ArrayList<Node> nodes2 = new ArrayList<>();
		for (Node node : layoutContext.getNodes()) {
			nodes.add(node);
			nodes2.add(node);
		}
		layers = layerProvider.calculateLayers(nodes);
		crossingReducer.crossReduction(layers);

		for (List<NodeWrapper> layer : layers) {
			if (layer.size() > last)
				last = layer.size();
			for (NodeWrapper nw : layer) {
				map.put(nw.node, nw);
			}
		}
		calculatePositions(layoutContext);
	}

	private void calculatePositions(LayoutContext context) {
		Rectangle boundary = LayoutProperties.getBounds(context.getGraph());
		if (dimension != null)
			boundary = new Rectangle(0, 0, dimension.getWidth(),
					dimension.getHeight());
		double dx = boundary.getWidth() / layers.size();
		double dy = boundary.getHeight() / (last + 1);
		if (direction == Direction.HORIZONTAL)
			for (Node node : context.getNodes()) {
				NodeWrapper nw = map.get(node);
				LayoutProperties.setLocation(node, new Point(
						(nw.layer + 0.5d) * dx, (nw.index + 0.5d) * dy));
			}
		else
			for (Node node : context.getNodes()) {
				NodeWrapper nw = map.get(node);
				LayoutProperties.setLocation(node, new Point(
						(nw.index + 0.5d) * dx, (nw.layer + 0.5d) * dy));
			}
	}

}