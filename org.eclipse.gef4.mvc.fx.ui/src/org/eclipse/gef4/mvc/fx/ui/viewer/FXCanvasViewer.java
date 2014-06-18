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

public class FXCanvasViewer extends FXViewer {

	public FXCanvasViewer(final FXCanvas canvas) {
		setSceneContainer(new ISceneContainer() {
			
			@Override
			public void setScene(Scene scene) {
				canvas.setScene(scene);
			}
		});
	}
}
