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
package org.eclipse.gef4.mvc.examples.logo.parts;

import javafx.scene.Node;
import javafx.scene.transform.Affine;

import org.eclipse.gef4.common.adapt.AdapterKey;
import org.eclipse.gef4.fx.nodes.FXGeometryNode;
import org.eclipse.gef4.geometry.planar.AffineTransform;
import org.eclipse.gef4.geometry.planar.IGeometry;
import org.eclipse.gef4.geometry.planar.IShape;
import org.eclipse.gef4.mvc.examples.logo.model.AbstractFXGeometricElement;
import org.eclipse.gef4.mvc.examples.logo.model.FXGeometricShape;
import org.eclipse.gef4.mvc.fx.policies.FXTransformPolicy;
import org.eclipse.gef4.mvc.parts.IVisualPart;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.SetMultimap;
import com.google.common.reflect.TypeToken;
import com.google.inject.Provider;

public class FXGeometricShapePart extends
		AbstractFXGeometricElementPart<FXGeometryNode<IShape>> {

	@Override
	protected void attachToAnchorageVisual(
			org.eclipse.gef4.mvc.parts.IVisualPart<Node, ? extends Node> anchorage,
			String role) {
		// nothing to do
	};

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
	protected FXGeometryNode<IShape> createVisual() {
		return new FXGeometryNode<IShape>();
	}

	@Override
	protected void detachFromAnchorageVisual(
			IVisualPart<Node, ? extends Node> anchorage, String role) {
		// nothing to do
	}

	@Override
	public void detachFromContentAnchorage(Object contentAnchorage, String role) {
		getContent().getAnchorages().remove(contentAnchorage);
	}

	@Override
	public void doRefreshVisual(FXGeometryNode<IShape> visual) {
		FXGeometricShape content = getContent();

		if (visual.getGeometry() != content.getGeometry()) {
			visual.setGeometry(content.getGeometry());
		}

		AffineTransform transform = content.getTransform();
		if (transform != null) {
			// transfer transformation to JavaFX
			@SuppressWarnings("serial")
			Affine affine = getAdapter(
					AdapterKey.<Provider<? extends Affine>> get(
							new TypeToken<Provider<? extends Affine>>() {
							},
							FXTransformPolicy.TRANSFORMATION_PROVIDER_ROLE))
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
	public void setContent(Object model) {
		if (model != null && !(model instanceof FXGeometricShape)) {
			throw new IllegalArgumentException(
					"Only IShape models are supported.");
		}
		super.setContent(model);
	}

}
