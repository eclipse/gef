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
package org.eclipse.gef4.zest.fx;

import javafx.geometry.Bounds;
import javafx.scene.Node;

import org.eclipse.core.commands.operations.IUndoableOperation;
import org.eclipse.gef4.geometry.planar.Dimension;
import org.eclipse.gef4.geometry.planar.Point;
import org.eclipse.gef4.layout.PropertiesHelper;
import org.eclipse.gef4.layout.interfaces.NodeLayout;
import org.eclipse.gef4.mvc.fx.policies.FXResizeRelocatePolicy;
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

			double dx = location.x - x;
			double dy = location.y - y;
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
		Bounds layoutBounds = visual.getLayoutBounds();
		PropertiesHelper.setLocation(nodeLayout, visual.getLayoutX(),
				visual.getLayoutY());
		PropertiesHelper.setSize(nodeLayout, layoutBounds.getWidth(),
				layoutBounds.getHeight());
	}

}
