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
import java.util.Map.Entry;

import org.eclipse.gef4.common.properties.PropertyStoreSupport;
import org.eclipse.gef4.graph.Edge;
import org.eclipse.gef4.graph.Graph;
import org.eclipse.gef4.layout.ILayoutProperties;
import org.eclipse.gef4.layout.interfaces.ConnectionLayout;
import org.eclipse.gef4.layout.interfaces.NodeLayout;

public class GraphEdgeLayout implements ConnectionLayout {

	private GraphLayoutContext context;
	private Edge edge;
	private PropertyStoreSupport pss = new PropertyStoreSupport(this);

	public GraphEdgeLayout(GraphLayoutContext context, Edge edge) {
		this.context = context;
		this.edge = edge;

		// graph directed?
		Object type = context.getGraph().getAttrs()
				.get(Graph.Attr.Key.GRAPH_TYPE.toString());
		if (type == Graph.Attr.Value.CONNECTIONS_DIRECTED
				|| type == Graph.Attr.Value.GRAPH_DIRECTED) {
			setProperty(ILayoutProperties.DIRECTED_PROPERTY, true);
		}

		// copy properties
		for (Entry<String, Object> e : edge.getAttrs().entrySet()) {
			setProperty(e.getKey(), e.getValue());
		}
	}

	@Override
	public void addPropertyChangeListener(PropertyChangeListener listener) {
		pss.addPropertyChangeListener(listener);
	}

	@Override
	public Object getProperty(String name) {
		return pss.getProperty(name);
	}

	@Override
	public NodeLayout getSource() {
		return context.getNodeLayout(edge.getSource());
	}

	@Override
	public NodeLayout getTarget() {
		return context.getNodeLayout(edge.getTarget());
	}

	@Override
	public void removePropertyChangeListener(PropertyChangeListener listener) {
		pss.removePropertyChangeListener(listener);
	}

	@Override
	public void setProperty(String name, Object value) {
		pss.setProperty(name, value);
	}

}
