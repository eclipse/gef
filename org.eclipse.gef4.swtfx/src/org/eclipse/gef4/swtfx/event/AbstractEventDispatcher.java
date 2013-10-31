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
package org.eclipse.gef4.swtfx.event;

public abstract class AbstractEventDispatcher implements IEventDispatcher {

	/**
	 * This method is called during the "Bubbling" phase of event processing.
	 * Normally, all registered event handlers are called during this phase. You
	 * can call <code>event.consume()</code> on the passed-in {@link Event} or
	 * return <code>null</code> to stop further event processing.
	 * 
	 * @param event
	 * @return the passed-in {@link Event} or <code>null</code>.
	 */
	public abstract Event dispatchBubblingEvent(Event event);

	/**
	 * This method is called during the "Capturing" phase of event processing.
	 * Normally, all registered event filters are called during this phase. You
	 * can call <code>event.consume()<code> on the passed-in {@link Event} or
	 * return <code>null</code> to stop further event processing.
	 * 
	 * @param event
	 * @return the passed-in {@link Event} or <code>null</code>
	 */
	public abstract Event dispatchCapturingEvent(Event event);

	@Override
	public Event dispatchEvent(Event event, IEventDispatchChain tail) {
		event = dispatchCapturingEvent(event);

		if (event != null && !event.isConsumed()) {
			// forward the event to the rest of the chain
			event = tail.dispatchEvent(event);

			if (event != null && !event.isConsumed()) {
				event = dispatchBubblingEvent(event);
			}
		}

		return event == null || event.isConsumed() ? null : event;
	}

}
