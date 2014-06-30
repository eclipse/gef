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
import java.util.Arrays;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.gef4.layout.interfaces.LayerProvider;
import org.eclipse.gef4.layout.interfaces.NodeLayout;

public class SimpleLayerProvider implements LayerProvider {

	private static final int MAX_LAYERS = 10;
	private final List<List<NodeWrapper>> layers = new ArrayList<List<NodeWrapper>>(
			MAX_LAYERS);
	private final Map<NodeLayout, NodeWrapper> map = new IdentityHashMap<NodeLayout, NodeWrapper>();

	private static List<NodeLayout> findRoots(List<NodeLayout> list) {
		List<NodeLayout> roots = new ArrayList<NodeLayout>();
		for (NodeLayout iter : list) { // no predecessors means: this is a root,
										// add it to list
			if (iter.getPredecessingNodes().length == 0)
				roots.add(iter);
		}
		return (roots);
	}

	/**
	 * Wraps all {@link NodeLayout} objects into an internal presentation
	 * {@link NodeWrapper} and inserts dummy wrappers into the layers between an
	 * object and their predecessing nodes if necessary. Finally, all nodes are
	 * chained over immediate adjacent layers down to their predecessors. This
	 * is necessary to apply the final step of the Sugiyama algorithm to refine
	 * the node position within a layer.
	 * 
	 * @param list
	 *            : List of all {@link NodeLayout} objects within the current
	 *            layer
	 */
	private void addLayer(List<NodeLayout> list) {
		ArrayList<NodeWrapper> layer = new ArrayList<NodeWrapper>(list.size());
		for (NodeLayout node : list) {
			// wrap each NodeLayout with the internal data object and provide a
			// corresponding mapping
			NodeWrapper nw = new NodeWrapper(node, layers.size());
			map.put(node, nw);
			layer.add(nw);
			// insert dummy nodes if the adjacent layer does not contain the
			// predecessor
			for (NodeLayout node_predecessor : node.getPredecessingNodes()) { // for
																				// all
																				// predecessors
				NodeWrapper nw_predecessor = map.get(node_predecessor);
				if (nw_predecessor != null) {
					for (int level = nw_predecessor.layer + 1; level < nw.layer; level++) {
						// add "virtual" wrappers (dummies) to the layers in
						// between
						// virtual wrappers are in fact parts of a double linked
						// list
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

	public List<List<NodeWrapper>> calculateLayers(List<NodeLayout> nodes) {
		map.clear();

		List<NodeLayout> predecessors = findRoots(nodes);
		nodes.removeAll(predecessors); // nodes now contains only nodes that are
										// no roots
		addLayer(predecessors);
		for (int level = 1; nodes.isEmpty() == false; level++) {
			if (level > MAX_LAYERS)
				throw new RuntimeException(
						"Graphical tree exceeds maximum depth of " + MAX_LAYERS
								+ "! (Graph not directed? Cycles?)");
			List<NodeLayout> layer = new ArrayList<NodeLayout>();
			for (NodeLayout item : nodes) {
				if (predecessors.containsAll(Arrays.asList(item
						.getPredecessingNodes())))
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
