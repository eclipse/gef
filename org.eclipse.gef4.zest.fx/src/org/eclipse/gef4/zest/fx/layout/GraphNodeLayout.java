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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.gef4.common.beans.property.ReadOnlyMapWrapperEx;
import org.eclipse.gef4.graph.Node;
import org.eclipse.gef4.layout.IEdgeLayout;
import org.eclipse.gef4.layout.INodeLayout;

import javafx.beans.property.ReadOnlyMapProperty;
import javafx.beans.property.ReadOnlyMapWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;

/**
 * The {@link GraphNodeLayout} is a {@link Node}-specific {@link INodeLayout}
 * implementation.
 *
 * @author mwienand
 *
 */
public class GraphNodeLayout implements INodeLayout {

	private final ReadOnlyMapWrapper<String, Object> attributesProperty = new ReadOnlyMapWrapperEx<>(this,
			ATTRIBUTES_PROPERTY, FXCollections.<String, Object> observableHashMap());
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
		this.attributesProperty.bindContentBidirectional(node.attributesProperty());
	}

	@Override
	public ReadOnlyMapProperty<String, Object> attributesProperty() {
		return attributesProperty.getReadOnlyProperty();
	}

	@Override
	public ObservableMap<String, Object> getAttributes() {
		return attributesProperty.get();
	}

	@Override
	public IEdgeLayout[] getIncomingConnections() {
		List<IEdgeLayout> incoming = new ArrayList<>();

		IEdgeLayout[] connections = context.getEdges();
		for (IEdgeLayout c : connections) {
			if (c.getTarget() == this) {
				incoming.add(c);
			}
		}

		return incoming.toArray(new IEdgeLayout[0]);
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
	public IEdgeLayout[] getOutgoingConnections() {
		List<IEdgeLayout> outgoing = new ArrayList<>();

		IEdgeLayout[] connections = context.getEdges();
		for (IEdgeLayout c : connections) {
			if (c.getSource() == this) {
				outgoing.add(c);
			}
		}

		return outgoing.toArray(new IEdgeLayout[0]);
	}

	@Override
	public INodeLayout[] getPredecessingNodes() {
		IEdgeLayout[] incomingConnections = getIncomingConnections();
		INodeLayout[] predecessors = new INodeLayout[incomingConnections.length];
		int i = 0;
		for (IEdgeLayout incomingConnection : incomingConnections) {
			predecessors[i++] = incomingConnection.getSource();
		}
		return predecessors;
	}

	@Override
	public INodeLayout[] getSuccessingNodes() {
		IEdgeLayout[] outgoingConnections = getOutgoingConnections();
		INodeLayout[] successors = new INodeLayout[outgoingConnections.length];
		int i = 0;
		for (IEdgeLayout outgoingConnection : outgoingConnections) {
			successors[i++] = outgoingConnection.getTarget();
		}
		return successors;
	}

}
