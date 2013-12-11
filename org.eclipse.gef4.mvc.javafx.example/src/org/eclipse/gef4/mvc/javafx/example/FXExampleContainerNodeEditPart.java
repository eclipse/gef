package org.eclipse.gef4.mvc.javafx.example;

import java.util.ArrayList;
import java.util.List;

import javafx.scene.Group;
import javafx.scene.Node;

import org.eclipse.gef4.mvc.javafx.AbstractFXNodeContentPart;

public class FXExampleContainerNodeEditPart extends AbstractFXNodeContentPart {

	private Group g;

	public FXExampleContainerNodeEditPart() {
		g = new Group();
		g.setAutoSizeChildren(false);
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
	protected boolean isModelObject(Object model) {
		return true;
	}

	@Override
	protected boolean isModelLink(Object model) {
		return false;
	}

}
