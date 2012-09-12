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
package org.eclipse.gef4.graphics;

/**
 * <p>
 * An {@link IWriteProperties} manages the {@link IGraphics} properties used
 * when displaying a text using the {@link IGraphics#write(String)} method.
 * </p>
 * 
 * @author mwienand
 * 
 */
public interface IWriteProperties extends IGraphicsProperties {

	/**
	 * Anti-aliasing is enabled per default.
	 */
	static final boolean DEFAULT_ANTIALIASING = true;

	/**
	 * The default {@link Color background color} for text is white.
	 */
	static final int DEFAULT_BACKGROUND_COLOR_R = 255;

	/**
	 * The default {@link Color background color} for text is white.
	 */
	static final int DEFAULT_BACKGROUND_COLOR_G = 255;

	/**
	 * The default {@link Color background color} for text is white.
	 */
	static final int DEFAULT_BACKGROUND_COLOR_B = 255;

	/**
	 * The default {@link Color background color} for text is fully transparent.
	 */
	static final int DEFAULT_BACKGROUND_COLOR_A = 0;

	/**
	 * The default {@link Color foreground color} for text is black.
	 */
	static final int DEFAULT_FOREGROUND_COLOR_R = 0;

	/**
	 * The default {@link Color foreground color} for text is black.
	 */
	static final int DEFAULT_FOREGROUND_COLOR_G = 0;

	/**
	 * The default {@link Color foreground color} for text is black.
	 */
	static final int DEFAULT_FOREGROUND_COLOR_B = 0;

	/**
	 * The default {@link Color foreground color} for text is fully opaque.
	 */
	static final int DEFAULT_FOREGROUND_COLOR_A = 255;

	/**
	 * Applies the {@link IWriteProperties} stored in this object to the
	 * underlying graphics system of the passed-in {@link IGraphics}. This
	 * operation renders the given {@link String}. It is called when the
	 * {@link IGraphics#write(String)} method is called.
	 * 
	 * @param g
	 *            the {@link IGraphics} to apply the {@link IWriteProperties} on
	 * @param text
	 *            the {@link String} to render
	 */
	void applyOn(IGraphics g, String text);

	/**
	 * Returns the {@link Color background color} that is associated with this
	 * {@link IWriteProperties}.
	 * 
	 * @return the {@link Color background color} that is associated with this
	 *         {@link IWriteProperties}
	 */
	Color getBackgroundColor();

	IWriteProperties getCopy();

	/**
	 * Returns the {@link Font} that is associated with this
	 * {@link IWriteProperties}.
	 * 
	 * @return the {@link Font} that is associated with this
	 *         {@link IWriteProperties}
	 */
	Font getFont();

	/**
	 * Returns the {@link Color foreground color} that is associated with this
	 * {@link IWriteProperties}.
	 * 
	 * @return the {@link Color foreground color} that is associated with this
	 *         {@link IWriteProperties}
	 */
	Color getForegroundColor();

	/**
	 * Returns <code>true</code> if anti-aliasing is enabled. Otherwise,
	 * <code>false</code> is returned.
	 * 
	 * @return <code>true</code> if anti-aliasing is enabled, otherwise
	 *         <code>false</code>
	 */
	boolean isAntialiasing();

	/**
	 * Enables or disables anti-aliasing for this {@link IWriteProperties}
	 * dependent on the given value. Anti-aliasing is enabled for a
	 * <code>true</code> value and disabled for a <code>false</code> value.
	 * 
	 * @param antialiasing
	 *            the new anti-aliasing setting for this
	 *            {@link IWriteProperties}
	 * @return <code>this</code> for convenience
	 */
	IWriteProperties setAntialiasing(boolean antialiasing);

	/**
	 * Sets the {@link Color background color} that is associated with this
	 * {@link IWriteProperties}.
	 * 
	 * @param backgroundColor
	 *            the new {@link Color background color}
	 * @return <code>this</code> for convenience
	 */
	IWriteProperties setBackgroundColor(Color backgroundColor);

	/**
	 * Sets the {@link Font} that is associated with this
	 * {@link IWriteProperties}.
	 * 
	 * @param font
	 *            the new {@link Font}
	 * @return <code>this</code> for convenience
	 */
	IWriteProperties setFont(Font font);

	/**
	 * Sets the {@link Color foreground color} that is associated with this
	 * {@link IWriteProperties}.
	 * 
	 * @param foregroundColor
	 *            the new {@link Color foreground color}
	 * @return <code>this</code> for convenience
	 */
	IWriteProperties setForegroundColor(Color foregroundColor);

}
