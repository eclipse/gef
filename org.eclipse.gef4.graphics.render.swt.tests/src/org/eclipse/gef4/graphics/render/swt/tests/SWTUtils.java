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
package org.eclipse.gef4.graphics.render.swt.tests;

import org.eclipse.gef4.graphics.render.swt.SWTGraphics;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Display;

class SWTUtils {

	private static final Display DEVICE = new Display();
	private static final Rectangle SCREEN_BOUNDS = new Rectangle(0, 0, 640, 480);

	static SWTGraphics createGraphics() {
		return new SWTGraphics(new GC(new Image(DEVICE, SCREEN_BOUNDS)));
	}

}
