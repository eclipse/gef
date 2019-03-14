/*******************************************************************************
 * Copyright (c) 2015, 2017 itemis AG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Alexander Nyßen (itemis AG) - initial API and implementation
 *     Matthias Wienand (itemis AG) - inline rotate gesture
 *
 *******************************************************************************/
package org.eclipse.gef.mvc.fx.gestures;

import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.gef.mvc.fx.domain.IDomain;
import org.eclipse.gef.mvc.fx.handlers.IOnRotateHandler;
import org.eclipse.gef.mvc.fx.parts.PartUtils;
import org.eclipse.gef.mvc.fx.viewer.IViewer;

import javafx.beans.binding.ObjectExpression;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.event.EventTarget;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.input.RotateEvent;

/**
 * The {@link RotateGesture} is an {@link AbstractGesture} to handle rotate
 * interaction gestures.
 * <p>
 * The {@link RotateGesture} handles the opening and closing of an transaction
 * operation via the {@link IDomain}, to which it is adapted. It controls that a
 * single transaction operation is used for the complete interaction , so all
 * interaction results can be undone in a single undo step.
 *
 * @author anyssen
 *
 */
public class RotateGesture extends AbstractGesture {
	/**
	 * The type of the policy that has to be supported by target parts.
	 */
	public static final Class<IOnRotateHandler> ON_ROTATE_POLICY_KEY = IOnRotateHandler.class;

	private final Map<IViewer, ChangeListener<Boolean>> viewerFocusChangeListeners = new IdentityHashMap<>();
	private Map<Scene, EventHandler<RotateEvent>> rotateFilters = new IdentityHashMap<>();

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
	 */
	protected ChangeListener<Boolean> createFocusChangeListener(
			final IViewer viewer) {
		ChangeListener<Boolean> viewerFocusChangeListener = new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> observable,
					Boolean oldValue, Boolean newValue) {
				if (newValue == null || !newValue) {
					// cancel target policies
					for (IOnRotateHandler policy : getActiveHandlers(viewer)) {
						policy.abortRotate();
					}
					// clear active policies and close execution
					// transaction
					clearActiveHandlers(viewer);
					getDomain().closeExecutionTransaction(RotateGesture.this);
				}
			}
		};
		return viewerFocusChangeListener;
	}

	/**
	 * Creates an {@link EventHandler} for {@link RotateEvent}s that forwards
	 * the events to the {@link IOnRotateHandler} implementations that can be
	 * determined for the individual events and the given {@link IViewer}.
	 *
	 * @param scene
	 *            The {@link Scene} where the {@link EventHandler} will be
	 *            registered.
	 * @return The {@link EventHandler} that can be registered at the given
	 *         {@link Scene}.
	 */
	protected EventHandler<RotateEvent> createRotateFilter(Scene scene) {
		return new EventHandler<RotateEvent>() {
			@Override
			public void handle(RotateEvent event) {
				if (!(event.getTarget() instanceof Node)) {
					return;
				}
				IViewer viewer = PartUtils.retrieveViewer(getDomain(),
						(Node) event.getTarget());
				if (viewer == null) {
					return;
				}
				if (RotateEvent.ROTATE.equals(event.getEventType())) {
					for (IOnRotateHandler policy : getActiveHandlers(viewer)) {
						policy.rotate(event);
					}
				} else if (RotateEvent.ROTATION_STARTED
						.equals(event.getEventType())) {
					// zoom finish may not occur, so close any preceding
					// transaction just in case
					if (!getDomain()
							.isExecutionTransactionOpen(RotateGesture.this)) {
						// TODO: this case should already be handled by the
						// underlying gesture
						// finish event may not properly occur in all cases; we
						// may continue to use the still open transaction
						getDomain()
								.openExecutionTransaction(RotateGesture.this);
					}

					// determine target policies
					EventTarget eventTarget = event.getTarget();
					setActiveHandlers(viewer,
							getHandlerResolver().resolve(RotateGesture.this,
									eventTarget instanceof Node
											? (Node) eventTarget
											: null,
									viewer, ON_ROTATE_POLICY_KEY));

					// send event to the policies
					for (IOnRotateHandler policy : getActiveHandlers(viewer)) {
						policy.startRotate(event);
					}
				} else if (RotateEvent.ROTATION_FINISHED
						.equals(event.getEventType())) {
					for (IOnRotateHandler policy : getActiveHandlers(viewer)) {
						policy.endRotate(event);
					}
					clearActiveHandlers(viewer);
					getDomain().closeExecutionTransaction(RotateGesture.this);
				}
			}
		};
	}

	@Override
	protected void doActivate() {
		super.doActivate();

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

			ObjectExpression<Scene> sceneProperty = viewer.getCanvas()
					.sceneProperty();
			sceneProperty.addListener(sceneListener);
			if (sceneProperty.get() != null) {
				sceneListener.changed(sceneProperty, null, sceneProperty.get());
			}
		}
	}

	@Override
	protected void doDeactivate() {
		for (Scene scene : new ArrayList<>(rotateFilters.keySet())) {
			unhookScene(scene);
		}
		for (final IViewer viewer : new ArrayList<>(
				viewerFocusChangeListeners.keySet())) {
			viewer.viewerFocusedProperty()
					.removeListener(viewerFocusChangeListeners.remove(viewer));
		}
		super.doDeactivate();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<? extends IOnRotateHandler> getActiveHandlers(IViewer viewer) {
		return (List<IOnRotateHandler>) super.getActiveHandlers(viewer);
	}

	private void hookScene(Scene scene) {
		// check if we already registered zoom listeners at the viewer's
		// scene (in case two viewer's share a single scene)
		if (rotateFilters.containsKey(scene)) {
			// we are already listening for events of this scene
			return;
		}

		// register zoom filter
		EventHandler<RotateEvent> rotateFilter = createRotateFilter(scene);
		scene.addEventFilter(RotateEvent.ANY, rotateFilter);
		rotateFilters.put(scene, rotateFilter);
	}

	private void unhookScene(Scene scene) {
		scene.removeEventFilter(RotateEvent.ANY, rotateFilters.remove(scene));
	}
}
