/*******************************************************************************
 * Copyright (c) 2014, 2015 itemis AG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Matthias Wienand (itemis AG) - initial API and implementation
 *     Alexander Nyßen (itemis AG) - refactorings
 *
 *******************************************************************************/
package org.eclipse.gef4.mvc.fx.tools;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.gef4.fx.gestures.AbstractPinchSpreadGesture;
import org.eclipse.gef4.mvc.fx.domain.FXDomain;
import org.eclipse.gef4.mvc.fx.parts.FXPartUtils;
import org.eclipse.gef4.mvc.fx.policies.AbstractFXOnPinchSpreadPolicy;
import org.eclipse.gef4.mvc.fx.viewer.FXViewer;
import org.eclipse.gef4.mvc.parts.IVisualPart;
import org.eclipse.gef4.mvc.tools.AbstractTool;
import org.eclipse.gef4.mvc.tools.ITool;
import org.eclipse.gef4.mvc.viewer.IViewer;

import javafx.event.EventTarget;
import javafx.scene.Node;
import javafx.scene.input.ZoomEvent;

/**
 * An {@link ITool} to handle pinch/spread (zoom) interaction gestures.
 * <p>
 * During each pinch/spread interaction, the tool identifies an
 * {@link IVisualPart} that serves as interaction target. It is identified via
 * hit-testing on the visuals and the availability of a corresponding
 * {@link AbstractFXOnPinchSpreadPolicy} (see
 * {@link #getTargetPart(IViewer, Node)}).
 * <p>
 * The {@link FXPinchSpreadTool} handles the opening and closing of an
 * transaction operation via the {@link FXDomain}, to which it is adapted. It
 * controls that a single transaction operation is used for the complete
 * interaction, so all interaction results can be undone in a single undo step.
 *
 * @author mwienand
 * @author anyssen
 *
 */
public class FXPinchSpreadTool extends AbstractTool<Node> {

	/**
	 * The type of the policy that has to be supported by target parts.
	 */
	// TODO: Rename to ON_PINCH_SPREAD_POLICY_KEY
	public static final Class<AbstractFXOnPinchSpreadPolicy> TOOL_POLICY_KEY = AbstractFXOnPinchSpreadPolicy.class;

	private final Map<IViewer<Node>, AbstractPinchSpreadGesture> gestures = new HashMap<IViewer<Node>, AbstractPinchSpreadGesture>();

	/**
	 * Returns a {@link Set} containing all
	 * {@link AbstractFXOnPinchSpreadPolicy}s that are installed on the given
	 * target {@link IVisualPart}.
	 *
	 * @param targetPart
	 *            The target {@link IVisualPart} of which the installed
	 *            {@link AbstractFXOnPinchSpreadPolicy}s are returned.
	 * @return A {@link Set} containing all
	 *         {@link AbstractFXOnPinchSpreadPolicy}s that are installed on the
	 *         given target {@link IVisualPart}.
	 */
	// TODO: Rename to getOnPinchSpreachPolicies()
	protected Set<? extends AbstractFXOnPinchSpreadPolicy> getPinchSpreadPolicies(
			IVisualPart<Node, ? extends Node> targetPart) {
		return new HashSet<>(targetPart
				.<AbstractFXOnPinchSpreadPolicy> getAdapters(TOOL_POLICY_KEY)
				.values());
	}

	/**
	 * Returns the target {@link IVisualPart} for the given target {@link Node}
	 * within the given {@link IViewer}.
	 *
	 * @param viewer
	 *            The {@link IViewer} which is searched for the target
	 *            {@link IVisualPart}.
	 * @param target
	 *            The target {@link Node} that received the input event.
	 * @return The target {@link IVisualPart} that was determined.
	 */
	protected IVisualPart<Node, ? extends Node> getTargetPart(
			IViewer<Node> viewer, Node target) {
		IVisualPart<Node, ? extends Node> targetPart = FXPartUtils
				.getTargetPart(Collections.singleton(viewer), target,
						TOOL_POLICY_KEY, true);
		return targetPart;
	}

	/**
	 * Returns a {@link Set} containing all
	 * {@link AbstractFXOnPinchSpreadPolicy}s that are supported by the target
	 * {@link IVisualPart} for the given {@link ZoomEvent}.
	 *
	 * @param viewer
	 *            The {@link IViewer} that is searched for a target
	 *            {@link IVisualPart}.
	 * @param e
	 *            The {@link ZoomEvent} that has to be transfered.
	 * @return A {@link Set} containing all
	 *         {@link AbstractFXOnPinchSpreadPolicy}s that are supported by the
	 *         target {@link IVisualPart} for the given {@link ZoomEvent}.
	 */
	protected Set<? extends AbstractFXOnPinchSpreadPolicy> getTargetPolicies(
			IViewer<Node> viewer, ZoomEvent e) {
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

		return getPinchSpreadPolicies(targetPart);
	}

	@Override
	protected void registerListeners() {
		super.registerListeners();
		for (final IViewer<Node> viewer : getDomain().getViewers().values()) {
			AbstractPinchSpreadGesture gesture = new AbstractPinchSpreadGesture() {
				@Override
				protected void zoom(ZoomEvent e) {
					// the start event might get lost, so we should open a
					// transaction if one is not already open
					for (AbstractFXOnPinchSpreadPolicy policy : getTargetPolicies(
							viewer, e)) {
						policy.zoom(e);
					}
				}

				@Override
				protected void zoomFinished(ZoomEvent e) {
					for (AbstractFXOnPinchSpreadPolicy policy : getTargetPolicies(
							viewer, e)) {
						policy.zoomFinished(e);
					}
					getDomain()
							.closeExecutionTransaction(FXPinchSpreadTool.this);
				}

				@Override
				protected void zoomStarted(ZoomEvent e) {
					// zoom finish may not occur, so close any preceding
					// transaction just in case

					if (!getDomain().isExecutionTransactionOpen(
							FXPinchSpreadTool.this)) {
						// TODO: this case should already be handled by the
						// underlying gesture
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
		for (AbstractPinchSpreadGesture gesture : gestures.values()) {
			gesture.setScene(null);
		}
		super.unregisterListeners();
	}

}
