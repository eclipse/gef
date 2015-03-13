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
import org.eclipse.gef4.layout.IConnectionLayout;
import org.eclipse.gef4.layout.IEntityLayout;
import org.eclipse.gef4.layout.ILayoutAlgorithm;
import org.eclipse.gef4.layout.ILayoutContext;
import org.eclipse.gef4.layout.ILayoutFilter;
import org.eclipse.gef4.layout.INodeLayout;
import org.eclipse.gef4.layout.ISubgraphLayout;
import org.eclipse.gef4.layout.LayoutProperties;
import org.eclipse.gef4.layout.algorithms.SpaceTreeLayoutAlgorithm.ExpandCollapseManager;
import org.eclipse.gef4.layout.listeners.IContextListener;
import org.eclipse.gef4.layout.listeners.IGraphStructureListener;
import org.eclipse.gef4.layout.listeners.ILayoutListener;
import org.eclipse.gef4.layout.listeners.IPruningListener;

class InternalLayoutContext implements ILayoutContext {

	final IContainer container;
	private final List<LayoutFilter> filters = new ArrayList<LayoutFilter>();
	private final List<IContextListener> contextListeners = new ArrayList<IContextListener>();
	private final List<IGraphStructureListener> graphStructureListeners = new ArrayList<IGraphStructureListener>();
	private final List<ILayoutListener> layoutListeners = new ArrayList<ILayoutListener>();
	private final List<IPruningListener> pruningListeners = new ArrayList<IPruningListener>();
	private ILayoutAlgorithm dynamicAlgorithm;
	private ILayoutAlgorithm staticAlgorithm;
	private ExpandCollapseManager expandCollapseManager;
	private SubgraphFactory subgraphFactory = new DefaultSubgraph.DefaultSubgraphFactory();
	private final HashSet<ISubgraphLayout> subgraphs = new HashSet<ISubgraphLayout>();
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

	public ISubgraphLayout createSubgraph(INodeLayout[] nodes) {
		checkChangesAllowed();
		InternalNodeLayout[] internalNodes = new InternalNodeLayout[nodes.length];
		for (int i = 0; i < nodes.length; i++) {
			internalNodes[i] = (InternalNodeLayout) nodes[i];
		}
		ISubgraphLayout subgraph = subgraphFactory.createSubgraph(
				internalNodes, this);
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
		for (Iterator<ISubgraphLayout> iterator = subgraphs.iterator(); iterator
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

	public ILayoutAlgorithm getDynamicLayoutAlgorithm() {
		return dynamicAlgorithm;
	}

	public ExpandCollapseManager getExpandCollapseManager() {
		return expandCollapseManager;
	}

	public INodeLayout[] getNodes() {
		ArrayList<InternalNodeLayout> result = new ArrayList<InternalNodeLayout>();
		for (Iterator<GraphNode> iterator = this.container.getNodes()
				.iterator(); iterator.hasNext();) {
			GraphNode node = iterator.next();
			if (!isLayoutItemFiltered(node)) {
				result.add(node.getLayout());
			}
		}
		return result.toArray(new INodeLayout[result.size()]);
	}

	public IEntityLayout[] getEntities() {
		HashSet<ISubgraphLayout> addedSubgraphs = new HashSet<ISubgraphLayout>();
		ArrayList<IEntityLayout> result = new ArrayList<IEntityLayout>();
		for (Iterator<GraphNode> iterator = this.container.getNodes()
				.iterator(); iterator.hasNext();) {
			GraphNode node = iterator.next();
			if (!isLayoutItemFiltered(node)) {
				InternalNodeLayout nodeLayout = node.getLayout();
				if (!nodeLayout.isPruned()) {
					result.add(nodeLayout);
				} else {
					ISubgraphLayout subgraph = nodeLayout.getSubgraph();
					if (!addedSubgraphs.contains(subgraph)) {
						result.add(subgraph);
						addedSubgraphs.add(subgraph);
					}
				}
			}
		}
		return result.toArray(new IEntityLayout[result.size()]);
	}

	public ISubgraphLayout[] getSubgraphs() {
		ISubgraphLayout[] result = new ISubgraphLayout[subgraphs.size()];
		int subgraphCount = 0;
		for (Iterator<ISubgraphLayout> iterator = subgraphs.iterator(); iterator
				.hasNext();) {
			ISubgraphLayout subgraph = iterator.next();
			INodeLayout[] nodes = subgraph.getNodes();
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
			ISubgraphLayout[] result2 = new ISubgraphLayout[subgraphCount];
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
			setp(LayoutProperties.DYNAMIC_LAYOUT_ENABLED_PROPERTY, enabled);
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

	public void setDynamicLayoutAlgorithm(ILayoutAlgorithm algorithm) {
		dynamicAlgorithm = algorithm;
		dynamicAlgorithm.setLayoutContext(this);
	}

	public void setExpandCollapseManager(
			ExpandCollapseManager expandCollapseManager) {
		this.expandCollapseManager = expandCollapseManager;
		expandCollapseManager.initExpansion(this);
	}

	public IConnectionLayout[] getConnections() {
		List<GraphConnection> connections = container.getConnections();
		IConnectionLayout[] result = new IConnectionLayout[connections.size()];
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
		IConnectionLayout[] result2 = new IConnectionLayout[i];
		System.arraycopy(result, 0, result2, 0, i);
		return result2;
	}

	public IConnectionLayout[] getConnections(IEntityLayout source,
			IEntityLayout target) {
		ArrayList<IConnectionLayout> result = new ArrayList<IConnectionLayout>();

		ArrayList<IEntityLayout> sourcesList = new ArrayList<IEntityLayout>();
		if (source instanceof INodeLayout) {
			sourcesList.add(source);
		}
		if (source instanceof ISubgraphLayout) {
			sourcesList.addAll(Arrays.asList(((ISubgraphLayout) source)
					.getNodes()));
		}

		HashSet<IEntityLayout> targets = new HashSet<IEntityLayout>();
		if (target instanceof INodeLayout) {
			targets.add(target);
		}
		if (target instanceof ISubgraphLayout) {
			targets.addAll(Arrays.asList(((ISubgraphLayout) target).getNodes()));
		}

		for (Iterator<IEntityLayout> iterator = sourcesList.iterator(); iterator
				.hasNext();) {
			INodeLayout source2 = (INodeLayout) iterator.next();
			IConnectionLayout[] outgoingConnections = source2
					.getOutgoingConnections();
			for (int i = 0; i < outgoingConnections.length; i++) {
				IConnectionLayout connection = outgoingConnections[i];
				if ((connection.getSource() == source2 && targets
						.contains(connection.getTarget()))
						|| (connection.getTarget() == source2 && targets
								.contains(connection.getSource()))) {
					result.add(connection);
				}
			}

		}
		return result.toArray(new IConnectionLayout[result.size()]);
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

	void setExpanded(INodeLayout node, boolean expanded) {
		setExpandedInvocation = true;
		if (expandCollapseManager != null) {
			expandCollapseManager.setExpanded(this, node, expanded);
		}
		setExpandedInvocation = false;
	}

	// TODO: remove
	boolean canExpand(INodeLayout node) {
		return expandCollapseManager != null
				&& expandCollapseManager.canExpand(this, node);
	}

	boolean canCollapse(INodeLayout node) {
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
	 * {@link #setDynamicLayoutAlgorithm(ILayoutAlgorithm) main algorithm} in
	 * that it's always used when {@link #applyLayoutAlgorithm(boolean)} and not
	 * after firing of events.
	 */
	void setLayoutAlgorithm(ILayoutAlgorithm algorithm) {
		this.staticAlgorithm = algorithm;
		this.staticAlgorithm.setLayoutContext(this);
	}

	ILayoutAlgorithm getLayoutAlgorithm() {
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

	public ILayoutAlgorithm getStaticLayoutAlgorithm() {
		return staticAlgorithm;
	}

	public void setStaticLayoutAlgorithm(ILayoutAlgorithm algorithm) {
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

	public void fireNodeAddedEvent(INodeLayout node) {
		boolean intercepted = flushChangesInvocation;
		IGraphStructureListener[] listeners = graphStructureListeners
				.toArray(new IGraphStructureListener[graphStructureListeners
						.size()]);
		for (int i = 0; i < listeners.length && !intercepted; i++) {
			intercepted = listeners[i].nodeAdded(this, node);
		}
		if (!intercepted) {
			applyDynamicLayout(true);
		}
	}

	public void fireNodeRemovedEvent(INodeLayout node) {
		boolean intercepted = flushChangesInvocation;
		IGraphStructureListener[] listeners = graphStructureListeners
				.toArray(new IGraphStructureListener[graphStructureListeners
						.size()]);
		for (int i = 0; i < listeners.length && !intercepted; i++) {
			intercepted = listeners[i].nodeRemoved(this, node);
		}
		if (!intercepted) {
			applyDynamicLayout(true);
		}
	}

	public void fireConnectionAddedEvent(IConnectionLayout connection) {
		InternalLayoutContext sourceContext = ((InternalNodeLayout) connection
				.getSource()).getOwnerLayoutContext();
		InternalLayoutContext targetContext = ((InternalNodeLayout) connection
				.getTarget()).getOwnerLayoutContext();
		if (sourceContext != targetContext) {
			return;
		}
		if (sourceContext == this) {
			boolean intercepted = flushChangesInvocation;
			IGraphStructureListener[] listeners = graphStructureListeners
					.toArray(new IGraphStructureListener[graphStructureListeners
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

	public void fireConnectionRemovedEvent(IConnectionLayout connection) {
		InternalLayoutContext sourceContext = ((InternalNodeLayout) connection
				.getSource()).getOwnerLayoutContext();
		InternalLayoutContext targetContext = ((InternalNodeLayout) connection
				.getTarget()).getOwnerLayoutContext();
		if (sourceContext != targetContext) {
			return;
		}
		if (sourceContext == this) {
			boolean intercepted = flushChangesInvocation;
			IGraphStructureListener[] listeners = graphStructureListeners
					.toArray(new IGraphStructureListener[graphStructureListeners
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
		IContextListener[] listeners = contextListeners
				.toArray(new IContextListener[contextListeners.size()]);
		for (int i = 0; i < listeners.length && !intercepted; i++) {
			intercepted = listeners[i].boundsChanged(this);
		}
		if (!intercepted) {
			applyDynamicLayout(true);
		}
	}

	public void fireBackgroundEnableChangedEvent() {
		IContextListener[] listeners = contextListeners
				.toArray(new IContextListener[contextListeners.size()]);
		for (int i = 0; i < listeners.length; i++) {
			listeners[i].backgroundEnableChanged(this);
		}
	}

	public void fireNodeResizedEvent(INodeLayout nodeLayout) {
		InternalNodeLayout node = (InternalNodeLayout) nodeLayout;
		if (!flushChangesInvocation) {
			node.refreshSize();
			node.refreshLocation();
		}
		boolean intercepted = flushChangesInvocation;
		ILayoutListener[] listeners = layoutListeners
				.toArray(new ILayoutListener[layoutListeners.size()]);
		for (int i = 0; i < listeners.length && !intercepted; i++) {
			intercepted = listeners[i].nodeResized(this, node);
		}
		if (!intercepted) {
			applyDynamicLayout(true);
		}
	}

	public void fireSubgraphMovedEvent(ISubgraphLayout subgraphLayout) {
		DefaultSubgraph subgraph = (DefaultSubgraph) subgraphLayout;
		if (!flushChangesInvocation) {
			subgraph.refreshLocation();
		}
		boolean intercepted = flushChangesInvocation;
		ILayoutListener[] listeners = layoutListeners
				.toArray(new ILayoutListener[layoutListeners.size()]);
		for (int i = 0; i < listeners.length && !intercepted; i++) {
			intercepted = listeners[i].subgraphMoved(this, subgraph);
		}
		if (!intercepted) {
			applyDynamicLayout(true);
		}
	}

	public void fireSubgraphResizedEvent(ISubgraphLayout subgraphLayout) {
		DefaultSubgraph subgraph = (DefaultSubgraph) subgraphLayout;
		if (!flushChangesInvocation) {
			subgraph.refreshSize();
			subgraph.refreshLocation();
		}
		boolean intercepted = flushChangesInvocation;
		ILayoutListener[] listeners = layoutListeners
				.toArray(new ILayoutListener[layoutListeners.size()]);
		for (int i = 0; i < listeners.length && !intercepted; i++) {
			intercepted = listeners[i].subgraphResized(this, subgraph);
		}
		if (!intercepted) {
			applyDynamicLayout(true);
		}
	}

	public void fireNodeMovedEvent(INodeLayout nodeLayout) {
		InternalNodeLayout node = (InternalNodeLayout) nodeLayout;
		if (!flushChangesInvocation) {
			node.refreshLocation();
		}
		boolean intercepted = flushChangesInvocation;
		ILayoutListener[] listeners = layoutListeners
				.toArray(new ILayoutListener[layoutListeners.size()]);
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
		IContextListener[] listeners = contextListeners
				.toArray(new IContextListener[contextListeners.size()]);
		for (int i = 0; i < listeners.length; i++) {
			listeners[i].pruningEnablementChanged(this);
		}
	}

	public void setProperty(String name, Object value) {
		if (LayoutProperties.BOUNDS_PROPERTY.equals(name)) {
			// TODO: there is no setBounds() what to do here?
		} else if (LayoutProperties.BOUNDS_EXPANDABLE_PROPERTY.equals(name)) {
			// TODO: there is no setBoundsExpandable()
		} else if (LayoutProperties.DYNAMIC_LAYOUT_ENABLED_PROPERTY
				.equals(name)) {
			if (value instanceof Boolean) {
				setDynamicLayoutEnabled((Boolean) value);
			}
		} else {
			setp(name, value);
		}
	}

	public Object getProperty(String name) {
		if (LayoutProperties.BOUNDS_PROPERTY.equals(name)) {
			return getBounds();
		} else if (LayoutProperties.BOUNDS_EXPANDABLE_PROPERTY.equals(name)) {
			// TODO
			return false;
		} else if (LayoutProperties.DYNAMIC_LAYOUT_ENABLED_PROPERTY
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

	public void schedulePostLayoutPass(Runnable runnable) {
	}

	public void unschedulePostLayoutPass(Runnable runnable) {
	}

	public boolean isLayoutIrrelevant(IConnectionLayout connLayout) {
		return false;
	}

	public boolean isLayoutIrrelevant(INodeLayout nodeLayout) {
		return false;
	}

	public void addLayoutFilter(ILayoutFilter layoutFilter) {
	}

	public void removeLayoutFilter(ILayoutFilter layoutFilter) {
	}

	public void unschedulePreLayoutPass(Runnable runnable) {
	}

	public void schedulePreLayoutPass(Runnable runnable) {
	}

}