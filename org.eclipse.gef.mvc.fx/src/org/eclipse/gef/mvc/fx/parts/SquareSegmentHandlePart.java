/*******************************************************************************
 * Copyright (c) 2014, 2017 itemis AG and others.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Matthias Wienand (itemis AG) - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.gef.mvc.fx.parts;

import javafx.scene.shape.StrokeType;

/**
 * An {@link AbstractSegmentHandlePart} with a quadratic
 * {@link javafx.scene.shape.Rectangle} visual.
 *
 * @author mwienand
 *
 */
public class SquareSegmentHandlePart
		extends AbstractSegmentHandlePart<javafx.scene.shape.Rectangle> {

	/**
	 * The default size for this part's visualization.
	 */
	public static final double DEFAULT_SIZE = 4;

	@Override
	protected javafx.scene.shape.Rectangle doCreateVisual() {
		javafx.scene.shape.Rectangle visual = new javafx.scene.shape.Rectangle();
		visual.setTranslateX(-DEFAULT_SIZE / 2);
		visual.setTranslateY(-DEFAULT_SIZE / 2);
		visual.setFill(getMoveFill());
		visual.setStroke(getStroke());
		visual.setWidth(DEFAULT_SIZE);
		visual.setHeight(DEFAULT_SIZE);
		visual.setStrokeWidth(1);
		visual.setStrokeType(StrokeType.OUTSIDE);
		return visual;
	}
}
