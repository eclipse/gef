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

import java.util.Collections;

import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;

import org.eclipse.gef4.fx.gestures.FXMouseDragGesture;
import org.eclipse.gef4.geometry.planar.Dimension;
import org.eclipse.gef4.geometry.planar.Point;
import org.eclipse.gef4.mvc.fx.parts.FXPartUtils;
import org.eclipse.gef4.mvc.fx.viewer.FXViewer;
import org.eclipse.gef4.mvc.parts.IVisualPart;
import org.eclipse.gef4.mvc.tools.AbstractDragTool;

public class FXDragTool extends AbstractDragTool<Node> {

	private FXMouseDragGesture gesture = new FXMouseDragGesture() {
		@Override
		protected void press(Node target, MouseEvent e) {
			IVisualPart<Node> eventTargetPart = FXPartUtils.getEventTargetPart(getDomain().getViewer(), e);
			
			System.out.println("dragging: <" + eventTargetPart + ">");
			
			FXDragTool.this.press(Collections.singletonList(eventTargetPart),
					new Point(e.getSceneX(), e.getSceneY()));
		}
		
		@Override
		protected void release(Node target, MouseEvent e, double dx, double dy) {
			FXDragTool.this.release(
					Collections.singletonList(FXPartUtils.getEventTargetPart(getDomain().getViewer(), e)),
					new Point(e.getSceneX(), e.getSceneY()), new Dimension(dx,
							dy));
		}

		@Override
		protected void drag(Node target, MouseEvent e, double dx, double dy) {
			FXDragTool.this.drag(Collections.singletonList(FXPartUtils.getEventTargetPart(getDomain().getViewer(), e)),
					new Point(e.getSceneX(), e.getSceneY()), new Dimension(dx,dy));
		}
	};

	private Scene scene;

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
