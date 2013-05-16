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
package org.eclipse.gef4.graphics.examples.doc;

import org.eclipse.gef4.geometry.planar.Dimension;
import org.eclipse.gef4.geometry.planar.Line;
import org.eclipse.gef4.geometry.planar.Point;
import org.eclipse.gef4.geometry.planar.PolyBezier;
import org.eclipse.gef4.geometry.planar.Polyline;
import org.eclipse.gef4.geometry.planar.RoundedRectangle;
import org.eclipse.gef4.graphics.Gradient;
import org.eclipse.gef4.graphics.Gradient.CycleMode;
import org.eclipse.gef4.graphics.IGraphics;
import org.eclipse.gef4.graphics.color.Color;
import org.eclipse.gef4.graphics.examples.IExample;
import org.eclipse.gef4.graphics.examples.SwtExample;
import org.eclipse.gef4.graphics.font.Font;

public class Example005 implements IExample {

	public static void main(String[] args) {
		new SwtExample(new Example005());
	}

	/**
	 * @param g
	 * @param arrowHead
	 */
	private void drawDownArrow(IGraphics g, Point arrowHead) {
		g.draw(new Line(arrowHead, arrowHead.getTranslated(-10, -10)));
		g.draw(new Line(arrowHead, arrowHead.getTranslated(10, -10)));
	}

	/**
	 * @param g
	 * @param nodeShape
	 * @param nodeShapeOutline
	 * @param nodeText
	 * @param textOffset
	 */
	private void drawNode(IGraphics g, RoundedRectangle nodeShape,
			PolyBezier nodeShapeOutline, String nodeText, Dimension textOffset) {
		g.fill(nodeShape).draw(nodeShapeOutline);
		g.translate(textOffset.width, textOffset.height).write(nodeText);
	}

	@Override
	public int getHeight() {
		return 480;
	}

	@Override
	public String getTitle() {
		return "Example 005 - States Stack";
	}

	@Override
	public int getWidth() {
		return 640;
	}

	@Override
	public void renderScene(IGraphics g) {
		g.setDeviceDpi(g.getLogicalDpi()); // work in pixels

		RoundedRectangle nodeShape = new RoundedRectangle(0, 0, 200, 100, 20,
				20);
		PolyBezier nodeShapeOutline = nodeShape.getOutline();
		String nodeText = "Node";
		Dimension textOffset = g.getTextDimension(nodeText);
		textOffset.width *= -0.5;
		textOffset.width += nodeShape.getWidth() / 2;
		textOffset.height = 10;

		g.translate(50, 50);
		g.setFill(new Gradient.Linear(nodeShape.getTop().getP1(), nodeShape
				.getBottom().getP1(), CycleMode.NO_CYCLE)
				.addStop(0, new Color(164, 198, 255))
				.addStop(0.5, new Color(128, 164, 255))
				.addStop(1, new Color(92, 128, 255)));
		g.setFontStyle(Font.STYLE_UNDERLINED);
		g.pushState();
		drawNode(g, nodeShape, nodeShapeOutline, nodeText, textOffset);

		g.restoreState();
		g.translate(0, 2 * nodeShape.getHeight());
		drawNode(g, nodeShape, nodeShapeOutline, nodeText, textOffset);

		g.restoreState();
		g.translate(1.5 * nodeShape.getWidth(), 2 * nodeShape.getHeight());
		drawNode(g, nodeShape, nodeShapeOutline, nodeText, textOffset);

		g.restoreState();
		g.translate(0.5 * nodeShape.getWidth(), nodeShape.getHeight());
		Point arrowHead = new Point(0, nodeShape.getHeight());
		g.draw(new Line(new Point(), arrowHead));
		drawDownArrow(g, arrowHead);

		g.restoreState();
		g.translate(nodeShape.getWidth(), 0.5 * nodeShape.getHeight());
		arrowHead = new Point(nodeShape.getWidth(), 1.5 * nodeShape.getHeight());
		g.draw(new Polyline(new Point(), new Point(nodeShape.getWidth(), 0),
				arrowHead));
		drawDownArrow(g, arrowHead);
	}
}
