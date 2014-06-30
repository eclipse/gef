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
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.gef4.layout.interfaces.ConnectionLayout;
import org.eclipse.gef4.layout.interfaces.LayerProvider;
import org.eclipse.gef4.layout.interfaces.NodeLayout;

/**
 * Processing the nodes based on depth first search and creating a list of
 * layers
 * 
 * @author Adam Kovacs
 * 
 */
public class DFSLayerProvider implements LayerProvider {

	private Map<NodeLayout, Integer> assignedNodes = new IdentityHashMap<NodeLayout, Integer>();

	/**
	 * Returns the mutual connections of the two array given as parameters.
	 * 
	 * @param a
	 * @param b
	 * @return
	 */
	private List<ConnectionLayout> intersectOfConnections(ConnectionLayout[] a,
			ConnectionLayout[] b) {
		ArrayList<ConnectionLayout> res = new ArrayList<ConnectionLayout>();

		for (int i = 0; i < a.length; i++)
			for (int j = 0; j < b.length; j++)
				if (a[i].equals(b[j]))
					res.add(a[i]);

		return res;
	}

	private void addToInitClosedList(NodeLayout node, int layout,
			List<NodeLayout> initClosedList, Map<NodeLayout, NodeWrapper> map) {
		NodeWrapper nw = new NodeWrapper(node, layout);
		map.put(node, nw);
		initClosedList.add(node);
	}

	/**
	 * Finds a root element in the list of nodes based on there connections.
	 * 
	 * @param nodes
	 * @return
	 */
	public ArrayList<NodeLayout> getRoots(List<NodeLayout> nodes) {
		ArrayList<NodeLayout> res = new ArrayList<NodeLayout>();

		for (NodeLayout node : nodes) {
			// directed edges
			if (node.getIncomingConnections().length == 0)
				res.add(node);
			else {
				int sizeOfIntersect = intersectOfConnections(
						node.getIncomingConnections(),
						node.getOutgoingConnections()).size();
				// there are more outgoing edges, besides the bidirectionals
				if (node.getOutgoingConnections().length > sizeOfIntersect)
					res.add(node);
				// only bidirectional edges, no incoming directed edges
				if (node.getIncomingConnections().length == sizeOfIntersect
						&& node.getOutgoingConnections().length == sizeOfIntersect)
					res.add(node);
			}
		}

		// if no sources then we only have bidirectional edges and/or cycles
		if (res.size() == 0)
			res.add(nodes.get(0));

		return res;
	}

	public Map<NodeLayout, Integer> getAssignedNodes() {
		return assignedNodes;
	}

	public void addAssignedNode(NodeLayout node, int layer) {
		assignedNodes.put(node, layer);
	}

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
	private void addLayer(List<NodeLayout> list,
			List<List<NodeWrapper>> layers, Map<NodeLayout, NodeWrapper> map) {
		ArrayList<NodeWrapper> layer = new ArrayList<NodeWrapper>(list.size());
		for (NodeLayout node : list) {
			// wrap each NodeLayout with the internal data object and provide a
			// corresponding mapping
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
	private ArrayList<NodeLayout> Unfold(NodeLayout toUnfold,
			Set<NodeLayout> openedList, Set<NodeLayout> closedList) {
		ArrayList<NodeLayout> res = new ArrayList<NodeLayout>();

		for (int i = 0; i < toUnfold.getOutgoingConnections().length; i++) {
			NodeLayout endPoint = toUnfold.getOutgoingConnections()[i]
					.getTarget();
			if (endPoint.equals(toUnfold))
				endPoint = toUnfold.getOutgoingConnections()[i].getSource();
			if (!closedList.contains(endPoint)
					&& !openedList.contains(endPoint)
					&& !res.contains(endPoint))
				res.add(endPoint);
		}
		for (int i = 0; i < toUnfold.getIncomingConnections().length; i++) {
			NodeLayout endPoint = toUnfold.getIncomingConnections()[i]
					.getTarget();
			if (endPoint.equals(toUnfold))
				endPoint = toUnfold.getIncomingConnections()[i].getSource();
			if (!closedList.contains(endPoint)
					&& !openedList.contains(endPoint)
					&& !res.contains(endPoint))
				res.add(endPoint);
		}

		return res;
	}

	public List<List<NodeWrapper>> calculateLayers(List<NodeLayout> nodeLayouts) {
		List<NodeLayout> nodes = new ArrayList<NodeLayout>(nodeLayouts);
		Set<NodeLayout> openedList = new HashSet<NodeLayout>();
		List<NodeLayout> initClosedList = new ArrayList<NodeLayout>();
		Set<NodeLayout> closedList = new HashSet<NodeLayout>();
		List<List<NodeWrapper>> layers = new ArrayList<List<NodeWrapper>>();
		Map<NodeLayout, NodeWrapper> map = new IdentityHashMap<NodeLayout, NodeWrapper>();

		// Assigns the given nodes to there layers
		if (assignedNodes.size() > 0) {
			for (NodeLayout node : nodes) {
				if (assignedNodes.containsKey(node))
					addToInitClosedList(node, assignedNodes.get(node),
							initClosedList, map);
			}
		}

		// Only at first iteration, clearing initClosedList, starting to build
		// layers
		if (initClosedList.size() > 0) {
			closedList.addAll(initClosedList);
			nodes.removeAll(initClosedList);
			initClosedList.clear();

			for (NodeLayout node : closedList) {
				if (map.get(node).layer < layers.size()) {
					layers.get(map.get(node).layer).add(map.get(node));
					updateIndex(layers.get(map.get(node).layer));
				} else {
					while (map.get(node).layer != layers.size()) {
						ArrayList<NodeLayout> layer = new ArrayList<NodeLayout>();
						addLayer(layer, layers, map);
					}
					ArrayList<NodeLayout> layer = new ArrayList<NodeLayout>();
					layer.add(node);
					addLayer(layer, layers, map);
				}
			}
		}

		ArrayList<NodeLayout> startPoints = new ArrayList<NodeLayout>();
		// Starts by finding a root or selecting the first from the assigned
		// ones
		if (layers.size() > 0 && layers.get(0).size() > 0)
			startPoints.add(layers.get(0).get(0).node);
		else if (layers.size() == 0) {
			startPoints.add(getRoots(nodes).get(0));
			addLayer(startPoints, layers, map);
		} else {
			startPoints.add(getRoots(nodes).get(0));
			for (NodeLayout startPoint : startPoints) {
				if (!map.containsKey(startPoint)) {
					NodeWrapper nw = new NodeWrapper(startPoint, 0);
					map.put(startPoint, nw);
					layers.get(0).add(nw);
				}
			}
			updateIndex(layers.get(0));
		}
		openedList.addAll(startPoints);
		NodeLayout toUnfold = startPoints.get(0);

		while (nodes.size() > 0) {
			// while openedList isn't empty it searches for further nodes and
			// adding them to the next layer
			while (openedList.size() != 0) {
				ArrayList<NodeLayout> unfolded = Unfold(toUnfold, openedList,
						closedList);
				if (unfolded.size() > 0) {
					int level = map.get(toUnfold).layer + 1;
					if (level < layers.size()) {
						for (NodeLayout n : unfolded) {
							if (!map.containsKey(n)) {
								NodeWrapper nw = new NodeWrapper(n, level);
								map.put(n, nw);
								layers.get(level).add(nw);
							}
						}
						updateIndex(layers.get(level));
					} else {
						ArrayList<NodeLayout> layer = new ArrayList<NodeLayout>();
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
				final NodeLayout node = nodes.get(0);
				openedList.add(node);
				NodeWrapper nw = new NodeWrapper(node, 0);
				map.put(node, nw);
				layers.get(0).add(nw);
			}
		}
		return layers;
	}
}
