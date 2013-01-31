package org.eclipse.gef4.graphics.examples;

import org.eclipse.gef4.geometry.planar.Polygon;
import org.eclipse.gef4.graphics.IGraphics;
import org.eclipse.gef4.graphics.color.Color;

public class PrintingUtil {

	public static void renderScene(IGraphics g) {
		g.translate(50, 200);
		g.pushState();
		// g.shear(-1, 0).translate(17, -36).scale(1, 2);
		g.shear(-1, 0).scale(1, 3);
		g.setWrite(new Color(172, 172, 172));
		g.write("GEF4 Graphics");
		g.popState();
		g.write("GEF4 Graphics");
		g.translate(0, 100);
		g.setFill(new Color(255, 0, 0));
		g.fill(new Polygon(0, 0, 100, 0, 50, 50));
	}

}
