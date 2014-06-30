/*******************************************************************************
 * Copyright (c) 2014 Adam Kovacs.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:    
 *    Adam Kovacs - initial implementation
 *******************************************************************************/
package org.eclipse.gef4.layout.algorithms;

import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.gef4.layout.interfaces.CrossingReducer;
import org.eclipse.gef4.layout.interfaces.NodeLayout;

/**
 * Implemented the CrossingReducer interface. This algorithm select neighbouring
 * nodes and decides there order based on the number of edge crossings between
 * them and those connected to them.
 * 
 * @author Adam Kovacs
 * 
 */
public class GreedyCrossingReducer implements CrossingReducer {
	private final Map<NodeLayout, NodeWrapper> map = new IdentityHashMap<NodeLayout, NodeWrapper>();
	private List<List<NodeWrapper>> layers = new ArrayList<List<NodeWrapper>>();
	private Map<Integer, Integer> crossesForLayers = new IdentityHashMap<Integer, Integer>();

	/**
	 * Filters the multiple connections from the two arrays.
	 * 
	 * @param a
	 * @param b
	 * @return
	 */
	private ArrayList<NodeLayout> unionOfNodes(NodeLayout[] a, NodeLayout[] b) {
		ArrayList<NodeLayout> res = new ArrayList<NodeLayout>();

		for (int i = 0; i < a.length; i++)
			res.add(a[i]);
		for (int i = 0; i < b.length; i++)
			if (!res.contains(b[i]))
				res.add(b[i]);

		return res;
	}

	/**
	 * Returns the number of crosses between the two nodes and those connected
	 * to them.
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
		ArrayList<NodeLayout> adjacentNodesOfA = unionOfNodes(
				nodeA.node.getPredecessingNodes(),
				nodeA.node.getSuccessingNodes());
		ArrayList<NodeLayout> adjacentNodesOfB = unionOfNodes(
				nodeB.node.getPredecessingNodes(),
				nodeB.node.getSuccessingNodes());

		for (NodeLayout aNode : adjacentNodesOfA) {
			ArrayList<Integer> alreadyCrossed = new ArrayList<Integer>();
			NodeWrapper aNodeWrapper = map.get(aNode);
			for (int i = 0; i < adjacentNodesOfB.size(); i++) {
				NodeWrapper nw = map.get(adjacentNodesOfB.get(i));
				if (!alreadyCrossed.contains(i) && nw != null) {
					// only if on the same side
					if ((nw.layer > nodeA.layer && aNodeWrapper.layer > nodeA.layer)
							|| (nw.layer < nodeA.layer && aNodeWrapper.layer < nodeA.layer)) {
						if (nodeA.index < nodeB.index) {
							if (aNodeWrapper.index > nw.index) {
								numOfCrosses++;
								alreadyCrossed.add(i);
							} else if (nw.index == aNodeWrapper.index) {
								if (nodeA.index >= nw.index) {
									// implies nodeB.index > nw.index
									if ((aNodeWrapper.layer > nw.layer && nodeA.layer < nw.layer)
											|| (aNodeWrapper.layer < nw.layer && nw.layer < nodeA.layer)) {
										// top-left or bottom-left quarter
										numOfCrosses++;
										alreadyCrossed.add(i);
									}
								} else if (nodeB.index <= nw.index) {
									// implies nodeA.index < nw.index
									if ((aNodeWrapper.layer > nw.layer && aNodeWrapper.layer < nodeB.layer)
											|| (aNodeWrapper.layer < nw.layer && aNodeWrapper.layer > nodeB.layer)) {
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
									if ((aNodeWrapper.layer > nw.layer && nodeB.layer > aNodeWrapper.layer)
											|| (aNodeWrapper.layer < nw.layer && aNodeWrapper.layer > nodeB.layer)) {
										// top-left or bottom-left quarter
										numOfCrosses++;
										alreadyCrossed.add(i);
									}
								} else if (nodeA.index <= nw.index) {
									// implies nodeA.index < nw.index
									if ((aNodeWrapper.layer > nw.layer && nw.layer > nodeA.layer)
											|| (aNodeWrapper.layer < nw.layer && nw.layer < nodeA.layer)) {
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
				if (layer.get(i).node != null && layer.get(i + 1).node != null) {
					int num1 = numberOfCrosses(layer.get(i), layer.get(i + 1));
					int num2 = numberOfCrosses(layer.get(i + 1), layer.get(i));
					if (num1 > num2 || (num1 == num2 && num1 > 0)) {
						if (!crossesForLayers.containsKey((layer.get(i).layer))
								|| crossesForLayers.get(layer.get(i).layer) > num2) {
							crossesForLayers.put(layer.get(i).layer, num2);
							res = true;
							int level = layer.get(0).layer;

							NodeWrapper tmp = layers.get(level).get(i);
							int tmpindex = layers.get(level).get(i).index;
							layers.get(level).get(i).index = layers.get(level)
									.get(i + 1).index;
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