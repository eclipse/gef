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

import javafx.geometry.Point2D;
import javafx.scene.input.ScrollEvent;

import org.eclipse.gef4.geometry.planar.AffineTransform;
import org.eclipse.gef4.mvc.fx.operations.FXChangeViewportOperation;
import org.eclipse.gef4.mvc.fx.viewer.FXViewer;
import org.eclipse.gef4.mvc.models.ViewportModel;

public class FXZoomOnScrollPolicy extends AbstractFXScrollPolicy {

	protected boolean isZoom(ScrollEvent event) {
		return event.isControlDown() || event.isAltDown();
	}

	@Override
	public void scroll(ScrollEvent event) {
		if (isZoom(event)) {
			zoomRelative(event.getDeltaY() > 0 ? 1.01 : 1 / 1.01,
					event.getSceneX(), event.getSceneY());
		}
	}

	public void zoomRelative(double relativeZoom, double sceneX, double sceneY) {
		ViewportModel viewportModel = getHost().getRoot().getViewer()
				.getAdapter(ViewportModel.class);
		// compute transformation
		Point2D contentGroupPivot = ((FXViewer) getHost().getRoot().getViewer())
				.getScrollPane().getContentGroup().sceneToLocal(sceneX, sceneY);
		AffineTransform newTransform = viewportModel.getContentsTransform()
				.concatenate(
						new AffineTransform()
								.translate(contentGroupPivot.getX(),
										contentGroupPivot.getY())
								.scale(relativeZoom, relativeZoom)
								.translate(-contentGroupPivot.getX(),
										-contentGroupPivot.getY()));
		// execute viewport change as operation
		FXChangeViewportOperation zoomOperation = new FXChangeViewportOperation(
				viewportModel, newTransform);
		getHost().getRoot().getViewer().getDomain().execute(zoomOperation);
	}

}
