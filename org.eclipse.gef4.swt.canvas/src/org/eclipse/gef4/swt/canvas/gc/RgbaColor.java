/*******************************************************************************
 * Copyright (c) 2012, 2013 itemis AG and others.
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
package org.eclipse.gef4.swt.canvas.gc;

import org.eclipse.swt.graphics.Device;

/**
 * <p>
 * A RgbaColor object is a representation of a color in RGBA color space.
 * Therefore, a RgbaColor object manages four channel values for the Red, Green,
 * Blue, and Alpha channels. The Red, Green, and Blue channels contain the color
 * information. The Alpha channel additionally contains transparency
 * information.
 * </p>
 * 
 * <p>
 * The channel values are limited to the range <code>[0;255]</code>. An
 * arbitrary integer value can be {@link #getChannelClamped(int) clamped} in
 * order to fit that range.
 * </p>
 * 
 * @author mwienand
 * 
 */
public class RgbaColor {

	/**
	 * The <i>alpha</i> value used in the {@link #RgbaColor() default
	 * constructor}.
	 */
	public static final int DEFAULT_ALPHA = 255;

	/**
	 * The <i>blue</i> value used in the {@link #RgbaColor() default
	 * constructor}.
	 */
	public static final int DEFAULT_BLUE = 0;

	/**
	 * The <i>green</i> value used in the {@link #RgbaColor() default
	 * constructor}.
	 */
	public static final int DEFAULT_GREEN = 0;

	/**
	 * The <i>red</i> value used in the {@link #RgbaColor() default constructor}
	 * .
	 */
	public static final int DEFAULT_RED = 0;

	/**
	 * <p>
	 * Returns the intensity value of the given ARGB pixel value.
	 * </p>
	 * 
	 * <p>
	 * Note that the alpha value does not influence the computation.
	 * </p>
	 * 
	 * @param pixel
	 *            the ARGB pixel value for which to compute the intensity
	 * @return the intensity value of the given ARGB pixel value
	 */
	public static int computePixelIntensity(int pixel) {
		return computePixelIntensity(getPixelRGBA(pixel));
	}

	/**
	 * <p>
	 * Returns the intensity value for the given alpha, red, green, and blue
	 * channel values.
	 * </p>
	 * 
	 * <p>
	 * Note that the alpha value does not influence the computation.
	 * </p>
	 * 
	 * @param rgba
	 *            the alpha, red, green, and blue channel values of the pixel
	 *            for which to compute the intensity
	 * @return the intensity value for the given alpha, red, green, and blue
	 *         channel values
	 */
	public static int computePixelIntensity(int[] rgba) {
		return computePixelIntensity(rgba, 1d / 3d, 1d / 3d, 1d / 3d);
	}

	/**
	 * <p>
	 * Returns the intensity value for the given red, green, blue, and alpha
	 * channel values. The individual channels are weighted according to the
	 * specified scale factors.
	 * </p>
	 * 
	 * <p>
	 * Note that the alpha value does not influence the computation.
	 * </p>
	 * 
	 * <p>
	 * Note that the sum of the provided weights controls the maximum possible
	 * intensity value. If the sum equals 1, then the maximum possible intensity
	 * value is 255. Greater weights increase the maximum possible intensity
	 * value.
	 * </p>
	 * 
	 * @param rgba
	 *            the red, green, blue, and alpha channel values of the pixel
	 *            for which to compute the intensity
	 * @param sr
	 *            the scale factor for the red channel
	 * @param sg
	 *            the scale factor for the green channel
	 * @param sb
	 *            the scale factor for the blue channel
	 * @return the intensity value for the given alpha, red, green, and blue
	 *         channel values
	 */
	public static int computePixelIntensity(int[] rgba, double sr, double sg,
			double sb) {
		return (int) (sr * rgba[0] + sg * rgba[1] + sb * rgba[2]);
	}

	/**
	 * Clamps the given color/alpha channel value to the range
	 * <code>[0;255]</code>. If the given value is smaller then the lower limit
	 * of the range, the lower limit is returned. If the given value is greater
	 * then the upper limit of the range, the upper limit is returned.
	 * Otherwise, the given value is returned.
	 * 
	 * @param channel
	 *            the color/alpha channel value to clamp
	 * @return the given value, clamped to the range <code>[0;255]</code>
	 */
	public static int getChannelClamped(int channel) {
		return Math.min(255, Math.max(0, channel));
	}

	/**
	 * Merges the given individual red, green, blue, and alpha component values
	 * into an ARGB pixel value.
	 * 
	 * @param rgba
	 *            array of <code>int</code> containing the individual red,
	 *            green, blue, and alpha components in the range
	 *            <code>[0;255]</code>
	 * @return an ARGB pixel value representing the given component values
	 */
	public static int getPixel(int... rgba) {
		if (rgba == null) {
			throw new IllegalArgumentException(
					"The passed-in rgba-array may not be null.");
		}
		if (rgba.length < 4) {
			throw new IllegalArgumentException(
					"The passed-in rgba-array does not provide enough elements: required 4, given "
							+ rgba.length);
		}
		return (rgba[3] & 0xff) << 24 | (rgba[0] & 0xff) << 16
				| (rgba[1] & 0xff) << 8 | rgba[2] & 0xff;
	}

	/**
	 * Returns the alpha channel value of the given ARGB pixel value.
	 * 
	 * @param pixel
	 *            the ARGB pixel value from which to extract the alpha channel
	 * @return the alpha channel value of the given ARGB pixel value
	 */
	public static int getPixelAlpha(int pixel) {
		return (pixel & 0xff000000) >>> 24;
	}

	/**
	 * Returns the blue channel value of the given ARGB pixel value.
	 * 
	 * @param pixel
	 *            the ARGB pixel value from which to extract the blue channel
	 * @return the blue channel value of the given ARGB pixel value
	 */
	public static int getPixelBlue(int pixel) {
		return pixel & 0xff;
	}

	/**
	 * Returns the green channel value of the given ARGB pixel value.
	 * 
	 * @param pixel
	 *            the ARGB pixel value from which to extract the green channel
	 * @return the green channel value of the given ARGB pixel value
	 */
	public static int getPixelGreen(int pixel) {
		return (pixel & 0xff00) >>> 8;
	}

	/**
	 * Returns the red channel value of the given ARGB pixel value.
	 * 
	 * @param pixel
	 *            the ARGB pixel value from which to extract the red channel
	 * @return the red channel value of the given ARGB pixel value
	 */
	public static int getPixelRed(int pixel) {
		return (pixel & 0xff0000) >>> 16;
	}

	/**
	 * Splits an ARGB pixel value into its 4 components.
	 * 
	 * @param pixel
	 *            an ARGB pixel value
	 * @return array of <code>int</code> containing the individual alpha, red,
	 *         green, and blue components of the given ARGB pixel value
	 */
	public static int[] getPixelRGBA(int pixel) {
		return new int[] { getPixelRed(pixel), getPixelGreen(pixel),
				getPixelBlue(pixel), getPixelAlpha(pixel) };
	}

	/**
	 * The red component of this {@link RgbaColor} in the range
	 * <code>[0;255]</code> .
	 * 
	 * @see #getRed()
	 * @see #setRed(int)
	 */
	private int r;

	/**
	 * The green component of this {@link RgbaColor} in the range
	 * <code>[0;255]</code>.
	 * 
	 * @see #getGreen()
	 * @see #setGreen(int)
	 */
	private int g;

	/**
	 * The blue component of this {@link RgbaColor} in the range
	 * <code>[0;255]</code>.
	 * 
	 * @see #getBlue()
	 * @see #setBlue(int)
	 */
	private int b;

	/**
	 * The alpha value associated with this {@link RgbaColor} in the range
	 * <code>[0;255]</code>.
	 * 
	 * @see #getAlpha()
	 * @see #setAlpha(int)
	 */
	private int a;

	/**
	 * Constructs a new {@link RgbaColor} object representing a fully opaque
	 * black, i.e. <i>r</i><code>=0</code>, <i>g</i><code>=0</code>, <i>b</i>
	 * <code>=0</code>, <i>a</i><code>=255</code>.
	 * 
	 * @see RgbaColor#RgbaColor(int, int, int, int)
	 */
	public RgbaColor() {
		this(DEFAULT_RED, DEFAULT_GREEN, DEFAULT_BLUE, DEFAULT_ALPHA);
	}

	/**
	 * Constructs a new {@link RgbaColor} object from the given <i>pixel</i>
	 * value which is expected to be in ARGB format, 8 bits per channel.
	 * 
	 * @param pixel
	 */
	public RgbaColor(int pixel) {
		this(getPixelRed(pixel), getPixelGreen(pixel), getPixelBlue(pixel),
				getPixelAlpha(pixel));
	}

	/**
	 * Constructs a new {@link RgbaColor} object representing a fully opaque
	 * color with the specified red, green, and blue components.
	 * 
	 * @param red
	 *            the {@link #r red} component of this {@link RgbaColor}
	 * @param green
	 *            the {@link #g green} component of this {@link RgbaColor}
	 * @param blue
	 *            the {@link #b blue} component of this {@link RgbaColor}
	 */
	public RgbaColor(int red, int green, int blue) {
		this(red, green, blue, DEFAULT_ALPHA);
	}

	/**
	 * Creates a new {@link RgbaColor} from the given values.
	 * 
	 * @param red
	 *            the {@link #r red} component of this {@link RgbaColor}
	 * @param green
	 *            the {@link #g green} component of this {@link RgbaColor}
	 * @param blue
	 *            the {@link #b blue} component of this {@link RgbaColor}
	 * @param alpha
	 *            the {@link #a alpha} component of this {@link RgbaColor}
	 */
	public RgbaColor(int red, int green, int blue, int alpha) {
		setRed(red);
		setGreen(green);
		setBlue(blue);
		setAlpha(alpha);
	}

	public RgbaColor(org.eclipse.swt.graphics.Color swtColor, int alpha) {
		this(swtColor.getRed(), swtColor.getGreen(), swtColor.getBlue(), alpha);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof RgbaColor) {
			RgbaColor o = (RgbaColor) obj;
			return o.hashCode() == hashCode();
		}
		return false;
	}

	/**
	 * Returns the alpha component of this {@link RgbaColor} in the range
	 * <code>[0;255]</code> where <code>0</code> signifies a fully transparent
	 * and <code>255</code> a fully opaque color.
	 * 
	 * @return the alpha component of this {@link RgbaColor} (in range
	 *         <code>[0;255]</code>)
	 */
	public int getAlpha() {
		return a;
	}

	/**
	 * Blends this {@link RgbaColor} and the passed-in {@link RgbaColor} with a
	 * blending ratio of <code>0.5</code>.
	 * 
	 * @param other
	 *            the {@link RgbaColor} to blend with this {@link RgbaColor}
	 * @return the {@link RgbaColor} resulting from blending this
	 *         {@link RgbaColor} and the passed-in {@link RgbaColor}
	 */
	public RgbaColor getBlended(RgbaColor other) {
		return getBlended(other, 0.5, 0.5, 0.5, 0.5, 1);
	}

	/**
	 * Blends this {@link RgbaColor} and the passed-in {@link RgbaColor} with
	 * the specified blending ratio. A blending ratio of <code>1</code> will
	 * fully retain the original channel value. A blending ratio of
	 * <code>0</code> will fully discard the original channel value.
	 * 
	 * @param other
	 *            the {@link RgbaColor} to blend with this {@link RgbaColor}
	 * @param x
	 *            the blending ratio
	 * @return the {@link RgbaColor} resulting from blending this
	 *         {@link RgbaColor} and the passed-in {@link RgbaColor}
	 */
	public RgbaColor getBlended(RgbaColor other, double x) {
		return getBlended(other, x, x, x, x, 1);
	}

	/**
	 * <p>
	 * Blends this {@link RgbaColor} and the passed-in {@link RgbaColor} with
	 * the specified blending ratio. A blending ratio of <code>1</code> will
	 * fully retain the original channel value. A blending ratio of
	 * <code>0</code> will fully discard the original channel value.
	 * </p>
	 * 
	 * <p>
	 * The <i>gammaCorrection</i> value can be used to transform from linear to
	 * sRGB color space. The common gamma correction value for such a
	 * transformation is <code>2.2</code>. The resulting {@link RgbaColor} is
	 * exponentiated by the reciprocal of this value.
	 * </p>
	 * 
	 * @param other
	 *            the {@link RgbaColor} to blend with this {@link RgbaColor}
	 * @param x
	 *            the blending ratio
	 * @param gammaCorrection
	 *            the resulting {@link RgbaColor} is exponentiated by the
	 *            reciprocal of this value
	 * @return the {@link RgbaColor} resulting from blending this
	 *         {@link RgbaColor} and the passed-in {@link RgbaColor}
	 */
	public RgbaColor getBlended(RgbaColor other, double x,
			double gammaCorrection) {
		return getBlended(other, x, x, x, x, gammaCorrection);
	}

	/**
	 * Blends this {@link RgbaColor} and the passed-in {@link RgbaColor} with
	 * the specified blending ratios. A blending ratio of <code>1</code> will
	 * fully retain the original channel value. A blending ratio of
	 * <code>0</code> will fully discard the original channel value.
	 * 
	 * @param other
	 *            the {@link RgbaColor} to blend with this {@link RgbaColor}
	 * @param xRed
	 *            the red-channel's blending ratio
	 * @param xGreen
	 *            the green-channel's blending ratio
	 * @param xBlue
	 *            the blue-channel's blending ratio
	 * @param xAlpha
	 *            the alpha-channel's blending ratio
	 * @return the {@link RgbaColor} resulting from blending this
	 *         {@link RgbaColor} and the passed-in {@link RgbaColor}
	 */
	public RgbaColor getBlended(RgbaColor other, double xRed, double xGreen,
			double xBlue, double xAlpha) {
		return getBlended(other, xRed, xGreen, xBlue, xAlpha, 1);
	}

	/**
	 * <p>
	 * Blends this {@link RgbaColor} and the passed-in {@link RgbaColor} with
	 * the specified blending ratios. A blending ratio of <code>1</code> will
	 * fully retain the original channel value. A blending ratio of
	 * <code>0</code> will fully discard the original channel value.
	 * </p>
	 * 
	 * <p>
	 * The <i>gammaCorrection</i> value can be used to transform from linear to
	 * sRGB color space. The common gamma correction value for such a
	 * transformation is <code>2.2</code>. The resulting {@link RgbaColor} is
	 * exponentiated by the reciprocal of this value.
	 * </p>
	 * 
	 * @param other
	 *            the {@link RgbaColor} to blend with this {@link RgbaColor}
	 * @param xRed
	 *            the red-channel's blending ratio
	 * @param xGreen
	 *            the green-channel's blending ratio
	 * @param xBlue
	 *            the blue-channel's blending ratio
	 * @param xAlpha
	 *            the alpha-channel's blending ratio
	 * @param gammaCorrection
	 *            the resulting color is exponentiated by the reciprocal of this
	 *            value
	 * @return the {@link RgbaColor} resulting from blending this
	 *         {@link RgbaColor} and the passed-in {@link RgbaColor}
	 */
	public RgbaColor getBlended(RgbaColor other, double xRed, double xGreen,
			double xBlue, double xAlpha, double gammaCorrection) {
		double r = getRed() * xRed + (1 - xRed) * other.getRed();
		double g = getGreen() * xGreen + (1 - xGreen) * other.getGreen();
		double b = getBlue() * xBlue + (1 - xBlue) * other.getBlue();
		double a = getAlpha() * xAlpha + (1 - xAlpha) * other.getAlpha();

		r = Math.pow(r / 255d, 1d / gammaCorrection) * 255d;
		g = Math.pow(g / 255d, 1d / gammaCorrection) * 255d;
		b = Math.pow(b / 255d, 1d / gammaCorrection) * 255d;
		a = Math.pow(a / 255d, 1d / gammaCorrection) * 255d;

		return new RgbaColor((int) r, (int) g, (int) b, (int) a);
	}

	/**
	 * Returns the blue component of this {@link RgbaColor} in the range
	 * <code>[0;255]</code>.
	 * 
	 * @return the blue component of this {@link RgbaColor} in the range
	 *         <code>[0;255]</code>
	 */
	public int getBlue() {
		return b;
	}

	/**
	 * Returns a copy of this {@link RgbaColor}.
	 * 
	 * @return a copy of this {@link RgbaColor}
	 */
	public RgbaColor getCopy() {
		return new RgbaColor(r, g, b, a);
	}

	/**
	 * Returns the green component of this {@link RgbaColor} in the range
	 * <code>[0;255]</code>.
	 * 
	 * @return the green component of this {@link RgbaColor} in the range
	 *         <code>[0;255]</code>
	 */
	public int getGreen() {
		return g;
	}

	/**
	 * Returns the red component of this {@link RgbaColor} in the range
	 * <code>[0;255]</code>.
	 * 
	 * @return the red component of this {@link RgbaColor} in the range
	 *         <code>[0;255]</code>
	 */
	public int getRed() {
		return r;
	}

	/**
	 * Returns an <code>int</code> array containing the individual red, green,
	 * blue, and alpha channel values which constitute this {@link RgbaColor}.
	 * 
	 * @return an <code>int</code> array containing the individual red, green,
	 *         blue, and alpha channel values which constitute this
	 *         {@link RgbaColor}
	 */
	public int[] getRGBA() {
		return new int[] { getRed(), getGreen(), getBlue(), getAlpha() };
	}

	@Override
	public int hashCode() {
		return (r << 24) + (g << 16) + (b << 8) + a;
	}

	/**
	 * Sets the alpha component of this {@link RgbaColor} to the specified value
	 * in the range <code>[0;255]</code> where <code>0</code> signifies a fully
	 * transparent color and <code>255</code> signifies a fully opaque color.
	 * 
	 * @param alpha
	 *            the new alpha component of this {@link RgbaColor} (in range
	 *            <code>[0;255]</code>)
	 * @return <code>this</code> for convenience
	 */
	public RgbaColor setAlpha(int alpha) {
		if (alpha < 0 || alpha > 255) {
			throw new IllegalArgumentException(
					"The given alpha component (alpha = " + alpha
							+ ") is not in the range [0;255]!");
		}
		a = alpha;
		return this;
	}

	/**
	 * Sets the blue component of this {@link RgbaColor} to the specified value
	 * in range <code>[0;255]</code>.
	 * 
	 * @param blue
	 *            the new blue component of this {@link RgbaColor} (in range
	 *            <code>[0;255]</code>)
	 * @return <code>this</code> for convenience
	 */
	public RgbaColor setBlue(int blue) {
		if (blue < 0 || blue > 255) {
			throw new IllegalArgumentException(
					"The given alpha component (alpha = " + blue
							+ ") is not in the range [0;255]!");
		}
		b = blue;
		return this;
	}

	/**
	 * Sets the green component of this {@link RgbaColor} to the specified value
	 * in range <code>[0;255]</code>.
	 * 
	 * @param green
	 *            the new green component of this {@link RgbaColor} (in range
	 *            <code>[0;255]</code>)
	 * @return <code>this</code> for convenience
	 */
	public RgbaColor setGreen(int green) {
		if (green < 0 || green > 255) {
			throw new IllegalArgumentException(
					"The given alpha component (alpha = " + green
							+ ") is not in the range [0;255]!");
		}
		g = green;
		return this;
	}

	/**
	 * Sets the red component of this {@link RgbaColor} to the specified value
	 * in range <code>[0;255]</code>.
	 * 
	 * @param red
	 *            the new red component of this {@link RgbaColor} (in range
	 *            <code>[0;255]</code>)
	 * @return <code>this</code> for convenience
	 */
	public RgbaColor setRed(int red) {
		if (red < 0 || red > 255) {
			throw new IllegalArgumentException(
					"The given alpha component (alpha = " + red
							+ ") is not in the range [0;255]!");
		}
		r = red;
		return this;
	}

	/**
	 * Sets this {@link RgbaColor}'s red, green, blue, and alpha components to
	 * those specified by the passed-in {@link RgbaColor} object.
	 * 
	 * @param color
	 * @return <code>this</code> for convenience
	 */
	public RgbaColor setTo(RgbaColor color) {
		r = color.r;
		g = color.g;
		b = color.b;
		a = color.a;
		return this;
	}

	public int toPixelARGB() {
		return getPixel(getRGBA());
	}

	@Override
	public String toString() {
		return "Color(r = " + r + ", g = " + g + ", b = " + b + ", a = " + a
				+ ")";
	}

	public org.eclipse.swt.graphics.Color toSwtColor(Device dev) {
		return SwtUtils.createSwtColor(dev, this);
	}

}
