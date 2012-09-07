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

import org.eclipse.gef4.geometry.euclidean.Angle;

/**
 * TODO
 * 
 * @author mwienand
 * 
 */
public class Color {

	/**
	 * TODO: implement IHSV and delete the interface!
	 * 
	 * An {@link IHSV} provides hue, saturation, and value component information
	 * for an {@link Color}. Furthermore, the modification of these values is
	 * facilitated. For example, you can easily brighten an {@link IHSV} by
	 * increasing its value.
	 */
	public interface IHSV {
		/**
		 * Decreases the saturation of this {@link IHSV}. The scale <i>p</i> is
		 * in the range <code>[0;1]</code> and defines the percentage of change.
		 * 
		 * @param p
		 *            the percentage of change (in range <code>[0;1]</code>)
		 * @return <code>this</code> for convenience
		 */
		IHSV decreaseSaturation(double p);

		/**
		 * Decreases the value of this {@link IHSV}. The scale <i>p</i> is in
		 * the range <code>[0;1]</code> and defines the percentage of change.
		 * 
		 * @param p
		 *            the percentage of change (in range <code>[0;1]</code>)
		 * @return <code>this</code> for convenience
		 */
		IHSV decreaseValue(double p);

		/**
		 * Returns the hue of this {@link IHSV}.
		 * 
		 * @return the hue of this {@link IHSV}
		 */
		Angle getHue();

		/**
		 * Returns the saturation of this {@link IHSV} in the range
		 * <code>[0;1]</code>.
		 * 
		 * @return the saturation of this {@link IHSV} (in range
		 *         <code>[0;1]</code>)
		 */
		double getSaturation();

		/**
		 * Returns the value of this {@link IHSV} in the range
		 * <code>[0;1]</code>.
		 * 
		 * @return the value of this {@link IHSV} (in range <code>[0;1]</code>)
		 */
		double getValue();

		/**
		 * Increases the saturation of this {@link IHSV}. The scale <i>p</i> is
		 * in the range <code>[0;1]</code> and defines the percentage of change.
		 * 
		 * @param p
		 *            the percentage of change (in range <code>[0;1]</code>)
		 * @return <code>this</code> for convenience
		 */
		IHSV increaseSaturation(double p);

		/**
		 * Increases the value of this {@link IHSV}. The scale <i>p</i> is in
		 * the range <code>[0;1]</code> and defines the percentage of change.
		 * 
		 * @param p
		 *            the percentage of change (in range <code>[0;1]</code>)
		 * @return <code>this</code> for convenience
		 */
		IHSV increaseValue(double p);

		/**
		 * Sets the hue of this {@link IHSV} to the specified {@link Angle}.
		 * 
		 * @param hue
		 *            the new hue {@link Angle} of this {@link IHSV}
		 * @return <code>this</code> for convenience
		 */
		IHSV setHue(Angle hue);

		/**
		 * Sets the saturation of this {@link IHSV} to the specified value in
		 * the range <code>[0;1]</code>.
		 * 
		 * @param saturation
		 *            the new saturation of this {@link IHSV} (in range
		 *            <code>[0;1]</code>)
		 * @return <code>this</code> for convenience
		 */
		IHSV setSaturation(double saturation);

		/**
		 * Sets the value of this {@link IHSV} to the specified value in the
		 * range <code>[0;1]</code>.
		 * 
		 * @param value
		 *            the new value of this {@link IHSV} (in range
		 *            <code>[0;1]</code>)
		 * @return <code>this</code> for convenience
		 */
		IHSV setValue(double value);
	}

	/**
	 * The <i>alpha</i> value used in the {@link #Color() default constructor}.
	 */
	public static final int DEFAULT_ALPHA = 255;

	/**
	 * The <i>blue</i> value used in the {@link #Color() default constructor}.
	 */
	public static final int DEFAULT_BLUE = 0;

	/**
	 * The <i>green</i> value used in the {@link #Color() default constructor}.
	 */
	public static final int DEFAULT_GREEN = 0;

	/**
	 * The <i>red</i> value used in the {@link #Color() default constructor}.
	 */
	public static final int DEFAULT_RED = 0;

	/**
	 * The red component of this {@link Color} in the range <code>[0;255]</code>
	 * .
	 */
	protected int r;

	/**
	 * The green component of this {@link Color} in the range
	 * <code>[0;255]</code>.
	 */
	protected int g;

	/**
	 * The blue component of this {@link Color} in the range
	 * <code>[0;255]</code>.
	 */
	protected int b;

	/**
	 * The alpha value associated with this {@link Color}.
	 */
	protected int a;

	/**
	 * Constructs a new {@link Color} object representing a fully opaque black,
	 * i.e. <i>r</i><code>=0</code>, <i>g</i><code>=0</code>, <i>b</i>
	 * <code>=0</code>, <i>a</i><code>=255</code>.
	 * 
	 * @see Color#Color(int, int, int, int)
	 */
	public Color() {
		this(DEFAULT_RED, DEFAULT_GREEN, DEFAULT_BLUE, DEFAULT_ALPHA);
	}

	/**
	 * Creates a new {@link Color} from the given values.
	 * 
	 * @param red
	 *            the {@link #r red} component of this {@link Color}
	 * @param green
	 *            the {@link #g green} component of this {@link Color}
	 * @param blue
	 *            the {@link #b blue} component of this {@link Color}
	 * @param alpha
	 *            the {@link #a alpha} component of this {@link Color}
	 */
	public Color(int red, int green, int blue, int alpha) {
		setRed(red);
		setGreen(green);
		setBlue(blue);
		setAlpha(alpha);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Color) {
			Color o = (Color) obj;
			return o.hashCode() == hashCode();
		}
		return false;
	}

	/**
	 * Returns the alpha component of this {@link Color} in the range
	 * <code>[0;255]</code> where <code>0</code> signifies a fully transparent
	 * and <code>255</code> a fully opaque color.
	 * 
	 * @return the alpha component of this {@link Color} (in range
	 *         <code>[0;255]</code>)
	 */
	public int getAlpha() {
		return a;
	}

	/**
	 * Returns the blue component of this {@link Color} in the range
	 * <code>[0;255]</code>.
	 * 
	 * @return the blue component of this {@link Color} in the range
	 *         <code>[0;255]</code>
	 */
	public int getBlue() {
		return b;
	}

	/**
	 * Returns a copy of this {@link Color}.
	 * 
	 * @return a copy of this {@link Color}
	 */
	public Color getCopy() {
		return new Color(r, g, b, a);
	}

	/**
	 * Returns the green component of this {@link Color} in the range
	 * <code>[0;255]</code>.
	 * 
	 * @return the green component of this {@link Color} in the range
	 *         <code>[0;255]</code>
	 */
	public int getGreen() {
		return g;
	}

	/**
	 * Returns an {@link IHSV} color space representation of this {@link Color}
	 * 's {@link #r red}, {@link #g green}, and {@link #b blue} values.
	 * 
	 * @return an {@link IHSV} color space representation of this {@link Color}
	 *         's {@link #r red}, {@link #g green}, and {@link #b blue} values
	 */
	public IHSV getHSV() {
		throw new UnsupportedOperationException(
				"This operation is not yet implemented.");
	}

	/**
	 * Returns the red component of this {@link Color} in the range
	 * <code>[0;255]</code>.
	 * 
	 * @return the red component of this {@link Color} in the range
	 *         <code>[0;255]</code>
	 */
	public int getRed() {
		return r;
	}

	@Override
	public int hashCode() {
		return (r << 24) + (g << 16) + (b << 8) + a;
	}

	/**
	 * Sets the alpha component of this {@link Color} to the specified value in
	 * the range <code>[0;255]</code> where <code>0</code> signifies a fully
	 * transparent color and <code>255</code> signifies a fully opaque color.
	 * 
	 * @param alpha
	 *            the new alpha component of this {@link Color} (in range
	 *            <code>[0;255]</code>)
	 * @return <code>this</code> for convenience
	 */
	public Color setAlpha(int alpha) {
		if (alpha < 0 || alpha > 255) {
			throw new IllegalArgumentException(
					"The given alpha component (alpha = " + alpha
					+ ") is not in the range [0;255]!");
		}
		a = alpha;
		return this;
	}

	/**
	 * Sets the blue component of this {@link Color} to the specified value in
	 * range <code>[0;255]</code>.
	 * 
	 * @param blue
	 *            the new blue component of this {@link Color} (in range
	 *            <code>[0;255]</code>)
	 * @return <code>this</code> for convenience
	 */
	public Color setBlue(int blue) {
		if (blue < 0 || blue > 255) {
			throw new IllegalArgumentException(
					"The given alpha component (alpha = " + blue
					+ ") is not in the range [0;255]!");
		}
		b = blue;
		return this;
	}

	/**
	 * Sets the green component of this {@link Color} to the specified value in
	 * range <code>[0;255]</code>.
	 * 
	 * @param green
	 *            the new green component of this {@link Color} (in range
	 *            <code>[0;255]</code>)
	 * @return <code>this</code> for convenience
	 */
	public Color setGreen(int green) {
		if (green < 0 || green > 255) {
			throw new IllegalArgumentException(
					"The given alpha component (alpha = " + green
					+ ") is not in the range [0;255]!");
		}
		g = green;
		return this;
	}

	/**
	 * Sets the red, green, and blue components of this {@link Color} according
	 * to the passed-in {@link IHSV} color representation.
	 * 
	 * @param hsv
	 * @return <code>this</code> for convenience
	 */
	public Color setHSV(IHSV hsv) {
		throw new UnsupportedOperationException(
				"This operation is not yet implemented.");
	}

	/**
	 * Sets the red component of this {@link Color} to the specified value in
	 * range <code>[0;255]</code>.
	 * 
	 * @param red
	 *            the new red component of this {@link Color} (in range
	 *            <code>[0;255]</code>)
	 * @return <code>this</code> for convenience
	 */
	public Color setRed(int red) {
		if (red < 0 || red > 255) {
			throw new IllegalArgumentException(
					"The given alpha component (alpha = " + red
					+ ") is not in the range [0;255]!");
		}
		r = red;
		return this;
	}

	/**
	 * Sets this {@link Color}'s red, green, blue, and alpha components to those
	 * specified by the passed-in {@link Color} object.
	 * 
	 * @param color
	 * @return <code>this</code> for convenience
	 */
	public Color setTo(Color color) {
		r = color.r;
		g = color.g;
		b = color.b;
		a = color.a;
		return this;
	}

	@Override
	public String toString() {
		return "Color(r = " + r + ", g = " + g + ", b = " + b + ", a = " + a
				+ ")";
	};

}
