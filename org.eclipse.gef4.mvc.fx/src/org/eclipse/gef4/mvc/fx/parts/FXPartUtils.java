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

import javafx.event.Event;
import javafx.event.EventTarget;
import javafx.scene.Node;

import org.eclipse.gef4.mvc.parts.IRootPart;
import org.eclipse.gef4.mvc.parts.IVisualPart;
import org.eclipse.gef4.mvc.policies.IPolicy;
import org.eclipse.gef4.mvc.viewer.IVisualViewer;

public class FXPartUtils {

	public static IVisualPart<Node> getEventTargetPart(
			IVisualViewer<Node> viewer, Event event,
			Class<IPolicy<Node>> supportedPolicy) {
		EventTarget target = event.getTarget();

		if (target instanceof Node) {
			Node rootVisual = ((IRootPart<Node>) viewer.getRootPart())
					.getVisual();

			// look for the Node in the visual-part-map, traverse the hierarchy
			// if needed
			Node targetNode = (Node) target;
			IVisualPart<Node> targetPart = viewer.getVisualPartMap().get(
					targetNode);
			while ((targetPart == null || supportedPolicy != null
					&& targetPart
							.getBound((Class<IPolicy<Node>>) supportedPolicy) == null)
					&& targetNode != rootVisual) {
				targetNode = targetNode.getParent();
				targetPart = viewer.getVisualPartMap().get(targetNode);
			}

			if (targetPart != null) {
				return targetPart;
			}
		}

		// TODO: make sure tools don't break if the target part cannot be found
		return null;
	}
	
	public static IVisualPart<Node> getEventTargetPart(
			IVisualViewer<Node> viewer, Event event) {
		return getEventTargetPart(viewer, event, null);
	}

}
