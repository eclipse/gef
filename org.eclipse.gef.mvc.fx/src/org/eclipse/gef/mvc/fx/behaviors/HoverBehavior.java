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
package org.eclipse.gef.mvc.fx.behaviors;

import java.util.Collections;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Map;

import org.eclipse.gef.common.collections.ObservableSetMultimap;
import org.eclipse.gef.fx.utils.CursorUtils;
import org.eclipse.gef.geometry.planar.Point;
import org.eclipse.gef.mvc.fx.models.HoverModel;
import org.eclipse.gef.mvc.fx.parts.AbstractFeedbackPart;
import org.eclipse.gef.mvc.fx.parts.AbstractHandlePart;
import org.eclipse.gef.mvc.fx.parts.IFeedbackPart;
import org.eclipse.gef.mvc.fx.parts.IFeedbackPartFactory;
import org.eclipse.gef.mvc.fx.parts.IHandlePart;
import org.eclipse.gef.mvc.fx.parts.IHandlePartFactory;
import org.eclipse.gef.mvc.fx.parts.IVisualPart;
import org.eclipse.gef.mvc.fx.viewer.IViewer;

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

/**
 * The {@link HoverBehavior} can be registered on an {@link IVisualPart} in
 * order to react to {@link HoverModel} changes. It generates
 * {@link AbstractFeedbackPart}s and {@link AbstractHandlePart}s.
 *
 * @author mwienand
 *
 */
public class HoverBehavior extends AbstractBehavior {

	/**
	 * The adapter role for the {@link IFeedbackPartFactory} that is used to
	 * generate hover feedback parts.
	 */
	public static final String HOVER_FEEDBACK_PART_FACTORY = "HOVER_FEEDBACK_PART_FACTORY";

	/**
	 * The adapter role for the {@link IHandlePartFactory} that is used to
	 * generate hover handle parts.
	 */
	public static final String HOVER_HANDLE_PART_FACTORY = "HOVER_HANDLE_PART_FACTORY";

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

	private ChangeListener<IVisualPart<? extends Node>> hoverObserver = new ChangeListener<IVisualPart<? extends Node>>() {
		@Override
		public void changed(
				ObservableValue<? extends IVisualPart<? extends Node>> observable,
				IVisualPart<? extends Node> oldValue,
				IVisualPart<? extends Node> newValue) {
			onHoverChange(oldValue, newValue);
		}
	};

	private final Map<IVisualPart<? extends Node>, Effect> effects = new HashMap<>();

	private Map<IVisualPart<? extends Node>, PauseTransition> handleCreationDelayTransitions = new IdentityHashMap<>();

	private Map<IVisualPart<? extends Node>, PauseTransition> handleRemovalDelayTransitions = new IdentityHashMap<>();

	private Point initialPointerLocation;

	private final EventHandler<MouseEvent> mouseMoveHandler = new EventHandler<MouseEvent>() {
		@Override
		public void handle(MouseEvent event) {
			onMouseMove(event);
		}
	};

	private Map<IVisualPart<? extends Node>, ChangeListener<IVisualPart<? extends Node>>> hoveredParentChangeListeners = new IdentityHashMap<>();

	@Override
	protected void doActivate() {
		// register
		HoverModel hoverModel = getHoverModel();
		hoverModel.hoverProperty().addListener(hoverObserver);

		// create feedback and handles if we are already hovered
		IVisualPart<? extends Node> hover = hoverModel.getHover();
		if (hover != null) {
			onHoverChange(null, hover);
		}
	}

	@Override
	protected void doDeactivate() {
		HoverModel hoverModel = getHoverModel();

		// remove any pending feedback and handles
		IVisualPart<? extends Node> hover = hoverModel.getHover();
		if (hover != null) {
			onHoverChange(hover, null);
		}

		// unregister
		hoverModel.hoverProperty().removeListener(hoverObserver);

		stopAllDelays();
		// remove any pending feedback and handles
		clearFeedback();
		clearHandles();
	}

	@Override
	protected IFeedbackPartFactory getFeedbackPartFactory(IViewer viewer) {
		return getFeedbackPartFactory(viewer, HOVER_FEEDBACK_PART_FACTORY);
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

	@Override
	protected IHandlePartFactory getHandlePartFactory(IViewer viewer) {
		return getHandlePartFactory(viewer, HOVER_HANDLE_PART_FACTORY);
	}

	/**
	 * Returns the {@link HoverModel} in the context of the {@link #getHost()
	 * host}.
	 *
	 * @return The {@link HoverModel} in the context of the {@link #getHost()
	 *         host}.
	 */
	protected HoverModel getHoverModel() {
		IViewer viewer = getHost().getRoot().getViewer();
		HoverModel hoverModel = viewer.getAdapter(HoverModel.class);
		return hoverModel;
	}

	/**
	 * Returns <code>true</code> when the creation delay is currently running.
	 * Otherwise returns <code>false</code>.
	 *
	 * @param part
	 *            The {@link IVisualPart} for which to determine if the creation
	 *            delay is running.
	 * @return <code>true</code> when the creation delay is currently running,
	 *         otherwise <code>false</code>.
	 */
	protected boolean isInCreationDelay(IVisualPart<? extends Node> part) {
		return handleCreationDelayTransitions.containsKey(part)
				&& Animation.Status.RUNNING.equals(
						handleCreationDelayTransitions.get(part).getStatus());
	}

	/**
	 * Returns <code>true</code> when the removal delay is currently running.
	 * Otherwise returns <code>false</code>.
	 *
	 * @param part
	 *            The {@link IVisualPart} for which to determine if a removal
	 *            delay is currently running.
	 * @return <code>true</code> when the removal delay is currently running,
	 *         otherwise <code>false</code>.
	 */
	protected boolean isInRemovalDelay(IVisualPart<? extends Node> part) {
		return handleRemovalDelayTransitions.containsKey(part)
				&& Animation.Status.RUNNING.equals(
						handleRemovalDelayTransitions.get(part).getStatus());
	}

	/**
	 * Called when the creation delay for the given part runs out, i.e. when
	 * hover handles need to be created for the given part.
	 *
	 * @param hoveredPart
	 *            The part for which to create hover handles.
	 */
	protected void onCreationDelay(IVisualPart<? extends Node> hoveredPart) {
		unregisterMouseHandler();
		initialPointerLocation = null;
		addHandles(hoveredPart);
		ChangeListener<IVisualPart<? extends Node>> parentChangeListener = new ChangeListener<IVisualPart<? extends Node>>() {
			@Override
			public void changed(
					ObservableValue<? extends IVisualPart<? extends Node>> observable,
					IVisualPart<? extends Node> oldValue,
					IVisualPart<? extends Node> newValue) {
				hoveredParentChangeListeners.remove(hoveredPart);
				observable.removeListener(this);
				stopRemovalDelay(hoveredPart);
				if (hasHandles(hoveredPart)) {
					removeHandles(hoveredPart);
				}
				if (hasFeedback(hoveredPart)) {
					removeFeedback(hoveredPart);
				}
			}
		};
		hoveredParentChangeListeners.put(hoveredPart, parentChangeListener);
		hoveredPart.parentProperty().addListener(parentChangeListener);
		handleCreationDelayTransitions.remove(hoveredPart);
	}

	/**
	 * Called when the given part, or any of its hover handles, is hovered after
	 * none of them was hovered.
	 *
	 * @param hoveredPart
	 *            The part that was just hovered.
	 */
	protected void onHover(IVisualPart<? extends Node> hoveredPart) {
		if (isInRemovalDelay(hoveredPart)) {
			stopRemovalDelay(hoveredPart);
		} else {
			addFeedback(hoveredPart);
			startCreationDelay(hoveredPart);
		}
	}

	private void onHoverChange(IVisualPart<? extends Node> oldHovered,
			IVisualPart<? extends Node> newHovered) {
		if (oldHovered != null) {
			if (oldHovered instanceof IHandlePart) {
				// unhovering a handle part
				// remove feedback effect
				if (effects.containsKey(oldHovered)) {
					oldHovered.getVisual()
							.setEffect(effects.remove(oldHovered));
				} else {
					throw new IllegalStateException(
							"Cannot unhover/restore effect <" + oldHovered
									+ ">.");
				}
				// start removal delay for the host part
				ObservableSetMultimap<IVisualPart<? extends Node>, String> anchorages = oldHovered
						.getAnchoragesUnmodifiable();
				for (IVisualPart<? extends Node> anchorage : anchorages
						.keySet()) {
					if (hasHandles(anchorage)) {
						if (anchorage.getRoot() != null) {
							startRemovalDelay(anchorage);
						}
						break;
					}
				}
			} else {
				onUnhover(oldHovered);
			}
		}
		if (newHovered != null) {
			if (newHovered instanceof IHandlePart) {
				// hovering a handle part
				// add feedback effect
				effects.put(newHovered, newHovered.getVisual().getEffect());
				newHovered.getVisual().setEffect(
						getHandleHoverFeedbackEffect(Collections.emptyMap()));
				// stop removal delay for the host part
				ObservableSetMultimap<IVisualPart<? extends Node>, String> anchorages = newHovered
						.getAnchoragesUnmodifiable();
				for (IVisualPart<? extends Node> anchorage : anchorages
						.keySet()) {
					if (hasHandles(anchorage)) {
						stopRemovalDelay(anchorage);
						break;
					}
				}
			} else {
				onHover(newHovered);
			}
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
		double dx = event.getScreenX() - initialPointerLocation.x;
		double dy = event.getScreenY() - initialPointerLocation.y;
		if (Math.abs(dx) > MOUSE_MOVE_THRESHOLD
				|| Math.abs(dy) > MOUSE_MOVE_THRESHOLD) {
			// update pointer location
			initialPointerLocation = CursorUtils.getPointerLocation();
			// restart creation timer when the mouse is moved beyond
			// the threshold
			for (PauseTransition transition : handleCreationDelayTransitions
					.values()) {
				transition.playFromStart();
			}
		}
	}

	/**
	 * Called when the finish callback of the removal delay for the given part
	 * is executed.
	 *
	 * @param hoveredPart
	 *            The part for which to remove feedback and handles.
	 */
	protected void onRemovalDelay(IVisualPart<? extends Node> hoveredPart) {
		removeFeedback(hoveredPart);
		removeHandles(hoveredPart);
		if (hoveredParentChangeListeners.containsKey(hoveredPart)) {
			hoveredPart.parentProperty().removeListener(
					hoveredParentChangeListeners.remove(hoveredPart));
		}
	}

	/**
	 * Called when the given part, and all of its hover handles, are unhovered
	 * after any one of them was previously hovered.
	 *
	 * @param hoveredPart
	 *            The part that was previously hovered.
	 */
	protected void onUnhover(IVisualPart<? extends Node> hoveredPart) {
		if (!hasHandles(hoveredPart)) {
			if (isInCreationDelay(hoveredPart)) {
				stopCreationDelay(hoveredPart);
			}
			// XXX: Feedback could have been removed by the
			// hoveredParentChangeListener, therefore, we need to check if
			// feedback needs to be removed here.
			if (hasFeedback(hoveredPart)) {
				removeFeedback(hoveredPart);
			}
		} else {
			if (hoveredPart.getRoot() == null
					|| getHandles(hoveredPart).isEmpty()) {
				onRemovalDelay(hoveredPart);
			} else {
				startRemovalDelay(hoveredPart);
			}
		}
	}

	/**
	 * Registers the mouse handler that restarts the creation delay upon mouse
	 * moves.
	 *
	 * @param part
	 *            The {@link IVisualPart} that determines the {@link Scene} to
	 *            which a mouse handler is added.
	 */
	protected void registerMouseHandler(IVisualPart<? extends Node> part) {
		// store current pointer location to measure mouse movement
		initialPointerLocation = CursorUtils.getPointerLocation();
		final Scene scene = part.getVisual().getScene();
		scene.addEventFilter(MouseEvent.MOUSE_MOVED, mouseMoveHandler);
		scene.addEventFilter(MouseEvent.MOUSE_DRAGGED, mouseMoveHandler);
	}

	/**
	 * Starts the handle creation delay for the given visual part.
	 *
	 * @param hoveredPart
	 *            The part that was recently hovered.
	 */
	protected void startCreationDelay(
			final IVisualPart<? extends Node> hoveredPart) {
		registerMouseHandler(hoveredPart);
		// start creation delay transition
		PauseTransition transition = new PauseTransition(
				Duration.millis(CREATION_DELAY_MILLIS));
		handleCreationDelayTransitions.put(hoveredPart, transition);
		transition.setOnFinished(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				onCreationDelay(hoveredPart);
			}
		});
		transition.play();
	}

	/**
	 * Start the handle removal delay for the given visual part.
	 *
	 * @param hoveredPart
	 *            The part that was recently unhovered.
	 */
	protected void startRemovalDelay(
			final IVisualPart<? extends Node> hoveredPart) {
		PauseTransition transition = new PauseTransition(
				Duration.millis(REMOVAL_DELAY_MILLIS));
		handleRemovalDelayTransitions.put(hoveredPart, transition);
		transition.setOnFinished(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				onRemovalDelay(hoveredPart);
			}
		});
		transition.play();
	}

	/**
	 * Stops all creation and delay transitions.
	 */
	protected void stopAllDelays() {
		for (PauseTransition transition : handleCreationDelayTransitions
				.values()) {
			transition.stop();
		}
		handleCreationDelayTransitions.clear();
		for (PauseTransition transition : handleRemovalDelayTransitions
				.values()) {
			transition.stop();
		}
		handleCreationDelayTransitions.clear();
	}

	/**
	 * Stops the handle creation delay.
	 *
	 * @param part
	 *            The {@link IVisualPart} for which to stop the creation delay.
	 */
	protected void stopCreationDelay(IVisualPart<? extends Node> part) {
		PauseTransition transition = handleCreationDelayTransitions
				.remove(part);
		if (transition != null) {
			transition.stop();
		}
		unregisterMouseHandler();
	}

	/**
	 * Stops the handle removal delay.
	 *
	 * @param part
	 *            The {@link IVisualPart} for which to stop the removal delay.
	 */
	protected void stopRemovalDelay(IVisualPart<? extends Node> part) {
		PauseTransition transition = handleRemovalDelayTransitions.remove(part);
		if (transition != null) {
			transition.stop();
		}
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
