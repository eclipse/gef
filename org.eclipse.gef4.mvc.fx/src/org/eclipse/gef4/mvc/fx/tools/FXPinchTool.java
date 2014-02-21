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
import javafx.scene.Scene;
import javafx.scene.input.ZoomEvent;

import org.eclipse.gef4.fx.gestures.FXPinchGesture;
import org.eclipse.gef4.mvc.fx.parts.FXPartUtils;
import org.eclipse.gef4.mvc.fx.viewer.FXViewer;
import org.eclipse.gef4.mvc.policies.IPolicy;
import org.eclipse.gef4.mvc.tools.AbstractPinchTool;

public class FXPinchTool extends AbstractPinchTool<Node> {

	public FXPinchTool() {
	}

	private Scene scene;

	@SuppressWarnings("unchecked")
	private FXPinchGesture gesture = new FXPinchGesture() {
		@Override
		protected void zoomFinished(ZoomEvent e) {
			FXPinchTool.this.zoomFinished(FXPartUtils.getTargetParts(
					getDomain().getViewer(), e,
					(Class<IPolicy<Node>>) TOOL_POLICY_KEY), e.getZoomFactor(),
					e.getTotalZoomFactor());
		}

		@Override
		protected void zoomDetected(ZoomEvent e) {
			FXPinchTool.this.zoomDetected(FXPartUtils.getTargetParts(
					getDomain().getViewer(), e,
					(Class<IPolicy<Node>>) TOOL_POLICY_KEY), e.getZoomFactor(),
					e.getTotalZoomFactor());
		}

		@Override
		protected void zoomed(ZoomEvent e) {
			FXPinchTool.this.zoomed(FXPartUtils.getTargetParts(
					getDomain().getViewer(), e,
					(Class<IPolicy<Node>>) TOOL_POLICY_KEY), e.getZoomFactor(),
					e.getTotalZoomFactor());
		}
	};

	protected void registerListeners() {
		super.registerListeners();
		scene = ((FXViewer) getDomain().getViewer()).getCanvas().getScene();
	}

	@Override
	public void activate() {
		super.activate();
		if (scene != null) {
			gesture.setScene(scene);
		}
	}

	@Override
	public void deactivate() {
		if (scene != null) {
			gesture.setScene(null);
		}
		super.deactivate();
	}

	@Override
	protected void unregisterListeners() {
		gesture.setScene(null);
		super.unregisterListeners();
	}

}
