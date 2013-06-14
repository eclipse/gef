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
package org.eclipse.gef4.swt.canvas.test;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.gef4.swt.canvas.ev.Event;
import org.eclipse.gef4.swt.canvas.ev.IEventDispatchChain;
import org.eclipse.gef4.swt.canvas.ev.IEventDispatcher;

public class BasicEventDispatchChain implements IEventDispatchChain {

	private List<IEventDispatcher> dispatchChain;
	private Iterator<IEventDispatcher> iterator;

	public BasicEventDispatchChain() {
		reset();
	}

	@Override
	public IEventDispatchChain append(IEventDispatcher dispatcher) {
		dispatchChain.add(dispatcher);
		return this;
	}

	@Override
	public Event dispatchEvent(Event event) {
		if (dispatchChain.size() > 0) {
			if (iterator == null) {
				iterator = dispatchChain.iterator();
			}
			if (iterator.hasNext()) {
				event = iterator.next().dispatchEvent(event, this);
			}
		}
		return event;
	}

	@Override
	public IEventDispatchChain prepend(IEventDispatcher dispatcher) {
		dispatchChain.add(0, dispatcher);
		return this;
	}

	public void reset() {
		iterator = null;
		dispatchChain = new LinkedList<IEventDispatcher>();
	}

}
