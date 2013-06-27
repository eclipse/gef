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
package org.eclipse.gef4.swt.canvas;

import org.eclipse.gef4.swt.canvas.ev.AbstractEventDispatcher;
import org.eclipse.gef4.swt.canvas.ev.Event;
import org.eclipse.gef4.swt.canvas.ev.IEventDispatchChain;
import org.eclipse.gef4.swt.canvas.ev.IEventDispatcher;
import org.eclipse.gef4.swt.canvas.ev.types.MouseEvent;

/**
 * <p>
 * The {@link MouseTrackDispatcher} provides an {@link IEventDispatcher}
 * implementation to pre-process {@link MouseEvent mouse events}. If the mouse
 * cursor enters an {@link INode}, a {@link MouseEvent#MOUSE_ENTERED_TARGET}
 * event is fired. The entered {@link INode} will receive a
 * {@link MouseEvent#MOUSE_ENTERED} event instead of the
 * {@link MouseEvent#MOUSE_ENTERED_TARGET} event. Analogous,
 * {@link MouseEvent#MOUSE_EXITED_TARGET} events are translated to
 * {@link MouseEvent#MOUSE_EXITED} events for the exited {@link INode}.
 * </p>
 * 
 * @author mwienand
 * 
 */
public class MouseTrackDispatcher extends AbstractEventDispatcher {

	private final INode target;

	/**
	 * Constructs a new {@link MouseTrackDispatcher} for the passed-in
	 * {@link INode}. Note that the {@link MouseTrackDispatcher} has to be
	 * prepended to the other {@link IEventDispatcher}s in the
	 * {@link IEventDispatchChain}.
	 * 
	 * @param target
	 */
	public MouseTrackDispatcher(INode target) {
		this.target = target;
	}

	@Override
	public Event dispatchBubblingEvent(Event event) {
		if (event.getTarget() == target) {
			if (event.getEventType() == MouseEvent.MOUSE_ENTERED) {
				event.setEventType(MouseEvent.MOUSE_ENTERED_TARGET);
			} else if (event.getEventType() == MouseEvent.MOUSE_EXITED) {
				event.setEventType(MouseEvent.MOUSE_EXITED_TARGET);
			}
		}
		return event;
	}

	@Override
	public Event dispatchCapturingEvent(Event event) {
		if (event.getTarget() == target) {
			if (event.getEventType() == MouseEvent.MOUSE_ENTERED_TARGET) {
				event.setEventType(MouseEvent.MOUSE_ENTERED);
			} else if (event.getEventType() == MouseEvent.MOUSE_EXITED_TARGET) {
				event.setEventType(MouseEvent.MOUSE_EXITED);
			}
		}
		return event;
	}

}