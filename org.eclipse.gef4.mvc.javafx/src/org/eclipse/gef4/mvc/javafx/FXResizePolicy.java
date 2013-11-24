package org.eclipse.gef4.mvc.javafx;

import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.shape.Rectangle;

import org.eclipse.gef4.geometry.planar.Point;
import org.eclipse.gef4.mvc.aspects.resize.AbstractResizePolicy;
import org.eclipse.gef4.mvc.parts.INodeContentPart;

public class FXResizePolicy extends AbstractResizePolicy<Node> {

	Point absoluteInitialMousePressedLocation;

	@Override
	public void initResize(Point absoluteMousePressLocation) {
		absoluteInitialMousePressedLocation = absoluteMousePressLocation;
	}

	@Override
	public void performResize(Point absoluteMouseDragLocation) {
		Node hostVisual = getHost().getVisual();
		Node parentVisual = ((INodeContentPart<Node>) getHost()).getParent()
				.getVisual();
		Point2D absoluteLocalEndPosition = parentVisual
				.sceneToLocal(new Point2D(absoluteMouseDragLocation.x,
						absoluteMouseDragLocation.y));
		// just for testing purposes, relocate and resize to match the rectange 
		// TODO: this does not work if multiple parts are selected
		// create a proper implementation instead
		org.eclipse.gef4.geometry.planar.Rectangle r = new org.eclipse.gef4.geometry.planar.Rectangle(
				new Point(absoluteLocalEndPosition.getX(),
						absoluteLocalEndPosition.getY()), new Point(
						hostVisual.getLayoutX(), hostVisual.getLayoutY()));
		hostVisual.setLayoutX(r.getX());
		hostVisual.setLayoutY(r.getY());
		// TODO: what to do for non resizables?? and resizables here??
		if (hostVisual instanceof Rectangle) {
			Rectangle rectangle = (Rectangle) hostVisual;
			rectangle.setWidth(r.getWidth());
			rectangle.setHeight(r.getHeight());
		}
	}

	@Override
	public void commitResize(Point position) {
		// TODO: create IUndoableOperation to perform the resize and execute it
		// on the IOperationHistory

	}
}
