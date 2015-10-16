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
import org.eclipse.gef4.fx.nodes.ScrollPaneEx;
import org.eclipse.gef4.mvc.fx.domain.FXDomain;
import org.eclipse.gef4.mvc.parts.IRootPart;
import org.eclipse.gef4.mvc.parts.IVisualPart;
import org.eclipse.gef4.mvc.viewer.AbstractViewer;

import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;

/**
 * The {@link FXViewer} is an {@link AbstractViewer} that is parameterized by
 * {@link Node}. It manages a {@link ScrollPaneEx} and an {@link FXGridLayer}.
 * The scroll pane displays the viewer's contents and adds scrollbars when
 * necessary. The grid layer displays a grid in the background when enabled.
 *
 * @author anyssen
 *
 */
public class FXViewer extends AbstractViewer<Node> {

	/**
	 * Per default, a ScrollPane draws a border and background color. We do not
	 * want either.
	 */
	private static final String SCROLL_PANE_STYLE = "-fx-background-insets:0;-fx-padding:0;-fx-background-color:rgba(0,0,0,0);";

	/**
	 * The {@link ScrollPaneEx} that displays the viewer's contents.
	 */
	protected ScrollPaneEx scrollPane;

	/**
	 * The {@link FXGridLayer} that displays the grid in the background.
	 */
	protected FXGridLayer gridLayer;

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
	 * Returns the {@link Scene} in which the {@link ScrollPaneEx} of this
	 * {@link FXViewer} is displayed.
	 *
	 * @return The {@link Scene} in which the {@link ScrollPaneEx} of this
	 *         {@link FXViewer} is displayed.
	 */
	public Scene getScene() {
		return scrollPane.getScene();
	}

	/**
	 * Returns the {@link ScrollPaneEx} that is managed by this {@link FXViewer}
	 * .
	 *
	 * @return The {@link ScrollPaneEx} that is managed by this {@link FXViewer}
	 *         .
	 */
	// TODO: if (scrollPane == null) createVisuals();
	@SuppressWarnings("unchecked")
	public ScrollPaneEx getScrollPane() {
		if (scrollPane == null) {
			IRootPart<Node, ? extends Node> rootPart = getRootPart();
			if (rootPart != null) {
				scrollPane = new ScrollPaneEx();
				scrollPane.setStyle(SCROLL_PANE_STYLE);

				gridLayer = new FXGridLayer();
				scrollPane.getContentGroup().getChildren()
						.addAll((Parent) rootPart.getVisual());
				scrollPane.getScrolledPane().getChildren().add(gridLayer);
				gridLayer.toBack();

				gridLayer.bindBounds(
						scrollPane.scrollableBoundsProperty());
			}
		}
		return scrollPane;
	}

	@Override
	public void reveal(IVisualPart<Node, ? extends Node> visualPart) {
		scrollPane.reveal(visualPart.getVisual());
	}

}