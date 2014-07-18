/*******************************************************************************
 * Copyright (c) 2014 itemis AG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Alexander Nyßen (itemis AG) - initial API and implementation
 *     
 *******************************************************************************/
package org.eclipse.gef4.mvc.fx.ui.viewer;

import javafx.embed.swt.FXCanvas;
import javafx.scene.Scene;

import org.eclipse.gef4.mvc.fx.viewer.FXViewer;
import org.eclipse.gef4.mvc.fx.viewer.ISceneContainer;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;

public class FXCanvasSceneContainer implements ISceneContainer {

	FocusListener focusListener = new FocusListener() {
		@Override
		public void focusLost(FocusEvent e) {
//			System.out.println("Focus on canvas lost");
			// TODO: propagate to viewer
		}

		@Override
		public void focusGained(FocusEvent e) {
//			System.out.println("Focus on canvas gained.");
			// TODO: propagate to viewer
		}
	};

	private FXViewer viewer;
	private final FXCanvas canvas;

	public FXCanvasSceneContainer(FXViewer viewer, FXCanvas canvas) {
		this.viewer = viewer;
		this.canvas = canvas;
	}

	@Override
	public void setScene(Scene scene) {
		canvas.setScene(scene);
	}

	@Override
	public void registerFocusForwarding(FXViewer viewer) {
		canvas.addFocusListener(focusListener);
	}

	@Override
	public void unregisterFocusForwarding(FXViewer viewer) {
		canvas.removeFocusListener(focusListener);
	}
}