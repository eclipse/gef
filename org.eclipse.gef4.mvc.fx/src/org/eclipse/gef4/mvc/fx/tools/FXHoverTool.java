package org.eclipse.gef4.mvc.fx.tools;

import javafx.event.EventHandler;
import javafx.event.EventTarget;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;

import org.eclipse.gef4.mvc.fx.parts.FXRootVisualPart;
import org.eclipse.gef4.mvc.parts.IContentPart;
import org.eclipse.gef4.mvc.parts.IRootVisualPart;
import org.eclipse.gef4.mvc.parts.IVisualPart;
import org.eclipse.gef4.mvc.tools.AbstractHoverTool;

public class FXHoverTool extends AbstractHoverTool<Node> {

	private EventHandler<MouseEvent> hoverHandler = new EventHandler<MouseEvent>() {
		@Override
		public void handle(MouseEvent event) {
			IVisualPart<Node> targetPart = getTargetPart(event);
			if (targetPart == null) {
				hover(null);
			} else if (targetPart instanceof IRootVisualPart) {
				hover(null);
			} else if (targetPart instanceof IContentPart) {
				hover((IContentPart<Node>) targetPart);
			} else {
				throw new IllegalArgumentException("Unsupported part type.");
			}
		}
	};

	protected IVisualPart<Node> getTargetPart(MouseEvent event) {
		EventTarget target = event.getTarget();

		if (target instanceof Node) {
			Node targetNode = (Node) target;

			// look for the Node in the visual-part-map
			IVisualPart<Node> newSelection = getDomain().getViewer()
					.getVisualPartMap().get(targetNode);
			if (newSelection instanceof IVisualPart) {
				return (IVisualPart<Node>) newSelection;
			}

			// try to find the root visual in the target node's parent hierarchy
			FXRootVisualPart rootPart = (FXRootVisualPart) getDomain()
					.getViewer().getRootPart();
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

	@Override
	public void activate() {
		super.activate();
		getDomain().getViewer().getRootPart().getVisual().getScene()
				.addEventHandler(MouseEvent.MOUSE_MOVED, hoverHandler);
	}

	@Override
	public void deactivate() {
		getDomain().getViewer().getRootPart().getVisual().getScene()
				.removeEventHandler(MouseEvent.MOUSE_MOVED, hoverHandler);
		super.deactivate();
	}

}
