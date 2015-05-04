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
import java.beans.PropertyChangeSupport;
import java.util.Map.Entry;

import org.eclipse.gef4.common.properties.PropertyStoreSupport;
import org.eclipse.gef4.graph.Edge;
import org.eclipse.gef4.layout.IConnectionLayout;
import org.eclipse.gef4.layout.INodeLayout;
import org.eclipse.gef4.layout.LayoutProperties;
import org.eclipse.gef4.zest.fx.ZestProperties;

public class GraphEdgeLayout implements IConnectionLayout {

	private GraphLayoutContext context;
	private Edge edge;
	private PropertyChangeSupport pcs = new PropertyChangeSupport(this);
	private PropertyStoreSupport pss = new PropertyStoreSupport(this, pcs);

	public GraphEdgeLayout(GraphLayoutContext context, Edge edge) {
		this.context = context;
		this.edge = edge;

		// graph directed?
		Object type = context.getGraph().getAttrs().get(ZestProperties.GRAPH_TYPE);
		if (type == ZestProperties.GRAPH_TYPE_DIRECTED) {
			setProperty(LayoutProperties.DIRECTED_PROPERTY, true);
		}

		// copy properties
		for (Entry<String, Object> e : edge.getAttrs().entrySet()) {
			setProperty(e.getKey(), e.getValue());
		}
	}

	@Override
	public void addPropertyChangeListener(PropertyChangeListener listener) {
		pcs.addPropertyChangeListener(listener);
	}

	public Edge getEdge() {
		return edge;
	}

	@Override
	public Object getProperty(String name) {
		return pss.getProperty(name);
	}

	@Override
	public INodeLayout getSource() {
		return context.getNodeLayout(edge.getSource());
	}

	@Override
	public INodeLayout getTarget() {
		return context.getNodeLayout(edge.getTarget());
	}

	@Override
	public void removePropertyChangeListener(PropertyChangeListener listener) {
		pcs.removePropertyChangeListener(listener);
	}

	@Override
	public void setProperty(String name, Object value) {
		pss.setProperty(name, value);
	}

}
