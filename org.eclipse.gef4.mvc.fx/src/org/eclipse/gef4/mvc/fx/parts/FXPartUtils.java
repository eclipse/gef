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
import java.util.Map;

import org.eclipse.gef4.geometry.convert.fx.JavaFX2Geometry;
import org.eclipse.gef4.geometry.planar.Rectangle;
import org.eclipse.gef4.mvc.parts.IVisualPart;
import org.eclipse.gef4.mvc.policies.IPolicy;
import org.eclipse.gef4.mvc.viewer.IViewer;

import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;

/**
 * The {@link FXPartUtils} class contains utility methods for the identification
 * of possible event targets (
 * {@link #getTargetPart(Collection, Node, Class, boolean)}) and the computation
 * of collective bounds ({@link #getUnionedVisualBoundsInScene(Collection)}).
 *
 * @author anyssen
 *
 */
public class FXPartUtils {

	/**
	 * Returns the first {@link IVisualPart} in the given list of viewers, for
	 * which both of the following properties hold:
	 * <ol>
	 * <li>Supports the specified policy (which may be <code>null</code>).</li>
	 * <li>Provides a visual that belongs to the parent hierarchy of the given
	 * visual.</li>
	 * </ol>
	 *
	 * When no policy is specified (i.e. it is <code>null</code>), the first
	 * visual part that controls a visual in the hierarchy is returned.
	 *
	 * When the <i>searchHierarchy</i> is set to <code>false</code> only the
	 * first visual part is considered.
	 *
	 * @param <T>
	 *            The type of the {@link IPolicy} that should be supported.
	 *
	 * @param viewers
	 *            {@link Collection} of all {@link IViewer}s.
	 * @param visual
	 *            {@link Node} visual that received an event.
	 * @param supportedPolicy
	 *            Class of the {@link IPolicy} which has to be supported by the
	 *            target part. May be <code>null</code> to indicate that the
	 *            target part does not have to support a specific policy.
	 * @param searchHierarchy
	 *            If set to <code>true</code>, the full visual part hierarchy
	 *            will be searched, otherwise, the direct target part alone is
	 *            considered.
	 * @return The first suitable target part, or <code>null</code> if no
	 *         suitable target part can be found.
	 */
	public static <T extends IPolicy<Node>> IVisualPart<Node, ? extends Node> getTargetPart(
			Collection<IViewer<Node>> viewers, Node visual,
			Class<T> supportedPolicy, boolean searchHierarchy) {
		for (IViewer<Node> viewer : viewers) {
			Node rootVisual = viewer.getRootPart().getVisual();

			// determine target part for event target
			Node targetNode = visual;
			IVisualPart<Node, ? extends Node> targetPart = viewer
					.getVisualPartMap().get(targetNode);

			// traverse the node hierarchy to find a suitable target part
			while (targetNode != null
					&& (targetPart == null
							|| searchHierarchy && supportedPolicy != null
									&& targetPart.getAdapters(supportedPolicy)
											.isEmpty())
					&& targetNode != rootVisual) {
				targetNode = targetNode.getParent();
				targetPart = viewer.getVisualPartMap().get(targetNode);
			}

			if (targetPart != null) {
				// check if the found target part supports the policy
				if (supportedPolicy == null) {
					return targetPart;
				}
				if (!targetPart.getAdapters(supportedPolicy).isEmpty()) {
					return targetPart;
				}
				return null;
			}
		}
		// no suitable part could be found
		return null;
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
	 * {@link Parent}.
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
				if (!visualPartMap.containsKey(nestedVisual)) {
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
	 * Removes all visuals in the visual hierarchy of the given {@link Node}
	 * from the given <i>visualPartMap</i> where this
	 * {@link AbstractFXContentPart} is registered. Does nothing if the given
	 * visual is no {@link Parent}.
	 *
	 * @param visualPart
	 *            The {@link IVisualPart} that controls the given parent visual.
	 * @param visualPartMap
	 *            The map from which the visuals are removed.
	 * @param visual
	 *            The {@link Parent} whose visual hierarchy is unregistered.
	 */
	protected static void unregisterNestedVisuals(
			IVisualPart<Node, ? extends Node> visualPart,
			Map<Node, IVisualPart<Node, ? extends Node>> visualPartMap,
			Node visual) {
		if (visual instanceof Parent) {
			for (Node nestedVisual : ((Parent) visual)
					.getChildrenUnmodifiable()) {
				if (visualPartMap.containsKey(nestedVisual)
						&& visualPartMap.get(nestedVisual) == visualPart) {
					visualPartMap.remove(nestedVisual);
					if (nestedVisual instanceof Parent) {
						unregisterNestedVisuals(visualPart, visualPartMap,
								nestedVisual);
					}
				}
			}
		}
	}
}
