package org.eclipse.gef4.mvc.fx.example.parts;

import java.util.Arrays;

import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Rectangle;

import org.eclipse.gef4.geometry.planar.Point;
import org.eclipse.gef4.mvc.fx.example.policies.AbstractAnchorPointPolicy;
import org.eclipse.gef4.mvc.fx.example.policies.AbstractHandleDragPolicy;
import org.eclipse.gef4.mvc.fx.example.policies.AbstractNewAnchorPointPolicy;
import org.eclipse.gef4.mvc.fx.parts.AbstractFXHandlePart;
import org.eclipse.gef4.mvc.parts.IContentPart;

public class FXAnchorPointHandlePart extends AbstractFXHandlePart {
	
	private Rectangle visual;
	private Point startPoint;
	private Point currentPosition;
	private int wayPointIndex;

	public FXAnchorPointHandlePart(IContentPart<Node> contentPart, int index, Point wayPoint) {
		setTargetContentParts(Arrays.asList(contentPart));
		this.wayPointIndex = index;
		startPoint = wayPoint;
		currentPosition = new Point(startPoint);
		visual = new Rectangle(5, 5);
		visual.setTranslateY(- visual.getHeight() / 2);
		visual.setFill(new LinearGradient(0, 0, 0, 5, true,
				CycleMethod.NO_CYCLE, new Stop[] {
						new Stop(0.0, Color.web("#e4fbff")),
						new Stop(0.5, Color.web("#a5d3fb")),
						new Stop(1.0, Color.web("#d5faff")) }));
		visual.setStroke(Color.web("#5a61af"));
		installEditPolicy(AbstractHandleDragPolicy.class, new AbstractHandleDragPolicy() {
			@Override
			public void perform(double dx, double dy) {
				AbstractAnchorPointPolicy policy = getPolicy();
				currentPosition = startPoint.getTranslated(dx, dy);
				policy.moveAnchorPoint(wayPointIndex, currentPosition);
			}
			
			@Override
			public void init() {
				AbstractAnchorPointPolicy policy = getPolicy();
				policy.initAnchorPoint(wayPointIndex);
			}
			
			@Override
			public void commit(double dx, double dy) {
				AbstractAnchorPointPolicy policy = getPolicy();
				currentPosition = startPoint.getTranslated(dx, dy);
				policy.commitAnchorPoint(wayPointIndex, currentPosition);
			}
		});
	}
	
	protected AbstractAnchorPointPolicy getPolicy() {
		return getTargetContentParts().get(0).getEditPolicy(AbstractAnchorPointPolicy.class);
	}

	@Override
	public Node getVisual() {
		return visual;
	}

	@Override
	public void refreshVisual() {
		visual.setLayoutX(currentPosition.x);
		visual.setLayoutY(currentPosition.y);
	}

}
