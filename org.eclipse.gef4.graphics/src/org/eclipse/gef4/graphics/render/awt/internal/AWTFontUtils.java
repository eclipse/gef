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
package org.eclipse.gef4.graphics.render.awt.internal;

import java.awt.FontMetrics;
import java.awt.Graphics2D;

import org.eclipse.gef4.geometry.planar.Dimension;
import org.eclipse.gef4.graphics.render.IFontUtils;
import org.eclipse.gef4.graphics.render.IGraphics;
import org.eclipse.gef4.graphics.render.awt.AWTGraphics;

/**
 * The AWT {@link IFontUtils} implementation.
 * 
 * @author mwienand
 * 
 */
public class AWTFontUtils implements IFontUtils {

	private IGraphics graphics;

	/**
	 * Constructs a new {@link AWTFontUtils} for the passed-in {@link IGraphics}
	 * .
	 * 
	 * @param graphics
	 */
	public AWTFontUtils(IGraphics graphics) {
		this.graphics = graphics;
	}

	/**
	 * Returns the {@link IGraphics} implementation for which this
	 * {@link AWTFontUtils} was constructed.
	 * 
	 * @return the {@link IGraphics} implementation for which this
	 *         {@link AWTFontUtils} was constructed.
	 */
	protected IGraphics getGraphics() {
		return graphics;
	}

	public Dimension getTextDimension(String text) {
		Graphics2D g2d = ((AWTGraphics) graphics).getGraphics2D();
		FontMetrics fontMetrics = g2d.getFontMetrics();
		return new Dimension(fontMetrics.stringWidth(text),
				fontMetrics.getHeight());
	}

	/**
	 * Sets the {@link IGraphics} that is associated with this
	 * {@link AWTFontUtils} to the given value.
	 * 
	 * @param newGraphics
	 *            the new {@link IGraphics} to be associated with this
	 *            {@link AWTFontUtils}
	 * @return <code>this</code> for convenience
	 */
	protected AWTFontUtils setGraphics(IGraphics newGraphics) {
		graphics = newGraphics;
		return this;
	}

}
