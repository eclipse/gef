package org.eclipse.gef4.mvc.javafx;

import javafx.geometry.Bounds;
import javafx.scene.Node;

import org.eclipse.gef4.mvc.aspects.resizerelocate.AbstractResizeRelocatePolicy;

public class FXResizeRelocatePolicy extends AbstractResizeRelocatePolicy<Node> {

	private double initialLayoutX, initialLayoutY, initialWidth, initialHeight;

	@Override
	public void initResizeRelocate() {
		Node visual = getHost().getVisual();
		initialLayoutX = visual.getLayoutX();
		initialLayoutY = visual.getLayoutY();
		
		Bounds lb = visual.getLayoutBounds();
		initialWidth = lb.getWidth();
		initialHeight = lb.getHeight();
	}
	
	@Override
	public void performResizeRelocate(double dx, double dy, double dw, double dh) {
		Node visual = getHost().getVisual();
		
		visual.setLayoutX(initialLayoutX + dx);
		visual.setLayoutY(initialLayoutY + dy);
		visual.resize(initialWidth + dw, initialHeight + dh);
	}

	@Override
	public void commitResizeRelocate(double dx, double dy, double dw, double dh) {
		// TODO: create IUndoableOperation to perform the resize and execute it
		// on the IOperationHistory
	}
}
