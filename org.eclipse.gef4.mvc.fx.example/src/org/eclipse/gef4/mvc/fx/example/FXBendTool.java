package org.eclipse.gef4.mvc.fx.example;

import java.util.List;

import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;

import org.eclipse.gef4.geometry.planar.ICurve;
import org.eclipse.gef4.geometry.planar.Line;
import org.eclipse.gef4.geometry.planar.Point;
import org.eclipse.gef4.geometry.planar.PolyBezier;
import org.eclipse.gef4.mvc.fx.FXMouseDragGesture;
import org.eclipse.gef4.mvc.fx.FXViewer;
import org.eclipse.gef4.mvc.parts.IContentPart;
import org.eclipse.gef4.mvc.parts.IVisualPart;
import org.eclipse.gef4.mvc.tools.AbstractTool;

public class FXBendTool extends AbstractTool<Node> {

	private FXMouseDragGesture gesture = new FXMouseDragGesture() {
		private FXExampleCurvePart curvePart;
		private double startX;
		private double startY;

		@Override
		protected void release(Node target, MouseEvent e, double dx, double dy) {
		}

		@Override
		protected void press(Node target, MouseEvent e) {
			List<IContentPart<Node>> selected = getDomain().getViewer()
					.getContentPartSelection().getSelected();
			if (selected.size() <= 0) {
				throw new IllegalStateException("null selection!");
			}
			IContentPart<Node> contentPart = selected.get(0);
			if (contentPart instanceof FXExampleCurvePart) {
				curvePart = (FXExampleCurvePart) contentPart;
				
				IVisualPart<Node> handlePart = getDomain().getViewer()
						.getVisualPartMap().get(target);
				
				startX = handlePart.getVisual().getLayoutX();
				startY = handlePart.getVisual().getLayoutY();
				curvePart.anchorPoint = new Point(startX, startY);
			} else {
				throw new IllegalStateException("Illegal selection!");
			}
		}

		@Override
		protected void drag(Node target, MouseEvent e, double dx, double dy) {
			IVisualPart<Node> handlePart = getDomain().getViewer()
					.getVisualPartMap().get(target);
			if (!(handlePart instanceof FXBendHandlePart)) {
				throw new IllegalStateException("Illegal handle part!");
			}
			curvePart.anchorPoint.x = startX + dx;
			curvePart.anchorPoint.y = startY + dy;
			ICurve curve = curvePart.getModel();
			Point p1 = curve.getP1();
			Point p2 = curve.getP2();
			ICurve interpolation = curvePart.anchorPoint == null ? new Line(p1, p2) : PolyBezier.interpolateCubic(p1,
					curvePart.anchorPoint, p2);
			curvePart.setModel(interpolation);
			curvePart.refreshVisual();
		}
	};
	private Scene scene;

	protected void registerListeners() {
		super.registerListeners();
		scene = ((FXViewer) getDomain().getViewer()).getCanvas().getScene();
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
		super.deactivate();
		if (scene != null) {
			gesture.setScene(null);
		}
	}

	@Override
	protected void unregisterListeners() {
		gesture.setScene(null);
		super.unregisterListeners();
	}

}
