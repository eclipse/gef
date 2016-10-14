/*******************************************************************************
 * Copyright (c) 2014, 2016 itemis AG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Alexander Nyßen (itemis AG) - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.gef.mvc.fx.behaviors;

import org.eclipse.gef.fx.nodes.InfiniteCanvas;
import org.eclipse.gef.mvc.fx.models.GridModel;
import org.eclipse.gef.mvc.fx.parts.RootPart;
import org.eclipse.gef.mvc.fx.viewer.Viewer;

/**
 * The {@link GridBehavior} can be registered on an {@link RootPart} to apply
 * the information from the {@link GridModel} to the background grid that is
 * managed by the {@link Viewer}.
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
	 * {@link Viewer}.
	 *
	 * @return The {@link InfiniteCanvas} of the {@link #getHost() host's}
	 *         {@link Viewer}.
	 */
	protected InfiniteCanvas getCanvas() {
		return ((Viewer) getHost().getRoot().getViewer()).getCanvas();
	}

}
