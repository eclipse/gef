/*******************************************************************************
 * Copyright (c) 2015, 2016 itemis AG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Matthias Wienand (itemis AG) - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.gef4.mvc.fx.policies;

import org.eclipse.gef4.fx.nodes.InfiniteCanvas;

import javafx.animation.AnimationTimer;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

/**
 * The {@link FXPanOnTypePolicy} is an {@link IFXOnTypePolicy} that performs
 * viewport panning via the keyboard.
 *
 * @author mwienand
 *
 */
public class FXPanOnTypePolicy extends AbstractFXInteractionPolicy
		implements IFXOnTypePolicy {

	/**
	 * The default scroll amount per second, i.e. how many pixels the viewport
	 * is moved per second.
	 */
	public static final double DEFAULT_SCROLL_AMOUNT_PER_SECOND = 150d;

	private AnimationTimer timer;
	// timer running?
	private boolean isRunning;
	// store pressed state for direction keys
	private boolean isDown;
	private boolean isUp;
	private boolean isLeft;
	private boolean isRight;
	// time (milli seconds) when a key was pressed
	private long startMillisDown;
	private long startMillisUp;
	private long startMillisLeft;
	private long startMillisRight;
	// current press duration (milli seconds)
	private long currentMillisDown = 0;
	private long currentMillisUp = 0;
	private long currentMillisLeft = 0;
	private long currentMillisRight = 0;
	// total press duration (milli seconds)
	private long totalMillisDown = 0;
	private long totalMillisUp = 0;
	private long totalMillisLeft = 0;
	private long totalMillisRight = 0;

	private boolean invalidGesture = false;

	/**
	 * Returns the amount of units scrolled per second when a direction key is
	 * pressed.
	 *
	 * @return The amount of units scrolled per second when a direction key is
	 *         pressed.
	 */
	public double getScrollAmountPerSecond() {
		return DEFAULT_SCROLL_AMOUNT_PER_SECOND;
	}

	/**
	 * Returns <code>true</code> if the given {@link KeyEvent} should trigger
	 * panning. Otherwise returns <code>false</code>. Per default, will return
	 * <code>true</code> if <code>&lt;Up&gt;</code>, <code>&lt;Down&gt;</code>,
	 * <code>&lt;Left&gt;</code>, <code>&lt;Right&gt;</code>
	 *
	 * @param event
	 *            The {@link KeyEvent} in question.
	 * @return <code>true</code> to indicate that the given {@link KeyEvent}
	 *         should trigger panning, otherwise <code>false</code>.
	 */
	protected boolean isPan(KeyEvent event) {
		return event.getCode().equals(KeyCode.DOWN)
				|| event.getCode().equals(KeyCode.UP)
				|| event.getCode().equals(KeyCode.LEFT)
				|| event.getCode().equals(KeyCode.RIGHT);
	}

	@Override
	public void pressed(KeyEvent event) {
		invalidGesture = !isPan(event);
		if (invalidGesture) {
			return;
		}

		long now = System.currentTimeMillis();
		if (!isDown && event.getCode().equals(KeyCode.DOWN)) {
			isDown = true;
			startMillisDown = now;
			currentMillisDown = 0;
		} else if (!isUp && event.getCode().equals(KeyCode.UP)) {
			isUp = true;
			startMillisUp = now;
			currentMillisUp = 0;
		} else if (!isLeft && event.getCode().equals(KeyCode.LEFT)) {
			isLeft = true;
			startMillisLeft = now;
			currentMillisLeft = 0;
		} else if (!isRight && event.getCode().equals(KeyCode.RIGHT)) {
			isRight = true;
			startMillisRight = now;
			currentMillisRight = 0;
		}

		if (!isRunning && (isDown || isUp || isLeft || isRight)) {
			FXChangeViewportPolicy viewportPolicy = getHost().getRoot()
					.getAdapter(FXChangeViewportPolicy.class);
			init(viewportPolicy);
			if (timer == null) {
				/*
				 * XXX: The animation timer cannot be constructed atop, because
				 * it will then only be called once; reason unknown.
				 */
				timer = new AnimationTimer() {
					@Override
					public void handle(long nanos) {
						long now = System.currentTimeMillis();
						// compute millis pressed per direction
						if (isDown) {
							currentMillisDown = now - startMillisDown;
						}
						if (isUp) {
							currentMillisUp = now - startMillisUp;
						}
						if (isLeft) {
							currentMillisLeft = now - startMillisLeft;
						}
						if (isRight) {
							currentMillisRight = now - startMillisRight;
						}
						updateScrollPosition();
					}
				};
			}
			timer.start();
			isRunning = true;
		}
	}

	@Override
	public void released(KeyEvent event) {
		if (invalidGesture) {
			return;
		}

		long now = System.currentTimeMillis();

		if (event.getCode().equals(KeyCode.DOWN)) {
			isDown = false;
			totalMillisDown += now - startMillisDown;
			currentMillisDown = 0;
		} else if (event.getCode().equals(KeyCode.UP)) {
			isUp = false;
			totalMillisUp += now - startMillisUp;
			currentMillisUp = 0;
		} else if (event.getCode().equals(KeyCode.LEFT)) {
			isLeft = false;
			totalMillisLeft += now - startMillisLeft;
			currentMillisLeft = 0;
		} else if (event.getCode().equals(KeyCode.RIGHT)) {
			isRight = false;
			totalMillisRight += now - startMillisRight;
			currentMillisRight = 0;
		}

		if (isRunning && !isDown && !isUp && !isLeft && !isRight) {
			isRunning = false;
			timer.stop();
			updateScrollPosition();
			FXChangeViewportPolicy viewportPolicy = getHost().getRoot()
					.getAdapter(FXChangeViewportPolicy.class);
			commit(viewportPolicy);
			totalMillisDown = 0;
			totalMillisUp = 0;
			totalMillisLeft = 0;
			totalMillisRight = 0;
		}
	}

	@Override
	public void typed(KeyEvent event) {
	}

	@Override
	public void unfocus() {
	}

	/**
	 * Computes the viewport translation and applies it to the
	 * {@link InfiniteCanvas} of the host's viewer using the
	 * {@link FXChangeViewportPolicy}.
	 */
	protected void updateScrollPosition() {
		double scrollAmount = getScrollAmountPerSecond();
		double dx = ((totalMillisLeft + currentMillisLeft) / 1000d)
				* scrollAmount
				- ((totalMillisRight + currentMillisRight) / 1000d)
						* scrollAmount;
		double dy = ((totalMillisUp + currentMillisUp) / 1000d) * scrollAmount
				- ((totalMillisDown + currentMillisDown) / 1000d)
						* scrollAmount;
		FXChangeViewportPolicy viewportPolicy = getHost().getRoot()
				.getAdapter(FXChangeViewportPolicy.class);
		viewportPolicy.scrollAbsolute(dx, dy);
	}

}