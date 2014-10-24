/*******************************************************************************
 * Copyright (c) 2014 itemis AG and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Matthias Wienand (itemis AG) - initial API & implementation
 *
 *******************************************************************************/
package org.eclipse.gef4.zest.fx.policies;

import javafx.geometry.Bounds;
import javafx.scene.Node;

import org.eclipse.core.commands.operations.IUndoableOperation;
import org.eclipse.gef4.geometry.planar.Dimension;
import org.eclipse.gef4.geometry.planar.Point;
import org.eclipse.gef4.layout.PropertiesHelper;
import org.eclipse.gef4.layout.interfaces.NodeLayout;
import org.eclipse.gef4.mvc.fx.policies.FXResizeRelocatePolicy;
import org.eclipse.gef4.mvc.parts.IFeedbackPart;
import org.eclipse.gef4.mvc.parts.IVisualPart;
import org.eclipse.gef4.mvc.policies.AbstractPolicy;

public class NodeLayoutPolicy extends AbstractPolicy<Node> {

	public static Class<FXResizeRelocatePolicy> RESIZE_RELOCATE_POLICY_KEY = FXResizeRelocatePolicy.class;

	public NodeLayoutPolicy() {
	}

	public void adaptLayoutInformation(NodeLayout nodeLayout) {
		FXResizeRelocatePolicy policy = getHost().getAdapter(
				RESIZE_RELOCATE_POLICY_KEY);
		if (policy != null) {
			Node visual = getHost().getVisual();
			Bounds layoutBounds = visual.getLayoutBounds();
			double x = visual.getLayoutX();
			double y = visual.getLayoutY();
			double w = layoutBounds.getWidth();
			double h = layoutBounds.getHeight();

			Point location = PropertiesHelper.getLocation(nodeLayout);
			Dimension size = PropertiesHelper.getSize(nodeLayout);

			// location is the center of the node, therefore we subtract half
			// width/height from it
			double dx = location.x - size.width / 2 - x;
			double dy = location.y - size.height / 2 - y;
			double dw = size.width - w;
			double dh = size.height - h;

			policy.init();
			policy.performResizeRelocate(dx, dy, dw, dh);
			IUndoableOperation operation = policy.commit();
			if (operation != null) {
				executeOperation(operation);
			}
		}
	}

	public void provideLayoutInformation(NodeLayout nodeLayout) {
		Node visual = getHost().getVisual();
		Bounds hostBounds = visual.getLayoutBounds();
		double minx = hostBounds.getMinX();
		double miny = hostBounds.getMinY();
		double maxx = hostBounds.getMaxX();
		double maxy = hostBounds.getMaxY();
		// union node bounds with bounds of feedback visuals
		for (IVisualPart<Node> anchored : getHost().getAnchoreds()) {
			if (!(anchored instanceof IFeedbackPart)) {
				continue;
			}
			Node anchoredVisual = anchored.getVisual();
			Bounds anchoredBounds = anchoredVisual.getLayoutBounds();
			Bounds anchoredBoundsInHost = visual.sceneToLocal(anchoredVisual
					.localToScene(anchoredBounds));
			minx = Math.min(minx, anchoredBoundsInHost.getMinX());
			miny = Math.min(miny, anchoredBoundsInHost.getMinY());
			maxx = Math.max(maxx, anchoredBoundsInHost.getMaxX());
			maxy = Math.max(maxy, anchoredBoundsInHost.getMaxY());
		}

		PropertiesHelper.setLocation(nodeLayout, visual.getLayoutX() + minx,
				visual.getLayoutY() + miny);
		PropertiesHelper.setSize(nodeLayout, maxx - minx, maxy - miny);
		nodeLayout.setProperty("pruned", !visual.isVisible());
	}

}
