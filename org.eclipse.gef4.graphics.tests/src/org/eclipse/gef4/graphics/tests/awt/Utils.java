package org.eclipse.gef4.graphics.tests.awt;

import java.awt.image.BufferedImage;

import org.eclipse.gef4.graphics.IGraphics;
import org.eclipse.gef4.graphics.awt.DisplayGraphics;

class Utils {

	static IGraphics createGraphics() {
		return new DisplayGraphics(new BufferedImage(640, 480,
				java.awt.image.BufferedImage.TYPE_INT_ARGB).createGraphics());
	}

}
