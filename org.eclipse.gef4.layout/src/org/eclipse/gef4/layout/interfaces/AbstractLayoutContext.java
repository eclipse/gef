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
package org.eclipse.gef4.layout.interfaces;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.gef4.common.properties.PropertyStoreSupport;
import org.eclipse.gef4.layout.LayoutAlgorithm;
import org.eclipse.gef4.layout.LayoutProperties;

// TODO: replace fire* methods with property change mechanisms
public abstract class AbstractLayoutContext implements LayoutContext {

	private LayoutListenerSupport lls = new LayoutListenerSupport(this);
	private LayoutAlgorithm dynamicLayoutAlgorithm = null;
	private LayoutAlgorithm staticLayoutAlgorithm = null;
	private final List<NodeLayout> layoutNodes = new ArrayList<NodeLayout>();
	private final List<ConnectionLayout> layoutEdges = new ArrayList<ConnectionLayout>();
	private final List<SubgraphLayout> subgraphs = new ArrayList<SubgraphLayout>();

	private boolean flushChangesInvocation = false;

	private final List<Runnable> onFlushChanges = new ArrayList<Runnable>();
	private final List<ILayoutFilter> layoutFilters = new ArrayList<ILayoutFilter>();

	protected PropertyStoreSupport pss = new PropertyStoreSupport(this);
	protected PropertyChangeSupport pcs = new PropertyChangeSupport(this);

	public void addContextListener(ContextListener listener) {
		lls.addContextListener(listener);
	}

	/**
	 * Adds the given {@link ConnectionLayout} to the list of edges and fires a
	 * corresponding connection-added-event.
	 *
	 * @param edge
	 *            {@link ConnectionLayout} to add
	 */
	protected void addEdge(ConnectionLayout edge) {
		layoutEdges.add(edge);
		fireConnectionAddedEvent(edge);
	}

	public void addGraphStructureListener(GraphStructureListener listener) {
		lls.addGraphStructureListener(listener);
	}

	public void addLayoutFilter(ILayoutFilter layoutFilter) {
		layoutFilters.add(layoutFilter);
	}

	public void addLayoutListener(LayoutListener listener) {
		lls.addLayoutListener(listener);
	}

	/**
	 * Adds the given {@link NodeLayout} to the list of nodes and fires a
	 * corresponding node-added-event.
	 *
	 * @param node
	 *            {@link NodeLayout} to add
	 */
	protected void addNode(NodeLayout node) {
		layoutNodes.add(node);
		fireNodeAddedEvent(node);
	}

	public void addPropertyChangeListener(PropertyChangeListener listener) {
		pcs.addPropertyChangeListener(listener);
	}

	public void addPruningListener(PruningListener listener) {
		lls.addPruningListener(listener);
	}

	public void applyDynamicLayout(boolean clear) {
		if (dynamicLayoutAlgorithm != null) {
			dynamicLayoutAlgorithm.applyLayout(clear);
		}
	}

	public void applyStaticLayout(boolean clear) {
		if (staticLayoutAlgorithm != null) {
			staticLayoutAlgorithm.setLayoutContext(this);
			staticLayoutAlgorithm.applyLayout(clear);
		}
	}

	/**
	 * Removes all edges from this context using individual
	 * {@link #removeEdge(ConnectionLayout)} calls.
	 */
	protected void clearEdges() {
		for (ConnectionLayout edge : layoutEdges) {
			removeEdge(edge);
		}
	}

	/**
	 * Removes all nodes from this context using individual
	 * {@link #removeNode(NodeLayout)} calls.
	 */
	protected void clearNodes() {
		for (NodeLayout node : layoutNodes) {
			removeNode(node);
		}
	}

	protected void doFlushChanges(boolean animationHint) {
		// TODO: use specific flush-changes-listener to pass animationHint along
		for (Runnable r : onFlushChanges) {
			r.run();
		}
	}

	public void fireBackgroundEnableChangedEvent() {
		lls.fireBackgroundEnableChangedEvent();
	}

	public void fireBoundsChangedEvent() {
		if (!flushChangesInvocation) {
			lls.fireBoundsChangedEvent();
		}
	}

	public void fireConnectionAddedEvent(ConnectionLayout connection) {
		if (!flushChangesInvocation) {
			lls.fireConnectionAddedEvent(connection);
		}
	}

	public void fireConnectionRemovedEvent(ConnectionLayout connection) {
		if (!flushChangesInvocation) {
			lls.fireConnectionRemovedEvent(connection);
		}
	}

	public void fireNodeAddedEvent(NodeLayout node) {
		if (!flushChangesInvocation) {
			lls.fireNodeAddedEvent(node);
		}
	}

	public void fireNodeMovedEvent(NodeLayout node) {
		if (!flushChangesInvocation) {
			lls.fireNodeMovedEvent(node);
		}
	}

	public void fireNodeRemovedEvent(NodeLayout node) {
		if (!flushChangesInvocation) {
			lls.fireNodeRemovedEvent(node);
		}
	}

	public void fireNodeResizedEvent(NodeLayout node) {
		if (!flushChangesInvocation) {
			lls.fireNodeResizedEvent(node);
		}
	}

	public void firePruningEnableChangedEvent() {
		lls.firePruningEnableChangedEvent();
	}

	public void fireSubgraphMovedEvent(SubgraphLayout subgraph) {
		if (!flushChangesInvocation) {
			lls.fireSubgraphMovedEvent(subgraph);
		}
	}

	public void fireSubgraphResizedEvent(SubgraphLayout subgraph) {
		if (!flushChangesInvocation) {
			lls.fireSubgraphResizedEvent(subgraph);
		}
	}

	public void flushChanges(boolean animationHint) {
		flushChangesInvocation = true;
		doFlushChanges(animationHint);
		flushChangesInvocation = false;
	}

	public ConnectionLayout[] getConnections() {
		return layoutEdges.toArray(new ConnectionLayout[0]);
	}

	public ConnectionLayout[] getConnections(EntityLayout layoutEntity1,
			EntityLayout layoutEntity2) {
		List<ConnectionLayout> connections = new ArrayList<ConnectionLayout>();

		for (ConnectionLayout c : ((NodeLayout) layoutEntity1)
				.getOutgoingConnections()) {
			if (c.getTarget() == layoutEntity2) {
				connections.add(c);
			}
		}

		for (ConnectionLayout c : ((NodeLayout) layoutEntity2)
				.getOutgoingConnections()) {
			if (c.getTarget() == layoutEntity1) {
				connections.add(c);
			}
		}

		return connections.toArray(new ConnectionLayout[0]);
	}

	public LayoutAlgorithm getDynamicLayoutAlgorithm() {
		return dynamicLayoutAlgorithm;
	}

	public NodeLayout[] getNodes() {
		return layoutNodes.toArray(new NodeLayout[0]);
	}

	public Object getProperty(String name) {
		return pss.getProperty(name);
	}

	public LayoutAlgorithm getStaticLayoutAlgorithm() {
		return staticLayoutAlgorithm;
	}

	public SubgraphLayout[] getSubgraphs() {
		return subgraphs.toArray(new SubgraphLayout[0]);
	}

	public boolean isLayoutIrrelevant(ConnectionLayout connLayout) {
		for (ILayoutFilter filter : layoutFilters) {
			if (filter.isLayoutIrrelevant(connLayout)) {
				return true;
			}
		}
		return false;
	}

	public boolean isLayoutIrrelevant(NodeLayout nodeLayout) {
		for (ILayoutFilter filter : layoutFilters) {
			if (filter.isLayoutIrrelevant(nodeLayout)) {
				return true;
			}
		}
		return false;
	}

	public void removeContextListener(ContextListener listener) {
		lls.removeContextListener(listener);
	}

	/**
	 * Removes the given {@link ConnectionLayout} from the list of edges and
	 * fires a corresponding connection-removed-event.
	 *
	 * @param edge
	 *            {@link ConnectionLayout} to remove
	 */
	protected void removeEdge(ConnectionLayout edge) {
		layoutEdges.remove(edge);
		fireConnectionRemovedEvent(edge);
	}

	public void removeGraphStructureListener(GraphStructureListener listener) {
		lls.removeGraphStructureListener(listener);
	}

	public void removeLayoutFilter(ILayoutFilter layoutFilter) {
		layoutFilters.remove(layoutFilter);
	}

	public void removeLayoutListener(LayoutListener listener) {
		lls.removeLayoutListener(listener);
	}

	/**
	 * Removes the given {@link NodeLayout} from the managed list of nodes and
	 * fires a corresponding node-removed-event.
	 *
	 * @param node
	 *            {@link NodeLayout} to remove
	 */
	protected void removeNode(NodeLayout node) {
		layoutNodes.remove(node);
		fireNodeRemovedEvent(node);
	}

	public void removePropertyChangeListener(PropertyChangeListener listener) {
		pcs.removePropertyChangeListener(listener);
	}

	public void removePruningListener(PruningListener listener) {
		lls.removePruningListener(listener);
	}

	public void scheduleForFlushChanges(Runnable runnable) {
		if (runnable == null) {
			throw new IllegalArgumentException("Runnable may not be null.");
		}
		onFlushChanges.add(runnable);
	}

	public void setDynamicLayoutAlgorithm(LayoutAlgorithm dynamicLayoutAlgorithm) {
		LayoutAlgorithm oldDynamicLayoutAlgorithm = this.dynamicLayoutAlgorithm;
		if (oldDynamicLayoutAlgorithm != dynamicLayoutAlgorithm) {
			this.dynamicLayoutAlgorithm = dynamicLayoutAlgorithm;
			dynamicLayoutAlgorithm.setLayoutContext(this);
			pcs.firePropertyChange(DYNAMIC_LAYOUT_ALGORITHM_PROPERTY,
					oldDynamicLayoutAlgorithm, dynamicLayoutAlgorithm);
		}
	}

	public void setProperty(String name, Object value) {
		Object oldValue = pss.getProperty(name);
		pss.setProperty(name, value);
		if (oldValue != value && (value == null || !value.equals(oldValue))) {
			// send notification
			if (LayoutProperties.BOUNDS_PROPERTY.equals(name)) {
				fireBoundsChangedEvent();
			} else if (LayoutProperties.DYNAMIC_LAYOUT_ENABLED_PROPERTY
					.equals(name)) {
				fireBackgroundEnableChangedEvent();
			} else if (LayoutProperties.PRUNING_ENABLED_PROPERTY.equals(name)) {
				firePruningEnableChangedEvent();
			}
		}
		pcs.firePropertyChange(name, oldValue, value);
	}

	public void setStaticLayoutAlgorithm(LayoutAlgorithm staticLayoutAlgorithm) {
		LayoutAlgorithm oldStaticLayoutAlgorithm = this.staticLayoutAlgorithm;
		if (oldStaticLayoutAlgorithm != staticLayoutAlgorithm) {
			this.staticLayoutAlgorithm = staticLayoutAlgorithm;
			staticLayoutAlgorithm.setLayoutContext(this);
			pcs.firePropertyChange(STATIC_LAYOUT_ALGORITHM_PROPERTY,
					oldStaticLayoutAlgorithm, staticLayoutAlgorithm);
		}
	}

	public void unscheduleFromFlushChanges(Runnable runnable) {
		if (!onFlushChanges.contains(runnable)) {
			new IllegalArgumentException(
					"Given Runnable is not contained in the list.")
					.printStackTrace();
		}
		onFlushChanges.remove(runnable);
	}

}
