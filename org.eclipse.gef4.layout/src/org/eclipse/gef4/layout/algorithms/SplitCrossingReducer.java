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
import java.util.Random;

import org.eclipse.gef4.layout.interfaces.CrossingReducer;
import org.eclipse.gef4.layout.interfaces.NodeLayout;

/**
 * Implements the CrossingReducer interface. This algorithm divides each layer
 * by a pivot node based on the relative position of connected nodes and decides
 * which side of the pivot point it should be for the fewer edge crossing.
 * 
 * @author Adam Kovacs
 * 
 */
public class SplitCrossingReducer implements CrossingReducer {
	private final Map<NodeLayout, NodeWrapper> map = new IdentityHashMap<NodeLayout, NodeWrapper>();

	/**
	 * Filters the multiple connections from the two arrays
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
	 * Selects the pivot node by random and decides the order.
	 * 
	 * @param layer
	 * @return
	 */
	private List<NodeWrapper> splitHeuristic(List<NodeWrapper> layer) {
		ArrayList<NodeWrapper> left = new ArrayList<NodeWrapper>();
		ArrayList<NodeWrapper> right = new ArrayList<NodeWrapper>();

		if (layer.size() < 1)
			return layer;
		Random random = new Random();
		NodeWrapper pivot = layer.get(random.nextInt(layer.size()));
		// NodeWrapper pivot = layer.get(0);
		// NodeWrapper pivot = layer.get((int)(layer.size() / 2));
		for (NodeWrapper node : layer) {
			if (!node.equals(pivot) && node.node != null && pivot.node != null) {
				int num1 = numberOfCrosses(node, pivot);
				int num2 = numberOfCrosses(pivot, node);
				if (num1 < num2)
					left.add(node);
				else if (num1 > num2)
					right.add(node);
				else {
					if (num1 == num2 && num1 > 0) {
						int tmpindex = map.get(pivot.node).index;
						map.get(pivot.node).index = map.get(node.node).index;
						map.get(node.node).index = tmpindex;
					}
					if (node.index < pivot.index)
						left.add(node);
					else
						right.add(node);
				}
			}
		}

		ArrayList<NodeWrapper> res = new ArrayList<NodeWrapper>();
		res.addAll(splitHeuristic(left));
		res.add(pivot);
		res.addAll(splitHeuristic(right));
		return res;
	}

	public Map<NodeLayout, NodeWrapper> crossReduction(
			List<List<NodeWrapper>> nodes) {
		// TODO Auto-generated method stub

		// Building the map
		for (List<NodeWrapper> layer : nodes)
			for (NodeWrapper nw : layer)
				map.put(nw.node, nw);
		for (int i = 0; i < nodes.size(); i++) {
			if (!nodes.get(i).isEmpty()) {
				splitHeuristic(nodes.get(i));
			}
		}

		return map;
	}
}