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
package org.eclipse.gef4.mvc.fx.example.tools;

import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;

import org.eclipse.gef4.mvc.fx.example.policies.AbstractHandleDragPolicy;
import org.eclipse.gef4.mvc.fx.tools.FXMouseDragGesture;
import org.eclipse.gef4.mvc.fx.viewer.FXViewer;
import org.eclipse.gef4.mvc.parts.IHandlePart;
import org.eclipse.gef4.mvc.parts.IVisualPart;
import org.eclipse.gef4.mvc.tools.AbstractTool;
import org.eclipse.gef4.mvc.viewer.IVisualPartViewer;

public class FXHandleDragTool extends AbstractTool<Node> {

	private FXMouseDragGesture gesture = new FXMouseDragGesture() {
		@Override
		protected void release(Node target, MouseEvent e, double dx, double dy) {
			IHandlePart<Node> targetPart = (IHandlePart<Node>) getTargetPart(target);
			if (targetPart == null)
				return;
			getPolicy(targetPart).commit(dx, dy);
		}

		@Override
		protected void press(Node target, MouseEvent e) {
			getPolicy(getTargetPart(target)).init(e.getButton());
		}

		@Override
		protected void drag(Node target, MouseEvent e, double dx, double dy) {
			IHandlePart<Node> targetPart = (IHandlePart<Node>) getTargetPart(target);
			if (targetPart == null)
				return;
			getPolicy(targetPart).perform(dx, dy);
		}
	};

	private Scene scene;

	protected IHandlePart<Node> getTargetPart(Node target) {
		// look for the Node in the visual-part-map
		IVisualPartViewer<Node> viewer = getDomain().getViewer();
		IVisualPart<Node> part = viewer.getVisualPartMap().get(target);
		if (part instanceof IHandlePart) {
			return (IHandlePart<Node>) part;
		}
		return null;
	}

	protected void registerListeners() {
		super.registerListeners();
		scene = ((FXViewer) getDomain().getViewer()).getCanvas().getScene();
	}

	protected AbstractHandleDragPolicy getPolicy(IHandlePart<Node> targetPart) {
		return targetPart.getEditPolicy(AbstractHandleDragPolicy.class);
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
