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

import org.eclipse.gef4.geometry.planar.Ellipse;
import org.eclipse.gef4.geometry.planar.Rectangle;
import org.eclipse.gef4.graphics.IGraphics;
import org.eclipse.gef4.graphics.color.Color;

public class XorModeExample implements IExample {

	@Override
	public int getHeight() {
		return 480;
	}

	@Override
	public String getTitle() {
		return "GEF4 Graphics - Xor-Mode Example";
	}

	@Override
	public int getWidth() {
		return 640;
	}

	@Override
	public void renderScene(IGraphics g) {
		g.scale(3, 3);
		g.setFill(new Color(0, 0, 255));
		g.fill(new Rectangle(5, 5, 90, 45));
		g.setXorMode(true);
		g.setFill(new Color(255, 255, 255));
		g.fill(new Rectangle(20, 20, 50, 50));
		g.setFill(new Color(255, 0, 0));
		g.fill(new Ellipse(80, 20, 50, 50));
	}

}
