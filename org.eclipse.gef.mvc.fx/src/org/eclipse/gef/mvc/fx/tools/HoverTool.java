/*******************************************************************************
 * Copyright (c) 2014, 2016 itemis AG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Matthias Wienand (itemis AG) - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.gef.mvc.fx.tools;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.gef.mvc.fx.policies.IOnHoverPolicy;
import org.eclipse.gef.mvc.fx.viewer.IViewer;

import javafx.event.EventHandler;
import javafx.event.EventTarget;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;

/**
 * The {@link HoverTool} is an {@link AbstractTool} that handles mouse hover
 * changes.
 *
 * @author mwienand
 *
 */
public class HoverTool extends AbstractTool {

	/**
	 * The type of the policy that has to be supported by target parts.
	 */
	public static final Class<IOnHoverPolicy> ON_HOVER_POLICY_KEY = IOnHoverPolicy.class;

	private final Map<Scene, EventHandler<MouseEvent>> hoverFilters = new HashMap<>();

	/**
	 * Creates an {@link EventHandler} for hover {@link MouseEvent}s. The
	 * handler will search for a target part within the given {@link IViewer}
	 * and notify all hover policies of that target part about hover changes.
	 * <p>
	 * If no target part can be identified, then the root part of the given
	 * {@link IViewer} is used as the target part.
	 *
	 * @param viewer
	 *            The {@link IViewer} for which to create the
	 *            {@link EventHandler}.
	 * @return The {@link EventHandler} that handles hover changes for the given
	 *         {@link IViewer}.
	 */
	protected EventHandler<MouseEvent> createHoverFilter(final IViewer viewer) {
		return new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				if (!event.getEventType().equals(MouseEvent.MOUSE_MOVED)
						&& !event.getEventType()
								.equals(MouseEvent.MOUSE_ENTERED_TARGET)
						&& !event.getEventType()
								.equals(MouseEvent.MOUSE_EXITED_TARGET)) {
					return;
				}

				EventTarget eventTarget = event.getTarget();
				if (eventTarget instanceof Node) {
					// FIXME: For some events, "The given target Node is not
					// contained within an IViewer."
					Collection<? extends IOnHoverPolicy> policies = getTargetPolicyResolver()
							.getTargetPolicies(HoverTool.this,
									(Node) eventTarget, ON_HOVER_POLICY_KEY);
					getDomain().openExecutionTransaction(HoverTool.this);
					// active policies are unnecessary because hover is not a
					// gesture, just one event at one point in time
					for (IOnHoverPolicy policy : policies) {
						policy.hover(event);
					}
					getDomain().closeExecutionTransaction(HoverTool.this);
				}
			}
		};
	}

	@Override
	protected void registerListeners() {
		super.registerListeners();
		for (IViewer viewer : getDomain().getViewers().values()) {
			Scene scene = viewer.getCanvas().getScene();
			if (!hoverFilters.containsKey(scene)) {
				EventHandler<MouseEvent> hoverFilter = createHoverFilter(
						viewer);
				scene.addEventFilter(MouseEvent.ANY, hoverFilter);
				hoverFilters.put(scene, hoverFilter);
			}
		}
	}

	@Override
	protected void unregisterListeners() {
		for (Scene scene : hoverFilters.keySet()) {
			scene.removeEventFilter(MouseEvent.ANY, hoverFilters.remove(scene));
		}
		super.unregisterListeners();
	}

}
