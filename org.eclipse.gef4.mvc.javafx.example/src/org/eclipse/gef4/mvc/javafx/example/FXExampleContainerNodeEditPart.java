package org.eclipse.gef4.mvc.javafx.example;

import java.util.ArrayList;
import java.util.List;

import javafx.scene.Group;
import javafx.scene.Node;

import org.eclipse.gef4.mvc.javafx.AbstractFXNodeEditPart;

public class FXExampleContainerNodeEditPart extends AbstractFXNodeEditPart {

	private Group g;

	public FXExampleContainerNodeEditPart() {
		g = new Group();
	}

	@Override
	public Node getVisual() {
		return g;
	}

	@Override
	public void refreshVisual() {
	}

	@Override
	protected List<Object> getModelNodeChildren() {
		List<Object> objs = new ArrayList<Object>();
		objs.addAll((List) getModel());
		return objs;
	}

}
