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

import org.eclipse.gef4.common.attributes.IAttributeStore;

import javafx.beans.property.ObjectProperty;

/**
 * Objects implementing {@link ILayoutContext} interface are used for exchanging
 * of information between layout algorithms and graphical objects displaying
 * graphs, i.e. an {@link ILayoutContext} represents a graph within the layout
 * model.
 */
public interface ILayoutContext extends IAttributeStore {

	/**
	 * An {@link ILayoutContext} notifies registered listeners about changes to
	 * the layout algorithm using this property name.
	 */
	public static final String LAYOUT_ALGORITHM_PROPERTY = "layoutAlgorithm";

	/**
	 * A property representing the layout algorithm used by this
	 * {@link ILayoutContext}.
	 * 
	 * @see #getLayoutAlgorithm()
	 * @see #setLayoutAlgorithm(ILayoutAlgorithm)
	 * 
	 * @return A property named {@link #LAYOUT_ALGORITHM_PROPERTY}.
	 */
	public ObjectProperty<ILayoutAlgorithm> layoutAlgorithmProperty();

	/**
	 * Applies the layout algorithm of this LayoutContext. The clean flag is
	 * passed-in to the layout algorithm to indicate whether the context changed
	 * significantly since the last layout pass.
	 * 
	 * @param clean
	 *            <code>true</code> to indicate that the algorithm has to fully
	 *            re-compute the layout, otherwise <code>false</code>.
	 */
	public void applyLayout(boolean clean);

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
	public IConnectionLayout[] getEdges();

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
	 * Sets the static layout algorithm for this context. The static algorithm
	 * has to be manually invoked, for example, after significant changes to the
	 * context.
	 * 
	 * @param algorithm
	 *            The new static {@link ILayoutAlgorithm} for this
	 *            {@link ILayoutContext}.
	 */
	public void setLayoutAlgorithm(ILayoutAlgorithm algorithm);

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
	 * @return The layout algorithm that is used by this {@link ILayoutContext}.
	 */
	public ILayoutAlgorithm getLayoutAlgorithm();

	/**
	 * Causes all the changes made to elements in this context to affect the
	 * display. Called from layout algorithms on finish of layout.
	 */
	public void flushChanges();

	/**
	 * Removes the given {@link Runnable} from the list of {@link Runnable}s
	 * which are executed before applying a layout, i.e. before
	 * {@link #applyLayout(boolean)}.
	 * 
	 * @param runnable
	 *            The {@link Runnable} to remove from the list of
	 *            {@link Runnable}s which are executed before applying a layout.
	 */
	public void unschedulePreLayoutPass(Runnable runnable);

	/**
	 * Adds the given {@link Runnable} to the list of {@link Runnable}s which
	 * are executed before applying a layout, i.e. before
	 * {@link #applyLayout(boolean)}.
	 * 
	 * @param runnable
	 *            The {@link Runnable} to add to the list of {@link Runnable}s
	 *            which are executed before applying a layout.
	 */
	public void schedulePreLayoutPass(Runnable runnable);

}
