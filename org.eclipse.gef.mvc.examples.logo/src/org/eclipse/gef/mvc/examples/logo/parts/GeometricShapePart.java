/*******************************************************************************
 * Copyright (c) 2014, 2017 itemis AG and others.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Alexander Nyßen (itemis AG) - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.gef.mvc.examples.logo.parts;

import java.util.Collections;
import java.util.List;

import org.eclipse.gef.fx.nodes.GeometryNode;
import org.eclipse.gef.geometry.convert.fx.FX2Geometry;
import org.eclipse.gef.geometry.convert.fx.Geometry2FX;
import org.eclipse.gef.geometry.planar.AffineTransform;
import org.eclipse.gef.geometry.planar.Dimension;
import org.eclipse.gef.geometry.planar.IGeometry;
import org.eclipse.gef.geometry.planar.IScalable;
import org.eclipse.gef.geometry.planar.IShape;
import org.eclipse.gef.geometry.planar.Rectangle;
import org.eclipse.gef.mvc.examples.logo.model.AbstractGeometricElement;
import org.eclipse.gef.mvc.examples.logo.model.GeometricShape;
import org.eclipse.gef.mvc.fx.parts.IResizableContentPart;
import org.eclipse.gef.mvc.fx.parts.ITransformableContentPart;
import org.eclipse.gef.mvc.fx.parts.IVisualPart;
import org.eclipse.gef.mvc.fx.viewer.InfiniteCanvasViewer;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.SetMultimap;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.StrokeType;
import javafx.scene.transform.Affine;
import javafx.scene.transform.Transform;

public class GeometricShapePart extends AbstractGeometricElementPart<GeometryNode<IShape>>
		implements ITransformableContentPart<GeometryNode<IShape>>, IResizableContentPart<GeometryNode<IShape>> {

	private final ChangeListener<? super Paint> fillObserver = new ChangeListener<Paint>() {
		@Override
		public void changed(ObservableValue<? extends Paint> observable, Paint oldValue, Paint newValue) {
			refreshVisual();
		}
	};
	private final boolean debugging = false;
	private javafx.scene.shape.Rectangle layoutBoundsRect;

	@Override
	protected void doActivate() {
		super.doActivate();
		getContent().fillProperty().addListener(fillObserver);
	}

	@Override
	protected void doAddContentChild(Object contentChild, int index) {
		// nothing to do
	}

	@Override
	protected void doAttachToAnchorageVisual(org.eclipse.gef.mvc.fx.parts.IVisualPart<? extends Node> anchorage,
			String role) {
		// nothing to do
	}

	@Override
	protected void doAttachToContentAnchorage(Object contentAnchorage, String role) {
		if (!(contentAnchorage instanceof AbstractGeometricElement)) {
			throw new IllegalArgumentException("Cannot attach to content anchorage: wrong type!");
		}
		getContent().getAnchorages().add((AbstractGeometricElement<?>) contentAnchorage);
	}

	@Override
	protected GeometryNode<IShape> doCreateVisual() {
		GeometryNode<IShape> geometryNode = new GeometryNode<>();
		if (debugging) {
			layoutBoundsRect = new javafx.scene.shape.Rectangle();
			layoutBoundsRect.setStrokeType(StrokeType.CENTERED);
			layoutBoundsRect.setFill(null);
			layoutBoundsRect.setStroke(Color.RED);
			layoutBoundsRect.setStrokeWidth(0.5);
			((InfiniteCanvasViewer) getRoot().getViewer()).getCanvas().getScrolledOverlayGroup().getChildren()
					.add(layoutBoundsRect);
			geometryNode.layoutBoundsProperty().addListener(new ChangeListener<Bounds>() {
				@Override
				public void changed(javafx.beans.value.ObservableValue<? extends Bounds> observable, Bounds oldValue,
						Bounds newValue) {
					updateLayoutBoundsRect(geometryNode);
				}
			});
			geometryNode.localToSceneTransformProperty().addListener(new ChangeListener<Transform>() {
				@Override
				public void changed(javafx.beans.value.ObservableValue<? extends Transform> observable,
						Transform oldValue, Transform newValue) {
					updateLayoutBoundsRect(geometryNode);
				}
			});
		}
		return geometryNode;
	}

	@Override
	protected void doDeactivate() {
		getContent().fillProperty().removeListener(fillObserver);
		super.doDeactivate();
	}

	@Override
	protected void doDetachFromAnchorageVisual(IVisualPart<? extends Node> anchorage, String role) {
		// nothing to do
	}

	@Override
	protected void doDetachFromContentAnchorage(Object contentAnchorage, String role) {
		getContent().getAnchorages().remove(contentAnchorage);
	}

	@Override
	protected SetMultimap<? extends Object, String> doGetContentAnchorages() {
		SetMultimap<Object, String> anchorages = HashMultimap.create();
		for (AbstractGeometricElement<? extends IGeometry> anchorage : getContent().getAnchorages()) {
			anchorages.put(anchorage, "link");
		}
		return anchorages;
	}

	@Override
	protected List<? extends Object> doGetContentChildren() {
		return Collections.emptyList();
	}

	@Override
	protected void doRefreshVisual(GeometryNode<IShape> visual) {
		GeometricShape content = getContent();

		if (visual.getGeometry() != content.getGeometry()) {
			visual.setGeometry(content.getGeometry());
		}

		AffineTransform transform = content.getTransform();
		if (transform != null) {
			setVisualTransform(Geometry2FX.toFXAffine(transform));
		}

		// apply stroke paint
		if (visual.getStroke() != content.getStroke()) {
			visual.setStroke(content.getStroke());
		}

		// stroke width
		if (visual.getStrokeWidth() != content.getStrokeWidth()) {
			visual.setStrokeWidth(content.getStrokeWidth());
		}

		if (visual.getFill() != content.getFill()) {
			visual.setFill(content.getFill());
		}

		// apply effect
		super.doRefreshVisual(visual);
	}

	@Override
	protected void doRemoveContentChild(Object contentChild) {
		// nothing to do
	}

	@Override
	protected void doReorderContentChild(Object contentChild, int newIndex) {
	}

	@Override
	public GeometricShape getContent() {
		return (GeometricShape) super.getContent();
	}

	@Override
	public Dimension getContentSize() {
		return getContent().getGeometry().getBounds().getSize();
	}

	@Override
	public Affine getContentTransform() {
		return Geometry2FX.toFXAffine(getContent().getTransform());
	}

	@Override
	public void setContent(Object model) {
		if (model != null && !(model instanceof GeometricShape)) {
			throw new IllegalArgumentException("Only IShape models are supported.");
		}
		super.setContent(model);
	}

	@Override
	public void setContentSize(Dimension size) {
		IShape geometry = getContent().getGeometry();
		Rectangle geometricBounds = geometry.getBounds();
		// XXX: The given <i>size</i> contains the stroke of the underlying
		// geometry, therefore, we need to subtract the stroke width from both
		// width and height (actually this depends on the stroke type (which is
		// centered, per default).
		double sx = (size.width - getContent().getStrokeWidth()) / geometricBounds.getWidth();
		double sy = (size.height - getContent().getStrokeWidth()) / geometricBounds.getHeight();
		((IScalable<?>) geometry).scale(sx, sy, geometricBounds.getX(), geometricBounds.getY());
	}

	@Override
	public void setContentTransform(Affine totalTransform) {
		getContent().setTransform(FX2Geometry.toAffineTransform(totalTransform));
	}

	private void updateLayoutBoundsRect(GeometryNode<IShape> geometryNode) {
		Bounds boundsInScene = geometryNode.localToScene(geometryNode.getLayoutBounds());
		Bounds boundsInParent = ((InfiniteCanvasViewer) getRoot().getViewer()).getCanvas().getScrolledOverlayGroup()
				.sceneToLocal(boundsInScene);
		layoutBoundsRect.setX(boundsInParent.getMinX());
		layoutBoundsRect.setY(boundsInParent.getMinY());
		layoutBoundsRect.setWidth(boundsInParent.getWidth());
		layoutBoundsRect.setHeight(boundsInParent.getHeight());
	}

}