/*******************************************************************************
 * Copyright (c) 2015 itemis AG and others.
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

import javafx.animation.AnimationTimer;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

import org.eclipse.gef4.mvc.fx.operations.FXChangeViewportOperation;
import org.eclipse.gef4.mvc.models.ViewportModel;

public class FXPanOnTypePolicy extends AbstractFXOnTypePolicy {

	public static final double DEFAULT_SCROLL_AMOUNT_PER_SECOND = 150d;

	private final AnimationTimer timer = new AnimationTimer() {
		@Override
		public void handle(long nanos) {
			long now = System.currentTimeMillis();
			// compute millis pressed per direction
			if (isDown) {
				currentMillisDown = now - startMillisDown;
			} else if (isUp) {
				currentMillisUp = now - startMillisUp;
			} else if (isLeft) {
				currentMillisLeft = now - startMillisLeft;
			} else if (isRight) {
				currentMillisRight = now - startMillisRight;
			}
			updateScrollPosition();
		}
	};
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
	// initial translation values
	private double initialTranslateX;
	private double initialTranslateY;

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

	protected ViewportModel getViewportModel() {
		return getHost().getRoot().getViewer().getAdapter(ViewportModel.class);
	}

	@Override
	public void pressed(KeyEvent event) {
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
			initialTranslateX = getViewportModel().getTranslateX();
			initialTranslateY = getViewportModel().getTranslateY();
			timer.start();
			isRunning = true;
		}
	}

	@Override
	public void released(KeyEvent event) {
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
			totalMillisDown = 0;
			totalMillisUp = 0;
			totalMillisLeft = 0;
			totalMillisRight = 0;
		}
	}

	protected void updateScrollPosition() {
		double scrollAmount = getScrollAmountPerSecond();
		double dx = ((totalMillisLeft + currentMillisLeft) / 1000d)
				* scrollAmount
				- ((totalMillisRight + currentMillisRight) / 1000d)
				* scrollAmount;
		double dy = ((totalMillisUp + currentMillisUp) / 1000d) * scrollAmount
				- ((totalMillisDown + currentMillisDown) / 1000d)
				* scrollAmount;
		getHost()
				.getRoot()
				.getViewer()
				.getDomain()
				.execute(
						new FXChangeViewportOperation(getViewportModel(),
								initialTranslateX + dx, initialTranslateY + dy));
	}

}