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
	 * <code>true</code> is returned, no dynamic layout will be applied after
	 * notifying all listeners, i.e. a dynamic layout pass will only be applied
	 * when all registered {@link ILayoutListener}s return <code>false</code>.
	 * 
	 * @param context
	 *            the layout context that fired the event
	 * @param node
	 *            the node that has moved
	 * @return <code>true</code> if no dynamic layout should be applied
	 *         afterwards.
	 */
	public boolean nodeMoved(ILayoutContext context, INodeLayout node);

	/**
	 * This method is called whenever size of a particular node is changed
	 * within observed context. This usually implicates change of position (the
	 * center of the node) and the receiver should be aware of it (no additional
	 * {@link #nodeMoved(ILayoutContext, INodeLayout)} event will be fired). If
	 * <code>true</code> is returned, no dynamic layout will be applied after
	 * notifying all listeners, i.e. a dynamic layout pass will only be applied
	 * when all registered {@link ILayoutListener}s return <code>false</code>.
	 * 
	 * @param context
	 *            the layout context that fired the event
	 * @param node
	 *            the node that was resized
	 * @return <code>true</code> if no dynamic layout should be applied
	 *         afterwards.
	 */
	public boolean nodeResized(ILayoutContext context, INodeLayout node);

}
