/*******************************************************************************
 * Copyright (c) 2014 itemis AG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Alexander Nyßen (itemis AG) - initial API and implementation
 *     
 *******************************************************************************/
package org.eclipse.gef4.mvc.fx.example.parts;

import java.util.Collections;
import java.util.List;

import javafx.geometry.Bounds;
import javafx.scene.Node;

import org.eclipse.gef4.fx.anchors.FXChopBoxAnchor;
import org.eclipse.gef4.fx.anchors.IFXAnchor;
import org.eclipse.gef4.fx.nodes.FXGeometryNode;
import org.eclipse.gef4.geometry.convert.fx.JavaFX2Geometry;
import org.eclipse.gef4.geometry.planar.AffineTransform;
import org.eclipse.gef4.geometry.planar.IGeometry;
import org.eclipse.gef4.geometry.planar.IShape;
import org.eclipse.gef4.geometry.planar.Point;
import org.eclipse.gef4.mvc.fx.behaviors.FXHoverBehavior;
import org.eclipse.gef4.mvc.fx.behaviors.FXSelectionBehavior;
import org.eclipse.gef4.mvc.fx.example.model.FXGeometricShape;
import org.eclipse.gef4.mvc.fx.policies.FXRelocateOnDragPolicy;
import org.eclipse.gef4.mvc.fx.policies.FXResizeRelocatePolicy;
import org.eclipse.gef4.mvc.fx.tools.FXDragTool;
import org.eclipse.gef4.mvc.parts.IVisualPart;

public class FXGeometricShapePart extends AbstractFXGeometricElementPart {

	private FXGeometryNode<IShape> visual;
	private IFXAnchor anchor;

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
			}
		};
		
		// interaction policies
		setAdapter(FXDragTool.TOOL_POLICY_KEY, new FXRelocateOnDragPolicy());

		// transaction policies
		setAdapter(FXResizeRelocatePolicy.class, new FXResizeRelocatePolicy());
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

		if (visual.getFill() != shapeVisual.getFill()) {
			visual.setFill(shapeVisual.getFill());
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
	public IFXAnchor getAnchor(IVisualPart<Node> anchored) {
		if (anchor == null) {
			// TODO: when to dispose the anchor properly??
			anchor = new FXChopBoxAnchor(getVisual()) {
				@Override
				protected IShape getAnchorageReferenceShape() {
					// return the visual's geometry within the coordinate system
					// of the anchorage Node
					return getVisual().getGeometry();
				}
			};
		}
		return anchor;
	}

}
