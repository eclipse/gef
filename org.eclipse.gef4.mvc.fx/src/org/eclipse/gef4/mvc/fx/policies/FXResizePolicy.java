/*******************************************************************************
 * Copyright (c) 2015 itemis AG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Matthias Wienand (itemis AG) - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.gef4.mvc.fx.policies;

import org.eclipse.gef4.geometry.planar.Dimension;
import org.eclipse.gef4.mvc.fx.operations.FXResizeOperation;
import org.eclipse.gef4.mvc.fx.parts.FXCircleSegmentHandlePart;
import org.eclipse.gef4.mvc.operations.ITransactionalOperation;
import org.eclipse.gef4.mvc.parts.IVisualPart;
import org.eclipse.gef4.mvc.policies.AbstractResizePolicy;
import org.eclipse.gef4.mvc.policies.AbstractTransactionPolicy;

import javafx.geometry.Bounds;
import javafx.scene.Node;

/**
 * The {@link FXResizePolicy} is an {@link AbstractTransactionPolicy} that
 * handles the resize of an {@link IVisualPart}.
 *
 * @author mwienand
 *
 */
public class FXResizePolicy extends AbstractResizePolicy<Node> {

	@Override
	protected ITransactionalOperation createOperation() {
		return new FXResizeOperation("Resize", getVisualToResize(),
				getCurrentSize(), 0, 0);
	}

	@Override
	protected Dimension getCurrentSize() {
		Bounds layoutBounds = getVisualToResize().getLayoutBounds();
		return new Dimension(layoutBounds.getWidth(), layoutBounds.getHeight());
	}

	/**
	 * Returns the minimum height. The height of a {@link Node} cannot be
	 * changed below this limit.
	 *
	 * @return The minimum height.
	 */
	protected double getMinimumHeight() {
		return FXCircleSegmentHandlePart.DEFAULT_SIZE;
	}

	/**
	 * Returns the minimum width. The width of a {@link Node} cannot be changed
	 * below this limit.
	 *
	 * @return The minimum width.
	 */
	protected double getMinimumWidth() {
		return FXCircleSegmentHandlePart.DEFAULT_SIZE;
	}

	/**
	 * Returns the {@link FXResizeOperation} that is used by this
	 * {@link FXResizePolicy}.
	 *
	 * @return The {@link FXResizeOperation} used by this
	 *         {@link AbstractTransactionPolicy}.
	 */
	protected FXResizeOperation getResizeOperation() {
		return (FXResizeOperation) getOperation();
	}

	/**
	 * Returns the {@link Node} that should be resized. Per default, this is the
	 * {@link #getHost() host's} visual.
	 *
	 * @return The {@link Node} that should be resized.
	 */
	protected Node getVisualToResize() {
		return getHost().getVisual();
	}

	@Override
	protected void updateResizeOperation(double dw, double dh) {
		FXResizeOperation resizeOperation = getResizeOperation();

		Node visual = resizeOperation.getVisual();
		boolean resizable = visual.isResizable();

		// convert resize into relocate in case node is not resizable
		double layoutDw = resizable ? dw : 0;
		double layoutDh = resizable ? dh : 0;

		// ensure visual is not resized below threshold
		if (resizable) {
			if (resizeOperation.getInitialSize().width
					+ layoutDw < getMinimumWidth()) {
				layoutDw = getMinimumWidth()
						- resizeOperation.getInitialSize().width;
			}
			if (resizeOperation.getInitialSize().height
					+ layoutDh < getMinimumHeight()) {
				layoutDh = getMinimumHeight()
						- resizeOperation.getInitialSize().height;
			}
		}

		// update and locally execute operation
		resizeOperation.setDw(layoutDw);
		resizeOperation.setDh(layoutDh);
	}
}
