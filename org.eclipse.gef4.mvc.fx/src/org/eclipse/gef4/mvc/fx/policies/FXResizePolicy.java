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
import org.eclipse.gef4.mvc.policies.AbstractPolicy;

import javafx.geometry.Bounds;
import javafx.scene.Node;

public class FXResizePolicy extends AbstractPolicy<Node>
		implements ITransactional {

	protected FXResizeNodeOperation resizeOperation;
	protected ForwardUndoCompositeOperation resizeAndRevealOperation;

	// can be overridden by subclasses to add an operation for model changes
	@Override
	public IUndoableOperation commit() {
		IUndoableOperation commit = null;
		// resizeOperation may be null if commit() is called more than once (see
		// bug #475554)
		if (resizeOperation != null && resizeOperation.hasEffect()) {
			commit = resizeAndRevealOperation;
		}
		resizeAndRevealOperation = null;
		resizeOperation = null;
		return commit;
	}

	protected Dimension getInitialSize(Node visualToResize) {
		Bounds layoutBounds = visualToResize.getLayoutBounds();
		return new Dimension(layoutBounds.getWidth(), layoutBounds.getHeight());
	}

	protected double getMinimumHeight() {
		return FXCircleSegmentHandlePart.DEFAULT_SIZE;
	}

	protected double getMinimumWidth() {
		return FXCircleSegmentHandlePart.DEFAULT_SIZE;
	}

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
	}

	public void performResize(double dw, double dh) {
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

	protected void updateOperation(double layoutDw, double layoutDh) {
		resizeOperation.setDw(layoutDw);
		resizeOperation.setDh(layoutDh);
	}

}
