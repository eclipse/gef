/*******************************************************************************
 * Copyright (c) 2009-2010 Mateusz Matela and others. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Mateusz Matela - initial API and implementation
 *               Ian Bull
 ******************************************************************************/
package org.eclipse.gef4.zest.core.widgets;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import org.eclipse.draw2d.Animation;
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

class InternalLayoutContext implements LayoutContext {

	final IContainer container;
	private final List<LayoutFilter> filters = new ArrayList<LayoutFilter>();
	private final List<ContextListener> contextListeners = new ArrayList<ContextListener>();
	private final List<GraphStructureListener> graphStructureListeners = new ArrayList<GraphStructureListener>();
	private final List<LayoutListener> layoutListeners = new ArrayList<LayoutListener>();
	private final List<PruningListener> pruningListeners = new ArrayList<PruningListener>();
	private LayoutAlgorithm mainAlgorithm;
	private LayoutAlgorithm layoutAlgorithm;
	private ExpandCollapseManager expandCollapseManager;
	private SubgraphFactory subgraphFactory = new DefaultSubgraph.DefaultSubgraphFactory();
	private final HashSet<SubgraphLayout> subgraphs = new HashSet<SubgraphLayout>();
	private boolean eventsOn = true;
	private boolean backgorundLayoutEnabled = false;
	private boolean externalLayoutInvocation = true;

	/**
	 * @param graph
	 *            the graph owning this context
	 */
	InternalLayoutContext(GraphWidget graph) {
		this.container = graph;
	}

	InternalLayoutContext(GraphContainer container) {
		this.container = container;
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

	public SubgraphLayout createSubgraph(NodeLayout[] nodes) {
		checkChangesAllowed();
		InternalNodeLayout[] internalNodes = new InternalNodeLayout[nodes.length];
		for (int i = 0; i < nodes.length; i++) {
			internalNodes[i] = (InternalNodeLayout) nodes[i];
		}
		SubgraphLayout subgraph = subgraphFactory.createSubgraph(internalNodes,
				this);
		subgraphs.add(subgraph);
		return subgraph;
	}

	void removeSubgrah(DefaultSubgraph subgraph) {
		subgraphs.remove(subgraph);
	}

	public void flushChanges(boolean animationHint) {
		// TODO support for asynchronous call
		if (!container.getGraph().isVisible() && animationHint) {
			return;
		}
		eventsOn = false;
		if (animationHint) {
			Animation.markBegin();
		}
		for (Iterator<GraphNode> iterator = container.getNodes().iterator(); iterator
				.hasNext();) {
			GraphNode node = iterator.next();
			node.applyLayoutChanges();
		}
		for (Iterator<GraphConnection> iterator = container.getConnections()
				.iterator(); iterator.hasNext();) {
			GraphConnection connection = iterator.next();
			connection.applyLayoutChanges();
		}
		for (Iterator<SubgraphLayout> iterator = subgraphs.iterator(); iterator
				.hasNext();) {
			DefaultSubgraph subgraph = (DefaultSubgraph) iterator.next();
			subgraph.applyLayoutChanges();
		}
		if (animationHint) {
			Animation.run(GraphWidget.ANIMATION_TIME);
		}
		eventsOn = true;
	}

	public Rectangle getBounds() {
		Rectangle result = new Rectangle(container.getLayoutBounds());
		result.setWidth(result.getWidth() - 20);
		result.setHeight(result.getHeight() - 20);
		return result;
	}

	public LayoutAlgorithm getIncrementalLayoutAlgorithm() {
		return mainAlgorithm;
	}

	public ExpandCollapseManager getExpandCollapseManager() {
		return expandCollapseManager;
	}

	public NodeLayout[] getNodes() {
		ArrayList<InternalNodeLayout> result = new ArrayList<InternalNodeLayout>();
		for (Iterator<GraphNode> iterator = this.container.getNodes()
				.iterator(); iterator.hasNext();) {
			GraphNode node = iterator.next();
			if (!isLayoutItemFiltered(node)) {
				result.add(node.getLayout());
			}
		}
		return result.toArray(new NodeLayout[result.size()]);
	}

	public EntityLayout[] getEntities() {
		HashSet<SubgraphLayout> addedSubgraphs = new HashSet<SubgraphLayout>();
		ArrayList<EntityLayout> result = new ArrayList<EntityLayout>();
		for (Iterator<GraphNode> iterator = this.container.getNodes()
				.iterator(); iterator.hasNext();) {
			GraphNode node = iterator.next();
			if (!isLayoutItemFiltered(node)) {
				InternalNodeLayout nodeLayout = node.getLayout();
				if (!nodeLayout.isPruned()) {
					result.add(nodeLayout);
				} else {
					SubgraphLayout subgraph = nodeLayout.getSubgraph();
					if (subgraph.isGraphEntity()
							&& !addedSubgraphs.contains(subgraph)) {
						result.add(subgraph);
						addedSubgraphs.add(subgraph);
					}
				}
			}
		}
		return result.toArray(new EntityLayout[result.size()]);
	}

	public SubgraphLayout[] getSubgraphs() {
		SubgraphLayout[] result = new SubgraphLayout[subgraphs.size()];
		int subgraphCount = 0;
		for (Iterator<SubgraphLayout> iterator = subgraphs.iterator(); iterator
				.hasNext();) {
			SubgraphLayout subgraph = iterator.next();
			NodeLayout[] nodes = subgraph.getNodes();
			for (int i = 0; i < nodes.length; i++) {
				if (!isLayoutItemFiltered(((InternalNodeLayout) nodes[i])
						.getNode())) {
					result[subgraphCount++] = subgraph;
					break;
				}
			}
		}
		if (subgraphCount == subgraphs.size()) {
			return result;
		} else {
			SubgraphLayout[] result2 = new SubgraphLayout[subgraphCount];
			System.arraycopy(result, 0, result2, 0, subgraphCount);
			return result2;
		}
	}

	public boolean isBoundsExpandable() {
		return false;
	}

	public boolean isIncrementalLayoutEnabled() {
		return backgorundLayoutEnabled;
	}

	public void setIncrementalLayoutEnabled(boolean enabled) {
		if (this.backgorundLayoutEnabled != enabled) {
			this.backgorundLayoutEnabled = enabled;
			fireBackgroundEnableChangedEvent();
		}
	}

	public boolean isPruningEnabled() {
		return expandCollapseManager != null;
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

	public void setIncrementalLayoutAlgorithm(LayoutAlgorithm algorithm) {
		mainAlgorithm = algorithm;
	}

	public void setExpandCollapseManager(
			ExpandCollapseManager expandCollapseManager) {
		this.expandCollapseManager = expandCollapseManager;
		expandCollapseManager.initExpansion(this);
	}

	public ConnectionLayout[] getConnections() {
		List<GraphConnection> connections = container.getConnections();
		ConnectionLayout[] result = new ConnectionLayout[connections.size()];
		int i = 0;
		for (Iterator<GraphConnection> iterator = connections.iterator(); iterator
				.hasNext();) {
			GraphConnection connection = iterator.next();
			if (!isLayoutItemFiltered(connection)) {
				result[i++] = connection.getLayout();
			}
		}
		if (i == result.length) {
			return result;
		}
		ConnectionLayout[] result2 = new ConnectionLayout[i];
		System.arraycopy(result, 0, result2, 0, i);
		return result2;
	}

	public ConnectionLayout[] getConnections(EntityLayout source,
			EntityLayout target) {
		ArrayList<ConnectionLayout> result = new ArrayList<ConnectionLayout>();

		ArrayList<EntityLayout> sourcesList = new ArrayList<EntityLayout>();
		if (source instanceof NodeLayout) {
			sourcesList.add(source);
		}
		if (source instanceof SubgraphLayout) {
			sourcesList.addAll(Arrays.asList(((SubgraphLayout) source)
					.getNodes()));
		}

		HashSet<EntityLayout> targets = new HashSet<EntityLayout>();
		if (target instanceof NodeLayout) {
			targets.add(target);
		}
		if (target instanceof SubgraphLayout) {
			targets.addAll(Arrays.asList(((SubgraphLayout) target).getNodes()));
		}

		for (Iterator<EntityLayout> iterator = sourcesList.iterator(); iterator
				.hasNext();) {
			NodeLayout source2 = (NodeLayout) iterator.next();
			ConnectionLayout[] outgoingConnections = source2
					.getOutgoingConnections();
			for (int i = 0; i < outgoingConnections.length; i++) {
				ConnectionLayout connection = outgoingConnections[i];
				if ((connection.getSource() == source2 && targets
						.contains(connection.getTarget()))
						|| (connection.getTarget() == source2 && targets
								.contains(connection.getSource()))) {
					result.add(connection);
				}
			}

		}
		return result.toArray(new ConnectionLayout[result.size()]);
	}

	void addFilter(LayoutFilter filter) {
		filters.add(filter);
	}

	void removeFilter(LayoutFilter filter) {
		filters.remove(filter);
	}

	boolean isLayoutItemFiltered(GraphItem item) {
		for (Iterator<LayoutFilter> it = filters.iterator(); it.hasNext();) {
			LayoutFilter filter = it.next();
			if (filter.isObjectFiltered(item)) {
				return true;
			}
		}
		return false;
	}

	void setExpanded(NodeLayout node, boolean expanded) {
		externalLayoutInvocation = true;
		if (expandCollapseManager != null) {
			expandCollapseManager.setExpanded(this, node, expanded);
		}
		externalLayoutInvocation = false;
	}

	boolean canExpand(NodeLayout node) {
		return expandCollapseManager != null
				&& expandCollapseManager.canExpand(this, node);
	}

	boolean canCollapse(NodeLayout node) {
		return expandCollapseManager != null
				&& expandCollapseManager.canCollapse(this, node);
	}

	void setSubgraphFactory(SubgraphFactory factory) {
		subgraphFactory = factory;
	}

	SubgraphFactory getSubgraphFactory() {
		return subgraphFactory;
	}

	void applyMainAlgorithm() {
		if (backgorundLayoutEnabled && mainAlgorithm != null) {
			mainAlgorithm.applyLayout(true);
			flushChanges(false);
		}
	}

	/**
	 * Sets layout algorithm for this context. It differs from
	 * {@link #setIncrementalLayoutAlgorithm(LayoutAlgorithm) main algorithm} in
	 * that it's always used when {@link #applyLayoutAlgorithm(boolean)} and not
	 * after firing of events.
	 */
	void setLayoutAlgorithm(LayoutAlgorithm algorithm) {
		this.layoutAlgorithm = algorithm;
		this.layoutAlgorithm.setLayoutContext(this);
	}

	LayoutAlgorithm getLayoutAlgorithm() {
		return layoutAlgorithm;
	}

	void applyLayout(boolean clean) {
		if (layoutAlgorithm != null) {
			externalLayoutInvocation = true;
			layoutAlgorithm.applyLayout(clean);
			externalLayoutInvocation = false;
		}
	}

	void checkChangesAllowed() {
		if (!backgorundLayoutEnabled && !externalLayoutInvocation) {
			throw new RuntimeException(
					"Layout not allowed to perform changes in layout context!");
		}
	}

	void fireNodeAddedEvent(NodeLayout node) {
		boolean intercepted = !eventsOn;
		GraphStructureListener[] listeners = graphStructureListeners
				.toArray(new GraphStructureListener[graphStructureListeners
						.size()]);
		for (int i = 0; i < listeners.length && !intercepted; i++) {
			intercepted = listeners[i].nodeAdded(this, node);
		}
		if (!intercepted) {
			applyMainAlgorithm();
		}
	}

	void fireNodeRemovedEvent(NodeLayout node) {
		boolean intercepted = !eventsOn;
		GraphStructureListener[] listeners = graphStructureListeners
				.toArray(new GraphStructureListener[graphStructureListeners
						.size()]);
		for (int i = 0; i < listeners.length && !intercepted; i++) {
			intercepted = listeners[i].nodeRemoved(this, node);
		}
		if (!intercepted) {
			applyMainAlgorithm();
		}
	}

	void fireConnectionAddedEvent(ConnectionLayout connection) {
		InternalLayoutContext sourceContext = ((InternalNodeLayout) connection
				.getSource()).getOwnerLayoutContext();
		InternalLayoutContext targetContext = ((InternalNodeLayout) connection
				.getTarget()).getOwnerLayoutContext();
		if (sourceContext != targetContext) {
			return;
		}
		if (sourceContext == this) {
			boolean intercepted = !eventsOn;
			GraphStructureListener[] listeners = graphStructureListeners
					.toArray(new GraphStructureListener[graphStructureListeners
							.size()]);
			for (int i = 0; i < listeners.length && !intercepted; i++) {
				intercepted = listeners[i].connectionAdded(this, connection);
			}
			if (!intercepted) {
				applyMainAlgorithm();
			}
		} else {
			sourceContext.fireConnectionAddedEvent(connection);
		}
	}

	void fireConnectionRemovedEvent(ConnectionLayout connection) {
		InternalLayoutContext sourceContext = ((InternalNodeLayout) connection
				.getSource()).getOwnerLayoutContext();
		InternalLayoutContext targetContext = ((InternalNodeLayout) connection
				.getTarget()).getOwnerLayoutContext();
		if (sourceContext != targetContext) {
			return;
		}
		if (sourceContext == this) {
			boolean intercepted = !eventsOn;
			GraphStructureListener[] listeners = graphStructureListeners
					.toArray(new GraphStructureListener[graphStructureListeners
							.size()]);
			for (int i = 0; i < listeners.length && !intercepted; i++) {
				intercepted = listeners[i].connectionRemoved(this, connection);
			}
			if (!intercepted) {
				applyMainAlgorithm();
			}
		} else {
			sourceContext.fireConnectionAddedEvent(connection);
		}
	}

	void fireBoundsChangedEvent() {
		boolean intercepted = !eventsOn;
		ContextListener[] listeners = contextListeners
				.toArray(new ContextListener[contextListeners.size()]);
		for (int i = 0; i < listeners.length && !intercepted; i++) {
			intercepted = listeners[i].boundsChanged(this);
		}
		if (!intercepted) {
			applyMainAlgorithm();
		}
	}

	void fireBackgroundEnableChangedEvent() {
		ContextListener[] listeners = contextListeners
				.toArray(new ContextListener[contextListeners.size()]);
		for (int i = 0; i < listeners.length; i++) {
			listeners[i].backgroundEnableChanged(this);
		}
	}

	void fireNodeMovedEvent(InternalNodeLayout node) {
		if (eventsOn) {
			node.refreshLocation();
		}
		boolean intercepted = !eventsOn;
		LayoutListener[] listeners = layoutListeners
				.toArray(new LayoutListener[layoutListeners.size()]);
		node.setLocation(node.getNode().getLocation().x, node.getNode()
				.getLocation().y);
		for (int i = 0; i < listeners.length && !intercepted; i++) {
			intercepted = listeners[i].nodeMoved(this, node);
		}
		if (!intercepted) {
			applyMainAlgorithm();
		}
	}

	void fireNodeResizedEvent(InternalNodeLayout node) {
		if (eventsOn) {
			node.refreshSize();
			node.refreshLocation();
		}
		boolean intercepted = !eventsOn;
		LayoutListener[] listeners = layoutListeners
				.toArray(new LayoutListener[layoutListeners.size()]);
		for (int i = 0; i < listeners.length && !intercepted; i++) {
			intercepted = listeners[i].nodeResized(this, node);
		}
		if (!intercepted) {
			applyMainAlgorithm();
		}
	}

	void fireSubgraphMovedEvent(DefaultSubgraph subgraph) {
		if (eventsOn) {
			subgraph.refreshLocation();
		}
		boolean intercepted = !eventsOn;
		LayoutListener[] listeners = layoutListeners
				.toArray(new LayoutListener[layoutListeners.size()]);
		for (int i = 0; i < listeners.length && !intercepted; i++) {
			intercepted = listeners[i].subgraphMoved(this, subgraph);
		}
		if (!intercepted) {
			applyMainAlgorithm();
		}
	}

	void fireSubgraphResizedEvent(DefaultSubgraph subgraph) {
		if (eventsOn) {
			subgraph.refreshSize();
			subgraph.refreshLocation();
		}
		boolean intercepted = !eventsOn;
		LayoutListener[] listeners = layoutListeners
				.toArray(new LayoutListener[layoutListeners.size()]);
		for (int i = 0; i < listeners.length && !intercepted; i++) {
			intercepted = listeners[i].subgraphResized(this, subgraph);
		}
		if (!intercepted) {
			applyMainAlgorithm();
		}
	}
}