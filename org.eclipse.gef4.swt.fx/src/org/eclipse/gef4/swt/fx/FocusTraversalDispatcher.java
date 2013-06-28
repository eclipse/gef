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
package org.eclipse.gef4.swt.fx;

import org.eclipse.gef4.swt.fx.event.AbstractEventDispatcher;
import org.eclipse.gef4.swt.fx.event.Event;
import org.eclipse.gef4.swt.fx.event.TraverseEvent;
import org.eclipse.swt.SWT;

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
			TraverseEvent traverseEvent = (TraverseEvent) event;
			if (target instanceof IFigure) {
				IFigure f = (IFigure) target;
				Group container = f.getContainer();
				IFigure nextFocusFigure = trav(traverseEvent, container);
				if (nextFocusFigure != null) {
					container.setFocusFigure(nextFocusFigure);
				}
			}
		}
		return event;
	}

	/**
	 * @param container
	 * @return
	 */
	private IFigure trav(TraverseEvent e, Group container) {
		int detail = e.getDetail();
		if (detail == SWT.TRAVERSE_ARROW_NEXT
				|| detail == SWT.TRAVERSE_PAGE_NEXT
				|| detail == SWT.TRAVERSE_TAB_NEXT) {
			return container.getNextFocusFigure();
		} else if (detail == SWT.TRAVERSE_ARROW_PREVIOUS
				|| detail == SWT.TRAVERSE_PAGE_PREVIOUS
				|| detail == SWT.TRAVERSE_TAB_PREVIOUS) {
			return container.getPreviousFocusFigure();
		} else {
			// TODO: What to do here?
			return null;
		}
	}

}
