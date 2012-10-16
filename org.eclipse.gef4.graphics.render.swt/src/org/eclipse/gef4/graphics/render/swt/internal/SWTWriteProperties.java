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
package org.eclipse.gef4.graphics.render.swt.internal;

import org.eclipse.gef4.graphics.Color;
import org.eclipse.gef4.graphics.Font;
import org.eclipse.gef4.graphics.render.AbstractWriteProperties;
import org.eclipse.gef4.graphics.render.IGraphics;
import org.eclipse.gef4.graphics.render.IWriteProperties;
import org.eclipse.gef4.graphics.render.swt.SWTGraphics;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;

public class SWTWriteProperties extends AbstractWriteProperties {

	protected GCLazyProperty<Color, org.eclipse.swt.graphics.Color> foregroundColorProperty = new GCLazyProperty<Color, org.eclipse.swt.graphics.Color>() {
		@Override
		protected org.eclipse.swt.graphics.Color generate(Color data) {
			return SWTGraphicsUtils.createSWTColor(data);
		}

		@Override
		protected void write(GC gc, org.eclipse.swt.graphics.Color val) {
			gc.setForeground(val);
		}
	};

	protected GCLazyProperty<Color, org.eclipse.swt.graphics.Color> backgroundColorProperty = new GCLazyProperty<Color, org.eclipse.swt.graphics.Color>() {
		@Override
		protected org.eclipse.swt.graphics.Color generate(Color data) {
			return SWTGraphicsUtils.createSWTColor(data);
		}

		@Override
		protected void write(GC gc, org.eclipse.swt.graphics.Color val) {
			gc.setBackground(val);
		}
	};

	protected GCLazyProperty<Font, org.eclipse.swt.graphics.Font> fontProperty = new GCLazyProperty<Font, org.eclipse.swt.graphics.Font>() {
		@Override
		protected org.eclipse.swt.graphics.Font generate(Font data) {
			return SWTGraphicsUtils.createSWTFont(data);
		}

		@Override
		protected void write(GC gc, org.eclipse.swt.graphics.Font val) {
			gc.setFont(val);
		}
	};

	private org.eclipse.swt.graphics.Color initialForegroundColor,
	initialBackgroundColor;

	/**
	 * Creates a new {@link SWTWriteProperties} and initializes the background
	 * color, foreground color, and the font.
	 */
	public SWTWriteProperties() {
		backgroundColorProperty.set(new Color(
				IWriteProperties.DEFAULT_BACKGROUND_COLOR_R,
				IWriteProperties.DEFAULT_BACKGROUND_COLOR_G,
				IWriteProperties.DEFAULT_BACKGROUND_COLOR_B,
				IWriteProperties.DEFAULT_BACKGROUND_COLOR_A));
		foregroundColorProperty.set(new Color(
				IWriteProperties.DEFAULT_FOREGROUND_COLOR_R,
				IWriteProperties.DEFAULT_FOREGROUND_COLOR_G,
				IWriteProperties.DEFAULT_FOREGROUND_COLOR_B,
				IWriteProperties.DEFAULT_FOREGROUND_COLOR_A));
		fontProperty.set(new Font());
	}

	public void applyOn(IGraphics g, String text) {
		GC gc = ((SWTGraphics) g).getGC();

		gc.setAntialias(antialiasing ? SWT.ON : SWT.OFF);

		foregroundColorProperty.apply(gc);
		backgroundColorProperty.apply(gc);
		fontProperty.apply(gc);

		boolean transparentBackground = getBackgroundColor().getAlpha() < 128;
		gc.drawText(text, 0, 0, transparentBackground);
	}

	public void cleanUp(IGraphics g) {
		GC gc = ((SWTGraphics) g).getGC();
		gc.setForeground(initialForegroundColor);
		gc.setBackground(initialBackgroundColor);
		gc.setFont(null);
		foregroundColorProperty.clean();
		backgroundColorProperty.clean();
		fontProperty.clean();
	}

	public Color getBackgroundColor() {
		return backgroundColorProperty.data.getCopy();
	}

	public SWTWriteProperties getCopy() {
		SWTWriteProperties copy = new SWTWriteProperties();
		copy.setForegroundColor(getForegroundColor());
		copy.setBackgroundColor(getBackgroundColor());
		copy.setFont(getFont());
		copy.antialiasing = antialiasing;
		return copy;
	}

	public Font getFont() {
		return fontProperty.data.getCopy();
	}

	public Color getForegroundColor() {
		return foregroundColorProperty.data.getCopy();
	}

	public void init(IGraphics g) {
		GC gc = ((SWTGraphics) g).getGC();
		initialForegroundColor = gc.getForeground();
		initialBackgroundColor = gc.getBackground();
	}

	public SWTWriteProperties setBackgroundColor(Color backgroundColor) {
		backgroundColorProperty.set(backgroundColor);
		return this;
	}

	public SWTWriteProperties setFont(Font font) {
		fontProperty.set(font);
		return this;
	}

	public SWTWriteProperties setForegroundColor(Color foregroundColor) {
		foregroundColorProperty.set(foregroundColor);
		return this;
	}

}
