/*******************************************************************************
 * Copyright (c) 2012 Rene Kuhlemann.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Rene Kuhlemann
 *******************************************************************************/
package org.eclipse.gef4.layout.algorithms;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.gef4.layout.interfaces.NodeLayout;

/**
 * Structure to store nodes and there positions in the layers. Furthermore
 * predecessors and successors can be assigned to the nodes.
 * 
 * @author Adam Kovacs
 * 
 */
public class NodeWrapper {
	int index;
	final int layer;
	final NodeLayout node;
	final List<NodeWrapper> pred = new LinkedList<NodeWrapper>();
	final List<NodeWrapper> succ = new LinkedList<NodeWrapper>();
	private static final int PADDING = -1;

	NodeWrapper(NodeLayout n, int l) {
		node = n;
		layer = l;
	} // NodeLayout wrapper

	NodeWrapper(int l) {
		this(null, l);
	} // Dummy to connect two NodeLayout objects

	NodeWrapper() {
		this(null, PADDING);
	} // Padding for final refinement phase

	void addPredecessor(NodeWrapper node) {
		pred.add(node);
	}

	void addSuccessor(NodeWrapper node) {
		succ.add(node);
	}

	boolean isDummy() {
		return ((node == null) && (layer != PADDING));
	}

	boolean isPadding() {
		return ((node == null) && (layer == PADDING));
	}

	int getBaryCenter(List<NodeWrapper> list) {
		if (list.isEmpty())
			return (this.index);
		if (list.size() == 1)
			return (list.get(0).index);
		double barycenter = 0;
		for (NodeWrapper node : list)
			barycenter += node.index;
		return ((int) (barycenter / list.size())); // always rounding off to
													// avoid wrap around in
													// position refining!!!
	}

	int getPriorityDown() {
		if (isPadding())
			return (0);
		if (isDummy()) {
			if (succ != null && succ.size() > 0) {
				if (succ.get(0).isDummy())
					return (Integer.MAX_VALUE); // part of a straight line
				else
					return (Integer.MAX_VALUE >> 1); // start of a straight line
			}
		}
		return (pred.size());
	}

	int getPriorityUp() {
		if (isPadding())
			return (0);
		if (isDummy()) {
			if (pred != null && pred.size() > 0) {
				if (pred.get(0).isDummy())
					return (Integer.MAX_VALUE); // part of a straight line
				else
					return (Integer.MAX_VALUE >> 1); // start of a straight line
			}
		}
		return (succ.size());
	}

}