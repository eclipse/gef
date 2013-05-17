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

import java.util.List;
import java.util.ListIterator;

import org.eclipse.gef4.geometry.planar.Point;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

public class EventDispatcher implements Listener {

	/*
	 * TODO: use an event table based on the event type so that we do not walk
	 * over all the listeners for all the events but rather walk over the
	 * listeners for the current event type only.
	 */

	// FIXME: do not listen to SWT.DragDetect because we want to get notified on
	// MouseDown events immediately
	final private static int[] EVENT_TYPES = new int[] { SWT.None, SWT.KeyDown,
			SWT.KeyUp, SWT.MouseUp, SWT.MouseDown, SWT.MouseMove,
			SWT.MouseEnter, SWT.MouseExit, SWT.MouseDoubleClick, SWT.Paint,
			SWT.Move, SWT.Resize, SWT.Dispose, SWT.Selection,
			SWT.DefaultSelection, SWT.FocusIn, SWT.FocusOut, SWT.Expand,
			SWT.Collapse, SWT.Iconify, SWT.Deiconify, SWT.Close, SWT.Show,
			SWT.Hide, SWT.Modify, SWT.Verify, SWT.Activate, SWT.Deactivate,
			SWT.Help, SWT.Arm, SWT.Traverse, SWT.MouseHover, SWT.HardKeyDown,
			SWT.HardKeyUp, SWT.MenuDetect, SWT.SetData, SWT.MouseVerticalWheel,
			SWT.MouseHorizontalWheel, SWT.Settings, SWT.EraseItem,
			SWT.MeasureItem, SWT.PaintItem, SWT.ImeComposition,
			SWT.OrientationChange, SWT.Skin, SWT.OpenDocument };

	private Group group;

	public EventDispatcher() {
	}

	public void addListeners() {
		for (int type : EVENT_TYPES) {
			group.addListener(type, this);
		}
	}

	@Override
	public void handleEvent(Event event) {
		List<IFigure> figures = group.getFigures();
		if (figures.size() < 1) {
			return;
		}

		Point mouseLocation = new Point(event.x, event.y);

		ListIterator<IFigure> i = figures.listIterator(figures.size());
		while (i.hasPrevious()) {
			IFigure f = i.previous();
			if (f.getBounds().getTransformedShape().contains(mouseLocation)) {
				// System.out.println("event " + event.type
				// + " consumed by figure "
				// + f.getBounds().getTransformedShape());
				f.handleEvent(event);
				return;
			}
		}

		// no one contains the event, so the group can handle it:
		group.handleEvent(event);
	}

	public void removeListeners() {
		for (int type : EVENT_TYPES) {
			group.removeListener(type, this);
		}
	}

	public void setGroup(Object group) {
		if (group instanceof Group) {
			this.group = (Group) group;
		} else {
			throw new IllegalArgumentException(
					"We need a defined gef4.swt.canvas.Group here!");
		}
	}

}
