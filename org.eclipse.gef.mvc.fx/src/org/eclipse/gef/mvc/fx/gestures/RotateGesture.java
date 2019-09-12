/*******************************************************************************
 * Copyright (c) 2015, 2019 itemis AG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Alexander Nyßen (itemis AG) - initial API and implementation
 *     Matthias Wienand (itemis AG) - inline rotate gesture
 *
 *******************************************************************************/
package org.eclipse.gef.mvc.fx.gestures;

import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.gef.mvc.fx.domain.IDomain;
import org.eclipse.gef.mvc.fx.handlers.IOnRotateHandler;
import org.eclipse.gef.mvc.fx.parts.PartUtils;
import org.eclipse.gef.mvc.fx.viewer.IViewer;

import javafx.event.EventHandler;
import javafx.event.EventTarget;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.input.RotateEvent;

/**
 * The {@link RotateGesture} is an {@link AbstractGesture} to handle rotate
 * interaction gestures.
 * <p>
 * The {@link RotateGesture} handles the opening and closing of an transaction
 * operation via the {@link IDomain}, to which it is adapted. It controls that a
 * single transaction operation is used for the complete interaction , so all
 * interaction results can be undone in a single undo step.
 *
 * @author anyssen
 *
 */
public class RotateGesture extends AbstractGesture {
	/**
	 * The type of the policy that has to be supported by target parts.
	 */
	public static final Class<IOnRotateHandler> ON_ROTATE_POLICY_KEY = IOnRotateHandler.class;

	private Map<Scene, EventHandler<RotateEvent>> rotateFilters = new IdentityHashMap<>();

	/**
	 * Creates an {@link EventHandler} for {@link RotateEvent}s that forwards
	 * the events to the {@link IOnRotateHandler} implementations that can be
	 * determined for the individual events and the given {@link IViewer}.
	 *
	 * @param scene
	 *            The {@link Scene} where the {@link EventHandler} will be
	 *            registered.
	 * @return The {@link EventHandler} that can be registered at the given
	 *         {@link Scene}.
	 */
	protected EventHandler<RotateEvent> createRotateFilter(Scene scene) {
		return new EventHandler<RotateEvent>() {
			@Override
			public void handle(RotateEvent event) {
				if (!(event.getTarget() instanceof Node)) {
					return;
				}
				IViewer viewer = PartUtils.retrieveViewer(getDomain(),
						(Node) event.getTarget());
				if (viewer == null) {
					return;
				}
				if (RotateEvent.ROTATE.equals(event.getEventType())) {
					for (IOnRotateHandler policy : getActiveHandlers(viewer)) {
						policy.rotate(event);
					}
				} else if (RotateEvent.ROTATION_STARTED
						.equals(event.getEventType())) {
					// zoom finish may not occur, so close any preceding
					// transaction just in case
					if (!getDomain()
							.isExecutionTransactionOpen(RotateGesture.this)) {
						// TODO: this case should already be handled by the
						// underlying gesture
						// finish event may not properly occur in all cases; we
						// may continue to use the still open transaction
						getDomain()
								.openExecutionTransaction(RotateGesture.this);
					}

					// determine target policies
					EventTarget eventTarget = event.getTarget();
					setActiveHandlers(viewer,
							getHandlerResolver().resolve(RotateGesture.this,
									eventTarget instanceof Node
											? (Node) eventTarget
											: null,
									viewer, ON_ROTATE_POLICY_KEY));

					// send event to the policies
					for (IOnRotateHandler policy : getActiveHandlers(viewer)) {
						policy.startRotate(event);
					}
				} else if (RotateEvent.ROTATION_FINISHED
						.equals(event.getEventType())) {
					for (IOnRotateHandler policy : getActiveHandlers(viewer)) {
						policy.endRotate(event);
					}
					clearActiveHandlers(viewer);
					getDomain().closeExecutionTransaction(RotateGesture.this);
				}
			}
		};
	}

	@Override
	protected void doHookScene(Scene scene) {
		EventHandler<RotateEvent> rotateFilter = createRotateFilter(scene);
		scene.addEventFilter(RotateEvent.ANY, rotateFilter);
		rotateFilters.put(scene, rotateFilter);
	}

	@Override
	protected void doUnhookScene(Scene scene) {
		scene.removeEventFilter(RotateEvent.ANY, rotateFilters.remove(scene));
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<? extends IOnRotateHandler> getActiveHandlers(IViewer viewer) {
		return (List<IOnRotateHandler>) super.getActiveHandlers(viewer);
	}
}
