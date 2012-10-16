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
package org.eclipse.gef4.graphics.render.awt;

import java.awt.Graphics2D;

import org.eclipse.gef4.graphics.render.AbstractGraphics;
import org.eclipse.gef4.graphics.render.IFontUtils;
import org.eclipse.gef4.graphics.render.IGraphics;
import org.eclipse.gef4.graphics.render.awt.internal.AWTBlitProperties;
import org.eclipse.gef4.graphics.render.awt.internal.AWTCanvasProperties;
import org.eclipse.gef4.graphics.render.awt.internal.AWTDrawProperties;
import org.eclipse.gef4.graphics.render.awt.internal.AWTFillProperties;
import org.eclipse.gef4.graphics.render.awt.internal.AWTFontUtils;
import org.eclipse.gef4.graphics.render.awt.internal.AWTWriteProperties;

/**
 * The AWT {@link IGraphics} implementation used to draw to the screen.
 * 
 * @author mwienand
 * 
 */
public class AWTGraphics extends AbstractGraphics {

	private Graphics2D g;

	/**
	 * Constructs a {@link AWTGraphics} from the given {@link Graphics2D}.
	 * 
	 * @param g2d
	 */
	public AWTGraphics(Graphics2D g2d) {
		this.g = g2d;

		AWTCanvasProperties cp = new AWTCanvasProperties();
		AWTDrawProperties dp = new AWTDrawProperties();
		AWTFillProperties fp = new AWTFillProperties();
		AWTBlitProperties bp = new AWTBlitProperties();
		AWTWriteProperties wp = new AWTWriteProperties();

		pushInitialState(cp, dp, fp, bp, wp);
	}

	public IFontUtils fontUtils() {
		return new AWTFontUtils(this);
	}

	/**
	 * Returns the {@link Graphics2D} that is associated with this
	 * {@link AWTGraphics}.
	 * 
	 * @return the {@link Graphics2D} that is associated with this
	 *         {@link AWTGraphics}
	 */
	public Graphics2D getGraphics2D() {
		return g;
	}

}
