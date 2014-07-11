/*******************************************************************************
 * Copyright (c) 2014 itemis AG and others.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Matthias Wienand (itemis AG) - initial API & implementation
 *     
 *******************************************************************************/
package org.eclipse.gef4.zest.fx.layout;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import org.eclipse.gef4.geometry.planar.Point;
import org.eclipse.gef4.graph.Node;
import org.eclipse.gef4.layout.PropertyStoreSupport;
import org.eclipse.gef4.layout.interfaces.ConnectionLayout;
import org.eclipse.gef4.layout.interfaces.EntityLayout;
import org.eclipse.gef4.layout.interfaces.NodeLayout;
import org.eclipse.gef4.layout.interfaces.SubgraphLayout;

public class GraphNodeLayout implements NodeLayout {

	// initialization context
	private GraphLayoutContext context;
	private PropertyStoreSupport ps = new PropertyStoreSupport();
	private Node node;
	private SubgraphLayout subgraph;

	public GraphNodeLayout(GraphLayoutContext context, Node node) {
		this.context = context;
		this.node = node;
		// copy properties
		for (Entry<String, Object> e : node.getAttrs().entrySet()) {
			setProperty(e.getKey(), e.getValue());
		}
	}

	@Override
	public ConnectionLayout[] getIncomingConnections() {
		List<ConnectionLayout> incoming = new ArrayList<ConnectionLayout>();

		ConnectionLayout[] connections = context.getConnections();
		for (ConnectionLayout c : connections) {
			if (c.getTarget() == this) {
				incoming.add(c);
			}
		}

		return incoming.toArray(new ConnectionLayout[0]);
	}

	@Override
	public Object[] getItems() {
		return new Object[] { node };
	}

	// FIXME: duplicate code! getIncomingConnections ~ getOutgoingConnections
	@Override
	public ConnectionLayout[] getOutgoingConnections() {
		List<ConnectionLayout> incoming = new ArrayList<ConnectionLayout>();

		ConnectionLayout[] connections = context.getConnections();
		for (ConnectionLayout c : connections) {
			if (c.getSource() == this) {
				incoming.add(c);
			}
		}

		return incoming.toArray(new ConnectionLayout[0]);
	}

	@Override
	public EntityLayout[] getPredecessingEntities() {
		return getPredecessingNodes();
	}

	@Override
	public NodeLayout[] getPredecessingNodes() {
		NodeLayout[] nodes = context.getNodes();

		int index = 0;
		while (nodes[index] != this) {
			index++;
		}

		int length = index;
		if (length == 0) {
			return new NodeLayout[0];
		}

		NodeLayout[] predecessors = new NodeLayout[length];
		for (int i = 0; i < predecessors.length; i++) {
			predecessors[i] = nodes[i];
		}
		return predecessors;
	}

	@Override
	public Object getProperty(String name) {
		return ps.getProperty(name);
	}

	@Override
	public SubgraphLayout getSubgraph() {
		return subgraph;
	}

	@Override
	public EntityLayout[] getSuccessingEntities() {
		return getSuccessingNodes();
	}

	@Override
	public NodeLayout[] getSuccessingNodes() {
		NodeLayout[] nodes = context.getNodes();

		int index = 0;
		while (nodes[index] != this) {
			index++;
		}

		int offset = index + 1;
		if (offset >= nodes.length) {
			return new NodeLayout[0];
		}

		NodeLayout[] successors = new NodeLayout[nodes.length - offset];
		for (int i = 0; i < successors.length; i++) {
			successors[i] = nodes[offset + i];
		}
		return successors;
	}

	@Override
	public void prune(SubgraphLayout subgraph) {
		// TODO: fire events
		if (this.subgraph != null) {
			this.subgraph.removeNodes(new NodeLayout[] { this });
		}
		this.subgraph = subgraph;
		subgraph.addNodes(new NodeLayout[] { this });
	}

	@Override
	public void setProperty(String name, Object value) {
		// TODO: remove NaN check here and ensure NaN is not passed in
		if ("location".equals(name)) {
			if (value instanceof Point) {
				Point p = (Point) value;
				if (Double.isNaN(p.x)) {
					p.x = 0;
				}
				if (Double.isNaN(p.y)) {
					p.y = 0;
				}
			}
		}
		ps.setProperty(name, value);
	}

}
