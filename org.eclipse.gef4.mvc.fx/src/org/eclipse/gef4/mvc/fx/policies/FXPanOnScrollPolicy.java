/*******************************************************************************
 * Copyright (c) 2014 itemis AG and others.
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
import org.eclipse.gef4.mvc.fx.operations.FXChangeViewportOperation;
import org.eclipse.gef4.mvc.fx.viewer.FXViewer;
import org.eclipse.gef4.mvc.models.ViewportModel;

public class FXPanOnScrollPolicy extends AbstractFXScrollPolicy {

	private static final int SAME_SCROLL_MILLIS = 100;
	private long lastMillis = 0;
	private boolean stopped = false;

	protected void applyPanning(double dx, double dy) {
		ViewportModel viewportModel = getHost().getRoot().getViewer()
				.getAdapter(ViewportModel.class);
		getHost()
				.getRoot()
				.getViewer()
				.getDomain()
				.execute(
						new FXChangeViewportOperation(viewportModel,
								viewportModel.getTranslateX() + dx,
								viewportModel.getTranslateY() + dy));
	}

	protected boolean isSuitable(ScrollEvent event) {
		// Do not scroll when a modifier key (<Alt>, <Control>, <Meta>) is
		// pressed.
		return !(event.isAltDown() || event.isControlDown() || event
				.isMetaDown());
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
		long delta = millis - lastMillis;
		lastMillis = millis;
		if (delta < SAME_SCROLL_MILLIS) {
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
		double dx = event.getDeltaX();
		double dy = event.getDeltaY();
		if (isSwapDirection(event)) {
			double t = dx;
			dx = dy;
			dy = t;
		}

		// Stop scrolling at the content-bounds.
		ScrollPaneEx scrollPane = ((FXViewer) getHost().getRoot().getViewer())
				.getScrollPane();
		Bounds contentBounds = scrollPane.getBoundsInViewport(scrollPane
				.getContentGroup());
		// horizontal
		if (contentBounds.getMinX() < 0 && contentBounds.getMinX() + dx >= 0) {
			dx = -contentBounds.getMinX();
			stopped = true;
		}
		if (contentBounds.getMaxX() > scrollPane.getWidth()
				&& contentBounds.getMaxX() + dx <= scrollPane.getWidth()) {
			dx = scrollPane.getWidth() - contentBounds.getMaxX();
			stopped = true;
		}
		// vertical
		if (contentBounds.getMinY() < 0 && contentBounds.getMinY() + dy >= 0) {
			dy = -contentBounds.getMinY();
			stopped = true;
		}
		if (contentBounds.getMaxY() > scrollPane.getHeight()
				&& contentBounds.getMaxY() + dy <= scrollPane.getHeight()) {
			dy = scrollPane.getHeight() - contentBounds.getMaxY();
			stopped = true;
		}

		// change viewport via operation
		applyPanning(dx, dy);
	}

}
