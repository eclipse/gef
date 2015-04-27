/*******************************************************************************
 * Copyright (c) 2014 itemis AG and others.
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

import javafx.scene.input.ScrollEvent;

public class FXZoomOnScrollPolicy extends AbstractFXScrollPolicy {

	private FXChangeViewportPolicy getViewportPolicy() {
		return getHost().getRoot().getAdapter(FXChangeViewportPolicy.class);
	}

	protected boolean isZoom(ScrollEvent event) {
		return event.isControlDown() || event.isAltDown();
	}

	@Override
	public void scroll(ScrollEvent event) {
		if (isZoom(event)) {
			zoomRelative(event.getDeltaY() > 0 ? 1.05 : 1 / 1.05,
					event.getSceneX(), event.getSceneY());
		}
	}

	public void zoomRelative(double relativeZoom, double sceneX, double sceneY) {
		FXChangeViewportPolicy viewportPolicy = getViewportPolicy();
		viewportPolicy.init();
		viewportPolicy.zoomRelative(relativeZoom, sceneX, sceneY);
		// TODO: does it make sense to make this undoable?
		viewportPolicy.commit();
	}

}
