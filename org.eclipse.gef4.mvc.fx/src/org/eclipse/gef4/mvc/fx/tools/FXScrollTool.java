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
import java.util.HashMap;
import java.util.Map;

import org.eclipse.gef4.fx.gestures.AbstractScrollGesture;
import org.eclipse.gef4.mvc.fx.policies.IFXOnScrollPolicy;
import org.eclipse.gef4.mvc.fx.viewer.FXViewer;
import org.eclipse.gef4.mvc.viewer.IViewer;

import javafx.event.EventTarget;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.input.ScrollEvent;

/**
 * The {@link FXScrollTool} is an {@link AbstractFXTool} that handles mouse
 * scroll events.
 *
 * @author mwienand
 *
 */
public class FXScrollTool extends AbstractFXTool {

	/**
	 * The type of the policy that has to be supported by target parts.
	 */
	public static final Class<IFXOnScrollPolicy> ON_SCROLL_POLICY_KEY = IFXOnScrollPolicy.class;

	private final Map<IViewer<Node>, AbstractScrollGesture> gestures = new HashMap<>();

	@Override
	protected void registerListeners() {
		super.registerListeners();
		for (final IViewer<Node> viewer : getDomain().getViewers().values()) {
			Scene scene = ((FXViewer) viewer).getScene();
			AbstractScrollGesture scrollGesture = new AbstractScrollGesture() {
				private Collection<? extends IFXOnScrollPolicy> policies;

				@Override
				protected void scroll(ScrollEvent event) {
					for (IFXOnScrollPolicy policy : policies) {
						policy.scroll(event);
					}
				}

				@Override
				protected void scrollFinished() {
					for (IFXOnScrollPolicy policy : policies) {
						policy.scrollFinished();
					}
					getDomain().closeExecutionTransaction(FXScrollTool.this);
				}

				@Override
				protected void scrollStarted(ScrollEvent event) {
					EventTarget eventTarget = event.getTarget();
					getDomain().openExecutionTransaction(FXScrollTool.this);
					policies = getTargetPolicies(
							viewer, eventTarget instanceof Node
									? (Node) eventTarget : null,
							ON_SCROLL_POLICY_KEY);
					for (IFXOnScrollPolicy policy : policies) {
						policy.scrollStarted(event);
					}
				}
			};
			scrollGesture.setScene(scene);
			gestures.put(viewer, scrollGesture);
		}
	}

	@Override
	protected void unregisterListeners() {
		for (AbstractScrollGesture gesture : gestures.values()) {
			gesture.setScene(null);
		}
		super.unregisterListeners();
	}

}
