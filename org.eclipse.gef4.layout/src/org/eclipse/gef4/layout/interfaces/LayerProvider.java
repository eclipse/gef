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
package org.eclipse.gef4.layout.interfaces;

import java.util.List;
import java.util.Map;

import org.eclipse.gef4.layout.algorithms.NodeWrapper;

/**
 * 
 * An interface for creating layers. Interface for parameterizable layering
 * heuristics.
 * 
 */
public interface LayerProvider {

	/**
	 * Returns the map of assignedNodes.
	 * 
	 * @return
	 */
	Map<NodeLayout, Integer> getAssignedNodes();

	/**
	 * Collects the assignedNodes.
	 * 
	 * @param node
	 * @param layer
	 *            number of the layer the node is assigned to
	 */
	void addAssignedNode(NodeLayout node, int layer);

	/**
	 * Clears the map of assignedNodes.
	 */
	void clearAssignedNodes();

	/**
	 * Creating layers of the nodes and makes it possible to assign layers to
	 * those nodes.
	 * 
	 * @param nodes
	 *            List of all the nodes that needs to be organized
	 * @param assignedNodes
	 *            Collection of the nodes which have preassigned layers
	 * @return
	 */
	List<List<NodeWrapper>> calculateLayers(List<NodeLayout> nodes,
			Map<NodeLayout, Integer> assignedNodes);
}
