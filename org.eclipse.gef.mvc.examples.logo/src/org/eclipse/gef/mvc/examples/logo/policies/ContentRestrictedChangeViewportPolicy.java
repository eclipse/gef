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
package org.eclipse.gef.mvc.examples.logo.policies;

import org.eclipse.gef.fx.nodes.InfiniteCanvas;
import org.eclipse.gef.mvc.fx.policies.FXChangeViewportPolicy;
import org.eclipse.gef.mvc.fx.viewer.FXViewer;

import javafx.geometry.Bounds;

public class ContentRestrictedChangeViewportPolicy extends FXChangeViewportPolicy {

	@Override
	protected void doLocallyExecuteOperation() {
		// determine current translation
		double tx = getChangeViewportOperation().getNewHorizontalScrollOffset();
		double ty = getChangeViewportOperation().getNewVerticalScrollOffset();

		// determine direction of change
		double dx = tx - getChangeViewportOperation().getInitialHorizontalScrollOffset();
		double dy = ty - getChangeViewportOperation().getInitialVerticalScrollOffset();

		// determine scrollable bounds
		FXViewer viewer = (FXViewer) getHost().getRoot().getViewer();
		InfiniteCanvas canvas = viewer.getCanvas();
		Bounds scrollableBounds = canvas.getScrollableBounds();

		// compute content restricted translation range
		// XXX: Scrolling is implemented by translating the scene graph node
		// that holds all children. Therefore, scrolling to the right is
		// actually moving this node to the left, and vice versa.
		double contentRestrictedMinTranslateX = -scrollableBounds.getMinX();
		double contentRestrictedMinTranslateY = -scrollableBounds.getMinY();
		// XXX: The size of the canvas is the visible area which has to be
		// subtracted from the scrollable bounds to yield the translation range.
		double contentRestrictedMaxTranslateX = -(scrollableBounds.getMaxX() - canvas.getWidth());
		double contentRestrictedMaxTranslateY = -(scrollableBounds.getMaxY() - canvas.getHeight());

		// sort content restricted bounds numerically
		if (contentRestrictedMinTranslateX > contentRestrictedMaxTranslateX) {
			double tmp = contentRestrictedMinTranslateX;
			contentRestrictedMinTranslateX = contentRestrictedMaxTranslateX;
			contentRestrictedMaxTranslateX = tmp;
		}
		if (contentRestrictedMinTranslateY > contentRestrictedMaxTranslateY) {
			double tmp = contentRestrictedMinTranslateY;
			contentRestrictedMinTranslateY = contentRestrictedMaxTranslateY;
			contentRestrictedMaxTranslateY = tmp;
		}

		// do not allow translation when the corresponding scrollbar is
		// invisible, and otherwise restrict the translation values to the
		// scrollable bounds
		if (!canvas.getHorizontalScrollBar().isVisible()) {
			tx = getChangeViewportOperation().getInitialHorizontalScrollOffset();
		} else if (tx < contentRestrictedMinTranslateX || tx > contentRestrictedMaxTranslateX) {
			// restrict to minimum/maximum depending on direction
			if (dx < 0) {
				tx = contentRestrictedMinTranslateX;
			} else {
				tx = contentRestrictedMaxTranslateX;
			}
		}
		if (!canvas.getVerticalScrollBar().isVisible()) {
			ty = getChangeViewportOperation().getInitialVerticalScrollOffset();
		} else if (ty < contentRestrictedMinTranslateY || ty > contentRestrictedMaxTranslateY) {
			// restrict to minimum/maximum depending on direction
			if (dy < 0) {
				ty = contentRestrictedMinTranslateY;
			} else {
				ty = contentRestrictedMaxTranslateY;
			}
		}

		// change operation values
		getChangeViewportOperation().setNewHorizontalScrollOffset(tx);
		getChangeViewportOperation().setNewVerticalScrollOffset(ty);

		// execute
		super.locallyExecuteOperation();
	}

}
