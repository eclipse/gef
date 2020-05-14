/*******************************************************************************
 * Copyright (c) 2014, 2019 itemis AG and others.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Alexander Nyßen (itemis AG) - initial API and implementation
 *     Matthias Wienand (itemis AG) - API renames and pull up (un-)hooking
 *
 *******************************************************************************/
package org.eclipse.gef.mvc.fx.gestures;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.eclipse.gef.common.activate.ActivatableSupport;
import org.eclipse.gef.mvc.fx.domain.IDomain;
import org.eclipse.gef.mvc.fx.handlers.IHandler;
import org.eclipse.gef.mvc.fx.viewer.IViewer;

import javafx.beans.binding.ObjectExpression;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Scene;

/**
 * The {@link AbstractGesture} can be used as a base class for {@link IGesture}
 * implementations.
 *
 * @author anyssen
 * @author mwienand
 *
 */
public abstract class AbstractGesture implements IGesture {

	private ActivatableSupport acs = new ActivatableSupport(this);
	private ReadOnlyObjectWrapper<IDomain> domainProperty = new ReadOnlyObjectWrapper<>();
	private Map<IViewer, List<IHandler>> activeHandlers = new IdentityHashMap<>();
	private final Map<IViewer, ChangeListener<Boolean>> viewerFocusChangeListeners = new IdentityHashMap<>();
	private final Map<ObservableValue<Scene>, ChangeListener<? super Scene>> sceneChangeListeners = new IdentityHashMap<>();
	private final Set<Scene> hookedScenes = Collections
			.newSetFromMap(new IdentityHashMap<>());

	/**
	 * Aborts the currently active policies for the given {@link IViewer},
	 * clears active handlers, and closes the execution transaction for this
	 * gesture.
	 *
	 * @param viewer
	 *            The {@link IViewer}
	 * @since 5.2
	 */
	protected void abortPolicies(final IViewer viewer) {
		doAbortPolicies(viewer);
		clearActiveHandlers(viewer);
		getDomain().closeExecutionTransaction(AbstractGesture.this);
	}

	@Override
	public final void activate() {
		acs.activate(null, this::doActivate);
	}

	@Override
	public final ReadOnlyBooleanProperty activeProperty() {
		return acs.activeProperty();
	}

	@Override
	public ReadOnlyObjectProperty<IDomain> adaptableProperty() {
		return domainProperty.getReadOnlyProperty();
	}

	/**
	 * Clears the list of active handlers of this gesture for the given viewer.
	 *
	 * @param viewer
	 *            The {@link IViewer} for which to clear the active handlers of
	 *            this gesture.
	 * @see #getActiveHandlers(IViewer)
	 * @see #setActiveHandlers(IViewer, Collection)
	 */
	protected void clearActiveHandlers(IViewer viewer) {
		if (viewer == null) {
			throw new IllegalArgumentException(
					"The given viewer may not be null.");
		}
		activeHandlers.remove(viewer);
	}

	/**
	 * Creates a {@link ChangeListener} for the
	 * {@link IViewer#viewerFocusedProperty() focused} property of the given
	 * {@link IViewer}.
	 *
	 * @param viewer
	 *            The {@link IViewer} for which to create a
	 *            {@link ChangeListener} for its
	 *            {@link IViewer#viewerFocusedProperty() focused} property.
	 * @return The newly created {@link ChangeListener} for the
	 *         {@link IViewer#viewerFocusedProperty() focused} property of the
	 *         given {@link IViewer}.
	 * @since 5.2
	 */
	protected ChangeListener<Boolean> createFocusChangeListener(
			final IViewer viewer) {
		ChangeListener<Boolean> viewerFocusChangeListener = new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> observable,
					Boolean oldValue, Boolean newValue) {
				if (newValue == null || !newValue) {
					abortPolicies(viewer);
				}
			}
		};
		return viewerFocusChangeListener;
	}

	@Override
	public final void deactivate() {
		acs.deactivate(this::doDeactivate, null);
	}

	/**
	 * This method is called in response to an {@link IViewer} losing focus.
	 * Therefore, any active handlers should be aborted.
	 *
	 * @param viewer
	 *            The {@link IViewer} that lost focus.
	 * @since 5.2
	 */
	protected void doAbortPolicies(final IViewer viewer) {
	}

	/**
	 * This method is called when a valid {@link IDomain} is attached to this
	 * gesture so that you can register event listeners for various inputs
	 * (keyboard, mouse) or model changes (selection, scroll offset / viewport).
	 */
	protected void doActivate() {
		ChangeListener<? super Scene> sceneListener = (exp, oldScene,
				newScene) -> {
			if (oldScene != null) {
				// Check that no other viewer still uses that scene before
				// unhooking it
				if (getDomain().getViewers().values().stream()
						.noneMatch(v -> v.getCanvas().getScene() == oldScene)) {
					unhookScene(oldScene);
				}
			}
			if (newScene != null) {
				hookScene(newScene);
			}
		};

		for (final IViewer viewer : getDomain().getViewers().values()) {
			// register a viewer focus change listener
			ChangeListener<Boolean> viewerFocusChangeListener = createFocusChangeListener(
					viewer);
			viewer.viewerFocusedProperty()
					.addListener(viewerFocusChangeListener);
			viewerFocusChangeListeners.put(viewer, viewerFocusChangeListener);

			// ensure re-hooking in response to Scene changes
			ObjectExpression<Scene> sceneProperty = viewer.getCanvas()
					.sceneProperty();
			sceneProperty.addListener(sceneListener);
			sceneChangeListeners.put(sceneProperty, sceneListener);
			if (sceneProperty.get() != null) {
				sceneListener.changed(sceneProperty, null, sceneProperty.get());
			}
		}
	}

	/**
	 * This method is called when the attached {@link IDomain} is reset to
	 * <code>null</code> so that you can unregister previously registered event
	 * listeners.
	 */
	protected void doDeactivate() {
		// remove scene change listeners
		for (Entry<ObservableValue<Scene>, ChangeListener<? super Scene>> entry : sceneChangeListeners
				.entrySet()) {
			ObservableValue<Scene> sceneProperty = entry.getKey();
			sceneProperty.removeListener(entry.getValue());
		}
		sceneChangeListeners.clear();

		// remove viewer focus change listeners
		for (final IViewer viewer : new ArrayList<>(
				viewerFocusChangeListeners.keySet())) {
			viewer.viewerFocusedProperty()
					.removeListener(viewerFocusChangeListeners.remove(viewer));
		}
		viewerFocusChangeListeners.clear();

		// unhook scenes
		for (Scene scene : hookedScenes) {
			unhookScene(scene);
		}
	}

	/**
	 * Add listeners to the given {@link Scene} as needed.
	 *
	 * @param scene
	 *            The {@link Scene} where listeners should be added.
	 * @since 5.2
	 */
	protected void doHookScene(Scene scene) {
	}

	/**
	 * You should unregister event listeners that were previously registered
	 * within {@link #doHookScene(Scene)}.
	 *
	 * @param scene
	 *            The {@link Scene} where listeners should be removed.
	 * @since 5.2
	 */
	protected void doUnhookScene(Scene scene) {
	}

	@Override
	public List<? extends IHandler> getActiveHandlers(IViewer viewer) {
		if (activeHandlers.containsKey(viewer)) {
			return Collections.unmodifiableList(activeHandlers.get(viewer));
		} else {
			return Collections.emptyList();
		}
	}

	@Override
	public IDomain getAdaptable() {
		return domainProperty.get();
	}

	@Override
	public IDomain getDomain() {
		return getAdaptable();
	}

	/**
	 * Returns the {@link IHandlerResolver} of the {@link IDomain}.
	 *
	 * @return the {@link IHandlerResolver} of the {@link IDomain}.
	 */
	protected IHandlerResolver getHandlerResolver() {
		return getDomain().getAdapter(IHandlerResolver.class);
	}

	/**
	 * This method is called when the {@link Scene} that contains the canvas of
	 * an {@link IViewer} changes. It is also called for the initial
	 * {@link Scene}. You can register event listeners for various inputs
	 * (keyboard, mouse) or model changes (selection, scroll offset / viewport)
	 * as needed.
	 *
	 * @param newScene
	 *            The new {@link Scene}.
	 * @since 5.2
	 */
	protected void hookScene(Scene newScene) {
		if (hookedScenes.contains(newScene)) {
			// already registered for this scene
			return;
		}
		hookedScenes.add(newScene);
		doHookScene(newScene);
	}

	@Override
	public final boolean isActive() {
		return acs.isActive();
	}

	/**
	 * Set the active handlers of this gesture to the given handlers.
	 *
	 * @param viewer
	 *            The {@link IViewer} for which to store the active handlers of
	 *            this gesture.
	 * @param activeHandlers
	 *            The active handlers of this gesture.
	 * @see #clearActiveHandlers(IViewer)
	 * @see #getActiveHandlers(IViewer)
	 */
	protected void setActiveHandlers(IViewer viewer,
			Collection<? extends IHandler> activeHandlers) {
		if (viewer == null) {
			throw new IllegalArgumentException(
					"The given viewer may not be null.");
		}
		if (activeHandlers == null) {
			throw new IllegalArgumentException(
					"The given activePolicies may not be null.");
		}
		for (IHandler ap : activeHandlers) {
			if (ap.getHost().getViewer() != viewer) {
				throw new IllegalArgumentException(
						"Resolved handler is not hosted within viewer.");
			}
		}
		clearActiveHandlers(viewer);
		this.activeHandlers.put(viewer, new ArrayList<>(activeHandlers));
	}

	@Override
	public void setAdaptable(IDomain adaptable) {
		if (isActive()) {
			throw new IllegalStateException(
					"The reference to the IDomain may not be changed while the gesture is active. Please deactivate the gesture before setting the IEditDomain and re-activate it afterwards.");
		}
		domainProperty.set(adaptable);
	}

	/**
	 * This method is called when the {@link Scene} that contains the canvas of
	 * an {@link IViewer} changes. You can unregister event listeners for
	 * various inputs (keyboard, mouse) or model changes (selection, scroll
	 * offset / viewport) as needed.
	 *
	 * @param oldScene
	 *            The old {@link Scene}
	 * @since 5.2
	 */
	protected void unhookScene(Scene oldScene) {
		if (hookedScenes.contains(oldScene)) {
			doUnhookScene(oldScene);
			hookedScenes.remove(oldScene);
		}
	}
}
