/*******************************************************************************
 * Copyright (c) 2014, 2015 itemis AG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Alexander Nyßen (itemis AG) - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.gef4.mvc.examples.logo.parts;

import java.util.Collections;
import java.util.List;

import org.eclipse.gef4.fx.nodes.GeometryNode;
import org.eclipse.gef4.geometry.planar.AffineTransform;
import org.eclipse.gef4.geometry.planar.IGeometry;
import org.eclipse.gef4.geometry.planar.IShape;
import org.eclipse.gef4.mvc.examples.logo.model.AbstractFXGeometricElement;
import org.eclipse.gef4.mvc.examples.logo.model.FXGeometricShape;
import org.eclipse.gef4.mvc.fx.policies.FXTransformPolicy;
import org.eclipse.gef4.mvc.parts.IVisualPart;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.SetMultimap;

import javafx.scene.Node;
import javafx.scene.transform.Affine;

public class FXGeometricShapePart
		extends AbstractFXGeometricElementPart<GeometryNode<IShape>> {

	@Override
	protected void attachToAnchorageVisual(
			org.eclipse.gef4.mvc.parts.IVisualPart<Node, ? extends Node> anchorage,
			String role) {
		// nothing to do
	}

	@Override
	protected GeometryNode<IShape> createVisual() {
		return new GeometryNode<IShape>();
	}

	@Override
	protected void detachFromAnchorageVisual(
			IVisualPart<Node, ? extends Node> anchorage, String role) {
		// nothing to do
	}

	@Override
	protected void doAddContentChild(Object contentChild, int index) {
		// nothing to do
	}

	@Override
	public void doAttachToContentAnchorage(Object contentAnchorage,
			String role) {
		if (!(contentAnchorage instanceof AbstractFXGeometricElement)) {
			throw new IllegalArgumentException(
					"Cannot attach to content anchorage: wrong type!");
		}
		getContent().getAnchorages()
				.add((AbstractFXGeometricElement<?>) contentAnchorage);
	}

	@Override
	public void doDetachFromContentAnchorage(Object contentAnchorage,
			String role) {
		getContent().getAnchorages().remove(contentAnchorage);
	}

	@Override
	public void doRefreshVisual(GeometryNode<IShape> visual) {
		FXGeometricShape content = getContent();

		if (visual.getGeometry() != content.getGeometry()) {
			visual.setGeometry(content.getGeometry());
		}

		AffineTransform transform = content.getTransform();
		if (transform != null) {
			// transfer transformation to JavaFX
			Affine affine = getAdapter(FXTransformPolicy.TRANSFORM_PROVIDER_KEY)
					.get();
			affine.setMxx(transform.getM00());
			affine.setMxy(transform.getM01());
			affine.setMyx(transform.getM10());
			affine.setMyy(transform.getM11());
			affine.setTx(transform.getTranslateX());
			affine.setTy(transform.getTranslateY());
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
	public List<? extends Object> getContentChildren() {
		return Collections.emptyList();
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
