/*******************************************************************************
 * Copyright (c) 2014, 2015 itemis AG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Alexander Nyßen (itemis AG) - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.gef4.mvc.fx.parts;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.gef4.geometry.convert.fx.JavaFX2Geometry;
import org.eclipse.gef4.geometry.planar.Rectangle;
import org.eclipse.gef4.mvc.parts.IRootPart;
import org.eclipse.gef4.mvc.parts.IVisualPart;
import org.eclipse.gef4.mvc.viewer.IViewer;

import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;

/**
 * The {@link FXPartUtils} class contains utility methods for the computation of
 * collective bounds ({@link #getUnionedVisualBoundsInScene(Collection)}), the
 * retrieval of the first {@link IVisualPart} in the visual hierarchy of a given
 * {@link Node} ( {@link #retrieveVisualPart(IViewer, Node)}), and the
 * (un-)registration of nested visuals (
 * {@link #registerNestedVisuals(IVisualPart, Map, Node)}, and
 * {@link #unregisterVisuals(IVisualPart, Map)}).
 *
 * @author anyssen
 *
 */
public class FXPartUtils {

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
			Collection<? extends IVisualPart<Node, ? extends Node>> parts) {
		Rectangle bounds = null;
		for (IVisualPart<Node, ? extends Node> part : parts) {
			Rectangle boundsInScene = JavaFX2Geometry
					.toRectangle(part.getVisual()
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
	 * Registers this {@link AbstractFXContentPart} for all visuals in the
	 * visual hierarchy of the given {@link Node} at the given
	 * <i>visualPartMap</i>. Does nothing if the given visual is no
	 * {@link Parent}. Does not register the part for visuals of children parts.
	 *
	 * @param visualPart
	 *            The {@link IVisualPart} that controls the given parent visual.
	 * @param visualPartMap
	 *            The map where the visuals are registered.
	 * @param visual
	 *            The {@link Node} whose visual hierarchy is registered.
	 */
	protected static void registerNestedVisuals(
			IVisualPart<Node, ? extends Node> visualPart,
			Map<Node, IVisualPart<Node, ? extends Node>> visualPartMap,
			Node visual) {
		if (visual instanceof Parent) {
			for (Node nestedVisual : ((Parent) visual)
					.getChildrenUnmodifiable()) {
				if (!visualPartMap.containsKey(nestedVisual)
						|| visualPart != visualPartMap.get(nestedVisual)
								.getParent()) {
					visualPartMap.put(nestedVisual, visualPart);
					if (nestedVisual instanceof Parent) {
						registerNestedVisuals(visualPart, visualPartMap,
								nestedVisual);
					}
				}
			}
		}
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
	public static IVisualPart<Node, ? extends Node> retrieveVisualPart(
			IViewer<Node> viewer, Node target) {
		// search for the first visual part in the visual hierarchy
		// TODO for all visuals, a visualpart should be available
		IVisualPart<Node, ? extends Node> targetPart = null;
		while (targetPart == null && target != null) {
			targetPart = viewer.getVisualPartMap().get(target);
			target = target.getParent();
		}

		// fallback to the root part if no target part was found
		IRootPart<Node, ? extends Node> rootPart = viewer.getRootPart();
		if (targetPart == null) {
			targetPart = rootPart;
		}

		return targetPart;
	}

	/**
	 * Removes all visuals from the given <i>visualPartMap</i> where this
	 * {@link AbstractFXContentPart} is registered.
	 *
	 * @param visualPart
	 *            The {@link IVisualPart} that controls the given parent visual.
	 * @param visualPartMap
	 *            The map from which the visuals are removed.
	 */
	protected static void unregisterVisuals(
			IVisualPart<Node, ? extends Node> visualPart,
			Map<Node, IVisualPart<Node, ? extends Node>> visualPartMap) {
		Set<Node> keySet = new HashSet<>(visualPartMap.keySet());
		for (Node visual : keySet) {
			if (visualPartMap.get(visual) == visualPart) {
				visualPartMap.remove(visual);
			}
		}
	}

}
