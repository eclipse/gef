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
import org.eclipse.gef.mvc.fx.operations.ChangeViewportOperation;
import org.eclipse.gef.mvc.fx.operations.ITransactionalOperation;
import org.eclipse.gef.mvc.fx.ui.MvcFxUiBundle;
import org.eclipse.gef.mvc.fx.viewer.IViewer;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.widgets.Event;

import javafx.scene.Parent;

/**
 * The {@link FitToViewportAction} is an {@link IViewerAction} that will zoom
 * and scroll the {@link IViewer#getCanvas()} (provided that it is an
 * {@link InfiniteCanvas}) so that its contents are centered and fill the
 * viewport. However, the zoom factor is restricted to the range from 0.25 to
 * 4.0, which can be customized via subclassing ({@link #getMinZoom()},
 * {@link #getMaxZoom()}).
 *
 * @author mwienand
 *
 */
public class FitToViewportAction extends AbstractViewerAction {

	/**
	 * Constructs a new {@link FitToViewportAction}.
	 */
	public FitToViewportAction() {
		this("Fit-To-Viewport", IAction.AS_PUSH_BUTTON,
				MvcFxUiBundle.getDefault().getImageRegistry().getDescriptor(
						MvcFxUiBundle.IMG_ICONS_FIT_TO_VIEWPORT));
	}

	/**
	 * Constructs a new {@link FitToViewportAction} with the given text and
	 * style. Also sets the given {@link ImageDescriptor} for this action.
	 *
	 * @param text
	 *            Text for the action.
	 * @param style
	 *            Style for the action, see {@link IAction} for details.
	 * @param imageDescriptor
	 *            {@link ImageDescriptor} specifying the icon for the action.
	 */
	protected FitToViewportAction(String text, int style,
			ImageDescriptor imageDescriptor) {
		super(text, style, imageDescriptor);
	}

	@Override
	protected ITransactionalOperation createOperation(Event event) {
		InfiniteCanvas infiniteCanvas = getInfiniteCanvas();
		if (infiniteCanvas == null) {
			throw new IllegalStateException(
					"Cannot perform FitToSizeAction, because no InfiniteCanvas can be determiend.");
		}
		if (infiniteCanvas.getContentBounds().isEmpty()) {
			// nothing to do
			return null;
		}

		ChangeViewportOperation viewportOperation = new ChangeViewportOperation(
				infiniteCanvas);
		infiniteCanvas.fitToSize(getMinZoom(), getMaxZoom());
		viewportOperation.setNewContentTransform(FX2Geometry
				.toAffineTransform(infiniteCanvas.getContentTransform()));
		viewportOperation.setNewHorizontalScrollOffset(
				infiniteCanvas.getHorizontalScrollOffset());
		viewportOperation.setNewVerticalScrollOffset(
				infiniteCanvas.getVerticalScrollOffset());
		return viewportOperation;
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
	 * Returns the maximum zoom level, which is <code>4.0</code> per default.
	 *
	 * @return The maximum zoom level.
	 */
	protected double getMaxZoom() {
		return 4;
	}

	/**
	 * Returns the minimum zoom level, which is <code>0.25</code> per default.
	 *
	 * @return The minimum zoom level.
	 */
	protected double getMinZoom() {
		return 0.25;
	}
}
