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

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

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
public abstract class AbstractLayoutContext implements ILayoutContext {

	private ObjectProperty<ILayoutAlgorithm> layoutAlgorithmProperty = new SimpleObjectProperty<>(
			this, LAYOUT_ALGORITHM_PROPERTY);
	private final List<INodeLayout> layoutNodes = new ArrayList<>();
	private final List<IConnectionLayout> layoutEdges = new ArrayList<>();

	private final List<Runnable> postLayoutPass = new ArrayList<>();
	private final List<Runnable> preLayoutPass = new ArrayList<>();
	private final List<ILayoutFilter> layoutFilters = new ArrayList<>();

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

	@Override
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

	@Override
	public void applyLayout(boolean clear) {
		ILayoutAlgorithm layoutAlgorithm = layoutAlgorithmProperty.get();
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

	@Override
	public void flushChanges() {
		doFlushChanges();
	}

	@Override
	public IConnectionLayout[] getEdges() {
		return layoutEdges.toArray(new IConnectionLayout[0]);
	}

	@Override
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

	@Override
	public INodeLayout[] getNodes() {
		return layoutNodes.toArray(new INodeLayout[0]);
	}

	@Override
	public ILayoutAlgorithm getLayoutAlgorithm() {
		return layoutAlgorithmProperty.get();
	}

	@Override
	public boolean isLayoutIrrelevant(IConnectionLayout connLayout) {
		for (ILayoutFilter filter : layoutFilters) {
			if (filter.isLayoutIrrelevant(connLayout)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean isLayoutIrrelevant(INodeLayout nodeLayout) {
		for (ILayoutFilter filter : layoutFilters) {
			if (filter.isLayoutIrrelevant(nodeLayout)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public ObjectProperty<ILayoutAlgorithm> layoutAlgorithmProperty() {
		return layoutAlgorithmProperty;
	};

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

	@Override
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

	@Override
	public void schedulePostLayoutPass(Runnable runnable) {
		if (runnable == null) {
			throw new IllegalArgumentException("Runnable may not be null.");
		}
		if (!postLayoutPass.contains(runnable)) {
			postLayoutPass.add(runnable);
		}
	}

	@Override
	public void schedulePreLayoutPass(Runnable runnable) {
		if (runnable == null) {
			throw new IllegalArgumentException("Runnable may not be null.");
		}
		if (!preLayoutPass.contains(runnable)) {
			preLayoutPass.add(runnable);
		}
	}

	@Override
	public void setLayoutAlgorithm(ILayoutAlgorithm newLayoutAlgorithm) {
		layoutAlgorithmProperty.set(newLayoutAlgorithm);
	}

	@Override
	public void unschedulePostLayoutPass(Runnable runnable) {
		if (runnable == null) {
			throw new IllegalArgumentException("Runnable may not be null.");
		}
		if (postLayoutPass.contains(runnable)) {
			postLayoutPass.remove(runnable);
		}
	}

	@Override
	public void unschedulePreLayoutPass(Runnable runnable) {
		if (runnable == null) {
			throw new IllegalArgumentException("Runnable may not be null.");
		}
		if (preLayoutPass.contains(runnable)) {
			preLayoutPass.remove(runnable);
		}
	}

}
