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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.gef4.fx.gestures.AbstractPinchSpreadGesture;
import org.eclipse.gef4.mvc.fx.domain.FXDomain;
import org.eclipse.gef4.mvc.fx.policies.IFXOnPinchSpreadPolicy;
import org.eclipse.gef4.mvc.fx.viewer.FXViewer;
import org.eclipse.gef4.mvc.tools.AbstractTool;
import org.eclipse.gef4.mvc.viewer.IViewer;

import com.google.inject.Inject;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventTarget;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.input.ZoomEvent;

/**
 * The {@link FXPinchSpreadTool} is an {@link AbstractTool} to handle
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
public class FXPinchSpreadTool extends AbstractTool<Node> {
	/**
	 * The type of the policy that has to be supported by target parts.
	 */
	public static final Class<IFXOnPinchSpreadPolicy> ON_PINCH_SPREAD_POLICY_KEY = IFXOnPinchSpreadPolicy.class;
	@Inject
	private ITargetPolicyResolver targetPolicyResolver;

	private final Map<Scene, AbstractPinchSpreadGesture> gestures = new HashMap<>();
	private final Map<IViewer<Node>, ChangeListener<Boolean>> viewerFocusChangeListeners = new HashMap<>();

	@SuppressWarnings("unchecked")
	@Override
	public List<? extends IFXOnPinchSpreadPolicy> getActivePolicies(
			IViewer<Node> viewer) {
		return (List<IFXOnPinchSpreadPolicy>) super.getActivePolicies(viewer);
	}

	@Override
	protected void registerListeners() {
		super.registerListeners();
		for (final IViewer<Node> viewer : getDomain().getViewers().values()) {
			// register a viewer focus change listener
			ChangeListener<Boolean> viewerFocusChangeListener = new ChangeListener<Boolean>() {
				@Override
				public void changed(
						ObservableValue<? extends Boolean> observable,
						Boolean oldValue, Boolean newValue) {
					if (newValue == null || !newValue) {
						// cancel target policies
						for (IFXOnPinchSpreadPolicy policy : getActivePolicies(
								viewer)) {
							policy.zoomAborted();
						}
						// clear active policies and close execution
						// transaction
						clearActivePolicies(viewer);
						getDomain().closeExecutionTransaction(
								FXPinchSpreadTool.this);
					}

				}
			};
			viewer.viewerFocusedProperty()
					.addListener(viewerFocusChangeListener);
			viewerFocusChangeListeners.put(viewer, viewerFocusChangeListener);

			Scene scene = viewer.getRootPart().getVisual().getScene();
			if (gestures.containsKey(scene)) {
				continue;
			}

			AbstractPinchSpreadGesture gesture = new AbstractPinchSpreadGesture() {
				@Override
				protected void zoom(ZoomEvent e) {
					// the start event might get lost, so we should open a
					// transaction if one is not already open
					for (IFXOnPinchSpreadPolicy policy : getActivePolicies(
							viewer)) {
						policy.zoom(e);
					}
				}

				@Override
				protected void zoomFinished(ZoomEvent e) {
					for (IFXOnPinchSpreadPolicy policy : getActivePolicies(
							viewer)) {
						policy.zoomFinished(e);
					}
					clearActivePolicies(viewer);
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

					// determine target policies
					EventTarget eventTarget = e.getTarget();
					setActivePolicies(viewer,
							targetPolicyResolver.getTargetPolicies(
									FXPinchSpreadTool.this,
									eventTarget instanceof Node
											? (Node) eventTarget : null,
									ON_PINCH_SPREAD_POLICY_KEY));

					// send event to the policies
					for (IFXOnPinchSpreadPolicy policy : getActivePolicies(
							viewer)) {
						policy.zoomStarted(e);
					}
				}
			};
			gesture.setScene(((FXViewer) viewer).getScene());
			gestures.put(scene, gesture);
		}
	}

	@Override
	protected void unregisterListeners() {
		for (Scene scene : new ArrayList<>(gestures.keySet())) {
			gestures.remove(scene).setScene(null);
		}
		for (final IViewer<Node> viewer : new ArrayList<>(
				viewerFocusChangeListeners.keySet())) {
			viewer.viewerFocusedProperty()
					.removeListener(viewerFocusChangeListeners.remove(viewer));
		}
		super.unregisterListeners();
	}

}
