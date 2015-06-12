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
package org.eclipse.gef4.layout.listeners;

import org.eclipse.gef4.layout.ILayoutContext;
import org.eclipse.gef4.layout.INodeLayout;
import org.eclipse.gef4.layout.ISubgraphLayout;

/**
 * An {@link IPruningListener} is notified about pruning changes, i.e. adding
 * {@link INodeLayout}s to an {@link ISubgraphLayout} (pruning), or removing
 * {@link INodeLayout}s from an {@link ISubgraphLayout} (unpruning).
 */
public interface IPruningListener {

	/**
	 * This method is called when some nodes are pruned in a layout context. If
	 * <code>true</code> is returned, no dynamic layout will be applied after
	 * notifying all listeners, i.e. a dynamic layout pass will only be applied
	 * when all registered {@link IPruningListener}s return <code>false</code>.
	 * 
	 * @param context
	 *            the layout context that fired the event
	 * @param subgraph
	 *            subgraphs that have been created or had nodes added
	 * @return <code>true</code> if no dynamic layout should be applied
	 *         afterwards.
	 */
	public boolean nodesPruned(ILayoutContext context,
			ISubgraphLayout[] subgraph);

	/**
	 * This method is called when some nodes are unpruned in a layout context,
	 * that is they are no longer part of a subgraph. If <code>true</code> is
	 * returned, no dynamic layout will be applied after notifying all
	 * listeners, i.e. a dynamic layout pass will only be applied when all
	 * registered {@link IPruningListener}s return <code>false</code>.
	 * 
	 * @param context
	 *            the layout context that fired the event
	 * @param nodes
	 *            nodes that have been unpruned
	 * @return <code>true</code> if no dynamic layout should be applied
	 *         afterwards.
	 */
	public boolean nodesUnpruned(ILayoutContext context, INodeLayout[] nodes);

}
