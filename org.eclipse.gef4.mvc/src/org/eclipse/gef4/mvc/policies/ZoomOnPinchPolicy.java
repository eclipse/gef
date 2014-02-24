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
package org.eclipse.gef4.mvc.policies;

import org.eclipse.gef4.mvc.models.IZoomModel;
import org.eclipse.gef4.mvc.viewer.IVisualViewer;

/**
 * {@link IPinchPolicy} implementation for adjusting the {@link IVisualViewer}'s
 * {@link IZoomModel} corresponding to the pinch/spread touch gesture.
 * 
 * @author mwienand
 * 
 */
public class ZoomOnPinchPolicy<V> extends AbstractPolicy<V> implements
		IPinchPolicy<V> {

	private double initialZoomFactor;
	private IZoomModel zoomModel;

	@Override
	public void zoomDetected(double partialFactor) {
		zoomModel = getHost().getRoot().getViewer().getZoomModel();
		initialZoomFactor = zoomModel.getZoomFactor();
		zoomModel.setZoomFactor(initialZoomFactor * partialFactor);
	}

	@Override
	public void zoomed(double partialFactor, double totalFactor) {
		zoomModel.setZoomFactor(initialZoomFactor * totalFactor);
	}

	@Override
	public void zoomFinished(double totalFactor) {
		zoomModel.setZoomFactor(initialZoomFactor * totalFactor);
	}

}
