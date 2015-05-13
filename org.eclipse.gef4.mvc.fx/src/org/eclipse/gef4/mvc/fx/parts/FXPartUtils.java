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

import org.eclipse.gef4.geometry.convert.fx.JavaFX2Geometry;
import org.eclipse.gef4.geometry.planar.Rectangle;
import org.eclipse.gef4.mvc.parts.IVisualPart;
import org.eclipse.gef4.mvc.policies.IPolicy;
import org.eclipse.gef4.mvc.viewer.IViewer;

import javafx.scene.Node;

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

}
