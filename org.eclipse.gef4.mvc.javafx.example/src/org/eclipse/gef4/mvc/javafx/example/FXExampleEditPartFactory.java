package org.eclipse.gef4.mvc.javafx.example;

import java.util.List;

import javafx.geometry.Rectangle2D;
import javafx.scene.Node;

import org.eclipse.gef4.mvc.parts.IConnectionEditPart;
import org.eclipse.gef4.mvc.parts.IEditPart;
import org.eclipse.gef4.mvc.parts.INodeEditPart;
import org.eclipse.gef4.mvc.partviewer.IEditPartFactory;

public class FXExampleEditPartFactory implements IEditPartFactory<Node> {

	@Override
	public INodeEditPart<Node> createNodeEditPart(IEditPart<Node> parent,
			Object model) {
		if(model instanceof Rectangle2D){
			return new FXExampleNodeEditPart();			
		}
		else if (model instanceof List){
			return new FXExampleContainerNodeEditPart();
		}
		else {
			throw new IllegalArgumentException();
		}
	}

	@Override
	public IConnectionEditPart<Node> createConnectionEditPart(
			IEditPart<Node> sourceOrTarget, Object model) {
		return null;
	}

}
