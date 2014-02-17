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
package org.eclipse.gef4.mvc.fx.tools;

import javafx.event.EventTarget;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;

import org.eclipse.gef4.mvc.fx.parts.FXRootPart;
import org.eclipse.gef4.mvc.parts.IVisualPart;
import org.eclipse.gef4.mvc.viewer.IVisualViewer;

public class FXToolUtils {

	public static IVisualPart<Node> getTargetPart(
			IVisualViewer<Node> viewer, MouseEvent event) {
		EventTarget target = event.getTarget();
	
		if (target instanceof Node) {
			Node targetNode = (Node) target;
	
			// look for the Node in the visual-part-map
			IVisualPart<Node> newSelection = viewer.getVisualPartMap().get(
					targetNode);
			if (newSelection instanceof IVisualPart) {
				return (IVisualPart<Node>) newSelection;
			}
	
			// try to find the root visual in the target node's parent hierarchy
			FXRootPart rootPart = (FXRootPart) viewer.getRootPart();
			Node rootVisual = rootPart.getVisual();
			while (targetNode != null && targetNode != rootVisual) {
				targetNode = targetNode.getParent();
			}
	
			if (targetNode == rootVisual) {
				return rootPart;
			}
		}
	
		return null;
	}

}
