/*******************************************************************************
 * Copyright (c) 2014, 2017 itemis AG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Matthias Wienand (itemis AG) - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.gef.mvc.fx.gestures;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.gef.mvc.fx.handlers.IOnScrollHandler;
import org.eclipse.gef.mvc.fx.parts.PartUtils;
import org.eclipse.gef.mvc.fx.viewer.IViewer;

import javafx.animation.PauseTransition;
import javafx.beans.binding.ObjectExpression;
import javafx.beans.value.ChangeListener;
import javafx.event.EventHandler;
import javafx.event.EventTarget;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.input.ScrollEvent;
import javafx.util.Duration;

/**
 * The {@link ScrollGesture} is an {@link AbstractGesture} that handles mouse
 * scroll events.
 *
 * @author mwienand
 *
 */
public class ScrollGesture extends AbstractGesture {

	/**
	 * The type of the policy that has to be supported by target parts.
	 */
	public static final Class<IOnScrollHandler> ON_SCROLL_POLICY_KEY = IOnScrollHandler.class;

	/**
	 * The default duration in milliseconds that has to pass without receiving a
	 * {@link ScrollEvent} so that the gesture is assumed to have finished.
	 * <p>
	 * Value: 180 (ms)
	 */
	public static final int DEFAULT_FINISH_DELAY_MILLIS = 180;

	private final Map<IViewer, ChangeListener<Boolean>> viewerFocusChangeListeners = new IdentityHashMap<>();
	private final Set<IViewer> inScroll = new HashSet<>();
	private final Map<IViewer, PauseTransition> finishDelayTransitions = new HashMap<>();
	private final Map<IViewer, EventHandler<ScrollEvent>> scrollFilters = new HashMap<>();
	private final Map<EventHandler<ScrollEvent>, Scene> scrollFilterScenes = new HashMap<>();

	/**
	 * Aborts the currently active policies for the given {@link IViewer}.
	 *
	 * @param viewer
	 *            The {@link IViewer}
	 */
	@Override
	protected void abortPolicies(final IViewer viewer) {
		inScroll.remove(viewer);
		super.abortPolicies(viewer);
	}

	/**
	 *
	 * @param viewer
	 *            The {@link IViewer}
	 * @return A {@link PauseTransition}
	 */
	protected PauseTransition createFinishDelayTransition(
			final IViewer viewer) {
		PauseTransition pauseTransition = new PauseTransition(
				Duration.millis(getFinishDelayMillis()));
		pauseTransition.setOnFinished((ae) -> {
			scrollFinished(viewer);
			inScroll.remove(viewer);
		});
		return pauseTransition;
	}

	/**
	 *
	 * @param viewer
	 *            The {@link IViewer}
	 * @return An {@link EventHandler} for {@link ScrollEvent}.
	 */
	protected EventHandler<ScrollEvent> createScrollFilter(
			final IViewer viewer) {
		return new EventHandler<ScrollEvent>() {
			@Override
			public void handle(ScrollEvent event) {
				if (!(event.getTarget() instanceof Node)
						|| PartUtils.retrieveViewer(getDomain(),
								(Node) event.getTarget()) != viewer) {
					return;
				}
				playFinishDelayTransition(viewer);
				if (!inScroll.contains(viewer)) {
					inScroll.add(viewer);
					scrollStarted(viewer, event);
				} else {
					scroll(viewer, event);
				}
			}
		};
	}

	@Override
	protected void doAbortPolicies(IViewer viewer) {
		for (IOnScrollHandler policy : getActiveHandlers(viewer)) {
			policy.abortScroll();
		}
	}

	@Override
	protected void doActivate() {
		for (final IViewer viewer : getDomain().getViewers().values()) {
			// register a viewer focus change listener
			ChangeListener<Boolean> viewerFocusChangeListener = createFocusChangeListener(
					viewer);
			viewer.viewerFocusedProperty()
					.addListener(viewerFocusChangeListener);
			viewerFocusChangeListeners.put(viewer, viewerFocusChangeListener);

			ChangeListener<? super Scene> sceneListener = (exp, oldScene,
					newScene) -> {
				if (oldScene != null) {
					unhookScene(viewer);
				}
				if (newScene != null) {
					hookScene(newScene, viewer);
				}
			};

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
		for (final IViewer viewer : new ArrayList<>(
				viewerFocusChangeListeners.keySet())) {
			abortPolicies(viewer);
			if (finishDelayTransitions.containsKey(viewer)) {
				finishDelayTransitions.remove(viewer).stop();
			}
			unhookScene(viewer);
			viewer.viewerFocusedProperty()
					.removeListener(viewerFocusChangeListeners.remove(viewer));
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<? extends IOnScrollHandler> getActiveHandlers(IViewer viewer) {
		return (List<IOnScrollHandler>) super.getActiveHandlers(viewer);
	}

	/**
	 * Returns the duration in milliseconds that has to pass without receiving a
	 * {@link ScrollEvent} so that the gesture is assumed to have finished.
	 *
	 * @return The duration in milliseconds that has to pass without receiving a
	 *         {@link ScrollEvent} so that the gesture is assumed to have
	 *         finished.
	 */
	protected long getFinishDelayMillis() {
		return DEFAULT_FINISH_DELAY_MILLIS;
	}

	private void hookScene(Scene scene, IViewer viewer) {
		// register scroll filter
		EventHandler<ScrollEvent> scrollFilter = createScrollFilter(viewer);
		scrollFilters.put(viewer, scrollFilter);
		scrollFilterScenes.put(scrollFilter, scene);
		scene.addEventFilter(ScrollEvent.SCROLL, scrollFilter);
	}

	/**
	 * (Re-)Starts playing the finish-delay-transition.
	 *
	 * @param viewer
	 *            The {@link IViewer}
	 */
	protected void playFinishDelayTransition(IViewer viewer) {
		if (!finishDelayTransitions.containsKey(viewer)) {
			finishDelayTransitions.put(viewer,
					createFinishDelayTransition(viewer));
		}
		PauseTransition pauseTransition = finishDelayTransitions.get(viewer);
		pauseTransition.stop();
		pauseTransition.setDuration(Duration.millis(getFinishDelayMillis()));
		pauseTransition.playFromStart();
	}

	/**
	 * Callback method that is invoked for all but the first {@link ScrollEvent}
	 * of a scroll gesture.
	 *
	 * @param viewer
	 *            The {@link IViewer}.
	 * @param event
	 *            The corresponding {@link ScrollEvent}.
	 */
	protected void scroll(IViewer viewer, ScrollEvent event) {
		for (IOnScrollHandler policy : getActiveHandlers(viewer)) {
			policy.scroll(event);
		}
	}

	/**
	 * Callback method that is invoked when the scroll gesture ends, i.e. no
	 * {@link ScrollEvent} was fired for the number of milliseconds specified in
	 * {@link #DEFAULT_FINISH_DELAY_MILLIS}.
	 *
	 * @param viewer
	 *            The {@link IViewer}.
	 */
	protected void scrollFinished(IViewer viewer) {
		for (IOnScrollHandler policy : getActiveHandlers(viewer)) {
			policy.endScroll();
		}
		clearActiveHandlers(viewer);
		getDomain().closeExecutionTransaction(ScrollGesture.this);
	}

	/**
	 * Callback method that is invoked for the first {@link ScrollEvent} of a
	 * scroll gesture.
	 *
	 * @param viewer
	 *            The {@link IViewer}.
	 * @param event
	 *            The corresponding {@link ScrollEvent}.
	 */
	protected void scrollStarted(IViewer viewer, ScrollEvent event) {
		EventTarget eventTarget = event.getTarget();
		getDomain().openExecutionTransaction(ScrollGesture.this);
		setActiveHandlers(viewer,
				getHandlerResolver().resolve(ScrollGesture.this,
						eventTarget instanceof Node ? (Node) eventTarget : null,
						viewer, ON_SCROLL_POLICY_KEY));
		for (IOnScrollHandler policy : getActiveHandlers(viewer)) {
			policy.startScroll(event);
		}
	}

	private void unhookScene(IViewer viewer) {
		EventHandler<ScrollEvent> filter = scrollFilters.remove(viewer);
		scrollFilterScenes.remove(filter).removeEventFilter(ScrollEvent.SCROLL,
				filter);
	}
}
