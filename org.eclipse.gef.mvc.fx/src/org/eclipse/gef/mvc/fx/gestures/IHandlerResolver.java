/*******************************************************************************
 * Copyright (c) 2016, 2017 itemis AG and others.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Matthias Wienand (itemis AG) - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.gef.mvc.fx.gestures;

import java.util.List;

import org.eclipse.gef.common.adapt.IAdaptable;
import org.eclipse.gef.mvc.fx.domain.IDomain;
import org.eclipse.gef.mvc.fx.handlers.IHandler;
import org.eclipse.gef.mvc.fx.viewer.IViewer;

import javafx.scene.Node;

/**
 * The {@link IHandlerResolver} provides a mechanism to determine and prioritize
 * all {@link IHandler handlers} that are to be notified about certain input
 * events (see {@link #resolve(IGesture, Node, IViewer, Class)} for details).
 *
 * @author mwienand
 *
 */
public interface IHandlerResolver extends IAdaptable.Bound<IDomain> {

	/**
	 * Determines and prioritizes all {@link IHandler handlers} of the specified
	 * type for the given {@link IViewer} and target {@link Node} that are to be
	 * notified about an input event that was directed at the {@link Node}.
	 *
	 * @param <T>
	 *            Type parameter specifying the type of handler that is
	 *            collected.
	 * @param contextGesture
	 *            The {@link IGesture} for which to determine target handlers.
	 * @param target
	 *            The target {@link Node} that received an input event.
	 * @param viewer
	 *            The {@link IViewer} that contains the given target
	 *            {@link Node}.
	 * @param handlerType
	 *            The type of the handlers to return.
	 * @return All matching policies within the hierarchy from the root part to
	 *         the target part.
	 */
	public <T extends IHandler> List<? extends T> resolve(
			IGesture contextGesture, Node target, IViewer viewer,
			Class<T> handlerType);
}
