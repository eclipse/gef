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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javafx.event.EventTarget;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;

import org.eclipse.gef4.fx.gestures.FXMouseDragGesture;
import org.eclipse.gef4.geometry.planar.Dimension;
import org.eclipse.gef4.mvc.fx.parts.FXPartUtils;
import org.eclipse.gef4.mvc.fx.policies.AbstractFXClickPolicy;
import org.eclipse.gef4.mvc.fx.policies.AbstractFXDragPolicy;
import org.eclipse.gef4.mvc.fx.viewer.FXViewer;
import org.eclipse.gef4.mvc.parts.IVisualPart;
import org.eclipse.gef4.mvc.policies.IPolicy;
import org.eclipse.gef4.mvc.tools.AbstractTool;
import org.eclipse.gef4.mvc.viewer.IViewer;

public class FXClickDragTool extends AbstractTool<Node> {

	public static final Class<AbstractFXClickPolicy> CLICK_TOOL_POLICY_KEY = AbstractFXClickPolicy.class;
	public static final Class<AbstractFXDragPolicy> DRAG_TOOL_POLICY_KEY = AbstractFXDragPolicy.class;

	private final Map<IViewer<Node>, FXMouseDragGesture> gestures = new HashMap<IViewer<Node>, FXMouseDragGesture>();
	private boolean dragInProgress;
	private final Map<AbstractFXDragPolicy, MouseEvent> pressEvents = new HashMap<AbstractFXDragPolicy, MouseEvent>();
	private Map<EventTarget, IVisualPart<Node, ? extends Node>> interactionTargetOverrides = new HashMap<EventTarget, IVisualPart<Node, ? extends Node>>();

	protected Set<? extends AbstractFXClickPolicy> getClickPolicies(
			IVisualPart<Node, ? extends Node> targetPart) {
		return new HashSet<AbstractFXClickPolicy>(targetPart
				.<AbstractFXClickPolicy> getAdapters(CLICK_TOOL_POLICY_KEY)
				.values());
	}

	protected Set<? extends AbstractFXDragPolicy> getDragPolicies(
			IVisualPart<Node, ? extends Node> targetPart) {
		return new HashSet<AbstractFXDragPolicy>(targetPart
				.<AbstractFXDragPolicy> getAdapters(DRAG_TOOL_POLICY_KEY)
				.values());
	}

	protected <T extends IPolicy<Node>> IVisualPart<Node, ? extends Node> getTargetPart(
			final IViewer<Node> viewer, Node target, Class<T> policy) {
		if (interactionTargetOverrides.containsKey(target)) {
			IVisualPart<Node, ? extends Node> overridingTarget = interactionTargetOverrides
					.get(target);
			if (policy != null
					&& overridingTarget.getAdapters(policy).isEmpty()) {
				return null;
			}
			return overridingTarget;
		}
		return FXPartUtils.getTargetPart(Collections.singleton(viewer), target,
				policy, true);
	}

	public boolean isDragging() {
		return dragInProgress;
	}

	/**
	 * Registers the given {@link IVisualPart} as the target part for all JavaFX
	 * events which are send to the given {@link EventTarget} during the
	 * currently active or next press-drag-release gesture.
	 *
	 * @param target
	 *            The JavaFX {@link EventTarget} for which the given target
	 *            should be used.
	 * @param targetPart
	 *            The {@link IVisualPart} to use as the target for all JavaFX
	 *            events directed at the given {@link EventTarget}.
	 */
	public void overrideTargetForThisInteraction(EventTarget target,
			IVisualPart<Node, ? extends Node> targetPart) {
		interactionTargetOverrides.put(target, targetPart);
	}

	@Override
	protected void registerListeners() {
		super.registerListeners();

		for (final IViewer<Node> viewer : getDomain().getViewers().values()) {
			FXMouseDragGesture gesture = new FXMouseDragGesture() {
				@Override
				protected void drag(Node target, MouseEvent e, double dx,
						double dy) {
					if (!dragInProgress) {
						return;
					}
					IVisualPart<Node, ? extends Node> targetPart = getTargetPart(
							viewer, target, DRAG_TOOL_POLICY_KEY);
					// when no part processes the event, send it to the root
					// part
					if (targetPart == null) {
						targetPart = viewer.getRootPart();
					}
					Collection<? extends AbstractFXDragPolicy> policies = getDragPolicies(targetPart);
					for (AbstractFXDragPolicy policy : policies) {
						policy.drag(e, new Dimension(dx, dy));
					}
				}

				@Override
				protected void press(Node target, MouseEvent e) {
					// do not notify other listeners
					e.consume();

					// click first
					IVisualPart<Node, ? extends Node> clickTargetPart = getTargetPart(
							viewer, target, CLICK_TOOL_POLICY_KEY);
					// when no part processes the event, send it to the root
					// part
					if (clickTargetPart == null) {
						clickTargetPart = viewer.getRootPart();
					}
					Collection<? extends AbstractFXClickPolicy> clickPolicies = getClickPolicies(clickTargetPart);
					getDomain().openExecutionTransaction();
					for (AbstractFXClickPolicy policy : clickPolicies) {
						policy.click(e);
					}
					getDomain().closeExecutionTransaction();

					// drag second, but only for single clicks
					if (e.getClickCount() == 1) {
						IVisualPart<Node, ? extends Node> dragTargetPart = getTargetPart(
								viewer, target, DRAG_TOOL_POLICY_KEY);

						// if no part wants to process the drag event, send it
						// to the root part
						if (dragTargetPart == null) {
							dragTargetPart = viewer.getRootPart();
						}
						Collection<? extends AbstractFXDragPolicy> dragPolicies = getDragPolicies(dragTargetPart);
						getDomain().openExecutionTransaction();
						for (AbstractFXDragPolicy policy : dragPolicies) {
							dragInProgress = true;
							pressEvents.put(policy, e);
							policy.press(e);
						}
					}
				}

				@Override
				protected void release(Node target, MouseEvent e, double dx,
						double dy) {
					if (!dragInProgress) {
						return;
					}
					IVisualPart<Node, ? extends Node> targetPart = getTargetPart(
							viewer, target, DRAG_TOOL_POLICY_KEY);
					// if no part wants to process the event, send it to the
					// root part
					if (targetPart == null) {
						targetPart = viewer.getRootPart();
					}
					Collection<? extends AbstractFXDragPolicy> policies = getDragPolicies(targetPart);
					for (AbstractFXDragPolicy policy : policies) {
						pressEvents.remove(policy);
						policy.release(e, new Dimension(dx, dy));
					}
					getDomain().closeExecutionTransaction();
					dragInProgress = false;
					interactionTargetOverrides.clear();
				}
			};

			gesture.setScene(((FXViewer) viewer).getScene());
			gestures.put(viewer, gesture);
		}
	}

	@Override
	protected void unregisterListeners() {
		for (FXMouseDragGesture gesture : gestures.values()) {
			gesture.setScene(null);
		}
		super.unregisterListeners();
	}

}
