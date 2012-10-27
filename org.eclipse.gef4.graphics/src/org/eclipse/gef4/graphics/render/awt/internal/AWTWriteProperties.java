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
import java.awt.RenderingHints;

import org.eclipse.gef4.graphics.Color;
import org.eclipse.gef4.graphics.Font;
import org.eclipse.gef4.graphics.render.AbstractWriteProperties;
import org.eclipse.gef4.graphics.render.IGraphics;
import org.eclipse.gef4.graphics.render.IWriteProperties;
import org.eclipse.gef4.graphics.render.awt.AWTGraphics;

/**
 * The {@link IWriteProperties} implemention for AWT.
 * 
 * @author mwienand
 */
public class AWTWriteProperties extends AbstractWriteProperties {

	/**
	 * The current {@link Color background color} associated with this
	 * {@link AbstractWriteProperties}.
	 */
	protected Color backgroundColor;

	/**
	 * The current {@link Color foreground color} associated with this
	 * {@link AbstractWriteProperties}.
	 */
	protected Color foregroundColor;

	/**
	 * The current {@link Font} associated with this
	 * {@link AbstractWriteProperties}.
	 */
	protected Font font;

	/**
	 * <p>
	 * Constructs a new {@link AWTWriteProperties} with default background and
	 * foreground color, and default font.
	 * </p>
	 * 
	 * <p>
	 * The default values for the
	 * </p>
	 */
	public AWTWriteProperties() {
		backgroundColor = new Color(
				IWriteProperties.DEFAULT_BACKGROUND_COLOR_R,
				IWriteProperties.DEFAULT_BACKGROUND_COLOR_G,
				IWriteProperties.DEFAULT_BACKGROUND_COLOR_B,
				IWriteProperties.DEFAULT_BACKGROUND_COLOR_A);
		foregroundColor = new Color(
				IWriteProperties.DEFAULT_FOREGROUND_COLOR_R,
				IWriteProperties.DEFAULT_FOREGROUND_COLOR_G,
				IWriteProperties.DEFAULT_FOREGROUND_COLOR_B,
				IWriteProperties.DEFAULT_FOREGROUND_COLOR_A);
		font = new Font();
	}

	public void applyOn(IGraphics graphics, String text) {
		Graphics2D g = ((AWTGraphics) graphics).getGraphics2D();

		g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
				antialiasing ? RenderingHints.VALUE_TEXT_ANTIALIAS_ON
						: RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
		g.setColor(AWTGraphicsUtils.toAWTColor(foregroundColor));
		g.setBackground(AWTGraphicsUtils.toAWTColor(backgroundColor));
		g.setFont(AWTGraphicsUtils.toAWTFont(font));

		FontMetrics fontMetrics = g.getFontMetrics();
		g.drawString(text, 0, fontMetrics.getMaxAscent());
	}

	public void cleanUp(IGraphics g) {
		// nothing to do
	}

	public Color getBackgroundColor() {
		return backgroundColor.getCopy();
	}

	public AWTWriteProperties getCopy() {
		AWTWriteProperties copy = new AWTWriteProperties();
		copy.setAntialiasing(antialiasing);
		copy.setBackgroundColor(backgroundColor);
		copy.setForegroundColor(foregroundColor);
		copy.setFont(font);
		return copy;
	}

	public Font getFont() {
		return font.getCopy();
	}

	public Color getForegroundColor() {
		return foregroundColor.getCopy();
	}

	public void init(IGraphics g) {
		// nothing to do
	}

	public IWriteProperties setBackgroundColor(Color backgroundColor) {
		this.backgroundColor.setTo(backgroundColor);
		return this;
	}

	public IWriteProperties setFont(Font font) {
		this.font.setTo(font);
		return this;
	}

	public IWriteProperties setForegroundColor(Color foregroundColor) {
		this.foregroundColor.setTo(foregroundColor);
		return this;
	}

}
