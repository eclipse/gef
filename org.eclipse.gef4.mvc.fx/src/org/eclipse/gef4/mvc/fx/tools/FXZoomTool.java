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

import java.util.List;

import javafx.scene.Node;
import javafx.scene.input.ZoomEvent;

import org.eclipse.gef4.fx.gestures.FXPinchGesture;
import org.eclipse.gef4.mvc.fx.parts.FXPartUtils;
import org.eclipse.gef4.mvc.fx.policies.AbstractFXZoomPolicy;
import org.eclipse.gef4.mvc.fx.viewer.IFXViewer;
import org.eclipse.gef4.mvc.parts.IVisualPart;
import org.eclipse.gef4.mvc.tools.AbstractTool;

public class FXZoomTool extends AbstractTool<Node> {

	public static final Class<AbstractFXZoomPolicy> TOOL_POLICY_KEY = AbstractFXZoomPolicy.class;

	private final FXPinchGesture gesture = new FXPinchGesture() {
		@Override
		protected void zoomDetected(ZoomEvent e) {

			FXZoomTool.this.zoomDetected(FXPartUtils.getTargetParts(getDomain()
					.getViewer(), e, TOOL_POLICY_KEY), e, e.getZoomFactor(), e
					.getTotalZoomFactor());
		}

		@Override
		protected void zoomed(ZoomEvent e) {
			FXZoomTool.this.zoomed(FXPartUtils.getTargetParts(getDomain()
					.getViewer(), e, TOOL_POLICY_KEY), e, e.getZoomFactor(), e
					.getTotalZoomFactor());
		}

		@Override
		protected void zoomFinished(ZoomEvent e) {
			FXZoomTool.this.zoomFinished(FXPartUtils.getTargetParts(getDomain()
					.getViewer(), e, TOOL_POLICY_KEY), e, e.getZoomFactor(), e
					.getTotalZoomFactor());
		}
	};

	public FXZoomTool() {
	}

	protected AbstractFXZoomPolicy getToolPolicy(IVisualPart<Node> targetPart) {
		return targetPart.getAdapter(TOOL_POLICY_KEY);
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

	/**
	 * Reaction to the detection of pinch (close fingers) gestures.
	 */
	protected void zoomDetected(List<IVisualPart<Node>> targetParts,
			ZoomEvent e, double partialFactor, double totalFactor) {
		for (IVisualPart<Node> targetPart : targetParts) {
			AbstractFXZoomPolicy policy = getToolPolicy(targetPart);
			if (policy != null) {
				policy.zoomDetected(e, partialFactor);
			}
		}
	}

	/**
	 * Continuous reaction to pinch (close fingers) gestures. Called
	 * continuously on finger movement, after the gesture has been detected, and
	 * before it has been finished.
	 */
	protected void zoomed(List<IVisualPart<Node>> targetParts, ZoomEvent e,
			double partialFactor, double totalFactor) {
		for (IVisualPart<Node> targetPart : targetParts) {
			AbstractFXZoomPolicy policy = getToolPolicy(targetPart);
			if (policy != null) {
				policy.zoomed(e, partialFactor, totalFactor);
			}
		}
	}

	/**
	 * Reaction to the finish of pinch (close fingers) gestures.
	 */
	protected void zoomFinished(List<IVisualPart<Node>> targetParts,
			ZoomEvent e, double partialFactor, double totalFactor) {
		for (IVisualPart<Node> targetPart : targetParts) {
			AbstractFXZoomPolicy policy = getToolPolicy(targetPart);
			if (policy != null) {
				policy.zoomFinished(e, totalFactor);
			}
		}
	}

}
