/*******************************************************************************
 * Copyright (c) 2015 itemis AG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Alexander Nyßen (itemis AG) - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.gef4.mvc.fx.tools;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.gef4.fx.gestures.AbstractFXRotateGesture;
import org.eclipse.gef4.mvc.fx.parts.FXPartUtils;
import org.eclipse.gef4.mvc.fx.policies.AbstractFXOnRotatePolicy;
import org.eclipse.gef4.mvc.fx.viewer.FXViewer;
import org.eclipse.gef4.mvc.parts.IVisualPart;
import org.eclipse.gef4.mvc.tools.AbstractTool;
import org.eclipse.gef4.mvc.viewer.IViewer;

import javafx.event.EventTarget;
import javafx.scene.Node;
import javafx.scene.input.RotateEvent;

public class FXRotateTool extends AbstractTool<Node> {

	// TODO: Rename to ON_ROTATE_POLICY_KEY
	public static final Class<AbstractFXOnRotatePolicy> TOOL_POLICY_KEY = AbstractFXOnRotatePolicy.class;

	private final Map<IViewer<Node>, AbstractFXRotateGesture> gestures = new HashMap<IViewer<Node>, AbstractFXRotateGesture>();

	public FXRotateTool() {
	}

	// TODO: Rename to getOnRotatePolicies()
	protected Set<? extends AbstractFXOnRotatePolicy> getRotatePolicies(
			IVisualPart<Node, ? extends Node> targetPart) {
		return new HashSet<>(targetPart
				.<AbstractFXOnRotatePolicy> getAdapters(TOOL_POLICY_KEY)
				.values());
	}

	protected Set<? extends AbstractFXOnRotatePolicy> getTargetPolicies(
			IViewer<Node> viewer, RotateEvent e) {
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

		return getRotatePolicies(targetPart);
	}

	@Override
	protected void registerListeners() {
		super.registerListeners();
		for (final IViewer<Node> viewer : getDomain().getViewers().values()) {
			AbstractFXRotateGesture gesture = new AbstractFXRotateGesture() {

				@Override
				protected void rotate(RotateEvent event) {
					for (AbstractFXOnRotatePolicy policy : getTargetPolicies(
							viewer, event)) {
						policy.rotate(event);
					}
				}

				@Override
				protected void rotationFinished(RotateEvent event) {
					for (AbstractFXOnRotatePolicy policy : getTargetPolicies(
							viewer, event)) {
						policy.rotationFinished(event);
					}
					getDomain().closeExecutionTransaction(FXRotateTool.this);
				}

				@Override
				protected void rotationStarted(RotateEvent event) {
					getDomain().openExecutionTransaction(FXRotateTool.this);
					for (AbstractFXOnRotatePolicy policy : getTargetPolicies(
							viewer, event)) {
						policy.rotationStarted(event);
					}
				}
			};
			gesture.setScene(((FXViewer) viewer).getScene());
			gestures.put(viewer, gesture);
		}

	}

	@Override
	protected void unregisterListeners() {
		for (AbstractFXRotateGesture gesture : gestures.values()) {
			gesture.setScene(null);
		}
		super.unregisterListeners();
	}

}
