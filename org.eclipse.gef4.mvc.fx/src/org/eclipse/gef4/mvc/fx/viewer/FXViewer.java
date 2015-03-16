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
package org.eclipse.gef4.mvc.fx.viewer;

import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;

import org.eclipse.gef4.fx.nodes.FXGridLayer;
import org.eclipse.gef4.fx.nodes.ScrollPaneEx;
import org.eclipse.gef4.mvc.fx.domain.FXDomain;
import org.eclipse.gef4.mvc.fx.parts.FXRootPart;
import org.eclipse.gef4.mvc.parts.IRootPart;
import org.eclipse.gef4.mvc.parts.IVisualPart;
import org.eclipse.gef4.mvc.viewer.AbstractViewer;

public class FXViewer extends AbstractViewer<Node> {

	/**
	 * Per default, a ScrollPane draws a border and background color. We do not
	 * want either.
	 */
	private static final String SCROLL_PANE_STYLE = "-fx-background-insets:0;-fx-padding:0;-fx-background-color:rgba(0,0,0,0);";

	protected ISceneContainer sceneContainer;
	protected Scene scene = null;
	protected ScrollPaneEx scrollPane;
	protected FXGridLayer gridLayer;

	/**
	 * Creates a {@link Scene}, inserts the given root visual into it, and sets
	 * that {@link Scene} on the given {@link ISceneContainer}.
	 *
	 * @param container
	 *            The container for the {@link Scene}.
	 * @param rootVisual
	 *            The visual of the {@link FXRootPart}.
	 */
	@SuppressWarnings("unchecked")
	protected void createAndHookScene(ISceneContainer container,
			Parent rootVisual) {
		scrollPane = new ScrollPaneEx();
		scrollPane.setStyle(SCROLL_PANE_STYLE);

		gridLayer = new FXGridLayer();
		scrollPane.getContentGroup().getChildren().addAll(rootVisual);
		scrollPane.getScrolledPane().getChildren().add(gridLayer);
		gridLayer.toBack();

		// bind grid layer size
		SimpleObjectProperty<Bounds> scrollableBoundsProperty = new SimpleObjectProperty<Bounds>() {
			{
				bind(scrollPane.getScrollableBoundsBinding());
			}
		};
		gridLayer.bindMinSizeToBounds(scrollableBoundsProperty);
		gridLayer
				.bindPrefSizeToUnionedBounds(new ReadOnlyObjectProperty[] { scrollableBoundsProperty });

		scene = new Scene(scrollPane);
		sceneContainer.setScene(scene);
	}

	@Override
	public FXDomain getDomain() {
		return (FXDomain) super.getDomain();
	}

	public FXGridLayer getGridLayer() {
		return gridLayer;
	}

	public Scene getScene() {
		return scene;
	}

	public ScrollPaneEx getScrollPane() {
		return scrollPane;
	}

	@Override
	public void reveal(IVisualPart<Node, ? extends Node> visualPart) {
		scrollPane.reveal(visualPart.getVisual());
	}

	public void setSceneContainer(ISceneContainer sceneContainer) {
		this.sceneContainer = sceneContainer;
		if (sceneContainer != null) {
			if (scene == null) {
				IRootPart<Node, ? extends Node> rootPart = getRootPart();
				if (rootPart != null) {
					createAndHookScene(sceneContainer,
							(Parent) rootPart.getVisual());
				}
			} else {
				sceneContainer.setScene(scene);
			}
		}
	}

}