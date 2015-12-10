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
import org.eclipse.gef4.mvc.fx.operations.FXResizeNodeOperation;
import org.eclipse.gef4.mvc.fx.operations.FXRevealOperation;
import org.eclipse.gef4.mvc.fx.parts.FXCircleSegmentHandlePart;
import org.eclipse.gef4.mvc.operations.ForwardUndoCompositeOperation;
import org.eclipse.gef4.mvc.operations.ITransactionalOperation;
import org.eclipse.gef4.mvc.parts.IVisualPart;
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
public class FXResizePolicy extends AbstractTransactionPolicy<Node> {

	// can be overridden by subclasses to add an operation for model changes
	@Override
	public ITransactionalOperation commit() {
		// FIXME: FXRevealOperation does not properly compute isNoop(); thus we
		// have to evaluate the resize operation alone here.
		FXResizeNodeOperation resizeOperation = getResizeOperation();
		boolean commit = resizeOperation != null && !resizeOperation.isNoOp();
		resizeOperation = null;
		// execute super.commit()
		ITransactionalOperation op = super.commit();
		return commit ? op : null;
	}

	@Override
	protected ITransactionalOperation createOperation() {
		// create operation for commit and rollback
		ForwardUndoCompositeOperation resizeAndRevealOperation = new ForwardUndoCompositeOperation(
				"Resize and Reveal");
		resizeAndRevealOperation.add(new FXResizeNodeOperation("Resize",
				getVisualToResize(), getSize(getVisualToResize()), 0, 0));
		resizeAndRevealOperation.add(new FXRevealOperation(getHost()));
		return resizeAndRevealOperation;
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
	 * Returns the {@link FXResizeNodeOperation} that is used by this
	 * {@link FXResizePolicy}.
	 *
	 * @return The {@link FXResizeNodeOperation} nested within the
	 *         {@link ITransactionalOperation} used by this
	 *         {@link AbstractTransactionPolicy}.
	 */
	protected FXResizeNodeOperation getResizeOperation() {
		return (FXResizeNodeOperation) ((ForwardUndoCompositeOperation) getOperation())
				.getOperations().get(0);
	}

	/**
	 * Returns the current size of the passed-in {@link Node}, i.e. the width
	 * and height of its layout-bounds.
	 *
	 * @param visualToResize
	 *            The {@link Node} for which to return the current size.
	 * @return The current size of the passed-in {@link Node}.
	 */
	protected Dimension getSize(Node visualToResize) {
		Bounds layoutBounds = visualToResize.getLayoutBounds();
		return new Dimension(layoutBounds.getWidth(), layoutBounds.getHeight());
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

	/**
	 * Resizes the {@link #getVisualToResize() visual} by the given delta width
	 * and delta height.
	 *
	 * @param dw
	 *            The delta width.
	 * @param dh
	 *            The delta height.
	 */
	public void resize(double dw, double dh) {
		checkInitialized();

		FXResizeNodeOperation resizeOperation = getResizeOperation();

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
		locallyExecuteOperation();
	}
}
