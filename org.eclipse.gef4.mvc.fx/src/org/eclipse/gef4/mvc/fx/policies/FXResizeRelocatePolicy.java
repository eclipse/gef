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

import javafx.scene.Node;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.IUndoableOperation;
import org.eclipse.gef4.geometry.planar.Dimension;
import org.eclipse.gef4.geometry.planar.Point;
import org.eclipse.gef4.mvc.fx.operations.FXResizeRelocateNodeOperation;
import org.eclipse.gef4.mvc.fx.parts.FXSegmentHandlePart;
import org.eclipse.gef4.mvc.models.GridModel;
import org.eclipse.gef4.mvc.operations.ITransactional;
import org.eclipse.gef4.mvc.policies.AbstractPolicy;

public class FXResizeRelocatePolicy extends AbstractPolicy<Node> implements
		ITransactional {

	private FXResizeRelocateNodeOperation operation;

	// can be overridden by subclasses to add an operation for model changes
	// TODO: pull up to IPolicy interface
	@Override
	public IUndoableOperation commit() {
		FXResizeRelocateNodeOperation commit = operation;
		operation = null;
		return commit;
	}

	protected double getMinimumHeight() {
		return FXSegmentHandlePart.SIZE;
	}

	protected double getMinimumWidth() {
		return FXSegmentHandlePart.SIZE;
	}

	protected Dimension getSnapToGridOffset(final double startX,
			final double startY, final double layoutDx, final double layoutDy,
			final double gridCellWidthFraction,
			final double gridCellHeightFraction) {
		final GridModel gridModel = getHost().getRoot().getViewer()
				.getAdapter(GridModel.class);
		double snapOffsetX = 0, snapOffsetY = 0;
		if ((gridModel != null) && gridModel.isSnapToGrid()) {
			// determine snap width
			final double snapWidth = gridModel.getGridCellWidth()
					* gridCellWidthFraction;
			final double snapHeight = gridModel.getGridCellHeight()
					* gridCellHeightFraction;

			snapOffsetX = (startX + layoutDx) % snapWidth;
			if (snapOffsetX > (snapWidth / 2)) {
				snapOffsetX = snapWidth - snapOffsetX;
				snapOffsetX *= -1;
			}

			snapOffsetY = ((startY + layoutDy) % snapHeight);
			if (snapOffsetY > (snapHeight / 2)) {
				snapOffsetY = snapHeight - snapOffsetY;
				snapOffsetY *= -1;
			}
		}
		return new Dimension(snapOffsetX, snapOffsetY);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.eclipse.gef4.mvc.fx.policies.ITransactionalPolicy#init()
	 */
	@Override
	public void init() {
		// create "empty" operation
		operation = new FXResizeRelocateNodeOperation(getHost().getVisual());
	}

	public void performResizeRelocate(double dx, double dy, double dw, double dh) {
		Node visual = getHost().getVisual();
		boolean resizable = visual.isResizable();

		// convert resize into relocate in case node is not resizable
		double layoutDx = resizable ? dx : dx + dw / 2;
		double layoutDy = resizable ? dy : dy + dh / 2;
		double layoutDw = resizable ? dw : 0;
		double layoutDh = resizable ? dh : 0;

		// ensure visual is not resized below threshold
		if (resizable) {
			if (operation.getOldSize().width + layoutDw < getMinimumWidth()) {
				layoutDw = getMinimumWidth() - operation.getOldSize().width;
			}
			if (operation.getOldSize().height + layoutDh < getMinimumHeight()) {
				layoutDh = getMinimumHeight() - operation.getOldSize().height;
			}
		}

		// snap-to-grid
		Point start = operation.getOldLocation();
		// TODO: make stepping (0.5) configurable
		Dimension snapOffset = getSnapToGridOffset(start.x, start.y, layoutDx,
				layoutDy, 0.5, 0.5);
		layoutDx = layoutDx - snapOffset.width;
		layoutDy = layoutDy - snapOffset.height;

		// update operation
		operation.setDx(layoutDx);
		operation.setDy(layoutDy);
		operation.setDw(layoutDw);
		operation.setDh(layoutDh);

		try {
			// execute locally
			operation.execute(null, null);
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
	}
}
