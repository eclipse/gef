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

import javafx.scene.Node;
import javafx.scene.input.MouseEvent;

import org.eclipse.gef4.fx.gestures.FXMouseDragGesture;
import org.eclipse.gef4.geometry.planar.Dimension;
import org.eclipse.gef4.mvc.fx.parts.FXPartUtils;
import org.eclipse.gef4.mvc.fx.policies.AbstractFXClickPolicy;
import org.eclipse.gef4.mvc.fx.policies.AbstractFXDragPolicy;
import org.eclipse.gef4.mvc.fx.viewer.FXViewer;
import org.eclipse.gef4.mvc.parts.IVisualPart;
import org.eclipse.gef4.mvc.tools.AbstractTool;
import org.eclipse.gef4.mvc.viewer.IViewer;

public class FXClickDragTool extends AbstractTool<Node> {

	public static final Class<AbstractFXClickPolicy> CLICK_TOOL_POLICY_KEY = AbstractFXClickPolicy.class;
	public static final Class<AbstractFXDragPolicy> DRAG_TOOL_POLICY_KEY = AbstractFXDragPolicy.class;

	private final Map<IViewer<Node>, FXMouseDragGesture> gestures = new HashMap<IViewer<Node>, FXMouseDragGesture>();
	private boolean dragInProgress;
	private final Map<AbstractFXDragPolicy, MouseEvent> pressEvents = new HashMap<AbstractFXDragPolicy, MouseEvent>();

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

	public boolean isDragging() {
		return dragInProgress;
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
					IVisualPart<Node, ? extends Node> targetPart = FXPartUtils
							.getTargetPart(Collections.singleton(viewer),
									target, DRAG_TOOL_POLICY_KEY);
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
					IVisualPart<Node, ? extends Node> clickTargetPart = FXPartUtils
							.getTargetPart(Collections.singleton(viewer),
									target, null);
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
						IVisualPart<Node, ? extends Node> dragTargetPart = FXPartUtils
								.getTargetPart(Collections.singleton(viewer),
										target, DRAG_TOOL_POLICY_KEY);
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
					IVisualPart<Node, ? extends Node> targetPart = FXPartUtils
							.getTargetPart(Collections.singleton(viewer),
									target, DRAG_TOOL_POLICY_KEY);
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
