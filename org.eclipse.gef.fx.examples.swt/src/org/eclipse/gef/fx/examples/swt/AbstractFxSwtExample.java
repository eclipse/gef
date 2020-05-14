/*******************************************************************************
 * Copyright (c) 2013, 2019 itemis AG and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Matthias Wienand (itemis AG) - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.gef.fx.examples.swt;

import org.eclipse.gef.fx.swt.canvas.FXCanvasEx;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import javafx.scene.Scene;

public abstract class AbstractFxSwtExample {

	protected Shell shell;
	protected FXCanvasEx canvas;

	public AbstractFxSwtExample(String shellText) {
		Display display = new Display();
		shell = new Shell(display);
		shell.setLayout(new FillLayout());
		canvas = new FXCanvasEx(shell, SWT.NONE);

		Scene scene = createScene();
		canvas.setScene(scene);

		shell.setSize((int) scene.getWidth(), (int) scene.getHeight());
		shell.setText(shellText);
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
