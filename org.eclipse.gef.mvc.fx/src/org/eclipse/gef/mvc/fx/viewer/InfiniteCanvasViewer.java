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
package org.eclipse.gef.mvc.fx.viewer;

import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.TreeMap;

import org.eclipse.gef.common.activate.ActivatableSupport;
import org.eclipse.gef.common.activate.IActivatable;
import org.eclipse.gef.common.adapt.AdaptableSupport;
import org.eclipse.gef.common.adapt.AdapterKey;
import org.eclipse.gef.common.adapt.inject.InjectAdapters;
import org.eclipse.gef.common.beans.property.ReadOnlyListWrapperEx;
import org.eclipse.gef.common.beans.property.ReadOnlyMapWrapperEx;
import org.eclipse.gef.common.collections.CollectionUtils;
import org.eclipse.gef.fx.nodes.InfiniteCanvas;
import org.eclipse.gef.fx.utils.NodeUtils;
import org.eclipse.gef.mvc.fx.domain.IDomain;
import org.eclipse.gef.mvc.fx.parts.IContentPart;
import org.eclipse.gef.mvc.fx.parts.IRootPart;
import org.eclipse.gef.mvc.fx.parts.IVisualPart;

import com.google.common.reflect.TypeToken;

import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.beans.property.ReadOnlyListProperty;
import javafx.beans.property.ReadOnlyListWrapper;
import javafx.beans.property.ReadOnlyMapProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Window;

/**
 * The {@link InfiniteCanvasViewer} is an {@link IViewer} that manages an
 * {@link InfiniteCanvas} to display the viewer's contents.
 *
 * @author anyssen
 */
public class InfiniteCanvasViewer implements IViewer {

	private InfiniteCanvas infiniteCanvas;

	private boolean isInitialized = false;
	private boolean isWindowFocused = false;
	private boolean isFocusOwnerFocused = false;

	private ReadOnlyBooleanWrapper viewerFocusedProperty = new ReadOnlyBooleanWrapper(
			false);

	private ObservableList<Object> contents = CollectionUtils
			.observableArrayList();

	private ReadOnlyListWrapper<Object> contentsProperty = new ReadOnlyListWrapperEx<>(
			this, CONTENTS_PROPERTY, contents);

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

	private ChangeListener<Scene> sceneListener = new ChangeListener<Scene>() {
		@Override
		public void changed(ObservableValue<? extends Scene> observable,
				Scene oldValue, Scene newValue) {
			onSceneChanged(oldValue, newValue);
		}
	};

	private ActivatableSupport acs = new ActivatableSupport(this);

	private AdaptableSupport<IViewer> ads = new AdaptableSupport<>(this);

	// XXX: Use HashMap for contentPartMap so that equals() is used for
	// containment tests, which is also used when working with lists. The
	// implementation needs to match the implementation that is used within
	// ContentBehavior.
	private ObservableMap<Object, IContentPart<? extends Node>> contentPartMap = FXCollections
			.observableMap(new HashMap<>());
	private ReadOnlyMapProperty<Object, IContentPart<? extends Node>> contentPartMapProperty;
	private ObservableMap<Node, IVisualPart<? extends Node>> visualPartMap = FXCollections
			.observableMap(new IdentityHashMap<>());
	private ReadOnlyMapProperty<Node, IVisualPart<? extends Node>> visualPartMapProperty;

	private ReadOnlyObjectWrapper<IDomain> domainProperty = new ReadOnlyObjectWrapper<>();

	/**
	 * Creates a new {@link InfiniteCanvasViewer}.
	 */
	public InfiniteCanvasViewer() {
		super();
		// add binding to viewer focused property to have its value computed
		// based on the values of:
		// - window focused
		// - focusOwner
		// - focusOwner focused
		viewerFocusedProperty.bind(viewerFocusedPropertyBinding);
	}

	@Override
	public final void activate() {
		acs.activate(null, this::doActivate);
	}

	/**
	 * Activates the adapters registered at this {@link InfiniteCanvasViewer}.
	 */
	protected void activateAdapters() {
		// XXX: We keep a sorted map of adapters so activation
		// is performed in a deterministic order
		new TreeMap<>(ads.getAdapters()).values().forEach((adapter) -> {
			if (adapter instanceof IActivatable) {
				((IActivatable) adapter).activate();
			}
		});
	}

	@Override
	public final ReadOnlyBooleanProperty activeProperty() {
		return acs.activeProperty();
	}

	@Override
	public ReadOnlyObjectProperty<IDomain> adaptableProperty() {
		return domainProperty.getReadOnlyProperty();
	}

	@Override
	public ReadOnlyMapProperty<AdapterKey<?>, Object> adaptersProperty() {
		return ads.adaptersProperty();
	}

	@Override
	public ReadOnlyMapProperty<Object, IContentPart<? extends Node>> contentPartMapProperty() {
		if (contentPartMapProperty == null) {
			contentPartMapProperty = new ReadOnlyMapWrapperEx<>(this,
					CONTENT_PART_MAP_PROPERTY, contentPartMap);
		}
		return contentPartMapProperty;
	}

	@Override
	public ReadOnlyListProperty<Object> contentsProperty() {
		return contentsProperty.getReadOnlyProperty();
	}

	@Override
	public final void deactivate() {
		acs.deactivate(this::doDeactivate, null);
	}

	/**
	 * Deactivates the adapters registered at this {@link InfiniteCanvasViewer}.
	 */
	protected void deactivateAdapters() {
		// XXX: We keep a sorted map of adapters so deactivation
		// is performed in a deterministic order
		new TreeMap<>(ads.getAdapters()).values().forEach((adapter) -> {
			if (adapter instanceof IActivatable) {
				((IActivatable) adapter).deactivate();
			}
		});
	}

	@Override
	public void dispose() {
		// ensure all listeners are properly unregistered
		if (infiniteCanvas != null) {
			if (infiniteCanvas.getScene() != null) {
				onSceneChanged(infiniteCanvas.getScene(), null);
			}
			infiniteCanvas.sceneProperty().removeListener(sceneListener);
			infiniteCanvas = null;
		}

		// unbind viewer focused property
		viewerFocusedProperty.unbind();
		viewerFocusedProperty = null;

		// dispose adapters (including root part and models)
		ads.dispose();
		ads = null;

		// clear content part map
		if (!contentPartMap.isEmpty()) {
			throw new IllegalStateException(
					"Content part map was not properly cleared!");
		}
		contentPartMap = null;

		// clear visual part map
		if (!visualPartMap.isEmpty()) {
			throw new IllegalStateException(
					"Visual part map was not properly cleared!");
		}
		visualPartMap = null;

		// unset activatable support
		acs = null;
	}

	/**
	 * Activates this {@link InfiniteCanvasViewer}, which activates its
	 * adapters.
	 */
	protected void doActivate() {
		if (getDomain() == null) {
			throw new IllegalStateException(
					"Domain has to be set before activation.");
		}
		if (getRootPart() == null) {
			throw new IllegalStateException(
					"RootPart has to be set before activation.");
		}
		if (infiniteCanvas == null) {
			throw new IllegalStateException(
					"Viewer controls have to be hooked before activation.");
		}
		activateAdapters();
	}

	/**
	 * Deactivates this {@link InfiniteCanvasViewer}, which deactivates its
	 * adapters.
	 */
	protected void doDeactivate() {
		deactivateAdapters();
	}

	@Override
	public IDomain getAdaptable() {
		return domainProperty.get();
	}

	@Override
	public <T> T getAdapter(AdapterKey<T> key) {
		return ads.getAdapter(key);
	}

	@Override
	public <T> T getAdapter(Class<T> classKey) {
		return ads.getAdapter(classKey);
	}

	@Override
	public <T> T getAdapter(TypeToken<T> key) {
		return ads.getAdapter(key);
	}

	@Override
	public <T> AdapterKey<T> getAdapterKey(T adapter) {
		return ads.getAdapterKey(adapter);
	}

	@Override
	public ObservableMap<AdapterKey<?>, Object> getAdapters() {
		return ads.getAdapters();
	}

	@Override
	public <T> Map<AdapterKey<? extends T>, T> getAdapters(
			Class<? super T> classKey) {
		return ads.getAdapters(classKey);
	}

	@Override
	public <T> Map<AdapterKey<? extends T>, T> getAdapters(
			TypeToken<? super T> key) {
		return ads.getAdapters(key);
	}

	/**
	 * Returns the {@link InfiniteCanvas} that is managed by this
	 * {@link InfiniteCanvasViewer} .
	 *
	 * @return The {@link InfiniteCanvas} that is managed by this
	 *         {@link InfiniteCanvasViewer} .
	 */
	@Override
	public InfiniteCanvas getCanvas() {
		if (infiniteCanvas == null) {
			infiniteCanvas = new InfiniteCanvas();
			infiniteCanvas.sceneProperty().addListener(sceneListener);

			// hook root visual
			IRootPart<? extends Node> rootPart = getRootPart();
			infiniteCanvas.getContentGroup().getChildren()
					.addAll((Parent) rootPart.getVisual());
		}
		return infiniteCanvas;
	}

	/**
	 * @see IViewer#getContentPartMap()
	 */
	@Override
	public Map<Object, IContentPart<? extends Node>> getContentPartMap() {
		return contentPartMap;
	}

	@Override
	public ObservableList<Object> getContents() {
		return contents;
	}

	@Override
	public IDomain getDomain() {
		return domainProperty.get();
	}

	@SuppressWarnings("serial")
	@Override
	public IRootPart<? extends Node> getRootPart() {
		return ads.getAdapter(new TypeToken<IRootPart<? extends Node>>() {
		});
	}

	/**
	 * Returns the {@link Scene} in which the {@link InfiniteCanvas} of this
	 * {@link InfiniteCanvasViewer} is displayed.
	 *
	 * @return The {@link Scene} in which the {@link InfiniteCanvas} of this
	 *         {@link InfiniteCanvasViewer} is displayed.
	 */
	public Scene getScene() {
		return getCanvas().getScene();
	}

	/**
	 * @see IViewer#getVisualPartMap()
	 */
	@Override
	public Map<Node, IVisualPart<? extends Node>> getVisualPartMap() {
		return visualPartMap;
	}

	@Override
	public final boolean isActive() {
		return acs.isActive();
	}

	@Override
	public boolean isViewerFocused() {
		return viewerFocusedProperty.get();
	}

	private void onFocusOwnerChanged(Node oldFocusOwner, Node newFocusOwner) {
		if (oldFocusOwner != null
				&& NodeUtils.isNested(getCanvas(), oldFocusOwner)) {
			oldFocusOwner.focusedProperty()
					.removeListener(focusOwnerFocusedObserver);
		}
		if (newFocusOwner != null
				&& NodeUtils.isNested(getCanvas(), newFocusOwner)) {
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
			// set. This could also possibly be done in the FocusBehavior, but
			// keeping it here we can limit 'knowledge' about the embedded
			// window.
			getRootPart().getVisual().requestFocus();
			isInitialized = true;
		}
	}

	@Override
	public void reveal(IVisualPart<? extends Node> visualPart) {
		if (visualPart == null) {
			getCanvas().setHorizontalScrollOffset(0);
			getCanvas().setVerticalScrollOffset(0);
		} else {
			getCanvas().reveal(visualPart.getVisual());
		}
	}

	@Override
	public void setAdaptable(IDomain domain) {
		domainProperty.set(domain);
	}

	@Override
	public <T> void setAdapter(T adapter) {
		ads.setAdapter(adapter);
	}

	@Override
	public <T> void setAdapter(T adapter, String role) {
		ads.setAdapter(adapter, role);
	}

	@Override
	public <T> void setAdapter(TypeToken<T> adapterType, T adapter) {
		ads.setAdapter(adapterType, adapter);
	}

	@InjectAdapters
	@Override
	public <T> void setAdapter(TypeToken<T> adapterType, T adapter,
			String role) {
		ads.setAdapter(adapterType, adapter, role);
	}

	@Override
	public <T> void unsetAdapter(T adapter) {
		ads.unsetAdapter(adapter);
	}

	@Override
	public ReadOnlyBooleanProperty viewerFocusedProperty() {
		return viewerFocusedProperty.getReadOnlyProperty();
	}

	@Override
	public ReadOnlyMapProperty<Node, IVisualPart<? extends Node>> visualPartMapProperty() {
		if (visualPartMapProperty == null) {
			visualPartMapProperty = new ReadOnlyMapWrapperEx<>(this,
					VISUAL_PART_MAP_PROPERTY, visualPartMap);
		}
		return visualPartMapProperty;
	}

}