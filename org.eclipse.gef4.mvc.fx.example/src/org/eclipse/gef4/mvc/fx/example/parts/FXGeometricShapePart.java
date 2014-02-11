package org.eclipse.gef4.mvc.fx.example.parts;

import java.util.Collections;
import java.util.List;

import javafx.geometry.Bounds;
import javafx.scene.Node;

import org.eclipse.gef4.fx.nodes.FXGeometryNode;
import org.eclipse.gef4.geometry.convert.fx.JavaFX2Geometry;
import org.eclipse.gef4.geometry.planar.AffineTransform;
import org.eclipse.gef4.geometry.planar.IShape;
import org.eclipse.gef4.geometry.planar.Point;
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

public class FXGeometricShapePart extends AbstractFXGeometricElementPart {

	private FXGeometryNode<IShape> visual;
	private IAnchor<Node> anchor;

	public FXGeometricShapePart() {
		visual = new FXGeometryNode<IShape>() {
			@Override
			public void resize(double width, double height) {
				if (isResizable()) {
					super.resize(width, height);
				} else {
					// TODO: this is duplicate code, share the transform with
					// the visual, and only update the transform here
					Bounds bounds = getLayoutBounds();
					double sx = width / bounds.getWidth();
					double sy = height / bounds.getHeight();
					Point start = new Point(bounds.getMinX(), bounds.getMinY());
					Point[] p = new Point[] { start.getCopy() };
					Point.scale(p, sx, sy, 0, 0);
					AffineTransform additionalTransform = new AffineTransform()
							.scale(sx, sy).translate(-p[0].x + start.x,
									-p[0].y + start.y);
					setGeometry(getGeometry().getTransformed(
							additionalTransform));
				}
				updatePathElements();
			}
		};
		installPolicy(ISelectionPolicy.class, new ISelectionPolicy.Impl<Node>());
		installPolicy(IHoverPolicy.class, new IHoverPolicy.Impl<Node>() {
			@Override
			public boolean isHoverable() {
				return !getHost().getRoot().getViewer().getSelectionModel()
						.getSelected().contains(getHost());
			}
		});
		installPolicy(AbstractSelectionFeedbackPolicy.class,
				new FXSelectionFeedbackByEffectPolicy());
		installPolicy(AbstractHoverFeedbackPolicy.class,
				new FXHoverFeedbackByEffectPolicy());
		installPolicy(AbstractResizeRelocatePolicy.class,
				new FXResizeRelocatePolicy() {
					@Override
					public void commitResizeRelocate(double dx, double dy,
							double dw, double dh) {
						// Bounds bounds = visual.getLayoutBounds();
						// double width = bounds.getWidth();
						// double height = bounds.getHeight();
						//
						// double sx = width == 0 ? 1 : (width + dw) / width;
						// double sy = height == 0 ? 1 : (height + dh) / height;
						//
						// AffineTransform additionalTransform = new
						// AffineTransform(
						// sx, 0, 0, sy, dx, dy);
						//
						// AffineTransform oldTransform = getContent()
						// .getTransform();
						// if (oldTransform == null) {
						// getContent().setTransform(additionalTransform);
						// } else {
						// getContent().setTransform(
						// oldTransform.getCopy().preConcatenate(
						// additionalTransform));
						// }
					}
				});
	}

	@Override
	public FXGeometricShape getContent() {
		return (FXGeometricShape) super.getContent();
	}

	@Override
	public void setContent(Object model) {
		if (!(model instanceof FXGeometricShape)) {
			throw new IllegalArgumentException(
					"Only IShape models are supported.");
		}
		super.setContent(model);
	}

	@Override
	public FXGeometryNode<IShape> getVisual() {
		return (FXGeometryNode<IShape>) visual;
	}

	@Override
	public void refreshVisual() {
		FXGeometricShape shapeVisual = getContent();
		if (visual.getGeometry() != shapeVisual.getGeometry()) {
			// TODO: respect offset, scaling, etc.
			if (shapeVisual.getTransform() == null) {
				visual.setGeometry(shapeVisual.getGeometry());
			} else {
				visual.setGeometry(shapeVisual.getGeometry().getTransformed(
						shapeVisual.getTransform()));
			}
		}

		// apply stroke paint
		if (visual.getStroke() != shapeVisual.getStroke()) {
			visual.setStroke(shapeVisual.getStroke());
		}

		// stroke width
		if (visual.getStrokeWidth() != shapeVisual.getStrokeWidth()) {
			visual.setStrokeWidth(shapeVisual.getStrokeWidth());
		}

		if (visual.getFill() != shapeVisual.fill) {
			visual.setFill(shapeVisual.fill);
		}

		// apply effect
		super.refreshVisual();
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public List<Object> getContentAnchored() {
		if (getParent() != null) {
			List anchored = getContent().getAnchoreds();
			if (anchored == null) {
				return Collections.emptyList();
			}
			return anchored;
		}
		return super.getContentAnchored();
	}

	@Override
	public void attachVisualToAnchorageVisual(Node anchorageVisual,
			IAnchor<Node> anchor) {
	}

	@Override
	public void detachVisualFromAnchorageVisual(Node anchorageVisual,
			IAnchor<Node> anchor) {
	}

	@Override
	protected IAnchor<Node> getAnchor(IVisualPart<Node> anchored) {
		if (anchor == null) {
			// TODO: when to dispose the anchor properly??
			anchor = new FXChopBoxAnchor(getVisual()) {
				@Override
				protected IShape getAnchorageReferenceShape() {
					// return the visual's geometry, translated to scene
					// coordinates
					AffineTransform local2SceneAffineTransform = JavaFX2Geometry
							.toAffineTransform(getAnchorage()
									.getLocalToSceneTransform());
					return getVisual().getGeometry().getTransformed(
							local2SceneAffineTransform);
				}
			};
		}
		return anchor;
	}
}
