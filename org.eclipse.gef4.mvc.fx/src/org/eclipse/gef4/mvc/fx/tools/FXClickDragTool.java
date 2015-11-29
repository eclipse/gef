/*******************************************************************************
 * Copyright (c) 2014, 2015 itemis AG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Matthias Wienand (itemis AG) - initial API and implementation
 *     Alexander Nyßen (itemis AG) - refactorings
 *
 *******************************************************************************/
package org.eclipse.gef4.mvc.fx.tools;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.gef4.fx.gestures.AbstractMouseDragGesture;
import org.eclipse.gef4.geometry.planar.Dimension;
import org.eclipse.gef4.mvc.fx.domain.FXDomain;
import org.eclipse.gef4.mvc.fx.parts.FXPartUtils;
import org.eclipse.gef4.mvc.fx.policies.AbstractFXOnClickPolicy;
import org.eclipse.gef4.mvc.fx.policies.AbstractFXOnDragPolicy;
import org.eclipse.gef4.mvc.fx.viewer.FXViewer;
import org.eclipse.gef4.mvc.parts.IVisualPart;
import org.eclipse.gef4.mvc.policies.IPolicy;
import org.eclipse.gef4.mvc.tools.AbstractTool;
import org.eclipse.gef4.mvc.tools.ITool;
import org.eclipse.gef4.mvc.viewer.IViewer;

import javafx.scene.Node;
import javafx.scene.input.MouseEvent;

/**
 * An {@link ITool} to handle click/drag interaction gestures.
 * <p>
 * As click and drag are 'overlapping' gestures (a click is part of each drag,
 * which is composed out of click, drag, and release), these are handled
 * together here, even while distinct interaction policies will be queried to
 * handle the respective gesture parts.
 * <p>
 * During each click/drag interaction, the tool identifies respective
 * {@link IVisualPart}s that serve as interaction targets for click and drag
 * respectively. They are identified via hit-testing on the visuals and the
 * availability of a corresponding {@link AbstractFXOnClickPolicy} or
 * {@link AbstractFXOnDragPolicy} (see
 * {@link #getTargetPart(IViewer, Node, Class)}).
 * <p>
 * The {@link FXClickDragTool} handles the opening and closing of an transaction
 * operation via the {@link FXDomain}, to which it is adapted. It controls that
 * a single transaction operation is used for the complete interaction
 * (including the click and potential drag part), so all interaction results can
 * be undone in a single undo step.
 *
 * @author mwienand
 * @author anyssen
 *
 */
public class FXClickDragTool extends AbstractTool<Node> {

	/**
	 * The typeKey used to retrieve those policies that are able to handle the
	 * click part of the click/drag interaction gesture.
	 */
	// TODO: Rename to ON_CLICK_POLICY_KEY
	public static final Class<AbstractFXOnClickPolicy> CLICK_TOOL_POLICY_KEY = AbstractFXOnClickPolicy.class;
	/**
	 * The typeKey used to retrieve those policies that are able to handle the
	 * drag part of the click/drag interaction gesture.
	 */
	// TODO: Rename to ON_DRAG_POLICY_KEY
	public static final Class<AbstractFXOnDragPolicy> DRAG_TOOL_POLICY_KEY = AbstractFXOnDragPolicy.class;

	private final Map<IViewer<Node>, AbstractMouseDragGesture> gestures = new HashMap<>();
	private boolean dragInProgress;
	private final Map<AbstractFXOnDragPolicy, MouseEvent> pressEvents = new HashMap<>();

	/**
	 * Returns a {@link Set} containing all {@link AbstractFXOnClickPolicy}s of
	 * the given target {@link IVisualPart}.
	 *
	 * @param targetPart
	 *            The {@link IVisualPart} of which the
	 *            {@link AbstractFXOnClickPolicy}s are returned.
	 * @return A {@link Set} containing all {@link AbstractFXOnClickPolicy}s of
	 *         the given target {@link IVisualPart}.
	 */
	// TODO: Rename to getOnClickPolicies()
	protected Set<? extends AbstractFXOnClickPolicy> getClickPolicies(
			IVisualPart<Node, ? extends Node> targetPart) {
		return new HashSet<>(targetPart
				.<AbstractFXOnClickPolicy> getAdapters(CLICK_TOOL_POLICY_KEY)
				.values());
	}

	/**
	 * Returns a {@link Set} containing all {@link AbstractFXOnDragPolicy}s of
	 * the given target {@link IVisualPart}.
	 *
	 * @param targetPart
	 *            The {@link IVisualPart} of which the
	 *            {@link AbstractFXOnDragPolicy}s are returned.
	 * @return A {@link Set} containing all {@link AbstractFXOnDragPolicy}s of
	 *         the given target {@link IVisualPart}.
	 */
	// TODO: Rename to getOnDragPolicies()
	protected Set<? extends AbstractFXOnDragPolicy> getDragPolicies(
			IVisualPart<Node, ? extends Node> targetPart) {
		return new HashSet<>(targetPart
				.<AbstractFXOnDragPolicy> getAdapters(DRAG_TOOL_POLICY_KEY)
				.values());
	}

	/**
	 * Returns the target {@link IVisualPart} for the given target {@link Node}
	 * within the given {@link IViewer} that supports the given <i>policy</i>.
	 *
	 * @param <T>
	 *            The type of the policy that has to be supported.
	 * @param viewer
	 *            The {@link IViewer} which is searched for the target
	 *            {@link IVisualPart}.
	 * @param target
	 *            The target {@link Node} that received the input event.
	 * @param policy
	 *            The {@link Class} of the policy that has to be supported.
	 * @return The target {@link IVisualPart} that was determined.
	 */
	protected <T extends IPolicy<Node>> IVisualPart<Node, ? extends Node> getTargetPart(
			final IViewer<Node> viewer, Node target, Class<T> policy) {
		return FXPartUtils.getTargetPart(Collections.singleton(viewer), target,
				policy, true);
	}

	@Override
	protected void registerListeners() {
		super.registerListeners();

		for (final IViewer<Node> viewer : getDomain().getViewers().values()) {
			AbstractMouseDragGesture gesture = new AbstractMouseDragGesture() {
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
					Collection<? extends AbstractFXOnDragPolicy> policies = getDragPolicies(
							targetPart);
					for (AbstractFXOnDragPolicy policy : policies) {
						policy.drag(e, new Dimension(dx, dy));
					}
				}

				@Override
				protected void press(Node target, MouseEvent e) {
					// click first
					IVisualPart<Node, ? extends Node> clickTargetPart = getTargetPart(
							viewer, target, CLICK_TOOL_POLICY_KEY);
					// when no part processes the event, send it to the root
					// part
					if (clickTargetPart == null) {
						clickTargetPart = viewer.getRootPart();
					}

					Collection<? extends AbstractFXOnClickPolicy> clickPolicies = getClickPolicies(
							clickTargetPart);
					getDomain().openExecutionTransaction(FXClickDragTool.this);
					for (AbstractFXOnClickPolicy policy : clickPolicies) {
						policy.click(e);
					}

					// drag second, but only for single clicks
					if (e.getClickCount() == 1) {
						IVisualPart<Node, ? extends Node> dragTargetPart = getTargetPart(
								viewer, target, DRAG_TOOL_POLICY_KEY);

						// if no part wants to process the drag event, send it
						// to the root part
						if (dragTargetPart == null) {
							dragTargetPart = viewer.getRootPart();
						}
						Collection<? extends AbstractFXOnDragPolicy> dragPolicies = getDragPolicies(
								dragTargetPart);
						for (AbstractFXOnDragPolicy policy : dragPolicies) {
							dragInProgress = true;
							pressEvents.put(policy, e);
							policy.press(e);
						}
					}

					if (!dragInProgress) {
						getDomain().closeExecutionTransaction(
								FXClickDragTool.this);
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
					Collection<? extends AbstractFXOnDragPolicy> policies = getDragPolicies(
							targetPart);
					for (AbstractFXOnDragPolicy policy : policies) {
						pressEvents.remove(policy);
						policy.release(e, new Dimension(dx, dy));
					}
					getDomain().closeExecutionTransaction(FXClickDragTool.this);
					dragInProgress = false;
				}
			};

			gesture.setScene(((FXViewer) viewer).getScene());
			gestures.put(viewer, gesture);
		}
	}

	@Override
	protected void unregisterListeners() {
		for (AbstractMouseDragGesture gesture : gestures.values()) {
			gesture.setScene(null);
		}
		super.unregisterListeners();
	}

}
