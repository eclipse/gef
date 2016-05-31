/*******************************************************************************
 * Copyright (c) 2014, 2015 itemis AG and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Alexander Nyßen (itemis AG)  - initial API and implementation
 *     Matthias Wienand (itemis AG) - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.gef4.fx.utils;

import java.awt.MouseInfo;
import java.awt.PointerInfo;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.eclipse.gef4.fx.FxBundle;
import org.eclipse.gef4.geometry.planar.Point;

import javafx.scene.Cursor;
import javafx.scene.Scene;

/**
 * The {@link CursorUtils} class contains utility methods for determining the
 * current pointer location ({@link #getPointerLocation()}) and for forcing a
 * mouse cursor update ({@link #forceCursorUpdate(Scene)}).
 *
 * @author anyssen
 * @author mwienand
 *
 */
public class CursorUtils {

	private static final String JAVA_AWT_HEADLESS_PROPERTY = "java.awt.headless";

	/**
	 * Forces the JavaFX runtime to update the mouse cursor. This is useful when
	 * you want to change the mouse cursor independently of mouse movement.
	 *
	 * @param scene
	 *            The {@link Scene} to update the cursor for.
	 */
	public static void forceCursorUpdate(Scene scene) {
		try {
			Field mouseHandlerField = scene.getClass()
					.getDeclaredField("mouseHandler");
			mouseHandlerField.setAccessible(true);
			Object mouseHandler = mouseHandlerField.get(scene);
			Class<?> mouseHandlerClass = Class
					.forName("javafx.scene.Scene$MouseHandler");
			Method updateCursorMethod = mouseHandlerClass
					.getDeclaredMethod("updateCursor", Cursor.class);
			updateCursorMethod.setAccessible(true);
			updateCursorMethod.invoke(mouseHandler, scene.getCursor());
			Method updateCursorFrameMethod = mouseHandlerClass
					.getDeclaredMethod("updateCursorFrame");
			updateCursorFrameMethod.setAccessible(true);
			updateCursorFrameMethod.invoke(mouseHandler);
		} catch (Exception x) {
			throw new IllegalStateException(x);
		}
	}

	/**
	 * Returns the current pointer location.
	 *
	 * @return The current pointer location.
	 */
	public static Point getPointerLocation() {
		// find pointer location (OS specific)
		String os = System.getProperty("os.name");
		if (os.startsWith("Mac OS X") && FxBundle.getContext() == null) {
			// use special glass robot for MacOS
			com.sun.glass.ui.Robot robot = com.sun.glass.ui.Application
					.GetApplication().createRobot();
			return new Point(robot.getMouseX(), robot.getMouseY());
		} else {
			// Ensure AWT is not considered to be in headless mode, as
			// otherwise MouseInfo#getPointerInfo() will not work.

			// adjust AWT headless property, if required
			String awtHeadlessPropertyValue = System
					.getProperty(JAVA_AWT_HEADLESS_PROPERTY);
			if (awtHeadlessPropertyValue != null
					&& awtHeadlessPropertyValue != Boolean.FALSE.toString()) {
				System.setProperty(JAVA_AWT_HEADLESS_PROPERTY,
						Boolean.FALSE.toString());
			}
			// retrieve mouse location
			PointerInfo pi = MouseInfo.getPointerInfo();
			java.awt.Point mp = pi.getLocation();

			// restore AWT headless property
			if (awtHeadlessPropertyValue != null) {
				System.setProperty(JAVA_AWT_HEADLESS_PROPERTY,
						awtHeadlessPropertyValue);
			}
			return new Point(mp.x, mp.y);
		}
	}

}
