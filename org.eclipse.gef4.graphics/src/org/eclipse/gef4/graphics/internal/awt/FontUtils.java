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
package org.eclipse.gef4.graphics.internal.awt;

import java.awt.FontMetrics;
import java.awt.Graphics2D;

import org.eclipse.gef4.geometry.planar.Dimension;
import org.eclipse.gef4.graphics.IFontUtils;
import org.eclipse.gef4.graphics.IGraphics;

public class FontUtils implements IFontUtils {

	protected IGraphics graphics;

	public FontUtils(IGraphics graphics) {
		this.graphics = graphics;
	}

	public Dimension getTextDimension(String text) {
		Graphics2D g2d = ((DisplayGraphics) graphics).getGraphics2D();
		FontMetrics fontMetrics = g2d.getFontMetrics();
		return new Dimension(fontMetrics.stringWidth(text),
				fontMetrics.getHeight());
	}

}
