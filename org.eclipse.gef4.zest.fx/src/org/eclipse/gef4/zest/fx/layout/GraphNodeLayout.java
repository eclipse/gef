/*******************************************************************************
 * Copyright (c) 2014, 2015 itemis AG and others.
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

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.gef4.common.properties.KeyedPropertyChangeEvent;
import org.eclipse.gef4.common.properties.PropertyChangeNotifierSupport;
import org.eclipse.gef4.graph.Node;
import org.eclipse.gef4.layout.IConnectionLayout;
import org.eclipse.gef4.layout.INodeLayout;

/**
 * The {@link GraphNodeLayout} is a {@link Node}-specific {@link INodeLayout}
 * implementation.
 *
 * @author mwienand
 *
 */
public class GraphNodeLayout implements INodeLayout {

	private PropertyChangeNotifierSupport pcs = new PropertyChangeNotifierSupport(this);
	private PropertyChangeListener nodeAttributesListener = new PropertyChangeListener() {

		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			// forward any property change events with us as source
			if (evt instanceof KeyedPropertyChangeEvent) {
				pcs.fireKeyedPropertyChange(evt.getPropertyName(), ((KeyedPropertyChangeEvent) evt).getKey(),
						evt.getOldValue(), evt.getNewValue());
			} else {
				pcs.firePropertyChange(evt.getPropertyName(), evt.getOldValue(), evt.getNewValue());
			}
		}
	};

	private GraphLayoutContext context;
	private Node node;

	/**
	 * Constructs a new {@link GraphNodeLayout} for the given {@link Node} in
	 * the given {@link GraphLayoutContext}.
	 *
	 * @param context
	 *            The {@link GraphLayoutContext} that contains this
	 *            {@link GraphNodeLayout}.
	 * @param node
	 *            The corresponding {@link Node}.
	 */
	public GraphNodeLayout(GraphLayoutContext context, Node node) {
		this.context = context;
		this.node = node;
		node.addPropertyChangeListener(nodeAttributesListener);
	}

	@Override
	public void addPropertyChangeListener(PropertyChangeListener listener) {
		pcs.addPropertyChangeListener(listener);
	}

	@Override
	public Map<String, Object> getAttributes() {
		return node.getAttributes();
	}

	@Override
	public IConnectionLayout[] getIncomingConnections() {
		List<IConnectionLayout> incoming = new ArrayList<>();

		IConnectionLayout[] connections = context.getEdges();
		for (IConnectionLayout c : connections) {
			if (c.getTarget() == this) {
				incoming.add(c);
			}
		}

		return incoming.toArray(new IConnectionLayout[0]);
	}

	/**
	 * Returns the corresponding {@link Node}.
	 *
	 * @return The corresponding {@link Node}.
	 */
	public Node getNode() {
		return node;
	}

	@Override
	public IConnectionLayout[] getOutgoingConnections() {
		List<IConnectionLayout> outgoing = new ArrayList<>();

		IConnectionLayout[] connections = context.getEdges();
		for (IConnectionLayout c : connections) {
			if (c.getSource() == this) {
				outgoing.add(c);
			}
		}

		return outgoing.toArray(new IConnectionLayout[0]);
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
	public void removePropertyChangeListener(PropertyChangeListener listener) {
		pcs.removePropertyChangeListener(listener);
	}

}
