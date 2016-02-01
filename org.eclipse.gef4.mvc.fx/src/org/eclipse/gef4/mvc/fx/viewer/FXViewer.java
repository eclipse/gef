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

import org.eclipse.gef4.fx.nodes.InfiniteCanvas;
import org.eclipse.gef4.mvc.fx.domain.FXDomain;
import org.eclipse.gef4.mvc.parts.IRootPart;
import org.eclipse.gef4.mvc.parts.IVisualPart;
import org.eclipse.gef4.mvc.viewer.AbstractViewer;

import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;

/**
 * The {@link FXViewer} is an {@link AbstractViewer} that is parameterized by
 * {@link Node}. It manages an {@link InfiniteCanvas} that displays the viewer's
 * contents, adds scrollbars when necessary, and renders a background grid.
 *
 * @author anyssen
 *
 */
public class FXViewer extends AbstractViewer<Node> {

	/**
	 * Defines the default CSS styling for the {@link InfiniteCanvas}: no
	 * background, no border.
	 */
	public static final String DEFAULT_STYLE = "-fx-background-insets:0;-fx-padding:0;-fx-background-color:rgba(0,0,0,0);";

	/**
	 * Defines the CSS styling that is used to highlight a focused viewer.
	 */
	public static final String FOCUSED_STYLE = "-fx-background-insets:0;-fx-padding:0;-fx-background-color:rgba(0,0,0,0);-fx-border-color:#8ec0fc;-fx-border-width:3px;";

	/**
	 * The {@link InfiniteCanvas} that displays the viewer's contents.
	 */
	protected InfiniteCanvas infiniteCanvas;

	private ReadOnlyBooleanWrapper viewerFocusedProperty = new ReadOnlyBooleanWrapper(
			false);

	private ChangeListener<Node> focusOwnerObserver = new ChangeListener<Node>() {
		@Override
		public void changed(ObservableValue<? extends Node> observable,
				Node oldValue, Node newValue) {
			if (oldValue != newValue) {
				onFocusOwnerChanged(oldValue, newValue);
			}
		}
	};

	private ChangeListener<Boolean> isFocusOwnerFocusedObserver = new ChangeListener<Boolean>() {
		@Override
		public void changed(ObservableValue<? extends Boolean> observable,
				Boolean oldValue, Boolean newValue) {
			// XXX: If a new focusOwner is set and the old focusOwner was
			// focused, it will fire a "focused" change from true to false. Such
			// events are disregarded, so that the viewer does not lose its
			// focus if it will gain it immediately afterwards (which is handled
			// within #focusOwnerChanged()).
			if (observable == getCanvas().getScene().getFocusOwner()) {
				if (oldValue == null ? newValue != null
						: !oldValue.equals(newValue)) {
					onFocusOwnerFocusedChanged(newValue);
				}
			}
		}
	};

	/**
	 * Returns the {@link InfiniteCanvas} that is managed by this
	 * {@link FXViewer} .
	 *
	 * @return The {@link InfiniteCanvas} that is managed by this
	 *         {@link FXViewer} .
	 */
	public InfiniteCanvas getCanvas() {
		if (infiniteCanvas == null) {
			IRootPart<Node, ? extends Node> rootPart = getRootPart();
			if (rootPart != null) {
				infiniteCanvas = new InfiniteCanvas();
				infiniteCanvas.setStyle(DEFAULT_STYLE);

				// register root visual
				infiniteCanvas.getContentGroup().getChildren()
						.addAll((Parent) rootPart.getVisual());

				// register scene, focus owner, and focused listener if scene is
				// available
				if (infiniteCanvas.getScene() != null) {
					onSceneChanged(null, infiniteCanvas.getScene());
				}

				// ensure we can properly react to scene and focus owner changes
				infiniteCanvas.sceneProperty()
						.addListener(new ChangeListener<Scene>() {
							@Override
							public void changed(
									ObservableValue<? extends Scene> observable,
									Scene oldValue, Scene newValue) {
								onSceneChanged(oldValue, newValue);
							}
						});
			}
		}
		return infiniteCanvas;
	}

	@Override
	public FXDomain getDomain() {
		return (FXDomain) super.getDomain();
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
	public boolean isViewerFocused() {
		return viewerFocusedProperty.get();
	}

	@Override
	public boolean isViewerVisual(Node node) {
		while (node != null) {
			if (node == infiniteCanvas) {
				return true;
			}
			node = node.getParent();
		}
		return false;
	}

	private void onFocusOwnerChanged(Node oldFocusOwner, Node newFocusOwner) {
		if (oldFocusOwner != null && isViewerVisual(oldFocusOwner)) {
			oldFocusOwner.focusedProperty()
					.removeListener(isFocusOwnerFocusedObserver);
		}
		if (newFocusOwner != null && isViewerVisual(newFocusOwner)) {
			newFocusOwner.focusedProperty()
					.addListener(isFocusOwnerFocusedObserver);
			// check if viewer is focused
			if (newFocusOwner.focusedProperty().get()) {
				viewerFocusedProperty.set(true);
			}
		} else {
			// viewer unfocused
			viewerFocusedProperty.set(false);
		}
	}

	private void onFocusOwnerFocusedChanged(Boolean isFocusOwnerFocused) {
		viewerFocusedProperty.set(isFocusOwnerFocused);
	}

	private void onSceneChanged(Scene oldScene, Scene newScene) {
		Node oldFocusOwner = null;
		Node newFocusOwner = null;
		if (oldScene != null) {
			oldFocusOwner = oldScene.focusOwnerProperty().get();
			oldScene.focusOwnerProperty().removeListener(focusOwnerObserver);
		}
		if (newScene != null) {
			newFocusOwner = newScene.focusOwnerProperty().get();
			newScene.focusOwnerProperty().addListener(focusOwnerObserver);
		}
		onFocusOwnerChanged(oldFocusOwner, newFocusOwner);
	}

	@Override
	public void reveal(IVisualPart<Node, ? extends Node> visualPart) {
		getCanvas().reveal(visualPart.getVisual());
	}

	@Override
	public ReadOnlyBooleanProperty viewerFocusedProperty() {
		return viewerFocusedProperty.getReadOnlyProperty();
	}

}