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

class Utils {

	public static int computeIntensity(int argb) {
		return computeIntensity(getARGB(argb));
	}

	public static int computeIntensity(int[] argb) {
		return computeIntensity(argb, 0.333, 0.334, 0.333);
	}

	public static int computeIntensity(int[] argb, double sr, double sg, double sb) {
		return (int) (sr * argb[1] + sg * argb[2] + sb * argb[3]);
	}

	/**
	 * Splits an ARGB pixel value into its 4 components.
	 * 
	 * @param pixel
	 * @return array of <code>int</code> containing the individual alpha, red,
	 *         green, and blue components of the given ARGB pixel value in the
	 *         range <code>[0;255]</code>
	 */
	static int[] getARGB(int pixel) {
		int a = (pixel & 0xff000000) >>> 24;
		int r = (pixel & 0xff0000) >>> 16;
		int g = (pixel & 0xff00) >>> 8;
		int b = pixel & 0xff;
		return new int[] { a, r, g, b };
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
	static int getClamped(int channel) {
		return Math.min(255, Math.max(0, channel));
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
	static int getPixel(int... argb) {
		return (argb[0] & 0xff) << 24 | (argb[1] & 0xff) << 16
				| (argb[2] & 0xff) << 8 | argb[3] & 0xff;
	}

}
