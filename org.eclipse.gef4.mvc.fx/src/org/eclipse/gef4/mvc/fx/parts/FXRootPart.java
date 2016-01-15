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

import org.eclipse.gef4.mvc.parts.IContentPart;
import org.eclipse.gef4.mvc.parts.IFeedbackPart;
import org.eclipse.gef4.mvc.parts.IHandlePart;
import org.eclipse.gef4.mvc.parts.IVisualPart;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Bounds;
import javafx.scene.Group;
import javafx.scene.Node;

/**
 * The {@link FXRootPart} is an {@link AbstractFXRootPart} that is parameterized
 * by {@link Group}. It manages a number of layers for the visualization,
 * namely, the content layer, feedback layer, and handle layer. The visuals of
 * the different {@link IVisualPart}s are inserted into these layers depending
 * on their type, i.e. {@link IContentPart} visuals are inserted into the
 * content layer, {@link IFeedbackPart} visuals are inserted into the feedback
 * layer, and {@link IHandlePart} visuals are inserted into the handle layer.
 * <p>
 * The layers are stacked on top of each other with the content layer at the
 * bottom and the handle layer at the top. The feedback layer in the middle is
 * mouse transparent, i.e. you cannot interact with the visuals in this layer.
 *
 * @author anyssen
 *
 */
public class FXRootPart extends AbstractFXRootPart<Group> {

	/**
	 * The content layer visual.
	 */
	public Group contentLayer;

	/**
	 * The handle layer visual.
	 */
	public Group handleLayer;

	/**
	 * The feedback layer visual.
	 */
	public Group feedbackLayer;

	/**
	 * Default constructor.
	 */
	public FXRootPart() {
	}

	@Override
	protected void addChildVisual(IVisualPart<Node, ? extends Node> child,
			int index) {
		if (child instanceof IContentPart) {
			int contentLayerIndex = 0;
			for (int i = 0; i < index; i++) {
				if (i < getChildrenUnmodifiable().size()
						&& getChildrenUnmodifiable()
								.get(i) instanceof IContentPart) {
					contentLayerIndex++;
				}
			}
			getContentLayer().getChildren().add(contentLayerIndex,
					child.getVisual());
		} else if (child instanceof IFeedbackPart) {
			int feedbackLayerIndex = 0;
			for (int i = 0; i < index; i++) {
				if (i < getChildrenUnmodifiable().size()
						&& (getChildrenUnmodifiable()
								.get(i) instanceof IFeedbackPart)) {
					feedbackLayerIndex++;
				}
			}
			getFeedbackLayer().getChildren().add(feedbackLayerIndex,
					child.getVisual());
		} else {
			int handleLayerIndex = 0;
			for (int i = 0; i < index; i++) {
				if (i < getChildrenUnmodifiable().size()
						&& (getChildrenUnmodifiable()
								.get(i) instanceof IHandlePart)) {
					handleLayerIndex++;
				}
			}
			getHandleLayer().getChildren().add(handleLayerIndex,
					child.getVisual());
		}
	}

	/**
	 * Creates the content layer visual.
	 *
	 * @return The content layer visual.
	 */
	protected Group createContentLayer() {
		Group contentLayer = createLayer(false);
		contentLayer.setPickOnBounds(true);
		return contentLayer;
	}

	/**
	 * Creates the feedback layer visual.
	 *
	 * @return The feedback layer visual.
	 */
	protected Group createFeedbackLayer() {
		return createLayer(true);
	}

	/**
	 * Creates the handle layer visual.
	 *
	 * @return The handle layer visual.
	 */
	protected Group createHandleLayer() {
		return createLayer(false);
	}

	/**
	 * Creates a {@link Group} and sets its {@link Group#pickOnBoundsProperty()}
	 * to <code>false</code>. Does also set its
	 * {@link Group#mouseTransparentProperty()} to the given value.
	 *
	 * @param mouseTransparent
	 *            The value for the layer's
	 *            {@link Group#mouseTransparentProperty()}.
	 * @return The created layer.
	 */
	protected Group createLayer(boolean mouseTransparent) {
		Group layer = new Group();
		layer.setPickOnBounds(false);
		layer.setMouseTransparent(mouseTransparent);
		return layer;
	}

	@Override
	protected Group createVisual() {
		contentLayer = createContentLayer();
		/*
		 * XXX: The following is a workaround to ensure that visuals do not
		 * disappear when the content layer is scaled (zooming). This is,
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
		contentLayer.boundsInLocalProperty()
				.addListener(new ChangeListener<Bounds>() {
					@Override
					public void changed(
							ObservableValue<? extends Bounds> observable,
							Bounds oldValue, Bounds newValue) {
					}
				});

		feedbackLayer = createFeedbackLayer();

		handleLayer = createHandleLayer();

		return new Group(contentLayer, feedbackLayer, handleLayer);
	}

	@Override
	public void doRefreshVisual(Group visual) {
		// nothing to do
	}

	/**
	 * Returns the content layer visual. The content layer visual is created in
	 * case it was not created before.
	 *
	 * @see #createContentLayer()
	 *
	 * @return The content layer visual.
	 */
	protected Group getContentLayer() {
		if (contentLayer == null) {
			createVisual();
		}
		return contentLayer;
	}

	/**
	 * Returns the feedback layer visual. The feedback layer visual is created
	 * in case it was not created before.
	 *
	 * @see #createFeedbackLayer()
	 *
	 * @return The feedback layer visual.
	 */
	protected Group getFeedbackLayer() {
		if (feedbackLayer == null) {
			createVisual();
		}
		return feedbackLayer;
	}

	/**
	 * Returns the handle layer visual. The handle layer visual is created in
	 * case it was not created before.
	 *
	 * @see #createHandleLayer()
	 *
	 * @return The handle layer visual.
	 */
	protected Group getHandleLayer() {
		if (handleLayer == null) {
			createVisual();
		}
		return handleLayer;
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

}
