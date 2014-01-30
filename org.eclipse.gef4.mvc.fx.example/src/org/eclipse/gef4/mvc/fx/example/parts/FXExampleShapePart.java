package org.eclipse.gef4.mvc.fx.example.parts;

import java.util.Collections;
import java.util.List;

import javafx.geometry.Bounds;
import javafx.scene.Node;

import org.eclipse.gef4.geometry.planar.AffineTransform;
import org.eclipse.gef4.geometry.planar.IShape;
import org.eclipse.gef4.mvc.anchors.IAnchor;
import org.eclipse.gef4.mvc.fx.anchors.FXChopBoxAnchor;
import org.eclipse.gef4.mvc.fx.example.model.FXGeometricShape;
import org.eclipse.gef4.mvc.fx.policies.FXHoverFeedbackByEffectPolicy;
import org.eclipse.gef4.mvc.fx.policies.FXResizeRelocatePolicy;
import org.eclipse.gef4.mvc.fx.policies.FXSelectionFeedbackByEffectPolicy;
import org.eclipse.gef4.mvc.parts.IVisualPart;
import org.eclipse.gef4.mvc.policies.AbstractHoverFeedbackPolicy;
import org.eclipse.gef4.mvc.policies.AbstractResizeRelocatePolicy;
import org.eclipse.gef4.mvc.policies.AbstractSelectionFeedbackPolicy;
import org.eclipse.gef4.mvc.policies.IHoverPolicy;
import org.eclipse.gef4.mvc.policies.ISelectionPolicy;
import org.eclipse.gef4.swtfx.GeometryNode;

public class FXExampleShapePart extends AbstractFXExampleElementPart {

	private GeometryNode<IShape> visual;
	private IAnchor<Node> anchor;

	public FXExampleShapePart() {
		visual = new GeometryNode<IShape>();
		installEditPolicy(ISelectionPolicy.class, new ISelectionPolicy.Impl<Node>());
		installEditPolicy(IHoverPolicy.class, new IHoverPolicy.Impl<Node>() {
			@Override
			public boolean isHoverable() {
				return !getHost().getRoot().getViewer().getSelectionModel()
						.getSelected().contains(getHost());
			}
		});
		installEditPolicy(AbstractSelectionFeedbackPolicy.class,
				new FXSelectionFeedbackByEffectPolicy());
		installEditPolicy(AbstractHoverFeedbackPolicy.class,
				new FXHoverFeedbackByEffectPolicy());
		installEditPolicy(AbstractResizeRelocatePolicy.class,
				new FXResizeRelocatePolicy() {
					@Override
					public void commitResizeRelocate(double dx, double dy,
							double dw, double dh) {
						Bounds bounds = visual.getLayoutBounds();
						double width = bounds.getWidth();
						double height = bounds.getHeight();

						double sx = width == 0 ? 1 : (width + dw) / width;
						double sy = height == 0 ? 1 : (height + dh) / height;

						AffineTransform additionalTransform = new AffineTransform(
								sx, 0, 0, sy, dx, dy);

						AffineTransform oldTransform = getModel()
								.getTransform();
						if (oldTransform == null) {
							getModel().setTransform(additionalTransform);
						} else {
							getModel().setTransform(
									oldTransform.getCopy().preConcatenate(
											additionalTransform));
						}
					}
				});
	}

	@Override
	public FXGeometricShape getModel() {
		return (FXGeometricShape) super.getModel();
	}

	@Override
	public void setModel(Object model) {
		if (!(model instanceof FXGeometricShape)) {
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
		FXGeometricShape shapeVisual = getModel();
		if (visual.getGeometry() != shapeVisual.getGeometry()) {
			// TODO: respect offset, scaling, etc.
			if (shapeVisual.getTransform() == null) {
				visual.setGeometry(shapeVisual.getGeometry());
			} else {
				visual.setGeometry(shapeVisual.getGeometry().getTransformed(
						shapeVisual.getTransform()));
			}
		}
		if (visual.getEffect() != shapeVisual.effect) {
			visual.setEffect(shapeVisual.effect);
		}
		if (visual.getFill() != shapeVisual.fill) {
			visual.setFill(shapeVisual.fill);
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	protected List<Object> getModelAnchored() {
		if (getParent() != null) {
			List anchored = getModel().anchored;
			if (anchored == null) {
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
		if (anchor == null) {
			anchor = new FXChopBoxAnchor();
		}
		// register listeners
		anchor.setAnchorage(getVisual());
		return anchor;
	}
}
