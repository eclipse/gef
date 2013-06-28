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
package org.eclipse.gef4.swt.fx.examples;

import org.eclipse.gef4.geometry.planar.Rectangle;
import org.eclipse.gef4.swt.fx.Group;
import org.eclipse.gef4.swt.fx.ShapeFigure;
import org.eclipse.gef4.swt.fx.gc.ArcType;
import org.eclipse.gef4.swt.fx.gc.GraphicsContext;
import org.eclipse.gef4.swt.fx.gc.RgbaColor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.FontData;

public class ArcTypesExample implements IExample {

	public static void main(String[] args) {
		new Example(new ArcTypesExample());
	}

	@Override
	public void addUi(Group c) {
		ShapeFigure f = new ShapeFigure(new Rectangle(0, 0, 640, 480)) {
			{
				getPaintStateByReference().getFillByReference().setColor(
						new RgbaColor(0xffffffff));
				getPaintStateByReference().setFontDataByReference(
						new FontData("Times", 12, SWT.BOLD));
			}

			@Override
			public void paint(GraphicsContext g) {
				super.paint(g); // background

				g.fillText("stroke", 40, 0);
				g.fillText("fill", 160, 0);

				g.translate(0, 20);
				g.fillText("ArcType.OPEN", 260, 40);
				g.fillText("ArcType.CHORD", 260, 160);
				g.fillText("ArcType.ROUND", 260, 280);

				g.strokeArc(20, 20, 100, 100, 0, 270, ArcType.OPEN);
				g.strokeArc(20, 140, 100, 100, 0, 270, ArcType.CHORD);
				g.strokeArc(20, 260, 100, 100, 0, 270, ArcType.ROUND);

				g.fillArc(140, 20, 100, 100, 0, 270, ArcType.OPEN);
				g.fillArc(140, 140, 100, 100, 0, 270, ArcType.CHORD);
				g.fillArc(140, 260, 100, 100, 0, 270, ArcType.ROUND);
			}
		};
		c.addFigures(f);
	}

	@Override
	public int getHeight() {
		return 480;
	}

	@Override
	public String getTitle() {
		return "ArcType Example";
	}

	@Override
	public int getWidth() {
		return 640;
	}

}
