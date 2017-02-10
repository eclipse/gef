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
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.gef.mvc.fx.domain.IDomain;
import org.eclipse.gef.mvc.fx.policies.IOnRotatePolicy;
import org.eclipse.gef.mvc.fx.viewer.IViewer;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.event.EventTarget;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.input.RotateEvent;

/**
 * The {@link RotateTool} is an {@link AbstractTool} to handle rotate
 * interaction gestures.
 * <p>
 * The {@link RotateTool} handles the opening and closing of an transaction
 * operation via the {@link IDomain}, to which it is adapted. It controls that a
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

	private final Map<IViewer, ChangeListener<Boolean>> viewerFocusChangeListeners = new IdentityHashMap<>();
	private Map<Scene, EventHandler<RotateEvent>> rotateFilters = new IdentityHashMap<>();

	/**
	 * Creates a {@link ChangeListener} for the
	 * {@link IViewer#viewerFocusedProperty() focused} property of the given
	 * {@link IViewer}.
	 *
	 * @param viewer
	 *            The {@link IViewer} for which to create a
	 *            {@link ChangeListener} for its
	 *            {@link IViewer#viewerFocusedProperty() focused} property.
	 * @return The newly created {@link ChangeListener} for the
	 *         {@link IViewer#viewerFocusedProperty() focused} property of the
	 *         given {@link IViewer}.
	 */
	@Override
	protected ChangeListener<Boolean> createFocusChangeListener(
			final IViewer viewer) {
		ChangeListener<Boolean> viewerFocusChangeListener = new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> observable,
					Boolean oldValue, Boolean newValue) {
				if (newValue == null || !newValue) {
					// cancel target policies
					for (IOnRotatePolicy policy : getActivePolicies(viewer)) {
						policy.abortRotate();
					}
					// clear active policies and close execution
					// transaction
					clearActivePolicies(viewer);
					getDomain().closeExecutionTransaction(RotateTool.this);
				}
			}
		};
		return viewerFocusChangeListener;
	}

	/**
	 * Creates an {@link EventHandler} for {@link RotateEvent}s that forwards
	 * the events to the {@link IOnRotatePolicy} implementations that can be
	 * determined for the individual events and the given {@link IViewer}.
	 *
	 * @param scene
	 *            The {@link Scene} where the {@link EventHandler} will be
	 *            registered.
	 * @param viewer
	 *            The {@link IViewer} to which events are forwarded.
	 * @return The {@link EventHandler} that can be registered at the given
	 *         {@link Scene}.
	 */
	protected EventHandler<RotateEvent> createRotateFilter(Scene scene,
			IViewer viewer) {
		return new EventHandler<RotateEvent>() {
			@Override
			public void handle(RotateEvent event) {
				if (RotateEvent.ROTATE.equals(event.getEventType())) {
					for (IOnRotatePolicy policy : getActivePolicies(viewer)) {
						policy.rotate(event);
					}
				} else if (RotateEvent.ROTATION_STARTED
						.equals(event.getEventType())) {
					// zoom finish may not occur, so close any preceding
					// transaction just in case
					if (!getDomain()
							.isExecutionTransactionOpen(RotateTool.this)) {
						// TODO: this case should already be handled by the
						// underlying gesture
						// finish event may not properly occur in all cases; we
						// may continue to use the still open transaction
						getDomain().openExecutionTransaction(RotateTool.this);
					}

					// determine target policies
					EventTarget eventTarget = event.getTarget();
					setActivePolicies(viewer,
							getTargetPolicyResolver().getTargetPolicies(
									RotateTool.this,
									eventTarget instanceof Node
											? (Node) eventTarget : null,
									ON_ROTATE_POLICY_KEY));

					// send event to the policies
					for (IOnRotatePolicy policy : getActivePolicies(viewer)) {
						policy.startRotate(event);
					}
				} else if (RotateEvent.ROTATION_FINISHED
						.equals(event.getEventType())) {
					for (IOnRotatePolicy policy : getActivePolicies(viewer)) {
						policy.endRotate(event);
					}
					clearActivePolicies(viewer);
					getDomain().closeExecutionTransaction(RotateTool.this);
				}
			}
		};
	}

	@Override
	protected void doActivate() {
		super.doActivate();
		for (final IViewer viewer : getDomain().getViewers().values()) {
			// register a viewer focus change listener
			ChangeListener<Boolean> viewerFocusChangeListener = createFocusChangeListener(
					viewer);
			viewer.viewerFocusedProperty()
					.addListener(viewerFocusChangeListener);
			viewerFocusChangeListeners.put(viewer, viewerFocusChangeListener);

			// check if we already registered zoom listeners at the viewer's
			// scene (in case two viewer's share a single scene)
			Scene scene = viewer.getCanvas().getScene();
			if (rotateFilters.containsKey(scene)) {
				// we are already listening for events of this scene
				continue;
			}

			// register zoom filter
			EventHandler<RotateEvent> rotateFilter = createRotateFilter(scene,
					viewer);
			scene.addEventFilter(RotateEvent.ANY, rotateFilter);
			rotateFilters.put(scene, rotateFilter);
		}
	}

	@Override
	protected void doDeactivate() {
		for (Scene scene : new ArrayList<>(rotateFilters.keySet())) {
			scene.removeEventFilter(RotateEvent.ANY,
					rotateFilters.remove(scene));
		}
		for (final IViewer viewer : new ArrayList<>(
				viewerFocusChangeListeners.keySet())) {
			viewer.viewerFocusedProperty()
					.removeListener(viewerFocusChangeListeners.remove(viewer));
		}
		super.doDeactivate();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<? extends IOnRotatePolicy> getActivePolicies(IViewer viewer) {
		return (List<IOnRotatePolicy>) super.getActivePolicies(viewer);
	}
}
