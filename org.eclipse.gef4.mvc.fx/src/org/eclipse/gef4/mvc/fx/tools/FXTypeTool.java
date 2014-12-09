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

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javafx.event.EventHandler;
import javafx.event.EventTarget;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.input.KeyEvent;

import org.eclipse.gef4.mvc.fx.policies.AbstractFXTypePolicy;
import org.eclipse.gef4.mvc.fx.viewer.FXViewer;
import org.eclipse.gef4.mvc.models.FocusModel;
import org.eclipse.gef4.mvc.parts.IVisualPart;
import org.eclipse.gef4.mvc.tools.AbstractTool;
import org.eclipse.gef4.mvc.viewer.IViewer;

public class FXTypeTool extends AbstractTool<Node> {

	public static final Class<AbstractFXTypePolicy> TOOL_POLICY_KEY = AbstractFXTypePolicy.class;

	private final EventHandler<? super KeyEvent> pressedFilter = new EventHandler<KeyEvent>() {
		@Override
		public void handle(KeyEvent event) {
			getDomain().openExecutionTransaction();
			Collection<? extends AbstractFXTypePolicy> policies = getTargetPolicies(event);
			for (AbstractFXTypePolicy policy : policies) {
				policy.pressed(event);
			}
			getDomain().closeExecutionTransaction();
		}
	};

	private final EventHandler<? super KeyEvent> releasedFilter = new EventHandler<KeyEvent>() {
		@Override
		public void handle(KeyEvent event) {
			getDomain().openExecutionTransaction();
			Collection<? extends AbstractFXTypePolicy> policies = getTargetPolicies(event);
			for (AbstractFXTypePolicy policy : policies) {
				policy.released(event);
			}
			getDomain().closeExecutionTransaction();
		}
	};

	protected Set<? extends AbstractFXTypePolicy> getKeyPolicies(
			IVisualPart<Node, ? extends Node> targetPart) {
		return new HashSet<>(targetPart.<AbstractFXTypePolicy> getAdapters(
				TOOL_POLICY_KEY).values());
	}

	protected Set<? extends AbstractFXTypePolicy> getTargetPolicies(
			KeyEvent event) {
		EventTarget target = event.getTarget();
		if (!(target instanceof Node)) {
			return Collections.emptySet();
		}

		Scene scene = ((Node) target).getScene();
		if (scene == null) {
			return Collections.emptySet();
		}

		IVisualPart<Node, ? extends Node> targetPart = null;
		for (IViewer<Node> viewer : getDomain().getViewers().values()) {
			if (viewer instanceof FXViewer) {
				if (((FXViewer) viewer).getScene() == scene) {
					IVisualPart<Node, ? extends Node> part = viewer
							.<FocusModel<Node>> getAdapter(FocusModel.class)
							.getFocused();
					if (part == null) {
						targetPart = viewer.getRootPart();
					} else {
						targetPart = part;
					}
					break;
				}
			}
		}

		if (targetPart == null) {
			return Collections.emptySet();
		}

		return getKeyPolicies(targetPart);
	}

	@Override
	protected void registerListeners() {
		for (IViewer<Node> viewer : getDomain().getViewers().values()) {
			viewer.getRootPart().getVisual().getScene()
					.addEventFilter(KeyEvent.KEY_PRESSED, pressedFilter);
			viewer.getRootPart().getVisual().getScene()
					.addEventFilter(KeyEvent.KEY_RELEASED, releasedFilter);
		}
	}

	@Override
	protected void unregisterListeners() {
		for (IViewer<Node> viewer : getDomain().getViewers().values()) {
			viewer.getRootPart().getVisual().getScene()
					.removeEventFilter(KeyEvent.KEY_PRESSED, pressedFilter);
			viewer.getRootPart().getVisual().getScene()
					.removeEventFilter(KeyEvent.KEY_RELEASED, releasedFilter);
		}
	}

}
