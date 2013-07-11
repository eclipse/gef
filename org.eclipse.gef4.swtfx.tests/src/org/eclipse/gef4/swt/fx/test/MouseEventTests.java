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
package org.eclipse.gef4.swt.fx.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.awt.AWTException;
import java.awt.Robot;

import org.eclipse.gef4.swtfx.CanvasFigure;
import org.eclipse.gef4.swtfx.event.IEventHandler;
import org.eclipse.gef4.swtfx.event.MouseEvent;
import org.eclipse.gef4.swtfx.layout.Pane;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.junit.Test;

public class MouseEventTests {

	private static final int HEIGHT = 300;
	private static final int WIDTH = 400;

	@Test
	public void test_mouse_enters_window() {
		final Display display = new Display();
		final Shell shell = new Shell(display);
		shell.setLayout(new GridLayout());

		Pane pane = new Pane(shell);
		pane.setLayoutData(new GridData(GridData.FILL_BOTH));

		CanvasFigure canvas = new CanvasFigure(640, 480);
		pane.addChildNodes(canvas);

		final int[] state = new int[] { 0 };

		pane.addEventHandler(MouseEvent.MOUSE_ENTERED_TARGET,
				new IEventHandler<MouseEvent>() {
					@Override
					public void handle(MouseEvent event) {
						switch (state[0]) {
						case 0:
							assertEquals(MouseEvent.MOUSE_ENTERED.getName(),
									event.getEventType().getName());
							state[0] = 1; // canvas is next
							break;
						case 2:
							assertEquals(
									MouseEvent.MOUSE_ENTERED_TARGET.getName(),
									event.getEventType().getName());
							state[0] = 3; // result state
							break;
						default:
							assertTrue(false);
						}
					}
				});

		canvas.addEventHandler(MouseEvent.MOUSE_ENTERED_TARGET,
				new IEventHandler<MouseEvent>() {
					@Override
					public void handle(MouseEvent event) {
						switch (state[0]) {
						case 1:
							assertEquals(MouseEvent.MOUSE_ENTERED.getName(),
									event.getEventType().getName());
							state[0] = 2; // one more for the group
							break;
						default:
							assertTrue(false);
						}
					}
				});

		shell.pack();
		shell.open();
		shell.setBounds(0, 0, 400, 300);
		shell.redraw();

		new Thread() {
			@Override
			public void run() {
				try {
					Robot robot = new Robot();
					robot.mouseMove(WIDTH / 2, HEIGHT / 2);
				} catch (AWTException e) {
					e.printStackTrace();
				}

				display.syncExec(new Runnable() {
					@Override
					public void run() {
						shell.dispose();
					}
				});
			}
		}.start();

		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}

		// all events processed?
		assertEquals(3, state[0]);
	}

}
