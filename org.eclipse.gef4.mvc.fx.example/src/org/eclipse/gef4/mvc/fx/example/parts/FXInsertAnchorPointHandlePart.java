package org.eclipse.gef4.mvc.fx.example.parts;

import java.util.Arrays;

import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Rectangle;

import org.eclipse.gef4.geometry.planar.Point;
import org.eclipse.gef4.mvc.fx.example.policies.AbstractNewAnchorPointPolicy;
import org.eclipse.gef4.mvc.fx.example.policies.AbstractHandleDragPolicy;
import org.eclipse.gef4.mvc.fx.parts.AbstractFXHandlePart;
import org.eclipse.gef4.mvc.parts.IContentPart;

public class FXInsertAnchorPointHandlePart extends AbstractFXHandlePart {

	private Point startPoint;
	private Point currentPosition;
	private int wayPointIndex;
	private Rectangle visual;
	
	public FXInsertAnchorPointHandlePart(IContentPart<Node> host, int index, Point midPoint) {
		setTargetContentParts(Arrays.asList(host));
		this.wayPointIndex = index;
		startPoint = midPoint;
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
			public void init() {
				AbstractNewAnchorPointPolicy policy = getAnchorPointPolicy();
				policy.initAnchorPoint(wayPointIndex, startPoint);
			}

			@Override
			public void perform(double dx, double dy) {
				AbstractNewAnchorPointPolicy policy = getAnchorPointPolicy();
				currentPosition = startPoint.getTranslated(dx, dy);
				policy.moveAnchorPoint(wayPointIndex, currentPosition);
				refreshVisual();
			}
			
			@Override
			public void commit(double dx, double dy) {
				AbstractNewAnchorPointPolicy policy = getAnchorPointPolicy();
				currentPosition = startPoint.getTranslated(dx, dy);
				policy.commitAnchorPoint(wayPointIndex, currentPosition);
				refreshVisual();
			}
		});
	}
	
	protected AbstractNewAnchorPointPolicy getAnchorPointPolicy() {
		return getTargetContentParts().get(0).getEditPolicy(AbstractNewAnchorPointPolicy.class);
	}
	
	@Override
	public void refreshVisual() {
		visual.setLayoutX(currentPosition.x);
		visual.setLayoutY(currentPosition.y);
	}

	@Override
	public Rectangle getVisual() {
		return visual;
	}

}
