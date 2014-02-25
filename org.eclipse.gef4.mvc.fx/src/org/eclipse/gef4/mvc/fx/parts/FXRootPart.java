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
package org.eclipse.gef4.mvc.fx.parts;

import java.util.Arrays;
import java.util.List;

import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;

import org.eclipse.gef4.mvc.fx.viewer.FXViewer;
import org.eclipse.gef4.mvc.parts.AbstractRootPart;
import org.eclipse.gef4.mvc.parts.IContentPart;
import org.eclipse.gef4.mvc.parts.IVisualPart;
import org.eclipse.gef4.mvc.viewer.IVisualViewer;

public class FXRootPart extends AbstractRootPart<Node> {

	/**
	 * Per default, a ScrollPane draws a border and background color. We do not
	 * want either.
	 */
	private static final String SCROLL_PANE_STYLE = "-fx-background-insets:0;-fx-padding:0;-fx-background-color:rgba(0,0,0,0);";

	private ScrollPane scrollPane;
	private StackPane layersStackPane;

	private Pane contentLayer;
	private Pane handleLayer;
	private Pane feedbackLayer;

	private Parent scrollPaneInput;

	public FXRootPart() {
		createRootVisual();
	}

	protected void createRootVisual() {
		contentLayer = createContentLayer();
		handleLayer = createHandleLayer();
		feedbackLayer = createFeedbackLayer();

		layersStackPane = createLayersStackPane(Arrays.asList(new Pane[] {
				contentLayer, handleLayer, feedbackLayer }));

		scrollPaneInput = createScrollPaneInput(layersStackPane);

		scrollPane = createScrollPane(scrollPaneInput);
	}

	protected ScrollPane createScrollPane(Parent scrollPaneInput) {
		ScrollPane scrollPane = new ScrollPane();
		scrollPane.setContent(scrollPaneInput);
		scrollPane.setPannable(false);
		scrollPane.setStyle(SCROLL_PANE_STYLE);
		return scrollPane;
	}

	protected StackPane createLayersStackPane(List<Pane> layers) {
		StackPane layersStackPane = new StackPane();
		layersStackPane.getChildren().addAll(layers);
		return layersStackPane;
	}

	protected Pane createContentLayer() {
		return createLayer(false);
	}

	protected Pane createHandleLayer() {
		return createLayer(false);
	}

	protected Pane createFeedbackLayer() {
		Pane feedbackLayer = createLayer(true);
		return feedbackLayer;
	}

	protected Parent createScrollPaneInput(StackPane layersStackPane) {
		return new Group(layersStackPane);
	}

	protected Pane createLayer(boolean mouseTransparent) {
		Pane layer = new Pane();
		layer.setPickOnBounds(false);
		layer.setMouseTransparent(mouseTransparent);
		return layer;
	}

	public ScrollPane getScrollPane() {
		return scrollPane;
	}
	
	public StackPane getLayerStackPane(){
		return layersStackPane;
	}

	public Pane getHandleLayer() {
		return handleLayer;
	}

	public Pane getContentLayer() {
		return contentLayer;
	}

	public Pane getFeedbackLayer() {
		return feedbackLayer;
	}

	@Override
	public void setViewer(IVisualViewer<Node> newViewer) {
		if (getViewer() != null) {
			unregisterFromVisualPartMap();
		}
		if (newViewer != null && !(newViewer instanceof FXViewer)) {
			throw new IllegalArgumentException();
		}
		super.setViewer(newViewer);
		if (getViewer() != null) {
			registerAtVisualPartMap();
		}
	}

	@Override
	public FXViewer getViewer() {
		return (FXViewer) super.getViewer();
	}

	@Override
	public void refreshVisual() {
		// nothing to do
	}

	@Override
	protected void registerAtVisualPartMap() {
		getViewer().getVisualPartMap().put(layersStackPane, this);
		for (Node child : layersStackPane.getChildren()) {
			// register root edit part also for the layers
			getViewer().getVisualPartMap().put(child, this);
		}
		
		// register root visual as well
		getViewer().getVisualPartMap().put(getVisual(), this);
	}

	@Override
	protected void unregisterFromVisualPartMap() {
		getViewer().getVisualPartMap().remove(layersStackPane);
		for (Node child : layersStackPane.getChildren()) {
			// register root edit part also for the layers
			getViewer().getVisualPartMap().remove(child);
		}
		
		// unregister root visual as well
		getViewer().getVisualPartMap().remove(getVisual());
	}

	@Override
	public Node getVisual() {
		return scrollPane;
	}

	@Override
	protected void addChildVisual(IVisualPart<Node> child, int index) {
		if (child instanceof IContentPart) {
			int contentLayerIndex = 0;
			for (int i = 0; i < index; i++) {
				if (i < getChildren().size()
						&& getChildren().get(i) instanceof IContentPart) {
					contentLayerIndex++;
				}
			}
			contentLayer.getChildren()
					.add(contentLayerIndex, child.getVisual());
		} else {
			int handleLayerIndex = 0;
			for (int i = 0; i < index; i++) {
				if (i < getChildren().size()
						&& !(getChildren().get(i) instanceof IContentPart)) {
					handleLayerIndex++;
				}
			}
			handleLayer.getChildren().add(handleLayerIndex, child.getVisual());
		}
	}

	@Override
	protected void removeChildVisual(IVisualPart<Node> child) {
		if (child instanceof IContentPart) {
			contentLayer.getChildren().remove(child.getVisual());
		} else {
			handleLayer.getChildren().remove(child.getVisual());
		}
	}

}
