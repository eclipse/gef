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

import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;

public class FXViewer extends AbstractViewer<Node> {

	/**
	 * Per default, a ScrollPane draws a border and background color. We do not
	 * want either.
	 */
	private static final String SCROLL_PANE_STYLE = "-fx-background-insets:0;-fx-padding:0;-fx-background-color:rgba(0,0,0,0);";

	protected ScrollPaneEx scrollPane;
	protected FXGridLayer gridLayer;

	@Override
	public FXDomain getDomain() {
		return (FXDomain) super.getDomain();
	}

	public FXGridLayer getGridLayer() {
		return gridLayer;
	}

	public Scene getScene() {
		return scrollPane.getScene();
	}

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

				// bind grid layer size
				SimpleObjectProperty<Bounds> scrollableBoundsProperty = new SimpleObjectProperty<Bounds>() {
					{
						bind(scrollPane.getScrollableBoundsBinding());
					}
				};
				gridLayer.bindMinSizeToBounds(scrollableBoundsProperty);
				gridLayer.bindPrefSizeToUnionedBounds(
						new ReadOnlyObjectProperty[] {
								scrollableBoundsProperty });
			}
		}
		return scrollPane;
	}

	@Override
	public void reveal(IVisualPart<Node, ? extends Node> visualPart) {
		scrollPane.reveal(visualPart.getVisual());
	}

}