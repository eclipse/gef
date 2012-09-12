/*******************************************************************************
 * Copyright (c) 2012 itemis AG and others.
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
package org.eclipse.gef4.graphics.internal.swt;

import org.eclipse.gef4.geometry.planar.Dimension;
import org.eclipse.gef4.graphics.IFontUtils;
import org.eclipse.gef4.graphics.IGraphics;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;

public class FontUtils implements IFontUtils {

	protected IGraphics graphics;

	public FontUtils(IGraphics graphics) {
		this.graphics = graphics;
	}

	public Dimension getTextDimension(String text) {
		graphics.pushState();
		graphics.writeProperties().applyOn(graphics, "");
		GC gc = ((DisplayGraphics) graphics).getGC();
		Point extent = gc.stringExtent(text);
		graphics.popState();
		return new Dimension(extent.x, extent.y);
	}

}
