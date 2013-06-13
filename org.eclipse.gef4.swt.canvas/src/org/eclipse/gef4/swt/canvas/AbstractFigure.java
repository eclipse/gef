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
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.MouseWheelListener;
import org.eclipse.swt.widgets.Event;

public abstract class AbstractFigure implements IFigure {

	private GraphicsContextState paintState = new GraphicsContextState();
	private List<IEventListener> eventListeners = new LinkedList<IEventListener>();
	private Group container;

	@Override
	public boolean addEventListener(IEventListener eventListener) {
		return eventListeners.add(eventListener);
	}

	@Override
	public void addKeyListener(KeyListener listener) {
		addEventListener(new WrappedEventListener(listener));
	}

	@Override
	public void addMouseListener(MouseListener listener) {
		addEventListener(new WrappedEventListener(listener));
	}

	@Override
	public void addMouseMoveListener(MouseMoveListener listener) {
		addEventListener(new WrappedEventListener(listener));
	}

	@Override
	public void addMouseWheelListener(MouseWheelListener listener) {
		addEventListener(new WrappedEventListener(listener));
	}

	protected abstract void doPaint(GraphicsContext g);

	@Override
	public boolean forceRequestFocus() {
		return container.forceFocusFigure(this);
	}

	@Override
	public Group getContainer() {
		return container;
	}

	@Override
	public GraphicsContextState getPaintStateByReference() {
		return paintState;
	}

	@Override
	public void handleEvent(Event event) {
		// System.out.println(this + " checks event listeners...");
		for (IEventListener listener : eventListeners) {
			// System.out.println("...listener: " + listener);
			if (listener.handlesEvent(event)) {
				// System.out.println("......handles the event!");
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
	public void removeKeyListener(KeyListener listener) {
		removeWrappedListener(listener);
	}

	@Override
	public void removeMouseListener(MouseListener listener) {
		removeWrappedListener(listener);
	}

	@Override
	public void removeMouseMoveListener(MouseMoveListener listener) {
		removeWrappedListener(listener);
	}

	@Override
	public void removeMouseWheelListener(MouseWheelListener listener) {
		removeWrappedListener(listener);
	}

	private void removeWrappedListener(Object listener) {
		for (IEventListener l : eventListeners) {
			if (l instanceof WrappedEventListener) {
				if (((WrappedEventListener) l).getListenerReference() == listener) {
					removeEventListener(l);
					return;
				}
			}
		}
	}

	@Override
	public boolean requestFocus() {
		return container.setFocusFigure(this);
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
