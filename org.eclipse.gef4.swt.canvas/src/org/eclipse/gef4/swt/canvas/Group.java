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
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;

public class Group extends org.eclipse.swt.widgets.Canvas implements
		PaintListener, INode, DisposeListener {

	private List<IFigure> figures = new LinkedList<IFigure>();
	private EventDispatcher eventDispatcher;
	private List<IEventListener> eventListeners = new LinkedList<IEventListener>();
	private PaintListener backgroundPaintListener;

	public Group(Composite parent) {
		super(parent, SWT.NONE);
		addPaintListener(this);
		setEventDispatcher(new EventDispatcher());
		addDisposeListener(this);
	}

	public void addBackgroundPaintListener(PaintListener l) {
		backgroundPaintListener = l;
	}

	@Override
	public boolean addEventListener(IEventListener eventListener) {
		return eventListeners.add(eventListener);
	}

	public EventDispatcher getEventDispatcher() {
		return eventDispatcher;
	}

	public List<IFigure> getFigures() {
		return figures;
	}

	public void handleEvent(Event event) {
		for (IEventListener listener : eventListeners) {
			if (listener.handlesEvent(event)) {
				listener.handleEvent(event);
			}
		}
	}

	@Override
	public void paintControl(PaintEvent e) {
		if (backgroundPaintListener != null) {
			backgroundPaintListener.paintControl(e);
		}

		GraphicsContext g = new GraphicsContext(e.gc);

		// our rendering order is the reverse of SWT's
		for (IFigure figure : getFigures()) {
			/*
			 * Note that the guarding of figures is only necessary if the
			 * figures do not guard their paint method.
			 */
			g.save();
			g.setUpGuard();
			figure.paint(g);
			g.takeDownGuard();
			g.restore();
		}
	}

	@Override
	public boolean removeEventListener(IEventListener eventListener) {
		return eventListeners.remove(eventListener);
	}

	public void setEventDispatcher(EventDispatcher eventDispatcher) {
		if (this.eventDispatcher != null) {
			this.eventDispatcher.removeListeners();
		}
		this.eventDispatcher = eventDispatcher;
		eventDispatcher.setGroup(this);
		eventDispatcher.addListeners();
	}

	@Override
	public void widgetDisposed(DisposeEvent e) {
		if (eventDispatcher != null) {
			eventDispatcher.removeListeners();
		}
	}

}
