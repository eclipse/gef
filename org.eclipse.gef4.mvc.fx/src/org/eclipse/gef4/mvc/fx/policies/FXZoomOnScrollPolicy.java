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

import org.eclipse.gef4.geometry.planar.AffineTransform;
import org.eclipse.gef4.mvc.models.ViewportModel;

public class FXZoomOnScrollPolicy extends AbstractFXScrollPolicy {

	protected boolean isZoom(ScrollEvent event) {
		return event.isControlDown() || event.isAltDown();
	}

	@Override
	public void scroll(ScrollEvent event) {
		if (isZoom(event)) {
			zoomRelative(event.getDeltaY() > 0 ? 1.25 : 0.8, event.getSceneX(),
					event.getSceneY());
		}
	}

	public void zoomRelative(double relativeZoom, double pivotX, double pivotY) {
		ViewportModel viewportModel = getHost().getRoot().getViewer()
				.getAdapter(ViewportModel.class);
		// TODO: pivot
		viewportModel
				.setContentsTransform(viewportModel.getContentsTransform()
						.concatenate(
								new AffineTransform().scale(relativeZoom,
										relativeZoom)));
	}

}
