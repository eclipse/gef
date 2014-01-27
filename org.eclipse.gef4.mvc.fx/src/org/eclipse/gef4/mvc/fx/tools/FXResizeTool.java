package org.eclipse.gef4.mvc.fx.tools;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;

import org.eclipse.gef4.geometry.convert.fx.JavaFX2Geometry;
import org.eclipse.gef4.geometry.planar.Point;
import org.eclipse.gef4.geometry.planar.Rectangle;
import org.eclipse.gef4.mvc.fx.parts.FXBoxHandlePart;
import org.eclipse.gef4.mvc.fx.viewer.FXViewer;
import org.eclipse.gef4.mvc.parts.IContentPart;
import org.eclipse.gef4.mvc.parts.IVisualPart;
import org.eclipse.gef4.mvc.tools.AbstractResizeRelocateTool;

public class FXResizeTool extends AbstractResizeRelocateTool<Node> {

	private Pos pos;

	private FXMouseDragGesture gesture = new FXMouseDragGesture() {
		@Override
		protected void release(Node target, MouseEvent e, double dx, double dy) {
			commitResize(new Point(e.getSceneX(), e.getSceneY()));
		}

		@Override
		protected void press(Node target, MouseEvent e) {
			IVisualPart<Node> part = getDomain().getViewer()
					.getVisualPartMap().get(target);

			if (part instanceof FXBoxHandlePart) {
				pos = ((FXBoxHandlePart) part).getPos();
			} else {
				return;
			}

			initResize(new Point(e.getSceneX(), e.getSceneY()));
		}

		@Override
		protected void drag(Node target, MouseEvent e, double dx, double dy) {
			performResize(new Point(e.getSceneX(), e.getSceneY()));
		}
	};

	@Override
	public void activate() {
		super.activate();
		gesture.setScene(((FXViewer) getDomain().getViewer()).getCanvas()
				.getScene());
	}

	@Override
	public void deactivate() {
		gesture.setScene(null);
		super.deactivate();
	}

	@Override
	protected Rectangle getVisualBounds(IContentPart<Node> contentPart) {
		if (contentPart == null) {
			throw new IllegalArgumentException("contentPart may not be null!");
		}
		return JavaFX2Geometry.toRectangle(contentPart.getVisual()
				.localToScene(contentPart.getVisual().getBoundsInLocal()));
	}

	@Override
	protected ReferencePoint getReferencePoint() {
		if (pos == Pos.BOTTOM_RIGHT) {
			return ReferencePoint.BOTTOM_RIGHT;
		} else if (pos == Pos.BOTTOM_LEFT) {
			return ReferencePoint.BOTTOM_LEFT;
		} else if (pos == Pos.TOP_RIGHT) {
			return ReferencePoint.TOP_RIGHT;
		} else if (pos == Pos.TOP_LEFT) {
			return ReferencePoint.TOP_LEFT;
		} else {
			throw new IllegalStateException("unknown Pos!");
		}
	}

}
