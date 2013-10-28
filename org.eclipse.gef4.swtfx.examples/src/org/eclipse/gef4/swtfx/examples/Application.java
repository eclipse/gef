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

import org.eclipse.gef4.swtfx.Scene;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

public abstract class Application {

	private Display display;
	private Shell shell;

	public Application() {
		display = new Display();
		shell = new Shell(display);
		shell.setText("org.eclipse.gef4.swtfx");
		shell.setLayout(new GridLayout());

		Scene scene = start(shell);
		scene.setLayoutData(new GridData(GridData.FILL_BOTH));

		shell.pack();
		shell.open();

		double prefWidth = scene.getRoot().getPrefWidth();
		double prefHeight = scene.getRoot().getPrefHeight();
		shell.setSize((int) Math.ceil(prefWidth), (int) Math.ceil(prefHeight));
		Rectangle clientArea = shell.getClientArea();
		shell.setSize((int) prefWidth * 2 - clientArea.width, (int) prefHeight
				* 2 - clientArea.height);

		shell.redraw();

		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}

	public abstract Scene start(Shell shell);

}
