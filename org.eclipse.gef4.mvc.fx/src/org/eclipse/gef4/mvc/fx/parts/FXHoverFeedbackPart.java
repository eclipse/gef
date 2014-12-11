/*******************************************************************************
 * Copyright (c) 2014 itemis AG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Matthias Wienand (itemis AG) - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.gef4.mvc.fx.parts;

import javafx.scene.effect.DropShadow;
import javafx.scene.effect.Effect;
import javafx.scene.paint.Color;
import javafx.scene.shape.StrokeType;

import org.eclipse.gef4.fx.nodes.FXGeometryNode;
import org.eclipse.gef4.fx.nodes.FXUtils;
import org.eclipse.gef4.geometry.planar.ICurve;
import org.eclipse.gef4.geometry.planar.IGeometry;

import com.google.inject.Provider;

public class FXHoverFeedbackPart extends
		AbstractFXFeedbackPart<FXGeometryNode<IGeometry>> {

	public static final Color DEFAULT_STROKE = Color.web("#5a61af");

	private final Provider<? extends IGeometry> feedbackGeometryProvider;

	public FXHoverFeedbackPart(
			Provider<? extends IGeometry> feedbackGeometryProvider) {
		this.feedbackGeometryProvider = feedbackGeometryProvider;
	}

	@Override
	protected FXGeometryNode<IGeometry> createVisual() {
		FXGeometryNode<IGeometry> visual = new FXGeometryNode<IGeometry>();
		visual.setFill(Color.TRANSPARENT);
		visual.setMouseTransparent(true);
		visual.setManaged(false);
		visual.setStrokeType(StrokeType.OUTSIDE);
		visual.setStrokeWidth(1);

		// hover specific
		visual.setEffect(getHoverFeedbackEffect());
		visual.setStroke(DEFAULT_STROKE);
		return visual;
	}

	@Override
	public void doRefreshVisual(FXGeometryNode<IGeometry> visual) {
		if (getAnchorages().size() != 1) {
			return;
		}

		IGeometry feedbackGeometry = getFeedbackGeometry();
		if (feedbackGeometry == null) {
			return;
		}

		visual.setGeometry(feedbackGeometry);

		if (feedbackGeometry instanceof ICurve) {
			// stroke centered
			visual.setStrokeType(StrokeType.CENTERED);
		} else {
			// stroke outside
			visual.setStrokeType(StrokeType.OUTSIDE);
		}
	}

	protected IGeometry getFeedbackGeometry() {
		return FXUtils
				.sceneToLocal(getVisual(), feedbackGeometryProvider.get());
	}

	public Effect getHoverFeedbackEffect() {
		DropShadow effect = new DropShadow();
		effect.setRadius(3);
		return effect;
	}

}
