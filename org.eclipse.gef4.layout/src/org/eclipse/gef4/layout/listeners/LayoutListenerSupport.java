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
package org.eclipse.gef4.layout.listeners;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.gef4.layout.IConnectionLayout;
import org.eclipse.gef4.layout.ILayoutContext;
import org.eclipse.gef4.layout.INodeLayout;
import org.eclipse.gef4.layout.ISubgraphLayout;

public class LayoutListenerSupport {

	private final ILayoutContext context;
	private final List<IContextListener> contextListeners = new ArrayList<IContextListener>();
	private final List<IGraphStructureListener> graphStructureListeners = new ArrayList<IGraphStructureListener>();
	private final List<ILayoutListener> layoutListeners = new ArrayList<ILayoutListener>();
	private final List<IPruningListener> pruningListeners = new ArrayList<IPruningListener>();

	public LayoutListenerSupport(ILayoutContext context) {
		this.context = context;
	}

	public void addContextListener(IContextListener listener) {
		contextListeners.add(listener);
	}

	public void addGraphStructureListener(IGraphStructureListener listener) {
		graphStructureListeners.add(listener);
	}

	public void addLayoutListener(ILayoutListener listener) {
		layoutListeners.add(listener);
	}

	public void addPruningListener(IPruningListener listener) {
		pruningListeners.add(listener);
	}

	public void fireBackgroundEnableChangedEvent() {
		for (IContextListener listener : contextListeners) {
			listener.backgroundEnableChanged(context);
		}
	}

	public void fireBoundsChangedEvent() {
		boolean intercepted = false;
		for (IContextListener listener : contextListeners) {
			intercepted = listener.boundsChanged(context);
		}
		if (!intercepted) {
			context.applyDynamicLayout(true);
		}
	}

	public void fireConnectionAddedEvent(IConnectionLayout connection) {
		boolean intercepted = false;
		for (IGraphStructureListener listener : graphStructureListeners) {
			intercepted = listener.connectionAdded(context, connection);
		}
		if (!intercepted) {
			context.applyDynamicLayout(true);
		}
	}

	public void fireConnectionRemovedEvent(IConnectionLayout connection) {
		boolean intercepted = false;
		for (IGraphStructureListener listener : graphStructureListeners) {
			intercepted = listener.connectionRemoved(context, connection);
		}
		if (!intercepted) {
			context.applyDynamicLayout(true);
		}
	}

	public void fireNodeAddedEvent(INodeLayout node) {
		boolean intercepted = false;
		for (IGraphStructureListener listener : graphStructureListeners) {
			intercepted = listener.nodeAdded(context, node);
		}
		if (!intercepted) {
			context.applyDynamicLayout(true);
		}
	}

	public void fireNodeMovedEvent(INodeLayout node) {
		boolean intercepted = false;
		for (ILayoutListener listener : layoutListeners) {
			intercepted = listener.nodeMoved(context, node);
		}
		if (!intercepted) {
			context.applyDynamicLayout(true);
		}
	}

	public void fireNodeRemovedEvent(INodeLayout node) {
		boolean intercepted = false;
		for (IGraphStructureListener listener : graphStructureListeners) {
			intercepted = listener.nodeRemoved(context, node);
		}
		if (!intercepted) {
			context.applyDynamicLayout(true);
		}
	}

	public void fireNodeResizedEvent(INodeLayout node) {
		boolean intercepted = false;
		for (ILayoutListener listener : layoutListeners) {
			intercepted = listener.nodeResized(context, node);
		}
		if (!intercepted) {
			context.applyDynamicLayout(true);
		}
	}

	public void firePruningEnableChangedEvent() {
		for (IContextListener listener : contextListeners) {
			listener.pruningEnablementChanged(context);
		}
	}

	public void fireSubgraphMovedEvent(ISubgraphLayout subgraph) {
		boolean intercepted = false;
		for (ILayoutListener listener : layoutListeners) {
			intercepted = listener.subgraphMoved(context, subgraph);
		}
		if (!intercepted) {
			context.applyDynamicLayout(true);
		}
	}

	public void fireSubgraphResizedEvent(ISubgraphLayout subgraph) {
		boolean intercepted = false;
		for (ILayoutListener listener : layoutListeners) {
			intercepted = listener.subgraphResized(context, subgraph);
		}
		if (!intercepted) {
			context.applyDynamicLayout(true);
		}
	}

	public void removeContextListener(IContextListener listener) {
		contextListeners.remove(listener);
	}

	public void removeGraphStructureListener(IGraphStructureListener listener) {
		graphStructureListeners.remove(listener);
	}

	public void removeLayoutListener(ILayoutListener listener) {
		layoutListeners.remove(listener);
	}

	public void removePruningListener(IPruningListener listener) {
		pruningListeners.remove(listener);
	}

}
