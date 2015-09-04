/*******************************************************************************
 * Copyright (c) 2014, 2015 itemis AG and others.
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

import javafx.scene.paint.Color;
import javafx.scene.shape.StrokeType;

/**
 * An {@link AbstractFXSegmentHandlePart} with a
 * {@link javafx.scene.shape.Rectangle} visual.
 *
 * @author mwienand
 *
 */
public class FXRectangleSegmentHandlePart
		extends AbstractFXSegmentHandlePart<javafx.scene.shape.Rectangle> {

	/**
	 * The default size for this part's visualization.
	 */
	public static final double DEFAULT_SIZE = 5;

	/**
	 * The default stroke color for this part's visualization.
	 */
	public static final Color DEFAULT_STROKE = Color.web("#5a61af");

	/**
	 * The default fill color for this part's visualization.
	 */
	public static final Color DEFAULT_FILL = Color.web("#d5faff");

	@Override
	protected javafx.scene.shape.Rectangle createVisual() {
		javafx.scene.shape.Rectangle visual = new javafx.scene.shape.Rectangle();
		visual.setTranslateX(-DEFAULT_SIZE / 2);
		visual.setTranslateY(-DEFAULT_SIZE / 2);
		visual.setFill(DEFAULT_FILL);
		visual.setStroke(DEFAULT_STROKE);
		visual.setWidth(DEFAULT_SIZE);
		visual.setHeight(DEFAULT_SIZE);
		visual.setStrokeWidth(1);
		visual.setStrokeType(StrokeType.OUTSIDE);
		return visual;
	}

}
