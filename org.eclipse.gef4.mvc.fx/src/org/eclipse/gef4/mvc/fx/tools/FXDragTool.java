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
import java.util.List;
import java.util.Map;

import javafx.scene.Node;
import javafx.scene.input.MouseEvent;

import org.eclipse.gef4.fx.gestures.FXMouseDragGesture;
import org.eclipse.gef4.geometry.planar.Dimension;
import org.eclipse.gef4.mvc.fx.parts.FXPartUtils;
import org.eclipse.gef4.mvc.fx.policies.AbstractFXDragPolicy;
import org.eclipse.gef4.mvc.fx.viewer.IFXViewer;
import org.eclipse.gef4.mvc.parts.IContentPart;
import org.eclipse.gef4.mvc.parts.IVisualPart;
import org.eclipse.gef4.mvc.tools.AbstractTool;

public class FXDragTool extends AbstractTool<Node> {

	public static final Class<AbstractFXDragPolicy> TOOL_POLICY_KEY = AbstractFXDragPolicy.class;

	private final FXMouseDragGesture gesture = new FXMouseDragGesture() {
		@Override
		protected void drag(Node target, MouseEvent e, double dx, double dy,
				List<Node> nodesUnderMouse) {
			FXDragTool.this.drag(FXPartUtils.getTargetParts(getDomain()
					.getViewer(), e, TOOL_POLICY_KEY), e,
					new Dimension(dx, dy), nodesUnderMouse);
		}

		@Override
		protected void press(Node target, MouseEvent e) {
			FXDragTool.this.press(FXPartUtils.getTargetParts(getDomain()
					.getViewer(), e, TOOL_POLICY_KEY), e);
		}

		@Override
		protected void release(Node target, MouseEvent e, double dx, double dy,
				List<Node> nodesUnderMouse) {
			FXDragTool.this.release(FXPartUtils.getTargetParts(getDomain()
					.getViewer(), e, TOOL_POLICY_KEY), e,
					new Dimension(dx, dy), nodesUnderMouse);
		}
	};

	protected void drag(List<IVisualPart<Node>> targetParts, MouseEvent e,
			Dimension delta, List<Node> nodesUnderMouse) {
		for (IVisualPart<Node> targetPart : targetParts) {
			AbstractFXDragPolicy policy = getToolPolicy(targetPart);
			if (policy != null) {
				policy.drag(e, delta, nodesUnderMouse,
						getPartsUnderMouse(nodesUnderMouse));
			}
		}
	}

	private List<IContentPart<Node>> getPartsUnderMouse(
			List<Node> nodesUnderMouse) {
		List<IContentPart<Node>> parts = new ArrayList<IContentPart<Node>>();
		Map<Node, IVisualPart<Node>> partMap = getDomain().getViewer()
				.getVisualPartMap();
		for (Node node : nodesUnderMouse) {
			if (partMap.containsKey(node)) {
				IVisualPart<Node> part = partMap.get(node);
				if (part instanceof IContentPart) {
					parts.add((IContentPart<Node>) part);
				}
			}
		}
		return parts;
	}

	protected AbstractFXDragPolicy getToolPolicy(IVisualPart<Node> targetPart) {
		return targetPart.getAdapter(TOOL_POLICY_KEY);
	}

	protected void press(List<IVisualPart<Node>> targetParts, MouseEvent e) {
		for (IVisualPart<Node> targetPart : targetParts) {
			AbstractFXDragPolicy policy = getToolPolicy(targetPart);
			if (policy != null) {
				policy.press(e);
			}
		}
	}

	@Override
	protected void registerListeners() {
		super.registerListeners();
		gesture.setScene(((IFXViewer) getDomain().getViewer()).getScene());
	}

	protected void release(List<IVisualPart<Node>> targetParts, MouseEvent e,
			Dimension delta, List<Node> nodesUnderMouse) {
		for (IVisualPart<Node> targetPart : targetParts) {
			AbstractFXDragPolicy policy = getToolPolicy(targetPart);
			if (policy != null) {
				policy.release(e, delta, nodesUnderMouse,
						getPartsUnderMouse(nodesUnderMouse));
			}
		}
	}

	@Override
	protected void unregisterListeners() {
		gesture.setScene(null);
		super.unregisterListeners();
	}

}
