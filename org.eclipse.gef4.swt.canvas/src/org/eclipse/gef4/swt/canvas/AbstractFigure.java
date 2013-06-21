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

import org.eclipse.gef4.swt.canvas.ev.Event;
import org.eclipse.gef4.swt.canvas.ev.EventHandlerManager;
import org.eclipse.gef4.swt.canvas.ev.EventType;
import org.eclipse.gef4.swt.canvas.ev.IEventDispatchChain;
import org.eclipse.gef4.swt.canvas.ev.IEventDispatcher;
import org.eclipse.gef4.swt.canvas.ev.IEventHandler;
import org.eclipse.gef4.swt.canvas.gc.GraphicsContextState;

public abstract class AbstractFigure implements IFigure {

	private GraphicsContextState paintState = new GraphicsContextState();
	private EventHandlerManager dispatcher = new EventHandlerManager();
	private Group container;

	@Override
	public <T extends Event> void addEventFilter(EventType<T> type,
			IEventHandler<T> filter) {
		dispatcher.addEventFilter(type, filter);
	}

	@Override
	public <T extends Event> void addEventHandler(EventType<T> type,
			IEventHandler<T> handler) {
		dispatcher.addEventHandler(type, handler);
	}

	@Override
	public IEventDispatchChain buildEventDispatchChain(IEventDispatchChain tail) {
		return DefaultEventDispatchChainBuilder.buildEventDispatchChain(this);
	}

	@Override
	public IEventDispatcher getEventDispatcher() {
		return dispatcher;
	}

	@Override
	public GraphicsContextState getPaintStateByReference() {
		return paintState;
	}

	@Override
	public INode getParentNode() {
		return container;
	}

	@Override
	public <T extends Event> void removeEventFilter(EventType<T> type,
			IEventHandler<T> filter) {
		dispatcher.removeEventFilter(type, filter);
	}

	@Override
	public <T extends Event> void removeEventHandler(EventType<T> type,
			IEventHandler<T> handler) {
		dispatcher.removeEventHandler(type, handler);
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
