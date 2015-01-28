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
import java.util.ArrayList;
import java.util.List;

import org.eclipse.gef4.common.properties.PropertyStoreSupport;
import org.eclipse.gef4.layout.LayoutAlgorithm;
import org.eclipse.gef4.layout.LayoutProperties;
import org.eclipse.gef4.layout.interfaces.ConnectionLayout;
import org.eclipse.gef4.layout.interfaces.ContextListener;
import org.eclipse.gef4.layout.interfaces.EntityLayout;
import org.eclipse.gef4.layout.interfaces.GraphStructureListener;
import org.eclipse.gef4.layout.interfaces.LayoutContext;
import org.eclipse.gef4.layout.interfaces.LayoutListener;
import org.eclipse.gef4.layout.interfaces.NodeLayout;
import org.eclipse.gef4.layout.interfaces.PruningListener;
import org.eclipse.gef4.layout.interfaces.SubgraphLayout;

// TODO: replace fire* methods with property change mechanisms
public abstract class AbstractLayoutContext implements LayoutContext {

	private LayoutListenerSupport lls = new LayoutListenerSupport(this);
	private LayoutAlgorithm dynamicLayoutAlgorithm = null;
	private LayoutAlgorithm staticLayoutAlgorithm = null;
	private final List<NodeLayout> layoutNodes = new ArrayList<NodeLayout>();
	private final List<ConnectionLayout> layoutEdges = new ArrayList<ConnectionLayout>();
	private final List<SubgraphLayout> subgraphs = new ArrayList<SubgraphLayout>();

	private boolean flushChangesInvocation = false;

	protected PropertyStoreSupport pss = new PropertyStoreSupport(this);
	protected PropertyChangeSupport pcs = new PropertyChangeSupport(this);

	@Override
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

	@Override
	public void addGraphStructureListener(GraphStructureListener listener) {
		lls.addGraphStructureListener(listener);
	}

	@Override
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

	@Override
	public void addPropertyChangeListener(PropertyChangeListener listener) {
		pcs.addPropertyChangeListener(listener);
	}

	@Override
	public void addPruningListener(PruningListener listener) {
		lls.addPruningListener(listener);
	}

	@Override
	public void applyDynamicLayout(boolean clear) {
		if (dynamicLayoutAlgorithm != null) {
			dynamicLayoutAlgorithm.applyLayout(clear);
		}
	}

	@Override
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

	/**
	 * As we have to guard invocations of {@link #flushChanges(boolean)}, the
	 * true flushing of changes happens here.
	 *
	 * @param animationHint
	 */
	protected abstract void doFlushChanges(boolean animationHint);

	@Override
	public void fireBackgroundEnableChangedEvent() {
		lls.fireBackgroundEnableChangedEvent();
	}

	@Override
	public void fireBoundsChangedEvent() {
		if (!flushChangesInvocation) {
			lls.fireBoundsChangedEvent();
		}
	}

	@Override
	public void fireConnectionAddedEvent(ConnectionLayout connection) {
		if (!flushChangesInvocation) {
			lls.fireConnectionAddedEvent(connection);
		}
	}

	@Override
	public void fireConnectionRemovedEvent(ConnectionLayout connection) {
		if (!flushChangesInvocation) {
			lls.fireConnectionRemovedEvent(connection);
		}
	}

	@Override
	public void fireNodeAddedEvent(NodeLayout node) {
		if (!flushChangesInvocation) {
			lls.fireNodeAddedEvent(node);
		}
	}

	@Override
	public void fireNodeMovedEvent(NodeLayout node) {
		if (!flushChangesInvocation) {
			lls.fireNodeMovedEvent(node);
		}
	}

	@Override
	public void fireNodeRemovedEvent(NodeLayout node) {
		if (!flushChangesInvocation) {
			lls.fireNodeRemovedEvent(node);
		}
	}

	@Override
	public void fireNodeResizedEvent(NodeLayout node) {
		if (!flushChangesInvocation) {
			lls.fireNodeResizedEvent(node);
		}
	}

	@Override
	public void firePruningEnableChangedEvent() {
		lls.firePruningEnableChangedEvent();
	}

	@Override
	public void fireSubgraphMovedEvent(SubgraphLayout subgraph) {
		if (!flushChangesInvocation) {
			lls.fireSubgraphMovedEvent(subgraph);
		}
	}

	@Override
	public void fireSubgraphResizedEvent(SubgraphLayout subgraph) {
		if (!flushChangesInvocation) {
			lls.fireSubgraphResizedEvent(subgraph);
		}
	}

	@Override
	public void flushChanges(boolean animationHint) {
		flushChangesInvocation = true;
		doFlushChanges(animationHint);
		flushChangesInvocation = false;
	}

	@Override
	public ConnectionLayout[] getConnections() {
		return layoutEdges.toArray(new ConnectionLayout[0]);
	}

	@Override
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

	@Override
	public LayoutAlgorithm getDynamicLayoutAlgorithm() {
		return dynamicLayoutAlgorithm;
	}

	@Override
	public NodeLayout[] getNodes() {
		return layoutNodes.toArray(new NodeLayout[0]);
	}

	@Override
	public Object getProperty(String name) {
		return pss.getProperty(name);
	}

	@Override
	public LayoutAlgorithm getStaticLayoutAlgorithm() {
		return staticLayoutAlgorithm;
	}

	@Override
	public SubgraphLayout[] getSubgraphs() {
		return subgraphs.toArray(new SubgraphLayout[0]);
	}

	@Override
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

	@Override
	public void removeGraphStructureListener(GraphStructureListener listener) {
		lls.removeGraphStructureListener(listener);
	}

	@Override
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

	@Override
	public void removePropertyChangeListener(PropertyChangeListener listener) {
		pcs.removePropertyChangeListener(listener);
	}

	@Override
	public void removePruningListener(PruningListener listener) {
		lls.removePruningListener(listener);
	}

	@Override
	public void setDynamicLayoutAlgorithm(LayoutAlgorithm dynamicLayoutAlgorithm) {
		LayoutAlgorithm oldDynamicLayoutAlgorithm = this.dynamicLayoutAlgorithm;
		if (oldDynamicLayoutAlgorithm != dynamicLayoutAlgorithm) {
			this.dynamicLayoutAlgorithm = dynamicLayoutAlgorithm;
			dynamicLayoutAlgorithm.setLayoutContext(this);
			pcs.firePropertyChange(DYNAMIC_LAYOUT_ALGORITHM_PROPERTY,
					oldDynamicLayoutAlgorithm, dynamicLayoutAlgorithm);
		}
	}

	@Override
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

	@Override
	public void setStaticLayoutAlgorithm(LayoutAlgorithm staticLayoutAlgorithm) {
		LayoutAlgorithm oldStaticLayoutAlgorithm = this.staticLayoutAlgorithm;
		if (oldStaticLayoutAlgorithm != staticLayoutAlgorithm) {
			this.staticLayoutAlgorithm = staticLayoutAlgorithm;
			staticLayoutAlgorithm.setLayoutContext(this);
			pcs.firePropertyChange(STATIC_LAYOUT_ALGORITHM_PROPERTY,
					oldStaticLayoutAlgorithm, staticLayoutAlgorithm);
		}
	}

}
