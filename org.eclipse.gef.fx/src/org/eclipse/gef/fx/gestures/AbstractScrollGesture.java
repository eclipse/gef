/*******************************************************************************
 * Copyright (c) 2016 itemis AG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Matthias Wienand (itemis AG) - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.gef.fx.gestures;

import javafx.animation.PauseTransition;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.input.ScrollEvent;
import javafx.util.Duration;

/**
 * The {@link AbstractScrollGesture} can be used to react to mouse wheel scroll
 * events. The first scroll event starts the scroll gesture. Subsequent events
 * belong to the same gesture until no scroll events are received for the finish
 * duration, in which case the gesture ends. It is an abstract class that
 * defines three methods that have to be implemented by subclasses:
 * <ol>
 * <li>{@link #scrollStarted(ScrollEvent)}
 * <li>{@link #scroll(ScrollEvent)}
 * <li>{@link #scrollFinished()}
 * </ol>
 *
 * @author mwienand
 *
 */
public abstract class AbstractScrollGesture extends AbstractGesture {

	/**
	 * The default duration in milliseconds that has to pass without receiving a
	 * {@link ScrollEvent} so that the gesture is assumed to have finished.
	 * <p>
	 * Value: 180 (ms)
	 */
	public static final int DEFAULT_FINISH_DELAY_MILLIS = 180;

	private boolean inScroll;

	private PauseTransition finishDelayTransition = new PauseTransition(
			Duration.millis(DEFAULT_FINISH_DELAY_MILLIS));

	private EventHandler<ActionEvent> onFinishDelayTransition = new EventHandler<ActionEvent>() {
		@Override
		public void handle(ActionEvent event) {
			inScroll = false;
			scrollFinished();
		}
	};

	{
		finishDelayTransition.setCycleCount(1);
		finishDelayTransition.setOnFinished(onFinishDelayTransition);
	}

	private EventHandler<? super ScrollEvent> scrollFilter = new EventHandler<ScrollEvent>() {
		@Override
		public void handle(ScrollEvent event) {
			playFinishDelayTransition();
			if (!inScroll) {
				inScroll = true;
				scrollStarted(event);
			} else {
				scroll(event);
			}
		}
	};

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
	 */
	protected void playFinishDelayTransition() {
		finishDelayTransition.stop();
		finishDelayTransition
				.setDuration(Duration.millis(getFinishDelayMillis()));
		finishDelayTransition.playFromStart();
	}

	@Override
	protected void register() {
		getScene().addEventFilter(ScrollEvent.SCROLL, scrollFilter);
	}

	/**
	 * Callback method that is invoked for all but the first {@link ScrollEvent}
	 * of a scroll gesture.
	 *
	 * @param event
	 *            The corresponding {@link ScrollEvent}.
	 */
	protected abstract void scroll(ScrollEvent event);

	/**
	 * Callback method that is invoked when the scroll gesture ends, i.e. no
	 * {@link ScrollEvent} was fired for the number of milliseconds specified in
	 * {@link #DEFAULT_FINISH_DELAY_MILLIS}.
	 */
	protected abstract void scrollFinished();

	/**
	 * Callback method that is invoked for the first {@link ScrollEvent} of a
	 * scroll gesture.
	 *
	 * @param event
	 *            The corresponding {@link ScrollEvent}.
	 */
	protected abstract void scrollStarted(ScrollEvent event);

	@Override
	protected void unregister() {
		getScene().removeEventFilter(ScrollEvent.SCROLL, scrollFilter);
	}

}
