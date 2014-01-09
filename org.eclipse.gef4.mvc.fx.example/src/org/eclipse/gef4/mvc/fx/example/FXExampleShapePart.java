package org.eclipse.gef4.mvc.fx.example;

import java.util.Collections;
import java.util.List;

import javafx.scene.Node;
import javafx.scene.paint.Color;

import org.eclipse.gef4.geometry.planar.IShape;
import org.eclipse.gef4.mvc.anchors.IAnchor;
import org.eclipse.gef4.mvc.aspects.resizerelocate.AbstractResizeRelocatePolicy;
import org.eclipse.gef4.mvc.aspects.selection.AbstractSelectionPolicy;
import org.eclipse.gef4.mvc.fx.AbstractFXContentPart;
import org.eclipse.gef4.mvc.fx.FXResizeRelocatePolicy;
import org.eclipse.gef4.mvc.fx.FXSelectionPolicy;
import org.eclipse.gef4.mvc.fx.anchors.FXChopBoxAnchor;
import org.eclipse.gef4.mvc.parts.IVisualPart;
import org.eclipse.gef4.swtfx.GeometryNode;

public class FXExampleShapePart extends AbstractFXContentPart {

	private GeometryNode<IShape> visual;
	private IAnchor<Node> anchor;

	public FXExampleShapePart() {
		visual = new GeometryNode<IShape>();
		// TODO: use a proper anchor that computes a position on the border
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
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	protected List<Object> getModelAnchored() {
		if(getParent() != null){
			List anchored = ((FXExampleModelPart)getParent()).getModel().getAnchors().get(getModel());
			if(anchored == null){
				return Collections.emptyList();
			}
			return anchored;
		}
		return super.getModelAnchored();
	}

	@Override
	public void attachVisualToAnchorageVisual(IAnchor<Node> anchor) {
	}

	@Override
	public void detachVisualFromAnchorageVisual(IAnchor<Node> anchor) {
	}

	@Override
	protected IAnchor<Node> getAnchor(IVisualPart<Node> anchored) {
		if(anchor == null){
			anchor = new FXChopBoxAnchor();
		}
		// register listeners
		anchor.setAnchorage(getVisual());
		return anchor;
	}
}
