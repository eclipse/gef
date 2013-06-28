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

import org.eclipse.gef4.swt.fx.event.Event;
import org.eclipse.gef4.swt.fx.event.EventType;
import org.eclipse.gef4.swt.fx.event.IEventDispatcher;
import org.eclipse.gef4.swt.fx.event.IEventHandler;
import org.eclipse.gef4.swt.fx.event.IEventTarget;
import org.eclipse.gef4.swt.fx.event.TraverseEvent;

/**
 * The {@link INode} interface is a key abstraction of the SWT FX component.
 * There is an SWT Widget implementation called {@link Group} and lightweight
 * implementations called {@link IFigure figures}.
 * 
 * @author mwienand
 * 
 */
public interface INode extends IEventTarget {

	/**
	 * Adds an event filter for the specified {@link EventType} to the list of
	 * event filters managed by this {@link INode}. An event filter is called
	 * during the "Capturing" phase of event processing.
	 * 
	 * @param type
	 *            the {@link EventType} for which the filter will be called
	 * @param filter
	 *            the {@link IEventHandler} which is called on events of the
	 *            specified type
	 */
	public <T extends Event> void addEventFilter(EventType<T> type,
			IEventHandler<T> filter);

	/**
	 * Adds an event handler for the specified {@link EventType} to the list of
	 * event handlers managed by this {@link INode}. An event handler is called
	 * during the "Bubbling" phase of event processing.
	 * 
	 * @param type
	 *            the {@link EventType} for which the handler will be called
	 * @param filter
	 *            the {@link IEventHandler} which is called on events of the
	 *            specified type
	 */
	public <T extends Event> void addEventHandler(EventType<T> type,
			IEventHandler<T> handler);

	/**
	 * Returns the {@link IEventDispatcher} used to dispatch events for this
	 * {@link INode}.
	 * 
	 * @return the {@link IEventDispatcher} used to dispatch events for this
	 *         {@link INode}
	 */
	public IEventDispatcher getEventDispatcher();

	/**
	 * Returns the parent {@link INode} or <code>null</code> if this is the root
	 * of the hierarchy.
	 * 
	 * @return the parent {@link INode} or <code>null</code> if this is the root
	 *         of the hierarchy
	 */
	public INode getParentNode();

	/**
	 * @return <code>true</code> if this {@link INode} currently has keyboard
	 *         focus, otherwise <code>false</code>
	 */
	public boolean hasFocus();

	/**
	 * Determines if the focus of this {@link INode} is traversable, i.e. it
	 * reacts to {@link TraverseEvent}s.
	 * 
	 * @return <code>true</code> if the focus of this {@link INode} is
	 *         traversable, otherwise <code>false</code>
	 */
	public boolean isFocusTraversable();

	/**
	 * Removes the given {@link IEventHandler event filter} from the list of
	 * listeners managed by this {@link INode} for the specified
	 * {@link EventType}.
	 * 
	 * @param type
	 * @param filter
	 */
	public <T extends Event> void removeEventFilter(EventType<T> type,
			IEventHandler<T> filter);

	/**
	 * Removes the given {@link IEventHandler event handler} from the list of
	 * listeners managed by this {@link INode} for the specified
	 * {@link EventType}.
	 * 
	 * @param type
	 * @param filter
	 */
	public <T extends Event> void removeEventHandler(EventType<T> type,
			IEventHandler<T> handler);

	/**
	 * Tries to bind keyboard events to this {@link INode}. Uses the SWT
	 * forceFocus() method.
	 * 
	 * @return <code>true</code> on success, otherwise <code>false</code>
	 */
	public boolean requestFocus();

	/**
	 * Sets the focusTraversable property of this {@link INode}. If the focus of
	 * an {@link INode} is traversable, it will react to {@link TraverseEvent}s
	 * by switching through the hierarchy.
	 * 
	 * @param focusTraversable
	 */
	public void setFocusTraversable(boolean focusTraversable);

}
