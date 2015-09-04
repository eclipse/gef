/*******************************************************************************
 * Copyright (c) 2014, 2015 itemis AG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Matthias Wienand (itemis AG) - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.gef4.mvc.fx.behaviors;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.gef4.fx.nodes.FXUtils;
import org.eclipse.gef4.geometry.planar.Point;
import org.eclipse.gef4.mvc.behaviors.HoverBehavior;
import org.eclipse.gef4.mvc.fx.parts.AbstractFXFeedbackPart;
import org.eclipse.gef4.mvc.fx.parts.AbstractFXHandlePart;
import org.eclipse.gef4.mvc.models.HoverModel;
import org.eclipse.gef4.mvc.parts.IFeedbackPart;
import org.eclipse.gef4.mvc.parts.IHandlePart;
import org.eclipse.gef4.mvc.parts.IVisualPart;

import javafx.animation.Animation;
import javafx.animation.PauseTransition;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.Effect;
import javafx.scene.input.MouseEvent;
import javafx.util.Duration;

/**
 * The {@link FXHoverBehavior} can be registered on an {@link IVisualPart} in
 * order to react to {@link HoverModel} changes. It generates
 * {@link AbstractFXFeedbackPart}s and {@link AbstractFXHandlePart}s.
 *
 * @author mwienand
 *
 */
public class FXHoverBehavior extends HoverBehavior<Node> {

	/**
	 * Time in milliseconds until the hover handles are removed when the host is
	 * not hovered anymore.
	 */
	public static final int REMOVAL_DELAY_MILLIS = 500;

	/**
	 * Time in milliseconds until the hover handles are created when the host is
	 * hovered.
	 */
	public static final int CREATION_DELAY_MILLIS = 250;

	/**
	 * Distance in pixels which the mouse is allowed to move so that it is
	 * regarded to be stationary.
	 */
	public static final double MOUSE_MOVE_THRESHOLD = 4;

	/**
	 * Searches for the specified part in the given list of root parts. Returns
	 * <code>true</code> if the part can be found. Otherwise returns
	 * <code>false</code>.
	 *
	 * @param rootParts
	 *            List of root parts to search through.
	 * @param part
	 *            The part that is checked for containment.
	 * @return <code>true</code> when the part is contained in the hierarchy
	 *         given by <i>rootParts</i>, otherwise <code>false</code>.
	 */
	public static boolean isContained(
			List<? extends IVisualPart<Node, ? extends Node>> rootParts,
			IVisualPart<Node, ? extends Node> part) {
		// validate arguments
		if (part == null) {
			return false;
		}
		// check root parts
		if (rootParts == null || rootParts.isEmpty()) {
			return false;
		}
		// recurse over root parts
		for (IVisualPart<Node, ? extends Node> root : rootParts) {
			if (root == part) {
				return true;
			}
			if (isContained(root.getChildren(), part)) {
				return true;
			}
		}
		return false;
	}

	private final Map<IVisualPart<Node, ? extends Node>, Effect> effects = new HashMap<IVisualPart<Node, ? extends Node>, Effect>();
	private boolean isFeedback;
	private boolean isHandles;
	private PauseTransition creationDelayTransition;
	private PauseTransition removalDelayTransition;
	private Point initialPointerLocation;
	private final EventHandler<MouseEvent> mouseMoveHandler = new EventHandler<MouseEvent>() {
		@Override
		public void handle(MouseEvent event) {
			onMouseMove(event);
		}
	};

	@Override
	protected void addFeedback(
			List<? extends IVisualPart<Node, ? extends Node>> targets,
			Map<Object, Object> contextMap) {
		isFeedback = true;
		if (getHost() instanceof IHandlePart) {
			// add effect to handle parts as feedback, because feedback parts
			// would be drawn behind the handles
			for (IVisualPart<Node, ? extends Node> part : targets) {
				Node visual = part.getVisual();
				effects.put(part, visual.getEffect());
				visual.setEffect(getHandleHoverFeedbackEffect(contextMap));
			}
		} else {
			super.addFeedback(targets, contextMap);
		}
	}

	@Override
	public void deactivate() {
		if (isInCreationDelay()) {
			stopCreationDelay();
		}
		if (isInRemovalDelay()) {
			stopRemovalDelay();
		}
		super.deactivate();
	}

	/**
	 * Returns the {@link Effect} that is applied to {@link IHandlePart}s as a
	 * replacement for {@link IFeedbackPart}s which are created for normal
	 * parts.
	 *
	 * @param contextMap
	 *            A map with context information that might be needed to
	 *            identify the concrete creation context.
	 * @return The {@link Effect} that is applied to {@link IHandlePart}s as a
	 *         replacement for {@link IFeedbackPart}s which are created for
	 *         normal parts.
	 */
	public Effect getHandleHoverFeedbackEffect(Map<Object, Object> contextMap) {
		DropShadow effect = new DropShadow();
		effect.setRadius(5);
		return effect;
	}

	/**
	 * Returns <code>true</code> if the given {@link IVisualPart} is either the
	 * host or a handle part controlled by this behavior (
	 * {@link #getHandleParts()}).
	 *
	 * @param part
	 *            The {@link IVisualPart} to test.
	 * @return <code>true</code> if the given {@link IVisualPart} is either the
	 *         host ({@link #getHost()}) or a handle part controlled by this
	 *         behavior ({@link #getHandleParts()}), <code>false</code>
	 *         otherwise.
	 */
	protected boolean isHostOrHoverHandlePart(
			IVisualPart<Node, ? extends Node> part) {
		return getHost() == part || isContained(getHandleParts(), part);
	}

	/**
	 * Returns <code>true</code> when the creation delay is currently running.
	 * Otherwise returns <code>false</code>.
	 *
	 * @return <code>true</code> when the creation delay is currently running,
	 *         otherwise <code>false</code>.
	 */
	protected boolean isInCreationDelay() {
		return creationDelayTransition != null && Animation.Status.RUNNING
				.equals(creationDelayTransition.getStatus());
	}

	/**
	 * Returns <code>true</code> when the removal delay is currently running.
	 * Otherwise returns <code>false</code>.
	 *
	 * @return <code>true</code> when the removal delay is currently running,
	 *         otherwise <code>false</code>.
	 */
	protected boolean isInRemovalDelay() {
		return removalDelayTransition != null && Animation.Status.RUNNING
				.equals(removalDelayTransition.getStatus());
	}

	/**
	 * Called as soon as the creation delay finishes.
	 */
	protected void onCreationDelay() {
		unregisterMouseHandler();
		initialPointerLocation = null;
		addHandles(Collections.singletonList(getHost()));
		isHandles = !getHandleParts().isEmpty();
	}

	/**
	 * Called when the host, or any of its hover handles, is hovered after none
	 * of them was hovered.
	 */
	protected void onHover() {
		if (isInRemovalDelay()) {
			stopRemovalDelay();
		}
		if (!isFeedback) {
			addFeedback(Collections.singletonList(getHost()));
		}
		if (!isHandles) {
			startHandleCreationDelay();
		}
	}

	@Override
	protected void onHoverChange(IVisualPart<Node, ? extends Node> oldHovered,
			IVisualPart<Node, ? extends Node> newHovered) {
		boolean wasHovered = isHostOrHoverHandlePart(oldHovered);
		boolean isHovered = isHostOrHoverHandlePart(newHovered);
		if (!wasHovered && isHovered) {
			onHover();
		} else if (wasHovered && !isHovered) {
			onUnhover();
		}
	}

	/**
	 * Called when the mouse moves while the creation delay is running. Restarts
	 * the creation delay if the mouse moves past the
	 * {@link #MOUSE_MOVE_THRESHOLD}.
	 *
	 * @param event
	 *            The {@link MouseEvent} for the mouse move.
	 */
	protected void onMouseMove(MouseEvent event) {
		if (!isInCreationDelay()) {
			throw new IllegalStateException(
					"Mouse handler is active, although the creation timer is not running.");
		}
		double dx = event.getScreenX() - initialPointerLocation.x;
		double dy = event.getScreenY() - initialPointerLocation.y;
		if (Math.abs(dx) > MOUSE_MOVE_THRESHOLD
				|| Math.abs(dy) > MOUSE_MOVE_THRESHOLD) {
			// update pointer location
			initialPointerLocation = FXUtils.getPointerLocation();
			// restart creation timer when the mouse is moved beyond
			// the threshold
			creationDelayTransition.playFromStart();
		}
	}

	/**
	 * Called when the pressed visual is released and we are unhovered.
	 */
	protected void onRelease() {
		removeFeedback(Collections.singletonList(getHost()));
		isHandles = false;
		removeHandles(Collections.singletonList(getHost()));
	}

	/**
	 * Called when the removal delay finishes.
	 */
	protected void onRemovalDelay() {
		removeFeedback(Collections.singletonList(getHost()));
		isHandles = false;
		removeHandles(Collections.singletonList(getHost()));
	}

	/**
	 * Called when the host, and all of its hover handles, are unhovered after
	 * any one of them was previously hovered.
	 */
	protected void onUnhover() {
		if (!isHandles && isFeedback) {
			if (isInCreationDelay()) {
				stopCreationDelay();
			}
			removeFeedback(Collections.singletonList(getHost()));
		} else if (isHandles) {
			startHandleRemovalDelay();
		}
	}

	/**
	 * Registers the mouse handler that restarts the creation delay upon mouse
	 * moves.
	 */
	protected void registerMouseHandler() {
		// store current pointer location to measure mouse movement
		initialPointerLocation = FXUtils.getPointerLocation();
		final Scene scene = getHost().getVisual().getScene();
		scene.addEventFilter(MouseEvent.MOUSE_MOVED, mouseMoveHandler);
		scene.addEventFilter(MouseEvent.MOUSE_DRAGGED, mouseMoveHandler);
	}

	@Override
	protected void removeFeedback(
			List<? extends IVisualPart<Node, ? extends Node>> targets) {
		isFeedback = false;
		if (getHost() instanceof IHandlePart) {
			// replace feedback effect with the original effect
			for (IVisualPart<Node, ? extends Node> part : targets) {
				Node visual = part.getVisual();
				visual.setEffect(effects.remove(part));
			}
		} else {
			super.removeFeedback(targets);
		}
	}

	/**
	 * Starts the handle creation delay.
	 */
	protected void startHandleCreationDelay() {
		registerMouseHandler();
		// start creation delay transition
		creationDelayTransition = new PauseTransition(
				Duration.millis(CREATION_DELAY_MILLIS));
		creationDelayTransition.setOnFinished(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				onCreationDelay();
			}
		});
		creationDelayTransition.play();
	}

	/**
	 * Starts the handle removal delay.
	 */
	protected void startHandleRemovalDelay() {
		removalDelayTransition = new PauseTransition(
				Duration.millis(REMOVAL_DELAY_MILLIS));
		removalDelayTransition.setOnFinished(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				onRemovalDelay();
			}
		});
		removalDelayTransition.play();
	}

	/**
	 * Stops the handle creation delay.
	 */
	protected void stopCreationDelay() {
		creationDelayTransition.stop();
		unregisterMouseHandler();
	}

	/**
	 * Stops the handle removal delay.
	 */
	protected void stopRemovalDelay() {
		removalDelayTransition.stop();
	}

	/**
	 * Unregisters the mouse handler.
	 */
	protected void unregisterMouseHandler() {
		final Scene scene = getHost().getVisual().getScene();
		scene.removeEventFilter(MouseEvent.MOUSE_MOVED, mouseMoveHandler);
		scene.removeEventFilter(MouseEvent.MOUSE_DRAGGED, mouseMoveHandler);
		initialPointerLocation = null;
	}

}
