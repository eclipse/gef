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

import org.eclipse.gef4.common.beans.property.ReadOnlyMapWrapperEx;
import org.eclipse.gef4.graph.Edge;
import org.eclipse.gef4.layout.IConnectionLayout;
import org.eclipse.gef4.layout.INodeLayout;

import javafx.beans.property.ReadOnlyMapProperty;
import javafx.beans.property.ReadOnlyMapWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;

/**
 * The {@link GraphEdgeLayout} is an {@link Edge}-specific
 * {@link IConnectionLayout} implementation.
 *
 * @author mwienand
 *
 */
public class GraphEdgeLayout implements IConnectionLayout {

	private final ReadOnlyMapWrapper<String, Object> attributesProperty = new ReadOnlyMapWrapperEx<>(this,
			ATTRIBUTES_PROPERTY, FXCollections.<String, Object> observableHashMap());
	private GraphLayoutContext context;
	private Edge edge;

	/**
	 * Constructs a new {@link GraphEdgeLayout} for the given {@link Edge} in
	 * the given {@link GraphLayoutContext}.
	 *
	 * @param context
	 *            The {@link GraphLayoutContext} that contains this
	 *            {@link GraphEdgeLayout}.
	 * @param edge
	 *            The corresponding {@link Edge}.
	 */
	public GraphEdgeLayout(GraphLayoutContext context, Edge edge) {
		this.context = context;
		this.edge = edge;
		this.attributesProperty.bindContentBidirectional(edge.attributesProperty());
	}

	@Override
	public ReadOnlyMapProperty<String, Object> attributesProperty() {
		return attributesProperty.getReadOnlyProperty();
	}

	@Override
	public ObservableMap<String, Object> getAttributes() {
		return attributesProperty.get();
	}

	/**
	 * Returns the corresponding {@link Edge}.
	 *
	 * @return The corresponding {@link Edge}.
	 */
	public Edge getEdge() {
		return edge;
	}

	@Override
	public INodeLayout getSource() {
		return context.getNodeLayout(edge.getSource());
	}

	@Override
	public INodeLayout getTarget() {
		return context.getNodeLayout(edge.getTarget());
	}

}
