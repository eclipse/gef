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

import org.eclipse.gef4.mvc.models.IZoomModel;

public class FXZoomOnZoomPolicy extends AbstractFXZoomPolicy {

	private double initialZoomFactor;
	private IZoomModel zoomModel;

	@Override
	public void zoomDetected(ZoomEvent e, double partialFactor) {
		zoomModel = getHost().getRoot().getViewer().getZoomModel();
		initialZoomFactor = zoomModel.getZoomFactor();
		zoomModel.setZoomFactor(initialZoomFactor * partialFactor);
	}

	@Override
	public void zoomed(ZoomEvent e, double partialFactor, double totalFactor) {
		zoomModel.setZoomFactor(initialZoomFactor * totalFactor);
	}

	@Override
	public void zoomFinished(ZoomEvent e, double totalFactor) {
		zoomModel.setZoomFactor(initialZoomFactor * totalFactor);
	}

}
