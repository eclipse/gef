package org.eclipse.gef4.mvc.fx.example;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javafx.scene.Group;
import javafx.scene.Node;

import org.eclipse.gef4.mvc.anchors.IAnchor;
import org.eclipse.gef4.mvc.fx.AbstractFXContentPart;
import org.eclipse.gef4.mvc.fx.example.FXExampleViewPart.ExampleGeometricModel;
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
	public ExampleGeometricModel getModel() {
		return (ExampleGeometricModel)super.getModel();
	}

	@Override
	protected List<Object> getModelChildren() {
		List<Object> objs = new ArrayList<Object>();
		objs.addAll(Arrays.asList(getModel().getAllGeometries()));
		return objs;
	}

	@Override
	public void attachVisualToAnchorageVisual(IAnchor<Node> anchor) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void detachVisualFromAnchorageVisual(IAnchor<Node> anchor) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected IAnchor<Node> getAnchor(IVisualPart<Node> anchored) {
		// TODO Auto-generated method stub
		return null;
	}

	
}
