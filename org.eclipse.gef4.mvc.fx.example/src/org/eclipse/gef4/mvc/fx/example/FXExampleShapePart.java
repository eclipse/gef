package org.eclipse.gef4.mvc.fx.example;

import javafx.scene.Node;
import javafx.scene.paint.Color;

import org.eclipse.gef4.geometry.planar.IShape;
import org.eclipse.gef4.mvc.anchors.IAnchor;
import org.eclipse.gef4.mvc.aspects.resizerelocate.AbstractResizeRelocatePolicy;
import org.eclipse.gef4.mvc.aspects.selection.AbstractSelectionPolicy;
import org.eclipse.gef4.mvc.fx.AbstractFXNodeContentPart;
import org.eclipse.gef4.mvc.fx.FXResizeRelocatePolicy;
import org.eclipse.gef4.mvc.fx.FXSelectionPolicy;
import org.eclipse.gef4.mvc.parts.IContentPart;
import org.eclipse.gef4.swtfx.GeometryNode;

public class FXExampleShapePart extends AbstractFXNodeContentPart {

	private GeometryNode<IShape> visual;

	public FXExampleShapePart() {
		visual = new GeometryNode<IShape>();
		visual.setFill(Color.RED);
		installEditPolicy(AbstractSelectionPolicy.class,
				new FXSelectionPolicy());
		installEditPolicy(AbstractResizeRelocatePolicy.class,
				new FXResizeRelocatePolicy());
	}

	@Override
	public IShape getModel() {
		return (IShape) super.getModel();
	}

	@Override
	public void setModel(Object model) {
		if (!(model instanceof IShape)) {
			throw new IllegalArgumentException(
					"Only IShape models are supported.");
		}
		super.setModel(model);
	}

	@Override
	public Node getVisual() {
		return visual;
	}

	@Override
	public void refreshVisual() {
		IShape shape = getModel();
		if (visual.getGeometry() != shape) {
			visual.setGeometry(shape);
		}
	}

	@Override
	public IAnchor<Node> getAnchor(IContentPart<Node> fixedPart) {
		// TODO Auto-generated method stub
		return null;
	}

}
