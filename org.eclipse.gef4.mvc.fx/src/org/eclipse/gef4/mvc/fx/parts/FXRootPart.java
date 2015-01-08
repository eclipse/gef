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

import java.util.Map;

import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Bounds;
import javafx.scene.Group;
import javafx.scene.Node;

import org.eclipse.gef4.fx.nodes.FXGridLayer;
import org.eclipse.gef4.fx.nodes.ScrollPaneEx;
import org.eclipse.gef4.mvc.parts.IContentPart;
import org.eclipse.gef4.mvc.parts.IFeedbackPart;
import org.eclipse.gef4.mvc.parts.IHandlePart;
import org.eclipse.gef4.mvc.parts.IVisualPart;
import org.eclipse.gef4.mvc.viewer.IViewer;

public class FXRootPart extends AbstractFXRootPart<ScrollPaneEx> {

	/**
	 * Per default, a ScrollPane draws a border and background color. We do not
	 * want either.
	 */
	private static final String SCROLL_PANE_STYLE = "-fx-background-insets:0;-fx-padding:0;-fx-background-color:rgba(0,0,0,0);";

	private FXGridLayer gridLayer;
	public Group contentLayer;
	public Group handleLayer;
	public Group feedbackLayer;

	private Group scrollPaneContent;

	public FXRootPart() {
	}

	@Override
	protected void addChildVisual(IVisualPart<Node, ? extends Node> child,
			int index) {
		if (child instanceof IContentPart) {
			int contentLayerIndex = 0;
			for (int i = 0; i < index; i++) {
				if (i < getChildren().size()
						&& getChildren().get(i) instanceof IContentPart) {
					contentLayerIndex++;
				}
			}
			getContentLayer().getChildren().add(contentLayerIndex,
					child.getVisual());
		} else if (child instanceof IFeedbackPart) {
			int feedbackLayerIndex = 0;
			for (int i = 0; i < index; i++) {
				if (i < getChildren().size()
						&& (getChildren().get(i) instanceof IFeedbackPart)) {
					feedbackLayerIndex++;
				}
			}
			getFeedbackLayer().getChildren().add(feedbackLayerIndex,
					child.getVisual());
		} else {
			int handleLayerIndex = 0;
			for (int i = 0; i < index; i++) {
				if (i < getChildren().size()
						&& (getChildren().get(i) instanceof IHandlePart)) {
					handleLayerIndex++;
				}
			}
			getHandleLayer().getChildren().add(handleLayerIndex,
					child.getVisual());
		}
	}

	protected Group createContentLayer() {
		Group contentLayer = createLayer(false);
		contentLayer.setPickOnBounds(true);
		return contentLayer;
	}

	protected Group createFeedbackLayer() {
		return createLayer(true);
	}

	protected FXGridLayer createGridLayer() {
		return new FXGridLayer();
	}

	protected Group createHandleLayer() {
		return createLayer(false);
	}

	protected Group createLayer(boolean mouseTransparent) {
		Group layer = new Group();
		layer.setPickOnBounds(false);
		layer.setMouseTransparent(mouseTransparent);
		return layer;
	}

	protected ScrollPaneEx createScrollPane(final Group scrollPaneInput) {
		ScrollPaneEx scrollPane = new ScrollPaneEx();
		scrollPane.getContentGroup().getChildren().add(scrollPaneInput);
		scrollPane.setStyle(SCROLL_PANE_STYLE);
		return scrollPane;
	}

	protected Group createScrollPaneContent(Node... layers) {
		return new Group(layers);
	}

	@Override
	protected ScrollPaneEx createVisual() {
		contentLayer = createContentLayer();
		/*
		 * IMPORTANT: The following is a workaround to ensure that visuals do
		 * not disappear when the content layer is scaled (zooming). This is,
		 * because computeBounds() on the (lazy) bounds-in-local property of the
		 * content layer is not performed when the property is invalidated.
		 * 
		 * We could register an invalidation listener that explicitly triggers
		 * computeBounds() (by calling get() on the bounds-in-local property),
		 * to fix the problems. However, this would be invoked too often.
		 * 
		 * Instead, we register a dummy change listener (that actually does not
		 * do anything) to fix the problem by means of a side effect. This is
		 * sufficient to fix the problems, because the JavaFX ExpressionHelper
		 * (which is responsible of firing the change events) calls getValue()
		 * on the observable when a change event is to be fired (which is
		 * triggered when at least one change listener is registered). The
		 * getValue() call will in turn recompute the bounds (by calling get()
		 * on the bounds-in-local property, which triggers a call to
		 * computeBounds()). If no listener is registered, the bounds-in-local
		 * value will not be recomputed (computeBounds() will not be called)
		 * even if invalidated.
		 */
		contentLayer.boundsInLocalProperty().addListener(
				new ChangeListener<Bounds>() {
					@Override
					public void changed(
							ObservableValue<? extends Bounds> observable,
							Bounds oldValue, Bounds newValue) {
					}
				});

		feedbackLayer = createFeedbackLayer();

		handleLayer = createHandleLayer();

		gridLayer = createGridLayer();

		scrollPaneContent = createScrollPaneContent(new Node[] {// gridLayer,
		contentLayer, feedbackLayer, handleLayer });

		final ScrollPaneEx scrollPane = createScrollPane(scrollPaneContent);
		// put gridlayer next to the other layers
		scrollPane.getCanvas().getChildren().add(gridLayer);
		gridLayer.toBack();

		// TODO: These could each be extracted to a helper, because its generic
		// functionality not specific to a grid layer (ensure layer is as large
		// as viewport; ensure layer is as large as other layers).
		SimpleObjectProperty<Bounds> scrollableBoundsProperty = new SimpleObjectProperty<Bounds>() {
			{
				bind(scrollPane.getScrollableBoundsBinding());
			}
		};
		gridLayer.bindMinSizeToBounds(scrollableBoundsProperty);
		gridLayer
				.bindPrefSizeToUnionedBounds(new ReadOnlyObjectProperty[] { scrollableBoundsProperty });

		return scrollPane;
	}

	@Override
	public void doRefreshVisual(ScrollPaneEx visual) {
		// nothing to do
	}

	protected Group getContentLayer() {
		if (contentLayer == null) {
			createVisual();
		}
		return contentLayer;
	}

	protected Group getFeedbackLayer() {
		if (feedbackLayer == null) {
			createVisual();
		}
		return feedbackLayer;
	}

	public FXGridLayer getGridLayer() {
		if (gridLayer == null) {
			createVisual();
		}
		return gridLayer;
	}

	protected Group getHandleLayer() {
		if (handleLayer == null) {
			createVisual();
		}
		return handleLayer;
	}

	public ScrollPaneEx getScrollPane() {
		return getVisual();
	}

	public Group getScrollPaneContent() {
		if (scrollPaneContent == null) {
			createVisual();
		}
		return scrollPaneContent;
	}

	@Override
	protected void registerAtVisualPartMap(IViewer<Node> viewer,
			ScrollPaneEx visual) {
		Group scrollPaneContent = (Group) visual.getContentGroup()
				.getChildren().get(0);
		Map<Node, IVisualPart<Node, ? extends Node>> registry = viewer
				.getVisualPartMap();
		registry.put(scrollPaneContent, this);
		for (Node child : scrollPaneContent.getChildren()) {
			// register root edit part also for the layers
			registry.put(child, this);
		}

		// register root visual as well
		registry.put(getVisual(), this);
	}

	@Override
	protected void removeChildVisual(IVisualPart<Node, ? extends Node> child,
			int index) {
		if (child instanceof IContentPart) {
			getContentLayer().getChildren().remove(child.getVisual());
		} else if (child instanceof IFeedbackPart) {
			getFeedbackLayer().getChildren().remove(child.getVisual());
		} else {
			getHandleLayer().getChildren().remove(child.getVisual());
		}
	}

	public void removeGridLayer() {
		FXGridLayer gridLayer = getGridLayer();
		if (!scrollPaneContent.getChildren().contains(gridLayer)) {
			return;
		}
		scrollPaneContent.getChildren().remove(gridLayer);
		// TODO: unbind the grid layer
	}

	@Override
	protected void unregisterFromVisualPartMap(IViewer<Node> viewer,
			ScrollPaneEx visual) {
		Group scrollPaneContent = (Group) visual.getContentGroup()
				.getChildren().get(0);
		Map<Node, IVisualPart<Node, ? extends Node>> registry = viewer
				.getVisualPartMap();
		registry.remove(scrollPaneContent);
		for (Node child : scrollPaneContent.getChildren()) {
			// register root edit part also for the layers
			registry.remove(child);
		}

		// unregister root visual as well
		registry.remove(getVisual());
	}

}
