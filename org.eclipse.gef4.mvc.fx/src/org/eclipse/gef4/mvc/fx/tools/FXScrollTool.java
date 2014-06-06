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
import org.eclipse.gef4.mvc.fx.viewer.IFXViewer;
import org.eclipse.gef4.mvc.parts.IVisualPart;
import org.eclipse.gef4.mvc.policies.IPolicy;
import org.eclipse.gef4.mvc.policies.IScrollPolicy;
import org.eclipse.gef4.mvc.tools.AbstractTool;

public class FXScrollTool extends AbstractTool<Node> {

	@SuppressWarnings("rawtypes")
	public static final Class<? extends IPolicy> TOOL_POLICY_KEY = IScrollPolicy.class;

	public FXScrollTool() {
	}

	@SuppressWarnings({ "unchecked" })
	protected IScrollPolicy<Node> getToolPolicy(IVisualPart<Node> targetPart) {
		return (IScrollPolicy<Node>) targetPart.getAdapter(TOOL_POLICY_KEY);
	}

	private Scene scene;

	private EventHandler<ScrollEvent> scrollListener = new EventHandler<ScrollEvent>() {
		@SuppressWarnings({ "unchecked" })
		@Override
		public void handle(ScrollEvent event) {
			if (event.isControlDown()) {
				event.consume();

				// TODO: create IScrollPolicy, search for it here, etc.
				// currently ZoomOnScrollPolicy is always used
				List<IVisualPart<Node>> targetParts = FXPartUtils
						.getTargetParts(getDomain().getViewer(), event,
								(Class<IPolicy<Node>>) TOOL_POLICY_KEY);
				double deltaY = event.getDeltaY();

				for (IVisualPart<Node> targetPart : targetParts) {
					IScrollPolicy<Node> policy = getToolPolicy(targetPart);
					if (policy != null) {
						policy.scroll(deltaY);
					}
				}
			}
		}
	};

	@Override
	public void activate() {
		super.activate();
		if (scene != null) {
			doRegisterListeners();
		}
	}

	@Override
	public void deactivate() {
		if (scene != null) {
			doUnregisterListeners();
		}
		super.deactivate();
	}

	@Override
	protected void registerListeners() {
		super.registerListeners();
		scene = ((IFXViewer) getDomain().getViewer()).getScene();
	}

	@Override
	protected void unregisterListeners() {
		if (scene != null) {
			doUnregisterListeners();
		}
		super.unregisterListeners();
	}

	private void doRegisterListeners() {
		scene.addEventFilter(ScrollEvent.SCROLL, scrollListener);
	}

	private void doUnregisterListeners() {
		scene.removeEventFilter(ScrollEvent.SCROLL, scrollListener);
	}

}
