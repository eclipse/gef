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

import org.eclipse.gef4.fx.gestures.AbstractRotateGesture;
import org.eclipse.gef4.mvc.fx.domain.FXDomain;
import org.eclipse.gef4.mvc.fx.parts.FXPartUtils;
import org.eclipse.gef4.mvc.fx.policies.AbstractFXOnRotatePolicy;
import org.eclipse.gef4.mvc.fx.viewer.FXViewer;
import org.eclipse.gef4.mvc.parts.IVisualPart;
import org.eclipse.gef4.mvc.tools.AbstractTool;
import org.eclipse.gef4.mvc.tools.ITool;
import org.eclipse.gef4.mvc.viewer.IViewer;

import javafx.event.EventTarget;
import javafx.scene.Node;
import javafx.scene.input.RotateEvent;

/**
 * An {@link ITool} to handle rotate interaction gestures.
 * <p>
 * During each rotate interaction, the tool identifies an {@link IVisualPart}
 * that serves as interaction target. It is identified via hit-testing on the
 * visuals and the availability of a corresponding
 * {@link AbstractFXOnRotatePolicy} (see {@link #getTargetPart(IViewer, Node)}).
 * <p>
 * The {@link FXRotateTool} handles the opening and closing of an transaction
 * operation via the {@link FXDomain}, to which it is adapted. It controls that
 * a single transaction operation is used for the complete interaction , so all
 * interaction results can be undone in a single undo step.
 *
 * @author anyssen
 *
 */
public class FXRotateTool extends AbstractTool<Node> {

	/**
	 * The type of the policy that has to be supported by target parts.
	 */
	// TODO: Rename to ON_ROTATE_POLICY_KEY
	public static final Class<AbstractFXOnRotatePolicy> TOOL_POLICY_KEY = AbstractFXOnRotatePolicy.class;

	private final Map<IViewer<Node>, AbstractRotateGesture> gestures = new HashMap<>();

	/**
	 * Returns a {@link Set} containing all {@link AbstractFXOnRotatePolicy}s
	 * that are installed on the given target {@link IVisualPart}.
	 *
	 * @param targetPart
	 *            The target {@link IVisualPart} of which the
	 *            {@link AbstractFXOnRotatePolicy}s are returned.
	 * @return A {@link Set} containing all {@link AbstractFXOnRotatePolicy}s
	 *         that are installed on the given target {@link IVisualPart}.
	 */
	// TODO: Rename to getOnRotatePolicies()
	protected Set<? extends AbstractFXOnRotatePolicy> getRotatePolicies(
			IVisualPart<Node, ? extends Node> targetPart) {
		return new HashSet<>(targetPart
				.<AbstractFXOnRotatePolicy> getAdapters(TOOL_POLICY_KEY)
				.values());
	}

	/**
	 * Returns the target {@link IVisualPart} within the given {@link IViewer}
	 * for the given target {@link Node} that received the input event.
	 *
	 * @param viewer
	 *            The {@link IViewer} in which a target {@link IVisualPart} is
	 *            searched.
	 * @param target
	 *            The target {@link Node} that received the input event.
	 * @return The determined target {@link IVisualPart}.
	 */
	protected IVisualPart<Node, ? extends Node> getTargetPart(
			IViewer<Node> viewer, Node target) {
		IVisualPart<Node, ? extends Node> targetPart = FXPartUtils
				.getTargetPart(Collections.singleton(viewer), target,
						TOOL_POLICY_KEY, true);
		return targetPart;
	}

	/**
	 * Returns a {@link Set} containing all {@link AbstractFXOnRotatePolicy}s
	 * that are installed on the target {@link IVisualPart} for the given
	 * {@link RotateEvent}. The target {@link IVisualPart} is determined using
	 * {@link #getTargetPart(IViewer, Node)}.
	 *
	 * @param viewer
	 *            The {@link IViewer} in which the input event occured.
	 * @param e
	 *            The input {@link RotateEvent}.
	 * @return A {@link Set} containing all {@link AbstractFXOnRotatePolicy}s
	 *         that are installed on the target {@link IVisualPart} for the
	 *         given input event.
	 */
	protected Set<? extends AbstractFXOnRotatePolicy> getTargetPolicies(
			IViewer<Node> viewer, RotateEvent e) {
		EventTarget target = e.getTarget();
		if (!(target instanceof Node)) {
			return null;
		}

		Node targetNode = (Node) target;
		IVisualPart<Node, ? extends Node> targetPart = getTargetPart(viewer,
				targetNode);

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
			AbstractRotateGesture gesture = new AbstractRotateGesture() {

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
		for (AbstractRotateGesture gesture : gestures.values()) {
			gesture.setScene(null);
		}
		super.unregisterListeners();
	}

}
