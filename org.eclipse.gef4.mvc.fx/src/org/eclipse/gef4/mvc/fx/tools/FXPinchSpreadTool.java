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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.gef4.fx.gestures.AbstractPinchSpreadGesture;
import org.eclipse.gef4.mvc.fx.domain.FXDomain;
import org.eclipse.gef4.mvc.fx.policies.IFXOnPinchSpreadPolicy;
import org.eclipse.gef4.mvc.fx.viewer.FXViewer;
import org.eclipse.gef4.mvc.viewer.IViewer;

import javafx.event.EventTarget;
import javafx.scene.Node;
import javafx.scene.input.ZoomEvent;

/**
 * The {@link FXPinchSpreadTool} is an {@link AbstractFXTool} to handle
 * pinch/spread (zoom) interaction gestures.
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
public class FXPinchSpreadTool extends AbstractFXTool {

	/**
	 * The type of the policy that has to be supported by target parts.
	 */
	public static final Class<IFXOnPinchSpreadPolicy> ON_PINCH_SPREAD_POLICY_KEY = IFXOnPinchSpreadPolicy.class;

	private final Map<IViewer<Node>, AbstractPinchSpreadGesture> gestures = new HashMap<>();

	@Override
	protected void registerListeners() {
		super.registerListeners();
		for (final IViewer<Node> viewer : getDomain().getViewers().values()) {
			AbstractPinchSpreadGesture gesture = new AbstractPinchSpreadGesture() {
				private List<? extends IFXOnPinchSpreadPolicy> policies;

				@Override
				protected void zoom(ZoomEvent e) {
					// the start event might get lost, so we should open a
					// transaction if one is not already open
					for (IFXOnPinchSpreadPolicy policy : policies) {
						policy.zoom(e);
					}
				}

				@Override
				protected void zoomFinished(ZoomEvent e) {
					for (IFXOnPinchSpreadPolicy policy : policies) {
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
					EventTarget eventTarget = e.getTarget();
					policies = getTargetPolicies(
							viewer, eventTarget instanceof Node
									? (Node) eventTarget : null,
							ON_PINCH_SPREAD_POLICY_KEY);
					for (IFXOnPinchSpreadPolicy policy : policies) {
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
