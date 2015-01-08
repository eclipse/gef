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
import org.eclipse.gef4.mvc.fx.parts.FXRootPart;
import org.eclipse.gef4.mvc.models.ViewportModel;

public class FXPanOnScrollPolicy extends AbstractFXScrollPolicy {

	private static final int SAME_SCROLL_MILLIS = 70;
	private long lastMillis = 0;
	private boolean stopped = false;

	@Override
	public void scroll(ScrollEvent event) {
		// do not scroll when a modifier key is down
		if (event.isAltDown() || event.isControlDown() || event.isMetaDown()
				|| event.isShiftDown()) {
			return;
		}

		// do not scroll when this gesture was stopped at the content bounds
		long millis = System.currentTimeMillis();
		if (millis - lastMillis < SAME_SCROLL_MILLIS) {
			if (stopped) {
				return;
			}
		} else {
			stopped = false;
		}
		lastMillis = millis;

		double x = event.getDeltaX();
		double y = event.getDeltaY();

		// stop at content bounds
		ScrollPaneEx scrollPane = ((FXRootPart) getHost().getRoot())
				.getScrollPane();
		Bounds contentBounds = scrollPane.getBoundsInViewport(scrollPane
				.getContentGroup());
		// horizontal
		if (contentBounds.getMinX() < 0 && contentBounds.getMinX() + x > 0) {
			x = -contentBounds.getMinX();
			stopped = true;
		}
		if (contentBounds.getMaxX() > scrollPane.getWidth()
				&& contentBounds.getMaxX() + x < scrollPane.getWidth()) {
			x = scrollPane.getWidth() - contentBounds.getMaxX();
			stopped = true;
		}
		// vertical
		if (contentBounds.getMinY() < 0 && contentBounds.getMinY() + y > 0) {
			y = -contentBounds.getMinY();
			stopped = true;
		}
		if (contentBounds.getMaxY() > scrollPane.getHeight()
				&& contentBounds.getMaxY() + y < scrollPane.getHeight()) {
			y = scrollPane.getHeight() - contentBounds.getMaxY();
			stopped = true;
		}

		// TODO obtain policy to manipulate the viewport model
		ViewportModel viewportModel = getHost().getRoot().getViewer()
				.getAdapter(ViewportModel.class);
		viewportModel.setTranslateX(viewportModel.getTranslateX() + x);
		viewportModel.setTranslateY(viewportModel.getTranslateY() + y);
	}

}
