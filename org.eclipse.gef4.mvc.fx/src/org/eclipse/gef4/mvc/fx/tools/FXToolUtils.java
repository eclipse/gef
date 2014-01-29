package org.eclipse.gef4.mvc.fx.tools;

import javafx.event.EventTarget;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;

import org.eclipse.gef4.mvc.fx.parts.FXRootVisualPart;
import org.eclipse.gef4.mvc.parts.IVisualPart;
import org.eclipse.gef4.mvc.viewer.IVisualPartViewer;

public class FXToolUtils {

	public static IVisualPart<Node> getTargetPart(
			IVisualPartViewer<Node> viewer, MouseEvent event) {
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
			FXRootVisualPart rootPart = (FXRootVisualPart) viewer.getRootPart();
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
