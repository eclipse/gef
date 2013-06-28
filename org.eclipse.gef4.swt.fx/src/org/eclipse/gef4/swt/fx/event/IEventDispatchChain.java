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

public interface IEventDispatchChain {

	/**
	 * Adds the given {@link IEventDispatcher} to (the end of) this
	 * {@link IEventDispatchChain}. After this call, the passed-in dispatcher
	 * will be the last element in the chain.
	 * 
	 * @param dispatcher
	 * @return <code>this</code> for convenience
	 */
	IEventDispatchChain append(IEventDispatcher dispatcher);

	/**
	 * Dispatches the passed-in {@link Event} by passing the {@link Event} to
	 * the first {@link IEventDispatcher} in the chain.
	 * 
	 * @param event
	 * @return
	 */
	Event dispatchEvent(Event event);

	/**
	 * Adds the given {@link IEventDispatcher} to (the start of) this
	 * {@link IEventDispatchChain}. After this call, the passed-in dispatcher
	 * will be the first element in the chain.
	 * 
	 * @param dispatcher
	 * @return <code>this</code> for convenience
	 */
	IEventDispatchChain prepend(IEventDispatcher dispatcher);

}
