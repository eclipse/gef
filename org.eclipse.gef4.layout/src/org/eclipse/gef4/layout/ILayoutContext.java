/*******************************************************************************
 * Copyright (c) 2009, 2015 Mateusz Matela and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors: Mateusz Matela - initial API and implementation
 *               Ian Bull
 ******************************************************************************/
package org.eclipse.gef4.layout;

import org.eclipse.gef4.common.properties.IPropertyStore;
import org.eclipse.gef4.layout.listeners.IContextListener;
import org.eclipse.gef4.layout.listeners.IGraphStructureListener;
import org.eclipse.gef4.layout.listeners.ILayoutListener;

/**
 * Objects implementing {@link ILayoutContext} interface are used for exchanging
 * of information between layout algorithms and graphical objects displaying
 * graphs, i.e. an {@link ILayoutContext} represents a graph within the layout
 * model.
 */
public interface ILayoutContext extends IPropertyStore {

	/**
	 * An {@link ILayoutContext} notifies registered listeners about changes to
	 * the static layout algorithm using this property name.
	 */
	public static final String STATIC_LAYOUT_ALGORITHM_PROPERTY = "staticLayoutAlgorithm";

	/**
	 * An {@link ILayoutContext} notifies registered listeners about changes to
	 * the dynamic layout algorithm using this property name.
	 */
	public static final String DYNAMIC_LAYOUT_ALGORITHM_PROPERTY = "dynamicLayoutAlgorithm";

	/**
	 * Applies the background layout algorithm of this LayoutContext. The clean
	 * flag is passed-in to the background layout algorithm to indicate whether
	 * the context changed significantly since the last layout pass.
	 * 
	 * @param clean
	 *            <code>true</code> to indicate that the algorithm has to fully
	 *            re-compute the layout, otherwise <code>false</code>.
	 */
	public void applyDynamicLayout(boolean clean);

	/**
	 * Applies the static layout algorithm of this LayoutContext. The clean flag
	 * is passed-in to the static layout algorithm to indicate whether the
	 * context changed significantly since the last layout pass.
	 * 
	 * @param clean
	 *            <code>true</code> to indicate that the algorithm has to fully
	 *            re-compute the layout, otherwise <code>false</code>.
	 */
	public void applyStaticLayout(boolean clean);

	/**
	 * Returns all the nodes that should be laid out. Replacing elements in the
	 * returned array does not affect this context.
	 * 
	 * @return array of nodes to lay out
	 */
	public INodeLayout[] getNodes();

	/**
	 * Returns all the connections between nodes that should be laid out.
	 * Replacing elements in the returned array does not affect this context.
	 * 
	 * @return array of connections between nodes
	 */
	public IConnectionLayout[] getConnections();

	/**
	 * Returns all the connections between given source and target entities. If
	 * given entity is a subgraph, connections adjacent to each of its nodes
	 * will be included in the result. All the undirected nodes connecting the
	 * two nodes will be also included in the result. Replacing elements in the
	 * returned array does not affect this context.
	 * 
	 * @param layoutEntity1
	 *            The source entity.
	 * @param layoutEntity2
	 *            The target entity.
	 * @return The connections between the source and target entities.
	 */
	public IConnectionLayout[] getConnections(INodeLayout layoutEntity1,
			INodeLayout layoutEntity2);

	/**
	 * Sets the dynamic layout algorithm for this context. This algorithm will
	 * be used to relayout graph items using
	 * {@link ILayoutAlgorithm#applyLayout(boolean)} after every event that is
	 * not intercepted by any listener when currently changes are not being
	 * flushed and background layout is enabled. The clean flag for the
	 * background layout algorithm can be set to <code>false</code> by the
	 * context when reacting to events.
	 * 
	 * @param algorithm
	 *            The new dynamic {@link ILayoutAlgorithm} for this
	 *            {@link ILayoutContext}.
	 */
	public void setDynamicLayoutAlgorithm(ILayoutAlgorithm algorithm);

	/**
	 * Sets the static layout algorithm for this context. The static algorithm
	 * has to be manually invoked, for example, after significant changes to the
	 * context.
	 * 
	 * @param algorithm
	 *            The new static {@link ILayoutAlgorithm} for this
	 *            {@link ILayoutContext}.
	 */
	public void setStaticLayoutAlgorithm(ILayoutAlgorithm algorithm);

	/**
	 * Adds the given {@link Runnable} to the list of runnables which are called
	 * when this {@link ILayoutContext} is asked to apply all changes made to
	 * its elements to the display, i.e. within {@link #flushChanges()}.
	 * 
	 * @param runnable
	 *            A {@link Runnable} called whenever this context is asked to
	 *            apply all changes made to its elements to the display.
	 */
	public void schedulePostLayoutPass(Runnable runnable);

	/**
	 * Removes the given {@link Runnable} from the list of runnables which are
	 * called when this {@link ILayoutContext} is asked to apply all changes
	 * made to its elements to the display, i.e. within {@link #flushChanges()}.
	 * 
	 * @param runnable
	 *            The {@link Runnable} that should no longer get called when
	 *            flushing changes.
	 */
	public void unschedulePostLayoutPass(Runnable runnable);

	/**
	 * Returns <code>true</code> when the given {@link IConnectionLayout} is not
	 * relevant for layout according to the configured {@link ILayoutFilter
	 * layout filters}. Otherwise returns <code>false</code>.
	 * 
	 * @param connLayout
	 *            The {@link IConnectionLayout} in question.
	 * @return <code>true</code> when the given {@link IConnectionLayout} is not
	 *         relevant for layout according to the configure layout filters,
	 *         otherwise <code>false</code>.
	 */
	public boolean isLayoutIrrelevant(IConnectionLayout connLayout);

	/**
	 * Returns <code>true</code> when the given {@link INodeLayout} is not
	 * relevant for layout according to the configured {@link ILayoutFilter
	 * layout filters}. Otherwise returns <code>false</code>.
	 * 
	 * @param nodeLayout
	 *            The {@link INodeLayout} in question.
	 * @return <code>true</code> when the given {@link INodeLayout} is not
	 *         relevant for layout according to the configure layout filters,
	 *         otherwise <code>false</code>.
	 */
	public boolean isLayoutIrrelevant(INodeLayout nodeLayout);

	/**
	 * Adds the given ILayoutFilter to this {@link ILayoutContext}.
	 * 
	 * @param layoutFilter
	 *            The ILayoutFilter to add to this context.
	 */
	public void addLayoutFilter(ILayoutFilter layoutFilter);

	/**
	 * Removes the given ILayoutFilter from this {@link ILayoutContext}.
	 * 
	 * @param layoutFilter
	 *            The ILayoutFilter to remove to this context.
	 */
	public void removeLayoutFilter(ILayoutFilter layoutFilter);

	/**
	 * Returns the static layout algorithm used to layout a newly initialized
	 * graph or after heavy changes to it.
	 * 
	 * @return the static layout algorithm
	 */
	public ILayoutAlgorithm getStaticLayoutAlgorithm();

	/**
	 * @return the background algorithm of this context (see
	 *         {@link #setDynamicLayoutAlgorithm(ILayoutAlgorithm)} for details)
	 */
	public ILayoutAlgorithm getDynamicLayoutAlgorithm();

	/**
	 * Adds a listener to the context that will be notified about changes in
	 * this context's layout, that is movement and resizing of nodes /
	 * subgraphs. The notifications will not include changes made with API
	 * included in layout related interfaces, so that layout algorithms won't be
	 * notified about changes they invoke. Only internal changes of the system
	 * will fire events.
	 * 
	 * @param listener
	 *            listener to add
	 */
	public void addLayoutListener(ILayoutListener listener);

	/**
	 * Removes a layout listener from this context.
	 * 
	 * @param listener
	 *            listener to remove
	 */
	public void removeLayoutListener(ILayoutListener listener);

	/**
	 * Adds a listener to the context that will be notified about changes in
	 * graph structure, that is addition and removal of nodes and connections.
	 * The notifications will not include changes made with API included in
	 * layout related interfaces, so that layout algorithms won't be notified
	 * about changes they invoke. Only internal changes of the system will fire
	 * events.
	 * 
	 * @param listener
	 *            listener to add
	 */
	public void addGraphStructureListener(IGraphStructureListener listener);

	/**
	 * Removes a graph structure listener from this context.
	 * 
	 * @param listener
	 *            listener to remove
	 */
	public void removeGraphStructureListener(IGraphStructureListener listener);

	/**
	 * Adds a listener to the context that will be notified about changes
	 * related to its configuration.
	 * 
	 * @param listener
	 *            listener to add
	 */
	public void addContextListener(IContextListener listener);

	/**
	 * Removes a context listener from this context.
	 * 
	 * @param listener
	 *            listener to remove
	 */
	public void removeContextListener(IContextListener listener);

	/**
	 * Causes all the changes made to elements in this context to affect the
	 * display. Called from layout algorithms on finish of layout.
	 */
	public void flushChanges();

	/**
	 * Notifies all previously registered {@link IGraphStructureListener}s about
	 * the newly added node.
	 * 
	 * @param node
	 *            NodeLayout of newly added node
	 */
	public void fireNodeAddedEvent(INodeLayout node);

	/**
	 * Notifies all previously registered {@link IGraphStructureListener}s about
	 * the removed node.
	 * 
	 * @param node
	 *            NodeLayout of removed node
	 */
	public void fireNodeRemovedEvent(INodeLayout node);

	/**
	 * Notifies all previously registered {@link IGraphStructureListener}s about
	 * the newly added connection.
	 * 
	 * @param connection
	 *            ConnectionLayout of newly added connection
	 */
	public void fireConnectionAddedEvent(IConnectionLayout connection);

	/**
	 * Notifies all previously registered {@link IGraphStructureListener}s about
	 * the removed connection.
	 * 
	 * @param connection
	 *            ConnectionLayout of removed connection
	 */
	public void fireConnectionRemovedEvent(IConnectionLayout connection);

	/**
	 * Notifies all previously registered {@link IContextListener}s about the
	 * bounds change.
	 */
	public void fireBoundsChangedEvent();

	/**
	 * Notifies all previously registered {@link IContextListener}s about the
	 * state of the background layout flag.
	 */
	public void fireBackgroundEnableChangedEvent();

	/**
	 * Notifies all previously registered {@link ILayoutListener}s about the
	 * moved node.
	 * 
	 * @param node
	 *            NodeLayout of moved node
	 */
	public void fireNodeMovedEvent(INodeLayout node);

	/**
	 * Notifies all previously registered {@link ILayoutListener}s about the
	 * resized node.
	 * 
	 * @param node
	 *            NodeLayout of resized node
	 */
	public void fireNodeResizedEvent(INodeLayout node);

	/**
	 * Removes the given {@link Runnable} from the list of {@link Runnable}s
	 * which are executed before applying a layout, i.e. before
	 * {@link #applyDynamicLayout(boolean)} or
	 * {@link #applyStaticLayout(boolean)}.
	 * 
	 * @param runnable
	 *            The {@link Runnable} to remove from the list of
	 *            {@link Runnable}s which are executed before applying a layout.
	 */
	public void unschedulePreLayoutPass(Runnable runnable);

	/**
	 * Adds the given {@link Runnable} to the list of {@link Runnable}s which
	 * are executed before applying a layout, i.e. before
	 * {@link #applyDynamicLayout(boolean)} or
	 * {@link #applyStaticLayout(boolean)}.
	 * 
	 * @param runnable
	 *            The {@link Runnable} to add to the list of {@link Runnable}s
	 *            which are executed before applying a layout.
	 */
	public void schedulePreLayoutPass(Runnable runnable);

}
