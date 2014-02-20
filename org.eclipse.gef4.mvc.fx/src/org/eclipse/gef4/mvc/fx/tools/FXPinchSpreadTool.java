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

import java.util.Collections;
import java.util.List;

import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.input.ZoomEvent;

import org.eclipse.gef4.fx.gestures.FXPinchSpreadGesture;
import org.eclipse.gef4.mvc.fx.parts.FXPartUtils;
import org.eclipse.gef4.mvc.fx.viewer.FXViewer;
import org.eclipse.gef4.mvc.parts.IVisualPart;
import org.eclipse.gef4.mvc.policies.IPolicy;
import org.eclipse.gef4.mvc.tools.AbstractPinchSpreadTool;

public class FXPinchSpreadTool extends AbstractPinchSpreadTool<Node> {

	public FXPinchSpreadTool() {
	}

	private Scene scene;
	private FXPinchSpreadGesture gesture = new FXPinchSpreadGesture() {
		@Override
		protected void spreadFinished(ZoomEvent e, double partialFactor,
				double totalFactor) {
			FXPinchSpreadTool.this.spreadFinished(findTargetParts(e),
					partialFactor, totalFactor);
		}

		@Override
		protected void spreadDetected(ZoomEvent e, double partialFactor,
				double totalFactor) {
			FXPinchSpreadTool.this.spreadDetected(findTargetParts(e),
					partialFactor, totalFactor);
		}

		@SuppressWarnings({ "unchecked" })
		private List<IVisualPart<Node>> findTargetParts(ZoomEvent e) {
			IVisualPart<Node> targetPart = FXPartUtils.getEventTargetPart(
					getDomain().getViewer(), e,
					(Class<IPolicy<Node>>) TOOL_POLICY_KEY);
			if (targetPart == null) {
				return Collections.emptyList();
			}
			return Collections.singletonList(targetPart);
		}

		@Override
		protected void spread(ZoomEvent e, double partialFactor,
				double totalFactor) {
			FXPinchSpreadTool.this.spread(findTargetParts(e), partialFactor,
					totalFactor);
		}

		@Override
		protected void pinchFinished(ZoomEvent e, double partialFactor,
				double totalFactor) {
			FXPinchSpreadTool.this.pinchFinished(findTargetParts(e),
					partialFactor, totalFactor);
		}

		@Override
		protected void pinchDetected(ZoomEvent e, double partialFactor,
				double totalFactor) {
			FXPinchSpreadTool.this.pinchDetected(findTargetParts(e),
					partialFactor, totalFactor);
		}

		@Override
		protected void pinch(ZoomEvent e, double partialFactor,
				double totalFactor) {
			FXPinchSpreadTool.this.pinch(findTargetParts(e), partialFactor,
					totalFactor);
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
