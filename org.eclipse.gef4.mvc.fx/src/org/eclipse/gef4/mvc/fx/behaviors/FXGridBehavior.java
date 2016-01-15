/*******************************************************************************
 * Copyright (c) 2014, 2015 itemis AG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Alexander Nyßen (itemis AG) - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.gef4.mvc.fx.behaviors;

import org.eclipse.gef4.fx.nodes.InfiniteCanvas;
import org.eclipse.gef4.mvc.behaviors.AbstractBehavior;
import org.eclipse.gef4.mvc.fx.parts.FXRootPart;
import org.eclipse.gef4.mvc.fx.viewer.FXViewer;
import org.eclipse.gef4.mvc.models.GridModel;

import javafx.scene.Node;

/**
 * The {@link FXGridBehavior} can be registered on an {@link FXRootPart} to
 * apply the information from the {@link GridModel} to the background grid that
 * is managed by the {@link FXViewer}.
 *
 * @author anyssen
 *
 */
public class FXGridBehavior extends AbstractBehavior<Node> {

	@Override
	protected void doActivate() {
		GridModel gridModel = getHost().getRoot().getViewer()
				.getAdapter(GridModel.class);
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
	 * {@link FXViewer}.
	 *
	 * @return The {@link InfiniteCanvas} of the {@link #getHost() host's}
	 *         {@link FXViewer}.
	 */
	protected InfiniteCanvas getCanvas() {
		return ((FXViewer) getHost().getRoot().getViewer()).getCanvas();
	}

}
