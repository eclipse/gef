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
package org.eclipse.gef4.swt.fx.test;

import org.eclipse.gef4.swtfx.event.BasicEventDispatchChain;
import org.eclipse.gef4.swtfx.event.EventHandlerManager;
import org.eclipse.gef4.swtfx.event.IEventDispatchChain;
import org.eclipse.gef4.swtfx.event.IEventTarget;

public class SimpleTarget implements IEventTarget {

	public EventHandlerManager dispatcher = new EventHandlerManager();
	public SimpleTarget parent;

	public SimpleTarget(SimpleTarget parent) {
		this.parent = parent;
	}

	@Override
	public IEventDispatchChain buildEventDispatchChain(IEventDispatchChain edc) {
		BasicEventDispatchChain chain = new BasicEventDispatchChain();
		chain.append(dispatcher);
		SimpleTarget next = parent;
		while (next != null) {
			chain.prepend(next.dispatcher);
			next = next.parent;
		}
		return chain;
	}

}
