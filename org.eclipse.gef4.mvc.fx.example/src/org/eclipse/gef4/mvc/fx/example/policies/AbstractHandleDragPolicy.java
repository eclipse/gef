package org.eclipse.gef4.mvc.fx.example.policies;

import javafx.scene.Node;

import org.eclipse.gef4.mvc.policies.AbstractEditPolicy;

public abstract class AbstractHandleDragPolicy extends AbstractEditPolicy<Node> {
	
	public abstract void init();
	
	public abstract void perform(double dx, double dy);
	
	public abstract void commit(double dx, double dy);

}
