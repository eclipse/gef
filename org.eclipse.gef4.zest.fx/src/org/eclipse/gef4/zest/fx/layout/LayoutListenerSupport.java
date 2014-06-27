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

import org.eclipse.gef4.layout.interfaces.ConnectionLayout;
import org.eclipse.gef4.layout.interfaces.ContextListener;
import org.eclipse.gef4.layout.interfaces.GraphStructureListener;
import org.eclipse.gef4.layout.interfaces.LayoutContext;
import org.eclipse.gef4.layout.interfaces.LayoutListener;
import org.eclipse.gef4.layout.interfaces.NodeLayout;
import org.eclipse.gef4.layout.interfaces.PruningListener;
import org.eclipse.gef4.layout.interfaces.SubgraphLayout;

public class LayoutListenerSupport {

	private final LayoutContext context;
	private final List<ContextListener> contextListeners = new ArrayList<ContextListener>();
	private final List<GraphStructureListener> graphStructureListeners = new ArrayList<GraphStructureListener>();
	private final List<LayoutListener> layoutListeners = new ArrayList<LayoutListener>();
	private final List<PruningListener> pruningListeners = new ArrayList<PruningListener>();

	public LayoutListenerSupport(LayoutContext context) {
		this.context = context;
	}

	public void addContextListener(ContextListener listener) {
		contextListeners.add(listener);
	}

	public void addGraphStructureListener(GraphStructureListener listener) {
		graphStructureListeners.add(listener);
	}

	public void addLayoutListener(LayoutListener listener) {
		layoutListeners.add(listener);
	}

	public void addPruningListener(PruningListener listener) {
		pruningListeners.add(listener);
	}

	public void fireBackgroundEnableChangedEvent() {
		for (ContextListener listener : contextListeners) {
			listener.backgroundEnableChanged(context);
		}
	}

	public void fireBoundsChangedEvent() {
		boolean intercepted = false;
		for (ContextListener listener : contextListeners) {
			intercepted = listener.boundsChanged(context);
		}
		if (!intercepted) {
			context.applyDynamicLayout(true);
		}
	}

	public void fireConnectionAddedEvent(ConnectionLayout connection) {
		boolean intercepted = false;
		for (GraphStructureListener listener : graphStructureListeners) {
			intercepted = listener.connectionAdded(context, connection);
		}
		if (!intercepted) {
			context.applyDynamicLayout(true);
		}
	}

	public void fireConnectionRemovedEvent(ConnectionLayout connection) {
		boolean intercepted = false;
		for (GraphStructureListener listener : graphStructureListeners) {
			intercepted = listener.connectionRemoved(context, connection);
		}
		if (!intercepted) {
			context.applyDynamicLayout(true);
		}
	}

	public void fireNodeAddedEvent(NodeLayout node) {
		boolean intercepted = false;
		for (GraphStructureListener listener : graphStructureListeners) {
			intercepted = listener.nodeAdded(context, node);
		}
		if (!intercepted) {
			context.applyDynamicLayout(true);
		}
	}

	public void fireNodeMovedEvent(NodeLayout node) {
		boolean intercepted = false;
		for (LayoutListener listener : layoutListeners) {
			intercepted = listener.nodeMoved(context, node);
		}
		if (!intercepted) {
			context.applyDynamicLayout(true);
		}
	}

	public void fireNodeRemovedEvent(NodeLayout node) {
		boolean intercepted = false;
		for (GraphStructureListener listener : graphStructureListeners) {
			intercepted = listener.nodeRemoved(context, node);
		}
		if (!intercepted) {
			context.applyDynamicLayout(true);
		}
	}

	public void fireNodeResizedEvent(NodeLayout node) {
		boolean intercepted = false;
		for (LayoutListener listener : layoutListeners) {
			intercepted = listener.nodeResized(context, node);
		}
		if (!intercepted) {
			context.applyDynamicLayout(true);
		}
	}

	public void firePruningEnableChangedEvent() {
		for (ContextListener listener : contextListeners) {
			listener.pruningEnablementChanged(context);
		}
	}

	public void fireSubgraphMovedEvent(SubgraphLayout subgraph) {
		boolean intercepted = false;
		for (LayoutListener listener : layoutListeners) {
			intercepted = listener.subgraphMoved(context, subgraph);
		}
		if (!intercepted) {
			context.applyDynamicLayout(true);
		}
	}

	public void fireSubgraphResizedEvent(SubgraphLayout subgraph) {
		boolean intercepted = false;
		for (LayoutListener listener : layoutListeners) {
			intercepted = listener.subgraphResized(context, subgraph);
		}
		if (!intercepted) {
			context.applyDynamicLayout(true);
		}
	}

	public void removeContextListener(ContextListener listener) {
		contextListeners.remove(listener);
	}

	public void removeGraphStructureListener(GraphStructureListener listener) {
		graphStructureListeners.remove(listener);
	}

	public void removeLayoutListener(LayoutListener listener) {
		layoutListeners.remove(listener);
	}

	public void removePruningListener(PruningListener listener) {
		pruningListeners.remove(listener);
	}

}
