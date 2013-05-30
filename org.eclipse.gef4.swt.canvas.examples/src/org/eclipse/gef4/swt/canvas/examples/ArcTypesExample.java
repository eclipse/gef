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
package org.eclipse.gef4.swt.canvas.examples;

import org.eclipse.gef4.swt.canvas.Group;
import org.eclipse.gef4.swt.canvas.gc.ArcType;
import org.eclipse.gef4.swt.canvas.gc.GraphicsContext;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.widgets.Canvas;

public class ArcTypesExample implements IExample {

	public static void main(String[] args) {
		new Example(new ArcTypesExample());
	}

	private Canvas c;

	@Override
	public void addUi(Group c) {
		this.c = c;
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

	@Override
	public void render(GraphicsContext g) {
		long time = System.currentTimeMillis();

		g.clearRect(0, 0, c.getSize().x, c.getSize().y);
		g.setFont(new FontData("Times", 12, SWT.BOLD));

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

		System.out.println("render time = "
				+ (System.currentTimeMillis() - time) + "ms");
	}

}
