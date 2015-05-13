/*******************************************************************************
 * Copyright (c) 2014, 2015 itemis AG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Matthias Wienand (itemis AG) - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.gef4.mvc.fx.tools;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javafx.event.EventHandler;
import javafx.event.EventTarget;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;

import org.eclipse.gef4.fx.nodes.FXUtils;
import org.eclipse.gef4.mvc.fx.policies.AbstractFXOnHoverPolicy;
import org.eclipse.gef4.mvc.fx.viewer.FXViewer;
import org.eclipse.gef4.mvc.parts.IVisualPart;
import org.eclipse.gef4.mvc.tools.AbstractTool;
import org.eclipse.gef4.mvc.viewer.IViewer;

public class FXHoverTool extends AbstractTool<Node> {

	public static final Class<AbstractFXOnHoverPolicy> TOOL_POLICY_KEY = AbstractFXOnHoverPolicy.class;

	private final Map<FXViewer, EventHandler<MouseEvent>> hoverFilters = new HashMap<FXViewer, EventHandler<MouseEvent>>();

	protected EventHandler<MouseEvent> createHoverFilter(final FXViewer viewer) {
		return new EventHandler<MouseEvent>() {
			protected Collection<? extends AbstractFXOnHoverPolicy> getTargetPolicies(
					final MouseEvent event) {
				EventTarget target = event.getTarget();
				if (!(target instanceof Node)) {
					return Collections.emptyList();
				}

				Scene scene = ((Node) target).getScene();
				if (scene == null) {
					return Collections.emptyList();
				}

				// pick target nodes
				List<Node> targetNodes = FXUtils.getNodesAt(scene.getRoot(),
						event.getSceneX(), event.getSceneY());

				IVisualPart<Node, ? extends Node> targetPart = null;
				outer: for (int i = 0; i < targetNodes.size(); i++) {
					Node n = targetNodes.get(i);
					if (viewer.getScene() == scene) {
						IVisualPart<Node, ? extends Node> part = viewer
								.getVisualPartMap().get(n);
						if (part != null) {
							targetPart = part;
							break outer;
						}
					}
				}

				// if no target part could be found, send the event to the root
				// part
				if (targetPart == null) {
					targetPart = viewer.getRootPart();
				}

				Collection<? extends AbstractFXOnHoverPolicy> policies = getHoverPolicies(targetPart);
				return policies;
			}

			@Override
			public void handle(MouseEvent event) {
				if (!event.getEventType().equals(MouseEvent.MOUSE_MOVED)
						&& !event.getEventType().equals(
								MouseEvent.MOUSE_DRAGGED)) {
					return;
				}

				Collection<? extends AbstractFXOnHoverPolicy> policies = getTargetPolicies(event);
				for (AbstractFXOnHoverPolicy policy : policies) {
					policy.hover(event);
				}
			}
		};
	}

	protected Set<? extends AbstractFXOnHoverPolicy> getHoverPolicies(
			IVisualPart<Node, ? extends Node> targetPart) {
		return new HashSet<>(targetPart.<AbstractFXOnHoverPolicy> getAdapters(
				TOOL_POLICY_KEY).values());
	}

	@Override
	protected void registerListeners() {
		for (IViewer<Node> viewer : getDomain().getViewers().values()) {
			if (viewer instanceof FXViewer) {
				EventHandler<MouseEvent> hoverFilter = createHoverFilter((FXViewer) viewer);
				hoverFilters.put((FXViewer) viewer, hoverFilter);
				viewer.getRootPart().getVisual().getScene()
						.addEventFilter(MouseEvent.ANY, hoverFilter);
			}
		}
	}

	@Override
	protected void unregisterListeners() {
		for (Map.Entry<FXViewer, EventHandler<MouseEvent>> e : hoverFilters
				.entrySet()) {
			e.getKey().getRootPart().getVisual().getScene()
					.removeEventFilter(MouseEvent.ANY, e.getValue());
		}
		hoverFilters.clear();
	}

}
