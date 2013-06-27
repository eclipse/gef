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
import org.eclipse.gef4.swt.canvas.ev.types.TraverseEvent;

public class FocusTraversalDispatcher extends AbstractEventDispatcher {

	private INode target;

	public FocusTraversalDispatcher(INode target) {
		this.target = target;
	}

	@Override
	public Event dispatchBubblingEvent(Event event) {
		return event;
	}

	@Override
	public Event dispatchCapturingEvent(Event event) {
		if (event.getEventType() == TraverseEvent.ANY) {
			if (target instanceof IFigure) {
				IFigure f = (IFigure) target;
				Group container = f.getContainer();
				IFigure nextFocusFigure = container.getNextFocusFigure();
				if (nextFocusFigure != null) {
					container.setFocusFigure(nextFocusFigure);
				} else {
					System.out.println("What to do now?!");
				}
			} else {
				Group g = (Group) target;
				System.out.println("What to do now?!");
			}
		}
		return event;
	}

}
