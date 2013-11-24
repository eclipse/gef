package org.eclipse.gef4.mvc.javafx;

import javafx.scene.Group;
import javafx.scene.Node;

import org.eclipse.gef4.mvc.parts.AbstractNodeContentPart;
import org.eclipse.gef4.mvc.parts.IVisualPart;

public abstract class AbstractFXNodeContentPart extends
		AbstractNodeContentPart<Node> {

	@Override
	protected void addChildVisual(IVisualPart<Node> child, int index) {
		if (getVisual() instanceof Group) {
			((Group) getVisual()).getChildren().add(index, child.getVisual());
		} else {
			throw new UnsupportedOperationException();
		}
	}

	@Override
	protected void removeChildVisual(IVisualPart<Node> child) {
		if (getVisual() instanceof Group) {
			((Group) getVisual()).getChildren().remove(child.getVisual());
		} else {
			throw new UnsupportedOperationException();
		}
	}

}
