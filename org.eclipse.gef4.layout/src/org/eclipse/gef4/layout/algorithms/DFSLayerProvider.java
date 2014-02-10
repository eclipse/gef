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
	private ArrayList<NodeLayout> openedList = new ArrayList<NodeLayout>();
	private ArrayList<NodeLayout> closedList = new ArrayList<NodeLayout>();
	private ArrayList<NodeLayout> initClosedList = new ArrayList<NodeLayout>();
	private final List<List<NodeWrapper>> layers = new ArrayList<List<NodeWrapper>>();
	private final Map<NodeLayout, NodeWrapper> map = new IdentityHashMap<NodeLayout, NodeWrapper>();

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

	private void addToInitClosedList(NodeLayout node, int layout) {
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
		// TODO Auto-generated method stub
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
		// TODO Auto-generated method stub
		return assignedNodes;
	}

	public void addAssignedNode(NodeLayout node, int layer) {
		// TODO Auto-generated method stub
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
	private void addLayer(List<NodeLayout> list) {
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
	private ArrayList<NodeLayout> Unfold(NodeLayout toUnfold) {
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

	public List<List<NodeWrapper>> calculateLayers(List<NodeLayout> nodes,
			Map<NodeLayout, Integer> assignedNodes) {
		// TODO Auto-generated method stub
		openedList.clear();
		initClosedList.clear();
		closedList.clear();
		layers.clear();
		map.clear();

		// Assigns the given nodes to there layers
		if (assignedNodes != null) {
			for (NodeLayout node : nodes) {
				if (assignedNodes.containsKey(node))
					addToInitClosedList(node, assignedNodes.get(node));
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
						addLayer(layer);
					}
					ArrayList<NodeLayout> layer = new ArrayList<NodeLayout>();
					layer.add(node);
					addLayer(layer);
				}
			}
		}

		ArrayList<NodeLayout> startPoints = new ArrayList<NodeLayout>();
		if (nodes.size() > 0) {

			// Starts by finding a root or selecting the first from the assigned
			// ones
			if (layers.size() > 0 && layers.get(0).size() > 0)
				startPoints.add(layers.get(0).get(0).node);
			else if (layers.size() == 0) {
				startPoints.add(getRoots(nodes).get(0));
				addLayer(startPoints);
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

			// while openedList isn't empty it searches for further nodes and
			// adding them to the next layer
			NodeLayout toUnfold = startPoints.get(0);
			while (openedList.size() != 0) {
				ArrayList<NodeLayout> unfolded = Unfold(toUnfold);
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
						addLayer(layer);
					}
					openedList.addAll(0, unfolded);
				}
				closedList.add(toUnfold);
				openedList.remove(toUnfold);
				nodes.remove(toUnfold);

				if (openedList.size() != 0)
					toUnfold = openedList.get(0);
			}

			// If there is more nodes, but openedList is empty means that a new
			// root element is required
			if (nodes.size() > 0) {
				calculateLayers(nodes, null);
			}
		}
		return layers;
	}
}
