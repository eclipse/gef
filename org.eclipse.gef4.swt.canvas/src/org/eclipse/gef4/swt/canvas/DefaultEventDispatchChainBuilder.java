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

import org.eclipse.gef4.swt.canvas.ev.BasicEventDispatchChain;
import org.eclipse.gef4.swt.canvas.ev.IEventDispatchChain;

public class DefaultEventDispatchChainBuilder {

	public static IEventDispatchChain buildEventDispatchChain(INode target) {
		BasicEventDispatchChain chain = new BasicEventDispatchChain();
		chain.append(target.getEventDispatcher());
		INode next = target.getParentNode();
		while (next != null) {
			chain.prepend(next.getEventDispatcher());
			next = next.getParentNode();
		}
		return chain;
	}

}
