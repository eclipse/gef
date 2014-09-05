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

import javafx.scene.Node;
import javafx.scene.input.ZoomEvent;

import org.eclipse.gef4.mvc.models.ZoomModel;
import org.eclipse.gef4.mvc.policies.ZoomPolicy;

public class FXZoomOnPinchSpreadPolicy extends AbstractFXPinchSpreadPolicy {

	private double initialZoomFactor;

	@SuppressWarnings("unchecked")
	private ZoomPolicy<Node> getZoomPolicy() {
		return getHost().getAdapter(ZoomPolicy.class);
	}

	@Override
	public void zoomDetected(ZoomEvent e, double partialFactor,
			double totalFactor) {
		initialZoomFactor = getHost().getRoot().getViewer()
				.getAdapter(ZoomModel.class).getZoomFactor();
		ZoomPolicy<Node> policy = getZoomPolicy();
		if (policy != null) {
			policy.zoomAbsolute(initialZoomFactor * totalFactor);
		}
	}

	@Override
	public void zoomed(ZoomEvent e, double partialFactor, double totalFactor) {
		ZoomPolicy<Node> policy = getZoomPolicy();
		if (policy != null) {
			policy.zoomAbsolute(initialZoomFactor * totalFactor);
		}
	}

	@Override
	public void zoomFinished(ZoomEvent e, double partialFactor,
			double totalFactor) {
		ZoomPolicy<Node> policy = getZoomPolicy();
		if (policy != null) {
			policy.zoomAbsolute(initialZoomFactor * totalFactor);
		}
	}

}
