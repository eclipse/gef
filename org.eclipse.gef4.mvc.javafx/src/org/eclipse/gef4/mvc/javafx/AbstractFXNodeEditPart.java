package org.eclipse.gef4.mvc.javafx;

import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Parent;

import org.eclipse.gef4.mvc.parts.AbstractNodeEditPart;
import org.eclipse.gef4.mvc.parts.INodeEditPart;

public abstract class AbstractFXNodeEditPart extends AbstractNodeEditPart<Node> {

	
	@Override
	protected void addNodeChildVisual(INodeEditPart<Node> child, int index) {
		if(getVisual() instanceof Parent){
			((Group)getVisual()).getChildren().add(index, child.getVisual());
		}
		else {
			throw new UnsupportedOperationException();
		}
	}

	@Override
	protected void removeNodeChildVisual(INodeEditPart<Node> child) {
		if(getVisual() instanceof Parent){
			((Group)getVisual()).getChildren().remove(child.getVisual());
		}
		else {
			throw new UnsupportedOperationException();
		}
	}

}
