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

import java.util.List;

import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;

import org.eclipse.gef4.fx.gestures.FXMouseDragGesture;
import org.eclipse.gef4.geometry.planar.Dimension;
import org.eclipse.gef4.geometry.planar.Point;
import org.eclipse.gef4.mvc.fx.parts.FXPartUtils;
import org.eclipse.gef4.mvc.fx.policies.AbstractFXDragPolicy;
import org.eclipse.gef4.mvc.fx.viewer.FXViewer;
import org.eclipse.gef4.mvc.parts.IVisualPart;
import org.eclipse.gef4.mvc.policies.IPolicy;
import org.eclipse.gef4.mvc.tools.AbstractTool;

// TODO: refactor implementation
public class FXDragTool extends AbstractTool<Node> {

	@SuppressWarnings("rawtypes")
	public static final Class<? extends IPolicy> TOOL_POLICY_KEY = AbstractFXDragPolicy.class;

	@SuppressWarnings({ "unchecked", "rawtypes" })
	protected AbstractFXDragPolicy getToolPolicy(IVisualPart<Node> targetPart) {
		return targetPart.getBound((Class<IPolicy>) TOOL_POLICY_KEY);
	}

	@SuppressWarnings("unchecked")
	private FXMouseDragGesture gesture = new FXMouseDragGesture() {
		@Override
		protected void press(Node target, MouseEvent e) {
			FXDragTool.this.press(FXPartUtils.getTargetParts(getDomain()
					.getViewer(), e, (Class<IPolicy<Node>>) TOOL_POLICY_KEY),
					new Point(e.getSceneX(), e.getSceneY()));
		}

		@Override
		protected void release(Node target, MouseEvent e, double dx, double dy) {
			FXDragTool.this.release(FXPartUtils.getTargetParts(getDomain()
					.getViewer(), e, (Class<IPolicy<Node>>) TOOL_POLICY_KEY),
					new Point(e.getSceneX(), e.getSceneY()), new Dimension(dx,
							dy));
		}

		@Override
		protected void drag(Node target, MouseEvent e, double dx, double dy) {
			FXDragTool.this.drag(FXPartUtils.getTargetParts(getDomain()
					.getViewer(), e, (Class<IPolicy<Node>>) TOOL_POLICY_KEY),
					new Point(e.getSceneX(), e.getSceneY()), new Dimension(dx,
							dy));
		}
	};

	protected void press(List<IVisualPart<Node>> targetParts, Point mouseLocation) {
		for (IVisualPart<Node> targetPart : targetParts) {
			AbstractFXDragPolicy policy = getToolPolicy(targetPart);
			if (policy != null)
				policy.press(mouseLocation);
		}
	}

	protected void drag(List<IVisualPart<Node>> targetParts, Point mouseLocation,
			Dimension delta) {
		for (IVisualPart<Node> targetPart : targetParts) {
			AbstractFXDragPolicy policy = getToolPolicy(targetPart);
			if (policy != null)
				policy.drag(mouseLocation, delta);
		}
	}

	protected void release(List<IVisualPart<Node>> targetParts,
			Point mouseLocation, Dimension delta) {
		for (IVisualPart<Node> targetPart : targetParts) {
			AbstractFXDragPolicy policy = getToolPolicy(targetPart);
			if (policy != null)
				policy.release(mouseLocation, delta);
		}
	}

	private Scene scene;

	@Override
	protected void registerListeners() {
		super.registerListeners();
		scene = ((FXViewer) getDomain().getViewer()).getCanvas().getScene();
	}

	@Override
	public void activate() {
		super.activate();
		if (scene != null) {
			gesture.setScene(scene);
		}
	}

	@Override
	public void deactivate() {
		if (scene != null) {
			gesture.setScene(null);
		}
		super.deactivate();
	}

	@Override
	protected void unregisterListeners() {
		gesture.setScene(null);
		super.unregisterListeners();
	}

}
