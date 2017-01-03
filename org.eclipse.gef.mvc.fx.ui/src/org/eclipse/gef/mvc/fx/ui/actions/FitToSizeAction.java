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
import org.eclipse.gef.mvc.fx.viewer.IViewer;
import org.eclipse.jface.action.Action;
import org.eclipse.swt.widgets.Event;

import javafx.scene.Parent;

/**
 * The {@link FitToSizeAction} is an {@link IViewerAction} that will zoom and
 * scroll the {@link IViewer#getCanvas()} (provided that it is an
 * {@link InfiniteCanvas}) so that its contents are centered and fill the
 * viewport. However, the zoom factor is restricted to the range from 0.25 to
 * 4.0, which can be customized via subclassing ({@link #getMinZoom()},
 * {@link #getMaxZoom()}).
 *
 * @author wienand
 *
 */
public class FitToSizeAction extends Action implements IViewerAction {

	private IViewer viewer;

	/**
	 * Returns the {@link InfiniteCanvas} of the viewer where this action is
	 * installed.
	 *
	 * @return The {@link InfiniteCanvas} of the viewer.
	 */
	protected InfiniteCanvas getInfiniteCanvas() {
		Parent canvas = viewer.getCanvas();
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

	/**
	 * Returns the {@link IViewer} for which this {@link IViewerAction} was
	 * {@link #init(IViewer) initialized}, or <code>null</code>.
	 *
	 * @return The {@link IViewer} or <code>null</code>.
	 */
	protected IViewer getViewer() {
		return viewer;
	}

	@Override
	public void init(IViewer viewer) {
		this.viewer = viewer;
	}

	@Override
	public void runWithEvent(Event event) {
		InfiniteCanvas infiniteCanvas = getInfiniteCanvas();
		if (infiniteCanvas != null) {
			infiniteCanvas.fitToSize(getMinZoom(), getMaxZoom());
		} else {
			throw new IllegalStateException(
					"Cannot perform FitToSizeAction, because no InfiniteCanvas can be determined for the active viewer '" //$NON-NLS-1$
							+ viewer + "'."); //$NON-NLS-1$
		}
	}
}
