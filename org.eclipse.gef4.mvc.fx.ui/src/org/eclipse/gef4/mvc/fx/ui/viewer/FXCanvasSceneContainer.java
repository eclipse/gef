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

import org.eclipse.gef4.mvc.fx.viewer.ISceneContainer;

public class FXCanvasSceneContainer implements ISceneContainer {

	private final FXCanvas canvas;

	public FXCanvasSceneContainer(FXCanvas canvas) {
		this.canvas = canvas;
	}

	@Override
	public void setScene(Scene scene) {
		canvas.setScene(scene);
	}

}