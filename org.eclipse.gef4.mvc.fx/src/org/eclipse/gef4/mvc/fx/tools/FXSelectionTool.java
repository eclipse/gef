package org.eclipse.gef4.mvc.fx.tools;

import javafx.event.EventHandler;
import javafx.event.EventTarget;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;

import org.eclipse.gef4.mvc.fx.parts.FXRootVisualPart;
import org.eclipse.gef4.mvc.fx.viewer.FXViewer;
import org.eclipse.gef4.mvc.parts.IContentPart;
import org.eclipse.gef4.mvc.parts.IRootVisualPart;
import org.eclipse.gef4.mvc.parts.IVisualPart;
import org.eclipse.gef4.mvc.tools.AbstractSelectionTool;

public class FXSelectionTool extends AbstractSelectionTool<Node> {

	private EventHandler<MouseEvent> pressedHandler = new EventHandler<MouseEvent>() {
		@Override
		public void handle(MouseEvent event) {
			IVisualPart<Node> targetPart = getTargetPart(event);
			if (targetPart == null) {
				return;
			}

			boolean append = event.isControlDown();
			if (targetPart instanceof IRootVisualPart) {
				select(null, append);
			} else if (targetPart instanceof IContentPart) {
				select((IContentPart<Node>) targetPart, append);
			} else {
				throw new IllegalArgumentException(
						"This tool only supports IRootVisualPart and IContentPart targets");
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
		((FXViewer) getDomain().getViewer()).getCanvas().getScene()
				.addEventHandler(MouseEvent.MOUSE_PRESSED, pressedHandler);
	}

	@Override
	public void deactivate() {
		((FXViewer) getDomain().getViewer()).getCanvas().getScene()
				.removeEventHandler(MouseEvent.MOUSE_PRESSED, pressedHandler);
		super.deactivate();
	}

}
