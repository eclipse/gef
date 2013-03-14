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

public class ForegroundPatternExample implements IExample {

	@Override
	public int getHeight() {
		return 480;
	}

	@Override
	public String getTitle() {
		return "GEF4 Graphics - Draw Pattern";
	}

	@Override
	public int getWidth() {
		return 640;
	}

	@Override
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
