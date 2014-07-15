/*******************************************************************************
 * Copyright (c) 2014 itemis AG and others.
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

import java.util.List;

import javafx.event.EventHandler;
import javafx.event.EventTarget;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;

import org.eclipse.gef4.fx.nodes.FXUtils;
import org.eclipse.gef4.mvc.fx.policies.AbstractFXHoverPolicy;
import org.eclipse.gef4.mvc.fx.viewer.FXViewer;
import org.eclipse.gef4.mvc.parts.IVisualPart;
import org.eclipse.gef4.mvc.tools.AbstractTool;
import org.eclipse.gef4.mvc.viewer.IViewer;

public class FXHoverTool extends AbstractTool<Node> {

	public static final Class<AbstractFXHoverPolicy> TOOL_POLICY_KEY = AbstractFXHoverPolicy.class;

	private final EventHandler<MouseEvent> hoverFilter = new EventHandler<MouseEvent>() {
		@Override
		public void handle(MouseEvent event) {
			if (!event.getEventType().equals(MouseEvent.MOUSE_MOVED)
					&& !event.getEventType().equals(MouseEvent.MOUSE_DRAGGED)) {
				return;
			}

			EventTarget target = event.getTarget();
			if (!(target instanceof Node)) {
				return;
			}

			Scene scene = ((Node) target).getScene();

			// pick target nodes
			List<Node> targetNodes = FXUtils.getNodesAt(scene.getRoot(),
					event.getSceneX(), event.getSceneY());

			IVisualPart<Node> targetPart = null;
			outer: for (int i = 0; i < targetNodes.size(); i++) {
				Node n = targetNodes.get(i);
				for (IViewer<Node> viewer : getDomain().getViewers()) {
					if (viewer instanceof FXViewer) {
						if (((FXViewer) viewer).getScene() == scene) {
							IVisualPart<Node> part = ((FXViewer) viewer)
									.getVisualPartMap().get(n);
							if (part != null) {
								targetPart = part;
								break outer;
							}
						}
					}
				}
			}

			if (targetPart == null) {
				return;
			}

			AbstractFXHoverPolicy policy = getToolPolicy(targetPart);
			if (policy != null) {
				policy.hover(event);
			}
		}
	};

	protected AbstractFXHoverPolicy getToolPolicy(IVisualPart<Node> targetPart) {
		return targetPart.getAdapter(TOOL_POLICY_KEY);
	}

	@Override
	protected void registerListeners() {
		for (IViewer<Node> viewer : getDomain().getViewers()) {
			viewer.getRootPart().getVisual().getScene()
					.addEventFilter(MouseEvent.ANY, hoverFilter);
		}
	}

	@Override
	protected void unregisterListeners() {
		for (IViewer<Node> viewer : getDomain().getViewers()) {
			viewer.getRootPart().getVisual().getScene()
					.removeEventFilter(MouseEvent.ANY, hoverFilter);
		}
	}

}
