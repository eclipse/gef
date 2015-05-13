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
import org.eclipse.gef4.layout.ILayoutAlgorithm;

/**
 * 
 * @author irbull
 * 
 * @noextend This interface is not intended to be extended by clients.
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface ILayoutListener {

	/**
	 * This method is called whenever location of a particular node is changed
	 * within observed context. This usually implicates change of position (the
	 * center of the node) and the receiver should be aware of it (no additional
	 * {@link #nodeMoved(ILayoutContext, INodeLayout)} event will be fired). If
	 * true is returned, it means that the receiving listener has intercepted
	 * this event. Intercepted events will not be passed to the rest of the
	 * listeners. If the event is not intercepted by any listener,
	 * {@link ILayoutAlgorithm#applyLayout(boolean)} will be called on the
	 * context's main algorithm.LayoutListener
	 * 
	 * @param context
	 *            the layout context that fired the event
	 * @param node
	 *            the node that has moved
	 * @return true if no further operations after this event are required
	 */
	public boolean nodeMoved(ILayoutContext context, INodeLayout node);

	/**
	 * This method is called whenever size of a particular node is changed
	 * within observed context. This usually implicates change of position (the
	 * center of the node) and the receiver should be aware of it (no additional
	 * {@link #nodeMoved(ILayoutContext, INodeLayout)} event will be fired).
	 * 
	 * If true is returned, it means that the receiving listener has intercepted
	 * this event. Intercepted events will not be passed to the rest of the
	 * listeners. If the event is not intercepted by any listener,
	 * {@link ILayoutAlgorithm#applyLayout(boolean)} will be called on the
	 * context's main algorithm.
	 * 
	 * @param context
	 *            the layout context that fired the event
	 * @param node
	 *            the node that was resized
	 * @return true if no further operations after this event are required
	 */
	public boolean nodeResized(ILayoutContext context, INodeLayout node);

	/**
	 * This method is called whenever location of a particular subgraph is
	 * changed within observed context. If true is returned, it means that the
	 * receiving listener has intercepted this event. Intercepted events will
	 * not be passed to the rest of the listeners. If the event is not
	 * intercepted by any listener, {@link ILayoutAlgorithm#applyLayout(boolean)}
	 * will be called on the context's main algorithm.
	 * 
	 * @param context
	 *            the layout context that fired the event
	 * @param subgraph
	 *            the subgraph that has moved
	 * @return true if no further operations after this event are required
	 */
	public boolean subgraphMoved(ILayoutContext context, ISubgraphLayout subgraph);

	/**
	 * This method is called whenever size of a particular subgraph is changed
	 * within observed context. This usually implicates change of position (the
	 * center of the node) and the receiver should be aware of it (no additional
	 * {@link #nodeMoved(ILayoutContext, INodeLayout)} event will be fired).
	 * 
	 * If true is returned, it means that the receiving listener has intercepted
	 * this event. Intercepted events will not be passed to the rest of the
	 * listeners. If the event is not intercepted by any listener,
	 * {@link ILayoutAlgorithm#applyLayout(boolean)} will be called on the
	 * context's main algorithm.
	 * 
	 * @param context
	 *            the layout context that fired the event
	 * @param subgraph
	 *            the subgraph that was resized
	 * @return true if no further operations after this event are required
	 */
	public boolean subgraphResized(ILayoutContext context,
			ISubgraphLayout subgraph);

}
