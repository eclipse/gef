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

import java.util.Collection;
import java.util.IdentityHashMap;
import java.util.Map;

import org.eclipse.gef.geometry.planar.Point;
import org.eclipse.gef.mvc.fx.policies.IOnHoverPolicy;
import org.eclipse.gef.mvc.fx.viewer.IViewer;

import javafx.animation.Animation.Status;
import javafx.animation.PauseTransition;
import javafx.event.EventHandler;
import javafx.event.EventTarget;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.util.Duration;

/**
 * The {@link HoverTool} is an {@link AbstractTool} that handles mouse hover
 * changes.
 *
 * @author mwienand
 *
 */
public class HoverTool extends AbstractTool {

	/**
	 * Time in milliseconds until the hover handles are removed when the host is
	 * not hovered anymore.
	 */
	public static final int UNHOVER_INTENT_MILLIS = 500;

	/**
	 * Time in milliseconds until the hover handles are created when the host is
	 * hovered.
	 */
	public static final int HOVER_INTENT_MILLIS = 250;

	/**
	 * Distance in pixels which the mouse is allowed to move so that it is
	 * regarded to be stationary.
	 */
	public static final double HOVER_INTENT_MOUSE_MOVE_THRESHOLD = 4;

	/**
	 * The type of the policy that has to be supported by target parts.
	 */
	public static final Class<IOnHoverPolicy> ON_HOVER_POLICY_KEY = IOnHoverPolicy.class;

	private final Map<Scene, EventHandler<MouseEvent>> hoverFilters = new IdentityHashMap<>();
	// TODO: Investigate if hover intent works with multiple scenes, or if
	// multiple scenes require special treatment.
	private Point initialHoverIntentScreenPosition;
	private PauseTransition hoverIntentDelay = new PauseTransition(
			Duration.millis(HOVER_INTENT_MILLIS));
	private PauseTransition unhoverIntentDelay = new PauseTransition(
			Duration.millis(UNHOVER_INTENT_MILLIS));
	private Node hoverIntent;
	private Node potentialHoverIntent;

	{
		hoverIntentDelay.setOnFinished((ae) -> onHoverIntentDelayFinished());
		unhoverIntentDelay
				.setOnFinished((ae) -> onUnhoverIntentDelayFinished());
	}

	/**
	 * Creates an {@link EventHandler} for hover {@link MouseEvent}s. The
	 * handler will search for a target part within the given {@link IViewer}
	 * and notify all hover policies of that target part about hover changes.
	 * <p>
	 * If no target part can be identified, then the root part of the given
	 * {@link IViewer} is used as the target part.
	 *
	 * @param viewer
	 *            The {@link IViewer} for which to create the
	 *            {@link EventHandler}.
	 * @return The {@link EventHandler} that handles hover changes for the given
	 *         {@link IViewer}.
	 */
	protected EventHandler<MouseEvent> createHoverFilter(final IViewer viewer) {
		return new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent event) {
				// stop hover intent delay if mouse moved
				if (hoverIntentDelay.getStatus().equals(Status.RUNNING)) {
					double dx = initialHoverIntentScreenPosition.x
							- event.getScreenX();
					double dy = initialHoverIntentScreenPosition.y
							- event.getScreenY();
					if (Math.abs(dx) > HOVER_INTENT_MOUSE_MOVE_THRESHOLD || Math
							.abs(dy) > HOVER_INTENT_MOUSE_MOVE_THRESHOLD) {
						hoverIntentDelay.playFromStart();
						initialHoverIntentScreenPosition.x = event.getScreenX();
						initialHoverIntentScreenPosition.y = event.getScreenY();
					}
				}

				// only handle events where the mouse target changes
				if (!event.getEventType().equals(MouseEvent.MOUSE_MOVED)
						&& !event.getEventType()
								.equals(MouseEvent.MOUSE_ENTERED_TARGET)
						&& !event.getEventType()
								.equals(MouseEvent.MOUSE_EXITED_TARGET)) {
					return;
				}

				// determine new target
				EventTarget eventTarget = event.getTarget();
				if (eventTarget instanceof Node) {
					// determine hover policies
					Collection<? extends IOnHoverPolicy> policies = getTargetPolicyResolver()
							.getTargetPolicies(HoverTool.this,
									(Node) eventTarget, ON_HOVER_POLICY_KEY);
					getDomain().openExecutionTransaction(HoverTool.this);
					// active policies are unnecessary because hover is not a
					// gesture, just one event at one point in time
					for (IOnHoverPolicy policy : policies) {
						policy.hover(event);
					}
					getDomain().closeExecutionTransaction(HoverTool.this);

					if (eventTarget != hoverIntent) {
						potentialHoverIntent = (Node) eventTarget;
						initialHoverIntentScreenPosition = new Point(
								event.getScreenX(), event.getScreenY());
						hoverIntentDelay.playFromStart();

						if (hoverIntent != null) {
							if (!unhoverIntentDelay.getStatus()
									.equals(Status.RUNNING)) {
								unhoverIntentDelay.playFromStart();
							}
						} else {
							unhoverIntentDelay.stop();
						}
					} else {
						hoverIntentDelay.stop();
						unhoverIntentDelay.stop();
					}
				}
			}
		};
	}

	/**
	 *
	 * @param hoverIntent
	 *            The hover intent {@link Node}.
	 */
	protected void delegateHoverIntent(Node hoverIntent) {
		// determine hover policies
		Collection<? extends IOnHoverPolicy> policies = getTargetPolicyResolver()
				.getTargetPolicies(HoverTool.this, this.hoverIntent,
						ON_HOVER_POLICY_KEY);
		getDomain().openExecutionTransaction(HoverTool.this);
		// active policies are unnecessary because hover is not a
		// gesture, just one event at one point in time
		for (IOnHoverPolicy policy : policies) {
			policy.hoverIntent(hoverIntent);
		}
		getDomain().closeExecutionTransaction(HoverTool.this);
	}

	@Override
	protected void doActivate() {
		super.doActivate();
		for (IViewer viewer : getDomain().getViewers().values()) {
			Scene scene = viewer.getCanvas().getScene();
			if (!hoverFilters.containsKey(scene)) {
				EventHandler<MouseEvent> hoverFilter = createHoverFilter(
						viewer);
				scene.addEventFilter(MouseEvent.ANY, hoverFilter);
				hoverFilters.put(scene, hoverFilter);
			}
		}
	}

	@Override
	protected void doDeactivate() {
		hoverIntentDelay.stop();
		unhoverIntentDelay.stop();
		for (Scene scene : hoverFilters.keySet()) {
			scene.removeEventFilter(MouseEvent.ANY, hoverFilters.remove(scene));
		}
		super.doDeactivate();
	}

	/**
	 * Callback method that is invoked when the mouse was stationary over a
	 * visual for some amount of time.
	 */
	protected void onHoverIntentDelayFinished() {
		unhoverIntentDelay.stop();
		hoverIntent = potentialHoverIntent;
		potentialHoverIntent = null;
		delegateHoverIntent(hoverIntent);
	}

	/**
	 * Callback method that is invoked when the mouse was not stationary and did
	 * not move over the current hover intent for some amount of time.
	 */
	protected void onUnhoverIntentDelayFinished() {
		delegateHoverIntent(null);
		hoverIntent = null;
	}
}
