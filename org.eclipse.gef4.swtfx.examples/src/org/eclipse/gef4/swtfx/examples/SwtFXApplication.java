/*******************************************************************************
 * Copyright (c) 2013, 2014 itemis AG and others.
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

import javafx.scene.Scene;

import org.eclipse.gef4.swtfx.SwtFXCanvas;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

public abstract class SwtFXApplication {

	protected Shell shell;
	protected SwtFXCanvas canvas;

	public SwtFXApplication() {
		Display display = new Display();
		shell = new Shell(display);
		shell.setLayout(new FillLayout());
		canvas = new SwtFXCanvas(shell, SWT.NONE);

		Scene scene = createScene();
		canvas.setScene(scene);

		shell.setSize((int) scene.getWidth(), (int) scene.getHeight());
		shell.open();

		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		display.dispose();
	}

	public abstract Scene createScene();

}
