/*******************************************************************************
 * Copyright (c) 2015, 2016 itemis AG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Alexander Nyßen (itemis AG) - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.gef.mvc.fx.tools;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.gef.fx.gestures.AbstractRotateGesture;
import org.eclipse.gef.mvc.fx.domain.FXDomain;
import org.eclipse.gef.mvc.fx.policies.IFXOnRotatePolicy;
import org.eclipse.gef.mvc.fx.viewer.FXViewer;
import org.eclipse.gef.mvc.tools.AbstractTool;
import org.eclipse.gef.mvc.viewer.IViewer;

import com.google.inject.Inject;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventTarget;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.input.RotateEvent;

/**
 * The {@link FXRotateTool} is an {@link AbstractTool} to handle rotate
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
public class FXRotateTool extends AbstractTool<Node> {
	/**
	 * The type of the policy that has to be supported by target parts.
	 */
	public static final Class<IFXOnRotatePolicy> ON_ROTATE_POLICY_KEY = IFXOnRotatePolicy.class;
	@Inject
	private ITargetPolicyResolver targetPolicyResolver;

	private final Map<Scene, AbstractRotateGesture> gestures = new HashMap<>();
	private final Map<IViewer<Node>, ChangeListener<Boolean>> viewerFocusChangeListeners = new HashMap<>();

	@SuppressWarnings("unchecked")
	@Override
	public List<? extends IFXOnRotatePolicy> getActivePolicies(
			IViewer<Node> viewer) {
		return (List<IFXOnRotatePolicy>) super.getActivePolicies(viewer);
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
						for (IFXOnRotatePolicy policy : getActivePolicies(
								viewer)) {
							policy.rotationAborted();
						}
						// clear active policies and close execution
						// transaction
						clearActivePolicies(viewer);
						getDomain()
								.closeExecutionTransaction(FXRotateTool.this);
					}

				}
			};
			viewer.viewerFocusedProperty()
					.addListener(viewerFocusChangeListener);
			viewerFocusChangeListeners.put(viewer, viewerFocusChangeListener);

			Scene scene = ((FXViewer) viewer).getScene();
			if (gestures.containsKey(scene)) {
				continue;
			}

			AbstractRotateGesture gesture = new AbstractRotateGesture() {
				@Override
				protected void rotate(RotateEvent event) {
					for (IFXOnRotatePolicy policy : getActivePolicies(viewer)) {
						policy.rotate(event);
					}
				}

				@Override
				protected void rotationFinished(RotateEvent event) {
					for (IFXOnRotatePolicy policy : getActivePolicies(viewer)) {
						policy.rotationFinished(event);
					}
					clearActivePolicies(viewer);
					getDomain().closeExecutionTransaction(FXRotateTool.this);
				}

				@Override
				protected void rotationStarted(RotateEvent event) {
					EventTarget eventTarget = event.getTarget();
					setActivePolicies(viewer,
							targetPolicyResolver.getTargetPolicies(
									FXRotateTool.this,
									eventTarget instanceof Node
											? (Node) eventTarget : null,
									ON_ROTATE_POLICY_KEY));
					getDomain().openExecutionTransaction(FXRotateTool.this);
					for (IFXOnRotatePolicy policy : getActivePolicies(viewer)) {
						policy.rotationStarted(event);
					}
				}
			};
			gesture.setScene(scene);
			gestures.put(scene, gesture);
		}

	}

	@Override
	protected void unregisterListeners() {
		for (IViewer<Node> viewer : new ArrayList<>(
				viewerFocusChangeListeners.keySet())) {
			viewer.viewerFocusedProperty()
					.removeListener(viewerFocusChangeListeners.remove(viewer));
		}
		for (Scene scene : new ArrayList<>(gestures.keySet())) {
			gestures.remove(scene).setScene(null);
		}
		super.unregisterListeners();
	}

}
