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
 *     Alexander Nyßen (itemis AG) - code refactoring
 *
 *******************************************************************************/
package org.eclipse.gef.mvc.fx.parts;

import org.eclipse.gef.fx.nodes.Connection;

import com.google.common.collect.SetMultimap;

import javafx.scene.Node;
import javafx.scene.shape.Circle;
import javafx.scene.shape.StrokeType;

/**
 * The {@link CircleSegmentHandlePart} is an {@link AbstractSegmentHandlePart}
 * that uses {@link Circle} for the visualization.
 *
 * @author mwienand
 * @author anyssen
 *
 */
public class CircleSegmentHandlePart extends AbstractSegmentHandlePart<Circle> {

	/**
	 * The default size for this part's visualization.
	 */
	protected static final double DEFAULT_SIZE = 5;

	/**
	 * Creates the visual representation of this selection handle.
	 *
	 * @return {@link Node} representing the handle visually
	 */
	@Override
	protected Circle doCreateVisual() {
		Circle circle = new Circle(DEFAULT_SIZE / 2d);
		// initialize invariant visual properties
		circle.setStroke(getStroke());
		circle.setFill(getMoveFill());
		circle.setStrokeWidth(1);
		circle.setStrokeType(StrokeType.OUTSIDE);
		return circle;
	}

	@Override
	public void doRefreshVisual(Circle visual) {
		super.doRefreshVisual(visual);
		updateColor();
	}

	/**
	 * Updates the color of this part's visualization. If this handle part
	 * represents a way or end point of an {@link Connection}, it's color will
	 * be set to indicate whether the handle is connected to another part or
	 * not.
	 */
	protected void updateColor() {
		// only update when bound to anchorage
		SetMultimap<IVisualPart<? extends Node>, String> anchorages = getAnchoragesUnmodifiable();
		if (getRoot() == null || anchorages.keySet().size() != 1) {
			return;
		}

		Circle visual = getVisual();
		// no need to update the color if we are invisible
		if (!visual.isVisible()) {
			return;
		}
		if (getSegmentParameter() != 0.0 && getSegmentParameter() != 1.0) {
			visual.setFill(getInsertFill());
			visual.setRadius(DEFAULT_SIZE * 2d / 5d);
		} else {
			visual.setRadius(DEFAULT_SIZE / 2d);
			// determine connected state for end point handles
			boolean connected = false;
			IVisualPart<? extends Node> targetPart = anchorages.keySet()
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
				visual.setFill(getConnectedFill());
			} else {
				visual.setFill(getMoveFill());
			}
		}
	}
}
