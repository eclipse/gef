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
import java.util.HashSet;
import java.util.Set;

import org.eclipse.gef4.mvc.fx.policies.AbstractFXOnTypePolicy;
import org.eclipse.gef4.mvc.fx.viewer.FXViewer;
import org.eclipse.gef4.mvc.models.FocusModel;
import org.eclipse.gef4.mvc.parts.IVisualPart;
import org.eclipse.gef4.mvc.tools.AbstractTool;
import org.eclipse.gef4.mvc.viewer.IViewer;

import javafx.event.EventHandler;
import javafx.event.EventTarget;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.input.KeyEvent;

public class FXTypeTool extends AbstractTool<Node> {

	// TODO: Rename to ON_TYPE_POLICY_KEY
	public static final Class<AbstractFXOnTypePolicy> TOOL_POLICY_KEY = AbstractFXOnTypePolicy.class;

	private final EventHandler<? super KeyEvent> pressedFilter = new EventHandler<KeyEvent>() {
		@Override
		public void handle(KeyEvent event) {
			getDomain().openExecutionTransaction(FXTypeTool.this);
			Collection<? extends AbstractFXOnTypePolicy> policies = getTargetPolicies(
					event);
			for (AbstractFXOnTypePolicy policy : policies) {
				policy.pressed(event);
			}
		}
	};

	private final EventHandler<? super KeyEvent> releasedFilter = new EventHandler<KeyEvent>() {
		@Override
		public void handle(KeyEvent event) {
			Collection<? extends AbstractFXOnTypePolicy> policies = getTargetPolicies(
					event);
			for (AbstractFXOnTypePolicy policy : policies) {
				policy.released(event);
			}
			getDomain().closeExecutionTransaction(FXTypeTool.this);
		}
	};

	// TODO: Rename to getOnTypePolicies()
	protected Set<? extends AbstractFXOnTypePolicy> getKeyPolicies(
			IVisualPart<Node, ? extends Node> targetPart) {
		return new HashSet<>(
				targetPart.<AbstractFXOnTypePolicy> getAdapters(TOOL_POLICY_KEY)
						.values());
	}

	protected Set<? extends AbstractFXOnTypePolicy> getTargetPolicies(
			KeyEvent event) {
		EventTarget target = event.getTarget();
		if (target instanceof Scene) {
			return getTargetPolicies((Scene) target);
		} else if (target instanceof Node) {
			Scene scene = ((Node) target).getScene();
			if (scene == null) {
				return Collections.emptySet();
			}
			return getTargetPolicies(scene);
		} else {
			return Collections.emptySet();
		}
	}

	protected Set<? extends AbstractFXOnTypePolicy> getTargetPolicies(
			Scene scene) {
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
			Scene scene = viewer.getRootPart().getVisual().getScene();
			scene.addEventFilter(KeyEvent.KEY_PRESSED, pressedFilter);
			scene.addEventFilter(KeyEvent.KEY_RELEASED, releasedFilter);
		}
	}

	@Override
	protected void unregisterListeners() {
		for (IViewer<Node> viewer : getDomain().getViewers().values()) {
			Scene scene = viewer.getRootPart().getVisual().getScene();
			scene.removeEventFilter(KeyEvent.KEY_PRESSED, pressedFilter);
			scene.removeEventFilter(KeyEvent.KEY_RELEASED, releasedFilter);
		}
	}

}
