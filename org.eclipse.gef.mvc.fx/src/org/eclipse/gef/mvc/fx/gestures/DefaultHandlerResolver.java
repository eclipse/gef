/*******************************************************************************
 * Copyright (c) 2016 itemis AG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Matthias Wienand (itemis AG) - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.gef.mvc.fx.gestures;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import org.eclipse.gef.common.adapt.AdapterKey;
import org.eclipse.gef.common.adapt.IAdaptable;
import org.eclipse.gef.mvc.fx.domain.IDomain;
import org.eclipse.gef.mvc.fx.handlers.IHandler;
import org.eclipse.gef.mvc.fx.parts.IVisualPart;
import org.eclipse.gef.mvc.fx.parts.PartUtils;
import org.eclipse.gef.mvc.fx.viewer.IViewer;

import com.google.common.reflect.TypeToken;

import javafx.scene.Node;

/**
 * The {@link DefaultHandlerResolver} is an {@link IHandlerResolver} that works
 * in two stages:
 * <ol>
 * <li>Examining the active handlers of other tools to find "multi-gesture"
 * handlers that implement or extend the given target handler type. If any
 * "multi-gesture" handlers are found, the target resolution finishes and these
 * are returned as the target handlers. Otherwise, the target resolution
 * continues with the next stage.
 * <li>Examining the handlers of the visual parts that are contained within the
 * root-to-target path in the visual part hierarchy. All handlers that implement
 * or extend the given target handler type are returned as target handlers. The
 * handlers that are registered on the root part have highest precedence, i.e.
 * they will be executed first, and the handlers that are registered on the
 * target part have lowest precedence, i.e. they will be executed last.
 * </ol>
 * For details, take a look at the
 * {@link #resolve(IGesture, Node, IViewer, Class)} method.
 *
 * @author mwienand
 *
 */
public class DefaultHandlerResolver extends IAdaptable.Bound.Impl<IDomain>
		implements IHandlerResolver {

	private final static class AdapterKeyComparator
			implements Comparator<AdapterKey<?>> {
		private boolean descending;

		public AdapterKeyComparator(boolean descending) {
			this.descending = descending;
		}

		@Override
		public int compare(AdapterKey<?> lhs, AdapterKey<?> rhs) {
			int cmp = lhs.getRole().compareTo(rhs.getRole());
			return descending ? -cmp : cmp;
		}
	}

	/**
	 * The comparator that is used to sort drag adapters.
	 */
	private final static AdapterKeyComparator ADAPTER_KEY_COMPARATOR = new AdapterKeyComparator(
			true);

	/**
	 * {@inheritDoc}
	 * <p>
	 * This strategy works in two stages:
	 * <ol>
	 * <li>Examining the active handlers of other gestures to find
	 * "multi-gesture" handlers that implement or extend the given target type.
	 * If any "multi-gesture" handlers are found, the target resolution finishes
	 * and these are returned as the target handlers. Otherwise, the target
	 * resolution continues with the next stage.
	 * <li>Examining the handlers of the visual parts that are contained within
	 * the root-to-target path in the visual part hierarchy. All handlers that
	 * implement or extend the given target handler type are returned as target
	 * handlers. The handlers that are registered on the root part have highest
	 * precedence, i.e. they will be executed first, and the handlers that are
	 * registered on the target part have lowest precedence, i.e. they will be
	 * executed last.
	 * </ol>
	 * The second stage is structured in two parts:
	 * <ol>
	 * <li>Determination of the target part.
	 * <li>Determination of the target handler based on the target part.
	 * </ol>
	 * The first {@link IVisualPart} that controls a {@link Node} within the
	 * parent hierarchy of the given target {@link Node} is used as the target
	 * part. If no target part can be found, the root part is used as the target
	 * part.
	 * <p>
	 * Beginning at the root part, and walking down the visual part hierarchy,
	 * all handlers of the specified type are collected. The handlers that are
	 * registered on one part are (lexicographically) sorted by their role, so
	 * that the target handler selection is deterministic.
	 * <p>
	 * For example, when you have 3 parts, the root part, an intermediate part,
	 * and a leaf part, the target handler selection for a handler of type X
	 * could yield the following results:
	 * <ol>
	 * <li>LayeredRootPart.X with role "0"
	 * <li>LayeredRootPart.X with role "1"
	 * <li>IntermediatePart.X with role "a"
	 * <li>IntermediatePart.X with role "b"
	 * <li>LeafPart.X with role "x"
	 * <li>LeafPart.X with role "y"
	 * </ol>
	 * These handlers would then all be executed/notified about an input event
	 * by the calling tool.
	 */
	@Override
	@SuppressWarnings({ "serial", "unchecked" })
	public <T extends IHandler> List<? extends T> resolve(IGesture gesture,
			Node target, IViewer viewer, Class<T> handlerType) {
		// System.out.println("\n=== determine target handlers ===");
		// System.out.println("viewer = " + viewer);
		// System.out.println("raw target node = " + target);
		// System.out.println("handler type = " + handlerType);

		// determine outer targets, i.e. already running/active handlers of
		// other tools
		// System.out.println("Outer target handlers:");
		List<T> outerTargetHandlers = new ArrayList<>();
		Collection<IGesture> gestures = viewer.getDomain()
				.getAdapters(new TypeToken<IGesture>() {
				}).values();
		for (IGesture g : gestures) {
			// System.out.println("[find active handlers of " + tool + "]");
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

		// collect all handlers on the way from the target part to the
		// root part
		IVisualPart<? extends Node> part = targetPart;
		List<T> handlers = new ArrayList<>();
		while (part != null) {
			// System.out.println("[find handlers for " + part + "]");
			// determine on-drag-handlers
			Map<AdapterKey<? extends T>, T> partHandlers = part
					.<T> getAdapters(handlerType);

			// sort descending by role (converted to integer)
			List<AdapterKey<? extends T>> descendinglySortedKeys = new ArrayList<>(
					partHandlers.keySet());
			Collections.sort(descendinglySortedKeys, ADAPTER_KEY_COMPARATOR);

			// add to the list of handlers
			for (AdapterKey<? extends T> key : descendinglySortedKeys) {
				// System.out.println("add handler " + key);
				handlers.add(partHandlers.get(key));
			}

			// go one level up in the hierarchy
			part = part.getParent();
		}

		// reverse order in which handlers are returned so that parent handlers
		// are called before child handlers
		Collections.reverse(handlers);

		// System.out.println("RETURN in reverse order:");
		// for (T p : handlers) {
		// System.out.println(p.getHost() + " -> " + p);
		// }

		return handlers;
	}
}
