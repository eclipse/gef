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

import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Window;

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
	public static final String FOCUSED_STYLE = "-fx-background-insets:0;-fx-padding:0;-fx-background-color:rgba(0,0,0,0);-fx-border-color:#8ec0fc;-fx-border-width:2px;";

	/**
	 * The {@link InfiniteCanvas} that displays the viewer's contents.
	 */
	protected InfiniteCanvas infiniteCanvas;

	private boolean isInitialized = false;
	private boolean isWindowFocused = false;
	private boolean isFocusOwnerFocused = false;

	private ReadOnlyBooleanWrapper viewerFocusedProperty = new ReadOnlyBooleanWrapper(
			false);

	private BooleanBinding viewerFocusedPropertyBinding = new BooleanBinding() {
		@Override
		protected boolean computeValue() {
			return isWindowFocused && isFocusOwnerFocused;
		}
	};

	private ChangeListener<Window> windowObserver = new ChangeListener<Window>() {

		@Override
		public void changed(ObservableValue<? extends Window> observable,
				Window oldValue, Window newValue) {
			onWindowChanged(oldValue, newValue);
		}
	};

	private ChangeListener<Boolean> windowFocusedObserver = new ChangeListener<Boolean>() {

		@Override
		public void changed(ObservableValue<? extends Boolean> observable,
				Boolean oldValue, Boolean newValue) {
			onWindowFocusedChanged(oldValue, newValue);
		}
	};

	private ChangeListener<Node> focusOwnerObserver = new ChangeListener<Node>() {
		@Override
		public void changed(ObservableValue<? extends Node> observable,
				Node oldValue, Node newValue) {
			if (oldValue != newValue) {
				onFocusOwnerChanged(oldValue, newValue);
			}
		}
	};

	private ChangeListener<Boolean> focusOwnerFocusedObserver = new ChangeListener<Boolean>() {
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
					onFocusOwnerFocusedChanged(oldValue, newValue);
				}
			}
		}
	};

	/**
	 * Creates a new {@link FXViewer}.
	 */
	public FXViewer() {
		super();
		// add binding to viewer focused property to have its value computed
		// based on the values of:
		// - window focused
		// - focusOwner
		// - focusOwner focused
		viewerFocusedProperty.bind(viewerFocusedPropertyBinding);
	}

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
				if (infiniteCanvas.getScene() != null) {
					onSceneChanged(null, infiniteCanvas.getScene());
				}
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
					.removeListener(focusOwnerFocusedObserver);
		}
		if (newFocusOwner != null && isViewerVisual(newFocusOwner)) {
			newFocusOwner.focusedProperty()
					.addListener(focusOwnerFocusedObserver);
			// check if viewer is focused
			if (Boolean.TRUE.equals(newFocusOwner.focusedProperty().get())) {
				isFocusOwnerFocused = true;
				viewerFocusedPropertyBinding.invalidate();
			}
		} else {
			// viewer unfocused
			isFocusOwnerFocused = false;
			viewerFocusedPropertyBinding.invalidate();
		}
	}

	private void onFocusOwnerFocusedChanged(Boolean oldValue,
			Boolean newValue) {
		isFocusOwnerFocused = Boolean.TRUE.equals(newValue);
		viewerFocusedPropertyBinding.invalidate();
	}

	private void onSceneChanged(Scene oldScene, Scene newScene) {
		Window oldWindow = null;
		Window newWindow = null;
		Node oldFocusOwner = null;
		Node newFocusOwner = null;
		if (oldScene != null) {
			oldWindow = oldScene.windowProperty().get();
			oldScene.windowProperty().removeListener(windowObserver);
			oldFocusOwner = oldScene.focusOwnerProperty().get();
			oldScene.focusOwnerProperty().removeListener(focusOwnerObserver);
		}
		if (newScene != null) {
			newWindow = newScene.windowProperty().get();
			newScene.windowProperty().addListener(windowObserver);
			newFocusOwner = newScene.focusOwnerProperty().get();
			newScene.focusOwnerProperty().addListener(focusOwnerObserver);
		}
		onWindowChanged(oldWindow, newWindow);
		onFocusOwnerChanged(oldFocusOwner, newFocusOwner);
	}

	private void onWindowChanged(Window oldValue, Window newValue) {
		if (oldValue != null) {
			oldValue.focusedProperty().removeListener(windowFocusedObserver);
		}
		if (newValue != null) {
			newValue.focusedProperty().addListener(windowFocusedObserver);
			// check if window is focused
			if (Boolean.TRUE.equals(newValue.focusedProperty().get())) {
				isWindowFocused = true;
				viewerFocusedPropertyBinding.invalidate();
			}
		} else {
			// window unfocused
			isInitialized = false;
			isWindowFocused = false;
			viewerFocusedPropertyBinding.invalidate();
		}
	}

	private void onWindowFocusedChanged(Boolean oldValue, Boolean newValue) {
		isWindowFocused = Boolean.TRUE.equals(newValue);
		viewerFocusedPropertyBinding.invalidate();
		if (!isInitialized) {
			// XXX: When the embedded scene is opened, the viewer needs to
			// request focus for the root visual once so that a focus owner is
			// set. This could also possibly be done in the FXFocusBehavior, but
			// keeping it here we can limit 'knowledge' about the embedded
			// window.
			getRootPart().getVisual().requestFocus();
			isInitialized = true;
		}
	}

	@Override
	public void reveal(IVisualPart<Node, ? extends Node> visualPart) {
		if (visualPart == null) {
			getCanvas().setHorizontalScrollOffset(0);
			getCanvas().setVerticalScrollOffset(0);
		} else {
			getCanvas().reveal(visualPart.getVisual());
		}
	}

	@Override
	public ReadOnlyBooleanProperty viewerFocusedProperty() {
		return viewerFocusedProperty.getReadOnlyProperty();
	}

}