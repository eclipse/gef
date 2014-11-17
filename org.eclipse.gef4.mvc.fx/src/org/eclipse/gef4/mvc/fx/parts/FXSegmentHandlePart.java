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

import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.StrokeType;

import org.eclipse.gef4.geometry.planar.BezierCurve;

import com.google.inject.Provider;

/**
 * A {@link FXSegmentHandlePart} is bound to one segment of a handle geometry.
 * The segmentIndex identifies that segment (0, 1, 2, ...). The segmentParameter
 * specifies the position of this handle part on the segment (0 = start, 0.5 =
 * mid, 1 = end).
 *
 * These parts are used for selection feedback per default.
 *
 * @author mwienand
 * @author anyssen
 *
 */
public class FXSegmentHandlePart extends AbstractFXSegmentHandlePart<Circle> {

	public static final Color DEFAULT_STROKE = Color.web("#5a61af");
	public static final Color DEFAULT_FILL = Color.WHITE;
	public static final double DEFAULT_SIZE = 5d;

	public FXSegmentHandlePart(Provider<BezierCurve[]> segmentsProvider,
			int segmentIndex, double segmentParameter) {
		super(segmentsProvider, segmentIndex, segmentParameter);
	}

	/**
	 * Creates the visual representation of this selection handle.
	 *
	 * @return {@link Node} representing the handle visually
	 */
	@Override
	protected Circle createVisual() {
		Circle circle = new Circle(DEFAULT_SIZE / 2d);
		// initialize invariant visual properties
		circle.setStroke(DEFAULT_STROKE);
		circle.setFill(DEFAULT_FILL);
		circle.setStrokeWidth(1);
		circle.setStrokeType(StrokeType.OUTSIDE);
		return circle;
	}

}
