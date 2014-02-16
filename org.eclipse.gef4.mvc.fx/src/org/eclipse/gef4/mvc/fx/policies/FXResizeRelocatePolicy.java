/*******************************************************************************
 * Copyright (c) 2014 itemis AG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Alexander Nyßen (itemis AG) - initial API and implementation
 *     
 *******************************************************************************/
package org.eclipse.gef4.mvc.fx.policies;

import javafx.geometry.Bounds;
import javafx.scene.Node;

import org.eclipse.gef4.mvc.policies.AbstractPolicy;
import org.eclipse.gef4.mvc.policies.IResizeRelocatePolicy;

public class FXResizeRelocatePolicy extends AbstractPolicy<Node> implements IResizeRelocatePolicy<Node> {

	private double initialLayoutX, initialLayoutY, initialWidth, initialHeight;

	@Override
	public void initResizeRelocate() {
		Node visual = getHost().getVisual();
		initialLayoutX = visual.getLayoutX();
		initialLayoutY = visual.getLayoutY();

		Bounds lb = visual.getLayoutBounds();
		initialWidth = lb.getWidth();
		initialHeight = lb.getHeight();
	}

	@Override
	public void performResizeRelocate(double dx, double dy, double dw, double dh) {
		Node visual = getHost().getVisual();
		if (visual.isResizable()) {
			if (dx != 0) {
				visual.setLayoutX(initialLayoutX + dx);
			}
			if (dy != 0) {
				visual.setLayoutY(initialLayoutY + dy);
			}
			if (dw != 0 || dw != 0) {
				visual.resize(initialWidth + dw, initialHeight + dh);
			}
		} else {
			// compute new position based on resized bounds
			visual.setLayoutX(initialLayoutX + dx + dw / 2);
			visual.setLayoutY(initialLayoutY + dy + dh / 2);
		}
	}

	@Override
	public void commitResizeRelocate(double dx, double dy, double dw, double dh) {
		// TODO: create IUndoableOperation to perform the resize and execute it
		// on the IOperationHistory
	}
}
