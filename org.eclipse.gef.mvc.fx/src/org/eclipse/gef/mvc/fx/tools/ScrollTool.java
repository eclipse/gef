/*******************************************************************************
 * Copyright (c) 2014, 2016 itemis AG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Matthias Wienand (itemis AG) - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.gef.mvc.fx.tools;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.gef.mvc.fx.policies.IOnScrollPolicy;
import org.eclipse.gef.mvc.fx.viewer.IViewer;

import javafx.animation.PauseTransition;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.event.EventTarget;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.input.ScrollEvent;
import javafx.util.Duration;

/**
 * The {@link ScrollTool} is an {@link AbstractTool} that handles mouse scroll
 * events.
 *
 * @author mwienand
 *
 */
public class ScrollTool extends AbstractTool {

	/**
	 * The type of the policy that has to be supported by target parts.
	 */
	public static final Class<IOnScrollPolicy> ON_SCROLL_POLICY_KEY = IOnScrollPolicy.class;

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
	protected void abortPolicies(final IViewer viewer) {
		inScroll.remove(viewer);
		// cancel target policies
		for (IOnScrollPolicy policy : getActivePolicies(viewer)) {
			policy.abortScroll();
		}
		// clear active policies and close execution
		// transaction
		clearActivePolicies(viewer);
		getDomain().closeExecutionTransaction(ScrollTool.this);
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
	protected void doActivate() {
		super.doActivate();
		for (final IViewer viewer : getDomain().getViewers().values()) {
			// register a viewer focus change listener
			ChangeListener<Boolean> viewerFocusChangeListener = new ChangeListener<Boolean>() {
				@Override
				public void changed(
						ObservableValue<? extends Boolean> observable,
						Boolean oldValue, Boolean newValue) {
					if (newValue == null || !newValue) {
						abortPolicies(viewer);
					}
				}
			};
			viewer.viewerFocusedProperty()
					.addListener(viewerFocusChangeListener);
			viewerFocusChangeListeners.put(viewer, viewerFocusChangeListener);

			// register scroll filter
			Scene scene = viewer.getCanvas().getScene();
			EventHandler<ScrollEvent> scrollFilter = createScrollFilter(viewer);
			scrollFilters.put(viewer, scrollFilter);
			scrollFilterScenes.put(scrollFilter, scene);
			scene.addEventFilter(ScrollEvent.SCROLL, scrollFilter);
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
			EventHandler<ScrollEvent> filter = scrollFilters.remove(viewer);
			scrollFilterScenes.remove(filter)
					.removeEventFilter(ScrollEvent.SCROLL, filter);
			viewer.viewerFocusedProperty()
					.removeListener(viewerFocusChangeListeners.remove(viewer));
		}
		super.doDeactivate();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<? extends IOnScrollPolicy> getActivePolicies(IViewer viewer) {
		return (List<IOnScrollPolicy>) super.getActivePolicies(viewer);
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
		for (IOnScrollPolicy policy : getActivePolicies(viewer)) {
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
		for (IOnScrollPolicy policy : getActivePolicies(viewer)) {
			policy.endScroll();
		}
		clearActivePolicies(viewer);
		getDomain().closeExecutionTransaction(ScrollTool.this);
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
		getDomain().openExecutionTransaction(ScrollTool.this);
		setActivePolicies(viewer,
				getTargetPolicyResolver().resolvePolicies(ScrollTool.this,
						eventTarget instanceof Node ? (Node) eventTarget : null,
						viewer, ON_SCROLL_POLICY_KEY));
		for (IOnScrollPolicy policy : getActivePolicies(viewer)) {
			policy.startScroll(event);
		}
	}
}
