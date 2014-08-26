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

import javafx.scene.paint.Color;
import javafx.scene.shape.StrokeType;

import org.eclipse.gef4.fx.nodes.FXGeometryNode;
import org.eclipse.gef4.geometry.planar.IGeometry;

/**
 * A handle part used for showing feedback based on layout bounds of an
 * underlying target part
 *
 * @author nyssen
 *
 */
// TODO: maybe this class can be removed -> not much communality
public abstract class AbstractFXBoundsFeedbackPart extends
AbstractFXFeedbackPart {

	private FXGeometryNode<IGeometry> feedbackVisual;

	protected FXGeometryNode<IGeometry> createFeedbackVisual() {
		FXGeometryNode<IGeometry> feedbackVisual = new FXGeometryNode<IGeometry>();
		feedbackVisual.setFill(Color.TRANSPARENT);
		feedbackVisual.setMouseTransparent(true);
		feedbackVisual.setManaged(false);
		feedbackVisual.setStrokeType(StrokeType.OUTSIDE);
		feedbackVisual.setStrokeWidth(1);
		return feedbackVisual;
	}

	@Override
	public void doRefreshVisual() {
		if (getAnchorages().size() != 1) {
			return;
		}

		IGeometry feedbackGeometry = getFeedbackGeometry();
		if (feedbackGeometry == null) {
			return;
		}

		getVisual().setGeometry(feedbackGeometry);
	}

	protected abstract IGeometry getFeedbackGeometry();

	@Override
	public FXGeometryNode<IGeometry> getVisual() {
		if (feedbackVisual == null) {
			feedbackVisual = createFeedbackVisual();
		}
		return feedbackVisual;
	}
}