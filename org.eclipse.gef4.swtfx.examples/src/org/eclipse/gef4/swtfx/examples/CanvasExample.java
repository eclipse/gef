/*******************************************************************************
 * Copyright (c) 2013 itemis AG and others.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Matthias Wienand (itemis AG) - initial API and implementation
 * 
 *******************************************************************************/
package org.eclipse.gef4.swtfx.examples;

import org.eclipse.gef4.swtfx.CanvasFigure;
import org.eclipse.gef4.swtfx.Group;
import org.eclipse.gef4.swtfx.Scene;
import org.eclipse.gef4.swtfx.gc.GraphicsContext;
import org.eclipse.swt.widgets.Shell;

public class CanvasExample extends Application {

	public static void main(String[] args) {
		new CanvasExample();
	}

	@Override
	public Scene start(Shell shell) {
		Group root = new Group();
		CanvasFigure canvas = new CanvasFigure(400, 300);
		root.addChildNodes(canvas);

		GraphicsContext gc = canvas.getGraphicsContext();
		gc.arc(100, 100, 40, 60, 10, 90);
		gc.rect(0, 0, 20, 20);
		gc.fill();
		gc.cleanUp();

		// TODO: the following should not be necessary
		root.setPrefWidth(400);
		root.setPrefHeight(300);

		return new Scene(shell, root);
	}

}
