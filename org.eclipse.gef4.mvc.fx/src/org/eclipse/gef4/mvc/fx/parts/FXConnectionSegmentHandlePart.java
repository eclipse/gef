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

import org.eclipse.gef4.fx.nodes.FXConnection;
import org.eclipse.gef4.geometry.planar.BezierCurve;
import org.eclipse.gef4.mvc.parts.IVisualPart;

import com.google.common.collect.SetMultimap;
import com.google.inject.Provider;

// TODO: merge with FXSegmentHandlePart
public class FXConnectionSegmentHandlePart extends FXSegmentHandlePart {

	public static final Color FILL_CONNECTED = Color.web("#ff0000");
	public static final Color FILL_UNCONNECTED = Color.web("#d5faff");

	public FXConnectionSegmentHandlePart(
			Provider<BezierCurve[]> segmentsProvider, int segmentIndex,
			double segmentParameter) {
		super(segmentsProvider, segmentIndex, segmentParameter);
	}

	@Override
	public void doRefreshVisual(Circle visual) {
		super.doRefreshVisual(visual);
		updateColor();
	}

	protected void updateColor() {
		// only update when bound to anchorage
		FXRootPart rootPart = (FXRootPart) getRoot();
		SetMultimap<IVisualPart<Node, ? extends Node>, String> anchorages = getAnchorages();
		if (rootPart == null || anchorages.keySet().size() != 1) {
			return;
		}

		Circle visual = getVisual();
		// no need to update the color if we are invisible
		if (!visual.isVisible()) {
			return;
		}
		if (getSegmentParameter() != 0.0 && getSegmentParameter() != 1.0) {
			// handle in the middle of a segment
			visual.setFill(FXSegmentHandlePart.DEFAULT_FILL);
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
				} else if (getSegmentParameter() + getSegmentIndex() == getSegmentsInScene().length) {
					// handle at end point
					connected = connection.isEndConnected();
				}
			}
			// update color according to connected state
			if (connected) {
				visual.setFill(FILL_CONNECTED);
			} else {
				visual.setFill(FILL_UNCONNECTED);
			}
		}
	}

}
