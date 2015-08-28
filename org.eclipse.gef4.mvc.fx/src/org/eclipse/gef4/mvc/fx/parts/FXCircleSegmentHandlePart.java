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

import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.StrokeType;

import org.eclipse.gef4.fx.nodes.FXConnection;
import org.eclipse.gef4.mvc.parts.IVisualPart;

import com.google.common.collect.SetMultimap;

/**
 * An {@link AbstractFXSegmentHandlePart} with a {@link Circle} visual.
 *
 * @author mwienand
 * @author anyssen
 *
 */
public class FXCircleSegmentHandlePart
		extends AbstractFXSegmentHandlePart<Circle> {

	public static final Color DEFAULT_STROKE = Color.web("#5a61af");
	public static final Color DEFAULT_FILL = Color.WHITE;
	public static final Color CONNECTED_FILL = Color.web("#ff0000");
	public static final Color UNCONNECTED_FILL = Color.web("#d5faff");
	public static final double DEFAULT_SIZE = 5d;

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

	@Override
	public void doRefreshVisual(Circle visual) {
		super.doRefreshVisual(visual);
		updateColor();
	}

	protected void updateColor() {
		// only update when bound to anchorage
		SetMultimap<IVisualPart<Node, ? extends Node>, String> anchorages = getAnchorages();
		if (getRoot() == null || anchorages.keySet().size() != 1) {
			return;
		}

		Circle visual = getVisual();
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
			if (targetPart.getVisual() instanceof FXConnection) {
				FXConnection connection = (FXConnection) targetPart.getVisual();
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
				visual.setFill(CONNECTED_FILL);
			} else {
				visual.setFill(UNCONNECTED_FILL);
			}
		}
	}

}
