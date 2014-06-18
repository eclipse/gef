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

				List<IVisualPart<Node>> targetParts = FXPartUtils
						.getTargetParts(getDomain().getViewers(), event,
								TOOL_POLICY_KEY);
				double deltaY = event.getDeltaY();

				for (IVisualPart<Node> targetPart : targetParts) {
					AbstractFXScrollPolicy policy = getToolPolicy(targetPart);
					if (policy != null) {
						policy.scroll(event, deltaY);
					}
				}
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
