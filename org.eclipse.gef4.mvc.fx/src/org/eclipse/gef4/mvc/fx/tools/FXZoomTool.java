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
package org.eclipse.gef4.mvc.fx.tools;

import javafx.scene.Node;
import javafx.scene.input.ZoomEvent;

import org.eclipse.gef4.fx.gestures.FXPinchGesture;
import org.eclipse.gef4.mvc.fx.parts.FXPartUtils;
import org.eclipse.gef4.mvc.fx.viewer.IFXViewer;
import org.eclipse.gef4.mvc.policies.IPolicy;
import org.eclipse.gef4.mvc.tools.AbstractZoomTool;

public class FXZoomTool extends AbstractZoomTool<Node> {

	@SuppressWarnings("unchecked")
	private final FXPinchGesture gesture = new FXPinchGesture() {
		@Override
		protected void zoomDetected(ZoomEvent e) {
			FXZoomTool.this.zoomDetected(FXPartUtils.getTargetParts(getDomain()
					.getViewer(), e, (Class<IPolicy<Node>>) TOOL_POLICY_KEY), e
					.getZoomFactor(), e.getTotalZoomFactor());
		}

		@Override
		protected void zoomed(ZoomEvent e) {
			FXZoomTool.this.zoomed(FXPartUtils.getTargetParts(getDomain()
					.getViewer(), e, (Class<IPolicy<Node>>) TOOL_POLICY_KEY), e
					.getZoomFactor(), e.getTotalZoomFactor());
		}

		@Override
		protected void zoomFinished(ZoomEvent e) {
			FXZoomTool.this.zoomFinished(FXPartUtils.getTargetParts(getDomain()
					.getViewer(), e, (Class<IPolicy<Node>>) TOOL_POLICY_KEY), e
					.getZoomFactor(), e.getTotalZoomFactor());
		}
	};

	public FXZoomTool() {
	}

	@Override
	protected void registerListeners() {
		super.registerListeners();
		gesture.setScene(((IFXViewer) getDomain().getViewer()).getScene());
	}

	@Override
	protected void unregisterListeners() {
		gesture.setScene(null);
		super.unregisterListeners();
	}

}
