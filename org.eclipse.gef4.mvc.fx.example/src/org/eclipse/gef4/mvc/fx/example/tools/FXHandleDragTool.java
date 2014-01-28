package org.eclipse.gef4.mvc.fx.example.tools;

import java.util.List;

import javafx.event.EventTarget;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;

import org.eclipse.gef4.mvc.fx.example.parts.FXAnchorPointHandlePart;
import org.eclipse.gef4.mvc.fx.example.parts.FXExampleCurvePart;
import org.eclipse.gef4.mvc.fx.example.policies.AbstractHandleDragPolicy;
import org.eclipse.gef4.mvc.fx.parts.FXRootVisualPart;
import org.eclipse.gef4.mvc.fx.tools.FXMouseDragGesture;
import org.eclipse.gef4.mvc.fx.viewer.FXViewer;
import org.eclipse.gef4.mvc.parts.IContentPart;
import org.eclipse.gef4.mvc.parts.IHandlePart;
import org.eclipse.gef4.mvc.parts.IVisualPart;
import org.eclipse.gef4.mvc.policies.AbstractResizeRelocatePolicy;
import org.eclipse.gef4.mvc.tools.AbstractTool;

public class FXHandleDragTool extends AbstractTool<Node> {

	private FXMouseDragGesture gesture = new FXMouseDragGesture() {
		@Override
		protected void release(Node target, MouseEvent e, double dx, double dy) {
			IVisualPart<Node> targetPart = getTargetPart(e);
			IHandlePart<Node> handlePart = (IHandlePart<Node>) targetPart;
			AbstractHandleDragPolicy handleDragPolicy = getPolicy(handlePart);
			handleDragPolicy.commit(dx, dy);
		}

		@Override
		protected void press(Node target, MouseEvent e) {
			IVisualPart<Node> targetPart = getTargetPart(e);
			IHandlePart<Node> handlePart = (IHandlePart<Node>) targetPart;
			AbstractHandleDragPolicy handleDragPolicy = getPolicy(handlePart);
			handleDragPolicy.init();
		}

		@Override
		protected void drag(Node target, MouseEvent e, double dx, double dy) {
			IVisualPart<Node> targetPart = getTargetPart(e);
			IHandlePart<Node> handlePart = (IHandlePart<Node>) targetPart;
			AbstractHandleDragPolicy handleDragPolicy = getPolicy(handlePart);
			handleDragPolicy.perform(dx, dy);
		}
	};
	
	private Scene scene;
	
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

	protected void registerListeners() {
		super.registerListeners();
		scene = ((FXViewer) getDomain().getViewer()).getCanvas().getScene();
	}

	protected AbstractHandleDragPolicy getPolicy(IHandlePart<Node> targetPart) {
		return targetPart.getEditPolicy(AbstractHandleDragPolicy.class);
	}

	@Override
	public void activate() {
		super.activate();
		if (scene != null) {
			gesture.setScene(scene);
		}
	}

	@Override
	public void deactivate() {
		if (scene != null) {
			gesture.setScene(null);
		}
		super.deactivate();
	}

	@Override
	protected void unregisterListeners() {
		gesture.setScene(null);
		super.unregisterListeners();
	}

}
