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
 * The {@link DefaultHandlerResolver} is an {@link IHandlerResolver}
 * that works in two stages:
 * <ol>
 * <li>Examining the active policies of other tools to find "multi-gesture"
 * policies that implement or extend the given target policy type. If any
 * "multi-gesture" policies are found, the target resolution finishes and these
 * are returned as the target policies. Otherwise, the target resolution
 * continues with the next stage.
 * <li>Examining the policies of the visual parts that are contained within the
 * root-to-target path in the visual part hierarchy. All policies that implement
 * or extend the given target policy type are returned as target policies. The
 * policies that are registered on the root part have highest precedence, i.e.
 * they will be executed first, and the policies that are registered on the
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
	 * <li>Examining the active policies of other tools to find "multi-gesture"
	 * policies that implement or extend the given target policy type. If any
	 * "multi-gesture" policies are found, the target resolution finishes and
	 * these are returned as the target policies. Otherwise, the target
	 * resolution continues with the next stage.
	 * <li>Examining the policies of the visual parts that are contained within
	 * the root-to-target path in the visual part hierarchy. All policies that
	 * implement or extend the given target policy type are returned as target
	 * policies. The policies that are registered on the root part have highest
	 * precedence, i.e. they will be executed first, and the policies that are
	 * registered on the target part have lowest precedence, i.e. they will be
	 * executed last.
	 * </ol>
	 * The second stage is structured in two parts:
	 * <ol>
	 * <li>Determination of the target part.
	 * <li>Determination of the target policies based on the target part.
	 * </ol>
	 * The first {@link IVisualPart} that controls a {@link Node} within the
	 * parent hierarchy of the given target {@link Node} is used as the target
	 * part. If no target part can be found, the root part is used as the target
	 * part.
	 * <p>
	 * Beginning at the root part, and walking down the visual part hierarchy,
	 * all policies of the specified type are collected. The policies that are
	 * registered on one part are (lexicographically) sorted by their role, so
	 * that the target policy selection is deterministic.
	 * <p>
	 * For example, when you have 3 parts, the root part, an intermediate part,
	 * and a leaf part, the target policy selection for a policy of type X could
	 * yield the following results:
	 * <ol>
	 * <li>LayeredRootPart.X with role "0"
	 * <li>LayeredRootPart.X with role "1"
	 * <li>IntermediatePart.X with role "a"
	 * <li>IntermediatePart.X with role "b"
	 * <li>LeafPart.X with role "x"
	 * <li>LeafPart.X with role "y"
	 * </ol>
	 * These policies would then all be executed/notified about an input event
	 * by the calling tool.
	 */
	@Override
	@SuppressWarnings({ "serial", "unchecked" })
	public <T extends IHandler> List<? extends T> resolve(IGesture gesture,
			Node target, IViewer viewer, Class<T> handlerClass) {
		// System.out.println("\n=== determine target policies ===");
		// System.out.println("viewer = " + viewer);
		// System.out.println("raw target node = " + target);
		// System.out.println("policy class = " + policyClass);

		// determine outer targets, i.e. already running/active policies of
		// other tools
		// System.out.println("Outer target policies:");
		List<T> outerTargetHandlers = new ArrayList<>();
		Collection<IGesture> tools = viewer.getDomain()
				.getAdapters(new TypeToken<IGesture>() {
				}).values();
		for (IGesture tool : tools) {
			// System.out.println("[find active policies of " + tool + "]");
			if (tool != gesture) {
				for (IHandler policy : tool.getActiveHandlers(viewer)) {
					if (policy.getClass().isAssignableFrom(handlerClass)) {
						// System.out.println("add active policy " + policy);
						try {
							outerTargetHandlers.add((T) policy);
						} catch (ClassCastException e) {
							// ignore target policy if type parameter is not
							// appropriate
						}
					}
				}
			}
		}

		// already active policies that can process the events take precedence
		// over scene graph related target policies
		if (!outerTargetHandlers.isEmpty()) {
			// System.out.println("RETURN outer target policies:");
			// for (T p : outerTargetPolicies) {
			// System.out.println(p.getHost() + " -> " + p);
			// }
			return outerTargetHandlers;
		}

		// determine target part as the part that controls the first node in the
		// scene graph hierarchy of the given target node
		// System.out.println("Inner target policies:");
		IVisualPart<? extends Node> targetPart = PartUtils
				.retrieveVisualPart(viewer, target);

		// System.out.println("target part = " + targetPart);

		// collect all on-drag-policies on the way from the target part to the
		// root part
		IVisualPart<? extends Node> part = targetPart;
		List<T> handlers = new ArrayList<>();
		while (part != null) {
			// System.out.println("[find policies for " + part + "]");
			// determine on-drag-policies
			Map<AdapterKey<? extends T>, T> partHandlers = part
					.<T> getAdapters(handlerClass);

			// sort descending by role (converted to integer)
			List<AdapterKey<? extends T>> descendinglySortedKeys = new ArrayList<>(
					partHandlers.keySet());
			Collections.sort(descendinglySortedKeys, ADAPTER_KEY_COMPARATOR);

			// add to the list of policies
			for (AdapterKey<? extends T> key : descendinglySortedKeys) {
				// System.out.println("add policy " + key);
				handlers.add(partHandlers.get(key));
			}

			// go one level up in the hierarchy
			part = part.getParent();
		}

		// reverse order in which policies are returned so that parent policies
		// are called before child policies
		Collections.reverse(handlers);

		// System.out.println("RETURN in reverse order:");
		// for (T p : policies) {
		// System.out.println(p.getHost() + " -> " + p);
		// }

		return handlers;
	}
}
