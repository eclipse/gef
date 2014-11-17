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

import org.eclipse.gef4.geometry.planar.IGeometry;
import org.eclipse.gef4.geometry.planar.Point;
import org.eclipse.gef4.geometry.planar.Rectangle;

import com.google.inject.Provider;

/**
 * @author mwienand
 * @author anyssen
 *
 */
public class FXCornerHandlePart extends
		AbstractFXCornerHandlePart<javafx.scene.shape.Rectangle> {

	public FXCornerHandlePart(Provider<IGeometry> handleGeometryProvider,
			Pos pos) {
		super(handleGeometryProvider, pos);
	}

	@Override
	protected javafx.scene.shape.Rectangle createVisual() {
		javafx.scene.shape.Rectangle visual = new javafx.scene.shape.Rectangle();
		visual.setFill(Color.web("#d5faff"));
		visual.setStroke(Color.web("#5a61af"));
		visual.setWidth(5);
		visual.setHeight(5);
		visual.setStrokeWidth(1);
		visual.setStrokeType(StrokeType.OUTSIDE);
		return visual;
	}

	@Override
	public void doRefreshVisual(javafx.scene.shape.Rectangle visual) {
		updateLocation(visual);
	}

	protected void updateLocation(javafx.scene.shape.Rectangle visual) {
		Rectangle handleGeometry = getHandleGeometry();

		if (handleGeometry != null) {
			double xInset = getXInset();
			double yInset = getYInset();

			Point topLeft = handleGeometry.getTopLeft();
			Point topRight = handleGeometry.getTopRight();
			Point bottomRight = handleGeometry.getBottomRight();
			Point bottomLeft = handleGeometry.getBottomLeft();

			if (Pos.TOP_LEFT == getPos()) {
				visual.setLayoutX(topLeft.x - xInset);
				visual.setLayoutY(topLeft.y - yInset);
			} else if (Pos.TOP_RIGHT == getPos()) {
				visual.setLayoutX(topRight.x - xInset);
				visual.setLayoutY(topRight.y - yInset);
			} else if (Pos.BOTTOM_RIGHT == getPos()) {
				visual.setLayoutX(bottomRight.x - xInset);
				visual.setLayoutY(bottomRight.y - yInset);
			} else if (Pos.BOTTOM_LEFT == getPos()) {
				visual.setLayoutX(bottomLeft.x - xInset);
				visual.setLayoutY(bottomLeft.y - yInset);
			} else {
				throw new IllegalArgumentException(
						"Unsupported position constant.");
			}
		}
	}

}
