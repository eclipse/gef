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

import java.awt.Graphics2D;

import org.eclipse.gef4.graphics.AbstractGraphics;
import org.eclipse.gef4.graphics.IFontUtils;
import org.eclipse.gef4.graphics.IGraphics;
import org.eclipse.gef4.graphics.IImageUtils;

/**
 * The AWT {@link IGraphics} implementation used to draw to the screen.
 * 
 * @author mwienand
 * 
 */
public class DisplayGraphics extends AbstractGraphics {

	private Graphics2D g;

	/**
	 * Constructs a {@link DisplayGraphics} from the given {@link Graphics2D}.
	 * 
	 * @param g2d
	 */
	public DisplayGraphics(Graphics2D g2d) {
		this.g = g2d;

		CanvasProperties cp = new CanvasProperties();
		DrawProperties dp = new DrawProperties();
		FillProperties fp = new FillProperties();
		BlitProperties bp = new BlitProperties();
		WriteProperties wp = new WriteProperties();

		pushInitialState(cp, dp, fp, bp, wp);
	}

	public IFontUtils fontUtils() {
		return new FontUtils(this);
	}

	/**
	 * Returns the {@link Graphics2D} that is associated with this
	 * {@link DisplayGraphics}.
	 * 
	 * @return the {@link Graphics2D} that is associated with this
	 *         {@link DisplayGraphics}
	 */
	public Graphics2D getGraphics2D() {
		return g;
	}

	public IImageUtils imageUtils() {
		return new ImageUtils();
	}

}
