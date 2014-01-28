package org.eclipse.gef4.mvc.fx.example.policies;

import javafx.scene.Node;

import org.eclipse.gef4.geometry.planar.Point;
import org.eclipse.gef4.mvc.policies.AbstractEditPolicy;

public abstract class AbstractNewAnchorPointPolicy extends AbstractEditPolicy<Node> {

	public abstract void initAnchorPoint(int wayPointIndex, Point p);
	
	public abstract void moveAnchorPoint(int wayPointIndex, Point p);
	
	public abstract void commitAnchorPoint(int wayPointIndex, Point p);
	
}
