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
package org.eclipse.gef4.mvc.fx.parts;

import java.awt.geom.NoninvertibleTransformException;

import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.StrokeType;

import org.eclipse.gef4.fx.nodes.FXGeometryNode;
import org.eclipse.gef4.geometry.convert.fx.JavaFX2Geometry;
import org.eclipse.gef4.geometry.planar.AffineTransform;
import org.eclipse.gef4.geometry.planar.IGeometry;

import com.google.inject.Provider;

/**
 * A handle part used for showing feedback based on layout bounds of an
 * underlying target part
 * 
 * @author nyssen
 * 
 */
public class FXGeometricFeedbackPart extends AbstractFXFeedbackPart {

	private final Provider<IGeometry> feedbackGeometryProvider;
	private final FXGeometryNode<IGeometry> feedbackVisual;

	public FXGeometricFeedbackPart(Provider<IGeometry> feedbackGeometryProvider) {
		this.feedbackGeometryProvider = feedbackGeometryProvider;
		feedbackVisual = new FXGeometryNode<IGeometry>(
				feedbackGeometryProvider.get());
		feedbackVisual.setFill(Color.TRANSPARENT);
		feedbackVisual.setMouseTransparent(true);
		feedbackVisual.setManaged(false);
		feedbackVisual.setStrokeType(StrokeType.OUTSIDE);
		feedbackVisual.setStrokeWidth(1);
	}

	@Override
	public void doRefreshVisual() {
		if (getAnchorages().size() == 0) {
			return;
		}

		// we need to combine several transformations to get the
		// target-to-(feedback-handle-)parent-transform
		Node targetVisual = getAnchorages().get(0).getVisual();
		AffineTransform targetToSceneTx = JavaFX2Geometry
				.toAffineTransform(targetVisual.getLocalToSceneTransform());
		AffineTransform parentToSceneTx = JavaFX2Geometry
				.toAffineTransform(feedbackVisual.getParent()
						.getLocalToSceneTransform());

		// invert parentToSceneTx
		AffineTransform sceneToParentTx = null;
		try {
			sceneToParentTx = parentToSceneTx.getCopy().invert();
		} catch (NoninvertibleTransformException e) {
			// TODO: How do we recover from this?!
			throw new IllegalStateException(e);
		}

		// transform feedback geometry from target space to local parent space
		AffineTransform targetToParentTx = targetToSceneTx
				.preConcatenate(sceneToParentTx);
		IGeometry feedbackGeometry = feedbackGeometryProvider.get();
		feedbackVisual.setGeometry(feedbackGeometry
				.getTransformed(targetToParentTx));
	}

	@Override
	public FXGeometryNode<IGeometry> getVisual() {
		return feedbackVisual;
	}

}