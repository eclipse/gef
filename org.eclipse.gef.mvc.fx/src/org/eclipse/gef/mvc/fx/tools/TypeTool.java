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

import org.eclipse.gef.mvc.fx.models.FocusModel;
import org.eclipse.gef.mvc.fx.parts.PartUtils;
import org.eclipse.gef.mvc.fx.policies.IOnStrokePolicy;
import org.eclipse.gef.mvc.fx.policies.IOnTypePolicy;
import org.eclipse.gef.mvc.fx.viewer.IViewer;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.event.EventTarget;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

/**
 * The {@link TypeTool} is an {@link AbstractTool} that handles keyboard input.
 *
 * @author mwienand
 *
 */
public class TypeTool extends AbstractTool {

	/**
	 * The type of the policy that has to be supported by target parts.
	 */
	public static final Class<IOnTypePolicy> ON_TYPE_POLICY_KEY = IOnTypePolicy.class;

	/**
	 * The type of the policy that has to be supported by target parts.
	 */
	public static final Class<IOnStrokePolicy> ON_STROKE_POLICY_KEY = IOnStrokePolicy.class;

	private Map<Scene, EventHandler<? super KeyEvent>> pressedFilterMap = new IdentityHashMap<>();
	private Map<Scene, EventHandler<? super KeyEvent>> releasedFilterMap = new IdentityHashMap<>();
	private Map<Scene, EventHandler<? super KeyEvent>> typedFilterMap = new IdentityHashMap<>();
	private Map<IViewer, ChangeListener<Boolean>> viewerFocusChangeListeners = new HashMap<>();

	private IViewer activeViewer;

	@SuppressWarnings("unchecked")
	@Override
	public List<? extends IOnStrokePolicy> getActivePolicies(IViewer viewer) {
		return (List<IOnStrokePolicy>) super.getActivePolicies(viewer);
	}

	@Override
	protected void doActivate() {
		for (final IViewer viewer : getDomain().getViewers().values()) {
			// check if we have access to a FocusModel
			FocusModel focusModel = viewer.getAdapter(FocusModel.class);
			if (focusModel == null) {
				throw new IllegalStateException("Cannot find FocusModel.");
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
					for (IViewer v : getDomain().getViewers().values()) {
						if (v.isViewerFocused()) {
							return;
						}
					}
					// cancel target policies
					for (IOnStrokePolicy policy : getActivePolicies(
							activeViewer)) {
						policy.abortPress();
					}
					// clear active policies
					clearActivePolicies(activeViewer);
					activeViewer = null;
					// close execution transaction
					getDomain().closeExecutionTransaction(TypeTool.this);
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
					boolean isInitialPress = false;
					if (pressedKeys.isEmpty()) {
						// determine viewer that contains the given target part
						Node targetNode = null;
						EventTarget target = event.getTarget();
						if (target instanceof Node) {
							targetNode = (Node) target;
							activeViewer = PartUtils.retrieveViewer(getDomain(),
									targetNode);
						} else if (target instanceof Scene) {
							// first focused viewer in that scene
							for (IViewer v : getDomain().getViewers()
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
						getDomain().openExecutionTransaction(TypeTool.this);
						isInitialPress = true;

						// determine target policies on first key press
						setActivePolicies(activeViewer,
								getTargetPolicyResolver().getTargetPolicies(
										TypeTool.this, targetNode,
										ON_STROKE_POLICY_KEY));
					}

					// store initially pressed key
					pressedKeys.add(event.getCode());

					// notify target policies
					for (IOnStrokePolicy policy : getActivePolicies(
							activeViewer)) {
						if (isInitialPress) {
							policy.initialPress(event);
						} else {
							policy.press(event);
						}
					}
				}
			};
			pressedFilterMap.put(scene, pressedFilter);

			EventHandler<KeyEvent> releasedFilter = new EventHandler<KeyEvent>() {
				@Override
				public void handle(KeyEvent event) {
					boolean isFinalRelease = pressedKeys.size() == 1
							&& pressedKeys.contains(event.getCode());

					// notify target policies
					for (IOnStrokePolicy policy : getActivePolicies(
							activeViewer)) {
						if (isFinalRelease) {
							policy.finalRelease(event);
						} else {
							policy.release(event);
						}
					}

					// check if the last pressed key is released now
					if (isFinalRelease) {
						// clear active policies and close execution transaction
						// only when the initially pressed key is released
						clearActivePolicies(activeViewer);
						activeViewer = null;
						getDomain().closeExecutionTransaction(TypeTool.this);
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
						getDomain().openExecutionTransaction(TypeTool.this);
					}

					// determine viewer that contains the given target part
					EventTarget target = event.getTarget();
					Node targetNode = null;
					if (target instanceof Node) {
						targetNode = (Node) target;
					} else if (target instanceof Scene) {
						// first focused viewer in that scene
						for (IViewer v : getDomain().getViewers().values()) {
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

					Collection<? extends IOnTypePolicy> policies = getTargetPolicyResolver()
							.getTargetPolicies(TypeTool.this, targetNode,
									ON_TYPE_POLICY_KEY);
					// active policies are unnecessary because TYPED is not a
					// gesture, just one event at one point in time
					for (IOnTypePolicy policy : policies) {
						policy.type(event, pressedKeys);
					}
					if (pressedKeys.isEmpty()) {
						getDomain().closeExecutionTransaction(TypeTool.this);
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
	protected void doDeactivate() {
		for (IViewer viewer : getDomain().getViewers().values()) {
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
