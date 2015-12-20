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
import java.util.Map;

import org.eclipse.gef4.common.properties.KeyedPropertyChangeEvent;
import org.eclipse.gef4.common.properties.PropertyChangeNotifierSupport;
import org.eclipse.gef4.graph.Edge;
import org.eclipse.gef4.layout.IConnectionLayout;
import org.eclipse.gef4.layout.INodeLayout;
import org.eclipse.gef4.layout.LayoutProperties;
import org.eclipse.gef4.zest.fx.ZestProperties;

/**
 * The {@link GraphEdgeLayout} is an {@link Edge}-specific
 * {@link IConnectionLayout} implementation.
 *
 * @author mwienand
 *
 */
public class GraphEdgeLayout implements IConnectionLayout {

	private PropertyChangeNotifierSupport pcs = new PropertyChangeNotifierSupport(this);
	private PropertyChangeListener edgeAttributesListener = new PropertyChangeListener() {

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
		this.edge.addPropertyChangeListener(edgeAttributesListener);

		// graph directed?
		Object type = context.getGraph().getAttributes().get(ZestProperties.GRAPH_TYPE);
		if (type == ZestProperties.GRAPH_TYPE_DIRECTED) {
			getAttributes().put(LayoutProperties.DIRECTED_PROPERTY, true);
		}
	}

	@Override
	public void addPropertyChangeListener(PropertyChangeListener listener) {
		pcs.addPropertyChangeListener(listener);
	}

	@Override
	public Map<String, Object> getAttributes() {
		return edge.getAttributes();
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

	@Override
	public void removePropertyChangeListener(PropertyChangeListener listener) {
		pcs.removePropertyChangeListener(listener);
	}
}
