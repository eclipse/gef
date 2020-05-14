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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.gef.common.adapt.IAdaptable;
import org.eclipse.gef.mvc.fx.domain.IDomain;
import org.eclipse.gef.mvc.fx.handlers.IHandler;
import org.eclipse.gef.mvc.fx.parts.IRootPart;
import org.eclipse.gef.mvc.fx.parts.IVisualPart;
import org.eclipse.gef.mvc.fx.parts.PartUtils;
import org.eclipse.gef.mvc.fx.viewer.IViewer;

import com.google.common.reflect.TypeToken;

import javafx.scene.Node;

/**
 * The {@link DefaultHandlerResolver} is the default implementation of
 * {@link IHandlerResolver}.
 *
 * It works in two stages. First, the active handlers of other gestures are
 * examined to find "multi-gesture" handlers that implement or extend the given
 * target type. If any "multi-gesture" handlers are found, the target resolution
 * finishes and these active handlers are returned as the target handlers.
 * Otherwise, the visual part hierarchy is searched for respective handlers.
 * Beginning with the visual part that controls the given {@link Node}, the
 * hierarchy is walked up. The resolution finishes as soon as handlers of the
 * desired type can be obtained from a respective visual part. If no handlers
 * have been located before, the {@link IRootPart} is queried last. The
 * retrieved handlers (if more than one) are (lexicographically) sorted by their
 * role, so that the target handler selection is deterministic.
 *
 * @author mwienand
 *
 */
public class DefaultHandlerResolver extends IAdaptable.Bound.Impl<IDomain>
		implements IHandlerResolver {

	@Override
	@SuppressWarnings({ "serial", "unchecked" })
	public <T extends IHandler> List<? extends T> resolve(IGesture gesture,
			Node target, IViewer viewer, Class<T> handlerType) {
		// System.out.println("\n=== determine target handlers ===");
		// System.out.println("viewer = " + viewer);
		// System.out.println("raw target node = " + target);
		// System.out.println("handler type = " + handlerType);

		// determine outer targets, i.e. already running/active handlers of
		// other gestures
		// System.out.println("Outer target handlers:");
		List<T> outerTargetHandlers = new ArrayList<>();
		Collection<IGesture> gestures = viewer.getDomain()
				.getAdapters(new TypeToken<IGesture>() {
				}).values();
		for (IGesture g : gestures) {
			// System.out.println("[find active handlers of " + gesture + "]");
			if (g != gesture) {
				for (IHandler handler : g.getActiveHandlers(viewer)) {
					if (handler.getClass().isAssignableFrom(handlerType)) {
						// System.out.println("add active handler " + handler);
						try {
							outerTargetHandlers.add((T) handler);
						} catch (ClassCastException e) {
							// ignore target handler if type parameter is not
							// appropriate
						}
					}
				}
			}
		}

		// already active handlers that can process the events take precedence
		// over scene graph related target handlers
		if (!outerTargetHandlers.isEmpty()) {
			// System.out.println("RETURN outer target handlers:");
			// for (T p : outerTargetHandlers) {
			// System.out.println(p.getHost() + " -> " + p);
			// }
			return outerTargetHandlers;
		}

		// determine target part as the part that controls the first node in the
		// scene graph hierarchy of the given target node
		// System.out.println("Inner target handlers:");
		IVisualPart<? extends Node> targetPart = PartUtils
				.retrieveVisualPart(viewer, target);

		// System.out.println("target part = " + targetPart);

		// search handlers up the hierarchy
		IVisualPart<? extends Node> part = targetPart;
		List<T> handlers = new ArrayList<>();
		while (part != null && handlers.isEmpty()) {
			// System.out.println("[find handlers for " + part + "]");
			// determine handlers
			handlers.addAll(part.getAdapters(handlerType).values());

			// go one level up in the hierarchy
			part = part.getParent();
		}

		// System.out.println("RETURN in reverse order:");
		// for (T p : handlers) {
		// System.out.println(p.getHost() + " -> " + p);
		// }

		return handlers;
	}
}
