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
import java.util.Map;

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

	protected Collection<? extends AbstractFXClickPolicy> getClickPolicies(
			IVisualPart<Node> targetPart) {
		return targetPart.<AbstractFXClickPolicy> getAdapters(
				CLICK_TOOL_POLICY_KEY).values();
	}

	protected Collection<? extends AbstractFXDragPolicy> getDragPolicies(
			IVisualPart<Node> targetPart) {
		return targetPart.<AbstractFXDragPolicy> getAdapters(
				DRAG_TOOL_POLICY_KEY).values();
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
					IVisualPart<Node> targetPart = FXPartUtils.getTargetPart(
							Collections.singleton(viewer), target,
							DRAG_TOOL_POLICY_KEY);
					if (targetPart != null) {
						Collection<? extends AbstractFXDragPolicy> policies = getDragPolicies(targetPart);
						for (AbstractFXDragPolicy policy : policies) {
							policy.drag(e, new Dimension(dx, dy));
						}
					}
				}

				@Override
				protected void press(Node target, MouseEvent e) {
					// do not notify other listeners
					e.consume();

					// click first
					IVisualPart<Node> clickTargetPart = FXPartUtils
							.getTargetPart(getDomain().getViewers().values(),
									target, null);
					if (clickTargetPart != null) {
						Collection<? extends AbstractFXClickPolicy> policies = getClickPolicies(clickTargetPart);
						getDomain().openTransaction();
						for (AbstractFXClickPolicy policy : policies) {
							policy.click(e);
						}
						getDomain().closeTransaction();
					}

					// drag second
					IVisualPart<Node> dragTargetPart = FXPartUtils
							.getTargetPart(Collections.singleton(viewer),
									target, DRAG_TOOL_POLICY_KEY);
					if (dragTargetPart != null) {
						Collection<? extends AbstractFXDragPolicy> policies = getDragPolicies(dragTargetPart);
						getDomain().openTransaction();
						for (AbstractFXDragPolicy policy : policies) {
							dragInProgress = true;
							policy.press(e);
						}
					}
				}

				@Override
				protected void release(Node target, MouseEvent e, double dx,
						double dy) {
					IVisualPart<Node> targetPart = FXPartUtils.getTargetPart(
							Collections.singleton(viewer), target,
							DRAG_TOOL_POLICY_KEY);
					if (targetPart != null) {
						Collection<? extends AbstractFXDragPolicy> policies = getDragPolicies(targetPart);
						for (AbstractFXDragPolicy policy : policies) {
							policy.release(e, new Dimension(dx, dy));
						}
						getDomain().closeTransaction();
					}
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
