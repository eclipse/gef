package org.eclipse.gef4.mvc.fx.example;

import java.util.ArrayList;
import java.util.List;

import javafx.scene.Group;
import javafx.scene.Node;

import org.eclipse.gef4.mvc.anchors.IAnchor;
import org.eclipse.gef4.mvc.fx.AbstractFXNodeContentPart;
import org.eclipse.gef4.mvc.parts.IContentPart;

public class FXExampleContainerPart extends AbstractFXNodeContentPart {

	private Group g;

	public FXExampleContainerPart() {
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
	public IAnchor<Node> getAnchor(IContentPart<Node> fixedPart) {
		// TODO Auto-generated method stub
		return null;
	}

}
