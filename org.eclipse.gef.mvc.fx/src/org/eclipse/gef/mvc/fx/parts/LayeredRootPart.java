/*******************************************************************************
 * Copyright (c) 2014, 2017 itemis AG and others.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Alexander Nyßen (itemis AG) - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.gef.mvc.fx.parts;

import java.util.List;

import org.eclipse.gef.mvc.fx.viewer.IViewer;

import com.google.common.collect.Multiset;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Bounds;
import javafx.scene.Group;
import javafx.scene.Node;

/**
 * The {@link LayeredRootPart} is an {@link IRootPart} that manages a number of
 * layers for the visualization, namely, the content layer, feedback layer, and
 * handle layer. The visuals of the different {@link IVisualPart}s are inserted
 * into these layers depending on their type, i.e. {@link IContentPart} visuals
 * are inserted into the content layer, {@link IFeedbackPart} visuals are
 * inserted into the feedback layer, and {@link IHandlePart} visuals are
 * inserted into the handle layer.
 * <p>
 * The layers are stacked on top of each other with the content layer at the
 * bottom and the handle layer at the top. The feedback layer in the middle is
 * mouse transparent, i.e. you cannot interact with the visuals in this layer.
 *
 * @author anyssen
 *
 */
public class LayeredRootPart extends AbstractVisualPart<Group>
		implements IRootPart<Group> {

	private Group contentLayer;
	private Group handleLayer;
	private Group feedbackLayer;

	/**
	 * Default constructor.
	 */
	public LayeredRootPart() {
	}

	@Override
	protected void activateChildren() {
		// activate content part children first (which might lead to the
		// creation of feedback and handle part children)
		for (IContentPart<? extends Node> child : getContentPartChildren()) {
			child.activate();
		}
		// activate remaining children
		for (IVisualPart<? extends Node> child : getChildrenUnmodifiable()) {
			if (!(child instanceof IContentPart)) {
				child.activate();
			}
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
	protected void deactivateChildren() {
		// deactivate content part children first (which might lead to the
		// removal of feedback and handle part children)
		for (IContentPart<? extends Node> child : getContentPartChildren()) {
			child.deactivate();
		}
		// deactivate remaining children
		for (IVisualPart<? extends Node> child : getChildrenUnmodifiable()) {
			if (!(child instanceof IContentPart)) {
				child.deactivate();
			}
		}
	}

	@Override
	protected IViewer determineViewer(IVisualPart<? extends Node> parent,
			Multiset<IVisualPart<? extends Node>> anchoreds) {
		// XXX: The root part is the only part that has a direct link to the
		// viewer. It should not be unregistered via the default mechanism, but
		// when the viewer is explicitly unset. (see #setAdaptable(IViewer)
		// below)
		return getViewer();
	}

	@Override
	protected void doAddChildVisual(IVisualPart<? extends Node> child,
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

	@Override
	protected void doAttachToAnchorageVisual(
			IVisualPart<? extends Node> anchorage, String role) {
		throw new UnsupportedOperationException(
				"IRootVisualPart does not support this");
	}

	@Override
	protected Group doCreateVisual() {
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
	protected void doDetachFromAnchorageVisual(
			IVisualPart<? extends Node> anchorage, String role) {
		throw new UnsupportedOperationException(
				"IRootVisualPart does not support this");
	}

	@Override
	protected void doRefreshVisual(Group visual) {
		// nothing to do
	}

	@Override
	protected void doRemoveChildVisual(IVisualPart<? extends Node> child,
			int index) {
		if (child instanceof IContentPart) {
			getContentLayer().getChildren().remove(child.getVisual());
		} else if (child instanceof IFeedbackPart) {
			getFeedbackLayer().getChildren().remove(child.getVisual());
		} else {
			getHandleLayer().getChildren().remove(child.getVisual());
		}
	}

	/**
	 * Returns the content layer visual. The content layer visual is created in
	 * case it was not created before.
	 *
	 * @see #createContentLayer()
	 *
	 * @return The content layer visual.
	 */
	public Group getContentLayer() {
		if (contentLayer == null) {
			doCreateVisual();
		}
		return contentLayer;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<IContentPart<? extends Node>> getContentPartChildren() {
		return PartUtils.filterParts(getChildrenUnmodifiable(),
				IContentPart.class);
	}

	/**
	 * Returns the feedback layer visual. The feedback layer visual is created
	 * in case it was not created before.
	 *
	 * @see #createFeedbackLayer()
	 *
	 * @return The feedback layer visual.
	 */
	public Group getFeedbackLayer() {
		if (feedbackLayer == null) {
			doCreateVisual();
		}
		return feedbackLayer;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<IFeedbackPart<? extends Node>> getFeedbackPartChildren() {
		return PartUtils.filterParts(getChildrenUnmodifiable(),
				IFeedbackPart.class);
	}

	/**
	 * Returns the handle layer visual. The handle layer visual is created in
	 * case it was not created before.
	 *
	 * @see #createHandleLayer()
	 *
	 * @return The handle layer visual.
	 */
	public Group getHandleLayer() {
		if (handleLayer == null) {
			doCreateVisual();
		}
		return handleLayer;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<IHandlePart<? extends Node>> getHandlePartChildren() {
		return PartUtils.filterParts(getChildrenUnmodifiable(),
				IHandlePart.class);
	}

	@Override
	public IRootPart<? extends Node> getRoot() {
		return this;
	}

}
