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
package org.eclipse.gef4.graphics.examples;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

public class SwtXorTest implements PaintListener {

	public static void main(String[] args) {
		new SwtXorTest();
	}

	private Display display;

	public SwtXorTest() {
		display = new Display();

		Shell shell = new Shell(display, SWT.SHELL_TRIM | SWT.DOUBLE_BUFFERED);
		shell.setText("test");
		shell.setBackground(display.getSystemColor(SWT.COLOR_WHITE));
		shell.pack();
		shell.open();
		shell.setBounds(0, 0, 640, 480);
		shell.addPaintListener(this);
		shell.redraw();

		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}

	@Override
	public void paintControl(PaintEvent e) {
		// xor mode does not work when advanced is enabled
		e.gc.setAdvanced(true);
		e.gc.setBackground(display.getSystemColor(SWT.COLOR_BLUE));
		e.gc.fillRectangle(20, 20, 100, 100);
		e.gc.setXORMode(true);
		e.gc.setBackground(display.getSystemColor(SWT.COLOR_BLACK));
		e.gc.fillRectangle(20, 20, 100, 100);
	}

}
