/*******************************************************************************
 * Copyright (c) 2015, 2016 itemis AG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Matthias Wienand (itemis AG) - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.gef.mvc.fx.policies;

import org.eclipse.gef.geometry.planar.Dimension;
import org.eclipse.gef.mvc.fx.operations.FXResizeOperation;
import org.eclipse.gef.mvc.fx.parts.FXSquareSegmentHandlePart;
import org.eclipse.gef.mvc.fx.parts.IFXResizableVisualPart;
import org.eclipse.gef.mvc.operations.ITransactionalOperation;
import org.eclipse.gef.mvc.parts.IVisualPart;
import org.eclipse.gef.mvc.policies.AbstractResizePolicy;
import org.eclipse.gef.mvc.policies.AbstractTransactionPolicy;

import javafx.scene.Node;
import javafx.scene.layout.Region;

/**
 * The {@link FXResizePolicy} is an {@link AbstractTransactionPolicy} that
 * handles the resize of an {@link IVisualPart}.
 *
 * @author mwienand
 *
 */
// TODO: respect max width and height
public class FXResizePolicy extends AbstractResizePolicy<Node> {

	/**
	 * Computes the applicable delta from the given delta width and delta height
	 * values, i.e. respecting the part's minimum size.
	 *
	 * @param dw
	 *            The width delta.
	 * @param dh
	 *            The height delta.
	 * @return A {@link Dimension} containing the applicable delta based on the
	 *         given values.
	 */
	protected Dimension computeApplicableDelta(double dw, double dh) {
		FXResizeOperation resizeOperation = getResizeOperation();

		Node visual = resizeOperation.getResizablePart().getResizableVisual();
		boolean resizable = visual.isResizable();

		// convert resize into relocate in case node is not resizable
		double layoutDw = resizable ? dw : 0;
		double layoutDh = resizable ? dh : 0;

		if (resizable && (layoutDw != 0 || layoutDh != 0)) {
			// ensure visual is not resized below threshold
			double minimumWidth = getMinimumWidth();
			double minimumHeight = getMinimumHeight();
			if (resizeOperation.getInitialSize().width
					+ layoutDw < minimumWidth) {
				layoutDw = minimumWidth
						- resizeOperation.getInitialSize().width;
			}
			if (resizeOperation.getInitialSize().height
					+ layoutDh < minimumHeight) {
				layoutDh = minimumHeight
						- resizeOperation.getInitialSize().height;
			}
		}

		Dimension applicable = new Dimension(layoutDw, layoutDh);
		return applicable;
	}

	@Override
	protected ITransactionalOperation createOperation() {
		return new FXResizeOperation("Resize", getHost(), getCurrentSize(), 0,
				0);
	}

	@Override
	protected Dimension getCurrentSize() {
		return getHost().getVisualSize();
	}

	/**
	 * Returns the delta height of the {@link #getResizeOperation() resize
	 * operation} that is used by this policy.
	 *
	 * @return The delta height of the {@link #getResizeOperation() resize
	 *         operation} that is used by this policy.
	 */
	public double getDeltaHeight() {
		return getResizeOperation().getDw();
	}

	/**
	 * Returns the delta width of the {@link #getResizeOperation() resize
	 * operation} that is used by this policy.
	 *
	 * @return The delta width of the {@link #getResizeOperation() resize
	 *         operation} that is used by this policy.
	 */
	public double getDeltaWidth() {
		return getResizeOperation().getDw();
	}

	@Override
	public IFXResizableVisualPart<? extends Node> getHost() {
		return (IFXResizableVisualPart<? extends Node>) super.getHost();
	}

	/**
	 * Returns the minimum height. The height of a {@link Node} cannot be
	 * changed below this limit.
	 *
	 * @return The minimum height.
	 */
	protected double getMinimumHeight() {
		Node visualToResize = getVisualToResize();
		double computedMinHeight = -1;
		if (visualToResize instanceof Region) {
			computedMinHeight = ((Region) visualToResize).getMinHeight();
		} else if (visualToResize.isResizable()) {
			computedMinHeight = visualToResize.minHeight(-1);
		}
		return Math.max(computedMinHeight,
				FXSquareSegmentHandlePart.DEFAULT_SIZE);
	}

	/**
	 * Returns the minimum width. The width of a {@link Node} cannot be changed
	 * below this limit.
	 *
	 * @return The minimum width.
	 */
	protected double getMinimumWidth() {
		Node visualToResize = getVisualToResize();
		double computedMinWidth = -1;
		if (visualToResize instanceof Region) {
			computedMinWidth = ((Region) visualToResize).getMinWidth();
		} else if (visualToResize.isResizable()) {
			computedMinWidth = visualToResize.minWidth(-1);
		}
		return Math.max(computedMinWidth,
				FXSquareSegmentHandlePart.DEFAULT_SIZE);
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
		return getHost().getResizableVisual();
	}

	@Override
	protected void updateResizeOperation(double dw, double dh) {
		Dimension adjusted = computeApplicableDelta(dw, dh);
		double layoutDw = adjusted.width;
		double layoutDh = adjusted.height;
		// update and locally execute operation
		FXResizeOperation op = getResizeOperation();
		op.setDw(layoutDw);
		op.setDh(layoutDh);
	}

}
