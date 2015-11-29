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
package org.eclipse.gef4.layout.listeners;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.gef4.layout.AbstractLayoutContext;
import org.eclipse.gef4.layout.IConnectionLayout;
import org.eclipse.gef4.layout.ILayoutContext;
import org.eclipse.gef4.layout.INodeLayout;
import org.eclipse.gef4.layout.ISubgraphLayout;
import org.eclipse.gef4.layout.LayoutProperties;

/**
 * The {@link LayoutListenerSupport} can handle the (un-)registration of layout
 * event listeners ({@link IContextListener}, {@link IGraphStructureListener},
 * {@link ILayoutListener}, and {@link IPruningListener}) and firing of events.
 * It is used by the {@link AbstractLayoutContext}.
 * 
 * @author mwienand
 *
 */
public class LayoutListenerSupport {

	private final ILayoutContext context;
	private final List<IContextListener> contextListeners = new ArrayList<>();
	private final List<IGraphStructureListener> graphStructureListeners = new ArrayList<>();
	private final List<ILayoutListener> layoutListeners = new ArrayList<>();
	private final List<IPruningListener> pruningListeners = new ArrayList<>();

	/**
	 * Constructs a new {@link LayoutListenerSupport} for the given
	 * {@link ILayoutContext}.
	 * 
	 * @param context
	 *            The {@link ILayoutContext} for which this
	 *            {@link LayoutListenerSupport} handles (un-)registration of
	 *            layout listeners and firing of events.
	 */
	public LayoutListenerSupport(ILayoutContext context) {
		this.context = context;
	}

	/**
	 * Adds the given {@link IContextListener} to the list of listeners which
	 * are notified about context changes.
	 * 
	 * @param listener
	 *            The {@link IContextListener} which is added to the listeners
	 *            list.
	 */
	public void addContextListener(IContextListener listener) {
		contextListeners.add(listener);
	}

	/**
	 * Adds the given {@link IGraphStructureListener} to the list of listeners
	 * which are notified about structural changes.
	 * 
	 * @param listener
	 *            The {@link IGraphStructureListener} which is added to the
	 *            listeners list.
	 */
	public void addGraphStructureListener(IGraphStructureListener listener) {
		graphStructureListeners.add(listener);
	}

	/**
	 * Adds the given {@link ILayoutListener} to the list of listeners which are
	 * notified about layout changes.
	 * 
	 * @param listener
	 *            The {@link ILayoutListener} which is added to the listeners
	 *            list.
	 */
	public void addLayoutListener(ILayoutListener listener) {
		layoutListeners.add(listener);
	}

	/**
	 * Adds the given {@link IPruningListener} to the list of listeners which
	 * are notified about pruning changes.
	 * 
	 * @param listener
	 *            The {@link IPruningListener} which is added to the listeners
	 *            list.
	 */
	public void addPruningListener(IPruningListener listener) {
		pruningListeners.add(listener);
	}

	/**
	 * Notifies all {@link IContextListener}s via
	 * {@link IContextListener#backgroundEnableChanged(ILayoutContext)}.
	 */
	public void fireBackgroundEnableChangedEvent() {
		for (IContextListener listener : contextListeners) {
			listener.backgroundEnableChanged(context);
		}
	}

	/**
	 * Notifies all {@link IContextListener}s via
	 * {@link IContextListener#boundsChanged(ILayoutContext)}.
	 * <p>
	 * A dynamic layout is applied afterwards unless all listeners return
	 * <code>true</code>.
	 */
	public void fireBoundsChangedEvent() {
		boolean intercepted = false;
		for (IContextListener listener : contextListeners) {
			boolean intercept = listener.boundsChanged(context);
			if (!intercepted) {
				intercepted = intercept;
			}
		}
		if (!intercepted) {
			context.applyDynamicLayout(true);
		}
	}

	/**
	 * Notifies all {@link IGraphStructureListener}s via
	 * {@link IGraphStructureListener#connectionAdded(ILayoutContext, IConnectionLayout)}
	 * .
	 * <p>
	 * A dynamic layout is applied afterwards unless all listeners return
	 * <code>true</code>.
	 * 
	 * @param connection
	 *            The {@link IConnectionLayout} which was added to the
	 *            {@link ILayoutContext}.
	 */
	public void fireConnectionAddedEvent(IConnectionLayout connection) {
		boolean intercepted = false;
		for (IGraphStructureListener listener : graphStructureListeners) {
			boolean intercept = listener.connectionAdded(context, connection);
			if (!intercepted) {
				intercepted = intercept;
			}
		}
		if (!intercepted) {
			context.applyDynamicLayout(true);
		}
	}

	/**
	 * Notifies all {@link IGraphStructureListener}s via
	 * {@link IGraphStructureListener#connectionRemoved(ILayoutContext, IConnectionLayout)}
	 * .
	 * <p>
	 * A dynamic layout is applied afterwards unless all listeners return
	 * <code>true</code>.
	 * 
	 * @param connection
	 *            The {@link IConnectionLayout} which was removed from the
	 *            {@link ILayoutContext}.
	 */
	public void fireConnectionRemovedEvent(IConnectionLayout connection) {
		boolean intercepted = false;
		for (IGraphStructureListener listener : graphStructureListeners) {
			boolean intercept = listener.connectionRemoved(context, connection);
			if (!intercepted) {
				intercepted = intercept;
			}
		}
		if (!intercepted) {
			context.applyDynamicLayout(true);
		}
	}

	/**
	 * Notifies all {@link IGraphStructureListener}s via
	 * {@link IGraphStructureListener#nodeAdded(ILayoutContext, INodeLayout)} .
	 * <p>
	 * A dynamic layout is applied afterwards unless all listeners return
	 * <code>true</code>.
	 * 
	 * @param node
	 *            The {@link INodeLayout} which was added to the
	 *            {@link ILayoutContext}.
	 */
	public void fireNodeAddedEvent(INodeLayout node) {
		boolean intercepted = false;
		for (IGraphStructureListener listener : graphStructureListeners) {
			boolean intercept = listener.nodeAdded(context, node);
			if (!intercepted) {
				intercepted = intercept;
			}
		}
		if (!intercepted) {
			context.applyDynamicLayout(true);
		}
	}

	/**
	 * Notifies all {@link ILayoutListener}s via
	 * {@link ILayoutListener#nodeMoved(ILayoutContext, INodeLayout)} .
	 * <p>
	 * A dynamic layout is applied afterwards unless all listeners return
	 * <code>true</code>.
	 * 
	 * @param node
	 *            The {@link INodeLayout} whose
	 *            {@link LayoutProperties#LOCATION_PROPERTY} changed.
	 */
	public void fireNodeMovedEvent(INodeLayout node) {
		boolean intercepted = false;
		for (ILayoutListener listener : layoutListeners) {
			boolean intercept = listener.nodeMoved(context, node);
			if (!intercepted) {
				intercepted = intercept;
			}
		}
		if (!intercepted) {
			context.applyDynamicLayout(true);
		}
	}

	/**
	 * Notifies all {@link IGraphStructureListener}s via
	 * {@link IGraphStructureListener#nodeRemoved(ILayoutContext, INodeLayout)}
	 * .
	 * <p>
	 * A dynamic layout is applied afterwards unless all listeners return
	 * <code>true</code>.
	 * 
	 * @param node
	 *            The {@link INodeLayout} which was removed from the
	 *            {@link ILayoutContext}.
	 */
	public void fireNodeRemovedEvent(INodeLayout node) {
		boolean intercepted = false;
		for (IGraphStructureListener listener : graphStructureListeners) {
			boolean intercept = listener.nodeRemoved(context, node);
			if (!intercepted) {
				intercepted = intercept;
			}
		}
		if (!intercepted) {
			context.applyDynamicLayout(true);
		}
	}

	/**
	 * Notifies all {@link ILayoutListener}s via
	 * {@link ILayoutListener#nodeResized(ILayoutContext, INodeLayout)} .
	 * <p>
	 * A dynamic layout is applied afterwards unless all listeners return
	 * <code>true</code>.
	 * 
	 * @param node
	 *            The {@link INodeLayout} whose
	 *            {@link LayoutProperties#SIZE_PROPERTY} changed.
	 */
	public void fireNodeResizedEvent(INodeLayout node) {
		boolean intercepted = false;
		for (ILayoutListener listener : layoutListeners) {
			boolean intercept = listener.nodeResized(context, node);
			if (!intercepted) {
				intercepted = intercept;
			}
		}
		if (!intercepted) {
			context.applyDynamicLayout(true);
		}
	}

	/**
	 * Notifies all {@link IContextListener}s via
	 * {@link IContextListener#pruningEnablementChanged(ILayoutContext)}.
	 */
	public void firePruningEnableChangedEvent() {
		for (IContextListener listener : contextListeners) {
			listener.pruningEnablementChanged(context);
		}
	}

	/**
	 * Notifies all {@link ILayoutListener}s via
	 * {@link ILayoutListener#subgraphMoved(ILayoutContext, ISubgraphLayout)} .
	 * <p>
	 * A dynamic layout is applied afterwards unless all listeners return
	 * <code>true</code>.
	 * 
	 * @param subgraph
	 *            The {@link ISubgraphLayout} whose
	 *            {@link LayoutProperties#LOCATION_PROPERTY} changed.
	 */
	public void fireSubgraphMovedEvent(ISubgraphLayout subgraph) {
		boolean intercepted = false;
		for (ILayoutListener listener : layoutListeners) {
			boolean intercept = listener.subgraphMoved(context, subgraph);
			if (!intercepted) {
				intercepted = intercept;
			}
		}
		if (!intercepted) {
			context.applyDynamicLayout(true);
		}
	}

	/**
	 * Notifies all {@link ILayoutListener}s via
	 * {@link ILayoutListener#subgraphResized(ILayoutContext, ISubgraphLayout)}
	 * .
	 * <p>
	 * A dynamic layout is applied afterwards unless all listeners return
	 * <code>true</code>.
	 * 
	 * @param subgraph
	 *            The {@link ISubgraphLayout} whose
	 *            {@link LayoutProperties#SIZE_PROPERTY} changed.
	 */
	public void fireSubgraphResizedEvent(ISubgraphLayout subgraph) {
		boolean intercepted = false;
		for (ILayoutListener listener : layoutListeners) {
			boolean intercept = listener.subgraphResized(context, subgraph);
			if (!intercepted) {
				intercepted = intercept;
			}
		}
		if (!intercepted) {
			context.applyDynamicLayout(true);
		}
	}

	/**
	 * Removes the given {@link IContextListener} from the list of listeners
	 * which are notified about context changes.
	 * 
	 * @param listener
	 *            The {@link IContextListener} which is removed from the
	 *            listeners list.
	 */
	public void removeContextListener(IContextListener listener) {
		contextListeners.remove(listener);
	}

	/**
	 * Removes the given {@link IGraphStructureListener} from the list of
	 * listeners which are notified about structural changes.
	 * 
	 * @param listener
	 *            The {@link IGraphStructureListener} which is removed from the
	 *            listeners list.
	 */
	public void removeGraphStructureListener(IGraphStructureListener listener) {
		graphStructureListeners.remove(listener);
	}

	/**
	 * Removes the given {@link ILayoutListener} from the list of listeners
	 * which are notified about layout changes.
	 * 
	 * @param listener
	 *            The {@link ILayoutListener} which is removed from the
	 *            listeners list.
	 */
	public void removeLayoutListener(ILayoutListener listener) {
		layoutListeners.remove(listener);
	}

	/**
	 * Removes the given {@link IPruningListener} from the list of listeners
	 * which are notified about pruning changes.
	 * 
	 * @param listener
	 *            The {@link IPruningListener} which is removed from the
	 *            listeners list.
	 */
	public void removePruningListener(IPruningListener listener) {
		pruningListeners.remove(listener);
	}

}
