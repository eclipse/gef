/*******************************************************************************
 * Copyright (c) 2016 itemis AG and others.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Matthias Wienand (itemis AG) - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.gef.mvc.examples.logo;

import org.eclipse.gef.fx.nodes.InfiniteCanvas;
import org.eclipse.gef.mvc.examples.logo.behaviors.PaletteFocusBehavior;
import org.eclipse.gef.mvc.fx.viewer.IViewer;
import org.eclipse.gef.mvc.fx.viewer.InfiniteCanvasViewer;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.scene.Parent;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;

public class MvcLogoExampleViewersComposite {

	private static final double PALETTE_INDICATOR_WIDTH = 10d;
	private final HBox composite;

	public MvcLogoExampleViewersComposite(IViewer contentViewer, IViewer paletteViewer) {
		// determine viewers' root nodes
		Parent contentRootNode = contentViewer.getCanvas();
		final InfiniteCanvas paletteRootNode = ((InfiniteCanvasViewer) paletteViewer).getCanvas();

		// arrange viewers above each other
		AnchorPane viewersPane = new AnchorPane();
		viewersPane.getChildren().addAll(contentRootNode, paletteRootNode);

		// create palette indicator
		Pane paletteIndicator = new Pane();
		paletteIndicator.setStyle("-fx-background-color: rgba(128,128,128,1);");
		paletteIndicator.setMaxSize(PALETTE_INDICATOR_WIDTH, Double.MAX_VALUE);
		paletteIndicator.setMinSize(PALETTE_INDICATOR_WIDTH, 0d);

		// show palette indicator next to the viewer area
		composite = new HBox();
		// XXX: Set transparent background for the composite HBox, because
		// otherwise, the HBox will have a grey background.
		composite.setStyle("-fx-background-color: transparent;");
		composite.getChildren().addAll(paletteIndicator, viewersPane);

		// ensure composite fills the whole space
		composite.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
		composite.setMinSize(0, 0);
		composite.setFillHeight(true);

		// no spacing between viewers and palette indicator
		composite.setSpacing(0d);

		// ensure viewers fill the space
		HBox.setHgrow(viewersPane, Priority.ALWAYS);
		viewersPane.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
		AnchorPane.setBottomAnchor(contentRootNode, 0d);
		AnchorPane.setLeftAnchor(contentRootNode, 0d);
		AnchorPane.setRightAnchor(contentRootNode, 0d);
		AnchorPane.setTopAnchor(contentRootNode, 0d);
		AnchorPane.setBottomAnchor(paletteRootNode, 0d);
		AnchorPane.setLeftAnchor(paletteRootNode, 0d);
		AnchorPane.setTopAnchor(paletteRootNode, 0d);

		// disable grid layer for palette
		paletteRootNode.setZoomGrid(false);
		paletteRootNode.setShowGrid(false);

		// disable horizontal scrollbar for palette
		paletteRootNode.setHorizontalScrollBarPolicy(ScrollBarPolicy.NEVER);

		// set palette background
		paletteRootNode.setStyle(PaletteFocusBehavior.DEFAULT_STYLE);

		// hide palette at first
		paletteRootNode.setVisible(false);

		// register listener to show/hide palette
		paletteIndicator.setOnMouseEntered(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				paletteRootNode.setVisible(true);
			}
		});
		paletteRootNode.setOnMouseExited(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				paletteRootNode.setVisible(false);
			}
		});

		// register listeners to update the palette width
		paletteRootNode.getContentGroup().layoutBoundsProperty().addListener(new ChangeListener<Bounds>() {
			@Override
			public void changed(ObservableValue<? extends Bounds> observable, Bounds oldValue, Bounds newValue) {
				double scrollBarWidth = paletteRootNode.getVerticalScrollBar().isVisible()
						? paletteRootNode.getVerticalScrollBar().getLayoutBounds().getWidth() : 0;
				paletteRootNode.setPrefWidth(newValue.getWidth() + scrollBarWidth);
			}
		});
		paletteRootNode.getVerticalScrollBar().visibleProperty().addListener(new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
				double contentWidth = paletteRootNode.getContentGroup().getLayoutBounds().getWidth();
				double scrollBarWidth = newValue ? paletteRootNode.getVerticalScrollBar().getLayoutBounds().getWidth()
						: 0;
				paletteRootNode.setPrefWidth(contentWidth + scrollBarWidth);
			}
		});

		// hide palette when a palette element is pressed
		paletteRootNode.addEventHandler(MouseEvent.MOUSE_PRESSED, new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				if (event.getTarget() != paletteRootNode) {
					paletteRootNode.setVisible(false);
				}
			}
		});
	}

	public Parent getComposite() {
		return composite;
	}

}
