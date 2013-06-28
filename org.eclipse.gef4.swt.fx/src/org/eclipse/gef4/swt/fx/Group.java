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

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

import org.eclipse.gef4.geometry.planar.Point;
import org.eclipse.gef4.swt.fx.event.DefaultEventDispatchChainBuilder;
import org.eclipse.gef4.swt.fx.event.EventHandlerManager;
import org.eclipse.gef4.swt.fx.event.EventType;
import org.eclipse.gef4.swt.fx.event.IEventDispatchChain;
import org.eclipse.gef4.swt.fx.event.IEventDispatcher;
import org.eclipse.gef4.swt.fx.event.IEventHandler;
import org.eclipse.gef4.swt.fx.event.SwtEventTargetSelector;
import org.eclipse.gef4.swt.fx.gc.GraphicsContext;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.widgets.Composite;

public class Group extends org.eclipse.swt.widgets.Canvas implements
		PaintListener, INode, DisposeListener {

	private List<IFigure> figures = new LinkedList<IFigure>();
	private EventHandlerManager dispatcher = new EventHandlerManager();
	private SwtEventTargetSelector swtEventDispatcher;
	private boolean focusTraversable = true;

	public Group(Composite parent) {
		super(parent, SWT.NONE);
		addPaintListener(this);
		addDisposeListener(this);
		swtEventDispatcher = new SwtEventTargetSelector(this);
	}

	@Override
	public <T extends org.eclipse.gef4.swt.fx.event.Event> void addEventFilter(
			EventType<T> type, IEventHandler<T> filter) {
		dispatcher.addEventFilter(type, filter);
	}

	@Override
	public <T extends org.eclipse.gef4.swt.fx.event.Event> void addEventHandler(
			EventType<T> type, IEventHandler<T> handler) {
		dispatcher.addEventHandler(type, handler);
	}

	public void addFigures(IFigure... figures) {
		this.figures.addAll(Arrays.asList(figures));
		for (IFigure f : figures) {
			f.setContainer(this);
		}
	}

	@Override
	public IEventDispatchChain buildEventDispatchChain(IEventDispatchChain tail) {
		return DefaultEventDispatchChainBuilder.buildEventDispatchChain(this,
				tail);
	}

	@Override
	public IEventDispatcher getEventDispatcher() {
		return dispatcher;
	}

	public IFigure getFigureAt(Point position) {
		ListIterator<IFigure> it = figures.listIterator(figures.size());
		while (it.hasPrevious()) {
			IFigure f = it.previous();
			if (f.getBounds().getTransformedShape().contains(position)) {
				return f;
			}
		}
		return null;
	}

	public List<IFigure> getFigures() {
		return figures;
	}

	public IFigure getFocusFigure() {
		return swtEventDispatcher.getFocusTarget();
	}

	public IFigure getNextFocusFigure() {
		IFigure focus = swtEventDispatcher.getFocusTarget();
		if (focus == null) {
			// first figure
			return figures.get(0);
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

	@Override
	public Group getParentNode() {
		Composite parent = getParent();
		if (parent instanceof Group) {
			return (Group) parent;
		}
		return null;
	}

	public IFigure getPreviousFocusFigure() {
		IFigure focus = swtEventDispatcher.getFocusTarget();
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

	@SuppressWarnings("all")
	@Override
	public boolean hasFocus() {
		return isFocusControl() && swtEventDispatcher.getFocusTarget() == null;
	}

	@Override
	public boolean isFocusTraversable() {
		return focusTraversable;
	}

	@Override
	public void paintControl(PaintEvent e) {
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
			try {
				g.takeDownGuard();
			} catch (IllegalStateException x) {
				throw new IllegalStateException(
						"Did you forget to call restore() in your drawing code?",
						x);
			}
			g.restore();
		}
	}

	@Override
	public <T extends org.eclipse.gef4.swt.fx.event.Event> void removeEventFilter(
			EventType<T> type, IEventHandler<T> filter) {
		dispatcher.removeEventFilter(type, filter);
	}

	@Override
	public <T extends org.eclipse.gef4.swt.fx.event.Event> void removeEventHandler(
			EventType<T> type, IEventHandler<T> handler) {
		dispatcher.removeEventHandler(type, handler);
	}

	@Override
	public boolean requestFocus() {
		return forceFocus();
	}

	public boolean setFocusFigure(IFigure focusFigure) {
		if (focusFigure == null) {
			swtEventDispatcher.setFocusTarget(null);
			return true;
		}

		if (focusFigure.getParentNode() != this) {
			throw new IllegalArgumentException(
					"The given IFigure is no child of this Group!");
		}
		if (forceFocus()) {
			swtEventDispatcher.setFocusTarget(focusFigure);
			return true;
		}
		return false;
	}

	@Override
	public void setFocusTraversable(boolean focusTraversable) {
		this.focusTraversable = focusTraversable;
	}

	@Override
	public void widgetDisposed(DisposeEvent e) {
		if (swtEventDispatcher != null) {
			swtEventDispatcher.removeListeners();
		}
	}

}
