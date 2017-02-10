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

import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.gef.fx.gestures.AbstractScrollGesture;
import org.eclipse.gef.mvc.fx.policies.IOnScrollPolicy;
import org.eclipse.gef.mvc.fx.viewer.IViewer;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventTarget;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.input.ScrollEvent;

/**
 * The {@link ScrollTool} is an {@link AbstractTool} that handles mouse scroll
 * events.
 *
 * @author mwienand
 *
 */
public class ScrollTool extends AbstractTool {
	/**
	 * The type of the policy that has to be supported by target parts.
	 */
	public static final Class<IOnScrollPolicy> ON_SCROLL_POLICY_KEY = IOnScrollPolicy.class;

	private final Map<Scene, AbstractScrollGesture> gestures = new IdentityHashMap<>();
	private final Map<IViewer, ChangeListener<Boolean>> viewerFocusChangeListeners = new IdentityHashMap<>();

	@Override
	protected void doActivate() {
		super.doActivate();
		for (final IViewer viewer : getDomain().getViewers().values()) {
			// register a viewer focus change listener
			ChangeListener<Boolean> viewerFocusChangeListener = new ChangeListener<Boolean>() {
				@Override
				public void changed(
						ObservableValue<? extends Boolean> observable,
						Boolean oldValue, Boolean newValue) {
					if (newValue == null || !newValue) {
						// cancel target policies
						for (IOnScrollPolicy policy : getActivePolicies(
								viewer)) {
							policy.abortScroll();
						}
						// clear active policies and close execution
						// transaction
						clearActivePolicies(viewer);
						getDomain().closeExecutionTransaction(ScrollTool.this);
					}

				}
			};
			viewer.viewerFocusedProperty()
					.addListener(viewerFocusChangeListener);
			viewerFocusChangeListeners.put(viewer, viewerFocusChangeListener);

			Scene scene = viewer.getCanvas().getScene();
			if (gestures.containsKey(scene)) {
				continue;
			}

			// register scrolling listener
			AbstractScrollGesture scrollGesture = new AbstractScrollGesture() {
				@Override
				protected void scroll(ScrollEvent event) {
					for (IOnScrollPolicy policy : getActivePolicies(viewer)) {
						policy.scroll(event);
					}
				}

				@Override
				protected void scrollFinished() {
					for (IOnScrollPolicy policy : getActivePolicies(viewer)) {
						policy.endScroll();
					}
					clearActivePolicies(viewer);
					getDomain().closeExecutionTransaction(ScrollTool.this);
				}

				@Override
				protected void scrollStarted(ScrollEvent event) {
					EventTarget eventTarget = event.getTarget();
					getDomain().openExecutionTransaction(ScrollTool.this);
					setActivePolicies(viewer,
							getTargetPolicyResolver().getTargetPolicies(
									ScrollTool.this,
									eventTarget instanceof Node
											? (Node) eventTarget : null,
									ON_SCROLL_POLICY_KEY));
					for (IOnScrollPolicy policy : getActivePolicies(viewer)) {
						policy.startScroll(event);
					}
				}
			};
			scrollGesture.setScene(scene);
			gestures.put(scene, scrollGesture);
		}
	}

	@Override
	protected void doDeactivate() {
		for (Scene scene : new ArrayList<>(gestures.keySet())) {
			gestures.remove(scene).setScene(null);
		}
		for (final IViewer viewer : new ArrayList<>(
				viewerFocusChangeListeners.keySet())) {
			viewer.viewerFocusedProperty()
					.removeListener(viewerFocusChangeListeners.remove(viewer));
		}
		super.doDeactivate();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<? extends IOnScrollPolicy> getActivePolicies(IViewer viewer) {
		return (List<IOnScrollPolicy>) super.getActivePolicies(viewer);
	}
}
