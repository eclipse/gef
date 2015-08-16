/*******************************************************************************
 * Copyright (c) 2014, 2015 itemis AG and others.
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

import org.eclipse.gef4.fx.gestures.AbstractFXPinchSpreadGesture;
import org.eclipse.gef4.mvc.fx.parts.FXPartUtils;
import org.eclipse.gef4.mvc.fx.policies.AbstractFXOnPinchSpreadPolicy;
import org.eclipse.gef4.mvc.fx.viewer.FXViewer;
import org.eclipse.gef4.mvc.parts.IVisualPart;
import org.eclipse.gef4.mvc.tools.AbstractTool;
import org.eclipse.gef4.mvc.viewer.IViewer;

import javafx.event.EventTarget;
import javafx.scene.Node;
import javafx.scene.input.ZoomEvent;

public class FXPinchSpreadTool extends AbstractTool<Node> {

	public static final Class<AbstractFXOnPinchSpreadPolicy> TOOL_POLICY_KEY = AbstractFXOnPinchSpreadPolicy.class;

	private final Map<IViewer<Node>, AbstractFXPinchSpreadGesture> gestures = new HashMap<IViewer<Node>, AbstractFXPinchSpreadGesture>();

	public FXPinchSpreadTool() {
	}

	protected Set<? extends AbstractFXOnPinchSpreadPolicy> getPinchSpreadPolicies(
			IVisualPart<Node, ? extends Node> targetPart) {
		return new HashSet<>(targetPart
				.<AbstractFXOnPinchSpreadPolicy> getAdapters(TOOL_POLICY_KEY)
				.values());
	}

	protected Set<? extends AbstractFXOnPinchSpreadPolicy> getTargetPolicies(
			IViewer<Node> viewer, ZoomEvent e) {
		EventTarget target = e.getTarget();
		if (!(target instanceof Node)) {
			return null;
		}

		Node targetNode = (Node) target;
		IVisualPart<Node, ? extends Node> targetPart = FXPartUtils
				.getTargetPart(Collections.singleton(viewer), targetNode,
						TOOL_POLICY_KEY, true);

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
			AbstractFXPinchSpreadGesture gesture = new AbstractFXPinchSpreadGesture() {
				@Override
				protected void zoom(ZoomEvent e) {
					System.out.println("ZOOM");
					// the start event might get lost, so we should open a
					// transaction if one is not already open
					for (AbstractFXOnPinchSpreadPolicy policy : getTargetPolicies(
							viewer, e)) {
						policy.zoom(e);
					}
				}

				@Override
				protected void zoomFinished(ZoomEvent e) {
					System.out.println("ZOOM FINISH");
					for (AbstractFXOnPinchSpreadPolicy policy : getTargetPolicies(
							viewer, e)) {
						policy.zoomFinished(e);
					}
					getDomain()
							.closeExecutionTransaction(FXPinchSpreadTool.this);
				}

				@Override
				protected void zoomStarted(ZoomEvent e) {
					System.out.println("ZOOM START");
					// zoom finish may not occur, so close any preceding
					// transaction just in case

					if (!getDomain().isExecutionTransactionOpen(
							FXPinchSpreadTool.this)) {
						// finish event may not properly occur in all cases; we
						// may continue to use the still open transaction
						getDomain().openExecutionTransaction(
								FXPinchSpreadTool.this);
					}
					for (AbstractFXOnPinchSpreadPolicy policy : getTargetPolicies(
							viewer, e)) {
						policy.zoomStarted(e);
					}
				}
			};
			gesture.setScene(((FXViewer) viewer).getScene());
			gestures.put(viewer, gesture);
		}

	}

	@Override
	protected void unregisterListeners() {
		for (AbstractFXPinchSpreadGesture gesture : gestures.values()) {
			gesture.setScene(null);
		}
		super.unregisterListeners();
	}

}
