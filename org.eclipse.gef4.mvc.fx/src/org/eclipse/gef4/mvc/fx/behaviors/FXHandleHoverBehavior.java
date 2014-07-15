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
package org.eclipse.gef4.mvc.fx.behaviors;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javafx.scene.Node;
import javafx.scene.effect.Effect;
import javafx.scene.effect.Light;
import javafx.scene.effect.Lighting;

import org.eclipse.gef4.geometry.planar.IGeometry;
import org.eclipse.gef4.mvc.behaviors.AbstractHoverBehavior;
import org.eclipse.gef4.mvc.parts.IVisualPart;

/**
 * The {@link FXHandleHoverBehavior} can be used to generate hover feedback for
 * handle parts. As handle parts are on the top layer (per default), normal
 * feedback parts would be rendered behind them. Therefore, this behavior uses a
 * JavaFX {@link Effect} as feedback.
 * 
 * @author mwienand
 * 
 */
public class FXHandleHoverBehavior extends AbstractHoverBehavior<Node> {

	private Effect hoverEffect = null;
	private final Map<IVisualPart<Node>, Effect> effects = new HashMap<IVisualPart<Node>, Effect>();

	@Override
	protected void addFeedback(List<? extends IVisualPart<Node>> targets) {
		for (IVisualPart<Node> part : targets) {
			Node visual = part.getVisual();
			effects.put(part, visual.getEffect());
			visual.setEffect(getHoverEffect());
		}
	}

	@Override
	protected void addHandles(List<? extends IVisualPart<Node>> targets) {
		// do not create handles
	}

	@Override
	protected IGeometry getFeedbackGeometry() {
		return null;
	}

	protected Effect getHoverEffect() {
		if (hoverEffect != null) {
			return hoverEffect;
		}

		// create hover effect
		Light.Distant light = new Light.Distant();
		light.setAzimuth(-135.0);
		final Lighting lighting = new Lighting();
		lighting.setLight(light);
		lighting.setSpecularConstant(0);
		lighting.setSurfaceScale(3);
		hoverEffect = lighting;
		return lighting;
	}

	@Override
	protected void removeFeedback(
			java.util.List<? extends IVisualPart<Node>> targets) {
		for (IVisualPart<Node> part : targets) {
			Node visual = part.getVisual();
			visual.setEffect(effects.get(part));
		}
		effects.clear();
	}

	@Override
	protected void removeHandles(List<? extends IVisualPart<Node>> targets) {
		// no handles to remove
	}

}
