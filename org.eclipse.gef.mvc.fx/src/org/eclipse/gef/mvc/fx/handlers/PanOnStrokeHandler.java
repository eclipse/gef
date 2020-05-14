/*******************************************************************************
 * Copyright (c) 2015, 2017 itemis AG and others.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Matthias Wienand (itemis AG) - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.gef.mvc.fx.handlers;

import org.eclipse.gef.fx.nodes.InfiniteCanvas;
import org.eclipse.gef.mvc.fx.policies.ViewportPolicy;

import javafx.animation.AnimationTimer;
import javafx.geometry.Pos;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

/**
 * The {@link PanOnStrokeHandler} is an {@link IOnTypeHandler} that performs
 * viewport panning via the keyboard.
 *
 * @author mwienand
 *
 */
public class PanOnStrokeHandler extends AbstractHandler
		implements IOnStrokeHandler {

	/**
	 * The default scroll amount per second, i.e. how many pixels the viewport
	 * is moved per second.
	 */
	public static final double DEFAULT_SCROLL_AMOUNT_PER_SECOND = 150d;

	private PanningSupport panningSupport = new PanningSupport();

	private AnimationTimer timer;
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

	// save if gesture is valid
	private boolean invalidGesture = false;

	// cached during interaction
	private ViewportPolicy viewportPolicy;

	@Override
	public void abortPress() {
		if (invalidGesture) {
			return;
		}
		rollback(viewportPolicy);
		this.viewportPolicy = null;
	}

	/**
	 * Returns the {@link ViewportPolicy} that is to be used for changing the
	 * viewport. This method is called within {@link #initialPress(KeyEvent)}
	 * where the resulting policy is cached for the keyboard.
	 *
	 * @return The {@link ViewportPolicy} that is to be used for changing the
	 *         viewport.
	 */
	protected ViewportPolicy determineViewportPolicy() {
		return getHost().getRoot().getAdapter(ViewportPolicy.class);
	}

	@Override
	public void finalRelease(KeyEvent event) {
		if (invalidGesture) {
			return;
		}

		updateMillisOnKeyRelease(event);

		timer.stop();
		updateScrollPosition();
		commit(viewportPolicy);
		viewportPolicy = null;
		totalMillisDown = 0;
		totalMillisUp = 0;
		totalMillisLeft = 0;
		totalMillisRight = 0;
	}

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
	 * Returns the cached {@link ViewportPolicy} that was returned by
	 * {@link #determineViewportPolicy()} within
	 * {@link #initialPress(KeyEvent)}.
	 *
	 * @return The cached {@link ViewportPolicy}.
	 */
	public final ViewportPolicy getViewportPolicy() {
		return viewportPolicy;
	}

	@Override
	public void initialPress(KeyEvent event) {
		invalidGesture = !isPan(event);
		if (invalidGesture) {
			return;
		}

		// determine viewport policy to cache
		viewportPolicy = determineViewportPolicy();
		init(viewportPolicy);

		updateMillisOnKeyPress(event);

		if (timer == null) {
			// FIXME: Test if we can construct the timer during field
			// initialization. A previous comment indicated that this was not
			// possible.
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
	}

	/**
	 * Returns <code>true</code> to signify that scrolling and zooming is
	 * restricted to the content bounds, <code>false</code> otherwise.
	 * <p>
	 * When content-restricted, the policy behaves texteditor-like, i.e. the
	 * pivot point for zooming is at the top of the viewport and at the left of
	 * the contents, and free space is only allowed to the right and to the
	 * bottom of the contents. Therefore, the policy does not allow panning or
	 * zooming if it would result in free space within the viewport at the top
	 * or left sides of the contents.
	 *
	 * @return <code>true</code> to signify that scrolling and zooming is
	 *         restricted to the content bounds, <code>false</code> otherwise.
	 */
	protected boolean isContentRestricted() {
		return false;
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
	public void press(KeyEvent event) {
		updateMillisOnKeyPress(event);
	}

	@Override
	public void release(KeyEvent event) {
		updateMillisOnKeyRelease(event);
	}

	private void updateMillisOnKeyPress(KeyEvent event) {
		long now = System.currentTimeMillis();
		if (!isDown && event.getCode().equals(KeyCode.DOWN)) {
			startMillisDown = now;
			currentMillisDown = 0;
			isDown = true;
		} else if (!isUp && event.getCode().equals(KeyCode.UP)) {
			startMillisUp = now;
			currentMillisUp = 0;
			isUp = true;
		} else if (!isLeft && event.getCode().equals(KeyCode.LEFT)) {
			startMillisLeft = now;
			currentMillisLeft = 0;
			isLeft = true;
		} else if (!isRight && event.getCode().equals(KeyCode.RIGHT)) {
			startMillisRight = now;
			currentMillisRight = 0;
			isRight = true;
		}
	}

	private void updateMillisOnKeyRelease(KeyEvent event) {
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
	}

	/**
	 * Computes the viewport translation and applies it to the
	 * {@link InfiniteCanvas} of the host's viewer using the
	 * {@link ViewportPolicy}.
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
		viewportPolicy.scroll(false, dx, dy);

		// restrict panning to contents
		if (isContentRestricted()) {
			panningSupport.removeFreeSpace(viewportPolicy, Pos.TOP_LEFT, true);
			panningSupport.removeFreeSpace(viewportPolicy, Pos.BOTTOM_RIGHT,
					false);
		}
	}

}