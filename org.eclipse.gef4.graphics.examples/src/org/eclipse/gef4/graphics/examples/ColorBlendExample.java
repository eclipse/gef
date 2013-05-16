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

import org.eclipse.gef4.geometry.planar.Rectangle;
import org.eclipse.gef4.graphics.IGraphics;
import org.eclipse.gef4.graphics.color.Color;

public class ColorBlendExample implements IExample {

	private static final int PAD = 10;
	private static final Rectangle SMALL = new Rectangle(0, 0, 50, 50);
	private static final Rectangle BIG = new Rectangle(0, 0, 2
			* SMALL.getWidth() + PAD, SMALL.getHeight());

	@Override
	public int getHeight() {
		return 300;
	}

	@Override
	public String getTitle() {
		return "Color Blend Example";
	}

	@Override
	public int getWidth() {
		return 400;
	}

	@Override
	public void renderScene(IGraphics g) {
		g.pushState();

		Color white = new Color(255, 255, 255);
		Color red = new Color(255, 0, 0);
		Color whiteRed = showBlend(g, white, red);

		Color cyan = new Color(0, 255, 255);
		Color magenta = new Color(255, 0, 255);
		g.translate(2 * PAD + BIG.getWidth(), 0);
		Color cyanMagenta = showBlend(g, cyan, magenta);

		Color orange = new Color(255, 128, 0);
		Color lightBlue = new Color(137, 207, 230);
		g.translate(2 * PAD + BIG.getWidth(), 0);
		Color orangeBlue = showBlend(g, orange, lightBlue);

		g.restoreState();
		g.translate(0, 3 * PAD + BIG.getHeight() + SMALL.getHeight());

		Color green = new Color(0, 255, 0);
		Color yellow = new Color(255, 255, 0);
		Color greenYellow = showBlend(g, green, yellow);

		g.translate(2 * PAD + BIG.getWidth(), 0);
		showBlend(g, orangeBlue, cyanMagenta);

		g.translate(2 * PAD + BIG.getWidth(), 0);
		showBlend(g, greenYellow, whiteRed);

		g.popState();
	}

	private Color showBlend(IGraphics g, Color c1, Color c2) {
		Color blended = c1.getBlended(c2, 0.5, 2.2);

		g.pushState();
		g.translate(PAD, PAD);
		g.setFill(c1).fill(SMALL);
		g.translate(SMALL.getWidth() + PAD, 0);
		g.setFill(c2).fill(SMALL);
		g.translate(-SMALL.getWidth() - PAD, SMALL.getHeight() + PAD);
		g.setFill(blended).fill(BIG);
		g.popState();

		return blended;
	}

}
