/*******************************************************************************
 * Copyright (c) 2014, 2017 itemis AG and others.
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
package org.eclipse.gef.mvc.fx.tools;

import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.gef.mvc.fx.domain.IDomain;
import org.eclipse.gef.mvc.fx.parts.PartUtils;
import org.eclipse.gef.mvc.fx.policies.IOnPinchSpreadPolicy;
import org.eclipse.gef.mvc.fx.viewer.IViewer;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.event.EventTarget;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.input.ZoomEvent;

/**
 * The {@link PinchSpreadInteraction} is an {@link AbstractInteraction} to handle pinch/spread
 * (zoom) interaction gestures.
 * <p>
 * The {@link PinchSpreadInteraction} handles the opening and closing of an transaction
 * operation via the {@link IDomain}, to which it is adapted. It controls that a
 * single transaction operation is used for the complete interaction, so all
 * interaction results can be undone in a single undo step.
 *
 * @author mwienand
 * @author anyssen
 *
 */
public class PinchSpreadInteraction extends AbstractInteraction {
	/**
	 * The type of the policy that has to be supported by target parts.
	 */
	public static final Class<IOnPinchSpreadPolicy> ON_PINCH_SPREAD_POLICY_KEY = IOnPinchSpreadPolicy.class;

	private final Map<IViewer, ChangeListener<Boolean>> viewerFocusChangeListeners = new IdentityHashMap<>();
	private Map<Scene, EventHandler<ZoomEvent>> zoomFilters = new IdentityHashMap<>();

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
	protected ChangeListener<Boolean> createFocusChangeListener(
			final IViewer viewer) {
		ChangeListener<Boolean> viewerFocusChangeListener = new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> observable,
					Boolean oldValue, Boolean newValue) {
				if (newValue == null || !newValue) {
					// cancel target policies
					for (IOnPinchSpreadPolicy policy : getActivePolicies(
							viewer)) {
						policy.abortZoom();
					}
					// clear active policies and close execution
					// transaction
					clearActivePolicies(viewer);
					getDomain().closeExecutionTransaction(PinchSpreadInteraction.this);
				}
			}
		};
		return viewerFocusChangeListener;
	}

	/**
	 * Creates an {@link EventHandler} for {@link ZoomEvent}s that forwards the
	 * events to the {@link IOnPinchSpreadPolicy} implementations that can be
	 * determined for the individual events and the given {@link IViewer}.
	 *
	 * @param scene
	 *            The {@link Scene} where the {@link EventHandler} will be
	 *            registered.
	 * @return The {@link EventHandler} that can be registered at the given
	 *         {@link Scene}.
	 */
	protected EventHandler<ZoomEvent> createZoomFilter(Scene scene) {
		return new EventHandler<ZoomEvent>() {
			private IViewer activeViewer;

			@Override
			public void handle(ZoomEvent event) {
				if (ZoomEvent.ZOOM.equals(event.getEventType())) {
					if (activeViewer == null) {
						return;
					}
					for (IOnPinchSpreadPolicy policy : getActivePolicies(
							activeViewer)) {
						policy.zoom(event);
					}
				} else if (ZoomEvent.ZOOM_STARTED
						.equals(event.getEventType())) {
					if (!(event.getTarget() instanceof Node)) {
						return;
					}
					IViewer viewer = PartUtils.retrieveViewer(getDomain(),
							(Node) event.getTarget());
					if (viewer == null) {
						return;
					}
					activeViewer = viewer;

					// zoom finish may not occur, so close any preceding
					// transaction just in case
					if (!getDomain()
							.isExecutionTransactionOpen(PinchSpreadInteraction.this)) {
						// TODO: this case should already be handled by the
						// underlying gesture
						// finish event may not properly occur in all cases; we
						// may continue to use the still open transaction
						getDomain()
								.openExecutionTransaction(PinchSpreadInteraction.this);
					}

					// determine target policies
					EventTarget eventTarget = event.getTarget();
					setActivePolicies(activeViewer,
							getTargetPolicyResolver().resolvePolicies(
									PinchSpreadInteraction.this,
									eventTarget instanceof Node
											? (Node) eventTarget : null,
									activeViewer, ON_PINCH_SPREAD_POLICY_KEY));

					// send event to the policies
					for (IOnPinchSpreadPolicy policy : getActivePolicies(
							viewer)) {
						policy.startZoom(event);
					}
				} else if (ZoomEvent.ZOOM_FINISHED
						.equals(event.getEventType())) {
					if (activeViewer == null) {
						return;
					}
					for (IOnPinchSpreadPolicy policy : getActivePolicies(
							activeViewer)) {
						policy.endZoom(event);
					}
					clearActivePolicies(activeViewer);
					getDomain().closeExecutionTransaction(PinchSpreadInteraction.this);
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
			if (zoomFilters.containsKey(scene)) {
				// we are already listening for events of this scene
				continue;
			}

			// register zoom filter
			EventHandler<ZoomEvent> zoomFilter = createZoomFilter(scene);
			scene.addEventFilter(ZoomEvent.ANY, zoomFilter);
			zoomFilters.put(scene, zoomFilter);
		}
	}

	@Override
	protected void doDeactivate() {
		for (Scene scene : new ArrayList<>(zoomFilters.keySet())) {
			scene.removeEventFilter(ZoomEvent.ANY, zoomFilters.remove(scene));
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
	public List<? extends IOnPinchSpreadPolicy> getActivePolicies(
			IViewer viewer) {
		return (List<IOnPinchSpreadPolicy>) super.getActivePolicies(viewer);
	}
}
