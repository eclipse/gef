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
import org.eclipse.gef.mvc.fx.domain.Domain;
import org.eclipse.gef.mvc.fx.policies.IOnRotatePolicy;
import org.eclipse.gef.mvc.fx.viewer.IViewer;

import com.google.inject.Inject;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventTarget;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.input.RotateEvent;

/**
 * The {@link RotateTool} is an {@link AbstractTool} to handle rotate
 * interaction gestures.
 * <p>
 * The {@link RotateTool} handles the opening and closing of an transaction
 * operation via the {@link Domain}, to which it is adapted. It controls that a
 * single transaction operation is used for the complete interaction , so all
 * interaction results can be undone in a single undo step.
 *
 * @author anyssen
 *
 */
public class RotateTool extends AbstractTool {
	/**
	 * The type of the policy that has to be supported by target parts.
	 */
	public static final Class<IOnRotatePolicy> ON_ROTATE_POLICY_KEY = IOnRotatePolicy.class;
	@Inject
	private ITargetPolicyResolver targetPolicyResolver;

	private final Map<Scene, AbstractRotateGesture> gestures = new HashMap<>();
	private final Map<IViewer, ChangeListener<Boolean>> viewerFocusChangeListeners = new HashMap<>();

	@SuppressWarnings("unchecked")
	@Override
	public List<? extends IOnRotatePolicy> getActivePolicies(IViewer viewer) {
		return (List<IOnRotatePolicy>) super.getActivePolicies(viewer);
	}

	@Override
	protected void registerListeners() {
		super.registerListeners();
		for (final IViewer viewer : getDomain().getViewers().values()) {
			// register a viewer focus change listener
			ChangeListener<Boolean> viewerFocusChangeListener = new ChangeListener<Boolean>() {
				@Override
				public void changed(
						ObservableValue<? extends Boolean> observable,
						Boolean oldValue, Boolean newValue) {
					if (newValue == null || !newValue) {
						// cancel target policies
						for (IOnRotatePolicy policy : getActivePolicies(
								viewer)) {
							policy.abortRotate();
						}
						// clear active policies and close execution
						// transaction
						clearActivePolicies(viewer);
						getDomain().closeExecutionTransaction(RotateTool.this);
					}

				}
			};
			viewer.viewerFocusedProperty()
					.addListener(viewerFocusChangeListener);
			viewerFocusChangeListeners.put(viewer, viewerFocusChangeListener);

			Scene scene = viewer.getCanvas().getScene();
			if (gestures.containsKey(scene)) {
				continue;
			}

			AbstractRotateGesture gesture = new AbstractRotateGesture() {
				@Override
				protected void rotate(RotateEvent event) {
					for (IOnRotatePolicy policy : getActivePolicies(viewer)) {
						policy.rotate(event);
					}
				}

				@Override
				protected void rotationFinished(RotateEvent event) {
					for (IOnRotatePolicy policy : getActivePolicies(viewer)) {
						policy.endRotate(event);
					}
					clearActivePolicies(viewer);
					getDomain().closeExecutionTransaction(RotateTool.this);
				}

				@Override
				protected void rotationStarted(RotateEvent event) {
					EventTarget eventTarget = event.getTarget();
					setActivePolicies(viewer,
							targetPolicyResolver.getTargetPolicies(
									RotateTool.this,
									eventTarget instanceof Node
											? (Node) eventTarget : null,
									ON_ROTATE_POLICY_KEY));
					getDomain().openExecutionTransaction(RotateTool.this);
					for (IOnRotatePolicy policy : getActivePolicies(viewer)) {
						policy.startRotate(event);
					}
				}
			};
			gesture.setScene(scene);
			gestures.put(scene, gesture);
		}

	}

	@Override
	protected void unregisterListeners() {
		for (IViewer viewer : new ArrayList<>(
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
