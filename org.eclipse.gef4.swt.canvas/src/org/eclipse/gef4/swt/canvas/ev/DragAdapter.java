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
package org.eclipse.gef4.swt.canvas.ev;

import org.eclipse.gef4.geometry.planar.Point;
import org.eclipse.gef4.swt.canvas.IEventListener;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Event;

public class DragAdapter implements IEventListener {

	private boolean dragging;
	private Point start;

	@Override
	public void handleEvent(Event event) {
		switch (event.type) {
		case SWT.MouseDown:
			dragging = true;
			start = new Point(event.x, event.y);
			break;
		case SWT.MouseUp:
			dragging = false;
			break;
		case SWT.MouseMove:
			onDrag(start, event);
			break;
		}
	}

	@Override
	public boolean handlesEvent(Event event) {
		switch (event.type) {
		case SWT.MouseDown:
		case SWT.MouseUp:
			return true;
		case SWT.MouseMove:
			return dragging;
		}
		return false;
	}

	public void onDrag(Point start, Event event) {

	}

}
