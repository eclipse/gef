/*******************************************************************************
 * Copyright (c) 2013 itemis AG and others.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Matthias Wienand (itemis AG) - initial API and implementation
 * 
 *******************************************************************************/
package org.eclipse.gef4.swt.fx.event;

import org.eclipse.gef4.swt.fx.INode;
import org.w3c.dom.events.EventTarget;

/**
 * The {@link IEventTarget} interface is implemented by every {@link INode}. An
 * {@link IEventTarget} can receive events. An {@link IEventTarget} is
 * responsible for building a so called {@link IEventDispatchChain} via the
 * {@link #buildEventDispatchChain(IEventDispatchChain)} method which is used to
 * dispatch events for the target.
 * 
 * @author mwienand
 * 
 */
public interface IEventTarget {

	/**
	 * <p>
	 * Builds an {@link IEventDispatchChain} for this {@link EventTarget},
	 * modifying the passed-in {@link IEventDispatchChain chain}. Per default,
	 * dispatchers are prepended to the current chain. Note that you have to
	 * pass the chain on to the parent {@link INode} using its
	 * {@link #buildEventDispatchChain(IEventDispatchChain)} method.
	 * </p>
	 * <p>
	 * For an example implementation, take a look at the
	 * {@link DefaultEventDispatchChainBuilder}.
	 * </p>
	 * 
	 * @param chain
	 *            current {@link IEventDispatchChain}
	 * @return modified {@link IEventDispatchChain}
	 */
	IEventDispatchChain buildEventDispatchChain(IEventDispatchChain chain);

}
