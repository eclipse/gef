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

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.gef.mvc.fx.parts.FXPartUtils;
import org.eclipse.gef.mvc.fx.policies.IFXOnTypePolicy;
import org.eclipse.gef.mvc.models.FocusModel;
import org.eclipse.gef.mvc.tools.AbstractTool;
import org.eclipse.gef.mvc.viewer.IViewer;

import com.google.common.reflect.TypeToken;
import com.google.inject.Inject;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.event.EventTarget;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

/**
 * The {@link FXTypeTool} is an {@link AbstractTool} that handles keyboard
 * input.
 *
 * @author mwienand
 *
 */
public class FXTypeTool extends AbstractTool<Node> {

	/**
	 * The type of the policy that has to be supported by target parts.
	 */
	public static final Class<IFXOnTypePolicy> ON_TYPE_POLICY_KEY = IFXOnTypePolicy.class;

	private Map<Scene, EventHandler<? super KeyEvent>> pressedFilterMap = new IdentityHashMap<>();
	private Map<Scene, EventHandler<? super KeyEvent>> releasedFilterMap = new IdentityHashMap<>();
	private Map<Scene, EventHandler<? super KeyEvent>> typedFilterMap = new IdentityHashMap<>();
	private Map<IViewer<Node>, ChangeListener<Boolean>> viewerFocusChangeListeners = new HashMap<>();

	private IViewer<Node> activeViewer;

	@Inject
	private ITargetPolicyResolver targetPolicyResolver;

	@SuppressWarnings("unchecked")
	@Override
	public List<? extends IFXOnTypePolicy> getActivePolicies(
			IViewer<Node> viewer) {
		return (List<IFXOnTypePolicy>) super.getActivePolicies(viewer);
	}

	@SuppressWarnings("serial")
	@Override
	protected void registerListeners() {
		for (final IViewer<Node> viewer : getDomain().getViewers().values()) {
			// check if we have access to a FocusModel<Node>
			FocusModel<Node> focusModel = viewer
					.getAdapter(new TypeToken<FocusModel<Node>>() {
					});
			if (focusModel == null) {
				throw new IllegalStateException(
						"Cannot find FocusModel<Node>.");
			}

			// store the key that is initially pressed so that we can wait for
			// it to be released
			final Set<KeyCode> pressedKeys = new HashSet<>();

			// register a viewer focus change listener to release the initially
			// pressed key when the window loses focus
			ChangeListener<Boolean> viewerFocusChangeListener = new ChangeListener<Boolean>() {
				@Override
				public void changed(
						ObservableValue<? extends Boolean> observable,
						Boolean oldValue, Boolean newValue) {
					// cannot abort if no activeViewer
					if (activeViewer == null) {
						return;
					}
					// check if any viewer is focused
					for (IViewer<Node> v : getDomain().getViewers().values()) {
						if (v.isViewerFocused()) {
							return;
						}
					}
					// cancel target policies
					for (IFXOnTypePolicy policy : getActivePolicies(
							activeViewer)) {
						policy.unfocus();
					}
					// clear active policies
					clearActivePolicies(activeViewer);
					activeViewer = null;
					// close execution transaction
					getDomain().closeExecutionTransaction(FXTypeTool.this);
					// unset pressed keys
					pressedKeys.clear();
				}
			};
			viewer.viewerFocusedProperty()
					.addListener(viewerFocusChangeListener);
			viewerFocusChangeListeners.put(viewer, viewerFocusChangeListener);

			Scene scene = viewer.getRootPart().getVisual().getScene();
			if (pressedFilterMap.containsKey(scene)) {
				continue;
			}

			// generate event handlers
			EventHandler<KeyEvent> pressedFilter = new EventHandler<KeyEvent>() {
				@Override
				public void handle(KeyEvent event) {
					if (pressedKeys.isEmpty()) {
						// determine viewer that contains the given target part
						Node targetNode = null;
						EventTarget target = event.getTarget();
						if (target instanceof Node) {
							targetNode = (Node) target;
							activeViewer = FXPartUtils
									.retrieveViewer(getDomain(), targetNode);
						} else if (target instanceof Scene) {
							// first focused viewer in that scene
							for (IViewer<Node> v : getDomain().getViewers()
									.values()) {
								if (v.getRootPart().getVisual()
										.getScene() == target) {
									if (v.isViewerFocused()) {
										activeViewer = v;
										break;
									}
								}
							}
							if (activeViewer != null) {
								targetNode = activeViewer.getRootPart()
										.getVisual();
							}
						} else {
							throw new IllegalStateException(
									"Unsupported event target: " + target);
						}

						if (activeViewer == null) {
							// no focused viewer could be found for the target
							// scene
							return;
						}

						// open execution transaction
						getDomain().openExecutionTransaction(FXTypeTool.this);

						// determine target policies on first key press
						setActivePolicies(activeViewer,
								targetPolicyResolver.getTargetPolicies(
										FXTypeTool.this, targetNode,
										ON_TYPE_POLICY_KEY));
					}

					// store initially pressed key
					pressedKeys.add(event.getCode());

					// notify target policies
					for (IFXOnTypePolicy policy : getActivePolicies(
							activeViewer)) {
						policy.pressed(event);
					}
				}
			};
			pressedFilterMap.put(scene, pressedFilter);

			EventHandler<KeyEvent> releasedFilter = new EventHandler<KeyEvent>() {
				@Override
				public void handle(KeyEvent event) {
					// notify target policies
					for (IFXOnTypePolicy policy : getActivePolicies(
							activeViewer)) {
						policy.released(event);
					}

					// check if the last pressed key is released now
					if (pressedKeys.size() == 1
							&& pressedKeys.contains(event.getCode())) {
						// clear active policies and close execution transaction
						// only when the initially pressed key is released
						clearActivePolicies(activeViewer);
						activeViewer = null;
						getDomain().closeExecutionTransaction(FXTypeTool.this);
					}
					pressedKeys.remove(event.getCode());
				}
			};
			releasedFilterMap.put(scene, releasedFilter);

			EventHandler<KeyEvent> typedFilter = new EventHandler<KeyEvent>() {
				@Override
				public void handle(KeyEvent event) {
					// System.out.println("typed " + event);
					if (pressedKeys.isEmpty()) {
						getDomain().openExecutionTransaction(FXTypeTool.this);
					}

					// determine viewer that contains the given target part
					EventTarget target = event.getTarget();
					Node targetNode = null;
					if (target instanceof Node) {
						targetNode = (Node) target;
					} else if (target instanceof Scene) {
						// first focused viewer in that scene
						for (IViewer<Node> v : getDomain().getViewers()
								.values()) {
							if (v.getRootPart().getVisual()
									.getScene() == target) {
								if (v.isViewerFocused()) {
									targetNode = v.getRootPart().getVisual();
									break;
								}
							}
						}
					} else {
						throw new IllegalStateException(
								"Unsupported event target: " + target);
					}

					Collection<? extends IFXOnTypePolicy> policies = targetPolicyResolver
							.getTargetPolicies(FXTypeTool.this, targetNode,
									ON_TYPE_POLICY_KEY);
					// active policies are unnecessary because TYPED is not a
					// gesture, just one event at one point in time
					for (IFXOnTypePolicy policy : policies) {
						policy.typed(event);
					}
					if (pressedKeys.isEmpty()) {
						getDomain().closeExecutionTransaction(FXTypeTool.this);
					}
				}
			};
			typedFilterMap.put(scene, typedFilter);

			scene.addEventFilter(KeyEvent.KEY_PRESSED, pressedFilter);
			scene.addEventFilter(KeyEvent.KEY_RELEASED, releasedFilter);
			scene.addEventFilter(KeyEvent.KEY_TYPED, typedFilter);
		}
	}

	@Override
	protected void unregisterListeners() {
		for (IViewer<Node> viewer : getDomain().getViewers().values()) {
			viewer.viewerFocusedProperty()
					.removeListener(viewerFocusChangeListeners.remove(viewer));
			Scene scene = viewer.getRootPart().getVisual().getScene();
			if (pressedFilterMap.containsKey(scene)) {
				scene.removeEventFilter(KeyEvent.KEY_PRESSED,
						pressedFilterMap.remove(scene));
			}
			if (releasedFilterMap.containsKey(scene)) {
				scene.removeEventFilter(KeyEvent.KEY_RELEASED,
						releasedFilterMap.remove(scene));
			}
			if (typedFilterMap.containsKey(scene)) {
				scene.removeEventFilter(KeyEvent.KEY_TYPED,
						typedFilterMap.remove(scene));
			}
		}
	}

}
