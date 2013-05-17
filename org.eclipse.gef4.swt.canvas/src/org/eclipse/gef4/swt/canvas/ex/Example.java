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
package org.eclipse.gef4.swt.canvas.ex;

import org.eclipse.gef4.swt.canvas.gc.GraphicsContext;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

public class Example implements PaintListener {

	private IExample ex;
	private Display display;
	private Shell shell;
	private Canvas canvas;

	public Example(IExample ex) {
		this.ex = ex;
		int w = ex.getWidth();
		int h = ex.getHeight();

		display = new Display();
		shell = new Shell(display);
		shell.setText("org.eclipse.gef4.swt.canvas - " + ex.getTitle());
		shell.setLayout(new GridLayout());

		canvas = new Canvas(shell, SWT.NONE);
		canvas.addPaintListener(this);
		canvas.setSize(w, h);
		canvas.setLayoutData(new GridData(GridData.FILL_BOTH));

		ex.addUi(canvas);

		shell.pack();
		shell.open();
		shell.setBounds(0, 0, w, h);
		Rectangle clientArea = shell.getClientArea();
		shell.setBounds(0, 0, 2 * w - clientArea.width, 2 * h
				- clientArea.height);
		shell.redraw();

		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}

	public Canvas getCanvas() {
		return canvas;
	}

	@Override
	public void paintControl(PaintEvent e) {
		GC gc = e.gc;
		GraphicsContext gefGc = new GraphicsContext(gc);
		ex.render(gefGc);
		gefGc.cleanUp();
	}

}
