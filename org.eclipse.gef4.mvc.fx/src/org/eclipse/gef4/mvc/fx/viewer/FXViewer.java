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
package org.eclipse.gef4.mvc.fx.viewer;

import org.eclipse.gef4.fx.nodes.FXGridLayer;
import org.eclipse.gef4.fx.nodes.InfiniteCanvas;
import org.eclipse.gef4.mvc.fx.domain.FXDomain;
import org.eclipse.gef4.mvc.parts.IRootPart;
import org.eclipse.gef4.mvc.parts.IVisualPart;
import org.eclipse.gef4.mvc.viewer.AbstractViewer;

import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;

/**
 * The {@link FXViewer} is an {@link AbstractViewer} that is parameterized by
 * {@link Node}. It manages a {@link InfiniteCanvas} and an {@link FXGridLayer}.
 * The scroll pane displays the viewer's contents and adds scrollbars when
 * necessary. The grid layer displays a grid in the background when enabled.
 *
 * @author anyssen
 *
 */
public class FXViewer extends AbstractViewer<Node> {

	/**
	 * Defines the default CSS styling for the {@link InfiniteCanvas}: no
	 * background, no border.
	 */
	private static final String CANVAS_STYLE = "-fx-background-insets:0;-fx-padding:0;-fx-background-color:rgba(0,0,0,0);";

	/**
	 * The {@link InfiniteCanvas} that displays the viewer's contents.
	 */
	protected InfiniteCanvas infiniteCanvas;

	/**
	 * The {@link FXGridLayer} that displays the grid in the background.
	 */
	protected FXGridLayer gridLayer;

	/**
	 * Returns the {@link InfiniteCanvas} that is managed by this
	 * {@link FXViewer} .
	 *
	 * @return The {@link InfiniteCanvas} that is managed by this
	 *         {@link FXViewer} .
	 */
	// TODO: if (scrollPane == null) createVisuals();
	public InfiniteCanvas getCanvas() {
		if (infiniteCanvas == null) {
			IRootPart<Node, ? extends Node> rootPart = getRootPart();
			if (rootPart != null) {
				infiniteCanvas = new InfiniteCanvas();
				infiniteCanvas.setStyle(CANVAS_STYLE);

				gridLayer = new FXGridLayer();
				infiniteCanvas.getContentGroup().getChildren()
						.addAll((Parent) rootPart.getVisual());
				infiniteCanvas.getScrolledPane().getChildren().add(gridLayer);
				gridLayer.toBack();

				// bind translation and bounds of grid layer
				gridLayer.gridTransformProperty().get().txProperty()
						.bind(infiniteCanvas.contentTransformProperty().get()
								.txProperty());
				gridLayer.gridTransformProperty().get().tyProperty()
						.bind(infiniteCanvas.contentTransformProperty().get()
								.tyProperty());
				gridLayer.bindBounds(infiniteCanvas.scrollableBoundsProperty());
			}
		}
		return infiniteCanvas;
	}

	@Override
	public FXDomain getDomain() {
		return (FXDomain) super.getDomain();
	}

	/**
	 * Returns the {@link FXGridLayer} that is managed by this {@link FXViewer}.
	 *
	 * @return The {@link FXGridLayer} that is managed by this {@link FXViewer}.
	 */
	// TODO: if (gridLayer == null) createVisuals();
	public FXGridLayer getGridLayer() {
		return gridLayer;
	}

	/**
	 * Returns the {@link Scene} in which the {@link InfiniteCanvas} of this
	 * {@link FXViewer} is displayed.
	 *
	 * @return The {@link Scene} in which the {@link InfiniteCanvas} of this
	 *         {@link FXViewer} is displayed.
	 */
	public Scene getScene() {
		return infiniteCanvas.getScene();
	}

	@Override
	public void reveal(IVisualPart<Node, ? extends Node> visualPart) {
		infiniteCanvas.reveal(visualPart.getVisual());
	}

}