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
package org.eclipse.gef4.zest.fx.models;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.AbstractMap;
import java.util.IdentityHashMap;
import java.util.Map;

import org.eclipse.gef4.common.properties.IPropertyChangeNotifier;
import org.eclipse.gef4.graph.Graph;
import org.eclipse.gef4.layout.ILayoutAlgorithm;
import org.eclipse.gef4.layout.ILayoutContext;
import org.eclipse.gef4.layout.algorithms.SpringLayoutAlgorithm;

public class LayoutModel implements IPropertyChangeNotifier {

	private static SpringLayoutAlgorithm getDefaultAlgorithm() {
		return new SpringLayoutAlgorithm();
	}

	public static final String LAYOUT_CONTEXT_PROPERTY = "layoutContext";

	private Map<Graph, ILayoutContext> graphLayoutContext = new IdentityHashMap<Graph, ILayoutContext>();

	private ILayoutAlgorithm previousAlgorithm;

	private PropertyChangeSupport pcs = new PropertyChangeSupport(this);

	@Override
	public void addPropertyChangeListener(PropertyChangeListener listener) {
		pcs.addPropertyChangeListener(listener);
	}

	public ILayoutContext getLayoutContext(Graph graph) {
		return graphLayoutContext.get(graph);
	}

	public void removeLayoutContext(Graph graph) {
		if (graph == null) {
			throw new IllegalArgumentException("Graph may not be null.");
		}
		ILayoutContext oldContext = graphLayoutContext.remove(graph);
		if (oldContext != null) {
			previousAlgorithm = oldContext.getStaticLayoutAlgorithm();
			// notify listeners
			pcs.firePropertyChange(LAYOUT_CONTEXT_PROPERTY,
					new AbstractMap.SimpleEntry<Graph, ILayoutContext>(graph,
							oldContext),
					new AbstractMap.SimpleEntry<Graph, ILayoutContext>(graph,
							null));
		}
	}

	@Override
	public void removePropertyChangeListener(PropertyChangeListener listener) {
		pcs.removePropertyChangeListener(listener);
	}

	public void setLayoutContext(Graph graph, ILayoutContext context) {
		if (graph == null) {
			throw new IllegalArgumentException("Graph may not be null.");
		}

		ILayoutContext oldContext = graphLayoutContext.get(graph);
		graphLayoutContext.put(graph, context);

		// in case new context does not specify an algorithm, transfer old
		// context (or set default, if no context was set before)
		if (context.getStaticLayoutAlgorithm() == null) {
			if (previousAlgorithm != null) {
				context.setStaticLayoutAlgorithm(previousAlgorithm);
			} else {
				context.setStaticLayoutAlgorithm(getDefaultAlgorithm());
			}
		}
		if (context != oldContext) {
			// notify listeners
			pcs.firePropertyChange(LAYOUT_CONTEXT_PROPERTY,
					new AbstractMap.SimpleEntry<Graph, ILayoutContext>(graph,
							oldContext),
					new AbstractMap.SimpleEntry<Graph, ILayoutContext>(graph,
							context));
		}
	}

}
