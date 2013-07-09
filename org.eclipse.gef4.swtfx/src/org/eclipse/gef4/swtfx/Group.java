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
package org.eclipse.gef4.swtfx;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.gef4.geometry.planar.AffineTransform;
import org.eclipse.gef4.swtfx.event.EventHandlerManager;
import org.eclipse.gef4.swtfx.event.SwtEventTargetSelector;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.widgets.Composite;

public class Group extends AbstractParent {

	private List<IFigure> figures = new LinkedList<IFigure>();

	private EventHandlerManager dispatcher = new EventHandlerManager();
	private SwtEventTargetSelector swtEventDispatcher;
	private boolean focusTraversable = true;
	private AffineTransform transform = new AffineTransform();

	public Group(Composite parent) {
		super(parent);
	}

	public IFigure getFirstFigure() {
		return figures.size() > 0 ? figures.get(0) : null;
	}

	public IFigure getLastFigure() {
		return figures.size() > 0 ? figures.get(figures.size() - 1) : null;
	}

	public IFigure getNextFocusFigure() {
		IFigure focus = swtEventDispatcher.getFocusTarget();
		if (focus == null) {
			return getFirstFigure();
		}

		boolean thisIsIt = false;
		for (IFigure f : figures) {
			if (thisIsIt) {
				return f;
			}
			if (f == focus) {
				thisIsIt = true;
			}
		}

		// no next figure available
		return null;
	}

	public IFigure getPreviousFocusFigure() {
		IFigure focus = swtEventDispatcher.getFocusTarget();

		if (focus == null) {
			return getLastFigure();
		}

		IFigure last = null;
		for (IFigure f : figures) {
			if (f == focus) {
				return last;
			}
			last = f;
		}

		// no previous figure available
		return null;
	}

	@Override
	public void widgetDisposed(DisposeEvent e) {
		if (swtEventDispatcher != null) {
			swtEventDispatcher.removeListeners();
		}
	}

}
