/*******************************************************************************
 * Copyright (c) 2014, 2017 itemis AG and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Matthias Wienand (itemis AG) - initial API & implementation
 *
 *******************************************************************************/
package org.eclipse.gef.layout;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.gef.graph.Edge;
import org.eclipse.gef.graph.Graph;
import org.eclipse.gef.graph.Node;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ObservableList;

/**
 * The {@link LayoutContext} provides the context in which to layout a
 * {@link Graph}. It maintains an {@link ILayoutAlgorithm} that can be applied,
 * supports the handling and execution of pre and post {@link Runnable}s, and
 * filtering of layout objects using {@link ILayoutFilter}.
 * 
 * @author mwienand
 *
 */
public class LayoutContext {

	/**
	 * An {@link LayoutContext} notifies registered listeners about changes to
	 * the layout algorithm using this property name.
	 */
	public static final String LAYOUT_ALGORITHM_PROPERTY = "layoutAlgorithm";

	private ObjectProperty<ILayoutAlgorithm> layoutAlgorithmProperty = new SimpleObjectProperty<>(
			this, LAYOUT_ALGORITHM_PROPERTY);

	private Graph graph;
	private final List<Runnable> postLayoutPass = new ArrayList<>();
	private final List<Runnable> preLayoutPass = new ArrayList<>();
	private final List<ILayoutFilter> layoutFilters = new ArrayList<>();

	/**
	 * Adds the given ILayoutFilter to this {@link LayoutContext}.
	 * 
	 * @param layoutFilter
	 *            The ILayoutFilter to add to this context.
	 */
	public void addLayoutFilter(ILayoutFilter layoutFilter) {
		layoutFilters.add(layoutFilter);
	}

	/**
	 * Applies the layout algorithm of this LayoutContext. The clean flag is
	 * passed-in to the layout algorithm to indicate whether the context changed
	 * significantly since the last layout pass.
	 * 
	 * @param clear
	 *            <code>true</code> to indicate that the algorithm has to fully
	 *            re-compute the layout, otherwise <code>false</code>.
	 */
	public void applyLayout(boolean clear) {
		ILayoutAlgorithm layoutAlgorithm = layoutAlgorithmProperty.get();
		if (layoutAlgorithm != null) {
			preLayout();
			layoutAlgorithm.applyLayout(this, clear);
			postLayout();
		}
	}

	/**
	 * Initiated by the context or by an {@link ILayoutAlgorithm} to perform
	 * steps that are scheduled to be run after the layout pass. Should not be
	 * called by clients.
	 */
	public void postLayout() {
		for (Runnable r : new ArrayList<>(postLayoutPass)) {
			r.run();
		}
	}

	/**
	 * Initiated by the context or by an {@link ILayoutAlgorithm} to perform
	 * steps that are scheduled to be run before the layout pass. Should not be
	 * called by clients.
	 */
	public void preLayout() {
		for (Runnable r : preLayoutPass) {
			r.run();
		}
	}

	/**
	 * Returns the graph that is to be layouted.
	 * 
	 * @return The {@link Graph} that is to be layouted.
	 */
	public Graph getGraph() {
		return graph;
	}

	/**
	 * Sets the graph that is to be layouted by this context.
	 * 
	 * @param graph
	 *            The {@link Graph} to layout.
	 */
	public void setGraph(Graph graph) {
		// TODO: we should not allow to pass in null here. Instead we should
		// guard ourselves against null.
		if (graph == null) {
			graph = new Graph();
		}
		this.graph = graph;
	}

	/**
	 * Returns all the nodes that should be laid out. Replacing elements in the
	 * returned array does not affect this context.
	 * 
	 * @return array of nodes to lay out
	 */
	// TODO: remove this (algorithms should use getGraph().getNodes())
	public Node[] getNodes() {
		ObservableList<Node> nodes = graph.getNodes();
		List<Node> layoutRelevantNodes = new ArrayList<>();
		for (Node n : nodes) {
			if (!isLayoutIrrelevant(n)) {
				layoutRelevantNodes.add(n);
			}
		}
		return layoutRelevantNodes.toArray(new Node[] {});
	}

	/**
	 * Returns all the connections between nodes that should be laid out.
	 * Replacing elements in the returned array does not affect this context.
	 * 
	 * @return array of connections between nodes
	 */
	public Edge[] getEdges() {
		ObservableList<Edge> edges = graph.getEdges();
		List<Edge> layoutRelevantEdges = new ArrayList<>();
		for (Edge e : edges) {
			if (!isLayoutIrrelevant(e)) {
				layoutRelevantEdges.add(e);
			}
		}
		return layoutRelevantEdges.toArray(new Edge[] {});
	}

	/**
	 * Returns the static layout algorithm used to layout a newly initialized
	 * graph or after heavy changes to it.
	 * 
	 * @return The layout algorithm that is used by this {@link LayoutContext}.
	 */
	public ILayoutAlgorithm getLayoutAlgorithm() {
		return layoutAlgorithmProperty.get();
	}

	/**
	 * Returns <code>true</code> when the given {@link Edge} is not relevant for
	 * layout according to the configured {@link ILayoutFilter layout filters}.
	 * Otherwise returns <code>false</code>.
	 * 
	 * @param edge
	 *            The {@link Edge} in question.
	 * @return <code>true</code> when the given {@link Edge} is not relevant for
	 *         layout according to the configure layout filters, otherwise
	 *         <code>false</code>.
	 */
	public boolean isLayoutIrrelevant(Edge edge) {
		for (ILayoutFilter filter : layoutFilters) {
			if (filter.isLayoutIrrelevant(edge)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Returns <code>true</code> when the given {@link Node} is not relevant for
	 * layout according to the configured {@link ILayoutFilter layout filters}.
	 * Otherwise returns <code>false</code>.
	 * 
	 * @param nodeLayout
	 *            The {@link Node} in question.
	 * @return <code>true</code> when the given {@link Node} is not relevant for
	 *         layout according to the configure layout filters, otherwise
	 *         <code>false</code>.
	 */
	public boolean isLayoutIrrelevant(Node nodeLayout) {
		for (ILayoutFilter filter : layoutFilters) {
			if (filter.isLayoutIrrelevant(nodeLayout)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * A property representing the layout algorithm used by this
	 * {@link LayoutContext}.
	 * 
	 * @see #getLayoutAlgorithm()
	 * @see #setLayoutAlgorithm(ILayoutAlgorithm)
	 * 
	 * @return A property named {@link #LAYOUT_ALGORITHM_PROPERTY}.
	 */
	public ObjectProperty<ILayoutAlgorithm> layoutAlgorithmProperty() {
		return layoutAlgorithmProperty;
	};

	/**
	 * Removes the given ILayoutFilter from this {@link LayoutContext}.
	 * 
	 * @param layoutFilter
	 *            The ILayoutFilter to remove to this context.
	 */
	public void removeLayoutFilter(ILayoutFilter layoutFilter) {
		layoutFilters.remove(layoutFilter);
	}

	/**
	 * Adds the given {@link Runnable} to the list of runnables which are called
	 * when this {@link LayoutContext} is asked to apply all changes made to its
	 * elements to the display.
	 * 
	 * @param runnable
	 *            A {@link Runnable} called whenever this context is asked to
	 *            apply all changes made to its elements to the display.
	 */
	public void schedulePostLayoutPass(Runnable runnable) {
		if (runnable == null) {
			throw new IllegalArgumentException("Runnable may not be null.");
		}
		if (!postLayoutPass.contains(runnable)) {
			postLayoutPass.add(runnable);
		}
	}

	/**
	 * Adds the given {@link Runnable} to the list of {@link Runnable}s which
	 * are executed before applying a layout, i.e. before
	 * {@link #applyLayout(boolean)}.
	 * 
	 * @param runnable
	 *            The {@link Runnable} to add to the list of {@link Runnable}s
	 *            which are executed before applying a layout.
	 */
	public void schedulePreLayoutPass(Runnable runnable) {
		if (runnable == null) {
			throw new IllegalArgumentException("Runnable may not be null.");
		}
		if (!preLayoutPass.contains(runnable)) {
			preLayoutPass.add(runnable);
		}
	}

	/**
	 * Sets the layout algorithm for this context.
	 * 
	 * @param algorithm
	 *            The new {@link ILayoutAlgorithm} for this
	 *            {@link LayoutContext}.
	 */
	public void setLayoutAlgorithm(ILayoutAlgorithm algorithm) {
		layoutAlgorithmProperty.set(algorithm);
	}

	/**
	 * Removes the given {@link Runnable} from the list of runnables which are
	 * called when this {@link LayoutContext} is asked to apply all changes made
	 * to its elements to the display.
	 * 
	 * @param runnable
	 *            The {@link Runnable} that should no longer get called when
	 *            flushing changes.
	 */
	public void unschedulePostLayoutPass(Runnable runnable) {
		if (runnable == null) {
			throw new IllegalArgumentException("Runnable may not be null.");
		}
		if (postLayoutPass.contains(runnable)) {
			postLayoutPass.remove(runnable);
		}
	}

	/**
	 * Removes the given {@link Runnable} from the list of {@link Runnable}s
	 * which are executed before applying a layout, i.e. before
	 * {@link #applyLayout(boolean)}.
	 * 
	 * @param runnable
	 *            The {@link Runnable} to remove from the list of
	 *            {@link Runnable}s which are executed before applying a layout.
	 */
	public void unschedulePreLayoutPass(Runnable runnable) {
		if (runnable == null) {
			throw new IllegalArgumentException("Runnable may not be null.");
		}
		if (preLayoutPass.contains(runnable)) {
			preLayoutPass.remove(runnable);
		}
	}
}
