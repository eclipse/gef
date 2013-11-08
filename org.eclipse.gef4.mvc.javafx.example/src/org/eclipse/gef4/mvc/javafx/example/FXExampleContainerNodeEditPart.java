package org.eclipse.gef4.mvc.javafx.example;

import java.util.ArrayList;
import java.util.List;

import javafx.scene.Group;
import javafx.scene.Node;

import org.eclipse.gef4.mvc.javafx.AbstractFXNodeEditPart;
import org.eclipse.gef4.mvc.parts.IEditPart;

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
	protected List<Object> getModelChildren() {
		List<Object> objs = new ArrayList<Object>();
		objs.addAll((List) getModel());
		return objs;
	}

	@Override
	protected boolean isNodeModel(Object model) {
		return true;
	}

	@Override
	protected boolean isConnectionModel(Object model) {
		return false;
	}
	
	

}
