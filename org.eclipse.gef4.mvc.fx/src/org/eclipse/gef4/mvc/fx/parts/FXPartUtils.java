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
import java.util.List;

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
	 * @param viewers
	 * @param visual
	 * @param supportedPolicy
	 * @return the target part
	 */
	public static IVisualPart<Node> getTargetPart(
			Collection<IViewer<Node>> viewers, Node visual,
			Class<? extends IPolicy<Node>> supportedPolicy) {
		for (IViewer<Node> viewer : viewers) {
			Node rootVisual = viewer.getRootPart().getVisual();

			// traverse the node hierarchy to find a suitable part
			Node targetNode = visual;
			IVisualPart<Node> targetPart = viewer.getVisualPartMap().get(
					targetNode);
			while (targetNode != null
					&& (targetPart == null || supportedPolicy != null
					&& targetPart.getAdapter(supportedPolicy) == null)
					&& targetNode != rootVisual) {
				targetNode = targetNode.getParent();
				targetPart = viewer.getVisualPartMap().get(targetNode);
			}

			if (targetPart != null) {
				return targetPart;
			}
		}

		return null;
	}

	public static Bounds getUnionedVisualBoundsInScene(
			List<IVisualPart<Node>> parts) {
		org.eclipse.gef4.geometry.planar.Rectangle unionedBoundsInScene = null;
		for (IVisualPart<Node> cp : parts) {
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
