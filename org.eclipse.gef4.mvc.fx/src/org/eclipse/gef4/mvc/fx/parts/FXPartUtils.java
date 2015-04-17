/*******************************************************************************
 * Copyright (c) 2014 itemis AG and others.
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

import javafx.geometry.Bounds;
import javafx.scene.Node;

import org.eclipse.gef4.geometry.convert.fx.Geometry2JavaFX;
import org.eclipse.gef4.geometry.convert.fx.JavaFX2Geometry;
import org.eclipse.gef4.mvc.parts.IVisualPart;
import org.eclipse.gef4.mvc.policies.IPolicy;
import org.eclipse.gef4.mvc.viewer.IViewer;

public class FXPartUtils {

	/**
	 * Returns the first {@link IVisualPart} in the given list of viewers, for
	 * which both of the following properties hold:
	 * <ol>
	 * <li>Supports the specified policy (which may be <code>null</code>).</li>
	 * <li>Provides a visual that belong to the parent hierarchy of the given
	 * visual.</li>
	 * </ol>
	 *
	 * When no policy is specified (i.e. it is <code>null</code>), the first
	 * visual part that controls a visual in the hierarchy is returned.
	 *
	 * When the <i>searchHierarchy</i> is set to <code>false</code> only the
	 * part directly controlling the given visual is considered.
	 *
	 * @param viewers
	 *            {@link Collection} of all {@link IViewer}s.
	 * @param visual
	 *            {@link Node} visual of the target part.
	 * @param supportedPolicy
	 *            Class of the {@link IPolicy} which has to be supported by the
	 *            target part. May be <code>null</code> to indicate that the
	 *            target part does not have to support a specific policy.
	 * @param searchHierarchy
	 *            If set to <code>true</code>, the full parent hierarchy will be
	 *            searched, otherwise, the direct target part alone is
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

			if (searchHierarchy) {
				// traverse the node hierarchy to find a suitable part
				while (targetNode != null
						&& (targetPart == null || supportedPolicy != null
								&& targetPart.getAdapters(supportedPolicy)
										.isEmpty()) && targetNode != rootVisual) {
					targetNode = targetNode.getParent();
					targetPart = viewer.getVisualPartMap().get(targetNode);
				}
			}

			// check if the found target part supports the policy
			if (targetPart != null) {
				if (supportedPolicy != null) {
					if (!targetPart.getAdapters(supportedPolicy).isEmpty()) {
						return targetPart;
					}
				} else {
					return targetPart;
				}
			}
		}

		return null;
	}

	public static Bounds getUnionedVisualBoundsInScene(
			Collection<? extends IVisualPart<Node, ? extends Node>> parts) {
		org.eclipse.gef4.geometry.planar.Rectangle unionedBoundsInScene = null;
		for (IVisualPart<Node, ? extends Node> cp : parts) {
			Bounds boundsInScene = cp.getVisual().localToScene(
					cp.getVisual().getLayoutBounds());
			if (unionedBoundsInScene == null) {
				unionedBoundsInScene = JavaFX2Geometry
						.toRectangle(boundsInScene);
			} else {
				unionedBoundsInScene.union(JavaFX2Geometry
						.toRectangle(boundsInScene));
			}
		}
		if (unionedBoundsInScene != null) {
			return Geometry2JavaFX.toFXBounds(unionedBoundsInScene);
		} else {
			return null;
		}
	}

}
