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
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.StrokeType;

import org.eclipse.gef4.fx.nodes.FXUtils;
import org.eclipse.gef4.geometry.planar.IGeometry;
import org.eclipse.gef4.geometry.planar.Point;
import org.eclipse.gef4.geometry.planar.Rectangle;

import com.google.inject.Provider;

/**
 * @author mwienand
 * @author anyssen
 *
 */
public class FXCornerHandlePart extends AbstractFXHandlePart implements
Comparable<FXCornerHandlePart> {

	private javafx.scene.shape.Rectangle visual = null;
	private final Provider<IGeometry> handleGeometryProvider;
	private final Pos pos;

	public FXCornerHandlePart(Provider<IGeometry> handleGeometryProvider,
			Pos pos) {
		this.handleGeometryProvider = handleGeometryProvider;
		this.pos = pos;
		visual = new javafx.scene.shape.Rectangle();
		visual.setFill(Color.web("#d5faff"));
		visual.setStroke(Color.web("#5a61af"));
		visual.setWidth(5);
		visual.setHeight(5);
		visual.setStrokeWidth(1);
		visual.setStrokeType(StrokeType.OUTSIDE);
	}

	@Override
	public int compareTo(FXCornerHandlePart o) {
		// if we are bound to the same anchorages, we may compare positions,
		// otherwise we are not comparable
		if (!getAnchorages().equals(o.getAnchorages())) {
			throw new IllegalArgumentException(
					"Can only compare FXBoxHandles that are bound to the same anchorages.");
		}
		return pos.compareTo(o.pos);
	}

	@Override
	public void doRefreshVisual() {
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

	protected Rectangle getHandleGeometry() {
		// TODO: we have to ensure we can also work with rotated rectangles
		// (i.e. polygons) properly (i.e. place the handles in the rotated end
		// point locations)
		return FXUtils.sceneToLocal(getVisual().getParent(),
				handleGeometryProvider.get()).getBounds();
	}

	public Pos getPos() {
		return pos;
	}

	@Override
	public Node getVisual() {
		return visual;
	}

	protected double getXInset() {
		double xInset = visual.getWidth() / 2.0;
		return xInset;
	}

	protected double getYInset() {
		double yInset = visual.getHeight() / 2.0;
		return yInset;
	}

}
