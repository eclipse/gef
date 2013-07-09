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
 * The InputEvent class is the base class for all input events, i.e. mouse and
 * keyboard events.
 * 
 * @author mwienand
 * 
 */
public class InputEvent extends Event {

	public static final EventType<InputEvent> ANY = new EventType<InputEvent>(
			Event.ANY, "InputEvent");

	private static final long serialVersionUID = 1L;

	public InputEvent(Object source, IEventTarget target,
			EventType<? extends InputEvent> type) {
		super(source, target, type);
	}

}
