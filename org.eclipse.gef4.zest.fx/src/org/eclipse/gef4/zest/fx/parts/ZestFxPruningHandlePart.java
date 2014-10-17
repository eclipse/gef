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

import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polygon;

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
	protected StackPane createVisual() {
		StackPane stackPane = new StackPane();
		Circle shape = new Circle(10);
		shape.setStroke(Color.BLUE);
		shape.setFill(Color.WHITE);
		Polygon plus = new Polygon(-15, -2, -15, 2, -2, 2, -2, 15, 2, 15, 2, 2,
				15, 2, 15, -2, 2, -2, 2, -15, -2, -15, -2, -2);
		plus.setStroke(Color.BLACK);
		plus.setFill(Color.GREEN);
		stackPane.getChildren().addAll(shape, plus);
		return stackPane;
	}

	@Override
	public void doRefreshVisual() {
		// TODO: animate visibility by fading in/out
		super.doRefreshVisual();
	}

	@Override
	public StackPane getVisual() {
		return (StackPane) super.getVisual();
	}

}