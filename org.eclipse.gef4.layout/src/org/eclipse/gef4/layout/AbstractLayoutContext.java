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
package org.eclipse.gef4.layout;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.gef4.common.properties.PropertyStoreSupport;
import org.eclipse.gef4.layout.listeners.IContextListener;
import org.eclipse.gef4.layout.listeners.IGraphStructureListener;
import org.eclipse.gef4.layout.listeners.ILayoutListener;
import org.eclipse.gef4.layout.listeners.LayoutListenerSupport;

/**
 * The {@link AbstractLayoutContext} is an abstract {@link ILayoutContext}
 * implementation which supports the (un-)registration of
 * {@link PropertyChangeListener}s and firing of events, the (un-)registration
 * of any layout listeners and firing of events, the handling and execution of
 * pre and post {@link Runnable}s, and filtering of layout objects using
 * {@link ILayoutFilter}.
 * 
 * @author mwienand
 *
 */
// TODO: replace fire* methods with property change mechanism
public abstract class AbstractLayoutContext implements ILayoutContext {

	private LayoutListenerSupport lls = new LayoutListenerSupport(this);
	private ILayoutAlgorithm dynamicLayoutAlgorithm = null;
	private ILayoutAlgorithm staticLayoutAlgorithm = null;
	private final List<INodeLayout> layoutNodes = new ArrayList<>();
	private final List<IConnectionLayout> layoutEdges = new ArrayList<>();

	private boolean flushChangesInvocation = false;

	private final List<Runnable> postLayoutPass = new ArrayList<>();
	private final List<Runnable> preLayoutPass = new ArrayList<>();
	private final List<ILayoutFilter> layoutFilters = new ArrayList<>();

	/**
	 * Support object for the (un-)registration of
	 * {@link PropertyChangeListener}s and firing of events.
	 */
	protected PropertyChangeSupport pcs = new PropertyChangeSupport(this);

	/**
	 * Support object for reading/writing general properties.
	 */
	protected PropertyStoreSupport pss = new PropertyStoreSupport(this, pcs);

	public void addContextListener(IContextListener listener) {
		lls.addContextListener(listener);
	}

	/**
	 * Adds the given {@link IConnectionLayout} to the list of edges and fires a
	 * corresponding connection-added-event.
	 *
	 * @param edge
	 *            {@link IConnectionLayout} to add
	 */
	protected void addEdge(IConnectionLayout edge) {
		layoutEdges.add(edge);
		fireConnectionAddedEvent(edge);
	}

	public void addGraphStructureListener(IGraphStructureListener listener) {
		lls.addGraphStructureListener(listener);
	}

	public void addLayoutFilter(ILayoutFilter layoutFilter) {
		layoutFilters.add(layoutFilter);
	}

	public void addLayoutListener(ILayoutListener listener) {
		lls.addLayoutListener(listener);
	}

	/**
	 * Adds the given {@link INodeLayout} to the list of nodes and fires a
	 * corresponding node-added-event.
	 *
	 * @param node
	 *            {@link INodeLayout} to add
	 */
	protected void addNode(INodeLayout node) {
		layoutNodes.add(node);
		fireNodeAddedEvent(node);
	}

	public void addPropertyChangeListener(PropertyChangeListener listener) {
		pcs.addPropertyChangeListener(listener);
	}

	public void applyDynamicLayout(boolean clear) {
		if (dynamicLayoutAlgorithm != null) {
			for (Runnable r : preLayoutPass) {
				r.run();
			}
			dynamicLayoutAlgorithm.applyLayout(clear);
		}
	}

	public void applyStaticLayout(boolean clear) {
		if (staticLayoutAlgorithm != null) {
			for (Runnable r : preLayoutPass) {
				r.run();
			}
			staticLayoutAlgorithm.setLayoutContext(this);
			staticLayoutAlgorithm.applyLayout(clear);
		}
	}

	/**
	 * Removes all edges from this context using individual
	 * {@link #removeEdge(IConnectionLayout)} calls.
	 */
	protected void clearEdges() {
		for (IConnectionLayout edge : new ArrayList<>(layoutEdges)) {
			removeEdge(edge);
		}
	}

	/**
	 * Removes all nodes from this context using individual
	 * {@link #removeNode(INodeLayout)} calls.
	 */
	protected void clearNodes() {
		for (INodeLayout node : new ArrayList<>(layoutNodes)) {
			removeNode(node);
		}
	}

	/**
	 * Executes all scheduled post-layout {@link Runnable}s (previously added by
	 * {@link #schedulePostLayoutPass(Runnable)}.
	 */
	protected void doFlushChanges() {
		for (Runnable r : new ArrayList<>(postLayoutPass)) {
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

	public void fireConnectionAddedEvent(IConnectionLayout connection) {
		if (!flushChangesInvocation) {
			lls.fireConnectionAddedEvent(connection);
		}
	}

	public void fireConnectionRemovedEvent(IConnectionLayout connection) {
		if (!flushChangesInvocation) {
			lls.fireConnectionRemovedEvent(connection);
		}
	}

	public void fireNodeAddedEvent(INodeLayout node) {
		if (!flushChangesInvocation) {
			lls.fireNodeAddedEvent(node);
		}
	}

	public void fireNodeMovedEvent(INodeLayout node) {
		if (!flushChangesInvocation) {
			lls.fireNodeMovedEvent(node);
		}
	}

	public void fireNodeRemovedEvent(INodeLayout node) {
		if (!flushChangesInvocation) {
			lls.fireNodeRemovedEvent(node);
		}
	}

	public void fireNodeResizedEvent(INodeLayout node) {
		if (!flushChangesInvocation) {
			lls.fireNodeResizedEvent(node);
		}
	}

	public void flushChanges() {
		flushChangesInvocation = true;
		doFlushChanges();
		flushChangesInvocation = false;
	}

	public IConnectionLayout[] getConnections() {
		return layoutEdges.toArray(new IConnectionLayout[0]);
	}

	public IConnectionLayout[] getConnections(INodeLayout layoutEntity1,
			INodeLayout layoutEntity2) {
		List<IConnectionLayout> connections = new ArrayList<>();

		for (IConnectionLayout c : layoutEntity1.getOutgoingConnections()) {
			if (c.getTarget() == layoutEntity2) {
				connections.add(c);
			}
		}

		for (IConnectionLayout c : layoutEntity2.getOutgoingConnections()) {
			if (c.getTarget() == layoutEntity1) {
				connections.add(c);
			}
		}

		return connections.toArray(new IConnectionLayout[0]);
	}

	public ILayoutAlgorithm getDynamicLayoutAlgorithm() {
		return dynamicLayoutAlgorithm;
	}

	public INodeLayout[] getNodes() {
		return layoutNodes.toArray(new INodeLayout[0]);
	}

	public Object getProperty(String name) {
		return pss.getProperty(name);
	}

	public ILayoutAlgorithm getStaticLayoutAlgorithm() {
		return staticLayoutAlgorithm;
	}

	public boolean isLayoutIrrelevant(IConnectionLayout connLayout) {
		for (ILayoutFilter filter : layoutFilters) {
			if (filter.isLayoutIrrelevant(connLayout)) {
				return true;
			}
		}
		return false;
	}

	public boolean isLayoutIrrelevant(INodeLayout nodeLayout) {
		for (ILayoutFilter filter : layoutFilters) {
			if (filter.isLayoutIrrelevant(nodeLayout)) {
				return true;
			}
		}
		return false;
	}

	public void removeContextListener(IContextListener listener) {
		lls.removeContextListener(listener);
	}

	/**
	 * Removes the given {@link IConnectionLayout} from the list of edges and
	 * fires a corresponding connection-removed-event.
	 *
	 * @param edge
	 *            {@link IConnectionLayout} to remove
	 */
	protected void removeEdge(IConnectionLayout edge) {
		layoutEdges.remove(edge);
		fireConnectionRemovedEvent(edge);
	}

	public void removeGraphStructureListener(IGraphStructureListener listener) {
		lls.removeGraphStructureListener(listener);
	}

	public void removeLayoutFilter(ILayoutFilter layoutFilter) {
		layoutFilters.remove(layoutFilter);
	}

	public void removeLayoutListener(ILayoutListener listener) {
		lls.removeLayoutListener(listener);
	}

	/**
	 * Removes the given {@link INodeLayout} from the managed list of nodes and
	 * fires a corresponding node-removed-event.
	 *
	 * @param node
	 *            {@link INodeLayout} to remove
	 */
	protected void removeNode(INodeLayout node) {
		layoutNodes.remove(node);
		fireNodeRemovedEvent(node);
	}

	public void removePropertyChangeListener(PropertyChangeListener listener) {
		pcs.removePropertyChangeListener(listener);
	}

	public void schedulePostLayoutPass(Runnable runnable) {
		if (runnable == null) {
			throw new IllegalArgumentException("Runnable may not be null.");
		}
		postLayoutPass.add(runnable);
	}

	public void schedulePreLayoutPass(Runnable runnable) {
		if (runnable == null) {
			throw new IllegalArgumentException("Runnable may not be null.");
		}
		preLayoutPass.add(runnable);
	}

	public void setDynamicLayoutAlgorithm(
			ILayoutAlgorithm dynamicLayoutAlgorithm) {
		ILayoutAlgorithm oldDynamicLayoutAlgorithm = this.dynamicLayoutAlgorithm;
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
			}
		}
		// TODO: check if this is really needed, as the property store already
		// fires the respective event
		pcs.firePropertyChange(name, oldValue, value);
	}

	public void setStaticLayoutAlgorithm(
			ILayoutAlgorithm staticLayoutAlgorithm) {
		ILayoutAlgorithm oldStaticLayoutAlgorithm = this.staticLayoutAlgorithm;
		if (oldStaticLayoutAlgorithm != staticLayoutAlgorithm) {
			this.staticLayoutAlgorithm = staticLayoutAlgorithm;
			staticLayoutAlgorithm.setLayoutContext(this);
			pcs.firePropertyChange(STATIC_LAYOUT_ALGORITHM_PROPERTY,
					oldStaticLayoutAlgorithm, staticLayoutAlgorithm);
		}
	}

	public void unschedulePostLayoutPass(Runnable runnable) {
		if (!postLayoutPass.contains(runnable)) {
			new IllegalArgumentException(
					"Given Runnable is not contained in the list.")
							.printStackTrace();
		}
		postLayoutPass.remove(runnable);
	}

	public void unschedulePreLayoutPass(Runnable runnable) {
		if (!preLayoutPass.contains(runnable)) {
			new IllegalArgumentException(
					"Given Runnable is not contained in the list.")
							.printStackTrace();
		}
		preLayoutPass.remove(runnable);
	}

}
