/*******************************************************************************
 * Copyright (c) 2014 itemis AG and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Matthias Wienand (itemis AG) - initial API & implementation
 *
 *******************************************************************************/
package org.eclipse.gef4.zest.fx.parts;

import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

import org.eclipse.gef4.common.adapt.AdapterKey;
import org.eclipse.gef4.geometry.planar.BezierCurve;
import org.eclipse.gef4.mvc.fx.parts.FXSegmentHandlePart;
import org.eclipse.gef4.mvc.policies.HoverPolicy;
import org.eclipse.gef4.zest.fx.policies.NoHoverPolicy;

import com.google.inject.Provider;

public class ZestFxPruningHandlePart extends FXSegmentHandlePart {

	public ZestFxPruningHandlePart(
			Provider<BezierCurve[]> segmentsInSceneProvider, int segmentIndex,
			double segmentParameter) {
		super(segmentsInSceneProvider, segmentIndex, segmentParameter);
		setAdapter(AdapterKey.get(HoverPolicy.class), new NoHoverPolicy());
	}

	@Override
	protected Circle createVisual() {
		Circle circle = (Circle) super.createVisual();
		circle.setRadius(10);
		circle.setFill(Color.BLUEVIOLET);
		return circle;
	}

	@Override
	public void doRefreshVisual() {
		// TODO: animate visibility by fading in/out
		super.doRefreshVisual();
	}

	@Override
	public Circle getVisual() {
		return (Circle) super.getVisual();
	}

}