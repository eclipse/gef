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
 * A Font is a lightweight object storing font information, such as the font
 * family, the font size, and the font style.
 * 
 * @author mwienand
 * 
 */
public class Font {

	/**
	 * A normal font style indicates that the font is neither bold, nor italic,
	 * nor underlined.
	 */
	public static final int STYLE_NORMAL = 0x0;

	/**
	 * A bold font style increases the weight of the font.
	 */
	public static final int STYLE_BOLD = 0x1;

	/**
	 * A font with italic style will draw the text cursive.
	 */
	public static final int STYLE_ITALIC = 0x2;

	/**
	 * An underlined font style indicates that text will be drawn underlined.
	 */
	public static final int STYLE_UNDERLINED = 0x4;

	/**
	 * The default font family is set to "Times New Roman".
	 */
	public static final String DEFAULT_FAMILY = "Times New Roman";

	/**
	 * The default font size is set to 14 points.
	 */
	public static final double DEFAULT_SIZE = 14;

	/**
	 * The default font style is set to {@link #STYLE_NORMAL}.
	 */
	public static final int DEFAULT_STYLE = STYLE_NORMAL;

	private static final int MINIMUM_STYLE_VALUE = 0;
	private static final int MAXIMUM_STYLE_VALUE = STYLE_NORMAL | STYLE_BOLD
			| STYLE_ITALIC | STYLE_UNDERLINED;

	private static boolean isBold(final int style) {
		return (style & STYLE_BOLD) != 0;
	}

	private static boolean isItalic(final int style) {
		return (style & STYLE_ITALIC) != 0;
	}

	private static boolean isUnderlined(final int style) {
		return (style & STYLE_UNDERLINED) != 0;
	}

	/**
	 * The font family associated with this {@link Font}.
	 */
	protected String family = DEFAULT_FAMILY;

	/**
	 * The font size associated with this {@link Font}.
	 */
	protected double size = DEFAULT_SIZE;

	/**
	 * The font style associated with this {@link Font}.
	 */
	protected int style = DEFAULT_STYLE;

	/**
	 * The default constructor, initializing the attributes of this {@link Font}
	 * to the following default values:
	 * <ul>
	 * <li><code>{@link #family} = {@link #DEFAULT_FAMILY}</code></li>
	 * <li><code>{@link #size} = {@link #DEFAULT_SIZE}</code></li>
	 * <li><code>{@link #style} = {@link #DEFAULT_STYLE}</code></li>
	 * </ul>
	 */
	public Font() {
	}

	/**
	 * Constructs a new {@link Font} and associates it with the given values.
	 * 
	 * @param family
	 *            the font family to use
	 * @param size
	 *            the font size to use
	 * @param style
	 *            the font style to use
	 */
	public Font(String family, double size, int style) {
		setFamily(family);
		setSize(size);
		setStyle(style);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Font) {
			Font o = (Font) obj;
			return style == o.style && size == o.size
					&& family.equals(o.family);
		}
		return false;
	}

	/**
	 * Returns a copy of this {@link Font}.
	 * 
	 * @return a copy of this {@link Font}
	 */
	public Font getCopy() {
		return new Font(family, size, style);
	}

	/**
	 * Returns the font family associated with this {@link Font}.
	 * 
	 * @return the font family associated with this {@link Font}
	 */
	public String getFamily() {
		return family;
	}

	/**
	 * Returns the font size associated with this {@link Font}.
	 * 
	 * @return the font size associated with this {@link Font}
	 */
	public double getSize() {
		return size;
	}

	/**
	 * Returns the font style associated with this {@link Font}.
	 * 
	 * @return the font style associated with this {@link Font}
	 */
	public int getStyle() {
		return style;
	}

	/**
	 * Returns <code>true</code> if the {@link #STYLE_BOLD} flag of the
	 * {@link #style} associated with this {@link Font} is set. Otherwise,
	 * returns <code>false</code>.
	 * 
	 * @return <code>true</code> if the {@link #STYLE_BOLD} flag of the
	 *         {@link #style} associated with this {@link Font} is set,
	 *         otherwise <code>false</code>
	 */
	public boolean isBold() {
		return isBold(style);
	}

	/**
	 * Returns <code>true</code> if the {@link #STYLE_ITALIC} flag of the
	 * {@link #style} associated with this {@link Font} is set. Otherwise,
	 * returns <code>false</code>.
	 * 
	 * @return <code>true</code> if the {@link #STYLE_ITALIC} flag of the
	 *         {@link #style} associated with this {@link Font} is set,
	 *         otherwise <code>false</code>
	 */
	public boolean isItalic() {
		return isItalic(style);
	}

	/**
	 * Returns <code>true</code> if the {@link #style} associated with this
	 * {@link Font} is set to {@link #STYLE_NORMAL}. Otherwise, returns
	 * <code>false</code>.
	 * 
	 * @return <code>true</code> if the {@link #style} associated with this
	 *         {@link Font} is set to {@link #STYLE_NORMAL}, otherwise
	 *         <code>false</code>
	 */
	public boolean isNormal() {
		return !isBold(style) && !isItalic(style) && !isUnderlined(style);
	}

	/**
	 * Returns <code>true</code> if the {@link #STYLE_UNDERLINED} flag of the
	 * {@link #style} associated with this {@link Font} is set. Otherwise,
	 * returns <code>false</code>.
	 * 
	 * @return <code>true</code> if the {@link #STYLE_UNDERLINED} flag of the
	 *         {@link #style} associated with this {@link Font} is set,
	 *         otherwise <code>false</code>
	 */
	public boolean isUnderlined() {
		return isUnderlined(style);
	}

	/**
	 * Sets the font family associated with this {@link Font} to the passed-in
	 * value.
	 * 
	 * @param family
	 *            the new font family for this {@link Font}
	 * @return <code>this</code> for convenience
	 */
	public Font setFamily(final String family) {
		// TODO: validation
		this.family = family;
		return this;
	}

	/**
	 * Sets the font size associated with this {@link Font} to the passed-in
	 * value (in points).
	 * 
	 * @param size
	 *            the new font size for this {@link Font}
	 * @return <code>this</code> for convenience
	 */
	public Font setSize(final double size) {
		// TODO: validation
		this.size = size;
		return this;
	}

	/**
	 * Sets the font style associated with this {@link Font} to the passed-in
	 * value.
	 * 
	 * @param style
	 *            the new font style for this {@link Font}
	 * @return <code>this</code> for convenience
	 */
	public Font setStyle(final int style) {
		if (MINIMUM_STYLE_VALUE > style || style > MAXIMUM_STYLE_VALUE) {
			throw new IllegalArgumentException("The given style (bold = "
					+ isBold(style) + ", italic = " + isItalic(style)
					+ ", underlined = " + isUnderlined(style) + ", rest = "
					+ (style & ~MAXIMUM_STYLE_VALUE)
					+ ") is invalid, because it stores additional information (the rest value).");
		}
		this.style = style;
		return this;
	}

	/**
	 * Sets the family, size, and style of this {@link Font} to the family,
	 * size, and style of the given other {@link Font}, respectively.
	 * 
	 * @param font
	 *            the {@link Font} that provides the new family, size, and style
	 *            values
	 * @return <code>this</code> for convenience
	 */
	public Font setTo(Font font) {
		setFamily(font.getFamily());
		setSize(font.getSize());
		setStyle(font.getStyle());
		return this;
	}

	@Override
	public String toString() {
		return "Font(family = " + family + ", size = " + size + ", style = "
				+ style + ")";
	}

}
