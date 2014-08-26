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

import org.eclipse.gef4.geometry.planar.IGeometry;

import com.google.inject.Provider;

public class FXHoverFeedbackPart extends AbstractFXGeometricFeedbackPart {

	private final Provider<IGeometry> hoverFeedbackGeometryProvider;

	public FXHoverFeedbackPart(Provider<IGeometry> hoverFeedbackGeometryProvider) {
		this.hoverFeedbackGeometryProvider = hoverFeedbackGeometryProvider;
	}

	@Override
	public void doRefreshVisual() {
		if (getAnchorages().isEmpty()) {
			return;
		}
		super.doRefreshVisual();
		getVisual().setEffect(getHoverFeedbackEffect());
		getVisual().setStroke(Color.web("#5a61af"));
	}

	@Override
	protected Provider<IGeometry> getFeedbackGeometryProvider() {
		return hoverFeedbackGeometryProvider;
	}

	public Effect getHoverFeedbackEffect() {
		DropShadow effect = new DropShadow();
		effect.setRadius(3);
		return effect;
	}

}
