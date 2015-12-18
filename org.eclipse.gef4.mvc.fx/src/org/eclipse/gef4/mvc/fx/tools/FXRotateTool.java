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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.gef4.fx.gestures.AbstractRotateGesture;
import org.eclipse.gef4.mvc.fx.domain.FXDomain;
import org.eclipse.gef4.mvc.fx.policies.AbstractFXOnRotatePolicy;
import org.eclipse.gef4.mvc.fx.viewer.FXViewer;
import org.eclipse.gef4.mvc.viewer.IViewer;

import javafx.event.EventTarget;
import javafx.scene.Node;
import javafx.scene.input.RotateEvent;

/**
 * The {@link FXRotateTool} is an {@link AbstractFXTool} to handle rotate
 * interaction gestures.
 * <p>
 * The {@link FXRotateTool} handles the opening and closing of an transaction
 * operation via the {@link FXDomain}, to which it is adapted. It controls that
 * a single transaction operation is used for the complete interaction , so all
 * interaction results can be undone in a single undo step.
 *
 * @author anyssen
 *
 */
public class FXRotateTool extends AbstractFXTool {

	/**
	 * The type of the policy that has to be supported by target parts.
	 */
	// TODO: Rename to ON_ROTATE_POLICY_KEY
	public static final Class<AbstractFXOnRotatePolicy> TOOL_POLICY_KEY = AbstractFXOnRotatePolicy.class;

	private final Map<IViewer<Node>, AbstractRotateGesture> gestures = new HashMap<>();

	@Override
	protected void registerListeners() {
		super.registerListeners();
		for (final IViewer<Node> viewer : getDomain().getViewers().values()) {
			AbstractRotateGesture gesture = new AbstractRotateGesture() {

				private List<? extends AbstractFXOnRotatePolicy> policies;

				@Override
				protected void rotate(RotateEvent event) {
					for (AbstractFXOnRotatePolicy policy : policies) {
						policy.rotate(event);
					}
				}

				@Override
				protected void rotationFinished(RotateEvent event) {
					for (AbstractFXOnRotatePolicy policy : policies) {
						policy.rotationFinished(event);
					}
					getDomain().closeExecutionTransaction(FXRotateTool.this);
				}

				@Override
				protected void rotationStarted(RotateEvent event) {
					EventTarget eventTarget = event.getTarget();
					policies = getTargetPolicies(
							viewer, eventTarget instanceof Node
									? (Node) eventTarget : null,
							TOOL_POLICY_KEY);
					getDomain().openExecutionTransaction(FXRotateTool.this);
					for (AbstractFXOnRotatePolicy policy : policies) {
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
		for (AbstractRotateGesture gesture : gestures.values()) {
			gesture.setScene(null);
		}
		super.unregisterListeners();
	}

}
