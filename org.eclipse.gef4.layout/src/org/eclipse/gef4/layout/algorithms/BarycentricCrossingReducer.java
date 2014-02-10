/*******************************************************************************
 * Copyright (c) 2012 Rene Kuhlemann.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Rene Kuhlemann - provided first version of code based on the initial paper
 *    		of Sugiyama et al. (http://dx.doi.org/10.1109/TSMC.1981.4308636),
 *          associated to bugzilla entry #384730 
 *******************************************************************************/
package org.eclipse.gef4.layout.algorithms;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.gef4.layout.interfaces.CrossingReducer;
import org.eclipse.gef4.layout.interfaces.NodeLayout;

public class BarycentricCrossingReducer implements CrossingReducer {

	private List<List<NodeWrapper>> layers = new ArrayList<List<NodeWrapper>>();
	private Map<NodeLayout, NodeWrapper> map = new IdentityHashMap<NodeLayout, NodeWrapper>();
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
	 * Reduces connection crossings between two adjacent layers by a combined
	 * top-down and bottom-up approach. It uses a heuristic approach based on
	 * the predecessor's barycenter.
	 */
	private void reduceCrossings() {
		for (int round = 0; round < MAX_SWEEPS; round++) {
			if ((round & 1) == 0) { // if round is even then do a bottom-up scan
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

	private void refineLayers() { // from
									// Sugiyama
		// paper: down,
		// up and down
		// again yields best results, wonder why...
		for (int index = 1; index < layers.size(); index++)
			refineLayersDown(layers.get(index));
		for (int index = layers.size() - 2; index >= 0; index--)
			refineLayersUp(layers.get(index));
		for (int index = 1; index < layers.size(); index++)
			refineLayersDown(layers.get(index));
	}

	private void refineLayersDown(List<NodeWrapper> layer) {
		// first, get a priority list
		List<NodeWrapper> list = new ArrayList<NodeWrapper>(layer);
		Collections.sort(list, new Comparator<NodeWrapper>() {
			public int compare(NodeWrapper node1, NodeWrapper node2) {
				return (node2.getPriorityDown() - node1.getPriorityDown()); // descending
				// ordering!!!
			}
		});
		// second, remove padding from the layer's end and place them in front
		// of the current node to improve its position
		for (NodeWrapper iter : list) {
			if (iter.isPadding())
				break; // break, if there are no more "real" nodes
			int delta = iter.getBaryCenter(iter.pred) - iter.index; // distance
			// to new
			// position
			for (int i = 0; i < delta; i++)
				layer.add(iter.index, layer.remove(last));
		}
		updateIndex(layer);
	}

	private void refineLayersUp(List<NodeWrapper> layer) {
		// first, get a priority list
		List<NodeWrapper> list = new ArrayList<NodeWrapper>(layer);
		Collections.sort(list, new Comparator<NodeWrapper>() {
			public int compare(NodeWrapper node1, NodeWrapper node2) {
				return (node2.getPriorityUp() - node1.getPriorityUp()); // descending
				// ordering!!!
			}
		});
		// second, remove padding from the layer's end and place them in front
		// of the current node to improve its position
		for (NodeWrapper iter : list) {
			if (iter.isPadding())
				break; // break, if there are no more "real" nodes
			int delta = iter.getBaryCenter(iter.succ) - iter.index; // distance
			// to new
			// position
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

	public Map<NodeLayout, NodeWrapper> crossReduction(
			List<List<NodeWrapper>> nodes) {
		this.layers = nodes;
		// TODO Auto-generated method stub
		padLayers();
		for (int i = 0; i < layers.size(); i++) { // reduce and
			// refine
			// iteratively, depending on
			// the depth of the graph
			reduceCrossings();
			refineLayers();
		}
		reduceCrossings();

		return map;
	}
}
