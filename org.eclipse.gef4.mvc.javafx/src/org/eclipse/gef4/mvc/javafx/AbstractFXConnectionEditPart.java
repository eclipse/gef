package org.eclipse.gef4.mvc.javafx;

import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Parent;

import org.eclipse.gef4.mvc.parts.AbstractConnectionEditPart;
import org.eclipse.gef4.mvc.parts.IContentsEditPart;

public abstract class AbstractFXConnectionEditPart extends AbstractConnectionEditPart<Node> {

	@Override
	protected void addChildVisual(IContentsEditPart<Node> child, int index) {
		if(getVisual() instanceof Parent){
			((Group)getVisual()).getChildren().add(index, child.getVisual());
		}
		else {
			throw new UnsupportedOperationException();
		}
	}

	@Override
	protected void removeChildVisual(IContentsEditPart<Node> child) {
		if(getVisual() instanceof Parent){
			((Group)getVisual()).getChildren().remove(child.getVisual());
		}
		else {
			throw new UnsupportedOperationException();
		}
	}

}
