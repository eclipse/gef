/*******************************************************************************
 * Copyright (c) 2014, 2017 itemis AG and others.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Alexander Nyßen (itemis AG) - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.gef.mvc.fx.parts;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;

import org.eclipse.gef.common.adapt.AdapterKey;
import org.eclipse.gef.fx.utils.NodeUtils;
import org.eclipse.gef.geometry.convert.fx.FX2Geometry;
import org.eclipse.gef.geometry.planar.Rectangle;
import org.eclipse.gef.mvc.fx.domain.IDomain;
import org.eclipse.gef.mvc.fx.viewer.IViewer;

import com.google.common.collect.Multiset;

import javafx.scene.Node;
import javafx.scene.Scene;

/**
 * Provides utilities needed in the context of {@link IVisualPart}s.
 *
 * @author anyssen
 *
 */
public class PartUtils {

	/**
	 * Searches the given collection of {@link IVisualPart}s for elements of the
	 * specified type.
	 *
	 * @param <T>
	 *            The type of returned elements.
	 * @param parts
	 *            The collection of parts which is filtered.
	 * @param type
	 *            The type of returned elements.
	 * @return A list of all elements of the specified type.
	 */
	public static <T extends IVisualPart<? extends Node>> List<T> filterParts(
			Collection<? extends IVisualPart<? extends Node>> parts,
			Class<T> type) {
		return filterParts(parts, (p) -> type.isInstance(p));
	}

	/**
	 * Searches the given collection of {@link IVisualPart}s for elements of the
	 * specified type.
	 *
	 * @param <T>
	 *            The type of returned elements.
	 * @param parts
	 *            The collection of parts which is filtered.
	 * @param filter
	 *            The type of returned elements.
	 * @return A list of all elements of the specified type.
	 */
	@SuppressWarnings("unchecked")
	public static <T extends IVisualPart<? extends Node>> List<T> filterParts(
			Collection<? extends IVisualPart<? extends Node>> parts,
			Predicate<? super IVisualPart<? extends Node>> filter) {
		List<T> filtered = new ArrayList<>();
		for (IVisualPart<? extends Node> c : parts) {
			if (filter.test(c)) {
				filtered.add((T) c);
			}
		}
		return filtered;
	}

	/**
	 * Collects the anchoreds of all given {@link IVisualPart}s.
	 *
	 * @param anchorages
	 *            The collection of {@link IVisualPart}s for which the anchoreds
	 *            are collected.
	 * @return A list of all the anchoreds of all the given {@link IVisualPart}
	 *         s.
	 */
	public static List<IVisualPart<? extends Node>> getAnchoreds(
			Collection<? extends IVisualPart<? extends Node>> anchorages) {
		List<IVisualPart<? extends Node>> anchoreds = new ArrayList<>();
		for (IVisualPart<? extends Node> a : anchorages) {
			anchoreds.addAll(a.getAnchoredsUnmodifiable());
		}
		return anchoreds;
	}

	/**
	 * Collects the anchoreds of the given {@link IVisualPart} which are
	 * registered under the specified role.
	 *
	 * @param anchorage
	 *            The {@link IVisualPart} for which the anchoreds are collected.
	 * @param role
	 *            The role under which the anchoreds have to be registered to be
	 *            collected.
	 * @return A list of the anchoreds of the given {@link IVisualPart} which
	 *         are registered under the specified role.
	 */
	public static Set<IVisualPart<? extends Node>> getAnchoreds(
			IVisualPart<? extends Node> anchorage, String role) {
		HashSet<IVisualPart<? extends Node>> result = new HashSet<>();
		Multiset<IVisualPart<? extends Node>> anchoreds = anchorage
				.getAnchoredsUnmodifiable();
		for (IVisualPart<? extends Node> anchored : anchoreds) {
			if (anchored.getAnchoragesUnmodifiable().containsEntry(anchorage,
					role)) {
				result.add(anchored);
			}
		}
		return result;
	}

	/**
	 * Returns the unioned visual bounds of the given {@link IVisualPart}s in
	 * the coordinate system of the {@link Scene}.
	 *
	 * @param parts
	 *            The {@link IVisualPart}s for which the unioned bounds are
	 *            computed.
	 * @return The unioned visual bounds of the given {@link IVisualPart}s in
	 *         the coordinate system of the {@link Scene}.
	 */
	public static Rectangle getUnionedVisualBoundsInScene(
			Collection<? extends IVisualPart<? extends Node>> parts) {
		Rectangle bounds = null;
		for (IVisualPart<? extends Node> part : parts) {
			Rectangle boundsInScene = FX2Geometry.toRectangle(part.getVisual()
					.localToScene(part.getVisual().getLayoutBounds()));
			if (bounds == null) {
				bounds = boundsInScene;
			} else {
				bounds.union(boundsInScene);
			}
		}
		return bounds;
	}

	/**
	 * Returns the {@link IViewer} of the given {@link IDomain} in which the
	 * given {@link Node} is contained, or <code>null</code> if the given
	 * {@link Node} is not contained within an {@link IViewer} of the given
	 * {@link IDomain}.
	 *
	 * @param domain
	 *            The {@link IDomain} that contains the {@link IViewer}s that
	 *            are tested to contain the given target {@link Node}.
	 * @param target
	 *            The {@link Node} for which to determine the containg
	 *            {@link IViewer}.
	 * @return The {@link IViewer} in which the given target {@link Node} is
	 *         contained, or <code>null</code>.
	 */
	public static IViewer retrieveViewer(IDomain domain, Node target) {
		// determine viewers within domain
		Map<AdapterKey<? extends IViewer>, IViewer> viewers = domain
				.getAdapters(IViewer.class);

		// test if the target node is contained within any of the viewers
		for (IViewer viewer : viewers.values()) {
			if (NodeUtils.isNested(viewer.getCanvas(), target)) {
				return viewer;
			}
		}

		// visual is not contained within any of the viewers of the given domain
		return null;
	}

	/**
	 * Returns the first {@link IVisualPart} in the visual hierarchy of the
	 * given {@link Node}. If no {@link IVisualPart} can be found within the
	 * visual hierarchy of the {@link Node}, the {@link IRootPart} of the given
	 * {@link IViewer} is returned.
	 *
	 * @param viewer
	 *            The {@link IViewer} in which the {@link IVisualPart} is
	 *            searched.
	 * @param target
	 *            The {@link Node} for which the corresponding
	 *            {@link IVisualPart} is to be returned.
	 * @return The first {@link IVisualPart} in the visual hierarchy of the
	 *         given {@link Node}, or the {@link IRootPart} of the given
	 *         {@link IViewer}.
	 */
	public static IVisualPart<? extends Node> retrieveVisualPart(IViewer viewer,
			Node target) {
		// search for the first visual part in the visual hierarchy
		IVisualPart<? extends Node> targetPart = null;
		while (targetPart == null && target != null) {
			targetPart = viewer.getVisualPartMap().get(target);
			target = target.getParent();
		}

		// fallback to the root part if no target part was found
		if (targetPart == null) {
			targetPart = viewer.getRootPart();
		}
		return targetPart;
	}

}
