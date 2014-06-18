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
import java.util.Collections;
import java.util.List;

import javafx.event.Event;
import javafx.event.EventTarget;
import javafx.geometry.Bounds;
import javafx.scene.Node;

import org.eclipse.gef4.geometry.convert.fx.Geometry2JavaFX;
import org.eclipse.gef4.geometry.convert.fx.JavaFX2Geometry;
import org.eclipse.gef4.mvc.parts.IVisualPart;
import org.eclipse.gef4.mvc.policies.IPolicy;
import org.eclipse.gef4.mvc.viewer.IViewer;

public class FXPartUtils {

	public static IVisualPart<Node> getEventTargetPart(
			Collection<IViewer<Node>> viewers, Event event) {
		return getEventTargetPart(viewers, event, null);
	}

	public static IVisualPart<Node> getEventTargetPart(
			Collection<IViewer<Node>> viewers, Event event,
			Class<? extends IPolicy<Node>> supportedPolicy) {
		EventTarget target = event.getTarget();

		for (IViewer<Node> viewer : viewers) {
			if (target instanceof Node) {
				Node rootVisual = viewer.getRootPart().getVisual();

				// look for the Node in the visual-part-map, traverse the
				// hierarchy
				// if needed
				Node targetNode = (Node) target;
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
		}

		return null;
	}

	/**
	 * Returns a list (currently containing zero or one element) containing the
	 * viable target parts for the given viewer, event, and policy class.
	 *
	 * @param viewer
	 * @param event
	 * @param policy
	 * @return
	 */
	public static List<IVisualPart<Node>> getTargetParts(
			Collection<IViewer<Node>> viewers, Event event,
			Class<? extends IPolicy<Node>> policy) {
		IVisualPart<Node> eventTargetPart = getEventTargetPart(viewers, event,
				policy);
		if (eventTargetPart == null) {
			return Collections.emptyList();
		}
		return Collections.singletonList(eventTargetPart);
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
