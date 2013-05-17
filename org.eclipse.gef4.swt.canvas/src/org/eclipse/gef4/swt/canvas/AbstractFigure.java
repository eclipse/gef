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

import java.util.LinkedList;
import java.util.List;

import org.eclipse.gef4.swt.canvas.gc.GraphicsContext;
import org.eclipse.gef4.swt.canvas.gc.GraphicsContextState;

public abstract class AbstractFigure implements IFigure {

	private GraphicsContextState paintState = new GraphicsContextState();
	private List<IEventListener> eventListeners = new LinkedList<IEventListener>();
	private Group container;

	@Override
	public boolean addEventListener(IEventListener eventListener) {
		return eventListeners.add(eventListener);
	}

	protected abstract void doPaint(GraphicsContext g);

	@Override
	public Group getContainer() {
		return container;
	}

	@Override
	public GraphicsContextState getPaintStateByReference() {
		return paintState;
	}

	@Override
	public void handleEvent(Object event) {
		/*
		 * TODO: Move this handleEvent method to some utility class. (It is used
		 * in the Gruop, too.)
		 */
		for (IEventListener listener : eventListeners) {
			if (listener.handlesEvent(event)) {
				listener.handleEvent(event);
			}
		}
	}

	@Override
	final public void paint(GraphicsContext g) {
		g.pushState(paintState);
		g.setUpGuard();
		doPaint(g);
		g.takeDownGuard();
		g.restore();
	}

	@Override
	public boolean removeEventListener(IEventListener eventListener) {
		return eventListeners.remove(eventListener);
	}

	@Override
	public void setContainer(Group group) {
		container = group;
	}

	@Override
	public void update() {
		if (container != null) {
			container.redraw();
		}
	}

}
