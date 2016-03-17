/*******************************************************************************
 * Copyright (c) 2016 itemis AG and others.
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

import org.eclipse.gef4.fx.nodes.Connection;
import org.eclipse.gef4.geometry.euclidean.Angle;
import org.eclipse.gef4.geometry.euclidean.Vector;
import org.eclipse.gef4.geometry.planar.BezierCurve;
import org.eclipse.gef4.geometry.planar.Point;
import org.eclipse.gef4.mvc.parts.IVisualPart;

import com.google.common.collect.SetMultimap;

import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeType;

/**
 * An {@link AbstractFXSegmentHandlePart} with a rectangular
 * {@link javafx.scene.shape.Rectangle} visual.
 *
 * @author mwienand
 *
 */
public class FXRectangleSegmentHandlePart
		extends AbstractFXSegmentHandlePart<javafx.scene.shape.Rectangle> {

	/**
	 * The default width for this part's visualization.
	 */
	public static final double DEFAULT_WIDTH = 3;

	/**
	 * The default length for this part's visualization.
	 */
	public static final double DEFAULT_LENGTH = 8;

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
		visual.setTranslateX(-DEFAULT_LENGTH / 2);
		visual.setTranslateY(-DEFAULT_WIDTH / 2);
		visual.setFill(DEFAULT_FILL);
		visual.setStroke(DEFAULT_STROKE);
		visual.setWidth(DEFAULT_LENGTH);
		visual.setHeight(DEFAULT_WIDTH);
		visual.setStrokeWidth(1);
		visual.setStrokeType(StrokeType.OUTSIDE);
		return visual;
	}

	@Override
	public void doRefreshVisual(Rectangle visual) {
		super.doRefreshVisual(visual);
		updateColor();
	}

	/**
	 * Updates the color of this part's visualization. If this handle part
	 * represents a way or end point of an {@link Connection}, it's color will
	 * be set to {@link FXCircleSegmentHandlePart#CONNECTED_FILL} if that handle
	 * is connected to another part, and
	 * {@link FXCircleSegmentHandlePart#UNCONNECTED_FILL} otherwise. If this
	 * handle part represents a middle point on a segment, it's color will be
	 * set to {@link #DEFAULT_FILL}.
	 */
	protected void updateColor() {
		// only update when bound to anchorage
		SetMultimap<IVisualPart<Node, ? extends Node>, String> anchorages = getAnchoragesUnmodifiable();
		if (getRoot() == null || anchorages.keySet().size() != 1) {
			return;
		}

		Rectangle visual = getVisual();
		// no need to update the color if we are invisible
		if (!visual.isVisible()) {
			return;
		}

		if (getSegmentParameter() != 0.0 && getSegmentParameter() != 1.0) {
			// handle in the middle of a segment
			visual.setFill(FXCircleSegmentHandlePart.DEFAULT_FILL);
		} else {
			// determine connected state for end point handles
			boolean connected = false;
			IVisualPart<Node, ? extends Node> targetPart = anchorages.keySet()
					.iterator().next();
			if (targetPart.getVisual() instanceof Connection) {
				Connection connection = (Connection) targetPart.getVisual();
				if (getSegmentIndex() + getSegmentParameter() == 0.0) {
					// handle at start point
					connected = connection.isStartConnected();
				} else if (getSegmentParameter()
						+ getSegmentIndex() == getSegmentsInScene().length) {
					// handle at end point
					connected = connection.isEndConnected();
				}
			}
			// update color according to connected state
			if (connected) {
				visual.setFill(FXCircleSegmentHandlePart.CONNECTED_FILL);
			} else {
				visual.setFill(FXCircleSegmentHandlePart.UNCONNECTED_FILL);
			}
		}
	}

	@Override
	protected void updateLocation(Rectangle visual) {
		super.updateLocation(visual);
		BezierCurve bezierSegmentInParent = getBezierSegmentInParent();
		if (bezierSegmentInParent == null) {
			return;
		}

		Point direction = bezierSegmentInParent.getDerivative()
				.get(getSegmentParameter());
		Vector directionVector = new Vector(direction.x, direction.y);
		if (directionVector.isNull()) {
			return;
		}

		Vector xVector = new Vector(1, 0);
		Angle angleCcw = xVector.getAngleCCW(directionVector);
		visual.setRotate(angleCcw.deg());
	}

}
