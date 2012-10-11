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
package org.eclipse.gef4.graphics.images;

/**
 * This class contains utility methods to work with ARGB pixel values.
 * 
 * @author mwienand
 * 
 */
public class Utils {

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
	public static int computeIntensity(int pixel) {
		return computeIntensity(getARGB(pixel));
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
	 * @param argb
	 *            the alpha, red, green, and blue channel values of the pixel
	 *            for which to compute the intensity
	 * @return the intensity value for the given alpha, red, green, and blue
	 *         channel values
	 */
	public static int computeIntensity(int[] argb) {
		return computeIntensity(argb, 0.3333, 0.3334, 0.3333);
	}

	/**
	 * <p>
	 * Returns the intensity value for the given alpha, red, green, and blue
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
	 * @param argb
	 *            the alpha, red, green, and blue channel values of the pixel
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
	public static int computeIntensity(int[] argb, double sr, double sg, double sb) {
		return (int) (sr * argb[1] + sg * argb[2] + sb * argb[3]);
	}

	/**
	 * Returns the alpha channel value of the given ARGB pixel value.
	 * 
	 * @param pixel
	 *            the ARGB pixel value from which to extract the alpha channel
	 * @return the alpha channel value of the given ARGB pixel value
	 */
	public static int getAlpha(int pixel) {
		return (pixel & 0xff000000) >>> 24;
	}

	/**
	 * Splits an ARGB pixel value into its 4 components.
	 * 
	 * @param pixel
	 * @return array of <code>int</code> containing the individual alpha, red,
	 *         green, and blue components of the given ARGB pixel value in the
	 *         range <code>[0;255]</code>
	 */
	public static int[] getARGB(int pixel) {
		int a = getAlpha(pixel);
		int r = getRed(pixel);
		int g = getGreen(pixel);
		int b = getBlue(pixel);
		return new int[] { a, r, g, b };
	}

	/**
	 * Returns the blue channel value of the given ARGB pixel value.
	 * 
	 * @param pixel
	 *            the ARGB pixel value from which to extract the blue channel
	 * @return the blue channel value of the given ARGB pixel value
	 */
	public static int getBlue(int pixel) {
		return pixel & 0xff;
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
	public static int getClamped(int channel) {
		return Math.min(255, Math.max(0, channel));
	}

	/**
	 * Returns the green channel value of the given ARGB pixel value.
	 * 
	 * @param pixel
	 *            the ARGB pixel value from which to extract the green channel
	 * @return the green channel value of the given ARGB pixel value
	 */
	public static int getGreen(int pixel) {
		return (pixel & 0xff00) >>> 8;
	}

	/**
	 * Merges the given individual alpha, red, green, and blue component values
	 * into an ARGB pixel value.
	 * 
	 * @param argb
	 *            array of <code>int</code> containing the individual alpha,
	 *            red, green, and blue components in the range
	 *            <code>[0;255]</code>
	 * @return an ARGB pixel value representing the given component values
	 */
	public static int getPixel(int... argb) {
		return (argb[0] & 0xff) << 24 | (argb[1] & 0xff) << 16
				| (argb[2] & 0xff) << 8 | argb[3] & 0xff;
	}

	/**
	 * Returns the red channel value of the given ARGB pixel value.
	 * 
	 * @param pixel
	 *            the ARGB pixel value from which to extract the red channel
	 * @return the red channel value of the given ARGB pixel value
	 */
	public static int getRed(int pixel) {
		return (pixel & 0xff0000) >>> 16;
	}

}
