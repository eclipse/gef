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
package org.eclipse.gef4.mvc.fx.parts;

import java.util.Map;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Bounds;
import javafx.scene.Group;
import javafx.scene.Node;

import org.eclipse.gef4.mvc.parts.IContentPart;
import org.eclipse.gef4.mvc.parts.IFeedbackPart;
import org.eclipse.gef4.mvc.parts.IHandlePart;
import org.eclipse.gef4.mvc.parts.IVisualPart;
import org.eclipse.gef4.mvc.viewer.IViewer;

public class FXRootPart extends AbstractFXRootPart<Group> {

	public Group contentLayer;
	public Group handleLayer;
	public Group feedbackLayer;

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

	protected Group createHandleLayer() {
		return createLayer(false);
	}

	protected Group createLayer(boolean mouseTransparent) {
		Group layer = new Group();
		layer.setPickOnBounds(false);
		layer.setMouseTransparent(mouseTransparent);
		return layer;
	}

	protected Group createScrollPaneContent(Node... layers) {
		return new Group(layers);
	}

	@Override
	protected Group createVisual() {
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

		return createScrollPaneContent(new Node[] { contentLayer,
				feedbackLayer, handleLayer });
	}

	@Override
	public void doRefreshVisual(Group visual) {
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

	protected Group getHandleLayer() {
		if (handleLayer == null) {
			createVisual();
		}
		return handleLayer;
	}

	@Override
	protected void registerAtVisualPartMap(IViewer<Node> viewer, Group visual) {
		Map<Node, IVisualPart<Node, ? extends Node>> registry = viewer
				.getVisualPartMap();
		registry.put(getVisual(), this);
		for (Node child : getVisual().getChildren()) {
			// register root edit part also for the layers
			registry.put(child, this);
		}
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

	@Override
	protected void unregisterFromVisualPartMap(IViewer<Node> viewer,
			Group visual) {
		Map<Node, IVisualPart<Node, ? extends Node>> registry = viewer
				.getVisualPartMap();
		registry.remove(getVisual());
		for (Node child : getVisual().getChildren()) {
			// register root edit part also for the layers
			registry.remove(child);
		}
	}

}
