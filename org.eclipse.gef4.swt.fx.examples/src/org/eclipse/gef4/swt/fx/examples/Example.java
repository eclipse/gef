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
package org.eclipse.gef4.swt.fx.examples;

import org.eclipse.gef4.swt.fx.Group;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

public class Example {

	private Display display;
	private Shell shell;
	private Group group;

	public Example(IExample ex) {
		int w = ex.getWidth();
		int h = ex.getHeight();

		display = new Display();
		shell = new Shell(display);
		shell.setText("org.eclipse.gef4.swt.fx - " + ex.getTitle());
		shell.setLayout(new GridLayout());

		group = new Group(shell);
		// group.addBackgroundPaintListener(this);
		group.setSize(w, h);
		group.setLayoutData(new GridData(GridData.FILL_BOTH));

		ex.addUi(group);

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
		return group;
	}

	// @Override
	// public void paintControl(PaintEvent e) {
	// GC gc = e.gc;
	// GraphicsContext gefGc = new GraphicsContext(gc);
	// ex.render(gefGc);
	// gefGc.cleanUp();
	// }

}
