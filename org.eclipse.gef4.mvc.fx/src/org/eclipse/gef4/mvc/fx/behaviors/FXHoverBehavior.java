/*******************************************************************************
 * Copyright (c) 2014 itemis AG and others.
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
import org.eclipse.gef4.mvc.models.HoverModel;
import org.eclipse.gef4.mvc.parts.IHandlePart;
import org.eclipse.gef4.mvc.parts.IVisualPart;

/**
 * A hover behavior that in addition to the {@link HoverBehavior} adds hover
 * handles and lightweight (JavaFX effect) hover feedback to IHandlePart hosts.
 *
 * @author anyssen
 * @author wienand
 *
 */
public class FXHoverBehavior extends HoverBehavior<Node> {

	public static final int REMOVAL_DELAY_MILLIS = 500;
	public static final int CREATION_DELAY_MILLIS = 500;
	public static final double MOUSE_MOVE_THRESHOLD = 4;

	private final Map<IVisualPart<Node, ? extends Node>, Effect> effects = new HashMap<IVisualPart<Node, ? extends Node>, Effect>();
	private PauseTransition creationDelayTransition;
	private PauseTransition removalDelayTransition;
	private Point initialPointerLocation;

	private final EventHandler<MouseEvent> mouseMoveHandler = new EventHandler<MouseEvent>() {
		@Override
		public void handle(MouseEvent event) {
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
	};

	@Override
	protected void addFeedback(
			List<? extends IVisualPart<Node, ? extends Node>> targets,
			Map<Object, Object> contextMap) {
		if (getHost() instanceof IHandlePart) {
			for (IVisualPart<Node, ? extends Node> part : targets) {
				Node visual = part.getVisual();
				effects.put(part, visual.getEffect());
				visual.setEffect(getHandleHoverFeedbackEffect(contextMap));
			}
		} else {
			super.addFeedback(targets, contextMap);
		}
	}

	protected void afterCreationDelay() {
		// check if still hovered
		if (isaHoverPart(getHoverModel().getHover())) {
			addHandles(Collections.singletonList(getHost()));
		}
	}

	protected void afterRemovalDelay() {
		// check if still un-hovered
		if (!isaHoverPart(getHoverModel().getHover())) {
			removeFeedback(Collections.singletonList(getHost()));
			removeHandles(Collections.singletonList(getHost()));
		}
	}

	@Override
	public void deactivate() {
		// stop timers
		if (creationDelayTransition != null) {
			stopCreationTimer();
		}
		if (removalDelayTransition != null) {
			removalDelayTransition.stop();
		}
		// deactivate rest
		super.deactivate();
	}

	public Effect getHandleHoverFeedbackEffect(Map<Object, Object> contextMap) {
		DropShadow effect = new DropShadow();
		effect.setRadius(5);
		return effect;
	}

	@Override
	protected HoverModel<Node> getHoverModel() {
		return super.getHoverModel();
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
		if (getHost() == part) {
			return true;
		}
		List<IHandlePart<Node, ? extends Node>> handleParts = getHandleParts();
		if (handleParts == null || handleParts.isEmpty() || part == null) {
			return false;
		}
		return handleParts.contains(part);
	}

	@Override
	protected void onHoverChange(IVisualPart<Node, ? extends Node> oldHovered,
			IVisualPart<Node, ? extends Node> newHovered) {
		// determine if the host or any hover handle part is/was hovered
		boolean isHovered = isaHoverPart(newHovered);
		boolean wasHovered = isaHoverPart(oldHovered);
		// check if initially hovered
		if (isHovered && !wasHovered) {
			// if hovered during removal delay, we stop the removal
			if (removalDelayTransition != null
					&& Animation.Status.RUNNING.equals(removalDelayTransition
							.getStatus())) {
				removalDelayTransition.stop();
				return;
			}
			// register mouse move filters
			final Scene scene = getHost().getVisual().getScene();
			scene.addEventFilter(MouseEvent.MOUSE_MOVED, mouseMoveHandler);
			scene.addEventFilter(MouseEvent.MOUSE_DRAGGED, mouseMoveHandler);
			// and start the creation delay
			creationDelayTransition = new PauseTransition(
					Duration.millis(CREATION_DELAY_MILLIS));
			creationDelayTransition
					.setOnFinished(new EventHandler<ActionEvent>() {
						@Override
						public void handle(ActionEvent event) {
							scene.removeEventFilter(MouseEvent.MOUSE_MOVED,
									mouseMoveHandler);
							scene.removeEventFilter(MouseEvent.MOUSE_DRAGGED,
									mouseMoveHandler);
							afterCreationDelay();
						}
					});
			creationDelayTransition.play();
			// and generate feedback
			addFeedback(Collections.singletonList(getHost()));
		} else if (wasHovered) {
			// if un-hovered during creation delay, we stop the creation
			if (creationDelayTransition != null
					&& Animation.Status.RUNNING.equals(creationDelayTransition
							.getStatus())) {
				stopCreationTimer();
			}
			// if no hover handles were created, remove feedback and be happy
			if (getHandleParts() == null || getHandleParts().isEmpty()) {
				removeFeedback(Collections.singletonList(getHost()));
				return;
			}
			// otherwise, we start the removal delay
			removalDelayTransition = new PauseTransition(
					Duration.millis(REMOVAL_DELAY_MILLIS));
			removalDelayTransition
					.setOnFinished(new EventHandler<ActionEvent>() {
						@Override
						public void handle(ActionEvent event) {
							afterRemovalDelay();
						}
					});
			removalDelayTransition.play();
		}
	}

	@Override
	protected void removeFeedback(
			List<? extends IVisualPart<Node, ? extends Node>> targets) {
		if (getHost() instanceof IHandlePart) {
			for (IVisualPart<Node, ? extends Node> part : targets) {
				Node visual = part.getVisual();
				visual.setEffect(effects.remove(part));
			}
		} else {
			super.removeFeedback(targets);
		}
	}

	protected void stopCreationTimer() {
		// unregister mouse move handler
		final Scene scene = getHost().getVisual().getScene();
		scene.removeEventFilter(MouseEvent.MOUSE_MOVED, mouseMoveHandler);
		scene.removeEventFilter(MouseEvent.MOUSE_DRAGGED, mouseMoveHandler);
		// reset initial pointer location
		initialPointerLocation = null;
		// and stop transition
		creationDelayTransition.stop();
	}

}
