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

import javafx.geometry.Pos;
import javafx.scene.paint.Color;
import javafx.scene.shape.StrokeType;

import org.eclipse.gef4.geometry.planar.BezierCurve;

import com.google.inject.Provider;

/**
 * An {@link AbstractFXCornerHandlePart} with a
 * {@link javafx.scene.shape.Rectangle} visual.
 *
 * @author mwienand
 * @author anyssen
 *
 */
public class FXMultiBoundsCornerHandlePart extends
		AbstractFXCornerHandlePart<javafx.scene.shape.Rectangle> {

	public static final double DEFAULT_SIZE = 5;
	public static final Color DEFAULT_STROKE = Color.web("#5a61af");
	public static final Color DEFAULT_FILL = Color.web("#d5faff");

	public FXMultiBoundsCornerHandlePart(
			Provider<BezierCurve[]> segmentsProvider, int segmentIndex,
			double segmentParameter, Pos position) {
		super(segmentsProvider, segmentIndex, segmentParameter, position);
	}

	@Override
	protected javafx.scene.shape.Rectangle createVisual() {
		javafx.scene.shape.Rectangle visual = new javafx.scene.shape.Rectangle();
		visual.setFill(DEFAULT_FILL);
		visual.setStroke(DEFAULT_STROKE);
		visual.setWidth(DEFAULT_SIZE);
		visual.setHeight(DEFAULT_SIZE);
		visual.setStrokeWidth(1);
		visual.setStrokeType(StrokeType.OUTSIDE);
		return visual;
	}

}
