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

import javafx.animation.Animation;
import javafx.animation.PauseTransition;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.Effect;
import javafx.scene.input.MouseEvent;
import javafx.util.Duration;

import org.eclipse.gef4.geometry.planar.Point;
import org.eclipse.gef4.mvc.behaviors.HoverBehavior;
import org.eclipse.gef4.mvc.parts.IFeedbackPart;
import org.eclipse.gef4.mvc.parts.IHandlePart;
import org.eclipse.gef4.mvc.parts.IVisualPart;

public class FXHoverBehavior extends HoverBehavior<Node> {

	/**
	 * Searches the given {@link IVisualPart}s for a visual (JavaFX {@link Node}
	 * ) that is currently pressed, i.e. the mouse was pressed over the visual.
	 *
	 * @param parts
	 *            The list of parts to search for a pressed visual.
	 * @return The part whose visual is currently pressed, or <code>null</code>.
	 */
	public static IVisualPart<Node, ? extends Node> getPressed(
			List<? extends IVisualPart<Node, ? extends Node>> parts) {
		if (parts == null || parts.isEmpty()) {
			return null;
		}
		for (IVisualPart<Node, ? extends Node> part : parts) {
			if (part.getVisual().isPressed()) {
				return part;
			}
			IVisualPart<Node, ? extends Node> pressed = getPressed(part
					.getChildren());
			if (pressed != null) {
				return pressed;
			}
		}
		return null;
	}

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

	public static final int REMOVAL_DELAY_MILLIS = 500;
	public static final int CREATION_DELAY_MILLIS = 500;
	public static final double MOUSE_MOVE_THRESHOLD = 4;
	private final Map<IVisualPart<Node, ? extends Node>, Effect> effects = new HashMap<IVisualPart<Node, ? extends Node>, Effect>();
	private boolean isFeedback;
	private boolean isHandles;
	private PauseTransition creationDelayTransition;
	private PauseTransition removalDelayTransition;
	private Point initialPointerLocation;
	private Node pressedVisual;
	private final EventHandler<MouseEvent> mouseMoveHandler = new EventHandler<MouseEvent>() {
		@Override
		public void handle(MouseEvent event) {
			onMouseMove(event);
		}
	};
	private final ChangeListener<? super Boolean> pressedListener = new ChangeListener<Boolean>() {
		@Override
		public void changed(ObservableValue<? extends Boolean> observable,
				Boolean oldPressed, Boolean newPressed) {
			onRelease();
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
		if (isInPressedListener()) {
			removePressedListener();
		}
		super.deactivate();
	}

	/**
	 * Returns the {@link Effect} that is applied to {@link IHandlePart}s as a
	 * replacement for {@link IFeedbackPart}s which are created for normal
	 * parts.
	 *
	 * @param contextMap
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
	 * current host or a hover handle part. Otherwise returns <code>false</code>
	 * .
	 *
	 * @param part
	 * @return
	 */
	protected boolean isaHoverPart(IVisualPart<Node, ? extends Node> part) {
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
		return creationDelayTransition != null
				&& Animation.Status.RUNNING.equals(creationDelayTransition
						.getStatus());
	}

	/**
	 * Returns <code>true</code> when the pressed listener is currently active.
	 * Otherwise returns <code>false</code>.
	 *
	 * @return <code>true</code> when the pressed listener is currently active,
	 *         otherwise <code>false</code>.
	 */
	protected boolean isInPressedListener() {
		return pressedVisual != null;
	}

	/**
	 * Returns <code>true</code> when the removal delay is currently running.
	 * Otherwise returns <code>false</code>.
	 *
	 * @return <code>true</code> when the removal delay is currently running,
	 *         otherwise <code>false</code>.
	 */
	protected boolean isInRemovalDelay() {
		return removalDelayTransition != null
				&& Animation.Status.RUNNING.equals(removalDelayTransition
						.getStatus());
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
		if (isInPressedListener()) {
			removePressedListener();
		}
	}

	@Override
	protected void onHoverChange(IVisualPart<Node, ? extends Node> oldHovered,
			IVisualPart<Node, ? extends Node> newHovered) {
		boolean wasHovered = isaHoverPart(oldHovered);
		boolean isHovered = isaHoverPart(newHovered);
		if (!wasHovered && isHovered) {
			onHover();
		} else if (wasHovered && !isHovered) {
			onUnhover();
		}
	}

	/**
	 * Called when the mouse moves while the mouse handler is active.
	 */
	protected void onMouseMove(MouseEvent event) {
		if (!isInCreationDelay()) {
			throw new IllegalStateException(
					"Mouse handler is active, although the creation timer is not running.");
		}
		if (initialPointerLocation == null) {
			// TODO: Find out how to read the current pointer location, so
			// that we do not have to use the first position after a mouse
			// move.
			initialPointerLocation = new Point(event.getScreenX(),
					event.getScreenY());
		} else {
			double dx = event.getScreenX() - initialPointerLocation.x;
			double dy = event.getScreenY() - initialPointerLocation.y;
			if (Math.abs(dx) > MOUSE_MOVE_THRESHOLD
					|| Math.abs(dy) > MOUSE_MOVE_THRESHOLD) {
				// restart creation timer when the mouse is moved beyond
				// the threshold
				creationDelayTransition.playFromStart();
			}
		}
	}

	/**
	 * Called when the pressed visual is released and we are unhovered.
	 */
	protected void onRelease() {
		removePressedListener();
		removeFeedback(Collections.singletonList(getHost()));
		isHandles = false;
		removeHandles(Collections.singletonList(getHost()));
	}

	/**
	 * Called when the removal delay finishes.
	 */
	protected void onRemovalDelay() {
		IVisualPart<Node, ? extends Node> pressed = getPressed(getHandleParts());
		if (pressed == null) {
			removeFeedback(Collections.singletonList(getHost()));
			isHandles = false;
			removeHandles(Collections.singletonList(getHost()));
		} else {
			pressedVisual = pressed.getVisual();
			pressedVisual.pressedProperty().addListener(pressedListener);
		}
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

	protected void removePressedListener() {
		pressedVisual.pressedProperty().removeListener(pressedListener);
		pressedVisual = null;
	}

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

	protected void stopCreationDelay() {
		creationDelayTransition.stop();
		unregisterMouseHandler();
	}

	protected void stopRemovalDelay() {
		removalDelayTransition.stop();
	}

	protected void unregisterMouseHandler() {
		final Scene scene = getHost().getVisual().getScene();
		scene.removeEventFilter(MouseEvent.MOUSE_MOVED, mouseMoveHandler);
		scene.removeEventFilter(MouseEvent.MOUSE_DRAGGED, mouseMoveHandler);
		initialPointerLocation = null;
	}

}
