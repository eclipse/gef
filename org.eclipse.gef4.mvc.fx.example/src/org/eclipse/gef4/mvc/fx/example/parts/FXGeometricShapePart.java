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

import javafx.scene.Node;

import org.eclipse.gef4.fx.anchors.FXChopBoxAnchor;
import org.eclipse.gef4.fx.anchors.IFXAnchor;
import org.eclipse.gef4.fx.nodes.FXGeometryNode;
import org.eclipse.gef4.geometry.planar.IGeometry;
import org.eclipse.gef4.geometry.planar.IShape;
import org.eclipse.gef4.mvc.fx.example.model.AbstractFXGeometricElement;
import org.eclipse.gef4.mvc.fx.example.model.FXGeometricShape;
import org.eclipse.gef4.mvc.parts.IVisualPart;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.SetMultimap;

public class FXGeometricShapePart extends AbstractFXGeometricElementPart {

	private final FXGeometryNode<IShape> visual;
	private IFXAnchor anchor;

	public FXGeometricShapePart() {
		visual = new FXGeometryNode<IShape>();
	}

	@Override
	public void attachToContentAnchorage(Object contentAnchorage, String role) {
		if (!(contentAnchorage instanceof AbstractFXGeometricElement)) {
			throw new IllegalArgumentException(
					"Cannot attach to content anchorage: wrong type!");
		}
		getContent().getAnchorages().add(
				(AbstractFXGeometricElement<?>) contentAnchorage);
	}

	@Override
	public void detachFromContentAnchorage(Object contentAnchorage, String role) {
		getContent().getAnchorages().remove(contentAnchorage);
	}

	@Override
	public void doRefreshVisual() {
		FXGeometricShape shapeVisual = getContent();
		if (visual.getGeometry() != shapeVisual.getGeometry()) {
			visual.setGeometry(shapeVisual.getGeometry());
		}

		if (shapeVisual.getTransform() != null) {
			visual.relocate(shapeVisual.getTransform().getTranslateX()
					+ visual.getLayoutBounds().getMinX(), shapeVisual
					.getTransform().getTranslateY()
					+ visual.getLayoutBounds().getMinY());
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
		super.doRefreshVisual();
	}

	@Override
	public IFXAnchor getAnchor(IVisualPart<Node> anchored) {
		if (anchor == null) {
			anchor = new FXChopBoxAnchor(getVisual());
		}
		return anchor;
	}

	@Override
	public FXGeometricShape getContent() {
		return (FXGeometricShape) super.getContent();
	}

	@Override
	public SetMultimap<? extends Object, String> getContentAnchorages() {
		SetMultimap<Object, String> anchorages = HashMultimap.create();
		for (AbstractFXGeometricElement<? extends IGeometry> anchorage : getContent()
				.getAnchorages()) {
			anchorages.put(anchorage, "link");
		}
		return anchorages;
	}

	@Override
	public FXGeometryNode<IShape> getVisual() {
		return visual;
	}

	@Override
	public void setContent(Object model) {
		if (model != null && !(model instanceof FXGeometricShape)) {
			throw new IllegalArgumentException(
					"Only IShape models are supported.");
		}
		super.setContent(model);
	}

}
