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
import org.eclipse.gef.mvc.fx.operations.ForwardUndoCompositeOperation;
import org.eclipse.gef.mvc.fx.operations.ITransactionalOperation;
import org.eclipse.gef.mvc.fx.operations.ResizeContentOperation;
import org.eclipse.gef.mvc.fx.operations.ResizeOperation;
import org.eclipse.gef.mvc.fx.parts.IResizableContentPart;
import org.eclipse.gef.mvc.fx.parts.IVisualPart;
import org.eclipse.gef.mvc.fx.parts.SquareSegmentHandlePart;

import javafx.scene.Node;

/**
 * The {@link ResizePolicy} is an {@link AbstractTransactionPolicy} that handles
 * the resize of an {@link IVisualPart}.
 *
 * @author mwienand
 *
 */
// TODO: respect max width and height
public class ResizePolicy extends AbstractTransactionPolicy {

	private Dimension initialSize;

	/**
	 * Apply the new size to the host.
	 *
	 * @param dw
	 *            The width delta.
	 * @param dh
	 *            The height delta.
	 */
	protected void applySize(double dw, double dh) {
		updateResizeOperation(dw, dh);
		locallyExecuteOperation();
	}

	@Override
	public ITransactionalOperation commit() {
		ITransactionalOperation commitOperation = super.commit();
		if (commitOperation != null && !commitOperation.isNoOp()
				&& isContentResizable()) {
			// chain content changes
			ForwardUndoCompositeOperation composite = new ForwardUndoCompositeOperation(
					"Resize Content");
			composite.add(commitOperation);
			composite.add(createResizeContentOperation());
			commitOperation = composite;
		}

		// clear state
		initialSize = null;

		return commitOperation;
	}

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
		Node visual = getVisualToResize();
		boolean resizable = visual.isResizable();
		// TODO: remove boolean resizable, only use ResizePolicy for
		// IResizableContentPart so that we can safely resize it
		resizable = true;

		// convert resize into relocate in case node is not resizable
		double layoutDw = resizable ? dw : 0;
		double layoutDh = resizable ? dh : 0;

		if (resizable && (layoutDw != 0 || layoutDh != 0)) {
			// ensure visual is not resized below threshold
			double minimumWidth = getMinimumWidth();
			double minimumHeight = getMinimumHeight();
			ResizeOperation resizeOperation = getResizeOperation();
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
		return new ResizeOperation("Resize", getHost(), getCurrentSize(), 0, 0);
	}

	/**
	 * Create an operation to resize the content.
	 *
	 * @return The operation to resize the content.
	 */
	protected ITransactionalOperation createResizeContentOperation() {
		ResizeContentOperation<Node> resizeOperation = new ResizeContentOperation<>(
				getHost(), getInitialSize(), getCurrentSize());
		return resizeOperation;
	}

	/**
	 * Returns the current size of the {@link IResizableContentPart}.
	 *
	 * @return The current size.
	 */
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
		return getResizeOperation().getDh();
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
	public IResizableContentPart<? extends Node> getHost() {
		return (IResizableContentPart<? extends Node>) super.getHost();
	}

	/**
	 * Returns the initial size of the {@link IResizableContentPart}.
	 *
	 * @return The initial size.
	 */
	protected Dimension getInitialSize() {
		return initialSize;
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
		if (visualToResize.isResizable()) {
			computedMinHeight = visualToResize.minHeight(-1);
		}
		return Math.max(computedMinHeight,
				SquareSegmentHandlePart.DEFAULT_SIZE);
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
		if (visualToResize.isResizable()) {
			computedMinWidth = visualToResize.minWidth(-1);
		}
		return Math.max(computedMinWidth, SquareSegmentHandlePart.DEFAULT_SIZE);
	}

	/**
	 * Returns the {@link ResizeOperation} that is used by this
	 * {@link ResizePolicy}.
	 *
	 * @return The {@link ResizeOperation} used by this
	 *         {@link AbstractTransactionPolicy}.
	 */
	protected ResizeOperation getResizeOperation() {
		return (ResizeOperation) getOperation();
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
	public void init() {
		super.init();
		initialSize = getCurrentSize();
	}

	/**
	 * Returns whether the content part supports a content resize operation.
	 *
	 * @return <code>true</code> if content resize is supported,
	 *         <code>false</code> otherwise.
	 */
	protected boolean isContentResizable() {
		return getHost() instanceof IResizableContentPart;
	}

	/**
	 * Resizes the host by the given delta width and delta height.
	 *
	 * @param finalDw
	 *            The delta width.
	 * @param finalDh
	 *            The delta height.
	 */
	public void resize(double finalDw, double finalDh) {
		checkInitialized();
		applySize(finalDw, finalDh);
	}

	/**
	 * Update the resize operation to the new final dh and dw values.
	 *
	 * @param dw
	 *            The new final width delta.
	 * @param dh
	 *            The new final height delta.
	 */
	protected void updateResizeOperation(double dw, double dh) {
		Dimension adjusted = computeApplicableDelta(dw, dh);

		// System.out.println(
		// "want to resize by " + dw + ", " + dh + ", but applicable is "
		// + adjusted.width + ", " + adjusted.height);

		double layoutDw = adjusted.width;
		double layoutDh = adjusted.height;
		// update and locally execute operation
		ResizeOperation op = getResizeOperation();
		op.setDw(layoutDw);
		op.setDh(layoutDh);
	}

}
