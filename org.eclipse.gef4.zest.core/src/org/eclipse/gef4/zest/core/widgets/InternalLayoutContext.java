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

import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import org.eclipse.draw2d.Animation;
import org.eclipse.gef4.common.properties.PropertyStoreSupport;
import org.eclipse.gef4.geometry.planar.Rectangle;
import org.eclipse.gef4.layout.ILayoutProperties;
import org.eclipse.gef4.layout.LayoutAlgorithm;
import org.eclipse.gef4.layout.LayoutPropertiesHelper;
import org.eclipse.gef4.layout.algorithms.SpaceTreeLayoutAlgorithm.ExpandCollapseManager;
import org.eclipse.gef4.layout.interfaces.ConnectionLayout;
import org.eclipse.gef4.layout.interfaces.ContextListener;
import org.eclipse.gef4.layout.interfaces.EntityLayout;
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
	private LayoutAlgorithm dynamicAlgorithm;
	private LayoutAlgorithm staticAlgorithm;
	private ExpandCollapseManager expandCollapseManager;
	private SubgraphFactory subgraphFactory = new DefaultSubgraph.DefaultSubgraphFactory();
	private final HashSet<SubgraphLayout> subgraphs = new HashSet<SubgraphLayout>();
	private boolean dynamicLayoutEnabled = false;
	private PropertyStoreSupport ps = new PropertyStoreSupport(this);

	// guard method calls
	// TODO: Remove if setExpandedInvocation is not really needed; you can
	// bypass it by using getExpandCM() anyway.
	private boolean setExpandedInvocation = false;
	private boolean flushChangesInvocation = false;
	private boolean staticLayoutInvocation = false;

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
		flushChangesInvocation = true;
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
		flushChangesInvocation = false;
	}

	public Rectangle getBounds() {
		Rectangle result = new Rectangle(container.getLayoutBounds());
		result.setWidth(result.getWidth() - 20);
		result.setHeight(result.getHeight() - 20);
		return result;
	}

	public LayoutAlgorithm getDynamicLayoutAlgorithm() {
		return dynamicAlgorithm;
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
					if (LayoutPropertiesHelper.isGraphEntity(subgraph)
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

	public boolean isDynamicLayoutEnabled() {
		return dynamicLayoutEnabled;
	}

	public void setDynamicLayoutEnabled(boolean enabled) {
		if (this.dynamicLayoutEnabled != enabled) {
			this.dynamicLayoutEnabled = enabled;
			fireBackgroundEnableChangedEvent();
			setp(ILayoutProperties.DYNAMIC_LAYOUT_ENABLED_PROPERTY, enabled);
		}
	}

	private void setp(String name, Object value) {
		ps.setProperty(name, value);
	}

	private Object getp(String name) {
		return ps.getProperty(name);
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

	public void setDynamicLayoutAlgorithm(LayoutAlgorithm algorithm) {
		dynamicAlgorithm = algorithm;
		dynamicAlgorithm.setLayoutContext(this);
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
		setExpandedInvocation = true;
		if (expandCollapseManager != null) {
			expandCollapseManager.setExpanded(this, node, expanded);
		}
		setExpandedInvocation = false;
	}

	// TODO: remove
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

	public void applyDynamicLayout(boolean clean) {
		if (dynamicLayoutEnabled && dynamicAlgorithm != null) {
			if (this != dynamicAlgorithm.getLayoutContext()) {
				throw new IllegalStateException(
						"Dynamic algorithm is bound to a different context!");
			}
			dynamicAlgorithm.applyLayout(clean);
			flushChanges(false);
		}
	}

	/**
	 * Sets layout algorithm for this context. It differs from
	 * {@link #setDynamicLayoutAlgorithm(LayoutAlgorithm) main algorithm} in
	 * that it's always used when {@link #applyLayoutAlgorithm(boolean)} and not
	 * after firing of events.
	 */
	void setLayoutAlgorithm(LayoutAlgorithm algorithm) {
		this.staticAlgorithm = algorithm;
		this.staticAlgorithm.setLayoutContext(this);
	}

	LayoutAlgorithm getLayoutAlgorithm() {
		return staticAlgorithm;
	}

	public void applyStaticLayout(boolean clean) {
		if (staticAlgorithm != null) {
			if (this != staticAlgorithm.getLayoutContext()) {
				throw new IllegalStateException(
						"Static algorithm is bound to a different context!");
			}
			staticLayoutInvocation = true;
			staticAlgorithm.applyLayout(clean);
			staticLayoutInvocation = false;
		}
	}

	public LayoutAlgorithm getStaticLayoutAlgorithm() {
		return staticAlgorithm;
	}

	public void setStaticLayoutAlgorithm(LayoutAlgorithm algorithm) {
		staticAlgorithm = algorithm;
		staticAlgorithm.setLayoutContext(this);
	}

	void checkChangesAllowed() {
		boolean externalLayoutInvocation = staticLayoutInvocation
				|| setExpandedInvocation;
		if (!dynamicLayoutEnabled && !externalLayoutInvocation) {
			throw new IllegalStateException(
					"no dynamic layout and no external layout invocation");
		}
	}

	public void fireNodeAddedEvent(NodeLayout node) {
		boolean intercepted = flushChangesInvocation;
		GraphStructureListener[] listeners = graphStructureListeners
				.toArray(new GraphStructureListener[graphStructureListeners
						.size()]);
		for (int i = 0; i < listeners.length && !intercepted; i++) {
			intercepted = listeners[i].nodeAdded(this, node);
		}
		if (!intercepted) {
			applyDynamicLayout(true);
		}
	}

	public void fireNodeRemovedEvent(NodeLayout node) {
		boolean intercepted = flushChangesInvocation;
		GraphStructureListener[] listeners = graphStructureListeners
				.toArray(new GraphStructureListener[graphStructureListeners
						.size()]);
		for (int i = 0; i < listeners.length && !intercepted; i++) {
			intercepted = listeners[i].nodeRemoved(this, node);
		}
		if (!intercepted) {
			applyDynamicLayout(true);
		}
	}

	public void fireConnectionAddedEvent(ConnectionLayout connection) {
		InternalLayoutContext sourceContext = ((InternalNodeLayout) connection
				.getSource()).getOwnerLayoutContext();
		InternalLayoutContext targetContext = ((InternalNodeLayout) connection
				.getTarget()).getOwnerLayoutContext();
		if (sourceContext != targetContext) {
			return;
		}
		if (sourceContext == this) {
			boolean intercepted = flushChangesInvocation;
			GraphStructureListener[] listeners = graphStructureListeners
					.toArray(new GraphStructureListener[graphStructureListeners
							.size()]);
			for (int i = 0; i < listeners.length && !intercepted; i++) {
				intercepted = listeners[i].connectionAdded(this, connection);
			}
			if (!intercepted) {
				applyDynamicLayout(true);
			}
		} else {
			sourceContext.fireConnectionAddedEvent(connection);
		}
	}

	public void fireConnectionRemovedEvent(ConnectionLayout connection) {
		InternalLayoutContext sourceContext = ((InternalNodeLayout) connection
				.getSource()).getOwnerLayoutContext();
		InternalLayoutContext targetContext = ((InternalNodeLayout) connection
				.getTarget()).getOwnerLayoutContext();
		if (sourceContext != targetContext) {
			return;
		}
		if (sourceContext == this) {
			boolean intercepted = flushChangesInvocation;
			GraphStructureListener[] listeners = graphStructureListeners
					.toArray(new GraphStructureListener[graphStructureListeners
							.size()]);
			for (int i = 0; i < listeners.length && !intercepted; i++) {
				intercepted = listeners[i].connectionRemoved(this, connection);
			}
			if (!intercepted) {
				applyDynamicLayout(true);
			}
		} else {
			sourceContext.fireConnectionAddedEvent(connection);
		}
	}

	public void fireBoundsChangedEvent() {
		boolean intercepted = flushChangesInvocation;
		ContextListener[] listeners = contextListeners
				.toArray(new ContextListener[contextListeners.size()]);
		for (int i = 0; i < listeners.length && !intercepted; i++) {
			intercepted = listeners[i].boundsChanged(this);
		}
		if (!intercepted) {
			applyDynamicLayout(true);
		}
	}

	public void fireBackgroundEnableChangedEvent() {
		ContextListener[] listeners = contextListeners
				.toArray(new ContextListener[contextListeners.size()]);
		for (int i = 0; i < listeners.length; i++) {
			listeners[i].backgroundEnableChanged(this);
		}
	}

	public void fireNodeResizedEvent(NodeLayout nodeLayout) {
		InternalNodeLayout node = (InternalNodeLayout) nodeLayout;
		if (!flushChangesInvocation) {
			node.refreshSize();
			node.refreshLocation();
		}
		boolean intercepted = flushChangesInvocation;
		LayoutListener[] listeners = layoutListeners
				.toArray(new LayoutListener[layoutListeners.size()]);
		for (int i = 0; i < listeners.length && !intercepted; i++) {
			intercepted = listeners[i].nodeResized(this, node);
		}
		if (!intercepted) {
			applyDynamicLayout(true);
		}
	}

	public void fireSubgraphMovedEvent(SubgraphLayout subgraphLayout) {
		DefaultSubgraph subgraph = (DefaultSubgraph) subgraphLayout;
		if (!flushChangesInvocation) {
			subgraph.refreshLocation();
		}
		boolean intercepted = flushChangesInvocation;
		LayoutListener[] listeners = layoutListeners
				.toArray(new LayoutListener[layoutListeners.size()]);
		for (int i = 0; i < listeners.length && !intercepted; i++) {
			intercepted = listeners[i].subgraphMoved(this, subgraph);
		}
		if (!intercepted) {
			applyDynamicLayout(true);
		}
	}

	public void fireSubgraphResizedEvent(SubgraphLayout subgraphLayout) {
		DefaultSubgraph subgraph = (DefaultSubgraph) subgraphLayout;
		if (!flushChangesInvocation) {
			subgraph.refreshSize();
			subgraph.refreshLocation();
		}
		boolean intercepted = flushChangesInvocation;
		LayoutListener[] listeners = layoutListeners
				.toArray(new LayoutListener[layoutListeners.size()]);
		for (int i = 0; i < listeners.length && !intercepted; i++) {
			intercepted = listeners[i].subgraphResized(this, subgraph);
		}
		if (!intercepted) {
			applyDynamicLayout(true);
		}
	}

	public void fireNodeMovedEvent(NodeLayout nodeLayout) {
		InternalNodeLayout node = (InternalNodeLayout) nodeLayout;
		if (!flushChangesInvocation) {
			node.refreshLocation();
		}
		boolean intercepted = flushChangesInvocation;
		LayoutListener[] listeners = layoutListeners
				.toArray(new LayoutListener[layoutListeners.size()]);
		node.setLocation(node.getNode().getLocation().x, node.getNode()
				.getLocation().y);
		for (int i = 0; i < listeners.length && !intercepted; i++) {
			intercepted = listeners[i].nodeMoved(this, node);
		}
		if (!intercepted) {
			applyDynamicLayout(true);
		}
	}

	public void firePruningEnableChangedEvent() {
		ContextListener[] listeners = contextListeners
				.toArray(new ContextListener[contextListeners.size()]);
		for (int i = 0; i < listeners.length; i++) {
			listeners[i].pruningEnablementChanged(this);
		}
	}

	public void setProperty(String name, Object value) {
		if (ILayoutProperties.BOUNDS_PROPERTY.equals(name)) {
			// TODO: there is no setBounds() what to do here?
		} else if (ILayoutProperties.BOUNDS_EXPANDABLE_PROPERTY.equals(name)) {
			// TODO: there is no setBoundsExpandable()
		} else if (ILayoutProperties.DYNAMIC_LAYOUT_ENABLED_PROPERTY
				.equals(name)) {
			if (value instanceof Boolean) {
				setDynamicLayoutEnabled((Boolean) value);
			}
		} else {
			setp(name, value);
		}
	}

	public Object getProperty(String name) {
		if (ILayoutProperties.BOUNDS_PROPERTY.equals(name)) {
			return getBounds();
		} else if (ILayoutProperties.BOUNDS_EXPANDABLE_PROPERTY.equals(name)) {
			// TODO
			return false;
		} else if (ILayoutProperties.DYNAMIC_LAYOUT_ENABLED_PROPERTY
				.equals(name)) {
			return isDynamicLayoutEnabled();
		} else {
			return getp(name);
		}
	}

	public void addPropertyChangeListener(PropertyChangeListener listener) {
		ps.addPropertyChangeListener(listener);
	}

	public void removePropertyChangeListener(PropertyChangeListener listener) {
		ps.addPropertyChangeListener(listener);
	}

}