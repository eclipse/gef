package org.eclipse.gef4.mvc.fx.tools;

import javafx.event.EventTarget;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;

import org.eclipse.gef4.mvc.parts.IRootVisualPart;
import org.eclipse.gef4.mvc.parts.IVisualPart;
import org.eclipse.gef4.mvc.viewer.IVisualPartViewer;

public class FXPartUtils {

	public static IVisualPart<Node> getMouseTargetPart(
			IVisualPartViewer<Node> viewer, MouseEvent event) {
		EventTarget target = event.getTarget();

		if (target instanceof Node) {
			Node targetNode = (Node) target;

			// try to find the root visual in the target node's parent hierarchy
			Node rootVisual = ((IRootVisualPart<Node>) viewer.getRootPart())
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
				return (IRootVisualPart<Node>) viewer.getRootPart();
			}
		}

		return null;
	}
}
