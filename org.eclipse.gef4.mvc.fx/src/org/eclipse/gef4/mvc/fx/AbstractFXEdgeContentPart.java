package org.eclipse.gef4.mvc.fx;

import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.layout.Pane;

import org.eclipse.gef4.mvc.parts.AbstractEdgeContentPart;
import org.eclipse.gef4.mvc.parts.IVisualPart;

public abstract class AbstractFXEdgeContentPart extends
		AbstractEdgeContentPart<Node> {

	@Override
	protected void addChildVisual(IVisualPart<Node> child, int index) {
		if (getVisual() instanceof Group) {
			((Group) getVisual()).getChildren().add(index, child.getVisual());
		} else if (getVisual() instanceof Pane) {
			((Pane) getVisual()).getChildren().add(index, child.getVisual());
		} else {
			throw new UnsupportedOperationException();
		}
	}

	@Override
	protected void removeChildVisual(IVisualPart<Node> child) {
		if (getVisual() instanceof Group) {
			((Group) getVisual()).getChildren().remove(child.getVisual());
		} else if (getVisual() instanceof Pane) {
			((Pane) getVisual()).getChildren().remove(child.getVisual());
		} else {
			throw new UnsupportedOperationException();
		}
	}

}
