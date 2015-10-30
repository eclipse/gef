/*******************************************************************************
 * Copyright (c) 2014, 2015 itemis AG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Alexander Nyßen (itemis AG) - initial API and implementation
 *     Matthias Wienand (itemis AG) - contributions for Bugzillas #449129 & #468780
 *
 *******************************************************************************/
package org.eclipse.gef4.mvc.fx.policies;

import org.eclipse.gef4.fx.nodes.InfiniteCanvas;
import org.eclipse.gef4.geometry.planar.Dimension;
import org.eclipse.gef4.mvc.fx.viewer.FXViewer;

import javafx.geometry.Bounds;
import javafx.scene.input.ScrollEvent;

/**
 * The {@link FXPanOnScrollPolicy} is an {@link AbstractFXOnScrollPolicy} that
 * pans (i.e. moves/scrolls) the viewport upon scrolling the mouse wheel.
 *
 * @author anyssen
 * @author mwienand
 *
 */
public class FXPanOnScrollPolicy extends AbstractFXOnScrollPolicy {

	private static final int SAME_SCROLL_MILLIS = 100;
	private long lastMillis = 0;
	private boolean stopped = false;

	/*
	 * TODO: stoppedHorizontal, stoppedVertical (as context)
	 */

	/**
	 * Applies the given translation to the viewport.
	 *
	 * @param dx
	 *            The horizontal translation.
	 * @param dy
	 *            The vertical translation.
	 */
	protected void applyPanning(double dx, double dy) {
		FXChangeViewportPolicy viewportPolicy = getHost().getRoot()
				.getAdapter(FXChangeViewportPolicy.class);
		init(viewportPolicy);
		viewportPolicy.scrollRelative(dx, dy);
		commit(viewportPolicy);
	}

	/**
	 * Computes the translation for the given {@link ScrollEvent}. The
	 * horizontal and vertical translation is inverted when
	 * {@link #isSwapDirection(ScrollEvent)} returns <code>true</code>.
	 *
	 * @param event
	 *            The original {@link ScrollEvent}.
	 * @return A {@link Dimension} storing the horizontal and vertical
	 *         translation.
	 */
	protected Dimension computeDelta(ScrollEvent event) {
		double dx = event.getDeltaX();
		double dy = event.getDeltaY();
		if (isSwapDirection(event)) {
			double t = dx;
			dx = dy;
			dy = t;
		}
		return new Dimension(dx, dy);
	}

	/**
	 * Returns <code>true</code> if the given {@link ScrollEvent} should trigger
	 * panning. Otherwise returns <code>false</code>.
	 *
	 * @param event
	 *            The {@link ScrollEvent} in question.
	 * @return <code>true</code> to indicate that the given {@link ScrollEvent}
	 *         should trigger panning, otherwise <code>false</code>.
	 */
	protected boolean isSuitable(ScrollEvent event) {
		// Do not scroll when a modifier key (<Alt>, <Control>, <Meta>) is
		// pressed.
		return !(event.isAltDown() || event.isControlDown()
				|| event.isMetaDown());
	}

	/**
	 * Returns <code>true</code> if the pan direction should be inverted for the
	 * given {@link ScrollEvent}. Otherwise returns <code>false</code>.
	 *
	 * @param event
	 *            The {@link ScrollEvent} in question.
	 * @return <code>true</code> if the pan direction should be inverted,
	 *         otherwise <code>false</code>.
	 */
	protected boolean isSwapDirection(ScrollEvent event) {
		// Swap horizontal/vertical when the <Shift> key is pressed.
		return event.isShiftDown();
	}

	@Override
	public void scroll(ScrollEvent event) {
		if (!isSuitable(event)) {
			return;
		}

		// Determine if this ScrollEvent belongs to the same gesture as the last
		// event by checking the system time. When events arise in rapid
		// succession, they are associated with the same gesture.
		long millis = System.currentTimeMillis();
		long deltaMillis = millis - lastMillis;
		lastMillis = millis;
		if (deltaMillis < SAME_SCROLL_MILLIS) {
			// Cancel processing if the gesture was stopped at the
			// content-bounds already. The next event that does not belong to
			// this same scroll gesture will advance the viewport beyond the
			// content-bounds.
			if (stopped) {
				return;
			}
		} else {
			stopped = false;
		}

		// Determine horizontal and vertical translation.
		Dimension delta = computeDelta(event);

		// Stop scrolling at the content-bounds.
		stopped = stopAtContentBounds(delta);

		// change viewport via operation
		applyPanning(delta.width, delta.height);
	}

	private boolean stopAtContentBounds(Dimension delta) {
		InfiniteCanvas infiniteCanvas = ((FXViewer) getHost().getRoot()
				.getViewer()).getCanvas();
		Bounds contentBounds = infiniteCanvas.getContentBounds();
		boolean stopped = false;
		if (contentBounds.getMinX() < 0
				&& contentBounds.getMinX() + delta.width >= 0) {
			// If the left side of the content-bounds was left-of the viewport
			// before scrolling and will not be left-of the viewport after
			// scrolling, then the left side of the content-bounds was reached
			// by scrolling. Therefore, scrolling should stop at the left side
			// of the content-bounds now.
			delta.width = -contentBounds.getMinX();
			stopped = true;
		} else if (contentBounds.getMaxX() > infiniteCanvas.getWidth()
				&& contentBounds.getMaxX() + delta.width <= infiniteCanvas
						.getWidth()) {
			// If the right side of the content-bounds was right-of the viewport
			// before scrolling and will not be right-of the viewport after
			// scrolling, then the right side of the content-bounds was reached
			// by scrolling. Therefore, scrolling should stop at the right side
			// of the content-bounds now.
			delta.width = infiniteCanvas.getWidth() - contentBounds.getMaxX();
			stopped = true;
		}
		if (contentBounds.getMinY() < 0
				&& contentBounds.getMinY() + delta.height >= 0) {
			// If the top side of the content-bounds was top-of the
			// viewport before scrolling and will not be top-of the viewport
			// after scrolling, then the top side of the content-bounds was
			// reached by scrolling. Therefore, scrolling should stop at the
			// top side of the content-bounds now.
			delta.height = -contentBounds.getMinY();
			stopped = true;
		} else if (contentBounds.getMaxY() > infiniteCanvas.getHeight()
				&& contentBounds.getMaxY() + delta.height <= infiniteCanvas
						.getHeight()) {
			// If the bottom side of the content-bounds was bottom-of the
			// viewport before scrolling and will not be top-of the viewport
			// after scrolling, then the bottom side of the content-bounds was
			// reached by scrolling. Therefore, scrolling should stop at the
			// bottom side of the content-bounds now.
			delta.height = infiniteCanvas.getHeight() - contentBounds.getMaxY();
			stopped = true;
		}
		return stopped;
	}

}
