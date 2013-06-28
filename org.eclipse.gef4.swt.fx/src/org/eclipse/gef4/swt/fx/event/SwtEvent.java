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


/**
 * An SwtEvent is fired for all SWT events that do not have a GEF4 counterpart.
 * The original SWT event object is stored as the source of the event and
 * therefore accessible via {@link #getSource()}.
 * 
 * @author mwienand
 * 
 */
public class SwtEvent extends Event {

	private static final long serialVersionUID = 1L;

	public static final EventType<SwtEvent> ANY = new EventType<SwtEvent>(
			Event.ANY, "SwtEvent");

	public SwtEvent(Object swtEvent, IEventTarget target,
			EventType<? extends Event> type) {
		super(swtEvent, target, type);
	}

	@Override
	public String toString() {
		return "SwtEvent(" + getSource() + ")";
	}

}
