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

import javafx.event.EventHandler;
import javafx.event.EventTarget;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.input.ScrollEvent;

import org.eclipse.gef4.mvc.fx.parts.FXPartUtils;
import org.eclipse.gef4.mvc.fx.policies.AbstractFXScrollPolicy;
import org.eclipse.gef4.mvc.fx.viewer.FXViewer;
import org.eclipse.gef4.mvc.parts.IVisualPart;
import org.eclipse.gef4.mvc.tools.AbstractTool;
import org.eclipse.gef4.mvc.viewer.IViewer;

public class FXScrollTool extends AbstractTool<Node> {

	public static final Class<AbstractFXScrollPolicy> TOOL_POLICY_KEY = AbstractFXScrollPolicy.class;

	private final EventHandler<ScrollEvent> scrollListener = new EventHandler<ScrollEvent>() {
		@Override
		public void handle(ScrollEvent event) {
			if (event.isControlDown()) {
				event.consume();

				EventTarget target = event.getTarget();
				if (!(target instanceof Node)) {
					return;
				}

				Node targetNode = (Node) target;
				IVisualPart<Node> targetPart = FXPartUtils.getTargetPart(
						getDomain().getViewers(), targetNode, TOOL_POLICY_KEY);
				if (targetPart == null) {
					return;
				}

				AbstractFXScrollPolicy policy = getToolPolicy(targetPart);
				if (policy == null) {
					throw new IllegalStateException(
							"Target part does not support required policy!");
				}

				policy.scroll(event, event.getDeltaY());
			}
		}
	};

	protected AbstractFXScrollPolicy getToolPolicy(IVisualPart<Node> targetPart) {
		return targetPart.getAdapter(TOOL_POLICY_KEY);
	}

	@Override
	protected void registerListeners() {
		super.registerListeners();

		for (IViewer<Node> viewer : getDomain().getViewers()) {
			Scene scene = ((FXViewer) viewer).getScene();
			scene.addEventFilter(ScrollEvent.SCROLL, scrollListener);
		}
	}

	@Override
	protected void unregisterListeners() {
		for (IViewer<Node> viewer : getDomain().getViewers()) {
			Scene scene = ((FXViewer) viewer).getScene();
			scene.removeEventFilter(ScrollEvent.SCROLL, scrollListener);
		}

		super.unregisterListeners();
	}

}
