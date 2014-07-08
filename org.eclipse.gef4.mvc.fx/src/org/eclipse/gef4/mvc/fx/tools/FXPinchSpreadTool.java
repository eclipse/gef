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

import java.util.HashMap;
import java.util.Map;

import javafx.event.EventTarget;
import javafx.scene.Node;
import javafx.scene.input.ZoomEvent;

import org.eclipse.gef4.fx.gestures.FXPinchSpreadGesture;
import org.eclipse.gef4.mvc.fx.parts.FXPartUtils;
import org.eclipse.gef4.mvc.fx.policies.AbstractFXZoomPolicy;
import org.eclipse.gef4.mvc.fx.viewer.FXViewer;
import org.eclipse.gef4.mvc.parts.IVisualPart;
import org.eclipse.gef4.mvc.tools.AbstractTool;
import org.eclipse.gef4.mvc.viewer.IViewer;

public class FXPinchSpreadTool extends AbstractTool<Node> {

	public static final Class<AbstractFXZoomPolicy> TOOL_POLICY_KEY = AbstractFXZoomPolicy.class;

	private final Map<IViewer<Node>, FXPinchSpreadGesture> gestures = new HashMap<IViewer<Node>, FXPinchSpreadGesture>();

	public FXPinchSpreadTool() {
	}

	protected AbstractFXZoomPolicy getToolPolicy(IVisualPart<Node> targetPart) {
		return targetPart.getAdapter(TOOL_POLICY_KEY);
	}

	@Override
	protected void registerListeners() {
		super.registerListeners();

		for (IViewer<Node> viewer : getDomain().getViewers()) {
			FXPinchSpreadGesture gesture = new FXPinchSpreadGesture() {
				protected AbstractFXZoomPolicy getPolicy(ZoomEvent e) {
					EventTarget target = e.getTarget();
					if (!(target instanceof Node)) {
						return null;
					}

					Node targetNode = (Node) target;
					IVisualPart<Node> targetPart = FXPartUtils.getTargetPart(
							getDomain().getViewers(), targetNode,
							TOOL_POLICY_KEY);
					if (targetPart == null) {
						return null;
					}

					AbstractFXZoomPolicy policy = getToolPolicy(targetPart);
					if (policy == null) {
						throw new IllegalStateException(
								"Target part does not support required policy!");
					}

					return policy;
				}

				@Override
				protected void zoomDetected(ZoomEvent e) {
					AbstractFXZoomPolicy policy = getPolicy(e);
					policy.zoomDetected(e, e.getZoomFactor(),
							e.getTotalZoomFactor());
				}

				@Override
				protected void zoomed(ZoomEvent e) {
					AbstractFXZoomPolicy policy = getPolicy(e);
					policy.zoomed(e, e.getZoomFactor(), e.getTotalZoomFactor());
				}

				@Override
				protected void zoomFinished(ZoomEvent e) {
					AbstractFXZoomPolicy policy = getPolicy(e);
					policy.zoomFinished(e, e.getZoomFactor(),
							e.getTotalZoomFactor());
				}
			};
			gesture.setScene(((FXViewer) viewer).getScene());
			gestures.put(viewer, gesture);
		}

	}

	@Override
	protected void unregisterListeners() {
		for (FXPinchSpreadGesture gesture : gestures.values()) {
			gesture.setScene(null);
		}
		super.unregisterListeners();
	}

}
