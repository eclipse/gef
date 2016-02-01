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
import java.util.HashMap;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.gef4.mvc.fx.policies.IFXOnTypePolicy;
import org.eclipse.gef4.mvc.fx.viewer.FXViewer;
import org.eclipse.gef4.mvc.models.FocusModel;
import org.eclipse.gef4.mvc.parts.IVisualPart;
import org.eclipse.gef4.mvc.tools.AbstractTool;
import org.eclipse.gef4.mvc.viewer.IViewer;

import com.google.common.reflect.TypeToken;

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

	private Map<IViewer<Node>, EventHandler<? super KeyEvent>> pressedFilterMap = new IdentityHashMap<>();
	private Map<IViewer<Node>, EventHandler<? super KeyEvent>> releasedFilterMap = new IdentityHashMap<>();
	private Map<IViewer<Node>, EventHandler<? super KeyEvent>> typedFilterMap = new IdentityHashMap<>();
	private Map<IViewer<Node>, ChangeListener<Boolean>> viewerFocusChangeListeners = new HashMap<>();

	@SuppressWarnings("unchecked")
	@Override
	public List<? extends IFXOnTypePolicy> getActivePolicies(
			IViewer<Node> viewer) {
		return (List<IFXOnTypePolicy>) super.getActivePolicies(viewer);
	}

	/**
	 * Returns a {@link Set} containing all {@link IFXOnTypePolicy}s that are
	 * installed on the given target {@link IVisualPart}.
	 *
	 * @param targetPart
	 *            The target {@link IVisualPart} of which the
	 *            {@link IFXOnTypePolicy}s are returned.
	 * @return A {@link Set} containing all {@link IFXOnTypePolicy}s that are
	 *         installed on the given target {@link IVisualPart}.
	 */
	// TODO: Rename to getOnTypePolicies()
	protected Set<? extends IFXOnTypePolicy> getKeyPolicies(
			IVisualPart<Node, ? extends Node> targetPart) {
		return new HashSet<>(targetPart
				.<IFXOnTypePolicy> getAdapters(ON_TYPE_POLICY_KEY).values());
	}

	/**
	 * Returns a {@link Set} containing all {@link IFXOnTypePolicy}s that are
	 * installed on the target {@link IVisualPart} for the given
	 * {@link KeyEvent}. The target {@link IVisualPart} is determined by using
	 * {@link #getTargetPolicies(Scene)}.
	 *
	 * @param event
	 *            The {@link KeyEvent} to transfer.
	 * @return A {@link Set} containing all {@link IFXOnTypePolicy}s that are
	 *         installed on the target {@link IVisualPart} for the given
	 *         {@link KeyEvent}.
	 */
	protected Set<? extends IFXOnTypePolicy> getTargetPolicies(KeyEvent event) {
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

	/**
	 * Returns a {@link Set} containing all {@link IFXOnTypePolicy}s that are
	 * installed on the target {@link IVisualPart} for the given {@link Scene}.
	 * If an {@link IVisualPart} within the given {@link Scene} has keyboard
	 * focus, that part is used as the target part. Otherwise, the root part of
	 * the {@link IViewer} that is rendered in the given {@link Scene} is used
	 * as the target part.
	 *
	 * @param scene
	 *            The {@link Scene} for which to determine the
	 *            {@link IFXOnTypePolicy}s that are installed on the target
	 *            {@link IVisualPart}.
	 * @return A {@link Set} containing all {@link IFXOnTypePolicy}s that are
	 *         installed on the target {@link IVisualPart} for the given
	 *         {@link Scene}.
	 */
	@SuppressWarnings("serial")
	protected Set<? extends IFXOnTypePolicy> getTargetPolicies(Scene scene) {
		IVisualPart<Node, ? extends Node> targetPart = null;
		for (IViewer<Node> viewer : getDomain().getViewers().values()) {
			if (viewer instanceof FXViewer) {
				if (((FXViewer) viewer).getScene() == scene) {
					FocusModel<Node> focusModel = viewer
							.getAdapter(new TypeToken<FocusModel<Node>>() {
							});
					if (focusModel == null) {
						throw new IllegalStateException(
								"Cannot find FocusModel<Node>.");
					}
					IVisualPart<Node, ? extends Node> part = focusModel
							.getFocus();
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
					if (newValue == null || !newValue) {
						if (pressedKeys.isEmpty()) {
							return;
						}
						// cancel target policies
						for (IFXOnTypePolicy policy : getActivePolicies(
								viewer)) {
							policy.unfocus();
						}
						// clear active policies and close execution
						// transaction
						clearActivePolicies(viewer);
						getDomain().closeExecutionTransaction(FXTypeTool.this);
						// unset pressed keys
						pressedKeys.clear();
					}

				}
			};
			viewer.viewerFocusedProperty()
					.addListener(viewerFocusChangeListener);
			viewerFocusChangeListeners.put(viewer, viewerFocusChangeListener);

			// generate event handlers
			EventHandler<KeyEvent> pressedFilter = new EventHandler<KeyEvent>() {
				@Override
				public void handle(KeyEvent event) {
					// store initially pressed key
					// System.out.println("pressed " + event);
					if (pressedKeys.isEmpty()) {
						// open exec tx on first key press
						getDomain().openExecutionTransaction(FXTypeTool.this);
						// determine target policies on first key press
						setActivePolicies(viewer, getTargetPolicies(event));
					}
					pressedKeys.add(event.getCode());

					// notify target policies
					for (IFXOnTypePolicy policy : getActivePolicies(viewer)) {
						policy.pressed(event);
					}
				}
			};
			pressedFilterMap.put(viewer, pressedFilter);

			EventHandler<KeyEvent> releasedFilter = new EventHandler<KeyEvent>() {
				@Override
				public void handle(KeyEvent event) {
					// System.out.println("released " + event);

					// notify target policies
					for (IFXOnTypePolicy policy : getActivePolicies(viewer)) {
						policy.released(event);
					}

					// check if the last pressed key is released now
					if (pressedKeys.size() == 1
							&& pressedKeys.contains(event.getCode())) {
						// clear active policies and close execution transaction
						// only when the initially pressed key is released
						clearActivePolicies(viewer);
						getDomain().closeExecutionTransaction(FXTypeTool.this);
					}
					pressedKeys.remove(event.getCode());
				}
			};
			releasedFilterMap.put(viewer, releasedFilter);

			EventHandler<KeyEvent> typedFilter = new EventHandler<KeyEvent>() {
				@Override
				public void handle(KeyEvent event) {
					// System.out.println("typed " + event);
					if (pressedKeys.isEmpty()) {
						getDomain().openExecutionTransaction(FXTypeTool.this);
					}
					Collection<? extends IFXOnTypePolicy> policies = getTargetPolicies(
							event);
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
			typedFilterMap.put(viewer, typedFilter);

			Scene scene = viewer.getRootPart().getVisual().getScene();
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
			scene.removeEventFilter(KeyEvent.KEY_PRESSED,
					pressedFilterMap.remove(viewer));
			scene.removeEventFilter(KeyEvent.KEY_RELEASED,
					releasedFilterMap.remove(viewer));
			scene.removeEventFilter(KeyEvent.KEY_TYPED,
					typedFilterMap.remove(viewer));
		}
	}

}
