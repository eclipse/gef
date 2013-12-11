package org.eclipse.gef4.mvc.fx;

import java.util.List;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Rectangle;

import org.eclipse.gef4.mvc.parts.AbstractHandlePart;
import org.eclipse.gef4.mvc.parts.IContentPart;
import org.eclipse.gef4.mvc.tools.ITool;

public class FXHandlePart extends AbstractHandlePart<Node> {

	private ChangeListener<Number> positionChangeListener = new ChangeListener<Number>() {

		@Override
		public void changed(ObservableValue<? extends Number> observable,
				Number oldValue, Number newValue) {
			refreshVisual();
		}
	};

	private ChangeListener<Bounds> boundsChangeListener = new ChangeListener<Bounds>() {

		@Override
		public void changed(ObservableValue<? extends Bounds> observable,
				Bounds oldValue, Bounds newValue) {
			refreshVisual();
		}
	};

	private Rectangle visual = null;
	private Pos pos;

	private FXResizeTool fxResizeTool;

	public FXHandlePart(List<IContentPart<Node>> targetParts, Pos pos) {
		setTargetContentParts(targetParts);
		this.pos = pos;
		visual = new Rectangle();
		visual.setFill(new LinearGradient(0, 0, 0, 5, true,
				CycleMethod.NO_CYCLE, new Stop[] {
						new Stop(0.0, Color.web("#e4fbff")),
						new Stop(0.5, Color.web("#a5d3fb")),
						new Stop(1.0, Color.web("#d5faff")) }));
		visual.setStroke(Color.web("#5a61af"));
		visual.setWidth(5);
		visual.setHeight(5);
	}

	@Override
	public void activate() {
		super.activate();
		for (IContentPart<Node> target : getTargetContentParts()) {
			target.getVisual().layoutXProperty()
					.addListener(positionChangeListener);
			target.getVisual().layoutYProperty()
					.addListener(positionChangeListener);
			target.getVisual().layoutBoundsProperty()
					.addListener(boundsChangeListener);
		}
	}

	@Override
	public void deactivate() {
		for (IContentPart<Node> target : getTargetContentParts()) {
			target.getVisual().layoutXProperty()
					.removeListener(positionChangeListener);
			target.getVisual().layoutYProperty()
					.removeListener(positionChangeListener);
			target.getVisual().layoutBoundsProperty()
					.removeListener(boundsChangeListener);
		}
		super.deactivate();
	}

	@Override
	public Node getVisual() {
		return visual;
	}

	@Override
	public void refreshVisual() {
		Bounds unionedBoundsInScene = getUnionedBoundsInScene(getTargetContentParts());
		Bounds layoutBounds = visual.getParent().sceneToLocal(
				unionedBoundsInScene);
		double xInset = visual.getWidth() / 2.0;
		double yInset = visual.getWidth() / 2.0;
		if (Pos.TOP_LEFT == getPos()) {
			visual.setLayoutX(layoutBounds.getMinX() - xInset);
			visual.setLayoutY(layoutBounds.getMinY() - yInset);
		} else if (Pos.TOP_RIGHT == getPos()) {
			visual.setLayoutX(layoutBounds.getMaxX() - xInset);
			visual.setLayoutY(layoutBounds.getMinY() - yInset);
		} else if (Pos.BOTTOM_RIGHT == getPos()) {
			visual.setLayoutX(layoutBounds.getMaxX() - xInset);
			visual.setLayoutY(layoutBounds.getMaxY() - yInset);
		} else if (Pos.BOTTOM_LEFT == getPos()) {
			visual.setLayoutX(layoutBounds.getMinX() - xInset);
			visual.setLayoutY(layoutBounds.getMaxY() - yInset);
		} else {
			throw new IllegalArgumentException("Unsupported position constant.");
		}
	}

	private Bounds getUnionedBoundsInScene(List<IContentPart<Node>> selection) {
		org.eclipse.gef4.geometry.planar.Rectangle unionedBoundsInScene = null;
		for (IContentPart<Node> cp : selection) {
//			Bounds boundsInParent = cp.getVisual().getBoundsInParent();
//			Bounds boundsInScene = cp.getVisual().getParent()
//					.localToScene(boundsInParent);
			Bounds boundsInScene = cp.getVisual().localToScene(cp.getVisual().getLayoutBounds());
			if (unionedBoundsInScene == null) {
				unionedBoundsInScene = boundsToRectangle(boundsInScene);
			} else {
				unionedBoundsInScene.union(boundsToRectangle(boundsInScene));
			}
		}
		return rectangleToBounds(unionedBoundsInScene);
	}

	// TODO: Move this into GEF4 Geometry Conversion JavaFX
	private org.eclipse.gef4.geometry.planar.Rectangle boundsToRectangle(
			Bounds b) {
		return new org.eclipse.gef4.geometry.planar.Rectangle(b.getMinX(),
				b.getMinY(), b.getWidth(), b.getHeight());
	}

	// TODO: Move this into GEF4 Geometry Conversion JavaFX
	private Bounds rectangleToBounds(
			org.eclipse.gef4.geometry.planar.Rectangle r) {
		return new BoundingBox(r.getX(), r.getY(), r.getWidth(), r.getHeight());
	}

	@Override
	public ITool<Node> getHandleTool() {
		if (fxResizeTool == null) {
			fxResizeTool = new FXResizeTool();
		}
		return fxResizeTool;
	}

	public Pos getPos() {
		return pos;
	}

}
