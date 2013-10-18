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
import java.awt.event.InputEvent;

import org.eclipse.gef4.swtfx.CanvasFigure;
import org.eclipse.gef4.swtfx.Scene;
import org.eclipse.gef4.swtfx.event.IEventHandler;
import org.eclipse.gef4.swtfx.event.MouseEvent;
import org.eclipse.gef4.swtfx.layout.Pane;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.junit.Test;

/*
 * TODO: Replace AWT robot with SWT bot to generate events or send events directly
 */
public class MouseEventTests {

	public static interface IRobotTest {
		public void run(Robot robot);
	}

	private static final int HEIGHT = 300;

	private static final int WIDTH = 400;

	/**
	 * Creates a {@link Scene} and embeds it into the given {@link Shell}.
	 * Registers a {@link GridLayout} on the shell and sets the layout data of
	 * the scene to {@link GridData#FILL_BOTH}.
	 * 
	 * @param shell
	 * @return the created {@link Scene}
	 */
	private static Scene createScene(final Shell shell) {
		shell.setLayout(new GridLayout());
		Scene scene = new Scene(shell, new Pane());
		scene.setLayoutData(new GridData(GridData.FILL_BOTH));
		return scene;
	}

	/**
	 * Runs an SWT event loop for the given {@link Display} as long as the given
	 * {@link Shell} is not disposed.
	 * 
	 * @param display
	 * @param shell
	 */
	private static void eventLoop(final Display display, final Shell shell) {
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}

	/**
	 * Opens the given {@link Shell} and triggers a repaint.
	 * 
	 * @param shell
	 */
	private static void openShell(final Shell shell) {
		shell.pack();
		shell.open();
		shell.setBounds(0, 0, WIDTH, HEIGHT);
		shell.redraw();
	}

	private static void testGui(final Display display, final Shell shell,
			final IRobotTest iRobotTest) {
		new Thread() {
			@Override
			public void run() {
				// move mouse into and out of the shell
				try {
					Robot robot = new Robot();
					robot.setAutoDelay(100);
					iRobotTest.run(robot);
				} catch (AWTException e) {
					e.printStackTrace();
				}

				// close shell
				display.syncExec(new Runnable() {
					@Override
					public void run() {
						shell.close();
					}
				});
			}
		}.start();
	}

	@Test
	public void test_attributes() {
		final Display display = Display.getDefault();
		final Shell shell = new Shell(display);

		Scene scene = createScene(shell);
		Pane root = (Pane) scene.getRoot();

		final int[] state = new int[] { 0 };

		root.addEventHandler(MouseEvent.MOUSE_PRESSED,
				new IEventHandler<MouseEvent>() {
					@Override
					public void handle(MouseEvent event) {
						int button = event.getButton();
						assertEquals(1, button);
						state[0]++;
					}
				});

		openShell(shell);

		testGui(display, shell, new IRobotTest() {
			@Override
			public void run(Robot robot) {
				// click left button in shell
				robot.mouseMove(WIDTH / 2, HEIGHT / 2);
				robot.mousePress(InputEvent.BUTTON1_MASK);
				robot.mouseRelease(InputEvent.BUTTON1_MASK);
			}
		});

		eventLoop(display, shell);

		assertEquals(1, state[0]);
	}

	@Test
	public void test_enter_exit() {
		final Display display = Display.getDefault();
		final Shell shell = new Shell(display);

		Scene scene = createScene(shell);
		Pane root = (Pane) scene.getRoot();

		CanvasFigure canvas = new CanvasFigure(640, 480);
		root.addChildNodes(canvas);

		final int[] state = new int[] { 0 };

		root.addEventHandler(MouseEvent.MOUSE_ENTERED_TARGET,
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

		root.addEventHandler(MouseEvent.MOUSE_EXITED_TARGET,
				new IEventHandler<MouseEvent>() {
					@Override
					public void handle(MouseEvent event) {
						switch (state[0]) {
						case 3:
							assertEquals(MouseEvent.MOUSE_EXITED.getName(),
									event.getEventType().getName());
							state[0] = 4; // canvas is next
							break;
						case 5:
							assertEquals(
									MouseEvent.MOUSE_EXITED_TARGET.getName(),
									event.getEventType().getName());
							state[0] = 6; // result state
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

		canvas.addEventHandler(MouseEvent.MOUSE_EXITED_TARGET,
				new IEventHandler<MouseEvent>() {
					@Override
					public void handle(MouseEvent event) {
						switch (state[0]) {
						case 4:
							assertEquals(MouseEvent.MOUSE_EXITED.getName(),
									event.getEventType().getName());
							state[0] = 5; // one more for the group
							break;
						default:
							assertTrue(false);
						}
					}
				});

		openShell(shell);

		testGui(display, shell, new IRobotTest() {
			@Override
			public void run(Robot robot) {
				// move mouse into and out of the shell
				robot.mouseMove(WIDTH / 2, HEIGHT / 2);
				robot.mouseMove(2 * WIDTH, 2 * HEIGHT);
			}
		});

		eventLoop(display, shell);

		// all events processed?
		assertEquals(6, state[0]);
	}

}
