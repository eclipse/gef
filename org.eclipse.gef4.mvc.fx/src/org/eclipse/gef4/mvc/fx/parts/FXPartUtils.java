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

import javafx.event.EventTarget;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;

import org.eclipse.gef4.mvc.parts.IRootPart;
import org.eclipse.gef4.mvc.parts.IVisualPart;
import org.eclipse.gef4.mvc.viewer.IVisualViewer;

public class FXPartUtils {

	public static IVisualPart<Node> getMouseTargetPart(
			IVisualViewer<Node> viewer, MouseEvent event) {
		EventTarget target = event.getTarget();

		if (target instanceof Node) {
			Node targetNode = (Node) target;

			// try to find the root visual in the target node's parent hierarchy
			Node rootVisual = ((IRootPart<Node>) viewer.getRootPart())
					.getVisual();

			// look for the Node in the visual-part-map, traverse the hierarchy if needed
			IVisualPart<Node> targetPart = viewer.getVisualPartMap().get(
					targetNode);
			while (targetPart == null && targetNode != null
					&& targetNode != rootVisual) {
				targetNode = targetNode.getParent();
				targetPart = viewer.getVisualPartMap().get(targetNode);
			}
			if(targetPart != null){
				return targetPart;
			}
			if (targetNode == rootVisual) {
				return (IRootPart<Node>) viewer.getRootPart();
			}
		}

		return null;
	}
}
