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

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.IUndoableOperation;
import org.eclipse.gef4.geometry.planar.Dimension;
import org.eclipse.gef4.geometry.planar.Point;
import org.eclipse.gef4.mvc.fx.operations.FXResizeRelocateOperation;
import org.eclipse.gef4.mvc.policies.AbstractPolicy;
import org.eclipse.gef4.mvc.policies.IPolicy;

public class FXResizeRelocatePolicy extends AbstractPolicy<Node> implements
		IPolicy<Node> {

	protected double initialLayoutX, initialLayoutY, initialWidth,
			initialHeight;
	private FXResizeRelocateOperation operation;

	public void initResizeRelocate() {
		Node visual = getHost().getVisual();
		initialLayoutX = visual.getLayoutX();
		initialLayoutY = visual.getLayoutY();

		Bounds lb = visual.getLayoutBounds();
		initialWidth = lb.getWidth();
		initialHeight = lb.getHeight();
	}

	public void performResizeRelocate(double dx, double dy, double dw, double dh) {
		Node visual = getHost().getVisual();
		boolean resizable = visual.isResizable();

		// convert resize into relocate in case node is not resizable
		double layoutDx = resizable ? dx : dx + dw / 2;
		double layoutDy = resizable ? dy : dy + dh / 2;
		double layoutDw = resizable ? dw : 0;
		double layoutDh = resizable ? dh : 0;

		// create undoable operation
		if (layoutDx == 0 && layoutDy == 0 && layoutDw == 0 && layoutDh == 0) {
			operation = null;
		} else {
			operation = new FXResizeRelocateOperation("Resize/Relocate",
					visual, new Point(initialLayoutX, initialLayoutY),
					new Dimension(initialWidth, initialHeight), layoutDx,
					layoutDy, layoutDw, layoutDh);
			try {
				// execute locally only
				operation.execute(null, null);
			} catch (ExecutionException e) {
				e.printStackTrace();
			}
		}
	}

	public IUndoableOperation commit() {
		IUndoableOperation commit = operation;
		operation = null;
		return commit;
	}
}
