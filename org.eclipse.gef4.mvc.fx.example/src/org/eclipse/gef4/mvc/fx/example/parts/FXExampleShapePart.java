package org.eclipse.gef4.mvc.fx.example.parts;

import java.util.Collections;
import java.util.List;

import javafx.scene.Node;

import org.eclipse.gef4.geometry.planar.IShape;
import org.eclipse.gef4.mvc.anchors.IAnchor;
import org.eclipse.gef4.mvc.fx.anchors.FXChopBoxAnchor;
import org.eclipse.gef4.mvc.fx.example.model.FXGeometricShapeElement;
import org.eclipse.gef4.mvc.fx.parts.AbstractFXContentPart;
import org.eclipse.gef4.mvc.fx.policies.FXHoverFeedbackPolicy;
import org.eclipse.gef4.mvc.fx.policies.FXResizeRelocatePolicy;
import org.eclipse.gef4.mvc.fx.policies.FXSelectionFeedbackPolicy;
import org.eclipse.gef4.mvc.parts.IVisualPart;
import org.eclipse.gef4.mvc.policies.AbstractHandlePolicy;
import org.eclipse.gef4.mvc.policies.AbstractHoverFeedbackPolicy;
import org.eclipse.gef4.mvc.policies.AbstractResizeRelocatePolicy;
import org.eclipse.gef4.mvc.policies.AbstractSelectionFeedbackPolicy;
import org.eclipse.gef4.swtfx.GeometryNode;

public class FXExampleShapePart extends AbstractFXContentPart {

	private GeometryNode<IShape> visual;
	private IAnchor<Node> anchor;

	@SuppressWarnings("unchecked")
	public FXExampleShapePart() {
		visual = new GeometryNode<IShape>();
		installEditPolicy(AbstractSelectionFeedbackPolicy.class,
				new FXSelectionFeedbackPolicy());
		installEditPolicy(AbstractHoverFeedbackPolicy.class,
				new FXHoverFeedbackPolicy());
		installEditPolicy(AbstractResizeRelocatePolicy.class,
				new FXResizeRelocatePolicy());
		installEditPolicy(AbstractHandlePolicy.class, new AbstractHandlePolicy<Node>() {
		});
	}

	@Override
	public FXGeometricShapeElement getModel() {
		return (FXGeometricShapeElement) super.getModel();
	}

	@Override
	public void setModel(Object model) {
		if (!(model instanceof FXGeometricShapeElement)) {
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
		FXGeometricShapeElement shapeVisual = getModel();
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
