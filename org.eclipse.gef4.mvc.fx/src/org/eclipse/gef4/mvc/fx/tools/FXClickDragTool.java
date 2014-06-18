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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javafx.scene.Node;
import javafx.scene.input.MouseEvent;

import org.eclipse.gef4.fx.gestures.FXMouseDragGesture;
import org.eclipse.gef4.geometry.planar.Dimension;
import org.eclipse.gef4.mvc.fx.parts.FXPartUtils;
import org.eclipse.gef4.mvc.fx.policies.AbstractFXClickPolicy;
import org.eclipse.gef4.mvc.fx.policies.AbstractFXDragPolicy;
import org.eclipse.gef4.mvc.fx.viewer.FXViewer;
import org.eclipse.gef4.mvc.parts.IContentPart;
import org.eclipse.gef4.mvc.parts.IVisualPart;
import org.eclipse.gef4.mvc.tools.AbstractTool;
import org.eclipse.gef4.mvc.viewer.IViewer;

public class FXClickDragTool extends AbstractTool<Node> {

	public static final Class<AbstractFXClickPolicy> CLICK_TOOL_POLICY_KEY = AbstractFXClickPolicy.class;
	public static final Class<AbstractFXDragPolicy> DRAG_TOOL_POLICY_KEY = AbstractFXDragPolicy.class;

	private final Map<IViewer<Node>, FXMouseDragGesture> gestures = new HashMap<IViewer<Node>, FXMouseDragGesture>();

	protected AbstractFXClickPolicy getClickPolicy(IVisualPart<Node> targetPart) {
		return targetPart.getAdapter(CLICK_TOOL_POLICY_KEY);
	}

	protected AbstractFXDragPolicy getDragPolicy(IVisualPart<Node> targetPart) {
		return targetPart.getAdapter(DRAG_TOOL_POLICY_KEY);
	}

	private List<IContentPart<Node>> getPartsUnderMouse(
			List<Node> nodesUnderMouse) {
		List<IContentPart<Node>> parts = new ArrayList<IContentPart<Node>>();
		for (IViewer<Node> viewer : getDomain().getViewers()) {
			Map<Node, IVisualPart<Node>> partMap = viewer.getVisualPartMap();
			for (Node node : nodesUnderMouse) {
				if (partMap.containsKey(node)) {
					IVisualPart<Node> part = partMap.get(node);
					if (part instanceof IContentPart) {
						parts.add((IContentPart<Node>) part);
					}
				}
			}
		}
		return parts;
	}

	@Override
	protected void registerListeners() {
		super.registerListeners();

		for (final IViewer<Node> viewer : getDomain().getViewers()) {
			FXMouseDragGesture gesture = new FXMouseDragGesture() {
				@Override
				protected void drag(Node target, MouseEvent e, double dx,
						double dy, List<Node> nodesUnderMouse) {
					for (IVisualPart<Node> targetPart : FXPartUtils
							.getTargetParts(Collections.singleton(viewer), e,
									DRAG_TOOL_POLICY_KEY)) {
						AbstractFXDragPolicy policy = getDragPolicy(targetPart);
						if (policy != null) {
							policy.drag(e, new Dimension(dx, dy),
									nodesUnderMouse,
									getPartsUnderMouse(nodesUnderMouse));
						}
					}
				}

				@Override
				protected void press(Node target, MouseEvent e) {
					// click first
					IVisualPart<Node> clickTargetPart = FXPartUtils
							.getEventTargetPart(getDomain().getViewers(), e);
					if (clickTargetPart != null) {
						AbstractFXClickPolicy policy = getClickPolicy(clickTargetPart);
						if (policy != null) {
							policy.click(e);
						}
					}

					// drag second
					for (IVisualPart<Node> dragTargetPart : FXPartUtils
							.getTargetParts(Collections.singleton(viewer), e,
									DRAG_TOOL_POLICY_KEY)) {
						AbstractFXDragPolicy policy = getDragPolicy(dragTargetPart);
						if (policy != null) {
							policy.press(e);
						}
					}
				}

				@Override
				protected void release(Node target, MouseEvent e, double dx,
						double dy, List<Node> nodesUnderMouse) {
					List<IVisualPart<Node>> targetParts = FXPartUtils
							.getTargetParts(Collections.singleton(viewer), e,
									DRAG_TOOL_POLICY_KEY);
					for (IVisualPart<Node> targetPart : targetParts) {
						AbstractFXDragPolicy policy = getDragPolicy(targetPart);
						if (policy != null) {
							policy.release(e, new Dimension(dx, dy),
									nodesUnderMouse,
									getPartsUnderMouse(nodesUnderMouse));
						}
					}
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
