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

/**
 * <p>
 * An {@link IEventDispatcher} can be inserted into an
 * {@link IEventDispatchChain}. This interface is the GEF4 counterpart of the
 * JavaFX EventDispatchChain interface.
 * </p>
 * 
 * <p>
 * An IEventDispatcher is capable of processing an {@link Event} during both
 * phases of event processing, bubbling and capturing. This is done using a
 * recursive mechanism:
 * 
 * <blockquote>
 * 
 * <pre>
 * &#064;Override
 * public Event dispatchEvent(Event event, IEventDispatchChain tail) {
 * 	event = dispatchCapturingEvent(event);
 * 
 * 	if (event != null &amp;&amp; !event.isConsumed()) {
 * 		// forward the event to the rest of the chain
 * 		event = tail.dispatchEvent(event);
 * 
 * 		if (event != null &amp;&amp; !event.isConsumed()) {
 * 			event = dispatchBubblingEvent(event);
 * 		}
 * 	}
 * 
 * 	return event == null || event.isConsumed() ? null : event;
 * }
 * </pre>
 * 
 * </blockquote>
 * </p>
 * 
 * <p>
 * An abstract implementation of this interface is provided by the
 * {@link AbstractEventDispatcher}. If you want to create your own
 * IEventDispatcher it is best to subclass that one.
 * </p>
 * 
 * @see {@link EventHandlerManager} <br />
 *      Every INode uses an EventHandlerManager to register and dispatch event
 *      filters and event handlers specified by the user.
 * 
 * @see {@link MouseTrackDispatcher} <br />
 *      Every INode uses a MouseTrackDispatcher for the transformation of
 *      MOUSE_ENTERED_TARGET and MOUSE_EXITED_TARGET events to MOUSE_ENTERED and
 *      MOUSE_EXITED events.
 * 
 * @see {@link FocusTraversalDispatcher} <br />
 *      Every INode uses a FocusTraversalDispatcher to process traversal-key
 *      presses.
 * 
 * @author mwienand
 * 
 */
public interface IEventDispatcher {

	/**
	 * <p>
	 * Dispatches the passed-in {@link Event} and eventually passes it along to
	 * the passed-in {@link IEventDispatchChain}.
	 * </p>
	 * 
	 * <p>
	 * The method returns the passed-in {@link Event} object or
	 * <code>null</code> to cancel further event processing. Event processing is
	 * also stopped, when the event is {@link Event#consume() consumed}.
	 * </p>
	 * 
	 * @param event
	 *            the {@link Event} to dispatch
	 * @param tail
	 *            the {@link IEventDispatchChain} to further dispatch the event
	 * @return the passed-in event, or <code>null</code> to cancel event
	 *         processing
	 */
	Event dispatchEvent(Event event, IEventDispatchChain tail);

}
