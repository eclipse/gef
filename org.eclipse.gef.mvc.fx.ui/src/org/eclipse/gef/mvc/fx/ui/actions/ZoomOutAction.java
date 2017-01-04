/*******************************************************************************
 * Copyright (c) 2017 itemis AG and others.
 *
 * All rights reserved. This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License v1.0 which
 * accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Matthias Wienand (itemis AG) - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.gef.mvc.fx.ui.actions;

import org.eclipse.gef.fx.nodes.InfiniteCanvas;
import org.eclipse.gef.geometry.convert.fx.FX2Geometry;
import org.eclipse.gef.geometry.planar.AffineTransform;
import org.eclipse.gef.mvc.fx.operations.ITransactionalOperation;
import org.eclipse.gef.mvc.fx.policies.ViewportPolicy;

import javafx.geometry.Point2D;
import javafx.scene.Parent;

/**
 *
 * @author mwienand
 *
 */
public class ZoomOutAction extends AbstractViewerAction {

	/**
	 *
	 */
	public ZoomOutAction() {
		super("Zoom Out");
		setEnabled(true);
	}

	@Override
	protected ITransactionalOperation createOperation() {
		InfiniteCanvas infiniteCanvas = getInfiniteCanvas();
		if (infiniteCanvas == null) {
			throw new IllegalStateException(
					"Cannot perform ResetZoomAction, because no InfiniteCanvas can be determiend.");
		}

		AffineTransform contentTransform = FX2Geometry
				.toAffineTransform(infiniteCanvas.getContentTransform());
		double sx = contentTransform.getScaleX() * getZoomFactor();

		Point2D pivotInScene = infiniteCanvas.localToScene(
				infiniteCanvas.getWidth() / 2, infiniteCanvas.getHeight() / 2);

		ViewportPolicy viewportPolicy = getViewer().getRootPart()
				.getAdapter(ViewportPolicy.class);
		if (viewportPolicy == null) {
			throw new IllegalStateException(
					"Cannot perform ResetZoomAction, because no ViewportPolicy can be determined.");
		}

		viewportPolicy.init();
		viewportPolicy.zoom(false, false, sx, pivotInScene.getX(),
				pivotInScene.getY());
		ITransactionalOperation operation = viewportPolicy.commit();
		return operation;
	}

	/**
	 * Returns the {@link InfiniteCanvas} of the viewer where this action is
	 * installed.
	 *
	 * @return The {@link InfiniteCanvas} of the viewer.
	 */
	protected InfiniteCanvas getInfiniteCanvas() {
		Parent canvas = getViewer().getCanvas();
		if (canvas instanceof InfiniteCanvas) {
			return (InfiniteCanvas) canvas;
		}
		return null;
	}

	/**
	 * Returns the relative zoom factor that is applied when performing this
	 * action.
	 *
	 * @return The relative zoom factor that is applied when performing this
	 *         action.
	 */
	protected double getZoomFactor() {
		return 0.8;
	}
}
