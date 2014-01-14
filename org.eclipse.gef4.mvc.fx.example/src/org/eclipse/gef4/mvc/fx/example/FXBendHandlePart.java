package org.eclipse.gef4.mvc.fx.example;

import java.util.Arrays;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Rectangle;

import org.eclipse.gef4.geometry.planar.BezierCurve;
import org.eclipse.gef4.geometry.planar.ICurve;
import org.eclipse.gef4.geometry.planar.Point;
import org.eclipse.gef4.mvc.parts.AbstractHandlePart;
import org.eclipse.gef4.mvc.parts.IContentPart;

public class FXBendHandlePart extends AbstractHandlePart<Node> {
	
	private double parameter = 0.5;
	private Rectangle visual;

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
	
	public FXBendHandlePart(IContentPart<Node> contentPart, double parameter) {
		setTargetContentParts(Arrays.asList(contentPart));
		this.parameter = parameter;
		visual = new Rectangle(5, 5);
		visual.setTranslateY(- visual.getHeight() / 2);
		visual.setFill(new LinearGradient(0, 0, 0, 5, true,
				CycleMethod.NO_CYCLE, new Stop[] {
						new Stop(0.0, Color.web("#e4fbff")),
						new Stop(0.5, Color.web("#a5d3fb")),
						new Stop(1.0, Color.web("#d5faff")) }));
		visual.setStroke(Color.web("#5a61af"));
	}
	
	public double getParameter() {
		return parameter;
	}

	@Override
	public Node getVisual() {
		return visual;
	}

	@Override
	public void refreshVisual() {
		IContentPart<Node> part = getTargetContentParts().get(0);
		Point point = ((FXExampleCurvePart) part).anchorPoint;
		if (point != null) {
			visual.setLayoutX(point.x);
			visual.setLayoutY(point.y);
		}
		Object model = part.getModel();
		if (model instanceof ICurve) {
			BezierCurve[] bezier = ((ICurve) model).toBezier();
			if (bezier.length == 1) {
				BezierCurve curve = bezier[0];
				point = curve.get(parameter);
				visual.setLayoutX(point.x);
				visual.setLayoutY(point.y);
			}
		}
	}

}
