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
package org.eclipse.gef4.zest.layout;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.gef4.geometry.planar.Rectangle;
import org.eclipse.gef4.layout.LayoutAlgorithm;
import org.eclipse.gef4.layout.interfaces.ConnectionLayout;
import org.eclipse.gef4.layout.interfaces.ContextListener;
import org.eclipse.gef4.layout.interfaces.EntityLayout;
import org.eclipse.gef4.layout.interfaces.ExpandCollapseManager;
import org.eclipse.gef4.layout.interfaces.GraphStructureListener;
import org.eclipse.gef4.layout.interfaces.LayoutContext;
import org.eclipse.gef4.layout.interfaces.LayoutListener;
import org.eclipse.gef4.layout.interfaces.NodeLayout;
import org.eclipse.gef4.layout.interfaces.PruningListener;
import org.eclipse.gef4.layout.interfaces.SubgraphLayout;

public abstract class AbstractLayoutContext implements LayoutContext {

	private LayoutListenerSupport lls = new LayoutListenerSupport(this);
	private Rectangle bounds = new Rectangle(0, 0, 0, 0);
	private boolean boundsExpandable = false;
	private boolean dynamicLayoutEnabled = true;
	private LayoutAlgorithm dynamicAlgorithm = null;
	private LayoutAlgorithm staticAlgorithm = null;
	private ExpandCollapseManager expandCollapseManager = null;
	private final List<NodeLayout> layoutNodes = new ArrayList<NodeLayout>();
	private final List<ConnectionLayout> layoutEdges = new ArrayList<ConnectionLayout>();
	private final List<SubgraphLayout> subgraphs = new ArrayList<SubgraphLayout>();

	private boolean flushChangesInvocation = false;

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
	public void addPruningListener(PruningListener listener) {
		lls.addPruningListener(listener);
	}

	@Override
	public void applyDynamicLayout(boolean clear) {
		if (dynamicAlgorithm != null) {
			dynamicAlgorithm.applyLayout(clear);
		}
	}

	@Override
	public void applyStaticLayout(boolean clear) {
		if (staticAlgorithm != null) {
			staticAlgorithm.applyLayout(clear);
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
	public Rectangle getBounds() {
		return bounds;
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
		return dynamicAlgorithm;
	}

	@Override
	public ExpandCollapseManager getExpandCollapseManager() {
		return expandCollapseManager;
	}

	@Override
	public NodeLayout[] getNodes() {
		return layoutNodes.toArray(new NodeLayout[0]);
	}

	@Override
	public LayoutAlgorithm getStaticLayoutAlgorithm() {
		return staticAlgorithm;
	}

	@Override
	public SubgraphLayout[] getSubgraphs() {
		return subgraphs.toArray(new SubgraphLayout[0]);
	}

	@Override
	public boolean isBoundsExpandable() {
		return boundsExpandable;
	}

	@Override
	public boolean isDynamicLayoutEnabled() {
		return dynamicLayoutEnabled;
	}

	@Override
	public boolean isPruningEnabled() {
		return false;
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
	public void removePruningListener(PruningListener listener) {
		lls.removePruningListener(listener);
	}

	public void setBounds(Rectangle rect) {
		bounds.setBounds(rect);
	}

	@Override
	public void setDynamicLayoutAlgorithm(LayoutAlgorithm algorithm) {
		dynamicAlgorithm = algorithm;
		dynamicAlgorithm.setLayoutContext(this);
	}

	@Override
	public void setDynamicLayoutEnabled(boolean enabled) {
		dynamicLayoutEnabled = enabled;
	}

	@Override
	public void setExpandCollapseManager(
			ExpandCollapseManager expandCollapseManager) {
		this.expandCollapseManager = expandCollapseManager;
	}

	@Override
	public void setStaticLayoutAlgorithm(LayoutAlgorithm fullLayoutAlgorithm) {
		this.staticAlgorithm = fullLayoutAlgorithm;
		fullLayoutAlgorithm.setLayoutContext(this);
	}

}
