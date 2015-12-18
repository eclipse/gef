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
import java.util.ArrayList;
import java.util.List;

import org.eclipse.gef4.common.properties.PropertyStoreSupport;

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
// TODO: replace fire* methods with property change mechanism -> layout
// interfaces all extend IPropertyStore, thus are IPropertyChangeNotifier
public abstract class AbstractLayoutContext implements ILayoutContext {

	private ILayoutAlgorithm layoutAlgorithm = null;
	private final List<INodeLayout> layoutNodes = new ArrayList<>();
	private final List<IConnectionLayout> layoutEdges = new ArrayList<>();

	private final List<Runnable> postLayoutPass = new ArrayList<>();
	private final List<Runnable> preLayoutPass = new ArrayList<>();
	private final List<ILayoutFilter> layoutFilters = new ArrayList<>();

	/**
	 * Support object for reading/writing general properties.
	 */
	protected PropertyStoreSupport pss = new PropertyStoreSupport(this);

	/**
	 * Adds the given {@link IConnectionLayout} to the list of edges and fires a
	 * corresponding connection-added-event.
	 *
	 * @param edge
	 *            {@link IConnectionLayout} to add
	 */
	protected void addEdge(IConnectionLayout edge) {
		layoutEdges.add(edge);
	}

	public void addLayoutFilter(ILayoutFilter layoutFilter) {
		layoutFilters.add(layoutFilter);
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
	}

	public void addPropertyChangeListener(PropertyChangeListener listener) {
		pss.addPropertyChangeListener(listener);
	}

	public void applyLayout(boolean clear) {
		if (layoutAlgorithm != null) {
			for (Runnable r : preLayoutPass) {
				r.run();
			}
			layoutAlgorithm.setLayoutContext(this);
			layoutAlgorithm.applyLayout(clear);
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

	public void flushChanges() {
		doFlushChanges();
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

	public INodeLayout[] getNodes() {
		return layoutNodes.toArray(new INodeLayout[0]);
	}

	public Object getProperty(String name) {
		return pss.getProperty(name);
	}

	public ILayoutAlgorithm getLayoutAlgorithm() {
		return layoutAlgorithm;
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

	/**
	 * Removes the given {@link IConnectionLayout} from the list of edges and
	 * fires a corresponding connection-removed-event.
	 *
	 * @param edge
	 *            {@link IConnectionLayout} to remove
	 */
	protected void removeEdge(IConnectionLayout edge) {
		layoutEdges.remove(edge);
	}

	public void removeLayoutFilter(ILayoutFilter layoutFilter) {
		layoutFilters.remove(layoutFilter);
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
	}

	public void removePropertyChangeListener(PropertyChangeListener listener) {
		pss.removePropertyChangeListener(listener);
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

	public void setProperty(String name, Object value) {
		// will set property value and fire notification (if value changed).
		pss.setProperty(name, value);
	}

	public void setLayoutAlgorithm(ILayoutAlgorithm newLayoutAlgorithm) {
		ILayoutAlgorithm oldLayoutAlgorithm = this.layoutAlgorithm;
		if (oldLayoutAlgorithm != newLayoutAlgorithm) {
			this.layoutAlgorithm = newLayoutAlgorithm;
			newLayoutAlgorithm.setLayoutContext(this);
			pss.firePropertyChange(LAYOUT_ALGORITHM_PROPERTY,
					oldLayoutAlgorithm, newLayoutAlgorithm);
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
