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
import org.eclipse.gef4.mvc.fx.operations.FXRevealOperation;
import org.eclipse.gef4.mvc.fx.parts.FXCircleSegmentHandlePart;
import org.eclipse.gef4.mvc.models.GridModel;
import org.eclipse.gef4.mvc.operations.ForwardUndoCompositeOperation;
import org.eclipse.gef4.mvc.operations.ITransactional;
import org.eclipse.gef4.mvc.policies.AbstractPolicy;

public class FXResizeRelocatePolicy extends AbstractPolicy<Node> implements
		ITransactional {

	protected static Dimension getSnapToGridOffset(GridModel gridModel,
			final double localX, final double localY,
			final double gridCellWidthFraction,
			final double gridCellHeightFraction) {
		double snapOffsetX = 0, snapOffsetY = 0;
		if ((gridModel != null) && gridModel.isSnapToGrid()) {
			// determine snap width
			final double snapWidth = gridModel.getGridCellWidth()
					* gridCellWidthFraction;
			final double snapHeight = gridModel.getGridCellHeight()
					* gridCellHeightFraction;

			snapOffsetX = localX % snapWidth;
			if (snapOffsetX > (snapWidth / 2)) {
				snapOffsetX = snapWidth - snapOffsetX;
				snapOffsetX *= -1;
			}

			snapOffsetY = localY % snapHeight;
			if (snapOffsetY > (snapHeight / 2)) {
				snapOffsetY = snapHeight - snapOffsetY;
				snapOffsetY *= -1;
			}
		}
		return new Dimension(snapOffsetX, snapOffsetY);
	}

	protected FXResizeRelocateNodeOperation resizeRelocateOperation;
	protected ForwardUndoCompositeOperation forwardUndoOperation;

	// can be overridden by subclasses to add an operation for model changes
	@Override
	public IUndoableOperation commit() {
		IUndoableOperation commit = forwardUndoOperation;
		resizeRelocateOperation = null;
		return commit;
	}

	protected double getMinimumHeight() {
		return FXCircleSegmentHandlePart.DEFAULT_SIZE;
	}

	protected double getMinimumWidth() {
		return FXCircleSegmentHandlePart.DEFAULT_SIZE;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.gef4.mvc.fx.policies.ITransactionalPolicy#init()
	 */
	@Override
	public void init() {
		// create "empty" operation
		resizeRelocateOperation = new FXResizeRelocateNodeOperation(getHost()
				.getVisual());
		FXRevealOperation revealOperation = new FXRevealOperation(getHost());
		forwardUndoOperation = new ForwardUndoCompositeOperation(
				resizeRelocateOperation.getLabel());
		forwardUndoOperation.add(resizeRelocateOperation);
		forwardUndoOperation.add(revealOperation);
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
			if (resizeRelocateOperation.getOldSize().width + layoutDw < getMinimumWidth()) {
				layoutDw = getMinimumWidth()
						- resizeRelocateOperation.getOldSize().width;
			}
			if (resizeRelocateOperation.getOldSize().height + layoutDh < getMinimumHeight()) {
				layoutDh = getMinimumHeight()
						- resizeRelocateOperation.getOldSize().height;
			}
		}

		// snap-to-grid
		Point start = resizeRelocateOperation.getOldLocation();
		Dimension snapToGridOffset = getSnapToGridOffset(getHost().getRoot()
				.getViewer().<GridModel> getAdapter(GridModel.class), start.x
				+ layoutDx, start.y + layoutDy, 0.5, 0.5);
		layoutDx = layoutDx - snapToGridOffset.width;
		layoutDy = layoutDy - snapToGridOffset.height;

		updateOperation(layoutDx, layoutDy, layoutDw, layoutDh);

		// locally execute operation
		try {
			forwardUndoOperation.execute(null, null);
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
	}

	protected void updateOperation(double layoutDx, double layoutDy,
			double layoutDw, double layoutDh) {
		resizeRelocateOperation.setDx(layoutDx);
		resizeRelocateOperation.setDy(layoutDy);
		resizeRelocateOperation.setDw(layoutDw);
		resizeRelocateOperation.setDh(layoutDh);
	}

}
