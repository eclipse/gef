/*******************************************************************************
 * Copyright (c) 2014, 2016 itemis AG and others.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Alexander Nyßen (itemis AG) - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.gef.mvc.fx.behaviors;

import org.eclipse.gef.fx.nodes.InfiniteCanvas;
import org.eclipse.gef.mvc.fx.models.GridModel;
import org.eclipse.gef.mvc.fx.parts.IRootPart;
import org.eclipse.gef.mvc.fx.viewer.InfiniteCanvasViewer;

/**
 * The {@link GridBehavior} can be registered on an {@link IRootPart} to apply
 * the information from the {@link GridModel} to the background grid that is
 * managed by the {@link InfiniteCanvasViewer}.
 *
 * @author anyssen
 *
 */
public class GridBehavior extends AbstractBehavior {

	@Override
	protected void doActivate() {
		GridModel gridModel = getHost().getRoot().getViewer()
				.getAdapter(GridModel.class);
		if (gridModel == null) {
			throw new IllegalStateException(
					"Unable to retrieve GridModel viewer adapter. Please check your adapter bindings.");
		}
		InfiniteCanvas canvas = getCanvas();
		canvas.showGridProperty().bind(gridModel.showGridProperty());
		canvas.zoomGridProperty().bind(gridModel.zoomGridProperty());
		canvas.gridCellWidthProperty().bind(gridModel.gridCellWidthProperty());
		canvas.gridCellHeightProperty()
				.bind(gridModel.gridCellHeightProperty());
	}

	@Override
	protected void doDeactivate() {
		InfiniteCanvas canvas = getCanvas();
		canvas.showGridProperty().unbind();
		canvas.zoomGridProperty().unbind();
		canvas.gridCellWidthProperty().unbind();
		canvas.gridCellHeightProperty().unbind();
	}

	/**
	 * Returns the {@link InfiniteCanvas} of the {@link #getHost() host's}
	 * {@link InfiniteCanvasViewer}.
	 *
	 * @return The {@link InfiniteCanvas} of the {@link #getHost() host's}
	 *         {@link InfiniteCanvasViewer}.
	 */
	protected InfiniteCanvas getCanvas() {
		return ((InfiniteCanvasViewer) getHost().getRoot().getViewer())
				.getCanvas();
	}

}
