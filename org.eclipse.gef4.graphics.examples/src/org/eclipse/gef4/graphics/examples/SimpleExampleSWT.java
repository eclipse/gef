/*******************************************************************************
 * Copyright (c) 2012 itemis AG and others.
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
package org.eclipse.gef4.graphics.examples;

import java.io.IOException;

import org.eclipse.gef4.graphics.swt.SwtGraphics;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

public class SimpleExampleSWT implements PaintListener {

	public static void main(String[] args) {
		new SimpleExampleSWT("first test example...");
	}

	Shell shell;
	SwtGraphics g;

	public SimpleExampleSWT(String title) {
		Display display = new Display();

		shell = new Shell(display, SWT.SHELL_TRIM | SWT.DOUBLE_BUFFERED);
		shell.setText(title);
		shell.setBounds(0, 0, 640, 480);

		// open the shell before creating the controllable shapes so that their
		// default coordinates are not changed due to the resize of their canvas
		shell.open();
		shell.addPaintListener(this);
		shell.redraw();

		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}

	public void paintControl(PaintEvent e) {
		g = new SwtGraphics(e.gc);
		try {
			SimpleExampleUtil.draw(g);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}

}
