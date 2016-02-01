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

import org.eclipse.gef4.common.adapt.AdapterKey;
import org.eclipse.gef4.geometry.convert.fx.JavaFX2Geometry;
import org.eclipse.gef4.geometry.planar.Rectangle;
import org.eclipse.gef4.mvc.domain.IDomain;
import org.eclipse.gef4.mvc.parts.IRootPart;
import org.eclipse.gef4.mvc.parts.IVisualPart;
import org.eclipse.gef4.mvc.viewer.IViewer;

import com.google.common.reflect.TypeToken;

import javafx.scene.Node;
import javafx.scene.Scene;

/**
 * The {@link FXPartUtils} class contains utility methods for the computation of
 * collective bounds ({@link #getUnionedVisualBoundsInScene(Collection)}), the
 * retrieval of the first {@link IVisualPart} in the visual hierarchy of a given
 * {@link Node} ( {@link #retrieveVisualPart(IViewer, Node)}).
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
	@SuppressWarnings("serial")
	public static IViewer<Node> retrieveViewer(IDomain<Node> domain,
			Node target) {
		// determine viewers within domain
		Map<AdapterKey<? extends IViewer<Node>>, IViewer<Node>> viewers = domain
				.getAdapters(new TypeToken<IViewer<Node>>() {
				});

		// test if the target node is contained within any of the viewers
		for (IViewer<Node> viewer : viewers.values()) {
			if (viewer.isViewerVisual(target)) {
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
	public static IVisualPart<Node, ? extends Node> retrieveVisualPart(
			IViewer<Node> viewer, Node target) {
		// search for the first visual part in the visual hierarchy
		IVisualPart<Node, ? extends Node> targetPart = null;
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
