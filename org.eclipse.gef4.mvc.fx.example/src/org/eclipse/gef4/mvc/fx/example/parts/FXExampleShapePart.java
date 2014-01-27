package org.eclipse.gef4.mvc.fx.example.parts;

import java.util.Collections;
import java.util.List;

import javafx.scene.Node;
import javafx.scene.paint.Color;

import org.eclipse.gef4.geometry.planar.IShape;
import org.eclipse.gef4.mvc.anchors.IAnchor;
import org.eclipse.gef4.mvc.fx.anchors.FXChopBoxAnchor;
import org.eclipse.gef4.mvc.fx.example.model.FXGeometricShapeVisual;
import org.eclipse.gef4.mvc.fx.parts.AbstractFXContentPart;
import org.eclipse.gef4.mvc.fx.policies.FXResizeRelocatePolicy;
import org.eclipse.gef4.mvc.fx.policies.FXSelectionPolicy;
import org.eclipse.gef4.mvc.parts.IVisualPart;
import org.eclipse.gef4.mvc.policies.AbstractResizeRelocatePolicy;
import org.eclipse.gef4.mvc.policies.AbstractSelectionPolicy;
import org.eclipse.gef4.swtfx.GeometryNode;

public class FXExampleShapePart extends AbstractFXContentPart {

	private GeometryNode<IShape> visual;
	private IAnchor<Node> anchor;

	public FXExampleShapePart() {
		visual = new GeometryNode<IShape>();
		installEditPolicy(AbstractSelectionPolicy.class,
				new FXSelectionPolicy());
		installEditPolicy(AbstractResizeRelocatePolicy.class,
				new FXResizeRelocatePolicy());
	}

	@Override
	public FXGeometricShapeVisual getModel() {
		return (FXGeometricShapeVisual) super.getModel();
	}

	@Override
	public void setModel(Object model) {
		if (!(model instanceof FXGeometricShapeVisual)) {
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
		FXGeometricShapeVisual shapeVisual = getModel();
		if (visual.getGeometry() != shapeVisual.geometry) {
			// TODO: respect offset, scaling, etc.
			visual.setGeometry(shapeVisual.geometry);
		}
		if(visual.getEffect() != shapeVisual.effect){
			visual.setEffect(shapeVisual.effect);
		}
		if(visual.getFill() != shapeVisual.fill){
			visual.setFill(shapeVisual.fill);
		}
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	protected List<Object> getModelAnchored() {
		if(getParent() != null){
			List anchored = getModel().anchored;
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
