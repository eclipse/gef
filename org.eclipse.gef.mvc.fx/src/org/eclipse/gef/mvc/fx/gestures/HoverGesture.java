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

import java.util.Collection;
import java.util.IdentityHashMap;
import java.util.Map;

import org.eclipse.gef.geometry.planar.Point;
import org.eclipse.gef.mvc.fx.handlers.IOnHoverHandler;
import org.eclipse.gef.mvc.fx.parts.PartUtils;
import org.eclipse.gef.mvc.fx.viewer.IViewer;

import javafx.animation.Animation.Status;
import javafx.animation.PauseTransition;
import javafx.beans.binding.ObjectExpression;
import javafx.beans.value.ChangeListener;
import javafx.event.EventHandler;
import javafx.event.EventTarget;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.util.Duration;

/**
 * The {@link HoverGesture} is an {@link AbstractGesture} that handles mouse
 * hover changes.
 *
 * @author mwienand
 *
 */
public class HoverGesture extends AbstractGesture {

	/**
	 * Time in milliseconds until the hover handles are created when the host is
	 * hovered.
	 */
	public static final long HOVER_INTENT_MILLIS = 250;

	/**
	 * Distance in pixels which the mouse is allowed to move so that it is
	 * regarded to be stationary.
	 */
	public static final double HOVER_INTENT_MOUSE_MOVE_THRESHOLD = 4;

	/**
	 * The type of the policy that has to be supported by target parts.
	 */
	public static final Class<IOnHoverHandler> ON_HOVER_POLICY_KEY = IOnHoverHandler.class;

	private final Map<Scene, EventHandler<MouseEvent>> hoverFilters = new IdentityHashMap<>();

	// TODO: Investigate if hover intent works with multiple scenes, or if
	// multiple scenes require special treatment.
	private Point hoverIntentScreenPosition;
	private PauseTransition hoverIntentDelay = new PauseTransition(
			Duration.millis(getHoverIntentMillis()));
	private Node hoverIntent;
	private Node potentialHoverIntent;

	{
		hoverIntentDelay.setOnFinished((ae) -> onHoverIntentDelayFinished());
	}

	/**
	 * Creates an {@link EventHandler} for hover {@link MouseEvent}s. The
	 * handler will search for a target part within the given {@link IViewer}
	 * and notify all hover policies of that target part about hover changes.
	 * <p>
	 * If no target part can be identified, then the root part of the given
	 * {@link IViewer} is used as the target part.
	 *
	 * @return The {@link EventHandler} that handles hover changes for the given
	 *         {@link IViewer}.
	 */
	protected EventHandler<MouseEvent> createHoverFilter() {
		return new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				updateHoverIntentPosition(event);
				if (!isHoverEvent(event)) {
					return;
				}
				EventTarget eventTarget = event.getTarget();
				if (eventTarget instanceof Node) {
					IViewer viewer = PartUtils.retrieveViewer(getDomain(),
							(Node) eventTarget);
					if (viewer != null) {
						notifyHover(viewer, event, (Node) eventTarget);
					}
					updateHoverIntent(event, (Node) eventTarget);
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

		for (IViewer viewer : getDomain().getViewers().values()) {
			// XXX: Filter is only registered once per scene. The IViewer is
			// determined for each input event individually.
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
		hoverIntentDelay.stop();
		for (Scene scene : hoverFilters.keySet()) {
			unhookScene(scene);
		}
		super.doDeactivate();
	}

	/**
	 * Returns the duration (in millis) for which the mouse should be
	 * stationarry to trigger hover intent.
	 *
	 * @return the duration (in millis) for which the mouse should be stationary
	 *         to trigger hover intent.
	 */
	protected long getHoverIntentMillis() {
		return HOVER_INTENT_MILLIS;
	}

	/**
	 * Returns the number of pixels the mouse is allowed to move to be still
	 * regarded as stationary.
	 *
	 * @return the number of pixels the mouse is allowed to move to be still
	 *         regarded as stationary.
	 */
	protected double getHoverIntentMouseMoveThreshold() {
		return HOVER_INTENT_MOUSE_MOVE_THRESHOLD;
	}

	private void hookScene(Scene scene) {
		if (!hoverFilters.containsKey(scene)) {
			EventHandler<MouseEvent> hoverFilter = createHoverFilter();
			scene.addEventFilter(MouseEvent.ANY, hoverFilter);
			hoverFilters.put(scene, hoverFilter);
		}
	}

	/**
	 * Returns <code>true</code> if the given {@link MouseEvent} should be
	 * tested for changing the hover target.
	 *
	 * @param event
	 *            The {@link MouseEvent}.
	 * @return <code>true</code> if the given {@link MouseEvent} should be
	 *         tested for changing the hover target
	 */
	protected boolean isHoverEvent(MouseEvent event) {
		return event.getEventType().equals(MouseEvent.MOUSE_MOVED)
				|| event.getEventType().equals(MouseEvent.MOUSE_ENTERED_TARGET)
				|| event.getEventType().equals(MouseEvent.MOUSE_EXITED_TARGET);
	}

	/**
	 *
	 * @param viewer
	 *            The {@link IViewer}.
	 * @param event
	 *            The corresponding {@link MouseEvent}.
	 * @param eventTarget
	 *            The target {@link Node}.
	 */
	protected void notifyHover(IViewer viewer, MouseEvent event,
			Node eventTarget) {
		// determine hover policies
		Collection<? extends IOnHoverHandler> policies = getHandlerResolver()
				.resolve(HoverGesture.this, eventTarget, viewer,
						ON_HOVER_POLICY_KEY);
		getDomain().openExecutionTransaction(HoverGesture.this);
		// active policies are unnecessary because hover is not a
		// gesture, just one event at one point in time
		for (IOnHoverHandler policy : policies) {
			policy.hover(event);
		}
		getDomain().closeExecutionTransaction(HoverGesture.this);
	}

	/**
	 *
	 * @param viewer
	 *            The {@link IViewer}.
	 * @param hoverIntent
	 *            The hover intent {@link Node}.
	 */
	protected void notifyHoverIntent(IViewer viewer, Node hoverIntent) {
		// determine hover policies
		Collection<? extends IOnHoverHandler> policies = getHandlerResolver()
				.resolve(HoverGesture.this, hoverIntent, viewer,
						ON_HOVER_POLICY_KEY);
		getDomain().openExecutionTransaction(HoverGesture.this);
		// active policies are unnecessary because hover is not a
		// gesture, just one event at one point in time
		for (IOnHoverHandler policy : policies) {
			policy.hoverIntent(hoverIntent);
		}
		getDomain().closeExecutionTransaction(HoverGesture.this);
	}

	/**
	 * Callback method that is invoked when the mouse was stationary over a
	 * visual for some amount of time.
	 */
	private void onHoverIntentDelayFinished() {
		hoverIntent = potentialHoverIntent;
		potentialHoverIntent = null;
		IViewer viewer = PartUtils.retrieveViewer(getDomain(), hoverIntent);
		if (viewer != null) {
			notifyHoverIntent(viewer, hoverIntent);
		}
	}

	private void unhookScene(Scene scene) {
		scene.removeEventFilter(MouseEvent.ANY, hoverFilters.remove(scene));
	}

	/**
	 * Updates hover intent delays depending on the given event and hovered
	 * node.
	 *
	 * @param event
	 *            The {@link MouseEvent}.
	 * @param eventTarget
	 *            The hovered {@link Node}.
	 */
	private void updateHoverIntent(MouseEvent event, Node eventTarget) {
		if (eventTarget != hoverIntent) {
			potentialHoverIntent = eventTarget;
			hoverIntentScreenPosition = new Point(event.getScreenX(),
					event.getScreenY());
			hoverIntentDelay.playFromStart();
		} else {
			hoverIntentDelay.stop();
		}
	}

	/**
	 * Updates the hover intent position (and restarts the hover intent delay)
	 * if the mouse was moved too much.
	 *
	 * @param event
	 *            The {@link MouseEvent}.
	 */
	private void updateHoverIntentPosition(MouseEvent event) {
		if (hoverIntentDelay.getStatus().equals(Status.RUNNING)) {
			double dx = hoverIntentScreenPosition.x - event.getScreenX();
			double dy = hoverIntentScreenPosition.y - event.getScreenY();
			double threshold = getHoverIntentMouseMoveThreshold();
			if (Math.abs(dx) > threshold || Math.abs(dy) > threshold) {
				hoverIntentDelay.playFromStart();
				hoverIntentScreenPosition.x = event.getScreenX();
				hoverIntentScreenPosition.y = event.getScreenY();
			}
		}
	}
}
