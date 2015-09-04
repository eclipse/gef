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

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.IUndoableOperation;
import org.eclipse.gef4.geometry.planar.Dimension;
import org.eclipse.gef4.mvc.fx.operations.FXResizeNodeOperation;
import org.eclipse.gef4.mvc.fx.operations.FXRevealOperation;
import org.eclipse.gef4.mvc.fx.parts.FXCircleSegmentHandlePart;
import org.eclipse.gef4.mvc.operations.ForwardUndoCompositeOperation;
import org.eclipse.gef4.mvc.operations.ITransactional;
import org.eclipse.gef4.mvc.parts.IVisualPart;
import org.eclipse.gef4.mvc.policies.AbstractPolicy;

import javafx.geometry.Bounds;
import javafx.scene.Node;

/**
 * The {@link FXResizePolicy} is a {@link ITransactional transactional}
 * {@link AbstractPolicy policy} that handles the resize of an
 * {@link IVisualPart}.
 *
 * @author mwienand
 *
 */
public class FXResizePolicy extends AbstractPolicy<Node>
		implements ITransactional {

	/**
	 * Stores the <i>initialized</i> flag for this policy, i.e.
	 * <code>true</code> after {@link #init()} was called, and
	 * <code>false</code> after {@link #commit()} was called, respectively.
	 */
	protected boolean initialized;
	/**
	 * The {@link FXResizeNodeOperation} that is used to resize the host's
	 * visual.
	 */
	protected FXResizeNodeOperation resizeOperation;
	/**
	 * The {@link ForwardUndoCompositeOperation} that assembles the resize
	 * operation and the reveal operation.
	 */
	protected ForwardUndoCompositeOperation resizeAndRevealOperation;

	// can be overridden by subclasses to add an operation for model changes
	@Override
	public IUndoableOperation commit() {
		if (!initialized) {
			return null;
		}
		// after commit, we need to be re-initialized
		initialized = false;

		IUndoableOperation commit = null;
		if (resizeOperation != null && resizeOperation.hasEffect()) {
			commit = resizeAndRevealOperation;
		}
		resizeAndRevealOperation = null;
		resizeOperation = null;
		return commit;
	}

	/**
	 * Returns the current size of the passed-in {@link Node}, i.e. the width
	 * and height of its layout-bounds.
	 *
	 * @param visualToResize
	 *            The {@link Node} for which to return the current size.
	 * @return The current size of the passed-in {@link Node}.
	 */
	protected Dimension getInitialSize(Node visualToResize) {
		Bounds layoutBounds = visualToResize.getLayoutBounds();
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
	 * Returns the {@link Node} that should be resized. Per default, this is the
	 * {@link #getHost() host's} visual.
	 *
	 * @return The {@link Node} that should be resized.
	 */
	protected Node getVisualToResize() {
		return getHost().getVisual();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.eclipse.gef4.mvc.fx.policies.ITransactionalPolicy#init()
	 */
	@Override
	public void init() {
		// create "empty" operation
		Node visualToResize = getVisualToResize();
		resizeOperation = new FXResizeNodeOperation("Resize", visualToResize,
				getInitialSize(visualToResize), 0, 0);
		resizeAndRevealOperation = new ForwardUndoCompositeOperation(
				resizeOperation.getLabel());
		resizeAndRevealOperation.add(resizeOperation);
		resizeAndRevealOperation.add(new FXRevealOperation(getHost()));
		initialized = true;
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
	public void performResize(double dw, double dh) {
		// ensure we have been properly initialized
		if (!initialized) {
			throw new IllegalStateException("Not yet initialized!");
		}

		Node visual = resizeOperation.getVisual();
		boolean resizable = visual.isResizable();

		// convert resize into relocate in case node is not resizable
		double layoutDw = resizable ? dw : 0;
		double layoutDh = resizable ? dh : 0;

		// ensure visual is not resized below threshold
		if (resizable) {
			if (resizeOperation.getOldSize().width
					+ layoutDw < getMinimumWidth()) {
				layoutDw = getMinimumWidth()
						- resizeOperation.getOldSize().width;
			}
			if (resizeOperation.getOldSize().height
					+ layoutDh < getMinimumHeight()) {
				layoutDh = getMinimumHeight()
						- resizeOperation.getOldSize().height;
			}
		}

		updateOperation(layoutDw, layoutDh);

		// locally execute operation
		try {
			resizeAndRevealOperation.execute(null, null);
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Updates the resize operation to use the given delta width and delta
	 * height.
	 *
	 * @param layoutDw
	 *            The new delta width.
	 * @param layoutDh
	 *            The new delta height.
	 */
	protected void updateOperation(double layoutDw, double layoutDh) {
		resizeOperation.setDw(layoutDw);
		resizeOperation.setDh(layoutDh);
	}

}
