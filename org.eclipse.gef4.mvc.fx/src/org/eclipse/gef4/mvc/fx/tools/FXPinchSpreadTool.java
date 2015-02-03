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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javafx.event.EventTarget;
import javafx.scene.Node;
import javafx.scene.input.ZoomEvent;

import org.eclipse.gef4.fx.gestures.FXPinchSpreadGesture;
import org.eclipse.gef4.mvc.fx.parts.FXPartUtils;
import org.eclipse.gef4.mvc.fx.policies.AbstractFXPinchSpreadPolicy;
import org.eclipse.gef4.mvc.fx.viewer.FXViewer;
import org.eclipse.gef4.mvc.parts.IVisualPart;
import org.eclipse.gef4.mvc.tools.AbstractTool;
import org.eclipse.gef4.mvc.viewer.IViewer;

public class FXPinchSpreadTool extends AbstractTool<Node> {

	public static final Class<AbstractFXPinchSpreadPolicy> TOOL_POLICY_KEY = AbstractFXPinchSpreadPolicy.class;

	private final Map<IViewer<Node>, FXPinchSpreadGesture> gestures = new HashMap<IViewer<Node>, FXPinchSpreadGesture>();

	public FXPinchSpreadTool() {
	}

	protected Set<? extends AbstractFXPinchSpreadPolicy> getPinchSpreadPolicies(
			IVisualPart<Node, ? extends Node> targetPart) {
		return new HashSet<>(targetPart
				.<AbstractFXPinchSpreadPolicy> getAdapters(TOOL_POLICY_KEY)
				.values());
	}

	protected Set<? extends AbstractFXPinchSpreadPolicy> getTargetPolicies(
			IViewer<Node> viewer, ZoomEvent e) {
		EventTarget target = e.getTarget();
		if (!(target instanceof Node)) {
			return null;
		}

		Node targetNode = (Node) target;
		IVisualPart<Node, ? extends Node> targetPart = FXPartUtils
				.getTargetPart(Collections.singleton(viewer), targetNode,
						TOOL_POLICY_KEY);

		// send event to root part if no target part can be found
		if (targetPart == null) {
			targetPart = viewer.getRootPart();
		}

		return getPinchSpreadPolicies(targetPart);
	}

	@Override
	protected void registerListeners() {
		super.registerListeners();
		for (final IViewer<Node> viewer : getDomain().getViewers().values()) {
			FXPinchSpreadGesture gesture = new FXPinchSpreadGesture() {
				@Override
				protected void zoomDetected(ZoomEvent e) {
					for (AbstractFXPinchSpreadPolicy policy : getTargetPolicies(
							viewer, e)) {
						policy.zoomDetected(e);
					}
				}

				@Override
				protected void zoomed(ZoomEvent e) {
					for (AbstractFXPinchSpreadPolicy policy : getTargetPolicies(
							viewer, e)) {
						policy.zoomed(e);
					}
				}

				@Override
				protected void zoomFinished(ZoomEvent e) {
					for (AbstractFXPinchSpreadPolicy policy : getTargetPolicies(
							viewer, e)) {
						policy.zoomFinished(e);
					}
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
