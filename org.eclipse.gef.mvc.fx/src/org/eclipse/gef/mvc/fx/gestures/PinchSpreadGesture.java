/*******************************************************************************
 * Copyright (c) 2014, 2019 itemis AG and others.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Matthias Wienand (itemis AG) - initial API and implementation
 *     Alexander Nyßen (itemis AG) - refactorings
 *
 *******************************************************************************/
package org.eclipse.gef.mvc.fx.gestures;

import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.gef.mvc.fx.domain.IDomain;
import org.eclipse.gef.mvc.fx.handlers.IOnPinchSpreadHandler;
import org.eclipse.gef.mvc.fx.parts.PartUtils;
import org.eclipse.gef.mvc.fx.viewer.IViewer;

import javafx.event.EventHandler;
import javafx.event.EventTarget;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.input.ZoomEvent;

/**
 * The {@link PinchSpreadGesture} is an {@link AbstractGesture} to handle
 * pinch/spread (zoom) interaction gestures.
 * <p>
 * The {@link PinchSpreadGesture} handles the opening and closing of an
 * transaction operation via the {@link IDomain}, to which it is adapted. It
 * controls that a single transaction operation is used for the complete
 * interaction, so all interaction results can be undone in a single undo step.
 *
 * @author mwienand
 * @author anyssen
 *
 */
public class PinchSpreadGesture extends AbstractGesture {
	/**
	 * The type of the policy that has to be supported by target parts.
	 */
	public static final Class<IOnPinchSpreadHandler> ON_PINCH_SPREAD_POLICY_KEY = IOnPinchSpreadHandler.class;

	private Map<Scene, EventHandler<ZoomEvent>> zoomFilters = new IdentityHashMap<>();

	/**
	 * Creates an {@link EventHandler} for {@link ZoomEvent}s that forwards the
	 * events to the {@link IOnPinchSpreadHandler} implementations that can be
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
					for (IOnPinchSpreadHandler policy : getActiveHandlers(
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
					if (!getDomain().isExecutionTransactionOpen(
							PinchSpreadGesture.this)) {
						// TODO: this case should already be handled by the
						// underlying gesture
						// finish event may not properly occur in all cases; we
						// may continue to use the still open transaction
						getDomain().openExecutionTransaction(
								PinchSpreadGesture.this);
					}

					// determine target policies
					EventTarget eventTarget = event.getTarget();
					setActiveHandlers(activeViewer,
							getHandlerResolver().resolve(
									PinchSpreadGesture.this,
									eventTarget instanceof Node
											? (Node) eventTarget
											: null,
									activeViewer, ON_PINCH_SPREAD_POLICY_KEY));

					// send event to the policies
					for (IOnPinchSpreadHandler policy : getActiveHandlers(
							viewer)) {
						policy.startZoom(event);
					}
				} else if (ZoomEvent.ZOOM_FINISHED
						.equals(event.getEventType())) {
					if (activeViewer == null) {
						return;
					}
					for (IOnPinchSpreadHandler policy : getActiveHandlers(
							activeViewer)) {
						policy.endZoom(event);
					}
					clearActiveHandlers(activeViewer);
					getDomain()
							.closeExecutionTransaction(PinchSpreadGesture.this);
				}
			}
		};
	}

	@Override
	protected void doAbortPolicies(IViewer viewer) {
		for (IOnPinchSpreadHandler policy : getActiveHandlers(viewer)) {
			policy.abortZoom();
		}
	}

	@Override
	protected void doHookScene(Scene scene) {
		EventHandler<ZoomEvent> zoomFilter = createZoomFilter(scene);
		scene.addEventFilter(ZoomEvent.ANY, zoomFilter);
		zoomFilters.put(scene, zoomFilter);
	}

	@Override
	protected void doUnhookScene(Scene scene) {
		scene.removeEventFilter(ZoomEvent.ANY, zoomFilters.remove(scene));
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<? extends IOnPinchSpreadHandler> getActiveHandlers(
			IViewer viewer) {
		return (List<IOnPinchSpreadHandler>) super.getActiveHandlers(viewer);
	}
}
