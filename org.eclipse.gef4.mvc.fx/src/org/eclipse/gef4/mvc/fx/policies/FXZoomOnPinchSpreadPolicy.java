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

import javafx.scene.input.ZoomEvent;

import org.eclipse.gef4.geometry.planar.AffineTransform;
import org.eclipse.gef4.mvc.fx.operations.FXChangeViewportOperation;
import org.eclipse.gef4.mvc.models.ViewportModel;

public class FXZoomOnPinchSpreadPolicy extends AbstractFXPinchSpreadPolicy {

	private AffineTransform initialContentsTx;

	public void zoom(double zoom) {
		ViewportModel viewportModel = getHost().getRoot().getViewer()
				.getAdapter(ViewportModel.class);
		// execute zoom as viewport change operation
		FXChangeViewportOperation zoomOperation = new FXChangeViewportOperation(
				viewportModel,
				initialContentsTx.concatenate(new AffineTransform().setToScale(
						zoom, zoom)));
		getHost().getRoot().getViewer().getDomain().execute(zoomOperation);
	}

	@Override
	public void zoomDetected(ZoomEvent e, double partialFactor,
			double totalFactor) {
		initialContentsTx = getHost().getRoot().getViewer()
				.getAdapter(ViewportModel.class).getContentsTransform();
		zoom(totalFactor);
	}

	@Override
	public void zoomed(ZoomEvent e, double partialFactor, double totalFactor) {
		zoom(totalFactor);
	}

	@Override
	public void zoomFinished(ZoomEvent e, double partialFactor,
			double totalFactor) {
		zoom(totalFactor);
	}

}
