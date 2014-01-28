package org.eclipse.gef4.mvc.fx.parts;

import java.util.List;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Bounds;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Rectangle;

import org.eclipse.gef4.geometry.convert.fx.Geometry2JavaFX;
import org.eclipse.gef4.geometry.convert.fx.JavaFX2Geometry;
import org.eclipse.gef4.mvc.parts.AbstractHandlePart;
import org.eclipse.gef4.mvc.parts.IContentPart;

public class FXBoxHandlePart extends AbstractFXHandlePart {

	private Rectangle visual = null;
	private Pos pos;

	public FXBoxHandlePart(List<IContentPart<Node>> targetParts, Pos pos) {
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
			// Bounds boundsInParent = cp.getVisual().getBoundsInParent();
			// Bounds boundsInScene = cp.getVisual().getParent()
			// .localToScene(boundsInParent);
			Bounds boundsInScene = cp.getVisual().localToScene(
					cp.getVisual().getLayoutBounds());
			if (unionedBoundsInScene == null) {
				unionedBoundsInScene = JavaFX2Geometry
						.toRectangle(boundsInScene);
			} else {
				unionedBoundsInScene.union(JavaFX2Geometry
						.toRectangle(boundsInScene));
			}
		}
		return Geometry2JavaFX.toFXBounds(unionedBoundsInScene);
	}

	public Pos getPos() {
		return pos;
	}

}
