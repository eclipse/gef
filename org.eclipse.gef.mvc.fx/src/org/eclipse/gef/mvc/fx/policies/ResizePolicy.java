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
		if (dw != 0 || dh != 0) {
			// ensure visual is not resized below threshold
			Node visual = getHost().getVisual();
			Dimension initialSize = getInitialSize();
			double minimumWidth = visual.minWidth(initialSize.height + dh);
			double minimumHeight = visual.minHeight(initialSize.width + dw);
			if (initialSize.width + dw < minimumWidth) {
				dw = minimumWidth - initialSize.width;
			}
			if (initialSize.height + dh < minimumHeight) {
				dh = minimumHeight - initialSize.height;
			}
		}
		return new Dimension(dw, dh);
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
		return getResizeOperation().getInitialSize();
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
	 * Computes the applicable delta width and height from the given intended
	 * delta values and updates the operation accordingly.
	 *
	 * @param intendedDeltaWidth
	 *            The intended width delta.
	 * @param intendedDeltaHeight
	 *            The intended height delta.
	 */
	protected void updateResizeOperation(double intendedDeltaWidth,
			double intendedDeltaHeight) {
		Dimension applicableDelta = computeApplicableDelta(intendedDeltaWidth,
				intendedDeltaHeight);
		ResizeOperation op = getResizeOperation();
		op.setDw(applicableDelta.width);
		op.setDh(applicableDelta.height);
	}
}
