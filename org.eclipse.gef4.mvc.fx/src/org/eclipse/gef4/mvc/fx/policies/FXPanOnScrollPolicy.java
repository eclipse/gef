/*******************************************************************************
 * Copyright (c) 2014, 2015 itemis AG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Alexander Nyßen (itemis AG) - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.gef4.mvc.fx.policies;

import javafx.geometry.Bounds;
import javafx.scene.input.ScrollEvent;

import org.eclipse.gef4.fx.nodes.ScrollPaneEx;
import org.eclipse.gef4.geometry.planar.Dimension;
import org.eclipse.gef4.mvc.fx.viewer.FXViewer;

public class FXPanOnScrollPolicy extends AbstractFXOnScrollPolicy {

	private static final int SAME_SCROLL_MILLIS = 100;
	private long lastMillis = 0;
	private boolean stopped = false;

	/*
	 * TODO: stoppedHorizontal, stoppedVertical (as context)
	 */

	protected void applyPanning(double dx, double dy) {
		FXChangeViewportPolicy viewportPolicy = getHost().getRoot()
				.getAdapter(FXChangeViewportPolicy.class);
		init(viewportPolicy);
		viewportPolicy.scrollRelative(dx, dy);
		commit(viewportPolicy);
	}

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

	protected boolean isSuitable(ScrollEvent event) {
		// Do not scroll when a modifier key (<Alt>, <Control>, <Meta>) is
		// pressed.
		return !(event.isAltDown() || event.isControlDown()
				|| event.isMetaDown());
	}

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
		ScrollPaneEx scrollPane = ((FXViewer) getHost().getRoot().getViewer())
				.getScrollPane();
		Bounds contentBounds = scrollPane
				.getBoundsInViewport(scrollPane.getContentGroup());
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
		} else if (contentBounds.getMaxX() > scrollPane.getWidth()
				&& contentBounds.getMaxX() + delta.width <= scrollPane
						.getWidth()) {
			// If the right side of the content-bounds was right-of the viewport
			// before scrolling and will not be right-of the viewport after
			// scrolling, then the right side of the content-bounds was reached
			// by scrolling. Therefore, scrolling should stop at the right side
			// of the content-bounds now.
			delta.width = scrollPane.getWidth() - contentBounds.getMaxX();
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
		} else if (contentBounds.getMaxY() > scrollPane.getHeight()
				&& contentBounds.getMaxY() + delta.height <= scrollPane
						.getHeight()) {
			// If the bottom side of the content-bounds was bottom-of the
			// viewport before scrolling and will not be top-of the viewport
			// after scrolling, then the bottom side of the content-bounds was
			// reached by scrolling. Therefore, scrolling should stop at the
			// bottom side of the content-bounds now.
			delta.height = scrollPane.getHeight() - contentBounds.getMaxY();
			stopped = true;
		}
		return stopped;
	}

}
