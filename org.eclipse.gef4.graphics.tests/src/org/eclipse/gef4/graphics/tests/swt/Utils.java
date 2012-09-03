package org.eclipse.gef4.graphics.tests.swt;

import org.eclipse.gef4.graphics.IGraphics;
import org.eclipse.gef4.graphics.swt.DisplayGraphics;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Display;

class Utils {

	private static final Display DEVICE = new Display();
	private static final Rectangle SCREEN_BOUNDS = new Rectangle(0, 0, 640, 480);

	static IGraphics createGraphics() {
		return new DisplayGraphics(new GC(new Image(DEVICE, SCREEN_BOUNDS)));
	}

}
