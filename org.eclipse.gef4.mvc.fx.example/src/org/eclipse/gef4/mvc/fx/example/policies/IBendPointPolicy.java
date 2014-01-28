package org.eclipse.gef4.mvc.fx.example.policies;

import javafx.scene.Node;

import org.eclipse.gef4.geometry.planar.Point;
import org.eclipse.gef4.mvc.policies.IEditPolicy;

public interface IBendPointPolicy extends IEditPolicy<Node> {

	public Point createNewBendPoint();
	
}
