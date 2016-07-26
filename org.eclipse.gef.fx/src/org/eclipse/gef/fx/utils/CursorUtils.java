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
package org.eclipse.gef.fx.utils;

import java.awt.MouseInfo;
import java.awt.PointerInfo;

import org.eclipse.gef.fx.FxBundle;
import org.eclipse.gef.geometry.planar.Point;

/**
 * The {@link CursorUtils} class contains utility methods for determining the
 * current pointer location ({@link #getPointerLocation()}).
 *
 * @author anyssen
 * @author mwienand
 *
 */
public class CursorUtils {

	private static final String JAVA_AWT_HEADLESS_PROPERTY = "java.awt.headless";

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
