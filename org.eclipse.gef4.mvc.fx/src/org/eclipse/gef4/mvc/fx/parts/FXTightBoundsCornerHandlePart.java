/*******************************************************************************
 * Copyright (c) 2015 itemis AG and others.
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

import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Transform;

import org.eclipse.gef4.geometry.planar.BezierCurve;
import org.eclipse.gef4.mvc.parts.IVisualPart;

import com.google.common.collect.SetMultimap;
import com.google.inject.Provider;

public class FXTightBoundsCornerHandlePart extends
		AbstractFXCornerHandlePart<Rectangle> {

	public static final double DEFAULT_SIZE = 5d;
	public static final Color DEFAULT_STROKE = Color.web("#5a61af");
	public static final Color DEFAULT_FILL = Color.web("#d5faff");

	public FXTightBoundsCornerHandlePart(
			Provider<BezierCurve[]> segmentsProvider, int segmentIndex,
			double segmentParameter, Pos position) {
		super(segmentsProvider, segmentIndex, segmentParameter, position);
	}

	@Override
	protected Rectangle createVisual() {
		Rectangle rectangle = new Rectangle(0, 0, DEFAULT_SIZE, DEFAULT_SIZE);
		rectangle.setTranslateX(-DEFAULT_SIZE / 2d);
		rectangle.setTranslateY(-DEFAULT_SIZE / 2d);
		rectangle.setFill(DEFAULT_FILL);
		rectangle.setStroke(DEFAULT_STROKE);
		return rectangle;
	}

	@Override
	public void doRefreshVisual(Rectangle visual) {
		super.doRefreshVisual(visual);
		updateOrientation();
	}

	protected void updateOrientation() {
		SetMultimap<IVisualPart<Node, ? extends Node>, String> anchorages = getAnchorages();
		if (anchorages.isEmpty()) {
			return;
		}
		IVisualPart<Node, ? extends Node> anchorage = anchorages.keySet()
				.iterator().next();
		ObservableList<Transform> transforms = anchorage.getVisual()
				.getTransforms();
		if (transforms.size() > 0) {
			Transform lastTx = transforms.get(transforms.size() - 1);
			if (lastTx instanceof Rotate) {
				double angle = ((Rotate) lastTx).getAngle();
				getVisual().setRotate(angle);
			}
		}
	}

}
