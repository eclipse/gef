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

	public static final EventType<Event> ANY = EventType.ROOT;

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

	/**
	 * Marks this {@link Event} as consumed, i.e. already processed. Consumed
	 * events are not further dispatched.
	 */
	public void consume() {
		if (consumed) {
			throw new IllegalStateException("This Event is already consumed.");
		}
		consumed = true;
	}

	/**
	 * Returns the {@link EventType} associated with this Event.
	 * 
	 * @return the {@link EventType} associated with this Event
	 */
	public EventType<? extends Event> getEventType() {
		return type;
	}

	/**
	 * Returns the {@link IEventTarget} for which this Event was generated.
	 * 
	 * @return the {@link IEventTarget} for which this Event was generated
	 */
	public IEventTarget getTarget() {
		return target;
	}

	/**
	 * Checks if this Event is already consumed, i.e. {@link #consume()} has
	 * been called on this Event. Returns <code>true</code> if so, otherwise
	 * <code>false</code>.
	 * 
	 * @return <code>true</code> if {@link #consume()} has been called on this
	 *         Event, otherwise <code>false</code>
	 */
	public boolean isConsumed() {
		return consumed;
	}

	/**
	 * Sets the {@link EventType} of this Event to the passed-in EventType. This
	 * is used internally to transform events of one type to another.
	 * Specifically, the {@link MouseTrackDispatcher} makes use of this method
	 * to transform MOUSE_ENTERED_TARGET and MOUSE_EXITED_TARGET events to
	 * MOUSE_ENTERED and MOUSE_EXITED events.
	 * 
	 * @param newType
	 *            the new {@link EventType} for this Event
	 */
	public void setEventType(EventType<? extends Event> newType) {
		type = newType;
	}

}
