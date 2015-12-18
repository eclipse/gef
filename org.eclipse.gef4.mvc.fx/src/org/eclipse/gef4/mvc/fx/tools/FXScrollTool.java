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
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.gef4.mvc.fx.policies.AbstractFXOnScrollPolicy;
import org.eclipse.gef4.mvc.fx.viewer.FXViewer;
import org.eclipse.gef4.mvc.parts.IVisualPart;
import org.eclipse.gef4.mvc.viewer.IViewer;

import javafx.event.EventHandler;
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
	// TODO: Rename to ON_SCROLL_POLICY_KEY
	public static final Class<AbstractFXOnScrollPolicy> TOOL_POLICY_KEY = AbstractFXOnScrollPolicy.class;

	private final Map<FXViewer, EventHandler<ScrollEvent>> scrollListeners = new HashMap<>();

	private EventHandler<ScrollEvent> createScrollListener(
			final IViewer<Node> viewer) {
		return new EventHandler<ScrollEvent>() {
			@Override
			public void handle(ScrollEvent event) {
				EventTarget eventTarget = event.getTarget();
				getDomain().openExecutionTransaction(FXScrollTool.this);
				Collection<? extends AbstractFXOnScrollPolicy> policies = getTargetPolicies(
						viewer,
						eventTarget instanceof Node ? (Node) eventTarget : null,
						TOOL_POLICY_KEY);
				for (AbstractFXOnScrollPolicy policy : policies) {
					policy.scroll(event);
				}
				getDomain().closeExecutionTransaction(FXScrollTool.this);
			}
		};
	}

	/**
	 * Returns a {@link Set} containing all {@link AbstractFXOnScrollPolicy}s
	 * that are installed on the given target {@link IVisualPart}.
	 *
	 * @param targetPart
	 *            The target {@link IVisualPart} of which the
	 *            {@link AbstractFXOnScrollPolicy}s are returned.
	 * @return A {@link Set} containing all {@link AbstractFXOnScrollPolicy}s
	 *         that are installed on the given target {@link IVisualPart}.
	 */
	// TODO: Rename to getOnScrollPolicies
	protected Set<? extends AbstractFXOnScrollPolicy> getScrollPolicies(
			IVisualPart<Node, ? extends Node> targetPart) {
		return new HashSet<>(targetPart
				.<AbstractFXOnScrollPolicy> getAdapters(TOOL_POLICY_KEY)
				.values());
	}

	@Override
	protected void registerListeners() {
		super.registerListeners();
		for (IViewer<Node> viewer : getDomain().getViewers().values()) {
			Scene scene = ((FXViewer) viewer).getScene();
			EventHandler<ScrollEvent> scrollListener = createScrollListener(
					viewer);
			scrollListeners.put((FXViewer) viewer, scrollListener);
			scene.addEventFilter(ScrollEvent.SCROLL, scrollListener);
		}
	}

	@Override
	protected void unregisterListeners() {
		for (Map.Entry<FXViewer, EventHandler<ScrollEvent>> e : scrollListeners
				.entrySet()) {
			Scene scene = e.getKey().getScene();
			scene.removeEventFilter(ScrollEvent.SCROLL, e.getValue());
		}
		super.unregisterListeners();
	}

}
