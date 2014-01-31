package org.eclipse.gef4.mvc.fx.example.parts;

import java.util.ArrayList;
import java.util.List;

import javafx.scene.Group;
import javafx.scene.Node;

import org.eclipse.gef4.mvc.anchors.IAnchor;
import org.eclipse.gef4.mvc.fx.example.model.FXGeometricModel;
import org.eclipse.gef4.mvc.fx.parts.AbstractFXContentPart;
import org.eclipse.gef4.mvc.parts.IVisualPart;

public class FXExampleModelPart extends AbstractFXContentPart {

	private Group g;

	public FXExampleModelPart() {
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
	public FXGeometricModel getModel() {
		return (FXGeometricModel) super.getModel();
	}

	@Override
	protected List<Object> getModelChildren() {
		List<Object> objs = new ArrayList<Object>();
		objs.addAll(getModel().getShapeVisuals());
		objs.addAll(getModel().getCurveVisuals());
		return objs;
	}

	@Override
	public void attachVisualToAnchorageVisual(Node anchorageVisual, IAnchor<Node> anchor) {
		// TODO Auto-generated method stub

	}

	@Override
	public void detachVisualFromAnchorageVisual(Node anchorageVisual, IAnchor<Node> anchor) {
		// TODO Auto-generated method stub

	}

	@Override
	protected IAnchor<Node> getAnchor(IVisualPart<Node> anchored) {
		// TODO Auto-generated method stub
		return null;
	}

}
