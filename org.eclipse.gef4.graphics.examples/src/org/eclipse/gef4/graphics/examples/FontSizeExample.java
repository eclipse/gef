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

import org.eclipse.gef4.geometry.planar.Dimension;
import org.eclipse.gef4.graphics.IGraphics;

public class FontSizeExample implements IExample {

	private static final int PAD = 10;

	@Override
	public int getHeight() {
		return 600;
	}

	@Override
	public String getTitle() {
		return "Font Size Example";
	}

	@Override
	public int getWidth() {
		return 800;
	}

	@Override
	public void renderScene(IGraphics g) {
		double y = PAD, x = PAD;
		g.translate(x, y);
		for (double size = 3.5; size < 33; size += 0.5) {
			String text = "Text in size " + size + "pt";
			g.setFontSize(size);
			Dimension dim = g.getTextDimension(text);
			y += dim.height;
			if (y > getHeight()) {
				g.translate(PAD + dim.width, PAD + dim.height - y);
				y = dim.height + PAD;
			}
			g.write(text);
			g.translate(0, dim.height);
		}
	}

}
