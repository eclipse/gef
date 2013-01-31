package org.eclipse.gef4.graphics.examples;

import org.eclipse.gef4.geometry.euclidean.Angle;
import org.eclipse.gef4.geometry.planar.Point;
import org.eclipse.gef4.geometry.planar.PolyBezier;
import org.eclipse.gef4.geometry.planar.Rectangle;
import org.eclipse.gef4.graphics.Gradient;
import org.eclipse.gef4.graphics.IGraphics;
import org.eclipse.gef4.graphics.LineCap;
import org.eclipse.gef4.graphics.LineJoin;
import org.eclipse.gef4.graphics.color.Color;

public class ForegroundPatternUtil {

	public ForegroundPatternUtil() {
	}

	public void renderScene(IGraphics g) {
		g.setDraw(new Gradient.Linear(new Point(), new Point(100, 100))
				.addStop(0, new Color(64, 32, 228)).addStop(1,
						new Color(32, 128, 228)));
		// g.translate(100, 100);
		g.setLineWidth(12).setLineCap(LineCap.ROUND)
				.setLineJoin(LineJoin.ROUND);
		g.draw(new Rectangle(0, 0, 100, 100).getOutline());
		g.draw(new Rectangle(20, 20, 60, 60).getRotatedCCW(Angle.fromDeg(45))
				.getOutline());

		g.translate(150, 150);
		g.draw(PolyBezier.interpolateCubic(new Point()));
	}

}
