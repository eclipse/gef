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
package org.eclipse.gef4.swt.canvas.ev;

import java.util.EventObject;

/**
 * This is the base class of the event hierarchy. An {@link Event} object
 * consists of an event source, the {@link IEventTarget event target}, and its
 * {@link EventType event type}. An Event can be consumed to stop further
 * processing of it.
 * 
 * @author mwienand
 * 
 */
public class Event extends EventObject {

	private static final long serialVersionUID = 1L;

	public static void fireEvent(IEventTarget target, Event event) {
		IEventDispatchChain chain = target
				.buildEventDispatchChain(new BasicEventDispatchChain());
		chain.dispatchEvent(event);
	}

	private EventType<? extends Event> type;
	private IEventTarget target;
	private boolean consumed;

	private static Object NULL_SOURCE = "";

	public Event(EventType<? extends Event> type) {
		this(NULL_SOURCE, null, type);
	}

	public Event(Object source, IEventTarget target,
			EventType<? extends Event> type) {
		super(source);
		this.target = target;
		this.type = type;
	}

	@Override
	protected Event clone() throws CloneNotSupportedException {
		Event copy = new Event(getSource(), getTarget(), getEventType());
		copy.consumed = consumed;
		return copy;
	}

	public void consume() {
		if (consumed) {
			throw new IllegalStateException("This Event is already consumed.");
		}
		consumed = true;
	}

	public EventType<? extends Event> getEventType() {
		return type;
	}

	public IEventTarget getTarget() {
		return target;
	}

	public boolean isConsumed() {
		return consumed;
	}

	public void setEventType(EventType<? extends Event> newType) {
		type = newType;
	}

}
