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

import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import org.eclipse.gef4.common.properties.PropertyStoreSupport;
import org.eclipse.gef4.geometry.planar.Point;
import org.eclipse.gef4.graph.Node;
import org.eclipse.gef4.layout.IConnectionLayout;
import org.eclipse.gef4.layout.IEntityLayout;
import org.eclipse.gef4.layout.INodeLayout;
import org.eclipse.gef4.layout.ISubgraphLayout;
import org.eclipse.gef4.layout.LayoutProperties;

public class GraphNodeLayout implements INodeLayout {

	// initialization context
	private GraphLayoutContext context;
	private PropertyStoreSupport ps = new PropertyStoreSupport(this);
	private Node node;
	private ISubgraphLayout subgraph;

	public GraphNodeLayout(GraphLayoutContext context, Node node) {
		this.context = context;
		this.node = node;
		// copy properties
		for (Entry<String, Object> e : node.getAttrs().entrySet()) {
			setProperty(e.getKey(), e.getValue());
		}
	}

	@Override
	public void addPropertyChangeListener(PropertyChangeListener listener) {
		ps.addPropertyChangeListener(listener);
	}

	@Override
	public IConnectionLayout[] getIncomingConnections() {
		List<IConnectionLayout> incoming = new ArrayList<IConnectionLayout>();

		IConnectionLayout[] connections = context.getConnections();
		for (IConnectionLayout c : connections) {
			if (c.getTarget() == this) {
				incoming.add(c);
			}
		}

		return incoming.toArray(new IConnectionLayout[0]);
	}

	@Override
	public Object[] getItems() {
		return new Object[] { node };
	}

	@Override
	public IConnectionLayout[] getOutgoingConnections() {
		List<IConnectionLayout> outgoing = new ArrayList<IConnectionLayout>();

		IConnectionLayout[] connections = context.getConnections();
		for (IConnectionLayout c : connections) {
			if (c.getSource() == this) {
				outgoing.add(c);
			}
		}

		return outgoing.toArray(new IConnectionLayout[0]);
	}

	@Override
	public IEntityLayout[] getPredecessingEntities() {
		return getPredecessingNodes();
	}

	@Override
	public INodeLayout[] getPredecessingNodes() {
		IConnectionLayout[] incomingConnections = getIncomingConnections();
		INodeLayout[] predecessors = new INodeLayout[incomingConnections.length];
		int i = 0;
		for (IConnectionLayout incomingConnection : incomingConnections) {
			predecessors[i++] = incomingConnection.getSource();
		}
		return predecessors;
	}

	@Override
	public Object getProperty(String name) {
		return ps.getProperty(name);
	}

	@Override
	public ISubgraphLayout getSubgraph() {
		return subgraph;
	}

	@Override
	public IEntityLayout[] getSuccessingEntities() {
		return getSuccessingNodes();
	}

	@Override
	public INodeLayout[] getSuccessingNodes() {
		IConnectionLayout[] outgoingConnections = getOutgoingConnections();
		INodeLayout[] successors = new INodeLayout[outgoingConnections.length];
		int i = 0;
		for (IConnectionLayout outgoingConnection : outgoingConnections) {
			successors[i++] = outgoingConnection.getTarget();
		}
		return successors;
	}

	@Override
	public void prune(ISubgraphLayout subgraph) {
		// TODO: fire events
		if (this.subgraph != null) {
			this.subgraph.removeNodes(new INodeLayout[] { this });
		}
		this.subgraph = subgraph;
		subgraph.addNodes(new INodeLayout[] { this });
	}

	@Override
	public void removePropertyChangeListener(PropertyChangeListener listener) {
		ps.removePropertyChangeListener(listener);
	}

	@Override
	public void setProperty(String name, Object value) {
		Object oldValue = ps.getProperty(name);
		// TODO: remove NaN check here and ensure NaN is not passed in
		if (LayoutProperties.LOCATION_PROPERTY.equals(name)) {
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
		// send notification
		if (value != oldValue && (value == null || !value.equals(oldValue))) {
			if (LayoutProperties.LOCATION_PROPERTY.equals(name)) {
				context.fireNodeMovedEvent(this);
			} else if (LayoutProperties.SIZE_PROPERTY.equals(name)) {
				context.fireNodeResizedEvent(this);
			} else if ("hidden".equals(name)) {
				context.firePruningChanged(this);
			}
		}
	}

}
